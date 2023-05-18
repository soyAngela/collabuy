package com.example.collabuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Activity actividadLogin = this;

        EditText editRegisUsuario = findViewById(R.id.editRegisUsuario);
        EditText editRegisContra = findViewById(R.id.editRegisContrasena);
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
                registrarUsuario(editRegisUsuario.getText().toString(), String.valueOf(editRegisContra.getText().toString().hashCode()));
            }
        });
    }

    public void registrarUsuario(String usuario, String contra){

        Data data = new Data.Builder()
                .putString("url", "registro.php")
                .putString("usuario",usuario)
                .putString("contra",contra)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPHP.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            if(workInfo.getOutputData().getString("resultado") != null && workInfo.getOutputData().getString("resultado").equals("1")){
                                SessionManager.getInstance(getApplicationContext()).setSession(usuario, contra);
                                Intent i = new Intent(getApplicationContext(), pantalla_bienvenida.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(Registro.this, "El usuario o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}