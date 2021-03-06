package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_buyer_choice extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataInquirerInfo> inquireList;
    private Context context;
    private Interface_buyer_choice buyerChoiceListener;

    public void setBuyerChoiceListener(Interface_buyer_choice buyerChoiceListener){
        this.buyerChoiceListener=buyerChoiceListener;
    }

    public Adapter_buyer_choice(ArrayList<DataInquirerInfo> inquireList, Context context) {
        this.inquireList = inquireList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_buyer_choice, parent, false);
            Adapter_buyer_choice.choiceViewHolder choiceViewHolder = new Adapter_buyer_choice.choiceViewHolder(view);
            return choiceViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(inquireList.get(position).getImageRoute()==null){
            ((choiceViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_person_black);
        }
        else{
            Glide.with(context).load(inquireList.get(position).getImageRoute()).into( ((choiceViewHolder) holder).imageView);
        }

        ((choiceViewHolder) holder).nickname.setText(inquireList.get(position).getNickname());
        String timeDifference= timeDifferentCheck(inquireList.get(position).getFinalChatTime());
        ((choiceViewHolder) holder).finalChatTime.setText("????????? ?????? : "+timeDifference);
    }


    @Override
    public int getItemCount() {
        return inquireList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class choiceViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView imageView;
        protected TextView nickname,finalChatTime;
        public choiceViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(buyerChoiceListener!=null){
                        buyerChoiceListener.buyerChoiceClick(position);
                    }

                }
            });
            finalChatTime=itemView.findViewById(R.id.recyclerview_buyer_choice_last_chat);
            imageView=itemView.findViewById(R.id.recyclerview_buyer_choice_profile_image);
            nickname=itemView.findViewById(R.id.recyclerview_buyer_choice_nickname);

        }
    }


    public String timeDifferentCheck(String uploadTime){

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        //???->??? -> ?????? -> ??? -> ??? ??? ??????????????? ????????????
        String nowTime = formatter.format(date);

        String date1 = nowTime; //??????1
        String date2 = uploadTime; //??????2

        try{
            Date format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(date1);
            Date format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //??? ??????
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //??? ??????
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //??? ??????
            long diffDays = diffSec / (24*60*60); //????????? ??????

//            System.out.println(diffSec + "??? ??????");
//            System.out.println(diffMin + "??? ??????");
//            System.out.println(diffHor + "??? ??????");
//            System.out.println(diffDays + "??? ??????");

            if(diffSec<0){
                return "1??????";
            }
            else if(diffDays!=0){
                return (String.valueOf(diffDays)+"??? ???");
            }
            else if(diffHor!=0){
                return (String.valueOf(diffHor)+"?????? ???");
            }
            else if(diffMin!=0){
                return (String.valueOf(diffMin)+"??? ???");
            }
            else if(diffSec!=0){
                return (String.valueOf(diffSec)+"??? ???");
            }
            else{
                return "1??????";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "1??????";
    }

    public interface  Interface_buyer_choice{
        public void buyerChoiceClick(int position);
    }
}
