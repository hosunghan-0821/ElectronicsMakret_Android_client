package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Activity_main_home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        Intent intent=getIntent();

        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame,new Fragment_home()).commit();

        if(intent.getStringExtra("fragmentNum")!=null){
            if(intent.getStringExtra("fragmentNum").equals("3")){
               bottomNavigationView.setSelectedItemId(R.id.mypage_fragment);
               changeFragment(new Fragment_mypage());
            }

        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case  R.id.home_fragment:
                        changeFragment(new Fragment_home());
                    break;
                    case R.id.chat_fragment:
                        changeFragment(new Fragment_chat());
                        break;
                    case R.id.mypage_fragment:
                        changeFragment(new Fragment_mypage());
                        break;

                  default:
                    break;
                }
                return true;
            }
        });
    }
    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame,fragment);
        transaction.commit();

    }
}