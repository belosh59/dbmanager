package com.belosh.dbmanager.service;

import com.belosh.dbmanager.enity.ExportDataVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void exportDataToCSV (List<ExportDataVO> exportDataVOList, File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             Workbook workbook = new HSSFWorkbook()) {

            for (ExportDataVO exportDataVO : exportDataVOList) {
                Sheet spreadsheet = workbook.createSheet(exportDataVO.getSource().replace("*", ""));

                Row columnsRow = spreadsheet.createRow(0);

                // Export Columns
                List<String> columns = exportDataVO.getColumns();
                for (int i=0; i < columns.size(); i++) {
                    columnsRow.createCell(i).setCellValue(columns.get(i));
                }

                // Export Data
                List<List<String>> data = exportDataVO.getRows();
                for (int i=0; i < data.size(); i++) {
                    Row dataRow = spreadsheet.createRow(i + 1); // Plus 1 due to columns row already set to index 0
                    List<String> dataRowList = data.get(i);

                    for (int j = 0; j < dataRowList.size(); j++) {
                        dataRow.createCell(j).setCellValue(dataRowList.get(j));
                    }
                }
            }

            workbook.write(fileOutputStream);
            log.info("Data has been successfully exported to: " + file);
        } catch (IOException e) {
            String errorMessage = "Unable to export data due to File I/O Exception";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
