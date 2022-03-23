package com.example.electronicsmarket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NetworkConnectionCheck extends ConnectivityManager.NetworkCallback {
    private int beforeNetwork=0;
    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    public NetworkConnectionCheck(Context context){
        this.context =context;
        networkRequest= new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        this.connectivityManager=(ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public void register() {
        beforeNetwork=NetworkStatus.getConnectivityStatus(context);
        this.connectivityManager.registerNetworkCallback(networkRequest, this);}

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }


    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);

        //네트워크가 연결되었을 때 할 동작
        Log.e("456","network available : " + network.describeContents());

        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(NetworkStatus.getConnectivityStatus(context)!=3 ){

            if(NetworkStatus.getConnectivityStatus(context)==2){
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if(NetworkStatus.getConnectivityStatus(context)!=beforeNetwork){

                Log.e("123","이전 네트워크와 :"+beforeNetwork +"현재 네트워크 : "+NetworkStatus.getConnectivityStatus(context));
                Intent stopIntent = new Intent(context,Service_Example.class);
                context.stopService(stopIntent);
                Intent startIntent = new Intent(context,Service_Example.class);
                context.startService(startIntent);
            }
            else{
                Intent intent = new Intent(context,Service_Example.class);
                context.startService(intent);
            }

            Intent intent = new Intent("networkStatus");
            intent.putExtra("networkStatus", 0);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }
        beforeNetwork=NetworkStatus.getConnectivityStatus(context);
        Log.e("456","인터넷 상태 : "+NetworkStatus.getConnectivityStatus(context));
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        //네트워크 연결이 끊겼을 때 할 동작
        Log.e("456","network lost" + network.describeContents());
        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(NetworkStatus.getConnectivityStatus(context)==3){
            //이때만 UI 나타나게 하고
            Intent stopIntent = new Intent(context,Service_Example.class);
            context.stopService(stopIntent);
            Log.e("456","인터넷 상태 : 연결 가능한 네트워크 x" );
            Intent intent = new Intent("networkStatus");
            intent.putExtra("networkStatus", 3);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        else{
            Log.e("456","인터넷 상태 : "+NetworkStatus.getConnectivityStatus(context));
        }
        beforeNetwork=NetworkStatus.getConnectivityStatus(context);

    }
}
