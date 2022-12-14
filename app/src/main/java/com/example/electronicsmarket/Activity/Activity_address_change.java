package com.example.electronicsmarket.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicsmarket.R;

public class Activity_address_change extends AppCompatActivity {


    private ImageView backImage;
    private TextView addressChangeConfirm,addressChangeAddress;
    private EditText addressChangeReceiverName,addressChangeAddressDetail;
    private EditText addressChangeReceiverPhoneNum;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_change);

        variableInit();



        //주소변경
        addressChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_address_change.this, Activity_get_address.class);
                addressLauncher.launch(intent);
            }
        });

        //확인버튼
        addressChangeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //예외처리.
                if(addressChangeReceiverName.getText().toString().equals("")||
                        addressChangeAddress.getText().toString().equals("")||
                        addressChangeAddressDetail.getText().toString().equals("")||
                        addressChangeReceiverPhoneNum.getText().toString().equals("")
                ){
                    Toast.makeText(getApplicationContext(), "모든 정보를 포함해서 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(addressChangeReceiverPhoneNum.getText().toString().length()<=10){
                    Toast.makeText(getApplicationContext(), "휴대번호 제대로 적어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent =new Intent ();
                intent.putExtra("receiverName",addressChangeReceiverName.getText().toString());
                intent.putExtra("address",address);
                intent.putExtra("addressDetail",addressChangeAddressDetail.getText().toString());
                intent.putExtra("receiverPhoneNum", addressChangeReceiverPhoneNum.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

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
                            addressChangeAddress.setText(address);
                            addressChangeAddress.setTextColor(Color.BLACK);
                            addressChangeAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                        }
                    }
                }
        );


    public void variableInit(){



        //editText
        addressChangeReceiverName=(EditText) findViewById(R.id.address_change_receiver_name);
        addressChangeAddressDetail=(EditText)findViewById(R.id.address_change_address_detail);
        addressChangeReceiverPhoneNum=(EditText)findViewById(R.id.address_change_receiver_phoneNum);

        //ImageView
        backImage=(ImageView) findViewById(R.id.address_change_back_arrow);
        //textview
        addressChangeAddress=(TextView)findViewById(R.id.address_change_address);
        addressChangeConfirm=(TextView) findViewById(R.id.address_change_confirm);
    }
}