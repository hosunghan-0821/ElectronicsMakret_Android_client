package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_delivery_info extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DeliveryInfo deliveryList;

    public Adapter_delivery_info(DeliveryInfo deliveryList) {
        this.deliveryList = deliveryList;
    }
    public void setDeliveryInfo(DeliveryInfo deliveryList){
        this.deliveryList=deliveryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_delivery_info, parent, false);
        return new DeliveryInfoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DeliveryInfoViewHolder){
             ((DeliveryInfoViewHolder) holder).timeText.setText(deliveryList.getTimeArr().get(position));
             ((DeliveryInfoViewHolder) holder).placeText.setText(deliveryList.getPlaceArr().get(position));
            ((DeliveryInfoViewHolder) holder).detailText.setText(deliveryList.getDetailArr().get(position));

        }
    }

    @Override
    public int getItemCount() {
        if(deliveryList.getTimeArr()==null){
            return 0;
        }
        else{
            return deliveryList.getTimeArr().size();
        }

    }

    public class DeliveryInfoViewHolder extends RecyclerView.ViewHolder{

        protected TextView timeText,placeText,detailText;
        public DeliveryInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText=itemView.findViewById(R.id.recyclerview_delivery_info_time);
            placeText=itemView.findViewById(R.id.recyclerview_delivery_info_place);
            detailText=itemView.findViewById(R.id.recyclerview_delivery_info_detail);
        }
    }
}
