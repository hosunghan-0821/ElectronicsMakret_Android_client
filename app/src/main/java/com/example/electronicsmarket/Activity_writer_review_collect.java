package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_writer_review_collect extends AppCompatActivity {

    private Retrofit retrofit;
    private Adapter_review_info adapter;
    private ArrayList<ReviewInfo> reviewList;
    private RecyclerView reviewRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sharedPreferences;
    private String id;
    private String cursorPostNum, phasingNum;
    private String userId;
    private ImageView backImage;
    private boolean isFinalPhase = false, onCreateViewIsSet = false, scrollCheck = true;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_review_collect);
        variableInit();
        Intent intent = getIntent();
        userId = intent.getStringExtra("email");
        nickname=intent.getStringExtra("nickname");
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ReviewAllInfo> call = service.getReviewInfo(cursorPostNum, phasingNum, userId,nickname);
        call.enqueue(new Callback<ReviewAllInfo>() {
            @Override
            public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewAllInfo reviewAllInfo = response.body();

                    for (int i = 0; i < reviewAllInfo.getReviewInfo().size(); i++) {
                        try {
                            reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.setReviewList(reviewList);
                    adapter.notifyDataSetChanged();
                    if (reviewList.size() != 0) {
                        cursorPostNum = reviewList.get(reviewList.size() - 1).getReviewNo();
                    }
                    if (!response.body().getReviewNum().equals("5")) {
                        isFinalPhase = true;
                    }
                }

            }

            @Override
            public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

            }
        });

        //?????? ????????? ??????????????? ?????? ???????????? postList??? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reviewRecyclerview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (!v.canScrollVertically(1) && scrollCheck) {
                        //Log.e("123","???????????? ??????????????????. ?????? ???????????? ??????????????? ????????? ??????");
                        scrollCheck = false;
                        System.out.println("postinfoSize : " + reviewList.size());

                        if (!isFinalPhase) {
                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<ReviewAllInfo> call = service.getReviewInfo(cursorPostNum, phasingNum, userId,null);
                            call.enqueue(new Callback<ReviewAllInfo>() {
                                @Override
                                public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {
                                    ReviewAllInfo reviewAllInfo= response.body();;
                                    int beforePosition=reviewList.size();

                                    for(int i=0;i<reviewAllInfo.getReviewInfo().size();i++){
                                        try{
                                          reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                                        }catch (Exception e){

                                        }
                                    }
                                    adapter.setReviewList(reviewList);
                                    adapter.notifyItemRangeInserted(beforePosition,5);
                                    if(reviewList.size()!=0){
                                        cursorPostNum=reviewList.get(reviewList.size()-1).getReviewNo();
                                    }
                                    if(!response.body().getReviewNum().equals("5")){
                                        isFinalPhase=true;
                                    }
                                    scrollCheck=true;

                                }

                                @Override
                                public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "????????? ????????? ???????????? ????????? ??????;", Toast.LENGTH_SHORT).show();
        }




        adapter.setListener(new Adapter_review_info.Interface_review_item_click() {
            @Override
            public void onReviewProductClick(int position) {
                Intent intent =new Intent(Activity_writer_review_collect.this,Activity_post_read.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                startActivity(intent);
            }

            @Override
            public void onUserInfoClick(int position) {
                Intent intent = new Intent(Activity_writer_review_collect.this,Activity_seller_info.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                intent.putExtra("nickname",reviewList.get(position).getReviewWriter());
                startActivity(intent);
            }
        });
    }

    public void variableInit() {
        //?????? xml

        backImage=findViewById(R.id.writer_review_collect_back_arrow);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //????????????
        cursorPostNum = "0";
        phasingNum = "5";

        //recyclerview
        reviewList = new ArrayList<>();
        reviewRecyclerview = findViewById(R.id.writer_review_collect_recyclerview);
        linearLayoutManager = new LinearLayoutManager(Activity_writer_review_collect.this);
        adapter = new Adapter_review_info(reviewList, Activity_writer_review_collect.this);

        reviewRecyclerview.setLayoutManager(linearLayoutManager);
        reviewRecyclerview.setAdapter(adapter);

        //retrofit ??????
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");
    }
}