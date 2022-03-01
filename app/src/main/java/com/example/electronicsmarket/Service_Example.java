package com.example.electronicsmarket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Service_Example extends Service {

    public static final String CHANGE_LINE_CHAR="_!@#$%_";

    private Socket socket;
    private PrintWriter out;
    private NotificationCompat.Builder builder = null;
    public static final String CHANNEL_ID = "ChatServiceChannel";
    private   NotificationManager notificationManager;
    private boolean isCloseSocket=false;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String readValue = intent.getStringExtra("message");
            writeThread writeThread = new writeThread(readValue);
            writeThread.start();

        }
    };


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("123", "onTaskRemoved : ");
        writeThread writeThread = new writeThread("close");
        writeThread.start();

        stopSelf();

    }

    public Service_Example() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("123", "onBind()");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("chatDataToServer"));
        Log.e("123", "service onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("123", "service onStartCommand()");


        String nickName = intent.getStringExtra("nickName");
        createNotificationChannel();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.
                    socket = new Socket("192.168.163.1", 80);
                    //192.168.163.1

                    Log.e("123", "통신성공");
                    //연결이 성공 했다면, 듣는 쓰레드 지속적으로 유지시켜야함.
                    ListenThread listenThread = new ListenThread();
                    listenThread.start();
                    //OutputStream
                    out = new PrintWriter(socket.getOutputStream(), true);
                    // shared 값 가져오기
                    out.println(nickName);

                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }

            }
        });
        thread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("123", "service onDestroy()");


        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);

    }

    public class writeThread extends Thread {

        String readValue;

        public writeThread(String readValue) {
            this.readValue = readValue;
        }

        public void run() {

            try {

                Log.e("123",readValue);
                readValue=readValue.replace("\n",CHANGE_LINE_CHAR);
                out.println(readValue);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public class ListenThread extends Thread {


        public void run() {

            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String readValue;
                while ((readValue = reader.readLine()) != null) {
                    System.out.println("readvalue + :" + readValue);

                    if (readValue != null) {
                        if (readValue.equals("곽민준:noti")) {

                            builder =new NotificationCompat.Builder(Service_Example.this,CHANNEL_ID)
                                    .setContentTitle("실험")
                                    .setContentText("123415")
                                    .setSmallIcon(R.drawable.ic_baseline_favorite_24);

                            notificationManager.notify(1000,builder.build());
                        }
                    }
                    Intent intent = new Intent("chatData");
                    readValue=readValue.replace(CHANGE_LINE_CHAR,"\n");
                    intent.putExtra("message", readValue);

                    LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                }
                reader.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void createNotificationChannel() {
        //Manager, builder,channel 활용해서 notification 만든다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = getSystemService(NotificationManager.class);
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            if(notificationManager!= null){
                notificationManager.createNotificationChannel(serviceChannel);
            }
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }
    }
}