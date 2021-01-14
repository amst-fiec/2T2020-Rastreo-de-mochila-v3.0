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

    private Timer t = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


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

            startForeground(1,notification);

        }
        catch (Exception e){
            e.printStackTrace();
        }

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

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificacionChannel= new NotificationChannel("ChannelId1","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificacionChannel);
        }

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

}
