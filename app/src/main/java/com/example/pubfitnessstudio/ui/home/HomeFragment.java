package com.example.pubfitnessstudio.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import androidx.fragment.app.Fragment;
import com.example.pubfitnessstudio.R;
import com.example.pubfitnessstudio.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private DatabaseHelper databaseHelper, dbHelper;
    private TextView goalCaloriesText, goalCarbsText, goalProteinsText, goalFatsText;
    private Map<String, String> data;
    private List<String> allDates;
    private ImageView imageView;
    private int selected_date;
    private Spinner spinner;
    private EditText editText;
    private TextView unitTextView;
    private Button submitButton;

    private HashMap<String, Object> userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        goalCaloriesText = rootView.findViewById(R.id.goalCalories_text);
        goalCarbsText = rootView.findViewById(R.id.goalCarbs_text);
        goalProteinsText = rootView.findViewById(R.id.goalProteins_text);
        goalFatsText = rootView.findViewById(R.id.goalFats_text);

        loadUserData();

        for (String key : userData.keySet()) {
            Log.d("HomeFragment", key + ": " + userData.get(key));
        }

        // Display user image
        imageView = rootView.findViewById(R.id.userImage);
        loadUserImage();

        // Initialize DatabaseHelper and ListView
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseHelper = new DatabaseHelper(getContext());
        allDates = databaseHelper.getAllDates();
        databaseHelper.close();

        loadDaysData(currentDate);

        if (!allDates.contains(currentDate)) {
            allDates.add(currentDate);
        }

        int size = allDates.size();

        Log.d("HomeFragment", "Total active dates: " + String.format("%d", size));
        Button leftButton = rootView.findViewById(R.id.previous_day);
        Button rightButton = rootView.findViewById(R.id.next_day);
        selected_date = size-1;

        TextView displayDate = rootView.findViewById(R.id.display_day);
        TextView Calories = rootView.findViewById(R.id.calories);
        TextView Carbs = rootView.findViewById(R.id.carbs);
        TextView Proteins = rootView.findViewById(R.id.proteins);
        TextView Fats = rootView.findViewById(R.id.fats);
        TextView BMI = rootView.findViewById(R.id.bmi);
        TextView Water = rootView.findViewById(R.id.water);

        Calories.setText(data.get("totalCalories") + " kcal");
        Carbs.setText(data.get("totalCarbs") + " g");
        Proteins.setText(data.get("totalProteins") + " g");
        Fats.setText(data.get("totalFats") + " g");
        BMI.setText(data.get("bmi"));
        Water.setText(data.get("water") + " l");

        leftButton.setOnClickListener(v-> {
            if(selected_date>0){
                selected_date -= 1;
                databaseHelper = new DatabaseHelper(getContext());
                loadDaysData(allDates.get(selected_date));
                databaseHelper.close();
                displayDate.setText(allDates.get(selected_date));
                if(selected_date==size-1){
                    displayDate.setText("Today");
                }
                Calories.setText(data.get("totalCalories") + " kcal");
                Carbs.setText(data.get("totalCarbs") + " g");
                Proteins.setText(data.get("totalProteins") + " g");
                Fats.setText(data.get("totalFats") + " g");
                BMI.setText(data.get("bmi"));
                Water.setText(data.get("water") + " l");
            } else {
                Toast.makeText(getActivity(), "This is the last date!", Toast.LENGTH_SHORT).show();
            }
        });

        rightButton.setOnClickListener(v -> {
            if(selected_date<size-1){
                selected_date += 1;
                databaseHelper = new DatabaseHelper(getContext());
                loadDaysData(allDates.get(selected_date));
                databaseHelper.close();
                displayDate.setText(allDates.get(selected_date));
                Calories.setText(data.get("totalCalories") + " kcal");
                Carbs.setText(data.get("totalCarbs") + " g");
                Proteins.setText(data.get("totalProteins") + " g");
                Fats.setText(data.get("totalFats") + " g");
                BMI.setText(data.get("bmi"));
                Water.setText(data.get("water") + " l");
            } else {
                Toast.makeText(getActivity(), "This is the first date!", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize UI elements
        spinner = rootView.findViewById(R.id.spinner);
        editText = rootView.findViewById(R.id.editText);
        unitTextView = rootView.findViewById(R.id.unitTextView);
        submitButton = rootView.findViewById(R.id.submitButton);

        // Define items for the spinner
        String[] spinnerItems = {"Calories", "Carbs", "Proteins", "Fats"};

        // Create an ArrayAdapter to populate the spinner with items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), //getContext()
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set the default selected item (1st item in the list)
        spinner.setSelection(0);
        updateUnitText(0);

        // Spinner item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateUnitText(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String input = editText.getText().toString().trim();

            // Check if input is not empty
            if (!input.isEmpty()) {
                try {
                    double inputValue = Double.parseDouble(input);
                    int selectedItem = spinner.getSelectedItemPosition();

                    HashMap<String, Object> updateUserData = new HashMap<>();
                    updateUserData.put("primarykey", "Primary Key");


                    Log.d("NotificationsFrame", String.format("%s, %.1f", selectedItem, inputValue));
                    switch (selectedItem) {
                        case 0:
                            updateUserData.put("goalCalories", inputValue);
                            break;
                        case 1:
                            updateUserData.put("goalCarbs", inputValue);
                            break;
                        case 2:
                            updateUserData.put("goalProteins", inputValue);
                            break;
                        case 3:
                            updateUserData.put("goalFats", inputValue);
                            break;
                    }
                    // Call the method to insert data
                    dbHelper = new DatabaseHelper(getContext());
                    dbHelper.insertUserData(updateUserData);
                    dbHelper.close();

                    // Show a success message
                    Toast.makeText(getContext(), "Goal updated!", Toast.LENGTH_SHORT).show();

                    // Reload the user data and update the UI without recreating the fragment
                    loadUserData();
                    loadDaysData(allDates.get(selected_date));

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Show error if EditText is empty
                Toast.makeText(getContext(), "Please enter a value.", Toast.LENGTH_SHORT).show();
            }
            editText.setText("");
        });

        return rootView;
    }

    private void loadUserImage(){
        String imagePath = (String) userData.get("imageUri");

        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getContext(), "Image not found", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(getContext(), "No image saved", Toast.LENGTH_SHORT).show();
            int x = 1;
        }
    }

    // Update the unit label based on the selected item in the spinner
    private void updateUnitText(int position) {
        switch (position) {
            case 0:
                unitTextView.setText("kcal");
                break;
            case 1:
                unitTextView.setText("g");
                break;
            case 2:
                unitTextView.setText("g");
                break;
            case 3:
                unitTextView.setText("g");
                break;
        }
    }

    // Method to reload user data after goal update
    private void loadUserData() {
        dbHelper = new DatabaseHelper(getContext());
        userData = dbHelper.getUserData();
        dbHelper.close();

        // Retrieve the stored values (provide default values in case they don't exist)
        String goalCalories = String.valueOf(userData.get("goalCalories"));
        String goalCarbs = String.valueOf(userData.get("goalCarbs"));
        String goalProteins = String.valueOf(userData.get("goalProteins"));
        String goalFats = String.valueOf(userData.get("goalFats"));

        goalCaloriesText.setText("Calories Goal: " + goalCalories + " kcal");
        goalCarbsText.setText("Carbs Goal: " + goalCarbs + " g");
        goalProteinsText.setText("Proteins Goal: " + goalProteins + " g");
        goalFatsText.setText("Fats Goal: " + goalFats + " g");
    }

    // Method to reload data for the selected date
    private void loadDaysData(String currentDate) {
        dbHelper = new DatabaseHelper(getContext());
        data = dbHelper.getASpecificDay(currentDate);
        dbHelper.close();
    }

}
