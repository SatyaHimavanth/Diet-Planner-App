package com.example.pubfitnessstudio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.example.pubfitnessstudio.database.DatabaseHelper;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserGoalData";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LAST_LOGGED_IN = "LastLogin";
    private static final String DEFAULT_ADMIN_USERNAME = "PubFit";
    private static final String DEFAULT_ADMIN_PASSWORD = "SecretAdminPassword";
    private SharedPreferences prefs;
    private String currentDate;
    private String LastLoggedIn;
    private HashMap<String, Object> userData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        HashMap<String, Object> putUserData = new HashMap<String, Object>();

        putUserData.put("adminUser", "PubFitAdmin");
        putUserData.put("adminPassword", "SecretAdminPassword");

        // Call the method to insert data
        dbHelper = new DatabaseHelper(this);
        dbHelper.insertUserData(putUserData);
        userData = dbHelper.getUserData();
        dbHelper.close();

        for (String key : userData.keySet()) {
            Log.d("SplashActivity", key + ": " + userData.get(key));
        }

        Intent intent;
        if (checkLastLogin()) {
            // If the user is logged in, skip the login screen and go directly to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Otherwise, show the login screen
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close the SplashActivity
    }

    private boolean checkLastLogin(){

        boolean login_condition = false;

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        LastLoggedIn = (String) userData.get("LastLogin");
        // Create a SimpleDateFormat object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Convert the string dates to Date objects
            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(LastLoggedIn);

            // Calculate the difference
            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if(diffInDays<30){
                login_condition = true;
            }
        } catch (Exception e){
            Log.d("SplashActivity", "Error in getting Login date!");
        }
        return login_condition;
    }
}
