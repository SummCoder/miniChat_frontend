package com.example.minichat.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.R;
import com.example.minichat.utils.UserUtil;

import java.io.IOException;

/**
 * @author SummCoder
 * @date 2024/1/13 10:41
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_username_register;
    private EditText et_password1;
    private EditText et_password2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btn_register = findViewById(R.id.btn_register);
        TextView tv_register = findViewById(R.id.tv_register);
        et_username_register = findViewById(R.id.et_username_register);
        et_password1 = findViewById(R.id.et_password_register);
        et_password2 = findViewById(R.id.et_password_register2);
        btn_register.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_register){
            String register_username = et_username_register.getText().toString();
            String password_first = et_password1.getText().toString();
            String password_second = et_password2.getText().toString();
            if(register_username.equals("")){
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password_first.equals("") || password_second.equals("")){
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!password_second.equals(password_first)){
                Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isRegister;
            try {
                isRegister = UserUtil.register(register_username, password_first);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(isRegister){
                Toast.makeText(this, "新用户注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("username", register_username);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else {
                Toast.makeText(this, "该用户已存在！", Toast.LENGTH_SHORT).show();
            }
        }else {
            // 返回进行登录
            finish();
        }
    }
}
