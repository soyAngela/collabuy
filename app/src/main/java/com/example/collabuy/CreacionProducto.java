package com.example.collabuy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreacionProducto extends AppCompatActivity {

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ImageView imagen;
    private Bitmap foto;
    private String fotoen64;
    private String nombreProducto;
    private String descripcion;
    private String usuario;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sM = SessionManager.getInstance(this);
        usuario = sM.getUsername();
        setContentView(R.layout.nuevoproducto);
        imagen = (ImageView) findViewById(R.id.nuevaFoto);
        takePictureLauncher =
                registerForActivityResult(new
                        ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK &&
                            result.getData()!= null) {
                        //Se recoge el resultado y se muestra en un ImageView
                        Bundle bundle = result.getData().getExtras();
                        foto = (Bitmap) bundle.get("data");
                        imagen.setImageBitmap(redimensionarImagen(foto));
                    } else {
                        Log.d("TakenPicture", "No photo taken");
                    }
                });
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sacarFoto();
            }
        });
        Button bNprod = (Button) findViewById(R.id.bNuevoProducto);
        bNprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearProducto();
            }
        });
    }

    private void sacarFoto(){
        //Se piden los permisos necesarios para acceder a la camara
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.CAMERA}, 11);
        }
        //Llama a la actividad que permite acceder a la camara y sacar la foto cuando se pulsa el botón
        Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(elIntentFoto);
    }

    private Bitmap redimensionarImagen(Bitmap foto){
        int anchoDestino = imagen.getWidth();
        int altoDestino = imagen.getHeight();
        int anchoImagen = foto.getWidth();
        int altoImagen = foto.getHeight();
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }
        Bitmap fotoRedimensionada = Bitmap.createScaledBitmap(foto,anchoFinal,altoFinal,true);
        return fotoRedimensionada;
    }

    private void crearProducto(){
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] fototransformada = stream.toByteArray();
                    fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
                    String parametros = "nombre=" + nombreProducto + "&descripcion=" + descripcion + "&imagen=" + fotoen64 + "&usuario=" + usuario;

                    String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/creacion_producto.php";
                    HttpURLConnection urlConnection = null;
                    URL destino = new URL(direccion);
                    urlConnection = (HttpURLConnection) destino.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(parametros.toString());
                    out.close();
                    int statusCode = 0;
                    statusCode = urlConnection.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (foto != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
        }
        savedInstanceState.putString("foto",fotoen64);
        EditText nProducto = (EditText) findViewById(R.id.nuevoProductoNombre);
        savedInstanceState.putString("nombreProducto", nProducto.getText().toString());
        EditText desc = (EditText) findViewById(R.id.nuevosProductoDesc);
        savedInstanceState.putString("descripcion", desc.getText().toString());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fotoen64 = savedInstanceState.getString("foto");
        nombreProducto = savedInstanceState.getString("nombreProducto");
        descripcion = savedInstanceState.getString("descripcion");
    }

    protected void onResume() {
        super.onResume();
        EditText nProducto = (EditText) findViewById(R.id.nuevoProductoNombre);
        nProducto.setText(nombreProducto);
        EditText desc = (EditText) findViewById(R.id.nuevosProductoDesc);
        desc.setText(descripcion);
        if(fotoen64 != null){
            byte[] decodedString = Base64.decode(fotoen64, Base64.DEFAULT);
            foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagen.setImageBitmap(foto);
        }
    }
}
