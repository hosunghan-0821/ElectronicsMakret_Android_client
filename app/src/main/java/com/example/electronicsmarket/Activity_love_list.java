package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class Activity_love_list extends AppCompatActivity {

    private ImageView backImage;
    private Adapter_post_all_info adapter;
    private RecyclerView loveListRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PostInfo> loveList;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private String id;
    private TextView noResultText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_list);
        variableInit();


        //retrofit 통신하여서 리사이클러뷰 만들기.
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostAllInfo> call = service.getClientInfo(id,"loveList");
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                Log.e("456","통신성공");
                if(response.isSuccessful() && response.body()!=null){
                    PostAllInfo postAllInfo = response.body();
                    Log.e("123",response.body().getProductNum());

                    loveList=postAllInfo.postInfo;
                    if(loveList.size()==0){
                        noResultText.setVisibility(View.VISIBLE);
                    }
                    else{
                        noResultText.setVisibility(View.INVISIBLE);
                    }
                    adapter.setPostList(loveList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {
                Log.e("456","통신실패");
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void variableInit(){


        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // shared 값 가져오기
        sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");

        noResultText=(TextView) findViewById(R.id.love_list_no_result_text);

        //recyclerview 관련
        loveListRecyclerview=findViewById(R.id.love_list_recyclerview);
        linearLayoutManager=new LinearLayoutManager(Activity_love_list.this);
        loveList =new ArrayList<>();
        adapter = new Adapter_post_all_info(loveList,Activity_love_list.this);

        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent =new Intent(Activity_love_list.this,Activity_post_read.class);
                intent.putExtra("postNum",loveList.get(position).getPostNum());
                startActivity(intent);
            }
        });
        adapter.setLikeListCancelListener(new Adapter_post_all_info.Interface_like_list_cancel() {
            @Override
            public void onItemCancel(int position) {
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataMemberSignup> call = service.setLikeList(id,loveList.get(position).getPostNum(),"delete");
                call.enqueue(new Callback<DataMemberSignup>() {
                    @Override
                    public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {

                        if(response.isSuccessful()&&response.body().isSuccess()){

                            loveList.remove(position);
                            if(loveList.size()==0){
                                noResultText.setVisibility(View.VISIBLE);
                            }
                            else{
                                noResultText.setVisibility(View.INVISIBLE);
                            }
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(getApplicationContext(), "관심목록 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                        Log.e("123","통신 onFailure ()");
                    }
                });


            }
        });

        loveListRecyclerview.setAdapter(adapter);
        loveListRecyclerview.setLayoutManager(linearLayoutManager);

        backImage=(ImageView) findViewById(R.id.love_list_back_arrow);



    }
}