package com.example.collabuy;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConexionPHP extends Worker {

    public ConexionPHP(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String url = getInputData().getString("url");
        Data param = getInputData();

        if(url == null){
            return Result.failure();
        }

        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/"+url;
        HttpURLConnection urlConnection = null;
        Uri.Builder builder = new Uri.Builder();
        String parametros = "";
        Log.d("collalogs", "url: "+url);
        switch(url){
            case "iniciosesion.php":
                break;

            case "registro.php":
                builder.appendQueryParameter("usuario", param.getString("usuario"))
                        .appendQueryParameter("contrasena", param.getString("contra"));
                break;

            case "anadirLista.php":
                builder.appendQueryParameter("usuario", param.getString("usuario"))
                        .appendQueryParameter("nombre", param.getString("nombre"))
                        .appendQueryParameter("clave", param.getString("clave"));
                break;

            case "creacionLista.php":
                builder.appendQueryParameter("usuario", param.getString("usuario"))
                        .appendQueryParameter("nombre", param.getString("nombre"))
                        .appendQueryParameter("clave", param.getString("clave"));
                break;

            case "obtenerListaProductos.php":
                builder.appendQueryParameter("lista", param.getString("lista"));
                break;

            case "obtenerListaListas.php":
                builder.appendQueryParameter("usuario", param.getString("usuario"));
                break;

            case "abandonarLista.php":
                builder.appendQueryParameter("usuario", param.getString("usuario"))
                        .appendQueryParameter("id", param.getString("id"));
                break;

            case "editarLista.php":
                builder.appendQueryParameter("id", param.getString("id"))
                        .appendQueryParameter("nombre", param.getString("nombre"))
                        .appendQueryParameter("clave", param.getString("clave"));
                break;

            default:
                return Result.failure();
        }

        parametros = builder.build().getEncodedQuery();
        Log.d("collalogs", "parametros: "+parametros);
        try{
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d("collalogs", "statusCode: "+statusCode);
            if (statusCode == 200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();

                Log.d("collalogs", "Request result: "+result);

                Data resultado = new Data.Builder()
                        .putString("resultado", result)
                        .build();
                return Result.success(resultado);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return Result.failure();
    }
}

