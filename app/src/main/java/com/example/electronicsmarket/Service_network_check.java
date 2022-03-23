package com.example.electronicsmarket;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class Service_network_check extends Service {
    NetworkConnectionCheck networkConnectionCheck;
    public Service_network_check() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //  LOLLIPOP Version 이상..
            if(networkConnectionCheck==null){
                networkConnectionCheck=new NetworkConnectionCheck(getApplicationContext());
                networkConnectionCheck.register();
            }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         //  LOLLIPOP Version 이상..
            if(networkConnectionCheck!=null) networkConnectionCheck.unregister();

    }
}