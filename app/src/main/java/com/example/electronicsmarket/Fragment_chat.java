package com.example.electronicsmarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        variableInit(view);

        //서버로부터 데이터 가져오기..
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName);
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
                }
            }

            @Override
            public void onFailure(Call<DataChatRoomAll> call, Throwable t) {
                Log.e("123",t.getMessage());
            }
        });
        return view;
    }

    public void variableInit(View view) {

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