package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class ModoEstatico extends AppCompatActivity {

    private int tiempo=0;
    //private int tiempoInicial=0;
    Intent intent;
    time time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_estatico);

        time= new time();
        time.execute();

        Switch switchSMS = (Switch) findViewById(R.id.switchSMS);
        intent= new Intent(this,ServicioSMS.class);

        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            time.cancel(true);

                            if(ActivityCompat.checkSelfPermission(
                                    ModoEstatico.this, Manifest.permission.SEND_SMS)
                                    != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(
                                    ModoEstatico.this,Manifest
                                            .permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(ModoEstatico.this,new String[]
                                        { Manifest.permission.SEND_SMS,},1000);
                            }else{
                            };

                            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                                startForegroundService(intent);
                            }else {
                                startService(intent);
                            }
                        } else {
                            ServicioSMS.tiempo.cancel(true);
                            stopService(intent);
                        }
                    }
                }
        );


    }

    public void hilo(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        time= new time();
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
            Toast.makeText(getApplicationContext(),"Notificación Recibida.",Toast.LENGTH_LONG).show();

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

    public void volverMenu(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
    }

    public void cambiarModo(View view) {
        startActivity(new Intent(getApplicationContext(), ModoLive.class));
        finish();
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