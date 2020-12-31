package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

public class ModoEstatico extends AppCompatActivity {

    private int tiempo=0;
    private int tiempoInicial=0;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_estatico);

        time time= new time();
        time.execute();

    }

    public void hilo(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        time time= new time();
        time.execute();
    }

    public class time extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            for (int i=1; i<=10; i++){
                hilo();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean eBoolean){
            ejecutar();
            addNotification();
            //Toast.makeText(getApplicationContext(),"Notificaciones Activadas.",Toast.LENGTH_LONG).show();

        }
    }

    private void addNotification() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel("com.example.finder","com.example.finder",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,"com.example.finder")
                .setSmallIcon(R.drawable.spot)
                .setContentTitle("FINDER ALERT")
                .setContentText("Alerta de movimiento en dispositivo IoT.")
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }

    public void Salir(View view) {
        //Intent intent= new Intent(getApplicationContext(),Servicio.class);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        System.exit(0);
    }

    public void volverMenu(View view) {
        System.exit(1);
    }

    public void cambiarModo(View view) {

        if(ServicioSMS.isRunning=true){
            stopService(intent);
        }
        startActivity(new Intent(getApplicationContext(), ModoLive.class));

    }

    public void aggTiempo(View view){
        numberPickerDialog();
    }

    private void numberPickerDialog(){
        NumberPicker myNumberPicker= new NumberPicker(this);
        myNumberPicker.setMaxValue(60);
        myNumberPicker.setMinValue(1);
        NumberPicker.OnValueChangeListener myValChangedListener= new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tiempo=i1;
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder alertaTiempo= new AlertDialog.Builder(this).setView(myNumberPicker);
        alertaTiempo.setTitle(Html.fromHtml("<font color= '#00BCD4'>"+"Seleccione la duración en minutos:"+"</font>"));
        alertaTiempo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"Duración máxima de "+tiempo+" minutos.",Toast.LENGTH_LONG).show();

            }
        });
        alertaTiempo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"Operación cancelada.",Toast.LENGTH_LONG).show();

            }
        });
        alertaTiempo.show();
    }
}