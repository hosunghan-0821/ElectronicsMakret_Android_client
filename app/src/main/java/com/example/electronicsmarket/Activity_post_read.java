package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

    private String nickName;
    private TextView postReadBuyProductDelivery,postReadChat;
    private Retrofit retrofit;
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator, postReadLinearBottomOption;
    private String sellerId;
    private FrameLayout postReadSellerFrame;
    private ArrayList<String> imageRoute;
    private ImageView updateDeleteImage, backImage, locationDetailImage, postReadLikeNumImage, postReadLikeImage;
    private Adapter_image_viewpager adapter;
    private CircleImageView circleImageView;
    private TextView postReadProductNum, postReadTitle, postReadPrice, postReadDelivery, postReadSellType1, postReadSellType2, postReadCategory, postReadContents, postReadLocationInfo;
    private TextView postReadNickname, postReadId, postReadTime, postReadLike, postReadView, postReadStatusText, postReadPlaceDetail, postReadMoveLoveList;
    private String postNum, postLocationAddress, postLocationName;
    private Double postReadLongitude, postReadLatitude;
    private LinearLayout postReadLinearStatus, postReadLinearLoveList;
    private boolean like = true, isRunning = false;
    private SharedPreferences sharedPreferences;
    private String id;
    private Thread thread;
    private Handler handler;
    private TextView postReadStatus;
    private boolean viewPlusCheck=false;

    @Override
    public void onButtonClicked(String text) {
        if(text.equals("????????????")){
          Intent intent =new Intent(Activity_post_read.this,Activity_buyer_choice.class);
          intent.putExtra("postNum",postNum);
          startActivity(intent);
        }
        //postReadStatusText.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_read);
        variableInit();

        imageRoute = new ArrayList<String>();
        adapter = new Adapter_image_viewpager(Activity_post_read.this, imageRoute);

        //????????? ???????????? intent??? postNum ??????
        Intent intent = getIntent();
        postNum = intent.getStringExtra("postNum");
        if (postNum == null) {
            postNum = "22";
        }
        //onresume ??? ?????? ??????????????? ?????????????????????, ??? ??? ,????????? ???????????? ?????? ????????????. ???????????? ???????????????
        //oncreate ??? ?????? ????????? ??? ?????????, ?????? ??????????????????

        Log.e("123","?????? ?????? ???");
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call2 = service.viewPlus(postNum);
        call2.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    Log.e("456","????????????");
                    Log.e("123","?????? ??????");
//                    Log.e("123","getviewnum"+response.body().getPostViewNum());
                     postReadView.setText(response.body().getPostViewNum());

                }
            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {

            }
        });

        //???????????? ????????? ?????? ?????? intent.
        postReadChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Activity_post_read.this,Activity_trade_chat.class);
                intent.putExtra("seller",postReadNickname.getText().toString());
                intent.putExtra("postNum",postNum);
                intent.putExtra("buyer",nickName);
                startActivity(intent);
            }
        });

        //???????????? ?????? ????????? ?????? ?????? intent.
        postReadBuyProductDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postReadSellType2.getVisibility() == View.GONE) {
                    Toast.makeText(getApplicationContext(), "?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Activity_post_read.this, Activity_buy_product_delivery.class);
                intent.putExtra("postNum", postNum);
                startActivity(intent);
            }
        });


        //???????????? ?????? thread??? ?????? ui ?????? ??????
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 1) {
                    isRunning = true;
                    postReadLinearLoveList.setVisibility(View.VISIBLE);
                } else {
                    postReadLinearLoveList.setVisibility(View.GONE);
                    isRunning = false;
                }
            }
        };

        //??????????????? ???????????? text
        postReadMoveLoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_post_read.this, Activity_love_list.class);
                startActivity(intent);
            }
        });


        //????????? ?????? ???????????????.
        postReadLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like) {
                    //????????? ???????????? ????????? ??? , ????????? ??????
                    setLikeList("insert");
                } else {
                    setLikeList("delete");
                    //????????? ?????? ?????? ????????? ??? ,????????? ??????.
                }

            }
        });


        //?????? sheet dialog??? ?????????
        postReadLinearStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_bottom_sheet bottomSheet = new Dialog_bottom_sheet();
                bottomSheet.show(getSupportFragmentManager(), "Dialog_bottom_sheet");
            }
        });

