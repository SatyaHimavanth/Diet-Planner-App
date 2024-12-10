package com.example.pubfitnessstudio.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private final MutableLiveData<List<String>> foodNamesLiveData = new MutableLiveData<>();


    public void setFoodNames(List<String> foodNames) {
        foodNamesLiveData.setValue(foodNames);
    }

    public LiveData<List<String>> getFoodNames() {
        return foodNamesLiveData;
    }
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
