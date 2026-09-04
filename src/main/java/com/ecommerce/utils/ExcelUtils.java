package com.ecommerce.utils;

import com.ecommerce.constants.FrameworkConstants;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to read test data from Excel (.xlsx) files using Apache POI.
 */
public final class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {}

    /**
     * Reads all rows from the specified sheet into a List of HashMaps,
     * where keys represent column headers from the first row.
     */
    public static List<Map<String, String>> getTestData(String sheetName) {
        return getTestData(FrameworkConstants.getTestDataExcelPath(), sheetName);
    }

    /**
     * Reads test data from a given workbook path and sheet name.
     */
    public static List<Map<String, String>> getTestData(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet with name '" + sheetName + "' not found in: " + filePath);
            }

            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                LOGGER.warn("Sheet [{}] has no data rows.", sheetName);
                return dataList;
            }

            int lastColNum = sheet.getRow(0).getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int col = 0; col < lastColNum; col++) {
                headers.add(formatter.formatCellValue(sheet.getRow(0).getCell(col)).trim());
            }

            for (int r = 1; r <= lastRowNum; r++) {
                if (sheet.getRow(r) == null) continue;
                Map<String, String> rowMap = new HashMap<>();
                boolean hasContent = false;

                for (int c = 0; c < lastColNum; c++) {
                    String key = headers.get(c);
                    String val = formatter.formatCellValue(sheet.getRow(r).getCell(c)).trim();
                    if (!val.isEmpty()) {
                        hasContent = true;
                    }
                    rowMap.put(key, val);
                }

                if (hasContent) {
                    dataList.add(rowMap);
                }
            }

            LOGGER.info("Successfully loaded {} test data rows from sheet [{}]", dataList.size(), sheetName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Excel file not found at: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }

        return dataList;
    }
}
