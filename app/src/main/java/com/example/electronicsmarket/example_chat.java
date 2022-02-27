package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class example_chat extends AppCompatActivity {

    private EditText editText;
    private Button button2;
    private TextView textView,sendText;
    private Handler handler;
    private PrintWriter out;
    private Socket socket;
    private example_adapter adapter;
    private ArrayList<exampleData> dataList;
    private RecyclerView exampleRecyclerview;
    private String nickName;
    private LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver dataReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String readValue=intent.getStringExtra("message");
            System.out.println("chatActivity : "+readValue);

            Message msg = new Message();
            Bundle bundle= new Bundle();
            bundle.putString("read",readValue);
            msg.setData(bundle);;
            handler.sendMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_chat);
        editText = findViewById(R.id.edittext);
        button2 = findViewById(R.id.button2);
        textView = findViewById(R.id.example_textview);


        ///

        exampleRecyclerview=findViewById(R.id.example_recyclerview);
        linearLayoutManager=new LinearLayoutManager(example_chat.this);
        dataList=new ArrayList<>();
        adapter=new example_adapter(dataList);

        exampleRecyclerview.setLayoutManager(linearLayoutManager);
        exampleRecyclerview.setAdapter(adapter);

        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        nickName=sharedPreferences.getString("nickName","");


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                String data=bundle.getString("read");
//                data.split(":");
//
//                try{
//                    dataList.add(new exampleData(data.split(":")[0], data.split(":")[1]));
//                    adapter.notifyDataSetChanged();
//                }catch (Exception e){
//
//                }
                dataList.add(new exampleData("확인",data));
                adapter.notifyDataSetChanged();

            }
        };

//        Fragment_chat.listeningThread.setTcpInterface(new TcpInterface() {
//            @Override
//            public void changeText(String readValue) {
//
//
//                Message msg = new Message();
//                Bundle bundle= new Bundle();
//                bundle.putString("read",readValue);
//                msg.setData(bundle);;
//                handler.sendMessage(msg);
//            }
//        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("여기 들어옴");
                        try {
                            //먼저 port 와 host(ip) 값을 통해서 서버와 연결을한다.
                            socket = new Socket("192.168.0.6", 80);
                            //192.168.163.1

                            //연결이 성공 했다면, 듣는 쓰레드 지속적으로 유지시켜야함.
                            ListeningThread listeningThread = new ListeningThread(socket, getApplicationContext());
                            listeningThread.start();

                            //OutputStream
                            out = new PrintWriter(socket.getOutputStream(),true);
                            // shared 값 가져오기

                            out.println(nickName);
                            out.println("1");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();


            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText.getText().toString()!=null){
                    if(!editText.getText().equals("")){

//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                out.println(editText.getText().toString());
//                            }
//                        });
//                       thread.start();
                        Log.e("123","여기읽음");
                        Intent intent = new Intent("chatDataToServer");
                        intent.putExtra("message", editText.getText().toString());
                        LocalBroadcastManager.getInstance(example_chat.this).sendBroadcast(intent);
                        Log.e("123","여기읽음1");

                    }
                }
                editText.setText("");
            }

        });
    }
    public class ListeningThread extends Thread{

        private Socket socket=null;
        private Context context;

        public ListeningThread(Socket socket, Context context){
            this.socket=socket;
            this.context=context;
        }

        public ListeningThread(Socket socket) {
            this.socket = socket;
        }

        public void run(){

            try{
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String readValue;
                while((readValue=reader.readLine())!=null){

                    System.out.println("readvalue + :"+readValue);
//                    Message msg = new Message();
//                    Bundle bundle= new Bundle();
//                    bundle.putString("read",readValue);
//                    msg.setData(bundle);;
//                    handler.sendMessage(msg);

                }
                reader.close();
                socket.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,new IntentFilter("chatData"));

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{

//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                        out.println("quit");
//                }
//            });
//            thread.start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}