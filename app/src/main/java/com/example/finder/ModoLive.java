package com.example.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class ModoLive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_live);
    }

    public void volverMenu(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

    public void Ubicacion(View view){
        AlertaInternet();
    }

    private void AlertaInternet(){
        AlertDialog.Builder builder= new AlertDialog.Builder(ModoLive.this);
        builder.setTitle("ALERTA")
                .setMessage("No se puede establecer conexión con la nube.")
                .setIcon(R.drawable.warning)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }

                })
                .setCancelable(false);

        AlertDialog dialog= builder.create();
        dialog.show();
    }
}