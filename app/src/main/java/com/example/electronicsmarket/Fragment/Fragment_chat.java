package com.example.electronicsmarket.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.electronicsmarket.Activity.Activity_alarm_collect;
import com.example.electronicsmarket.Activity.Activity_trade_chat;
import com.example.electronicsmarket.Adapter.Adapter_chat_room;
import com.example.electronicsmarket.Dto.DataChatRoom;
import com.example.electronicsmarket.Dto.DataChatRoomAll;
import com.example.electronicsmarket.R;
import com.example.electronicsmarket.infra.Retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_chat extends Fragment {

    private RecyclerView chatRecyclerView;
    private ImageView chatNotificationImage;
    private Retrofit retrofit;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<DataChatRoom> roomList;
    private String nickName;
    private Adapter_chat_room adapter;
    private String cursorChatRoomNum, phasingNum;
    private boolean isFinalPhase = false, onCreateViewIsSet = false, scrollCheck = true;
    private Handler handler;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String purpose = intent.getStringExtra("purpose");
            Message msg = new Message();
            Bundle bundle = new Bundle();
            if(purpose!=null){
                if (purpose.equals("reloadRoomList")) {
                    String readValue = intent.getStringExtra("message");
                    //만약 알림이 왔을 떄, 데이터 reload 할 경우라 하나하나 데이터 가져올 때랑 비교좀 해보자.
                    bundle.putString("purpose","reloadRoomList");
                    bundle.putString("message", readValue);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else if (purpose.equals("reloadAlarmImage")) {
                    bundle.putString("purpose","reloadAlarmImage");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

            }

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        variableInit(view);
        chatNotificationImage.setVisibility(View.INVISIBLE);

        //서버로부터 데이터 가져오기..
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName, phasingNum, cursorChatRoomNum, "chatList");
        call.enqueue(new Callback<DataChatRoomAll>() {
            @Override
            public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {


                if (response.isSuccessful() && response.body() != null) {

                    DataChatRoomAll dataChatRoomALL = response.body();

                    for (int i = 0; i < dataChatRoomALL.getRoomList().size(); i++) {
                        try {
                            roomList.add(dataChatRoomALL.getRoomList().get(i));
                        } catch (Exception e) {

                        }

                    }
                    adapter.notifyDataSetChanged();
                    if (roomList.size() != 0) {
                        cursorChatRoomNum = roomList.get(roomList.size() - 1).getFinalChatTime();
                    }
                    if (!response.body().getRoomCount().equals(phasingNum)) {
                        isFinalPhase = true;
                    }
                    onCreateViewIsSet = true;
                    if (response.body().isNotification()) {
                        chatNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                        chatNotificationImage.setVisibility(View.VISIBLE);
                    } else {
                        chatNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_24);
                        chatNotificationImage.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataChatRoomAll> call, Throwable t) {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chatRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!v.canScrollVertically(1) && scrollCheck) {
                        scrollCheck = false;
                        if (!isFinalPhase) {
                            Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName, phasingNum, cursorChatRoomNum, "chatList");
                            call.enqueue(new Callback<DataChatRoomAll>() {
                                @Override
                                public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                                    if (response.isSuccessful() && response.body() != null) {

                                        DataChatRoomAll dataChatRoomALL = response.body();

                                        for (int i = 0; i < dataChatRoomALL.getRoomList().size(); i++) {
                                            try {
                                                roomList.add(dataChatRoomALL.getRoomList().get(i));
                                            } catch (Exception e) {

                                            }

                                        }
                                        adapter.notifyDataSetChanged();
                                        if (roomList.size() != 0) {
                                            cursorChatRoomNum = roomList.get(roomList.size() - 1).getFinalChatTime();
                                        }
                                        if (!response.body().getRoomCount().equals(phasingNum)) {
                                            isFinalPhase = true;
                                        }
                                        onCreateViewIsSet = true;
                                        scrollCheck = true;
                                    }
                                }

                                @Override
                                public void onFailure(Call<DataChatRoomAll> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "버전이 낮아서 스크롤링 페이징 안됨;", Toast.LENGTH_SHORT).show();
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                Bundle bundle = msg.getData();
                String purpose=bundle.getString("purpose");
                if(purpose.equals("reloadAlarmImage")){
                    chatNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                    return;
                }
                String data = bundle.getString("message");
                //데이터를 서버에서 가져올게 아니라 그냥 바로 업뎃해줄까?
                //그러 cursor가 햇갈리게 될텐데
                //이 부분 수정해야함
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName, "update", cursorChatRoomNum, "chatList");
                call.enqueue(new retrofit2.Callback<DataChatRoomAll>() {
                    @Override
                    public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                        //통신 성공할 경우
                        if (response.isSuccessful() && response.body() != null) {

                            roomList.clear();

                            DataChatRoomAll dataChatRoomALL = response.body();
                            for (int i = 0; i < dataChatRoomALL.getRoomList().size(); i++) {
                                try {
                                    roomList.add(dataChatRoomALL.getRoomList().get(i));
                                } catch (Exception e) {

                                }
                            }
                        }
                        adapter.setRoomList(roomList);
                        adapter.notifyDataSetChanged();
                        if (roomList.size() != 0) {
                            cursorChatRoomNum = roomList.get(roomList.size() - 1).getFinalChatTime();
                        }
                        if (!response.body().getRoomCount().equals(phasingNum)) {
                            isFinalPhase = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<DataChatRoomAll> call, Throwable t) {

                    }


                });
            }
        };

        chatNotificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_alarm_collect.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataReceiver, new IntentFilter("reloadRoomList"));

        if (onCreateViewIsSet) {
            chatNotificationImage.setVisibility(View.INVISIBLE);
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<DataChatRoomAll> call = service.getRoomAllInfo(nickName, "update", cursorChatRoomNum, "chatList");
            call.enqueue(new Callback<DataChatRoomAll>() {
                @Override
                public void onResponse(Call<DataChatRoomAll> call, Response<DataChatRoomAll> response) {

                    //통신 성공할 경우
                    if (response.isSuccessful() && response.body() != null) {
                        roomList.clear();
                        DataChatRoomAll dataChatRoomALL = response.body();
                        for (int i = 0; i < dataChatRoomALL.getRoomList().size(); i++) {
                            try {
                                roomList.add(dataChatRoomALL.getRoomList().get(i));
                            } catch (Exception e) {

                            }
                        }
                        if (response.body().isNotification()) {
                            chatNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                            chatNotificationImage.setVisibility(View.VISIBLE);
                        } else {
                            chatNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_24);
                            chatNotificationImage.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<DataChatRoomAll> call, Throwable t) {

                }


            });
        }

    }

    public void variableInit(View view) {

        cursorChatRoomNum = "0";
        phasingNum = "7";

        //기본 xml 연결.
        chatNotificationImage = view.findViewById(R.id.chat_notification);

        //recyclerview 관련
        chatRecyclerView = view.findViewById(R.id.chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        roomList = new ArrayList<>();
        adapter = new Adapter_chat_room(getActivity(), roomList);

        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(adapter);


        //shared 관련
        // shared 값 가져오기
        sharedPreferences = getContext().getSharedPreferences("noAlarmArrayList", Context.MODE_PRIVATE);


        //adapter clickListener 장착
        adapter.setRoomClickListener(new Adapter_chat_room.Adapter_chat_room_click() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), Activity_trade_chat.class);
                intent.putExtra("roomNum", roomList.get(position).getRoomNum());
                startActivity(intent);
            }

            @Override
            public void onOptionClick(int position, View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflate = popup.getMenuInflater();
                //채팅방 알림끄기 에 따라서, 알림 켜기 로 바꿔야하기 때문에, menu inflate하는걸 다르게 해야겠네.
                boolean noAlarmCheck = false;
                for (int i = 0; i < getNoAlarmRoomArrayList().size(); i++) {

                    if (getNoAlarmRoomArrayList().get(i) != null) {
                        if (getNoAlarmRoomArrayList().get(i).equals(roomList.get(position).getRoomNum())) {
                            noAlarmCheck = true;
                            break;
                        }
                    }

                }
                if (!noAlarmCheck) {
                    inflate.inflate(R.menu.chat_room_menu, popup.getMenu());
                    popup.show();
                } else {
                    inflate.inflate(R.menu.chat_room_no_alarm_menu, popup.getMenu());
                    popup.show();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.chat_room_out) {
                            //채팅방 나가기

                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<DataChatRoom> call = service.userRoomOut(roomList.get(position).getRoomNum(), nickName, roomList.get(position).getOtherUserNickname());
                            call.enqueue(new Callback<DataChatRoom>() {
                                @Override
                                public void onResponse(Call<DataChatRoom> call, Response<DataChatRoom> response) {

                                    if (response.isSuccessful() && response.body() != null) {

                                        //여기서 해당 포지션 삭제해야함.
                                        if (response.body().getSuccess()) {


                                            //채팅방 알림껏던 기록들 제거해야함

                                            ArrayList<String> noAlarmArrayList = getNoAlarmRoomArrayList();
                                            for (int i = 0; i < noAlarmArrayList.size(); i++) {
                                                if (noAlarmArrayList.get(i) != null) {
                                                    if (noAlarmArrayList.get(i).equals(roomList.get(position).getRoomNum())) {

                                                        noAlarmArrayList.remove(i);
                                                        break;
                                                    }
                                                }
                                            }

                                            setNoAlarmRoomArrayList(noAlarmArrayList);
                                            roomList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<DataChatRoom> call, Throwable t) {

                                }
                            });
                        } else if (id == R.id.chat_room_announce_off) {
                            //채팅방 알림끄기

                            ArrayList<String> noAlarmArrayList = getNoAlarmRoomArrayList();
                            noAlarmArrayList.add(roomList.get(position).getRoomNum());
                            setNoAlarmRoomArrayList(noAlarmArrayList);
                            adapter.notifyItemChanged(position);
                        } else if (id == R.id.chat_room_announce_on) {
                            ArrayList<String> noAlarmArrayList = getNoAlarmRoomArrayList();
                            for (int i = 0; i < noAlarmArrayList.size(); i++) {
                                if (noAlarmArrayList.get(i) != null) {
                                    if (noAlarmArrayList.get(i).equals(roomList.get(position).getRoomNum())) {
                                        noAlarmArrayList.remove(i);
                                        adapter.notifyItemChanged(position);
                                        break;
                                    }
                                }
                            }
                            setNoAlarmRoomArrayList(noAlarmArrayList);
                        }

                        return false;
                    }
                });
            }
        });

        //레트로핏 선언
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.72.60/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //닉네임 가져오기..
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        nickName = sharedPreferences.getString("nickName", "");
    }

    public void setNoAlarmRoomArrayList(ArrayList<String> noAlarmArrayList) {

        Gson gson = new GsonBuilder().create();
        Type arraylistType = new TypeToken<ArrayList<String>>() {       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
        }.getType();

        String objectToString = gson.toJson(noAlarmArrayList, arraylistType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("noAlarmArrayList", objectToString);
        editor.apply();
    }

    public ArrayList<String> getNoAlarmRoomArrayList() {

        //gson 을 활용하여서 shared에 저장된 string을 object로 변환
        Gson gson = new GsonBuilder().create();

        ArrayList<String> noAlarmArrayList;
        String stringToObject = sharedPreferences.getString("noAlarmArrayList", "");
        Type arraylistType = new TypeToken<ArrayList<String>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();
        try {
            noAlarmArrayList = gson.fromJson(stringToObject, arraylistType);
            if (noAlarmArrayList == null) {
                noAlarmArrayList = new ArrayList<String>();
            }
            return noAlarmArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return noAlarmArrayList = new ArrayList<>();
        }

    }


}