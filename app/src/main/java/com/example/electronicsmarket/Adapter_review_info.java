package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_review_info extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReviewInfo> reviewList;
    private Context context;
    private Interface_review_item_click listener;

    public void setListener(Interface_review_item_click listener){
        this.listener=listener;
    }
    public Adapter_review_info(ArrayList<ReviewInfo> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
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

        }
        return "1초전";
    }
    public void setReviewList(ArrayList<ReviewInfo> reviewList){
        this.reviewList=reviewList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_seller_review, parent, false);
        return new Adapter_review_info.ReviewInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ReviewInfoViewHolder){

            //일단 텍스트만 제대로 적용되는지 확인하자
            String postTitle = reviewList.get(position).getPostTitle();
            if(postTitle.length()>10){
                postTitle=postTitle.substring(0,10)+"...";
            }
            ((ReviewInfoViewHolder) holder).reviewTime.setText(timeDifferentCheck(reviewList.get(position).getReviewTime()));
            ((ReviewInfoViewHolder) holder).reviewWriter.setText(reviewList.get(position).getReviewWriter());
            ((ReviewInfoViewHolder) holder).reviewContents.setText(reviewList.get(position).getReviewContents());
            ((ReviewInfoViewHolder) holder).reviewProduct.setText("구매상품: "+postTitle);

            //이미지 처리
            Glide.with(context).load(reviewList.get(position).getReviewWriterProfile()).into(((ReviewInfoViewHolder) holder).profileImage);

            //점수 처리
            ((ReviewInfoViewHolder) holder).reviewRating.setRating(Float.parseFloat(reviewList.get(position).getReviewRating()));

        }

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }



    public class ReviewInfoViewHolder extends RecyclerView.ViewHolder{

        protected TextView reviewWriter,reviewContents,reviewProduct,reviewTime;
        protected CircleImageView profileImage;
        protected RatingBar reviewRating;
        public ReviewInfoViewHolder(@NonNull View itemView) {
            super(itemView);


            reviewWriter=itemView.findViewById(R.id.post_seller_review_nickname);
            reviewContents=itemView.findViewById(R.id.post_seller_review_contents);
            reviewProduct=itemView.findViewById(R.id.post_seller_review_buy_product);
            reviewTime=itemView.findViewById(R.id.post_seller_review_time);

            profileImage=itemView.findViewById(R.id.post_seller_review_writer_profile);
            reviewRating=itemView.findViewById(R.id.post_seller_review_rating);

            reviewWriter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position= getAdapterPosition();

                    if(listener!=null){
                        listener.onUserInfoClick(position);
                    }

                }
            });

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onUserInfoClick(position);
                    }
                }
            });

            reviewProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= getAdapterPosition();
                    if(listener!=null){
                        listener.onReviewProductClick(position);
                    }
                }
            });

        }
    }

    public interface Interface_review_item_click{
        void onReviewProductClick(int position);
        void onUserInfoClick(int position);
    }
}
