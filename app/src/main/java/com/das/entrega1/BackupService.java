package com.das.entrega1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BackupService extends Service {
    public static final String ACTION_PROGRESO = "com.das.entrega1.PROGRESO_BACKUP";
    private static final String CHANNEL_ID = "CanalBackup";
    private static final int NOTIF_ID = 101;

    @Override
    public void onCreate() {
        super.onCreate();
        crearCanalNotificacion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Construimos la notificación con comportamiento inmediato
        Notification notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notif_backup_titulo))
                .setContentText(getString(R.string.notif_backup_texto))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build();

        //Foreground Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notificacion, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIF_ID, notificacion);
        }

        // Segundo plano
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 10) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent broadcastIntent = new Intent(ACTION_PROGRESO);
                broadcastIntent.setPackage(getPackageName());
                broadcastIntent.putExtra("porcentaje", i);
                sendBroadcast(broadcastIntent);
            }

            Intent intentFinal = new Intent(ACTION_PROGRESO);
            intentFinal.setPackage(getPackageName());
            intentFinal.putExtra("terminado", true);
            sendBroadcast(intentFinal);

            stopForeground(true);
            stopSelf();
        }).start();

        return START_STICKY;
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.canal_backup_nombre),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}