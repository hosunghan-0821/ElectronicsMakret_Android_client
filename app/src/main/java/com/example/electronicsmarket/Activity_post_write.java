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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
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


    InputMethodManager imm;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private LinearLayout linearCategory;
    private ItemTouchHelper mItemTouchHelper;
    private CheckBox deliverCheckBox;
    private ImageView backImage;

    private String locationPlaceName, locationAddressName, locationLatitude, locationLongitude, imageListInfo = "";
    private String deleteImage = "";
    private String postNum, sellTypeText = "";
    ArrayList<File> imageFileCollect;
    ArrayList<String> imageStringRoute;
    ArrayList<MultipartBody.Part> files;
    Retrofit retrofit;

    private TextView postWriteText, postImageInfo, categoryText, postLocationtext,postLocationGuideText, postReadCompleteText;
    private RecyclerView imageRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_post_image imageAdapter;
    private ArrayList<Data_post_image> imageList;
    private TextView imageNumberText;
    private ImageView selectImage, updateDeleteImage;
    private EditText postTitle, postPrice, postContents,postLocationInfo;
    private LinearLayout linearLocationSelect,linearLocationSelectInfo;

    HashMap<String, RequestBody> requestMap = new HashMap<>();
    private Spinner postCategory;
    private ArrayAdapter<String> categoryAdapter, sellTypeAdapter;
    private String strAmount = "";
    private RadioGroup radioGroup;
    private RadioButton radioALL, radioDirect, radioDelivery;//
    private boolean update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        variableInit();

        selectImage.setOnClickListener(imageClick);

        //??????????????????
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();

        update = intent.getBooleanExtra("update", false);
        if (update) {
            postReadCompleteText.setText("??????");
            postNum = intent.getStringExtra("postNum");
            Log.e("123", "????????????");

            //???????????? ??? ?????? ????????? ?????? ???????????? ????????? ????????????
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<PostInfo> call = service.getPostInfo(postNum,"write","update");
            call.enqueue(new Callback<PostInfo>() {
                @Override
                public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PostInfo data = response.body();

                        //???????????? shared??? ?????? ??? ???????????????.
                        // shared ??? ????????????
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("category",data.getPostCategory());
                        editor.commit();
                        //???????????? ??????
                        locationPlaceName = data.getPostLocationName();
                        locationAddressName = data.getPostLocationAddress();
                        locationLatitude = String.valueOf(data.getPostLocationLatitude());
                        locationLongitude = String.valueOf(data.getPostLocationLongitude());

                        postLocationtext.setText(data.getPostLocationName());
                        postLocationInfo.setText(data.getPostLocationDetail());
                        postTitle.setText(data.getPostTitle());
                        postPrice.setText(data.getPostPrice());
                        postContents.setText(data.getPostContents());
                        categoryText.setText(data.getPostCategory());
                        if (data.getPostDelivery().equals("Y")) {
                            deliverCheckBox.setChecked(true);
                        } else {
                            deliverCheckBox.setChecked(false);
                        }
                        Log.e("123", data.getPostSellType());
                        if (data.getPostSellType().equals("?????????/????????????")) {
                            radioALL.toggle();
                            //????????? ?????? ????????? ????????????, ??????,?????? ??????????????? ?????? ?????? ?????? ???????????????.
                        } else if (data.getPostSellType().equals("?????????")) {
                            radioDirect.toggle();
                            //????????? ?????? ????????? ????????????, ??????,?????? ??????????????? ?????? ?????? ?????? ???????????????.
                        } else if (data.getPostSellType().equals("????????????")) {
                            radioDelivery.toggle();
                        }
                        for (int i = 0; i < data.getImageRoute().size(); i++) {
                            Data_post_image imageURL = new Data_post_image();
                            imageURL.setImgUrl(data.getImageRoute().get(i));
                            imageList.add(imageURL);
                        }
                        postImageInfo.setVisibility(View.INVISIBLE);
                        imageAdapter.setImageList(imageList);
                        imageAdapter.notifyDataSetChanged();
                        imageNumberText.setText(imageList.size() + "/5");
                    }
                }

                @Override
                public void onFailure(Call<PostInfo> call, Throwable t) {

                }
            });

        } else {
            Log.e("123", "??????????????????");
        }


        //?????? ??????
        linearLocationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_post_write.this, Activity_place_search_previous.class);
                locationLauncher.launch(intent);
            }
        });

        //EDIT_Text ????????? listener
        postLocationInfo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){

                  default:
                      imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(postLocationInfo.getWindowToken(), 0);
                      postLocationInfo.clearFocus();

                    break;
                }
                return true;
            }
        });

        //radioGroup onclickListener;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_all) {
                    postLocationGuideText.setVisibility(View.VISIBLE);
                    linearLocationSelect.setVisibility(View.VISIBLE);
                    linearLocationSelectInfo.setVisibility(View.VISIBLE);
                    deliverCheckBox.setVisibility(View.VISIBLE);
                    sellTypeText = "?????????/????????????";
                    // ?????????/ ????????????
                } else if (checkedId == R.id.radio_button_direct) {
                    postLocationGuideText.setVisibility(View.VISIBLE);
                    linearLocationSelect.setVisibility(View.VISIBLE);
                    linearLocationSelectInfo.setVisibility(View.VISIBLE);
                    deliverCheckBox.setVisibility(View.INVISIBLE);
                    sellTypeText = "?????????";
                    // ?????????
                } else {
                    postLocationGuideText.setVisibility(View.GONE);
                    linearLocationSelect.setVisibility(View.GONE);
                    linearLocationSelectInfo.setVisibility(View.GONE);
                    deliverCheckBox.setVisibility(View.VISIBLE);
                    sellTypeText = "????????????";
                    //????????????
                }

            }
        });

        //?????? , ????????? ????????????
        postPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(strAmount)) {
                    strAmount = makeStringComma(s.toString().replace(",", ""));
                    postPrice.setText(strAmount);
                    Editable editable = postPrice.getText();
                    Selection.setSelection(editable, strAmount.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(imageAdapter, new ItemTouchHelperListener() {
            @Override
            public boolean onItemMove(int from_position, int to_position) {

                //update ????????? ????????? ??????, ???????????? ??? ????????? ????????? ????????? ???????????? ????????? ?????????recyclerview????????? ???????????????.
                if (update) {
                    Collections.swap(imageList, from_position, to_position);
                    imageAdapter.setImageList(imageList);
                    imageAdapter.notifyDataSetChanged();
                    return false;
                }

                //????????? ????????? ???!
                //recyclerview arrayList ????????? ?????????, imageUriFile ????????? ????????????.
                Data_post_image data = imageList.get(from_position);
                imageList.remove(from_position);
                imageList.add(to_position, data);
                imageAdapter.setImageList(imageList);
                imageAdapter.notifyDataSetChanged();
                imageNumberText.setText(imageList.size() + "/5");

                Collections.swap(imageFileCollect, from_position, to_position);
                Log.e("123", imageFileCollect.toString());
                return false;
            }

            @Override
            public void onItemSwipe(int position) {

            }
        }));
        mItemTouchHelper.attachToRecyclerView(imageRecyclerview);


        //????????? ???????????? ?????? ?????????
        postWriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageListInfo = "";
                requestMap = new HashMap<>();
                files.clear();


                //????????? ??? ??????????????? ??????
                if (update) {
                    //????????? ?????? ??????????????? ?????? ?????? ???????????? ?????? ?????? ????????????.
                    for (int i = 0; i < imageList.size(); i++) {

                        //?????? ????????? ????????? ?????? ?????? ???????????? ?????? ?????????
                        if (imageList.get(i).getImguri() != null) {

                            Log.e("123", (i + 1) + "?????? ????????? ?????? ????????? ??????????????? ");
                            File uriFile = new File(createCopyAndReturnRealPath(imageList.get(i).getImguri(), "image" + i));
                            Log.e("123", uriFile.getPath());
                            imageFileCollect.add(uriFile);
                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), uriFile);
                            MultipartBody.Part filepart = MultipartBody.Part.createFormData("image" + i, "image", fileBody);
                            files.add(filepart);
                            Log.e("123", files.toString());
                            //??????
                        }

                    }

                    //????????? ????????? ????????? ?????? ????????? ????????????
                    Log.e("123", String.valueOf(imageList.size()));
                    for (int i = 0; i < imageList.size(); i++) {

                        if (imageList.get(i).getImguri() != null) {
                            String str = "image";
                            imageListInfo += str + i + "///" + i + "///";
                        } else {
                            imageListInfo += "" + (imageList.get(i).getImgUrl()) + "///" + i + "///";
                        }

                    }
                    //???????????? ?????????????????? //?????? ????????? ?????? ????????? ???????????????.
                }

                //????????? ????????? ?????? ???????????? ????????????
                RequestBody imageInfo = RequestBody.create(MediaType.parse("text/plain"), imageListInfo);
                requestMap.put("imageListInfo", imageInfo);

                if (!update) {
                    if (imageList.size() == 0 || postTitle.getText().toString().equals("") || postPrice.getText().toString().equals("")) {

                        Toast.makeText(Activity_post_write.this, "?????? ??????1??? , ??????,?????? ?????? ????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (imageList.size() == 0 || postTitle.getText().toString().equals("") || postPrice.getText().toString().equals("")) {

                        Toast.makeText(Activity_post_write.this, "?????? ??????1??? ??????,?????? ?????? ????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //????????? ??????

                //???????????? ?????? ????????????
                if (!(radioALL.isChecked() || radioDirect.isChecked() || radioDelivery.isChecked())) {
                    Toast.makeText(Activity_post_write.this, "???????????? ??????", Toast.LENGTH_SHORT).show();
                    return;
                }
                //???????????? ??????
                RequestBody placeName, addressName, Longitude, latitude,placeDetail;
                if (postLocationtext.getText().toString().equals("????????? ???????????????") || radioDelivery.isChecked()) {
                    placeName = RequestBody.create(MediaType.parse("text/plain"), "");
                    addressName = RequestBody.create(MediaType.parse("text/plain"), "???????????? ??????");
                    Longitude = RequestBody.create(MediaType.parse("text/plain"), "");
                    latitude = RequestBody.create(MediaType.parse("text/plain"), "");
                    placeDetail =  RequestBody.create(MediaType.parse("text/plain"), "");
                } else {
                    placeName = RequestBody.create(MediaType.parse("text/plain"), locationPlaceName);
                    addressName = RequestBody.create(MediaType.parse("text/plain"), locationAddressName);
                    Longitude = RequestBody.create(MediaType.parse("text/plain"), locationLongitude);
                    latitude = RequestBody.create(MediaType.parse("text/plain"), locationLatitude);
                    placeDetail = RequestBody.create(MediaType.parse("text/plain"),postLocationInfo.getText().toString());
                }

                //?????? ?????? ?????? ?????????????????? ?????????
                if (!update) {

                    for (int i = 0; i < imageFileCollect.size(); i++) {
                        try {
                            Log.e("123", String.valueOf(i));
                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFileCollect.get(i));
                            MultipartBody.Part filepart = MultipartBody.Part.createFormData("image" + i, "image", fileBody);
                            files.add(filepart);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("123", files.toString());
                    }
                }
                SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
                String id = sharedPreferences.getString("userId", "");
                RequestBody deliveryCost;
                if (deliverCheckBox.isChecked() && (radioALL.isChecked() || radioDelivery.isChecked())) {
                    deliveryCost = RequestBody.create(MediaType.parse("text/plain"), "Y");
                } else {
                    deliveryCost = RequestBody.create(MediaType.parse("text/plain"), "N");
                }

                RequestBody deleteImageRoute = RequestBody.create(MediaType.parse("text/plain"), deleteImage);
                RequestBody title = RequestBody.create(MediaType.parse("text/plain"), postTitle.getText().toString());
                RequestBody price = RequestBody.create(MediaType.parse("text/plain"), postPrice.getText().toString());
                RequestBody contents = RequestBody.create(MediaType.parse("text/plain"), postContents.getText().toString());
                RequestBody category = RequestBody.create(MediaType.parse("text/plain"), categoryText.getText().toString());
                RequestBody sellType = RequestBody.create(MediaType.parse("text/plain"), sellTypeText);
                RequestBody email = RequestBody.create(MediaType.parse("text/plain"), id);


                requestMap.put("placeDetail",placeDetail);
                requestMap.put("deleteImage", deleteImageRoute);
                requestMap.put("placeName", placeName);
                requestMap.put("addressName", addressName);
                requestMap.put("longitude", Longitude);
                requestMap.put("latitude", latitude);
                requestMap.put("deliveryCost", deliveryCost);
                requestMap.put("title", title);
                requestMap.put("price", price);
                requestMap.put("contents", contents);
                requestMap.put("category", category);
                requestMap.put("sellType", sellType);
                requestMap.put("email", email);

                RetrofitService service = retrofit.create(RetrofitService.class);
                //??????????????? ?????? ?????? php ??? ????????? ?????????, ???????????????
                if (update) {
                    RequestBody postNumBody = RequestBody.create(MediaType.parse("text/plain"), postNum);
                    requestMap.put("postNum", postNumBody);
                    Call<DataMemberSignup> updateCall = service.sendUpdate(files, requestMap);
                    updateCall.enqueue(new Callback<DataMemberSignup>() {
                        @Override
                        public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {

                            Log.e("123", "????????????");
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().isSuccess()) {
                                    //????????? ????????? ?????? ???????????? ??????????????? ??????.
                                    for (int i = 0; i < imageFileCollect.size(); i++) {
                                        if (imageFileCollect.get(i).exists()) {
                                            boolean result = imageFileCollect.get(i).delete();
                                            Log.e("123", String.valueOf(result));
                                        }
                                    }

                                    Intent intent = new Intent(Activity_post_write.this, Activity_post_read.class);
                                    intent.putExtra("postNum", response.body().getMessage());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                            Log.e("123", "????????????");
                        }
                    });
                    return;
                }

                //????????? ?????? ?????? ?????????
                Call<DataMemberSignup> call = service.sendMultiImage(files, requestMap);
                call.enqueue(new Callback<DataMemberSignup>() {
                    @Override
                    public void onResponse(Call<DataMemberSignup> call, Response<DataMemberSignup> response) {
                        Log.e("123", "????????????");
                        if (response.isSuccessful() && response.body() != null) {
                            //????????? ????????? ?????? ???????????? ??????????????? ??????.
                            for (int i = 0; i < imageFileCollect.size(); i++) {
                                if (imageFileCollect.get(i).exists()) {
                                    boolean result = imageFileCollect.get(i).delete();
                                    Log.e("123", String.valueOf(result));
                                }
                            }

                            Intent intent = new Intent(Activity_post_write.this, Activity_post_read.class);
                            intent.putExtra("postNum", response.body().getMessage());
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<DataMemberSignup> call, Throwable t) {
                        Log.e("123", "????????????");
                    }
                });
            }
        });

        //???????????? ?????? ?????? ?????????
        linearCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_post_write.this, Activity_category_1.class);
                categoryLauncher.launch(intent);
            }
        });

        //????????? ?????? ??? ???????????? ?????? ?????????.
        imageAdapter.setListener(new Interface_post_listener() {
            @Override
            public void onItemClick(Adapter_post_image.ImageViewholder imageViewholder, int position) {


                if (update) {

                    if (imageList.get(position).getImguri() == null) {
                        deleteImage += imageList.get(position).getImgUrl() + "///";
                        Log.e("123", deleteImage);
                    }
                    imageList.remove(position);
                    imageAdapter.setImageList(imageList);
                    imageAdapter.notifyItemRemoved(position);
                    imageNumberText.setText(imageList.size() + "/5");
                    return;
                }

                if (imageFileCollect.get(position).exists()) {

                    boolean result = imageFileCollect.get(position).delete();
                    Log.e("123", String.valueOf(result));

                }
                imageFileCollect.remove(position);

                imageList.remove(position);
                if (imageList.size() == 0) {
                    postImageInfo.setVisibility(View.VISIBLE);
                }
                imageAdapter.notifyItemRemoved(position);
                imageNumberText.setText(imageList.size() + "/5");
                Log.e("123", imageFileCollect.toString());
            }
        });

    }

    private ActivityResultLauncher<Intent> locationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Log.e("123", "locationLauncehr");

                        locationPlaceName = intent.getStringExtra("location_placeName");
                        locationAddressName = intent.getStringExtra("location_addressName");
                        locationLatitude = intent.getStringExtra("location_latitude");
                        locationLongitude = intent.getStringExtra("location_longitude");
                        postLocationtext.setText(locationPlaceName);
                        linearLocationSelectInfo.setVisibility(View.VISIBLE);
                    }
                }
            });


    // 1000?????? ??????
    protected String makeStringComma(String str) {    // ????????? ????????????.
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    //??????????????? ????????? ????????????.
    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (imageFileCollect.size() >= 5) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                builder.setTitle("??????");
                builder.setMessage("???????????? ?????? 5???????????? ????????? ???????????????.");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    if (result.getResultCode() == RESULT_OK) {
                        Log.e("123", "41223");
                        Intent intent = result.getData();
                        String category = intent.getStringExtra("category");
                        Log.e("123", category);
                    }
                }
            }
    );

    //??????????????? ????????? ???????????? ???, resultLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        postImageInfo.setVisibility(View.INVISIBLE);
                        //???????????? ????????? ????????? ??????.
                        files.clear();
                        Intent intent = result.getData();
                        //????????? ????????????

                        if (intent.getClipData() == null) {

                            if (imageList.size() + 1 == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                                builder.setTitle("??????");
                                builder.setMessage("???????????? ?????? 5???????????? ????????? ???????????????.");
                                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                                return;
                            }
                            Log.e("123", "????????? ?????? ??????");
                            Uri imageUri = intent.getData();
                            Data_post_image data = new Data_post_image(imageUri);
                            data.setImgUrl(imageUri.toString());
                            imageList.add(data);

                            //????????? ??????(??????o)
                            if (update) {
                                imageAdapter.setImageList(imageList);
                                imageAdapter.notifyDataSetChanged();
                                imageNumberText.setText(imageList.size() + "/5");
                                return;
                            }

                            //????????? ??????(??????x) ????????????????????? ???????????????

                            File uriFile = new File(createCopyAndReturnRealPath(imageUri, "image"));
                            imageFileCollect.add(uriFile);
                            imageAdapter.notifyDataSetChanged();
                            imageNumberText.setText(imageList.size() + "/5");
                        }

                        //????????? ????????? ????????? ??????
                        else {
                            ClipData clipData = intent.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                if (imageList.size() + 1 == 6) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_post_write.this);

                                    builder.setTitle("??????");
                                    builder.setMessage("???????????? ?????? 5???????????? ????????? ???????????????.");
                                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            imageAdapter.notifyDataSetChanged();
                                            imageNumberText.setText(imageList.size() + "/5");
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                    return;
                                }
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                //Log.e("123", imageUri.toString());
                                Data_post_image data = new Data_post_image(imageUri);
                                data.setImgUrl(imageUri.toString());
                                imageList.add(data);

                                //????????? ??????(??????o)
                                if (update) {

                                }
                                //?????? ?????? ??? ???????????? ???????????? ?????? ???????????? ????????????..
                                //????????? ??????(??????x) ????????????????????? ???????????????
                                else {
                                    File uriFile = new File(createCopyAndReturnRealPath(imageUri, "image" + i));
                                    imageFileCollect.add(uriFile);

                                    Log.e("123", uriFile.getPath());
                                }

                            }
                            imageAdapter.setImageList(imageList);
                            imageAdapter.notifyDataSetChanged();
                            imageNumberText.setText(imageList.size() + "/5");
                        }

                    } else {
                        Toast.makeText(Activity_post_write.this, "???????????? ????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    /* ????????? ????????? ????????? ???, ??? ????????? ?????? ?????? ???????????? ????????? */
    public String createCopyAndReturnRealPath(Uri uri, String fileName) {
        Bitmap bitmap;
        final ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null)
            return null;
        // ?????? ????????? ?????? ??????????????? ?????? ??????
        String filePath = getApplicationInfo().dataDir + File.separator + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(filePath);

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = null;
                try {
                    //?????? ????????? ?????? ????????? ??? ????????? outputstream ??????
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    //outputstream??? ?????? bitearray[] ??? ????????? ??????
                    fos.write(bitmapdata);

                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ????????? ????????? ????????? ??? ???????????? ?????? ????????? ????????????
        //tempFileList.add(filePath);

//        try {
//            // ??????????????? ?????? uri ??? ??????  ???????????? ????????? ???????????? ????????????.
//            InputStream inputStream = contentResolver.openInputStream(uri);
//            if (inputStream == null)
//                return null;
//            // ????????? ????????? ???????????? ?????? ????????? ????????? ????????????.
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


        return file.getAbsolutePath(); // ????????? ????????? ???????????? ??????
    }

    public void variableInit() {
        //shared
        sharedPreferences = getSharedPreferences("category", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        //
        postReadCompleteText=findViewById(R.id.post_write_complete);
        postLocationGuideText=findViewById(R.id.post_write_location_guide_text);
        backImage = findViewById(R.id.post_write_back_arrow);
        postLocationtext = findViewById(R.id.post_write_location_text);
        categoryText = findViewById(R.id.post_write_category_text);
        deliverCheckBox = findViewById(R.id.deliver_cost_checkbox);
        linearLocationSelect = findViewById(R.id.linear_location_select);

        linearLocationSelectInfo=findViewById(R.id.linear_location_select_info);
        postLocationInfo=findViewById(R.id.post_write_location_text_info);

        linearCategory = findViewById(R.id.linear_category);

        radioGroup = findViewById(R.id.radio_group);
        radioALL = findViewById(R.id.radio_button_all);
        radioDirect = findViewById(R.id.radio_button_direct);
        radioDelivery = findViewById(R.id.radio_button_delivery);

        postImageInfo = findViewById(R.id.post_image_info_text);
        postTitle = findViewById(R.id.post_write_title);
        postPrice = findViewById(R.id.post_write_price);
        postContents = findViewById(R.id.post_write_contents);

        //????????? ????????????
        imageStringRoute = new ArrayList<>();

        //spinner ??????
//        postSellType = findViewById(R.id.post_write_sell_type);
//        sellTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sellTypeItems);
//        postSellType.setAdapter(sellTypeAdapter);

        //recyclerview ????????? ????????? ??????
        imageFileCollect = new ArrayList<>();
        files = new ArrayList<>();

        //recyclerview ?????? ??????
        imageRecyclerview = findViewById(R.id.recyclerView_post_write_image);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        // LinearLayoutManager.HORIZONTAL, false
        imageList = new ArrayList<Data_post_image>();
        imageAdapter = new Adapter_post_image(imageList, Activity_post_write.this);

        imageRecyclerview.setLayoutManager(linearLayoutManager);
        imageRecyclerview.setAdapter(imageAdapter);

        //
        imageNumberText = findViewById(R.id.post_write_image_num);
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
        // shared ??? ????????????

        String category = sharedPreferences.getString("category", "");
        categoryText.setText(category);
        if (categoryText.getText().toString().equals("")) {
            categoryText.setText("???????????? ??????");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < imageFileCollect.size(); i++) {
            if (imageFileCollect.get(i).exists()) {
                boolean result = imageFileCollect.get(i).delete();
                Log.e("123", String.valueOf(result));
            }

        }
    }
}