package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_buy_product_delivery extends AppCompatActivity {


    public static Activity buyProductActivity;
    private ImageView productImage, backImage;

    private CheckBox setStandardAddress;
    private TextView buyerAddressChange, productBuyerPhoneNumber, receiverChangeName;
    private TextView productTitle, productPrice, productBuyerName;
    private Spinner productBuySpinner;
    private EditText productDeliveryDetail, productBuyerAddressDetail, productBuyerAddress;
    private TextView productBuyApprove, productBuyCancel;
    private String address;
    private Retrofit retrofit;
    private String postNum;
    private String id, productName, productPriceS;
    private SharedPreferences sharedPreferences;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> arrayList;
    private RadioGroup payRadioGroup;
    private RadioButton radioCardPay, radioKakakoPay;
    private HashMap<String, RequestBody> requestMap = new HashMap<>();






    private ActivityResultLauncher<Intent> kakaoPayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    Log.e("123", "onActivityResult()");
                    Log.e("123", "result.getresultcode" + result.getResultCode());
//                    if (result.getResultCode() == Activity_kakaopay_api.payResultCode) {
//                        Log.e("123", result.getData().getStringExtra("state"));
//                        //?????? ?????? ??? ?????? ??????
//                        //????????? ???????????? ?????????, ????????? ?????? ?????? ???????????? ?????? ?????? db??? ????????? ??? ????????????, ????????? ?????? finish
//                        Log.e("123", "????????? ?????? ????????????");
//
//                        //?????????????????? ???????????? check ???????????????, ?????? ???????????? db??? ???????????? ????????? ??? ???????????????.
//                        if (setStandardAddress.isChecked()) {
//                            RequestBody isCheckBody = RequestBody.create(MediaType.parse("text/plain"), "true");
//                            requestMap.put("setStandardAddress", isCheckBody);
//                        }
//
//                        //?????? ???????????? ????????? ?????????, DB??? ??????????????????.
//                        //???????????? ???, ????????? ??????
//                        RequestBody postNumBody = RequestBody.create(MediaType.parse("text/plain"), postNum);
//                        RequestBody payTypeBody = RequestBody.create(MediaType.parse("text/plain"), "???????????????");
//                        RequestBody tradeTypeBody = RequestBody.create(MediaType.parse("text/plain"), "????????????");
//                        RequestBody buyerId = RequestBody.create(MediaType.parse("text/plain"), id);
//                        RequestBody buyerName = RequestBody.create(MediaType.parse("text/plain"), productBuyerName.getText().toString());
//
//
//                        requestMap.put("buyerName", buyerName);
//                        requestMap.put("postNum", postNumBody);
//                        requestMap.put("payType", payTypeBody);
//                        requestMap.put("tradeType", tradeTypeBody);
//                        requestMap.put("buyerId", buyerId);
//
//                        //??????????????? ??????
//                        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), productBuyerAddress.getText().toString());
//                        RequestBody addressDetailBody = RequestBody.create(MediaType.parse("text/plain"), productBuyerAddressDetail.getText().toString());
//                        RequestBody deliveryDetailBody = RequestBody.create(MediaType.parse("text/plain"), productBuySpinner.getSelectedItem().toString());
//                        RequestBody deliveryDetail2Body = RequestBody.create(MediaType.parse("text/plain"), productDeliveryDetail.getText().toString());
//
//                        //??????????????? ??????
//                        requestMap.put("deliveryDetail2Body", deliveryDetail2Body);
//                        requestMap.put("deliveryDetail", deliveryDetailBody);
//                        requestMap.put("addressDetail", addressDetailBody);
//                        requestMap.put("address", addressBody);
//
//
//                        RetrofitService service = retrofit.create(RetrofitService.class);
//                        Call<paymentInfo> call = service.sendPaymentInfo(requestMap);
//                        call.enqueue(new Callback<paymentInfo>() {
//                            @Override
//                            public void onResponse(Call<paymentInfo> call, Response<paymentInfo> response) {
//                                //????????? db??? ??????????????? -> ?????? ?????? ???????????? ???????????? ?????? ????????? ????????? ??? ?????????
//                            }
//
//                            @Override
//                            public void onFailure(Call<paymentInfo> call, Throwable t) {
//
//                            }
//                        });
//
//                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product_delivery);

        buyProductActivity=Activity_buy_product_delivery.this;
        variableInit();
        Intent i = getIntent();
        postNum = i.getStringExtra("postNum");





        //?????? ????????? ???????????? ???????????????, ?????? ????????? ????????????,????????? ???????????????,
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum, "delivery", id);
        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                if (response.isSuccessful() && response.body() != null) {

                    productName = response.body().getPostTitle();
                    productPriceS = response.body().getPostPrice();

                    productTitle.setText(productName);
                    productPrice.setText(productPriceS + "???");
                    Glide.with(Activity_buy_product_delivery.this).load(response.body().getImageRoute().get(0)).into(productImage);
                    productBuyerName.setText(response.body().getClientNickname());
                    productBuyerPhoneNumber.setText(response.body().getClientPhoneNumber());


                    Log.e("123", "getaddressdetail" + response.body().getAddressDetail());
                    String addressInfo = response.body().getAddressDetail();
                    String deliveryRequireInfo = response.body().getDeliveryRequire();

                    if (addressInfo != null) {

                        if (!addressInfo.equals("")) {
                            productBuyerAddress.setText(addressInfo.split("__")[0]);
                            productBuyerAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            productBuyerAddressDetail.setText(addressInfo.split("__")[1]);
                        }

                    }
                    if(response.body().getReceiverName()!=null){
                        productBuyerName.setText(response.body().getReceiverName());
                    }
                }
            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {

            }
        });

        // ????????? ?????? ??????
