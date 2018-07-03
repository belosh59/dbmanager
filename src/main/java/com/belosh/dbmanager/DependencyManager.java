package com.belosh.dbmanager;

import com.belosh.dbmanager.dao.JdbcDataReader;
import com.belosh.dbmanager.service.DataService;
import com.belosh.dbmanager.service.DatabaseService;
import com.belosh.dbmanager.service.ExportService;
import com.belosh.dbmanager.view.ContentInitializer;
import com.belosh.dbmanager.view.LayoutInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DependencyManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void setUp() {
        JdbcDataReader jdbcDataReader = new JdbcDataReader();
        ServiceLocator.register(JdbcDataReader.class.toString(), jdbcDataReader);

        String databaseConfigFileName = "/database.json";
        File configFile = new File(System.getProperty("user.dir") + databaseConfigFileName);

        DatabaseService databaseService = new DatabaseService();
        databaseService.setConfigFile(configFile);
        ServiceLocator.register(DatabaseService.class.toString(), databaseService);

        DataService dataService = new DataService();
        ServiceLocator.register(DataService.class.toString(), dataService);

        ExportService exportService = new ExportService();
        ServiceLocator.register(ExportService.class.toString(), exportService);

        ContentInitializer contentInitializer = new ContentInitializer();
        ServiceLocator.register(ContentInitializer.class.toString(), contentInitializer);

        LayoutInitializer layoutInitializer = new LayoutInitializer();
        layoutInitializer.initLayouts();

        log.info("ServiceLocator dependencies has been registered");
    }
}
