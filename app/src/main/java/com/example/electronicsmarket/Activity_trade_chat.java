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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_trade_chat extends AppCompatActivity {


    private boolean isFinalPhase=false,onCreateViewIsSet=false,scrollCheck=true;
    private int heightSum;
    private ImageView tradeChatImage;
    private TextView tradeChatProductTitle, tradeChatProductPrice, tradeChatLocation;
    private Retrofit retrofit;
    private TextView scrollHeight;
    private String postNum, seller, buyer,roomNum;
    private RecyclerView recyclerView;
    private ArrayList<exampleData> dataList;
    private LinearLayoutManager linearLayoutManager;
    private Handler handler;
    private Adapter_trade_chat adapter;
    private ArrayList<DataChat> chatList;
    private String nickName;
    private EditText tradeChatSendText;
    private ImageView tradeChatImageSend, tradeChatSendTextImage;
    private View.OnLayoutChangeListener layoutChangeListener;
    private String otherUserImageRoute;
    private String cursorChatNum,phasingNum;
    private Adapter_trade_chat.Interface_itemHeightCheck checkHeight;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String readValue = intent.getStringExtra("message");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("message", readValue);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_chat);
        variableInit();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent intent = getIntent();

        //이 채팅방으로 들어오는 경우를 두가지로 나누어서, 생각하고 있다
        //1번. 채팅문의하기를 통해 채팅방 들어오기
        //2번. 현재 존재하는 채팅목록으로 부터 들어오기
        if(intent.getStringExtra("roomNum")==null){
            postNum = intent.getStringExtra("postNum");
            seller = intent.getStringExtra("seller");
            buyer = intent.getStringExtra("buyer");
        }
        else{
            roomNum=intent.getStringExtra("roomNum");
        }

        //        Log.e("123","postNum : "+postNum);
        //        Log.e("123","seller : "+ seller);
        //        Log.e("123","buyer : "+ buyer);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoom> call = service.roomNumCheck(postNum, seller, buyer,roomNum,nickName);


        //채팅방 들어갔을 때, 기본적인 데이터들 가져오기
        call.enqueue(new Callback<DataChatRoom>() {
            @Override
            public void onResponse(Call<DataChatRoom> call, Response<DataChatRoom> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataChatRoom dataChatRoom = response.body();
                    roomNum=dataChatRoom.getRoomNum();
                    otherUserImageRoute=dataChatRoom.getOtherUserImageRoute();
                    tradeChatProductTitle.setText(dataChatRoom.getPostTitle());
                    tradeChatProductPrice.setText(dataChatRoom.getPostPrice() + "원");
                    tradeChatLocation.setText(dataChatRoom.getPostLocationName() + dataChatRoom.getPostLocationAddress());
                    Glide.with(Activity_trade_chat.this).load(dataChatRoom.getImageRoute()).into(tradeChatImage);

                    // 채팅방 정보 받아와서 데이터를 입력해야함.

                    //  데이터 보낼 떄 쓸 것들
                    //  채팅방 입장!!
                    Intent intent = new Intent("chatDataToServer");
                    intent.putExtra("message", dataChatRoom.getRoomNum());
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

                    // 채팅방 정보 입력한 후에, 데이터를 받을 준비를 완료 시킨다.
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).registerReceiver(dataReceiver, new IntentFilter("chatData"));

                    //이런 데이터들 다 불러오고 나서 채팅방 대화내용도 서버로부터 불러오기
                    Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum,phasingNum,cursorChatNum);
                    chatDataCall.enqueue(new Callback<DataChatAll>() {
                        @Override
                        public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                            if(response.isSuccessful()&&response.body()!=null){

                                DataChatAll dataChatAllList=response.body();
                                ArrayList<DataChat> chatArrayList=dataChatAllList.getDataChatAllList();
                                for(int i=0;i<chatArrayList.size();i++){

                                    String writerNickname=chatArrayList.get(i).getNickname();
                                    String chatText=chatArrayList.get(i).getChat();
                                    chatText=chatText.replace(Service_Example.CHANGE_LINE_CHAR,"\n");

                                    String chatTime=chatArrayList.get(i).getChatTime();
                                    Date chatDate;
                                    SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try{
                                        chatDate=chatTimeDbDateFormat.parse(chatTime);
                                        chatTime= chatTimedateFormat.format(chatDate);
                                    }catch (Exception e){

                                    }
                                    //나 자신일 경우
                                    if(writerNickname.equals(nickName)){
                                        chatList.add(0,new DataChat(1,chatText,chatTime,writerNickname));
                                    }
                                    //상대방일 경우
                                    else{
                                        chatList.add(0,new DataChat(0,chatText,chatTime,writerNickname,otherUserImageRoute));
                                    }

                                }
                                setStackFromEnd();
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(chatList.size()-1);
                                if(chatList.size()!=0){
                                    cursorChatNum=chatArrayList.get(chatArrayList.size()-1).getChatTime();
                                }
                                if(!response.body().getChatNum().equals(phasingNum)){
                                    isFinalPhase=true;
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<DataChatAll> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<DataChatRoom> call, Throwable t) {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(!v.canScrollVertically(-1)&&scrollCheck){

                        scrollCheck=false;
                        if(!isFinalPhase){

                            Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum,phasingNum,cursorChatNum);
                            chatDataCall.enqueue(new Callback<DataChatAll>() {
                                @Override
                                public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                                    if(response.isSuccessful()&&response.body()!=null){
                                        DataChatAll dataChatAllList=response.body();
                                        ArrayList<DataChat> chatArrayList=dataChatAllList.getDataChatAllList();
                                        for(int i=0;i<chatArrayList.size();i++){

                                            String writerNickname=chatArrayList.get(i).getNickname();
                                            String chatText=chatArrayList.get(i).getChat();
                                            chatText=chatText.replace(Service_Example.CHANGE_LINE_CHAR,"\n");

                                            String chatTime=chatArrayList.get(i).getChatTime();
                                            Date chatDate;
                                            SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                                            SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            try{
                                                chatDate=chatTimeDbDateFormat.parse(chatTime);
                                                chatTime= chatTimedateFormat.format(chatDate);
                                            }catch (Exception e){

                                            }
                                            //나 자신일 경우
                                            if(writerNickname.equals(nickName)){
                                                chatList.add(0,new DataChat(1,chatText,chatTime,writerNickname));
                                            }
                                            //상대방일 경우
                                            else{
                                                chatList.add(0,new DataChat(0,chatText,chatTime,writerNickname,otherUserImageRoute));
                                            }
                                           adapter.notifyItemInserted(0);
                                        }
                                        setStackFromEnd();
                                        if(chatList.size()!=0){
                                            cursorChatNum=chatArrayList.get(chatArrayList.size()-1).getChatTime();
                                        }
                                        if(!response.body().getChatNum().equals(phasingNum)){
                                            isFinalPhase=true;
                                        }
                                        scrollCheck=true;
                                    }
                                }

                                @Override
                                public void onFailure(Call<DataChatAll> call, Throwable t) {

                                }
                            });

                        }
                    }
                }
            });
        }
        else{
            Toast.makeText(Activity_trade_chat.this, "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }


        //데이터 전송 받았을 때, 데이터 보낸사람 닉네임 , 내용으로 나누어서
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String data = bundle.getString("message");

                try {
                    System.out.println("여기 들어옴?");
                    String writerNickname = data.split(":")[0];
                    String message = data.split(":")[1];

                    String formatedNow=getMessageTime();

                    //작성자가 나 자신일 경우.. 근데 이거 서버에서 이제 안보내서.. 의미 x
                    if (writerNickname.equals(nickName)) {
                        chatList.add(new DataChat(1, message, formatedNow, nickName));
                    }
                    else {
                        chatList.add(new DataChat(0, message, formatedNow, writerNickname,otherUserImageRoute));
                    }
                    setStackFromEnd();
                    adapter.notifyItemInserted(chatList.size()-1);
                    recyclerView.scrollToPosition(chatList.size()-1);

//                  adapter.notifyItemInserted(chatList.size()-1);

                    Log.e("123","recyclerview 바텀"+recyclerView.getBottom());
                    Log.e("123","recyclerview .getheight"+recyclerView.getLayoutManager().getHeight());
                    System.out.println("여기 들어옴?");
                } catch (Exception e) {

                }

            }
        };

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.e("123","3333");
//                if(recyclerView.canScrollVertically(1)||recyclerView.canScrollVertically(-1)){
//                    Log.e("123","4444");
//                    linearLayoutManager.setStackFromEnd(true);
//                }
//            }
//        });

        //텍스트 전송 버튼 누를 경우.
        tradeChatSendTextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tradeChatSendText.getText().toString()!=null ){

                    if(!tradeChatSendText.getText().toString().equals("")){
                        chatList.add(new DataChat(1,tradeChatSendText.getText().toString(),getMessageTime(),nickName));
                        setStackFromEnd();
                        recyclerView.scrollToPosition(chatList.size()-1);
                        adapter.notifyItemInserted(chatList.size()-1);

                        Intent intent = new Intent("chatDataToServer");
                        intent.putExtra("message", tradeChatSendText.getText().toString());
                        LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);
                        tradeChatSendText.setText("");
                    }
                }
            }
        });

    }

    public void setStackFromEnd(){

        if(chatList!=null){
            if(chatList.size()>5){
                linearLayoutManager.setStackFromEnd(true);
            }
        }


    }

    public void variableInit() {


        //
        cursorChatNum="0";
        phasingNum="10";

        //recyclerview 관련
        recyclerView = findViewById(R.id.trade_chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(Activity_trade_chat.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatList = new ArrayList<>();
        adapter = new Adapter_trade_chat(chatList, Activity_trade_chat.this);
        recyclerView.setAdapter(adapter);

        //retrofit
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //xml 기본 연결
        tradeChatProductPrice = (TextView) findViewById(R.id.trade_chat_product_price);
        tradeChatProductTitle = (TextView) findViewById(R.id.trade_chat_product_name);
        tradeChatImage = (ImageView) findViewById(R.id.trade_chat_product_image);
        tradeChatLocation = (TextView) findViewById(R.id.trade_chat_location);

        tradeChatSendText = (EditText) findViewById(R.id.trade_chat_send_text);
        tradeChatImageSend = (ImageView) findViewById(R.id.trade_chat_send_image);
        tradeChatSendTextImage = (ImageView) findViewById(R.id.trade_chat_send_text_image);


        //shared로 내 닉네임 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //채팅방 화면 나갈 때, 채팅방 나간 것을 자바 채팅서버에 데이터 전송해서 알려야함.
        Intent intent = new Intent("chatDataToServer");

        intent.putExtra("message", "quit");
        LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }


    public String getMessageTime(){

        String formatedNow;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            formatedNow = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        else{
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            //달->일 -> 시간 -> 분 -> 초 로 차이나는지 확인해서
            formatedNow = formatter.format(date);
        }
        return formatedNow;
    }
    public void displayHeight() {

        //화면 관련 내용 정리

        //        scrollHeight=findViewById(R.id.trade_chat_scroll_height_text);
//
//        Display display= getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics =new DisplayMetrics();
//        display.getMetrics(outMetrics);
//
//        float density=getResources().getDisplayMetrics().density;
//        float dpHeight= outMetrics.heightPixels/density;
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
//
//        ConstraintLayout.LayoutParams mLayoutParams =(ConstraintLayout.LayoutParams) scrollHeight.getLayoutParams();
//
//        int realDeviceWidth = displayMetrics.widthPixels;
//        int realDeviceHeight = displayMetrics.heightPixels;
//
//        Point size = new Point();
//        display.getSize(size); // or getSize(size)
//
//        Log.e("123","x : "+size.x);
//        Log.e("123","y : "+size.y);
//        mLayoutParams.bottomMargin=size.y-(int)(40*density);
//        Log.e("123","size : "+(size.y-(int)(20*density)));
//        scrollHeight.setLayoutParams(mLayoutParams);
        //2220
        //2160
    }
}