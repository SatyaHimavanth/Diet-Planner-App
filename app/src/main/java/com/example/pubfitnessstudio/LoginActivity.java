package com.example.pubfitnessstudio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.net.HttpURLConnection;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pubfitnessstudio.database.DatabaseHelper;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText, passwordEditText;
    private DatabaseHelper databaseHelper, dbHelper;

    private HashMap<String, Object> userData, putUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        dbHelper = new DatabaseHelper(getApplicationContext());
        userData = dbHelper.getUserData();
        dbHelper.close();

        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (authenticateUser(name, password)) {

                    Toast.makeText(LoginActivity.this, "Welcome " + userData.get("username"), Toast.LENGTH_SHORT).show();

                    // Redirect to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private boolean authenticateUser(String name, String password) {
//
//        if(name.equals("Reset") && password.equals("Reset")){
//            HashMap<String, Object> putUserData = new HashMap<>();
//            putUserData.put("primarykey", "Primary Key");
//            putUserData.put("username", "PubFit");
//            putUserData.put("password", "PubFit");
//
//            dbHelper = new DatabaseHelper(getApplicationContext());
//            dbHelper.insertUserData(putUserData);
//            dbHelper.close();
//
//            Toast.makeText(LoginActivity.this, "Password reset", Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
//            startActivity(intent);
//        }
//        // Replace this with real authentication logic (API call, local validation, etc.)
//        String user = (String) userData.get("username");
//        String pass = (String) userData.get("password");
//        Log.d("LoginActivity", "UserDetails: " + user + " " + pass);
//        String adminuser = (String) userData.get("adminUser");
//        String adminpass = (String) userData.get("adminPassword");
//
//        return (user.equals(name) || adminuser.equals(name)) || (pass.equals(password) || pass.equals(adminpass));
//    }

    private boolean authenticateUser(String name, String password) {
        final boolean[] isAuthenticated = {false};
        final String[] status = {""};

        CountDownLatch latch = new CountDownLatch(1);
        String deviceid = DeviceUtil.getDeviceId(getApplicationContext());
        LoginRequest loginRequest = new LoginRequest();

        // Start a new thread for the login request
        new Thread(() -> {
            try {
                Map<String, Object> response = loginRequest.sendLoginRequest(name, password, deviceid);
                System.out.println("Response: " + response);

                if (response != null && "Login Successful".equals(response.get("status"))) {
                    isAuthenticated[0] = true;

                    String lastSubDate = (String) response.get("last_sub_date");
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    HashMap<String, Object> putUserData = new HashMap<>();
                    putUserData.put("primarykey", "Primary Key");
                    putUserData.put("LastSubDay", lastSubDate);
                    putUserData.put("LastLogin", currentDate);
                    dbHelper = new DatabaseHelper(getApplicationContext());
                    dbHelper.insertUserData(putUserData);
                    dbHelper.close();

                } else {
                    status[0] = (String) response.get("status");
                }
            } catch (Exception e) {
                status[0] = "Login Failed";
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isAuthenticated[0]) {
            Toast.makeText(getApplicationContext(), status[0], Toast.LENGTH_SHORT).show();
        }

        return isAuthenticated[0];
    }


}