//        receiverChangeName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder DialogBuilder =new AlertDialog.Builder(v.getContext());
//                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_receiver_change, null, false);
//                DialogBuilder.setView(dialogView);
//                EditText changedName;
//
//                changedName=dialogView.findViewById(R.id.dialog_receive_change_name);
//                DialogBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        productBuyerName.setText(changedName.getText().toString());
//                        dialog.dismiss();
//                    }
//                });
//                DialogBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog changeDialog=DialogBuilder.create();
//                changeDialog.show();
//            }
//        });

        //???????????? ?????? ????????? ??????, ????????? ??????????????? ?????? ?????????????????? ??????.
        productBuyApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radioCardPay.isChecked() && !radioKakakoPay.isChecked()) {
                    Toast.makeText(Activity_buy_product_delivery.this, "?????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }

                //???????????? ??????!
                if(productBuyerAddress.getText().toString().equals("")||productBuyerAddressDetail.getText().toString().equals("")){
                    Toast.makeText(Activity_buy_product_delivery.this, "?????? ?????? ?????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (radioKakakoPay.isChecked()) {
                    //??????????????? ?????? ?????????,
                    Intent intent = new Intent(Activity_buy_product_delivery.this, Activity_kakaopay_api.class);
                    Log.e("123", productName);
                    Log.e("123", productPriceS);

                    //?????????????????? ???????????? check ???????????????, ?????? ???????????? db??? ???????????? ????????? ??? ???????????????.
                    if (setStandardAddress.isChecked()) {
                        intent.putExtra("setStandardAddress","true");
                    }
                    //???????????? ????????? ??????.
                    intent.putExtra("postNum",postNum);
                    intent.putExtra("buyerName",productBuyerName.getText().toString());
                    intent.putExtra("payType","???????????????");
                    intent.putExtra("tradeType","????????????");
                    intent.putExtra("buyerId",id);

                    //??????????????? ?????? ??????
                    intent.putExtra("deliveryDetail",productBuySpinner.getSelectedItem().toString());
                    intent.putExtra("deliveryDetail2Body",productDeliveryDetail.getText().toString());
                    intent.putExtra("address", productBuyerAddress.getText().toString());
                    intent.putExtra("addressDetail",productBuyerAddressDetail.getText().toString());

                    //???????????? ?????? intent??? ????????? ??????
                    intent.putExtra("productName", productName);
                    intent.putExtra("productPay", productPriceS.replace(",", ""));
                    startActivity(intent);
                } else {
                    //???????????? ?????????,

                }

            }
        });
        //????????? ?????? ??????
        buyerAddressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Activity_buy_product_delivery.this,Activity_address_change.class);
                addressChangeLauncher.launch(intent);

            }
        });

        //????????? ?????? ?????? ?????? ????????? -> ?????? ?????? ?????? ??????.
//        productBuyerAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Activity_buy_product_delivery.this, Activity_get_address.class);
//                addressLauncher.launch(intent);
//            }
//        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private ActivityResultLauncher<Intent> addressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        address = result.getData().getStringExtra("address");
                        productBuyerAddress.setText(address);
                        productBuyerAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> addressChangeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode()==RESULT_OK){
                        Intent intent =result.getData();
                        productBuyerAddress.setText(intent.getStringExtra("address"));
                        productBuyerAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                        productBuyerAddressDetail.setText(intent.getStringExtra("addressDetail"));
                        productBuyerName.setText(intent.getStringExtra("receiverName"));
                        productBuyerPhoneNumber.setText(intent.getStringExtra("receiverPhoneNum"));
                    }
                }
            }
    );


    public void variableInit() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //????????? ?????? ??????
        buyerAddressChange=(TextView) findViewById(R.id.buy_product_address_change);

        //???????????? ??? ?????? ??????
        productBuyApprove = (TextView) findViewById(R.id.buy_product_buy_approve);
        productBuyCancel = (TextView) findViewById(R.id.buy_product_cancel_buy);

        //????????????
        payRadioGroup = (RadioGroup) findViewById(R.id.buy_product_radio_group);
        radioCardPay = (RadioButton) findViewById(R.id.buy_product_radio_card_pay);
        radioKakakoPay = (RadioButton) findViewById(R.id.buy_product_radio_kakao_pay);

        //shared ??? ?????????????????? ??? ?????? ??????
        sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");

        productImage = (ImageView) findViewById(R.id.buy_product_image);
        backImage = (ImageView) findViewById(R.id.buy_product_back_arrow);

        productBuyerPhoneNumber = (TextView) findViewById(R.id.buy_product_buyer_phone_number);

        receiverChangeName = (TextView) findViewById(R.id.buy_product_receiver_name_change);
        productTitle = (TextView) findViewById(R.id.buy_product_title);
        productPrice = (TextView) findViewById(R.id.buy_product_price);
        productBuyerName = (TextView) findViewById(R.id.buy_product_buyer_name);
        productBuyerAddress = (EditText) findViewById(R.id.buy_product_buyer_address);
        productBuyerAddress.setFocusable(false);
        productBuyerAddressDetail = (EditText) findViewById(R.id.buy_product_buyer_address_detail);
        productBuyerAddressDetail.setFocusable(false);

        setStandardAddress = (CheckBox) findViewById(R.id.buy_product_address_check_box);

        productBuySpinner = (Spinner) findViewById(R.id.buy_product_delivery_detail_spinner);
        productDeliveryDetail = (EditText) findViewById(R.id.buy_product_delivery_detail_text);

        //spinner adapter
        arrayList = Arrays.asList(getResources().getStringArray(R.array.delivery_options));
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        productBuySpinner.setAdapter(arrayAdapter);

    }
}