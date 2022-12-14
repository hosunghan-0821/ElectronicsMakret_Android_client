package com.example.electronicsmarket.BroadcastReceiver;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.electronicsmarket.Service.Service_Restart;

public class BroadcastReceiver_Alarm extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("123","broadcastReceiver onReceive 1:");
        //알람을 받으면, startForegroundService 실행
        //여기서 Shared에 닉네임 없으면 실행 x
        // shared 값 가져오기
        SharedPreferences sharedPreferences= context.getSharedPreferences("autoLogin",MODE_PRIVATE);

        String nickname=sharedPreferences.getString("nickName","");
        if(nickname.equals("")){
            Log.e("123","로그아웃 상태여서 서비스 실행x");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent in = new Intent(context, Service_Restart.class);
            context.startForegroundService(in);
        }
    }
}
