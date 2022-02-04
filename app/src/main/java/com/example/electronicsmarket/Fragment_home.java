package com.example.electronicsmarket;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home,container,false);
        variableInit(view);
        //화면 생성시 처음 데이터 가져오기.
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum);
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                //이 부분 나중에 마지막 체크할 떄 사용할거야 갖고있어.
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
                cursorPostNum=postList.get(postList.size()-1).getPostNum();
                Log.e("123","oncreateView CursorPostNum"+cursorPostNum);
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

        //하단 터치시 페이징해서 정보 가져와서 postList에 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if(!v.canScrollVertically(1)&&scrollCheck){
                            Log.e("123","스크롤의 최하단입니다. 이거 연속으로 찍히는거면 터치의 문제");
                            scrollCheck=false;
//                            Toast.makeText(getActivity(), "스크롤의 최하단입니다.", Toast.LENGTH_SHORT).show()
                            System.out.println("postinfoSize : "+postList.size());
                            if(!isFinalPhase){
//                                postList.add(new PostInfo(1));
//                                adapter.notifyItemInserted(postList.size()-1);
                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,phasingNum);
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
                                        cursorPostNum=postList.get(postList.size()-1).getPostNum();
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


                        } else if(!v.canScrollVertically(-1)){
                            //Toast.makeText(getActivity(), "스크롤의 최상단입니다.", Toast.LENGTH_SHORT).show();
                        }



                }
            });
        }
        else{
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }


        //카테고리 선택시 카테고리 선택화면으로 이동
        postCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_category_all_post.class);
                startActivity(intent);
            }
        });

        //아이템 클릭 시  해당 상세보기 아이템으로 이동하기.
        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Log.e("123","onItemclick");
                Intent intent =new Intent(getActivity(),Activity_post_read.class);
                intent.putExtra("postNum",postList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        //새로고침 해주는 swipeRefreshLayout.  잠시 주석처리


        mainFrameRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                RetrofitService service = retrofit.create(RetrofitService.class);
                Log.e("123","onRefresh CursorPostNum"+cursorPostNum);
                Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update");
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

        postSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Activity_search_all_post.class);
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
        Log.e("123","onresume : ");
        //onResume 에는 기존에 존재하는 recyclerview 새로 고침하는 코드가 필요할듯.

        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            Log.e("123","onResume CursorPostNum"+cursorPostNum);
            Call<PostAllInfo> call = service.getPostAllInfo(cursorPostNum,"update");
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
        Log.e("123","onstop : ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("123","onstart : ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("123","onPause : ");
    }
}