package com.example.a20262cpm1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a20262cpm1.API.ApiService;
import com.example.a20262cpm1.Clases.Dispositivo;
import com.example.a20262cpm1.Clases.MenuAdapter;
import com.example.a20262cpm1.Clases.MenuItem;
import com.example.a20262cpm1.Clases.SesionData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Welcome extends AppCompatActivity implements MenuAdapter.OnItemClickListener
{
    private SesionData sesionManager;
    private Dispositivo dispositivo;

    private TextView tvBienvenida, tvNombreUsuario, tvEmail;
    private TextView tvModelo, tvVersion, tvDeviceId;
    private Button btnCerrarSesion;

    private RecyclerView ryMenu;
    private MenuAdapter adapter;
    private List<MenuItem> menuItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        sesionManager = new SesionData(this);
        dispositivo = new Dispositivo(this);

        tvBienvenida=findViewById(R.id.tvBienvenida);
        tvNombreUsuario=findViewById(R.id.tvNombreUsuario);
        tvEmail=findViewById(R.id.tvEmail);
        btnCerrarSesion=findViewById(R.id.btnCerrarSesion);

        if (!sesionManager.isLoggedIn())
        {
            irALogin();
            return;
        }

        //inicializarVistas();
        cargarDatos();
        configurarEventos();

        iniciarMenu();
    }
    private void iniciarMenu()
    {
        ryMenu=findViewById(R.id.rvMenu);
        ryMenu.setLayoutManager(new GridLayoutManager(this, 2));
        menuItemList = new ArrayList<>();
        adapter=new MenuAdapter(this,menuItemList, this);
        ryMenu.setAdapter(adapter);

        cargarMenu();
    }
    public void cargarMenu()
    {
        menuItemList.clear();
        String packageBase="com.example.a20262cpm1.Activities.";
        // 🔥 Por ahora estático - DESPUÉS vendrá de la BD
        menuItemList.add(new MenuItem(
                1,
                "Dispositivos",
                "Ver registrados",
                R.drawable.icono,
                "#667eea",
                ""
        ));

        menuItemList.add(new MenuItem(
                2,
                "Bitácora",
                "Historial de acciones",
                R.drawable.icono,
                "#4CAF50",
                ""
        ));

        // Cuando lo traigas de la BD, sería algo así:
        // menuItems = apiService.obtenerMenuUsuario(userId);

        // 🔥 Notificar al adapter que los datos cambiaron
        adapter.notifyDataSetChanged();
    }
    public void onItemClick(MenuItem item)
    {
        abrirActivityDinamico(item);
    }
    private void abrirActivityDinamico(MenuItem item)
    {
        try
        {
            // Validar que el nombre no esté vacío
            if (item.getNombreActivity() == null || item.getNombreActivity().trim().isEmpty())
            {
                Toast.makeText(this, "⚠️ El módulo '" + item.getTitulo() + "' no tiene Activity configurado", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 Reflexión: convertir el String en una clase
            Class<?> activityClass = Class.forName(item.getNombreActivity());

            // Crear el Intent con la clase obtenida
            Intent intent = new Intent(this, activityClass);

            // Opcional: pasar datos al Activity (como el título del módulo)
            intent.putExtra("TITULO_MODULO", item.getTitulo());
            intent.putExtra("MENU_ITEM_ID", item.getId()); // Si necesitas el ID

            startActivity(intent);

        } catch (ClassNotFoundException e) {
            // El Activity no existe o el nombre está mal escrito
            Toast.makeText(this, "❌ Error: No se encontró el módulo '" + item.getTitulo() + "'", Toast.LENGTH_SHORT).show();
            android.util.Log.e("MenuDinamico", "Activity no encontrado: " + item.getNombreActivity(), e);

        } catch (Exception e) {
            // Cualquier otro error
            Toast.makeText(this, "❌ Error al abrir el módulo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            android.util.Log.e("MenuDinamico", "Error inesperado", e);
        }
    }
    private void cargarDatos()
    {
        tvBienvenida.setText(obtenerSaludo());
        tvNombreUsuario.setText(sesionManager.getUserFullName());
        tvEmail.setText(sesionManager.getUserEmail());

        /*tvModelo.setText(dispositivo.getManufacturer() + " " + dispositivo.getModel());
        tvVersion.setText(dispositivo.getAndroidVersion() + " (API " + dispositivo.getApiLevel() + ")");
        tvDeviceId.setText(dispositivo.getPersistentId());*/
    }
    private String obtenerSaludo()
    {
        int hora = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hora < 12) return "🌅 Buenos días";
        else if (hora < 19) return "☀️ Buenas tardes";
        else return "🌙 Buenas noches";
    }
    private void configurarEventos()
    {
        btnCerrarSesion.setOnClickListener(v -> mostrarDialogoLogout());

        btnCerrarSesion.setOnLongClickListener(v -> {
            ejecutarLogout();
            return true;
        });
    }
    private void mostrarDialogoLogout()
    {
        new AlertDialog.Builder(this)
                .setTitle("🚪 Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?\n\n" +
                        "Usuario: " + sesionManager.getUserFullName())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar", (dialog, which) -> ejecutarLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarLogout()
    {
        btnCerrarSesion.setEnabled(false);
        btnCerrarSesion.setText("Cerrando sesión...");

        int usuarioId = sesionManager.getUserId();
        String deviceId = sesionManager.getDeviceId();

        ApiService.getInstance().logout(usuarioId, deviceId, new ApiService.ApiCallback()
        {
            @Override
            public void onSuccess(JSONObject response) {
                finalizarLogout();
            }

            @Override
            public void onError(String error) {
                finalizarLogout();
            }
        });
    }
    private void finalizarLogout() {
        runOnUiThread(() -> {
            sesionManager.cerrarSesion();

            Toast.makeText(Welcome.this,
                    "👋 ¡Hasta pronto!",
                    Toast.LENGTH_SHORT).show();

            irALogin();
        });
    }
    private void irALogin()
    {
        Intent intent = new Intent(Welcome.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed()
    {
        mostrarDialogoLogout();
    }
}