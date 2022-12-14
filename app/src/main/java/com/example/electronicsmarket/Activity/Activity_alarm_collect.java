package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.electronicsmarket.Adapter.Adapter_alarm_info;
import com.example.electronicsmarket.Dto.DataNotificationAllInfo;
import com.example.electronicsmarket.Dto.DataNotificationInfo;
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

public class Activity_alarm_collect extends AppCompatActivity {

    private ImageView alarmCollectBackImage;
    private RecyclerView alarmCollectRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_alarm_info adapter;
    private ArrayList<DataNotificationInfo> notificationList;
    private Retrofit retrofit;
    private String nickname,cursorNum,phasingNum,userId;
    private boolean isFinalPhase = false, scrollCheck = true;
    private SwipeRefreshLayout refreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_collect);
        variableInit();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataNotificationAllInfo> call = service.getNotification(cursorNum,phasingNum,nickname,"");

        call.enqueue(new Callback<DataNotificationAllInfo>() {
            @Override
            public void onResponse(Call<DataNotificationAllInfo> call, Response<DataNotificationAllInfo> response) {

                if(response.isSuccessful()&&response.body()!=null){

                    ArrayList<DataNotificationInfo>notificationDataList=response.body().getNotificationDataList();
                    for(int i=0;i<notificationDataList.size();i++){
                        notificationList.add(notificationDataList.get(i));
                    }
                    adapter.notifyDataSetChanged();

                    if(notificationDataList.size()!=0){
                        cursorNum=notificationDataList.get(notificationDataList.size()-1).getNotificationNum();
                    }
                    if(!response.body().getNotificationNum().equals(phasingNum)){
                        isFinalPhase=true;
                    }
                }
            }

            @Override
            public void onFailure(Call<DataNotificationAllInfo> call, Throwable t) {

            }
        });

        //하단 터치 리스너로 phasing 처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmCollectRecyclerview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!v.canScrollVertically(1) && scrollCheck){
                        scrollCheck=false;
                        if(!isFinalPhase){
                            Call<DataNotificationAllInfo> call = service.getNotification(cursorNum,phasingNum,nickname,"");
                            call.enqueue(new Callback<DataNotificationAllInfo>() {
                                @Override
                                public void onResponse(Call<DataNotificationAllInfo> call, Response<DataNotificationAllInfo> response) {

                                    if(response.isSuccessful()&&response.body()!=null){
                                        ArrayList<DataNotificationInfo>notificationDataList=response.body().getNotificationDataList();
                                        for(int i=0;i<notificationDataList.size();i++){
                                            notificationList.add(notificationDataList.get(i));
                                        }
                                        adapter.notifyDataSetChanged();

                                        if(notificationDataList.size()!=0){
                                            cursorNum=notificationDataList.get(notificationDataList.size()-1).getNotificationNum();
                                        }

                                        if(!response.body().getNotificationNum().equals(phasingNum)){
                                            isFinalPhase=true;
                                        }
                                        scrollCheck=true;
                                    }
                                }

                                @Override
                                public void onFailure(Call<DataNotificationAllInfo> call, Throwable t) {
                                    Log.e("123", t.getMessage());
                                }
                            });

                        }
                    }
                }
            });
        }

        // 화면 reload( 하단스크로링)
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call<DataNotificationAllInfo> call = service.getNotification(cursorNum,phasingNum,nickname,"update");
                call.enqueue(new Callback<DataNotificationAllInfo>() {
                    @Override
                    public void onResponse(Call<DataNotificationAllInfo> call, Response<DataNotificationAllInfo> response) {

                        if(response.isSuccessful() &&response.body()!=null){
                            notificationList.clear();
                            ArrayList<DataNotificationInfo>notificationDataList=response.body().getNotificationDataList();
                            for(int i=0;i<notificationDataList.size();i++){
                                notificationList.add(notificationDataList.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            //새로고침 완료 돌아가는거 멈추는거
                            refreshLayout.setRefreshing(false);

                        }
                    }
                    @Override
                    public void onFailure(Call<DataNotificationAllInfo> call, Throwable t) {
                        Log.e("123", t.getMessage());
                    }
                });
            }
        });

        //item onClickListener달기
        adapter.setItemClickListener(new Adapter_alarm_info.Interface_notification_itemClick() {
            @Override
            public void itemClick(int position, Adapter_alarm_info.alarmViewHolder viewHolder) {
                String type = notificationList.get(position).getType();

                if(type!=null){
                    //거래완료 알림
                    if(type.equals("0")){

                        //기존 리뷰가 존재하는지 확인
                        if(notificationList.get(position).isReview){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_alarm_collect.this);

                            builder.setTitle("리뷰 작성 알림");
                            builder.setMessage("기존 리뷰가 존재합니다 수정하시겠습니까?");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent =new Intent(Activity_alarm_collect.this, Activity_review_write.class);
                                    intent.putExtra("postNum",notificationList.get(position).getPostNum());
                                    intent.putExtra("isReview",notificationList.get(position).isReview());
                                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                                    notificationList.get(position).setNotificationIsRead("1");
                                    startActivity(intent);
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
                        //기존 리뷰가 없다면 바로 넘기기.
                        else{
                            Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
                            intent.putExtra("postNum",notificationList.get(position).getPostNum());
                            intent.putExtra("isReview",notificationList.get(position).isReview());
                            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                            notificationList.get(position).setNotificationIsRead("1");
                            startActivity(intent);

                        }

                    }
                    //리뷰등록 알림
                    else if(type.equals("1")){
                        Intent intent = new Intent(Activity_alarm_collect.this, Activity_writer_review_collect.class);
                        intent.putExtra("nickname",nickname);
                        intent.putExtra("email",userId);
                        viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                        notificationList.get(position).setNotificationIsRead("1");
                        startActivity(intent);
                    }
                    else if(type.equals("2")){
                        Intent intent = new Intent(Activity_alarm_collect.this, Activity_post_read.class);
                        intent.putExtra("postNum",notificationList.get(position).getPostNum());
                        notificationList.get(position).setNotificationIsRead("1");
                        viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                        startActivity(intent);

                    }
                }
            }
        });
