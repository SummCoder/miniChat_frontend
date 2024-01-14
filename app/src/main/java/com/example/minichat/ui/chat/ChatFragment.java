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

import com.example.minichat.R;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.entity.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private FragmentChatBinding binding;
    private ListView lv_chat;
    private List<Map<String, Object>> chatList;
    private ImageView iv_create;


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
        List<Chat> chats = new ArrayList<>();
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        chats.add(new Chat(0, "九月", "我是你的朋友九月", R.drawable.avatar0, 1));
        chats.add(new Chat(0, "十一月", "我是你的朋友十一月", R.drawable.avatar1, 1));
        updateUI(chats);
    }


    private void updateUI(List<Chat> chats) {
        chatList = new ArrayList<>();
        for (Chat chat : chats) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", chat.name);
            map.put("decs", chat.desc);
            map.put("avatar", chat.avatar);
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
}