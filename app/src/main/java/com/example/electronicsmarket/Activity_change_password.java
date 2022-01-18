package com.example.electronicsmarket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class Activity_change_password extends AppCompatActivity {

    Retrofit retrofit;
    TextView standardPasswordText,newPasswordText,newPasswordCheckText;
    EditText standardPassword,newPassword,newPasswordCheck;
    Button passwordCheckBtn;
    Boolean passwordCheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        variableInit();

        passwordCheckBtn.setOnClickListener(passwordCheckClick);
        newPassword.addTextChangedListener(checkPasswordRule);
        newPasswordCheck.addTextChangedListener(checkPasswordSame);

    }

    View.OnClickListener passwordCheckClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //일단 기존 비밀번호 입력이 되어있는지 확인, passwordCheck=true 인지
            //최종확인인으로 값 새 비밀번호 값, 새비밀번호 입력값이 같은지 체크하기.

            if(standardPassword.getText().toString().equals("")){
                standardPasswordText.setText("기존 비밀번호 입력");
                standardPasswordText.setTextColor(Color.RED);
                return;
            }
            else if(!passwordCheck){
                newPasswordCheckText.setTextColor(Color.RED);
                newPasswordCheckText.setText("비밀번호가 다릅니다");
            }
            else if(!newPassword.getText().toString().equals(newPasswordCheck.getText().toString())){
                newPasswordCheckText.setTextColor(Color.RED);
                newPasswordCheckText.setText("비밀번호가 다릅니다");
                passwordCheck=false;
            }
            else{

                //retrofit전송시작.
                Log.e("123","들어옴");
                SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                String id=sharedPreferences.getString("userId","");

                RetrofitService service = retrofit.create(RetrofitService.class);
                String standardPasswordHash = Activity_signup.getHash(standardPassword.getText().toString());
                String newPasswordHash= Activity_signup.getHash(newPassword.getText().toString());

                Call<MemberSignup> call = service.sendNewPassword(id,standardPasswordHash,newPasswordHash);

                call.enqueue(new Callback<MemberSignup>() {
                    @Override
                    public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                        if(response.isSuccessful()&&response.body()!=null){
                            if(response.body().isSuccess()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_change_password.this);

                                builder.setTitle("비밀번호 변경 완료");
                                builder.setMessage("재로그인 해주세요");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPreferences= getSharedPreferences("autoLogin",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();

                                        Intent intent =new Intent(Activity_change_password.this, Activity_main.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        dialog.dismiss();

                                    }
                                });
                                builder.show();

                            }
                            else{

                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_change_password.this);

                                builder.setTitle("기존 비밀번호를 확인해주세요");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        standardPassword.setText(null);
                                        newPassword.setText(null);
                                        newPasswordCheck.setText(null);
                                        dialog.dismiss();

                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MemberSignup> call, Throwable t) {
                        Toast.makeText(Activity_change_password.this, "통신 오류", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        }
    };

    TextWatcher checkPasswordRule= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(newPassword.getText().toString().equals("")){
                newPasswordText.setText("");
                return;
            }

            if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$",s)){
                newPasswordText.setTextColor(Color.RED);
                newPasswordText.setText("숫자,특문 포함 9~12 비밀번호 입력");
            }
            else{
                newPasswordText.setTextColor(Color.BLUE);
                newPasswordText.setText("사용가능한 비밀번호입니다.");

            }

        }
    };

    TextWatcher checkPasswordSame=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(newPasswordCheck.getText().toString().equals("")){
                newPasswordCheckText.setText("");
                passwordCheck=false;
                return;
            }

            if(newPassword.getText().toString().equals(newPasswordCheck.getText().toString())){
                newPasswordCheckText.setTextColor(Color.BLUE);
                newPasswordCheckText.setText("비밀번호 동일");
                passwordCheck=true;
            }

            else{
                newPasswordCheckText.setTextColor(Color.RED);
                newPasswordCheckText.setText("비밀번호가 다릅니다");
                passwordCheck=false;
            }

        }
    };

    public void variableInit(){


        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        standardPasswordText=findViewById(R.id.standard_password_text);
        newPasswordText=findViewById(R.id.new_password_text);
        newPasswordCheckText=findViewById(R.id.new_password_text_check);

        standardPassword=findViewById(R.id.standard_password);
        newPassword=findViewById(R.id.new_password);
        newPasswordCheck=findViewById(R.id.new_password_check);
        passwordCheckBtn=findViewById(R.id.change_password_button);
    }
}