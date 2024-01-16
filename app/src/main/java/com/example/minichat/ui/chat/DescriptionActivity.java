package com.example.minichat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.R;

/**
 * @author SummCoder
 * @date 2024/1/16 1:11
 */
public class DescriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] avatar = {
            R.drawable.avatar0, R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ImageView iv_moreBack = findViewById(R.id.iv_moreBack);
        iv_moreBack.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("robotDesc");
        assert bundle != null;
        ImageView iv_moreAvatar = findViewById(R.id.iv_moreAvatar);
        iv_moreAvatar.setImageResource(avatar[bundle.getInt("avatar")]);
        TextView tv_moreName = findViewById(R.id.tv_moreName);
        tv_moreName.setText(bundle.getString("name"));
        TextView tv_moreDesc = findViewById(R.id.tv_moreDesc);
        tv_moreDesc.setText(bundle.getString("desc"));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_moreBack){
            finish();
        }
    }
}
