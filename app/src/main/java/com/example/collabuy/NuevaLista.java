package com.example.collabuy;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;

public class NuevaLista extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // Configurar xml dependiendo de la orientación del teléfono
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.nueva_lista_land);
        }else{
            setContentView(R.layout.nueva_lista);
        }

    // Declaración de objetos

        EditText nombreLista = findViewById(R.id.editText_nombre);
        EditText claveLista = findViewById(R.id.editText_clave);

        Button anadirLista = findViewById(R.id.btn_anadir_lista);
        Button crearLista = findViewById(R.id.btn_crear_lista);

    // Funcionalidades de los botones

        // Añadir lista
            anadirLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // AÑADIR LISTA A USUARIO
                        String user = SessionManager.getInstance(getApplicationContext()).getUsername();
                        comprobarLista(user, claveLista.getText().toString());
                        // anadirLista(claveLista.getText(), usuario);
                        //      --> como obtener usuario?
                        //                      + accedemos a la lista después de añadirla?
                }
            });

        // Crear lista
            crearLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // CREAR LISTA EN USUARIO
                    String user = SessionManager.getInstance(getApplicationContext()).getUsername();
                    comprobarLista(user, claveLista.getText().toString());
                    // crearLista(nombreLista.getText(), claveLista.getText(), usuario);
                    //          --> obtener usuario como parámetro?
                }
            });
    }

    //comprobar si existe la lista y si ya está introducida en el usuario
    private void comprobarLista(String pUsuario, String pClaveLista){
        Data data = new Data.Builder()
                .putString("url", "comprobarLista.php")
                .putString("usuario", pUsuario)
                .putString("clave", pClaveLista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String resultado = workInfo.getOutputData().getString("resultado");
                            if(resultado != null && resultado.equals("1")){
                                // si existe la lista y está añadida
                                Toast.makeText(NuevaLista.this, "Ya está añadida", Toast.LENGTH_SHORT).show();
                            }else if (resultado != null && resultado.equals("2")){
                                // si existe la lista y no está añadida
                                EditText claveLista = findViewById(R.id.editText_clave);
                                String user = SessionManager.getInstance(getApplicationContext()).getUsername();
                                añadirLista(user, claveLista.getText().toString());
                            }else{
                                // si no existe la lista
                                Toast.makeText(NuevaLista.this, "No existe la lista", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    //Se añade una lista creada a la lista del usuario
    private void añadirLista(String pUsuario, String pClaveLista) {
        Data data = new Data.Builder()
                .putString("url", "anadirLista.php")
                .putString("usuario", pUsuario)
                .putString("clave", pClaveLista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null && workInfo.getOutputData().getString("resultado").equals("1")){
                                Toast.makeText(NuevaLista.this, "Se ha añadido la lista", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(NuevaLista.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(NuevaLista.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
        });
    }
}
