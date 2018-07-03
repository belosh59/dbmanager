package com.belosh.dbmanager.service

import com.belosh.dbmanager.enity.ExportDataVO
import javafx.scene.control.Cell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.junit.Assert
import org.junit.Test

class ExportServiceTest {

    @Test
    void testExportDataToCSV() {
        ExportService exportService = new ExportService();

        def resource = getClass().getClassLoader().getResource("")
        Assert.assertNotNull(resource)

        String path = resource.toURI().toString() + "test-export.slx"
        def file = new File(path)
        file.createNewFile()

        List<ExportDataVO> exportDataVOList = new ArrayList<>()

        List<String> columns = new ArrayList<>()
        columns.add("Column1")

        List<List<String>> data = new ArrayList<>()
        data.add(columns)

        ExportDataVO exportDataVO = new ExportDataVO()
        exportDataVO.setColumns(columns)
        exportDataVO.setRows(data)
        exportDataVO.setSource("test")

        exportDataVOList.add(exportDataVO)

        exportService.exportDataToCSV(exportDataVOList, file)

        // Test spreadsheet
        Workbook workbook = new HSSFWorkbook(new FileInputStream(file))
        Sheet sheet = workbook.getSheet("test")

        Cell column = sheet.getRow(0).getCell(0)
        Assert.assertEquals("Column1", columns)

        Cell dataCell = sheet.getRow(1).getCell(0)
        Assert.assertEquals("Column1", dataCell)

        file.delete()
    }
}
