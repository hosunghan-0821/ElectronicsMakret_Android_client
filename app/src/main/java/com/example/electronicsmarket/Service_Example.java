package com.example.electronicsmarket;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private SharedPreferences sharedPreferences;
    private NotificationManager foreNotificationManager;
    private NotificationManager notificationManager;
    private boolean isCloseSocket = false;
    private ListenThread listenThread;
    public static Service_Example tcpService;
    private Thread connectThread;
    private Handler handler;
    private Thread checkAlive;
    private String nickname, email;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //????????? ???????????? ?????????, JSONObject??? ????????? ????????? ??????
            String readValue = intent.getStringExtra("message");
            String purpose = intent.getStringExtra("purpose");

            if (purpose != null) {
                Log.e("123", "changeRoomNum : ");
                //????????? ????????? ????????? ????????? ????????? ??????;
                if (purpose.equals("changeRoomNum")) {

                    Log.e("123", "????????????3");
                    String roomNum = intent.getStringExtra("roomNum");

                    Log.e("123", "purpose : " + purpose);
                    String otherUserNickname = intent.getStringExtra("otherUserNickname");

                    //String roomNum, String otherUserNickname, String message
                    writeThread writeThread = new writeThread(roomNum, otherUserNickname, "????????????", purpose);
                    writeThread.start();
                }

                //????????? ????????? ????????? ???????????? ????????? ??????;
                else if (purpose.equals("send")) {

                    String message = intent.getStringExtra("message");
                    String call = intent.getStringExtra("callPurpose");
                    String roomNum= intent.getStringExtra("roomNum");
                    String caller = intent.getStringExtra("caller");
                    String otherUserNickname = intent.getStringExtra("sendToNickname");

                    Log.e("123","callPurpose : "+ call);
                    Log.e("123", "onReceive message :" + message);
                    if(call==null){
                        writeThread writeThread = new writeThread(message, "send");
                        writeThread.start();
                    }
                    else if (call.equals("call")){
                        writeThread writeThread = new writeThread(roomNum,otherUserNickname,message,"send","call");
                        writeThread.start();
                    }else if(call.equals("result")){
                        writeThread writeThread = new writeThread(roomNum,otherUserNickname,message,"send","result",caller);
                        writeThread.start();
                    }

                }
                //????????? ????????? ????????? ????????? ????????? ??????;
                else if (purpose.equals("sendImage")) {
                    String message = intent.getStringExtra("message");
                    writeThread writeThread = new writeThread(message, "sendImage");
                    writeThread.start();

                }
                //???????????? ?????????,
                else if (purpose.equals("sendNotification")) {
                    int type = intent.getIntExtra("type", -1);
                    String message = intent.getStringExtra("message");
                    String postNum = intent.getStringExtra("postNum");
                    String sendToNickname = intent.getStringExtra("sendToNickname");
                    writeThread writeThread = new writeThread(type, message, "sendNotification", sendToNickname, postNum);
                    writeThread.start();

                }

                //????????? ????????? ????????? ????????? ????????? ??????;
                else if (purpose.equals("quit")) {
                    Log.e("123", "quit : ");
                    writeThread writeThread = new writeThread("", "quit");
                    writeThread.start();

                }
                //????????? ????????? ????????? ?????? ?????? ??????;
                else if (purpose.equals("close")) {
                    Log.e("123", "close : ???????????? ");
                    writeThread writeThread = new writeThread("close", "closeSocket");
                    writeThread.start();
                    stopSelf();
                }

            }
            //purpose??? ?????? ???.. ??? ?????? ????????????
            else {
                Log.e("123", "purpose null ??? ??????");
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
        writeThread writeThread = new writeThread("close", "closeSocket");
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
        // shared ??? ????????????
        sharedPreferences = getSharedPreferences("noAlarmArrayList", MODE_PRIVATE);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        };

        // shared ??? ????????????
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        email = sharedPreferences.getString("userId", "");
        nickname = sharedPreferences.getString("nickName", "");


        //sharedPreferences= getApplicationContext().getSharedPreferences("noAlarmArrayList",Context.MODE_PRIVATE);


