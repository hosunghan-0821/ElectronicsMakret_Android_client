package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Activity_search_all_post extends AppCompatActivity {

    private EditText searchKeyword;
    private ImageView searchImage,backImage;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> prevSearchKeywordList;
    private Adapter_prev_search_keyword adapter;
    private SharedPreferences sharedPreferences;
    private TextView deleteAllPrevKeyword,noResultPrevKeywordText;

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
                    searchKeyword.setText(searchKeyword.getText().toString().replace(" ",""));
                    intent.putExtra("keyword",searchKeyword.getText().toString());

                    Boolean isDuplicateKeyword=false;
                    for(int i=0;i<prevSearchKeywordList.size();i++){
                        if(prevSearchKeywordList.get(i).equals(searchKeyword.getText().toString())){
                            prevSearchKeywordList.remove(i);
                            prevSearchKeywordList.add(0,searchKeyword.getText().toString());
                            isDuplicateKeyword=true;
                            break;
                        }
                    }
                    if(!isDuplicateKeyword){
                        prevSearchKeywordList.add(0,searchKeyword.getText().toString());
                    }


                    setSharedPrevKeywordList();

                    startActivity(intent);
                    searchKeyword.clearFocus();
                }
                else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteAllPrevKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prevSearchKeywordList.size()!=0){
                    prevSearchKeywordList.clear();
                    if(prevSearchKeywordList.size()==0){
                        noResultPrevKeywordText.setVisibility(View.VISIBLE);
                    }
                    else{
                        noResultPrevKeywordText.setVisibility(View.INVISIBLE);
                    }
                    setSharedPrevKeywordList();
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }


    public void variableInit(){

        //쉐어드 관련 선언.
        sharedPreferences=getSharedPreferences("prevKeyword",MODE_PRIVATE);
        prevSearchKeywordList=new ArrayList<>();
        getSharedPrevKeywordList();

        //리사이클러뷰 관련
        linearLayoutManager=new LinearLayoutManager(Activity_search_all_post.this);

        recyclerView=(RecyclerView)findViewById(R.id.search_post_prev_recyclerview);
        adapter=new Adapter_prev_search_keyword(prevSearchKeywordList, new Adapter_prev_search_keyword.Interface_prev_keyword_click() {
            @Override
            public void onItemClick(int position) {
                //여기서 아이템 클릭하면 검색화면으로 이동
                Intent intent =new Intent(Activity_search_all_post.this,Activity_category_search_result.class);
                String keyword=prevSearchKeywordList.get(position);
                intent.putExtra("keyword", keyword);

                prevSearchKeywordList.remove(position);
                prevSearchKeywordList.add(0,keyword);
                setSharedPrevKeywordList();
                startActivity(intent);

                searchKeyword.clearFocus();

            }

            @Override
            public void onCancelClick(int position) {
                // 아이템 지우면 shared 에 접근해서 지우기
                prevSearchKeywordList.remove(position);
                if(prevSearchKeywordList.size()==0){
                    noResultPrevKeywordText.setVisibility(View.VISIBLE);
                }
                else{
                    noResultPrevKeywordText.setVisibility(View.INVISIBLE);
                }
                adapter.notifyItemRemoved(position);
                setSharedPrevKeywordList();
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //ImageView
        backImage=(ImageView) findViewById(R.id.search_post_back_arrow);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //TextView
        noResultPrevKeywordText=(TextView) findViewById(R.id.search_post_prev_keyword_text);
        deleteAllPrevKeyword=(TextView)findViewById(R.id.search_post_delete_prev_keyword);

        //검색 관련
        searchKeyword=findViewById(R.id.search_post_keyword);
        searchImage=findViewById(R.id.search_post_search_image);
    }

    public void getSharedPrevKeywordList(){

        String stringToArray= sharedPreferences.getString("prevKeyword",null);
        Gson gson = new GsonBuilder().create();

        Type arrayListType = new TypeToken<ArrayList<String>>(){}.getType();
        try{
            prevSearchKeywordList=gson.fromJson(stringToArray,arrayListType);
            if(prevSearchKeywordList==null){
                prevSearchKeywordList=new ArrayList<String>();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSharedPrevKeywordList(){
        Gson gson = new Gson();
        Type arrayListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        String stringToJson = gson.toJson(prevSearchKeywordList, arrayListType);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("prevKeyword",stringToJson);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPrevKeywordList();
        if(prevSearchKeywordList.size()==0){
            noResultPrevKeywordText.setVisibility(View.VISIBLE);
        }
        else{
            noResultPrevKeywordText.setVisibility(View.INVISIBLE);
        }
        adapter.setPrevKewordList(prevSearchKeywordList);
        adapter.notifyDataSetChanged();


    }
}