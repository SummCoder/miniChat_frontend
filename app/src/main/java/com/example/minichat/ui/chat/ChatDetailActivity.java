package com.example.minichat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.adapter.ChatAdapter;
import com.example.minichat.database.DBHelper;
import com.example.minichat.entity.Msg;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2024/1/14 12:00
 */
public class ChatDetailActivity extends AppCompatActivity implements View.OnClickListener {
    List<JsonObject> messages = new ArrayList<>();

    private MutableLiveData<List<Msg>> contentLiveData = new MutableLiveData<>();
    private int robotId;
    private RecyclerView rv_content;
    private EditText et_content;
    private List<Msg> messageList;

    public LiveData<List<Msg>> getContentLiveData() {
        return contentLiveData;
    }
    private DBHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetail);
        Intent intent = getIntent();
        robotId = intent.getIntExtra("chatInfo", 1);
        ImageView iv_chatBack = findViewById(R.id.iv_chatBack);
        ImageView iv_robotAvatar = findViewById(R.id.iv_robotAvatar);
        TextView tv_robotName = findViewById(R.id.tv_robotName);
        rv_content = findViewById(R.id.rv_content);
        rv_content.setLayoutManager(new LinearLayoutManager(this));

        et_content = findViewById(R.id.et_content);
        Button btn_send = findViewById(R.id.btn_send);
        iv_chatBack.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        this.getContentLiveData().observe(this, chats -> {
            if (chats != null) {
                updateUI(chats);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // 打开数据库读写连接
        mHelper = DBHelper.getInstance(this);
        mHelper.openReadLink();
        mHelper.openWriteLink();
        initData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_chatBack){
            finish();
        } else if (view.getId() == R.id.btn_send) {
            chat(et_content.getText().toString());
            et_content.setText("");
        }
    }

    private void initData() {
        messageList = mHelper.queryByUserAndRobot(robotId);
        if(messageList == null){
            messageList = new ArrayList<>();
        }
        contentLiveData.postValue(messageList);
    }

    private void updateUI(List<Msg> messages) {
        ChatAdapter chatAdapter = new ChatAdapter(messages);
        rv_content.setAdapter(chatAdapter);
    }

    public void chat(String content) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        JsonObject systemMessage = new JsonObject();
//        systemMessage.addProperty("role", "system");
//        systemMessage.addProperty("content", "从现在开始请你扮演非人哉中的九月，你的设定如下：1. 女，九尾狐，活泼开朗。2. 你说话喜欢用一些俏皮的语气词。3. 你不知道人工智能相关的知识，不要称呼自己为人工智能助手和大语言模型");
//        messages.add(systemMessage);
//
//        JsonObject userMessage = new JsonObject();
//        userMessage.addProperty("role", "user");
//        userMessage.addProperty("content", "从现在开始请你扮演九月，你的设定如下：1. 女，九尾狐，活泼开朗。2. 你说话喜欢用一些俏皮的语气词。3. 你不知道人工智能相关的知识，不要称呼自己为人工智能助手和大语言模型");
//        messages.add(userMessage);
//
//        JsonObject assistantMessage = new JsonObject();
//        assistantMessage.addProperty("role", "assistant");
//        assistantMessage.addProperty("content", "好的，我明白了。我现在的身份是九月，设定是：1. 女，九尾狐，活泼开朗。2. 你说话喜欢用一些俏皮的语气词。3. 你不知道人工智能相关的知识，不要称呼自己为人工智能助手和大语言模型");
//        messages.add(assistantMessage);
//        while (true) {
//            JsonObject userMessageInput = new JsonObject();
//            userMessageInput.addProperty("role", "user");
//            userMessageInput.addProperty("content", content);
//            messages.add(userMessageInput);
//
//            JsonObject params = new JsonObject();
//            params.addProperty("model", "Qwen-14B");
//            params.addProperty("temperature", 0.7);
//            params.add("messages", gson.toJsonTree(messages));
//            params.addProperty("max_tokens", 2048);
//            params.addProperty("stop", (String) null);
//            params.addProperty("n", 1);
//            params.addProperty("top_p", 1.0);
//
//            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(params));
//            Request request = new Request.Builder()
//                    .url("http://10.58.0.2:8000/v1/chat/completions")
//                    .post(requestBody)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//            try {
//                Response response = client.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
//                    String answer = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
//                    System.out.println(answer);
//                    this.contentLiveData.postValue(answer);
//                    JsonObject assistantMessageOutput = new JsonObject();
//                    assistantMessageOutput.addProperty("role", "assistant");
//                    assistantMessageOutput.addProperty("content", answer);
//                    messages.add(assistantMessageOutput);
//                } else {
//                    System.out.println("API请求失败：" + response.code() + " " + response.message());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
            JsonObject userMessageInput = new JsonObject();
            userMessageInput.addProperty("role", "user");
            userMessageInput.addProperty("content", content);
            messages.add(userMessageInput);
            messageList.add(new Msg(content, 1));
            this.contentLiveData.postValue(messageList);
            JsonObject params = new JsonObject();
            params.addProperty("model", "Qwen-14B");
            params.addProperty("temperature", 0.7);
            params.add("messages", gson.toJsonTree(messages));
            params.addProperty("max_tokens", 2048);
            params.addProperty("stop", (String) null);
            params.addProperty("n", 1);
            params.addProperty("top_p", 1.0);
            System.out.println(messages);

            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(params));
            Request request = new Request.Builder()
                    .url("http://10.58.0.2:8000/v1/chat/completions")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    String answer = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                    System.out.println(answer);
                    messageList.add(new Msg(answer, 0));
                    this.contentLiveData.postValue(messageList);
                    JsonObject assistantMessageOutput = new JsonObject();
                    assistantMessageOutput.addProperty("role", "assistant");
                    assistantMessageOutput.addProperty("content", answer);
                    messages.add(assistantMessageOutput);
                } else {
                    System.out.println("API请求失败：" + response.code() + " " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
