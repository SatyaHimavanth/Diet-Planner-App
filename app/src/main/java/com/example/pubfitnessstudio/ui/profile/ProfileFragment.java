package com.example.pubfitnessstudio.ui.profile;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.time.Period;
import com.example.pubfitnessstudio.EditUserData;
import com.example.pubfitnessstudio.R;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pubfitnessstudio.ExcelExporter;
import com.example.pubfitnessstudio.database.DatabaseHelper;
import android.widget.Button;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private ImageView imageView;
    private DatabaseHelper dbHelper;
    private HashMap<String, Object> userData;
    private ExcelExporter exporter;
    private Button editProfileButton;
    TextView Name, height, weight, Age;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbHelper = new DatabaseHelper(getContext());
        userData = dbHelper.getUserData();
        dbHelper.close();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Name = view.findViewById(R.id.userName);
        height = view.findViewById(R.id.userHeight);
        weight = view.findViewById(R.id.userWeight);
        Age = view.findViewById(R.id.userAge);
        imageView = view.findViewById(R.id.userImage);

        loadUserData();
        loadUserImage();

        // Find the button by ID
        editProfileButton = view.findViewById(R.id.editProfile);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditUserData.class);
            startActivity(intent);
        });

        // Export Data
        exporter = new ExcelExporter(getContext());

        Button exportMealsButton = view.findViewById(R.id.btn_export_meals);
        Button exportBmiButton = view.findViewById(R.id.btn_export_bmi);

        // Set up listeners for the buttons
        exportMealsButton.setOnClickListener(v -> exporter.exportMealsToExcel());

        exportBmiButton.setOnClickListener(v -> exporter.exportBMIsToExcel());

        return view;
    }

    private void loadUserData(){
        // calculate Age
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        String userDOB = (String) userData.get("DOB");
        if (userDOB == null || userDOB.isEmpty()){
            userDOB = currentDate.toString();
        }
        LocalDate dob = LocalDate.parse(userDOB, formatter);
        int age = Period.between(dob, currentDate).getYears();

        //User data
        Name.setText((String) userData.get("username"));
        height.setText("Height: " + userData.get("height").toString() + " cm");
        weight.setText("Weight: " + userData.get("weight").toString() + " kg");
        Age.setText("Age: " + age);
    }
    private void loadUserImage(){
        String imagePath = (String) userData.get("imageUri");

        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getContext(), "Image not found please re-upload", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(getContext(), imagePath, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "No image saved", Toast.LENGTH_SHORT).show();
            int x = 1;
        }
    }
}
