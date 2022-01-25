package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_profile_update extends AppCompatActivity {

    public static final String TAG="ACTIVITY_PROFILE_UPDATE";
    Button updateCompleteBtn,nicknameCheckBtn;
    CircleImageView circleImageView;
    Retrofit retrofit;
    File imageFile=null;
    File uriFile;
    Bitmap bitmap;
    Uri selectedImage;
    SharedPreferences sharedPreferences;
    ImageView backImage,changeImage;
    EditText nickname;
    String originNickname;
    boolean isImageChange=false;
    boolean imageChangeF,nicknameCheck,standardImage=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_update);
        variableInit();
        setProfile(getApplicationContext());
        checkPermission();

        nicknameCheckBtn.setOnClickListener(nicknameClick);
        backImage.setOnClickListener(backImageClick);
        updateCompleteBtn.setOnClickListener(changeProfile);

        changeImage.setOnClickListener(changeClick);
        circleImageView.setOnClickListener(changeClick);

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("123","123");
                nicknameCheckBtn.setVisibility(View.VISIBLE);
                if(nickname.getText().toString().equals(originNickname)){
                    nicknameCheckBtn.setVisibility(View.INVISIBLE);
                    nicknameCheck=true;
                }
                else{
                    nicknameCheck=false;
                }
            }
        });
    }

    View.OnClickListener changeClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(Activity_profile_update.this, circleImageView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.profile_update_menu, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.gallery) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        //intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        galleryLauncher.launch(intent);

                    } else if (id == R.id.camera) {
                        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(intent);

                    } else {
                        circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                        standardImage=true;
                        imageFile=null;
                    }
                    return false;
                }
            });

        }
    };

    View.OnClickListener nicknameClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(nickname.getText().toString().equals(originNickname)){
                return;
            }
            AlertDialog.Builder builder =new AlertDialog.Builder(Activity_profile_update.this);
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<MemberSignup> call = service.sendNickname("nicknameCheck.php",nickname.getText().toString());
            call.enqueue(new Callback<MemberSignup>() {
                @Override
                public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                    if(response.isSuccessful()){
                        if(response.body().isSuccess()){
                            Log.e("123","123456");
                            nickname.setTextColor(Color.BLUE);
                            nickname.clearFocus();
                            builder.setTitle("닉네임 사용 가능합니다.");
                            nicknameCheck=true;
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }

                        else{
                            nickname.setTextColor(Color.RED);
                            builder.setTitle("닉네임 중복입니다.");
                            nicknameCheck=false;
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<MemberSignup> call, Throwable t) {

                }
            });
        }
    };
    View.OnClickListener backImageClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            (Activity_profile_update.this).finish();
        }
    };

    View.OnClickListener changeProfile =new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //우선 닉네임 중복체크 하고
            if(!nickname.getText().toString().equals(originNickname)){
                if(nicknameCheck){

                }
                else{
                    Toast.makeText(Activity_profile_update.this, "중복확인", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // 중복확인 끝나고 변경 절차가 시작
            SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
            String id=sharedPreferences.getString("userId","");


            // 닉네임바뀌었으면, 닉네임 변경
            if(!nickname.getText().toString().equals(originNickname)){
                Log.e("123","닉네임변경");
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<MemberSignup> call = service.setNickname(id,nickname.getText().toString());
                call.enqueue(new Callback<MemberSignup>() {
                    @Override
                    public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

                        if(response.isSuccessful()&&response.body()!=null){
                            if(response.body().isSuccess()){
                                //닉네임 변경 성공하고 나서, 이미지가 바뀐지 확인하고  바뀌엇으면 처리
                                if(isImageChange&&imageFile!=null){
                                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                                    MultipartBody.Part body= MultipartBody.Part.createFormData("upload",imageFile.getName(),reqFile);
                                    Log.e("123","이미지 변경");
                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<MemberSignup> call2 = service.setProfile(body,id);

                                    call2.enqueue(new Callback<MemberSignup>() {
                                        @Override
                                        public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

                                            if(response.isSuccessful()&& response.body()!=null ){
                                                if(response.body().getMessage().equals("업로드 성공")){
                                                    imageFile=null;
                                                    //Toast.makeText(Activity_profile_update.this, "변경완료", Toast.LENGTH_SHORT).show();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile_update.this);
                                                    builder.setTitle("프로필이 변경되었습니다.");
                                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            (Activity_profile_update.this).finish();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<MemberSignup> call, Throwable t) {
                                            Toast.makeText(Activity_profile_update.this, "통신오류", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                //이미지가 기본이미지로 되었을 경우
                                else if(standardImage){
                                    Log.e("123","666");
                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<MemberSignup> call2 = service.deleteProfile(id);
                                    call2.enqueue(new Callback<MemberSignup>() {
                                        @Override
                                        public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile_update.this);
                                            builder.setTitle("프로필이 변경되었습니다.");
                                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    (Activity_profile_update.this).finish();
                                                }
                                            });
                                            builder.show();

                                        }

                                        @Override
                                        public void onFailure(Call<MemberSignup> call, Throwable t) {
                                            Toast.makeText(Activity_profile_update.this, "통신오류", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile_update.this);
                                    builder.setTitle("프로필이 변경되었습니다.");
                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            (Activity_profile_update.this).finish();
                                        }
                                    });
                                    builder.show();
                                }

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "아이디중복", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MemberSignup> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //이미지가 바뀌었으면 이미지만 바꾸기

                //닉네임 변경 성공하고 나서, 이미지가 바뀐지 확인하고  바뀌엇으면 처리
                else if(isImageChange&&imageFile!=null){
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part body= MultipartBody.Part.createFormData("upload",imageFile.getName(),reqFile);
                    Log.e("123","이미지 변경");
                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<MemberSignup> call2 = service.setProfile(body,id);
                    call2.enqueue(new Callback<MemberSignup>() {
                        @Override
                        public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

                            if(response.isSuccessful()&& response.body()!=null ){
                                if(response.body().getMessage().equals("업로드 성공")){
                                    imageFile=null;
                                    //Toast.makeText(Activity_profile_update.this, "변경완료", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile_update.this);
                                    builder.setTitle("프로필이 변경되었습니다.");
                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            (Activity_profile_update.this).finish();
                                        }
                                    });
                                    builder.show();

                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MemberSignup> call, Throwable t) {
                            Toast.makeText(Activity_profile_update.this, "통신오류", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                //이미지가 기본이미지로 되었을 경우
                else if(standardImage){
                    Log.e("123","666");
                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<MemberSignup> call2 = service.deleteProfile(id);
                    call2.enqueue(new Callback<MemberSignup>() {
                        @Override
                        public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile_update.this);
                            builder.setTitle("프로필이 변경되었습니다.");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    (Activity_profile_update.this).finish();
                                }
                            });
                            builder.show();
                        }
                        @Override
                        public void onFailure(Call<MemberSignup> call, Throwable t) {
                            Toast.makeText(Activity_profile_update.this, "통신오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                Toast.makeText(Activity_profile_update.this, "변한게 없습니다.", Toast.LENGTH_SHORT).show();
            }



        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //권한을 허용 했을 경우
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("MainActivity", "권한 허용 : " + permissions[i]);
                }
            }
        }
    }

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        //받아온 uri 저장 후 , 절대경로 얻은 후 -> bitmap 으로바꾸고 -> 클라이언트 단에서 미리 이미지 띄우기
                        //그 다음 비트맵을 파일로 변경
                        Log.e("123","1234");
                        selectedImage = result.getData().getData();
                        //uri를 파일로 만들어서 전송하는방법.
                        //uriToFile(getApplicationContext(),selectedImage);

                        Log.e("1234",selectedImage.toString());
                        bitmap = BitmapFactory.decodeFile(getRealPathFromURI(selectedImage));
                        circleImageView.setImageBitmap(bitmap);
                        bitmapToFile(getApplicationContext(),bitmap);
                        standardImage=false;
                    }
                    else{
                        Log.e("123","이미지 선택 x");
                    }
                }
            }
    );
    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){

                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        Bitmap cameraBitmap = (Bitmap)bundle.get("data");
                        circleImageView.setImageBitmap(cameraBitmap);

                        bitmapToFile(getApplicationContext(),cameraBitmap);
                        standardImage=false;
                    }
                }
            }
    );



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

    private void uriToFile(Context context,Uri uri){
        Log.e("123","함수 들어옴");
        //File uriFile = new File(context.getCacheDir(),"image");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        uriFile = new File(getCacheDir(),getRealPathFromURI(uri));
        Log.e("123",uriFile.getPath().toString());
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), uriFile);
        MultipartBody.Part body= MultipartBody.Part.createFormData("upload",uriFile.getName(),reqFile);

        Log.e("123",uriFile.getPath());
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<MemberSignup> call2 = service.setProfile(body,"winsomed96@naver.com");
        call2.enqueue(new Callback<MemberSignup>() {
            @Override
            public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {

            }

            @Override
            public void onFailure(Call<MemberSignup> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void bitmapToFile(Context context, Bitmap bitmap){
        imageFile= new File(context.getCacheDir(),"profile");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            //파일 생성된 곳에 작성할 수 있도록 outputstream 생성
            fos = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //outputstream을 통해 bitearray[] 로 데이터 저장
            fos.write(bitmapdata);
            isImageChange=true;
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    private void setProfile(Context context){

        sharedPreferences= context.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String id=sharedPreferences.getString("userId","");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<MemberSignup> call = service.getProfile("getProfile.php",id);
        call.enqueue(new Callback<MemberSignup>() {
            @Override
            public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {
                MemberSignup memberInfo=response.body();
                nickname.setText(memberInfo.getNickname());
                originNickname=memberInfo.getNickname();

                String imageRoute= memberInfo.imageRoute;
                if(imageRoute.equals("")){
                    circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                }
                else{
                    Glide.with(context).load(imageRoute).into(circleImageView);
                }

            }
            @Override
            public void onFailure(Call<MemberSignup> call, Throwable t) {

            }
        });
    }

    public void variableInit(){
        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        changeImage=findViewById(R.id.change_icon);
        circleImageView = findViewById(R.id.update_profile_image);
        updateCompleteBtn=findViewById(R.id.update_complete_button);
        backImage=findViewById(R.id.back_image);
        nickname=findViewById(R.id.update_nickname);
        nicknameCheckBtn=findViewById(R.id.duplicate_button);


    }
}