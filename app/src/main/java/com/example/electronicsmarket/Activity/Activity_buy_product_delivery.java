package com.example.electronicsmarket.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
import com.example.electronicsmarket.Dto.PostInfo;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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





        //상품 정보들 가져와서 배치해주고, 기존 배송지 존재하면,데이터 세팅해두기,
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum, "delivery", id);
        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                if (response.isSuccessful() && response.body() != null) {

                    productName = response.body().getPostTitle();
                    productPriceS = response.body().getPostPrice();

                    productTitle.setText(productName);
                    productPrice.setText(productPriceS + "원");
                    Glide.with(Activity_buy_product_delivery.this).load(response.body().getImageRoute().get(0)).into(productImage);
                    productBuyerName.setText(response.body().getClientNickname());
                    productBuyerPhoneNumber.setText(response.body().getClientPhoneNumber());



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

        // 수령인 이름 변경
//        receiverChangeName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder DialogBuilder =new AlertDialog.Builder(v.getContext());
//                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_receiver_change, null, false);
//                DialogBuilder.setView(dialogView);
//                EditText changedName;
//
//                changedName=dialogView.findViewById(R.id.dialog_receive_change_name);
//                DialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        productBuyerName.setText(changedName.getText().toString());
//                        dialog.dismiss();
//                    }
//                });
//                DialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

        //결제확인 버튼 눌렀을 경우, 선택한 결제방식에 따라 다른화면으로 이동.
        productBuyApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radioCardPay.isChecked() && !radioKakakoPay.isChecked()) {
                    Toast.makeText(Activity_buy_product_delivery.this, "결제 방법을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //예외처리 필요!
                if(productBuyerAddress.getText().toString().equals("")||productBuyerAddressDetail.getText().toString().equals("")){
                    Toast.makeText(Activity_buy_product_delivery.this, "배송 받을 주소 제대로 적으세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (radioKakakoPay.isChecked()) {
                    //카카오페이 결제 선택시,
                    Intent intent = new Intent(Activity_buy_product_delivery.this, Activity_kakaopay_api.class);

                    //기본주소지로 사용자가 check 해놓았으면, 해당 체크된걸 db에 알려줘서 저장할 수 있게끔한다.
                    if (setStandardAddress.isChecked()) {
                        intent.putExtra("setStandardAddress","true");
                    }
                    //결제방식 데이터 전송.
                    intent.putExtra("postNum",postNum);
                    intent.putExtra("buyerName",productBuyerName.getText().toString());
                    intent.putExtra("payType","카카오페이");
                    intent.putExtra("tradeType","택배거래");
                    intent.putExtra("buyerId",id);

                    //배송지관련 정보 전송
                    intent.putExtra("deliveryDetail",productBuySpinner.getSelectedItem().toString());
                    intent.putExtra("deliveryDetail2Body",productDeliveryDetail.getText().toString());
                    intent.putExtra("address", productBuyerAddress.getText().toString());
                    intent.putExtra("addressDetail",productBuyerAddressDetail.getText().toString());

                    //상품정보 관련 intent로 데이터 전송
                    intent.putExtra("productName", productName);
                    intent.putExtra("productPay", productPriceS.replace(",", ""));
                    startActivity(intent);
                } else {
                    //카드결제 선택시,

                }

            }
        });
        //주소지 변경 버튼
        buyerAddressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Activity_buy_product_delivery.this, Activity_address_change.class);
                addressChangeLauncher.launch(intent);

            }
        });

        //배송지 주소 입력 버튼 누르면 -> 주소 찾는 화면 이동.
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

        //배송지 변경 버튼
        buyerAddressChange=(TextView) findViewById(R.id.buy_product_address_change);

        //결제확인 및 취소 버튼
        productBuyApprove = (TextView) findViewById(R.id.buy_product_buy_approve);
        productBuyCancel = (TextView) findViewById(R.id.buy_product_cancel_buy);

        //결제관련
        payRadioGroup = (RadioGroup) findViewById(R.id.buy_product_radio_group);
        radioCardPay = (RadioButton) findViewById(R.id.buy_product_radio_card_pay);
        radioKakakoPay = (RadioButton) findViewById(R.id.buy_product_radio_kakao_pay);

        //shared 에 저장되어있는 내 정보 활용
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