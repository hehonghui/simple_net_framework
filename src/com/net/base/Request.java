/**
 *
 *	created by Mr.Simple, Dec 20, 201412:35:23 PM.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类,T为请求返回的数据类型. 注意GET和DELETE不能传递参数,因为其请求的性质所致,用户可以将参数构建到url后传递进来到Request中.
 * 
 * @author mrsimple
 * @param <T>
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * http request method enum.
     * 
     * @author mrsimple
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        /** http request type */
        private String mHttpMethod = "";

        private HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举
     * 
     * @author mrsimple
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    /**
     * Default encoding for POST or PUT parameters. See
     * {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    protected int mSerialNum = 0;
    /**
     * 优先级默认设置为Normal
     */
    protected Priority mPriority = Priority.NORMAL;

    protected boolean isCancel = false;

    /** 该请求是否应该缓存 */
    private boolean mShouldCache = true;

    protected RequestListener<T> mRequestListener;

    private String mUrl = "";

    HttpMethod mHttpMethod = HttpMethod.GET;

    private Map<String, String> mHeaders = new HashMap<String, String>();

    private Map<String, String> mParams = new HashMap<String, String>();

    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        this(method, url, new HashMap<String, String>(), listener);
    }

    public Request(HttpMethod method, String url, Map<String, String> params,
            RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
        mParams = params;
    }

    /**
     * 从原生的网络请求中解析结果
     * 
     * @param response
     * @return
     */
    // public abstract Response<T> parseResponse(RawResponse response);

    public abstract T parseResponse(Response response);

    /**
     * 处理结果
     * 
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (mRequestListener != null && response != null) {
            mRequestListener.onComplete(response.getStatusCode(), result, response.getMessage());
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public RequestListener<T> getRequestListener() {
        return mRequestListener;
    }

    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public boolean isHttps() {
        return mUrl.startsWith("https");
    }

    /**
     * 该请求是否应该缓存
     * 
     * @param shouldCache
     */
    public void setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }

    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded
     * encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(another) ? this.getSerialNumber() - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }

    /**
     * 网络请求Listener
     * 
     * @author mrsimple
     * @param <T> 请求的response类型
     */
    public static interface RequestListener<T> {
        public void onStart();

        /**
         * 请求完成的回调
         * 
         * @param response
         */
        public void onComplete(int stCode, T response, String errMsg);
    }
}
