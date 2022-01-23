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

public class Adapter_post_all_info extends RecyclerView.Adapter<Adapter_post_all_info.AllInfoViewHolder> {

    ArrayList<PostInfo> postList;
    Context context;

    public Adapter_post_all_info(ArrayList<PostInfo> postList,Context context) {
        this.postList = postList;
        this.context=context;
    }
    public void setPostList(ArrayList<PostInfo> postList){
        this.postList=postList;
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
        holder.postPrice.setText(postList.get(position).getPostPrice());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class AllInfoViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView  postTitle,postPrice,postSellType1,postSellType2,postTime;


        public AllInfoViewHolder(@NonNull View itemView) {

            super(itemView);

            imageView=itemView.findViewById(R.id.post_all_image);
            postTitle=itemView.findViewById(R.id.post_all_title);
            postPrice=itemView.findViewById(R.id.post_all_price);
            postSellType1=itemView.findViewById(R.id.post_all_sell_type1);
            postSellType2=itemView.findViewById(R.id.post_all_sell_type2);
            postTime=itemView.findViewById(R.id.post_all_time);

        }
    }
}
