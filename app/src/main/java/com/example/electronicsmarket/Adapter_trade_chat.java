package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_trade_chat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataChat> chatList;
    private Context context;
    private Interface_itemHeightCheck checkHeightInterface;

    public Adapter_trade_chat(ArrayList<DataChat> chatList) {
        this.chatList = chatList;

    }
    public void setInterfaceCheckHeight(Interface_itemHeightCheck checkHeightInterface){
        this.checkHeightInterface=checkHeightInterface;
    }

    public Adapter_trade_chat(ArrayList<DataChat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //왼쪽(상대방이 채팅 칠 경우)
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_left, parent, false);
            LeftViewHolder leftViewHolder = new LeftViewHolder(view);
            return leftViewHolder;
        }
        //오른쪽(내가 채팅 칠 경우 )
        else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_right, parent, false);
            RightViewHolder rightViewHolder = new RightViewHolder(view);
            return rightViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof LeftViewHolder){
            ((LeftViewHolder) holder).chatText.setText(chatList.get(position).getChat());
            ((LeftViewHolder) holder).nickname.setText(chatList.get(position).getNickname());
            //시간 가공
            ((LeftViewHolder) holder).chatTime.setText(chatList.get(position).getChatTime());
            //상대방 회원 프로필 사진

            if(chatList.get(position).getProfileImageRoute()==null){
                ((LeftViewHolder) holder).profileImage.setImageResource(R.drawable.ic_baseline_person_black);
            }
            else{
                Glide.with(context).load(chatList.get(position).getProfileImageRoute()).into(((LeftViewHolder) holder).profileImage);
            }


        }
        else if (holder instanceof  RightViewHolder){
            ((RightViewHolder) holder).chatText.setText(chatList.get(position).getChat());

            //여기서 시간 가공 하면되지.
            ((RightViewHolder) holder).chatTime.setText(chatList.get(position).getChatTime());

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView profileImage;
        protected TextView chatText;
        protected TextView chatTime,nickname;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.chat_left_profile_image);
            chatText = itemView.findViewById(R.id.chat_left_text);
            chatTime = itemView.findViewById(R.id.chat_left_time);
            nickname=itemView.findViewById(R.id.chat_left_nickname);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {

        protected TextView chatText;
        protected TextView chatTime;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chat_right_text);
            chatTime = itemView.findViewById(R.id.chat_right_time);

        }
    }

    public interface Interface_itemHeightCheck{
        public void getViewHeight(View itemView,int viewType);
    }
}
