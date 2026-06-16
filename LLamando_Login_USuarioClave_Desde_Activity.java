private void verifLogin()
    {
        String usuario = txtuser.getText().toString().trim();
        String clave = txtpass.getText().toString().trim();

        // Validación local
        if (usuario.isEmpty() || clave.isEmpty())
        {
            Toast.makeText(this, "Ingrese usuario y clave", Toast.LENGTH_SHORT).show();
            //ToastPersonalizado.advertencia(this, "⚠️ Ingrese Usuario y Clave");
            return;
        }

        ApiService.getInstance().login(usuario, clave, new ApiService.ApiCallback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                runOnUiThread(() -> {
                    try
                    {
                        boolean exito = response.getBoolean("exito");
                        String mensaje = response.getString("mensaje");

                        if (exito)
                        {
                            Toast.makeText(MainActivity.this,"✅ " + mensaje,Toast.LENGTH_SHORT).show();
                            //ToastPersonalizado.exito(MainActivity.this, "✅ " + mensaje);

                            // Obtener datos del usuario
                            JSONObject user = response.getJSONObject("usuario");

                            Log.d("LOGIN", "Bienvenido: ");

							//Llamo el Siguiente Activity
                            Intent intent=new Intent(MainActivity.this,Welcome.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "❌ " + mensaje, Toast.LENGTH_LONG).show();
                            //ToastPersonalizado.error(MainActivity.this, "❌ Usuario o contraseña incorrectos");
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this,"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("*********ERROR CONSOLA",e.getMessage());
                        //ToastPersonalizado.error(MainActivity.this, "Error: " + e.getMessage());
                    }
                    finally {
                    }
                });
            }
            @Override
            public void onError(String error)
            {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "❌ Error: " + error, Toast.LENGTH_LONG).show();
                    Log.d("*********ERROR CONSOLA",error);
                    //ToastPersonalizado.error(MainActivity.this, "❌ Error: " + error);
                    //progressBar.setVisibility(View.GONE);
                    //btnLogin.setEnabled(true);
                });
            }
        });
    }