//        tcpService = Service_Example.this;
////        String nickName = intent.getStringExtra("nickName");
//        createNotificationChannel();
//        // shared ??? ????????????
//        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
//        String nickName = sharedPreferences.getString("nickName", "");
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    //192.168.163.1
//                    //?????? port ??? host(ip) ?????? ????????? ????????? ???????????????.
//                    socket = new Socket("219.248.76.133", 1234);
//                    //192.168.163.1
//                    //192.168.0.6
//                    Log.e("123", "????????????");
//                    //????????? ?????? ?????????, ?????? ????????? ??????????????? ??????????????????.
//                    listenThread = new ListenThread();
//                    listenThread.setDaemon(true);
//                    listenThread.start();
//                    //OutputStream
//                    out = new PrintWriter(socket.getOutputStream(), true);
//                    // shared ??? ???????????? JSON?????? ?????????
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
        // shared ??? ????????????
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        String nickName = sharedPreferences.getString("nickName", "");

        if (tcpService != null) {
            Log.e("123", "socket ?????? ??? ????????? ");
            return START_NOT_STICKY;
        }
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //192.168.163.1
                    //?????? port ??? host(ip) ?????? ????????? ????????? ???????????????.

                    socket = new Socket("219.248.76.133", 12345);
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(0);

                    //connection_time ????????? ??????, polling ???????????? ???????????? ???????????? ??????.. ?????? ????????? ?????? ?????????. client ?????????..
