package com.example.electronicsmarket;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/mail.php")
    Call<PostEmail> send( @Field("email") String email);

    @GET("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/memberOut.php")
    Call<MemberSignup> memberOut(@Query("email") String email,@Query("message") String message);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/{path}")
    Call<MemberSignup> getProfile(@Path("path") String path, @Field("email") String email);


    @Multipart
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/setProfile.php")
    Call<MemberSignup> setProfile(@Part MultipartBody.Part image );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/findPassword.php")
    Call<PostEmail> find( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/temporaryPassword.php")
    Call<PostEmail> newPassword( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/{path}")
    Call<MemberSignup> sendNickname(@Path("path") String path,@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/signup.php")
    Call<MemberSignup> sendMemberInfo(
            @Field("id") String id,
            @Field("password") String password,
            @Field("nickname") String nickname
            );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/login.php")
    Call<MemberSignup> sendLoginInfo(
            @Field("id") String id,
            @Field("password") String password

    );


}
