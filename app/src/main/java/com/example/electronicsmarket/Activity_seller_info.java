package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_seller_info extends AppCompatActivity {


    private ImageView backImage;
    private Adapter_seller_review_info adapterReview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_seller_post_info adapaterProduct;
    private CircleImageView sellerProfileImage;
    private Retrofit retrofit;
    private TextView sellerNickNameText,sellerProductNum,sellerProductInfoText;
    private String sellerNickname,beforePostNum;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView sellerProductRecyclerView,sellerReviewRecyclerView;
    private ArrayList<PostInfo> postSellerProductList;

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
        Call<PostAllInfo> call = service.getSellerProfile(sellerNickname);

        //여기서 판매자 닉네임 갖고, 판매자 닉네임으로 retrofit 통신해야함
        call.enqueue(new Callback<PostAllInfo>() {
            @Override
            public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {
                if(response.isSuccessful()&&response.body()!=null){

                    PostAllInfo allInfo=response.body();
//                    System.out.println(allInfo.getProductNum());
//                    for(int i=0;i<allInfo.getPostInfo().size();i++){
//                        System.out.println("판매제목 : "+ allInfo.getPostInfo().get(i).getPostTitle());
//                        System.out.println("판매가격 : "+ allInfo.getPostInfo().get(i).getPostPrice());
//                        System.out.println("post num : "+ allInfo.getPostInfo().get(i).getPostNum());
//                    }
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


    }

    public void variableInit(){

//        private GridLayoutManager gridLayoutManager;
//        private RecyclerView recyclerView;
//        private ArrayList<PostInfo> postInfoList;


        backImage=findViewById(R.id.post_seller_info_back_arrow);

        adapterReview=new Adapter_seller_review_info();

        postSellerProductList =new ArrayList<>();
        gridLayoutManager=new GridLayoutManager(Activity_seller_info.this,2);

        linearLayoutManager=new LinearLayoutManager(this);
        
        //리뷰목록 리사이클러뷰
        sellerReviewRecyclerView=findViewById(R.id.post_seller_review_recyclerview);
        sellerReviewRecyclerView.setLayoutManager(linearLayoutManager);
        sellerReviewRecyclerView.setAdapter(adapterReview);
        
        //판매목록 리사이클러뷰
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