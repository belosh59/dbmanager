package com.belosh.dbmanager.task;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.enity.DataVO;
import com.belosh.dbmanager.service.DataService;
import javafx.concurrent.Task;

import java.util.List;

public class JdbcDataReadTask extends Task<List<DataVO>> {
    private final DataService dataService = ServiceLocator.get(DataService.class.toString(), DataService.class);

    private String sql;
    private long executionTime;

    public JdbcDataReadTask(String sql) {
        this.sql = sql;
    }

    @Override
    protected List<DataVO> call() throws Exception {
        long startTime = System.currentTimeMillis();
        List<DataVO> dataVOList = dataService.executeStatements(sql);
        executionTime = System.currentTimeMillis() - startTime;

        return dataVOList;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
