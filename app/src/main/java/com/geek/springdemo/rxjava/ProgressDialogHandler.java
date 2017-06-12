package com.geek.springdemo.rxjava;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.geek.springdemo.view.RoundProgressDialog;

/**
 * Created by chenMeng on 2017/6/5.
 */

public class ProgressDialogHandler extends Handler{
    public static final int SHOW_PROGRESS_DIALOG = 1;//显示dialog
    public static final int DISMISS_PROGRESS_DIALOG = 2;//取消dialog
    public static final int SHOW_PROGRESS_PERCENTAGE = 3;//上传进度 显示百分比

    private RoundProgressDialog pd;

    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;
    private String title;//标题
    private String progress;//进度条

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }


    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable,String title) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
        this.title = title.equals("")?"加载中...":title;
    }

    private void initProgressDialog(){
        if (pd == null) {
            pd = RoundProgressDialog.createDialog(context);
            pd.setCancelable(cancelable);
            if (cancelable) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
            if (!pd.isShowing()) {
                pd.setMessage(title);
                pd.show();
            }
        }
    }

    /**
     * 上传进度百分比
     */
    private void setShowProgressPercentage(){
        if (pd == null) {
            pd = RoundProgressDialog.createDialog(context);
            pd.setCancelable(cancelable);
            if (cancelable) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
            if (!pd.isShowing()) {
                Log.e("result","progress:"+progress);
                pd.setMessage(progress);
                pd.show();
            }
        }
    }

    private void dismissProgressDialog(){
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
            case SHOW_PROGRESS_PERCENTAGE:
                setShowProgressPercentage();
                break;
        }
    }
}
