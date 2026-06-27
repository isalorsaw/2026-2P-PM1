package com.example.a20262cpm1.Componentes;

import android.content.Context;
import es.dmoral.toasty.Toasty;

public class ToastPersonalizado
{

    // ✅ ÉXITO (verde)
    public static void exito(Context context, String mensaje)
    {
        Toasty.success(context, mensaje, Toasty.LENGTH_LONG, true).show();
    }

    // ❌ ERROR (rojo)
    public static void error(Context context, String mensaje)
    {
        Toasty.error(context, mensaje, Toasty.LENGTH_LONG, true).show();
    }

    // ⚠️ ADVERTENCIA (amarillo)
    public static void advertencia(Context context, String mensaje)
    {
        Toasty.warning(context, mensaje, Toasty.LENGTH_LONG, true).show();
    }

    // ℹ️ INFORMACIÓN (azul)
    public static void info(Context context, String mensaje)
    {
        Toasty.info(context, mensaje, Toasty.LENGTH_LONG, true).show();
    }

    // ❓ PREGUNTA (morado)
    public static void pregunta(Context context, String mensaje)
    {
        Toasty.normal(context, mensaje, android.R.drawable.ic_dialog_info).show();
    }
}