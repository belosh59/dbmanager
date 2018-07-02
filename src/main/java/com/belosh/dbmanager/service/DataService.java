package com.belosh.dbmanager.service;

import com.belosh.dbmanager.ServiceLocator;
import com.belosh.dbmanager.dao.JdbcDataReader;
import com.belosh.dbmanager.enity.DataVO;

import java.util.List;

public class DataService {
    private JdbcDataReader jdbcDataReader = ServiceLocator.get(JdbcDataReader.class.toString(), JdbcDataReader.class);

    public List<DataVO> executeStatements(String sql) {
        return jdbcDataReader.executeStatements(sql);
    }
}
