package com.example.electronicsmarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_mypage extends Fragment  {
    //mypage_frame_lovelist
    private SharedPreferences sharedPreferences;
    private Button profileUpdate;
    private Retrofit retrofit;
    private TextView nickname;
    private ImageView settingImage;
    private de.hdodenhof.circleimageview.CircleImageView circleImageView;
    private FrameLayout frameLoveList,frameSellList,frameBuyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_mypage,container,false);
        variableInit(view);


        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_setting.class);
                startActivity(intent);
            }
        });

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Activity_profile_update.class);
                startActivity(intent);
            }
        });

        frameSellList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_sell_list.class);
                startActivity(intent);
            }
        });

        frameLoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_love_list.class);
                startActivity(intent);
            }
        });

        frameBuyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Activity_buy_list.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void variableInit(View view){

        Gson gson=new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        frameSellList=(FrameLayout)view.findViewById(R.id.mypage_frame_selllist);
        frameLoveList=(FrameLayout)view.findViewById(R.id.mypage_frame_lovelist);
        frameBuyList=(FrameLayout)view.findViewById(R.id.mypage_frame_buylist);

        nickname=view.findViewById(R.id.user_nickname);
        settingImage=view.findViewById(R.id.setting_image);
        circleImageView=view.findViewById(R.id.circleImageView);
        profileUpdate=view.findViewById(R.id.profile_update_button);
    }

    public void setProfile(Context context){
        sharedPreferences= context.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String id=sharedPreferences.getString("userId","");

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<MemberSignup> call = service.getProfile("getProfile.php",id);
        call.enqueue(new Callback<MemberSignup>() {
            @Override
            public void onResponse(Call<MemberSignup> call, Response<MemberSignup> response) {


                if(response.isSuccessful()&&response.body()!=null){

                    Log.e("123","응답옴");
                  MemberSignup memberInfo=response.body();
                  nickname.setText(memberInfo.getNickname());
                  String imageRoute= memberInfo.imageRoute;
                  if(imageRoute.equals("")){
                     circleImageView.setImageResource(R.drawable.ic_baseline_person_black);
                  }
                  else{
                        Glide.with(context).load(imageRoute).into(circleImageView);
                  }

                }
            }
            @Override
            public void onFailure(Call<MemberSignup> call, Throwable t) {
                Toast.makeText(getContext(), "응답 실패", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e("123","onresume");
        setProfile(getContext());
        super.onResume();

    }
}