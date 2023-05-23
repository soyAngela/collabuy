package com.example.collabuy;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class pantalla_bienvenida extends AppCompatActivity {
    private ArrayList<String[]> listaFinal = new ArrayList<String[]>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // configurar xml dependiendo de la orientación
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.usuario_bienvenida_land);
        }else{
            setContentView(R.layout.usuario_bienvenida);
        }

    // declaración de objetos

        Button botonNuevaLista = findViewById(R.id.botonNuevaLista);

    // obtener datos de BBDD y cargar en la lista

        // De manera manual --> Funciona
        /*ArrayList<String> datos = new ArrayList<String>();
        datos.add("Cumple Lucas");
        datos.add("Fiesta fin de curso");
        datos.add("Compra del miércoles");*/
        //ListaListasOverview adaptador = new ListaListasOverview(datos, getApplicationContext());
        //listaListas.setAdapter(adaptador);

        // De la bbdd remota falta obtener el usuario que ha iniciado sesion
        String user = SessionManager.getInstance(getApplicationContext()).getUsername();
        obtenerListaListas("lucas");

    // cuando se pulsa el botón de nueva lista
        botonNuevaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            if(workInfo.getOutputData().getString("resultado") != null){
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
