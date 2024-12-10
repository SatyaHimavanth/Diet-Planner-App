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

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        HashMap<String, Object> userData = new HashMap<>();
        HashMap<String, Object> UserRow;

        userData.put("username", "PubFit");
        userData.put("password", "PubFit");
        userData.put("adminUser", "PubFitAdmin");
        userData.put("adminPassword", "SecretAdminPassword");
        userData.put("LastLogin", 5.0);

        // Call the method to insert data
        dbHelper = new DatabaseHelper(this);
        dbHelper.insertUserData(userData);

        UserRow = dbHelper.getUserData();
        dbHelper.close();

        for (String key : UserRow.keySet()) {
            Log.d("SplashActivity", key + ": " + userData.get(key));
        }
        // Access SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Map<String, ?> allEntries = prefs.getAll();
        Log.d("SplashActivity", "Printing all shared preferences");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }

        while(true){
            updateUserData();
            String username = prefs.getString(KEY_USERNAME, null);
            String password = prefs.getString(KEY_PASSWORD, null);
            if(!username.isEmpty() && username != null && !password.isEmpty() && password != null){
                break;
            }
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
        LastLoggedIn = prefs.getString(KEY_LAST_LOGGED_IN, null);
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
    private void updateUserData(){
        SharedPreferences.Editor editor = prefs.edit();

        // initially clear the prefs
//        editor.clear();
//        editor.commit();

        // Set default admin credentials if not already set
        editor.putString("adminUsername", DEFAULT_ADMIN_USERNAME);
        editor.commit();
        editor.putString("adminPassword", DEFAULT_ADMIN_PASSWORD);
        editor.commit();

        String username = prefs.getString(KEY_USERNAME, null);
        String password = prefs.getString(KEY_PASSWORD, null);
        String lastLogin = prefs.getString(KEY_LAST_LOGGED_IN, null);

        if (username==null || username.isEmpty()){
            editor.putString("username", "PubFit");
            editor.commit();
        }

        if (password==null || password.isEmpty()){
            editor.putString("password", "PubFit");
            editor.commit();
        }

        if (lastLogin==null || lastLogin.isEmpty()){
            editor.putString(KEY_LAST_LOGGED_IN, "2000-01-01");
            editor.commit();
        }
    }
}
