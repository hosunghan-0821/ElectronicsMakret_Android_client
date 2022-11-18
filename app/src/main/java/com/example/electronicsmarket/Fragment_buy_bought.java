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


public class Fragment_buy_bought extends Fragment {


    private TextView noResultText;
    private ArrayList<PostInfo> boughtList;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_all_info adapter;
    private RecyclerView boughtRecyclerView;
    private Retrofit retrofit;
    private String cursorPostNum, phasingNum;
    private String id;
    private boolean isFinalPhase = false, scrollCheck = true, onCreateViewIsSet = false;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_bought, container, false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "boughtInfo", id);

        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                if (response.isSuccessful() && response.body() != null) {
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            boughtList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }

                    adapter.setPostList(boughtList);
                    adapter.notifyDataSetChanged();
                    if (boughtList.size() != 0) {
                        cursorPostNum = boughtList.get(boughtList.size() - 1).getTradeConfirmTime();
                    }

                    if (!response.body().getProductNum().equals("4")) {
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
            boughtRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "boughtInfo", id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("불러온 item 갯수 : " + response.body().getProductNum());
                                    PostAllInfo postAllInfo = response.body();
                                    int beforePosition = boughtList.size();

                                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                                        try {
                                            postAllInfo.postInfo.get(i).setViewType(0);
                                            boughtList.add(postAllInfo.postInfo.get(i));
                                        } catch (Exception e) {

                                        }
                                    }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                    adapter.setPostList(boughtList);
                                    adapter.notifyItemRangeInserted(beforePosition, 5);
                                    if (boughtList.size() != 0) {
                                        cursorPostNum = boughtList.get(boughtList.size() - 1).getTradeConfirmTime();
                                    }
                                    //응답 온 데이터 갯수가 4개가 아니라면 마지막 phase.
                                    if (!response.body().getProductNum().equals("4")) {
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
        } else {
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","boughtInfo",id);
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        boughtList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                boughtList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(boughtList);
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
        Log.e("123", "bought : onresume");
        if (onCreateViewIsSet) {
            RetrofitService service = retrofit.create(RetrofitService.class);
            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, "update", "boughtInfo", id);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : " + response.body().getProductNum());
                    boughtList.clear();
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            boughtList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }
                    adapter.setPostList(boughtList);
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
        if(boughtList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.GONE);
        }
    }

    public void variableInit(View view) {

        //기본 xml
        noResultText=view.findViewById(R.id.buy_bought_no_result_text);
        //refreshlayout
        refreshLayout=view.findViewById(R.id.buy_bought_refresh_layout);
        // shared 값 가져오기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");

        //커서 페이징 하기위해서 사용
        cursorPostNum = "0";
        phasingNum = "4";

        //retrofit2 관련
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        boughtRecyclerView = view.findViewById(R.id.buy_bought_recyclerview);
        boughtList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity()); //or getContext();
        adapter = new Adapter_post_all_info(boughtList, getActivity()); //or getContext();

        adapter.setStatus("review");
        adapter.setConfirmListener(new Adapter_post_all_info.Interface_buy_confirm_click() {
            @Override
            public void onConfirmClick(int position) {
                Log.e("123", "onconfirmclick");
                Intent intent = new Intent(getActivity(), Activity_review_write.class);
                intent.putExtra("postNum", boughtList.get(position).getPostNum());
                startActivity(intent);
            }
        });
        adapter.setReviewListener(new Adapter_post_all_info.Interface_review_update_delete() {
            @Override
            public void onReviewUpdateClick(int position) {
                Intent intent = new Intent(getActivity(), Activity_review_write.class);
                intent.putExtra("postNum", boughtList.get(position).getPostNum());
                intent.putExtra("isReview", true);
                startActivity(intent);
            }

            @Override
            public void onReviewDeleteClick(int position) {
                Intent intent =new Intent(getActivity(),Activity_review_write.class);
                intent.putExtra("postNum",boughtList.get(position).getPostNum());
                intent.putExtra("isReview",true);
                intent.putExtra("deleteReview",true);
                startActivity(intent);
            }
        });
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                if(boughtList.get(position).getPostTradeType()!=null){
                    if(boughtList.get(position).getPostTradeType().equals("택배거래")){
                        Intent intent = new Intent(getActivity(), Activity_trade_detail_info.class);

                        Log.e("123","tradeNum : "+boughtList.get(position).getTradeNum());
                        Log.e("123","tradeType :"+boughtList.get(position).getPostTradeType());
                        intent.putExtra("tradeType",boughtList.get(position).getPostTradeType());


                        intent.putExtra("tradeNum", boughtList.get(position).getTradeNum());
                        intent.putExtra("readType", "buyer");

                        startActivity(intent);
                    }

                    else{
                        Intent intent =new Intent(getActivity(),Activity_post_read.class);
                        intent.putExtra("postNum",boughtList.get(position).getPostNum());
                        startActivity(intent);

                    }
                }

            }
        });

        boughtRecyclerView.setAdapter(adapter);
        boughtRecyclerView.setLayoutManager(linearLayoutManager);
    }


}