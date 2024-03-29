package com.example.minichat.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.adapter.ChatAdapter;
import com.example.minichat.adapter.RobotAdapter;
import com.example.minichat.constant.constant;
import com.example.minichat.database.DBHelper;
import com.example.minichat.databinding.FragmentSearchBinding;
import com.example.minichat.entity.Chat;
import com.example.minichat.ui.chat.ChatDetailActivity;
import com.example.minichat.ui.chat.CreateAiActivity;
import com.example.minichat.user.LoginActivity;
import com.example.minichat.utils.UserUtil;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment implements RobotAdapter.OnChatItemClickListener{

    private FragmentSearchBinding binding;
    private final MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();
//    private SimpleAdapter simpleAdapter;

    private RobotAdapter robotAdapter;
    private RecyclerView rv_publicRobot;

    public LiveData<List<Chat>> getChatsLiveData() {
        return chatsLiveData;
    }

    private DBHelper mHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        lv_publicRobot = root.findViewById(R.id.lv_publicRobot);
        rv_publicRobot = root.findViewById(R.id.rv_publicRobot);
        rv_publicRobot.setLayoutManager(new LinearLayoutManager(getContext()));
        EditText et_search = root.findViewById(R.id.et_search);
        et_search.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = et_search.getText().toString();
                performSearch(searchText); // 执行搜索操作
                et_search.setText("");
                return true;
            }
            return false;
        });
        mHelper = DBHelper.getInstance(getContext());
        mHelper.openReadLink();
        mHelper.openWriteLink();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    // 执行搜索操作的方法
    private void performSearch(String searchText) {
        // 在这里执行搜索操作
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("searchItem", searchText);
        startActivity(intent);
    }

    private void initData() {
        fetchData();
        this.getChatsLiveData().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null && !chats.isEmpty()) {
                rv_publicRobot.setVisibility(View.VISIBLE);
                updateUI(chats);
            }else {
                rv_publicRobot.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(List<Chat> chats) {

        robotAdapter = new RobotAdapter(chats, this);
        rv_publicRobot.setAdapter(robotAdapter);

    }

    private void navigateToChatDetail(Chat chat) {
        Intent intent = new Intent(getContext(), ChatDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("chatId", chat.id);
        bundle.putInt("avatar", chat.avatar);
        bundle.putString("name", chat.name);
        bundle.putString("desc", chat.desc);
        intent.putExtra("chatInfo", bundle);
        startActivity(intent);
    }


    private void fetchData(){
        new Thread(() -> {
            List<Chat> chatData = new ArrayList<>();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/getpublic")
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
        }).start();
    }

    private void deleteRobot(int robotId) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/delete/" + robotId)
                    .delete()
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
                                mHelper.deleteMsg(robotId);
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "聊天机器人移除成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }).start();
    }

    private void addRobot(int robotId){
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            FormBody formBody = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/add/" + robotId)
                    .post(formBody)
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
                            if (code == 201) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "聊天机器人添加成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    // 实现接口方法，在这里执行跳转操作
    @Override
    public void onChatItemClicked(Chat chat) {
        navigateToChatDetail(chat);
    }
    @Override
    public void addPublic(int robotId){
        addRobot(robotId);
    }

    public void deletePublic(int robotId){
        deleteRobot(robotId);
    }
}