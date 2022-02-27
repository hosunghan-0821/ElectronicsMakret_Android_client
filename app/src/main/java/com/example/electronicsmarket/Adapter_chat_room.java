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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_chat_room extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<DataChatRoom> roomList;
    private Adapter_chat_room_click listener;

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
            if(roomList.get(position).getOtherUserImageRoute()==null){
                ((RoomViewHolder) holder).chatOtherProfileImage.setImageResource(R.drawable.ic_baseline_person_black);
            }
            else{
                Glide.with(context).load(roomList.get(position).getOtherUserImageRoute()).into(((RoomViewHolder) holder).chatOtherProfileImage);

            }

        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView chatOtherProfileImage;
        protected TextView chatOtherNickname,chatFinalChat,chatFinalChatDate;


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

            chatOtherProfileImage=itemView.findViewById(R.id.chat_room_profile_image);
            chatOtherNickname=itemView.findViewById(R.id.chat_room_other_user_nickname);
            chatFinalChat=itemView.findViewById(R.id.chat_room_final_chat);
            chatFinalChatDate=itemView.findViewById(R.id.chat_room_final_chat_day);
        }
    }
    public interface Adapter_chat_room_click{
        void onItemClick(int position);
    }
}
