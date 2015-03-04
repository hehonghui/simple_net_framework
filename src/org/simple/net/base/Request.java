/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 bboyfeiyu@gmail.com, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.simple.net.base;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类. 注意GET和DELETE不能传递参数,因为其请求的性质所致,用户可以将参数构建到url后传递进来到Request中.
 * 
 * @author mrsimple
 * @param <T> T为请求返回的数据类型
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
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    /**
     * Default Content-type
     */
    public final static String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * 请求序列号
     */
    protected int mSerialNum = 0;
    /**
     * 优先级默认设置为Normal
     */
    protected Priority mPriority = Priority.NORMAL;
    /**
     * 是否取消该请求
     */
    protected boolean isCancel = false;

    /** 该请求是否应该缓存 */
    private boolean mShouldCache = true;
    /**
     * 请求Listener
     */
    protected RequestListener<T> mRequestListener;
    /**
     * 请求的url
     */
    private String mUrl = "";
    /**
     * 请求的方法
     */
    HttpMethod mHttpMethod = HttpMethod.GET;

    /**
     * 请求的header
     */
    private Map<String, String> mHeaders = new HashMap<String, String>();
    /**
     * 请求参数
     */
    private Map<String, String> mBodyParams = new HashMap<String, String>();

    /**
     * @param method
     * @param url
     * @param listener
     */
    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }

    public void addHeader(String name, String value) {
        mHeaders.put(name, value);
    }

    /**
     * 从原生的网络请求中解析结果
     * 
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);

    /**
     * 处理Response,该方法运行在UI线程.
     * 
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (mRequestListener != null) {
            int stCode = response != null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unkown error";
            Log.e("", "### 执行回调 : stCode = " + stCode + ", result : " + result + ", err : " + msg);
            mRequestListener.onComplete(stCode, result, msg);
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
        return mBodyParams;
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
        return myPriority.equals(anotherPriority) ? this.getSerialNumber()
                - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mShouldCache ? 1231 : 1237);
        result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request<?> other = (Request<?>) obj;
        if (mHeaders == null) {
            if (other.mHeaders != null)
                return false;
        } else if (!mHeaders.equals(other.mHeaders))
            return false;
        if (mHttpMethod != other.mHttpMethod)
            return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mShouldCache != other.mShouldCache)
            return false;
        if (mUrl == null) {
            if (other.mUrl != null)
                return false;
        } else if (!mUrl.equals(other.mUrl))
            return false;
        return true;
    }

    /**
     * 网络请求Listener
     * 
     * @author mrsimple
     * @param <T> 请求的response类型
     */
    public static interface RequestListener<T> {
        /**
         * 请求完成的回调
         * 
         * @param response
         */
        public void onComplete(int stCode, T response, String errMsg);
    }
}
