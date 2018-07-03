package com.belosh.dbmanager.service

import com.belosh.dbmanager.ServiceLocator
import com.belosh.dbmanager.dao.JdbcDataReader
import com.belosh.dbmanager.enity.Database
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import sun.plugin.dom.exception.InvalidStateException

class DatabaseServiceITest {
    private DatabaseService databaseService

    @Before
    void setup() {
        def resource = getClass().getClassLoader().getResource("cfg/tets-database.json")
        Assert.assertNotNull(resource)

        def file = new File(resource.toURI())

        JdbcDataReader jdbcDataReader = new JdbcDataReader()
        ServiceLocator.register(JdbcDataReader.class.toString(), jdbcDataReader)
        databaseService = new DatabaseService()
        databaseService.setConfigFile(file)
        databaseService.getConfiguredDatabses()
    }

    @Test
    void testGetConfiguredDatabses() {
        List<Database> databases = databaseService.getConfiguredDatabses()
        Assert.assertEquals(3, databases.size())
    }

    /**
     * Below test case cover transitively methods:
     * @getConfiguredDatabses
     * @addDatabaseConfiguration
     * @removeDatabaseConfigurationByName
     */
    @Test
    void testAddDatabaseConfiguration() {
        List<Database> databasesBefore = databaseService.getConfiguredDatabses()
        Assert.assertEquals(3, databasesBefore.size())

        String testHost = "test-host"
        String testDatabaseName = "test-database"
        String testPort = "111"
        String testUser = "test-user"
        String testPassword = "test-password"

        Database testDatabase = new Database()
        testDatabase.setHost(testHost)
        testDatabase.setDatabaseName(testDatabaseName)
        testDatabase.setPort(testPort)
        testDatabase.setUser(testUser)
        testDatabase.setPassword(testPassword)

        databaseService.addDatabaseConfiguration(testDatabase)

        List<Database> databasesAfterAdd = databaseService.getConfiguredDatabses()
        Assert.assertEquals(4, databasesAfterAdd.size())

        Database databaseFromCfg = databaseService.getDatabaseByName("test-database")
        Assert.assertEquals(testHost, databaseFromCfg.getHost())
        Assert.assertEquals(testDatabaseName, databaseFromCfg.getDatabaseName())
        Assert.assertEquals(testPort, databaseFromCfg.getPort())
        Assert.assertEquals(testUser, databaseFromCfg.getUser())
        Assert.assertEquals(testPassword, databaseFromCfg.getPassword())

        databaseService.removeDatabaseConfigurationByName(testDatabaseName)

        List<Database> databasesAfterRemove = databaseService.getConfiguredDatabses()
        Assert.assertEquals(3, databasesAfterRemove.size())

    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none()

    /**
     * Below test case cover transitively methods:
     * @setUpDataSource
     * @addDatabaseConfiguration
     */
    @Test
    void testGetUserTables() {
        databaseService.setUpDataSource("d3n2i90vr7qpeg")
        List<String> tables = databaseService.getUserTables()
        Assert.assertEquals(3, tables.size())


        expectedEx.expect(RuntimeException.class)
        expectedEx.expectMessage("Requested database was not found: wrong-database")
        // Negative case
        databaseService.setUpDataSource("wrong-database")

    }
}