//        //onCreate ??? ???,
//        //retrofit ??????
//        RetrofitService service = retrofit.create(RetrofitService.class);
//        Call<PostInfo> call = service.getPostInfo(postNum, "read", id);
//        Log.e("456", id);
//        call.enqueue(new Callback<PostInfo>() {
//            @Override
//            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
//                sellerId = response.body().getMemberId();
//
//                //System.out.println("isClientLike"+response.body().isClientIsLike());
//                // shared ??? ????????????
//                Log.e("123", "??????" + response.body().getPostLocationDetail());
//
//                // client ??? ????????? ????????? ????????? ??????, ????????? ?????????????????? like boolean ?????? ??????;
//                if (response.body().isClientIsLike()) {
//                    postReadLikeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
//                    postReadLikeNumImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
//                    like = false;
//                }
//
//                // ???????????? ?????? ??????
//                if (sellerId != null) {
//                    if (!sellerId.equals(id)) {
//                        postReadLinearStatus.setVisibility(View.GONE);
//                    } else {
//                        postReadLinearBottomOption.setVisibility(View.GONE);
//                    }
//                }
//                //????????? ??????
//                PostInfo info = response.body();
//                adapter.setImageRoute(info.getImageRoute());
//                adapter.notifyDataSetChanged();
//                setupIndicators(info.getImageRoute().size());
//                if (info.getMemberImage() == null) {
//                    circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
//                } else {
//                    Glide.with(getApplicationContext()).load(info.getMemberImage().toString()).into(circleImageView);
//                }
//
//
//                //?????? ?????? ?????? ???????????????
//                timeDifferentCheck(info.getPostRegTime());
//                //???????????? ??????
//                if (info.getPostLocationAddress() != null) {
//                    if (info.getPostLocationAddress().equals("")) {
//                        postReadLocationInfo.setText(" " + info.getPostLocationName());
//
//                    } else {
//                        postReadLocationInfo.setText(" " + info.getPostLocationName() + " ( " + info.getPostLocationAddress() + " )");
//                        if (info.getPostLocationDetail() != null) {
//                            if (!info.getPostLocationDetail().equals("")) {
//                                locationDetailImage.setVisibility(View.VISIBLE);
//                                postReadPlaceDetail.setText("???????????? ?????? : " + info.getPostLocationDetail());
//                            }
//                        }
//
//                    }
//                }
//
//                postReadLatitude = info.getPostLocationLatitude();
//                postReadLongitude = info.getPostLocationLongitude();
//                postLocationName = info.getPostLocationName();
//                postLocationAddress = info.getPostLocationAddress();
//
//                //
//                Log.e("123", info.getProductNum());
//                postReadProductNum.setText(info.getProductNum());
//                postReadView.setText(info.getPostViewNum());
//                postReadLike.setText(info.getPostLikeNum());
//
//                postReadContents.setText(info.getPostContents());
//                System.out.println("postcontents" + info.getPostContents());
//
//
//                postReadTitle.setText(info.getPostTitle());
//                postReadPrice.setText(info.getPostPrice() + "???");
//                if (info.getPostCategory().equals("???????????? ??????")) {
//                    postReadCategory.setVisibility(View.INVISIBLE);
//                }
//                postReadCategory.setText("  *  " + info.getPostCategory());
//                postReadNickname.setText(info.getNickname());
//
//                if (info.getPostSellType().equals("?????????/????????????")) {
//
//
//                } else if (info.getPostSellType().equals("?????????")) {
//                    postReadDelivery.setVisibility(View.GONE);
//                    postReadSellType2.setVisibility(View.GONE);
//                } else {
//                    postReadSellType1.setVisibility(View.GONE);
//                }
//
//                if (info.getPostDelivery().equals("Y")) {
//                    postReadDelivery.setText("????????? ??????");
//                } else {
//                    postReadDelivery.setText("????????? ??????");
//                }
//
//                Log.e("123", "????????????");
//
//            }
//
//            @Override
//            public void onFailure(Call<PostInfo> call, Throwable t) {
//                Log.e("123", "????????????");
//            }
//        });


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


        //????????? frame ???????????? ????????? ????????? ????????????
        postReadSellerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123", "????????? ????????? ??????");

                Intent intent = new Intent(Activity_post_read.this, Activity_seller_info.class);
                intent.putExtra("nickname", postReadNickname.getText().toString());
                intent.putExtra("postNum", postNum);
                startActivity(intent);
            }
        });
        //???????????? ??????
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //???????????? ?????? api??? ??? ?????? ???????????? ?????????.
        postReadLocationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("123",postReadLocationInfo.getText().toString());
                Intent intent = new Intent(Activity_post_read.this, Activity_location_map.class);
                if (postReadLocationInfo.getText().toString().equals(" ???????????? ??????")) {
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
        // ????????????  ?????? ?????? ????????? ?????????????????? ???????????? ?????????!!
        updateDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // shared ??? ????????????

//                id=sharedPreferences.getString("userId","");
                PopupMenu popup = new PopupMenu(Activity_post_read.this, updateDeleteImage);
                Log.e("123", "sellerId : " + sellerId);
                Log.e("123", "userId : " + id);
                if (sellerId.equals(id)) {
                    MenuInflater inflate = popup.getMenuInflater();
                    inflate.inflate(R.menu.post_update_menu, popup.getMenu());
                    popup.show();
                } else {
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
                            builder.setTitle("Used Electronics market ??????");
                            builder.setMessage("???????????? ?????? ?????????????????????????");
                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("123", "????????????");
                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<DataMemberSignup> call = service.sendDeletePostInfo(postNum);
                                    call.enqueue(new Callback<DataMemberSignup>() {
                                        @Override
                                        public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                                            Log.e("123", "????????????");
                                            if (response.isSuccessful() && response.body() != null) {
                                                if (response.body().isSuccess()) {
                                                    Toast.makeText(Activity_post_read.this, "????????? ????????????", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Activity_post_read.this, Activity_main_home.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(Activity_post_read.this, "????????? ????????????", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DataMemberSignup> call, Throwable t) {

                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();


                        } else if (id == R.id.post_write_report) {

                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("123","?????? ?????? ???");
        //retrofit ??????
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum, "read", id);
        Log.e("456", id);
        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                Log.e("123","?????? ?????????");
                sellerId = response.body().getMemberId();

                //System.out.println("isClientLike"+response.body().isClientIsLike());
                // shared ??? ????????????
                Log.e("123", "??????" + response.body().getPostLocationDetail());

                // client ??? ????????? ????????? ????????? ??????, ????????? ?????????????????? like boolean ?????? ??????;
                if (response.body().isClientIsLike()) {
                    postReadLikeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
                    postReadLikeNumImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
                    like = false;
                }
                // client??? ????????? ????????? ????????? ?????????, ???????????????
                else{
                    postReadLikeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_border_24));
                    postReadLikeNumImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_border_24));
                    like=true;
                }

                // ???????????? ?????? ??????
                if (sellerId != null) {
                    if (!sellerId.equals(id)) {
                        postReadLinearStatus.setVisibility(View.GONE);
                    } else {
                        postReadLinearBottomOption.setVisibility(View.GONE);
                    }
                }

                //????????? ??????
                PostInfo info = response.body();

                //????????? ?????????????????? ?????????????????? ???????????? ?????? ???????????????
                if(info.getPostStatus()!=null){
                    if(!info.getPostStatus().equals("Y")){
                        //????????????,?????????,???????????? -> ?????? ??????????????? ??????
                        postReadLinearStatus.setVisibility(View.GONE);
                        postReadBuyProductDelivery.setVisibility(View.GONE);
                        adapter.setStatus(1);
                        postReadStatus.setVisibility(View.VISIBLE);

                        postReadStatusText.setText("????????????");
                    }

                    else{
                        //????????? ->
                        postReadStatus.setVisibility(View.INVISIBLE);
                        adapter.setStatus(0);
                        postReadStatusText.setText("?????????");
                    }

                }

                adapter.setImageRoute(info.getImageRoute());
                adapter.notifyDataSetChanged();
                setupIndicators(info.getImageRoute().size());
                if (info.getMemberImage() == null) {
                    circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                } else {
                    Glide.with(getApplicationContext()).load(info.getMemberImage().toString()).into(circleImageView);
                }


                //?????? ?????? ?????? ???????????????
                timeDifferentCheck(info.getPostRegTime());
                //???????????? ??????
                if (info.getPostLocationAddress() != null) {
                    if (info.getPostLocationAddress().equals("")) {
                        postReadLocationInfo.setText(" " + info.getPostLocationName());

                    } else {
                        postReadLocationInfo.setText(" " + info.getPostLocationName() + " ( " + info.getPostLocationAddress() + " )");
                        if (info.getPostLocationDetail() != null) {
                            if (!info.getPostLocationDetail().equals("")) {
                                locationDetailImage.setVisibility(View.VISIBLE);
                                postReadPlaceDetail.setText("???????????? ?????? : " + info.getPostLocationDetail());
                            }
                        }

                    }
                }

                postReadLatitude = info.getPostLocationLatitude();
                postReadLongitude = info.getPostLocationLongitude();
                postLocationName = info.getPostLocationName();
                postLocationAddress = info.getPostLocationAddress();

                //
                Log.e("123", info.getProductNum());
                postReadProductNum.setText(info.getProductNum());
                //postReadView.setText(info.getPostViewNum());
                postReadLike.setText(info.getPostLikeNum());

                postReadContents.setText(info.getPostContents());
                System.out.println("postcontents" + info.getPostContents());


                postReadTitle.setText(info.getPostTitle());
                postReadPrice.setText(info.getPostPrice() + "???");
                if (info.getPostCategory().equals("???????????? ??????")) {
                    postReadCategory.setVisibility(View.INVISIBLE);
                }
                postReadCategory.setText("  *  " + info.getPostCategory());
                postReadNickname.setText(info.getNickname());

                if (info.getPostSellType().equals("?????????/????????????")) {


                } else if (info.getPostSellType().equals("?????????")) {
                    postReadDelivery.setVisibility(View.GONE);
                    postReadSellType2.setVisibility(View.GONE);
                } else {
                    postReadSellType1.setVisibility(View.GONE);
                }

                if (info.getPostDelivery().equals("Y")) {
                    postReadDelivery.setText("????????? ??????");
                } else {
                    postReadDelivery.setText("????????? ??????");
                }

                Log.e("123", "????????????");

            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {
                Log.e("123", "????????????");
            }
        });

    }

    private void setLikeList(String state) {

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<DataMemberSignup> call = service.setLikeList(id, postNum, state,postReadNickname.getText().toString());
        call.enqueue(new Callback<DataMemberSignup>() {
            @Override
            public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("456", "?????? ?????????");
                    //?????????  ??????
                    if (response.body().isSuccess() && state.equals("insert")) {
                        postReadLike.setText(String.valueOf(Integer.parseInt(postReadLike.getText().toString()) + 1));
                        postReadLikeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
                        postReadLikeNumImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24));
//                        customToastLikeList("?????? ????????? ??????????????? ?????????????????????.");
                        if (!isRunning) {
                            thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Message msg = new Message();
                                    Message msg2 = new Message();
                                    try {
                                        msg.arg1 = 1;
                                        handler.sendMessage(msg);
                                        thread.sleep(3000);
                                        msg2.arg1 = 0;
                                        handler.sendMessage(msg2);
                                    } catch (Exception e) {
                                        thread.interrupt();
                                        System.out.println(e);
                                    }

                                }
                            });
                            thread.start();
                        }
                        like = false;
                        //????????? ?????? ?????? ?????? ??????????????? ?????? ?????????..
                        if(response.body().isLikeNotification()){
                            Log.e("123","noti ??????? "+response.body().isLikeNotification());
                            Intent intent = new Intent("chatDataToServer");
                            intent.putExtra("type", 2);
                            intent.putExtra("purpose", "sendNotification");
                            intent.putExtra("postNum", postNum);
                            intent.putExtra("sendToNickname", postReadNickname.getText().toString());
                            intent.putExtra("message", nickName + "?????? ???????????? ??????\" "+postReadTitle.getText().toString()+"\" ??? ????????? ???????????????.");
                            LocalBroadcastManager.getInstance(Activity_post_read.this).sendBroadcast(intent);
                        }
                        else{
                            Log.e("123","noti ??????? "+response.body().isLikeNotification());
                        }

                    }
                    //????????? ??????
                    else if (response.body().isSuccess() && state.equals("delete")) {
                        postReadLike.setText(String.valueOf(Integer.parseInt(postReadLike.getText().toString()) - 1));
                        postReadLikeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_border_24));
                        postReadLikeNumImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_border_24));
                        like = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                Toast.makeText(Activity_post_read.this, "?????? ??????", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupIndicators(int count) {
        layoutIndicator.removeAllViewsInLayout();
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


        postReadChat=findViewById(R.id.post_read_chat);
        postReadStatus=findViewById(R.id.post_read_status_text);

        sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");
        nickName= sharedPreferences.getString("nickName","");

        //postReadTitle,postReadPrice,postReadDelivery,postReadSellType1,PostReadSellType2,postReadCategory,postReadContents;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        postReadLinearBottomOption = (LinearLayout) findViewById(R.id.post_read_bottom_option);

        postReadBuyProductDelivery = findViewById(R.id.post_read_buy_product_delivery);
        postReadMoveLoveList = findViewById(R.id.post_read_move_like_list);
        postReadLinearLoveList = findViewById(R.id.post_read_linear_love_list);
        postReadLikeNumImage = findViewById(R.id.post_read_like_num_image);
        postReadLikeImage = findViewById(R.id.post_read_like_image);

        locationDetailImage = findViewById(R.id.post_read_location_detail_image);
        postReadPlaceDetail = findViewById(R.id.post_read_place_detail);
        backImage = findViewById(R.id.post_write_category_1_back_arrow);
        updateDeleteImage = findViewById(R.id.post_write_update_delete);
        postReadStatusText = findViewById(R.id.post_read_post_status);
        postReadLinearStatus = findViewById(R.id.post_read_linear_status);
        postReadProductNum = findViewById(R.id.post_read_item_num);
        postReadTime = findViewById(R.id.post_read_time_check);
        postReadLike = findViewById(R.id.post_read_like_num);
        postReadView = findViewById(R.id.post_read_view_num);


        postReadSellerFrame = findViewById(R.id.post_read_seller_frame);
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

    public void customToastLikeList(String message) {
        LayoutInflater inflater = getLayoutInflater();

        View parent = (Activity_post_read.this).getWindow().getDecorView();
        View layout = inflater.inflate(R.layout.toast_custon_like_list, null, false);
        LinearLayout linearLayout = layout.findViewById(R.id.inner_linearLayout);
        Log.e("123", String.valueOf(parent.getWidth()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(parent.getWidth() - 200, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        TextView toastText = layout.findViewById(R.id.toast_textview);

        toastText.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setView(layout);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO ???????????? ???????????? ???????????? (????????? ??????)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO ???????????? ???????????? ???????????? (?????? ??????)
        toast.setGravity(Gravity.BOTTOM, 0, 150); //TODO ???????????? ???????????? ???????????? (?????? ??????)
        toast.setDuration(Toast.LENGTH_SHORT); //????????? ?????? ??????
        toast.show();

    }

    public void timeDifferentCheck(String uploadTime) {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        //???->??? -> ?????? -> ??? -> ??? ??? ??????????????? ????????????
        String nowTime = formatter.format(date);

        String date1 = nowTime; //??????1
        String date2 = uploadTime; //??????2

        try {
            Date format1 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date1);
            Date format2 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //??? ??????
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //??? ??????
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //??? ??????
            long diffDays = diffSec / (24 * 60 * 60); //????????? ??????

            System.out.println(diffSec + "??? ??????");
            System.out.println(diffMin + "??? ??????");
            System.out.println(diffHor + "??? ??????");
            System.out.println(diffDays + "??? ??????");

            if (diffSec < 0) {
                postReadTime.setText("1??????");
            } else if (diffDays != 0) {
                postReadTime.setText(String.valueOf(diffDays) + "??? ???");
            } else if (diffHor != 0) {
                postReadTime.setText(String.valueOf(diffHor) + "?????? ???");
            } else if (diffMin != 0) {
                postReadTime.setText(String.valueOf(diffMin) + "??? ???");
            } else if (diffSec != 0) {
                postReadTime.setText(String.valueOf(diffSec) + "??? ???");
            } else {
                postReadTime.setText("1??????");
            }

        } catch (Exception e) {

        }
    }


}