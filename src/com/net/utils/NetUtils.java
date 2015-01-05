/**
 *
 *	created by Mr.Simple, Dec 20, 20141:52:32 PM.
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

package com.net.utils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public abstract class NetUtils {
    /**
     * @param inputStream
     * @return
     */
    public static String streamToString(InputStream inputStream) {
        BufferedInputStream bStream = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[inputStream.available()];
            bStream.read(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(bStream);
            closeSilently(inputStream);
        }

        return new String(buffer);
    }

    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }
}
