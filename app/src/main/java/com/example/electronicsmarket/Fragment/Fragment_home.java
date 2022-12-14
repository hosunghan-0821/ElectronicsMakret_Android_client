package com.example.electronicsmarket.Fragment;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.electronicsmarket.Activity.Activity_alarm_collect;
import com.example.electronicsmarket.Activity.Activity_category_all_post;
import com.example.electronicsmarket.Activity.Activity_post_read;
import com.example.electronicsmarket.Activity.Activity_post_write;
import com.example.electronicsmarket.Activity.Activity_search_all_post;
import com.example.electronicsmarket.Adapter.Adapter_post_all_info;
import com.example.electronicsmarket.Dto.DataNotificationInfo;
import com.example.electronicsmarket.Dto.PostAllInfo;
import com.example.electronicsmarket.Dto.PostInfo;
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
        //화면 생성시 처음 데이터 가져오기.
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"allInfo","");
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                //이 부분 나중에 마지막 체크할 떄 사용할거야 갖고있어.

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


                if(!response.body().getProductNum().equals("5")){
                    isFinalPhase=true;
                }
                onCreateViewIsSet=true;
            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {


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

        //하단 터치시 페이징해서 정보 가져와서 postList에 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if(!v.canScrollVertically(1)&&scrollCheck){

                            scrollCheck=false;
//                            Toast.makeText(getActivity(), "스크롤의 최하단입니다.", Toast.LENGTH_SHORT).show()
                            System.out.println("postinfoSize : "+postList.size());
                            if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"allInfo","");
                                call.enqueue(new Callback<PostAllInfo>() {
                                    @Override
                                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                        System.out.println("불러온 item 갯수 : "+response.body().getProductNum());
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
                                        //응답 온 데이터 갯수가 5개가 아니라면 마지막 phase.
                                        if(!response.body().getProductNum().equals("5")){
                                            isFinalPhase=true;
                                        }
                                        scrollCheck=true;
                                    }
                                    @Override
                                    public void onFailure(Call<PostAllInfo> call, Throwable t) {

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

        //카테고리 선택시 카테고리 선택화면으로 이동
        postCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_category_all_post.class);
                startActivity(intent);
            }
        });

        //아이템 클릭 시  해당 상세보기 아이템으로 이동하기.
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {

                Intent intent =new Intent(getActivity(), Activity_post_read.class);
                intent.putExtra("postNum",postList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        //새로고침 해주는 swipeRefreshLayout.  잠시 주석처리


        mainFrameRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);

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


                    }
                });
            }
        });

        //검색이미지.
        postSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_search_all_post.class);
                startActivity(intent);
            }
        });

        //알림이미지
        alarmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), Activity_alarm_collect.class);
                startActivity(intent);
            }
        });

        return view;
    }


    View.OnClickListener postWriteClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), Activity_post_write.class);
            startActivity(intent);
        }
    };

    public void variableInit(View view){

        ;
        // shared 값 가져오기
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

        //onResume 에는 기존에 존재하는 recyclerview 새로 고침하는 코드가 필요할듯.

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


                }
            });


        }

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataReceiver);

    }
}