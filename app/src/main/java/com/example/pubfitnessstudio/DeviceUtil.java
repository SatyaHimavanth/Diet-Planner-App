package com.example.pubfitnessstudio;


import android.provider.Settings;
import android.content.Context;

public class DeviceUtil {
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
