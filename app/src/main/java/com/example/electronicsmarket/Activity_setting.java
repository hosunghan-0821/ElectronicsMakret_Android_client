package com.example.electronicsmarket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Activity_setting extends AppCompatActivity {

    ImageView backImage;
    Button logoutBtn,memberOutBtn,passwordChangeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logoutBtn=findViewById(R.id.logout_button);
        backImage=findViewById(R.id.back_arrow);
        memberOutBtn=findViewById(R.id.memberout_button);
        passwordChangeBtn=findViewById(R.id.password_change_button);

        passwordChangeBtn.setOnClickListener(changePassword);
        memberOutBtn.setOnClickListener(memberOut);
        backImage.setOnClickListener(back);
        logoutBtn.setOnClickListener(logout);


    }
    View.OnClickListener changePassword=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Activity_setting.this,Activity_change_password.class);
            startActivity(intent);
        }
    };

    View.OnClickListener memberOut= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Activity_setting.this, Activity_member_out.class);
            startActivity(intent);

        }
    };
    View.OnClickListener back = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Activity_setting.this, Activity_main_home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("fragmentNum","3");
            startActivity(intent);
        }
    };

    View.OnClickListener logout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder =new AlertDialog.Builder(Activity_setting.this);

            builder.setTitle("로그아웃 하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences= getSharedPreferences("autoLogin",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    dialog.dismiss();
                    Intent intent =new Intent(Activity_setting.this, Activity_main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
}