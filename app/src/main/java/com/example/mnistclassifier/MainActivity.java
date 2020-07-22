package com.example.mnistclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final static int GET_PERMISSION = 123; // 获取权限的请求码
    private final static int GET_PHOTO = 321; // 获取图片的请求码
    private EditText editText; // 文本发送框
    private Button button; // 发送按钮
    private RecyclerView recyclerView; // 聊天信息框
    private MsgAdapter msgAdapter; // 聊天信息框适配器
    private List<Msg> msgList; // 信息列表
    private MsgManager msgManager;//信息列表管理器

    private enum Status {
        ASK_POSITION,
        POSITION_TOP,
        POSITION_BASE,
        TOP_NORMAL,
        TOP_ABNORMAL,
        FALL,
        NO_FALL;
    }

    private Status status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        editText = (EditText) findViewById(R.id.input_text);
        button = (Button) findViewById(R.id.send_button);
        msgList = new ArrayList<>();
        msgAdapter = new MsgAdapter(msgList); // 适配器适配信息列表
        recyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager); // 聊天信息框添加一个布局管理者
        recyclerView.setAdapter(msgAdapter); // 聊天信息框添加一个适配器
        msgManager = new MsgManager(msgList, recyclerView, msgAdapter); // 初始化信息列表管理器

        String welcome = "欢迎使用柑橘叶片疾病诊断系统。\n请问您要诊断的叶片位于顶梢还是基枝？";
        msgManager.addTextMsg(welcome, Msg.TYPE_RECEIVE);
        status = Status.ASK_POSITION;


        //检查权限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "正在申请权限",
                    Toast.LENGTH_SHORT).show();
            //权限不足，申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, GET_PERMISSION);
        }

        //长按按钮，跳转至相册，选取图片
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_PHOTO);
                return true;
            }
        });

        // 点击按钮，发送文本框内容
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString(); // 获取文本框内容
                msgManager.addTextMsg(content, Msg.TYPE_SEND);
                editText.setText(""); // 将文本框清零
                switch (status) {
                    case ASK_POSITION:
                        switch (content) {
                            case "顶梢":
                                msgManager.addTextMsg("请上传盯梢叶片的图片。", Msg.TYPE_RECEIVE);
                                break;
                            case "基枝":
                                msgManager.addTextMsg("请上传基枝叶片的图片。", Msg.TYPE_RECEIVE);
                                break;
                            default:
                                msgManager.addTextMsg("请回答正确的部位名称（顶梢或基枝）。", Msg.TYPE_RECEIVE);
                        }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GET_PHOTO:

                try {
                    // 获取图片
                    Uri uri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));

                    // 生成一个新的图片信息，加入信息列表并显示
                    msgManager.addImgMsg(bitmap, Msg.TYPE_SEND);

                    // 对图片进行分类
                    Classifier classifier = new Classifier(this);
                    String res = classifier.classify(bitmap);

                    // 生成一个新的图片信息，加入信息列表并显示
                    msgManager.addTextMsg(res, Msg.TYPE_RECEIVE);

                } catch (NullPointerException e) {
                    Log.e(TAG, "获取数据失败");
                } catch (IOException e) {
                    Log.e(TAG, "分类器初始化失败");
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PERMISSION:
                if (grantResults.length != 2
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[1] != PackageManager.PERMISSION_GRANTED) {//权限获取失败
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }
}