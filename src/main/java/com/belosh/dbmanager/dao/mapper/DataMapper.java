package com.belosh.dbmanager.dao.mapper;

import com.belosh.dbmanager.enity.DataVO;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataMapper implements RowMapper<DataVO> {

    @Override
    public DataVO mapRow(ResultSet resultSet) {
        List<List<String>> rows = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i);
                columns.add(columnName);
            }

            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                rows.add(row);
            }

            DataVO dataVO = new DataVO();
            dataVO.setColumns(columns);
            dataVO.setRows(rows);

            return dataVO;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to map ResultSet to DataVO", e);
        }

    }
}
