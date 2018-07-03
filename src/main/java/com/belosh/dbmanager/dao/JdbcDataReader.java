package com.belosh.dbmanager.dao;

import com.belosh.dbmanager.dao.mapper.DataMapper;
import com.belosh.dbmanager.dao.mapper.UserTablesMapper;
import com.belosh.dbmanager.enity.DataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin.dom.exception.InvalidStateException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcDataReader {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DataMapper DATA_MAPPER = new DataMapper();
    private static final String USER_TABLES_SQL = "SELECT table_name FROM information_schema.tables WHERE table_schema='public'";
    private static final UserTablesMapper USER_TABLES_MAPPER = new UserTablesMapper();

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DataVO> executeStatements(String sql) {
        long startExecution = System.currentTimeMillis();
        if (dataSource == null) {
            throw new IllegalStateException("Connection is not established. Please connect to database.");
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            List<DataVO> dataVOList = new ArrayList<>();

            boolean hasResults = statement.execute(sql);
            int updateCount = statement.getUpdateCount();
            while (hasResults || updateCount != -1) {
                long startQueryExecution = System.currentTimeMillis();
                DataVO dataVO;
                if (hasResults) {
                    try (ResultSet resultSet = statement.getResultSet()) {
                        dataVO = DATA_MAPPER.mapRow(resultSet);
                        dataVO.setUpdatable(false);
                        dataVO.setChangesCount(dataVO.getRows().size());
                    }
                } else {
                    dataVO = new DataVO();
                    dataVO.setUpdatable(true);
                    dataVO.setChangesCount(updateCount);
                }
                dataVO.setExecutionTime(System.currentTimeMillis() - startQueryExecution);
                dataVOList.add(dataVO);

                hasResults = statement.getMoreResults(); // Implicitly close any active result set, do I need to close it above?
                updateCount = statement.getUpdateCount();
            }

            log.info("Requested statement executed: {} in {} ms", sql, System.currentTimeMillis() - startExecution);
            return dataVOList;

        } catch (SQLException e) {
            String message = "Unable to execute query: " + sql + " due to " + e.getMessage();
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    public void checkConnection() {
        try {
            dataSource.getConnection();
            log.info("Connected to specified database via provided datasource");
        } catch (SQLException e) {
            log.error("Unable to connect to database via provided datasource");
            throw new InvalidStateException("Unable to connect to database via provided datasource");
        }
    }

    public List<String> getUserTables() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(USER_TABLES_SQL)) {
            return USER_TABLES_MAPPER.mapRow(resultSet);
        } catch (SQLException e) {
            throw new InvalidStateException("Unable to get user tables");
        }
    }

}
