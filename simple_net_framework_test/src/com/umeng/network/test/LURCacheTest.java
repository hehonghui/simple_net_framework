/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
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

package com.umeng.network.test;

import android.support.v4.util.LruCache;
import android.test.AndroidTestCase;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.simple.net.base.Request;
import org.simple.net.base.Response;
import org.simple.net.base.Request.HttpMethod;
import org.simple.net.requests.StringRequest;

public class LURCacheTest extends AndroidTestCase {

    /**
     * Reponse缓存
     */
    private LruCache<Request<?>, Response> mResponseCache;

    protected void setUp() throws Exception {
        super.setUp();
        initCache();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void initCache() {
        mResponseCache = new LruCache<Request<?>, Response>(108 * 1024);
    }

    public void testMultiRequestCache() {

        for (int i = 0; i < 20; i++) {
            StringRequest request = new StringRequest(HttpMethod.GET, "http://url" + i, null);
            request.getParams().put("key-" + i, "value-1");
            request.getHeaders().put("header-" + i, "header-" + i);
            Response response = new Response(mStatusLine);
            mResponseCache.put(request, response);
        }

        assertEquals(20, mResponseCache.size());

    }

    public void testMultiRequestWithSame() {

        for (int i = 0; i < 20; i++) {
            StringRequest request = new StringRequest(HttpMethod.GET, "http://url", null);
            request.getParams().put("key-1", "value-1");
            request.getHeaders().put("header-1", "header-1");
            Response response = new Response(mStatusLine);
            mResponseCache.put(request, response);
        }

        assertEquals(1, mResponseCache.size());

    }

    StatusLine mStatusLine = new StatusLine() {

        @Override
        public int getStatusCode() {
            return 0;
        }

        @Override
        public String getReasonPhrase() {
            return "msg";
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return new ProtocolVersion("http", 1, 1);
        }
    };

}
