package com.example.mnistclassifier;


import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MsgManager {
    private RecyclerView recyclerView; // 聊天信息框
    private MsgAdapter msgAdapter; // 聊天信息框适配器
    private List<Msg> msgList; // 信息列表

    public MsgManager(List<Msg> msgList, RecyclerView recyclerView, MsgAdapter msgAdapter) {
        this.msgList = msgList;
        this.recyclerView = recyclerView;
        this.msgAdapter = msgAdapter;
    }

    private void addMsg(Msg msg) {
        msgList.add(msg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);
        recyclerView.scrollToPosition(msgList.size() - 1);
    }

    public void addTextMsg(String content, int type) {
        Msg msg = new Msg(content, type);
        addMsg(msg);
    }

    public void addImgMsg(Bitmap content, int type) {
        Msg msg = new Msg(content, type);
        addMsg(msg);
    }
}
