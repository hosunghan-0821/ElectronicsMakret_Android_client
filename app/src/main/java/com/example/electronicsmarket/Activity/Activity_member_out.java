package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.electronicsmarket.Dto.DataMemberSignup;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_member_out extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Retrofit retrofit;
    Button cancelBtn,writeBtn;
    EditText reasonText;
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    String[] items={"선택하세요","비매너 사용자를 만났어요","물품이 안팔려요","사고 싶은 물건이 없어요","기타"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_out);

        variableInit();


        for(int i=0;i<items.length;i++){
            arrayList.add(items[i]);
        }


        arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0){
                    cancelBtn.setVisibility(View.VISIBLE);
                    writeBtn.setVisibility(View.VISIBLE);
                    if(arrayList.get(position).equals("기타")){
                        reasonText.setVisibility(View.VISIBLE);
                    }
                    else{
                        reasonText.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    cancelBtn.setVisibility(View.INVISIBLE);
                    writeBtn.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(arrayAdapter);

    }
    public void variableInit(){
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        spinner=findViewById(R.id.member_out_spinner);
        reasonText=findViewById(R.id.member_out_reason);
        cancelBtn=findViewById(R.id.cancel_button);
        writeBtn=findViewById(R.id.write_button);

        cancelBtn.setOnClickListener(cancel);
        writeBtn.setOnClickListener(write);

    }

    View.OnClickListener write=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String reason;
            if(spinner.getSelectedItem().toString().equals("기타")){
                if(reasonText.getText().toString().equals("")){
                    Toast.makeText(Activity_member_out.this, "사유를 적어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    reason=reasonText.getText().toString();
                }
            }
            else{
                reason=spinner.getSelectedItem().toString();
            }

            AlertDialog.Builder builder =new AlertDialog.Builder(Activity_member_out.this);

            builder.setTitle("정말 회원탈퇴 하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                    String id=sharedPreferences.getString("userId","");
                    Log.e("123",id);
                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<DataMemberSignup> call = service.memberOut(id,reason);
                    call.enqueue(new Callback<DataMemberSignup>() {
                        @Override
                        public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                            if(response.isSuccessful()&&response.body()!=null){
                                if(response.body().getMessage().equals("회원탈퇴")){

                                    //회원 로그인 shared 정보 파기
                                    sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.clear();
                                    edit.commit();
                                    //
                                    dialog.dismiss();

                                    //회원 로그아웃 절차
                                    Toast.makeText(Activity_member_out.this, "회원탈퇴 완료", Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(Activity_member_out.this, Activity_main.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                            Log.e("123","통신 실패");
                            dialog.dismiss();
                        }
                    });
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.show();



        }
    };
    View.OnClickListener cancel=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}