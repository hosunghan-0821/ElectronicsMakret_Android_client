package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_place_search_result extends RecyclerView.Adapter<Adapter_place_search_result.SearchViewHolder> {

    ArrayList<DataSearch> dataSearchList;

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

            placeText=itemView.findViewById(R.id.search_place_name);
            addressText=itemView.findViewById(R.id.search_address_name);

        }
    }
}
