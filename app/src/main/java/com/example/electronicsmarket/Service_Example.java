package com.example.electronicsmarket;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.AlarmManager;
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

import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Calendar;

public class Service_Example extends Service {

    public static final String CHANGE_LINE_CHAR = "_!@#$%_";
    public static final String ALARM_ORDER = "__alarm__";
    public static final String CHANNEL_ID = "Default1";
    public static final String CHANNEL_ID_HEAD = "Default2";
    public static final String notiGroup = "notification_group";
    public final static int NOTIFY_ID = 1234;

    private Socket socket;
    private PrintWriter out;
    private NotificationCompat.Builder builder = null;
    private NotificationCompat.Builder summaryBuilder = null;

    private NotificationManager foreNotificationManager;
    private NotificationManager notificationManager;
    private boolean isCloseSocket = false;
    private ListenThread listenThread;
    public static Service_Example tcpService;
    private Thread connectThread;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //여기서 데이터들 받아서, JSONObject로 만들고 서버로 전송
            String readValue = intent.getStringExtra("message");
            String purpose = intent.getStringExtra("purpose");

            if (purpose != null) {

                //서버로 보내는 목적이 방번호 변경일 경우;
                if (purpose.equals("changeRoomNum")) {

                    String roomNum = intent.getStringExtra("roomNum");
                    String otherUserNickname = intent.getStringExtra("otherUserNickname");

                    //String roomNum, String otherUserNickname, String message
                    writeThread writeThread = new writeThread(roomNum, otherUserNickname, "채팅전송", purpose);
                    writeThread.start();
                }

                //서버로 보내는 목적이 채팅내용 전달일 경우;
                else if (purpose.equals("send")) {

                    String message = intent.getStringExtra("message");
                    Log.e("123", "onReceive message :" + message);
                    writeThread writeThread = new writeThread(message, "send");
                    writeThread.start();
                }

                //서버로 보내는 목적이 채팅방 나가는 경우;
                else if (purpose.equals("quit")) {
                    writeThread writeThread = new writeThread("", "quit");
                    writeThread.start();
                }

            }
            //purpose가 없을 떄.. 좀 있다 바꿀꺼야
            else {
                writeThread writeThread = new writeThread(readValue);
                writeThread.start();
            }


        }
    };


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("123", "onTaskRemoved : ");
//        writeThread writeThread = new writeThread("close");
//        writeThread.start();
        writeThread writeThread = new writeThread("close","closeSocket");
        writeThread.start();

        stopSelf();
        setNotificationService();

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
//        tcpService = Service_Example.this;
////        String nickName = intent.getStringExtra("nickName");
//        createNotificationChannel();
//        // shared 값 가져오기
//        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
//        String nickName = sharedPreferences.getString("nickName", "");
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    //192.168.163.1
//                    //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.
//                    socket = new Socket("219.248.76.133", 1234);
//                    //192.168.163.1
//                    //192.168.0.6
//                    Log.e("123", "통신성공");
//                    //연결이 성공 했다면, 듣는 쓰레드 지속적으로 유지시켜야함.
//                    listenThread = new ListenThread();
//                    listenThread.setDaemon(true);
//                    listenThread.start();
//                    //OutputStream
//                    out = new PrintWriter(socket.getOutputStream(), true);
//                    // shared 값 가져오기 JSON으로 넘기기
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("nickname", nickName);
//                    out.println(jsonObject.toString());
//
//                }
//                catch(ConnectException ea){
//                    tcpService=null;
//                    Intent intent =new Intent(getApplicationContext(),Service_Example.class);
//                    startService(intent);
//                }
//                catch (Exception e) {
//                    System.out.println(e);
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        thread.start();
        Log.e("123", "service onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.e("123", "service onStartCommand()");

//        String nickName = intent.getStringExtra("nickName");
        createNotificationChannel();
        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        String nickName = sharedPreferences.getString("nickName", "");

        if(tcpService!=null){
            return START_NOT_STICKY;
        }
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //192.168.163.1
                    //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.

                    socket = new Socket("192.168.163.1", 80);
                    //연결성공하면, 서비스가 연결되었다는것을 인지지
                    tcpService = Service_Example.this;
                    //219.248.76.133  집 동적 ip /port 1234
                    //192.168.163.1  local ip / port 80
                    //192.168.0.6
                    //192.168.0.5    집 무선인터넷
                    Log.e("123", "통신성공");
                    //연결이 성공 했다면, 듣는 쓰레드 지속적으로 유지시켜야함.
                    listenThread = new ListenThread();
                    listenThread.setDaemon(true);
                    listenThread.start();
                    //OutputStream
                    out = new PrintWriter(socket.getOutputStream(), true);
                    // shared 값 가져오기 JSON으로 넘기기
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("nickname", nickName);
                    out.println(jsonObject.toString());

                }
                catch(ConnectException ea){
                    if(tcpService==null){
                        Intent intent =new Intent(getApplicationContext(),Service_Example.class);
                        startService(intent);
                    }
                    else{
                        Log.e("123","이미연결되어있음");
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }

            }
        });
        connectThread.setDaemon(true);
        connectThread.start();

        //** return START_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("123", "service onDestroy()");
        tcpService = null;

        Thread.currentThread().interrupt();

        if (listenThread != null) {
            //Log.e("123","listenThread interrupt");
            listenThread.interrupt();
            Log.e("123","listenThtread isAlive"+listenThread.isAlive());
            Log.e("123","Thread.currentThread isAlive"+Thread.currentThread().isAlive());
            try{
                socket.close();
            }catch (Exception ea){
                Log.e("123",ea.toString());
            }
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);

    }

    public void setNotificationService() {

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        Intent intent = new Intent(this, BroadcastReceiver_Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);


    }

    public class writeThread extends Thread {

        private String readValue, roomNum, otherUserNickname, message, purpose;

        public writeThread(String roomNum, String otherUserNickname, String message, String purpose) {
            this.roomNum = roomNum;
            this.otherUserNickname = otherUserNickname;
            this.message = message;
            this.purpose = purpose;
        }

        public writeThread(String readValue) {
            this.readValue = readValue;
        }

        public writeThread(String message, String purpose) {
            this.message = message;
            this.purpose = purpose;
        }

        public void run() {

            try {
                //여기서 JSON으로 바꿔서 보낸다.

                //데이터 보내는 목적이 입장한 채팅방 번호알림일 때,
                if (purpose != null) {

                    //채팅방 입장하기기
                    if (purpose.equals("changeRoomNum")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("roomNum", roomNum);
                        jsonObject.put("otherUserNickname", otherUserNickname);
                        out.println(jsonObject.toString());
                    }
                    //채팅방 보내기
                    else if (purpose.equals("send")) {
                        Log.e("123", "writeThread message :" + message);
                        JSONObject jsonObject = new JSONObject();
                        message = message.replace("\n", CHANGE_LINE_CHAR);
                        jsonObject.put("message", message);
                        jsonObject.put("purpose", "send");
                        out.println(jsonObject.toString());
                    }
                    //채팅방 나가기
                    else if (purpose.equals("quit")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", "");
                        jsonObject.put("purpose", "quit");
                        out.println(jsonObject.toString());
                    }
                    //소켓종료
                    else if(purpose.equals("closeSocket")){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", "close");
                        jsonObject.put("purpose", "close");
                        out.println(jsonObject.toString());
                    }

                } else {
                    readValue = readValue.replace("\n", CHANGE_LINE_CHAR);
                    out.println(readValue);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public class ListenThread extends Thread {

        JsonParser jsonParser = new JsonParser();

        public void run() {

            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String readValue;
                while ((readValue = reader.readLine()) != null) {
                    System.out.println("readvalue  :" + readValue);
                    String writer = null;
                    String message = null;
                    String notice = null;
                    JSONObject jsonObject = new JSONObject(readValue);
                    try {
                        System.out.println("Notice : " + jsonObject.getString("notice"));
                        notice = jsonObject.getString("notice");
                        writer = jsonObject.getString("writer");
                        message = jsonObject.getString("message");
                    } catch (Exception e) {
                    }
                    //채팅방 명수 정보 받기. 방 맨처음 들어왔을 경우
                    if(jsonObject.getString("notice").equals("인원체크")){
                        Intent intent = new Intent("chatData");
                        intent.putExtra("purpose","인원체크");
                        intent.putExtra("peopleNum",jsonObject.getInt("peopleNum"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                        continue;
                    }
                    //채팅방 다른 유저이 들어왔을 경우
                    else if(jsonObject.getString("notice").equals("인원추가")){
                        //인원추가 할 때, 데이터도 reload 해야하네
                        Intent intent = new Intent("chatData");
                        intent.putExtra("purpose","인원추가");
                        intent.putExtra("peopleNum",jsonObject.getInt("peopleNum"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                        continue;
                    }
                    //채팅방 다른 유저 나갔을 경우
                    else if(jsonObject.getString("notice").equals("인원감소")){
                        Intent intent = new Intent("chatData");
                        intent.putExtra("purpose","인원감소");
                        intent.putExtra("peopleNum",jsonObject.getInt("peopleNum"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                        continue;
                    }
                    //채팅이 아니라 debugging 필요한 정보들 왔을 때 코드 여기까지 작동
                    if (writer == null || message == null) {
                        Log.e("123", "notice만 존재");
                        continue;
                    }

                    //알림전송용 데이터일 경우
                    if (notice.equals("알림전송")) {
                        //알람 보내는 명령일 경우
                        try {

                            String notifyRoom =jsonObject.getString("notifyRoom");
                            //채팅방 밖에 있고, 채팅목록화면에 있다면, 데이터 reload 해야함. 전달될 때마다.
                            Intent intent = new Intent("reloadRoomList");
                            message = message.replace(CHANGE_LINE_CHAR, "\n");
                            intent.putExtra("message", message);
                            LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                            //Notification 만들기
                            Intent notifyIntent = new Intent(Service_Example.this, Activity_trade_chat.class);
                            notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            Log.e("123", "notifyRoom" + notifyRoom);
                            notifyIntent.putExtra("roomNum", notifyRoom);
                            PendingIntent notifyPendingIntent =
                                    PendingIntent.getActivity(
                                            getApplicationContext(),
                                            Integer.parseInt(notifyRoom),
                                            notifyIntent,
                                            PendingIntent.FLAG_CANCEL_CURRENT
                                    );
                            builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentTitle("채팅 알림 메시지")
                                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

                                    .setContentIntent(notifyPendingIntent)

                                    .setContentText(writer+ " : "+ message)
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
//                          notificationManager.notify(NOTIFY_ID, summaryBuilder.build());
                            continue;

                        } catch (Exception e) {
                            System.out.println("알림 체크하는 부분 오류");
                            e.printStackTrace();
                        }
                    }
                    //알람처리가 안될 경우 (방에 둘다 있다는 뜻)
                    Intent intent = new Intent("chatData");
                    message = message.replace(CHANGE_LINE_CHAR, "\n");
                    //readValue = readValue.replace(CHANGE_LINE_CHAR, "\n");
                    intent.putExtra("writer", writer);
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                }
                reader.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("123", "서비스 종료 ListeningThread 종료");
                writeThread writeThread = new writeThread("close","closeSocket");
                writeThread.start();
                stopSelf();
                try{
                    socket.close();
                }catch (Exception ea){
                    Log.e("123",ea.toString());
                }

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

    }
}