//        adapter.setItemClickListener(new Adapter_alarm_info.Interface_notification_itemClick() {
//            @Override
//            public void itemClick(int position) {
//                String type = notificationList.get(position).getType();
//
//                if(type!=null){
//                    //거래완료 알림
//                    if(type.equals("0")){
//
//                        //기존 리뷰가 존재하는지 확인
//                        if(notificationList.get(position).isReview){
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_alarm_collect.this);
//
//                            builder.setTitle("리뷰 작성 알림");
//                            builder.setMessage("기존 리뷰가 존재합니다 수정하시겠습니까?");
//                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
//                                    intent.putExtra("postNum",notificationList.get(position).getPostNum());
//                                    intent.putExtra("isReview",notificationList.get(position).isReview());
//                                    startActivity(intent);
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.show();
//
//
//                        }
//                        //기존 리뷰가 없다면 바로 넘기기.
//                        else{
//                            Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
//                            intent.putExtra("postNum",notificationList.get(position).getPostNum());
//                            intent.putExtra("isReview",notificationList.get(position).isReview());
//                            startActivity(intent);
//                        }
//
//                    }
//                    //리뷰등록 알림
//                    else if(type.equals("1")){
//                        Intent intent = new Intent(Activity_alarm_collect.this,Activity_writer_review_collect.class);
//                        intent.putExtra("nickname",nickname);
//                        intent.putExtra("email",userId);
//                        startActivity(intent);
//                    }
//                    else if(type.equals("2")){
//                        Intent intent = new Intent(Activity_alarm_collect.this,Activity_post_read.class);
//                        intent.putExtra("postNum",notificationList.get(position).getPostNum());
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
    }



    public void variableInit(){


        //refreshlayout
        refreshLayout=findViewById(R.id.alarm_collect_refresh_layout);
        //Phasing 관련
        cursorNum = "0";
        phasingNum = "5";

        //기본 xml
        alarmCollectBackImage=findViewById(R.id.alarm_collect_back_arrow);

        alarmCollectBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //recyclerview 관련
        alarmCollectRecyclerview=findViewById(R.id.alarm_collect_recyclerview);
        notificationList=new ArrayList<>();
        linearLayoutManager= new LinearLayoutManager(Activity_alarm_collect.this);
        adapter=new Adapter_alarm_info(notificationList,Activity_alarm_collect.this);
        alarmCollectRecyclerview.setAdapter(adapter);
        alarmCollectRecyclerview.setLayoutManager(linearLayoutManager);
        //retrofit2
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        nickname=sharedPreferences.getString("nickName","");
        userId=sharedPreferences.getString("userId","");


    }
}