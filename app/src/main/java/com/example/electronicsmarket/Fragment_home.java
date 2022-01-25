package com.example.electronicsmarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_home extends Fragment  {

    ArrayList<PostInfo> postList;
    Adapter_post_all_info adapter;
    RecyclerView recyclerView;
    Retrofit retrofit;
    ImageView settingImage;
    Button button;
    ImageView postWriteImage;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home,container,false);
        variableInit(view);


        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getPostAllInfo();

        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                PostAllInfo postAllInfo =response.body();

                for(int i=0;i<postAllInfo.postInfo.size();i++){
                    try{
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

        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Log.e("123","onItemclick");
                Intent intent =new Intent(getActivity(),Activity_post_read.class);
                intent.putExtra("postNum",postList.get(position).getPostNum());
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

        postList=new ArrayList<>();
        postWriteImage=view.findViewById(R.id.post_write_plus_image);
        postWriteImage.setOnClickListener(postWriteClick);
        recyclerView=view.findViewById(R.id.post_all_recycelrview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter=new Adapter_post_all_info(postList,getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

}