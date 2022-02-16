package com.example.electronicsmarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

                }

            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {

            }
        });

        //하단 터치시 페이징해서 정보 가져와서 postList에 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            soldRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(!v.canScrollVertically(1)&&scrollCheck){
                        //Log.e("123","스크롤의 최하단입니다. 이거 연속으로 찍히는거면 터치의 문제");
                        scrollCheck=false;
//                       Toast.makeText(getActivity(), "스크롤의 최하단입니다.", Toast.LENGTH_SHORT).show()
                        //System.out.println("postinfoSize : "+sellingList.size());
                        if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum,"soldInfo",id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("불러온 item 갯수 : "+response.body().getProductNum());
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

                                    //응답 온 데이터 갯수가 5개가 아니라면 마지막 phase.
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
//                    else if(!v.canScrollVertically(-1)){
//                        //Toast.makeText(getActivity(), "스크롤의 최상단입니다.", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),Activity_trade_detail_info.class);
                intent.putExtra("tradeNum",soldList.get(position).getTradeNum());
                intent.putExtra("readType","seller");
                startActivity(intent);

            }
        });

        return view;
    }

    public void variableInit(View view) {

        cursorPostNum="0";
        phasingNum="5";

        // shared 값 가져오기
        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");

        //retrofit2 관련
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //recyclerview 관련
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