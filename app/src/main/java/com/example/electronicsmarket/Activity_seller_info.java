package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_seller_info extends AppCompatActivity {


    private TextView totalReviewNum;
    private TextView sellerReviewMore;
    private ImageView backImage;
    private Adapter_review_info adapterReview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_seller_post_info adapaterProduct;
    private CircleImageView sellerProfileImage;
    private Retrofit retrofit;
    private TextView sellerNickNameText,sellerProductNum,sellerProductInfoText;
    private String sellerNickname,beforePostNum;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView sellerProductRecyclerView,sellerReviewRecyclerView;
    private ArrayList<PostInfo> postSellerProductList;
    private ArrayList<ReviewInfo> reviewList;
    private String cursorPostNum,phasingNum;
    private String id;
    private RatingBar ratingBar;
    private TextView scoreText,noResultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);
        variableInit();
        adapaterProduct=new Adapter_seller_post_info(postSellerProductList,this);
        sellerProductRecyclerView.setLayoutManager(gridLayoutManager);
        sellerProductRecyclerView.setAdapter(adapaterProduct);

        Intent intent =getIntent();
        sellerNickname=intent.getStringExtra("nickname");
        beforePostNum=intent.getStringExtra("postNum");
        sellerNickNameText.setText(sellerNickname);

        RetrofitService service = retrofit.create(RetrofitService.class);

        //리뷰목록가져오기 위한 비동기 통신
        Call<ReviewAllInfo> call2=service.getReviewInfo(cursorPostNum,phasingNum,id,sellerNickname);
        call2.enqueue(new Callback<ReviewAllInfo>() {
            @Override
            public void onResponse(Call<ReviewAllInfo> call, Response<ReviewAllInfo> response) {

                if(response.isSuccessful() && response.body()!=null){

                    ReviewAllInfo reviewAllInfo = response.body();

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



                    totalReviewNum.setText("("+reviewAllInfo.getTotalReviewNum()+")");
                    for(int i=0;i<reviewAllInfo.getReviewInfo().size();i++){
                        try{
                            reviewList.add(reviewAllInfo.getReviewInfo().get(i));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    adapterReview.setReviewList(reviewList);
                    adapterReview.notifyDataSetChanged();

                    if(reviewList.size()==0){
                        noResultText.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewAllInfo> call, Throwable t) {

            }
        });

        //여기서 판매자 닉네임 갖고, 판매자 닉네임으로 retrofit 통신해야함

        Call<PostAllInfo> call = service.getSellerProfile(sellerNickname);

        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                if(response.isSuccessful()&&response.body()!=null){

                    PostAllInfo allInfo=response.body();

                    //프로필이미지 등록
                    if(allInfo.getImageRoute()!=null){
                        if(!allInfo.getImageRoute().equals("")){
                            Glide.with(Activity_seller_info.this).load(allInfo.getImageRoute()).into(sellerProfileImage);
                        }
                    }
                    for(int i=0;i<allInfo.getPostInfo().size();i++){
                        if(!beforePostNum.equals(allInfo.getPostInfo().get(i).getPostNum())){
                            postSellerProductList.add(allInfo.getPostInfo().get(i));
                        }
                        else{
                            allInfo.setProductNum(String.valueOf(Integer.parseInt(allInfo.getProductNum())-1));
                        }

                    }
                    sellerProductInfoText.setText(sellerNickname+"님의 다른 판매 상품");
                    adapaterProduct.setPostList(postSellerProductList);
                    adapaterProduct.notifyDataSetChanged();
                    sellerProductNum.setText(" ("+allInfo.getProductNum()+"개)");
                }
            }

            @Override
            public void onFailure(Call<PostAllInfo> call, Throwable t) {

            }
        });



        //reviewAdapter 터치리스너 달기
        adapterReview.setListener(new Adapter_review_info.Interface_review_item_click() {
            @Override
            public void onReviewProductClick(int position) {

                Intent intent =new Intent(Activity_seller_info.this,Activity_post_read.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                startActivity(intent);
            }

            @Override
            public void onUserInfoClick(int position) {
                Intent intent = new Intent(Activity_seller_info.this,Activity_seller_info.class);
                intent.putExtra("postNum",reviewList.get(position).getPostNum());
                intent.putExtra("nickname",reviewList.get(position).getReviewWriter());
                startActivity(intent);
            }
        });


        //adpater의 터치리스너 달기
        adapaterProduct.setListener(new Adapter_seller_post_info.Interface_seller_item_click() {
            @Override
            public void onItemClick(int position) {
                String postNum=postSellerProductList.get(position).getPostNum();
                Intent intent =new Intent(Activity_seller_info.this,Activity_post_read.class);
                intent.putExtra("postNum",postNum);
                startActivity(intent);
            }
        });

        //
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sellerReviewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviewList.size()==0){
                    Toast.makeText(getApplicationContext(), "받은 거래후기가 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent =new Intent(Activity_seller_info.this,Activity_writer_review_collect.class);
                intent.putExtra("email",id);
                intent.putExtra("nickname",sellerNickname);
                startActivity(intent);
            }
        });


    }

    public void variableInit(){

        noResultText=(TextView) findViewById(R.id.post_seller_review_no_result);
        scoreText=(TextView)findViewById(R.id.post_seller_score_text);
        ratingBar=(RatingBar)findViewById(R.id.post_seller_rating_bar);
        sellerReviewMore=(TextView) findViewById(R.id.post_seller_review_more);

        cursorPostNum="0";
        phasingNum="2";

        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");

        backImage=findViewById(R.id.post_seller_info_back_arrow);
        totalReviewNum=findViewById(R.id.post_seller_review_num);
        //리뷰목록 리사이클러뷰
        reviewList=new ArrayList<>();
        adapterReview=new Adapter_review_info(reviewList,Activity_seller_info.this);
        linearLayoutManager=new LinearLayoutManager(this);
        sellerReviewRecyclerView=findViewById(R.id.post_seller_review_recyclerview);

        sellerReviewRecyclerView.setLayoutManager(linearLayoutManager);
        sellerReviewRecyclerView.setAdapter(adapterReview);
        
        //판매목록 리사이클러뷰
        postSellerProductList =new ArrayList<>();
        gridLayoutManager=new GridLayoutManager(Activity_seller_info.this,2);
        sellerProductRecyclerView=findViewById(R.id.post_seller_product_recyclerview);
        sellerProductInfoText=findViewById(R.id.post_seller_product_info_text);
        sellerProductNum=findViewById(R.id.post_seller_product_num);
       
        sellerNickNameText=findViewById(R.id.post_seller_nickname);
        sellerProfileImage =findViewById(R.id.post_seller_profile_image);

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}