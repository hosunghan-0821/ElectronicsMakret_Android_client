package com.example.electronicsmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter_post_image extends RecyclerView.Adapter<Adapter_post_image.ImageViewholder> {

    private ArrayList<Data_post_image> imageList;
    Interface_post_listener listener;
    ItemTouchHelperListener touchListener;
    Context context;

    public Adapter_post_image(ArrayList<Data_post_image> imageList,Context context) {
        this.imageList = imageList;
        this.context=context;
    }

    @NonNull
    @Override
    public Adapter_post_image.ImageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_write_image, parent, false);
        ImageViewholder imageViewholder = new ImageViewholder(view);
        return imageViewholder;

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_post_image.ImageViewholder holder, int position) {
        //holder.startTime.setText(recordList.get(position).getStartTime());
        //holder.image.setImageURI(imageList.get(position).getImguri());
        //Glide.with(getApplicationContext()).load(info.getMemberImage().toString()).into(circleImageView);
        Glide.with(context).load(imageList.get(position).getImgUrl()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return (null != imageList ? imageList.size() : 0);
    }

    public void setImageList(ArrayList<Data_post_image> imageList) {
        this.imageList = imageList;
    }
    public void setListener(Interface_post_listener listener){
        this.listener=listener;
    }



    public class ImageViewholder extends RecyclerView.ViewHolder {

        protected ImageView image,cancelImage;


        public ImageViewholder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.post_write_recyclerview_image);
            cancelImage=itemView.findViewById(R.id.post_write_cancel);

            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if(listener !=null){
                        listener.onItemClick(ImageViewholder.this,position);
                    }

                }
            });
        }

    }
}
