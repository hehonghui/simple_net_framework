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

package org.simple.net.httpstacks;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.simple.net.base.Request;
import org.simple.net.base.Response;
import org.simple.net.config.HttpClientConfig;

import java.util.Map;

/**
 * api 9以下使用HttpClient执行网络请求, https配置参考http://jackyrong.iteye.com/blog/1606444
 * 
 * @author mrsimple
 */
public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig mConfig = HttpClientConfig.getConfig();
    /**
     * HttpClient
     */
    HttpClient mHttpClient = AndroidHttpClient.newInstance(mConfig.userAgent);

    @Override
    public Response performRequest(Request<?> request) {
        try {
            HttpUriRequest httpRequest = createHttpRequest(request);
            // 添加连接参数
            setConnectionParams(httpRequest);
            // 添加header
            addHeaders(httpRequest, request.getHeaders());
            // https配置
            configHttps(request);
            // 执行请求
            HttpResponse response = mHttpClient.execute(httpRequest);
            // 构建Response
            Response rawResponse = new Response(response.getStatusLine());
            // 设置Entity
            rawResponse.setEntity(response.getEntity());
            return rawResponse;
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 如果是https请求,则使用用户配置的SSLSocketFactory进行配置.
     * 
     * @param request
     */
    private void configHttps(Request<?> request) {
        SSLSocketFactory sslSocketFactory = mConfig.getSocketFactory();
        if (request.isHttps() && sslSocketFactory != null) {
            Scheme sch = new Scheme("https", sslSocketFactory, 443);
            mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
    }

    /**
     * 设置连接参数,这里比较简单啊.一些优化设置就没有写了.
     * 
     * @param httpUriRequest
     */
    private void setConnectionParams(HttpUriRequest httpUriRequest) {
        HttpParams httpParams = httpUriRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, mConfig.connTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, mConfig.soTimeOut);
    }

    /**
     * 根据请求类型创建不同的Http请求
     * 
     * @param request
     * @return
     */
    static HttpUriRequest createHttpRequest(Request<?> request) {
        HttpUriRequest httpUriRequest = null;
        switch (request.getHttpMethod()) {
            case GET:
                httpUriRequest = new HttpGet(request.getUrl());
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(request.getUrl());
                break;
            case POST: {
                httpUriRequest = new HttpPost(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
            }
                break;
            case PUT: {
                httpUriRequest = new HttpPut(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPut) httpUriRequest, request);
            }
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }

        return httpUriRequest;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    /**
     * 将请求参数设置到HttpEntity中
     * 
     * @param httpRequest
     * @param request
     */
    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
            Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }
}
