package com.example.electronicsmarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fragment_chat extends Fragment {


    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_chat,container,false);

        button=view.findViewById(R.id.example_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), Activity_trade_detail_info.class);
                intent.putExtra("tradeNum","1");
                startActivity(intent);
            }
        });
        return view;
    }

}