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


public class Adapter_seller_post_info extends RecyclerView.Adapter<Adapter_seller_post_info.ViewHolder> {

    private ArrayList<PostInfo> postList;
    private Context context;

    private Interface_seller_item_click listener;

    public void setPostList(ArrayList<PostInfo> postList){
        this.postList=postList;
    }
    public Adapter_seller_post_info(ArrayList<PostInfo> postList, Context context) {
        this.postList = postList;
        this.context=context;
    }
    public void setListener(Interface_seller_item_click listener){
        this.listener=listener;
    }


    @NonNull
    @Override
    public Adapter_seller_post_info.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_seller_post, parent, false);
        return new Adapter_seller_post_info.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_seller_post_info.ViewHolder holder, int position) {
        Glide.with(context).load(postList.get(position).getImageRoute().get(0)).into(holder.postImage);
        holder.postTitle.setText(postList.get(position).getPostTitle());
        holder.postPrice.setText(postList.get(position).getPostPrice()+"원");

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView postTitle,postPrice;
        protected ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle=itemView.findViewById(R.id.post_seller_post_title);
            postPrice=itemView.findViewById(R.id.post_seller_post_price);
            postImage=itemView.findViewById(R.id.post_seller_post_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        getAdapterPosition();
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });

        }
    }

    public interface Interface_seller_item_click {
        void onItemClick(int position);

    }

}
