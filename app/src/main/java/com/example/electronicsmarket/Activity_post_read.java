package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_post_read extends AppCompatActivity {

    private Retrofit retrofit;
   private ViewPager2 sliderViewPager;
   private LinearLayout layoutIndicator;
   private String[] images = new String[]{
            "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/Resource/postImage/postNum_21_20220120_061627_2.jpg",
            "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/Resource/postImage/postNum_21_20220120_061627_1.jpg",
            "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/Resource/postImage/postNum_20_20220120_061552_0.jpg",
            "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg",
            "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg"
    };

    private CircleImageView circleImageView;
    private TextView postReadTitle,postReadPrice,postReadDelivery,postReadSellType1,postReadSellType2,postReadCategory,postReadContents;
    private TextView postReadNickname,postReadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_read);


        variableInit();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo("18");

        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                Log.e("123","통신성공");
            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {
                Log.e("123","통신실패");
           }
        });

        sliderViewPager= findViewById(R.id.viewpager_post_image);
        layoutIndicator= findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);

        sliderViewPager.setAdapter(new Adapter_image_viewpager(this,images));
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        setupIndicators(images.length);

    }

    private void setupIndicators(int count){
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 8, 16, 8);

        for(int i=0;i<indicators.length;i++){
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_add_24));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position){
        int childCount=layoutIndicator.getChildCount();
        for(int i=0;i<childCount;i++){
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if(i==position){
                imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_camera_alt_24));
            }
            else{
                imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_add_24));
            }
        }
    }

    public void variableInit(){

        //postReadTitle,postReadPrice,postReadDelivery,postReadSellType1,PostReadSellType2,postReadCategory,postReadContents;
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        postReadTitle=findViewById(R.id.post_read_title);
        postReadPrice=findViewById(R.id.post_read_price);
        postReadDelivery=findViewById(R.id.post_read_deliver_cost);
        postReadSellType1=findViewById(R.id.post_read_sell_type_1);
        postReadSellType2=findViewById(R.id.post_read_sell_type_2);
        postReadCategory=findViewById(R.id.post_read_category);
        postReadContents=findViewById(R.id.content);
        postReadNickname=findViewById(R.id.post_write_nickname);
        postReadId=findViewById(R.id.post_write_email);

        circleImageView=findViewById(R.id.post_write_profile_image);

    }
}