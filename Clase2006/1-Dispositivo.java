package com.example.a20262cpm1.Clases;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import org.json.JSONObject;
import java.util.UUID;

public class Dispositivo
{
    private final Context context;

    public Dispositivo(Context context)
    {
        this.context = context;
    }
    // OS Information
    public String getAndroidVersion()
    {
        return Build.VERSION.RELEASE;
    }
    public int getApiLevel()
    {
        return Build.VERSION.SDK_INT;
    }
    public String getSecurityPatch()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return Build.VERSION.SECURITY_PATCH;
        }
        return "N/A";
    }

    // Device Information
    public String getManufacturer()
    {
        return Build.MANUFACTURER;
    }

    public String getModel()
    {
        return Build.MODEL;
    }

    public String getBrand()
    {
        return Build.BRAND;
    }

    // Unique Device ID (ANDROID_ID)
    public String getAndroidId()
    {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    // Persistent UUID (survives everything except uninstall)
    public String getPersistentId()
    {
        SharedPreferences prefs = context.getSharedPreferences(
                "device_prefs", Context.MODE_PRIVATE
        );
        String id = prefs.getString("persistent_device_id", null);

        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString("persistent_device_id", id).apply();
        }

        return id;
    }
    // Full device info as JSON (perfect for your PHP API)
    public JSONObject getFullInfoAsJson()
    {
        try {
            JSONObject json = new JSONObject();
            json.put("android_version", getAndroidVersion());
            json.put("api_level", getApiLevel());
            json.put("manufacturer", getManufacturer());
            json.put("model", getModel());
            json.put("brand", getBrand());
            json.put("android_id", getAndroidId());
            json.put("persistent_id", getPersistentId());
            json.put("security_patch", getSecurityPatch());
            return json;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    // Get a formatted string for display
    public String getDeviceInfoString()
    {
        return String.format(
                "%s %s\nAndroid %s (API %d)\nID: %s",
                getManufacturer(),
                getModel(),
                getAndroidVersion(),
                getApiLevel(),
                getAndroidId()
        );
    }
}