package com.example.electronicsmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BroadcastReceiver_Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("123","broadcastReceiver onReceive 1:");
        //알람을 받으면, startForegroundService 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent in = new Intent(context, Service_Restart.class);
            context.startForegroundService(in);
        }
    }
}
