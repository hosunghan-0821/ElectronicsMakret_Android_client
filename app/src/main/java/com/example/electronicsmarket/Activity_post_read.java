package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

public class Activity_post_read extends AppCompatActivity {

    private Retrofit retrofit;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private ArrayList<String> imageRoute;
    private ImageView updateDeleteImage;
    private Adapter_image_viewpager adapter;
    private CircleImageView circleImageView;
    private TextView postReadTitle,postReadPrice,postReadDelivery,postReadSellType1,postReadSellType2,postReadCategory,postReadContents;
    private TextView postReadNickname,postReadId;
    private String postNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_read);


        imageRoute=new ArrayList<String>();
        adapter=new Adapter_image_viewpager(getApplicationContext(),imageRoute);
        variableInit();
        Intent intent = getIntent();

        postNum=intent.getStringExtra("postNum");
        if(postNum==null){
            postNum="22";
        }

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum);

        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                //이미지 처리
                PostInfo info = response.body();
                adapter.setImageRoute(info.getImageRoute());
                adapter.notifyDataSetChanged();
                setupIndicators(info.getImageRoute().size());
                if(info.getMemberImage()==null){
                    circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                }
                else{
                    Glide.with(getApplicationContext()).load(info.getMemberImage().toString()).into(circleImageView);
                }


                //로드
                postReadContents.setText(info.getPostContents().toString());
                postReadTitle.setText(info.getPostTitle());
                postReadPrice.setText(info.getPostPrice()+"원");
                postReadCategory.setText("  *  "+info.getPostCategory());
                postReadNickname.setText(info.getNickname());
                if(info.getPostDelivery().equals("Y")){
                    postReadDelivery.setText("배송비 포함");
                }
                else{
                    postReadDelivery.setText("배송비 별도");
                    postReadDelivery.setTextColor(Color.RED);
                }
                if(info.getPostSellType().equals("직거래/택배거래")){

                }else if(info.getPostSellType().equals("직거래")){
                    postReadSellType2.setVisibility(View.GONE);
                }else{
                    postReadSellType1.setVisibility(View.GONE);
                }

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

        sliderViewPager.setAdapter(adapter);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


        updateDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Activity_post_read.this, updateDeleteImage);
                MenuInflater inflate = popup.getMenuInflater();
                inflate.inflate(R.menu.post_update_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.post_write_update) {
                            Intent intent =new Intent(Activity_post_read.this,Activity_post_write.class);
                            intent.putExtra("update",true);
                            intent.putExtra("postNum",postNum);
                            startActivity(intent);
                        } else if (id == R.id.post_write_delete) {
                            //여기선 삭제..

                        }
                        return false;
                    }
                });

            }
        });



    }

    private void setupIndicators(int count){
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 8, 16, 8);

        for(int i=0;i<indicators.length;i++){
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_brightness_1_24));
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
                imageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_brightness_1_24));
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

        updateDeleteImage=findViewById(R.id.post_write_update_delete);

        postReadTitle=findViewById(R.id.post_read_title);
        postReadPrice=findViewById(R.id.post_read_price);
        postReadDelivery=findViewById(R.id.post_read_deliver_cost);
        postReadSellType1=findViewById(R.id.post_read_sell_type_1);
        postReadSellType2=findViewById(R.id.post_read_sell_type_2);
        postReadCategory=findViewById(R.id.post_read_category);
        postReadContents=findViewById(R.id.post_read_contents);
        postReadNickname=findViewById(R.id.post_write_nickname);


        circleImageView=findViewById(R.id.post_write_profile_image);

    }
}