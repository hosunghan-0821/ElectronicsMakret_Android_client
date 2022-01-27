package com.example.electronicsmarket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_post_read extends AppCompatActivity implements Dialog_bottom_sheet.BottomSheetListener {

    private Retrofit retrofit;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private String sellerId;
    private FrameLayout postReadSellerFrame;
    private ArrayList<String> imageRoute;
    private ImageView updateDeleteImage, backImage;
    private Adapter_image_viewpager adapter;
    private CircleImageView circleImageView;
    private TextView postReadProductNum,postReadTitle, postReadPrice, postReadDelivery, postReadSellType1, postReadSellType2, postReadCategory, postReadContents, postReadLocationInfo;
    private TextView postReadNickname, postReadId, postReadTime, postReadLike, postReadView,postReadStatusText;
    private String postNum, postLocationAddress, postLocationName;
    private Double postReadLongitude, postReadLatitude;
    private LinearLayout postReadLinearStatus;


    @Override
    public void onButtonClicked(String text) {
        postReadStatusText.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_read);


        imageRoute = new ArrayList<String>();
        adapter = new Adapter_image_viewpager(getApplicationContext(), imageRoute);
        variableInit();

        //게시글 읽기위해 intent로 postNum 받기
        Intent intent = getIntent();
        postNum = intent.getStringExtra("postNum");
        if (postNum == null) {
            postNum = "22";
        }

        //바텀 sheet dialog를 써보자
        postReadLinearStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_bottom_sheet bottomSheet = new Dialog_bottom_sheet();
                bottomSheet.show(getSupportFragmentManager(),"Dialog_bottom_sheet");
            }
        });




        //retrofit 통신
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum);
        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                sellerId=response.body().getMemberId();
                // shared 값 가져오기
                SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                String id=sharedPreferences.getString("userId","");
                if(sellerId!=null){
                    if(!sellerId.equals(id)){
                        postReadLinearStatus.setVisibility(View.GONE);
                    }
                }



                //이미지 처리
                PostInfo info = response.body();
                adapter.setImageRoute(info.getImageRoute());
                adapter.notifyDataSetChanged();
                setupIndicators(info.getImageRoute().size());
                if (info.getMemberImage() == null) {
                    circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                } else {
                    Glide.with(getApplicationContext()).load(info.getMemberImage().toString()).into(circleImageView);
                }


                //시간 관련 로직 작성해야지
                timeDifferentCheck(info.getPostRegTime());
                //위치정보 세팅
                if (info.getPostLocationAddress() != null) {
                    if (info.getPostLocationAddress().equals("")) {
                        postReadLocationInfo.setText(" " + info.getPostLocationName());
                    } else {
                        postReadLocationInfo.setText(" " + info.getPostLocationName() + " ( " + info.getPostLocationAddress() + " )");
                    }
                }

                postReadLatitude = info.getPostLocationLatitude();
                postReadLongitude = info.getPostLocationLongitude();
                postLocationName = info.getPostLocationName();
                postLocationAddress = info.getPostLocationAddress();

                //
                Log.e("123",info.getProductNum());
                postReadProductNum.setText(info.getProductNum());
                postReadView.setText(info.getPostViewNum());
                postReadLike.setText(info.getPostLikeNum());

                postReadContents.setText(info.getPostContents());
                System.out.println("postcontents" + info.getPostContents());


                postReadTitle.setText(info.getPostTitle());
                postReadPrice.setText(info.getPostPrice() + "원");
                if (info.getPostCategory().equals("카테고리 선택")) {
                    postReadCategory.setVisibility(View.INVISIBLE);
                }
                postReadCategory.setText("  *  " + info.getPostCategory());
                postReadNickname.setText(info.getNickname());

                if (info.getPostSellType().equals("직거래/택배거래")) {

                } else if (info.getPostSellType().equals("직거래")) {
                    postReadDelivery.setVisibility(View.GONE);
                    postReadSellType2.setVisibility(View.GONE);
                } else {
                    postReadSellType1.setVisibility(View.GONE);
                }

                if (info.getPostDelivery().equals("Y")) {
                    postReadDelivery.setText("배송비 포함");
                } else {
                    postReadDelivery.setText("배송비 별도");
                }

                Log.e("123", "통신성공");

            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {
                Log.e("123", "통신실패");
            }
        });


        sliderViewPager = findViewById(R.id.viewpager_post_image);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);

        sliderViewPager.setAdapter(adapter);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


        //판매자 frame 클릭하면 판매자 정보로 이동하기
        postReadSellerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123","판매자 프레임 선택");

                Intent intent = new Intent(Activity_post_read.this,Activity_seller_info.class);
                intent.putExtra("nickname",postReadNickname.getText().toString());
                intent.putExtra("postNum",postNum);
                startActivity(intent);
            }
        });
        //뒤로가기 버튼
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_post_read.this, Activity_main_home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        //간단하게 구글 api로 그 위치 보여주면 되겠다.
        postReadLocationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("123",postReadLocationInfo.getText().toString());
                Intent intent = new Intent(Activity_post_read.this, Activity_location_map.class);
                if (postReadLocationInfo.getText().toString().equals(" 장소정보 없음")) {
                    return;
                } else {
                    intent.putExtra("longitude", postReadLongitude);
                    intent.putExtra("latitude", postReadLatitude);
                    intent.putExtra("locationName", postLocationName);
                    intent.putExtra("locationAddress", postLocationAddress);
                    startActivity(intent);
                }
            }
        });
        // 수정삭제  상단 우측 액션바 작성자한테만 보이게끔 해야함!!
        updateDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // shared 값 가져오기
                SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                String id=sharedPreferences.getString("userId","");
                PopupMenu popup = new PopupMenu(Activity_post_read.this, updateDeleteImage);
                if(sellerId.equals(id)){
                    MenuInflater inflate = popup.getMenuInflater();
                    inflate.inflate(R.menu.post_update_menu, popup.getMenu());
                    popup.show();
                }
                else{
                    MenuInflater inflate = popup.getMenuInflater();
                    inflate.inflate(R.menu.post_report_menu, popup.getMenu());
                    popup.show();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.post_write_update) {
                            Intent intent = new Intent(Activity_post_read.this, Activity_post_write.class);
                            intent.putExtra("update", true);
                            intent.putExtra("postNum", postNum);
                            startActivity(intent);
                        } else if (id == R.id.post_write_delete) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_read.this);
                            builder.setTitle("Used Electronics market 알림");
                            builder.setMessage("게시글을 정말 삭제하시겠습니까?");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("123","확인버튼");
                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<MemberSignup> call = service.sendDeletePostInfo(postNum);
                                    call.enqueue(new Callback<MemberSignup>() {
                                        @Override
                                        public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                                            Log.e("123","통신성공");
                                            if(response.isSuccessful()&&response.body()!=null){
                                                if(response.body().isSuccess()){
                                                    Toast.makeText(Activity_post_read.this, "게시글 삭제완료", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Activity_post_read.this,Activity_main_home.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(Activity_post_read.this, "게시글 삭제실패", Toast.LENGTH_SHORT).show();
                                                }
                                       
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MemberSignup> call, Throwable t) {

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
                        else if(id==R.id.post_write_report){

                        }
                        return false;
                    }
                });
            }
        });
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_brightness_1_24));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_camera_alt_24));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_brightness_1_24));
            }
        }
    }

    public void variableInit() {

        //postReadTitle,postReadPrice,postReadDelivery,postReadSellType1,PostReadSellType2,postReadCategory,postReadContents;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        backImage = findViewById(R.id.post_write_category_1_back_arrow);
        updateDeleteImage = findViewById(R.id.post_write_update_delete);
        postReadStatusText=findViewById(R.id.post_read_post_status);
        postReadLinearStatus =findViewById(R.id.post_read_linear_status);
        postReadProductNum=findViewById(R.id. post_read_item_num);
        postReadTime = findViewById(R.id.post_read_time_check);
        postReadLike = findViewById(R.id.post_read_like_num);
        postReadView = findViewById(R.id.post_read_view_num);


        postReadSellerFrame=findViewById(R.id.post_read_seller_frame);
        postReadLocationInfo = findViewById(R.id.post_read_location_info);
        postReadTitle = findViewById(R.id.post_read_title);
        postReadPrice = findViewById(R.id.post_read_price);
        postReadDelivery = findViewById(R.id.post_read_deliver_cost);
        postReadSellType1 = findViewById(R.id.post_read_sell_type_1);
        postReadSellType2 = findViewById(R.id.post_read_sell_type_2);
        postReadCategory = findViewById(R.id.post_read_category);
        postReadContents = findViewById(R.id.post_read_contents);
        postReadNickname = findViewById(R.id.post_write_nickname);


        circleImageView = findViewById(R.id.post_write_profile_image);

    }

    public void timeDifferentCheck(String uploadTime) {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        //달->일 -> 시간 -> 분 -> 초 로 차이나는지 확인해서
        String nowTime = formatter.format(date);

        String date1 = nowTime; //날짜1
        String date2 = uploadTime; //날짜2

        try {
            Date format1 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date1);
            Date format2 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //시 차이
            long diffDays = diffSec / (24 * 60 * 60); //일자수 차이

            System.out.println(diffSec + "초 차이");
            System.out.println(diffMin + "분 차이");
            System.out.println(diffHor + "시 차이");
            System.out.println(diffDays + "일 차이");

            if (diffSec < 0) {
                postReadTime.setText("1초전");
            } else if (diffDays != 0) {
                postReadTime.setText(String.valueOf(diffDays) + "일 전");
            } else if (diffHor != 0) {
                postReadTime.setText(String.valueOf(diffHor) + "시간 전");
            } else if (diffMin != 0) {
                postReadTime.setText(String.valueOf(diffMin) + "분 전");
            } else if (diffSec != 0) {
                postReadTime.setText(String.valueOf(diffSec) + "초 전");
            } else {
                postReadTime.setText("1초전");
            }

        } catch (Exception e) {

        }


