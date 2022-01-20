package com.example.electronicsmarket;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Activity_category_2 extends AppCompatActivity {

    String[] mobileString ={"스마트폰","태블릿","웨어러블(워치)","케이스/보호필름/악세서리"};
    String[] audioString={"이어폰/헤드폰","아이팟/MP3/PMP","비디오/프로젝터","기타"};
    String[] notebook= {"노트북/넷북","키보드/마우스","노트북 가방/악세서리","모니터"};
    Interface_category_listener listener;
    LinearLayoutManager linearLayoutManager;
    Adapter_post_category adapter;
    ArrayList<String> categoryList;
    RecyclerView categoryRecycelrView;
    LinearLayout mobile;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category2);

        //recyclerview 관련
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoryList = new ArrayList<>();
        adapter=new Adapter_post_category(categoryList);
        categoryRecycelrView=findViewById(R.id.recyclerView_category_2);

        categoryRecycelrView.setLayoutManager(linearLayoutManager);
        categoryRecycelrView.setAdapter(adapter);

        listener=new Interface_category_listener() {
            @Override
            public void onItemClick(Adapter_post_category.CategoryViewholder categoryViewholder, int Position) {

                Intent intent = new Intent(Activity_category_2.this,Activity_post_write.class);
                // shared 값 가져오기
                String category=sharedPreferences.getString("category","");
                category=category+" > "+ categoryList.get(Position);
                //shared 값 입력하기
                editor.putString("category",category);
                editor.commit();
                //호출하는 액티비티가 스택에 존재할 경우,그 액티비티 위에 쌓여있는것들은 사라지게 하면서, 재사용가능
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        };

        adapter.setCategoryListener(listener);

        sharedPreferences=getSharedPreferences("category",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent=getIntent();
        if(intent.getStringExtra("category").equals("0")){
            for(int i=0;i<mobileString.length;i++){
                categoryList.add(mobileString[i]);
            }
            adapter.setCategoryList(categoryList);
            adapter.notifyDataSetChanged();
        }
        else if(intent.getStringExtra("category").equals("1")){
            for(int i=0;i<audioString.length;i++){
                categoryList.add(audioString[i]);
            }
            adapter.setCategoryList(categoryList);
            adapter.notifyDataSetChanged();
        }
        else{
            for(int i=0;i<notebook.length;i++){
                categoryList.add(notebook[i]);
            }
            adapter.setCategoryList(categoryList);
            adapter.notifyDataSetChanged();


        }


//        mobile=findViewById(R.id.linear_smart_category);
//        mobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Activity_category_2.this,Activity_post_write.class);
//                // shared 값 가져오기
//
//                String category=sharedPreferences.getString("category","");
//                category=category+"/모바일";
//                //shared 값 입력하기
//                editor.putString("category",category);
//
//                editor.commit();
//                //호출하는 액티비티가 스택에 존재할 경우,그 액티비티 위에 쌓여있는것들은 사라지게 하면서, 재사용가능
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//
//
//            }
//        });

    }
}