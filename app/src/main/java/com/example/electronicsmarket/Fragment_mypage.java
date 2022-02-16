package com.example.electronicsmarket;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_mypage extends Fragment  {

    private Adapter_review_info adapter;
    private ArrayList<ReviewInfo> reviewList;
    private RecyclerView reviewRecyclerview;
    private LinearLayoutManager linearLayoutManager;

    private SharedPreferences sharedPreferences;
    private Button profileUpdate;
    private Retrofit retrofit;
    private TextView nickname;
    private ImageView settingImage;
    private de.hdodenhof.circleimageview.CircleImageView circleImageView;
    private FrameLayout frameLoveList,frameSellList,frameBuyList;
    private TextView reviewMoreText;
    private String id;
    private String cursorPostNum,phasingNum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_mypage,container,false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ReviewAllInfo> call = service.getReviewInfo(cursorPostNum,phasingNum,id);
            call.enqueue(new Callback<ReviewAllInfo>() {
                @Override
                public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        ReviewAllInfo reviewAllInfo= response.body();

                        for(int i=0;i<reviewAllInfo.getReviewInfo().size();i++){
                            try{
                                reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        adapter.setReviewList(reviewList);
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

                }
            });


        reviewMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Activity_writer_review_collect.class);
                intent.putExtra("email",id);
                startActivity(intent);

            }
        });


        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_setting.class);
                startActivity(intent);
            }
        });

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Activity_profile_update.class);
                startActivity(intent);
            }
        });

        frameSellList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_sell_list.class);
                startActivity(intent);
            }
        });

        frameLoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_love_list.class);
                startActivity(intent);
            }
        });

        frameBuyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_buy_list.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void variableInit(View view){

        //거래관련
        cursorPostNum="0";
        phasingNum="3";

        //Recyclerview 관련
        reviewList=new ArrayList<>();
        reviewRecyclerview=view.findViewById(R.id.mypage_review_recyclerview);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        adapter=new Adapter_review_info(reviewList,getActivity());

        reviewRecyclerview.setLayoutManager(linearLayoutManager);
        reviewRecyclerview.setAdapter(adapter);

        //retrofit 관련
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        // shared 값 가져오기
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");


        frameSellList=(FrameLayout)view.findViewById(R.id.mypage_frame_selllist);
        frameLoveList=(FrameLayout)view.findViewById(R.id.mypage_frame_lovelist);
        frameBuyList=(FrameLayout)view.findViewById(R.id.mypage_frame_buylist);

        reviewMoreText=(TextView) view.findViewById(R.id.mypage_review_more_text);

        nickname=view.findViewById(R.id.user_nickname);
        settingImage=view.findViewById(R.id.setting_image);
        circleImageView=view.findViewById(R.id.circleImageView);
        profileUpdate=view.findViewById(R.id.profile_update_button);
    }

    public void setProfile(Context context){
        sharedPreferences= context.getSharedPreferences("autoLogin", MODE_PRIVATE);
        String id=sharedPreferences.getString("userId","");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<MemberSignup> call = service.getProfile("getProfile.php",id);
        call.enqueue(new Callback<MemberSignup>() {
            @Override
            public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {


                if(response.isSuccessful()&&response.body()!=null){

                    Log.e("123","응답옴");
                  MemberSignup memberInfo=response.body();
                  nickname.setText(memberInfo.getNickname());
                  String imageRoute= memberInfo.imageRoute;
                  if(imageRoute.equals("")){
                     circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                  }
                  else{
                        Glide.with(context).load(imageRoute).into(circleImageView);
                  }

                }
            }
            @Override
            public void onFailure(Call<MemberSignup> call, Throwable t) {
                Toast.makeText(getContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        Log.e("123","onresume");
        setProfile(getContext());
        super.onResume();

    }
}