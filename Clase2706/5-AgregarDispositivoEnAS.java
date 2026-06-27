package com.example.a20262cpm1.Clases;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class Dispositivo {

    private Context context;
    private String uniqueId;
    private String nombreEquipo;
    private String marca;
    private String modelo;
    private String sistemaOperativo;
    private String versionSO;
    private String dirMac;

    public Dispositivo(Context context) {
        this.context = context.getApplicationContext();
        inicializarInformacion();
    }

    private void inicializarInformacion() {
        // Unique ID (Android ID)
        this.uniqueId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // Nombre del equipo (modelo)
        this.nombreEquipo = Build.MODEL;

        // Marca del fabricante
        this.marca = Build.MANUFACTURER;

        // Modelo del dispositivo
        this.modelo = Build.MODEL;

        // Sistema operativo
        this.sistemaOperativo = "Android";

        // Versión de Android
        this.versionSO = Build.VERSION.RELEASE;

        // MAC Address (limitado en Android 6+)
        this.dirMac = obtenerMacAddress();
    }

    private String obtenerMacAddress()
    {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null && tm.getDeviceId() != null) {
                // En Android 6+ esto retorna null o un valor genérico
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // GETTERS
    public String getUniqueId() {
        return uniqueId;
    }

    public String getModel() {
        return modelo;
    }

    public String getManufacturer() {
        return marca;
    }

    public String getAndroidVersion() {
        return versionSO;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public String getDirMac() {
        return dirMac;
    }

    // Método adicional para obtener información completa del dispositivo
    public String getDispositivoCompleto() {
        return String.format("%s %s - Android %s", marca, modelo, versionSO);
    }

    // Método para obtener el SDK version (útil para validaciones)
    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}