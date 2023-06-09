package com.example.collabuy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.collabuy.adaptadores.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ListActivity extends AppCompatActivity {

    private String listId;
    private Activity activityLista = this;
    private String nombreLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle extras = getIntent().getExtras();
        listId = extras.getString("idLista");
        nombreLista = extras.getString("nombreLista");

        waitForList();
        getSupportActionBar().setTitle(nombreLista);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.list_add:
                Intent i = new Intent(this, Sugerencias.class);
                i.putExtra("idLista", listId);
                i.putExtra("nombreLista", nombreLista);
                activityLista.startActivityForResult(i, 1);
                break;
            case R.id.list_abandon:
                String user = SessionManager.getInstance(getApplicationContext()).getUsername();
                abandonList(this.listId, user);
                break;
            case R.id.list_settings:
                Intent intent = new Intent(this, ListPreferences.class);
                intent.putExtra("nombreLista", nombreLista);
                intent.putExtra("listId", listId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abandonList(String plistId, String pUser) {
        Data data = new Data.Builder()
                .putString("url", "abandonarLista.php")
                .putString("usuario", pUser)
                .putString("id", plistId)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String resultado = workInfo.getOutputData().getString("resultado");
                            if(resultado != null && resultado.equals("lista eliminada")){
                                // si existe la lista y está añadida
                                Toast.makeText(ListActivity.this, "Se ha eliminado la lista", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ListActivity.this, pantalla_bienvenida.class);
                                startActivity(intent);
                                finish();
                            }else if (resultado != null && resultado.equals("quedan participantes")){
                                // si existe la lista y no está añadida
                                Toast.makeText(ListActivity.this, "Ya no participas en la lista", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ListActivity.this, pantalla_bienvenida.class);
                                startActivity(intent);
                                finish();
                            }else{
                                // si no existe la lista
                                Toast.makeText(ListActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void createList(JSONArray list) {
        JSONArray groupedList = group(list);

        ListView productListView = findViewById(R.id.list_productList);

        ListAdapter adapter = new ProductListAdapter(groupedList, this, listId);
        productListView.setAdapter(adapter);
    }

    private JSONArray group(JSONArray list) {
        try {
            JSONArray pending = new JSONArray();
            JSONArray bought = new JSONArray();

            for (int i = 0; i < list.length(); i++) {
                JSONObject element = (JSONObject) list.get(i);
                if (element.getString("comprado").equals("0"))
                    pending.put(element);
                else
                    bought.put(element);
            }

            for (int i = 0; i < bought.length(); i++) {
                pending.put(bought.get(i));
            }
            return pending;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public void waitForList(){
        Data data = new Data.Builder()
                .putString("url", "obtenerListaProductos.php")
                .putString("lista",listId)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String result = workInfo.getOutputData().getString("resultado");

                            if(Objects.isNull(result)) {
                                Log.d("productList", "Connection error, null result");
                                deployEmptyList();
                            }

                            JSONArray list = JsonBuilder.buildList(result);

                            if (Objects.isNull(list)){
                                Log.d("productList", "Wrong format, result non serializable");
                                deployEmptyList();
                            }

                            createList(list);
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void deployEmptyList() {
        Toast.makeText(this, "This list is empty, start filling it now!", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            waitForList();
        }
    }

    protected void onResume(){
        waitForList();
        super.onResume();
    }
}