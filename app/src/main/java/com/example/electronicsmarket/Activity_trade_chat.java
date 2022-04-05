package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_trade_chat extends AppCompatActivity {

    private HashMap<String, RequestBody> requestMap;
    public static Activity_trade_chat activity_trade_chat;
    public static String roomNumGlobal;
    public static String otherUserNicknameGlobal;
    private boolean isFinalPhase = false, onCreateViewIsSet = false, scrollCheck = true;
    private int heightSum;
    private ImageView tradeChatImage, tradeChatVideoCamera;
    private TextView tradeChatProductTitle, tradeChatProductPrice, tradeChatLocation, tradeChatOtherUserNickname;
    private Retrofit retrofit;
    private TextView scrollHeight;
    private String postNum, seller, buyer, roomNum;
    private RecyclerView recyclerView;
    private ArrayList<exampleData> dataList;
    private LinearLayoutManager linearLayoutManager;
    private Handler handler;
    private Adapter_trade_chat adapter;
    private ArrayList<DataChat> chatList;
    private String nickName;
    private EditText tradeChatSendText;
    private ImageView tradeChatImageSend, tradeChatSendTextImage;
    private View.OnLayoutChangeListener layoutChangeListener;
    private String otherUserImageRoute;
    private String cursorChatNum, phasingNum;
    private Adapter_trade_chat.Interface_itemHeightCheck checkHeight;
    private boolean roomNumCheck = false;
    private String otherUserNickname;
    private ImageView backImage, tradeChatLocationImage;
    private int peopleNum;
    private TextView tradeChatSellType;
    private PermissionListener permissionlistener, videoCallPermissionListener;
    private boolean permissionCheck = false;
    private ArrayList<File> imageFileCollect;
    private ArrayList<MultipartBody.Part> files;
    private LinearLayout tradeChatAnnounce;
    private int resumeAddChatCheck;
    private Thread sendThread;
    private ArrayList<String> imageRoute;
    private ArrayList<String> roomMemberNickname;


    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //채팅방 명수확인
            String purpose = intent.getStringExtra("purpose");
            if (purpose != null) {
                if (purpose.equals("인원체크")) {

                    int chatRoomPeople = intent.getIntExtra("peopleNum", -1);
                    Log.e("123", "방 인원수" + chatRoomPeople);
                    ArrayList<String> roomUsers = intent.getStringArrayListExtra("roomUsers");

                    for (int i = 0; i < roomUsers.size(); i++) {
                        roomMemberNickname.add(roomUsers.get(i));
                    }
                    peopleNum = chatRoomPeople;

                } else if (purpose.equals("인원추가")) {
                    String userNickname = intent.getStringExtra("nickname");
                    boolean nameDuplicateCheck = false;
                    for (int i = 0; i < roomMemberNickname.size(); i++) {

                        if (userNickname.equals(roomMemberNickname.get(i))) {
                            Log.e("123", "중복된 닉네임 : " + userNickname);
                            nameDuplicateCheck = true;
                            break;
                        }
                    }
                    if (!nameDuplicateCheck) {
                        Log.e("123", "방 인원추가 중복안된 닉네임 " + userNickname);
                        roomMemberNickname.add(userNickname);
                        //데이터 reload 해야함
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("purpose", "reloadData");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        peopleNum++;
                    }

                } else if (purpose.equals("인원감소")) {
                    String nickname = intent.getStringExtra("nickname");
                    boolean nameDuplicateCheck = false;
                    for (int i = 0; i < roomMemberNickname.size(); i++) {
                        if (nickname.equals(roomMemberNickname.get(i))) {
                            nameDuplicateCheck = true;
                            roomMemberNickname.remove(i);
                            break;
                        }
                    }
                    if (nameDuplicateCheck) {
                        peopleNum--;
                    }

                } else if (purpose.equals("reload")) {

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("purpose", "reloadActivity");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

                for (int i = 0; i < roomMemberNickname.size(); i++) {
                    Log.e("123", "roomMemberNickname : " + roomMemberNickname.get(i));
                }
                Log.e("123", "나를 제외한 채팅방 인원수 : " + peopleNum);
                return;
            }
            //상대방으로부터 채팅 데이터 받는 용인지.
            String type = intent.getStringExtra("type");
            String message = intent.getStringExtra("message");
            String writer = intent.getStringExtra("writer");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("purpose", "getData");
            bundle.putString("writer", writer);
            bundle.putString("message", message);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("123", "oncreate()");
        setContentView(R.layout.activity_trade_chat);
        variableInit();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent intent = getIntent();

        //권한체크
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Activity_main_home.permissionCheck = true;
                //Toast.makeText(Activity_trade_chat.this, "사진,이미지 권한허용", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Activity_trade_chat.this, "이미지 전송하기 위해선 권한필요", Toast.LENGTH_SHORT).show();
            }
        };
        videoCallPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_trade_chat.this);

                builder.setTitle("영상통화 알림");
                builder.setMessage("\" "+otherUserNickname+" \"님에게 영상통화를 거시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Activity_trade_chat.this, Activity_video_call.class);
                        intent.putExtra("sendToNickname", otherUserNickname);
                        intent.putExtra("roomNum", roomNum);
                        intent.putExtra("position","caller");
                        startActivity(intent);

                        Intent sendAlarmintent = new Intent("chatDataToServer");
                        sendAlarmintent.putExtra("purpose", "sendNotification");
                        sendAlarmintent.putExtra("message", "영상통화");
                        sendAlarmintent.putExtra("sendToNickname", otherUserNickname);
                        LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(sendAlarmintent);

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();



            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Activity_trade_chat.this, "영상통화 하기위해서 권한 재확인 부탁 ", Toast.LENGTH_SHORT).show();
                return;
            }
        };

        //영상통화 클릭 리스너
        tradeChatVideoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoRequestPermission();
            }
        });


        //이 채팅방으로 들어오는 경우를 두가지로 나누어서, 생각하고 있다
        //1번. 채팅문의하기를 통해 채팅방 들어오기
        //2번. 현재 존재하는 채팅목록으로 부터 들어오기
        if (intent.getStringExtra("roomNum") == null) {
            postNum = intent.getStringExtra("postNum");
            seller = intent.getStringExtra("seller");
            buyer = intent.getStringExtra("buyer");
        } else {
            roomNum = intent.getStringExtra("roomNum");
        }
        activity_trade_chat = Activity_trade_chat.this;


        Log.e("123", "Service_Example instance " + Service_Example.tcpService);
        if (Service_Example.tcpService == null) {
            Log.e("123", "pendingIntent로 들어왔을 경우 service 재시작");
            Intent serviceIntent = new Intent(getApplicationContext(), Service_Example.class);
            startService(serviceIntent);
        }

        //        Log.e("123","postNum : "+postNum);
        //        Log.e("123","seller : "+ seller);
        //        Log.e("123","buyer : "+ buyer);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoom> call = service.roomNumCheck(postNum, seller, buyer, roomNum, nickName);

        //채팅방 들어갔을 때, 기본적인 데이터들 가져오기
        call.enqueue(new Callback<DataChatRoom>() {
            @Override
            public void onResponse(Call<DataChatRoom> call, Response<DataChatRoom> response) {
                if (response.isSuccessful() && response.body() != null) {

                    DataChatRoom dataChatRoom = response.body();

                    otherUserNickname = dataChatRoom.getOtherUserNickname();
                    roomNum = dataChatRoom.getRoomNum();

                    roomNumGlobal = roomNum;
                    otherUserNicknameGlobal = otherUserNickname;

                    tradeChatOtherUserNickname.setText(otherUserNickname);
                    otherUserImageRoute = dataChatRoom.getOtherUserImageRoute();
                    tradeChatProductTitle.setText(dataChatRoom.getPostTitle());
                    tradeChatProductPrice.setText(dataChatRoom.getPostPrice() + "원");
                    tradeChatSellType.setText("(" + dataChatRoom.getPostSellType() + ")");
                    //장소 정보 없을 경우에는 화면에 나타나지 않게.
                    if (dataChatRoom.getPostLocationName() != null) {

                        if (dataChatRoom.getPostLocationName().equals("장소정보 없음")) {

                            tradeChatLocation.setVisibility(View.INVISIBLE);
                            tradeChatLocationImage.setVisibility(View.INVISIBLE);

                        } else {

                            String postLocationName = "";
                            String postLocationDetail = "";
                            if (dataChatRoom.getPostLocationName().length() > 9) {
                                postLocationName = dataChatRoom.getPostLocationName().substring(0, 9) + "..";
                            } else {
                                postLocationName = dataChatRoom.getPostLocationName();
                            }
                            if (dataChatRoom.getPostLocationDetail().length() > 9) {
                                postLocationDetail = dataChatRoom.getPostLocationDetail().substring(0, 9) + "..";
                            } else {
                                postLocationDetail = dataChatRoom.getPostLocationDetail();
                            }
                            tradeChatLocation.setText("거래장소 : " + postLocationName + "\n상세위치 : " + postLocationDetail);
                        }
                    }

                    Glide.with(Activity_trade_chat.this).load(dataChatRoom.getImageRoute()).into(tradeChatImage);

                    // 채팅방 정보 받아와서 데이터를 입력해야함.

                    //  데이터 보낼 떄 쓸 것들
                    //  채팅방 입장!!
                    Intent intent = new Intent("chatDataToServer");
                    intent.putExtra("purpose", "changeRoomNum");
                    intent.putExtra("roomNum", roomNum);
                    intent.putExtra("otherUserNickname", otherUserNickname);
                    intent.putExtra("message", roomNum + ":" + otherUserNickname);
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

                    // 채팅방 정보 입력한 후에, 데이터를 받을 준비를 완료 시킨다.
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).registerReceiver(dataReceiver, new IntentFilter("chatData"));
                    //권한 체크. 권한 체크할 때, onresume/onpause 발생해서, 채팅방 나가고 들어오는 현상 생김.. socket통신할 때,
                    //어떻게 처리할지 생각해볼 필요는 있겠다.
//                    requestPermission();
                    //이런 데이터들 다 불러오고 나서 채팅방 대화내용도 서버로부터 불러오기
                    Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, phasingNum, cursorChatNum, nickName);
                    chatDataCall.enqueue(new Callback<DataChatAll>() {
                        @Override
                        public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                DataChatAll dataChatAllList = response.body();
                                ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                                //shared에 저장된 안보내진 데이터 입력 나타나게 하기;
                                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                                if (noSendDataArrayList.size() != 0) {
                                    for (int i = 0; i < noSendDataArrayList.size(); i++) {
                                        if (noSendDataArrayList.get(i).getChatRoomNum() != null) {
                                            if (noSendDataArrayList.get(i).getChatRoomNum().equals(roomNum)) {
                                                chatArrayList.add(0, noSendDataArrayList.get(i));
                                            }
                                        }

                                    }
                                }

                                for (int i = 0; i < chatArrayList.size(); i++) {
                                    //chatList에 넣기위해 가공 및 데이터 분류

                                    int networkStatus = chatArrayList.get(i).getNetworkStatus();
                                    String chatType = chatArrayList.get(i).getChatType();
                                    String writerNickname = chatArrayList.get(i).getNickname();
                                    String chatText = chatArrayList.get(i).getChat();
                                    chatText = chatText.replace(Service_Example.CHANGE_LINE_CHAR, "\n");
                                    String chatTime = chatArrayList.get(i).getChatTime();
                                    String isReadChat = chatArrayList.get(i).getIsReadChat();
                                    Date chatDate;
                                    SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    try {
                                        chatDate = chatTimeDbDateFormat.parse(chatTime);
                                        chatTime = chatTimedateFormat.format(chatDate);
                                    } catch (Exception e) {

                                    }
                                    //나 자신일 경우
                                    if (writerNickname.equals(nickName)) {

                                        if (chatType != null) {
                                            if (chatType.equals("text")) {

                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat, 3, "text", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                                }

                                            } else if (chatType.equals("image")) {

                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat, 3, "image", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                                }

                                            }
                                        }

                                    }
                                    //서버일 경우
                                    else if (writerNickname.equals("server")) {
                                        chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                    }
                                    //상대방일 경우
                                    else {

                                        if (chatType != null) {

                                            if (chatType.equals("text")) {
                                                chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("image")) {
                                                chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            }

                                        }
                                    }
                                }
                                setStackFromEnd();
                                resumeAddChatCheck = chatList.size();
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(chatList.size() - 1);
                                if (chatList.size() != 0) {
                                    cursorChatNum = chatArrayList.get(chatArrayList.size() - 1).getChatNum();
//                                    cursorChatNum = chatArrayList.get(chatArrayList.size() - 1).getChatTime();
                                }
                                if (!response.body().getChatNum().equals(phasingNum)) {
                                    isFinalPhase = true;
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<DataChatAll> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<DataChatRoom> call, Throwable t) {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (!v.canScrollVertically(-1) && scrollCheck) {

                        scrollCheck = false;
                        if (!isFinalPhase) {
                            Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, phasingNum, cursorChatNum, nickName);
                            chatDataCall.enqueue(new Callback<DataChatAll>() {
                                @Override
                                public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                                    if (response.isSuccessful() && response.body() != null) {
                                        DataChatAll dataChatAllList = response.body();
                                        ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();
                                        for (int i = 0; i < chatArrayList.size(); i++) {

                                            String chatType = chatArrayList.get(i).getChatType();
                                            String writerNickname = chatArrayList.get(i).getNickname();
                                            String chatText = chatArrayList.get(i).getChat();
                                            String isReadChat = chatArrayList.get(i).getIsReadChat();
                                            chatText = chatText.replace(Service_Example.CHANGE_LINE_CHAR, "\n");
                                            String chatTime = chatArrayList.get(i).getChatTime();
                                            Date chatDate;
                                            SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                                            SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            try {
                                                chatDate = chatTimeDbDateFormat.parse(chatTime);
                                                chatTime = chatTimedateFormat.format(chatDate);
                                            } catch (Exception e) {

                                            }
                                            //나 자신일 경우
                                            if (writerNickname.equals(nickName)) {

                                                if (chatType != null) {
                                                    if (chatType.equals("text")) {
                                                        chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                                    } else if (chatType.equals("image")) {
                                                        chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                                    }
                                                }

                                            }
                                            //서버일 경우
                                            else if (writerNickname.equals("server")) {
                                                chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                            }
                                            //상대방일 경우
                                            else {
                                                if (chatType != null) {

                                                    if (chatType.equals("text")) {
                                                        chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                                    } else if (chatType.equals("image")) {
                                                        chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                                    }

                                                }
                                            }
                                            adapter.notifyItemInserted(0);
                                        }

                                        setStackFromEnd();
                                        try {
                                            if (chatList.size() != 0) {
                                                cursorChatNum = chatArrayList.get(chatArrayList.size() - 1).getChatNum();
                                                Log.e("123", "cursorChatNum" + cursorChatNum);
                                                // cursorChatNum = chatArrayList.get(chatArrayList.size() - 1).getChatTime();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (!response.body().getChatNum().equals(phasingNum)) {
                                            isFinalPhase = true;
                                        }
                                        scrollCheck = true;
                                        resumeAddChatCheck = chatList.size();
                                    }

                                }

                                @Override
                                public void onFailure(Call<DataChatAll> call, Throwable t) {

                                }
                            });

                        }
                    } else if (!v.canScrollVertically(1)) {
                        tradeChatAnnounce.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Toast.makeText(Activity_trade_chat.this, "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }


        //데이터 전송 받았을 때, 데이터 보낸사람 닉네임 , 내용으로 나누어서
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose = bundle.getString("purpose");

                //채팅 데이터 넘어올 경우
                if (purpose.equals("getData")) {
                    String type = bundle.getString("type");
                    String message = bundle.getString("message");
                    String writerNickname = bundle.getString("writer");
                    try {
                        String formatedNow = getMessageTime();

                        //작성자가 나 자신일 경우.. 근데 이거 서버에서 이제 안보내서.. 의미 x
                        if (writerNickname.equals(nickName)) {
                            chatList.add(new DataChat(message, 1, formatedNow, nickName, "1"));
                        }
                        //서버로부터 온 알림일 경우,
                        else if (writerNickname.equals("server")) {
                            chatList.add(chatList.size() - 1, new DataChat(message, 2, formatedNow, "server", "1"));
                            adapter.notifyItemInserted(chatList.size() - 1);
                            recyclerView.scrollToPosition(chatList.size() - 1);
                            resumeAddChatCheck = chatList.size();
                        }
                        //상대방으로부터 온 경우
                        else {
                            if (type.equals("text")) {
                                chatList.add(new DataChat(0, message, formatedNow, writerNickname, otherUserImageRoute));
                                resumeAddChatCheck = chatList.size();

                            } else if (type.equals("image")) {
                                chatList.add(new DataChat(4, message, formatedNow, writerNickname, otherUserImageRoute));
                                resumeAddChatCheck = chatList.size();
                            }
                            //scroll 컨트롤
                            Log.e("123", "lastVisiblePosition : " + findLastVisiblePosition());
                            Log.e("123", "chatList.size() : " + chatList.size());
                            if (chatList.size() - findLastVisiblePosition() <= 2) {

                                tradeChatAnnounce.setVisibility(View.GONE);
                                adapter.notifyItemInserted(chatList.size() - 1);
                                recyclerView.scrollToPosition(chatList.size() - 1);
                            } else {
                                Log.e("123", "데이터 올 때 안내 메시지");
                                tradeChatAnnounce.setVisibility(View.VISIBLE);
                                adapter.notifyItemInserted(chatList.size() - 1);
                            }


                        }

                        setStackFromEnd();
//                  adapter.notifyItemInserted(chatList.size()-1);

                        Log.e("123", "recyclerview 바텀" + recyclerView.getBottom());
                        Log.e("123", "recyclerview .getheight" + recyclerView.getLayoutManager().getHeight());
                        System.out.println("여기 들어옴?");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (purpose.equals("reloadData")) {

                    for (int i = 0; i < chatList.size(); i++) {
                        chatList.get(i).setIsReadChat("1");
                    }
                    adapter.notifyDataSetChanged();
                } else if (purpose.equals("reloadActivity")) {
                    Log.e("123", "reloadActivity");
                    roomMemberNickname.clear();
                    //화면 reload할게 아니라 onResume 처럼 데이터를 다시 가져오는 방식을 사용하자

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, "update", cursorChatNum, nickName);
                    chatDataCall.enqueue(new retrofit2.Callback<DataChatAll>() {
                        @Override
                        public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                chatList.clear();
                                DataChatAll dataChatAllList = response.body();
                                ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                                //shared에 저장된 안보내진 데이터 입력 나타나게 하기;
                                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                                if (noSendDataArrayList.size() != 0) {
                                    for (int i = 0; i < noSendDataArrayList.size(); i++) {
                                        if (noSendDataArrayList.get(i).getChatRoomNum() != null) {
                                            if (noSendDataArrayList.get(i).getChatRoomNum().equals(roomNum)) {
                                                chatArrayList.add(0, noSendDataArrayList.get(i));
                                            }
                                        }

                                    }
                                }

                                for (int i = 0; i < chatArrayList.size(); i++) {
                                    String chatType = chatArrayList.get(i).getChatType();
                                    String writerNickname = chatArrayList.get(i).getNickname();
                                    String chatText = chatArrayList.get(i).getChat();
                                    String isReadChat = chatArrayList.get(i).getIsReadChat();
                                    int networkStatus = chatArrayList.get(i).getNetworkStatus();
                                    chatText = chatText.replace(Service_Example.CHANGE_LINE_CHAR, "\n");
                                    String chatTime = chatArrayList.get(i).getChatTime();
                                    Date chatDate;
                                    SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        chatDate = chatTimeDbDateFormat.parse(chatTime);
                                        chatTime = chatTimedateFormat.format(chatDate);
                                    } catch (Exception e) {

                                    }
                                    //나 자신일 경우
                                    if (writerNickname.equals(nickName)) {

                                        if (chatType != null) {

                                            if (chatType.equals("text")) {

                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat, 3, "text", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                                }

                                            } else if (chatType.equals("image")) {
                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat, 3, "image", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                                }

                                            }
                                        }
                                    }
                                    //서버일 경우
                                    else if (writerNickname.equals("server")) {
                                        chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                    }
                                    //상대방일 경우
                                    else {
                                        if (chatType != null) {

                                            if (chatType.equals("text")) {
                                                chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("image")) {
                                                chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            }

                                        }
                                    }
                                    // adapter.notifyItemInserted(0);
                                }
                                Log.e("123", " resume resumeAddCheck : " + resumeAddChatCheck);
                                if (resumeAddChatCheck != chatList.size()) {
                                    Log.e("123", "resume 했을 시 안내 메시지");
                                    tradeChatAnnounce.setVisibility(View.VISIBLE);
                                    resumeAddChatCheck = chatList.size();
                                }
                                adapter.notifyDataSetChanged();
//                        recyclerView.scrollToPosition(chatList.size() - 1);
                                setStackFromEnd();
                            }
                        }

                        @Override
                        public void onFailure(Call<DataChatAll> call, Throwable t) {

                        }
                    });
                }
            }
        };

        //announce 메시지 컨트롤
        tradeChatAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(chatList.size() - 1);
                tradeChatAnnounce.setVisibility(View.GONE);
            }
        });

        //adapter에 clickListener 달기
        adapter.setImageClickListener(new Adapter_trade_chat.Interface_imageClick() {
            @Override
            public void getImage(int position) {
                Intent intent = new Intent(Activity_trade_chat.this, Activity_image_download.class);
                intent.putExtra("imageRoute", chatList.get(position).getChat());
                intent.putExtra("imageSender", chatList.get(position).getNickname());
                startActivity(intent);
            }
        });
        //adapter 이미지 재전송 interface 달기
        adapter.setResendImageClickListener(new Adapter_trade_chat.Interface_imageResendClick() {
            @Override
            public void reSendImage(int position) {
                ArrayList<Uri> uriList = new ArrayList<>();

                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                for (int i = 0; i < noSendDataArrayList.size(); i++) {

                    if (chatList.get(position).getIdentifyNum() == noSendDataArrayList.get(i).getIdentifyNum()) {
                        String imageUri = chatList.get(position).getChat();
                        chatList.remove(position);
                        adapter.notifyItemRemoved(position);
                        noSendDataArrayList.remove(i);
                        setNoSendDataArrayList(noSendDataArrayList, roomNum);
                        uriList.add(Uri.parse(imageUri));
                        sendImage(uriList);
                        break;
                    }

                }
            }

            @Override
            public void deleteImage(int position) {

                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);

                for (int i = 0; i < noSendDataArrayList.size(); i++) {
                    if (chatList.get(position).getIdentifyNum() == noSendDataArrayList.get(i).getIdentifyNum()) {
                        chatList.remove(position);
                        adapter.notifyItemRemoved(position);
                        noSendDataArrayList.remove(i);
                        resumeAddChatCheck = chatList.size();
                        break;
                    }
                }
                setNoSendDataArrayList(noSendDataArrayList, roomNum);

            }
        });

        //adapter에 재전송 interface 달기
        adapter.setResendClickListener(new Adapter_trade_chat.Interface_resendClick() {
            @Override
            public void reSendText(int position) {
                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);

                for (int i = 0; i < noSendDataArrayList.size(); i++) {
                    if (chatList.get(position).getIdentifyNum() == noSendDataArrayList.get(i).getIdentifyNum()) {

                        String message = chatList.get(position).getChat();
                        chatList.remove(position);
                        adapter.notifyItemRemoved(position);
                        noSendDataArrayList.remove(i);
                        setNoSendDataArrayList(noSendDataArrayList, roomNum);
                        sendMessage(message);
                        break;
                    }
                }

            }

            @Override
            public void deleteText(int position) {
                ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);

                for (int i = 0; i < noSendDataArrayList.size(); i++) {
                    if (chatList.get(position).getIdentifyNum() == noSendDataArrayList.get(i).getIdentifyNum()) {
                        chatList.remove(position);
                        adapter.notifyItemRemoved(position);
                        noSendDataArrayList.remove(i);
                        resumeAddChatCheck = chatList.size();
                        break;
                    }
                }
                setNoSendDataArrayList(noSendDataArrayList, roomNum);
            }
        });

        //이미지 전송 버튼 누를 경우.
        tradeChatImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Activity_main_home.permissionCheck) {
                    //Toast.makeText(Activity_trade_chat.this, "권한승인", Toast.LENGTH_SHORT).show();
                    openImagesPicker();
                } else {
                    requestPermission();
                }

            }
        });

        //텍스트 전송 버튼 누를 경우.
        tradeChatSendTextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(tradeChatSendText.getText().toString());
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void sendMessage(String message) {

        //통신연결 안되있을 경우
        if (NetworkStatus.getConnectivityStatus(Activity_trade_chat.this) == 3) {

            if (message != null) {
                if (!message.equals("")) {
                    Log.e("123", "message : " + message);
                    ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                    DataChat datachat = new DataChat();
                    if (noSendDataArrayList.size() == 0) {
                        datachat = new DataChat(message, 1, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "text", 0, roomNum);
//                        Log.e("123","지나감1 size=0");
                    } else {
                        int identifyNum = noSendDataArrayList.get(noSendDataArrayList.size() - 1).getIdentifyNum();
                        datachat = new DataChat(message, 1, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "text", identifyNum + 1, roomNum);
//                        Log.e("123","지나감1 size!=0");
                    }
                    chatList.add(datachat);
                    setStackFromEnd();
                    adapter.notifyItemInserted(chatList.size() - 1);
                    recyclerView.scrollToPosition(chatList.size() - 1);
                    resumeAddChatCheck = chatList.size();
                    tradeChatSendText.setText("");

                    noSendDataArrayList.add(datachat);
                    Log.e("123", "dataChat : " + datachat.getChat());
                    Log.e("123", "noSendDataArrayList : " + noSendDataArrayList.get(noSendDataArrayList.size() - 1).getChat());
                    setNoSendDataArrayList(noSendDataArrayList, roomNum);
                    Log.e("123", "지나감2");
                    return;
                }
            }

        }
        //통신연결 되있을 경우 예외처리
        if (message != null) {
            if (!message.equals("")) {

                chatList.add(new DataChat(message, 1, getMessageTime(), nickName, Integer.toString(peopleNum)));
                setStackFromEnd();
                recyclerView.scrollToPosition(chatList.size() - 1);
                adapter.notifyItemInserted(chatList.size() - 1);
                resumeAddChatCheck = chatList.size();

                Intent intent = new Intent("chatDataToServer");
                intent.putExtra("purpose", "send");
                intent.putExtra("message", message);
                LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);
                tradeChatSendText.setText("");
            }
        }

    }


    public void setNoSendDataArrayList(ArrayList<DataChat> noSendData, String roomNum) {


        SharedPreferences sharedPreferences = getSharedPreferences("noSendData", MODE_PRIVATE);
        Gson gson = new GsonBuilder().create();
        Type arraylistType = new TypeToken<ArrayList<DataChat>>() {       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
        }.getType();

        String objectToString = gson.toJson(noSendData, arraylistType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(roomNum, objectToString);
        editor.apply();
    }

    public ArrayList<DataChat> getNoSendDataArrayList(String roomNum) {

        SharedPreferences sharedPreferences = getSharedPreferences("noSendData", MODE_PRIVATE);
        //gson 을 활용하여서 shared에 저장된 string을 object로 변환
        Gson gson = new GsonBuilder().create();

        ArrayList<DataChat> noSendArrayList;
        String stringToObject = sharedPreferences.getString(roomNum, "");
        Type arraylistType = new TypeToken<ArrayList<DataChat>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();
        try {
            noSendArrayList = gson.fromJson(stringToObject, arraylistType);
            if (noSendArrayList == null) {
                noSendArrayList = new ArrayList<DataChat>();
            }
            return noSendArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return noSendArrayList = new ArrayList<DataChat>();
        }

    }

    public void setStackFromEnd() {

        if (chatList != null) {
            if (chatList.size() > 5) {
                linearLayoutManager.setStackFromEnd(true);
            }
        }
    }

    private void videoRequestPermission() {

        TedPermission.with(Activity_trade_chat.this)
                .setPermissionListener(videoCallPermissionListener)
                .setRationaleMessage("영상통화를 하기 위해서는 권한 설정이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .check();

    }

    private void requestPermission() {

        TedPermission.with(Activity_trade_chat.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        //Manifest.permission.MODIFY_AUDIO_SETTINGS

    }

    private int findLastVisiblePosition() {
        return linearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    public void sendImage(ArrayList<Uri> uriList) {

        //통신이 안되 있을 경우
        if (NetworkStatus.getConnectivityStatus(Activity_trade_chat.this) == 3) {

            ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
            for (int i = 0; i < uriList.size(); i++) {

                DataChat datachat = new DataChat();

                Log.e("123", "uri.tostirng : " + uriList.get(i).toString());

                if (noSendDataArrayList.size() == 0) {
                    datachat = new DataChat(uriList.get(i).toString(), 3, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "image", 0, roomNum);
                } else {
                    int identifyNum = noSendDataArrayList.get(noSendDataArrayList.size() - 1).getIdentifyNum();
                    datachat = new DataChat(uriList.get(i).toString(), 3, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "image", identifyNum + 1, roomNum);
                }

                chatList.add(datachat);
                setStackFromEnd();
                adapter.notifyItemInserted(chatList.size() - 1);
                recyclerView.scrollToPosition(chatList.size() - 1);
                noSendDataArrayList.add(datachat);
            }
            resumeAddChatCheck = chatList.size();
            setNoSendDataArrayList(noSendDataArrayList, roomNum);
            return;
        }

        //선택한 이미지 절대경로로, 파일 만들어서 서버에 올리기
        imageFileCollect.clear();
        files.clear();
        requestMap.clear();

        //선택한 이미지 recyclerview에 처리
        for (int i = 0; i < uriList.size(); i++) {
            Log.e("123", "uriList.get(i).toString() : " + uriList.get(i).toString());
            chatList.add(new DataChat(uriList.get(i).toString(), 3, getMessageTime(), nickName, Integer.toString(peopleNum)));
            recyclerView.scrollToPosition(chatList.size() - 1);
            adapter.notifyItemInserted(chatList.size() - 1);
        }
        resumeAddChatCheck = chatList.size();
        //쓰레드로 file compress 찍어서, 서버에 올리기기

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < uriList.size(); i++) {
                    //uri를 통해 이미지 client에 표시하기

                    File imageFile = new File(createCopyAndReturnRealPath(uriList.get(i), "image" + i));
                    imageFileCollect.add(imageFile);
                    Log.e("123", uriList.get(i).getPath());
                    //uri를 통해 서버 업로드 하기 위해서 file 생성 및 multipart에 삽입
                    //File uriFile = new File(uriList.get(i).getPath());
                    //Log.e("123", uriFile.getPath());
                    //imageFileCollect.add(uriFile);
                    //RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), uriFile);
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part filepart = MultipartBody.Part.createFormData("image" + i, "image", fileBody);
                    files.add(filepart);
                }
                RequestBody roomNumBody, nickNameBody;
                roomNumBody = RequestBody.create(MediaType.parse("text/plain"), roomNum);
                nickNameBody = RequestBody.create(MediaType.parse("text/plain"), nickName);

                requestMap.put("roomNum", roomNumBody);
                requestMap.put("nickname", nickNameBody);

                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataChatImageRoute> call = service.chatImageFiles(files, requestMap);
                call.enqueue(new Callback<DataChatImageRoute>() {
                    @Override
                    public void onResponse(Call<DataChatImageRoute> call, Response<DataChatImageRoute> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("123", "위치확인0");
                            imageRoute = new ArrayList<>();
                            imageRoute = response.body().getImageRoute();
                            sendThread.start();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataChatImageRoute> call, Throwable t) {

                    }
                });
            }
        });
        thread.start();
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < imageRoute.size(); i++) {
                    //경로를 이제 service_example broadcast에 보내야함
                    Log.e("123", "imageRoute :" + imageRoute.get(i));
                    Log.e("123", "위치확인1");
                    Intent intent = new Intent("chatDataToServer");
                    intent.putExtra("purpose", "sendImage");
                    intent.putExtra("message", imageRoute.get(i));
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);
                    Log.e("123", "위치확인2");
                    if (imageFileCollect.get(i).exists()) {
                        imageFileCollect.get(i).delete();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {

                    }
                }
            }
        });


    }

    private void openImagesPicker() {
        final InputStream[] in = {null};

        TedBottomPicker.with(Activity_trade_chat.this)
                .setPeekHeight(1600)
                .showCameraTile(true) //카메라 보이기
                .setPreviewMaxCount(1000)
                .setSelectMaxCount(20)
                .setSelectMaxCountErrorText("20장 이하로 선택해주세요.")
                .showTitle(true)
                .showGalleryTile(true)
                .setCompleteButtonText("선택")
                .setEmptySelectionText("No Select")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {

                        ArrayList<Uri> arrayList = new ArrayList<>();
                        arrayList.addAll(uriList);
                        sendImage(arrayList);
//                        //통신이 안되 있을 경우
//                        if(NetworkStatus.getConnectivityStatus(Activity_trade_chat.this)==3){
//
//                            ArrayList<DataChat> noSendDataArrayList =getNoSendDataArrayList(roomNum);
//                            for(int i=0;i<uriList.size();i++){
//
//                                DataChat datachat=new DataChat();
//
//                                if(noSendDataArrayList.size()==0){
//                                    datachat =new DataChat(uriList.get(i).toString(),3,getMessageTime(),nickName,Integer.toString(peopleNum),3,"image",0,roomNum);
//                                }
//                                else{
//                                    int identifyNum=noSendDataArrayList.get(noSendDataArrayList.size()-1).getIdentifyNum();
//                                    datachat =new DataChat(uriList.get(i).toString(),3,getMessageTime(),nickName,Integer.toString(peopleNum),3,"image",identifyNum+1,roomNum);
//                                }
//
//                                chatList.add(datachat);
//                                setStackFromEnd();
//                                adapter.notifyItemInserted(chatList.size() - 1);
//                                recyclerView.scrollToPosition(chatList.size() - 1);
//                                noSendDataArrayList.add(datachat);
//                            }
//                            resumeAddChatCheck = chatList.size();
//                            setNoSendDataArrayList(noSendDataArrayList,roomNum);
//                            return;
//                        }
//
//                       //선택한 이미지 절대경로로, 파일 만들어서 서버에 올리기
//                        imageFileCollect.clear();
//                        files.clear();
//                        requestMap.clear();
//
//                        //선택한 이미지 recyclerview에 처리
//                        for (int i = 0; i < uriList.size(); i++) {
//                            //이부분에서, 네트워크 상태 확인하면서, 3번 일 경우, shared 에 저장시키고
//                            chatList.add(new DataChat(uriList.get(i).toString(), 3, getMessageTime(), nickName, Integer.toString(peopleNum)));
//                            recyclerView.scrollToPosition(chatList.size() - 1);
//                            adapter.notifyItemInserted(chatList.size() - 1);
//                        }
//                        resumeAddChatCheck = chatList.size();
//                        //쓰레드로 file compress 찍어서, 서버에 올리기기
//
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                for (int i = 0; i < uriList.size(); i++) {
//                                    //uri를 통해 이미지 client에 표시하기
//
//                                    File imageFile = new File(createCopyAndReturnRealPath(uriList.get(i), "image" + i));
//                                    imageFileCollect.add(imageFile);
//                                    Log.e("123", uriList.get(i).getPath());
//                                    //uri를 통해 서버 업로드 하기 위해서 file 생성 및 multipart에 삽입
//                                    //File uriFile = new File(uriList.get(i).getPath());
//                                    //Log.e("123", uriFile.getPath());
//                                    //imageFileCollect.add(uriFile);
//                                    //RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), uriFile);
//                                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
//                                    MultipartBody.Part filepart = MultipartBody.Part.createFormData("image" + i, "image", fileBody);
//                                    files.add(filepart);
//                                }
//                                RequestBody roomNumBody, nickNameBody;
//                                roomNumBody = RequestBody.create(MediaType.parse("text/plain"), roomNum);
//                                nickNameBody = RequestBody.create(MediaType.parse("text/plain"), nickName);
//
//                                requestMap.put("roomNum", roomNumBody);
//                                requestMap.put("nickname", nickNameBody);
//
//                                RetrofitService service = retrofit.create(RetrofitService.class);
//                                Call<DataChatImageRoute> call = service.chatImageFiles(files, requestMap);
//                                call.enqueue(new Callback<DataChatImageRoute>() {
//                                    @Override
//                                    public void onResponse(Call<DataChatImageRoute> call, Response<DataChatImageRoute> response) {
//                                        if (response.isSuccessful() && response.body() != null) {
//                                            Log.e("123","위치확인0");
//                                            imageRoute=new ArrayList<>();
//                                            imageRoute = response.body().getImageRoute();
//                                            sendThread.start();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<DataChatImageRoute> call, Throwable t) {
//
//                                    }
//                                });
//                            }
//                        });
//                        thread.start();
//                        sendThread=new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                for (int i = 0; i < imageRoute.size(); i++) {
//                                    //경로를 이제 service_example broadcast에 보내야함
//                                    Log.e("123", "imageRoute :"+imageRoute.get(i));
//                                    Log.e("123","위치확인1");
//                                    Intent intent = new Intent("chatDataToServer");
//                                    intent.putExtra("purpose", "sendImage");
//                                    intent.putExtra("message", imageRoute.get(i));
//                                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);
//                                    Log.e("123","위치확인2");
//                                    if (imageFileCollect.get(i).exists()) {
//                                        imageFileCollect.get(i).delete();
//                                    }
//                                    try{
//                                     Thread.sleep(500);
//                                    }catch (Exception e){
//
//                                    }
//                                }
//                            }
//                        });


                    }
                });

    }

    /* 이미지 파일을 복사한 후, 그 파일의 절대 경로 반환하는 메소드 */
    public String createCopyAndReturnRealPath(Uri uri, String fileName) {
        Bitmap bitmap;
        final ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null)
            return null;
        // 내부 저장소 안에 위치하도록 파일 생성
        String filePath = getApplicationInfo().dataDir + File.separator + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(filePath);
        Log.e("123", filePath);
        // 이 코드에 대해서 공부가 필요할 것 파일안에 데이터 bytearray로 넣는과정인데. 사실 잘 이해가 안된다
        // 따로 한번 정리해서 stickcode에 올리는 절차를 갖자.
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = null;
                try {
                    //파일 생성된 곳에 작성할 수 있도록 outputstream 생성
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    //outputstream을 통해 bitearray[] 로 데이터 저장
                    fos.write(bitmapdata);

                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath(); // 생성한 파일의 절대경로 반환
    }


    public void variableInit() {

        //
        tradeChatVideoCamera = findViewById(R.id.trade_chat_video_camera);
        //
        imageFileCollect = new ArrayList<>();
        files = new ArrayList<>();
        requestMap = new HashMap<>();

        cursorChatNum = "0";
        phasingNum = "10";

        //방에 참가한 회원닉네임 모음
        roomMemberNickname = new ArrayList<>();
        //recyclerview 관련
        recyclerView = findViewById(R.id.trade_chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(Activity_trade_chat.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatList = new ArrayList<>();
        adapter = new Adapter_trade_chat(chatList, Activity_trade_chat.this);
        recyclerView.setAdapter(adapter);

        //retrofit
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-199-7.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //xml 기본 연결
        tradeChatOtherUserNickname = (TextView) findViewById(R.id.trade_chat_other_nickname);
        tradeChatProductPrice = (TextView) findViewById(R.id.trade_chat_product_price);
        tradeChatProductTitle = (TextView) findViewById(R.id.trade_chat_product_name);
        tradeChatImage = (ImageView) findViewById(R.id.trade_chat_product_image);
        tradeChatLocation = (TextView) findViewById(R.id.trade_chat_location);
        tradeChatSellType = (TextView) findViewById(R.id.trade_chat_sell_type);

        tradeChatSendText = (EditText) findViewById(R.id.trade_chat_send_text);
        tradeChatImageSend = (ImageView) findViewById(R.id.trade_chat_send_image);
        tradeChatSendTextImage = (ImageView) findViewById(R.id.trade_chat_send_text_image);

        backImage = (ImageView) findViewById(R.id.trade_chat_back_arrow);
        tradeChatLocationImage = (ImageView) findViewById(R.id.trade_chat_loactin_image);

        tradeChatAnnounce = (LinearLayout) findViewById(R.id.trade_chat_announce_layout);

        //shared로 내 닉네임 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (roomNumCheck) {

            // 채팅방 정보 입력한 후에, 데이터를 받을 준비를 완료 시킨다.
            LocalBroadcastManager.getInstance(Activity_trade_chat.this).registerReceiver(dataReceiver, new IntentFilter("chatData"));
            //  데이터 보낼 떄 쓸 것들
            //  채팅방 입장!!
            Intent intent = new Intent("chatDataToServer");
            intent.putExtra("purpose", "changeRoomNum");
            intent.putExtra("roomNum", roomNum);
            intent.putExtra("otherUserNickname", otherUserNickname);
            intent.putExtra("message", roomNum + ":" + otherUserNickname);
            LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

            //retrofit을 통해 데이터 다시 받아와야지 cursor 사용해서.. ㅅㅂ
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, "update", cursorChatNum, nickName);
            chatDataCall.enqueue(new Callback<DataChatAll>() {
                @Override
                public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        chatList.clear();
                        DataChatAll dataChatAllList = response.body();
                        ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                        //shared에 저장된 안보내진 데이터 입력 나타나게 하기;
                        ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                        if (noSendDataArrayList.size() != 0) {
                            for (int i = 0; i < noSendDataArrayList.size(); i++) {
                                if (noSendDataArrayList.get(i).getChatRoomNum() != null) {
                                    if (noSendDataArrayList.get(i).getChatRoomNum().equals(roomNum)) {
                                        chatArrayList.add(0, noSendDataArrayList.get(i));
                                    }
                                }

                            }
                        }

                        for (int i = 0; i < chatArrayList.size(); i++) {
                            int networkStatus = chatArrayList.get(i).getNetworkStatus();
                            String chatType = chatArrayList.get(i).getChatType();
                            String writerNickname = chatArrayList.get(i).getNickname();
                            String chatText = chatArrayList.get(i).getChat();
                            String isReadChat = chatArrayList.get(i).getIsReadChat();
                            chatText = chatText.replace(Service_Example.CHANGE_LINE_CHAR, "\n");
                            String chatTime = chatArrayList.get(i).getChatTime();
                            Date chatDate;
                            SimpleDateFormat chatTimedateFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat chatTimeDbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                chatDate = chatTimeDbDateFormat.parse(chatTime);
                                chatTime = chatTimedateFormat.format(chatDate);
                            } catch (Exception e) {

                            }
                            //나 자신일 경우
                            if (writerNickname.equals(nickName)) {

                                if (chatType != null) {
                                    if (chatType.equals("text")) {

                                        if (networkStatus == 3) {
                                            chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat, 3, "text", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                        } else {
                                            chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                        }

                                    } else if (chatType.equals("image")) {

                                        if (networkStatus == 3) {
                                            chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat, 3, "image", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                        } else {
                                            chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                        }

                                    }
                                }
                            }
                            //서버일 경우
                            else if (writerNickname.equals("server")) {
                                chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                            }
                            //상대방일 경우
                            else {
                                if (chatType != null) {

                                    if (chatType.equals("text")) {
                                        chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                    } else if (chatType.equals("image")) {
                                        chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                    }

                                }
                            }
                            // adapter.notifyItemInserted(0);
                        }
                        Log.e("123", " resume resumeAddCheck : " + resumeAddChatCheck);
                        if (resumeAddChatCheck != chatList.size()) {
                            Log.e("123", "resume 했을 시 안내 메시지");
                            tradeChatAnnounce.setVisibility(View.VISIBLE);
                            resumeAddChatCheck = chatList.size();
                        }
                        adapter.notifyDataSetChanged();
//                        recyclerView.scrollToPosition(chatList.size() - 1);
                        setStackFromEnd();
                    }
                }

                @Override
                public void onFailure(Call<DataChatAll> call, Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("123", "onPause()");
        roomNumCheck = true;

        Log.e("123", " pause resumeAddCheck : " + resumeAddChatCheck);
        Intent intent = new Intent("chatDataToServer");

        intent.putExtra("purpose", "quit");
        intent.putExtra("message", "");
        LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("123", "onDestroy()");
        activity_trade_chat = null;
        roomNumGlobal = null;
        otherUserNicknameGlobal = null;
        //채팅방 화면 나갈 때, 채팅방 나간 것을 자바 채팅서버에 데이터 전송해서 알려야함.

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String getMessageTime() {

        String formatedNow;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            formatedNow = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            //달->일 -> 시간 -> 분 -> 초 로 차이나는지 확인해서
            formatedNow = formatter.format(date);
        }
        return formatedNow;
    }
}