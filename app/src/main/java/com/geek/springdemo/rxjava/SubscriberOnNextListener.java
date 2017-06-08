package com.geek.springdemo.rxjava;

/**
 * Created by chenMeng on 2017/6/5.
 */

public interface SubscriberOnNextListener<T> {
    void onNext(T t,int requestCode);
    void onError(Throwable e);
}
