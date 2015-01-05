/**
 *
 *	created by Mr.Simple, Dec 20, 201412:39:25 PM.
 *	Copyright (c) 2014, hehonghui@umeng.com All Rights Reserved.
 *
 *                #####################################################
 *                #                                                   #
 *                #                       _oo0oo_                     #   
 *                #                      o8888888o                    #
 *                #                      88" . "88                    #
 *                #                      (| -_- |)                    #
 *                #                      0\  =  /0                    #   
 *                #                    ___/`---'\___                  #
 *                #                  .' \\|     |# '.                 #
 *                #                 / \\|||  :  |||# \                #
 *                #                / _||||| -:- |||||- \              #
 *                #               |   | \\\  -  #/ |   |              #
 *                #               | \_|  ''\---/''  |_/ |             #
 *                #               \  .-\__  '-'  ___/-. /             #
 *                #             ___'. .'  /--.--\  `. .'___           #
 *                #          ."" '<  `.___\_<|>_/___.' >' "".         #
 *                #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 *                #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 *                #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 *                #                       `=---='                     #
 *                #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 *                #                                                   #
 *                #               佛祖保佑         永无BUG              #
 *                #                                                   #
 *                #####################################################
 */

package com.net.base;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author mrsimple
 */
public class Response extends BasicHttpResponse {

    public byte[] rawData = new byte[0];

    public Response(StatusLine statusLine) {
        super(statusLine);
    }

    public Response(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    @Override
    public void setEntity(HttpEntity entity) {
        super.setEntity(entity);
        rawData = entityToBytes(getEntity());
    }

    public byte[] getRawData() {
        return rawData;
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return getStatusLine().getReasonPhrase();
    }

    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] oneKB = new byte[1024];
        int length = 0;
        while (-1 != (length = inputStream.read(oneKB))) {
            bos.write(oneKB, 0, length);
        }
        return bos.toByteArray();
    }

    /** Reads the contents of HttpEntity into a byte[]. */
    private byte[] entityToBytes(HttpEntity entity) {
        byte[] arrayData = new byte[0];
        try {
            InputStream inputStream = entity.getContent();

            return inputStreamToByteArray(inputStream);
            // BufferedReader in = new BufferedReader(
            // new InputStreamReader(inputStream));
            // String decodedString;
            // while ((decodedString = in.readLine()) != null) {
            // Log.d("", decodedString);
            // }
            // in.close();
            //
            // DataInputStream dataInputStream = new
            // DataInputStream(inputStream);
            // arrayData = new byte[inputStream.available()];
            // Log.d("", "### 字节数量 : " + inputStream.available());
            // dataInputStream.read(arrayData, 0, arrayData.length);
            // dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayData;
    }

}
