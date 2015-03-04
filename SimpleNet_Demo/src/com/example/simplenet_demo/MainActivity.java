
package com.example.simplenet_demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.simple.net.base.Request.HttpMethod;
import org.simple.net.base.Request.RequestListener;
import org.simple.net.core.RequestQueue;
import org.simple.net.core.SimpleNet;
import org.simple.net.entity.MultipartEntity;
import org.simple.net.requests.MultipartRequest;
import org.simple.net.requests.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * SimpleNet简单示例
 * 
 * @author mrsimple
 */
public class MainActivity extends Activity {

    // 1、构建请求队列
    RequestQueue mQueue = SimpleNet.newRequestQueue();
    TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTv = (TextView) findViewById(R.id.result_tv);
        sendStringRequest();
    }

    /**
     * 发送GET请求,返回的是String类型的数据, 同理还有{@see JsonRequest}、{@see MultipartRequest}
     */
    private void sendStringRequest() {
        StringRequest request = new StringRequest(HttpMethod.GET, "http://www.baidu.com",
                new RequestListener<String>() {

                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        mResultTv.setText(Html.fromHtml(response));
                    }
                });

        mQueue.addRequest(request);
    }

    /**
     * 发送MultipartRequest,可以传字符串参数、文件、Bitmap等参数,这种请求为POST类型
     */
    protected void sendMultiRequest() {
        // 2、创建请求
        MultipartRequest multipartRequest = new MultipartRequest("你的url",
                new RequestListener<String>() {
                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        // 该方法执行在UI线程
                    }
                });

        // 3、添加各种参数
        // 添加header
        multipartRequest.addHeader("header-name", "value");

        // 通过MultipartEntity来设置参数
        MultipartEntity multi = multipartRequest.getMultiPartEntity();
        // 文本参数
        multi.addStringPart("location", "模拟的地理位置");
        multi.addStringPart("type", "0");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 直接从上传Bitmap
        multi.addBinaryPart("images", bitmapToBytes(bitmap));
        // 上传文件
        multi.addFilePart("imgfile", new File("storage/emulated/0/test.jpg"));

        // 4、将请求添加到队列中
        mQueue.addRequest(multipartRequest);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        mQueue.stop();
        super.onDestroy();
    }
}
