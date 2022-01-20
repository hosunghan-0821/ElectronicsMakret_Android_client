package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_post_category extends RecyclerView.Adapter<Adapter_post_category.CategoryViewholder> {

    ArrayList<String> categoryList;
    Interface_category_listener listener;

    public Adapter_post_category(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_category, parent, false);
        CategoryViewholder categoryViewholder =new CategoryViewholder(view);

        return categoryViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewholder holder, int position) {
        holder.categoryText.setText(categoryList.get(position));

    }


    @Override
    public int getItemCount() {
        return (null != categoryList ? categoryList.size() : 0);
    }

    public void setCategoryList(ArrayList<String> categoryList){
        this.categoryList=categoryList;
    }
    public void setCategoryListener(Interface_category_listener listener){
        this.listener=listener;
    }

    public class CategoryViewholder extends RecyclerView.ViewHolder{

        protected TextView categoryText;

        public CategoryViewholder(@NonNull View itemView) {
            super(itemView);

            categoryText=itemView.findViewById(R.id.category_text);

            categoryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(CategoryViewholder.this,position);
                    }
                }
            });
        }
    }

}
