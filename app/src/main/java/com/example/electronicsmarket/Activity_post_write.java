package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_post_write extends AppCompatActivity {


    private ItemTouchHelper mItemTouchHelper;

    ArrayList<File> imageFileCollect;
    //ArrayList<String> tempFileList;
    ArrayList<MultipartBody.Part> files;
    Retrofit retrofit;
    private TextView postWriteText;
    private RecyclerView imageRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_image imageAdapter;
    private ArrayList<Data_post_image> imageList;
    private TextView imageNumberText;
    private ImageView selectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        variableInit();

        String text = "some text";
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), text);

        selectImage.setOnClickListener(imageClick);

        //
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(imageAdapter, new ItemTouchHelperListener() {
            @Override
            public boolean onItemMove(int from_position, int to_position) {

                //순서가 변경될 때!
                //recyclerview arrayList 순서도 바꾸고, imageUriFile 순서도 바꿔야함.
                Data_post_image data = imageList.get(from_position);
                imageList.remove(from_position);
                imageList.add(to_position,data);
                imageAdapter.setImageList(imageList);
                imageAdapter.notifyDataSetChanged();
                imageNumberText.setText(imageList.size()+"/5");

                Collections.swap(imageFileCollect,from_position,to_position);
                Log.e("123",imageFileCollect.toString());
                return false;
            }
            @Override
            public void onItemSwipe(int position) {

            }
        }) );
        mItemTouchHelper.attachToRecyclerView(imageRecyclerview);



        //글쓰기 완료버튼 클릭 리스너
        postWriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                files.clear();
                for (int i = 0; i < imageFileCollect.size(); i++) {
                    try {
                        Log.e("123",String.valueOf(i));
                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFileCollect.get(i));
                        MultipartBody.Part filepart = MultipartBody.Part.createFormData("image" + i, "image", fileBody);
                        files.add(filepart);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("123",files.toString());

                }
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<MemberSignup> call = service.sendMultiImage(files,requestBody);
                call.enqueue(new Callback<MemberSignup>() {
                    @Override
                    public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                        Log.e("123", "통신성공");

                        //이미지 업로드 전송 성공하면 임시파일들 삭제.
                        for(int i=0; i<imageFileCollect.size();i++){
                            if(imageFileCollect.get(i).exists()){
                                boolean result=imageFileCollect.get(i).delete();
                                Log.e("123",String.valueOf(result));
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<MemberSignup> call, Throwable t) {
                        Log.e("123", "통신오류");
                    }
                });


            }
        });

        //이미지 지울 때 사용하는 클릭 리스너.
        imageAdapter.setListener(new Interface_post_listener() {
            @Override
            public void onItemClick(Adapter_post_image.ImageViewholder imageViewholder, int position) {

                if(imageFileCollect.get(position).exists()){

                    boolean result=imageFileCollect.get(position).delete();
                    Log.e("123",String.valueOf(result));

                }
                imageFileCollect.remove(position);
                imageList.remove(position);
                imageAdapter.notifyItemRemoved(position);
                imageNumberText.setText(imageList.size()+"/5");
                Log.e("123",imageFileCollect.toString());
            }
        });

    }


    //갤러리에서 이미지 가져오기.
    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(imageFileCollect.size()>5){
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                builder.setTitle("알림");
                builder.setMessage("이미지는 최대 5개까지만 업로드 가능합니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return;
            }
            Intent intent = new Intent();
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(intent.ACTION_GET_CONTENT);
            galleryLauncher.launch(intent);

        }
    };

    //갤러리에서 이미지 가져왔을 때, resultLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {


                        //갯수제한 걸어둘 필요가 있다.
                        files.clear();
                        Intent intent = result.getData();
                        //이미지 하나선택

                        if (intent.getClipData() == null) {

                            if(imageFileCollect.size()+1==6){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                                builder.setTitle("알림");
                                builder.setMessage("이미지는 최대 5개까지만 업로드 가능합니다.");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                                return;
                            }
                            Log.e("123", "이미지 한개 선택");
                            Uri imageUri= intent.getData();
                            Data_post_image data= new Data_post_image(imageUri);
                            imageList.add(data);
                            File uriFile = new File(createCopyAndReturnRealPath(imageUri,"image"));
                            imageFileCollect.add(uriFile);
                            imageAdapter.notifyDataSetChanged();
                            imageNumberText.setText(imageList.size()+"/5");
                        }

                        //이미지 여러개 선택할 경우
                        else{
                            ClipData clipData = intent.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                if(imageFileCollect.size()+1==6){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                                    builder.setTitle("알림");
                                    builder.setMessage("이미지는 최대 5개까지만 업로드 가능합니다.");
                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            imageAdapter.notifyDataSetChanged();
                                            imageNumberText.setText(imageList.size()+"/5");
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                    return;
                                }
                                Log.e("123", "444");
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                Log.e("123",imageUri.toString());
                                Data_post_image data = new Data_post_image(imageUri);
                                imageList.add(data);
                                //사진 저장 및 절대경로 받아오는 코드 찾아보고 이해하기..
                                File uriFile = new File(createCopyAndReturnRealPath(imageUri,"image"+i));
                                imageFileCollect.add(uriFile);

                                //Log.e("123", uriFile.getPath());
                            }
                            imageAdapter.notifyDataSetChanged();
                            imageNumberText.setText(imageList.size()+"/5");
                        }

                    }
                    else{
                        Toast.makeText(Activity_post_write.this, "아무것도 선택안함", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    /* 이미지 파일을 복사한 후, 그 파일의 절대 경로 반환하는 메소드 */
    public String createCopyAndReturnRealPath(Uri uri, String fileName) {
        final ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null)
            return null;
        // 내부 저장소 안에 위치하도록 파일 생성
        String filePath = getApplicationInfo().dataDir + File.separator + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
        File file = new File(filePath);
        // 서버에 이미지 업로드 후 삭제하기 위해 경로를 저장해둠
        //tempFileList.add(filePath);

        try {
            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 가져온다.
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;
            // 가져온 이미지 데이터를 아까 생성한 파일에 저장한다.
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath(); // 생성한 파일의 절대경로 반환
    }

    public void variableInit(){
        imageFileCollect= new ArrayList<>();
        //tempFileList=new ArrayList<>();
        files = new ArrayList<>();

        //recyclerview 관련 준비
        imageRecyclerview = findViewById(R.id.recyclerView_post_write_image);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        // LinearLayoutManager.HORIZONTAL, false
        imageList = new ArrayList<Data_post_image>();
        imageAdapter = new Adapter_post_image(imageList);

        imageRecyclerview.setLayoutManager(linearLayoutManager);
        imageRecyclerview.setAdapter(imageAdapter);

        //
        imageNumberText=findViewById(R.id.post_write_image_num);
        selectImage = findViewById(R.id.post_write_image_choice_image);
        postWriteText = findViewById(R.id.post_write_complete);

        //retrofit
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i=0; i<imageFileCollect.size();i++){
            if(imageFileCollect.get(i).exists()){
                boolean result=imageFileCollect.get(i).delete();
                Log.e("123",String.valueOf(result));
            }

        }
    }
}