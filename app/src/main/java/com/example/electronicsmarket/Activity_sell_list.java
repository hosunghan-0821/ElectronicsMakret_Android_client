package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class Activity_sell_list extends AppCompatActivity {

    private ArrayList<String> tabElement;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Adapter_sell_viewpager adapter;
    private Fragment_sell_selling sellingFrag;
    private Fragment_sell_reservation reservationFrag;
    private Fragment_sell_sold soldFrag;

    // fragment 여러개 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_list);
        variableInit();
    }

    public void variableInit() {

        //사용할 fragment 정의
        sellingFrag=new Fragment_sell_selling();
        reservationFrag=new Fragment_sell_reservation();
        soldFrag=new Fragment_sell_sold();

        //viewpager 관련 정의
        viewPager = (ViewPager2) findViewById(R.id.sell_list_viewpager);

        adapter= new Adapter_sell_viewpager(getSupportFragmentManager(),getLifecycle());
        adapter.addFragment(sellingFrag);
        adapter.addFragment(reservationFrag);
        adapter.addFragment(soldFrag);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);

        //tablayout 관련 정의
        tabLayout = (TabLayout) findViewById(R.id.sell_list_tab_control);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        tabElement.add("판매중");
        tabElement.add("예약중");
        tabElement.add("판매완료");

        //tablayout + viewpager2 연결
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                TextView textView = new TextView(Activity_sell_list.this);
//                textView.setText(tabElement.get(position));
//                textView.setTextColor(Color.BLACK);
//                textView.setGravity(Gravity.CENTER);
//                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                tab.setCustomView(textView);
                tab.setText(tabElement.get(position));

            }
        }).attach();
    }
}