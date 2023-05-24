package com.example.collabuy.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.collabuy.ConexionPHP;
import com.example.collabuy.ListActivity;
import com.example.collabuy.ProductActivity;
import com.example.collabuy.R;

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
import java.util.ArrayList;
import java.util.Objects;

public class ProductListAdapter extends BaseAdapter {

    private JSONArray list;
    private Context c;
    private String listId;
    private View.OnClickListener listener;

    public ProductListAdapter(JSONArray list, Context context, String listId){
        this.list = list;
        this.c = context;
        this.listId = listId;
    }

    @Override
    public int getCount() {
        return this.list.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.list.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View panel;

        JSONObject element;
        try {
            element = (JSONObject) list.get(i);
            boolean bought = element.getString("comprado").equals("1");
            //Both layouts have the very same elements and IDs
            if (bought)
                panel = LayoutInflater.from(c).inflate(R.layout.bought_product_list_item, viewGroup, false);
            else
                panel = LayoutInflater.from(c).inflate(R.layout.product_list_item,viewGroup,false);

            TextView name = (TextView) panel.findViewById(R.id.list_productName);

            //Cross the product name out if bought
            if (bought)
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            TextView amount = (TextView) panel.findViewById(R.id.list_productAmount);
            name.setText(element.getString("nombre"));
            amount.setText(element.getString("cantidad"));

            loadImage(element.getString("id"), panel);

            //When a product is clicked show its content in ProductActivity
            panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(c, ProductActivity.class);
                    try {
                        i.putExtra("productId",element.getString("id"));
                        i.putExtra("listId", listId);
                        c.startActivity(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            ImageButton buyButton = panel.findViewById(R.id.list_productBuy);

            if (!bought) {
                //Mark the product as bought when clicking on the shopping basket
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Log.d("ListRegistry", "Bought " + element.getString("nombre") + " at list " + "Lista de la compra");
                            buyProduct(Integer.parseInt(element.getString("id")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }else{
                //Cancel the purchase of the product when clicking on the cross
                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Log.d("ListRegistry", "Cancelled " + element.getString("nombre") + " at list " + "Lista de la compra");
                            cancelProduct(Integer.parseInt(element.getString("id")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            return panel;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void buyProduct(int productId) {
        markBought(productId, 1);
        ((ListActivity)c).waitForList();
    }

    private void cancelProduct(int productId) {
        markBought(productId, 0);
        ((ListActivity)c).waitForList();
    }

    private void markBought(int productId, int state) {
        Data data = new Data.Builder()
                .putString("url", "marcarComprado.php")
                .putString("productId", productId+"")
                .putString("listId", listId)
                .putString("comprado", state+"")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(c).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) c, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String result = workInfo.getOutputData().getString("resultado");

                            if(Objects.isNull(result)) {
                                Log.d("productList", "Connection error, null result");
                            }

                            if(state == 1)
                                Log.d("collalogs", "Product " + productId + " has been bought");
                            else
                                Log.d("collalogs", "Product " + productId + " has been cancelled");

                        }
                    }
                });
        WorkManager.getInstance(c).enqueue(otwr);
    }

    private void loadImage(String id, View view){
        //Se crea un nuevo thread para recuperar las imágenes almacenadas en la base de datos
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Dirección y parametros
                    String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/cargarFoto.php";
                    String parametros = "id="+id;
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
                            ImageView imageView = view.findViewById(R.id.list_productImage);
                            ((Activity)c).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageDrawable(new BitmapDrawable(c.getResources(), imagen));
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
