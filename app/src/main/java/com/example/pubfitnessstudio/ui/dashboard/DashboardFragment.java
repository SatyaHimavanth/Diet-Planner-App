package com.example.pubfitnessstudio.ui.dashboard;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.pubfitnessstudio.R;
import com.example.pubfitnessstudio.database.DatabaseHelper;
import com.example.pubfitnessstudio.database.FoodItem;
import com.example.pubfitnessstudio.database.FoodItemsLoader;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private LinearLayout reportLayout;
    private Spinner spinnerGender, spinnerActivity, spinnerAdjustment;
    private EditText etWeight, etHeight, etAge;
    private TextView tvResult;
    private Button btnCalculate;
    private DatabaseHelper dbHelper;
    private HashMap<String, Object> userData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHelper = new DatabaseHelper(getContext());
        userData = dbHelper.getUserData();
        dbHelper.close();

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

        // Check if food items are already loaded in the Singleton
        foodItems = FoodItemsManager.getInstance().getFoodItems();

        // If not loaded, load food items from source (e.g., Excel or database)
        if (foodItems == null) {
            foodItems = FoodItemsLoader.loadFoodItems(getContext()); // Load from Excel or DB
            FoodItemsManager.getInstance().setFoodItems(foodItems); // Store them in the Singleton
        }

        // Load food items
        foodItems = FoodItemsLoader.loadFoodItems(getContext());
        List<String> foodNames = new ArrayList<>();
        for (FoodItem item : foodItems) foodNames.add(item.getName());

        // Set up AutoCompleteTextViews
        setupMealAutoComplete("Breakfast", rootView.findViewById(R.id.breakfast_input), foodNames);
        setupMealAutoComplete("Lunch", rootView.findViewById(R.id.lunch_input), foodNames);
        setupMealAutoComplete("Snacks", rootView.findViewById(R.id.snacks_input), foodNames);
        setupMealAutoComplete("Dinner", rootView.findViewById(R.id.dinner_input), foodNames);

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

        // Report
        TextView bmi = rootView.findViewById(R.id.bmi);
        TextView bmr = rootView.findViewById(R.id.bmr);
        TextView fp = rootView.findViewById(R.id.fp);
        TextView tbw = rootView.findViewById(R.id.tbw);
        TextView pm = rootView.findViewById(R.id.pm);
        TextView bmc = rootView.findViewById(R.id.bcm);
        reportLayout = rootView.findViewById(R.id.reportLayout);

        // Male, Female
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        String userDOB = (String) userData.get("DOB");
        if (userDOB == null || userDOB.isEmpty()){
            userDOB = currentDate.toString();
        }
        LocalDate dob = LocalDate.parse(userDOB, formatter);

        double age = Period.between(dob, currentDate).getYears();

        double height = (Double) userData.get("height");
        double weight = (Double) userData.get("weight");
        String gender = (String) userData.get("gender");

        if(height==0 || weight==0 || gender==null || gender.isEmpty()){
            reportLayout.removeAllViews();

            // Create a new TextView for displaying the BMI
            TextView reportTextView = new TextView(getContext());
            reportTextView.setText("Please fill your details in Profile to see the report result");
            reportTextView.setTextSize(18);
            reportTextView.setPadding(8, 8, 8, 8);

            // Add the BMI TextView to the layout
            reportLayout.addView(reportTextView);
        } else {
            double BMI_value, TBW_value, BMC_value;
            double BMR_value, FAT_value, PM_value;

            BMI_value = (weight*2.20462*703)/(height*height*0.393701*0.393701);
            BMI_value = weight/(height*height/10000);

            try {
                String currentDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                // Initialize database helper
                databaseHelper = new DatabaseHelper(getContext());
                databaseHelper.insertBMI(currentDateStr, height, weight, BMI_value);
                databaseHelper.close();
                bmi.setText(String.format("%.2f kg/M2", BMI_value));
            } catch (Exception e){
                Toast.makeText(getContext(), "Error in saving BMI data", Toast.LENGTH_SHORT).show();
            }

            TBW_value = 0.6 * weight;
            BMC_value = 0.04 * weight;

            // online
            TBW_value = 2.447 - (0.09145 * age) + (0.1074 * height) + (0.3362 * weight);
//            BMC_value = ;

            tbw.setText(String.format("%.2f L", TBW_value));
            bmc.setText(String.format("%.2f kg", BMC_value));

            if(gender.equals("Male")){
                BMR_value = (10*weight) + (6.25*height) - (5*age) + 5;
                FAT_value = (1.2*BMI_value) + (0.23*age) - 10.8 - 5.4;
                PM_value = 0.2 * (weight * (1 - FAT_value/100));

                // online
                BMR_value = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);


            } else {
                BMR_value = (10*weight) + (6.25*height) - (5*age) - 161;
                FAT_value = (1.2*BMI_value) + (0.23*age) - 5.4;
                PM_value = 0.2 * (weight * (1 - FAT_value/100));

                // online
                BMR_value = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
            }

            bmr.setText(String.format("%.2f Kcal/day", BMR_value));
            fp.setText(String.format("%.2f %%", FAT_value));
            pm.setText(String.format("%.2f kg", PM_value));
        }


        // Load BMI Scale from asserts
//        try {
//            InputStream inputStream = requireContext().getAssets().open("bmi scale.jpg");
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            ImageView bmiScale = rootView.findViewById(R.id.bmi_scale);
//            bmiScale.setImageBitmap(bitmap);
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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