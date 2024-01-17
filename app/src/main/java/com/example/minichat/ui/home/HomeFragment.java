package com.example.minichat.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.minichat.MyApplication;
import com.example.minichat.R;
import com.example.minichat.databinding.FragmentHomeBinding;
import com.example.minichat.user.LoginActivity;
import com.example.minichat.user.ResetActivity;
import com.example.minichat.utils.UserUtil;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    private TextView tv_userName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tv_userName = root.findViewById(R.id.tv_userName);
        tv_userName.setText(MyApplication.getInstance().infoMap.get("username"));
        TextView tv_editPassword = root.findViewById(R.id.tv_editPassword);
        tv_editPassword.setOnClickListener(this);
        Button btn_logout = root.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_userName.setText(MyApplication.getInstance().infoMap.get("username"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_editPassword){
            Intent intent = new Intent(getContext(), ResetActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_logout) {
            UserUtil.logout();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}