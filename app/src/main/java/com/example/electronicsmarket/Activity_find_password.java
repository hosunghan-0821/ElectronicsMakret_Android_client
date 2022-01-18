package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_find_password extends AppCompatActivity {

    Thread thread;
    Handler handler;
    EditText findPasswordEmail,VerifyNumber;
    TextView timerText,emailVerifyText,verifyNumberText;
    Button verifyBtn,findPasswordBtn;
    Retrofit retrofit;
    String verifyNum;
    int i;
    boolean isRunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        variableInit();

        findPasswordBtn.setOnClickListener(findPassword);
        verifyBtn.setOnClickListener(verifyNumberClick);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.arg1<=0){
                    isRunning=false;
                }
                int minute,sec;
                minute=msg.arg1/60;
                sec=msg.arg1-(minute*60);

                if(sec>=10){
                    timerText.setTextColor(Color.RED);
                    timerText.setText("0"+minute+":"+sec);
                }
                else{
                    timerText.setTextColor(Color.RED);
                   timerText.setText("0"+minute+":"+"0"+sec);
                }

            }
        };
    }

    View.OnClickListener verifyNumberClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isRunning){
                Toast.makeText(Activity_find_password.this, "유효시간 초과", Toast.LENGTH_SHORT).show();
                return;
            }
            if(VerifyNumber.getText().toString().equals(verifyNum)){

                AlertDialog.Builder builder =new AlertDialog.Builder(Activity_find_password.this);
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<PostEmail> call = service.newPassword(findPasswordEmail.getText().toString());

                call.enqueue(new Callback<PostEmail>() {
                    @Override
                    public void onResponse(Call<PostEmail> call, Response<PostEmail> response) {
                        if(response.isSuccessful()&&response.body().getIsSuccess()!=null){
                            Log.e("123","123");
                            if(response.body().getIsSuccess().equals("비번변경 성공")){
                                builder.setTitle("임시 비밀번호를 이메일로 전송했습니다.");
                                builder.setMessage("로그인 화면으로 이동합니다.");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent =new Intent(Activity_find_password.this, Activity_main.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        thread.interrupt();
                                        isRunning=false;
                                        startActivity(intent);
//                                        (FindPasswordActivity.this).finish();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PostEmail> call, Throwable t) {
                        Toast.makeText(Activity_find_password.this, "서버 응답없음", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(Activity_find_password.this, "인증번호가 틀립니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    View.OnClickListener findPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
            String email=findPasswordEmail.getText().toString();

            if(email.equals("")){
                Toast.makeText(Activity_find_password.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                emailVerifyText.setVisibility(View.VISIBLE);
                emailVerifyText.setText("이메일을 입력해주세요");
                emailVerifyText.setTextColor(Color.RED);
                return;
            }
            else if(!pattern.matcher(email).matches()){
                Toast.makeText(Activity_find_password.this, "이메일 형식을 확인해주세요", Toast.LENGTH_SHORT).show();
                emailVerifyText.setVisibility(View.VISIBLE);
                emailVerifyText.setText("이메일 형식을 확인해주세요");
                emailVerifyText.setTextColor(Color.RED);
                return;
            }

            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<PostEmail> call = service.find(findPasswordEmail.getText().toString());
            call.enqueue(new Callback<PostEmail>() {
                @Override
                public void onResponse(Call<PostEmail> call, Response<PostEmail> response) {

                    Log.e("123","들어옴");
                    if(response.isSuccessful()&&response.body()!=null){

                        if(response.body().getIsSuccess().equals("아이디 없음")){
                            emailVerifyText.setVisibility(View.VISIBLE);
                            emailVerifyText.setText("아이디 정보가 없습니다.");
                            emailVerifyText.setTextColor(Color.RED);
                            return;

                        }
                        else if(response.body().getIsSuccess().equals("아이디 존재")){
                            Log.e("123","들어옴123");

                            verifyNumberText.setVisibility(View.VISIBLE);
                            verifyBtn.setVisibility(View.VISIBLE);
                            VerifyNumber.setVisibility(View.VISIBLE);
                            emailVerifyText.setVisibility(View.VISIBLE);
                            emailVerifyText.setText("메일 발송");
                            emailVerifyText.setTextColor(Color.BLUE);
                            AlertDialog.Builder builder =new AlertDialog.Builder(Activity_find_password.this);
                            builder.setTitle("이메일로 인증번호를 전송 했습니다.");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            verifyNum=response.body().getVerifyNumber();
                            if(isRunning){
                                thread.interrupt();
                            }

                            thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                     i=181;
                                     while(isRunning){
                                         try{
                                             Thread.sleep(1000);
                                             i--;
                                             if(i==0){
                                                 isRunning=false;
                                             }
                                             Message msg = new Message();
                                             msg.arg1=i;
                                             handler.sendMessage(msg);
                                         }catch (Exception e){
                                             break;
                                         }
                                     }
                                }
                            });
                            isRunning=true;
                            thread.start();
                        }
                    }

                }

                @Override
                public void onFailure(Call<PostEmail> call, Throwable t) {

                }
            });

        }
    };
    public void variableInit(){

        handler = new Handler();
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        findPasswordEmail=findViewById(R.id.find_password_id);
        VerifyNumber=findViewById(R.id.find_password__verify_number);

        verifyNumberText=findViewById(R.id.verify_number_text);
        timerText=findViewById(R.id.find_verify_timer);
        emailVerifyText=findViewById(R.id.find_email_verify_text);

        findPasswordBtn=findViewById(R.id.find_email_send_button);
        verifyBtn=findViewById(R.id.find_verify_number_check_button);
    }
}