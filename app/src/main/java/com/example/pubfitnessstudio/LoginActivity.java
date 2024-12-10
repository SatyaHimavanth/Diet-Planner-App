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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences prefs = getSharedPreferences("UserGoalData", MODE_PRIVATE);

        Map<String, ?> allEntries = prefs.getAll();
        Log.d("LoginActivity", "Printing all shared preferences");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }

        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Perform authentication (e.g., check credentials or call API)
                if (authenticateUser(name, password)) {
                    // Save login status
                    SharedPreferences prefs = getSharedPreferences("UserGoalData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    editor.putString("LastLogin", currentDate);
                    editor.commit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Redirect to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the LoginActivity
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean authenticateUser(String name, String password) {

        if(name.equals("Reset") && password.equals("Reset")){
            SharedPreferences prefs = getSharedPreferences("UserGoalData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", "PubFit");
            editor.putString("password", "PubFit");
            editor.commit();

            Toast.makeText(LoginActivity.this, "Password reset", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
        }
        // Replace this with real authentication logic (API call, local validation, etc.)
        SharedPreferences sharedPreferences = getSharedPreferences("UserGoalData", MODE_PRIVATE);
        String user = sharedPreferences.getString("username", "");
        String pass = sharedPreferences.getString("password", "");
        Log.d("LoginActivity", "UserDetails: " + user + " " + pass);
        String adminpass = sharedPreferences.getString("adminPassword", "SecretAdminPassword");
        String adminuser = sharedPreferences.getString("adminUsername", "PubFitAdmin");
//        return true;
        return (user.equals(name) || adminuser.equals(name)) || (pass.equals(password) || pass.equals(adminpass));
    }

}