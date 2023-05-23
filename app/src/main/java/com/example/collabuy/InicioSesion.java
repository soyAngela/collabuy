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

public class InicioSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String token = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        Activity actividadLogin = this;

        TextView textRegistrate = findViewById(R.id.textRegistrate);
        EditText editUsuario = findViewById(R.id.editUsuario);
        EditText editContrasena = findViewById(R.id.editContrasena);
        Button botonIniciar = findViewById(R.id.botonIniciar);

        textRegistrate.setPaintFlags(textRegistrate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String finalToken = token;
        textRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesion.this, Registro.class);
                intent.putExtra("token", finalToken);
                actividadLogin.startActivityForResult(intent, 1);
            }
        });


        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarUsuario(editUsuario.getText().toString(), String.valueOf(editContrasena.getText().toString().hashCode()), finalToken);
            }
        });
    }

    private void verificarUsuario(String usuario, String contra, String token) {

        Data data = new Data.Builder()
                .putString("url", "iniciosesion.php")
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
                                actualizarToken(usuario, contra, token);
                            }else{
                                Toast.makeText(InicioSesion.this, "El usuario o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void actualizarToken(String usuario, String contra, String token){
        Data data = new Data.Builder()
                .putString("url", "token.php")
                .putString("usuario", usuario)
                .putString("token", token)
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
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}