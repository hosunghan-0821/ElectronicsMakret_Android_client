package com.example.electronicsmarket;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

    public static final String CHANGE_LINE_CHAR = "_!@#$%_";
    public static final String ALARM_ORDER = "__alarm__";
    public static final String CHANNEL_ID = "Default1";
    public static final String CHANNEL_ID_HEAD = "Default2";
    public static final String notiGroup = "notification_group";
    public final static  int NOTIFY_ID=1234;

    private Socket socket;
    private PrintWriter out;
    private NotificationCompat.Builder builder = null;
    private NotificationCompat.Builder summaryBuilder = null;

    private NotificationManager foreNotificationManager;
    private NotificationManager notificationManager;
    private boolean isCloseSocket = false;
    public static Service_Example tcpService;

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
//        writeThread writeThread = new writeThread("close");
//        writeThread.start();

//        stopSelf();

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
        tcpService=Service_Example.this;


//        String nickName = intent.getStringExtra("nickName");
        createNotificationChannel();
        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        String nickName=sharedPreferences.getString("nickName","");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //192.168.163.1
                    //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.
                    socket = new Socket("192.168.0.6", 80);
                    //192.168.163.1
                    //192.168.0.6

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
        Log.e("123", "service onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("123", "service onStartCommand()");

//        createNotificationChannel();
//        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
//        String nickName=sharedPreferences.getString("nickName","");
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    //192.168.163.1
//                    //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.
//                    socket = new Socket("192.168.0.6", 80);
//                    //192.168.163.1
//                    //192.168.0.6
//
//                    Log.e("123", "통신성공");
//                    //연결이 성공 했다면, 듣는 쓰레드 지속적으로 유지시켜야함.
//                    ListenThread listenThread = new ListenThread();
//                    listenThread.start();
//                    //OutputStream
//                    out = new PrintWriter(socket.getOutputStream(), true);
//                    // shared 값 가져오기
//                    out.println(nickName);
//
//                } catch (Exception e) {
//                    System.out.println(e);
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        thread.start();
//        builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID_HEAD)
//                .setContentTitle("채팅 알림 메시지")
//                .setSmallIcon(R.drawable.ic_baseline_favorite_24)
//                .setAutoCancel(true);
//
//        startForeground(1234, builder.build());

//        try{
//            Thread.sleep(2000);
//        }catch (Exception e){
//
//        }
//
//        builder= new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID_HEAD)
//                .setContentTitle("채팅 알림 메시지")
//                .setSmallIcon(R.drawable.ic_baseline_favorite_24);
//        foreNotificationManager.notify(1234,builder.build());
//        foreNotificationManager.cancel(1234);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("123", "service onDestroy()");
        tcpService=null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);

    }

    public class writeThread extends Thread {

        String readValue;

        public writeThread(String readValue) {
            this.readValue = readValue;
        }

        public void run() {

            try {

                Log.e("123", readValue);
                readValue = readValue.replace("\n", CHANGE_LINE_CHAR);
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
                        //알람 보내는 명령일 경우
                        try {
                            if (readValue.split(":")[0].equals(ALARM_ORDER)) {

                                String notifyRoom=readValue.split(":")[1];
                                //채팅방 밖에 있고, 채팅목록화면에 있다면, 데이터 reload 해야함. 전달될 때마다.
                                Intent intent = new Intent("reloadRoomList");
                                readValue = readValue.replace(CHANGE_LINE_CHAR, "\n");
                                intent.putExtra("message", readValue);
                                LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);


                                //Notification 만들기

                                Intent notifyIntent = new Intent(Service_Example.this, Activity_trade_chat.class);
                                notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                Log.e("123","notifyRoom"+notifyRoom);
                                notifyIntent.putExtra("roomNum", readValue.split(":")[1]);
                                PendingIntent notifyPendingIntent =
                                        PendingIntent.getActivity(
                                                getApplicationContext(),
                                                Integer.parseInt(notifyRoom),
                                                notifyIntent,
                                                PendingIntent.FLAG_CANCEL_CURRENT
                                        );
                                builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentTitle("채팅 알림 메시지")
                                        .setContentIntent(notifyPendingIntent)
                                        .setContentText(readValue.split(":")[2] + " : " + readValue.split(":")[3])
                                        .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                                        .setAutoCancel(true)
                                        .setGroup(notiGroup);

                                summaryBuilder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setAutoCancel(true)
                                        .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                                        .setOnlyAlertOnce(true)
                                        .setGroup(notiGroup)
                                        .setGroupSummary(true);

                                notificationManager.notify(Integer.parseInt(notifyRoom), builder.build());
                                notificationManager.notify(NOTIFY_ID,summaryBuilder.build());
                                continue;
                            }

                        } catch (Exception e) {
                            System.out.println("알림 체크하는 부분 오류");
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent("chatData");
                    readValue = readValue.replace(CHANGE_LINE_CHAR, "\n");
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

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "background Service Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(serviceChannel);
            }
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            foreNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel foreServiceChannel = new NotificationChannel(CHANNEL_ID_HEAD,"foreground Service Channel",NotificationManager.IMPORTANCE_DEFAULT);

            if (foreNotificationManager != null) {
                foreNotificationManager.createNotificationChannel(foreServiceChannel);
            }
        }

    }
}