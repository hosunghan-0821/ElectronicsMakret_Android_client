package com.example.electronicsmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_chat extends Fragment {

    private RecyclerView chatRecyclerView;
    private ImageView chatNotificationImage;
    private Retrofit retrofit;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<DataChatRoom> roomList;
    private String nickName;
    private Adapter_chat_room adapter;
    private String cursorChatRoomNum,phasingNum;
    private boolean isFinalPhase=false,onCreateViewIsSet=false,scrollCheck=true;
    private Handler handler;


    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String readValue = intent.getStringExtra("message");
            //만약 알림이 왔을 떄, 데이터 reload 할 경우라 하나하나 데이터 가져올 때랑 비교좀 해보자.

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("message", readValue);
            msg.setData(bundle);
            Log.e("123","hadnler");
            //__alarm__:1:한포성:asdasd
            handler.sendMessage(msg);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        variableInit(view);

        //서버로부터 데이터 가져오기..
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName,phasingNum,cursorChatRoomNum);
        call.enqueue(new Callback<DataChatRoomAll>() {
            @Override
            public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {
                Log.e("123","통신옴");

                if(response.isSuccessful() && response.body()!=null){
                    Log.e("123","통신옴2");
                    DataChatRoomAll dataChatRoomALL =response.body();
                    Log.e("123",dataChatRoomALL.getRoomList().toString());
                    for(int i=0;i<dataChatRoomALL.getRoomList().size();i++){
                        try{
                            roomList.add(dataChatRoomALL.getRoomList().get(i));
                        }catch (Exception e){

                        }

                    }
                    adapter.notifyDataSetChanged();
                    if(roomList.size()!=0){
                        cursorChatRoomNum=roomList.get(roomList.size()-1).getFinalChatTime();
                    }
                    if(!response.body().getRoomCount().equals(phasingNum)){
                        isFinalPhase=true;
                    }
                    onCreateViewIsSet=true;
                }
            }

            @Override
            public void onFailure(Call<DataChatRoomAll> call, Throwable t) {
                Log.e("123",t.getMessage());
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chatRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(!v.canScrollVertically(1)&&scrollCheck){
                        scrollCheck=false;
                        if(!isFinalPhase){
                            Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName,phasingNum,cursorChatRoomNum);
                            call.enqueue(new Callback<DataChatRoomAll>() {
                                @Override
                                public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                                    if(response.isSuccessful() && response.body()!=null){
                                        Log.e("123","통신옴2");
                                        DataChatRoomAll dataChatRoomALL =response.body();
                                        Log.e("123",dataChatRoomALL.getRoomList().toString());
                                        for(int i=0;i<dataChatRoomALL.getRoomList().size();i++){
                                            try{
                                                roomList.add(dataChatRoomALL.getRoomList().get(i));
                                            }catch (Exception e){

                                            }

                                        }
                                        adapter.notifyDataSetChanged();
                                        if(roomList.size()!=0){
                                            cursorChatRoomNum=roomList.get(roomList.size()-1).getFinalChatTime();
                                        }
                                        if(!response.body().getRoomCount().equals(phasingNum)){
                                            isFinalPhase=true;
                                        }
                                        onCreateViewIsSet=true;
                                        scrollCheck=true;
                                    }
                                }

                                @Override
                                public void onFailure(Call<DataChatRoomAll> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.e("123","fragment확인");
                Bundle bundle = msg.getData();
                String data = bundle.getString("message");
                //데이터를 서버에서 가져올게 아니라 그냥 바로 업뎃해줄까?
                //그러 cursor가 햇갈리게 될텐데

                //시간도 받아와야하는데 .. 일단은 서버로 reload하는걸로 해보자.
                //onresume 이랑 다르겍 없지않나..?

                //이 부분 수정해야함
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName,phasingNum,cursorChatRoomNum);
                call.enqueue(new retrofit2.Callback<DataChatRoomAll>() {
                    @Override
                    public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                        //통신 성공할 경우
                        if(response.isSuccessful()&&response.body()!=null){
                            Log.e("123","response");
                            roomList.clear();
                            Log.e("123",response.body().getRoomList().toString());
                            DataChatRoomAll dataChatRoomALL =response.body();
                            for(int i=0;i<dataChatRoomALL.getRoomList().size();i++){
                                try{
                                    roomList.add(dataChatRoomALL.getRoomList().get(i));
                                }catch (Exception e){

                                }
                            }
                        }
                        adapter.setRoomList(roomList);
                        adapter.notifyDataSetChanged();
                        if(roomList.size()!=0){
                            cursorChatRoomNum=roomList.get(roomList.size()-1).getFinalChatTime();
                        }
                        if(!response.body().getRoomCount().equals(phasingNum)){
                            isFinalPhase=true;
                        }
                    }
                    @Override
                    public void onFailure(Call<DataChatRoomAll> call, Throwable t) {
                        Log.e("123", t.getMessage());
                    }


                });
            }
        };
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataReceiver, new IntentFilter("reloadRoomList"));

        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName,"update",cursorChatRoomNum);
            call.enqueue(new Callback<DataChatRoomAll>() {
                @Override
                public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                    //통신 성공할 경우
                    if(response.isSuccessful()&&response.body()!=null){
                        roomList.clear();
                        DataChatRoomAll dataChatRoomALL =response.body();
                        for(int i=0;i<dataChatRoomALL.getRoomList().size();i++){
                            try{
                                roomList.add(dataChatRoomALL.getRoomList().get(i));
                            }catch (Exception e){

                            }
                        }
                    }
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void onFailure(Call<DataChatRoomAll> call, Throwable t) {
                    Log.e("123", t.getMessage());
                }


            });
        }

    }

    public void variableInit(View view) {

        cursorChatRoomNum="0";
        phasingNum="7";

        //기본 xml 연결.
        chatNotificationImage = view.findViewById(R.id.chat_notification);

        //recyclerview 관련
        chatRecyclerView = view.findViewById(R.id.chat_recyclerview);
        linearLayoutManager=new LinearLayoutManager(getContext());
        roomList=new ArrayList<>();
        adapter=new Adapter_chat_room(getActivity(),roomList);

        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(adapter);

        //adapter clickListener 장착
        adapter.setRoomClickListener(new Adapter_chat_room.Adapter_chat_room_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),Activity_trade_chat.class);
                intent.putExtra("roomNum", roomList.get(position).getRoomNum());
                startActivity(intent);
            }
        });

        //레트로핏 선언
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //닉네임 가져오기..
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");
    }

}