package com.example.electronicsmarket;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WritingThread extends Thread{
    Socket socket =null;
    Context context;
    EditText editText;
    TextView textView;

    public WritingThread(Socket socket, EditText editText, TextView textView) {
        this.socket = socket;
        this.editText=editText;
        this.textView=textView;
    }


    public void  run(){
        try{
            OutputStream out= socket.getOutputStream();
            //PrintWriter 에 위 OutputStream 을 담아 사용;
            PrintWriter writer = new PrintWriter(out,true);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            String writeValue="";

            while(true){

                writeValue=editText.getText().toString();
                writer.println(writeValue);

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
