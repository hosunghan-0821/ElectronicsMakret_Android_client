package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class Activity_buy_list extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Adapter_sell_viewpager adapter;
    private ImageView backImage;

    private Fragment_buy_buying buyingFrag;
    private Fragment_buy_bought boughtFrag;
    private Fragment_buy_cancel cancelFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_list);
        variableInit();
        Intent intent = getIntent();
        if (intent.getStringExtra("boughtList") != null) {
            if (intent.getStringExtra("boughtList").equals("boughtList")) {
                viewPager.setCurrentItem(1);

            }
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void variableInit(){
        backImage=findViewById(R.id.buy_list_back_arrow);

        //사용할 fragment 정의
        buyingFrag=new Fragment_buy_buying();
        boughtFrag=new Fragment_buy_bought();
        cancelFrag=new Fragment_buy_cancel();

        //viewpager 관련 정의
        viewPager = (ViewPager2) findViewById(R.id.buy_list_viewpager);


        adapter = new Adapter_sell_viewpager(getSupportFragmentManager(),getLifecycle());
        adapter.addFragment(buyingFrag);
        adapter.addFragment(boughtFrag);
        adapter.addFragment(cancelFrag);

        viewPager.setAdapter(adapter);
        //화면 스와이프로 넘기게하는 옵션
        viewPager.setUserInputEnabled(true);

        tabLayout=(TabLayout) findViewById(R.id.buy_list_tab_control);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        break;
                    case 1:

                        viewPager.setCurrentItem(1);
                        break;
                    case 2 :

                        viewPager.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ArrayList<String> tabElement = new ArrayList<String>();
        tabElement.add("구매 진행중");
        tabElement.add("구매완료");
        tabElement.add("취소/환불");

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                tab.setText(tabElement.get(position));
            }
        }).attach();



    }
}