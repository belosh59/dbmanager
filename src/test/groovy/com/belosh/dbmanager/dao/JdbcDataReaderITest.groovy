package com.belosh.dbmanager.dao

import com.belosh.dbmanager.ServiceLocator
import com.belosh.dbmanager.enity.DataVO
import com.belosh.dbmanager.service.DatabaseService
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JdbcDataReaderITest {
    private JdbcDataReader jdbcDataReader

    @Before
    void setup() {
        def resource = getClass().getClassLoader().getResource("cfg/tets-database.json")
        Assert.assertNotNull(resource)

        def file = new File(resource.toURI())

        jdbcDataReader = new JdbcDataReader()
        ServiceLocator.register(JdbcDataReader.class.toString(), jdbcDataReader)

        DatabaseService databaseService = new DatabaseService()
        databaseService.setConfigFile(file)
        databaseService.getConfiguredDatabses()
        databaseService.setUpDataSource("d3n2i90vr7qpeg")
    }

    @Test
    void testExecuteStatements() {
        String sql = "CREATE TABLE itest (id int, row text);" +
                "INSERT INTO itest VALUES (1, 'first row');" +
                "INSERT INTO itest VALUES (2, '/;}[]{*?.,-+&^%#@!');" +
                "UPDATE itest SET row='updated row';" +
                "SELECT * FROM itest;" +
                "DROP TABLE itest;"

        List<DataVO> dataVOList = jdbcDataReader.executeStatements(sql)
        Assert.assertEquals(6, dataVOList.size())

        DataVO createTableDataVO = dataVOList.get(0)
        Assert.assertNull(createTableDataVO.getColumns())
        Assert.assertNull(createTableDataVO.getRows())
        Assert.assertEquals(0, createTableDataVO.getChangesCount())
        Assert.assertTrue(createTableDataVO.isUpdatable())

        DataVO insertDataVO = dataVOList.get(1)
        Assert.assertNull(insertDataVO.getColumns())
        Assert.assertNull(insertDataVO.getRows())
        Assert.assertEquals(1, insertDataVO.getChangesCount())
        Assert.assertTrue(insertDataVO.isUpdatable())

        insertDataVO = dataVOList.get(2)
        Assert.assertNull(insertDataVO.getColumns())
        Assert.assertNull(insertDataVO.getRows())
        Assert.assertEquals(1, insertDataVO.getChangesCount())
        Assert.assertTrue(insertDataVO.isUpdatable())

        DataVO updateDataVO = dataVOList.get(3)
        Assert.assertNull(updateDataVO.getColumns())
        Assert.assertNull(updateDataVO.getRows())
        Assert.assertEquals(2, updateDataVO.getChangesCount())
        Assert.assertTrue(updateDataVO.isUpdatable())

        DataVO selectDataVO = dataVOList.get(4)
        Assert.assertEquals(2, selectDataVO.getColumns().size())
        Assert.assertEquals(2, selectDataVO.getRows().size())
        Assert.assertEquals(2, selectDataVO.getChangesCount())
        Assert.assertFalse(selectDataVO.isUpdatable())

        DataVO dropDataVO = dataVOList.get(0)
        Assert.assertNull(dropDataVO.getColumns())
        Assert.assertNull(dropDataVO.getRows())
        Assert.assertEquals(0, dropDataVO.getChangesCount())
        Assert.assertTrue(dropDataVO.isUpdatable())
    }
}
