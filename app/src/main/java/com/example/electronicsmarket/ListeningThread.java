//package com.example.electronicsmarket;
//
//import android.content.Context;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.Socket;
//
//public class ListeningThread extends Thread{
//    private Socket socket=null;
//    private Context context;
//    private TcpInterface tcpInterface;
//
//    public ListeningThread(Socket socket, Context context){
//        this.socket=socket;
//        this.context=context;
//    }
//
//
//    public ListeningThread(Socket socket) {
//        this.socket = socket;
//    }
//    public void setTcpInterface(TcpInterface tcpInterface){
//        this.tcpInterface=tcpInterface;
//    }
//
//    public void run(){
//
//        try{
//            InputStream input = socket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//            String readValue;
//            while((readValue=reader.readLine())!=null){
//
//                System.out.println("readvalue + :"+readValue);
////                    Message msg = new Message();
////                    Bundle bundle= new Bundle();
////                    bundle.putString("read",readValue);
////                    msg.setData(bundle);;
////                    handler.sendMessage(msg);
//                if(tcpInterface!=null){
//                    tcpInterface.changeText(readValue);
//                }
//
//            }
//            reader.close();
//            socket.close();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//}
