package com.example.collabuy;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NuevaLista extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // Configurar xml dependiendo de la orientación del teléfono
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.nueva_lista_land);
        }else{
            setContentView(R.layout.nueva_lista);
        }

    // Declaración de objetos

        EditText nombreLista = findViewById(R.id.editText_nombre);
        EditText claveLista = findViewById(R.id.editText_clave);

        Button anadirLista = findViewById(R.id.btn_anadir_lista);
        Button crearLista = findViewById(R.id.btn_crear_lista);

    // Funcionalidades de los botones

        // Añadir lista
            anadirLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // AÑADIR LISTA A USUARIO
                }
            });

        // Crear lista
            crearLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // CREAR LISTA EN USUARIO
                }
            });


    }
}
