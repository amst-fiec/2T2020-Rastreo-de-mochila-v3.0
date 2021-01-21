package com.example.finder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class ServicioModoEstatico extends Service {

    public static Boolean activado=false;
    public static Timer t = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // se genera la notificacion primer plano del servicio inidicando que esta activo

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {

            createNotificationChannel();

            Intent intent1=new Intent(this,ModoEstatico.class);

            PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent1,0);

            Notification notification= new NotificationCompat.Builder(this,"ChannelId1")
                    .setSmallIcon(R.drawable.bolsa)
                    .setContentTitle("FINDER APP")
                    .setContentText("Modo Estático Activado.")
                    .setContentIntent(pendingIntent).build();

            activado=true;

            startForeground(1,notification);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        // cuando se cumple el tiempo limite del modo estatico, se elimina la notificacion en primer plano

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Reminder.activo){
                    stopForeground(true);
                }
            }
        }, 0, 2000);


        return super.onStartCommand(intent, flags, startId);
    }

    // se crea el canal de la notificacion en primer plano

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificacionChannel= new NotificationChannel("ChannelId1","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificacionChannel);
        }

    }

    // se destruye el servicio

    @Override
    public void onDestroy() {
        activado=false;
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

}
