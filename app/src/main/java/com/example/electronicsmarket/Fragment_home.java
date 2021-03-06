package com.example.electronicsmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_home extends Fragment  {

    private ArrayList<PostInfo> postList;
    private Adapter_post_all_info adapter;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ImageView postWriteImage,postCategoryImage,postSearchImage;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mainFrameRefresh;
    private String cursorPostNum,phasingNum;
    private boolean isFinalPhase=false,onCreateViewIsSet=false,scrollCheck=true;
    private ImageView alarmImage;
    private String nickname;
    private Handler handler;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String purpose = intent.getStringExtra("purpose");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            if (purpose.equals("reloadAlarmImage")) {
                bundle.putString("purpose","reloadAlarmImage");
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home,container,false);
        variableInit(view);
        //?????? ????????? ?????? ????????? ????????????.
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"allInfo","");
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                //??? ?????? ????????? ????????? ????????? ??? ??????????????? ????????????.
                //Log.e("123",response.body().getProductNum());
                System.out.println("getProductNum : "+response.body().getProductNum());
                PostAllInfo postAllInfo =response.body();
                for(int i=0;i<postAllInfo.postInfo.size();i++){
                    try{
                        postAllInfo.postInfo.get(i).setViewType(0);
                        postList.add(postAllInfo.postInfo.get(i));
                    }catch (Exception e){

                    }
                }
                adapter.setPostList(postList);
                adapter.notifyDataSetChanged();
                if(postList.size()!=0){
                    cursorPostNum=postList.get(postList.size()-1).getPostNum();
                }

                //Log.e("123","oncreateView CursorPostNum"+cursorPostNum);
                if(!response.body().getProductNum().equals("5")){
                    isFinalPhase=true;
                }
                onCreateViewIsSet=true;
            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {
                Log.e("123", t.getMessage());

            }
        });

        Call<DataNotificationInfo> alarmCall= service.notificationCheck(nickname);
        alarmCall.enqueue(new Callback<DataNotificationInfo>() {
            @Override
            public void onResponse(Call<DataNotificationInfo> call, Response<DataNotificationInfo> response) {
                if(response.isSuccessful() && response.body()!=null){
                    if(response.body().isNotification()){
                        alarmImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                    }
                    else{
                        alarmImage.setImageResource(R.drawable.ic_baseline_notifications_24);
                    }

                }
            }

            @Override
            public void onFailure(Call<DataNotificationInfo> call, Throwable t) {

            }
        });

        //?????? ????????? ??????????????? ?????? ???????????? postList??? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if(!v.canScrollVertically(1)&&scrollCheck){
                            //Log.e("123","???????????? ??????????????????. ?????? ???????????? ??????????????? ????????? ??????");
                            scrollCheck=false;
//                            Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                            System.out.println("postinfoSize : "+postList.size());
                            if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"allInfo","");
                                call.enqueue(new Callback<PostAllInfo>() {
                                    @Override
                                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                        System.out.println("????????? item ?????? : "+response.body().getProductNum());
                                        PostAllInfo postAllInfo =response.body();
                                        int beforePosition=postList.size();

                                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                                            try{
                                                postAllInfo.postInfo.get(i).setViewType(0);
                                                postList.add(postAllInfo.postInfo.get(i));
                                            }catch (Exception e){

                                            }
                                        }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                        adapter.setPostList(postList);
                                        adapter.notifyItemRangeInserted(beforePosition,5);
                                        if(postList.size()!=0){
                                            cursorPostNum=postList.get(postList.size()-1).getPostNum();
                                        }
                                        //?????? ??? ????????? ????????? 5?????? ???????????? ????????? phase.
                                        if(!response.body().getProductNum().equals("5")){
                                            isFinalPhase=true;
                                        }
                                        scrollCheck=true;
                                    }
                                    @Override
                                    public void onFailure(Call<PostAllInfo> call, Throwable t) {
                                        Log.e("123", t.getMessage());

                                    }
                                });
                            }


                        }
//                        else if(!v.canScrollVertically(-1)){
//                            //Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
//                        }



                }
            });
        }
        else{
            Toast.makeText(getActivity(), "????????? ????????? ???????????? ????????? ??????;", Toast.LENGTH_SHORT).show();
        }

        //handler
        handler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose=bundle.getString("purpose");
                if(purpose.equals("reloadAlarmImage")){
                    alarmImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                    return;
                }
            }
        };

        //???????????? ????????? ???????????? ?????????????????? ??????
        postCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_category_all_post.class);
                startActivity(intent);
            }
        });

        //????????? ?????? ???  ?????? ???????????? ??????????????? ????????????.
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                //Log.e("123","onItemclick");
                Intent intent =new Intent(getActivity(),Activity_post_read.class);
                intent.putExtra("postNum",postList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        //???????????? ????????? swipeRefreshLayout.  ?????? ????????????


        mainFrameRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","allInfo","");
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        postList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                postList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(postList);
                        adapter.notifyDataSetChanged();
                        mainFrameRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<PostAllInfo> call, Throwable t) {
                        Log.e("123", t.getMessage());

                    }
                });
            }
        });

        //???????????????.
        postSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_search_all_post.class);
                startActivity(intent);
            }
        });

        //???????????????
        alarmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_alarm_collect.class);
                startActivity(intent);
            }
        });

        return view;
    }


    View.OnClickListener postWriteClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),Activity_post_write.class);
            startActivity(intent);
        }
    };

    public void variableInit(View view){

        ;
        // shared ??? ????????????
        SharedPreferences sharedPreferences= getContext().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        nickname=sharedPreferences.getString("nickName","");


        alarmImage=view.findViewById(R.id.home_alarm_image);

        cursorPostNum="0";
        phasingNum="5";

        postList=new ArrayList<>();

        postSearchImage=view.findViewById(R.id.home_search_image);
        mainFrameRefresh =view.findViewById(R.id.main_frame_refresh);

        postWriteImage=view.findViewById(R.id.post_write_plus_image);
        postWriteImage.setOnClickListener(postWriteClick);

        recyclerView=view.findViewById(R.id.post_all_recycelrview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter=new Adapter_post_all_info(postList,getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        postCategoryImage=view.findViewById(R.id.home_category_choice_image);

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataReceiver, new IntentFilter("homeReloadAlarm"));
        //Log.e("123","onresume : ");
        //onResume ?????? ????????? ???????????? recyclerview ?????? ???????????? ????????? ????????????.

        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataNotificationInfo> alarmCall= service.notificationCheck(nickname);
            alarmCall.enqueue(new Callback<DataNotificationInfo>() {
                @Override
                public void onResponse(Call<DataNotificationInfo> call, Response<DataNotificationInfo> response) {
                    if(response.isSuccessful() && response.body()!=null){
                        if(response.body().isNotification()){
                            alarmImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);

                        }
                        else{
                            alarmImage.setImageResource(R.drawable.ic_baseline_notifications_24);

                        }

                    }
                }

                @Override
                public void onFailure(Call<DataNotificationInfo> call, Throwable t) {

                }
            });


            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","allInfo","");
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : "+response.body().getProductNum());
                    postList.clear();
                    PostAllInfo postAllInfo =response.body();
                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                        try{
                            postAllInfo.postInfo.get(i).setViewType(0);
                            postList.add(postAllInfo.postInfo.get(i));
                        }catch (Exception e){

                        }
                    }
                    adapter.setPostList(postList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<PostAllInfo> call, Throwable t) {
                    Log.e("123", t.getMessage());

                }
            });


        }

    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.e("123","onstop : ");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.e("123","onstart : ");
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataReceiver);
        //Log.e("123","onPause : ");
    }
}