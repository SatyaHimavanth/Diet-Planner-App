package com.example.pubfitnessstudio.ui.dashboard;

import static com.example.pubfitnessstudio.R.id.bmisLayout;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pubfitnessstudio.R;
import com.example.pubfitnessstudio.database.DatabaseHelper;
import com.example.pubfitnessstudio.database.FoodItem;
import com.example.pubfitnessstudio.database.FoodItemsLoader;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    private ExecutorService executorService;
    private DatabaseHelper databaseHelper;
    private List<FoodItem> foodItems;
    private final Map<String, List<Double>> mealTotals = new HashMap<>();
    private final Map<String, ChipGroup> mealChipGroups = new HashMap<>();
    private final Map<String, List<FoodItem>> selectedMealItems = new HashMap<>();

    double liters = 0;

    HashMap<String, Double> bmiData = new HashMap<>();
    private EditText weightEditText;
    private EditText heightEditText;
    private LinearLayout bmisLayout;

    private Spinner spinnerGender, spinnerActivity, spinnerAdjustment;
    private EditText etWeight, etHeight, etAge;
    private TextView tvResult;
    private Button btnCalculate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        executorService = Executors.newSingleThreadExecutor();

        // Initialize meal totals
        mealTotals.put("Breakfast", Arrays.asList(0.0, 0.0, 0.0, 0.0));
        mealTotals.put("Lunch", Arrays.asList(0.0, 0.0, 0.0, 0.0));
        mealTotals.put("Snacks", Arrays.asList(0.0, 0.0, 0.0, 0.0));
        mealTotals.put("Dinner", Arrays.asList(0.0, 0.0, 0.0, 0.0));

        // Initialize chip groups
        mealChipGroups.put("Breakfast", rootView.findViewById(R.id.breakfast_chip_group));
        mealChipGroups.put("Lunch", rootView.findViewById(R.id.lunch_chip_group));
        mealChipGroups.put("Snacks", rootView.findViewById(R.id.snacks_chip_group));
        mealChipGroups.put("Dinner", rootView.findViewById(R.id.dinner_chip_group));

        // Initialize selected meal items
        selectedMealItems.put("Breakfast", new ArrayList<>());
        selectedMealItems.put("Lunch", new ArrayList<>());
        selectedMealItems.put("Snacks", new ArrayList<>());
        selectedMealItems.put("Dinner", new ArrayList<>());

        // Load food items
        foodItems = FoodItemsLoader.loadFoodItems(getContext());
        List<String> foodNames = new ArrayList<>();
        for (FoodItem item : foodItems) foodNames.add(item.getName());

        // Set up AutoCompleteTextViews
        setupMealAutoComplete("Breakfast", rootView.findViewById(R.id.breakfast_input), foodNames);
        setupMealAutoComplete("Lunch", rootView.findViewById(R.id.lunch_input), foodNames);
        setupMealAutoComplete("Snacks", rootView.findViewById(R.id.snacks_input), foodNames);
        setupMealAutoComplete("Dinner", rootView.findViewById(R.id.dinner_input), foodNames);

        // Listen for food data from MainActivity and ViewModel
