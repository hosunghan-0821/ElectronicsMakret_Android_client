package com.example.electronicsmarket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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


public class Fragment_buy_buying extends Fragment {


    private TextView noResultText;
    private RecyclerView buyingRecyclerView;
    private Adapter_post_all_info adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PostInfo> buyingList;
    private Retrofit retrofit;
    private String cursorPostNum, phasingNum;
    private boolean isFinalPhase = false, scrollCheck = true, onCreateViewIsSet = false;
    private String id;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_buying, container, false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "buyingInfo", id);

        //기본적인 서버 데이터 가져와서 뿌려주기
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            buyingList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }

                    adapter.setPostList(buyingList);
                    adapter.notifyDataSetChanged();
                    if (buyingList.size() != 0) {
                        cursorPostNum = buyingList.get(buyingList.size() - 1).getBuyRegTime();
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
            buyingRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, phasingNum, "buyingInfo", id);
                            call.enqueue(new Callback<PostAllInfo>() {
                                @Override
                                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                                    System.out.println("불러온 item 갯수 : " + response.body().getProductNum());
                                    PostAllInfo postAllInfo = response.body();
                                    int beforePosition = buyingList.size();

                                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                                        try {
                                            postAllInfo.postInfo.get(i).setViewType(0);
                                            buyingList.add(postAllInfo.postInfo.get(i));
                                        } catch (Exception e) {

                                        }
                                    }
//                                        postList.remove(beforePosition-1);
//                                        adapter.notifyItemRemoved(beforePosition-1);

                                    adapter.setPostList(buyingList);
                                    adapter.notifyItemRangeInserted(beforePosition, 5);
                                    if (buyingList.size() != 0) {
                                        cursorPostNum = buyingList.get(buyingList.size() - 1).getBuyRegTime();
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
//                    else if(!v.canScrollVertically(-1)){
//                        //Toast.makeText(getActivity(), "스크롤의 최상단입니다.", Toast.LENGTH_SHORT).show();
//                    }


                }
            });
        } else {
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }

        //구매확정 버튼 눌럿을 떄,처리하기
        adapter.setConfirmListener(new Adapter_post_all_info.Interface_buy_confirm_click() {
            @Override
            public void onConfirmClick(int position) {
                //구매확정 버튼 누를경우.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("상품을 수령하셨나요?");
                builder.setMessage("구매확정 후에는 취소할 수 없습니다.\n 만약 상품 수령 전에 미리 구매확정을 할 경우 사기 위험에 노출될 수 있습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //여기서 확정 누르면 해줘야 하는일
                        // 1. 서버에 데이터 변경    => OK 확인
                        // 2. 현재 client 구매진행중 탭에서-> 구매완료 탭으로 이동하게끔 item 삭제 (Fragment 만들어야함)
                        // 3. 리뷰작성 페이지로 이동 => (Review 작성페이지) => 리뷰작성 OK 마무리,
                        RetrofitService service = retrofit.create(RetrofitService.class);

                        Call<PostInfo> call = service.confirmPayment(buyingList.get(position).getPostNum());
                        call.enqueue(new Callback<PostInfo>() {
                            @Override
                            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                                if(response.isSuccessful() && response.body()!=null){
                                    if(response.body().isSuccess()){
                                        Toast.makeText(getContext(), "구매확정 되었습니다!", Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(getActivity(),Activity_review_write.class);
                                        intent.putExtra("postNum",buyingList.get(position).getPostNum());
                                        startActivity(intent);
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<PostInfo> call, Throwable t) {
                                Toast.makeText(getContext(), "통신 오류!", Toast.LENGTH_SHORT).show();
                            }
                        });

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
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                //Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update","buyingInfo",id);
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        System.out.println("getProductNum : "+response.body().getProductNum());
                        buyingList.clear();
                        PostAllInfo postAllInfo =response.body();
                        for(int i=0;i<postAllInfo.postInfo.size();i++){
                            try{
                                postAllInfo.postInfo.get(i).setViewType(0);
                                buyingList.add(postAllInfo.postInfo.get(i));
                            }catch (Exception e){

                            }
                        }
                        adapter.setPostList(buyingList);
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
        Log.e("123", "buying : onresume");
        if (onCreateViewIsSet) {
            RetrofitService service = retrofit.create(RetrofitService.class);
            //Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum, "update", "buyingInfo", id);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    System.out.println("getProductNum : " + response.body().getProductNum());
                    buyingList.clear();
                    PostAllInfo postAllInfo = response.body();
                    for (int i = 0; i < postAllInfo.postInfo.size(); i++) {
                        try {
                            postAllInfo.postInfo.get(i).setViewType(0);
                            buyingList.add(postAllInfo.postInfo.get(i));
                        } catch (Exception e) {

                        }
                    }
                    adapter.setPostList(buyingList);
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
        if(buyingList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.GONE);
        }
    }

    public void variableInit(View view) {

        //xml 기본
        noResultText=view.findViewById(R.id.buy_buying_no_result_text);
        refreshLayout=view.findViewById(R.id.buy_buying_refresh_layout);
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
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //해당 recyclerview 관련
        buyingRecyclerView = view.findViewById(R.id.buy_buying_recyclerview);
        buyingList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity()); //or getContext();
        adapter = new Adapter_post_all_info(buyingList, getActivity()); //or getContext();
        adapter.setStatus("buy");


        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), Activity_trade_detail_info.class);
                intent.putExtra("tradeNum", buyingList.get(position).getTradeNum());
                intent.putExtra("readType", "buyer");
                startActivity(intent);
            }
        });

        buyingRecyclerView.setAdapter(adapter);
        buyingRecyclerView.setLayoutManager(linearLayoutManager);

    }
}