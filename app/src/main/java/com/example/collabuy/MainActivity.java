package com.example.collabuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    // Configurar xml dependiendo de la orientación
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_main_land);
        }
        else{
            setContentView(R.layout.activity_main);
        }

    // Declaración de objetos
        Button login = findViewById(R.id.btn_inicio_sesion);
        Button registro = findViewById(R.id.btn_registro);

    //Funcionalidades de los botones
        //      Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, pantalla_bienvenida.class);
                startActivity(intent);
                finish();
                //Cambiar a página de Inicio de sesión
                //Toast.makeText(MainActivity.this, "Se accede al inicio de sesión", Toast.LENGTH_SHORT).show();
            }
        });

        //      Registro
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cambiar a página de registro
                Toast.makeText(MainActivity.this, "Se accede al registro de usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}