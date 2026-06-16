private void verifBD()
    {
        ApiService.getInstance().checkConnection(new ApiService.ApiCallback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                // 🔴 IMPORTANTE: volver al hilo principal para mostrar Toast
                runOnUiThread(() -> {
                    try
                    {
                        boolean conectado = response.getBoolean("conectado");
                        String mensaje = response.getString("mensaje");
                        String version = response.getString("version_servidor");

                        if (conectado)
                        {
                            //Toast.makeText(MainActivity.this, "✅ " + mensaje + "\nMySQL: " + version, Toast.LENGTH_LONG).show();
                            //ToastPersonalizado.exito(MainActivity.this, "✅ " + mensaje + "\nMySQL: " + version);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "❌ No conectado", Toast.LENGTH_SHORT).show();
                            //ToastPersonalizado.error(MainActivity.this, "❌ No conectado");
                        }

                        Log.d("API", "Respuesta completa: " + response.toString());
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this, "Error al leer respuesta", Toast.LENGTH_SHORT).show();
                        //ToastPersonalizado.error(MainActivity.this, "Error al leer respuesta");
                    }
                    finally
                    {

                    }
                });
            }
            @Override
            public void onError(String error)
            {
                runOnUiThread(() -> {
                    //Toast.makeText(MainActivity.this, "❌ Error: " + error, Toast.LENGTH_LONG).show();
                    //ToastPersonalizado.error(MainActivity.this, "Error "+error);
                    Log.d("*********ERROR CONSOLA",error);
                });
            }
        });
    }