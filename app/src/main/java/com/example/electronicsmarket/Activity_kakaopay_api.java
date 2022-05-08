package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class Activity_kakaopay_api extends AppCompatActivity {

    public final static int payResultCode = 100;
    public String API_KEY="";
    private WebView webView;
    private MyWebViewClient myWebViewclient;
    private Handler handler;

    private ProgressBar progressBar;
    private WebSettings mWebSettings;
    private Retrofit retrofit;
    private String tidPin;
    private String pgToken;
    private String isFinishPay = "기본";
    private String appURL, productName, productPriceS;
    private String partnerOrderId, partnerUserId;
    private HashMap<String, RequestBody> requestMap = new HashMap<>();
    private Thread thread;
    private String tradeNum;

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused") //컴파일러가 일반적으로 경고하는 내용에서 제외시킬 때 사용하는 노테이션
        public void processResult(String data) {
            //db저장까지 끝난 후 , 화면전환하는곳.
            Log.e("123", "processResult();");

            //data = 결제 결과; 결과에 따라다르게 나올 수 있도록,
            if(data.equals("결제성공")){

                //결제상세보기 화면으로 이동
                Log.e("123","결제성공");
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                Intent intent =new Intent (Activity_kakaopay_api.this,Activity_trade_detail_info.class);
                intent.putExtra("tradeNum",tradeNum);
                intent.putExtra("readType","buyer");
                intent.putExtra("tradeType","택배거래");
                startActivity(intent);
                finish();
            }
            else if(data.equals("결제취소")){
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                finish();
            }
            else if(data.equals("결제실패")){
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaopay_api);
        webView = findViewById(R.id.webview);
        progressBar=findViewById(R.id.kakao_api_progressbar);
        Intent getIntent = getIntent();

        try{
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            API_KEY = applicationInfo.metaData.getString("KAKAO_API_KEY");
            Log.e("123456",API_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }


        productName = getIntent.getStringExtra("productName");
        productPriceS = getIntent.getStringExtra("productPay");

        Log.e("123", productName);
        Log.e("123", productPriceS);
        variableInit();

        handler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==0){
                    webView.loadUrl("javascript:processFinish();");
                }
                else if(msg.arg1==1){
                    Toast.makeText(getApplicationContext(), "결제실패", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<KaKaoPayResult> call = service.sendKaKaoPayReadyRequest(
                API_KEY,
                "TC0ONETIME",
                partnerOrderId,
                partnerUserId,
                productName,
                "1",
                productPriceS,
                "0",
                "http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/success.html",
                "http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/cancel.html",
                "http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/fail.html"
        );
        call.enqueue(new Callback<KaKaoPayResult>() {
            @Override
            public void onResponse(Call<KaKaoPayResult> call, Response<KaKaoPayResult> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.e("123", "통신성공");

                    String url = response.body().getRedirectUrl();
                    String tid = response.body().getTid();
                    webView.loadUrl(url);
                    tidPin = tid;
                    appURL = response.body().getAppUrl();
                    Log.e("123","tid : "+tidPin);
                } else {


                    Log.e("123", response.message());
                    Log.e("123", "통신실패");
                   Toast.makeText(getApplicationContext(), "카카오 페이 서버 통신 오류 발생 화면 reload", Toast.LENGTH_SHORT).show();
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Intent intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기

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
                .baseUrl("https://kapi.kakao.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        partnerOrderId = "100";
        partnerUserId = "hosung";

        webView.setWebViewClient(new MyWebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.addJavascriptInterface(new Activity_kakaopay_api.MyJavaScriptInterface(), "Android");
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

            Log.e("123", "url 변경확인");
            Log.e("123", "isFinishPay" + isFinishPay);

            if (url != null && url.contains("pg_token=")) {
                isFinishPay="결제저장대기";
                Log.e("123", "isFinishPay" + isFinishPay);
                //이 타이밍에 webview invisible 하고, progressbar 띄어서 확인해보자 어떻게 되는지.
                String pg_Token = url.substring(url.indexOf("pg_token=") + 9);
                pgToken = pg_Token;
                Log.e("123", "pg_token " + pgToken);
                Log.e("123", "tid" + tidPin);

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<KaKaoPayResult> call = service.sendKaKaoPayApprovalRequest(
                        API_KEY,
                        "TC0ONETIME",
                        partnerOrderId,
                        partnerUserId,
                        productName,
                        pgToken,
                        tidPin
                );
                call.enqueue(new Callback<KaKaoPayResult>() {
                    @Override
                    public void onResponse(Call<KaKaoPayResult> call, Response<KaKaoPayResult> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("123", "최종결제 통신성공");

                            Intent intent = getIntent();
                            //기존즈소지 설정
                            if(intent.getStringExtra("setStandardAddress")!=null){
                                RequestBody isCheckBody = RequestBody.create(MediaType.parse("text/plain"), "true");
                                requestMap.put("setStandardAddress", isCheckBody);
                            }

                            //결제방식 데이터 전송.
                            RequestBody postNumBody = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("postNum"));
                            RequestBody payTypeBody = RequestBody.create(MediaType.parse("text/plain"), "카카오페이");
                            RequestBody tradeTypeBody = RequestBody.create(MediaType.parse("text/plain"), "택배거래");
                            RequestBody buyerId = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("buyerId"));
                            RequestBody buyerName = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("buyerName"));
                            RequestBody tidBody =RequestBody.create(MediaType.parse("text/plain"), tidPin);

                            requestMap.put("tid",tidBody);
                            requestMap.put("buyerName", buyerName);
                            requestMap.put("postNum", postNumBody);
                            requestMap.put("payType", payTypeBody);
                            requestMap.put("tradeType", tradeTypeBody);
                            requestMap.put("buyerId", buyerId);

                            //배송지관련 정보 전송

                            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("address"));
                            RequestBody addressDetailBody = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("addressDetail"));
                            RequestBody deliveryDetailBody = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("deliveryDetail"));
                            RequestBody deliveryDetail2Body = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("deliveryDetail2Body"));

                            requestMap.put("deliveryDetail2Body", deliveryDetail2Body);
                            requestMap.put("deliveryDetail", deliveryDetailBody);
                            requestMap.put("addressDetail", addressDetailBody);
                            requestMap.put("address", addressBody);

                            RetrofitService service = retrofit.create(RetrofitService.class);

                            Call<PaymentInfo> call2 = service.sendPaymentInfo(requestMap);
                            call2.enqueue(new Callback<PaymentInfo>() {
                                @Override
                                public void onResponse(Call<PaymentInfo> call, Response<PaymentInfo> response) {
                                    //제대로 db에 저장됬으면 -> 구메 상세 페이지로 이동해서 해당 정보들 보여줄 수 있게끔
                                    if(response.isSuccessful()&&response.body()!=null){
                                        if(response.body().isSuccess){
                                            isFinishPay = "결제성공";
                                            tradeNum =response.body().getTradeNum();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<PaymentInfo> call, Throwable t) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onFailure(Call<KaKaoPayResult> call, Throwable t) {

                    }
                });

            } else if (url != null && url.startsWith("intent://")) {
                try {
                    //받은 url 정보를 갖고, 이제 모바일에서 카카오톡 앱을 찾아가서 실행시키는 구조인듯하다.
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(Activity_kakaopay_api.this, "카카오톡 앱이 없어요", Toast.LENGTH_SHORT).show();
                    }
                    isFinishPay="결제저장대기";
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("123", "아마 한번 찎힘 url : " + url);
            //view.loadUrl(url);
            return false;
            //이 함수 리턴 값 의미 : true to cancel the current load, otherwise return false.
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("456", "onStart : ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("123", "onResume : + isFinishPay" + isFinishPay);
        if (isFinishPay.equals("결제성공")) {
            webView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<1;i++){
                        try{
                            Thread.sleep(1000);
                            Message msg= new Message();
                            msg.arg1=0;
                            handler.sendMessage(msg);
                            break;
                        }catch (Exception e){
                            System.out.println("오류"+e);
                            Log.e("123","여기로들어옴1?");
                            e.printStackTrace();
                            finish();
                            break;
                        }

                    }
                }
            });
            thread.start();
//            webView.loadUrl("javascript:processFinish();");
        }
        else if(isFinishPay.equals("결제저장대기")){
            Log.e("123","결제저장대기");
            webView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<3;i++){
                        Log.e("123","쓰레드 도는중 i : "+ i);
                        Log.e("123","isFinishPay"+isFinishPay);
                        try{
                            if(isFinishPay.equals("결제성공")){
                                Message msg= new Message();
                                msg.arg1=0;
                                handler.sendMessage(msg);
                                break;
                            }
                            Thread.sleep(1000);
                        }catch (Exception e){
                            Log.e("123","여기로들어옴2?");
                            System.out.println("오류"+e);
                            e.printStackTrace();
                            finish();
                            break;
                        }
                    }
                    if(!isFinishPay.equals("결제성공")){
                        Message msg= new Message();
                        msg.arg1=1;
                        handler.sendMessage(msg);

                    }
                }
            });
            thread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("456", "onPause : ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("456", "onstop : ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("456", "onRestart : ");
    }
}

