package com.example.minichat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.constant.constant;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.entity.Chat;
import com.example.minichat.user.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private FragmentChatBinding binding;
    private ListView lv_chat;
    private List<Map<String, Object>> chatList;
    private ImageView iv_create;
    private int[] avatar = {
      R.drawable.avatar0, R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
      R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7
    };
    private MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();

    public LiveData<List<Chat>> getChatsLiveData() {
        return chatsLiveData;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv_chat = root.findViewById(R.id.lv_chat);
        iv_create = root.findViewById(R.id.iv_create);
        iv_create.setOnClickListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        fetchData();
        this.getChatsLiveData().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null && !chats.isEmpty()) {
                updateUI(chats);
            } else {
                lv_chat.removeAllViews();
            }
        });
    }


    private void updateUI(List<Chat> chats) {
        chatList = new ArrayList<>();
        for (Chat chat : chats) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", chat.name);
            map.put("decs", chat.desc);
            map.put("avatar", avatar[chat.avatar]);
            chatList.add(map);
        }

        String[] from = {"name", "decs", "avatar"};
        int[] to = {R.id.tv_name, R.id.tv_desc, R.id.iv_avatar};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), chatList, R.layout.item_chat, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // 为每个item设置点击事件
                view.setOnClickListener(v -> {
                    // 获取被点击的item的数据
                    Chat chat = chats.get(position);
                    // 执行跳转操作
                    navigateToChatDetail(chat);
                });
                return view;
            }
        };

        lv_chat.setAdapter(simpleAdapter);
    }

    private void navigateToChatDetail(Chat chat) {
        Intent intent = new Intent(getContext(), ChatDetailActivity.class);
        intent.putExtra("chatInfo", chat.id);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_create){
            // 跳转至创建ai机器人界面
            Intent intent = new Intent(getContext(), CreateAiActivity.class);
            startActivity(intent);
        }
    }

    private void fetchData(){
        List<Chat> chatData = new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(constant.IP_ADDRESS + "/user/getrobots")
                .header("Authorization", Objects.requireNonNull(MyApplication.getInstance().infoMap.get("token")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                if (response.isSuccessful()) {
                    if(response.code() == 401){
                        // token已过期，需要重新登录
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.putExtra("validate_token", false);
                        startActivity(intent);
                    }else {
                        int code = jsonObject.get("code").getAsInt();
                        if (code == 200) {
                            Gson gson = new Gson();
                            Chat[] chats = gson.fromJson(jsonObject.get("data").getAsJsonArray(), Chat[].class);
                            chatData.addAll(Arrays.asList(chats));
                            chatsLiveData.postValue(chatData);
                        }
                    }
                }
            }
        });
    }
}