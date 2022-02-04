package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity_search_all_post extends AppCompatActivity {

    private EditText searchKeyword;
    private ImageView searchImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_post);

        variableInit();
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchKeyword.getText().toString().equals("")){
                    Intent intent =new Intent(Activity_search_all_post.this,Activity_category_search_result.class);
                    intent.putExtra("keyword",searchKeyword.getText().toString());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void variableInit(){

        searchKeyword=findViewById(R.id.search_post_keyword);
        searchImage=findViewById(R.id.search_post_search_image);
    }
}