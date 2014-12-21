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

package com.umeng.net.core;

import com.umeng.net.base.RawResponse;
import com.umeng.net.base.Request;
import com.umeng.net.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 * @author mrsimple
 */
public final class NetworkDispatcher extends Thread {

    /**
     * 
     */
    BlockingQueue<Request<?>> mRequestQueue;
    /**
     * 
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
                final RawResponse response = mHttpStack.performRequest(cureentRequest);
                // delivery response
                mResponseDelivery.deliveryResponse(cureentRequest, response);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
