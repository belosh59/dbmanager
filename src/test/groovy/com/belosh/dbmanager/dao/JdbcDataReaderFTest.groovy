package com.belosh.dbmanager.dao

import com.belosh.dbmanager.ServiceLocator
import com.belosh.dbmanager.enity.DataVO
import com.belosh.dbmanager.service.DatabaseService
import org.apache.commons.dbcp.BasicDataSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import javax.sql.DataSource

class JdbcDataReaderFTest {
    private static final String DATABASE_DRIVER = "org.postgresql.ds.PGPoolingDataSource";
    private static final String DATABASE_PROPERTIES = "ssl=true;sslfactory=org.postgresql.ssl.NonValidatingFactory";

    private JdbcDataReader jdbcDataReader = new JdbcDataReader()
    private DataSource dataSource

    @Before
    void setup() {
        ServiceLocator.register(JdbcDataReader.class.toString(), jdbcDataReader);
        dataSource = new BasicDataSource()
        dataSource.setUrl("jdbc:hsqldb:mem:hsql;shutdown=true")
    }

    @Test
    void testExecuteStatements() {
        jdbcDataReader.setDataSource(dataSource)

        String createTableSql = "CREATE TABLE test (id INT)"
        List<DataVO> dataVOListcreateTable = jdbcDataReader.executeStatements(createTableSql)
        Assert.assertEquals(1, dataVOListcreateTable.size())
        DataVO createTableDataVO = dataVOListcreateTable.get(0)
        Assert.assertNull(createTableDataVO.getColumns())
        Assert.assertNull(createTableDataVO.getRows())
        Assert.assertEquals(0, createTableDataVO.getChangesCount())
        Assert.assertTrue(createTableDataVO.isUpdatable())

        String insertSql = "INSERT INTO test VALUES (59);"
        List<DataVO> insertDataVoList = jdbcDataReader.executeStatements(insertSql)
        Assert.assertEquals(1, insertDataVoList.size())
        DataVO insertDataVO = insertDataVoList.get(0)
        Assert.assertNull(insertDataVO.getColumns())
        Assert.assertNull(insertDataVO.getRows())
        Assert.assertEquals(1, insertDataVO.getChangesCount())
        Assert.assertTrue(insertDataVO.isUpdatable())

        String updateSql = "update test set id = 88"
        List<DataVO> updateDataVoList = jdbcDataReader.executeStatements(updateSql)
        Assert.assertEquals(1, updateDataVoList.size())
        DataVO updateDataVO = updateDataVoList.get(0)
        Assert.assertNull(updateDataVO.getColumns())
        Assert.assertNull(updateDataVO.getRows())
        Assert.assertEquals(1, updateDataVO.getChangesCount())
        Assert.assertTrue(updateDataVO.isUpdatable())

        String selectSql = "SELECT * FROM test"
        List<DataVO> selectDataVoList = jdbcDataReader.executeStatements(selectSql)
        Assert.assertEquals(1, selectDataVoList.size())
        DataVO selectDataVO = selectDataVoList.get(0)
        Assert.assertNotNull(selectDataVO.getColumns())
        Assert.assertNotNull(selectDataVO.getRows())
        Assert.assertEquals(1, selectDataVO.getChangesCount())
        Assert.assertFalse(selectDataVO.isUpdatable())

        String dropSql = "DROP TABLE test;"
        List<DataVO> dropDataVoList = jdbcDataReader.executeStatements(dropSql)
        Assert.assertEquals(1, dropDataVoList.size())
        DataVO dropDataVO = dropDataVoList.get(0)
        Assert.assertNull(dropDataVO.getColumns())
        Assert.assertNull(dropDataVO.getRows())
        Assert.assertEquals(0, dropDataVO.getChangesCount())
        Assert.assertTrue(dropDataVO.isUpdatable())

    }

    void testCheckConnection() {
    }

    void testGetUserTables() {
    }
}
