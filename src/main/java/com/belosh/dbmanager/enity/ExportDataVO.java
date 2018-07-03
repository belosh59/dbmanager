package com.belosh.dbmanager.enity;

import java.util.List;

public class ExportDataVO {
    private List<String> columns;
    private List<List<String>> rows;
    private String source;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "ExportDataVO{" +
                "columns=" + columns +
                ", rows=" + rows +
                ", source='" + source + '\'' +
                '}';
    }
}
