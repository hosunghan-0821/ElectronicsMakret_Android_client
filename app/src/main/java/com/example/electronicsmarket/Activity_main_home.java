package com.example.electronicsmarket;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class Activity_main_home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String nickName;
    private TextView networkText;
    private Handler handler;
    private PermissionListener permissionlistener;
    public static boolean permissionCheck;


    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status =intent.getIntExtra("networkStatus",0);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("networkStatus", status);
                msg.setData(bundle);
                handler.sendMessage(msg);


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        variableInit();
        Intent intent = getIntent();

        //권한체크
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                permissionCheck = true;
                //Toast.makeText(Activity_trade_chat.this, "사진,이미지 권한허용", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Activity_main_home.this, "이미지 전송하기 위해선 권한필요", Toast.LENGTH_SHORT).show();
            }
        };


        requestPermission();
        //네트워크 환경에 따라서 화면 처리 다르게.
        LocalBroadcastManager.getInstance(Activity_main_home.this).registerReceiver(dataReceiver, new IntentFilter("networkStatus"));

        if(NetworkStatus.getConnectivityStatus(Activity_main_home.this)==3){
            networkText.setVisibility(View.VISIBLE);
        }
        else{
            networkText.setVisibility(View.GONE);
        }

        handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                int status = bundle.getInt("networkStatus");
                if(status==3){
                    networkText.setVisibility(View.VISIBLE);
                }
                else{
                    networkText.setVisibility(View.GONE);
                }
            }
        };
//        Log.e("123","Activity_main_home intent() : "+intent.getStringExtra("chatFragment"));
//        Log.e("123","Activity_main_home intent() : "+intent.toString());


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment_home()).commit();

        //pendingIntent로 화면 넘어올 때, 이벤트처리
        if (intent.getStringExtra("chatFragment") != null) {
            if (intent.getStringExtra("chatFragment").equals("chatFragment")) {
                bottomNavigationView.setSelectedItemId(R.id.chat_fragment);
                changeFragment(new Fragment_chat());
            }
        }
        else if(intent.getStringExtra("mypageFragment")!=null){
            if (intent.getStringExtra("mypageFragment").equals("mypageFragment")) {
                bottomNavigationView.setSelectedItemId(R.id.mypage_fragment);
                changeFragment(new Fragment_mypage());
            }
        }
        //network상태처리 서비스 시작
        Intent networkIntent = new Intent(Activity_main_home.this,Service_network_check.class);
        startService(networkIntent);

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

    private void requestPermission() {

        TedPermission.with(Activity_main_home.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

    }


    public void variableInit() {
        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");
        networkText=(TextView) findViewById(R.id.network_text);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(Activity_main_home.this).unregisterReceiver(dataReceiver);
        Log.e("123","main_home_destroy() ");
    }
}