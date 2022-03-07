package com.example.electronicsmarket;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Activity_main_home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        variableInit();

        Intent intent = getIntent();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment_home()).commit();

        if (intent.getStringExtra("fragmentNum") != null) {
            if (intent.getStringExtra("fragmentNum").equals("3")) {
                bottomNavigationView.setSelectedItemId(R.id.mypage_fragment);
                changeFragment(new Fragment_mypage());
            }
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_fragment:
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

        Intent serviceIntent = new Intent(getApplicationContext(), Service_Example.class);
        startService(serviceIntent);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.e(TAG, "Service is not running - START FOREGROUND SERVICE");
//            getApplicationContext().startForegroundService(serviceIntent);
//        } else {
//            Log.e(TAG, "Service is not running - START SERVICE");
//            getApplicationContext().startService(serviceIntent);
//        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();

    }

    public void variableInit() {
        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("123","main_home_destroy() ");
    }
}