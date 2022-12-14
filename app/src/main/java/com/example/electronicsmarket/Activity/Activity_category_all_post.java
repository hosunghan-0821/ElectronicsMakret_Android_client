package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.electronicsmarket.Adapter.Adapter_category_info;
import com.example.electronicsmarket.Dto.DataCategory;
import com.example.electronicsmarket.R;

import java.util.ArrayList;

public class Activity_category_all_post extends AppCompatActivity {


    private ImageView backImage;
    private Adapter_category_info adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<DataCategory> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_all_post);
        variableInit();
    }

    public void variableInit(){


      backImage=(ImageView)findViewById(R.id.category_choice_back_arrow);
      backImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });
        //recyclerview 관련선언 코드들들
        categoryList=new ArrayList<DataCategory>();
        recyclerView=findViewById(R.id.category_choice_recyclerview);
        gridLayoutManager= new GridLayoutManager(Activity_category_all_post.this,3);
        addCategoryInfo();

        adapter=new Adapter_category_info(categoryList);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new Adapter_category_info.Interface_category_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(Activity_category_all_post.this,Activity_category_search_result.class);
                intent.putExtra("category",categoryList.get(position).getCategoryText());
                intent.putExtra("categorySend",categoryList.get(position).getCategorySendText());
                startActivity(intent);
            }
        });



    }

    public void addCategoryInfo(){
        categoryList.add(new DataCategory("스마트폰",R.drawable.category_smartphone,"스마트폰"));
        categoryList.add(new DataCategory("태블릿",R.drawable.category_tablet,"태블릿"));
        categoryList.add(new DataCategory("웨어러블\n워치",R.drawable.category_watch,"웨어러블"));
        categoryList.add(new DataCategory("이어폰/\n헤드폰",R.drawable.category_headphone,"이어폰/헤드폰"));
        categoryList.add(new DataCategory("비디오/\n프로젝터",R.drawable.category_video,"비디오/프로젝터"));
        categoryList.add(new DataCategory("키보드/\n마우스",R.drawable.category_keyboard,"키보드/마우스"));
        categoryList.add(new DataCategory("모니터",R.drawable.category_monitor,"모니터"));
        categoryList.add(new DataCategory("전자기기/\n기타용품",R.drawable.category_extra,"기타용품"));

    }
}