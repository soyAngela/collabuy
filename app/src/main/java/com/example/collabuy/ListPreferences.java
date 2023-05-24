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

public class ListPreferences extends AppCompatActivity{
    private String listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar xml dependiendo de la orientación del teléfono
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.preferencias_lista_land);
        }else{
            setContentView(R.layout.preferencias_lista);
        }
        getSupportActionBar().hide();

        //Declaracion de objetos
        EditText nombreLista = findViewById(R.id.editText_nombre);
        EditText claveLista = findViewById(R.id.editText_clave);

        Button botonEditar = findViewById(R.id.btn_editar_lista);
        Button botonCancelar = findViewById(R.id.btn_cancelar);

        Bundle extras = getIntent().getExtras();
        listId = extras.getString("listId");
        listName = extras.getString("nombreLista");
        nombreLista.setText(listName);

        //Funcionalidades

        //Inicializar el texto de los editText a los que tiene la lista

        //Cambiar el nombre y clave a la lista
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevoNombre = nombreLista.getText().toString();
                String nuevaClave = claveLista.getText().toString();
                if (!nuevoNombre.isEmpty() && !nuevaClave.isEmpty()){
                    editarLista(nuevoNombre, nuevaClave);
                }else{
                    Toast.makeText(ListPreferences.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Si se pulsa cancelar
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListPreferences.this, ListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void editarLista(String pNuevoNombre, String pNuevaClave) {
        Data data = new Data.Builder()
                .putString("url", "editarLista.php")
                .putString("id", listId)
                .putString("nombre",pNuevoNombre)
                .putString("clave", pNuevaClave)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null){
                                // cargar los datos obtenidos en la lista
                                String resultado = workInfo.getOutputData().getString("resultado");
                                if(resultado != null && resultado.equals("lista actualizada")){
                                    Toast.makeText(ListPreferences.this, "Lista actualizada.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ListPreferences.this, pantalla_bienvenida.class);
                                    startActivity(intent);
                                    finish();
                                }else if (resultado != null && resultado.equals("ya existe")){
                                    Toast.makeText(ListPreferences.this, "Ya existe esa lista.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ListPreferences.this, "Error", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}