package com.example.electronicsmarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_sell_sold extends Fragment {


    private RecyclerView soldRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_all_info adapter;
    private ArrayList<PostInfo> soldList;
    private String cursorPostNum,phasingNum;
    private String id;
    private Retrofit retrofit;
    private boolean isFinalPhase=false,scrollCheck=true,onCreateViewIsSet=false;
    private SwipeRefreshLayout refreshLayout;
    private TextView noResultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sell_sold, container, false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"soldInfo",id);
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                if(response.isSuccessful() &&response.body()!=null){
                    PostAllInfo postAllInfo =response.body();

                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                        try{
                            postAllInfo.postInfo.get(i).setViewType(0);
                            soldList.add(postAllInfo.postInfo.get(i));
                        }catch (Exception e){

                        }
                    }
                    adapter.setPostList(soldList);
                    adapter.notifyDataSetChanged();
                    if(soldList.size()!=0){
                        cursorPostNum=soldList.get(soldList.size()-1).getBuyRegTime();
                    }


                    if(!response.body().getProductNum().equals("5")){
                        isFinalPhase=true;
                    }
                    onCreateViewIsSet=true;
                    setNoResultText();

                }

            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {

            }
        });

        //?????? ????????? ??????????????? ?????? ???????????? postList??? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            soldRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(!v.canScrollVertically(1)&&scrollCheck){
                        //Log.e("123","???????????? ??????????????????. ?????? ???????????? ??????????????? ????????? ??????");
                        scrollCheck=false;
//                       Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                        //System.out.println("postinfoSize : "+sellingList.size());
                        if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"soldInfo",id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("????????? item ?????? : "+response.body().getProductNum());
                                    PostAllInfo postAllInfo =response.body();
                                    int beforePosition=soldList.size();

                                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                                        try{
                                            postAllInfo.postInfo.get(i).setViewType(0);
                                            soldList.add(postAllInfo.postInfo.get(i));
                                        }catch (Exception e){

                                        }
                                    }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                    adapter.setPostList(soldList);
                                    adapter.notifyItemRangeInserted(beforePosition,5);
                                    if(soldList.size()!=0){
                                        cursorPostNum=soldList.get(soldList.size()-1).getBuyRegTime();
                                    }

                                    //?????? ??? ????????? ????????? 5?????? ???????????? ????????? phase.
                                    if(!response.body().getProductNum().equals("5")){
                                        isFinalPhase=true;
                                    }
                                    scrollCheck=true;
                                    setNoResultText();
                                }
                                @Override
                                public void onFailure(Call<PostAllInfo> call, Throwable t) {
                                    Log.e("123", t.getMessage());

                                }
                            });
                        }


                    }
//                    else if(!v.canScrollVertically(-1)){
//                        //Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "????????? ????????? ???????????? ????????? ??????;", Toast.LENGTH_SHORT).show();
        }
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {

                if(soldList.get(position).getPostTradeType()!=null){
                    //??????????????? ?????? ???????????? ???????????? ??????
                    if(soldList.get(position).getPostTradeType().equals("????????????")){
                        Intent intent = new Intent(getActivity(),Activity_trade_detail_info.class);
                        Log.e("123","tradeNum : "+soldList.get(position).getTradeNum());
                        Log.e("123","tradeType :"+soldList.get(position).getPostTradeType());
                        intent.putExtra("tradeType",soldList.get(position).getPostTradeType());
                        intent.putExtra("tradeNum",soldList.get(position).getTradeNum());
                        intent.putExtra("readType","seller");
                        startActivity(intent);
                    }
                    //???????????? ??????, ?????????????????? ??????.
                    else{
                        Intent intent =new Intent(getActivity(),Activity_post_read.class);
                        intent.putExtra("postNum",soldList.get(position).getPostNum());
                        startActivity(intent);
                    }
                }



            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","soldInfo",id);
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        soldList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                soldList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(soldList);
                        adapter.notifyDataSetChanged();
                        //???????????? ?????? ??????????????? ????????????
                        refreshLayout.setRefreshing(false);
                        setNoResultText();
                    }

                    @Override
                    public void onFailure(Call<PostAllInfo> call, Throwable t) {
                        Log.e("123", t.getMessage());

                    }
                });
            }
        });

        return view;
    }
    public void setNoResultText(){
        if(soldList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.GONE);
        }

    }
    public void variableInit(View view) {

        noResultText=view.findViewById(R.id.sell_sold_no_result_text);
        refreshLayout=view.findViewById(R.id.sell_sold_refresh_layout);

        cursorPostNum="0";
        phasingNum="5";

        // shared ??? ????????????
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");

        //retrofit2 ??????
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //recyclerview ??????
        soldRecyclerView =view.findViewById(R.id.sell_sold_recyclerview);
        soldList=new ArrayList<>();
        adapter =new Adapter_post_all_info(soldList,getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity());

        soldRecyclerView.setLayoutManager(linearLayoutManager);
        soldRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","soldInfo",id);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : "+response.body().getProductNum());
                    soldList.clear();
                    PostAllInfo postAllInfo =response.body();
                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                        try{
                            postAllInfo.postInfo.get(i).setViewType(0);
                            soldList.add(postAllInfo.postInfo.get(i));
                        }catch (Exception e){

                        }
                    }
                    adapter.setPostList(soldList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<PostAllInfo> call, Throwable t) {
                    Log.e("123", t.getMessage());

                }
            });
        }
    }
}