package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_trade_detail_info extends AppCompatActivity {

    private TextView tradeInfoSubTitle, tradeInfoTrader, tradeInfoAnnounce, tradeInfoDeliveryInfo;
    private TextView tradeInfoDeliveryInput;
    private String id;
    private Retrofit retrofit;
    private String tradeNum, postNum;
    private ImageView backImage, tradeInfoProductImage;
    private TextView tradeInfoTitle, tradeInfoRequest, tradeInfoProductName, tradeInfoPrice, tradeInfoTradeType;
    private TextView tradeInfoSellerName, tradeinfoRegTime, tradeInfoPayType, tradeInfoPayPrice;
    private TextView tradeInfoReceiver, tradeInfoReceiverPhoneNum, tradeInfoReceiverAddress, tradeInfoDeliveryCompany, tradeInfoDeliveryNum;
    private TextView tradeInfoDeliveryRequire, tradeInfoSellerInfo;
    private Activity_buy_product_delivery buyProductActivity;
    private String readType;
    private String stringDeliveryStatus;
    private String tradeType;
    private ConstraintLayout constraintLayoutDeliveryInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail_info);
        variableInit();
        buyProductActivity = (Activity_buy_product_delivery) Activity_buy_product_delivery.buyProductActivity;
        if (buyProductActivity != null) {
            buyProductActivity.finish();
        }
        Intent intent = getIntent();

        tradeNum = intent.getStringExtra("tradeNum");
        readType = intent.getStringExtra("readType");
        tradeType=intent.getStringExtra("tradeType");
        Log.e("123","tradeType : "+tradeType);

        RetrofitService service = retrofit.create(RetrofitService.class);

        //???????????? ??????
        if(tradeType!=null){
            if(tradeType.equals("?????????")){
                Log.e("123","????????? ?????? ??????");
                constraintLayoutDeliveryInfo.setVisibility(View.GONE);
            }
            else if(tradeType.equals("????????????")){
                Call<PaymentInfo> call = service.getPaymentInfo(id, tradeNum);
                call.enqueue(new Callback<PaymentInfo>() {
                    @Override
                    public void onResponse(Call<PaymentInfo> call, Response<PaymentInfo> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            //?????? ????????????
                            postNum = response.body().getPostNum();
                            //?????? 0??? ?????????
                            Glide.with(Activity_trade_detail_info.this).load(response.body().getTradeImageRoute()).into(tradeInfoProductImage);
                            //?????????
                            tradeInfoProductName.setText(response.body().getTradeTitle());
                            //????????????
                            tradeInfoTradeType.setText(response.body().getTradeType());
                            //????????????
                            tradeInfoPayPrice.setText(response.body().getTradePrice() + "???");
                            tradeInfoPrice.setText(response.body().getTradePrice() + "???");

                            //readType ??? ?????????, ????????? ??????, ????????? ?????? ????????? ??????????????????
                            //?????????
                            if (readType != null) {
                                if (readType.equals("seller")) {
                                    tradeInfoSellerName.setText(response.body().getTradeBuyer());
                                } else {
                                    tradeInfoSellerName.setText(response.body().getTradeSeller());
                                }
                            }
                            if (response.body().getTradeDeliveryStatus() != null && readType != null) {
                                stringDeliveryStatus=response.body().getTradeDeliveryStatus();
                                if (readType.equals("seller") && response.body().getTradeDeliveryStatus().equals("????????????")) {
                                    tradeInfoAnnounce.setText("????????????. \n???????????? ???????????????.\n??????????????? ???????????? ???????????? ??? ???????????? ??????????????????");
                                }
                                else if(readType.equals("seller") && response.body().getTradeDeliveryStatus().equals("?????????")){
                                    tradeInfoAnnounce.setText("????????????. \n????????? ????????? ?????????.\n???????????? ???????????? ?????? ????????? ???????????????.");
                                }
                                else if(readType.equals("seller") && response.body().getTradeDeliveryStatus().equals("????????????")){
                                    tradeInfoAnnounce.setText("????????????. \n????????? ???????????? ???????????????.\n???????????? ??????????????? ??????????????????");
                                }
                                else if(readType.equals("buyer") && response.body().getTradeDeliveryStatus().equals("????????????")){
                                    tradeInfoAnnounce.setText("????????????. \n???????????? ???????????????.\n???????????? ????????? ??????????????????");
                                }
                                else if(readType.equals("buyer") && response.body().getTradeDeliveryStatus().equals("?????????")){
                                    tradeInfoAnnounce.setText("????????????. \n????????? ?????????. ????????? ??????????????????");
                                }
                                else if(readType.equals("buyer") && response.body().getTradeDeliveryStatus().equals("????????????")){
                                    tradeInfoAnnounce.setText("????????????. \n???????????? ???????????????. ?????? ????????? ???????????? ???????????????");
                                }


                            }

                            //????????????
                            tradeInfoPayType.setText(response.body().getTradePayType());

                            //???????????????
                            tradeInfoReceiver.setText(response.body().getTradeReceiver());

                            //????????? ??????
                            tradeInfoReceiverPhoneNum.setText(response.body().getTradeReceiverPhoneNum());

                            //?????? ??????
                            try {
                                String address = response.body().getTradeAddress().split("__")[0];
                                String addressDetail = response.body().getTradeAddress().split("__")[1];
                                tradeInfoReceiverAddress.setText(address + " " + addressDetail);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            String require = "";
                            try {
                                require += response.body().getTradeDeliveryRequire().split("__")[0];
                                require += " ";
                                require += response.body().getTradeDeliveryRequire().split("__")[1];

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //?????? ????????????
                            tradeInfoDeliveryRequire.setText(require);

                            if (response.body().getTradeDeliveryCompany() != null) {
                                tradeInfoDeliveryCompany.setText(response.body().getTradeDeliveryCompany());
                            }
                            if (response.body().getTradeDeliveryNum() != null) {
                                //Log.e("123", "getTradeDeliveryNum : " + response.body().getTradeDeliveryNum());
                                tradeInfoDeliveryNum.setText(response.body().getTradeDeliveryNum());
                            }
                            //???????????? ?????? ??????????????????

                            try {
                                String[] regTime = response.body().getTradeRegTime().split("-");
                                tradeinfoRegTime.setText(regTime[0] + " (" + regTime[1] + ")");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentInfo> call, Throwable t) {

                    }
                });

            }
        }

        //??????????????? ?????? ????????? ??????????????????.


        //??? ????????????, ?????? ????????????, ?????? ???????????? ?????? ??? ???????????? ?????????, readtype ????????? ?????? ui ??????
        if (readType != null) {
            if (readType.equals("seller")) {
                tradeInfoTitle.setText("?????? ?????? ??????");
                tradeInfoSubTitle.setText("?????? ??????");
                tradeInfoTrader.setText("?????????");
                tradeInfoDeliveryInput.setVisibility(View.VISIBLE);
                tradeInfoRequest.setText("?????? ??????");
            } else if (readType.equals("buyer")) {
                tradeInfoDeliveryInfo.setVisibility(View.VISIBLE);
            }
        }


        //???????????? ?????? ???????????? ??????.
        tradeInfoDeliveryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                if(tradeInfoDeliveryCompany.getText().toString()!=null){
                    if(tradeInfoDeliveryCompany.getText().toString().equals("????????????")){
                        Toast.makeText(getApplicationContext(), "???????????? ????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent(Activity_trade_detail_info.this, Activity_delivery_info.class);
                intent.putExtra("tradeNum", tradeNum);
                intent.putExtra("deliveryCompany", tradeInfoDeliveryCompany.getText().toString());
                intent.putExtra("deliveryNum", tradeInfoDeliveryNum.getText().toString());
                intent.putExtra("deliveryReceiver", tradeInfoReceiver.getText().toString());
                startActivity(intent);
            }
        });


        //????????? ???????????? ???????????? ??????.
        tradeInfoDeliveryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Activity_trade_detail_info.this, Activity_delivery_num_input.class);
                deliveryNumLauncher.launch(intent);

            }
        });

        //????????? ???????????? ???????????? ??????
        tradeInfoSellerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_trade_detail_info.this, Activity_seller_info.class);
                intent.putExtra("nickname", tradeInfoSellerName.getText().toString());
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

        //????????? ???????????? ????????????
        tradeInfoRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(readType!=null){
                    //????????? ???????????? ?????? ????????? ??????,
                    if(readType.equals("buyer")){

                        //?????? ????????? ?????? ???????????? ??????
                        if( stringDeliveryStatus.equals("????????????")){

                            Intent intent =new Intent(Activity_trade_detail_info.this,Activity_trade_refund_cancel.class);
                            intent.putExtra("tradeNum",tradeNum);
                            intent.putExtra("readType","buyer");
                            intent.putExtra("deliveryStatus","????????????");
                            intent.putExtra("productPrice",tradeInfoPayPrice.getText().toString());
                            startActivity(intent);

                        }

                        //?????? ?????? ???????????? ????????? ????????? ?????????
                        else if(stringDeliveryStatus.equals("?????????")||stringDeliveryStatus.equals("????????????")){

                        }

                    }
                    //????????? ???????????? ?????? ????????? ?????????
                    else if(readType.equals("seller")){

                    }


                }

            }
        });


    }


    private ActivityResultLauncher<Intent> deliveryNumLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        //DB??? ????????? ?????? ??? ????????? ??????, ????????? ??????.

                        String deliveryCompany = result.getData().getStringExtra("deliveryCompany");
                        String deliveryNum = result.getData().getStringExtra("deliveryNum");

                        Log.e("123", result.getData().getStringExtra("deliveryCompany"));
                        Log.e("123", result.getData().getStringExtra("deliveryNum"));
                        tradeInfoDeliveryCompany.setText(deliveryCompany);
                        tradeInfoDeliveryNum.setText(deliveryNum);

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<PaymentInfo> call = service.setDeliveryInfo(tradeNum, deliveryNum, deliveryCompany);
                        call.enqueue(new Callback<PaymentInfo>() {
                            @Override
                            public void onResponse(Call<PaymentInfo> call, Response<PaymentInfo> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().isSuccess) {
                                        Toast.makeText(Activity_trade_detail_info.this, "????????? ?????? ??????!", Toast.LENGTH_SHORT).show();

                                        if(response.body().getDeliveryStatus()!=null){
                                            if(response.body().getDeliveryStatus().equals("????????????")){
                                                tradeInfoAnnounce.setText("????????????. \n????????? ???????????? ???????????????.\n???????????? ??????????????? ??????????????????");
                                            }
                                        }
                                        else{
                                            tradeInfoAnnounce.setText("????????????. \n????????? ????????? ?????????.\n???????????? ???????????? ?????? ????????? ???????????????.");
                                        }

                                    } else {
                                        Toast.makeText(Activity_trade_detail_info.this, "????????? ?????? ??????!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<PaymentInfo> call, Throwable t) {

                            }
                        });

                    }

                }
            }
    );

    public void variableInit() {

        // shared ??? ????????????
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //xml ????????????.
        backImage = (ImageView) findViewById(R.id.trade_info_back_arrow);
        tradeInfoProductImage = (ImageView) findViewById(R.id.trade_info_image);

        tradeInfoDeliveryInput = (TextView) findViewById(R.id.trade_info_delivery_info_input);
        tradeInfoAnnounce = (TextView) findViewById(R.id.trade_info_announce_text);

        tradeInfoTitle = (TextView) findViewById(R.id.trade_info_title_text);
        tradeInfoRequest = (TextView) findViewById(R.id.trade_info_refund_cancel_text);
        tradeInfoProductName = (TextView) findViewById(R.id.trade_info_product_name);
        tradeInfoPrice = (TextView) findViewById(R.id.trade_info_price);
        tradeInfoPayType = (TextView) findViewById(R.id.trade_info_pay_type);
        tradeInfoTradeType = (TextView) findViewById(R.id.trade_info_trade_type);
        tradeInfoSellerInfo = (TextView) findViewById(R.id.trade_info_seller_info);

        tradeInfoSellerName = (TextView) findViewById(R.id.trade_info_seller);
        tradeinfoRegTime = (TextView) findViewById(R.id.trade_info_trade_reg_time);
        tradeInfoPayPrice = (TextView) findViewById(R.id.trade_info_pay_price);
        tradeInfoDeliveryRequire = (TextView) findViewById(R.id.trade_info_delivery_require);

        tradeInfoReceiverAddress = (TextView) findViewById(R.id.trade_info_receiver_address);
        tradeInfoReceiver = (TextView) findViewById(R.id.trade_info_receiver);
        tradeInfoReceiverPhoneNum = (TextView) findViewById(R.id.trade_info_receiver_phone_number);
        tradeInfoDeliveryCompany = (TextView) findViewById(R.id.trade_info_company_name);
        tradeInfoDeliveryNum = (TextView) findViewById(R.id.trade_info_delivery_number);

        tradeInfoSubTitle = (TextView) findViewById(R.id.trade_info_sub_title);
        tradeInfoTrader = (TextView) findViewById(R.id.trade_info_seller_or_buyer);

        tradeInfoDeliveryInfo = (TextView) findViewById(R.id.trade_info_delivery_info);

        constraintLayoutDeliveryInfo=(ConstraintLayout) findViewById(R.id.constraintLayout_delivery_info);


    }
}