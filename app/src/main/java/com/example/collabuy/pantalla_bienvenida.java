package com.example.collabuy;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.collabuy.adaptadores.ListaListasOverview;

import org.json.JSONArray;
import org.json.JSONException;

public class pantalla_bienvenida extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // configurar xml dependiendo de la orientación
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.usuario_bienvenida_land);
        }else{
            setContentView(R.layout.usuario_bienvenida);
        }
        getSupportActionBar().hide();

    // declaración de objetos
        Button botonNuevaLista = findViewById(R.id.botonNuevaLista);
        TextView nomUsuario = findViewById(R.id.text_nomUsu);
        Button botonCerrarSesion = findViewById(R.id.btn_logout);

    // obtener datos de BBDD y cargar en la lista y usuario en textView
        String user = SessionManager.getInstance(getApplicationContext()).getUsername();
        nomUsuario.setText(user);
        obtenerListaListas(user);

    // cuando se pulsa el botón de nueva lista
        botonNuevaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(pantalla_bienvenida.this, NuevaLista.class);
                startActivity(intent);
            }
        });

    // pulsar el botón de Logout o "X"
    botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(pantalla_bienvenida.this, "Se ha cerrado la sesión", Toast.LENGTH_SHORT).show();
            SessionManager.getInstance(getApplicationContext()).clearSession();
            Intent intent = new Intent(pantalla_bienvenida.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    });
    }

    public void obtenerListaListas(String pUsuario){

        Data data = new Data.Builder()
                .putString("url", "obtenerListaListas.php")
                .putString("usuario",pUsuario)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null && !workInfo.getOutputData().getString("resultado").equals("null")){
                                // cargar los datos obtenidos en la lista
                                String resultado = workInfo.getOutputData().getString("resultado");
                                if(resultado!=null) {
                                    JSONArray jsonArray = null;
                                    try {
                                        //Se transforma a un jsonArray el String con el resultado
                                        jsonArray = new JSONArray(resultado);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mostrarListas(jsonArray);
                                }

                            }else{
                                Toast.makeText(pantalla_bienvenida.this, "No existen listas.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void mostrarListas(JSONArray pListaFinal) {
        //llamada a metodo que coloque nombre y clave en overview + asignar onclick con idLista
        ListView listaListas = findViewById(R.id.listaNombres);
        ListaListasOverview adaptador = new ListaListasOverview(pListaFinal, this);
        listaListas.setAdapter(adaptador);

    }

}
