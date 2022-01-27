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

public class Adapter_post_all_info extends RecyclerView.Adapter<Adapter_post_all_info.AllInfoViewHolder> {

    Interface_info_item_click listener;
    ArrayList<PostInfo> postList;
    Context context;


    public Adapter_post_all_info(ArrayList<PostInfo> postList,Context context) {
        this.postList = postList;
        this.context=context;
    }
    public void setPostList(ArrayList<PostInfo> postList){
        this.postList=postList;
    }

    public void setItemClickListener(Interface_info_item_click listener){
        this.listener=listener;
    }


    public String timeDifferentCheck(String uploadTime){

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        //달->일 -> 시간 -> 분 -> 초 로 차이나는지 확인해서
        String nowTime = formatter.format(date);

        String date1 = nowTime; //날짜1
        String date2 = uploadTime; //날짜2

        try{
            Date format1 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date1);
            Date format2 = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //시 차이
            long diffDays = diffSec / (24*60*60); //일자수 차이

            System.out.println(diffSec + "초 차이");
            System.out.println(diffMin + "분 차이");
            System.out.println(diffHor + "시 차이");
            System.out.println(diffDays + "일 차이");

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

        }
        return "1초전";
    }


    @NonNull
    @Override
    public AllInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_all_info, parent, false);
        AllInfoViewHolder allinfoViewHolder = new AllInfoViewHolder(view);

        return allinfoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllInfoViewHolder holder, int position) {
        holder.postTitle.setText(postList.get(position).getPostTitle());
        Glide.with(context).load(postList.get(position).getImageRoute().get(0)).into(holder.imageView);
        holder.postPrice.setText(postList.get(position).getPostPrice()+"원");

        holder.postTime.setText(timeDifferentCheck(postList.get(position).getPostRegTime()));
        if(postList.get(position).getPostSellType().equals("직거래")){
            holder.postSellType1.setVisibility(View.VISIBLE);
            holder.postSellType2.setVisibility(View.GONE);
        }
        else if(postList.get(position).getPostSellType().equals("택배거래")){
            holder.postSellType2.setVisibility(View.VISIBLE);
            holder.postSellType1.setVisibility(View.GONE);
        }

        if(postList.get(position).getPostLocationName()!=null){
            if(!postList.get(position).getPostLocationName().equals("장소정보 없음")){

                holder.postImageLocation.setVisibility(View.VISIBLE);
                holder.postLocationName.setVisibility(View.VISIBLE);
                holder.postLocationName.setText(postList.get(position).getPostLocationName());
            }
            else{
                holder.postImageLocation.setVisibility(View.INVISIBLE);
                holder.postLocationName.setVisibility(View.INVISIBLE);
            }
        }

        holder.postLike.setText(postList.get(position).getPostLikeNum());
        holder.postView.setText(postList.get(position).getPostViewNum());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class AllInfoViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView,postImageLocation;
        protected TextView  postLocationName,postTitle,postPrice,postSellType1,postSellType2,postTime,postView,postLike;


        public AllInfoViewHolder(@NonNull View itemView) {

            super(itemView);

            postImageLocation=itemView.findViewById(R.id.post_all_location_image);
            postLocationName=itemView.findViewById(R.id.post_all_location);
            postLike=itemView.findViewById(R.id.post_all_like);
            postView=itemView.findViewById(R.id.post_all_view_num);
            imageView=itemView.findViewById(R.id.post_all_image);
            postTitle=itemView.findViewById(R.id.post_all_title);
            postPrice=itemView.findViewById(R.id.post_all_price);
            postSellType1=itemView.findViewById(R.id.post_all_sell_type1);
            postSellType2=itemView.findViewById(R.id.post_all_sell_type2);
            postTime=itemView.findViewById(R.id.post_all_time);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }

    public interface Interface_info_item_click {
        void onItemClick(int position);
    }



}
