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
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModoEstatico extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_reference;

    private int tiempo=0;
    Intent intent;
    public static Switch switchSMS;
    public static time time;
    public static tiempoSMS tiempoSMS;
    public static Boolean enviando=false;

    String telefono,ultimaLat,ultimaLon,longitud,latitud;
    String dispElegido="2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_estatico);

        mAuth = FirebaseAuth.getInstance();

        iniciarBaseDeDatos();
        leerTelefono();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
        }

        createNotificationChannel();

        switchSMS = findViewById(R.id.switchSMS);

        intent = new Intent(this, ServicioModoEstatico.class);

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
                            }else{ };
                            tiempoSMS = new tiempoSMS();
                            tiempoSMS.execute();
                        } else {
                            tiempoSMS.cancel(true);
                        } } } );
        actulizarDatos();
    }

    // se inicia base de datos

    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference();
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

    // clase para verificar cada cierto tiempo la ubicacion y enviar una notificacion

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
            verificarMovNotif(longitud,ultimaLon,latitud,ultimaLat);
            ultimaLon=longitud;
            ultimaLat=latitud;
        }
    }

    // se comprueba si los datos de longitud y latitud son los mismos a los valores anteriores y segun eso enviar notificacion

    private void verificarMovNotif(String datoLongitud, String ultimaLongitud, String datoLatitud, String ultimaLatitud){
        if(datoLongitud!=ultimaLongitud || datoLatitud!=ultimaLatitud){
            addNotification();
            Toast.makeText(getApplicationContext(),"Notificación Recibida.",Toast.LENGTH_LONG).show();
        }
    }

    // se crea la notificacion y su respectivo canal

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

    // metodo para regresar al menu principal

    public void volverMenu(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

    // metodo para ir al modo live incluso antes de terminar el modo estatico

    public void cambiarModo(View view) {
        if(ServicioModoEstatico.activado){
            cancelAlarm();
        }
        startActivity(new Intent(getApplicationContext(), ModoLive.class));
        finish();
    }

    // metodo para mostrar el number picker

    public void aggTiempo(View view){
        numberPickerDialog();
    }

    // se crea el number picker y se agregan funcionalidades a los botones ok y cancel. Ademas, se inicia el servicio de modo estatico

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
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }
                Reminder.activo=true;

                time= new time();
                time.execute();

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

    // se inicia la alarma segun el tiempo elegido en el number picker

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

    // se cancela la alarma y las tareas en caso de querer terminarlas antes

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ServicioModoEstatico.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        stopService(this.intent);
        //Reminder.activo=false;
        time.cancel(true);
        if(enviando){
            tiempoSMS.cancel(true);
            switchSMS.setChecked(false);
        }
        Toast.makeText(this,"Modo estático detenido.",Toast.LENGTH_LONG).show();
    }

    // se crea el canal de notificacion de la notificacion que indica que se termino el modo estatico

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("com.example.Finder1", "com.example.Finder1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public void ejecutar1(){
        tiempoSMS = new tiempoSMS();
        tiempoSMS.execute();
    }

    //clase para verificar cada cierto tiempo la ubicacion y enviar un mensaje

    public class tiempoSMS extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            for (int i=1; i<=10; i++){
                hilo();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean eBoolean){
            ejecutar1();
            verificarMovSMS(longitud,ultimaLon,latitud,ultimaLat,telefono);
            ultimaLon=longitud;
            ultimaLat=latitud;
            enviando=true;
        }
    }

    // se comprueba si los datos de longitud y latitud son los mismos a los valores anteriores y segun eso enviar mensajes SMS


    private void verificarMovSMS(String datoLongitud,String ultimaLongitud,String datoLatitud,String ultimaLatitud,String celular){
        if(datoLongitud!=ultimaLongitud || datoLatitud!=ultimaLatitud){
            EnviarSMS(celular);
            Toast.makeText(getApplicationContext(),"Mensaje Enviado.",Toast.LENGTH_LONG).show();
        }
    }

    // metodo que envia mensaje al numero obtenido de la base de datos

    public void EnviarSMS(String numero) {
        try{
            String mensaje = "Alerta de movimiento en dispositivo IoT.";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al enviar mensaje.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // metodo que obtiene el telefono del user de la base de datos

    public void leerTelefono(){
        db_reference.child("Usuario").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numero = String.valueOf(dataSnapshot.child("telefono").getValue());
                telefono =numero;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toException());
            }
        });

    }

    // hilo que se ejecuta cada 3 segundos obteniendo lectura de datos del dispositivo

    public void actulizarDatos(){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                leerDispositivo();
            } };
        handler.postDelayed(runnable, 3000);
    }

    // metodo que permite entrar a la base de datos y obtener latitud y longitud del dispositivo seleccionado

    public void leerDispositivo(){
        db_reference.child("Dispositivo").child("Dispositivo"+dispElegido).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String longitudObt = String.valueOf(dataSnapshot.child("longitud").getValue());
                String latitudObt = String.valueOf(dataSnapshot.child("latitud").getValue());

                longitud=longitudObt;
                latitud=latitudObt;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

}