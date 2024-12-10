package com.example.pubfitnessstudio.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitness_db";
    private static final int DATABASE_VERSION = 14;

    // Meal Table
    public static final String TABLE_MEAL = "meal_table";
    public static final String COLUMN_MEAL_DATE = "date";
    public static final String COLUMN_BREAKFAST = "breakfast";
    public static final String COLUMN_LUNCH = "lunch";
    public static final String COLUMN_SNACKS = "snacks";
    public static final String COLUMN_DINNER = "dinner";
    public static final String COLUMN_TOTAL_CALORIES = "totalCalories";
    public static final String COLUMN_TOTAL_CARBS = "totalCarbs";
    public static final String COLUMN_TOTAL_PROTEINS = "totalProteins";
    public static final String COLUMN_TOTAL_FATS = "totalFats";

    // Water Table
    public static final String TABLE_WATER = "water_table";
    public static final String COLUMN_WATER_DATE = "date";
    public static final String COLUMN_WATER = "water";

    // BMI TABLE
    public static final String TABLE_BMI = "bmi_table";
    public static final String COLUMN_BMI_DATE = "date";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_BMI = "bmi";

    public static final String TABLE_USER = "user_table";

    // SQL statement to create table
    // SQL statements to create tables
    private static final String CREATE_MEAL_TABLE =
            "CREATE TABLE " + TABLE_MEAL + " (" +
                    COLUMN_MEAL_DATE + " TEXT PRIMARY KEY, " +
                    COLUMN_BREAKFAST + " TEXT, " +
                    COLUMN_LUNCH + " TEXT, " +
                    COLUMN_SNACKS + " TEXT, " +
                    COLUMN_DINNER + " TEXT, " +
                    COLUMN_WATER + " REAL, " +
                    COLUMN_TOTAL_CALORIES + " REAL, " +
                    COLUMN_TOTAL_CARBS + " REAL, " +
                    COLUMN_TOTAL_PROTEINS + " REAL, " +
                    COLUMN_TOTAL_FATS + " REAL);";

    private static final String CREATE_BMI_TABLE =
            "CREATE TABLE " + TABLE_BMI + " (" +
                    COLUMN_BMI_DATE + " TEXT PRIMARY KEY, " +
                    COLUMN_HEIGHT + " REAL, " +
                    COLUMN_WEIGHT + " REAL, " +
                    COLUMN_BMI + " REAL);";

    private static final String CREATE_WATER_TABLE =
            "CREATE TABLE " + TABLE_WATER + " (" +
                    COLUMN_WATER_DATE + " TEXT PRIMARY KEY, " +
                    COLUMN_WATER + " REAL);";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    "primarykey TEXT PRIMARY KEY, " +
                    "username TEXT DEFAULT 'PubFit', " +
                    "password TEXT DEFAULT 'PubFit', " +
                    "gender TEXT, " +
                    "imageUri TEXT, " +
                    "adminUser TEXT DEFAULT 'PubFitAdmin', " +
                    "adminPassword TEXT DEFAULT 'SecretAdminPassword', " +
                    "LastLogin TEXT, " +
                    "AvailableDays REAL, " +
                    "DOB TEXT, " +
                    "height REAL, " +
                    "weight REAL, " +
                    "goalCalories REAL, " +
                    "goalCarbs REAL, " +
                    "goalProteins REAL, " +
                    "goalFats REAL);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_MEAL_TABLE); // Create meal table
            db.execSQL(CREATE_BMI_TABLE);  // Create BMI table
            db.execSQL(CREATE_WATER_TABLE);
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL("INSERT INTO " + TABLE_USER + " (" +
                    "primarykey, " +
                    "username, " +
                    "password, " +
                    "gender, " +
                    "imageUri, " +
                    "adminUser, " +
                    "adminPassword, " +
                    "LastLogin, " +
                    "AvailableDays, " +
                    "DOB, " +
                    "height, " +
                    "weight, " +
                    "goalCalories, " +
                    "goalCarbs, " +
                    "goalProteins, " +
                    "goalFats) VALUES (" +
                    "'Primary Key', " +
                    "'PubFit', " +   // username
                    "'PubFit', " +   // password
                    "'', " +
                    "'', " +    // imageUri
                    "'PubFitAdmin', " +   // adminUser (default)
                    "'SecretAdminPassword', " +   // adminPassword (default)
                    "'2020-01-01', " +   // LastLogin (use the current date or placeholder)
                    "0.0, " +
                    "'2000-01-01', " +   // DOB (example date)
                    "0.0, " +   // height (example)
                    "0.0, " +   // weight (example)
                    "0.0, " +   // goalCalories (example)
                    "0.0, " +   // goalCarbs (example)
                    "0.0, " +   // goalProteins (example)
                    "0.0);");   // goalFats (example)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Method to insert a meal entry into the database
    public void insertMeal(String date, String breakfast, String lunch, String snacks, String dinner, double totalCalories, double totalCarbs, double totalProteins, double totalFats) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Log.d("DatabaseHelper", String.format("User MEAL data: %s %.1f %.1f", date, totalCalories, totalProteins));
            ContentValues values = new ContentValues();
            values.put(COLUMN_MEAL_DATE, date);
            values.put(COLUMN_BREAKFAST, breakfast);
            values.put(COLUMN_LUNCH, lunch);
            values.put(COLUMN_SNACKS, snacks);
            values.put(COLUMN_DINNER, dinner);
            values.put(COLUMN_TOTAL_CALORIES, totalCalories);
            values.put(COLUMN_TOTAL_CARBS, totalCarbs);
            values.put(COLUMN_TOTAL_PROTEINS, totalProteins);
            values.put(COLUMN_TOTAL_FATS, totalFats);

            // Use "INSERT OR REPLACE" to replace a row if the primary key already exists
            db.insertWithOnConflict(TABLE_MEAL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();  // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();  // Commit or roll back the transaction
            db.close();
        }
    }

    public void insertWater(String date, double water){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Log.d("DatabaseHelper", String.format("User WATER data: %s %.1f", date, water));
            ContentValues values = new ContentValues();
            values.put(COLUMN_WATER_DATE, date);
            values.put(COLUMN_WATER, water);

            // Use "INSERT OR REPLACE" to replace a row if the primary key already exists
            db.insertWithOnConflict(TABLE_WATER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();  // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();  // Commit or roll back the transaction
            db.close();
        }
    }

    // Insert BMI data
    public void insertBMI(String date, double height, double weight, double bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Log.d("DatabaseHelper", "Attempting to load data");
        try {
            Log.d("DatabaseHelper", String.format("User BMI data: %s %.1f %.1f %.1f", date, height, weight, bmi));
            ContentValues values = new ContentValues();
            values.put(COLUMN_BMI_DATE, date);
            values.put(COLUMN_HEIGHT, height);
            values.put(COLUMN_WEIGHT, weight);
            values.put(COLUMN_BMI, bmi);
            db.insertWithOnConflict(TABLE_BMI, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("DatabaseHelper", "Data saved to BMI table");
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("DatabaseHelper", "Data not saved to BMI table");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertUserData(HashMap<String, Object> userData) {
        // Get a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Log.d("DatabaseHelper", "Attempting to load user data");
        try {
            ContentValues values = new ContentValues();
            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String) {
                    values.put(key, (String) value);  // For String values
                } else if (value instanceof Integer) {
                    values.put(key, (Integer) value); // For Integer values
                } else if (value instanceof Long) {
                    values.put(key, (Long) value);    // For Long values
                } else if (value instanceof Double) {
                    values.put(key, (Double) value);  // For Double values
                } else if (value == null) {
                    values.putNull(key);              // For null values
                }
            }

            db.update(TABLE_USER, values, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("DatabaseHelper", "Data not saved to User table");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Method to fetch all meal entries from the database
    @SuppressLint("Range")
    public List<Map<String, String>> getAllMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> mealDataList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MEAL, null);
            Log.d("DatabaseHelper", "Data retrieving from Meals table");

            // Check if the cursor is valid and has the required columns
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Map<String, String> mealData = new HashMap<>();
                    mealData.put("date", cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_DATE)));
                    mealData.put("breakfast", cursor.getString(cursor.getColumnIndex(COLUMN_BREAKFAST)));
                    mealData.put("lunch", cursor.getString(cursor.getColumnIndex(COLUMN_LUNCH)));
                    mealData.put("snacks", cursor.getString(cursor.getColumnIndex(COLUMN_SNACKS)));
                    mealData.put("dinner", cursor.getString(cursor.getColumnIndex(COLUMN_DINNER)));
                    mealData.put("totalCalories", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_CALORIES))));
                    mealData.put("totalCarbs", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_CARBS))));
                    mealData.put("totalProteins", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_PROTEINS))));
                    mealData.put("totalFats", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_FATS))));

                    printData(mealData);
                    mealDataList.add(mealData);
                }
                Log.d("DatabaseHelper", "Data retried from Meals table");

            } else {
                Log.e("DatabaseHelper", "Failed to load data from Meals database or columns are missing");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "An Exception occurred in getAllMeals");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return mealDataList; // Return null if columns are missing
    }

    @SuppressLint("Range")
    public List<Map<String, String>> getAllWaters() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> waterDataList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_WATER, null);
            Log.d("DatabaseHelper", "Data retrieving from Water table");

            // Check if the cursor is valid and has the required columns
            if (cursor != null) {
                while (cursor.moveToNext()){
                    Map<String, String> waterData = new HashMap<>();
                    waterData.put("date", cursor.getString(cursor.getColumnIndex(COLUMN_WATER_DATE)));
                    waterData.put("water", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_WATER))));

                    printData(waterData);
                    waterDataList.add(waterData);
                }
                Log.d("DatabaseHelper", "Data retried from Water table");

            } else {
                Log.e("DatabaseHelper", "Failed to load data from Water database or columns are missing");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "An Exception occurred in getAllWaters");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return waterDataList; // Return null if columns are missing
    }

    @SuppressLint("Range")
    public List<Map<String, String>> getAllBMIs() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Map<String, String>> bmiDataList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_BMI, null);
            Log.d("DatabaseHelper", "Data retrieving from BMI table");

            // Check if the cursor is valid and has the required columns
            if (cursor != null) {
                while (cursor.moveToNext()){
                    Map<String, String> bmiData = new HashMap<>();
                    bmiData.put("date", cursor.getString(cursor.getColumnIndex(COLUMN_BMI_DATE)));
                    bmiData.put("height", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT))));
                    bmiData.put("weight", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT))));
                    bmiData.put("bmi", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_BMI))));

                    printData(bmiData);
                    bmiDataList.add(bmiData);
                }
                Log.d("DatabaseHelper", "Data retried from BMI table");

            } else {
                Log.e("DatabaseHelper", "Failed to load data from BMI database or columns are missing");
            }
        } catch (Exception e) {
                Log.e("DatabaseHelper", "An Exception occurred in getAllBMIs");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return bmiDataList; // Return null if columns are missing
    }

    public void printData(Map<String, String> Data) {
        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, String> entry : Data.entrySet()) {
            output.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" | ");
        }
        if (output.length() > 0) {
            output.setLength(output.length() - 3);
        }
        Log.d("DatabaseHelper", output.toString());
    }

    @SuppressLint("Range")
    public HashMap<String, Object> getUserData() {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Object> userData = new HashMap<>();

        Cursor cursor = null;
        // Query to retrieve the row matching the username
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);

            // Check if the cursor contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Populate the HashMap with the retrieved column values
                userData.put("primarykey", cursor.getString(cursor.getColumnIndex("primarykey")));
                userData.put("username", cursor.getString(cursor.getColumnIndex("username")));
                userData.put("password", cursor.getString(cursor.getColumnIndex("password")));
                userData.put("gender", cursor.getString(cursor.getColumnIndex("gender")));
                userData.put("imageUri", cursor.getString(cursor.getColumnIndex("imageUri")));
                userData.put("adminUser", cursor.getString(cursor.getColumnIndex("adminUser")));
                userData.put("adminPassword", cursor.getString(cursor.getColumnIndex("adminPassword")));
                userData.put("LastLogin", cursor.getString(cursor.getColumnIndex("LastLogin")));
                userData.put("AvailableDays", cursor.getDouble(cursor.getColumnIndex("AvailableDays")));
                userData.put("DOB", cursor.getString(cursor.getColumnIndex("DOB")));
                userData.put("height", cursor.getDouble(cursor.getColumnIndex("height")));
                userData.put("weight", cursor.getDouble(cursor.getColumnIndex("weight")));
                userData.put("goalCalories", cursor.getDouble(cursor.getColumnIndex("goalCalories")));
                userData.put("goalCarbs", cursor.getDouble(cursor.getColumnIndex("goalCarbs")));
                userData.put("goalProteins", cursor.getDouble(cursor.getColumnIndex("goalProteins")));
                userData.put("goalFats", cursor.getDouble(cursor.getColumnIndex("goalFats")));
            } else {
                Log.e("DatabaseHelper", "Failed to get data from User table");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "An Exception occurred in getUserData");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        // Return the user data
        return userData;
    }


    @SuppressLint("Range")
    public Map<String, String> getASpecificDay(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> daysData = new HashMap<>();
        Cursor cursor;

        daysData.put("totalCalories", "0");
        daysData.put("totalCarbs", "0");
        daysData.put("totalProteins", "0");
        daysData.put("totalFats", "0");
        daysData.put("water", "0");
        daysData.put("height", "0");
        daysData.put("weight", "0");
        daysData.put("bmi", "0");

        try {
            cursor = null;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_MEAL + " WHERE " + COLUMN_MEAL_DATE + "='" + date + "'", null);
                Log.d("DatabaseHelper", "Data retrieving from MEAL table");
                Log.d("DatabaseHelper", "SELECT * FROM " + TABLE_MEAL + " WHERE " + COLUMN_MEAL_DATE + "='" + date + "'");
                // Check if the cursor is valid and has the required columns
                if (cursor != null) {
                    cursor.moveToFirst();
                    daysData.put("totalCalories", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_CALORIES))));
                    daysData.put("totalCarbs", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_CARBS))));
                    daysData.put("totalProteins", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_PROTEINS))));
                    daysData.put("totalFats", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_FATS))));

                    Log.d("DatabaseHelper", "Meal Data retried for one day");
                } else {
                    Log.e("DatabaseHelper", "No Meal data for that day");
                }
            } catch (Exception e) {
                Log.e("DatabaseHelper", "An Exception occurred retrieving Meal data");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            cursor = null;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_BMI + " WHERE " + COLUMN_MEAL_DATE + "='" + date + "'", null);
                Log.d("DatabaseHelper", "Data retrieving from BMI table");
                Log.d("DatabaseHelper", "SELECT * FROM " + TABLE_BMI + " WHERE " + COLUMN_BMI_DATE + "='" + date + "'");
                // Check if the cursor is valid and has the required columns
                if (cursor != null) {
                    cursor.moveToFirst();
                    daysData.put("height", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT))));
                    daysData.put("weight", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT))));
                    daysData.put("bmi", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_BMI))));

                    Log.d("DatabaseHelper", "BMI Data retried for one day");
                } else {
                    Log.e("DatabaseHelper", "No BMI data for that day");
                }
            } catch (Exception e) {
                Log.e("DatabaseHelper", "An Exception occurred retrieving BMI data");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            cursor = null;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + "='" + date + "'", null);
                Log.d("DatabaseHelper", "Data retrieving from Water table");
                Log.d("DatabaseHelper", "SELECT * FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + "='" + date + "'");
                // Check if the cursor is valid and has the required columns
                if (cursor != null) {
                    cursor.moveToFirst();
                    daysData.put("water", String.format("%.1f", cursor.getDouble(cursor.getColumnIndex(COLUMN_WATER))));

                    Log.d("DatabaseHelper", "WATER Data retried for one day");
                } else {
                    Log.e("DatabaseHelper", "No WATER data for that day");
                }
            } catch (Exception e) {
                Log.e("DatabaseHelper", "An Exception occurred retrieving WATER data");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } finally {
            db.close();
        }
        return daysData;
    }

    @SuppressLint("Range")
    public List<String> getAllDates(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> datesData = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT "+ COLUMN_MEAL_DATE +" FROM " + TABLE_MEAL, null);

            // Check if the cursor is valid and has the required columns
            if (cursor != null) {
                cursor.moveToFirst();
                while(cursor != null) {
                    datesData.add(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_DATE)));
                    cursor.moveToNext();
                }
            } else {
                Log.e("DatabaseHelper", "No dates");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "An Exception occurred retrieving dates data");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return datesData;
    }
}
