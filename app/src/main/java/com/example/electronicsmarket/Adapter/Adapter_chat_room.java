package com.example.electronicsmarket.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electronicsmarket.Dto.DataChatRoom;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.Service.Service_Example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_chat_room extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<DataChatRoom> roomList;
    private Adapter_chat_room_click listener;
    private SharedPreferences sharedPreferences;

    public void setRoomList(ArrayList<DataChatRoom> roomList){
        this.roomList=roomList;
    }

    public Adapter_chat_room(Context context, ArrayList<DataChatRoom> roomList) {
        this.context = context;
        this.roomList = roomList;
        // shared 값 가져오기
        sharedPreferences= context.getSharedPreferences("noAlarmArrayList",Context.MODE_PRIVATE);
    }
    public void setRoomClickListener(Adapter_chat_room_click listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof RoomViewHolder){

            ((RoomViewHolder) holder).chatOtherNickname.setText(roomList.get(position).getOtherUserNickname());
            //채팅 가공
            String finalChat="";
            try{
                finalChat=roomList.get(position).getFinalChat().replace(Service_Example.CHANGE_LINE_CHAR,"\n");
                if(finalChat.contains(Adapter_trade_chat.imageRoute)){
                    finalChat="(이미지)";
                }
            }catch (Exception e){

            }

            ((RoomViewHolder) holder).chatFinalChat.setText(finalChat);
            //날짜 가공
            String chatTime=roomList.get(position).getFinalChatTime();
            chatTime=finalChatTimeProcess(chatTime);
            ((RoomViewHolder) holder).chatFinalChatDate.setText(chatTime);

            //프로필 이미지
            if(roomList.get(position).getOtherUserImageRoute()==null){
                ((RoomViewHolder) holder).chatOtherProfileImage.setImageResource(R.drawable.ic_baseline_person_black);
            }
            else{
                Glide.with(context).load(roomList.get(position).getOtherUserImageRoute()).into(((RoomViewHolder) holder).chatOtherProfileImage);
            }
            //읽지 않은 메시지
            String noReadMessageNum=roomList.get(position).getNoReadMessageNum();
            if(noReadMessageNum!=null){

                if(roomList.get(position).getNoReadMessageNum().equals("0")){
                    ((RoomViewHolder) holder).chatNoReadMessage.setVisibility(View.INVISIBLE);
                }
                else{
                    ((RoomViewHolder) holder).chatNoReadMessage.setVisibility(View.VISIBLE);
                    ((RoomViewHolder)holder).chatNoReadMessage.setText(roomList.get(position).getNoReadMessageNum());

                }
            }
            boolean noAlarmCheck=false;
            for(int i=0;i<getNoAlarmRoomArrayList().size();i++){
                if(getNoAlarmRoomArrayList().get(i)!=null){
                    if(getNoAlarmRoomArrayList().get(i).equals(roomList.get(position).getRoomNum())){
                        noAlarmCheck=true;
                        break;
                    }
                }
            }
            if(noAlarmCheck){
                ((RoomViewHolder) holder).chatRoomAlarmImage.setVisibility(View.VISIBLE);
            }
            else{
                ((RoomViewHolder) holder).chatRoomAlarmImage.setVisibility(View.GONE);
            }


        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView chatOtherProfileImage;
        protected TextView chatOtherNickname,chatFinalChat,chatFinalChatDate,chatNoReadMessage;
        protected  ImageView chatRoomOption,chatRoomAlarmImage;


        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            chatRoomAlarmImage=itemView.findViewById(R.id.chat_room_no_alarm_image);
            chatRoomOption=itemView.findViewById(R.id.chat_room_room_options);
            chatNoReadMessage=itemView.findViewById(R.id.chat_room_no_read_message);
            chatOtherProfileImage=itemView.findViewById(R.id.chat_room_profile_image);
            chatOtherNickname=itemView.findViewById(R.id.chat_room_other_user_nickname);
            chatFinalChat=itemView.findViewById(R.id.chat_room_final_chat);
            chatFinalChatDate=itemView.findViewById(R.id.chat_room_final_chat_day);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(position);
                    }
                }
            });

            chatRoomOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onOptionClick(position,chatRoomOption);
                    }
                }
            });
        }
    }
    public ArrayList<String> getNoAlarmRoomArrayList(){

        //gson 을 활용하여서 shared에 저장된 string을 object로 변환
        Gson gson=new GsonBuilder().create();

        ArrayList<String> noAlarmArrayList;
        String stringToObject = sharedPreferences.getString("noAlarmArrayList", "");
        Type arraylistType = new TypeToken<ArrayList<String>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();
        try{
            noAlarmArrayList=gson.fromJson(stringToObject,arraylistType);
            if(noAlarmArrayList==null){
                noAlarmArrayList= new ArrayList<String>();
            }
            return noAlarmArrayList;
        }catch (Exception e){
            e.printStackTrace();
            return noAlarmArrayList=new ArrayList<>();
        }

    }
    public interface Adapter_chat_room_click{
        void onItemClick(int position);
        void onOptionClick(int position,View view);
    }

    public String finalChatTimeProcess(String chatTime){

        //String chatting 시간 Date 로 변환
        Date chatDate;
        //현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat chatDateFormat= new SimpleDateFormat("MM월dd일");
        SimpleDateFormat chatTimeDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            //날짜가 같을 경우 시간을 작성
            chatDate=chatTimeDbDateFormat.parse(chatTime);
            if(chatDateFormat.format(date).equals(chatDateFormat.format(chatDate))){
                return chatTimeDateFormat.format(chatDate);
            }
            //날짜가 다를경우 날짜를 작성
            else{
                return chatDateFormat.format(chatDate);
            }
        }catch (Exception e){

        }

        return "";
    };
}
