package com.example.finder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ServicioSMS extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        Tiempo time= new Tiempo();
        time.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent intent1=new Intent(this,MainActivity.class);

        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent1,0);

        Notification notification= new NotificationCompat.Builder(this,"ChannelId1")
                .setContentTitle("FINDER")
                .setContentText("Servicio SMS activado.")
                .setSmallIcon(R.drawable.spot)
                .setContentIntent(pendingIntent).build();

        startForeground(1,notification);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificacionChannel= new NotificationChannel("ChannelId1","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificacionChannel);
        }

    }

    public void hilo(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        Tiempo time= new Tiempo();
        time.execute();
    }

    public class Tiempo extends AsyncTask<Void,Integer,Boolean> {

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
            String myMsg = "Alerta de movimiento del dispositivo IoT.";
            String myNumber = "0986840420";
            EnviarSMS(myMsg,myNumber);
            Toast.makeText(getApplicationContext(),"Mensaje Enviado",Toast.LENGTH_LONG).show();

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    public void EnviarSMS(String mensaje, String numero) {
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
            Toast.makeText(this, "Mensaje enviado exitosamente.", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
