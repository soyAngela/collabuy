package com.example.collabuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Sugerencias extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> lista = new ArrayList<>();
    SearchView searchView;
    ImageView botonAnadir;
    String nombreLista;
    String listId;
    private ArrayList<String> idList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);

        // Se recuperan los datos de la lista
        Bundle extras = getIntent().getExtras();
        listId = extras.getString("idLista");
        nombreLista = extras.getString("nombreLista");

        // Declarar los objetos
        listView = findViewById(R.id.listaSugerencias);
        searchView = findViewById(R.id.searchView);
        botonAnadir = findViewById(R.id.botonAnadir);

        // Boton de anadir sugerencias
        botonAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto();
            }
        });

        rellenarLista();

        // Se crea el filtro para la lista
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (lista.contains(query)) {
                    adapter.getFilter().filter(query);
                } else {
                    Toast.makeText(Sugerencias.this, "Not found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    rellenarLista();
                }
                if(adapter != null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        // Al hacer click en un elemento de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idProd = idList.get(position);
                anadirElemento(listId, idProd);
            }
        });
    }

    // Metodo que lanza la peticion para anadir un nuevo elemento a la lista
    private void anadirElemento(String listId, String idProd) {
        Data data = new Data.Builder()
                .putString("url", "anadir_sugerencia_lista.php")
                .putString("idLista",listId)
                .putString("idProd", idProd)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            if(workInfo.getOutputData().getString("resultado") != null && workInfo.getOutputData().getString("resultado").equals("1")){
                                Intent intent = new Intent(Sugerencias.this, ListActivity.class);
                                Sugerencias.this.startActivity(intent);
                            }else{
                                Toast.makeText(Sugerencias.this, "No se ha podido añadir la sugerencia.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    // Metodo que lanza la peticion para recuperar todos los elemntos sugeribles
    private void rellenarLista() {
        ArrayList<String> sugerencias = null;
        Data data = new Data.Builder()
                .putString("url", "obtenerListaSugerencias.php")
                .putString("usuario", SessionManager.getInstance(getApplicationContext()).getUsername())
                .putString("listId", listId)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null){
                                String resultado = workInfo.getOutputData().getString("resultado");
                                if(resultado!=null) {
                                    JSONArray jsonArray = null;
                                    try {
                                        //Se transforma a un jsonArray el String con el resultado
                                        jsonArray = new JSONArray(resultado);
                                        mostrarLista(jsonArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }else{
                                Toast.makeText(Sugerencias.this, "No se han podido cargar las sugerencias.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    // Lanza la actividad para crear un nuevo producto
    private void crearProducto() {
        Intent intent = new Intent(Sugerencias.this, CreacionProducto.class);
        intent.putExtra("lista", listId);
        intent.putExtra("nombreLista", nombreLista);
        startActivity(intent);
        finish();
    }

    // Metodo que muestra la lista de sugerencias
    private void mostrarLista(JSONArray sugerencias) throws JSONException {
        //TODO: Pasar de JSONArray a Array<List>
        for(int i = 0; i<sugerencias.length(); i++){
            JSONObject elemento = (JSONObject) sugerencias.get(i);
            lista.add(elemento.getString("nombre"));
            idList.add(elemento.getString("id"));
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);
    }
}