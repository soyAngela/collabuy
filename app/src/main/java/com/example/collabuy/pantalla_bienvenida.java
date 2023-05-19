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


import com.example.collabuy.adaptadores.ListaListasOverview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class pantalla_bienvenida extends AppCompatActivity {
    private ArrayList<String[]> publicaciones = new ArrayList<>();
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
        ListView listaListas = findViewById(R.id.listaNombres);
        Button botonNuevaLista = findViewById(R.id.botonNuevaLista);

    // obtener datos de BBDD y cargar en la lista

        // De manera manual --> Funciona
        ArrayList<String> datos = new ArrayList<String>();
        datos.add("Cumple Lucas");
        datos.add("Fiesta fin de curso");
        datos.add("Compra del miércoles");

        //ListaListasOverview adaptador = new ListaListasOverview(datos, getApplicationContext());


        //listaListas.setAdapter(adaptador);

        // De la bbdd remota falta obtener el usuario que ha iniciado sesion
        String user = SessionManager.getInstance(getApplicationContext()).getUsername();

        obtenerListaListas(user); //con innerjoin Lista+Participación
        //      --> nombre,clave (id para onClick)

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

    public void obtenerListaListas(){
        //conexion con la bbdd mediante el php y obtener datos JSON
    }

    public void obtenerListaListas(String pUsuario){
        /*
        //atributo privado de clase
        private ArrayList<String[]> publicaciones = new ArrayList<>();

        //Recoger todas la publicaciones de la base de datos para poder mostrarlas
        Data datos = new Data.Builder() // para pasar al worker
                .putInt("metodo",0)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MostrarBDWebService.class)
                .setInputData(datos)
                .build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())  //llama al worker
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        String result = status.getOutputData().getString("result");
                        //Se almacena en el String result lo devuelto por el servicio web
                        if(!result.equals("null")) {
                            JSONArray jsonArray = null;
                            try {
                                //Se transforma a un jsonArray el String con el resultado
                                jsonArray = new JSONArray(result);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            //Se recorren todos los elementos del array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //Cada elemento del array es una publicación que contará con 2 elementos
                                String[] publicacion = new String[2];
                                String publicacionId = null;
                                //Se guardan todos los elementos recogiendolos con sus respectivas claves
                                try {
                                    publicacionId = jsonArray.getJSONObject(i).getString("PublicacionId");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                publicacion[0] = publicacionId;
                                String usuario = null;
                                try {
                                    usuario = jsonArray.getJSONObject(i).getString("Usuario");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                publicacion[1] = usuario;
                                String texto = null;
                                try {
                                    texto = jsonArray.getJSONObject(i).getString("Texto");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                publicacion[2] = texto;
                                String imagenId = null;
                                try {
                                    imagenId = jsonArray.getJSONObject(i).getString("ImagenId");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                //Se añade la publicación al array de publicaciones que se ha inicializado previamente
                                publicacion[3] = imagenId;
                                publicaciones.add(publicacion);
                            }
                            //Si hay al menos una publicación en el array se muestran
                            if (!publicaciones.isEmpty()){
                                cargarRecyclerView();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);*/
        /*StringRequest request = new StringRequest(Request.Method.GET, "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/obtenerListaListas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            Toast.makeText(pantalla_bienvenida.this, "No existen listas todavía", Toast.LENGTH_SHORT).show();
                        }else{
                            mostrarListas(response);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(pantalla_bienvenida.this, "ERROR CON LA CONEXION", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //manera de encontrar el usuario
                params.put("usuario", "usuario");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(pantalla_bienvenida.this);
        requestQueue.add(request);

        //conexion con la bbdd mediante el php y obtener datos JSON
    }*/}

    private void mostrarListas() {
        //llamada a metodo que coloque nombre y clave en overview + asignar onclick con idLista
    }

}
