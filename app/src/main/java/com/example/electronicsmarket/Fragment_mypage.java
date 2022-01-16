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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_mypage extends Fragment  {

    SharedPreferences sharedPreferences;
    Button profileUpdate;
    Retrofit retrofit;
    TextView nickname;
    ImageView settingImage;
    de.hdodenhof.circleimageview.CircleImageView circleImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.fragment_mypage,container,false);
        variableInit(view);
        setProfile(container.getContext());



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

                  MemberSignup memberInfo=response.body();
                  nickname.setText(memberInfo.getNickname());

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

}