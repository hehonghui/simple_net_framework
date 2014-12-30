
package com.umeng.net.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * TODO : no ok, 还有待封装: StringBody, ByteArrayBody, FileBody. see :
 * http://stackoverflow
 * .com/questions/16797468/how-to-send-a-multipart-form-data-
 * post-in-android-with-volley
 * 
 * @author mrsimple
 */
public class MultipartEntity implements HttpEntity {
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();

    private String boundary = null;

    ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
    boolean isSetLast = false;
    boolean isSetFirst = false;

    public MultipartEntity() {
        this.boundary = generateBoundary();
    }

    private String generateBoundary() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }

    public void writeFirstBoundaryIfNeeds() {
        try {
            mByteArrayOutputStream.write(("--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void addStringPart(final String key, final String value) {
        addRawPart(key, value.getBytes(), "Content-Type: text/plain; charset=UTF-8");
    }

    private void addRawPart(String key, byte[] rawData, String type) {
        writeFirstBoundaryIfNeeds();
        try {
            mByteArrayOutputStream.write((type + "\r\n").getBytes());
            mByteArrayOutputStream
                    .write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n")
                            .getBytes());
            mByteArrayOutputStream.write(rawData);
            // 换行
            mByteArrayOutputStream.write(("\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void addBinaryPart(String key, final byte[] rawData) {
        // writeFirstBoundaryIfNeeds();
        // try {
        // mByteArrayOutputStream.write("application/octet-stream\r\n".getBytes());
        // mByteArrayOutputStream
        // .write(("Content-Disposition: form-data; name=\"" + key +
        // "\"\r\n\r\n")
        // .getBytes());
        // mByteArrayOutputStream.write(rawData);
        // // 换行
        // mByteArrayOutputStream.write(("\r\n").getBytes());
        // } catch (final IOException e) {
        // e.printStackTrace();
        // }

        writeFirstBoundaryIfNeeds();
        try {
            mByteArrayOutputStream.write(("Content-Type: application/octet-stream" + "\r\n").getBytes());
            
            mByteArrayOutputStream.write(("Content-Disposition: form-data; name=\"" + key
                    + "\"; filename=\""
                    + "tempfile" + "\"\r\n").getBytes());
            mByteArrayOutputStream.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            mByteArrayOutputStream.write(rawData);
            // 换行
            mByteArrayOutputStream.write(("\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void addPart(final String key, final File file) {

        InputStream fin = null;
        writeFirstBoundaryIfNeeds();
        try {
            fin = new FileInputStream(file);
            String type = "Content-Type: application/octet-stream" + "\r\n";
            mByteArrayOutputStream.write(("Content-Disposition: form-data; name=\"" + key
                    + "\"; filename=\""
                    + file.getName() + "\"\r\n").getBytes());
            mByteArrayOutputStream.write(type.getBytes());
            mByteArrayOutputStream.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                mByteArrayOutputStream.write(tmp, 0, l);
            }
            mByteArrayOutputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getContentLength() {
        // writeLastBoundaryIfNeeds();
        return mByteArrayOutputStream.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        final String endString = "--" + boundary + "--\r\n";
        // 写入结束符
        mByteArrayOutputStream.write(endString.getBytes());
        //
        outstream.write(mByteArrayOutputStream.toByteArray());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(mByteArrayOutputStream.toByteArray());
    }
}
