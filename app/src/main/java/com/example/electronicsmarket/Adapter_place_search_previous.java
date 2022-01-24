package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class Adapter_place_search_previous extends RecyclerView.Adapter<Adapter_place_search_previous.PreviousViewHolder> {

    ArrayList<DataSearch> dataSearchPreviousList;

    Interface_previous_item_click previousListener;
    int check;

    public void setPreviousListener(Interface_previous_item_click previousListener){
        this.previousListener=previousListener;
    }

    public Adapter_place_search_previous(ArrayList<DataSearch> dataSearchPreviousList,int check) {
        this.dataSearchPreviousList = dataSearchPreviousList;
        this.check= check;
    }

    public void setDataSearchPreviousList(ArrayList<DataSearch> dataSearchPreviousList){
        this.dataSearchPreviousList=dataSearchPreviousList;
    }

    @NonNull
    @Override
    public Adapter_place_search_previous.PreviousViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_place_search_previous, parent, false);
        return new PreviousViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_place_search_previous.PreviousViewHolder holder, int position) {
       holder.placeName.setText(dataSearchPreviousList.get(position).getPlaceName());
       if(check==0){
           holder.checkImage.setImageResource(R.drawable.ic_baseline_check_24);
       }
    }

    @Override
    public int getItemCount() {
        return  dataSearchPreviousList.size();
    }

    public class PreviousViewHolder extends RecyclerView.ViewHolder {

        protected TextView placeName;
        protected ImageView cancelImage,checkImage;

        public PreviousViewHolder(@NonNull View itemView) {
            super(itemView);

            checkImage=itemView.findViewById(R.id.search_check_image);
            placeName=itemView.findViewById(R.id.search_place_name_previous);
            cancelImage=itemView.findViewById(R.id.search_previous_cancel_image);


            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(previousListener!=null){
                        previousListener.onItemClick(position);
                    }
                }

            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(previousListener!=null){
                        previousListener.mainItemClick(position);
                    }
                }
            });
        }
    }
    public interface Interface_previous_item_click {
        void onItemClick(int position);
        void mainItemClick(int position);
    }


}

