package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.electronicsmarket.R;

import java.util.Arrays;
import java.util.List;

public class Activity_delivery_num_input extends AppCompatActivity {

    private EditText deliveryNum;
    private Spinner deliveryCompanySpinner;
    private TextView deliveryConfirm;
    private ImageView backImage;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_num_input);
        variableInit();


        deliveryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //예외처리,
                if (deliveryCompanySpinner.getSelectedItem().toString().equals("택배사 선택")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_delivery_num_input.this);
                    builder.setTitle("알림");
                    builder.setMessage("택배사를 선택해주세요");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }
                else if(deliveryNum.getText().toString().length()<=9){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_delivery_num_input.this);
                    builder.setTitle("알림");
                    builder.setMessage("운송장 번호를 다시 확인해 주세요");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }

                Intent intent =new Intent();
                intent.putExtra("deliveryCompany",deliveryCompanySpinner.getSelectedItem().toString());
                intent.putExtra("deliveryNum",deliveryNum.getText().toString());
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

    public void variableInit() {
        deliveryConfirm = (TextView) findViewById(R.id.delivery_input_confirm);
        deliveryCompanySpinner = (Spinner) findViewById(R.id.delivery_input_company_spinner);
        deliveryNum = (EditText) findViewById(R.id.delivery_input_delivery_num);
        backImage=(ImageView) findViewById(R.id.delivery_input_back_arrow);

        arrayList = Arrays.asList(getResources().getStringArray(R.array.company_options));
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        deliveryCompanySpinner.setAdapter(arrayAdapter);
    }
}