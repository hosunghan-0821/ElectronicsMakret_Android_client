package com.example.electronicsmarket;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

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

        //?????? intent??? ?????? ?????? ?????? ????????? ?????????
        Intent intent = getIntent();


        //????????? ?????? ?????? ???, ?????? ???????????? ????????? case ?????????!
        if (intent.getStringExtra("category") != null) {
            //2. category ????????? ????????? ??????
            Log.e("123", "??????????????????");
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
                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 1. search??? ????????? ??????
            // Log.e("123", "????????????");
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

        //???????????? ???????????? ?????? ??????????????? ?????????, ?????? ????????? ?????????, ?????????????????????.
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

                            //?????? ???????????? ??????
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


                            //?????? ????????? ?????? ????????? ????????? ??????????????? ????????? ??????
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


        //filter ????????? ??????.
        filterResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNum=0;
                maxPrice=-1;
                minPrice=-1;
                filterText.setText("?????????");
                priceFilterText.setText("?????? ????????????");
                postInfoList.clear();
                postInfoList.addAll(originPostInfoList);
                adapter.setPostList(postInfoList);
                isSearchNoResult();
                adapter.notifyDataSetChanged();

            }
        });

        //?????? ?????? ?????? ??????
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
                //????????? 1000?????? ?????? ??????
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
                //????????? 1000?????? ?????? ??????
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


                DialogBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ((!maxPriceText.getText().toString().equals("")) && (!minPriceText.getText().toString().equals(""))) {
                            if (Integer.parseInt(maxPriceText.getText().toString().replace(",", "")) < Integer.parseInt(minPriceText.getText().toString().replace(",", ""))) {
                                Toast.makeText(getApplicationContext(), "??????????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                            }
                            //??????????????? ????????? ???????????? ???,
                            else {
                                minPrice = Integer.parseInt(minPriceText.getText().toString().replace(",", ""));
                                maxPrice = Integer.parseInt(maxPriceText.getText().toString().replace(",", ""));

                                //?????? ????????? ??????, sorting ??? arraylist ????????????
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
                                priceFilterText.setText(minPriceText.getText().toString() + " ~ " + maxPriceText.getText().toString() + "???");
                            }
                        }
                        //???????????? ???????????? ???,
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
                            priceFilterText.setText(minPriceText.getText().toString() + "??? ??????");
                        }
                        //???????????? ???????????? ???,
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
                            priceFilterText.setText(maxPriceText.getText().toString() + "??? ??????");
                        }

                        dialog.dismiss();
                    }
                });

                DialogBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog priceDialog = DialogBuilder.create();
                priceDialog.show();
            }
        });

        //???????????? ???????????? ??????
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

                //?????? ???
                newestText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {
                            Collections.sort(postInfoList, new FilterNewsetComparator());
                            filterText.setText("?????????");
                            filterNum = 0;
                            filterDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("?????????");
                            filterNum = 0;
                            filterDialog.dismiss();
                        }
                    }

                });

                //?????? ???????????? => ???????????????
                highPriceText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterPriceComparator().reversed());
                            } else {
                                Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
                            }
                            filterText.setText("?????? ?????????");
                            filterDialog.dismiss();
                            filterNum = 1;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("?????? ?????????");
                            filterNum = 1;
                            filterDialog.dismiss();
                        }
                    }
                });
                //?????? ???????????? => ?????? ???????????????
                lowPriceText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterPriceComparator());
                            } else {
                                Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
                            }
                            filterText.setText("?????? ?????????");
                            filterDialog.dismiss();
                            filterNum = 2;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("?????? ?????????");
                            filterNum = 2;
                            filterDialog.dismiss();
                        }

                    }
                });

                //????????? ???????????? => ??? ????????? ?????? ?????? ???
                viewText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfoList.size() != 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Collections.sort(postInfoList, new FilterViewComparator().reversed());
                            } else {
                                Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
                            }


                            filterText.setText("????????? ???");
                            filterNum = 3;
                            filterDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            filterText.setText("????????? ???");
                            filterNum = 2;
                            filterDialog.dismiss();
                        }
                    }
                });


            }
        });
    }

    // 1000?????? ??????
    protected String makeStringComma(String str) {    // ????????? ????????????.
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

        //?????? ?????? ????????? ????????? ????????????
        if(maxPrice!=-1&&minPrice!=-1){
            for (int i = 0; i < beSortPostList.size(); i++) {
                Log.e("123","?????? ?????? ????????? ????????? ????????????");
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice <= maxPrice && postPrice >= minPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
            return beSortPostList;
        }
        //????????? ??????????????? ????????????
        else if (minPrice !=-1){
            Log.e("123","????????? ??????????????? ????????????");
            for (int i = 0; i < beSortPostList.size(); i++) {
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice >= minPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
            return beSortPostList;
        }
        //????????? ??????????????? ?????? ??????
        else if(maxPrice!=-1){
            Log.e("123","????????? ??????????????? ?????? ??????");
            for (int i = 0; i <beSortPostList.size(); i++) {
                int postPrice = Integer.parseInt(beSortPostList.get(i).getPostPrice().replace(",", ""));
                if (!(postPrice <= maxPrice)) {
                    beSortPostList.remove(i);
                    i--;
                }
            }
        }
        //????????? ??????????????? ??????
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
            Log.e("123","??????"+originPostInfoList.get(i).getPostPrice());
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
                    Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(beSortPostList, new FilterPriceComparator());
                } else {
                    Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
                }

                break;

            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(beSortPostList, new FilterViewComparator().reversed());
                } else {
                    Toast.makeText(getApplicationContext(), "??????????????? ?????? ????????? sort x", Toast.LENGTH_SHORT).show();
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

        //?????????????????? ?????? ????????????
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