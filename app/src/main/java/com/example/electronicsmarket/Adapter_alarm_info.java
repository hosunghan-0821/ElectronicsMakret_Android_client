package com.example.electronicsmarket;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_alarm_info extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private ArrayList<DataNotificationInfo> notificationList;
    private Context context;
    private Interface_notification_itemClick itemClickListener;

    public void setItemClickListener(Interface_notification_itemClick itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public Adapter_alarm_info(ArrayList<DataNotificationInfo> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alarm_info, parent, false);
        Adapter_alarm_info.alarmViewHolder alarmViewHolder = new Adapter_alarm_info.alarmViewHolder(view);
        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder  instanceof alarmViewHolder){

            //알림내용
            ((alarmViewHolder)holder).messageText.setText(notificationList.get(position).getMessage());

            //읽지 않은 알람 내용표시
            if(notificationList.get(position).getNotificationIsRead().equals("0")){
                ((alarmViewHolder) holder).itemView.setBackgroundColor(Color.parseColor("#ece6cc"));
            }
            else{
                ((alarmViewHolder) holder).itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            //알림 type 에 따라 다른 text 띄어주기
            if(notificationList.get(position).getType().equals("0")){
                ((alarmViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_create_24);
                ((alarmViewHolder) holder).suggestText.setText("여기를 눌러 거래 후기를 남겨보세요!");
            }
            else if(notificationList.get(position).getType().equals("1")){
                ((alarmViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_write_24);
                ((alarmViewHolder) holder).suggestText.setText("여기를 눌러 후기를 확인해 보세요");
            }
            else if(notificationList.get(position).getType().equals("2")){
                ((alarmViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                ((alarmViewHolder) holder).suggestText.setText("여기를 눌러 해당 상품을 확인하세요");
            }


            //
            String timeDifference= timeDifferentCheck(notificationList.get(position).getNotificationRegTime());
            ((alarmViewHolder) holder).timeDifferenceText.setText(timeDifference);

        }


    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public String timeDifferentCheck(String uploadTime){

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //달->일 -> 시간 -> 분 -> 초 로 차이나는지 확인해서
        String nowTime = formatter.format(date);

        String date1 = nowTime; //날짜1
        String date2 = uploadTime; //날짜2

        try{
            Date format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1);
            Date format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //시 차이
            long diffDays = diffSec / (24*60*60); //일자수 차이

//            System.out.println(diffSec + "초 차이");
//            System.out.println(diffMin + "분 차이");
//            System.out.println(diffHor + "시 차이");
//            System.out.println(diffDays + "일 차이");

            if(diffSec<0){
                return "1초전";
            }
            else if(diffDays!=0){
                return (String.valueOf(diffDays)+"일 전");
            }
            else if(diffHor!=0){
                return (String.valueOf(diffHor)+"시간 전");
            }
            else if(diffMin!=0){
                return (String.valueOf(diffMin)+"분 전");
            }
            else if(diffSec!=0){
                return (String.valueOf(diffSec)+"초 전");
            }
            else{
                return "1초전";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "1초전";
    }

    public class alarmViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView imageView;
        protected TextView messageText,suggestText,timeDifferenceText;

        public alarmViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.recyclerview_alarm_info_image);

            messageText=itemView.findViewById(R.id.recyclerview_alarm_info_message);
            suggestText=itemView.findViewById(R.id.recyclerview_alarm_suggest);
            timeDifferenceText=itemView.findViewById(R.id.recyclerview_alarm_difference);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getAdapterPosition();
                    if(itemClickListener!=null){
                        itemClickListener.itemClick(position,alarmViewHolder.this);
                    }

                }
            });

        }
    }

    public interface Interface_notification_itemClick{
        public void itemClick(int position,Adapter_alarm_info.alarmViewHolder viewHolder);
    }
}
