package com.example.electronicsmarket.Activity;


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

import com.example.electronicsmarket.Adapter.Adapter_place_search_previous;
import com.example.electronicsmarket.Dto.DataSearch;
import com.example.electronicsmarket.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Activity_place_search_previous extends AppCompatActivity {




    RecyclerView locationRecyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter_place_search_previous adapter;
    ArrayList<DataSearch> locationSelectList;
    TextView searchText;
    ImageView searchImage,backArrow;
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

        //onCreate 할 떄, 이전에 저장했던 거래장소가 있으면 가져와서 adapter에 찍어서 보여주기.
        SharedPreferences sharedPreferences= getSharedPreferences("locationSelectList",MODE_PRIVATE);
        Gson gson=new GsonBuilder().create();
        String stringToObject = sharedPreferences.getString("locationSelectList", "");
        Type arraylistType = new TypeToken<ArrayList<DataSearch>>() {
        }.getType();
        try {
            locationSelectList = gson.fromJson(stringToObject, arraylistType);

            if (locationSelectList == null) {
                locationSelectList = new ArrayList<DataSearch>();
            }

        } catch (Exception e) {
            e.printStackTrace();
            locationSelectList = new ArrayList<DataSearch>();
        }
        adapter.setDataSearchPreviousList(locationSelectList);
        adapter.notifyDataSetChanged();

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

                locationSelectList.remove(position);
                adapter.setDataSearchPreviousList(locationSelectList);
                adapter.notifyItemRemoved(position);

                SharedPreferences sharedPreferences= getSharedPreferences("locationSelectList",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new GsonBuilder().create();
                Type arraylistType = new TypeToken<ArrayList<DataSearch>>() {                       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
                }.getType();
                String objectToString = gson.toJson(locationSelectList, arraylistType);
                editor.putString("locationSelectList", objectToString);
                editor.commit();

            }

            @Override
            public void mainItemClick(Adapter_place_search_previous.PreviousViewHolder viewHolder, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_place_search_previous.this);


                builder.setTitle("거래 장소 선택");
                builder.setMessage("다음 거래 장소를 선택합니다.\n"+locationSelectList.get(position).getPlaceName());
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent =new Intent();

                        intent.putExtra("location_placeName",locationSelectList.get(position).getPlaceName());
                        intent.putExtra("location_addressName",locationSelectList.get(position).getAddressName());
                        intent.putExtra("location_latitude",locationSelectList.get(position).getLatitude());
                        intent.putExtra("location_longitude",locationSelectList.get(position).getLongitude());
                        setResult(RESULT_OK,intent);


                        //shared 값 입력하기
                        SharedPreferences sharedPreferences= getSharedPreferences("locationSelectList",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        Gson gson = new GsonBuilder().create();

                        Type arraylistType = new TypeToken<ArrayList<DataSearch>>() {                       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
                        }.getType();
                        String objectToString = gson.toJson(locationSelectList, arraylistType);
                        editor.putString("locationSelectList", objectToString);
                        editor.commit();
                        finish();
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


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // setLocationPreferences= getSharedPreferences("setLocation",MODE_PRIVATE);
        if(!setLocationPreferences.getString("location_placeName","").equals("")){


            boolean checkDuplicate=false;
            DataSearch data = new DataSearch();
            //여기서 지역 선택한 영역에 추가해서 보여주면 되는거고.
            data.setAddressName(setLocationPreferences.getString("location_addressName",""));
            data.setLatitude(setLocationPreferences.getString("location_latitude",""));
            data.setPlaceName( setLocationPreferences.getString("location_placeName",""));
            data.setLongitude(setLocationPreferences.getString("location_longitude",""));

            int pos=0;
            for(int i=0;i<locationSelectList.size();i++){
                if(data.getPlaceName().equals(locationSelectList.get(i).getPlaceName())){
                    checkDuplicate=true;
                    pos=i;
                    break;
                }
            }
            if(!checkDuplicate){
                locationSelectList.add(0,data);
                adapter.setDataSearchPreviousList(locationSelectList);
                adapter.notifyDataSetChanged();
            }
            else{
                locationSelectList.add(0,data);
                locationSelectList.remove(pos+1);
                adapter.notifyDataSetChanged();
            }

        }

    }

    public void variableInit(){

        locationRecyclerView=findViewById(R.id.recyclerview_previous_search);
        backArrow=findViewById(R.id.place_search_previous_back_arrow);
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
