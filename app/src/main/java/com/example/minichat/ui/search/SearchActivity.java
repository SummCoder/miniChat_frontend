package com.example.minichat.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.adapter.RobotAdapter;
import com.example.minichat.constant.constant;
import com.example.minichat.database.DBHelper;
import com.example.minichat.entity.Chat;
import com.example.minichat.ui.chat.ChatDetailActivity;
import com.example.minichat.user.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2024/1/16 14:58
 */
public class SearchActivity extends AppCompatActivity implements RobotAdapter.OnChatItemClickListener{

    private final MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();
    private String searchItem;
    private RecyclerView rv_searchRobot;

    private RobotAdapter robotAdapter;
    private EditText et_search;

    public LiveData<List<Chat>> getChatsLiveData() {
        return chatsLiveData;
    }
    private DBHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        searchItem = intent.getStringExtra("searchItem");
        rv_searchRobot = findViewById(R.id.rv_searchRobot);
        rv_searchRobot.setLayoutManager(new LinearLayoutManager(this));
        et_search = findViewById(R.id.et_search1);
        et_search.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = et_search.getText().toString();
                performSearch(searchText); // 执行搜索操作
                et_search.setText("");
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = DBHelper.getInstance(this);
        mHelper.openReadLink();
        mHelper.openWriteLink();
    }

    // 执行搜索操作的方法
    private void performSearch(String searchText) {
        // 在这里执行搜索操作
        searchItem = searchText;
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        fetchData(searchItem);
        getChatsLiveData().observe(this, chats -> {
            if (chats != null && !chats.isEmpty()) {
                rv_searchRobot.setVisibility(View.VISIBLE);
                updateUI(chats);
            }else {
                rv_searchRobot.setVisibility(View.GONE);
            }
        });
    }



    private void updateUI(List<Chat> chats) {

        robotAdapter = new RobotAdapter(chats, this);
        rv_searchRobot.setAdapter(robotAdapter);

    }


    private void fetchData(String searchItem){
        List<Chat> chatData = new ArrayList<>();
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/search/" + searchItem)
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
                            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
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

    @Override
    public void onChatItemClicked(Chat chat) {
        navigateToChatDetail(chat);
    }

    private void navigateToChatDetail(Chat chat) {
        Intent intent = new Intent(this, ChatDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("chatId", chat.id);
        bundle.putInt("avatar", chat.avatar);
        bundle.putString("name", chat.name);
        bundle.putString("desc", chat.desc);
        intent.putExtra("chatInfo", bundle);
        startActivity(intent);
    }

    @Override
    public void addPublic(int robotId) {
        addRobot(robotId);
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
                            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                            intent.putExtra("validate_token", false);
                            startActivity(intent);
                        }else {
                            int code = jsonObject.get("code").getAsInt();
                            if (code == 201) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SearchActivity.this, "聊天机器人添加成功", Toast.LENGTH_SHORT).show();
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
    public void deletePublic(int robotId) {
        deleteRobot(robotId);
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
                            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                            intent.putExtra("validate_token", false);
                            startActivity(intent);
                        }else {
                            int code = jsonObject.get("code").getAsInt();
                            if (code == 200) {
                                mHelper.deleteMsg(robotId);
                                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "聊天机器人移除成功", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                }
            });
        }).start();
    }
}
