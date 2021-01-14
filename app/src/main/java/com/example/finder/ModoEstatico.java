package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class ModoEstatico extends AppCompatActivity {

    private int tiempo=0;
    Intent intent1, intent2;
    public static time time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_estatico);

        time= new time();
        time.execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
        }

        createNotificationChannel();

        Switch switchSMS = (Switch) findViewById(R.id.switchSMS);

        intent1 = new Intent(this,ServicioSMS.class);
        intent2= new Intent(this, ServicioModoEstatico.class);

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
                                startForegroundService(intent1);
                            }else {
                                startService(intent1);
                            }
                        } else {
                            ServicioSMS.tiempo.cancel(true);
                            stopService(intent1);
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
        managerCompat.notify(998,builder.build());

    }

    public void volverMenu(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
    }

    public void cambiarModo(View view) {
        cancelAlarm();
        startActivity(new Intent(getApplicationContext(), ModoLive.class));
        finish();
    }

    public void aggTiempo(View view){
        numberPickerDialog();
    }

    private void numberPickerDialog(){
        NumberPicker myNumberPicker= new NumberPicker(this);
        myNumberPicker.setMaxValue(60);
        myNumberPicker.setMinValue(0);
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

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                    startForegroundService(intent2);
                }else {
                    startService(intent2);
                }
                Reminder.activo=true;
                startAlarm(tiempo);
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


    private void startAlarm(int tiempo2) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Reminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        long time1= System.currentTimeMillis();
        long minutos= tiempo2*60;
        long segundos= 1000*minutos;
        alarmManager.set(AlarmManager.RTC_WAKEUP, time1+segundos, pendingIntent);
        Toast.makeText(getApplicationContext(),"Duración máxima de "+tiempo+" minutos.",Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ServicioModoEstatico.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        stopService(intent2);
        Toast.makeText(this,"Modo estático detenido.",Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("com.example.Finder1", "com.example.Finder1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}