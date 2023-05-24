package com.example.collabuy;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreacionProducto extends AppCompatActivity implements CamaraGaleriaDialog.ListenerdelDialogo{

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ImageView imagen;
    private Bitmap foto;
    private String fotoen64;
    private String nombreProducto;
    private String descripcion;
    private String usuario;
    private Thread thread;
    private String nombreLista;
    private String idLista;
    private Activity actividadPerfil = this;
    private String photoPath;

    private Uri contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idLista = extras.getString("lista");
            nombreLista = extras.getString("nombreLista");
        }
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
                TextView nP = findViewById(R.id.nuevoProductoNombre);
                nombreProducto = nP.getText().toString();
                TextView dP = findViewById(R.id.nuevosProductoDesc);
                descripcion = dP.getText().toString();
                try {
                    crearProducto();
                    getTokenParticipantes(idLista);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void volverLista(){
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("idLista", idLista);
        i.putExtra("nombreLista", nombreLista);
        startActivity(i);
    }

    private void sacarFoto(){
        //Se piden los permisos necesarios para acceder a la camara
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        DialogFragment dialogoalerta= new CamaraGaleriaDialog();
        dialogoalerta.show(getSupportFragmentManager(), "1");
    }

    private Bitmap redimensionarImagen(Bitmap foto){
        //Redimensionar la imagen al tamaño del imageView
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

    private void crearProducto() throws JSONException, IOException {
        //Dialogo para elegir entre la cámara y la galeria
        DialogFragment dialogoalerta= new CamaraGaleriaDialog();
        dialogoalerta.show(getSupportFragmentManager(), "1");
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    if (foto != null){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        foto.compress(Bitmap.CompressFormat.PNG, 60, stream);
                        byte[] fototransformada = stream.toByteArray();
                        fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
                    }
                    String parametros = "nombre=" + nombreProducto + "&descripcion=" + descripcion + "&imagen=" + fotoen64 + "&usuario=" + usuario + "&idLista=" + idLista;
                    String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/add_producto_lista.php";
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
                    if(statusCode == 200){
                    }else{
                        urlConnection.getErrorStream();
                        String b = urlConnection.getResponseMessage();
                        if(statusCode == 0){}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while(thread.isAlive()){}
    }

    @Override
    public void camara() {
        //Llama a la actividad que permite acceder a la camara y sacar la foto cuando se pulsa el botón
        Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(elIntentFoto);
    }
    @Override
    public void galeria() {
        //Acceder a la galería para poder elegir una foto
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        actividadPerfil.startActivityForResult(galeria, 2);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Metodo que recoge la foto una vez se ha  elegido de la galeria
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                contentUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                photoPath = cursor.getString(columnIndex);
                File file = new File(photoPath); //Cambiar el tamaÃ±ao del bitmap
                Bitmap originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //Convertir de Bitmap a byte[]
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                byte[] imagenRedimensionada = outputStream.toByteArray();

                fotoen64 = Base64.encodeToString(imagenRedimensionada,Base64.DEFAULT);
                imagen.setImageURI(contentUri); //Se muestra en la pantalla
            }
        }
    }

    private void getTokenParticipantes(String idLista){
        //Recuperar el token de todos los usuarios pertenecientes a la lista a la que se añade el producto
        Data data = new Data.Builder()
                .putString("url", "tokens_mensaje.php")
                .putString("idLista", idLista)
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
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    crearGrupo(jsonArray);
                                }
                            }else{
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void crearGrupo(JSONArray tokens){
        //Se crea el grupo con todos los participantes
        String nombreGrupo = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        Data data = new Data.Builder()
                .putString("url", "crear_grupo.php")
                .putString("tokens", String.valueOf(tokens))
                .putString("nombreGrupo", nombreGrupo)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null){
                                String resultado = workInfo.getOutputData().getString("resultado");
                                try {
                                    enviarMensaje(resultado);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void enviarMensaje(String nK) throws JSONException {
        //Envio de mensaje con el aviso de nuevo producto
        JSONObject n = new JSONObject(nK);
        String k = n.getString("notification_key");
        String mensaje = usuario + " ha añadido " + nombreProducto + " a la lista " + nombreLista;
        Data data = new Data.Builder()
                .putString("url", "mensaje.php")
                .putString("mensaje", mensaje)
                .putString("idLista", idLista)
                .putString("nK", k)
                .putString("nombreLista", nombreLista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getString("resultado") != null){
                                String resultado = workInfo.getOutputData().getString("resultado");
                                volverLista();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (foto != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.PNG, 60, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
        }
        savedInstanceState.putString("foto",fotoen64);
        EditText nProducto = (EditText) findViewById(R.id.nuevoProductoNombre);
        nombreProducto = nProducto.getText().toString();
        savedInstanceState.putString("nombreProducto", nombreProducto);
        EditText desc = (EditText) findViewById(R.id.nuevosProductoDesc);
        descripcion = desc.getText().toString();
        savedInstanceState.putString("descripcion", descripcion);
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
