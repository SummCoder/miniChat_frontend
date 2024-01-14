package com.example.minichat.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.utils.UserUtil;

/**
 * @author SummCoder
 * @date 2024/1/13 16:29
 */
public class ResetActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_reset_password1;
    private EditText et_reset_password2;
    private EditText et_reset_username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        et_reset_username = findViewById(R.id.et_reset_username);
        et_reset_password1 = findViewById(R.id.et_reset_password1);
        et_reset_password2 = findViewById(R.id.et_reset_password2);
        MyApplication myApplication = MyApplication.getInstance();
        et_reset_username.setText(myApplication.infoMap.get("username"));
        et_reset_username.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        String reset_username = et_reset_username.getText().toString();
        String reset_password_first = et_reset_password1.getText().toString();
        String reset_password_second = et_reset_password2.getText().toString();

        if(reset_username.equals("")){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(reset_password_first.equals("") || reset_password_second.equals("")){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!reset_password_second.equals(reset_password_first)){
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isReset = UserUtil.reset(this, reset_password_first);
        if(isReset){
            Toast.makeText(this, "修改密码成功！", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this, "密码修改失败！", Toast.LENGTH_SHORT).show();
        }
    }
}
