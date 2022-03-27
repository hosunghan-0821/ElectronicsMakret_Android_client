package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_buyer_choice_chatlist extends AppCompatActivity {

    private ImageView buyerChoiceChatlistBackImage;
    private RecyclerView buyerChoiceChatlistRecyclerview;
    private TextView buyerChoiceChatlistNoText;

    private String nickname;
    private Retrofit retrofit;
    private String cursorChatTime,phasingNum;

    private LinearLayoutManager linearLayoutManager;
    private Adapter_buyer_choice adapter;
    private ArrayList<DataInquirerInfo> chatRecentList;
    private boolean isFinalPhase = false, scrollCheck = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_choice_chatlist);
        variableInit();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataInquirerAllInfo> call = service.getInquirerInfo(cursorChatTime, phasingNum, nickname, "","recentChatList");
        call.enqueue(new Callback<DataInquirerAllInfo>() {
            @Override
            public void onResponse(Call<DataInquirerAllInfo> call, Response<DataInquirerAllInfo> response) {

                if(response.isSuccessful() && response.body()!=null){

                    ArrayList<DataInquirerInfo> chatList=response.body().getInquirerList();
                    for (int i = 0; i < chatList.size(); i++) {
                        chatRecentList.add(chatList.get(i));
                        Log.e("123", "finalChatTime" + chatList.get(i).getFinalChatTime());
                    }
                    adapter.notifyDataSetChanged();

                    if (chatList.size() == 0) {
                        buyerChoiceChatlistNoText.setVisibility(View.VISIBLE);
                    } else {
                        //커서 설정
                        cursorChatTime = chatList.get(chatList.size() - 1).getFinalChatTime();
                        Log.e("123", "on create finalChatTime" + cursorChatTime);
                        Log.e("123", "getInquireNum" + response.body().getInquireNum());
                        buyerChoiceChatlistNoText.setVisibility(View.GONE);
                    }
                    if (!response.body().getInquireNum().equals(phasingNum)) {
                        isFinalPhase = true;
                    }
                }

            }

            @Override
            public void onFailure(Call<DataInquirerAllInfo> call, Throwable t) {

            }
        });



    }

    public void variableInit(){

        //phsing 관련
        cursorChatTime="0";
        phasingNum="7";

        //retrofit2
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //기본 xml 연결
        buyerChoiceChatlistRecyclerview=findViewById(R.id.buyer_choice_chatlist_recyclerview);
        buyerChoiceChatlistNoText=findViewById(R.id.buyer_choice_chatlist_no_text);
        buyerChoiceChatlistBackImage=findViewById(R.id.buyer_choice_chatlist_back_arrow);

        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickName", "");

        //recyclerview 관련
        linearLayoutManager = new LinearLayoutManager(Activity_buyer_choice_chatlist.this);
        chatRecentList =new ArrayList<>();
        adapter=new Adapter_buyer_choice(chatRecentList,Activity_buyer_choice_chatlist.this);
        buyerChoiceChatlistRecyclerview.setLayoutManager(linearLayoutManager);
        buyerChoiceChatlistRecyclerview.setAdapter(adapter);

    }
}