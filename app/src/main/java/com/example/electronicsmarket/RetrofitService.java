package com.example.electronicsmarket;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/mail.php")
    Call<PostEmail> send( @Field("email") String email);

    @GET("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/memberOut.php")
    Call<MemberSignup> memberOut(@Query("email") String email,@Query("message") String message);

    @GET("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/setLikeList.php")
    Call<MemberSignup> setLikeList(@Query("email") String nickname,@Query("postNum") String postNum,@Query("state") String insDel);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/{path}")
    Call<MemberSignup> getProfile(@Path("path") String path, @Field("email") String email);


    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/setProfile.php")
    Call<MemberSignup> setProfile(@Part MultipartBody.Part image,@Query("email") String email );

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/setProfileNickname.php")
    Call<MemberSignup> setNickname(@Field("email") String email,@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/findPassword.php")
    Call<PostEmail> find( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/emailVerify/temporaryPassword.php")
    Call<PostEmail> newPassword( @Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postRead.php")
    Call<PostInfo> getPostInfo(@Field("postNum") String postNum,@Field("purpose") String purpose,@Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/viewPlus.php")
    Call<PostInfo> viewPlus(@Field("postNum") String postNum);


    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/deleteProfile.php")
    Call<MemberSignup> deleteProfile(@Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/{path}")
    Call<MemberSignup> sendNickname(@Path("path") String path,@Field("nickname") String nickname);


    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/getPostWriterInfo.php")
    Call<PostAllInfo> getSellerProfile(@Field("nickname") String nickname);


    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/signup.php")
    Call<MemberSignup> sendMemberInfo(
            @Field("id") String id,
            @Field("password") String password,
            @Field("nickname") String nickname
            );

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/changePassword.php")
    Call<MemberSignup> sendNewPassword(
            @Field("email") String id,
            @Field("password") String password,
            @Field("newPassword") String newPassword
    );

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/signup/login.php")
    Call<MemberSignup> sendLoginInfo(
            @Field("id") String id,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postDelete.php")
    Call<MemberSignup> sendDeletePostInfo(
            @Field("postNum") String postNum
    );

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postAllInfo.php")
    Call<PostAllInfo> getPostAllInfo(@Field("finalPostNum")String finalPostNum,@Field("phasingNum")String phasingNum,@Field("purpose") String purpose,@Field("email") String email);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postFilterAllInfo.php")
    Call<PostAllInfo> getFilterPostAllInfo(@Field("category")String category,@Field("searchKeyword")String searchKeyword);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/clientInfo.php")
    Call<PostAllInfo> getClientInfo(@Field("email")String email,@Field("state")String state);

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postWrite.php")
    Call<MemberSignup> sendMultiImage(
            @Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, RequestBody> params
            );

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/postApi/postUpdate.php")
    Call<MemberSignup> sendUpdate(
            @Part ArrayList<MultipartBody.Part> files, @PartMap HashMap<String, RequestBody> params
    );

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/paymentSuccess.php")
    Call<PaymentInfo> sendPaymentInfo(@PartMap HashMap<String,RequestBody> params);


    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/getPaymentInfo.php")
    Call<PaymentInfo> getPaymentInfo(@Field("email")String email, @Field("tradeNum")String tradeNum);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/setDeliveryInfo.php")
    Call<PaymentInfo> setDeliveryInfo(@Field("tradeNum")String tradeNum,@Field("deliveryNum")String deliveryNum,@Field("deliveryCompany")String deliveryCompany);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/lib/crawling.php")
    Call<DeliveryInfo> getDeliveryInfo(@Field("deliveryNum")String deliveryNum,@Field("deliveryCompany")String deliveryCompany,@Field("tradeNum")String tradeNum);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/setPaymentConfirm.php")
    Call<PostInfo> confirmPayment(@Field("postNum")String postNum);

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/setReviewInfo.php")
    Call<PostInfo> sendReviewInfo(@PartMap HashMap<String,RequestBody> params);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/getReviewAllInfo.php")
    Call<ReviewAllInfo> getReviewInfo(@Field("finalPostNum")String finalPostNum,@Field("phasingNum")String phasingNum,@Field("email")String email,@Field("nickname")String nickname);


    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/getReviewInfo.php")
    Call<ReviewInfo> getReviewOneInfo(@Field("postNum") String postNum);

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/updateReviewInfo.php")
    Call<PostInfo> updateReviewInfo(@PartMap HashMap<String,RequestBody> params);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/deleteReviewInfo.php")
    Call<PostInfo> deleteReviewInfo(@Field("postNum") String postNum);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/paymentApi/productRefund.php")
    Call<RefundInfo> productRefund (@Field("tradeNum") String tradeNum,@Field("email")String id,@Field("reason") String reason,@Field("deliveryStatus") String status);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/chatApi/roomNumCheck.php")
    Call<DataChatRoom> roomNumCheck (@Field("postNum") String postNum, @Field("seller")String seller, @Field("buyer") String buyer,@Field("roomNum") String roomNum,@Field("nickname")String nickName);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/chatApi/getRoomAllInfo.php")
    Call<DataChatRoomAll> getRoomAllInfo (@Field("nickname") String nickname,@Field("phasingNum") String phasingNum,@Field("cursorChatRoom") String cursorChatRoomNum);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/chatApi/getRoomChatInfo.php")
    Call<DataChatAll> getRoomChatInfo (@Field("roomNum") String roomNum,@Field("phasingNum")String phasingNum,@Field("cursorChatNum") String cursorChatNum,@Field("nickname") String nickname);

    @Multipart
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/chatApi/setImageFiles.php")
    Call<DataChatImageRoute> chatImageFiles(@Part ArrayList<MultipartBody.Part> files,@PartMap HashMap<String, RequestBody> params);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/chatApi/userRoomOut.php")
    Call<DataChatRoom> userRoomOut (@Field("roomNum") String roomNum,@Field("nickname")String nickname,@Field("otherUserNickname") String otherUserNickname);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/tradeApi/getInquirerInfo.php")
    Call<DataInquirerAllInfo> getInquirerInfo (@Field("nickname")String nickname, @Field("postNum") String postNum);

    @FormUrlEncoded
    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/tradeApi/tradeSuccess.php")
    Call<Void> tradeSuccess (@Field("nickname")String nickname, @Field("postNum") String postNum,@Field("buyer") String buyerNickname);




    //여기서부터 다른 restapi 사용하기 위한 것들
    @GET("https://dapi.kakao.com/v2/local/search/keyword.json")
    Call<DataSearchResult> sendPlace(

            @Header("Authorization")String Key,
            @Query("query") String query
    );

    @FormUrlEncoded
    @Headers({"content-type:application/x-www-form-urlencoded"})
    @POST("https://kapi.kakao.com/v1/payment/cancel")
    Call<KaKaoPayResult> sendKaKaoPayCancelRequest(
            @Header("Authorization") String Key ,
            @Field("cid") String cid,
            @Field("tid") String tid,
            @Field("cancel_amount") String cancel_amount,
            @Field("cancel_tax_free_amount") String cancel_tax_free_amount
    );


    @FormUrlEncoded
    @Headers({"content-type:application/x-www-form-urlencoded;charset=utf-8"})
    @POST("v1/payment/ready")
    Call<KaKaoPayResult> sendKaKaoPayReadyRequest(
            @Header("Authorization") String Key ,
            @Field("cid") String cid,
            @Field("partner_order_id") String partner_order_id,
            @Field("partner_user_id") String partner_user_id,
            @Field("item_name") String item_name,
            @Field("quantity") String quantity,
            @Field("total_amount") String total_amount,
            @Field("tax_free_amount") String tax_free_amount,
            @Field("approval_url") String approval_url,
            @Field("cancel_url") String cancel_url,
            @Field("fail_url") String fail_url
            );


    @FormUrlEncoded
    @Headers({"content-type:application/x-www-form-urlencoded"})
    @POST("https://kapi.kakao.com/v1/payment/approve")
    Call<KaKaoPayResult> sendKaKaoPayApprovalRequest(
            @Header("Authorization") String Key ,
            @Field("cid") String cid,
            @Field("partner_order_id") String partner_order_id,
            @Field("partner_user_id") String partner_user_id,
            @Field("item_name") String item_name,
            @Field("pg_token") String pg_token,
            @Field("tid") String tid
    );

    @POST("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/lib/example.php")
    Call<rExample> getJson ();

}
