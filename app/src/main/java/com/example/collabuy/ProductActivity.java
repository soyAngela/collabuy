package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.Objects;

public class ProductActivity extends AppCompatActivity {

    private String productId;
    private String listId;
    private int amount;
    private EditText amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle extras = getIntent().getExtras();
        productId = extras.getString("productId");
        listId = extras.getString("listId");

        waitForProduct();
    }

    private void waitForProduct() {
        Data data = new Data.Builder()
                .putString("url", "mostrarProducto.php")
                .putString("id",productId)
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
                                deployEmptyProduct();
                            }

                            JSONObject product = JsonBuilder.buildObject(result);

                            if (Objects.isNull(product)){
                                Log.d("productList", "Wrong format, result non serializable");
                                deployEmptyProduct();
                            }

                            showProduct(product);
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void showProduct(JSONObject product) {
        try {
            getSupportActionBar().setTitle(product.getString("nombre"));

            amount = Integer.parseInt(product.getString("cantidad"));
            amountView = findViewById(R.id.product_amount);
            amountView.setText(amount + "");

            TextView descriptionView = findViewById(R.id.product_description);
            descriptionView.setText(product.getString("descripcion"));
        }catch(JSONException e){
            e.printStackTrace();
            deployEmptyProduct();
        }
    }

    private void deployEmptyProduct() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
    }

    public void amountUp (View v){
        amount++;
        amountView.setText(amount+"");
    }

    public void amountDown (View v){
        amount--;
        amountView.setText(amount+"");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("amount", amount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        amount = savedInstanceState.getInt("amount");
        amountView.setText(amount + "");
    }

    @Override
    protected void onPause() {
        Log.d("collalogs", "activity paused");
        setAmount();
        super.onPause();
    }

    private void setAmount() {
        Data data = new Data.Builder()
                .putString("url", "ajustarCantidad.php")
                .putString("productId", productId)
                .putString("listId", listId)
                .putString("cantidad", amount+"")
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
                            }

                            Log.d("collalogs", "Amount updated");
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}