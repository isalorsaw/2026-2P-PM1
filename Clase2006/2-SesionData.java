package com.example.a20262cpm1.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;

public class SesionData
{
    private static final String PREF_NAME = "app_sesion";
    private static final String KEY_IS_LOGGED = "is_logged";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_FULLNAME = "user_fullname";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_DEVICE_ID = "device_id";

    private final SharedPreferences prefs;

    public SesionData(Context context)
    {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    // Guardar datos después del login
    public void guardarSesion(JSONObject usuario, String deviceId)
    {
        try
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_IS_LOGGED, true);
            editor.putInt(KEY_USER_ID, usuario.getInt("id"));
            editor.putString(KEY_USER_NAME, usuario.getString("usuario"));
            editor.putString(KEY_USER_FULLNAME, usuario.getString("nombre_completo"));
            editor.putString(KEY_USER_EMAIL, usuario.optString("correo", ""));
            editor.putString(KEY_DEVICE_ID, deviceId);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // Verificar si hay sesión activa
    public boolean isLoggedIn()
    {
        return prefs.getBoolean(KEY_IS_LOGGED, false);
    }
    // Obtener datos
    public int getUserId() { return prefs.getInt(KEY_USER_ID, 0); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, ""); }
    public String getUserFullName() { return prefs.getString(KEY_USER_FULLNAME, ""); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, ""); }
    public String getDeviceId() { return prefs.getString(KEY_DEVICE_ID, ""); }

    // Cerrar sesión
    public void cerrarSesion()
    {
        prefs.edit().clear().apply();
    }
}