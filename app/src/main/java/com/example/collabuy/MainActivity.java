package com.example.collabuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private String[] token = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getInstance(this).getUsername()!=SessionManager.NO_SESSION)
            startActivity(new Intent(this, pantalla_bienvenida.class));

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
                Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                intent.putExtra("token", token[0]);
                startActivity(intent);
                finish();
            }
        });

        //      Registro
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                intent.putExtra("token", token[0]);
                startActivity(intent);
                finish();
            }
        });

        pedirPermisos();

        //Token firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token[0] = task.getResult();
                    }
                });
    }

    private void pedirPermisos(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 11);
        }
    }

}