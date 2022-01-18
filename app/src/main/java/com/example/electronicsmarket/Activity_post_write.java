package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class Activity_post_write extends AppCompatActivity {


    private Interface_post_listener imageClickListener;
    private RecyclerView imageRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_image imageAdapter;
    private ArrayList<Data_post_image> imageList;

    private ImageView selectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

       //recyclerview 관련 준비
        imageRecyclerview=findViewById(R.id.recyclerView_post_write_image);
        linearLayoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        imageList=new ArrayList<Data_post_image>();
        imageAdapter=new Adapter_post_image(imageList);

        imageRecyclerview.setLayoutManager(linearLayoutManager);
        imageRecyclerview.setAdapter(imageAdapter);

        selectImage=findViewById(R.id.post_write_image_choice_image);
        selectImage.setOnClickListener(imageClick);


        imageAdapter.setListener(new Interface_post_listener() {
            @Override
            public void onItemClick(Adapter_post_image.ImageViewholder imageViewholder, int position) {
                imageList.remove(position);
                imageAdapter.notifyItemRemoved(position);
            }
        });
    }


    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//
            Intent intent =new Intent();
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setAction(intent.ACTION_GET_CONTENT);
            galleryLauncher.launch(intent);

        }
    };

        private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK){
                            Intent intent = result.getData();
                            //이미지 하나선택
                            if(intent.getClipData()==null){
                                Log.e("123","asd");
                                return;
                            }
                            //이미지 여러개 선택할 경우
                            ClipData clipData = intent.getClipData();
                            for(int i =0; i<clipData.getItemCount();i++){
                                Log.e("123","444");
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                Data_post_image data=new Data_post_image(imageUri);
                                imageList.add(data);
                            }

                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
}