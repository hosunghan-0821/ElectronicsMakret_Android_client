package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_video_call extends AppCompatActivity {

    private WebView webView;
    private String otherUserNickname, roomNum;
    private String position;
    private WebSettings mWebSettings;
    private String serverURL = "https://05b1-1-227-215-212.ngrok.io";
    private ProgressBar progressBar;
    private TextView callInfoText;
    private ImageView videoCameraOff, videoMicOff, callCancel, callCalleeCancel, videoSwap, callCalleeAccept;
    private Handler handler;
    private String  nickname;


    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused") //컴파일러가 일반적으로 경고하는 내용에서 제외시킬 때 사용하는 노테이션
        public void setCallInfo() {
            Log.e("123", "setCallInfo");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "setCallText");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
        @JavascriptInterface
        @SuppressWarnings("unused") //컴파일러가 일반적으로 경고하는 내용에서 제외시킬 때 사용하는 노테이션
        public void callOtherUser(){
            if(position.equals("caller")){
                Intent sendAlarmintent = new Intent("chatDataToServer");
                sendAlarmintent.putExtra("purpose", "sendNotification");
                sendAlarmintent.putExtra("message", nickname);
                sendAlarmintent.putExtra("postNum",roomNum);
                sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
                LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(sendAlarmintent);
            }
        }
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void disconnect(){
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "disconnect");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        variableInit();
        //다른 activity 로 넘어온 intent 전화 받는 사람, 채팅방 고유번호, (수신자 or 발신자) 인지 intent 를 통해 확인
        Intent intent = getIntent();
        otherUserNickname = intent.getStringExtra("sendToNickname");
        roomNum = intent.getStringExtra("roomNum");

        //발신자, 수신자에 따라서, view 변경하기
        if (intent.getStringExtra("position") != null) {
            //전화통화 건 사람일 경우
            position = "caller";
            videoCameraOff.setVisibility(View.VISIBLE);
            videoSwap.setVisibility(View.VISIBLE);
            videoMicOff.setVisibility(View.VISIBLE);
            callCancel.setVisibility(View.VISIBLE);


        } else {
            //전화통화 받은 사람일 경우
            position = "callee";
            callCalleeCancel.setVisibility(View.VISIBLE);
            callCalleeAccept.setVisibility(View.VISIBLE);
        }
        //웹뷰 세팅
        webViewInit();


        //헨들러 정의
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose = bundle.getString("purpose");

                if (purpose.equals("setCallText")) {
                    if (position.equals("caller")) {
                        callInfoText.setVisibility(View.VISIBLE);
                        callInfoText.setText(otherUserNickname + "\n연결 중입니다.");
                    } else {
                        callInfoText.setVisibility(View.VISIBLE);
                        callInfoText.setText(otherUserNickname + "\n영상통화해요 ~");
                    }
                }
                else if (purpose.equals("disconnect")){
                    webView.destroy();
                    finish();
                }
            }
        };


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
        Log.e("123", "창닫기 끝 ");
    }

    public void webViewInit() {
        //웹뷰 기본 세팅
        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.video_call_webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.e("123", "작동확인1");
                        String[] PERMISSIONS = {
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                        };
                        request.grant(PERMISSIONS);
                        Log.e("123", "작동확인2");
                    }
                });
            }
        });

        webView.addJavascriptInterface(new Activity_video_call.MyJavaScriptInterface(), "Android");
        webView.setInitialScale(1);
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
        //webView.loadUrl(serverURL +"?roomNum="+roomNum+"&sendToNickname="+otherUserNickname+"&position="+position);
        webView.loadUrl(serverURL + "/" + roomNum + "/" + otherUserNickname + "/" + position);

    }

    public void variableInit() {

        //기본 xml
        progressBar = findViewById(R.id.video_call_progressbar);
        callInfoText = findViewById(R.id.video_call_info);

        videoCameraOff = (ImageView) findViewById(R.id.video_call_camera_off);
        videoMicOff = (ImageView) findViewById(R.id.video_call_mic_off);
        videoSwap = (ImageView) findViewById(R.id.video_call_camera_swap);
        callCancel = (ImageView) findViewById(R.id.video_call_cancel);
        callCalleeAccept = (ImageView) findViewById(R.id.video_call_callee_accept);
        callCalleeCancel = (ImageView) findViewById(R.id.video_call_callee_cancel);


        // shared 값 가져오기
        SharedPreferences sharedPreferences=getSharedPreferences("autoLogin",MODE_PRIVATE);
        nickname=sharedPreferences.getString("nickName","");


    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            Log.e("123", "onPagedStarted");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
//            MediaPlayer mediaPlayer;
//            mediaPlayer=MediaPlayer.create(Activity_video_call.this,R.raw.callingsound);
//            mediaPlayer.start();

            Log.e("123", "onPagedFinished");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Log.e("123", "shouldOverrideUrlLoading()");
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    public class MyWebViewChromeClient extends WebChromeClient {


        public MyWebViewChromeClient() {
            super();

        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {

            if (request != null) {
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