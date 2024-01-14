package com.example.minichat.ui.chat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.constant.constant;
import com.example.minichat.user.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2024/1/14 13:41
 */
public class CreateAiActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_createAvatar;
    private CheckBox cb_publish;
    private EditText et_createName;
    private EditText et_createDesc;
    private int avatarNum = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ImageView iv_cancel = findViewById(R.id.iv_cancel);
        TextView tv_complete = findViewById(R.id.tv_complete);
        iv_createAvatar = findViewById(R.id.iv_createAvatar);
        ImageView iv_avatar0 = findViewById(R.id.iv_avatar0);
        ImageView iv_avatar1 = findViewById(R.id.iv_avatar1);
        ImageView iv_avatar2 = findViewById(R.id.iv_avatar2);
        ImageView iv_avatar3 = findViewById(R.id.iv_avatar3);
        ImageView iv_avatar4 = findViewById(R.id.iv_avatar4);
        ImageView iv_avatar5 = findViewById(R.id.iv_avatar5);
        ImageView iv_avatar6 = findViewById(R.id.iv_avatar6);
        ImageView iv_avatar7 = findViewById(R.id.iv_avatar7);
        iv_cancel.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        iv_avatar0.setOnClickListener(this);
        iv_avatar1.setOnClickListener(this);
        iv_avatar2.setOnClickListener(this);
        iv_avatar3.setOnClickListener(this);
        iv_avatar4.setOnClickListener(this);
        iv_avatar5.setOnClickListener(this);
        iv_avatar6.setOnClickListener(this);
        iv_avatar7.setOnClickListener(this);
        cb_publish = findViewById(R.id.cb_public);
        et_createName = findViewById(R.id.et_createName);
        et_createDesc = findViewById(R.id.et_createDesc);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_cancel){
            finish();
        } else if (view.getId() == R.id.tv_complete) {
            createRobot();
        } else if (view.getId() == R.id.iv_avatar0) {
            avatarNum = 0;
            iv_createAvatar.setImageResource(R.drawable.avatar0);
        } else if (view.getId() == R.id.iv_avatar1) {
            avatarNum = 1;
            iv_createAvatar.setImageResource(R.drawable.avatar1);
        } else if (view.getId() == R.id.iv_avatar2) {
            avatarNum = 2;
            iv_createAvatar.setImageResource(R.drawable.avatar2);
        } else if (view.getId() == R.id.iv_avatar3) {
            avatarNum = 3;
            iv_createAvatar.setImageResource(R.drawable.avatar3);
        } else if (view.getId() == R.id.iv_avatar4) {
            avatarNum = 4;
            iv_createAvatar.setImageResource(R.drawable.avatar4);
        } else if (view.getId() == R.id.iv_avatar5) {
            avatarNum = 5;
            iv_createAvatar.setImageResource(R.drawable.avatar5);
        } else if (view.getId() == R.id.iv_avatar6) {
            avatarNum = 6;
            iv_createAvatar.setImageResource(R.drawable.avatar6);
        } else if (view.getId() == R.id.iv_avatar7) {
            avatarNum = 7;
            iv_createAvatar.setImageResource(R.drawable.avatar7);
        }
    }

    private void createRobot(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("avatar", String.valueOf(avatarNum))
                .add("name", et_createName.getText().toString())
                .add("desc", et_createDesc.getText().toString())
                .add("whetherPublic", String.valueOf(cb_publish.isChecked() ? 1 : 0))
                .build();

        Request request = new Request.Builder()
                .url(constant.IP_ADDRESS + "/create")
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
                        Intent intent = new Intent(CreateAiActivity.this, LoginActivity.class);
                        intent.putExtra("validate_token", false);
                        startActivity(intent);
                    }else {
                        int code = jsonObject.get("code").getAsInt();
                        if (code == 201) {
                            runOnUiThread(() -> {
                                Toast.makeText(CreateAiActivity.this, "创建聊天机器人成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }else {
                            runOnUiThread(() -> {
                                Toast.makeText(CreateAiActivity.this, "创建聊天机器人失败！", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }
                }
            }
        });
    }
}
