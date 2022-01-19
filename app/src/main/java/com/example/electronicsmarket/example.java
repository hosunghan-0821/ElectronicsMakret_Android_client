package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class example extends AppCompatActivity {

    Retrofit retrofit;
    ArrayList<MultipartBody.Part> files;

    HashMap<String,RequestBody> requestMap;
    RequestBody textBody,nameBody,realBody;
    File[]  imagefiles= new File[5];
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        textBody = RequestBody.create(MediaType.parse("text/plain"),"하이");
        nameBody = RequestBody.create(MediaType.parse("text/plain"),"123ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ\nㅎㅎㅎㅎㅎ\nggㅎ" );
        realBody =RequestBody.create(MediaType.parse("text/plain"),"거래글 카테고리 어쩌구 저쩌구" );

        requestMap= new HashMap<>();
        requestMap.put("name",nameBody);
        requestMap.put("text",textBody);
        requestMap.put("real",realBody);
        button=findViewById(R.id.example_button);
        checkPermission();

        files = new ArrayList<MultipartBody.Part>();

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                PackageManager manager = getApplicationContext().getPackageManager();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                if (infos.size() > 0){
                   Log.e("123","123");
                }else{
                    Log.e("123","456");
                }
                galleryLauncher.launch(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                      if(result.getResultCode()==RESULT_OK){

                          if(result.getData().getClipData()==null){
                              return;
                          }
                          Log.e("123","들어옴");
                          files.clear();


                          ClipData clipData = result.getData().getClipData();
                          Log.e("clipData", String.valueOf(clipData.getItemCount()));

                          for (int i = 0; i < clipData.getItemCount(); i++){
                              Uri image1Uri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                              try {
//                                  Log.e("123",image1Uri.toString())  ;//uri를 list에 담는다.
                                  File uriFile = new File(getRealPathFromURI(image1Uri));
                                  imagefiles[i]=uriFile;

                                  Log.e("123",String.valueOf(imagefiles[i]));
                                  //파일들은 준비가 됬따.

                                  RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"),imagefiles[i]);

                                  MultipartBody.Part filepart= MultipartBody.Part.createFormData("image"+i,"image",fileBody);

                                  files.add(filepart);
                                  Log.e("123",String.valueOf(i));
                              } catch (Exception e) {
                                Log.e("123",e.getMessage());
                              }
                          }
                          Log.e("123",String.valueOf(files));
                          //여기서 이제 한번에 보내보자
//                          RetrofitService service = retrofit.create(RetrofitService.class);
//                          Call<MemberSignup> call=service.sendMultiImage(files);
//                          call.enqueue(new Callback<MemberSignup>() {
//                              @Override
//                              public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
//                                  Log.e("123","123");
//                              }
//
//                              @Override
//                              public void onFailure(Call<MemberSignup> call, Throwable t) {
//                                  Log.e("123","통신오류류");
//                             }
//                          });

                      }
                      else{
                          Log.e("123","456");
                          files.clear();
                          RetrofitService service = retrofit.create(RetrofitService.class);
//                          Call<MemberSignup> call=service.sendMultiImage(files);
//                          call.enqueue(new Callback<MemberSignup>() {
//                              @Override
//                              public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
//                                  Log.e("123","123");
//                              }
//
//                              @Override
//                              public void onFailure(Call<MemberSignup> call, Throwable t) {
//                                  Log.e("123","통신오류류");
//                              }
//                          });

                      }
                    }
                }
        );

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }
    public  void  checkPermission() {

        String temp = "";
        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.CAMERA + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            //권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else {
            //모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }


    }


}