package com.example.electronicsmarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class Fragment_home extends Fragment  {

    ImageView settingImage;
    Button button;
    ImageView postWriteImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root =inflater.inflate(R.layout.fragment_home,container,false);
        button=root.findViewById(R.id.example_button2);
        postWriteImage=root.findViewById(R.id.post_write_plus_image);
        postWriteImage.setOnClickListener(postWriteClick);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),Activity_post_read.class);
                startActivity(intent);
            }
        });
        return root;
    }


    View.OnClickListener postWriteClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),Activity_post_write.class);
            startActivity(intent);
        }
    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}