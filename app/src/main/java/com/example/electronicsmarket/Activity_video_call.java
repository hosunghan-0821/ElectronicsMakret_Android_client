package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
    private String position, timeToString = "";
    private WebSettings mWebSettings;
    private String serverURL = "https://f1e2-219-248-76-133.ngrok.io";
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
    private boolean cutterState = false;
    private boolean missedCall = false;
    private Thread missedThread;
    private boolean callOtherUser = false, alreadyCall = false;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String purpose = intent.getStringExtra("purpose");
            Log.e("123", "Video Call getIntent");
            Log.e("123", "purpose : " + intent.getStringExtra("purpose"));
            Log.e("123", "otherUserNickname : " + intent.getStringExtra("otherUserNickname"));
            String disconnectFromUser = intent.getStringExtra("otherUserNickname");
            if (purpose != null) {
                if (purpose.equals("disconnect")) {
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
        @SuppressWarnings("unused") //??????????????? ??????????????? ???????????? ???????????? ???????????? ??? ???????????? ????????????
        public void setCallInfo() {
            Log.e("123", "setCallInfo");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "setCallText");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused") //??????????????? ??????????????? ???????????? ???????????? ???????????? ??? ???????????? ????????????
        public void callOtherUser() {
            callOtherUser = true;
            if (position.equals("caller")) {

//                //????????? ????????? ???????????????.
//                Intent intent = new Intent("chatDataToServer");
//                intent.putExtra("purpose", "send");
//                intent.putExtra("message", "????????????");
//                intent.putExtra("callPurpose", "call");
//                intent.putExtra("otherUserNickname", otherUserNickname);
//                intent.putExtra("roomNum", roomNum);
//                LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(intent);

                Intent sendAlarmintent = new Intent("chatDataToServer");
                sendAlarmintent.putExtra("purpose", "sendNotification");
                sendAlarmintent.putExtra("message", nickname);
                sendAlarmintent.putExtra("postNum", roomNum);
                sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
                LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(sendAlarmintent);

                //?????? ??????????????? ????????????????????? ?????????.
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "socketConnect");
                msg.setData(bundle);
                handler.sendMessage(msg);

                //??? ?????? ????????? ????????? ???????????? ?????? thread ?????????
                missedThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15000);
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("purpose", "missedCall");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
                missedThread.start();

            } else if (position.equals("callee")) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "socketConnect");
                msg.setData(bundle);
                handler.sendMessage(msg);


            }
        }

        @JavascriptInterface
        @SuppressWarnings("unused") //??????????????? ??????????????? ???????????? ???????????? ???????????? ??? ???????????? ????????????
        public void socketConnectSuccess() {

            Log.e("123", "setCallInfo");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "socketConnectSuccess");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused") //??????????????? ??????????????? ???????????? ???????????? ???????????? ??? ???????????? ????????????
        public void missedThreadInterrupt() {
            if (missedThread != null) {
                missedThread.interrupt();
            }

        }

        @JavascriptInterface
        @SuppressWarnings("unused") //??????????????? ??????????????? ???????????? ???????????? ?????????
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


