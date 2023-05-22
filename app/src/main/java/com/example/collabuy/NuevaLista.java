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
                        comprobarLista(user, nombreLista.getText().toString(), claveLista.getText().toString());
                }
            });

        // Crear lista
            crearLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // CREAR LISTA EN USUARIO
                    String user = SessionManager.getInstance(getApplicationContext()).getUsername();
                    crearNuevaLista(nombreLista.getText().toString(), claveLista.getText().toString(), user);
                }
            });
    }

    //comprobar si existe la lista y si ya está introducida en el usuario
    private void comprobarLista(String pUsuario, String pNombreLista, String pClaveLista){
        Data data = new Data.Builder()
                .putString("url", "anadirLista.php")
                .putString("usuario", pUsuario)
                .putString("nombre", pNombreLista)
                .putString("clave", pClaveLista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String resultado = workInfo.getOutputData().getString("resultado");
                            if(resultado != null && resultado.equals("No existe lista")){
                                // si existe la lista y está añadida
                                Toast.makeText(NuevaLista.this, "No existe la lista", Toast.LENGTH_SHORT).show();
                            }else if (resultado != null && resultado.equals("lista añadida")){
                                // si existe la lista y no está añadida
                                Toast.makeText(NuevaLista.this, "Nueva lista añadida", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NuevaLista.this, pantalla_bienvenida.class);
                                startActivity(intent);
                                finish();
                            }else if (resultado != null && resultado.equals("ya participaba")){
                                // si no existe la lista
                                Toast.makeText(NuevaLista.this, "Ya participa en la lista", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void crearNuevaLista(String pNomLista, String pClaveLista, String pUsuario){
        Data data = new Data.Builder()
                .putString("url", "creacionLista.php")
                .putString("usuario", pUsuario)
                .putString("nombre", pNomLista)
                .putString("clave", pClaveLista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String resultado = workInfo.getOutputData().getString("resultado");
                            if(resultado != null && resultado.equals("error de creacion")){
                                // si existe la lista y está añadida
                                Toast.makeText(NuevaLista.this, "Error de creacion", Toast.LENGTH_SHORT).show();
                            }else if (resultado != null && resultado.equals("lista creada")){
                                // si existe la lista y no está añadida
                                Toast.makeText(NuevaLista.this, "Nueva lista creada", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NuevaLista.this, pantalla_bienvenida.class);
                                startActivity(intent);
                                finish();
                            }else if (resultado != null && resultado.equals("lista existente")){
                                // si no existe la lista
                                Toast.makeText(NuevaLista.this, "Ya existe la lista", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}
