package com.example.finder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Reminder extends BroadcastReceiver {

    public static Boolean activo;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context,"com.example.Finder1")
                .setSmallIcon(R.drawable.check)
                .setContentTitle("FINDER ALERT")
                .setContentText("Modo Estático Desactivado.")
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(999,builder.build());

        ModoEstatico.time.cancel(true);
        ModoEstatico.tiempoSMS.cancel(true);

        activo= false;
    }
}

