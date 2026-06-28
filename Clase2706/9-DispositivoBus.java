package com.example.a20262cpm1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.a20262cpm1.API.ApiService;
import com.example.a20262cpm1.Componentes.ToastPersonalizado;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class DispositivoBus extends AppCompatActivity
{
    private Button btnGenerar;
    private TableLayout tableLayout;
    private ProgressBar progressBar;
    private TextView tvMensaje;
    private TextView tvTituloResultados;
    private androidx.cardview.widget.CardView cardResultados;
    private Button btnFechaInicial, btnFechaFinal;
    private LinearLayout contenedorResultados;
    private int yearInicial, monthInicial, dayInicial;
    private int yearFinal, monthFinal, dayFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivo_bus);

        btnFechaInicial = findViewById(R.id.btnFechaInicial);
        btnFechaFinal = findViewById(R.id.btnFechaFinal);
        btnGenerar = findViewById(R.id.btnGenerar);
        contenedorResultados = findViewById(R.id.contenedorResultados);
        progressBar = findViewById(R.id.progressBar);
        tvMensaje = findViewById(R.id.tvMensaje);

        // Fecha inicial = hoy
        Calendar calendar = Calendar.getInstance();
        yearInicial = calendar.get(Calendar.YEAR);
        monthInicial = calendar.get(Calendar.MONTH);
        dayInicial = calendar.get(Calendar.DAY_OF_MONTH);
        btnFechaInicial.setText("📅 Inicial: " + String.format("%02d/%02d/%04d", dayInicial, monthInicial+1, yearInicial));

        // Fecha final = hoy
        yearFinal = calendar.get(Calendar.YEAR);
        monthFinal = calendar.get(Calendar.MONTH);
        dayFinal = calendar.get(Calendar.DAY_OF_MONTH);
        btnFechaFinal.setText("📅 Final: " + String.format("%02d/%02d/%04d", dayFinal, monthFinal+1, yearFinal));

        // Abrir DatePickerDialog al tocar los botones
        btnFechaInicial.setOnClickListener(v -> mostrarDatePicker(true));
        btnFechaFinal.setOnClickListener(v -> mostrarDatePicker(false));

        btnGenerar.setOnClickListener(v -> generarReporte());

        tvTituloResultados = findViewById(R.id.tvTituloResultados);
        cardResultados = findViewById(R.id.cardResultados);
    }
    // Método para mostrar el calendario flotante
    private void mostrarDatePicker(boolean esInicial)
    {
        int year = esInicial ? yearInicial : yearFinal;
        int month = esInicial ? monthInicial : monthFinal;
        int day = esInicial ? dayInicial : dayFinal;

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            if (esInicial)
            {
                yearInicial = y; monthInicial = m; dayInicial = d;
                btnFechaInicial.setText("📅 Inicial: " + String.format("%02d/%02d/%04d", d, m+1, y));
            }
            else
            {
                yearFinal = y; monthFinal = m; dayFinal = d;
                btnFechaFinal.setText("📅 Final: " + String.format("%02d/%02d/%04d", d, m+1, y));
            }
        }, year, month, day);
        dialog.show();
    }
    public void generarReporte()
    {
        String fechaInicial = String.format("%04d-%02d-%02d", yearInicial, monthInicial + 1, dayInicial);
        String fechaFinal   = String.format("%04d-%02d-%02d", yearFinal,   monthFinal   + 1, dayFinal);

        // Validar que la fecha inicial no sea mayor a la final
        if (fechaInicial.compareTo(fechaFinal) > 0)
        {
            //Toast.makeText(this, "La fecha inicial no puede ser mayor a la final", Toast.LENGTH_SHORT).show();
            ToastPersonalizado.error(this,"La fecha inicial no puede ser mayor a la final");
            return;
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            tvMensaje.setVisibility(View.GONE);
            contenedorResultados.removeAllViews();
            btnGenerar.setEnabled(false);

            ApiService.getInstance().consultarDispositivos(fechaInicial, fechaFinal, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            procesarRespuesta(response);
                        }
                    });
                }

                @Override
                public void onError(String error)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            progressBar.setVisibility(View.GONE);
                            btnGenerar.setEnabled(true);
                            tvMensaje.setText("❌ Error: " + error);
                            tvMensaje.setTextColor(0xFFD32F2F);
                            tvMensaje.setVisibility(View.VISIBLE);
                            ToastPersonalizado.error(DispositivoBus.this, "Error de conexión");
                        }
                    });
                }
            });
        }
    }
    private void procesarRespuesta(JSONObject response)
    {
        progressBar.setVisibility(View.GONE);
        btnGenerar.setEnabled(true);

        try
        {
            // Verificar status de la respuesta
            String status = response.optString("status", "error");
           //ToastPersonalizado.info(this,response.toString());
            if (!status.equalsIgnoreCase("success") && !status.equalsIgnoreCase("ok"))
            {
                String msg = response.optString("message", "Sin resultados");
                tvMensaje.setText("⚠️ " + msg);
                tvMensaje.setTextColor(0xFFF57C00);
                tvMensaje.setVisibility(View.VISIBLE);
                return;
            }

            // Obtener el array de dispositivos
            JSONArray dispositivos = response.optJSONArray("data");
            if (dispositivos == null || dispositivos.length() == 0)
            {
                tvMensaje.setText("No se encontraron dispositivos en el rango seleccionado");
                tvMensaje.setTextColor(0xFFF57C00);
                tvMensaje.setVisibility(View.VISIBLE);
                return;
            }

            // Inflar cada item_dispositivo.xml
            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = 0; i < dispositivos.length(); i++)
            {
                JSONObject dispo = dispositivos.getJSONObject(i);

                View itemView = inflater.inflate(R.layout.item_dispositivo, contenedorResultados, false);

                ((TextView) itemView.findViewById(R.id.tvItemId)).setText(dispo.optString("dispo_unique_id", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemEquipo)).setText(dispo.optString("dispo_nombre_equipo", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemMarca)).setText(dispo.optString("dispo_marca", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemModelo)).setText(dispo.optString("dispo_modelo", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemSo)).setText(dispo.optString("dispo_so", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemVersion)).setText(dispo.optString("dispo_so_version", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemMac)).setText(dispo.optString("dispo_mac", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemFRegistro)).setText(dispo.optString("dispo_fregistro", "-"));
                ((TextView) itemView.findViewById(R.id.tvItemFActual)).setText(dispo.optString("dispo_factual", "-"));

                contenedorResultados.addView(itemView);
            }

            // Mostrar el título y la tarjeta de resultados
            tvTituloResultados.setVisibility(View.VISIBLE);
            cardResultados.setVisibility(View.VISIBLE);

            tvMensaje.setText("✅ Total de registros: " + dispositivos.length());
            tvMensaje.setTextColor(0xFF388E3C);
            tvMensaje.setVisibility(View.VISIBLE);

        }
        catch (Exception e)
        {
            tvMensaje.setText("Error al procesar datos: " + e.getMessage());
            tvMensaje.setTextColor(0xFFD32F2F);
            tvMensaje.setVisibility(View.VISIBLE);

            tvTituloResultados.setVisibility(View.GONE);
            cardResultados.setVisibility(View.GONE);

            e.printStackTrace();


        }
    }
}