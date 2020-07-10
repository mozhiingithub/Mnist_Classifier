package com.example.mnistclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final static int GET_PERMISSION = 123;
    private final static int GET_PHOTO = 321;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
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

        //点击图片区域，跳转至相册，选取图片
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GET_PHOTO:

                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                    imageView.setImageBitmap(bitmap);
                    Classifier classifier = new Classifier(this);
                    String res = classifier.classify(bitmap);
                    textView.setText(res);

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