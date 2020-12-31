package com.example.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String dispositivos[] = {" ","Dispositivo 1", "Dispositivo 2", "Dispositivo 3", "Dispositivo 4"};
    private String bateria [] = {"81%", "25%", "32%", "73%"};

    private Button btn_Modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Spinner Valores= findViewById(R.id.spinner);
        Valores.setOnItemSelectedListener(this);

        ArrayAdapter items= new ArrayAdapter(this, android.R.layout.simple_spinner_item,dispositivos);
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Valores.setAdapter(items);

        btn_Modo= (Button)findViewById(R.id.btn_modo);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i>0) {
            Toast.makeText(this,"La batería del " + adapterView.getSelectedItem().toString() + " es " + bateria[i-1],Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void mostrarMaletas(View view) {
        startActivity(new Intent(getApplicationContext(), Maletas.class));
    }

    public void modo(View view) {
        final CharSequence[] items= {"Modo Estático","Modo Live"};
        AlertDialog.Builder alertaNotificaciones= new AlertDialog.Builder(this);
        alertaNotificaciones.setTitle(Html.fromHtml("<font color= '#00BCD4'>"+"Elija una opción:"+"</font>"));

        alertaNotificaciones.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast mensaje= Toast.makeText(getApplicationContext(),"Opción seleccionada: "+ items[i],Toast.LENGTH_LONG);
                mensaje.show();

                if (i==0){
                    startActivity(new Intent(getApplicationContext(), ModoEstatico.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), ModoLive.class));
                }

            }
        });
        AlertDialog alertaModo = alertaNotificaciones.create();
        alertaModo.show();
    }

    public void Salir(View view) {
        System.exit(1);
    }

}