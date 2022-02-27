package com.example.electronicsmarket;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class bef_ListeningThread extends Thread{

    private Socket socket=null;
    private Context context;

    public bef_ListeningThread(Socket socket, Context context){
        this.socket=socket;
        this.context=context;
    }
    public void run(){
        try{
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String readValue;
            while((readValue=reader.readLine())!=null){
                System.out.println("readvalue + :"+readValue);
//                Toast.makeText(context, readValue, Toast.LENGTH_SHORT).show();
            }

            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
