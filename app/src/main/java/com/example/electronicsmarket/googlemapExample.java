package com.example.electronicsmarket;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class googlemapExample extends AppCompatActivity {


    public final String API_KEY = "KakaoAK 508bceeac574adadb0a5337c161a375a";
    TextView searchText;
    ImageView searchImage;
    Retrofit retrofit;
    LinearLayout linearLayoutSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap_example);


        linearLayoutSearch=findViewById(R.id.linearlayout_search);
        searchImage=findViewById(R.id.imageview_search);
        searchText=findViewById(R.id.search_place_keyword);
        searchText.setFocusable(false);


        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(googlemapExample.this,Activity_place_search.class);

                searchLauncher.launch(intent);
            }
        });


        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

//        searchImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                RetrofitService service = retrofit.create(RetrofitService.class);
//                Call<DataSearchResult> call = service.sendPlace(API_KEY,  searchText.getText().toString());
//                call.enqueue(new Callback<DataSearchResult>() {
//                    @Override
//                    public void onResponse(Call<DataSearchResult> call, Response<DataSearchResult> response) {
//                        Log.e("123","성공");
//                        DataSearchResult searchResult = response.body();
//
//                        for(int i=0; i<10;i++){
//                            Log.e("123", searchResult.getPlaceAllInfo().get(i).getPlaceName());
//                            Log.e("123", searchResult.getPlaceAllInfo().get(i).getRoadAddress());
//                            Log.e("123", searchResult.getPlaceAllInfo().get(i).getAddressName());
//                            Log.e("123",searchResult.getPlaceAllInfo().get(i).getLatitude());
//                            Log.e("123",searchResult.getPlaceAllInfo().get(i).getLongitude());
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<DataSearchResult> call, Throwable t) {
//                        Log.e("123","실패");
//                    }
//                });
//            }
//        });

    }

    private ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode()==RESULT_OK){

                    }
                }
            }
    );


}
