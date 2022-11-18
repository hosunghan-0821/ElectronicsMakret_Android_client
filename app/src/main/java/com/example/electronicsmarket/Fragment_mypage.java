package com.example.electronicsmarket;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
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
    private TextView reviewNumText;
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
    private RatingBar ratingBar;
    private float ratingScore;
    private TextView scoreText,noResultText;
    private boolean onCreateViewIsSet=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_mypage,container,false);
        variableInit(view);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ReviewAllInfo> call = service.getReviewInfo(cursorPostNum,phasingNum,id,null);

        //review 정보 가져와서
            call.enqueue(new Callback<ReviewAllInfo>() {
                @Override
                public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        ReviewAllInfo reviewAllInfo= response.body();
                        if(!reviewAllInfo.getTotalReviewNum().equals("0")){
                            try{
                                float score = Float.parseFloat(reviewAllInfo.getTotalReviewScore())/Float.parseFloat(reviewAllInfo.getTotalReviewNum());
                                ratingBar.setRating(score);
                                DecimalFormat form = new DecimalFormat("#.#");
                                scoreText.setText(form.format(score)+"/5");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        else{
                            scoreText.setText("0/5");
                        }

                        reviewNumText.setText("거래후기("+reviewAllInfo.getTotalReviewNum()+")");
                        for(int i=0;i<reviewAllInfo.getReviewInfo().size();i++){
                            try{
                                reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        adapter.setReviewList(reviewList);
                        adapter.notifyDataSetChanged();

                        if(reviewList.size()==0){
                            noResultText.setVisibility(View.VISIBLE);
                        }
                        onCreateViewIsSet=true;
                    }
                }

                @Override
                public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

                }
            });


        reviewMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(reviewList.size()==0){
                    Toast.makeText(getActivity(), "받은 거래후기가 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

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

        adapter.setListener(new Adapter_review_info.Interface_review_item_click() {
            @Override
            public void onReviewProductClick(int position) {
                Intent intent =new Intent(getActivity(),Activity_post_read.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                startActivity(intent);
            }

            @Override
            public void onUserInfoClick(int position) {
                Intent intent = new Intent(getActivity(),Activity_seller_info.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                intent.putExtra("nickname",reviewList.get(position).getReviewWriter());
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
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        // shared 값 가져오기
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");


        noResultText=(TextView) view.findViewById(R.id.mypage_no_result_text);
        scoreText=(TextView) view.findViewById(R.id.mypage_score_text);
        ratingBar=(RatingBar) view.findViewById(R.id.mypage_rating_bar);
        frameSellList=(FrameLayout)view.findViewById(R.id.mypage_frame_selllist);
        frameLoveList=(FrameLayout)view.findViewById(R.id.mypage_frame_lovelist);
        frameBuyList=(FrameLayout)view.findViewById(R.id.mypage_frame_buylist);

        reviewMoreText=(TextView) view.findViewById(R.id.mypage_review_more_text);

        nickname=view.findViewById(R.id.user_nickname);
        settingImage=view.findViewById(R.id.setting_image);
        circleImageView=view.findViewById(R.id.circleImageView);
        profileUpdate=view.findViewById(R.id.profile_update_button);
        reviewNumText=view.findViewById(R.id.mypage_review_num);
    }

    public void setProfile(Context context){
        sharedPreferences= context.getSharedPreferences("autoLogin", MODE_PRIVATE);
        String id=sharedPreferences.getString("userId","");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataMemberSignup> call = service.getProfile("getProfile.php",id);
        call.enqueue(new Callback<DataMemberSignup>() {
            @Override
            public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {


                if(response.isSuccessful()&&response.body()!=null){

                    Log.e("123","응답옴");
                  DataMemberSignup memberInfo=response.body();
                  nickname.setText(memberInfo.getNickname());



                  //변한 닉네임 입력 shared 값 저장하기
                  sharedPreferences= getContext().getSharedPreferences("autoLogin",MODE_PRIVATE);
                  SharedPreferences.Editor editor = sharedPreferences.edit();
                  editor.putString("nickName",memberInfo.getNickname());
                  editor.commit();

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
            public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                Toast.makeText(getContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("123","onresume");
        setProfile(getContext());

        if(onCreateViewIsSet){
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<ReviewAllInfo> call = service.getReviewInfo(cursorPostNum,phasingNum,id,null);
            call.enqueue(new Callback<ReviewAllInfo>() {
                @Override
                public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {

                    if(response.isSuccessful()&&response.body()!=null){
                        reviewList.clear();
                        ReviewAllInfo reviewAllInfo= response.body();
                        if(!reviewAllInfo.getTotalReviewNum().equals("0")){
                            try{
                                float score = Float.parseFloat(reviewAllInfo.getTotalReviewScore())/Float.parseFloat(reviewAllInfo.getTotalReviewNum());
                                ratingBar.setRating(score);
                                DecimalFormat form = new DecimalFormat("#.#");
                                scoreText.setText(form.format(score)+"/5");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        else{
                            scoreText.setText("0/5");
                        }

                        reviewNumText.setText("거래후기("+reviewAllInfo.getTotalReviewNum()+")");
                        for(int i=0;i<reviewAllInfo.getReviewInfo().size();i++){
                            try{
                                reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        adapter.setReviewList(reviewList);
                        adapter.notifyDataSetChanged();

                        if(reviewList.size()==0){
                            noResultText.setVisibility(View.VISIBLE);
                        }
                        else{
                            noResultText.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

                }
            });
        }

    }
}