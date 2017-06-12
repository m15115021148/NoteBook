package com.geek.springdemo.rxjava;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上传 进度条 添加拦截器
 * Created by chenMeng on 2017/6/9.
 */

public class UpLoadProgressInterceptor implements Interceptor {
    private ProgressUploadListener mListener;

    public UpLoadProgressInterceptor(ProgressUploadListener uploadListener) {
        this.mListener = uploadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(null == request.body()){
            return chain.proceed(request);
        }

        Request build = request.newBuilder()
                .method(request.method(),
                        new ProgressRequestBody(request.body(),
                                mListener))
                .build();
        return chain.proceed(build);
    }
}
