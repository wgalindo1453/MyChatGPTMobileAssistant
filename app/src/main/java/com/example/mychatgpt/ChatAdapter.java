package com.example.mychatgpt;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> mMessageList;

    public ChatAdapter(List<Message> messageList) {
        this.mMessageList = messageList;
    }

    public boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        int backgroundColor = Color.parseColor("#4C5455"); // Replace with actual color
        if (isColorDark(backgroundColor)) {
            holder.tvMessage.setTextColor(Color.WHITE);
            holder.tvTimestamp.setTextColor(Color.WHITE);
        } else {
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvTimestamp.setTextColor(Color.BLACK);
        }


        Message message = mMessageList.get(position);
        holder.tvMessage.setText(message.getText());
        holder.tvTimestamp.setText(message.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;
        TextView tvTimestamp;

        public ChatViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tv_message);
            tvTimestamp = view.findViewById(R.id.tv_timestamp);
        }
    }
}
