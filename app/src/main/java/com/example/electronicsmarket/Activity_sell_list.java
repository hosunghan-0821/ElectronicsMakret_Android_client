package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class Activity_sell_list extends AppCompatActivity {

    private ArrayList<String> tabElement;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Adapter_sell_viewpager adapter;

    private Fragment_sell_selling sellingFrag;
    private Fragment_sell_cancel cancelFrag;
    private Fragment_sell_sold soldFrag;
    private ImageView backImage;

    // fragment 여러개 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_list);
        variableInit();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void variableInit() {


        //
        backImage=findViewById(R.id.sell_list_back_arrow);
        //사용할 fragment 정의
        sellingFrag=new Fragment_sell_selling();
        cancelFrag =new Fragment_sell_cancel();
        soldFrag=new Fragment_sell_sold();

        //viewpager 관련 정의
        viewPager = (ViewPager2) findViewById(R.id.sell_list_viewpager);

        adapter= new Adapter_sell_viewpager(getSupportFragmentManager(),getLifecycle());
        adapter.addFragment(sellingFrag);
        adapter.addFragment(soldFrag);
        adapter.addFragment(cancelFrag);
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
        tabElement.add("판매완료");
        tabElement.add("취소/환불");

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