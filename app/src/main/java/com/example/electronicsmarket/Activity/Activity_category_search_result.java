package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicsmarket.Adapter.Adapter_post_all_info;
import com.example.electronicsmarket.Dto.PostAllInfo;
import com.example.electronicsmarket.Dto.PostInfo;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_category_search_result extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private ImageView backImage,postSearchImage;
    private EditText searchKeyword;
    private Button filterResetBtn;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_all_info adapter;
    private ArrayList<PostInfo> postInfoList, originPostInfoList;
    private LinearLayout linearSearchKeyword;
    private TextView filterText, categoryText;
    private FrameLayout filterFrame, priceFrame;
    private Retrofit retrofit;
    private TextView newestText, highPriceText, lowPriceText, viewText, priceFilterText,noResultText;
    private TextWatcher textWatcher;
    private String strAmount = "";
    private int maxPrice=-1, minPrice=-1, filterNum = 0;
    private ArrayList<String> prevSearchKeywordList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search_result);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        variableInit();

        //받는 intent의 값에 따라 로직 다르게 가야함
        Intent intent = getIntent();


        //화면에 들어 왔을 떄, 어떤 목적으로 왔는지 case 나누기!
        if (intent.getStringExtra("category") != null) {
            //2. category 선택의 결과일 경우
            Log.e("123", "카테고리선택");
            categoryText.setVisibility(View.VISIBLE);
            String str = intent.getStringExtra("category");
            str = str.replace("\n", "");
            categoryText.setText(str);

            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<PostAllInfo> call = service.getFilterPostAllInfo(intent.getStringExtra("categorySend"), null);
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        PostAllInfo postAllInfo = response.body();


                        postInfoList = postAllInfo.postInfo;
                        for(int i=0;i<postInfoList.size();i++){
                            originPostInfoList.add(postInfoList.get(i));
                        }
                        adapter.setPostList(postInfoList);
                        isSearchNoResult();
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onFailure(Call<PostAllInfo> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "통신실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 1. search의 결과일 경우
            // Log.e("123", "검색결과");
            linearSearchKeyword.setVisibility(View.VISIBLE);
            searchKeyword.setText(intent.getStringExtra("keyword"));
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<PostAllInfo> call = service.getFilterPostAllInfo(null, intent.getStringExtra("keyword"));
            call.enqueue(new Callback<PostAllInfo>() {
                @Override
                public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                    if(response.isSuccessful() &&response.body()!=null){

                        PostAllInfo postAllInfo = response.body();
                        postInfoList=postAllInfo.postInfo;

                        for(int i=0;i<postInfoList.size();i++){
                            originPostInfoList.add(postInfoList.get(i));
                        }
                        adapter.setPostList(postInfoList);
                        isSearchNoResult();
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<PostAllInfo> call, Throwable t) {

                }
            });

        }

        //검색으로 들어왔을 경우 검색버튼을 누르면, 해당 필터에 맞춰서, 검색정렬해야함.
        postSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKeyword.clearFocus();
                InputMethodManager imm;
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchKeyword.getWindowToken(), 0);

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<PostAllInfo> call = service.getFilterPostAllInfo(null,searchKeyword.getText().toString());
                call.enqueue(new Callback<PostAllInfo>() {
                    @Override
                    public void onResponse(Call<PostAllInfo> call, Response<PostAllInfo> response) {

                        if(response.isSuccessful()&&response.body()!=null){

                            //최근 검색관련 정리
                            boolean isDuplicate =false;
                            getSharedPrevKeywordList();
                            for(int i=0;i<prevSearchKeywordList.size();i++){
                                if(prevSearchKeywordList.get(i).equals(searchKeyword.getText().toString())){
                                    prevSearchKeywordList.remove(i);
                                    prevSearchKeywordList.add(0,searchKeyword.getText().toString());
                                    isDuplicate=true;
                                    break;
                                }
                            }
                            if(!isDuplicate){
                                prevSearchKeywordList.add(0,searchKeyword.getText().toString());
                            }

                            setSharedPrevKeywordList();


                            //검색 결과에 따른 필터에 맞게끔 행동하도록 함수들 작성
                            postInfoList.clear();
                            originPostInfoList.clear();
                            PostAllInfo postAllInfo = response.body();
                            postInfoList=postAllInfo.postInfo;
                            originPostInfoList.addAll(postInfoList);

                            postInfoList=getSortedPostList();
                            postInfoList=getPriceSortedPostList(postInfoList);
                            adapter.setPostList(postInfoList);

                            isSearchNoResult();
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(Call<PostAllInfo> call, Throwable t) {

                    }
                });
            }
        });


        //filter 초기화 버튼.
        filterResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNum=0;
                maxPrice=-1;
                minPrice=-1;
                filterText.setText("최신순");
                priceFilterText.setText("가격 범위설정");
                postInfoList.clear();
                postInfoList.addAll(originPostInfoList);
                adapter.setPostList(postInfoList);
                isSearchNoResult();
                adapter.notifyDataSetChanged();

            }
        });

        //가격 범위 설정 코드
        priceFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_price_filter, null, false);
                DialogBuilder.setView(dialogView);
                EditText minPriceText, maxPriceText;

                minPriceText = dialogView.findViewById(R.id.dialog_price_filter_min_price);
                maxPriceText = dialogView.findViewById(R.id.dialog_price_filter_max_price);

                if(minPrice!=-1){
                    minPriceText.setText(makeStringComma(String.valueOf(minPrice)));
                }
                if(maxPrice!=-1){
                    maxPriceText.setText(makeStringComma(String.valueOf(maxPrice)));
                }
                //최소값 1000단위 콤마 박기
                minPriceText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(strAmount)) {
                            strAmount = makeStringComma(s.toString().replace(",", ""));
                            minPriceText.setText(strAmount);
                            Editable editable = minPriceText.getText();
                            Selection.setSelection(editable, strAmount.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                minPriceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            strAmount = "";
                        }
                    }
                });
                //최대값 1000단위 콤마 박기
                maxPriceText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(strAmount)) {
                            strAmount = makeStringComma(s.toString().replace(",", ""));
                            maxPriceText.setText(strAmount);
                            Editable editable = maxPriceText.getText();
                            Selection.setSelection(editable, strAmount.length());
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                maxPriceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            strAmount = "";
                        }
                    }
                });


                DialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ((!maxPriceText.getText().toString().equals("")) && (!minPriceText.getText().toString().equals(""))) {
                            if (Integer.parseInt(maxPriceText.getText().toString().replace(",", "")) < Integer.parseInt(minPriceText.getText().toString().replace(",", ""))) {
                                Toast.makeText(getApplicationContext(), "가격범위를 재확인 하세요", Toast.LENGTH_SHORT).show();
                            }
                            //가격범위를 제대로 채워놨을 떄,
                            else {
                                minPrice = Integer.parseInt(minPriceText.getText().toString().replace(",", ""));
                                maxPrice = Integer.parseInt(maxPriceText.getText().toString().replace(",", ""));

                                //가격 정하기 전에, sorting 된 arraylist 가져오기
                                Log.e("123","sortedPostList"+postInfoList.toString());

                                postInfoList=getSortedPostList();

                                for (int i = 0; i < postInfoList.size(); i++) {
                                    int postPrice = Integer.parseInt(postInfoList.get(i).getPostPrice().replace(",", ""));
                                    if (!(postPrice <= maxPrice && postPrice >= minPrice)) {
                                        postInfoList.remove(i);
                                        i--;
                                    } else {
//                                        Log.e("123","maxPrice : "+ String.valueOf(maxPrice));
//                                        Log.e("123","minPrice : "+ String.valueOf(minPrice));
//                                        Log.e("123",String.valueOf(postPrice));
                                    }
                                }

//                                for(int i=0;i<postInfoList.size();i++){
//                                    Log.e("123","postInfoList : "+postInfoList.get(i).getPostPrice());
//                                }
//                                for(int i=0;i<originPostInfoList.size();i++){
//                                    Log.e("123","originPostInfoList : "+originPostInfoList.get(i).getPostPrice());
//                                }
                                adapter.setPostList(postInfoList);
                                isSearchNoResult();
                                adapter.notifyDataSetChanged();
                                priceFilterText.setText(minPriceText.getText().toString() + " ~ " + maxPriceText.getText().toString() + "원");
                            }
                        }
                        //최대값을 비워놨을 때,
                        else if (maxPriceText.getText().toString().equals("")) {
                            minPrice = Integer.parseInt(minPriceText.getText().toString().replace(",", ""));
                            maxPrice = -1;

                            postInfoList=getSortedPostList();

                            for (int i = 0; i < postInfoList.size(); i++) {

                                int postPrice = Integer.parseInt(postInfoList.get(i).getPostPrice().replace(",", ""));
                                if (!(postPrice >= minPrice)) {
                                    postInfoList.remove(i);
                                    i--;
                                }

                            }
                            adapter.setPostList(postInfoList);
                            isSearchNoResult();
                            adapter.notifyDataSetChanged();
                            priceFilterText.setText(minPriceText.getText().toString() + "원 이상");
                        }
                        //최소값을 비워놨을 떄,
                        else if (minPriceText.getText().toString().equals("")) {

                            minPrice = -1;
                            maxPrice = Integer.parseInt(maxPriceText.getText().toString().replace(",", ""));
                            postInfoList=getSortedPostList();

                            for (int i = 0; i < postInfoList.size(); i++) {
                                int postPrice = Integer.parseInt(postInfoList.get(i).getPostPrice().replace(",", ""));
                                if (!(postPrice <= maxPrice)) {
                                    postInfoList.remove(i);
                                    i--;
                                }
                            }
                            adapter.setPostList(postInfoList);
                            isSearchNoResult();
                            adapter.notifyDataSetChanged();
                            priceFilterText.setText(maxPriceText.getText().toString() + "원 이하");
                        }

                        dialog.dismiss();
                    }
                });

                DialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog priceDialog = DialogBuilder.create();
                priceDialog.show();
            }
        });

        //정렬필터 설정하는 코드
        filterFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder filterDialogBuilder = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_category_filter, null, false);
                filterDialogBuilder.setView(dialogView);
                AlertDialog filterDialog = filterDialogBuilder.create();
                filterDialog.show();

                newestText = dialogView.findViewById(R.id.dialog_category_filter_newest);
                highPriceText = dialogView.findViewById(R.id.dialog_category_filter_high_price);
                lowPriceText = dialogView.findViewById(R.id.dialog_category_filter_low_price);
                viewText = dialogView.findViewById(R.id.dialog_category_filter_view_count);

                //최신 순
                newestText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {
                            Collections.sort(postInfoList, new FilterNewsetComparator());
                            filterText.setText("최신순");
                            filterNum = 0;
                            filterDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("최신순");
                            filterNum = 0;
                            filterDialog.dismiss();
                        }
                    }

                });

                //가격 내림차순 => 높은거부터
                highPriceText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterPriceComparator().reversed());
                            } else {
                                Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                            }
                            filterText.setText("가격 높은순");
                            filterDialog.dismiss();
                            filterNum = 1;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("가격 높은순");
                            filterNum = 1;
                            filterDialog.dismiss();
                        }
                    }
                });
                //가격 오름차순 => 가격 낮은거부터
                lowPriceText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterPriceComparator());
                            } else {
                                Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                            }
                            filterText.setText("가격 낮은순");
                            filterDialog.dismiss();
                            filterNum = 2;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("가격 낮은순");
                            filterNum = 2;
                            filterDialog.dismiss();
                        }

                    }
                });

                //조회수 내림차순 => 즉 조회수 큰게 제일 위
                viewText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterViewComparator().reversed());
                            } else {
                                Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                            }


                            filterText.setText("조회수 순");
                            filterNum = 3;
                            filterDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("조회수 순");
                            filterNum = 2;
                            filterDialog.dismiss();
                        }
                    }
                });


            }
        });
    }

    // 1000단위 콤마
    protected String makeStringComma(String str) {    // 천단위 콤마설정.
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    public ArrayList<PostInfo> getPriceSortedPostList(ArrayList<PostInfo> beforeList){
        ArrayList<PostInfo> beSortPostList;
        beSortPostList=new ArrayList<>();
        beSortPostList.addAll(beforeList);

        //최대 최소 범위가 정해져 있을경우
        if(maxPrice!=-1&&minPrice!=-1){
            for (int i = 0; i < beSortPostList.size(); i++) {
                Log.e("123","최대 최소 범위가 정해져 있을경우");
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice <= maxPrice && postPrice >= minPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
            return beSortPostList;
        }
        //최대값 정해져있지 않을경우
        else if (minPrice !=-1){
            Log.e("123","최대값 정해져있지 않을경우");
            for (int i = 0; i < beSortPostList.size(); i++) {
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice >= minPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
            return beSortPostList;
        }
        //최소값 정해져있지 않을 경우
        else if(maxPrice!=-1){
            Log.e("123","최소값 정해져있지 않을 경우");
            for (int i = 0; i <beSortPostList.size(); i++) {
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice <= maxPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
        }
        //필터가 안껴져있을 경우
        else{
            return beSortPostList;
        }
        return beSortPostList;
    }

    public void isSearchNoResult(){
        if(postInfoList.size()==0){
            noResultText.setVisibility(View.VISIBLE);
        }
        else{
            noResultText.setVisibility(View.INVISIBLE);
        }

    }
    public ArrayList<PostInfo> getSortedPostList() {
        ArrayList<PostInfo> beSortPostList;
        beSortPostList=new ArrayList<>();
//        for(int i=0;i<originPostInfoList.size();i++){
//            beSortPostList.add(originPostInfoList.get(i));
//        }
        beSortPostList.addAll(originPostInfoList);


        for(int i=0;i<originPostInfoList.size();i++){
            Log.e("123","가격"+originPostInfoList.get(i).getPostPrice());
        }

        switch (filterNum) {
            case 0:

                if (beSortPostList.size() != 0) {
                    Collections.sort(beSortPostList, new FilterNewsetComparator());
                }

                break;
            case 1:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(beSortPostList, new FilterPriceComparator().reversed());
                } else {
                    Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(beSortPostList, new FilterPriceComparator());
                } else {
                    Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                }

                break;

            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(beSortPostList, new FilterViewComparator().reversed());
                } else {
                    Toast.makeText(getApplicationContext(), "안드로이드 버전 낮아서 sort x", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        Log.e("123","sortedPostList"+beSortPostList.toString());
        return beSortPostList;
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

    public void variableInit() {

        prevSearchKeywordList=new ArrayList<String>();
        sharedPreferences=getSharedPreferences("prevKeyword",MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        noResultText=(TextView) findViewById(R.id.category_search_no_result_text);
        backImage=(ImageView) findViewById(R.id.category_search_result_back_arrow);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearSearchKeyword = (LinearLayout) findViewById(R.id.category_search_linear_keyword);

        originPostInfoList=new ArrayList<>();

        postSearchImage=(ImageView) findViewById(R.id.category_search_post_search_image);
        filterResetBtn=(Button) findViewById(R.id.category_search_filter_reset_button);

        searchKeyword=(EditText)findViewById(R.id.category_search_keyword);

        categoryText = (TextView) findViewById(R.id.category_search_category_text);
        filterText = (TextView) findViewById(R.id.category_search_result_text);
        priceFilterText = (TextView) findViewById(R.id.category_search_price_range);

        priceFrame = (FrameLayout) findViewById(R.id.category_search_frame_price_filter);
        filterFrame = (FrameLayout) findViewById(R.id.category_search_filter_frame);
        recyclerView = (RecyclerView) findViewById(R.id.category_search_result_recyclerview);

        //리사이클러뷰 관련 선언들들
        linearLayoutManager = new LinearLayoutManager(Activity_category_search_result.this);
        postInfoList = new ArrayList<>();
        adapter = new Adapter_post_all_info(postInfoList, Activity_category_search_result.this);

        adapter.setItemClickListener(new Adapter_post_all_info.Interface_info_item_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(Activity_category_search_result.this, Activity_post_read.class);
                intent.putExtra("postNum", postInfoList.get(position).getPostNum());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    static class FilterViewComparator implements Comparator<PostInfo> {

        @Override
        public int compare(PostInfo o1, PostInfo o2) {
            if (Integer.parseInt(o1.getPostViewNum()) > Integer.parseInt(o2.getPostViewNum())) {
                return 1;
            } else if (Integer.parseInt(o1.getPostViewNum()) < Integer.parseInt(o2.getPostViewNum())) {
                return -1;
            }
            return 0;
        }
    }

    static class FilterPriceComparator implements Comparator<PostInfo> {

        @Override
        public int compare(PostInfo o1, PostInfo o2) {

            int priceO1, priceO2;
            priceO1 = Integer.parseInt(o1.getPostPrice().replace(",", ""));
            priceO2 = Integer.parseInt(o2.getPostPrice().replace(",", ""));
            if (priceO1 > priceO2) {
                return 1;
            } else if (priceO1 < priceO2) {
                return -1;
            }
            return 0;
        }
    }

    static class FilterNewsetComparator implements Comparator<PostInfo> {

        @Override
        public int compare(PostInfo o1, PostInfo o2) {


            if (Integer.parseInt(o1.getPostNum()) > Integer.parseInt(o2.getPostNum())) {
                return -1;
            } else if (Integer.parseInt(o1.getPostNum()) < Integer.parseInt(o2.getPostNum())) {
                return 1;
            }
            return 0;
        }
    }
}