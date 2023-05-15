package com.example.collabuy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collabuy.adaptadores.ListaListasOverview;

import java.util.ArrayList;

public class pantalla_bienvenida extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // configurar xml dependiendo de la orientación
        setContentView(R.layout.usuario_bienvenida);

    // declaración de objetos
        ListView listaListas = findViewById(R.id.listaNombres);
        Button botonNuevaLista = findViewById(R.id.botonNuevaLista);

    // obtener datos de BBDD y cargar en la lista
        ArrayList<String> datos = new ArrayList<String>();
        datos.add("Cumple Lucas");
        datos.add("Fiesta fin de curso");
        datos.add("Compra del miércoles");

        ListaListasOverview adaptador = new ListaListasOverview(datos, getApplicationContext());

        listaListas.setAdapter(adaptador);

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

}
