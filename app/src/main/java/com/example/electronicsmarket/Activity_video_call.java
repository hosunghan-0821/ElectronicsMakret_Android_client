package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_video_call extends AppCompatActivity {

    private boolean cameraToggle = true, micToggle = true;
    private MediaPlayer mediaPlayer;
    private WebView webView;
    private String otherUserNickname, roomNum;
    private String position;
    private WebSettings mWebSettings;
    private String serverURL = "https://7a75-1-227-215-212.ngrok.io";
    private ProgressBar progressBar;
    private TextView callInfoText, callStatusBar;
    private ImageView videoCameraOff, videoMicOff, callCancel, callCalleeCancel, videoSwap, callCalleeAccept;
    private Handler handler;
    private String nickname;
    private boolean toggle = true, calleeAccept = false, isCalling = false;
    private int callTime = 0;
    private ConstraintLayout constraintLayout;
    private Thread thread;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String purpose= intent.getStringExtra("purpose");
            Log.e("123","Video Call getIntent");
            Log.e("123","purpose : "+intent.getStringExtra("purpose"));
            Log.e("123","otherUserNickname : "+intent.getStringExtra("otherUserNickname"));
            String disconnectFromUser=intent.getStringExtra("otherUserNickname");
            if(purpose!=null){
                if(purpose.equals("disconnect")){
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("purpose", "tcpDisconnect");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        }
    };


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
        public void callOtherUser() {
            if (position.equals("caller")) {

                //알림 상대방에게 보내고,
                Intent sendAlarmintent = new Intent("chatDataToServer");
                sendAlarmintent.putExtra("purpose", "sendNotification");
                sendAlarmintent.putExtra("message", nickname);
                sendAlarmintent.putExtra("postNum", roomNum);
                sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
                LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(sendAlarmintent);

                //위에 상태표시줄 서버연결상태로 바꾸기.
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "socketConnect");
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
            else if(position.equals("callee")){
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "socketConnect");
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
        }

        @JavascriptInterface
        @SuppressWarnings("unused") //컴파일러가 일반적으로 경고하는 내용에서 제외시킬 때 사용하는 노테이션
        public void socketConnectSuccess() {

            Log.e("123", "setCallInfo");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "socketConnectSuccess");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused") //컴파일러가 일반적으로 경고하는 내용에서 제외시
        public void socketConnectNotYet() {
            Log.e("123", "setCallInfo");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "socketConnectNotYet");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void disconnect() {

            isCalling = false;
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "disconnect");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void peerConnectionSuccess() {
            //벨소리 제거
            releaseMediaPlayer();
            //진동제거
            vibrator.cancel();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "peerConnectionStart");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
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
//            videoCameraOff.setVisibility(View.VISIBLE);
//            videoSwap.setVisibility(View.VISIBLE);
//            videoMicOff.setVisibility(View.VISIBLE);
//            callCancel.setVisibility(View.VISIBLE);
//            calleeAccept = true;

            callStatusBar.setVisibility(View.VISIBLE);
            callStatusBar.setText("\" " + otherUserNickname + " \" 님에게 연결 대기중입니다. \n(서버연결대기중)");

        } else {
            //전화통화 받은 사람일 경우
            position = "callee";
//            callCalleeCancel.setVisibility(View.VISIBLE);
//            callCalleeAccept.setVisibility(View.VISIBLE);

            callStatusBar.setVisibility(View.VISIBLE);
            callStatusBar.setText("\" " + otherUserNickname + " \" 님으로 부터 영상통화 요청이 왔습니다. \n(서버연결대기중)");
        }
        //웹뷰 세팅
        webViewInit();

        //mediaPlayer 벨소리 반복
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mp.start();
            }
        });


        //헨들러 정의
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose = bundle.getString("purpose");

                //영상준비가 완료되면, 필욘한 view 들 webview와 함꼐 띄운다.
                if (purpose.equals("setCallText")) {
                    //발신자
                    if (position.equals("caller")) {
                        videoCameraOff.setVisibility(View.VISIBLE);
                        videoSwap.setVisibility(View.VISIBLE);
                        videoMicOff.setVisibility(View.VISIBLE);
                        callCancel.setVisibility(View.VISIBLE);
                        calleeAccept = true;

                    }
                    //수신자
                    else {
                        callCalleeCancel.setVisibility(View.VISIBLE);
                        callCalleeAccept.setVisibility(View.VISIBLE);

                        //벨소리 시작 핸드폰 상태 체크해서 시작하자.
                        //return 값 : 0 소리모드 , 1 무음모드 ,2 진동모드
                        if (getPhoneAudioState() == 0) {
                            Log.e("123", "소리모드");
                            mediaPlayer.start();

                        } else if (getPhoneAudioState() == 2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long pattern[] = {400, 1000, 400, 1000};
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                            }

                        }


                    }
                } else if (purpose.equals("disconnect")) {
                    callFinish();

                } else if (purpose.equals("tcpDisconnect")) {
                    callFinish();
                } else if (purpose.equals("socketConnectSuccess")) {
                    //수락됬을 때,
                    calleeAccept = true;
                    callCalleeCancel.setVisibility(View.INVISIBLE);
                    callCalleeAccept.setVisibility(View.INVISIBLE);

                    videoCameraOff.setVisibility(View.VISIBLE);
                    videoSwap.setVisibility(View.VISIBLE);
                    videoMicOff.setVisibility(View.VISIBLE);
                    callCancel.setVisibility(View.VISIBLE);
                    //
                } else if (purpose.equals("socketConnectNotYet")) {
                    Toast.makeText(Activity_video_call.this, "서버지연 잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                } else if (purpose.equals("peerConnectionStart")) {
                    //통화시작되면 시간초 재기
                    Log.e("123", "몇번찎히나 보자");
                    callStatusBar.setText(otherUserNickname + "님과 연결되었습니다. 통화시간 00:00");
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isCalling) {

                                Log.e("123", "time : " + callTime);
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                }
                                callTime++;
                                setCallTime();
                            }
                        }
                    });
                    isCalling = true;
                    thread.setDaemon(true);
                    thread.start();
                }
                else if(purpose.equals("socketConnect")){

                    //벨소리시작

                    if(position.equals("caller")){
                        mediaPlayer.start();
                        callStatusBar.setText("\" " + otherUserNickname + " \" 님에게 연결 대기중입니다.");
                    }
                    else if(position.equals("callee")){


                        callStatusBar.setText("\" " + otherUserNickname + " \" 님으로 부터 영상통화 요청이 왔습니다.");

                    }

                }
            }
        };

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123", "눌리고 있냐");
                if (toggle && position.equals("caller")) {
                    videoCameraOff.setVisibility(View.INVISIBLE);
                    videoSwap.setVisibility(View.INVISIBLE);
                    videoMicOff.setVisibility(View.INVISIBLE);
                    callCancel.setVisibility(View.INVISIBLE);
                    toggle = false;
                } else if (!toggle && position.equals("caller")) {
                    videoCameraOff.setVisibility(View.VISIBLE);
                    videoSwap.setVisibility(View.VISIBLE);
                    videoMicOff.setVisibility(View.VISIBLE);
                    callCancel.setVisibility(View.VISIBLE);
                    toggle = true;
                }
