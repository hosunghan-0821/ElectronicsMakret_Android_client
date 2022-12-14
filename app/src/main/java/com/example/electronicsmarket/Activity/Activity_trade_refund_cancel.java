package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicsmarket.infra.KakaoPay.KaKaoPayResult;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.Dto.RefundInfo;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_trade_refund_cancel extends AppCompatActivity {

    public final String API_KEY = "KakaoAK 980cc4d9e8446ee50d352b75e72f73ae";
    private String id;
    private ImageView backImage;
    private TextView tradeRefundTitle,tradeRefundConfirm;
    private String readType,tradeNum,deliveryStatus;
    private EditText tradeRefundReason;
    private Retrofit retrofit;
    private String productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_refund_cancel);

        variableInit();

        Intent intent = getIntent();
        readType=intent.getStringExtra("readType");
        tradeNum=intent.getStringExtra("tradeNum");
        deliveryStatus=intent.getStringExtra("deliveryStatus");
        productPrice=intent.getStringExtra("productPrice");
        productPrice=productPrice.replace("원","");
        productPrice=productPrice.replace(",","");




        //환불요청 , 거래취소 요청
        tradeRefundConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<RefundInfo> call = service.productRefund(tradeNum,id,tradeRefundReason.getText().toString(),deliveryStatus);
                call.enqueue(new Callback<RefundInfo>() {
                    @Override
                    public void onResponse(Call<RefundInfo> call, Response<RefundInfo> response) {

                        if(response.isSuccessful()&&response.body()!=null){

                            if(response.body().isSuccess()){
                                Log.e("123","성공");
                                if(response.body().getTradeType()!=null){

                                    //결제방식이 카카오페이일 경우,
                                    if(response.body().getTradeType().equals("카카오페이")){
                                        Log.e("123","카카오페이일 경우");

                                        String tidPin=response.body().getKakaoTid();
                                        Log.e("123","tidPin"+tidPin);
                                        Log.e("123","productprcie"+productPrice);
                                        Call<KaKaoPayResult> call2=service.sendKaKaoPayCancelRequest(
                                                API_KEY,
                                                "TC0ONETIME",
                                                tidPin,
                                                productPrice,
                                                "0"
                                        );
                                        call2.enqueue(new Callback<KaKaoPayResult>() {
                                            @Override
                                            public void onResponse(Call<KaKaoPayResult> call, Response<KaKaoPayResult> response) {
                                                Log.e("123","카카오결제취소성공");

                                                Toast.makeText(getApplicationContext(), "환불처리 되었습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent =new Intent(Activity_trade_refund_cancel.this, Activity_buy_list.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                            }

                                            @Override
                                            public void onFailure(Call<KaKaoPayResult> call, Throwable t) {
                                                Log.e("123","카카오실패");
                                            }
                                        });
                                    }


                                }

                            }

                        }
                    }
                    @Override
                    public void onFailure(Call<RefundInfo> call, Throwable t) {
                        Log.e("123",t.toString());
                        Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void variableInit(){

        //sharedPreference
        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        id=sharedPreferences.getString("userId","");



        //retrofit 2 관련
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        backImage=(ImageView) findViewById(R.id.trade_refund_back_arrow);

        tradeRefundTitle=(TextView) findViewById(R.id.trade_refund_title);
        tradeRefundConfirm= (TextView) findViewById(R.id.trade_refund_confirm);

        tradeRefundReason=(EditText) findViewById(R.id.trade_refund_cancel_reason);

    }
}