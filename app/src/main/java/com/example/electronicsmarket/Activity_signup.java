package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_signup extends AppCompatActivity {



    Thread thread1;

    CheckBox policyCheckBox1,policyCheckBox2;
    EditText loginEmail,loginPassword,loginPasswordCheck,loginNickname,emailVerifyNumber;
    TextView emailCheckText,passwordCheckText,passwordRepeatCheckText,nicknameCheckText,verifyTimer,verifyNumberCheckText;
    Button emailSendBtn,nicknameCheckBtn,signupBtn,verifyNumberCheckBtn;
    Retrofit retrofit;

    int i;
    boolean isRunning=false, nicknameDuplicate=false, emailDuplicate=false, passwordCheck=false;
    private String verifynum,verifyNickname,verifyEmail="";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //사용할 변수들 따로 관리하는 함수
        variableInit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Btn onClickListener 달기
        signupBtn.setOnClickListener(signup);
        emailSendBtn.setOnClickListener(click);
        verifyNumberCheckBtn.setOnClickListener(verifyClick);
        nicknameCheckBtn.setOnClickListener(nicknameCheck);

        //비밀번호 확인
        loginPassword.addTextChangedListener(passwordWatcher);
        loginPasswordCheck.addTextChangedListener(passwordCheckWatcher);
        loginNickname.setOnFocusChangeListener(nicknameFocus);

        //thread 처리하기 위해서 핸들러
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.arg1<=0){
                    isRunning=false;
                }
                if(msg.arg1>=10){
                    verifyTimer.setText("00:"+msg.arg1);
                }
                else{
                    verifyTimer.setText("00:"+"0"+msg.arg1);
                }

            }
        };
    }

    public void variableInit(){

        //checkBox
        policyCheckBox1=findViewById(R.id.policy_checkBox1);
        policyCheckBox2=findViewById(R.id.policy_checkBox2);
        //editText
        loginEmail=findViewById(R.id.login_new_id);
        loginPassword=findViewById(R.id.login_new_password);
        loginPasswordCheck=findViewById(R.id.login_new_password_check);
        loginNickname=findViewById(R.id.login_nickname);
        emailVerifyNumber=findViewById(R.id.login_email_verify_number);

        //TextView
        emailCheckText=findViewById(R.id.login_email_verify);
        passwordCheckText=findViewById(R.id.login_password_verify);
        passwordRepeatCheckText=findViewById(R.id.login_password_same);
        nicknameCheckText=findViewById(R.id.nickname_check_text);
        verifyTimer=findViewById(R.id.verify_timer);
        verifyNumberCheckText=findViewById(R.id.verify_number_check_text);

        //Button
        verifyNumberCheckBtn=findViewById(R.id.find_verify_number_check_button);
        emailSendBtn=findViewById(R.id.email_send_button);
        nicknameCheckBtn=findViewById(R.id.nickname_check_button);
        signupBtn=findViewById(R.id.signup_button);

        //retrofit
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    TextWatcher passwordCheckWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            passwordRepeatCheckText.setVisibility(View.VISIBLE);

            if(loginPassword.getText().toString().equals(loginPasswordCheck.getText().toString())){
                passwordRepeatCheckText.setTextColor(Color.BLUE);
                passwordRepeatCheckText.setText("비밀번호 동일");
                passwordCheck=true;
            }
            else{
                passwordRepeatCheckText.setTextColor(Color.RED);
                passwordRepeatCheckText.setText("비밀번호가 다릅니다");
                passwordCheck=false;
            }
        }
    };
    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            passwordCheckText.setVisibility(View.VISIBLE);
            //정규식에 대한 최소한의 이해를 갖고 있자.  공부해야하는 부분중하나
            if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$",s)){
                passwordCheckText.setTextColor(Color.RED);
                passwordCheckText.setText("(영어 대소문자,특수문자,숫자)포함 9~12 비밀번호 입력하세요");
            }
            else{
                passwordCheckText.setTextColor(Color.BLUE);
                passwordCheckText.setText("사용가능한 비밀번호입니다.");

            }


        }
    };

    //닉네임 악질유저 방지
    View.OnFocusChangeListener nicknameFocus= new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //포커스 들어왔을 때,
            if(!hasFocus){
                Log.e("123","나감");
                if(!loginNickname.getText().toString().equals(verifyNickname)){
                    Log.e("123","나가면서 값다름");
                    nicknameCheckText.setTextColor(Color.RED);
                    nicknameCheckText.setText("닉네임 중복검사 해주세요");
                    nicknameDuplicate=false;
                }
            }

            else{
                Log.e("123","포커스들어옴");
                if(!loginNickname.getText().toString().equals(verifyNickname)){
                    Log.e("123","포커스들어옴 값 다름");
                    nicknameDuplicate=false;
                }
            }
        }
    };

    //회원가입 유효성 검사
    View.OnClickListener signup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            loginNickname.clearFocus();
            if(!verifyEmail.equals(loginEmail.getText().toString())){
                emailCheckText.setTextColor(Color.RED);
                emailCheckText.setText("이메일 변경됨 재인증 바람.");
                verifyEmail="###";
                verifyNumberCheckText.setTextColor(Color.RED);
                verifyNumberCheckText.setText("인증 재요구");
                return;
            }
            if(!loginPassword.getText().toString().equals(loginPasswordCheck.getText().toString())){
                passwordRepeatCheckText.setTextColor(Color.RED);
                passwordRepeatCheckText.setText("비밀번호 재확인");
                return;
            }

            //앞서서 인증받은 이메일 변경됫는지 확인 하고, 다음 항목들 검사하고, 전부 성공하면, 서버에 접속해서 DB에 회원정보 저장
            if(nicknameDuplicate&&emailDuplicate&&passwordCheck&&policyCheckBox1.isChecked()&&policyCheckBox2.isChecked()){
                RetrofitService  service = retrofit.create(RetrofitService.class);
                //여기서 비번 암호화해서 통신해야함 비번 암호화에 대해서 공부하고 옮기자

                String verifyPassword = loginPasswordCheck.getText().toString();

                Call<MemberSignup> call = service.sendMemberInfo(verifyEmail,getHash(verifyPassword),verifyNickname);
                call.enqueue(new Callback<MemberSignup>() {
                    @Override
                    public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

                        if(response.isSuccessful()&&response.body()!=null){

                            if(response.body().getMessage().equals("회원가입 성공")){
                                Toast.makeText(Activity_signup.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Activity_signup.this, Activity_main.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(Activity_signup.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<MemberSignup> call, Throwable t) {

                    }
                });



            }
            else{
                Toast.makeText(Activity_signup.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //닉네임 중복체크 리스너
    View.OnClickListener nicknameCheck =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(loginNickname.getText().toString().equals("")){
                Toast.makeText(Activity_signup.this, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            nicknameCheckText.setVisibility(View.VISIBLE);
            if(!Pattern.matches("^[A-Za-z0-9가-힣]{2,10}$",loginNickname.getText().toString())){
                nicknameCheckText.setTextColor(Color.RED);
                nicknameCheckText.setText("닉네임은 최소 2글자에서 10글자입니다");
                nicknameDuplicate=false;
                return;
            }
            Log.e("123","123");
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<MemberSignup> call = service.sendNickname("nicknameCheck.php", loginNickname.getText().toString());
            call.enqueue(new Callback<MemberSignup>() {
                @Override
                public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

                    if(response.isSuccessful()&&response.body()!=null){

                        if(response.body().getMessage().equals("닉네임 중복")){
                            nicknameCheckText.setText("닉네임 중복");
                            nicknameCheckText.setTextColor(Color.RED);
                        }
                        else{
                            nicknameCheckText.setText("닉네임 사용가능");
                            nicknameCheckText.setTextColor(Color.BLUE);
                            verifyNickname=loginNickname.getText().toString();
                            loginNickname.clearFocus();
                            nicknameDuplicate=true;
                        }
                    }
                }
                @Override
                public void onFailure(Call<MemberSignup> call, Throwable t) {


                }
            });
        }
    };

    View.OnClickListener verifyClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emailDuplicate=false;
            if(i<0){
                Toast.makeText(Activity_signup.this, "유효시간이 끝났습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(emailVerifyNumber.getText().toString().equals(verifynum)){
                verifyNumberCheckText.setVisibility(View.VISIBLE);
                verifyNumberCheckText.setTextColor(Color.BLUE);
                verifyNumberCheckText.setText("인증확인");
                verifyTimer.setVisibility(View.INVISIBLE);
                emailDuplicate=true;
                thread1.interrupt();
            }
            else{
                verifyNumberCheckText.setVisibility(View.VISIBLE);
                verifyNumberCheckText.setTextColor(Color.RED);
                verifyNumberCheckText.setText("인증실패");
            }

        }
    };
    //View onClickListener 따로 관리
    View.OnClickListener click= new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //아이디 확인에 따른 결과를 보여줘야하기 때문에
            if(emailCheckText.getVisibility()==View.GONE){
                emailCheckText.setVisibility(View.VISIBLE);
            }
            //이메일 유효한지,형식
            boolean checkMailForm=false;


            //이메일 형식을 확인하기위해 사용하는 부분
            Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
            String email = loginEmail.getText().toString();

            //여기서부터
            // STEP 1. 이메일이 유효한지 확인한다
            // STEP 2. 이메일이 유효하다면, 서버를 통해 아이디가 중복인지 체크하고,
            // 중복이 아니라면, 인증코드를 전달받는다
            // 만약 중복이라면, 중복이라고 알리기

            // STEP 3.

            //이메일이 유효하다면
            if(pattern.matcher(email).matches()){
                checkMailForm=true;
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<PostEmail> call = service.send(loginEmail.getText().toString());

                call.enqueue(new Callback<PostEmail>() {
                    @Override
                    public void onResponse(Call<PostEmail> call, Response<PostEmail> response) {
                        if(response.isSuccessful()&&response.body()!=null){
                            if(response.body().getIsSuccess().equals("이메일 전송 완료")){
                                Toast.makeText(Activity_signup.this, "이메일 전송", Toast.LENGTH_SHORT).show();
                                // emailVerifyNumber.setText(response.body().getVerifyNumber());
                                verifynum=response.body().getVerifyNumber();
                                emailCheckText.setTextColor(Color.BLUE);
                                emailCheckText.setText("사용가능한 이메일입니다.");
                                verifyEmail=loginEmail.getText().toString();
                                if(emailVerifyNumber.getVisibility()==View.GONE){
                                    emailVerifyNumber.setVisibility(View.VISIBLE);
                                }
                                if(verifyTimer.getVisibility()!=View.VISIBLE){
                                    verifyTimer.setVisibility(View.VISIBLE);
                                }
                                if(verifyNumberCheckBtn.getVisibility()==View.GONE){
                                    verifyNumberCheckBtn.setVisibility(View.VISIBLE);
                                }
                                if(isRunning){
                                    Log.e("123","여기들어옴");
                                    thread1.interrupt();
                                }
                                thread1=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        i=40;
                                        while (isRunning) {
                                            Message msg = new Message();
                                            msg.arg1=i;
                                            handler.sendMessage(msg);
                                            if(i<=0){
                                                isRunning=false;
                                            }
                                            try {
                                                Thread.sleep(1000);
                                                i-=1;
                                            } catch (Exception e) {
                                                Log.e("123","쓰레드 인터럽트");
                                                break;
                                            }

                                        }

                                    }
                                });
                                thread1.start();
                                isRunning=true;


                            }
                            else if(response.body().getIsSuccess().equals("아이디 중복")){
                                emailCheckText.setTextColor(Color.RED);
                                emailCheckText.setText("현재 이메일이 사용중입니다.");
                            }
                            else{
                                Toast.makeText(Activity_signup.this, "이메일 전송실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<PostEmail> call, Throwable t) {
                    }
                });
            }
            //이메일이 유효하지 않을 경우
            else{
                if(emailCheckText.getVisibility()==View.GONE){
                    emailCheckText.setVisibility(View.VISIBLE);
                }
                emailCheckText.setTextColor(Color.RED);
                emailCheckText.setText("이메일 형식을 확인하세요");
                return;
            }




            }

    };
    //해쉬함수 관련 함수
    public static String getHash(String str) {
        String digest = "";
        try{
            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte byteData[] = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            digest = sb.toString();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace(); digest = null;
        }
        return digest;
    }

}