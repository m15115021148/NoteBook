package com.geek.springdemo.rxjava;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 上传进度条
 * Created by chenMeng on 2017/6/9.
 */
public class ProgressRequestBody extends RequestBody {
    private ProgressUploadListener mListener;
    private RequestBody mRequestBody;
    private CountingSink mCountingSink;

    public ProgressRequestBody(RequestBody requestBody,ProgressUploadListener listener){
        this.mRequestBody = requestBody;
        this.mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        mCountingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(mCountingSink);

        mRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            String progress = (int) ((bytesWritten*1.0)/contentLength()*100)+"%";
            mListener.onUploadProgress(progress);
        }
    }
}
