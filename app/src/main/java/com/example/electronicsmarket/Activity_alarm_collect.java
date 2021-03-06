package com.example.electronicsmarket;

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

        //?????? ?????? ???????????? phasing ??????
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

        // ?????? reload( ??????????????????)
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
                            //???????????? ?????? ??????????????? ????????????
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

        //item onClickListener??????
        adapter.setItemClickListener(new Adapter_alarm_info.Interface_notification_itemClick() {
            @Override
            public void itemClick(int position, Adapter_alarm_info.alarmViewHolder viewHolder) {
                String type = notificationList.get(position).getType();

                if(type!=null){
                    //???????????? ??????
                    if(type.equals("0")){

                        //?????? ????????? ??????????????? ??????
                        if(notificationList.get(position).isReview){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_alarm_collect.this);

                            builder.setTitle("?????? ?????? ??????");
                            builder.setMessage("?????? ????????? ??????????????? ?????????????????????????");
                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
                                    intent.putExtra("postNum",notificationList.get(position).getPostNum());
                                    intent.putExtra("isReview",notificationList.get(position).isReview());
                                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                                    notificationList.get(position).setNotificationIsRead("1");
                                    startActivity(intent);
                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();


                        }
                        //?????? ????????? ????????? ?????? ?????????.
                        else{
                            Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
                            intent.putExtra("postNum",notificationList.get(position).getPostNum());
                            intent.putExtra("isReview",notificationList.get(position).isReview());
                            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                            notificationList.get(position).setNotificationIsRead("1");
                            startActivity(intent);

                        }

                    }
                    //???????????? ??????
                    else if(type.equals("1")){
                        Intent intent = new Intent(Activity_alarm_collect.this,Activity_writer_review_collect.class);
                        intent.putExtra("nickname",nickname);
                        intent.putExtra("email",userId);
                        viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                        notificationList.get(position).setNotificationIsRead("1");
                        startActivity(intent);
                    }
                    else if(type.equals("2")){
                        Intent intent = new Intent(Activity_alarm_collect.this,Activity_post_read.class);
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
//                    //???????????? ??????
//                    if(type.equals("0")){
//
//                        //?????? ????????? ??????????????? ??????
//                        if(notificationList.get(position).isReview){
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_alarm_collect.this);
//
//                            builder.setTitle("?????? ?????? ??????");
//                            builder.setMessage("?????? ????????? ??????????????? ?????????????????????????");
//                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
//                                    intent.putExtra("postNum",notificationList.get(position).getPostNum());
//                                    intent.putExtra("isReview",notificationList.get(position).isReview());
//                                    startActivity(intent);
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.show();
//
//
//                        }
//                        //?????? ????????? ????????? ?????? ?????????.
//                        else{
//                            Intent intent =new Intent(Activity_alarm_collect.this,Activity_review_write.class);
//                            intent.putExtra("postNum",notificationList.get(position).getPostNum());
//                            intent.putExtra("isReview",notificationList.get(position).isReview());
//                            startActivity(intent);
//                        }
//
//                    }
//                    //???????????? ??????
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
        //Phasing ??????
        cursorNum = "0";
        phasingNum = "5";

        //?????? xml
        alarmCollectBackImage=findViewById(R.id.alarm_collect_back_arrow);

        alarmCollectBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //recyclerview ??????
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
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // shared ??? ????????????
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        nickname=sharedPreferences.getString("nickName","");
        userId=sharedPreferences.getString("userId","");


    }
}