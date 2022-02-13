package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_trade_detail_info extends AppCompatActivity {

    private TextView tradeInfoSubTitle,tradeInfoTrader,tradeInfoAnnounce;
    private TextView tradeInfoDeliveryInput;
    private String id;
    private Retrofit retrofit;
    private String tradeNum,postNum;
    private ImageView backImage, tradeInfoProductImage;
    private TextView tradeInfoTitle,tradeInfoRequest,tradeInfoProductName,tradeInfoPrice,tradeInfoTradeType;
    private TextView tradeInfoSellerName,tradeinfoRegTime,tradeInfoPayType,tradeInfoPayPrice;
    private TextView tradeInfoReceiver,tradeInfoReceiverPhoneNum,tradeInfoReceiverAddress,tradeInfoDeliveryCompany,tradeInfoDeliveryNum;
    private TextView tradeInfoDeliveryRequire,tradeInfoSellerInfo;
    private Activity_buy_product_delivery buyProductActivity;
    private String readType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail_info);
        variableInit();
        buyProductActivity = (Activity_buy_product_delivery) Activity_buy_product_delivery.buyProductActivity;
        if(buyProductActivity!=null){
            buyProductActivity.finish();
        }
        Intent intent = getIntent();
        tradeNum=intent.getStringExtra("tradeNum");
        readType=intent.getStringExtra("readType");


        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PaymentInfo> call = service.getPaymentInfo(id,tradeNum);
        call.enqueue(new Callback<PaymentInfo>() {
            @Override
            public void onResponse(Call<PaymentInfo> call, Response<PaymentInfo> response) {
                if(response.isSuccessful()&&response.body()!=null){

                    //상품 일련번호
                    postNum=response.body().getPostNum();
                    //상품 0번 이미지
                    Glide.with(Activity_trade_detail_info.this).load(response.body().getTradeImageRoute()).into(tradeInfoProductImage);
                    //상품명
                    tradeInfoProductName.setText(response.body().getTradeTitle());
                    //거래방식
                    tradeInfoTradeType.setText(response.body().getTradeType());
                    //결제금액
                    tradeInfoPayPrice.setText(response.body().getTradePrice()+"원");
                    tradeInfoPrice.setText(response.body().getTradePrice()+"원");

                    //readType 에 따라서, 판매자 이름, 구매자 이름 나눠서 표기해줘야함
                    //판매자
                    if(readType!=null){
                        if(readType.equals("seller")){
                            tradeInfoSellerName.setText(response.body().getTradeBuyer());
                        }
                        else{
                            tradeInfoSellerName.setText(response.body().getTradeSeller());
                        }
                    }
                    if(response.body().getTradeDeliveryStatus()!=null&&readType!=null){
                        if(readType.equals("seller")&&response.body().getTradeDeliveryStatus().equals("배송대기")) {
                            tradeInfoAnnounce.setText("결제완료. \n배송대기 상태입니다.\n회원정보를 확인하고 택배발송 후 운송장을 입력해주세요");
                        }
                    }

                    //결제수단
                    tradeInfoPayType.setText(response.body().getTradePayType());

                    //배송수령인
                    tradeInfoReceiver.setText(response.body().getTradeReceiver());

                    //수령인 전번
                    tradeInfoReceiverPhoneNum.setText(response.body().getTradeReceiverPhoneNum());

                    //주소 가공
                    String address = response.body().getTradeAddress().split("__")[0];
                    String addressDetail =response.body().getTradeAddress().split("__")[1];
                    tradeInfoReceiverAddress.setText(address +" "+ addressDetail);

                    String require="";
                    try{
                        require += response.body().getTradeDeliveryRequire().split("__")[0];
                        require +=" ";
                        require += response.body().getTradeDeliveryRequire().split("__")[1];

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //배달 요청사항
                    tradeInfoDeliveryRequire.setText(require);

                    if(response.body().getTradeDeliveryCompany()!=null){
                        tradeInfoDeliveryCompany.setText(response.body().getTradeDeliveryCompany());
                    }
                    if(response.body().getTradeDeliveryNum()!=null){
                        tradeInfoDeliveryNum.setText(response.body().getTradeNum());
                    }
                    //구매일자 처리 가공해줘야함

                    String[] regTime=response.body().getTradeRegTime().split("-");
                    tradeinfoRegTime.setText(regTime[0]+ " ("+regTime[1]+")");


                }
            }

            @Override
            public void onFailure(Call<PaymentInfo> call, Throwable t) {

            }
        });

        //한 화면에서, 판매 상세조회, 구매 상세조회 두개 다 해결하기 위해서, readtype 나눠서 일부 ui 변경
        if(readType!=null){
            if(readType.equals("seller")){
                tradeInfoTitle.setText("판매 상세 조회");
                tradeInfoSubTitle.setText("판매 정보");
                tradeInfoTrader.setText("구매자");
                tradeInfoDeliveryInput.setVisibility(View.VISIBLE);
                tradeInfoRequest.setText("거래 취소");
            }
        }


        tradeInfoSellerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Activity_trade_detail_info.this,Activity_seller_info.class);
                intent.putExtra("nickname",tradeInfoSellerName.getText().toString());
                intent.putExtra("postNum",postNum);
                startActivity(intent);
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    public void variableInit(){

        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //xml 연동하기.
        backImage=(ImageView) findViewById(R.id.trade_info_back_arrow);
        tradeInfoProductImage =(ImageView) findViewById(R.id.trade_info_image);

        tradeInfoDeliveryInput=(TextView) findViewById(R.id.trade_info_delivery_info_input);
        tradeInfoAnnounce=(TextView) findViewById(R.id.trade_info_announce_text);

        tradeInfoTitle=(TextView) findViewById(R.id.trade_info_title_text);
        tradeInfoRequest=(TextView) findViewById(R.id.trade_info_refund_cancel_text);
        tradeInfoProductName=(TextView) findViewById(R.id.trade_info_product_name);
        tradeInfoPrice=(TextView) findViewById(R.id.trade_info_price);
        tradeInfoPayType=(TextView) findViewById(R.id.trade_info_pay_type);
        tradeInfoTradeType=(TextView)findViewById(R.id.trade_info_trade_type);
        tradeInfoSellerInfo=(TextView) findViewById(R.id.trade_info_seller_info);

        tradeInfoSellerName=(TextView) findViewById(R.id.trade_info_seller);
        tradeinfoRegTime=(TextView) findViewById(R.id.trade_info_trade_reg_time);
        tradeInfoPayPrice=(TextView) findViewById(R.id.trade_info_pay_price);
        tradeInfoDeliveryRequire=(TextView) findViewById(R.id.trade_info_delivery_require);

        tradeInfoReceiverAddress=(TextView)findViewById(R.id.trade_info_receiver_address);
        tradeInfoReceiver=(TextView) findViewById(R.id.trade_info_receiver);
        tradeInfoReceiverPhoneNum=(TextView) findViewById(R.id.trade_info_receiver_phone_number);
        tradeInfoDeliveryCompany=(TextView) findViewById(R.id.trade_info_company_name);
        tradeInfoDeliveryNum=(TextView) findViewById(R.id.trade_info_delivery_number);

        tradeInfoSubTitle=(TextView) findViewById(R.id.trade_info_sub_title);
        tradeInfoTrader=(TextView) findViewById(R.id.trade_info_seller_or_buyer);
    }
}