package com.belosh.dbmanager.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserTablesMapper implements RowMapper<List<String>> {
    @Override
    public List<String> mapRow(ResultSet resultSet) {
        List<String> userTables = new ArrayList<>();

        try {
            while (resultSet.next()){
                userTables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to map user tables", e);
        }

        return userTables;
    }
}
