package com.example.electronicsmarket.infra.Network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.electronicsmarket.Service.Service_Example;

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
                .build();
        this.connectivityManager=(ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public void register() {
        beforeNetwork= NetworkStatus.getConnectivityStatus(context);
        this.connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }


    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);

        //네트워크가 연결되었을 때 할 동작

        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(NetworkStatus.getConnectivityStatus(context)!=3 ){

            if(NetworkStatus.getConnectivityStatus(context)==2){
                try{
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if(NetworkStatus.getConnectivityStatus(context)!=beforeNetwork){


                Intent stopIntent = new Intent(context, Service_Example.class);
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

    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        //네트워크 연결이 끊겼을 때 할 동작

        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(NetworkStatus.getConnectivityStatus(context)==3){
            //이때만 UI 나타나게 하고
            Intent stopIntent = new Intent(context,Service_Example.class);
            context.stopService(stopIntent);

            Intent intent = new Intent("networkStatus");
            intent.putExtra("networkStatus", 3);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        beforeNetwork=NetworkStatus.getConnectivityStatus(context);

    }
}
