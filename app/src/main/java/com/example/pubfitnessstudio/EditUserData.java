package com.example.pubfitnessstudio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.example.pubfitnessstudio.database.DatabaseHelper;


public class EditUserData extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int REQUEST_PERMISSION = 101;
    private static final int PICK_IMAGE = 1;
    private DatabaseHelper dbHelper;
    private HashMap<String, Object> userData, putUserData;
    private EditText userName;
    private EditText userHeight;
    private EditText userWeight;
    private EditText userDOB;
    private Button btnUploadImage;
    private ImageView imageView;
    private File savedImageFile;
    private Button updateUserDetails;

    private Spinner spinnerGender;
    private EditText currentPassword;
    private EditText updatePassword;
    private EditText re_updatePassword;
    private Button passwordUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_data);

        dbHelper = new DatabaseHelper(getApplicationContext());
        userData = dbHelper.getUserData();
        dbHelper.close();


        // Initialize Views
        userName = findViewById(R.id.etName);
        spinnerGender = findViewById(R.id.spinner_gender);
        userHeight = findViewById(R.id.userHeight);
        userWeight = findViewById(R.id.userWeight);
        userDOB = findViewById(R.id.userDOB);

        userDOB.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Open DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserData.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = sdf.format(selectedDate.getTime());

                            userDOB.setText(formattedDate);
                        }
                    }, year, month, day);

            datePickerDialog.show();
        });

        updateUserDetails = findViewById(R.id.btnSave);
        updateUserDetails.setOnClickListener(v -> {
            String name = userName.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            System.out.println(gender);
            String height = userHeight.getText().toString().trim();
            String weight = userWeight.getText().toString().trim();
            String dob = userDOB.getText().toString().trim();

            if (name.isEmpty() || gender.isEmpty() || height.isEmpty() || weight.isEmpty() || dob.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {

                putUserData = new HashMap<>();
                putUserData.put("primarykey", "Primary Key");
                putUserData.put("username", name);
                putUserData.put("gender", gender);
                putUserData.put("height", Double.parseDouble(height));
                putUserData.put("weight", Double.parseDouble(weight));
                putUserData.put("DOB", dob);

                // Call the method to insert data
                dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.insertUserData(putUserData);
                dbHelper.close();

                // Show success message
                Toast.makeText(getApplicationContext(), "User details updated successfully", Toast.LENGTH_SHORT).show();

                userName.setText("");
                userWeight.setText("");
                userHeight.setText("");
                userDOB.setText("");

            }
        });

        // Image view
        imageView = findViewById(R.id.ivProfileImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        btnUploadImage.setOnClickListener((v -> {
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGallery, PICK_IMAGE);

            try {
                Thread.sleep(1000);  // Sleep for 3000 milliseconds (3 seconds)
            } catch (InterruptedException e) {
                e.printStackTrace();  // Handle the InterruptedException
            }
        }));

        // password update
//        currentPassword = findViewById(R.id.current_password);
//        updatePassword = findViewById(R.id.new_password);
//        re_updatePassword = findViewById(R.id.re_new_password);
//        passwordUpdateButton = findViewById(R.id.update_password);
//
//        passwordUpdateButton.setOnClickListener(v -> {
//            Log.d("EditUserData", "Password update button clicker");
//
//            String old_pass = currentPassword.getText().toString().trim();
//            String new_pass = updatePassword.getText().toString().trim();
//            String re_new_pass = re_updatePassword.getText().toString().trim();
//            String old_saved_pass = (String) userData.get("password");
//            String admin_pass = (String) userData.get("adminPassword");
//
//            Log.d("EditUserData", old_saved_pass + " " + old_pass + " " + new_pass + " " + re_new_pass);
//
//            if (old_pass.isEmpty() || new_pass.isEmpty() || re_new_pass.isEmpty()){
//                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                if (old_pass.equals(old_saved_pass) || old_pass.equals(admin_pass)){
//                    if(re_new_pass.equals(new_pass)){
//                        putUserData = new HashMap<>();
//                        putUserData.put("primarykey", "Primary Key");
//                        putUserData.put("password", new_pass);
//
//                        // Call the method to insert data
//                        dbHelper = new DatabaseHelper(getApplicationContext());
//                        dbHelper.insertUserData(putUserData);
//                        dbHelper.close();
//
//                        Toast.makeText(getApplicationContext(), "Password updated successful", Toast.LENGTH_SHORT).show();
//                        currentPassword.setText("");
//                        updatePassword.setText("");
//                        re_updatePassword.setText("");
//                    } else {
//                        Log.d("EditUserData", "update passwords mismatch" + new_pass + "!=" + re_new_pass);
//                        Toast.makeText(getApplicationContext(), "update passwords mismatch", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.d("EditUserData", "Old password did not match" + old_pass + "!=" + old_saved_pass);
//                    Toast.makeText(getApplicationContext(), "old password doesn't match", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);

                    // Save image to internal storage
                    savedImageFile = saveImageToInternalStorage(bitmap, "selected_image.jpg");

                    if (savedImageFile != null && savedImageFile.exists()) {
                        // Save the file path in SharedPreferences
                        HashMap<String, Object> putUserDataimg = new HashMap<>();
                        putUserDataimg.put("primarykey", "Primary Key");
                        putUserDataimg.put("imageUri", savedImageFile.getAbsolutePath());

                        // Call the method to insert data
                        dbHelper = new DatabaseHelper(getApplicationContext());
                        dbHelper.insertUserData(putUserDataimg);
                        dbHelper.close();

                        Toast.makeText(getApplicationContext(), "Image uri saved", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error in upload image", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveImageToInternalStorage(Bitmap bitmap, String fileName) throws IOException {
        File directory = getFilesDir(); // Internal storage directory
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        return file;
    }
}

