package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class Activity_buy_product_delivery extends AppCompatActivity {

    private ImageView productImage,backImage;

    private TextView buyerAddressChange,productBuyerPhoneNumber;
    private TextView productTitle,productPrice,productBuyerName,productBuyerAddress;
    private Spinner productBuySpinner;
    private EditText productDeliveryDetail,productBuyerAddressDetail;;
    private TextView productBuyApprove,productBuyCancel;
    private String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product_delivery);

        variableInit();




        //배송지 주소 찾는 화면면
        productBuyerAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Activity_buy_product_delivery.this,Activity_get_address.class);
                addressLauncher.launch(intent);
            }
        });

    }


    private ActivityResultLauncher<Intent> addressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK){

                            address=result.getData().getStringExtra("address");
                            productBuyerAddress.setText(address);
                        }
                    }
                }
        );


    public void variableInit(){
        productImage=(ImageView) findViewById(R.id.buy_product_image);
        backImage=(ImageView) findViewById(R.id.buy_product_back_arrow);

        productBuyerPhoneNumber=(TextView)findViewById(R.id.buy_product_buyer_phone_number);

        productTitle=(TextView) findViewById(R.id.buy_product_title);
        productPrice=(TextView) findViewById(R.id.buy_product_price);
        productBuyerName=(TextView) findViewById(R.id.buy_product_buyer_name);
        productBuyerAddress=(TextView) findViewById(R.id.buy_product_buyer_address);
        productBuyerAddressDetail=(EditText) findViewById(R.id.buy_product_buyer_address_detail);

        productBuySpinner=(Spinner) findViewById(R.id.buy_product_delivery_detail_spinner);
        productDeliveryDetail=(EditText) findViewById(R.id.buy_product_delivery_detail_text);

    }
}