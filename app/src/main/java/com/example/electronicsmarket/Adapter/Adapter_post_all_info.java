package com.example.electronicsmarket.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electronicsmarket.Dto.PostInfo;
import com.example.electronicsmarket.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter_post_all_info extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Interface_review_update_delete reviewListener;
    private Interface_info_item_click listener;
    private ArrayList<PostInfo> postList;
    private Context context;
    private Interface_like_list_cancel cancelListener;
    private Interface_buy_confirm_click confirmListener;
    private String status;

    public Adapter_post_all_info(ArrayList<PostInfo> postList,Context context) {
        this.postList = postList;
        this.context=context;
    }
    public void setReviewListener(Interface_review_update_delete reviewListener){
        this.reviewListener=reviewListener;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setConfirmListener(Interface_buy_confirm_click confirmListener){
        this.confirmListener=confirmListener;
    }
    public void setPostList(ArrayList<PostInfo> postList){
        this.postList=postList;
    }

    public void setItemClickListener(Interface_info_item_click listener){
        this.listener=listener;
    }
    public void setLikeListCancelListener(Interface_like_list_cancel cancelListener){this.cancelListener=cancelListener;}

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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if(viewType==0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_all_info, parent, false);
            AllInfoViewHolder allinfoViewHolder = new AllInfoViewHolder(view);
            return allinfoViewHolder;
        }

        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_progress, parent, false);
            ProgressViewholder progressViewholder = new ProgressViewholder(view);
            return progressViewholder;
        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  AllInfoViewHolder){

            //좋아요에서 사용되는 경우.
            if(postList.get(position).isLikeList()){
                ((AllInfoViewHolder) holder).cancelImage.setVisibility(View.VISIBLE);
            }
            else{
                ((AllInfoViewHolder) holder).cancelImage.setVisibility(View.GONE);
            }

            ((AllInfoViewHolder) holder).postTitle.setText(postList.get(position).getPostTitle());

            Glide.with(context).load(postList.get(position).getImageRoute().get(0)).into( ((AllInfoViewHolder) holder).imageView);

            ((AllInfoViewHolder) holder).postPrice.setText(postList.get(position).getPostPrice()+"원");

            ((AllInfoViewHolder) holder).postTime.setText(timeDifferentCheck(postList.get(position).getPostRegTime()));

            if(postList.get(position).getPostStatus().equals("DR")){
                ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellStatus.setText("판매완료 (배송대기)");
                ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.VISIBLE);
                ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
            }
            else if(postList.get(position).getPostStatus().equals("DS")){
                ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellStatus.setText("판매완료 (배송중)");
                ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.VISIBLE);
                ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
            }
            else if(postList.get(position).getPostStatus().equals("DF")){
                ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellStatus.setText("판매완료 (배송완료)");
                ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.VISIBLE);
                if(status!=null){
                    if(status.equals("buy")){
                        ((AllInfoViewHolder) holder).postBuyConfirm.setText("구매확정");
                        ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
                }

            }
            else if(postList.get(position).getPostStatus().equals("S")){
                ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                
                if(postList.get(position).getPostTradeType()!=null){
                    if(postList.get(position).getPostTradeType().equals("택배거래")){
                        ((AllInfoViewHolder) holder).postSellStatus.setText("판매완료 (구매확정)");
                    }
                    else{
                        ((AllInfoViewHolder) holder).postSellStatus.setText("판매완료 (직거래 완료)");
                    }
                }

                ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.VISIBLE);

                if(status!=null){
                    if(status.equals("review")){
                        if(postList.get(position).isReview()){
                            ((AllInfoViewHolder) holder).postReviewUpdate.setVisibility(View.VISIBLE);
                            ((AllInfoViewHolder) holder).postReviewDelete.setVisibility(View.VISIBLE);
                            ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.INVISIBLE);

                        }
                        else{
                            ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.VISIBLE);
                            ((AllInfoViewHolder) holder).postReviewUpdate.setVisibility(View.GONE);
                            ((AllInfoViewHolder) holder).postReviewDelete.setVisibility(View.GONE);
                            ((AllInfoViewHolder) holder).postBuyConfirm.setText("리뷰작성");
                        }
                    }
                }
                else{
                    ((AllInfoViewHolder) holder).postReviewUpdate.setVisibility(View.GONE);
                    ((AllInfoViewHolder) holder).postReviewDelete.setVisibility(View.GONE);
                    ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
                }

            }
            else if(postList.get(position).getPostStatus().equals("RF") ){
                ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                ((AllInfoViewHolder) holder).postSellStatus.setText("환불처리(환불승인)");
                ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.VISIBLE);
            }
            else if(postList.get(position).getPostStatus().equals("Y")&&status!=null){

                if(status.equals("selling")){

                    ((AllInfoViewHolder) holder).postBuyConfirm.setText("판매완료");
                    ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);

                    if(postList.get(position).getPostSellType().equals("직거래")){

                        ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.VISIBLE);
                        ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                        ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                    }
                    else if(postList.get(position).getPostSellType().equals("택배거래")){
                        ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.VISIBLE);
                        ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                        ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                        ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
                    }
                    else{
                        ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.VISIBLE);
                        ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.VISIBLE);
                        ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                    }

                }
            }
            else{
                ((AllInfoViewHolder) holder).postTime.setVisibility(View.VISIBLE);
                ((AllInfoViewHolder) holder).postBuyConfirm.setVisibility(View.GONE);
                if(postList.get(position).getPostSellType().equals("직거래")){

                    ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.GONE);
                    ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                }
                else if(postList.get(position).getPostSellType().equals("택배거래")){
                    ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.GONE);
                    ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                }
                else{
                    ((AllInfoViewHolder) holder).postSellType1.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postSellType2.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postSellStatus.setVisibility(View.GONE);
                }
            }

            if(postList.get(position).getPostLocationName()!=null){
                if(!postList.get(position).getPostLocationName().equals("장소정보 없음")){

                    ((AllInfoViewHolder) holder).postImageLocation.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postLocationName.setVisibility(View.VISIBLE);
                    ((AllInfoViewHolder) holder).postLocationName.setText(postList.get(position).getPostLocationName());
                }
                else{
                    ((AllInfoViewHolder) holder).postImageLocation.setVisibility(View.INVISIBLE);
                    ((AllInfoViewHolder) holder).postLocationName.setVisibility(View.INVISIBLE);
                }
            }

            ((AllInfoViewHolder) holder).postLike.setText(postList.get(position).getPostLikeNum());
            ((AllInfoViewHolder) holder).postView.setText(postList.get(position).getPostViewNum());
        }
        else if(holder instanceof ProgressViewholder){

        }


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getViewType();
    }

    public class AllInfoViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView,postImageLocation,cancelImage;
        protected TextView  postLocationName,postTitle,postPrice,postSellType1,postSellType2,postTime,postView,postLike;
        protected TextView postSellStatus,postBuyConfirm,postReviewUpdate,postReviewDelete;


        public AllInfoViewHolder(@NonNull View itemView) {

            super(itemView);
            postReviewUpdate=itemView.findViewById(R.id.post_all_review_update);
            postBuyConfirm=itemView.findViewById(R.id.post_all_info_buy_confirm);
            postReviewDelete=itemView.findViewById(R.id.post_all_review_delete);

            cancelImage=itemView.findViewById(R.id.post_all_cancel_image);
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
            postSellStatus=itemView.findViewById(R.id.post_all_sell_status);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(position);
                    }
                }
            });

            postReviewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(reviewListener!=null){
                        reviewListener.onReviewUpdateClick(position);
                    }
                }
            });
            postReviewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(reviewListener!=null){
                        reviewListener.onReviewDeleteClick(position);
                    }
                }
            });
            postBuyConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(confirmListener!=null){
                        confirmListener.onConfirmClick(position);
                    }
                }
            });

            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(cancelListener!=null){

                        cancelListener.onItemCancel(position);
                    }
                }
            });

        }
    }

    public interface Interface_info_item_click {
        void onItemClick(int position);
    }
    public interface Interface_like_list_cancel{
        void onItemCancel(int position);
    }
    public interface Interface_buy_confirm_click{
        void onConfirmClick(int position);
    }
    public interface Interface_review_update_delete{
        void onReviewUpdateClick(int position);
        void onReviewDeleteClick(int position);
    }


    public class ProgressViewholder extends RecyclerView.ViewHolder {

        protected ProgressBar progressBar;
        public ProgressViewholder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.recyclerview_progressBar);
        }
    }
}
