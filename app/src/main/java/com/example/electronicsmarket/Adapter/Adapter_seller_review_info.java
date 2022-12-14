package com.example.electronicsmarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronicsmarket.R;

public class Adapter_seller_review_info extends RecyclerView.Adapter<Adapter_seller_review_info.ViewHolder> {
    @NonNull
    @Override
    public Adapter_seller_review_info.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_seller_review, parent, false);
        return new Adapter_seller_review_info.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_seller_review_info.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
