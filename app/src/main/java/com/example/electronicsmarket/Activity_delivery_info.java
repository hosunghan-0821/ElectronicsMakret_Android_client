package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_delivery_info extends AppCompatActivity {


    private TextView deliveryInfoReceiver,deliveryInfoDeliveryCompany,deliveryInfoDeliveryNum,deliveryInfoStatus;
    private TextView  deliveryInfoNoResult;
    private RecyclerView deliveryRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DeliveryInfo deliveryInfo;
    private Retrofit retrofit;
    private String deliveryCompany,deliveryNum,deliveryReceiver;
    private Adapter_delivery_info adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_info);
        variableInit();
        Intent intent =getIntent();

        deliveryCompany=intent.getStringExtra("deliveryCompany");
        deliveryNum=intent.getStringExtra("deliveryNum");
        deliveryReceiver=intent.getStringExtra("deliveryReceiver");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DeliveryInfo> call = service.getDeliveryInfo(deliveryNum,deliveryCompany);

        call.enqueue(new Callback<DeliveryInfo>() {
            @Override
            public void onResponse(Call<DeliveryInfo> call, Response<DeliveryInfo> response) {

                Log.e("123","통신 123");
                if(response.isSuccessful()&&response.body()!=null){
                    Log.e("123","issuccess"+response.body().isSuccess());
                    if(response.body().isSuccess()){
                        deliveryInfo=response.body();
                        adapter.setDeliveryInfo(deliveryInfo);
                        adapter.notifyDataSetChanged();

                        deliveryInfoStatus.setText(deliveryInfo.getDeliveryStatus());
                        deliveryInfoStatus.setTextColor(Color.BLUE);
                    }
                    else{
                        deliveryInfoNoResult.setVisibility(View.VISIBLE);
                    }


                }
            }

            @Override
            public void onFailure(Call<DeliveryInfo> call, Throwable t) {
                Log.e("123","통신 실패"+t.getMessage());

            }
        });

        deliveryInfoDeliveryCompany.setText(deliveryCompany);
        deliveryInfoDeliveryNum.setText(deliveryNum);
        deliveryInfoReceiver.setText(deliveryReceiver);
    }

    public void variableInit(){

        //retrofit2
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        deliveryInfoNoResult=findViewById(R.id.delivery_info_no_result);

        //recyclerview 관련
        deliveryRecyclerView=findViewById(R.id.delivery_info_recyclerview);
        linearLayoutManager=new LinearLayoutManager(Activity_delivery_info.this);
        deliveryInfo=new DeliveryInfo();
        adapter=new Adapter_delivery_info(deliveryInfo);

        deliveryRecyclerView.setAdapter(adapter);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);

        //기본 xml 연결
        deliveryInfoReceiver=(TextView) findViewById(R.id.delivery_info_receiver);
        deliveryInfoDeliveryCompany=(TextView)findViewById(R.id.delivery_info_delivery_company);
        deliveryInfoDeliveryNum=(TextView)findViewById(R.id.delivery_info_delivery_num);
        deliveryInfoStatus=(TextView)findViewById(R.id.delivery_info_status);

    }
}