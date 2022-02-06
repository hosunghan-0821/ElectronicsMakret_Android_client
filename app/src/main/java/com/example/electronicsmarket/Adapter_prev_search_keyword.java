package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_prev_search_keyword extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> prevKewordList;
    private Adapter_prev_search_keyword.Interface_prev_keyword_click listener;

    public void setPrevKewordList (ArrayList<String> prevKewordList){
        this.prevKewordList=prevKewordList;
    }

    public Adapter_prev_search_keyword(ArrayList<String> prevKewordList, Interface_prev_keyword_click listener) {
        this.prevKewordList = prevKewordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_prev_keyword, parent, false);
        return new Adapter_prev_search_keyword.prevSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof prevSearchHolder){
            ((prevSearchHolder) holder).prevKeword.setText(prevKewordList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return prevKewordList.size();
    }

    public class prevSearchHolder extends RecyclerView.ViewHolder{

        protected TextView prevKeword;
        protected ImageView cancelImage;

        public prevSearchHolder(@NonNull View itemView) {
            super(itemView);
            prevKeword=itemView.findViewById(R.id.prev_search_post_keyword);
            cancelImage=itemView.findViewById(R.id.prev_search_keyword_delete_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= getAdapterPosition();
                    if (listener!=null){
                        listener.onItemClick(position);
                    }
                }
            });
            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(listener!=null){
                        listener.onCancelClick(position);
                    }
                }
            });
        }
    }

    public interface Interface_prev_keyword_click {
        void onItemClick(int position);
        void onCancelClick(int position);
    }
}
