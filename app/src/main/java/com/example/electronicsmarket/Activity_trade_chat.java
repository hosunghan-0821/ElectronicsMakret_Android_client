package com.example.electronicsmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

            //????????? ????????????
            String purpose = intent.getStringExtra("purpose");
            if (purpose != null) {
                if (purpose.equals("????????????")) {

                    int chatRoomPeople = intent.getIntExtra("peopleNum", -1);
                    Log.e("123", "??? ?????????" + chatRoomPeople);
                    ArrayList<String> roomUsers = intent.getStringArrayListExtra("roomUsers");

                    for (int i = 0; i < roomUsers.size(); i++) {
                        roomMemberNickname.add(roomUsers.get(i));
                    }
                    peopleNum = chatRoomPeople;

                } else if (purpose.equals("????????????")) {
                    String userNickname = intent.getStringExtra("nickname");
                    boolean nameDuplicateCheck = false;
                    for (int i = 0; i < roomMemberNickname.size(); i++) {

                        if (userNickname.equals(roomMemberNickname.get(i))) {
                            Log.e("123", "????????? ????????? : " + userNickname);
                            nameDuplicateCheck = true;
                            break;
                        }
                    }
                    if (!nameDuplicateCheck) {
                        Log.e("123", "??? ???????????? ???????????? ????????? " + userNickname);
                        roomMemberNickname.add(userNickname);
                        //????????? reload ?????????
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("purpose", "reloadData");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        peopleNum++;
                    }

                } else if (purpose.equals("????????????")) {
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
                Log.e("123", "?????? ????????? ????????? ????????? : " + peopleNum);
                return;
            }
            //????????????????????? ?????? ????????? ?????? ?????????.
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


    private ActivityResultLauncher<Intent> callResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("123", "oncreate()");
        setContentView(R.layout.activity_trade_chat);
        variableInit();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent intent = getIntent();

        //????????????
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Activity_main_home.permissionCheck = true;
                //Toast.makeText(Activity_trade_chat.this, "??????,????????? ????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Activity_trade_chat.this, "????????? ???????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
            }
        };

        //???????????? ?????? ?????? ??? ,???????????? ?????? ??????.
        videoCallPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_trade_chat.this);

                builder.setTitle("???????????? ??????");
                builder.setMessage("\" " + otherUserNickname + " \"????????? ??????????????? ???????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //???????????? ?????? ?????? ???????????? add ??????,

                        chatList.add(new DataChat("????????????", 5, getMessageTime(), nickName, Integer.toString(peopleNum)));
                        setStackFromEnd();
                        recyclerView.scrollToPosition(chatList.size() - 1);
                        adapter.notifyItemInserted(chatList.size() - 1);
                        resumeAddChatCheck = chatList.size();

//                        //????????? ????????? ???????????????.
//                        Intent intent = new Intent("chatDataToServer");
//                        intent.putExtra("purpose", "send");
//                        intent.putExtra("message", "????????????");
//                        intent.putExtra("type","call");
//                        LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

                        //????????? ?????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ????????? ????????? ?????????.

                        Intent callIntent = new Intent(Activity_trade_chat.this, Activity_video_call.class);
                        callIntent.putExtra("sendToNickname", otherUserNickname);
                        callIntent.putExtra("roomNum", roomNum);
                        callIntent.putExtra("position", "caller");
                        //callResultLauncher.launch(callIntent);

                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Activity_trade_chat.this, "???????????? ??????????????? ?????? ????????? ?????? ", Toast.LENGTH_SHORT).show();
                return;
            }
        };

        //???????????? ?????? ?????????
        tradeChatVideoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoRequestPermission();
            }
        });


        //??? ??????????????? ???????????? ????????? ???????????? ????????????, ???????????? ??????
        //1???. ????????????????????? ?????? ????????? ????????????
        //2???. ?????? ???????????? ?????????????????? ?????? ????????????
        if (intent.getStringExtra("roomNum") == null) {
            postNum = intent.getStringExtra("postNum");
            seller = intent.getStringExtra("seller");
            buyer = intent.getStringExtra("buyer");
        } else {
            roomNum = intent.getStringExtra("roomNum");
            Log.e("123", "roomNum : " + roomNum);
        }
        activity_trade_chat = Activity_trade_chat.this;


        Log.e("123", "Service_Example instance " + Service_Example.tcpService);
        if (Service_Example.tcpService == null) {
            Log.e("123", "pendingIntent??? ???????????? ?????? service ?????????");
            Intent serviceIntent = new Intent(getApplicationContext(), Service_Example.class);
            startService(serviceIntent);
        }

        //        Log.e("123","postNum : "+postNum);
        //        Log.e("123","seller : "+ seller);
        //        Log.e("123","buyer : "+ buyer);

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoom> call = service.roomNumCheck(postNum, seller, buyer, roomNum, nickName);

        //????????? ???????????? ???, ???????????? ???????????? ????????????
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
                    tradeChatProductPrice.setText(dataChatRoom.getPostPrice() + "???");
                    tradeChatSellType.setText("(" + dataChatRoom.getPostSellType() + ")");
                    //?????? ?????? ?????? ???????????? ????????? ???????????? ??????.
                    if (dataChatRoom.getPostLocationName() != null) {

                        if (dataChatRoom.getPostLocationName().equals("???????????? ??????")) {

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
                            tradeChatLocation.setText("???????????? : " + postLocationName + "\n???????????? : " + postLocationDetail);
                        }
                    }

                    Glide.with(Activity_trade_chat.this).load(dataChatRoom.getImageRoute()).into(tradeChatImage);

                    // ????????? ?????? ???????????? ???????????? ???????????????.

                    //  ????????? ?????? ??? ??? ??????
                    //  ????????? ??????!!
                    Intent intent = new Intent("chatDataToServer");
                    intent.putExtra("purpose", "changeRoomNum");
                    intent.putExtra("roomNum", roomNum);
                    intent.putExtra("otherUserNickname", otherUserNickname);
                    intent.putExtra("message", roomNum + ":" + otherUserNickname);
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

                    // ????????? ?????? ????????? ??????, ???????????? ?????? ????????? ?????? ?????????.
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).registerReceiver(dataReceiver, new IntentFilter("chatData"));
                    //?????? ??????. ?????? ????????? ???, onresume/onpause ????????????, ????????? ????????? ???????????? ?????? ??????.. socket????????? ???,
                    //????????? ???????????? ???????????? ????????? ?????????.
