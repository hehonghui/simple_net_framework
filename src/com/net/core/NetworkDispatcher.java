/**
 *
 *	created by Mr.Simple, Dec 20, 201412:37:50 PM.
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

package com.net.core;

import android.util.Log;

import com.net.base.Request;
import com.net.base.Response;
import com.net.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 * @author mrsimple
 */
public final class NetworkDispatcher extends Thread {

    /**
     * 网络请求队列
     */
    BlockingQueue<Request<?>> mRequestQueue;
    /**
     * 网络请求栈
     */
    HttpStack mHttpStack;
    /**
     * 结果分发器,将结果投递到主线程
     */
    ResponseDelivery mResponseDelivery = new ResponseDelivery();
    /**
     * 
     */
    private boolean isStop = false;

    public NetworkDispatcher(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        mRequestQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                final Request<?> cureentRequest = mRequestQueue.take();
                //
                mResponseDelivery.requestStart(cureentRequest);
                // execute request and parse raw response
                final Response response = mHttpStack.performRequest(cureentRequest);
                // delivery response
                mResponseDelivery.deliveryResponse(cureentRequest, response);
            }
        } catch (InterruptedException e) {
            Log.i("", "### 请求分发器退出");
        }

    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
