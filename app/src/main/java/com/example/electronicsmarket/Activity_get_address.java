package com.example.electronicsmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_get_address extends AppCompatActivity {

    private WebView webView;

    class MyJavaScriptInterface{
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processData(String data){
            Intent intent =new Intent();
            intent.putExtra("address",data);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_address);
        webViewInit();

    }

    public void webViewInit(){
        webView=findViewById(R.id.get_address_webview);

////        webView.setWebChromeClient( new WebChromeClient(){
////
////        });
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Log.e("123","onPageStarted : " );
//                super.onPageStarted(view, url, favicon);
//                webView.loadUrl("javascript:hi()");
//            }
//        });
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.addJavascriptInterface(new MyJavaScriptInterface(),"Android");
//        webView.loadUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/example/daum.html");
//        Log.e("123","loadUrl");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        webView.loadUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/realMarketServer/example/daum.html");
    }

}