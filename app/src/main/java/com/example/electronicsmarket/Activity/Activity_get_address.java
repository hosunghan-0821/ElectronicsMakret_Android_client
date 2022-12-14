package com.example.electronicsmarket.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.electronicsmarket.R;

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

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");


        webView.setWebViewClient(new WebViewClient() {
            //페이지를 다 load 하고 난 후에, 저 자바스크립트 함수 실행하도록
            @Override
            public void onPageFinished(WebView view, String url) {

                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }


        });

        webView.loadUrl("http://43.201.72.60/realMarketServer/lib/daum.html");
    }

}