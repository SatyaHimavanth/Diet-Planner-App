package com.example.pubfitnessstudio.database;

import android.content.Context;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FoodItemsLoader {

    public static List<FoodItem> loadFoodItems(Context context) {
        List<FoodItem> foodItems = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("food_items.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Assuming the first row contains headers
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String name = row.getCell(0).getStringCellValue();
                double calories = row.getCell(1).getNumericCellValue();
                double carbs = row.getCell(2).getNumericCellValue();
                double proteins = row.getCell(3).getNumericCellValue();
                double fats = row.getCell(4).getNumericCellValue();
                foodItems.add(new FoodItem(name, calories, carbs, proteins, fats));
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodItems;
    }
}
