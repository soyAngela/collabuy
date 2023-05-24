package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

        loadImage();
    }

    private void deployEmptyProduct() {
        Toast.makeText(this, getString(R.string.product_error), Toast.LENGTH_SHORT).show();
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
        amountView = findViewById(R.id.product_amount);
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

    private void loadImage(){
        //Se crea un nuevo thread para recuperar las imágenes almacenadas en la base de datos
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Dirección y parametros
                    String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/cargarFoto.php";
                    String parametros = "id="+productId;
                    HttpURLConnection urlConnection = null;
                    try {
                        URL destino = new URL(direccion);
                        urlConnection = (HttpURLConnection) destino.openConnection();
                        urlConnection.setConnectTimeout(5000);
                        urlConnection.setReadTimeout(5000);
                        //Se añaden los parámetros
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                        out.print(parametros);
                        out.close();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int statusCode = 0;
                    try {
                        statusCode = urlConnection.getResponseCode();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String res = "";
                    if (statusCode == 200) {
                        //La petición es correcta
                        BufferedInputStream inputStream = null;
                        try {
                            inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String line = "";
                        //Se almacena en el String result lo devuelto por la select
                        while (true) {
                            try {
                                if (!((line = bufferedReader.readLine()) != null)) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            res += line;
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //Si la respuesta no es vacia
                        if (!res.equals("null")) {
                            Bitmap imagen;
                            //Se eliminan los saltos de línea y espacios creados al enviar la imagen a la base de datos desde android
                            //Se vuelve a convertir a bitmap y se añade al array de imagenes
                            String r = res.replaceAll("\n", "");
                            String r2 = r.replaceAll(" ", "+");
                            byte[] decodedString = Base64.decode(r2, Base64.DEFAULT);
                            imagen = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            //Se carga la imagen en pantalla (hay que hacerlo desde el hilo original)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView imageView = findViewById(R.id.product_image);
                                    imageView.setImageDrawable(new BitmapDrawable(getResources(), imagen));
                                }
                            });
                        }
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //Comienza el thread
        thread.start();
    }
}