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


public class Fragment_sell_selling extends Fragment {

    private TextView noResultText;
    private RecyclerView sellingRecyclerView;
    private Adapter_post_all_info adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PostInfo> sellingList;
    private Retrofit retrofit;
    private String cursorPostNum,phasingNum;
    private boolean isFinalPhase=false,scrollCheck=true,onCreateViewIsSet=false;
    private String id;
    private SwipeRefreshLayout refreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_sell_selling,container,false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"sellingInfo",id);

        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                if(response.isSuccessful() &&response.body()!=null){
                    PostAllInfo postAllInfo =response.body();
                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                        try{
                            postAllInfo.postInfo.get(i).setViewType(0);
                            sellingList.add(postAllInfo.postInfo.get(i));
                        }catch (Exception e){

                        }
                    }

                    adapter.setPostList(sellingList);
                    adapter.notifyDataSetChanged();
                    if(sellingList.size()!=0){
                        cursorPostNum=sellingList.get(sellingList.size()-1).getPostNum();
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
            sellingRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(!v.canScrollVertically(1)&&scrollCheck){
                        //Log.e("123","???????????? ??????????????????. ?????? ???????????? ??????????????? ????????? ??????");
                        scrollCheck=false;
//                            Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                        //System.out.println("postinfoSize : "+sellingList.size());
                        if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"sellingInfo",id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("????????? item ?????? : "+response.body().getProductNum());
                                    PostAllInfo postAllInfo =response.body();
                                    int beforePosition=sellingList.size();

                                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                                        try{
                                            postAllInfo.postInfo.get(i).setViewType(0);
                                            sellingList.add(postAllInfo.postInfo.get(i));
                                        }catch (Exception e){

                                        }
                                    }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                    adapter.setPostList(sellingList);
                                    adapter.notifyItemRangeInserted(beforePosition,5);
                                    if(sellingList.size()!=0){
                                        cursorPostNum=sellingList.get(sellingList.size()-1).getPostNum();
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
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "????????? ????????? ???????????? ????????? ??????;", Toast.LENGTH_SHORT).show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","sellingInfo",id);
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        sellingList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                sellingList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(sellingList);
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

        adapter.setConfirmListener(new Adapter_post_all_info.Interface_buy_confirm_click() {
            @Override
            public void onConfirmClick(int position) {
                Intent intent =new Intent(getActivity(),Activity_buyer_choice.class);
                intent.putExtra("postNum",sellingList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),Activity_post_read.class);
                intent.putExtra("postNum",sellingList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","sellingInfo",id);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : "+response.body().getProductNum());
                    sellingList.clear();
                    PostAllInfo postAllInfo =response.body();
                    for(int i=0;i<postAllInfo.postInfo.size();i++){
                        try{
                            postAllInfo.postInfo.get(i).setViewType(0);
                            sellingList.add(postAllInfo.postInfo.get(i));
                        }catch (Exception e){

                        }
                    }
                    adapter.setPostList(sellingList);
                    adapter.notifyDataSetChanged();
                    setNoResultText();
                }

                @Override
                public void onFailure(Call<PostAllInfo> call, Throwable t) {
                    Log.e("123", t.getMessage());

                }
            });
        }
    }

    public void setNoResultText(){
        if(sellingList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.GONE);
        }
    }

    public void variableInit(View view){

        //xml
        noResultText=view.findViewById(R.id.sell_selling_no_result_text);
        refreshLayout=view.findViewById(R.id.sell_selling_refresh_layout);

        // shared ??? ????????????
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");


        //?????? ????????? ??????????????? ??????
        cursorPostNum="0";
        phasingNum="5";


        //retrofit2 ??????
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //?????? recyclerview ??????
        sellingRecyclerView=view.findViewById(R.id.sell_selling_recyclerview);
        sellingList=new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity()); //or getContext();
        adapter=new Adapter_post_all_info(sellingList,getActivity()); //or getContext();
        adapter.setStatus("selling");
        sellingRecyclerView.setAdapter(adapter);
        sellingRecyclerView.setLayoutManager(linearLayoutManager);

    }
}