//            isCalling = false;
//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putString("purpose", "disconnect");
//            msg.setData(bundle);
//            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void peerConnectionSuccess() {
            //????????? ??????
            releaseMediaPlayer();
            //????????????
            vibrator.cancel();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "peerConnectionStart");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void cannotCall() {
            //????????? ??????????????? ????????? ????????? ?????? activity ??????


            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("purpose", "cannotCall");
            msg.setData(bundle);
            handler.sendMessage(msg);

            Log.e("123", "????????? ?????????");
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
        //?????? activity ??? ????????? intent ?????? ?????? ??????, ????????? ????????????, (????????? or ?????????) ?????? intent ??? ?????? ??????
        Intent intent = getIntent();
        otherUserNickname = intent.getStringExtra("sendToNickname");
        roomNum = intent.getStringExtra("roomNum");

        //?????????, ???????????? ?????????, view ????????????
        if (intent.getStringExtra("position") != null) {
            //???????????? ??? ????????? ??????
            position = "caller";
//            videoCameraOff.setVisibility(View.VISIBLE);
//            videoSwap.setVisibility(View.VISIBLE);
//            videoMicOff.setVisibility(View.VISIBLE);
//            callCancel.setVisibility(View.VISIBLE);
//            calleeAccept = true;

            callStatusBar.setVisibility(View.VISIBLE);
            callStatusBar.setText("\" " + otherUserNickname + " \" ????????? ?????? ??????????????????. \n(?????????????????????)");

        } else {
            //???????????? ?????? ????????? ??????
            position = "callee";
//            callCalleeCancel.setVisibility(View.VISIBLE);
//            callCalleeAccept.setVisibility(View.VISIBLE);

            callStatusBar.setVisibility(View.VISIBLE);
            callStatusBar.setText("\" " + otherUserNickname + " \" ????????? ?????? ???????????? ????????? ????????????. \n(?????????????????????)");
        }
        //?????? ??????
        webViewInit();

        //mediaPlayer ????????? ??????
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mp.start();
            }
        });

        //????????? ??????
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose = bundle.getString("purpose");

                //??????????????? ????????????, ????????? view ??? webview??? ?????? ?????????.
                if (purpose.equals("setCallText")) {
                    //?????????
                    if (position.equals("caller")) {
                        videoCameraOff.setVisibility(View.VISIBLE);
                        videoSwap.setVisibility(View.VISIBLE);
                        videoMicOff.setVisibility(View.VISIBLE);
                        callCancel.setVisibility(View.VISIBLE);
                        calleeAccept = true;
                    }
                    //?????????
                    else {
                        videoSwap.setVisibility(View.VISIBLE);
                        callCalleeCancel.setVisibility(View.VISIBLE);
                        callCalleeAccept.setVisibility(View.VISIBLE);

                        //????????? ?????? ????????? ?????? ???????????? ????????????.
                        //return ??? : 0 ???????????? , 1 ???????????? ,2 ????????????
                        if (getPhoneAudioState() == 0) {
                            Log.e("123", "????????????");
                            mediaPlayer.start();

                        } else if (getPhoneAudioState() == 2) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                long pattern[] = {400, 1000, 400, 1000};
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                            }
                        }
                    }
                } else if (purpose.equals("disconnect")) {
                    callFinish(false);
                } else if (purpose.equals("tcpDisconnect")) {
                    callFinish(false);
                } else if (purpose.equals("socketConnectSuccess")) {
                    //???????????? ???,
                    calleeAccept = true;
                    callCalleeCancel.setVisibility(View.INVISIBLE);
                    callCalleeAccept.setVisibility(View.INVISIBLE);

                    videoCameraOff.setVisibility(View.VISIBLE);
                    videoSwap.setVisibility(View.VISIBLE);
                    videoMicOff.setVisibility(View.VISIBLE);
                    callCancel.setVisibility(View.VISIBLE);
                    //
                } else if (purpose.equals("socketConnectNotYet")) {
                    Toast.makeText(Activity_video_call.this, "???????????? ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                } else if (purpose.equals("peerConnectionStart")) {
                    //?????????????????? ????????? ??????
                    Log.e("123", "??????????????? ??????");
                    callStatusBar.setText(otherUserNickname + "?????? ?????????????????????. ???????????? 00:00");
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
                } else if (purpose.equals("socketConnect")) {

                    //???????????????
                    if (position.equals("caller")) {
                        mediaPlayer.start();
                        callStatusBar.setText("\" " + otherUserNickname + " \" ????????? ?????? ??????????????????.");
                    } else if (position.equals("callee")) {
                        callStatusBar.setText("\" " + otherUserNickname + " \" ????????? ?????? ???????????? ????????? ????????????.");

                    }

                } else if (purpose.equals("missedCall")) {
                    missedCall = true;
                    callFinish(true);
                    sendCancelCallAlarm();


                } else if (purpose.equals("cannotCall")) {
                    alreadyCall = true;
                    callFinish(true);
                }
            }
        };

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123", "????????? ??????");
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
                } else if (toggle && position.equals("callee") && calleeAccept) {
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


        //?????? ?????? ?????? ??????
        callCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinish(true);
                sendCancelCallAlarm();
            }
        });

        //?????? ?????? ??????
        callCalleeAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:startCall()");


            }
        });
        //?????? ?????? ??????
        callCalleeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinish(true);
                sendCancelCallAlarm();

            }
        });

        //?????? ?????? ?????? ??????
        videoSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //????????? ???????????? ?????? /????????? ???????????? ??????
                if (cameraToggle && micToggle) {
                    webView.loadUrl("javascript:changeFaceMode(true,true)");
                }
                //????????? ???????????? / ????????? ??????????????????
                else if (cameraToggle && !micToggle) {
                    webView.loadUrl("javascript:changeFaceMode(true,false)");
                }
                //????????? ????????????
                else if (!cameraToggle) {
                    Toast.makeText(Activity_video_call.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //????????? on/off ??????
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
        //????????? on/off ??????
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
                Log.e("123", "????????? ?????? off");
            }
        });

    }

    public void sendCancelCallAlarm() {

        if (!callOtherUser&&position.equals("caller")) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent sendAlarmintent = new Intent("chatDataToServer");
                sendAlarmintent.putExtra("type", -2);
                sendAlarmintent.putExtra("purpose", "sendNotification");
                sendAlarmintent.putExtra("message", nickname);
                sendAlarmintent.putExtra("postNum", roomNum);
                sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
                LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(sendAlarmintent);
            }
        }, 200);


    }

    //???????????? thread??? ????????????, ?????? ??????????????? view??? ??????
    public void setCallTime() {

        String minute, sec;
        //????????? ??????
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
        timeToString = minute + ":" + sec;
        callStatusBar.setText(otherUserNickname + "?????? ?????????????????????. ???????????? " + minute + ":" + sec);
    }

    @Override
    public void onBackPressed() {
    }

    public void webViewInit() {
        //?????? ?????? ??????
        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.video_call_webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.e("123", "????????????1");
                        String[] PERMISSIONS = {
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                        };
                        request.grant(PERMISSIONS);
                        Log.e("123", "????????????2");
                    }
                });
            }
        });

        webView.addJavascriptInterface(new Activity_video_call.MyJavaScriptInterface(), "Android");
        webView.setInitialScale(1);
        mWebSettings = webView.getSettings(); // ???????????? webSettings??? ????????? ??? ????????? ???.
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        //mWebSettings.setAllowContentAccess(true);
        mWebSettings.setJavaScriptEnabled(true); //???????????? javascript??? ??????????????? ??????
        mWebSettings.setUseWideViewPort(true); //?????? ????????? ?????????
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        //mWebSettings.setDomStorageEnabled(true);


        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setUserAgentString(null);
        mWebSettings.setLoadWithOverviewMode(true); // ????????????
        //webView.clearCache(true);
        //webView.loadUrl(serverURL +"?roomNum="+roomNum+"&sendToNickname="+otherUserNickname+"&position="+position);
        webView.loadUrl(serverURL + "/" + roomNum + "/" + otherUserNickname + "/" + position + "/" + nickname);

    }

    public int getPhoneAudioState() {
        int returnNum = -1;
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                Log.e("123", "????????????");
                returnNum = 0;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                Log.e("123", "????????????");
                returnNum = 1;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.e("123", "????????????");
                returnNum = 2;
                break;
            default:
                break;
        }
        Log.e("123", "???????????? : " + returnNum);
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
        //?????? xml
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


        // shared ??? ????????????
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickName", "");


    }

    public void callFinish(boolean cutter) {

        Log.e("123", "callFinsih()  ??????");
        //nullPointer exception ??????.
        if (cutterState) {
            return;
        }
        if (cutter) {
            cutterState = true;
            Thread saveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("123", "???????????? ?????? : " + nickname);
                    //tcp ????????? ????????? ??????
                    Intent resultIntent = new Intent("chatDataToServer");
                    resultIntent.putExtra("purpose", "send");
                    resultIntent.putExtra("callPurpose", "result");
                    resultIntent.putExtra("roomNum", roomNum);
                    resultIntent.putExtra("sendToNickname", otherUserNickname);

                    //?????? ?????? ????????? ?????? ??? ????????? ??????, (caller)
                    if (position.equals("caller")) {

                        //??????????????? ????????? ??????????????? ??? ?????? ???????????? db??? ??????
                        resultIntent.putExtra("caller", nickname);

                        if (timeToString != null) {
                            //?????? ?????? x
                            if (timeToString.equals("")) {
                                //????????? (?????????)???????????? ??? ???,
                                if (missedCall) {
                                    resultIntent.putExtra("message", "????????????3");
                                } else if (alreadyCall) {
                                    resultIntent.putExtra("message", "????????? ?????????");
                                }
                                //????????? ????????? ????????? ???,
                                else {
                                    resultIntent.putExtra("message", "????????????1");
                                }
                            }
                            //?????? ??????
                            else {
                                Log.e("123", "?????????????????? : " + timeToString);
                                resultIntent.putExtra("message", timeToString);
                            }
                        }
                    }
                    //?????? ?????? ????????? ????????? ?????? ????????? ?????? (callee)
                    else {
                        Log.e("123", "????????? ?????? : " + otherUserNickname);
                        resultIntent.putExtra("caller", otherUserNickname);

                        if (timeToString != null) {
                            //?????? ?????? x
                            if (timeToString.equals("")) {
                                resultIntent.putExtra("message", "????????????2");
                            }
                            //?????? ??????
                            else {
                                Log.e("123", "?????????????????? : " + timeToString);
                                resultIntent.putExtra("message", timeToString);
                            }
                        }

                    }
                    LocalBroadcastManager.getInstance(Activity_video_call.this).sendBroadcast(resultIntent);
                }
            });
            saveThread.start();
        }
        if (missedThread != null) {
            missedThread.interrupt();
        }
        Log.e("123", "???????????????");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("123", "postDelayed ??????");
                //????????? ??????,??????
                releaseMediaPlayer();
                vibrator.cancel();

                //timeThread ??????
                isCalling = false;
                if (thread != null) {
                    thread.interrupt();
                }
                webView.loadUrl("javascript:finishCall()");

                //background ?????? ????????? ??????..
                if (Activity_main_home.activity_main_home == null) {
                    Log.e("123", "finishAndRemoveTask()");
                    Intent intent = new Intent(Activity_video_call.this, Activity_main_home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("kill", true);
                    startActivity(intent);
                    if (webView != null) {
                        webView.destroy();
                    }
                    finish();
                }
                //?????? ??? ?????? ???????????? ????????? ??????
                else {
                    Log.e("123","foreground");
                    if (webView != null) {
                        webView.destroy();
                        Log.e("123","foreground webview destroy() ");
                    }
                    finish();

                    if (alreadyCall) {
                        Toast.makeText(getApplicationContext(), "???????????? ??????????????????. ?????? ??? ?????? ?????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 600);// 0.6??? ?????? ???????????? ??? ??? ??????

        Log.e("123", "???????????????2");
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
        //Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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