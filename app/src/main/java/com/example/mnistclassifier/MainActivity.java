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
    private final static int GET_PERMISSION = 123;
    private final static int GET_PHOTO = 321;
    private EditText editText;
    private Button button;
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private List<Msg> msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        editText = (EditText) findViewById(R.id.input_text);
        button = (Button) findViewById(R.id.send_button);
        msgList = new ArrayList<>();
        msgAdapter = new MsgAdapter(msgList);
        recyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(msgAdapter);


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
                    Msg sendImg = new Msg(bitmap, Msg.TYPE_SEND);
                    msgList.add(sendImg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);

                    // 对图片进行分类
                    Classifier classifier = new Classifier(this);
                    String res = classifier.classify(bitmap);

                    // 生成一个新的图片信息，加入信息列表并显示
                    Msg receiveText = new Msg(res, Msg.TYPE_RECEIVE);
                    msgList.add(receiveText);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);

//                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
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