package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicsmarket.Adapter.Adapter_buyer_choice;
import com.example.electronicsmarket.Dto.DataInquirerAllInfo;
import com.example.electronicsmarket.Dto.DataInquirerInfo;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
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
    private String cursorChatTime, phasingNum,postNum;

    private LinearLayoutManager linearLayoutManager;
    private Adapter_buyer_choice adapter;
    private ArrayList<DataInquirerInfo> chatRecentList;
    private boolean isFinalPhase = false, scrollCheck = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_choice_chatlist);
        variableInit();

        Intent intent =getIntent();
        postNum=intent.getStringExtra("postNum");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataInquirerAllInfo> call = service.getInquirerInfo(cursorChatTime, phasingNum, nickname, "", "recentChatList");
        call.enqueue(new Callback<DataInquirerAllInfo>() {
            @Override
            public void onResponse(Call<DataInquirerAllInfo> call, Response<DataInquirerAllInfo> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ArrayList<DataInquirerInfo> chatList = response.body().getInquirerList();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buyerChoiceChatlistRecyclerview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!v.canScrollVertically(1) && scrollCheck) {
                        scrollCheck = false;
                        if (!isFinalPhase) {
                            Call<DataInquirerAllInfo> call = service.getInquirerInfo(cursorChatTime, phasingNum, nickname, "", "recentChatList");
                            call.enqueue(new Callback<DataInquirerAllInfo>() {
                                @Override
                                public void onResponse(Call<DataInquirerAllInfo> call, Response<DataInquirerAllInfo> response) {

                                    if (response.isSuccessful() && response.body() != null) {

                                        ArrayList<DataInquirerInfo> chatList = response.body().getInquirerList();
                                        for (int i = 0; i < chatList.size(); i++) {
                                            chatRecentList.add(chatList.get(i));
                                            Log.e("123", "finalChatTime" + chatList.get(i).getFinalChatTime());
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (chatList.size() != 0) {
                                            cursorChatTime = chatList.get(chatList.size() - 1).getFinalChatTime();
                                        }

                                        if (!response.body().getInquireNum().equals(phasingNum)) {
                                            isFinalPhase = true;
                                        }
                                        scrollCheck = true;
                                    }

                                }

                                @Override
                                public void onFailure(Call<DataInquirerAllInfo> call, Throwable t) {
                                    Log.e("123", t.getMessage());
                                }
                            });

                        }
                    }
                }
            });
        }

        //adapter onclickListener달기
        adapter.setBuyerChoiceListener(new Adapter_buyer_choice.Interface_buyer_choice() {
            @Override
            public void buyerChoiceClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_buyer_choice_chatlist.this);

                builder.setTitle("구매자 선택");
                builder.setMessage("\"" + chatRecentList.get(position).getNickname() + "\"" + " 회원과 거래하셨나요?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sendToNickname=chatRecentList.get(position).getNickname();
//                        Intent intent = new Intent();
//                        intent.putExtra("buyer", chatRecentList.get(position).getNickname());
//                        setResult(RESULT_OK, intent);
                        Call<Void> call = service.tradeSuccess(nickname, postNum,  sendToNickname);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){

                                    Intent intent = new Intent("chatDataToServer");
                                    intent.putExtra("type", 0);
                                    intent.putExtra("purpose", "sendNotification");
                                    intent.putExtra("postNum", postNum);
                                    intent.putExtra("sendToNickname", sendToNickname);
                                    intent.putExtra("message", nickname + "님과 거래완료 되었습니다. 후기를 남겨주세요");
                                    LocalBroadcastManager.getInstance(Activity_buyer_choice_chatlist.this).sendBroadcast(intent);

                                    if(Activity_buyer_choice.activity_buyer_choice!=null){
                                        Activity_buyer_choice.activity_buyer_choice.finish();
                                    }
                                    dialog.dismiss();
                                    finish();
                                    Toast.makeText(getApplicationContext(), "거래완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("123","거래등록 실패"+ t.getMessage());
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    public void variableInit() {

        //phsing 관련
        cursorChatTime = "0";
        phasingNum = "7";

        //retrofit2
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //기본 xml 연결
        buyerChoiceChatlistRecyclerview = findViewById(R.id.buyer_choice_chatlist_recyclerview);
        buyerChoiceChatlistNoText = findViewById(R.id.buyer_choice_chatlist_no_text);
        buyerChoiceChatlistBackImage = findViewById(R.id.buyer_choice_chatlist_back_arrow);

        buyerChoiceChatlistBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickName", "");

        //recyclerview 관련
        linearLayoutManager = new LinearLayoutManager(Activity_buyer_choice_chatlist.this);
        chatRecentList = new ArrayList<>();
        adapter = new Adapter_buyer_choice(chatRecentList, Activity_buyer_choice_chatlist.this);
        buyerChoiceChatlistRecyclerview.setLayoutManager(linearLayoutManager);
        buyerChoiceChatlistRecyclerview.setAdapter(adapter);

    }
}