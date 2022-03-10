package com.example.electronicsmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BroadcastReceiver_Reboot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("123","broadcastReceiver reboot onReceive 1:");

        //여기서 retrofit으로부터 읽지 않은메시지 있다고 알림 띄울까?
        //어떻게하는게 좋을지 모르겟네
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent in = new Intent(context, Service_Restart.class);
            context.startForegroundService(in);
        }
    }
}
