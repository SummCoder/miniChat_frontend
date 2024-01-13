package com.example.minichat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.user.LoginActivity;
import com.example.minichat.user.ResetActivity;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {

    }
}