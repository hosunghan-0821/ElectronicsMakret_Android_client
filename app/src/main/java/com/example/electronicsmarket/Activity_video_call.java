package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_video_call extends AppCompatActivity {

    private WebView webView;
    private String otherUserNickname,roomNum;
    private WebSettings mWebSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        variableInit();

        Intent intent =getIntent();
        otherUserNickname=intent.getStringExtra("sendToNickname");
        roomNum=intent.getStringExtra("roomNum");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(webView.canGoBack()){
//            webView.goBack();
//        }else{
//            super.onBackPressed();
//        }
//        Log.e("123","창닫기 시작 ");
        webView.loadUrl("javascript:finishCall()");
        webView.destroy();

        finish();
        Log.e("123","창닫기 끝 ");
    }

    public void variableInit(){
        //웹뷰 기본 세팅
        WebView.setWebContentsDebuggingEnabled(true);
        webView=findViewById(R.id.video_call_webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.e("123","작동확인1");
                        String[] PERMISSIONS = {
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                        };
                        request.grant(PERMISSIONS);
                        Log.e("123","작동확인2");
                    }
                });
            }
        });


        //webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        mWebSettings = webView.getSettings(); // 웹뷰에서 webSettings를 사용할 수 있도록 함.
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        mWebSettings.setUseWideViewPort(true); //화면 사이즈 맞추기
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그
        webView.clearCache(true);
        webView.loadUrl("https://a872-219-248-76-133.ngrok.io");
    }

    public class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("123","onPagedStarted");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("123","onPagedFinished");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Log.e("123","shouldOverrideUrlLoading()");
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
    public class MyWebViewChromeClient extends WebChromeClient{


        public MyWebViewChromeClient() {
            super();

        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {

            if(request!=null){
                request.grant(request.getResources());
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }


    }
}