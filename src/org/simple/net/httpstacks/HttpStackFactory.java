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

import android.os.Build;

/**
 * 根据api版本选择HttpClient或者HttpURLConnection
 * 
 * @author mrsimple
 */
public final class HttpStackFactory {

    private static final int GINGERBREAD_SDK_NUM = 9;

    /**
     * 根据SDK版本号来创建不同的Http执行器,即SDK 9之前使用HttpClient,之后则使用HttlUrlConnection,
     * 两者之间的差别请参考 :
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     * 
     * @return
     */
    public static HttpStack createHttpStack() {
        int runtimeSDKApi = Build.VERSION.SDK_INT;
        if (runtimeSDKApi >= GINGERBREAD_SDK_NUM) {
            return new HttpUrlConnStack();
        }

        return new HttpClientStack();
    }
}
