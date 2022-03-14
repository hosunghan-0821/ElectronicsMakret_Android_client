package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_chat_room extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<DataChatRoom> roomList;
    private Adapter_chat_room_click listener;

    public void setRoomList(ArrayList<DataChatRoom> roomList){
        this.roomList=roomList;
    }

    public Adapter_chat_room(Context context, ArrayList<DataChatRoom> roomList) {
        this.context = context;
        this.roomList = roomList;
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


        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView chatOtherProfileImage;
        protected TextView chatOtherNickname,chatFinalChat,chatFinalChatDate,chatNoReadMessage;


        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(position);
                    }
                }
            });
            chatNoReadMessage=itemView.findViewById(R.id.chat_room_no_read_message);
            chatOtherProfileImage=itemView.findViewById(R.id.chat_room_profile_image);
            chatOtherNickname=itemView.findViewById(R.id.chat_room_other_user_nickname);
            chatFinalChat=itemView.findViewById(R.id.chat_room_final_chat);
            chatFinalChatDate=itemView.findViewById(R.id.chat_room_final_chat_day);
        }
    }
    public interface Adapter_chat_room_click{
        void onItemClick(int position);
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
