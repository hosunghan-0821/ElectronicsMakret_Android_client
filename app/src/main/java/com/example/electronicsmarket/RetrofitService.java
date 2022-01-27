package com.example.electronicsmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
    Call<MemberSignup> setProfile(@Part MultipartBody.Part image,@Query("email") String email );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/setProfileNickname.php")
    Call<MemberSignup> setNickname(@Field("email") String email,@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/findPassword.php")
    Call<PostEmail> find( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/temporaryPassword.php")
    Call<PostEmail> newPassword( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postRead.php")
    Call<PostInfo> getPostInfo(@Field("postNum") String postNum);


    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/deleteProfile.php")
    Call<MemberSignup> deleteProfile(@Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/{path}")
    Call<MemberSignup> sendNickname(@Path("path") String path,@Field("nickname") String nickname);


    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/getPostWriterInfo.php")
    Call<PostAllInfo> getSellerProfile(@Field("nickname") String nickname);


    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/signup.php")
    Call<MemberSignup> sendMemberInfo(
            @Field("id") String id,
            @Field("password") String password,
            @Field("nickname") String nickname
            );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/changePassword.php")
    Call<MemberSignup> sendNewPassword(
            @Field("email") String id,
            @Field("password") String password,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/login.php")
    Call<MemberSignup> sendLoginInfo(
            @Field("id") String id,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postDelete.php")
    Call<MemberSignup> sendDeletePostInfo(
            @Field("postNum") String postNum
    );

    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postAllInfo.php")
    Call<PostAllInfo> getPostAllInfo();

    @Multipart
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postWrite.php")
    Call<MemberSignup> sendMultiImage(
            @Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, RequestBody> params
            );

    @Multipart
    @POST("http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postUpdate.php")
    Call<MemberSignup> sendUpdate(
            @Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, RequestBody> params
    );

    @GET("https://dapi.kakao.com/v2/local/search/keyword.json")
    Call<DataSearchResult> sendPlace(

            @Header("Authorization")String Key,
            @Query("query") String query
    );
}