//        //String uploadTime=
//        int uploadTimeInt,nowTimeInt;
//
//        uploadTimeInt=Integer.parseInt(uploadTime.substring(4,6));
//        nowTimeInt=Integer.parseInt(nowTime.substring(4,6));
//        if(nowTimeInt-uploadTimeInt!=0){
//            Log.e("123","현재시간"+nowTime);
//            Log.e("123","업로드시간"+uploadTime);
//            postReadTime.setText(String.valueOf(nowTimeInt-uploadTimeInt)+"달 전");
//            return;
//        }
//        uploadTimeInt=Integer.parseInt(uploadTime.substring(6,8));
//        nowTimeInt=Integer.parseInt(nowTime.substring(6,8));
//        if(nowTimeInt-uploadTimeInt!=0){
//            Log.e("123","현재시간"+nowTime);
//            Log.e("123","업로드시간"+uploadTime);
//            postReadTime.setText(String.valueOf(nowTimeInt-uploadTimeInt)+"일 전");
//            return;
//        }
//        uploadTimeInt=Integer.parseInt(uploadTime.substring(9,11));
//        nowTimeInt=Integer.parseInt(nowTime.substring(9,11));
//        if(nowTimeInt-uploadTimeInt!=0){
//            Log.e("123","현재시간"+nowTime);
//            Log.e("123","업로드시간"+uploadTime);
//            postReadTime.setText(String.valueOf(nowTimeInt-uploadTimeInt)+"시간 전");
//            return;
//        }
//        uploadTimeInt=Integer.parseInt(uploadTime.substring(11,13));
//        nowTimeInt=Integer.parseInt(nowTime.substring(11,13));
//        if(nowTimeInt-uploadTimeInt!=0){
//            Log.e("123","현재시간"+nowTime);
//            Log.e("123","업로드시간"+uploadTime);
//            postReadTime.setText(String.valueOf(nowTimeInt-uploadTimeInt)+"분 전");
//            return;
//        }
//        uploadTimeInt=Integer.parseInt(uploadTime.substring(13,15));
//        nowTimeInt=Integer.parseInt(nowTime.substring(13,15));
//        if(nowTimeInt-uploadTimeInt!=0){
//            Log.e("123","현재시간"+nowTime);
//            Log.e("123","업로드시간"+uploadTime);
//            postReadTime.setText(String.valueOf(nowTimeInt-uploadTimeInt)+"초 전");
//            return;
//        }
//        else{
//            postReadTime.setText("0초 전");
//        }

//
//        Log.e("123","업로드시간"+String.valueOf(uploadTimeInt));
//        Log.e("123","현재시간"+String.valueOf(nowTimeInt));
//
//        Log.e("123","시간차이"+String.valueOf(nowTimeInt-uploadTimeInt));

    }
}