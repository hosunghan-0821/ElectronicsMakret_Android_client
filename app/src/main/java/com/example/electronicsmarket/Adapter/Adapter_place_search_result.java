package com.example.electronicsmarket.Adapter;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronicsmarket.Dto.DataSearch;
import com.example.electronicsmarket.Adapter.Interface.Interface_search_result_listener;
import com.example.electronicsmarket.R;

import java.util.ArrayList;

public class Adapter_place_search_result extends RecyclerView.Adapter<Adapter_place_search_result.SearchViewHolder> {

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    ArrayList<DataSearch> dataSearchList;
    Interface_search_result_listener listener;

    public void setmSelectedItems(SparseBooleanArray mSelectedItems){
        this.mSelectedItems=mSelectedItems;
    }
    public void setListener(Interface_search_result_listener listener){
        this.listener=listener;
    }
    public void setDataSearchList(ArrayList<DataSearch> dataSearchList){
        this.dataSearchList=dataSearchList;
    }

    public Adapter_place_search_result(ArrayList<DataSearch> dataSearchList) {

        this.dataSearchList=dataSearchList;
    }

    @NonNull
    @Override
    public Adapter_place_search_result.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_place_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_place_search_result.SearchViewHolder holder, int position) {



        Log.e("456",mSelectedItems.toString());
        Log.e("456",String.valueOf(mSelectedItems.get(position,false)));
        if ( mSelectedItems.get(position, false) ){
            //holder.itemView.setBackgroundColor(Color.GRAY);
            holder.itemView.setBackgroundResource(R.color.gray);
        } else {
            //holder.itemView.setBackgroundColor(Color.WHITE);
            holder.itemView.setBackgroundResource(R.color.whit_gray);
        }

        holder.placeText.setText(dataSearchList.get(position).getPlaceName());
        holder.addressText.setText(dataSearchList.get(position).getAddressName());

    }

    @Override
    public int getItemCount() {
        return dataSearchList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        protected TextView placeText,addressText;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getAdapterPosition();
                    int allPosition;
                    for (int i = 0; i < mSelectedItems.size(); i++) {
                        allPosition = mSelectedItems.keyAt(i);
                        mSelectedItems.put(allPosition, false);
                        notifyItemChanged(allPosition);
                    }

                    Log.e("456",String.valueOf(mSelectedItems.get(position,false)));

                    if ( mSelectedItems.get(position, false) ){
                        mSelectedItems.put(position, false);

//                        v.setBackgroundColor(Color.WHITE);
                          v.setBackgroundResource(R.color.whit_gray);


                    } else {
                        mSelectedItems.put(position, true);
                        //v.setBackgroundColor(Color.GRAY);
                        v.setBackgroundResource(R.color.gray);
                    }

                    if(listener!=null){
                        listener.onItemClick(SearchViewHolder.this,position);
                    }

                }
            });
            placeText=itemView.findViewById(R.id.search_place_name);
            addressText=itemView.findViewById(R.id.search_address_name);

        }
    }



}
