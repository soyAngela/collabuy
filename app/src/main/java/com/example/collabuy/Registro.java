package com.example.collabuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Activity actividadLogin = this;

        EditText editRegisUsuario = findViewById(R.id.editRegisUsuario);
        EditText editRegisContrasena = findViewById(R.id.editRegisContrasena);
        Button botonRegistrate = findViewById(R.id.botonRegistrate);
        TextView textYaInicia = findViewById(R.id.textYaInicia);

        textYaInicia.setPaintFlags(textYaInicia.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        textYaInicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Intent a la actividad de Login
                Intent intent = new Intent(Registro.this, InicioSesion.class);
                Registro.this.startActivity(intent);
            }
        });

        botonRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Resultado del boton de registro
            }
        });
    }
}