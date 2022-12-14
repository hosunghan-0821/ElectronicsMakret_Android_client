package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicsmarket.Dto.DataMemberSignup;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_main extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText loginId,loginPassword;
    private Button singupBtn,loginBtn;
    private TextView findBtn;
    private CheckBox autoLogin;
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        variableInit();
        getPreferences();

        singupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Activity_main.this, Activity_signup.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitService service = retrofit.create(RetrofitService.class);
                //비밀번호 암호화 필요
                // 로그인, 비밀번호 not 공백인것만 check
                if(!loginId.getText().toString().equals("")&&!loginPassword.getText().toString().equals("")){

                    Call<DataMemberSignup> call =service.sendLoginInfo(loginId.getText().toString(), Activity_signup.getHash(loginPassword.getText().toString()));
                    call.enqueue(new Callback<DataMemberSignup>() {
                        @Override
                        public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                            if(response.isSuccessful()&&response.body()!=null){

                                sharedPreferences= getSharedPreferences("autoLogin",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                if(response.body().getMessage().equals("로그인 성공")){
                                    if(autoLogin.isChecked()){

                                        //자동로그인 체크되면 0
                                        Log.e("123","닉네임"+response.body().getNickname());
                                        editor.putString("autoLogin","0");
                                        editor.putString("nickName",response.body().getNickname());
                                        editor.putString("userId",loginId.getText().toString());
                                        editor.putString("userPassword",loginPassword.getText().toString());
                                        editor.commit();
                                    }
                                    else{
                                        Log.e("123","닉네임"+response.body().getNickname());
                                        editor.putString("autoLogin","1");
                                        editor.putString("nickName",response.body().getNickname());
                                        editor.putString("userId",loginId.getText().toString());
                                        editor.putString("userPassword",loginPassword.getText().toString());
                                        editor.commit();
                                    }
                                    Toast.makeText(Activity_main.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(Activity_main.this, Activity_main_home.class);
                                    startActivity(intent);
                                    (Activity_main.this).finish();

                                }
                                else{
                                    Toast.makeText(Activity_main.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<DataMemberSignup> call, Throwable t) {

                        }
                    });

                }
                else{
                    Toast.makeText(Activity_main.this, "회원정보 제대로 입력", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_main.this, Activity_find_password.class);
                startActivity(intent);

            }
        });
    }
    public void variableInit(){

        autoLogin=findViewById(R.id.auto_login_check);
        findBtn=findViewById(R.id.find_password);
        singupBtn =findViewById(R.id.signup_button);
        loginId=findViewById(R.id.login_id);
        loginPassword=findViewById(R.id.login_password);
        loginBtn=findViewById(R.id.login_button);

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private void getPreferences(){

        sharedPreferences= getSharedPreferences("autoLogin",MODE_PRIVATE);
        String autoLoginNum=sharedPreferences.getString("autoLogin","");
        String id =sharedPreferences.getString("userId","");
        String password=sharedPreferences.getString("userPassword","");
        if(autoLoginNum.equals("0")){
            loginId.setText(id);
            loginPassword.setText(password);
            autoLogin.setChecked(true);

            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataMemberSignup> call =service.sendLoginInfo(id, Activity_signup.getHash(password));
            call.enqueue(new Callback<DataMemberSignup>() {
                @Override
                public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        if(response.body().getMessage().equals("로그인 성공")){

                            Intent intent =new Intent(Activity_main.this, Activity_main_home.class);
                            startActivity(intent);
                            (Activity_main.this).finish();
                            Toast.makeText(Activity_main.this, "자동로그인 성공", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Activity_main.this, "자동로그인 실패", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<DataMemberSignup> call, Throwable t) {

                }
            });
        }
        else{
            return;
        }



    }
}