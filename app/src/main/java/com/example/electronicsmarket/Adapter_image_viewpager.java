package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class Adapter_image_viewpager extends RecyclerView.Adapter<Adapter_image_viewpager.ViewPageHolder> {

    private Context context;
    private String[] sliderImage;

    public Adapter_image_viewpager(Context context, String[] sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public Adapter_image_viewpager.ViewPageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_viewpager_image, parent, false);
        return new ViewPageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_image_viewpager.ViewPageHolder holder, int position) {
        holder.bindSliderImage(sliderImage[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImage.length;
    }


    public class ViewPageHolder extends RecyclerView.ViewHolder {


        private ImageView imageView;
        public ViewPageHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(String imageURL){
            Glide.with(context).load(imageURL).into(imageView);
        }
    }
}
