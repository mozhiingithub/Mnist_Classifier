package com.example.mnistclassifier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> msgList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        if (Msg.TYPE_RECEIVE == msg.getType()) {
            holder.rightTextLayout.setVisibility(View.GONE);
            holder.rightImgLayout.setVisibility(View.GONE);
            if (Msg.TYPE_TEXT == msg.getContent_type()) {
                holder.leftTextLayout.setVisibility(View.VISIBLE);
                holder.leftImgLayout.setVisibility(View.GONE);
                holder.leftText.setText(msg.getText_content());
            } else if (Msg.TYPE_IMG == msg.getContent_type()) {
                holder.leftTextLayout.setVisibility(View.GONE);
                holder.leftImgLayout.setVisibility(View.VISIBLE);
                holder.leftImg.setImageBitmap(msg.getImg_content());
            }
        } else if (Msg.TYPE_SEND == msg.getType()) {
            holder.leftTextLayout.setVisibility(View.GONE);
            holder.leftImgLayout.setVisibility(View.GONE);
            if (Msg.TYPE_TEXT == msg.getContent_type()) {
                holder.rightTextLayout.setVisibility(View.VISIBLE);
                holder.rightImgLayout.setVisibility(View.GONE);
                holder.rightText.setText(msg.getText_content());
            } else if (Msg.TYPE_IMG == msg.getContent_type()) {
                holder.rightTextLayout.setVisibility(View.GONE);
                holder.rightImgLayout.setVisibility(View.VISIBLE);
                holder.rightImg.setImageBitmap(msg.getImg_content());
            }
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftTextLayout;
        LinearLayout rightTextLayout;
        LinearLayout leftImgLayout;
        LinearLayout rightImgLayout;
        TextView leftText;
        TextView rightText;
        ImageView leftImg;
        ImageView rightImg;

        public ViewHolder(View view) {
            super(view);
            leftTextLayout = (LinearLayout) view.findViewById(R.id.left_text_layout);
            rightTextLayout = (LinearLayout) view.findViewById(R.id.right_text_layout);
            leftImgLayout = (LinearLayout) view.findViewById(R.id.left_img_layout);
            rightImgLayout = (LinearLayout) view.findViewById(R.id.right_img_layout);
            leftText = (TextView) view.findViewById(R.id.left_text);
            rightText = (TextView) view.findViewById(R.id.right_text);
            leftImg = (ImageView) view.findViewById(R.id.left_img);
            rightImg = (ImageView) view.findViewById(R.id.right_img);
        }
    }

    public MsgAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }


}