//                else if(toggle&&position.equals("callee")&&!calleeAccept){
//                    callCalleeCancel.setVisibility(View.INVISIBLE);
//                    callCalleeAccept.setVisibility(View.INVISIBLE);
//                    toggle=false;
//                }
//                else if(!toggle&&position.equals("callee")&&!calleeAccept){
//                    callCalleeCancel.setVisibility(View.VISIBLE);
//                    callCalleeAccept.setVisibility(View.VISIBLE);
//                    toggle=true;
//                }
                else if (toggle && position.equals("callee") && calleeAccept) {
                    videoCameraOff.setVisibility(View.INVISIBLE);
                    videoSwap.setVisibility(View.INVISIBLE);
                    videoMicOff.setVisibility(View.INVISIBLE);
                    callCancel.setVisibility(View.INVISIBLE);
                    toggle = false;
                } else if (!toggle && position.equals("callee") && calleeAccept) {
                    videoCameraOff.setVisibility(View.VISIBLE);
                    videoSwap.setVisibility(View.VISIBLE);
                    videoMicOff.setVisibility(View.VISIBLE);
                    callCancel.setVisibility(View.VISIBLE);
                    toggle = true;
                }

            }
        });


        //전화 연결 종료 버튼
        callCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinish();
                sendCancelCallAlarm();
            }
        });

        //전화 수락 버튼
        callCalleeAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:startCall()");


            }
        });
        //전화 수신 거절
        callCalleeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinish();
                sendCancelCallAlarm();

            }
        });

        //화면 앞뒤 전환 버튼
        videoSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:changeFaceMode()");
            }
        });
        //카메라 on/off 기능
        videoCameraOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraToggle) {
                    videoCameraOff.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                    cameraToggle = false;
                } else {
                    videoCameraOff.setImageResource(R.drawable.ic_baseline_videocam_on_24);
                    cameraToggle = true;
                }
                webView.loadUrl("javascript:changeCameraMode()");

            }
        });
        //마이크 on/off 기능
        videoMicOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (micToggle) {
                    videoMicOff.setImageResource(R.drawable.ic_baseline_mic_off_24);
                    micToggle = false;
                } else {
                    videoMicOff.setImageResource(R.drawable.ic_baseline_mic_on_24);
                    micToggle = true;
                }
                webView.loadUrl("javascript:changeMuteMode()");
                Log.e("123", "마이크 끄기 off");
            }
        });

    }

    public void sendCancelCallAlarm(){

        Intent sendAlarmintent = new Intent("chatDataToServer");
        sendAlarmintent.putExtra("type",-2);
        sendAlarmintent.putExtra("purpose", "sendNotification");
        sendAlarmintent.putExtra("message", nickname);
        sendAlarmintent.putExtra("postNum", roomNum);
        sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
        LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(sendAlarmintent);

    }

    //통화시간 thread를 돌리면서, 상단 상태표시줄 view에 표시
    public void setCallTime() {
        String timeToString, minute, sec;
        //timeToString=Integer.toString(callTime);


        //시분초 관리
        if (callTime % 60 < 10) {
            sec = Integer.toString(callTime % 60);
            sec = "0" + sec;
        } else {
            sec = Integer.toString(callTime % 60);
        }
        if (callTime / 60 < 10) {
            minute = Integer.toString(callTime / 60);
            minute = "0" + minute;
        } else {
            minute = Integer.toString(callTime / 60);
        }

        callStatusBar.setText(otherUserNickname + "님과 연결되었습니다. 통화시간 " + minute + ":" + sec);
    }

    @Override
    public void onBackPressed() {
//        callFinish();
//        //벨소리 제거,진동
//        releaseMediaPlayer();
//        vibrator.cancel();
//
//        //timeThread 관련
//        isCalling = false;
//        if (thread != null) {
//            thread.interrupt();
//        }
//        //mediaplayer null로 변환
//        webView.loadUrl("javascript:finishCall()");
//        webView.removeAllViews();
//        webView.destroy();
//        finish();
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
        //mWebSettings.setAllowContentAccess(true);
        mWebSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        mWebSettings.setUseWideViewPort(true); //화면 사이즈 맞추기
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        //mWebSettings.setDomStorageEnabled(true);


        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setUserAgentString(null);
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그
        //webView.clearCache(true);
        //webView.loadUrl(serverURL +"?roomNum="+roomNum+"&sendToNickname="+otherUserNickname+"&position="+position);
        webView.loadUrl(serverURL + "/" + roomNum + "/" + otherUserNickname + "/" + position);

    }

    public int getPhoneAudioState() {
        int returnNum = -1;
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                Log.e("123", "소리모드");
                returnNum = 0;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                Log.e("123", "무음모드");
                returnNum = 1;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.e("123", "진동모드");
                returnNum = 2;
                break;
            default:
                break;
        }
        Log.e("123", "현재모드 : " + returnNum);
        return returnNum;
    }

    public void variableInit() {


        //localBroadCastRecevier
        LocalBroadcastManager.getInstance(Activity_video_call.this).registerReceiver(dataReceiver, new IntentFilter("videoCall"));

        //audioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        mediaPlayer = MediaPlayer.create(Activity_video_call.this, R.raw.callingsound);
        //기본 xml
        callStatusBar = findViewById(R.id.video_call_status_bar);
        constraintLayout = findViewById(R.id.video_call_constraint);
        //constraintLayout=findViewById(R.id.video_call_constraint);
        progressBar = findViewById(R.id.video_call_progressbar);
        callInfoText = findViewById(R.id.video_call_info);

        videoCameraOff = (ImageView) findViewById(R.id.video_call_camera_off);
        videoMicOff = (ImageView) findViewById(R.id.video_call_mic_off);
        videoSwap = (ImageView) findViewById(R.id.video_call_camera_swap);
        callCancel = (ImageView) findViewById(R.id.video_call_cancel);
        callCalleeAccept = (ImageView) findViewById(R.id.video_call_callee_accept);
        callCalleeCancel = (ImageView) findViewById(R.id.video_call_callee_cancel);


        // shared 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickName", "");


    }

    public void callFinish(){


        //벨소리 제거,진동
        releaseMediaPlayer();
        vibrator.cancel();

        //timeThread 관련
        isCalling = false;
        if (thread != null) {
            thread.interrupt();
        }
        //mediaplayer null로 변환
        webView.loadUrl("javascript:finishCall()");
        //background 에서 실행될 경우..
        if(Activity_main_home.activity_main_home==null){
            Log.e("123","finishAndRemoveTask()");
            Intent intent = new Intent(this,Activity_main_home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("kill", true);
            startActivity(intent);
            if(webView!=null){
                webView.destroy();
            }
            finish();
        }
        //기존 앱 켜진 상태에서 실행될 경우
        else{
            if(webView!=null){
                webView.destroy();
            }
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
        Toast.makeText(getApplicationContext(), "영상통화가 종료되었습니다.", Toast.LENGTH_SHORT).show();
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