//                    requestPermission();
                    //?????? ???????????? ??? ???????????? ?????? ????????? ??????????????? ??????????????? ????????????
                    Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, phasingNum, cursorChatNum, nickName);
                    chatDataCall.enqueue(new Callback<DataChatAll>() {
                        @Override
                        public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                DataChatAll dataChatAllList = response.body();
                                ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                                //shared??? ????????? ???????????? ????????? ?????? ???????????? ??????;
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
                                    //chatList??? ???????????? ?????? ??? ????????? ??????

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
                                    //??? ????????? ??????
                                    if (writerNickname.equals(nickName)) {

                                        if (chatType != null) {
                                            //?????? ????????? ??????
                                            if (chatType.equals("text")) {

                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat, 3, "text", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                                }

                                            }
                                            //????????? ??????
                                            else if (chatType.equals("image")) {

                                                if (networkStatus == 3) {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat, 3, "image", chatArrayList.get(i).getIdentifyNum(), chatArrayList.get(i).getChatRoomNum()));
                                                } else {
                                                    chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                                }
                                            }
                                            //???????????? ??????
                                            else if (chatType.equals("call")) {

                                                if (chatText.equals("????????????1")) {
                                                    chatText = "????????????";
                                                } else if (chatText.equals("????????????2")||chatText.equals("????????????3")) {
                                                    chatText = "????????????";
                                                }
                                                chatList.add(0, new DataChat(chatText, 5, chatTime, writerNickname, isReadChat));
                                            }
                                        }

                                    }
                                    //????????? ??????
                                    else if (writerNickname.equals("server")) {
                                        chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                    }
                                    //???????????? ??????
                                    else {

                                        if (chatType != null) {

                                            if (chatType.equals("text")) {
                                                chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("image")) {
                                                chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("call")) {

                                                if (chatText.equals("????????????1")||chatText.equals("????????????3")||chatText.equals("????????? ?????????")) {
                                                    chatText = "?????????";
                                                } else if (chatText.equals("????????????2")) {
                                                    chatText = "????????????";
                                                }

                                                chatList.add(0, new DataChat(6, chatText, chatTime, writerNickname, otherUserImageRoute));
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
                                            //??? ????????? ??????
                                            if (writerNickname.equals(nickName)) {

                                                if (chatType != null) {
                                                    if (chatType.equals("text")) {
                                                        chatList.add(0, new DataChat(chatText, 1, chatTime, writerNickname, isReadChat));
                                                    } else if (chatType.equals("image")) {
                                                        chatList.add(0, new DataChat(chatText, 3, chatTime, writerNickname, isReadChat));
                                                    }   //???????????? ??????
                                                    else if (chatType.equals("call")) {

                                                        if (chatText.equals("????????????1")) {
                                                            chatText = "????????????";
                                                        } else if (chatText.equals("????????????2")||chatText.equals("????????????3")) {
                                                            chatText = "????????????";
                                                        }

                                                        chatList.add(0, new DataChat(chatText, 5, chatTime, writerNickname, isReadChat));
                                                    }
                                                }

                                            }
                                            //????????? ??????
                                            else if (writerNickname.equals("server")) {
                                                chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                            }
                                            //???????????? ??????
                                            else {
                                                if (chatType != null) {

                                                    if (chatType.equals("text")) {
                                                        chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                                    } else if (chatType.equals("image")) {
                                                        chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                                    } else if (chatType.equals("call")) {
                                                        if (chatText.equals("????????????1")||chatText.equals("????????????3")||chatText.equals("????????? ?????????")) {
                                                            chatText = "?????????";
                                                        } else if (chatText.equals("????????????2")) {
                                                            chatText = "????????????";
                                                        }

                                                        chatList.add(0, new DataChat(6, chatText, chatTime, writerNickname, otherUserImageRoute));
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
            Toast.makeText(Activity_trade_chat.this, "????????? ????????? ???????????? ????????? ??????;", Toast.LENGTH_SHORT).show();
        }


        //????????? ?????? ????????? ???, ????????? ???????????? ????????? , ???????????? ????????????
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String purpose = bundle.getString("purpose");

                //?????? ????????? ????????? ??????
                if (purpose.equals("getData")) {
                    String type = bundle.getString("type");
                    String message = bundle.getString("message");
                    String writerNickname = bundle.getString("writer");
                    try {
                        String formatedNow = getMessageTime();

                        //???????????? ??? ????????? ??????.. ?????? ?????? ???????????? ?????? ????????????.. ?????? x
                        if (writerNickname.equals(nickName)) {
                            chatList.add(new DataChat(message, 1, formatedNow, nickName, "1"));
                        }
                        //??????????????? ??? ????????? ??????,
                        else if (writerNickname.equals("server")) {
                            chatList.add(chatList.size() - 1, new DataChat(message, 2, formatedNow, "server", "1"));
                            adapter.notifyItemInserted(chatList.size() - 1);
                            recyclerView.scrollToPosition(chatList.size() - 1);
                            resumeAddChatCheck = chatList.size();
                        }
                        //????????????????????? ??? ??????
                        else {
                            if (type.equals("text")) {
                                chatList.add(new DataChat(0, message, formatedNow, writerNickname, otherUserImageRoute));
                                resumeAddChatCheck = chatList.size();

                            } else if (type.equals("image")) {
                                chatList.add(new DataChat(4, message, formatedNow, writerNickname, otherUserImageRoute));
                                resumeAddChatCheck = chatList.size();
                            } else if (type.equals("call")) {
                                chatList.add(new DataChat(6, message, formatedNow, writerNickname, otherUserImageRoute));
                                resumeAddChatCheck = chatList.size();
                            }

                            //scroll ?????????
                            Log.e("123", "lastVisiblePosition : " + findLastVisiblePosition());
                            Log.e("123", "chatList.size() : " + chatList.size());
                            if (chatList.size() - findLastVisiblePosition() <= 2) {

                                tradeChatAnnounce.setVisibility(View.GONE);
                                adapter.notifyItemInserted(chatList.size() - 1);
                                recyclerView.scrollToPosition(chatList.size() - 1);
                            } else {
                                Log.e("123", "????????? ??? ??? ?????? ?????????");
                                tradeChatAnnounce.setVisibility(View.VISIBLE);
                                adapter.notifyItemInserted(chatList.size() - 1);
                            }


                        }

                        setStackFromEnd();
//                  adapter.notifyItemInserted(chatList.size()-1);

                        Log.e("123", "recyclerview ??????" + recyclerView.getBottom());
                        Log.e("123", "recyclerview .getheight" + recyclerView.getLayoutManager().getHeight());
                        System.out.println("?????? ??????????");
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
                    //?????? reload?????? ????????? onResume ?????? ???????????? ?????? ???????????? ????????? ????????????

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, "update", cursorChatNum, nickName);
                    chatDataCall.enqueue(new retrofit2.Callback<DataChatAll>() {
                        @Override
                        public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                chatList.clear();
                                DataChatAll dataChatAllList = response.body();
                                ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                                //shared??? ????????? ???????????? ????????? ?????? ???????????? ??????;
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
                                    //??? ????????? ??????
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
                                            } else if (chatType.equals("call")) {

                                                if (chatText.equals("????????????1")) {
                                                    chatText = "????????????";
                                                } else if (chatText.equals("????????????2")||chatText.equals("????????????3")) {
                                                    chatText = "????????????";
                                                }

                                                chatList.add(0, new DataChat(chatText, 5, chatTime, writerNickname, isReadChat));
                                            }

                                        }

                                    }
                                    //????????? ??????
                                    else if (writerNickname.equals("server")) {
                                        chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                                    }
                                    //???????????? ??????
                                    else {
                                        if (chatType != null) {

                                            if (chatType.equals("text")) {
                                                chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("image")) {
                                                chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            } else if (chatType.equals("call")) {

                                                if (chatText.equals("????????????1") ||chatText.equals("????????????3")||chatText.equals("????????? ?????????")) {
                                                    chatText = "?????????";
                                                } else if (chatText.equals("????????????2")) {
                                                    chatText = "????????????";
                                                }

                                                chatList.add(0, new DataChat(6, chatText, chatTime, writerNickname, otherUserImageRoute));
                                            }

                                        }
                                    }
                                    // adapter.notifyItemInserted(0);
                                }
                                Log.e("123", " resume resumeAddCheck : " + resumeAddChatCheck);
                                if (resumeAddChatCheck != chatList.size()) {
                                    Log.e("123", "resume ?????? ??? ?????? ?????????");
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

        //announce ????????? ?????????
        tradeChatAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(chatList.size() - 1);
                tradeChatAnnounce.setVisibility(View.GONE);
            }
        });

        //adapter??? clickListener ??????
        adapter.setImageClickListener(new Adapter_trade_chat.Interface_imageClick() {
            @Override
            public void getImage(int position) {
                Intent intent = new Intent(Activity_trade_chat.this, Activity_image_download.class);
                intent.putExtra("imageRoute", chatList.get(position).getChat());
                intent.putExtra("imageSender", chatList.get(position).getNickname());
                startActivity(intent);
            }
        });
        //adapter ????????? ????????? interface ??????
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

        //adapter??? ????????? interface ??????
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

        //????????? ?????? ?????? ?????? ??????.
        tradeChatImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Activity_main_home.permissionCheck) {
                    //Toast.makeText(Activity_trade_chat.this, "????????????", Toast.LENGTH_SHORT).show();
                    openImagesPicker();
                } else {
                    requestPermission();
                }

            }
        });

        //????????? ?????? ?????? ?????? ??????.
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

        //???????????? ???????????? ??????
        if (NetworkStatus.getConnectivityStatus(Activity_trade_chat.this) == 3) {

            if (message != null) {
                if (!message.equals("")) {
                    Log.e("123", "message : " + message);
                    ArrayList<DataChat> noSendDataArrayList = getNoSendDataArrayList(roomNum);
                    DataChat datachat = new DataChat();
                    if (noSendDataArrayList.size() == 0) {
                        datachat = new DataChat(message, 1, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "text", 0, roomNum);
//                        Log.e("123","?????????1 size=0");
                    } else {
                        int identifyNum = noSendDataArrayList.get(noSendDataArrayList.size() - 1).getIdentifyNum();
                        datachat = new DataChat(message, 1, getMessageTime(), nickName, Integer.toString(peopleNum), 3, "text", identifyNum + 1, roomNum);
//                        Log.e("123","?????????1 size!=0");
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
                    Log.e("123", "?????????2");
                    return;
                }
            }

        }
        //???????????? ????????? ?????? ????????????
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
        Type arraylistType = new TypeToken<ArrayList<DataChat>>() {       // ?????? ????????? ????????? type??? ???????????? ?????? Type ??? TypeToken .getType() ???????????? ????????????.
        }.getType();

        String objectToString = gson.toJson(noSendData, arraylistType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(roomNum, objectToString);
        editor.apply();
    }

    public ArrayList<DataChat> getNoSendDataArrayList(String roomNum) {

        SharedPreferences sharedPreferences = getSharedPreferences("noSendData", MODE_PRIVATE);
        //gson ??? ??????????????? shared??? ????????? string??? object??? ??????
        Gson gson = new GsonBuilder().create();

        ArrayList<DataChat> noSendArrayList;
        String stringToObject = sharedPreferences.getString(roomNum, "");
        Type arraylistType = new TypeToken<ArrayList<DataChat>>() {                           //Type, TypeToken??? ??????????????? ???????????? ?????? ????????? ????????????.
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

    //????????? ??????????????? ???????????? ??????. ????????? ???, videoCallPermissionListner??? ?????? ?????? ?????? ??????
    private void videoRequestPermission() {

        TedPermission.with(Activity_trade_chat.this)
                .setPermissionListener(videoCallPermissionListener)
                .setRationaleMessage("??????????????? ?????? ???????????? ?????? ????????? ???????????????.")
                .setDeniedMessage("[??????] > [??????] ?????? ????????? ????????? ??? ????????????..")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .check();

    }

    private void requestPermission() {

        TedPermission.with(Activity_trade_chat.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("????????? ???????????? ???????????? ?????? ????????? ???????????????.")
                .setDeniedMessage("[??????] > [??????] ?????? ????????? ????????? ??? ????????????..")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        //Manifest.permission.MODIFY_AUDIO_SETTINGS

    }

    private int findLastVisiblePosition() {
        return linearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    public void sendImage(ArrayList<Uri> uriList) {

        //????????? ?????? ?????? ??????
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

        //????????? ????????? ???????????????, ?????? ???????????? ????????? ?????????
        imageFileCollect.clear();
        files.clear();
        requestMap.clear();

        //????????? ????????? recyclerview??? ??????
        for (int i = 0; i < uriList.size(); i++) {
            Log.e("123", "uriList.get(i).toString() : " + uriList.get(i).toString());
            chatList.add(new DataChat(uriList.get(i).toString(), 3, getMessageTime(), nickName, Integer.toString(peopleNum)));
            recyclerView.scrollToPosition(chatList.size() - 1);
            adapter.notifyItemInserted(chatList.size() - 1);
        }
        resumeAddChatCheck = chatList.size();
        //???????????? file compress ?????????, ????????? ????????????

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < uriList.size(); i++) {
                    //uri??? ?????? ????????? client??? ????????????

                    File imageFile = new File(createCopyAndReturnRealPath(uriList.get(i), "image" + i));
                    imageFileCollect.add(imageFile);
                    Log.e("123", uriList.get(i).getPath());
                    //uri??? ?????? ?????? ????????? ?????? ????????? file ?????? ??? multipart??? ??????
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
                            Log.e("123", "????????????0");
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
                    //????????? ?????? service_example broadcast??? ????????????
                    Log.e("123", "imageRoute :" + imageRoute.get(i));
                    Log.e("123", "????????????1");
                    Intent intent = new Intent("chatDataToServer");
                    intent.putExtra("purpose", "sendImage");
                    intent.putExtra("message", imageRoute.get(i));
                    LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);
                    Log.e("123", "????????????2");
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
                .showCameraTile(true) //????????? ?????????
                .setPreviewMaxCount(1000)
                .setSelectMaxCount(20)
                .setSelectMaxCountErrorText("20??? ????????? ??????????????????.")
                .showTitle(true)
                .showGalleryTile(true)
                .setCompleteButtonText("??????")
                .setEmptySelectionText("No Select")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {

                        ArrayList<Uri> arrayList = new ArrayList<>();
                        arrayList.addAll(uriList);
                        sendImage(arrayList);

                    }
                });

    }

    /* ????????? ????????? ????????? ???, ??? ????????? ?????? ?????? ???????????? ????????? */
    public String createCopyAndReturnRealPath(Uri uri, String fileName) {
        Bitmap bitmap;
        final ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null)
            return null;
        // ?????? ????????? ?????? ??????????????? ?????? ??????
        String filePath = getApplicationInfo().dataDir + File.separator + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(filePath);
        Log.e("123", filePath);
        // ??? ????????? ????????? ????????? ????????? ??? ???????????? ????????? bytearray??? ??????????????????. ?????? ??? ????????? ?????????
        // ?????? ?????? ???????????? stickcode??? ????????? ????????? ??????.
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = null;
                try {
                    //?????? ????????? ?????? ????????? ??? ????????? outputstream ??????
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    //outputstream??? ?????? bitearray[] ??? ????????? ??????
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

        return file.getAbsolutePath(); // ????????? ????????? ???????????? ??????
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

        //?????? ????????? ??????????????? ??????
        roomMemberNickname = new ArrayList<>();
        //recyclerview ??????
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

        //xml ?????? ??????
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

        //shared??? ??? ????????? ????????????
        SharedPreferences sharedPreferences = getSharedPreferences("autoLogin", MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (roomNumCheck) {

            // ????????? ?????? ????????? ??????, ???????????? ?????? ????????? ?????? ?????????.
            LocalBroadcastManager.getInstance(Activity_trade_chat.this).registerReceiver(dataReceiver, new IntentFilter("chatData"));
            //  ????????? ?????? ??? ??? ??????
            //  ????????? ??????!!

            Intent intent = new Intent("chatDataToServer");
            intent.putExtra("purpose", "changeRoomNum");
            intent.putExtra("roomNum", roomNum);
            intent.putExtra("otherUserNickname", otherUserNickname);
            intent.putExtra("message", roomNum + ":" + otherUserNickname);
            LocalBroadcastManager.getInstance(Activity_trade_chat.this).sendBroadcast(intent);

            //retrofit??? ?????? ????????? ?????? ??????????????? cursor ????????????.. ??????
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataChatAll> chatDataCall = service.getRoomChatInfo(roomNum, "update", cursorChatNum, nickName);
            chatDataCall.enqueue(new Callback<DataChatAll>() {
                @Override
                public void onResponse(Call<DataChatAll> call, Response<DataChatAll> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        chatList.clear();
                        DataChatAll dataChatAllList = response.body();
                        ArrayList<DataChat> chatArrayList = dataChatAllList.getDataChatAllList();

                        //shared??? ????????? ???????????? ????????? ?????? ???????????? ??????;
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
                            //??? ????????? ??????
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
                                    } else if (chatType.equals("call")) {

                                        if (chatText.equals("????????????1")) {
                                            chatText = "????????????";
                                        } else if (chatText.equals("????????????2")||chatText.equals("????????????3")) {
                                            chatText = "????????????";
                                        }
                                        chatList.add(0, new DataChat(chatText, 5, chatTime, writerNickname, isReadChat));
                                    }

                                }
                            }
                            //????????? ??????
                            else if (writerNickname.equals("server")) {
                                chatList.add(0, new DataChat(chatText, 2, chatTime, "server", isReadChat));
                            }
                            //???????????? ??????
                            else {
                                if (chatType != null) {

                                    if (chatType.equals("text")) {
                                        chatList.add(0, new DataChat(0, chatText, chatTime, writerNickname, otherUserImageRoute));
                                    } else if (chatType.equals("image")) {
                                        chatList.add(0, new DataChat(4, chatText, chatTime, writerNickname, otherUserImageRoute));
                                    } else if (chatType.equals("call")) {
                                        if (chatText.equals("????????????1") ||chatText.equals("????????????3")||chatText.equals("????????? ?????????")) {
                                            chatText = "?????????";
                                        } else if (chatText.equals("????????????2")) {
                                            chatText = "????????????";
                                        }

                                        chatList.add(0, new DataChat(6, chatText, chatTime, writerNickname, otherUserImageRoute));
                                    }

                                }
                            }
                            // adapter.notifyItemInserted(0);
                        }
                        Log.e("123", " resume resumeAddCheck : " + resumeAddChatCheck);
                        if (resumeAddChatCheck != chatList.size()) {

                            Log.e("123", "resume ?????? ??? ?????? ?????????");
                            tradeChatAnnounce.setVisibility(View.VISIBLE);
                            resumeAddChatCheck = chatList.size();

                        }
                        adapter.notifyDataSetChanged();
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
        roomMemberNickname.clear();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("123", "onDestroy()");
        activity_trade_chat = null;
        roomNumGlobal = null;
        otherUserNicknameGlobal = null;
        //????????? ?????? ?????? ???, ????????? ?????? ?????? ?????? ??????????????? ????????? ???????????? ????????????.

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
            //???->??? -> ?????? -> ??? -> ??? ??? ??????????????? ????????????
            formatedNow = formatter.format(date);
        }
        return formatedNow;
    }
}