package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Activity_category_1 extends AppCompatActivity {

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    Intent intent;
    LinearLayout linearPhone,linearAudio,linearNotebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category1);

        linearPhone=findViewById(R.id.linear_phone_category);
        linearAudio=findViewById(R.id.linear_audio_category);
        linearNotebook=findViewById(R.id.linear_notebook_category);

        sharedPreferences= getSharedPreferences("category",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        linearPhone.setOnClickListener(click);
        linearAudio.setOnClickListener(click);
        linearNotebook.setOnClickListener(click);

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.linear_phone_category:
                    intent =new Intent(Activity_category_1.this,Activity_category_2.class);
                    intent.putExtra("category","0");
                    editor.putString("category","모바일기기");
                    editor.commit();

                    startActivity(intent);

                    break;

                case R.id.linear_audio_category :
                    intent =new Intent(Activity_category_1.this,Activity_category_2.class);
                    intent.putExtra("category","1");
                    editor.putString("category","오디오/영상기기");
                    editor.commit();
                    startActivity(intent);

                    break;

                case R.id.linear_notebook_category:
                    intent =new Intent(Activity_category_1.this,Activity_category_2.class);
                    intent.putExtra("category","2");
                    editor.putString("category","노트북/주변기기");
                    editor.commit();
                    startActivity(intent);

                    break;
              default:
                break;
            }

        }
    };
}