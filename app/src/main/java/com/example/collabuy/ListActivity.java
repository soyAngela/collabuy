package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collabuy.adaptadores.ProductListAdapter;

import org.json.JSONArray;

import java.util.Objects;

public class ListActivity extends AppCompatActivity {

    private String listId;
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
                Intent i = new Intent(this, CreacionProducto.class);
                i.putExtra("lista", listId);
                i.putExtra("nombreLista", nombreLista);
                startActivity(i);
                break;
            case R.id.list_abandon:
                Toast.makeText(this, "Currently unimplemented feature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.list_settings:
                Toast.makeText(this, "Currently unimplemented feature", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createList(JSONArray list){
        ListView productListView = findViewById(R.id.list_productList);

        ListAdapter adapter = new ProductListAdapter(list, this);
        productListView.setAdapter(adapter);
    }

    private void waitForList(){
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

                            JSONArray list = JsonBuilder.buildProductList(result);

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
}