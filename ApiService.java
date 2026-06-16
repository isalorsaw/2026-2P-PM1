package com.example.a20262cpm1.API;
import android.content.Context;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService
{
    // 🔴 CAMBIA ESTA URL por tu dominio real
    //private static final String BASE_URL = "https://tudominio.com/api"; //Casa
    private static String CHECKBD="/api/auth/checkdb.php";
    private static String LOGIN="/api/auth/login.php";

    private final OkHttpClient client;
    private static ApiService instance;

    // Patrón Singleton (opcional pero recomendado)
    public static ApiService getInstance()
    {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }
    public ApiService()
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }
    // Callback reutilizable
    public interface ApiCallback
    {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
    // ============================================
    // 1. VERIFICAR CONEXIÓN A LA BD
    // ============================================
    public void checkConnection(ApiCallback callback)
    {
        Request request = new Request.Builder()
                .url(BASE_URL + CHECKBD)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        executeRequest(request, callback);
    }
    // ============================================
    // 2. LOGIN (CONSULTAR USUARIO Y CLAVE)
    // ============================================
    public void login(String usuario, String clave, ApiCallback callback)
    {
        try
        {
            // Crear el JSON con los datos
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user", usuario);
            jsonBody.put("pass", clave);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + LOGIN)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            executeRequest(request, callback);

        }
        catch (Exception e)
        {
            callback.onError("Error al construir la petición: " + e.getMessage());
        }
    }
    //Ejecutar Logout=================================================
    public void logout(int usuarioId, String deviceId, ApiCallback callback)
    {
        try
        {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario_id", usuarioId);
            jsonBody.put("device_id", deviceId);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + LOGOUT)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            executeRequest(request, callback);

        }
        catch (Exception e)
        {
            callback.onError("Error al construir petición de logout: " + e.getMessage());
        }
    }
}