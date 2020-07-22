package com.example.mnistclassifier;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Classifier {
    private final static String TAG = "Classifier";
    private final static String modelPath = "converted_model.tflite";//模型文件名
    private Interpreter model;//模型解释器，通过tflite文件生成Java运行的结构。
    private final static int size = 28;//Mnist图片的规格为28*28
    private final static int float_byte_size = 4;//浮点型字节数
    private final static int img_byte_size = size * size * float_byte_size;//图片按字节存储大小
    private float[][] probability = new float[1][10];//概率分布

    //Classifier的构造函数
    //传参为activity，读取其中assets的tflite模型文件，从而构造model解释器
    //第一行代码的openFd方法要求构造函数必须检测IO错误，所以必须加上“throws IOException”
    public Classifier(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);//生成一个tflite文件的描述器。
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());//根据描述器，生成模型文件输入流
        FileChannel fileChannel = inputStream.getChannel();//由输入流获取通道
        ByteBuffer byteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength()
        );//根据描述器对文件起始位置、文件长度的信息，使用通道将文件映射到ByteBuffer中
        model = new Interpreter(byteBuffer);//解释器对buffer中的内容进行解析
    }

    //分类方法，传参为格式为Bitmap的图片信息。
    public int classify(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);//将任意规格的图像缩放为28*28
        ByteBuffer byteBuffer = bitMap2ByteBuffer(bitmap);//将Bitmap格式的图片转成模型所需的ByteBuffer格式
        model.run(byteBuffer, probability);//运行模型，获取概率分布
        int max_idx = 0;
        for (int i = 0; i < 10; i++) { // 找到置信度最高的数字
            if (probability[0][i] >= probability[0][max_idx]) {
                max_idx = i;
            }
        }
        return max_idx;
    }

    // 将Bitmap格式的图片转成模型所需的ByteBuffer格式
    private ByteBuffer bitMap2ByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(img_byte_size);//分配buffer空间
        byteBuffer.order(ByteOrder.nativeOrder());//设置buffer字节序为当前硬件平台的字节序
        int[] values = new int[size * size];//像素值数组，每个像素值包含像素点的RGB三者数值
        bitmap.getPixels(values, 0, size, 0, 0, size, size);//将bitmap中的像素值挪至values数组
        for (int value : values) {//遍历像素值数组，将数值写入buffer
            //value共32位，色深为8位的情况下，value的前24位依次显示R、G、B三通道的色深，即：1~8为R，9~16为G，17~24为B
            // 下方的0xFF，即 0000 0000 1111 1111，与0xFF作与运算，等价于只保留最低八位
            int r = (value >> 16) & 0xFF;//17~24为B，因此将value右移16位，再取最低八位
            int g = (value >> 8) & 0xFF;//9~16为G，因此将value右移8位，再取最低八位
            int b = value & 0xFF;//1~8位B，因此直接取value的最低八位
            float norm_value = (r + g + b) / 3.0f / 255.0f;//取三通道平均值以灰度化，再除以255以归一化
            byteBuffer.putFloat(norm_value);//将归一化后的灰度值写入buffer
        }
        return byteBuffer;
    }


}
