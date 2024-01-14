package com.example.minichat.utils;

import android.content.Context;
import android.content.Intent;

import com.example.minichat.MyApplication;
import com.example.minichat.constant.constant;
import com.example.minichat.user.LoginActivity;
import com.example.minichat.user.RegisterActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2024/1/13 10:53
 */
public class UserUtil {
    private static boolean isLoggedIn = false;
    private static boolean isReset = false;

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static boolean login(String username, String password) throws IOException {
        // 进行登录验证的逻辑，例如与服务器通信验证用户名和密码
        // 如果验证成功，将isLoggedIn设置为true
        // 如果验证失败，将isLoggedIn设置为false
        Thread thread = new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 构造登录请求的表单数据
            FormBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build();

            // 构造登录请求
            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/user/login")
                    .post(formBody)
                    .build();

            // 发送登录请求并处理响应
            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String responseBody;
            try {
                responseBody = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
            int code = jsonObject.get("code").getAsInt();
            if (code == 200){
                // 登录成功
                isLoggedIn = true;
                String Authorization = jsonObject.get("data").getAsString();
                MyApplication app = MyApplication.getInstance();
                app.infoMap.put("token", Authorization);
            } else if (code == 999) {
                isLoggedIn = false;
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isLoggedIn;
    }

    public static boolean register(String username, String password) throws IOException {
        Thread thread = new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 构造注册请求的表单数据
            FormBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build();

            // 构造注册请求
            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/user/register")
                    .post(formBody)
                    .build();

            // 发送注册请求并处理响应
            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String responseBody;
            try {
                responseBody = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
            int code = jsonObject.get("code").getAsInt();
            if (code == 201){
                // 登录成功
                isLoggedIn = true;
                String Authorization = jsonObject.get("data").getAsString();
                MyApplication app = MyApplication.getInstance();
                app.infoMap.put("token", Authorization);
            } else if (code == 999) {
                isLoggedIn = false;
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isLoggedIn;
    }

    public static boolean reset(Context ctx, String password) {
        isReset = false;
        Thread thread = new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 构造密码重置请求的表单数据
            FormBody formBody = new FormBody.Builder()
                    .add("password", password)
                    .build();

            // 构造密码重置请求
            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/user/reset")
                    .header("Authorization", Objects.requireNonNull(MyApplication.getInstance().infoMap.get("token")))
                    .post(formBody)
                    .build();
            // 发送密码重置请求并处理响应
            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String responseBody;
            try {
                responseBody = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
            if(response.code() == 401){
                // token已过期，需要重新登录
                Intent intent = new Intent(ctx, LoginActivity.class);
                intent.putExtra("validate_token", false);
                ctx.startActivity(intent);
            }else {
                int code = jsonObject.get("code").getAsInt();
                if (code == 200){
                    isReset = true;
                } else if (code == 400) {
                    isReset = false;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isReset;
    }

    public static void logout() {
        // 执行注销操作，例如清除登录状态、清除用户数据等
        isLoggedIn = false;
    }
}
