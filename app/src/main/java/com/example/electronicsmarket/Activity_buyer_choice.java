package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_buyer_choice extends AppCompatActivity {

    private ImageView buyerChoiceBackImage, buyerChoiceProductImage;
    private TextView buyerChoiceProductTitle, buyerChoiceProductPrice, buyerChoiceMoreUser;
    private RecyclerView buyerChoiceRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Retrofit retrofit;
    private String nickname, postNum;
    private Adapter_buyer_choice adapter;
    private ArrayList<DataInquirerInfo> inquireList;
    private TextView buyerChoiceNoListText;
    private String cursorChatTime, phasingNum;
    private boolean isFinalPhase = false, scrollCheck = true;
    public static Activity_buyer_choice activity_buyer_choice;


    private ActivityResultLauncher<Intent> chatListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        String sendToNickname = intent.getStringExtra("buyer");

                        Log.e("123", "buyer : " + intent.getStringExtra("buyer"));
                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<Void> call = service.tradeSuccess(nickname, postNum, sendToNickname);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {

                                    Log.e("123", "성공");
                                    //PHP 작업 제대로 하고 난 후, TCP를 통해 상대방 회원에게 거래완료 알림.
                                    sendTransactionNotification(sendToNickname);
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                 Log.e("123", t.getMessage());
                            }
                        });

                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_choice);
        variableInit();
        postNum = getIntent().getStringExtra("postNum");

        activity_buyer_choice=Activity_buyer_choice.this;
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataInquirerAllInfo> call = service.getInquirerInfo(cursorChatTime, phasingNum, nickname, postNum, "inquireList");

        call.enqueue(new Callback<DataInquirerAllInfo>() {
            @Override
            public void onResponse(Call<DataInquirerAllInfo> call, Response<DataInquirerAllInfo> response) {

                if (response.isSuccessful() && response.body() != null) {

                    //Log.e("123", "inquirerList size"+response.body().getInquirerList().size());
                    //Log.e("123",response.body().getProductImageRoute());

                    //해당 상품정보 보여주기.
                    Glide.with(Activity_buyer_choice.this).load(response.body().getProductImageRoute()).into(buyerChoiceProductImage);

                    buyerChoiceProductTitle.setText(response.body().getPostTitle());
                    buyerChoiceProductPrice.setText(response.body().getPostPrice() + "원");

                    //문의 리스트 보여주기
                    ArrayList<DataInquirerInfo> inquireUserList = response.body().getInquirerList();

                    for (int i = 0; i < inquireUserList.size(); i++) {
                        inquireList.add(inquireUserList.get(i));
                        Log.e("123", "finalChatTime" + inquireUserList.get(i).getFinalChatTime());

                    }
                    adapter.notifyDataSetChanged();
                    if (inquireUserList.size() == 0) {
                        buyerChoiceNoListText.setVisibility(View.VISIBLE);
                    } else {
                        //커서 설정
                        cursorChatTime = inquireUserList.get(inquireUserList.size() - 1).getFinalChatTime();
                        Log.e("123", "on create finalChatTime" + cursorChatTime);
                        Log.e("123", "getInquireNum" + response.body().getInquireNum());
                        buyerChoiceNoListText.setVisibility(View.GONE);
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
            buyerChoiceRecyclerview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!v.canScrollVertically(1) && scrollCheck) {
                        scrollCheck = false;
                        if (!isFinalPhase) {
                            Call<DataInquirerAllInfo> call = service.getInquirerInfo(cursorChatTime, phasingNum, nickname, postNum, "inquireList");
                            call.enqueue(new Callback<DataInquirerAllInfo>() {
                                @Override
                                public void onResponse(Call<DataInquirerAllInfo> call, Response<DataInquirerAllInfo> response) {


                                    if (response.isSuccessful() && response.body() != null) {
                                        ArrayList<DataInquirerInfo> inquireUserList = response.body().getInquirerList();

                                        for (int i = 0; i < inquireUserList.size(); i++) {
                                            inquireList.add(inquireUserList.get(i));


                                        }
                                        adapter.notifyDataSetChanged();

                                        if (inquireUserList.size() != 0) {
                                            cursorChatTime = inquireUserList.get(inquireUserList.size() - 1).getFinalChatTime();
                                            Log.e("123", "finalChatTime" + cursorChatTime);
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


        adapter.setBuyerChoiceListener(new Adapter_buyer_choice.Interface_buyer_choice() {
            @Override
            public void buyerChoiceClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_buyer_choice.this);

                builder.setTitle("구매자 선택");
                builder.setMessage("\"" + inquireList.get(position).getNickname() + "\"" + " 회원과 거래하셨나요?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Call<Void> call = service.tradeSuccess(nickname, postNum, inquireList.get(position).getNickname());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                if (response.isSuccessful()) {

                                    Log.e("123", "성공");
                                    //PHP 작업 제대로 하고 난 후, TCP를 통해 상대방 회원에게 거래완료 알림.
                                    sendTransactionNotification(inquireList.get(position).getNickname());
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("123", t.getMessage());
                            }
                        });

                        dialog.dismiss();
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

        buyerChoiceMoreUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_buyer_choice.this, Activity_buyer_choice_chatlist.class);
                intent.putExtra("postNum",postNum);
                startActivity(intent);
//              chatListLauncher.launch(intent);
            }
        });

    }

    public void sendTransactionNotification(String sendToNickname) {

        Intent intent = new Intent("chatDataToServer");
        intent.putExtra("type", 0);
        intent.putExtra("purpose", "sendNotification");
        intent.putExtra("postNum", postNum);
        intent.putExtra("sendToNickname", sendToNickname);
        intent.putExtra("message", nickname + "님과 거래완료 되었습니다. 후기를 남겨주세요");
        LocalBroadcastManager.getInstance(Activity_buyer_choice.this).sendBroadcast(intent);
        finish();

    }

    public void variableInit() {

        //Phasing 관련
        cursorChatTime = "0";
        phasingNum = "5";

        //retrofit2

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //xml 기본 연결
        buyerChoiceBackImage = (ImageView) findViewById(R.id.buyer_choice_back_arrow);
        buyerChoiceProductImage = (ImageView) findViewById(R.id.buyer_choice_product_image);
        buyerChoiceProductTitle = (TextView) findViewById(R.id.buyer_choice_product_title);
        buyerChoiceProductPrice = (TextView) findViewById(R.id.buyer_choice_product_price);
        buyerChoiceMoreUser = (TextView) findViewById(R.id.buyer_choice_more_user);
        buyerChoiceNoListText = (TextView) findViewById(R.id.buyer_choice_no_inquirer_text);

        buyerChoiceBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //recyclerview
        buyerChoiceRecyclerview = (RecyclerView) findViewById(R.id.buyer_choice_recyclerview);
        inquireList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(Activity_buyer_choice.this);
        adapter = new Adapter_buyer_choice(inquireList, Activity_buyer_choice.this);

        buyerChoiceRecyclerview.setLayoutManager(linearLayoutManager);
        buyerChoiceRecyclerview.setAdapter(adapter);

        //nickname
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickName", "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity_buyer_choice=null;
    }
}