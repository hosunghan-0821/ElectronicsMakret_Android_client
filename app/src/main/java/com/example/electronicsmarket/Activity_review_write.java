package com.example.electronicsmarket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_review_write extends AppCompatActivity {

    private String id;

    private ImageView backImage, productImage;
    private TextView reviewWriteProductName, reviewWriteProductPrice, reviewWriteConfirm;
    private RatingBar reviewWriteRatingBar;
    private EditText reviewWriteReview;
    private String postNum;
    private Retrofit retrofit;
    private HashMap<String, RequestBody> map;
    private boolean isReview = false, deleteReview = false;
    private String beforeUpdateRating;
    private TextView reviewWriteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        variableInit();
        Intent intent = getIntent();
        postNum = intent.getStringExtra("postNum");
        isReview = intent.getBooleanExtra("isReview", false);
        deleteReview = intent.getBooleanExtra("deleteReview", false);

        //우선 생성될 때, 기본 정보들 가져와서 화면에 뿌려주기
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<PostInfo> call = service.getPostInfo(postNum, "read", id);
        call.enqueue(new Callback<PostInfo>() {
            @Override
            public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                if (response.isSuccessful() && response.body() != null) {

                    reviewWriteProductName.setText(response.body().getPostTitle());
                    reviewWriteProductPrice.setText(response.body().getPostPrice() + "원");
                    Glide.with(Activity_review_write.this).load(response.body().getImageRoute().get(0)).into(productImage);
                }
            }

            @Override
            public void onFailure(Call<PostInfo> call, Throwable t) {

            }
        });

        //수정일 경우,기존 데이터 세팅해주기
        if (isReview) {
            reviewWriteTitle.setText("리뷰 수정하기");

            Call<ReviewInfo> settingReview = service.getReviewOneInfo(postNum);
            settingReview.enqueue(new Callback<ReviewInfo>() {
                @Override
                public void onResponse(Call<ReviewInfo> call, Response<ReviewInfo> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        beforeUpdateRating = response.body().getReviewRating();
                        reviewWriteReview.setText(response.body().getReviewContents());
                        reviewWriteRatingBar.setRating(Float.parseFloat(response.body().getReviewRating()));
                    }
                }

                @Override
                public void onFailure(Call<ReviewInfo> call, Throwable t) {

                }
            });
        }
        if (deleteReview) {
            reviewWriteConfirm.setText("삭제");
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_review_write.this);
            builder.setTitle("알림");
            builder.setMessage("해당 리뷰를 삭제하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Call<PostInfo> call = service.deleteReviewInfo(postNum);

                    call.enqueue(new Callback<PostInfo>() {
                        @Override
                        public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "리뷰 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<PostInfo> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        //등록버튼 눌렀을 떄, 서버에 통신하고 후기등록 된걸로 확인
        reviewWriteConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deleteReview) {
                    Call<PostInfo> call = service.deleteReviewInfo(postNum);
                    call.enqueue(new Callback<PostInfo>() {
                        @Override
                        public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "리뷰 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<PostInfo> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                //등록 버튼 눌렀을 떄, 예외처리,
                if (reviewWriteReview.getText().toString().length() < 10) {
                    Toast.makeText(getApplicationContext(), "후기를 10글자 이상 작성해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (reviewWriteRatingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "평점을 0.5점 이상 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestBody reviewBody = RequestBody.create(MediaType.parse("text/plain"), reviewWriteReview.getText().toString());
                RequestBody reviewRatingBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(reviewWriteRatingBar.getRating()));
                map.put("review", reviewBody);
                map.put("reviewRating", reviewRatingBody);
                map.put("postNum", RequestBody.create(MediaType.parse("text/plain"), postNum));
                map.put("email", RequestBody.create(MediaType.parse("text/plain"), id));

                if (!isReview) {
                    Call<PostInfo> call = service.sendReviewInfo(map);
                    call.enqueue(new Callback<PostInfo>() {
                        @Override
                        public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("123", "통신성공");

                                if (response.body().isSuccess()) {

                                    Toast.makeText(getApplicationContext(), "리뷰작성 성공", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PostInfo> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "통신 오류!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    map.put("beforeRating", RequestBody.create(MediaType.parse("text/plain"), beforeUpdateRating));
                    Call<PostInfo> call = service.updateReviewInfo(map);
                    call.enqueue(new Callback<PostInfo>() {

                        @Override
                        public void onResponse(Call<PostInfo> call, Response<PostInfo> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "리뷰수정 성공", Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PostInfo> call, Throwable t) {

                        }

                    });

                }

            }
        });

    }

    public void variableInit() {

        //retrofit2
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        //shared에 기본 아이디
        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");


        //request hash map
        map = new HashMap<>();
        //기본 xml 연결
        backImage = (ImageView) findViewById(R.id.review_write_back_arrow);
        productImage = (ImageView) findViewById(R.id.review_write_product_image);

        reviewWriteConfirm = (TextView) findViewById(R.id.review_write_review_confirm);
        reviewWriteProductName = (TextView) findViewById(R.id.review_write_product_name);
        reviewWriteProductPrice = (TextView) findViewById(R.id.review_write_product_price);

        reviewWriteReview = (EditText) findViewById(R.id.review_write_review_edit);
        reviewWriteRatingBar = (RatingBar) findViewById(R.id.review_write_rating_bar);

        reviewWriteTitle = (TextView) findViewById(R.id.review_write_title);
    }
}