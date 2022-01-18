package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class Activity_change_password extends AppCompatActivity {

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

        standardPasswordText=findViewById(R.id.standard_password_text);
        newPasswordText=findViewById(R.id.new_password_text);
        newPasswordCheckText=findViewById(R.id.new_password_text_check);

        standardPassword=findViewById(R.id.standard_password);
        newPassword=findViewById(R.id.new_password);
        newPasswordCheck=findViewById(R.id.new_password_check);
        passwordCheckBtn=findViewById(R.id.change_password_button);
    }
}