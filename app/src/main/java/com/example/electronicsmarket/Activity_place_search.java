package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_place_search extends AppCompatActivity implements OnMapReadyCallback {

    public final String API_KEY = "KakaoAK 508bceeac574adadb0a5337c161a375a";


    private Marker marker;
    private double latitude,longitude;
    private GoogleMap mMap;
    private Adapter_place_search_previous previousAdapter;
    private InputMethodManager imm;
    private Interface_search_result_listener resultListener;
    private Adapter_place_search_result adapter;
    private ArrayList<DataSearch> dataSearchList;
    private LinearLayoutManager linearLayoutManager,previousLayoutManager;
    private RecyclerView searchResultRecyclerview,searchPreviousRecyclerview;
    private Retrofit retrofit;
    private ImageView searchImage,backImage;
    private EditText placeSearchKeyword;
    private FragmentManager fm;
    private Button placeSelectBtn;
    private SupportMapFragment mapFragment;
    private SharedPreferences lastLocationPreferences,previousPreferences,setLocationPreferences;
    private ArrayList<DataSearch> previousArrayList;
    private TextView searchResultText;
    private String markerTitle;
    private String snippet;
    private SharedPreferences.Editor setLocationEditor;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        variableInit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //shared 관련 변수들
        lastLocationPreferences= getSharedPreferences("lastLocation",MODE_PRIVATE);
        previousPreferences = getSharedPreferences("previousLocation",MODE_PRIVATE);
        setLocationPreferences= getSharedPreferences("setLocation",MODE_PRIVATE);

        setLocationEditor = setLocationPreferences.edit();

        previousArrayList=getPreviousArrayList();
        previousAdapter.setDataSearchPreviousList(previousArrayList);
        previousAdapter.notifyDataSetChanged();

        //여기까지 shared에 저장된 정보를 갖고 왔으니깐 이제 recyclerview 띄어야함

        //구글 맵 띄우기
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         fm = getSupportFragmentManager();


         markerTitle=lastLocationPreferences.getString("placeName","");
         snippet=lastLocationPreferences.getString("placeAddress","");
         try{
             latitude=Double.parseDouble( lastLocationPreferences.getString("latitude","37.484793511299706"));
             longitude=Double.parseDouble( lastLocationPreferences.getString("longitude","126.97091020065321"));
         }catch (Exception e){
              latitude=  37.484793511299706;
              longitude=126.97091020065321;
         }

         mapFragment.getMapAsync(this);
//        FragmentTransaction ft;
//        ft = fm.beginTransaction();
//        ft.hide(mapFragment).commit();



        placeSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count<2){
                    searchResultText.setText("최근 검색장소");
                    searchPreviousRecyclerview.setVisibility(View.VISIBLE);
                    searchResultRecyclerview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        placeSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLocationEditor.commit();

                DataSearch data = new DataSearch();

                SharedPreferences sharedPreferences= getSharedPreferences("setLocation",MODE_PRIVATE);
                data.setAddressName(sharedPreferences.getString("location_addressName",""));
                data.setLatitude(sharedPreferences.getString("location_latitude",""));
                data.setPlaceName( sharedPreferences.getString("location_placeName",""));
                data.setLongitude( sharedPreferences.getString("location_longitude",""));

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_place_search.this);

                builder.setTitle("Used Electroncis 장소 선택");
                builder.setMessage("다음 장소를 선택하시겠습니까? \n"+ data.getPlaceName());
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor editor=lastLocationPreferences.edit();

                        //사용자가 선택한 데이터 > 최근 선택한 정보로 shared 에 저장
                        editor.putString("latitude",data.getLatitude());
                        editor.putString("longitude",data.getLongitude());
                        editor.putString("placeName",data.getPlaceName());
                        editor.putString("placeAddress",data.getAddressName());

                        editor.commit();
                        finish();

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


        //검색 아이템 클릭시 interface 연결
        resultListener=new Interface_search_result_listener() {
            @Override
            public void onItemClick(Adapter_place_search_result.SearchViewHolder viewHolder, int position) {

                //shared 값 입력하기
                setLocationEditor.putString("location_placeName",dataSearchList.get(position).getPlaceName());
                setLocationEditor.putString("location_addressName",dataSearchList.get(position).getAddressName());
                setLocationEditor.putString("location_latitude",dataSearchList.get(position).getLatitude());
                setLocationEditor.putString("location_longitude",dataSearchList.get(position).getLongitude());

                latitude=Double.parseDouble(dataSearchList.get(position).getLatitude());
                longitude=Double.parseDouble(dataSearchList.get(position).getLongitude());

                placeSearchKeyword.setText(dataSearchList.get(position).getPlaceName());
                placeSearchKeyword.clearFocus();

                mapFragment.getMapAsync(Activity_place_search.this::onMapReady);
                markerTitle=dataSearchList.get(position).getPlaceName();
                snippet=dataSearchList.get(position).getAddressName();

                SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
                previousAdapter.setmSelectedItems(mSelectedItems);
                previousAdapter.notifyDataSetChanged();

                boolean checkDuplicate=false;


                int pos=0;
                for(int i=0;i<previousArrayList.size();i++){
                    if(dataSearchList.get(position).getPlaceName().equals(previousArrayList.get(i).getPlaceName())){
                        checkDuplicate=true;
                        pos=i;
                        break;
                    }
                }
                if(!checkDuplicate){
                    previousArrayList.add(0,dataSearchList.get(position));
                    setPreviousArrayList(previousArrayList);
                }
                else{
                    previousArrayList.add(0,dataSearchList.get(position));
                    previousArrayList.remove(pos+1);
                    setPreviousArrayList(previousArrayList);
                }
            }
        };
        adapter.setListener(resultListener);

        previousAdapter.setPreviousListener(new Adapter_place_search_previous.Interface_previous_item_click() {
            @Override
            public void onItemClick(int position) {
                previousArrayList.remove(position);
                previousAdapter.notifyDataSetChanged();
                previousAdapter.setDataSearchPreviousList(previousArrayList);
                setPreviousArrayList(previousArrayList);
            }

            @Override
            public void mainItemClick(Adapter_place_search_previous.PreviousViewHolder viewHolder, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_place_search.this);

//                viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
//                view

                setLocationEditor.putString("location_placeName",previousArrayList.get(position).getPlaceName());
                setLocationEditor.putString("location_addressName",previousArrayList.get(position).getAddressName());
                setLocationEditor.putString("location_latitude",previousArrayList.get(position).getLatitude());
                setLocationEditor.putString("location_longitude",previousArrayList.get(position).getLongitude());

                latitude=Double.parseDouble(previousArrayList.get(position).getLatitude());
                longitude=Double.parseDouble(previousArrayList.get(position).getLongitude());

                placeSearchKeyword.setText(previousArrayList.get(position).getPlaceName());
                placeSearchKeyword.clearFocus();


                mapFragment.getMapAsync(Activity_place_search.this::onMapReady);
                markerTitle=previousArrayList.get(position).getPlaceName();
                snippet=previousArrayList.get(position).getAddressName();

            }
        });


        //검색 버튼 누르기
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultRecyclerview.setVisibility(View.VISIBLE);
                searchPreviousRecyclerview.setVisibility(View.INVISIBLE);
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataSearchResult> call = service.sendPlace(API_KEY,placeSearchKeyword.getText().toString());
                call.enqueue(new Callback<DataSearchResult>() {
                    @Override
                    public void onResponse(Call<DataSearchResult> call, Response<DataSearchResult> response) {
                        dataSearchList.clear();
                        placeSearchKeyword.clearFocus();
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

                        SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
                        searchResultText.setText("검색결과");
                        adapter.setmSelectedItems(mSelectedItems);
                        adapter.setDataSearchList(dataSearchList);
                        adapter.notifyDataSetChanged();
                        imm.hideSoftInputFromWindow(placeSearchKeyword.getWindowToken(), 0);
                    }

                    @Override
                    public void onFailure(Call<DataSearchResult> call, Throwable t) {
                        Log.e("123","실패");
                    }
                });

            }
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void variableInit(){

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        searchResultText=findViewById(R.id.search_result_text);
        backImage=findViewById(R.id.search_back_arrow);
        placeSelectBtn=findViewById(R.id.search_result_ok_button);

        searchResultRecyclerview=findViewById(R.id.recyclerview_search_result);
        searchPreviousRecyclerview=findViewById(R.id.recyclerview_previous_search_1);

        //리니어레이아웃매니저 , arrayList 초기화
        linearLayoutManager=new LinearLayoutManager(this);
        previousLayoutManager=new LinearLayoutManager(this);
        previousArrayList = new ArrayList<>();
        dataSearchList=new ArrayList<>();


        placeSearchKeyword=findViewById(R.id.search_place_keyword);
        searchImage=findViewById(R.id.imageview_search);

        previousAdapter=new Adapter_place_search_previous(previousArrayList,1);
        adapter= new Adapter_place_search_result(dataSearchList);

        searchPreviousRecyclerview.setLayoutManager(previousLayoutManager);
        searchPreviousRecyclerview.setAdapter(previousAdapter);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(markerTitle);
        markerOptions.snippet(snippet);
        markerOptions.position(location);
        markerOptions.getInfoWindowAnchorU();

        if(marker!=null){
            marker.remove();
        }
        marker=mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17));

    }

    public void setPreviousArrayList(ArrayList<DataSearch> previousArrayList){

        Gson gson = new GsonBuilder().create();
        Type arraylistType = new TypeToken<ArrayList<DataSearch>>() {       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
        }.getType();

        String objectToString = gson.toJson(previousArrayList, arraylistType);

        SharedPreferences.Editor editor = previousPreferences.edit();
        editor.putString("previousArrayList", objectToString);
        editor.apply();

    }

    public ArrayList<DataSearch> getPreviousArrayList(){

        //gson 을 활용하여서 shared에 저장된 string을 object로 변환
        Gson gson=new GsonBuilder().create();

        ArrayList<DataSearch> previousArrayList;
        String stringToObject = previousPreferences.getString("previousArrayList", "");
        Type arraylistType = new TypeToken<ArrayList<DataSearch>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();
        try{
            previousArrayList=gson.fromJson(stringToObject,arraylistType);
            if(previousArrayList==null){
                previousArrayList= new ArrayList<DataSearch>();
            }
            return previousArrayList;
        }catch (Exception e){
            e.printStackTrace();
            return previousArrayList=new ArrayList<>();
        }

    }
}