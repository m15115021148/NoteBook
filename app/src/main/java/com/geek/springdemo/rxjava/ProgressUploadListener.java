package com.geek.springdemo.rxjava;

/**
 * Created by chenMeng on 2017/6/9.
 */

public interface ProgressUploadListener {
    void onUploadProgress(long currentBytesCount, long totalBytesCount);
}
