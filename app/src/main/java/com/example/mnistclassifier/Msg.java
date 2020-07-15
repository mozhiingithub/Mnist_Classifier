package com.example.mnistclassifier;


import android.graphics.Bitmap;

public class Msg {
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_IMG = 3;
    private String text_content;
    private Bitmap img_content;

    private int type;
    private int content_type;

    public Msg(String content, int type) {
        this.text_content = content;
        this.type = type;
        this.content_type = Msg.TYPE_TEXT;
    }

    public Msg(Bitmap content, int type) {
        this.img_content = content;
        this.type = type;
        this.content_type = Msg.TYPE_IMG;
    }

    public int getType() {
        return type;
    }

    public int getContent_type() {
        return content_type;
    }

    public String getText_content() {
        return text_content;
    }

    public Bitmap getImg_content() {
        return img_content;
    }
}
