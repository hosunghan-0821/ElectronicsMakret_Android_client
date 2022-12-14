package com.example.electronicsmarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronicsmarket.Dto.DataCategory;
import com.example.electronicsmarket.R;

import java.util.ArrayList;

public class Adapter_category_info extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<DataCategory> categoryList=new ArrayList<>();

    private Interface_category_item_click listener;
    public void setOnItemClickListener(Interface_category_item_click listener){
        this.listener=listener;
    }

    public Adapter_category_info(ArrayList<DataCategory> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_category_info, parent, false);
        return new Adapter_category_info.CategoryViewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  CategoryViewholder){
            ((CategoryViewholder) holder).categoryText.setText(categoryList.get(position).getCategoryText());
            ((CategoryViewholder) holder).categoryImage.setImageResource(categoryList.get(position).getCategoryImage());
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }



    public class CategoryViewholder extends RecyclerView.ViewHolder{

        protected TextView categoryText;
        protected ImageView categoryImage;

        public CategoryViewholder(@NonNull View itemView) {
            super(itemView);
            categoryText=itemView.findViewById(R.id.recyclerview_category_text);
            categoryImage=itemView.findViewById(R.id.recyclerview_category_all_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(position);
                    }

                }
            });


        }
    }
    public interface Interface_category_item_click{
        void onItemClick(int position);
    }
}
