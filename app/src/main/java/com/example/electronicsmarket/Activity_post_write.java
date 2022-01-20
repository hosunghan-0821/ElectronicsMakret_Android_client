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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_post_write extends AppCompatActivity {



    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private LinearLayout linearCategory;
    private ItemTouchHelper mItemTouchHelper;
    private CheckBox deliverCheckBox;

    private String[] sellTypeItems={"거래방법 선택","직거래","택배거래","직거래/택배거래"};
    ArrayList<File> imageFileCollect;
    //ArrayList<String> tempFileList;
    ArrayList<MultipartBody.Part> files;
    Retrofit retrofit;
    private TextView postWriteText,postImageInfo,categoryText;
    private RecyclerView imageRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_image imageAdapter;
    private ArrayList<Data_post_image> imageList;
    private TextView imageNumberText;
    private ImageView selectImage;
    private EditText postTitle,postPrice,postContents;


    HashMap<String, RequestBody> requestMap = new HashMap<>();
    private Spinner postCategory,postSellType;
    private ArrayAdapter<String> categoryAdapter,sellTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        variableInit();

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
                requestMap = new HashMap<>();
                files.clear();
                if(imageFileCollect.size()==0||postTitle.getText().toString().equals("")||postPrice.getText().toString().equals("")){

                    Toast.makeText(Activity_post_write.this, "값을 제대로 넣으세요", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
                String id=sharedPreferences.getString("userId","");
                RequestBody deliveryCost;
                if(deliverCheckBox.isChecked()){
                    deliveryCost=RequestBody.create(MediaType.parse("text/plain"),"Y");
                }
                else{
                    deliveryCost=RequestBody.create(MediaType.parse("text/plain"),"N");
                }

                RequestBody title=RequestBody.create(MediaType.parse("text/plain"), postTitle.getText().toString());
                RequestBody price=RequestBody.create(MediaType.parse("text/plain"), postPrice.getText().toString());
                RequestBody contents=RequestBody.create(MediaType.parse("text/plain"), postContents.getText().toString());
                RequestBody category=RequestBody.create(MediaType.parse("text/plain"),categoryText.getText().toString());
                RequestBody sellType=RequestBody.create(MediaType.parse("text/plain"),postSellType.getSelectedItem().toString());
                RequestBody email=RequestBody.create(MediaType.parse("text/plain"),id);

                requestMap.put("deliveryCost",deliveryCost);
                requestMap.put("title",title);
                requestMap.put("price",price);
                requestMap.put("contents",contents);
                requestMap.put("category",category);
                requestMap.put("sellType",sellType);
                requestMap.put("email",email);

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<MemberSignup> call = service.sendMultiImage(files,requestMap);
                call.enqueue(new Callback<MemberSignup>() {
                    @Override
                    public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                        Log.e("123", "통신성공");


                        //이미지 업로드 전송 성공하면 임시파일들 삭제.
//                        for(int i=0; i<imageFileCollect.size();i++){
//                            if(imageFileCollect.get(i).exists()){
//                                boolean result=imageFileCollect.get(i).delete();
//                                Log.e("123",String.valueOf(result));
//                            }
//                        }

                    }

                    @Override
                    public void onFailure(Call<MemberSignup> call, Throwable t) {
                        Log.e("123", "통신오류");
                    }
                });


            }
        });


        //카테고리 선택 클릭 리스너

        linearCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Activity_post_write.this,Activity_category_1.class);
                categoryLauncher.launch(intent);
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
                if(imageList.size()==0){
                    postImageInfo.setVisibility(View.VISIBLE);
                }
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
            if(imageFileCollect.size()>=5){
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


        private ActivityResultLauncher<Intent> categoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK){
                            Log.e("123","41223");
                            Intent intent =result.getData();
                            String category= intent.getStringExtra("category");
                            Log.e("123",category);
                        }
                    }
                }
        );

    //갤러리에서 이미지 가져왔을 때, resultLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        postImageInfo.setVisibility(View.INVISIBLE);
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

                            //여기서부터파일 만드는과정

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

                                Log.e("123", uriFile.getPath());
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
        Bitmap bitmap;
        final ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null)
            return null;
        // 내부 저장소 안에 위치하도록 파일 생성
        String filePath = getApplicationInfo().dataDir + File.separator + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
        File file = new File(filePath);

        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmap= ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),uri));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = null;
                try {
                    //파일 생성된 곳에 작성할 수 있도록 outputstream 생성
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    //outputstream을 통해 bitearray[] 로 데이터 저장
                    fos.write(bitmapdata);

                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 서버에 이미지 업로드 후 삭제하기 위해 경로를 저장해둠
        //tempFileList.add(filePath);

//        try {
//            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 가져온다.
//            InputStream inputStream = contentResolver.openInputStream(uri);
//            if (inputStream == null)
//                return null;
//            // 가져온 이미지 데이터를 아까 생성한 파일에 저장한다.
//            OutputStream outputStream = new FileOutputStream(file);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = inputStream.read(buf)) > 0)
//                outputStream.write(buf, 0, len);
//            outputStream.close();
//            inputStream.close();
//        } catch (IOException ignore) {
//            return null;
//        }


        return file.getAbsolutePath(); // 생성한 파일의 절대경로 반환
    }

    public void variableInit(){
        //shared
        sharedPreferences=getSharedPreferences("category",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        //
        categoryText=findViewById(R.id.post_write_category_text);
        deliverCheckBox=findViewById(R.id.deliver_cost_checkbox);

        linearCategory=findViewById(R.id.linear_category);

        postImageInfo=findViewById(R.id.post_image_info_text);
        postTitle=findViewById(R.id.post_write_title);
        postPrice= findViewById(R.id.post_write_price);
        postContents=findViewById(R.id.post_write_contents);

        //spinner 관련
        postSellType=findViewById(R.id.post_write_sell_type);
        sellTypeAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,sellTypeItems);
        postSellType.setAdapter(sellTypeAdapter);

        //recyclerview 이미지 관련된 코드
        imageFileCollect= new ArrayList<>();
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
    protected void onResume() {
        super.onResume();
        // shared 값 가져오기

             String category=sharedPreferences.getString("category","");
             categoryText.setText(category);
             if(categoryText.getText().toString().equals("")){
                 categoryText.setText("카테고리 선택");
             }
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