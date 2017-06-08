package com.geek.springdemo.rxjava;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.geek.springdemo.config.RequestCode;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by chenMeng on 2017/6/5.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {
    private SubscriberOnNextListener<T> mListener;
    private ProgressDialogHandler mHandler;
    private int mRequestCode;

    public ProgressSubscriber(SubscriberOnNextListener<T> listener,
                              Context context,String title,int requestCode){
        this.mListener = listener;
        this.mRequestCode = requestCode;
        mHandler = new ProgressDialogHandler(context,this,true,title);
    }

    public ProgressSubscriber(SubscriberOnNextListener<T> listener,
                              Context context,int requestCode){
        this.mListener = listener;
        this.mRequestCode = requestCode;
        mHandler = new ProgressDialogHandler(context,this,true,"");
    }

    private void showProgressDialog(){
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG);
            msg.arg1 = mRequestCode;
            mHandler.sendMessage(msg);
        }
    }

    private void dismissProgressDialog(){
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG);
            msg.arg1 = mRequestCode;
            mHandler.sendMessage(msg);
            mHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        Log.d("result","completed:"+"获取数据完成！");
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Log.d("jack","onError:"+ RequestCode.ERRORINFO);
        } else if (e instanceof ConnectException) {
            Log.d("jack","onError:"+ RequestCode.NOLOGIN);
        } else {
            Log.d("jack","onError:"+ e.getMessage());
        }
        if (mListener != null){
            mListener.onError(e);
        }
        dismissProgressDialog();
    }

    @Override
    public void onNext(T t) {
        if (mListener != null){
            Log.d("jack","result:"+ JSON.toJSONString(t));
            mListener.onNext(t,mRequestCode);
        }
    }

    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()){
            this.unsubscribe();
        }
    }
}
