package com.example.electronicsmarket.Fragment;

import android.content.Context;
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

import com.example.electronicsmarket.Adapter.Adapter_post_all_info;
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


public class Fragment_buy_cancel extends Fragment {

    private TextView noResultText;
    private RecyclerView cancelRecyclerView;
    private Adapter_post_all_info adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PostInfo> cancelList;

    private Retrofit retrofit;
    private String cursorPostNum, phasingNum;

    private boolean isFinalPhase = false, scrollCheck = true, onCreateViewIsSet = false;
    private String id;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_cancel, container, false);
        variableInit(view);


        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "cancelInfo", id);

        //기본적인 서버 데이터 가져와서 뿌려주기
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            cancelList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }

                    adapter.setPostList(cancelList);
                    adapter.notifyDataSetChanged();
                    if (cancelList.size() != 0) {
                        cursorPostNum = cancelList.get(cancelList.size() - 1).getRefundRegTime();
                    }

                    if (!response.body().getProductNum().equals("5")) {
                        isFinalPhase = true;
                    }
                    onCreateViewIsSet = true;
                    setNoResultText();
                }
            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {

            }
        });

        //하단 터치시 페이징해서 정보 가져와서 postList에 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cancelRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (!v.canScrollVertically(1) && scrollCheck) {
                        //Log.e("123","스크롤의 최하단입니다. 이거 연속으로 찍히는거면 터치의 문제");
                        scrollCheck = false;
//                            Toast.makeText(getActivity(), "스크롤의 최하단입니다.", Toast.LENGTH_SHORT).show()
                        //System.out.println("postinfoSize : "+sellingList.size());
                        if (!isFinalPhase) {
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "cancelInfo", id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("불러온 item 갯수 : " + response.body().getProductNum());
                                    PostAllInfo postAllInfo = response.body();
                                    int beforePosition = cancelList.size();

                                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                                        try {
                                            postAllInfo.postInfo.get(i).setViewType(0);
                                            cancelList.add(postAllInfo.postInfo.get(i));
                                        } catch (Exception e) {

                                        }
                                    }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                    adapter.setPostList(cancelList);
                                    adapter.notifyItemRangeInserted(beforePosition, 5);
                                    if (cancelList.size() != 0) {
                                        cursorPostNum = cancelList.get(cancelList.size() - 1).getRefundRegTime();
                                    }
                                    //응답 온 데이터 갯수가 5개가 아니라면 마지막 phase.
                                    if (!response.body().getProductNum().equals("5")) {
                                        isFinalPhase = true;
                                    }
                                    scrollCheck = true;
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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","cancelInfo",id);
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        cancelList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                cancelList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(cancelList);
                        adapter.notifyDataSetChanged();
                        //새로고침 완료 돌아가는거 멈추는거
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

    @Override
    public void onResume() {
        super.onResume();

        if (onCreateViewIsSet) {
            RetrofitService service = retrofit.create(RetrofitService.class);
            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, "update", "cancelInfo", id);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : " + response.body().getProductNum());
                   cancelList.clear();
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            cancelList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }
                    adapter.setPostList(cancelList);
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
        if(cancelList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.GONE);
        }

    }
    public void variableInit(View view){


        //xml 기본
        noResultText=view.findViewById(R.id.buy_cancel_no_result_text);

        refreshLayout=view.findViewById(R.id.buy_cancel_refresh_layout);
        // shared 값 가져오기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");

        //커서 페이징 하기위해서 사용
        cursorPostNum = "0";
        phasingNum = "5";

        //retrofit2 관련
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //해당 recyclerview 관련
        cancelRecyclerView = view.findViewById(R.id.buy_cancel_recyclerview);
        cancelList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity()); //or getContext();
        adapter = new Adapter_post_all_info(cancelList, getActivity()); //or getContext();

        cancelRecyclerView.setAdapter(adapter);
        cancelRecyclerView.setLayoutManager(linearLayoutManager);

    }
}