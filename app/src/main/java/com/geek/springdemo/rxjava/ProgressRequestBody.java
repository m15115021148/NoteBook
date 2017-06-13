package com.geek.springdemo.rxjava;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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

        //当前写入字节数
        private long writtenBytesCount = 0L;
        //总字节长度，避免多次调用contentLength()方法
        private long totalBytesCount = 0L;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            writtenBytesCount += byteCount;
            //获得contentLength的值，后续不再调用
            if (totalBytesCount == 0) {
                totalBytesCount = contentLength();
            }
            Observable.just(writtenBytesCount)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            String progress = (int) ((writtenBytesCount*1.0)/totalBytesCount*100)+"%";
                            mListener.onUploadProgress(progress);
                        }
            });
        }
    }
}
