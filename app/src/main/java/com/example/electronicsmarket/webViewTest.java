package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class webViewTest extends AppCompatActivity  {

    public final String API_KEY = "KakaoAK 980cc4d9e8446ee50d352b75e72f73ae";
    private WebView webView;
    private MyWebViewClient myWebViewclient;

    private WebSettings mWebSettings;
    private Retrofit retrofit;
    private String tidPin;
    private String pgToken;
    private boolean isFinishPay=false;
    private String appURL;
    HashMap<String, String> params= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);
        webView = findViewById(R.id.webview);


        variableInit();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Log.e("123",params.toString());
        Call<KaKaoPayResult> call = service.sendKaKaoPayReadyRequest(
                API_KEY,
                "TC0ONETIME",
                "1001",
                "gorany",
                "실험",
                "1",
                "1000",
                "0",
                "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/success.html",
                "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/success.html",
                "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com/success.html"
                );
        call.enqueue(new Callback<KaKaoPayResult>() {
            @Override
            public void onResponse(Call<KaKaoPayResult> call, Response<KaKaoPayResult> response) {

                Log.e("123",response.message());
                if(response.isSuccessful()&&response.body()!=null){
                    Log.e("123","통신성공");
                    Log.e("123",response.body().getRedirectUrl());
                    String url = response.body().getRedirectUrl();
                    String tid=response.body().getTid();
                    webView.loadUrl(url);
                    tidPin = tid;
                    appURL=response.body().getAppUrl();
                }
                else{
                    Log.e("123","통신실패");
                }

            }

            @Override
            public void onFailure(Call<KaKaoPayResult> call, Throwable t) {

            }

        });

    }

    public void variableInit() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-15-164-99-218.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();



        webView.setWebViewClient(new MyWebViewClient()); // 이걸 안해주면 새창이 뜸

        mWebSettings = webView.getSettings(); // 웹뷰에서 webSettings를 사용할 수 있도록 함.
        mWebSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);//멀티윈도우 띄우는 것
        mWebSettings.setAllowFileAccess(true); //파일 엑세스
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그
        mWebSettings.setUseWideViewPort(true); //화면 사이즈 맞추기
        mWebSettings.setSupportZoom(true); // 화면 줌 사용 여부
        mWebSettings.setBuiltInZoomControls(true); //화면 확대 축소 사용 여부
        mWebSettings.setDisplayZoomControls(true); //화면 확대 축소시, webview에서 확대/축소 컨트롤 표시 여부
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 사용 재정의 value : LOAD_DEFAULT, LOAD_NORMAL, LOAD_CACHE_ELSE_NETWORK, LOAD_NO_CACHE, or LOAD_CACHE_ONLY
        mWebSettings.setDefaultFixedFontSize(14); //기본 고정 글꼴 크기, value : 1~72 사이의 숫자

//        params.put("cid", "TC0ONETIME"); // 가맹점 코드
//        params.put("partner_order_id", "1001"); // 가맹점 주문 번호
//        params.put("partner_user_id", "gorany"); // 가맹점 회원 아이디
//        params.put("item_name", "실험"); // 상품 이름
//        params.put("quantity", "1"); // 상품 수량
//        params.put("total_amount", "1000"); // 상품 총액
//        params.put("tax_free_amount", "0"); // 상품 비과세
//        params.put("approval_url", "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com"); // 결제 성공시 돌려 받을 url 주소
//        params.put("cancel_url", "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com"); // 결제 취소시 돌려 받을 url 주소
//        params.put("fail_url", "http://ec2-3-36-64-237.ap-northeast-2.compute.amazonaws.com"); // 결제 실패시 돌려 받을 url 주소


    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e("123","url 변경확인");
            Log.e("123","변경 url : "+url);

            if(url!=null && url.contains("pg_token=")){
                String pg_Token=url.substring(url.indexOf("pg_token=")+9);
                pgToken=pg_Token;
                Log.e("123","pg_token " +pgToken);
                Log.e("123","tid" + tidPin);

                RetrofitService service = retrofit.create(RetrofitService.class);
                Log.e("123",params.toString());
                Call<KaKaoPayResult> call = service.sendKaKaoPayApprovalRequest(
                        API_KEY,
                        "TC0ONETIME",
                        "1001",
                        "gorany",
                        "실험",
                         pgToken,
                         tidPin
                );
                call.enqueue(new Callback<KaKaoPayResult>() {
                    @Override
                    public void onResponse(Call<KaKaoPayResult> call, Response<KaKaoPayResult> response) {

                        if(response.isSuccessful() &&response.body()!=null){
                            isFinishPay=true;
                        }

                    }
                    @Override
                    public void onFailure(Call<KaKaoPayResult> call, Throwable t) {

                    }
                });

            }
            else if(url!=null && url.startsWith("intent://")){
                try{
                    //받은 url 정보를 갖고, 이제 모바일에서 카카오톡 앱을 찾아가서 실행시키는 구조인듯하다.
                    Intent intent =Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if(existPackage!=null){
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(webViewTest.this, "카카오톡 앱이 없어요", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Log.e("123","아마 한번 찎힘 url : "+url);
            view.loadUrl(url);
            return false;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("456","onStart : ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("456","onResume : + isFinishPay"+isFinishPay);
        if(isFinishPay){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("456","onPause : ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("456","onstop : ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("456","onRestart : ");
    }
}

