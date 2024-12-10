package com.example.pubfitnessstudio;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.pubfitnessstudio.database.DatabaseHelper;


public class ExcelExporter {

    private Context context;

    public ExcelExporter(Context context) {
        this.context = context;
    }

    public void exportMealsToExcel() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<Map<String, String>> data = dbHelper.getAllMeals();
        List<Map<String, String>> waterdata = dbHelper.getAllWaters();


        if (data != null) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Meals");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Breakfast");
            headerRow.createCell(2).setCellValue("Lunch");
            headerRow.createCell(3).setCellValue("Snacks");
            headerRow.createCell(4).setCellValue("Dinner");
            headerRow.createCell(5).setCellValue("Total Calories");
            headerRow.createCell(6).setCellValue("Total Carbs");
            headerRow.createCell(7).setCellValue("Total Proteins");
            headerRow.createCell(8).setCellValue("Total Fats");

            int rowIndex = 1;  // Starting row for data
            for (Map<String, String> meal : data) {
                // Access individual meal data
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(meal.get("date"));
                row.createCell(1).setCellValue(meal.get("breakfast"));
                row.createCell(2).setCellValue(meal.get("lunch"));
                row.createCell(3).setCellValue(meal.get("snacks"));
                row.createCell(4).setCellValue(meal.get("dinner"));
                row.createCell(5).setCellValue(meal.get("totalCalories"));
                row.createCell(6).setCellValue(meal.get("totalCarbs"));
                row.createCell(7).setCellValue(meal.get("totalProteins"));
                row.createCell(8).setCellValue(meal.get("totalFats"));
            }

            // Get the Downloads directory
            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create file
            File file = new File(downloadsDirectory, "meal_data.xlsx");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                workbook.close();
                Log.d("ExcelExporter", "Excel file created successfully: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ExcelExporter", "Error writing Excel file", e);
            }
        }
    }

    public void exportBMIsToExcel() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<Map<String, String>> data = dbHelper.getAllBMIs();

        if (data != null) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("BMI Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Height");
            headerRow.createCell(2).setCellValue("Weight");
            headerRow.createCell(3).setCellValue("BMI");

            int rowIndex = 1;  // Starting row for data
            for (Map<String, String> meal : data) {
                // Access individual meal data
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(meal.get("date"));
                row.createCell(6).setCellValue(meal.get("height"));
                row.createCell(7).setCellValue(meal.get("weight"));
                row.createCell(8).setCellValue(meal.get("bmi"));
            }

            // Get the Downloads directory
            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create file
            File file = new File(downloadsDirectory, "bmi_data.xlsx");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                workbook.close();
                Log.d("ExcelExporter", "Excel file created successfully: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ExcelExporter", "Error writing Excel file", e);
            }
        }
    }
}
