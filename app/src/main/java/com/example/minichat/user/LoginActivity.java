package com.example.minichat.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.database.DBHelper;
import com.example.minichat.entity.LoginInfo;
import com.example.minichat.utils.UserUtil;

import java.io.IOException;

/**
 * @author SummCoder
 */
public class LoginActivity  extends AppCompatActivity implements View.OnFocusChangeListener {

    private DBHelper mHelper;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private CheckBox cb_remember;
    private String username;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        boolean validate_token = intent.getBooleanExtra("validate_token", true);
        showLoginDialog();
        if(!validate_token){
            Toast.makeText(LoginActivity.this, "登录会话超时，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登录");

        // 设置对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        builder.setView(view);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 获取输入框和登录按钮
        usernameEditText = view.findViewById(R.id.et_username);
        passwordEditText = view.findViewById(R.id.et_password);
        Button loginButton = view.findViewById(R.id.btn_login);
        cb_remember = view.findViewById(R.id.cb_remember);

        // 阻止用户进行其他操作
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        passwordEditText.setOnFocusChangeListener(this);

        ActivityResultLauncher<Intent> register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result != null){
                Intent intent = result.getData();
                if(intent != null && result.getResultCode() == Activity.RESULT_OK){
                    Bundle bundle = intent.getExtras();
                    username = bundle.getString("username");
                    MyApplication app = MyApplication.getInstance();
                    app.infoMap.put("username", username);
                    dialog.dismiss();
                    finish();
                }
            }
        });

        // 设置注册文本的跳转事件
        TextView tvRegister = dialog.findViewById(R.id.tv_register);
        assert tvRegister != null;
        tvRegister.setOnClickListener(v -> {
            // 启动RegisterActivity，进行跳转
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            register.launch(intent);
        });


        // 设置登录按钮的点击事件
        loginButton.setOnClickListener(v -> {
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean login;
            try {
                login = UserUtil.login(username, password);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 进行登录验证，这里可以根据具体的登录逻辑进行处理
            if (login) {
                MyApplication app = MyApplication.getInstance();
                app.infoMap.put("username", username);
                app.infoMap.put("remember", cb_remember.isChecked() ? "是":"否");
                LoginInfo loginInfo = new LoginInfo(username, password, cb_remember.isChecked());
                mHelper.saveLoginInfo(loginInfo);
                // 登录成功，关闭对话框
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            } else {
                // 登录失败，提示用户登录失败
                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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
        reload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 关闭数据库连接
//        mHelper.closeLink();
    }

    // 进入页面时加载数据库中存储的用户名和密码
    private void reload(){
        LoginInfo info = mHelper.queryTop();
        if(info != null && info.remember){
            usernameEditText.setText(info.username);
            passwordEditText.setText(info.password);
            cb_remember.setChecked(true);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view.getId() == R.id.et_password && hasFocus){
            LoginInfo info = mHelper.queryByUsername(usernameEditText.getText().toString());
            if(info != null){
                passwordEditText.setText(info.password);
                cb_remember.setChecked(info.remember);
            }else {
                passwordEditText.setText("");
                cb_remember.setChecked(false);
            }
        }
    }

}
