package com.example.electronicsmarket;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class Activity_place_search_previous extends AppCompatActivity {



    RecyclerView locationRecyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter_place_search_previous adapter;
    ArrayList<DataSearch> locationSelectList;
    TextView searchText;
    ImageView searchImage;
    LinearLayout linearLayoutSearch;
    SharedPreferences  setLocationPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search_previous);

        variableInit();

        setLocationPreferences= getSharedPreferences("setLocation",MODE_PRIVATE);
        editor=setLocationPreferences.edit();
        editor.clear();
        editor.commit();

        linearLayoutSearch=findViewById(R.id.linearlayout_search);
        searchImage=findViewById(R.id.imageview_search);
        searchText=findViewById(R.id.search_place_keyword);
        searchText.setFocusable(false);


        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(Activity_place_search_previous.this,Activity_place_search.class);
                startActivity(intent);

            }
        });


        adapter.setPreviousListener(new Adapter_place_search_previous.Interface_previous_item_click() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void mainItemClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_place_search_previous.this);

                builder.setTitle("다음 장소를 선택하시겠습니까?");
                builder.setMessage(locationSelectList.get(position).getPlaceName());
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        dialog.dismiss();
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
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // setLocationPreferences= getSharedPreferences("setLocation",MODE_PRIVATE);
        if(!setLocationPreferences.getString("location_placeName","").equals("")){
            DataSearch data = new DataSearch();
            //여기서 지역 선택한 영역에 추가해서 보여주면 되는거고.
            data.setAddressName(setLocationPreferences.getString("location_addressName",""));
            data.setLatitude(setLocationPreferences.getString("location_latitude",""));
            data.setPlaceName( setLocationPreferences.getString("location_placeName",""));
            data.setLongitude(setLocationPreferences.getString("location_longitude",""));

            locationSelectList.add(0,data);
            adapter.setDataSearchPreviousList(locationSelectList);
            adapter.notifyDataSetChanged();
        }

    }

    public void variableInit(){

        locationRecyclerView=findViewById(R.id.recyclerview_previous_search);

        locationSelectList= new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        adapter=new Adapter_place_search_previous(locationSelectList,0);

        locationRecyclerView.setLayoutManager(linearLayoutManager);
        locationRecyclerView.setAdapter(adapter);
    }
    //    private ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//
//                    if(result.getResultCode()==RESULT_OK){
//
//                    }
//                }
//            }
//    );


}
