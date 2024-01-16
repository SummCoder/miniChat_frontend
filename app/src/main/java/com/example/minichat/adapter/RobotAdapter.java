package com.example.minichat.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.entity.Chat;

import java.util.List;

/**
 * @author SummCoder
 * @date 2024/1/16 14:01
 */
public class RobotAdapter extends RecyclerView.Adapter<RobotAdapter.RobotViewHolder>{
    private final List<Chat> chatList;
    private final OnChatItemClickListener listener;
    private final int[] avatar = {
            R.drawable.avatar0, R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7
    };

    public RobotAdapter(List<Chat> chatList, OnChatItemClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RobotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_public, parent, false);
        return new RobotViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RobotViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.iv_publicAvatar.setImageResource(avatar[chat.avatar]);
        holder.tv_publicName.setText(chat.name);
        holder.tv_publicDesc.setText(chat.desc);
        if(chat.whetherPublic == 0){
            holder.iv_publicAdd.setImageResource(R.drawable.ic_new);
        }else {
            holder.iv_publicAdd.setImageResource(R.drawable.ic_complete);
        }

        holder.itemView.setOnClickListener(v -> {
            // 执行跳转操作
            if(chat.whetherPublic == 1){
                listener.onChatItemClicked(chat);
            }
        });

        holder.iv_publicAdd.setOnClickListener(v -> {
            if(chat.whetherPublic == 0){
                listener.addPublic(chat.id);
                chat.whetherPublic = 1;
                chatList.set(position, chat);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("确定要移除该聊天机器人吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            listener.deletePublic(chat.id);
                            chat.whetherPublic = 0;
                            chatList.set(position, chat);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                            // 用户点击取消，不执行任何操作
                        })
                        .show();
                return;
            }
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class RobotViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_publicAvatar;
        TextView tv_publicName;
        TextView tv_publicDesc;
        ImageView iv_publicAdd;

        public RobotViewHolder(View itemView) {
            super(itemView);
            iv_publicAvatar = itemView.findViewById(R.id.iv_publicAvatar);
            tv_publicName = itemView.findViewById(R.id.tv_publicName);
            tv_publicDesc = itemView.findViewById(R.id.tv_publicDesc);
            iv_publicAdd = itemView.findViewById(R.id.iv_publicAdd);
        }
    }

    public interface OnChatItemClickListener {
        void onChatItemClicked(Chat chat);
        void addPublic(int robotId);
        void deletePublic(int robotId);
    }
}