//                    socket = new Socket();
//                    SocketAddress address = new InetSocketAddress("219.248.76.133", 12345);
//                    socket.connect(address,5000);
                    //??????????????????, ???????????? ???????????????????????? ??????
                    //toastMessage ??????
                    handleMessage("???????????? ????????????");
                    tcpService = Service_Example.this;

                    //192.168.35.119  ????????? wifi ip / port 80
                    //219.248.76.133  ??? ?????? ??????ip /port 12345
                    //219.248.76.133  ??? ?????? ip /port 1234
                    //192.168.163.1  local ip / port 80
                    //192.168.0.6
                    //192.168.0.5    ??? ???????????????
                    Log.e("123", "????????????");
                    //????????? ?????? ?????????, ?????? ????????? ??????????????? ??????????????????.
                    listenThread = new ListenThread();
                    listenThread.setDaemon(true);
                    listenThread.start();
                    //checkAlive.start();
                    //OutputStream
                    out = new PrintWriter(socket.getOutputStream(), true);
                    // shared ??? ???????????? JSON?????? ?????????
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("purpose", "connect");
                    jsonObject.put("nickname", nickName);
                    out.println(jsonObject.toString());

                    if (Activity_trade_chat.activity_trade_chat != null) {


                        jsonObject.put("purpose", "roomNumCheck");
                        jsonObject.put("roomNum", Activity_trade_chat.roomNumGlobal);
                        jsonObject.put("otherUserNickname", Activity_trade_chat.otherUserNicknameGlobal);
                        out.println(jsonObject.toString());

                        Intent reloadIntent = new Intent("chatData");
                        reloadIntent.putExtra("purpose", "reload");
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(reloadIntent);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent("reloadRoomList");
                    intent.putExtra("message", "reloadRoomList");
                    LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                } catch (SocketTimeoutException e) {
                    stopSelf();
                    if (tcpService == null) {
                        Intent intent = new Intent(getApplicationContext(), Service_Example.class);
                        startService(intent);
                    } else {
                        Log.e("123", "????????????????????????");
                    }
                } catch (SocketException socketException) {
                    socketException.printStackTrace();
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }

            }
        });
        connectThread.setDaemon(true);
        connectThread.start();
        checkAlive = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.getKeepAlive()) {
                        Log.e("123", "socketKeepAlive : " + socket.getKeepAlive());
                        Thread.sleep(10000);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

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
            Log.e("123", "listenThtread isAlive" + listenThread.isAlive());
            Log.e("123", "Thread.currentThread isAlive" + Thread.currentThread().isAlive());
            try {
                socket.close();
            } catch (Exception ea) {
                Log.e("123", ea.toString());
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
        private String sendToNickname, postNum;
        private int type;
        private Retrofit retrofit;
        private String stringMessageType;
        private String caller;


        public writeThread(String roomNum, String otherUserNickname, String message, String purpose) {
            this.roomNum = roomNum;
            this.otherUserNickname = otherUserNickname;
            this.message = message;
            this.purpose = purpose;
        }
        public writeThread(String roomNum, String otherUserNickname, String message, String purpose,String stringMessageType) {
            this.roomNum = roomNum;
            this.otherUserNickname = otherUserNickname;
            this.message = message;
            this.purpose = purpose;
            this.stringMessageType=stringMessageType;
        }
        public writeThread(String roomNum, String otherUserNickname, String message, String purpose,String stringMessageType,String caller) {
            this.roomNum = roomNum;
            this.otherUserNickname = otherUserNickname;
            this.message = message;
            this.purpose = purpose;
            this.stringMessageType=stringMessageType;
            this.caller=caller;
        }

        public writeThread(String message, String purpose, String stringMessageType) {
            this.message = message;
            this.purpose = purpose;
            this.stringMessageType = stringMessageType;
        }

        public writeThread(String readValue) {
            this.readValue = readValue;
        }

        public writeThread(String message, String purpose) {
            this.message = message;
            this.purpose = purpose;
        }

        public writeThread(int type, String message, String purpose, String sendToNickname, String postNum) {
            this.type = type;
            this.message = message;
            this.purpose = purpose;
            this.sendToNickname = sendToNickname;
            this.postNum = postNum;
        }

        public void saveNotification() {


            Log.e("123", "saveNotification () :");
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            HashMap<String, RequestBody> notificationHashMap = new HashMap<>();
            RequestBody typeRequest, memberRequest, messageRequest, postRequest, senderRequest;

            RetrofitService service = retrofit.create(RetrofitService.class);
            if (type == -1 || type == -2) {
                return;
            }
            switch (type) {
                case 0:
                    typeRequest = RequestBody.create(MediaType.parse("text/plain"), "0");
                    memberRequest = RequestBody.create(MediaType.parse("text/plain"), sendToNickname);
                    messageRequest = RequestBody.create(MediaType.parse("text/plain"), message);
                    postRequest = RequestBody.create(MediaType.parse("text/plain"), postNum);
                    senderRequest = RequestBody.create(MediaType.parse("text/plain"), nickname);

                    notificationHashMap.put("type", typeRequest);
                    notificationHashMap.put("sendToNickname", memberRequest);
                    notificationHashMap.put("message", messageRequest);
                    notificationHashMap.put("postNum", postRequest);
                    notificationHashMap.put("sender", senderRequest);
                    break;
                case 1:

                    typeRequest = RequestBody.create(MediaType.parse("text/plain"), "1");
                    memberRequest = RequestBody.create(MediaType.parse("text/plain"), sendToNickname);
                    messageRequest = RequestBody.create(MediaType.parse("text/plain"), message);
                    senderRequest = RequestBody.create(MediaType.parse("text/plain"), nickname);

                    notificationHashMap.put("type", typeRequest);
                    notificationHashMap.put("sendToNickname", memberRequest);
                    notificationHashMap.put("message", messageRequest);
                    notificationHashMap.put("sender", senderRequest);

                    break;
                case 2:
                    typeRequest = RequestBody.create(MediaType.parse("text/plain"), "2");
                    memberRequest = RequestBody.create(MediaType.parse("text/plain"), sendToNickname);
                    messageRequest = RequestBody.create(MediaType.parse("text/plain"), message);
                    postRequest = RequestBody.create(MediaType.parse("text/plain"), postNum);
                    senderRequest = RequestBody.create(MediaType.parse("text/plain"), nickname);

                    notificationHashMap.put("type", typeRequest);
                    notificationHashMap.put("sendToNickname", memberRequest);
                    notificationHashMap.put("message", messageRequest);
                    notificationHashMap.put("postNum", postRequest);
                    notificationHashMap.put("sender", senderRequest);
                    break;


                default:
                    break;
            }
            Call<Void> call = service.saveNotification(notificationHashMap);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "http notification ????????????", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("123", t.getMessage());

                }
            });

        }

        public void run() {

            try {
                //????????? JSON?????? ????????? ?????????.

                //????????? ????????? ????????? ????????? ????????? ??????????????? ???,
                if (purpose != null) {
                    //????????? ???????????????
                    if (purpose.equals("changeRoomNum")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("purpose", "roomNumCheck");
                        jsonObject.put("roomNum", roomNum);
                        jsonObject.put("otherUserNickname", otherUserNickname);
                        out.println(jsonObject.toString());
                    }
                    //????????? ?????? ?????????
                    else if (purpose.equals("send")) {

                        if(stringMessageType==null){

                            Log.e("123","??????????????? ????????? ");

                            Log.e("123", "writeThread message :" + message);
                            JSONObject jsonObject = new JSONObject();
                            message = message.replace("\n", CHANGE_LINE_CHAR);
                            jsonObject.put("message", message);
                            jsonObject.put("purpose", "send");
                            out.println(jsonObject.toString());
                        }

                        //?????? ???????????? ?????? ?????????
                        else if(stringMessageType.equals("call")){

                            Log.e("123","???????????? ???????????? ?????????");

                            JSONObject jsonObject = new JSONObject();
                            message = message.replace("\n", CHANGE_LINE_CHAR);


                            jsonObject.put("roomNum",roomNum);
                            jsonObject.put("message", message);
                            jsonObject.put("purpose", "send");
                            jsonObject.put("callPurpose","call");
                            out.println(jsonObject.toString());
                        }
                        //???????????? ?????? ????????????
                        else if(stringMessageType.equals("result")){

                            Log.e("123","???????????? ???????????? ?????????");

                            JSONObject jsonObject = new JSONObject();
                            message = message.replace("\n", CHANGE_LINE_CHAR);

                            jsonObject.put("caller",caller);
                            jsonObject.put("roomNum",roomNum);
                            jsonObject.put("message", message);
                            jsonObject.put("purpose", "send");
                            jsonObject.put("callPurpose","result");
                            jsonObject.put("sendToNickname",otherUserNickname);
                            out.println(jsonObject.toString());

                        }

                    }
                    //????????? ????????? ?????????
                    else if (purpose.equals("sendImage")) {
                        Log.e("123", "writeThread message :" + message);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", message);
                        jsonObject.put("purpose", "sendImage");
                        out.println(jsonObject.toString());
                        Log.e("123", "????????????4");

                    }
                    //???????????? ?????????
                    else if (purpose.equals("sendNotification")) {
                        Log.e("123","???????????? ????????? ");
                        Log.e("123", "writeThread message :" + message);
                        saveNotification();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", (int) type);
                        jsonObject.put("sendToNickname", sendToNickname);
                        jsonObject.put("postNum", postNum);
                        jsonObject.put("message", message);
                        jsonObject.put("purpose", "sendNotification");
                        out.println(jsonObject.toString());
                    }
                    //????????? ?????????
                    else if (purpose.equals("quit")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", "");
                        jsonObject.put("purpose", "quit");
                        out.println(jsonObject.toString());
                    }
                    //????????????
                    else if (purpose.equals("closeSocket")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("message", "close");
                        jsonObject.put("purpose", "close");
                        out.println(jsonObject.toString());
                    }
                    //????????????
//                    else if (purpose.equals("????????????")){
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("message", "alive");
//                        jsonObject.put("purpose", "????????????");
//                        out.println(jsonObject.toString());
//                    }

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
                //????????? ?????? ??????
                while ((readValue = reader.readLine()) != null) {
                    System.out.println("readvalue  :" + readValue);
                    String writer = null;
                    String message = null;
                    String notice = null;
                    String type = null;

                    //??????????????? ?????? ????????? ????????? ????????? ?????? ??????
                    JSONObject jsonObject = new JSONObject(readValue);
                    try {
                        System.out.println("Notice : " + jsonObject.getString("notice"));
                        notice = jsonObject.getString("notice");
                        writer = jsonObject.getString("writer");
                        message = jsonObject.getString("message");
                        type=jsonObject.getString("type");
                    } catch (Exception e) {
                    }

                    //????????? ?????? ?????? ??????. ??? ????????? ???????????? ??????
                    if (jsonObject.getString("notice").equals("????????????")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("nickname");

                        Log.e("123", "jsonArray : " + jsonArray);
                        ArrayList<String> roomUsers = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            roomUsers.add(jsonArray.getString(i));
                        }
                        Log.e("123", "jsonArray length : " + jsonArray.length());
                        Intent intent = new Intent("chatData");
                        intent.putStringArrayListExtra("roomUsers", roomUsers);
                        intent.putExtra("purpose", "????????????");
                        intent.putExtra("peopleNum", jsonObject.getInt("peopleNum"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

//                        try{
//                            Thread.sleep(500);
//                        }catch (Exception e){
//
//                        }

                        continue;
                    }
                    //????????? ?????? ????????? ???????????? ??????
                    else if (jsonObject.getString("notice").equals("????????????")) {
                        //???????????? ??? ???, ???????????? reload ????????????
                        Intent intent = new Intent("chatData");
                        intent.putExtra("purpose", "????????????");
                        intent.putExtra("peopleNum", jsonObject.getInt("peopleNum"));
                        intent.putExtra("nickname", jsonObject.getString("nickname"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                        continue;
                    }
                    //????????? ?????? ?????? ????????? ??????
                    else if (jsonObject.getString("notice").equals("????????????")) {
                        Intent intent = new Intent("chatData");
                        intent.putExtra("purpose", "????????????");
                        intent.putExtra("peopleNum", jsonObject.getInt("peopleNum"));
                        intent.putExtra("nickname", jsonObject.getString("nickname"));
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                        continue;
                    }
//                    else if (jsonObject.getString("notice").equals("????????????")){
//                        writeThread writeThread = new writeThread("alive", "????????????");
//                        writeThread.start();
//                        continue;
//                    }
                    //?????? ????????? ?????????
                    else if (jsonObject.getString("notice").equals("????????????")) {
                        //?????? ?????? ?????????
                        makeIndividualNotification(readValue);
                        continue;
                    }

                    //????????? ????????? debugging ????????? ????????? ?????? ??? ?????? ???????????? ??????
                    if (writer == null || message == null) {
                        Log.e("123", "notice??? ??????");
                        continue;
                    }

                    //??????????????? ???????????? ??????
                    if (notice.equals("????????????")) {
                        //?????? ????????? ????????? ??????
                        try {
                            String notifyRoom = jsonObject.getString("notifyRoom");

                            //????????? ?????? ??????, ????????????????????? ?????????, ????????? reload ?????????. ????????? ?????????.
                            Intent intent = new Intent("reloadRoomList");
                            message = message.replace(CHANGE_LINE_CHAR, "\n");
                            intent.putExtra("purpose", "reloadRoomList");
                            intent.putExtra("message", message);
                            LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                            //???????????? ?????? ???????????? ?????? ???????????? ???????????? ?????? ????????? ??????
                            boolean alarmCheck = false;
                            for (int i = 0; i < getNoAlarmRoomArrayList().size(); i++) {

                                if (getNoAlarmRoomArrayList().get(i) != null) {

                                    if (getNoAlarmRoomArrayList().get(i).equals(notifyRoom)) {
                                        alarmCheck = true;
                                        break;
                                    }

                                }
                            }
                            if (alarmCheck) {
                                continue;
                            }
                            if(type!=null){
                                if(type.equals("call")){
                                    continue;
                                }
                            }

                            //Notification ?????????
                            Intent notifyIntent = new Intent(Service_Example.this, Activity_trade_chat.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Service_Example.this);
                            stackBuilder.addNextIntentWithParentStack(notifyIntent);
                            Intent backStackIntent = stackBuilder.editIntentAt(0);
                            backStackIntent.putExtra("chatFragment", "chatFragment");
//                            Intent backStack2Intent=stackBuilder.editIntentAt(1);
//                            backStack2Intent.putExtra("check","123");

                            //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                            PendingIntent notifyStackPendingIntent = stackBuilder.getPendingIntent(Integer.parseInt(notifyRoom), PendingIntent.FLAG_CANCEL_CURRENT);
                            builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentTitle("?????? ?????? ?????????")
                                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

                                    .setContentIntent(notifyStackPendingIntent)

                                    .setContentText(writer + " : " + message)
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
                            System.out.println("?????? ???????????? ?????? ??????");
                            e.printStackTrace();
                        }
                    } else if (notice.equals("????????????")) {

                        //??????????????? ?????? ?????? (?????? ?????? ????????? ???)
                        Intent intent = new Intent("chatData");
                        message = message.replace(CHANGE_LINE_CHAR, "\n");
                        //readValue = readValue.replace(CHANGE_LINE_CHAR, "\n");
                        if(type==null){
                            intent.putExtra("type", "text");
                        }
                        else if(type.equals("call")){
                            intent.putExtra("type", "call");
                        }
                        intent.putExtra("writer", writer);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);

                    } else if (notice.equals("???????????????")) {
                        Intent intent = new Intent("chatData");
                        intent.putExtra("type", "image");
                        intent.putExtra("writer", writer);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                    }


                }
                reader.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("123", "????????? ?????? ListeningThread ??????");
                writeThread writeThread = new writeThread("close", "closeSocket");
                writeThread.start();

                try {
                    socket.close();
                } catch (Exception ea) {
                    Log.e("123", ea.toString());
                }

                //????????? ?????? ????????????, service ??????????????? ????????? ??????
                handleMessage("???????????? ????????????/ ????????? ??????");
                //setNotificationService();
                stopSelf();


                Intent intent = new Intent(getApplicationContext(), Service_Example.class);
                startService(intent);
            }

        }

    }

    public void makeIndividualNotification(String readValue) {

        try {
            Log.e("123", "???????????? ?????????");
            JSONObject jsonObject = new JSONObject(readValue);

            String postNum = jsonObject.getString("postNum");
            int type = jsonObject.getInt("type");

            Log.e("123", "type" + type);
            String message = jsonObject.getString("message");
            String title = "";

            Intent notifyIntent = new Intent();
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Service_Example.this);
            //?????? type ?????? ????????? ???????????????
            if (type == 0) {
                title = "???????????? ??????";
                notifyIntent = new Intent(Service_Example.this, Activity_review_write.class);
                notifyIntent.putExtra("postNum", postNum);
                notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                stackBuilder.addNextIntentWithParentStack(notifyIntent);

                Intent firstBackStackIntent = stackBuilder.editIntentAt(1);
                firstBackStackIntent.putExtra("boughtList", "boughtList");
                Intent backStackIntent = stackBuilder.editIntentAt(0);
                backStackIntent.putExtra("mypageFragment", "mypageFragment");
            } else if (type == -1) {
                Intent intent = new Intent(Service_Example.this, Activity_video_call.class);
                intent.putExtra("roomNum", postNum);
                intent.putExtra("sendToNickname", message);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            } else if (type == -2) {
                Intent intent = new Intent("videoCall");
                intent.putExtra("purpose", "disconnect");
                intent.putExtra("otherUserNickname", message);
                LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
                return;
            }
            else if (type == 1) {
                notifyIntent = new Intent(Service_Example.this, Activity_writer_review_collect.class);
                title = "???????????? ??????";
                notifyIntent.putExtra("email", email);
                notifyIntent.putExtra("nickname", nickname);
                notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                stackBuilder.addNextIntentWithParentStack(notifyIntent);
                Intent backStackIntent = stackBuilder.editIntentAt(0);
                backStackIntent.putExtra("mypageFragment", "mypageFragment");
            } else if (type == 2) {
                notifyIntent = new Intent(Service_Example.this, Activity_post_read.class);
                title = "????????? ??????";
                notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                notifyIntent.putExtra("postNum", postNum);
                stackBuilder.addNextIntentWithParentStack(notifyIntent);
//                Intent backStackIntent = stackBuilder.editIntentAt(0);
//                backStackIntent.putExtra("mypageFragment", "mypageFragment");
            }
            Intent intent = new Intent("reloadRoomList");
            intent.putExtra("purpose", "reloadAlarmImage");
            LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(intent);
            Intent mainHomeIntent = new Intent("homeReloadAlarm");
            mainHomeIntent.putExtra("purpose", "reloadAlarmImage");
            LocalBroadcastManager.getInstance(Service_Example.this).sendBroadcast(mainHomeIntent);
            //Notification ?????????

            //Intent backStackIntent = stackBuilder.editIntentAt(0);
            //backStackIntent.putExtra("chatFragment", "chatFragment");
//                            Intent backStack2Intent=stackBuilder.editIntentAt(1);
//                            backStack2Intent.putExtra("check","123");

            //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);




            //????????? ?????????.delay ?????????
            if(type==-3){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentTitle("????????? ??????")
                                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                                .setContentText(message)
                                .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                                .setAutoCancel(true);
                        notificationManager.notify(1000, builder.build());
                    }
                },400);
            }
            else{
                PendingIntent notifyStackPendingIntent = stackBuilder.getPendingIntent(1000, PendingIntent.FLAG_CANCEL_CURRENT);
                builder = new NotificationCompat.Builder(Service_Example.this, CHANNEL_ID)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(title)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setContentIntent(notifyStackPendingIntent)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                        .setAutoCancel(true)
                        .setGroup(notiGroup);
                notificationManager.notify(1000, builder.build());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createNotificationChannel() {
        //Manager, builder,channel ???????????? notification ?????????.
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

    public void handleMessage(String message) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public ArrayList<String> getNoAlarmRoomArrayList() {

        //gson ??? ??????????????? shared??? ????????? string??? object??? ??????
        Gson gson = new GsonBuilder().create();

        ArrayList<String> noAlarmArrayList;
        String stringToObject = sharedPreferences.getString("noAlarmArrayList", "");
        Type arraylistType = new TypeToken<ArrayList<String>>() {                           //Type, TypeToken??? ??????????????? ???????????? ?????? ????????? ????????????.
        }.getType();
        try {
            noAlarmArrayList = gson.fromJson(stringToObject, arraylistType);
            if (noAlarmArrayList == null) {
                noAlarmArrayList = new ArrayList<String>();
            }
            return noAlarmArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return noAlarmArrayList = new ArrayList<>();
        }

    }
}