package com.example.electronicsmarket.Service;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.electronicsmarket.R;
import com.example.electronicsmarket.Service.Service_Example;

public class Service_Restart extends Service {
    public Service_Restart() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default123")
                .setContentTitle("채팅 알림 메시지")
                .setSmallIcon(R.drawable.ic_baseline_favorite_24);

        startForeground(1234, builder.build());
        Intent in = new Intent(this, Service_Example.class);
        startService(in);
        //이전의 가짜 서비스 삭제
        stopForeground(true);
        stopSelf();


        return START_NOT_STICKY;
    }
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getBaseContext().getSystemService(NotificationManager.class);

            NotificationChannel serviceChannel = new NotificationChannel(
                    "default123",
                    "TEST",
                    NotificationManager.IMPORTANCE_NONE
            );
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}