package com.example.electronicsmarket;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
        Log.e("123","Activity_main_home intent() : "+intent.getStringExtra("chatFragment"));
        Log.e("123","Activity_main_home intent() : "+intent.toString());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment_home()).commit();

        if (intent.getStringExtra("chatFragment") != null) {
            if (intent.getStringExtra("chatFragment").equals("chatFragment")) {
                bottomNavigationView.setSelectedItemId(R.id.chat_fragment);
                changeFragment(new Fragment_chat());
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

        if(Service_Example.tcpService==null){
            Intent serviceIntent = new Intent(getApplicationContext(), Service_Example.class);
            startService(serviceIntent);
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.e(TAG, "Service is not running - START FOREGROUND SERVICE");
//            getApplicationContext().startForegroundService(serviceIntent);
//        } else {
//            Log.e(TAG, "Service is not running - START SERVICE");
//            getApplicationContext().startService(serviceIntent);
//        }
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent perMissionIntent = new Intent();
            perMissionIntent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            perMissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(perMissionIntent);
        }

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