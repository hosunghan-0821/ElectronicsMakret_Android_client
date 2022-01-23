package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_place_search extends AppCompatActivity {

    public final String API_KEY = "KakaoAK 508bceeac574adadb0a5337c161a375a";


    Adapter_place_search_result adapter;
    ArrayList<DataSearch> dataSearchList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView searchResultRecyclerview;
    Retrofit retrofit;
    ImageView searchImage;
    EditText placeSearchKeyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        variableInit();
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataSearchResult> call = service.sendPlace(API_KEY,placeSearchKeyword.getText().toString());
                call.enqueue(new Callback<DataSearchResult>() {
                    @Override
                    public void onResponse(Call<DataSearchResult> call, Response<DataSearchResult> response) {
                        dataSearchList.clear();
                        Log.e("123","성공");
                        DataSearchResult searchResult = response.body();
                        try{
                            for(int i=0; i<searchResult.getPlaceAllInfo().size();i++){

                                Log.e("123", searchResult.getPlaceAllInfo().get(i).getPlaceName());
                                Log.e("123", searchResult.getPlaceAllInfo().get(i).getRoadAddress());
                                Log.e("123", searchResult.getPlaceAllInfo().get(i).getAddressName());
                                Log.e("123",searchResult.getPlaceAllInfo().get(i).getLatitude());
                                Log.e("123",searchResult.getPlaceAllInfo().get(i).getLongitude());

                                dataSearchList.add(searchResult.getPlaceAllInfo().get(i));

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        adapter.setDataSearchList(dataSearchList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<DataSearchResult> call, Throwable t) {
                        Log.e("123","실패");
                    }
                });

            }
        });
    }

    public void variableInit(){

        searchResultRecyclerview=findViewById(R.id.recyclerview_search_result);

        linearLayoutManager=new LinearLayoutManager(this);
        dataSearchList=new ArrayList<>();

        placeSearchKeyword=findViewById(R.id.search_place_keyword);
        searchImage=findViewById(R.id.imageview_search);
        adapter= new Adapter_place_search_result(dataSearchList);

        searchResultRecyclerview.setLayoutManager(linearLayoutManager);
        searchResultRecyclerview.setAdapter(adapter);



        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}