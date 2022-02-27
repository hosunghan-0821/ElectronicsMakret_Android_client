package com.example.electronicsmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class example_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<exampleData> dataList;

    public example_adapter(ArrayList<exampleData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_example, parent, false);
        example_adapter.ExampleViewHolder exampleViewHolder = new example_adapter.ExampleViewHolder(view);
        return exampleViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  ExampleViewHolder){

            ((ExampleViewHolder) holder).idText.setText(dataList.get(position).getUserId());
            ((ExampleViewHolder) holder).contentText.setText(dataList.get(position).getContent());

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        protected TextView idText,contentText;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);

            idText=itemView.findViewById(R.id.example_user_name);
            contentText=itemView.findViewById(R.id.example_user_text);
        }
    }
}
