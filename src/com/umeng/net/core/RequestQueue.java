/**
 *
 *	created by Mr.Simple, Dec 20, 201412:41:58 PM.
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

import android.util.Log;

import com.umeng.net.base.Request;
import com.umeng.net.httpstacks.HttpStackFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列
 * 
 * @author mrsimple
 */
public final class RequestQueue {
    /**
     * 请求队列 [ Thread-safe ]
     */
    private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<Request<?>>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

    /**
     * CPU核心数 + 1个分发线程数
     */
    private int mDispatcherNums = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * 
     */
    private NetworkDispatcher[] mDispatchers = null;

    /**
     * private constructor
     */
    private RequestQueue() {
    }

    /**
     * @return
     */
    public static RequestQueue newRequestQueue() {
        RequestQueue queue = new RequestQueue();
        queue.start();
        return queue;
    }

    /**
     * 
     */
    private final void startNetworkDispatchers() {
        Log.d("", "### dispatcher nums : " + mDispatcherNums);
        //
        mDispatchers = new NetworkDispatcher[mDispatcherNums];
        for (int i = 0; i < mDispatcherNums; i++) {
            mDispatchers[i] = new NetworkDispatcher(mRequestQueue,
                    HttpStackFactory.createHttpStack());
            mDispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startNetworkDispatchers();
    }

    public void stop() {
        if (mDispatchers != null && mDispatchers.length > 0) {
            for (int i = 0; i < mDispatchers.length; i++) {
                mDispatchers[i].quit();
            }
        }
    }

    public void addRequest(Request<?> request) {
        request.setSerialNumber(this.generateSerialNumber());
        mRequestQueue.add(request);
    }

    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
}