//        DashboardViewModel viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
//        viewModel.getFoodNames().observe(getViewLifecycleOwner(), foodNames -> {
//            setupMealAutoComplete("Breakfast", rootView.findViewById(R.id.breakfast_input), foodNames);
//            setupMealAutoComplete("Lunch", rootView.findViewById(R.id.lunch_input), foodNames);
//            setupMealAutoComplete("Snacks", rootView.findViewById(R.id.snacks_input), foodNames);
//            setupMealAutoComplete("Dinner", rootView.findViewById(R.id.dinner_input), foodNames);
//        });

        // Submit button
        Button submitMealButton = rootView.findViewById(R.id.submit_button);
        submitMealButton.setOnClickListener(v -> saveToDatabase());


        // Water view
        liters = 0;
        Button waterIncr = rootView.findViewById(R.id.water_increment);
        TextView Displaywater = rootView.findViewById(R.id.water);
        Button waterDecr = rootView.findViewById(R.id.water_decrement);
        Button submitWaterButton = rootView.findViewById(R.id.water_submit);

        waterIncr.setOnClickListener(v -> {
            liters += 1;
            Displaywater.setText(String.format("%.0f", liters) + " Liters");
        });

        waterDecr.setOnClickListener(v -> {
            if (liters > 0){
                liters -= 1;
            }
            Displaywater.setText(String.format("%.0f", liters) + " Liters");
        });

        submitWaterButton.setOnClickListener(v -> {
            executorService.submit(() -> {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                // Initialize database helper
                databaseHelper = new DatabaseHelper(getContext());
                databaseHelper.insertWater(currentDate, liters);
                databaseHelper.close();
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Water data saved successfully!", Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(getContext(), "Water data saved successfully!", Toast.LENGTH_SHORT).show();

        });


        // Initialize views
        weightEditText = rootView.findViewById(R.id.weight);
        heightEditText = rootView.findViewById(R.id.height);
        bmisLayout = rootView.findViewById(R.id.bmisLayout);
        Button submitBMIButton = rootView.findViewById(R.id.form_submit_button);

        // Set the submit button onClick listener
        submitBMIButton.setOnClickListener(v -> saveData());

        // Load BMI Scale from asserts
        try {
            InputStream inputStream = requireContext().getAssets().open("bmi scale.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ImageView bmiScale = rootView.findViewById(R.id.bmi_scale);
            bmiScale.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // BMR
        // Initialize views
        spinnerGender = rootView.findViewById(R.id.spinner_gender);
        spinnerActivity = rootView.findViewById(R.id.spinner_activity);
        spinnerAdjustment = rootView.findViewById(R.id.spinner_adjustment);
        etWeight = rootView.findViewById(R.id.et_weight);
        etHeight = rootView.findViewById(R.id.et_height);
        etAge = rootView.findViewById(R.id.et_age);
        tvResult = rootView.findViewById(R.id.tv_result);
        btnCalculate = rootView.findViewById(R.id.btn_calculate);

        btnCalculate.setOnClickListener(v -> {
            calculateCalories();
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void setupMealAutoComplete(String mealType, AutoCompleteTextView inputView, List<String> foodNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), //getContext()
                android.R.layout.simple_dropdown_item_1line, foodNames);
        inputView.setAdapter(adapter);

        inputView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFood = adapter.getItem(position);
            FoodItem foodItem = findFoodItem(selectedFood);
            if (foodItem != null) {
                addChipToMeal(mealType, foodItem);
                updateMealTotals(mealType, foodItem, true);
            }
            inputView.setText(""); // Clear input
        });
    }

    private FoodItem findFoodItem(String name) {
        for (FoodItem item : foodItems) {
            if (item.getName().equals(name)) return item;
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void addChipToMeal(String mealType, FoodItem foodItem) {
        ChipGroup chipGroup = mealChipGroups.get(mealType);
        Chip chip = new Chip(getContext());
        chip.setText(foodItem.getName() + " (" + foodItem.getCalories() + " kcal " + foodItem.getProteins() + " g)");
        chip.setCloseIconVisible(true);

        Log.d("DashboardFragment", "addChipToMeal" + mealType + " " + foodItem);

        // Remove chip and update totals on close
        chip.setOnCloseIconClickListener(v -> {
            assert chipGroup != null;
            chipGroup.removeView(chip);
            updateMealTotals(mealType, foodItem, false);
            selectedMealItems.get(mealType).remove(foodItem);
        });

        assert chipGroup != null;
        chipGroup.addView(chip);
        selectedMealItems.get(mealType).add(foodItem);
    }

    private void updateMealTotals(String mealType, FoodItem foodItem, boolean add) {
        List<Double> currentTotal = mealTotals.get(mealType);

        double caloriesTotal = add ? currentTotal.get(0) + foodItem.getCalories() : currentTotal.get(0) - foodItem.getCalories();
        double carbsTotal = add ? currentTotal.get(1) + foodItem.getCarbs() : currentTotal.get(1) - foodItem.getCarbs();
        double protiensTotal = add ? currentTotal.get(2) + foodItem.getProteins() : currentTotal.get(2) - foodItem.getProteins();
        double fatsTotal = add ? currentTotal.get(3) + foodItem.getFats() : currentTotal.get(3) - foodItem.getFats();

        currentTotal.set(0, caloriesTotal);
        currentTotal.set(1, carbsTotal);
        currentTotal.set(2, protiensTotal);
        currentTotal.set(3, fatsTotal);

        Log.d("DashboardFragment", "updateMealTotals " + mealType + String.format("%.1f kcal %.1f g", caloriesTotal, protiensTotal));

        mealTotals.put(mealType, currentTotal);
        displayMealTotals();
    }

    private void displayMealTotals() {
        LinearLayout totalsLayout = getView().findViewById(R.id.totalsLayout);
        totalsLayout.removeAllViews();

        for (Map.Entry<String, List<Double>> entry : mealTotals.entrySet()) {
            String mealType = entry.getKey();

            double totalCalories = mealTotals.get(mealType).get(0);
//            double totalCarbs = macroNutrientsTotals.get(1);
            double totalProteins = mealTotals.get(mealType).get(2);
//            double totalFats = macroNutrientsTotals.get(3);
            Log.d("DashboardFragment", "displayMealTotals " + mealType + String.format("%.1f kcal %.1f g", totalCalories, totalProteins));

            TextView textView = new TextView(getContext());
            textView.setText(String.format("%s Total: Calories: %.1f kcal Proteins %.1f g", mealType, totalCalories, totalProteins));
            totalsLayout.addView(textView);
        }
    }

    private void saveToDatabase() {
        executorService.submit(() -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String breakfast = getMealItems("Breakfast");
            String lunch = getMealItems("Lunch");
            String snacks = getMealItems("Snacks");
            String dinner = getMealItems("Dinner");
            double totalCalories =  0;
            double totalCarbs = 0;
            double totalProteins = 0;
            double totalFats = 0;

            for (Map.Entry<String, List<Double>> entry : mealTotals.entrySet()) {
                String mealType = entry.getKey();

                totalCalories += mealTotals.get(mealType).get(0);
                totalCarbs += mealTotals.get(mealType).get(1);
                totalProteins += mealTotals.get(mealType).get(2);
                totalFats += mealTotals.get(mealType).get(3);
            }

            if (breakfast.isEmpty() && lunch.isEmpty() && snacks.isEmpty() && dinner.isEmpty()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Enter at least one food item", Toast.LENGTH_SHORT).show();
                    }
                });
//                Toast.makeText(getContext(), "Enter at least one food item", Toast.LENGTH_SHORT).show();
            } else {
                // Initialize database helper
                databaseHelper = new DatabaseHelper(getContext());
                // Perform database insert in background thread
                databaseHelper.insertMeal(currentDate, breakfast, lunch, snacks, dinner, totalCalories, totalCarbs, totalProteins, totalFats);

                databaseHelper.close();
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Meals  data saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });
//        Toast.makeText(getContext(), "Meals data saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private String getMealItems(String mealType) {
        StringBuilder items = new StringBuilder();
        for (FoodItem item : selectedMealItems.get(mealType)) {
            items.append(item.getName()).append(", ");
        }
        return items.length() > 0 ? items.substring(0, items.length() - 2) : ""; // Remove trailing comma
    }

    private void calculateAndDisplayBMI() {

        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();

        // Validate the inputs
        if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Please enter both weight and height.", Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(getContext(), "Please enter both weight and height.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Parse weight and height
        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);
        // Calculate BMI
        double bmi = (weight*2.20462*703)/(height*height*0.393701*0.393701);

        bmiData.put("height", height);
        bmiData.put("weight", weight);
        bmiData.put("bmi", bmi);
        displayBMI();
    }

    private void displayBMI() {
        // Clear any previous BMI results
        bmisLayout.removeAllViews();

        // Create a new TextView for displaying the BMI
        TextView bmiTextView = new TextView(getContext());
        bmiTextView.setText(String.format("Your BMI is: %.2f", bmiData.get("bmi")));
        bmiTextView.setTextSize(18);
        bmiTextView.setPadding(8, 8, 8, 8);

        // Add the BMI TextView to the layout
        bmisLayout.addView(bmiTextView);
    }

    private void saveData() {
        calculateAndDisplayBMI();
        executorService.submit(() -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            // Initialize database helper
            databaseHelper = new DatabaseHelper(getContext());
            databaseHelper.insertBMI(currentDate, bmiData.get("height"), bmiData.get("weight"), bmiData.get("bmi"));
            databaseHelper.close();
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "BMI data saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });
//        Toast.makeText(getContext(), "BMI data saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void calculateCalories() {
        try {
            String gender = spinnerGender.getSelectedItem().toString();
            double weight = Double.parseDouble(etWeight.getText().toString());
            double height = Double.parseDouble(etHeight.getText().toString());
            int age = Integer.parseInt(etAge.getText().toString());
            double activityFactor = getActivityFactor(spinnerActivity.getSelectedItemPosition());
            double adjustmentFactor = getAdjustmentFactor(spinnerAdjustment.getSelectedItemPosition());


            double bmr;
            if (gender.equals("Male")) {
                bmr = 10 * weight + 6.25 * height - 5 * age + 5;
            } else {
                bmr = 10 * weight + 6.25 * height - 5 * age - 161;
            }

            double totalCalories = bmr * activityFactor;

            String weight_gain_loss = String.format("To maintain your weight consume '%.2f' kcal\n", totalCalories);
            if(adjustmentFactor>0){
                weight_gain_loss += String.format("You need to consume Extra '%.2f' kcal to gain weight.\n", adjustmentFactor);
            } else {
                weight_gain_loss += String.format("You need to reduce your consumption by '%.2f' kcal to loose weight.\n", -1*adjustmentFactor);
            }
            tvResult.setText(String.format(weight_gain_loss+"Your daily calorie needs to be: %.2f kcal", totalCalories+adjustmentFactor));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please fill out all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private double getActivityFactor(int position) {
        switch (position) {
            case 0: return 1.2; // Sedentary
            case 1: return 1.375; // Lightly active
            case 2: return 1.55; // Moderately active
            case 3: return 1.725; // Very active
            case 4: return 1.9; // Extra active
            default: return 1.2;
        }
    }

    private double getAdjustmentFactor(int position) {
        switch (position) {
            case 0: return -250; // mild weight loss
            case 1: return -500; // weight loss
            case 2: return -1000; // extreme weight loss
            case 3: return 250; // mild weight gain
            case 4: return 500; // Extreme weight gain
            default: return -250;
        }
    }

}