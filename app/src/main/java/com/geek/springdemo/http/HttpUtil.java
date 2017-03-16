package com.geek.springdemo.http;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Description: 封装的网络框架工具
 *
 * @author zhangfan
 * @date 2017-1-11
 */
public class HttpUtil {
    private Handler handler;// Handler对象
    //	private final String CHARSET = "GBK";// 编码与服务器端字符编码一致为GBK
    private final String CHARSET = "utf-8";// 编码与服务器端字符编码一致为utf-8
    private final int TIMEOUT = 10000;// 连接超时,毫秒
    private final int RETRYCOUNT = 2;// 重定向次数
    /**
     * 请求结果成功
     */
    public static final int SUCCESS = 102;
    /**
     * 请求结果失败
     */
    public static final int FAILURE = 101;
    /**
     * 服务器返回结果为""(返回null，为失败FAILURE)
     */
    public static final int EMPTY = 104;

    /**
     * 数据加载中,用于下载/上传，进度条更新
     */
    public static final int LOADING = 103;

    // 日志tag
    private final String LOG_TAG = "jack";

    /**
     * 构造方法
     *
     * @param handler 消息处理对象，用于请求完成后的怎么处理返回的结果数据
     */
    public HttpUtil(Handler handler) {
        this.handler = handler;
    }

    /**
     * 解决中文乱码问题，及url不规范
     *
     * @param url
     * @return
     */
    private String formatUrl(String url) {
        String old_url = "";
        try {
            old_url = URLEncoder.encode(url, CHARSET);
            // 替换地址中的特殊字符
            String new_url = old_url.replace("%3A", ":").replace("%2F", "/")
                    .replace("%3F", "?").replace("%3D", "=")
                    .replace("%26", "&").replace("%2C", ",")
                    .replace("%20", " ").replace("%2B", "+")
                    .replace("%23", "#");
            Log.i(LOG_TAG, "url: " + url);
            return new_url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 访问网络，get方式,可中断
     *
     * @param order 请求顺序，用于一个类中发起多次请求
     * @param url   请求地址
     * @return 该对象，用于取消请求
     */

    public Callback.Cancelable sendGet(final int order, String url) {

        // 发起请求
        Callback.Cancelable cancelable = x.http().get(getParams(url),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Message msg = Message.obtain();
                        Log.i(LOG_TAG, "success_result: " + result);
                        if (result == null) {
                            msg.what = FAILURE;
                        } else if ("".equals(result)||"[]".equals(result) || "null".equals(result) ) {
                            msg.what = EMPTY;
                        } else {
                            msg.what = SUCCESS;
                            msg.obj = result;

                        }
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        Message msg = Message.obtain();
                        msg.what = FAILURE;
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            // int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            // String errorResult = httpEx.getResult();
                            Log.i(LOG_TAG, "HttpException: " + responseMsg);
                        } else { // 其他错误
                            Log.i(LOG_TAG, "otherError: " + ex.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {

                    }
                });
        return cancelable;
        // cancelable.cancel(); // 取消请求
    }

    /**
     * 获取post请求参数对象
     *
     * @param url
     * @return
     */
    public RequestParams getParams(String url) {
        String newUrl = formatUrl(url);
        RequestParams params = new RequestParams(newUrl);
        params.setCharset(CHARSET);
        params.setConnectTimeout(TIMEOUT);
        params.setMaxRetryCount(RETRYCOUNT);
        return params;
    }

    /**
     * 访问网络，post方式,可中断
     */
    public Callback.Cancelable sendPost(final int order, RequestParams params) {
        // 发起请求
        Callback.Cancelable cancelable = x.http().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Message msg = Message.obtain();
                        Log.i(LOG_TAG, "success_result: " + result);
                        if (result == null) {
                            msg.what = FAILURE;
                        } else if ("".equals(result)||"[]".equals(result)|| "null".equals(result)) {
                            msg.what = EMPTY;
                        } else {
                            msg.what = SUCCESS;
                            msg.obj = result;

                        }
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        Message msg = Message.obtain();
                        msg.what = FAILURE;
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            Log.i(LOG_TAG, "HttpException: " + responseCode
                                    + " " + responseMsg + " " + errorResult);
                        } else { // 其他错误
                            Log.i(LOG_TAG, "otherError: " + ex.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {

                    }
                });
        return cancelable;
    }

    /**
     * 访问网络，get方式,进行数据缓存
     */
    public Callback.Cancelable sendCache(final int order, String url) {
        RequestParams params = getParams(url);
        // 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
        params.setCacheMaxAge(1000 * 60);
        // 发起请求
        Callback.Cancelable cancelable = x.http().get(params,
                // 使用CacheCallback, xUtils将为该请求缓存数据.
                new Callback.CacheCallback<String>() {
                    private boolean hasError = false;
                    private String result = null;

                    @Override
                    public boolean onCache(String result) {
                        // 得到缓存数据, 缓存过期后不会进入这个方法.
                        this.result = result;
                        return true; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
                    }

                    @Override
                    public void onSuccess(String result) {

                        // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                        if (result != null) {
                            this.result = result;
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        hasError = true;

                        Message msg = Message.obtain();
                        msg.what = FAILURE;
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            // int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            // String errorResult = httpEx.getResult();
                            Log.i(LOG_TAG, "HttpException: " + responseMsg);
                        } else { // 其他错误
                            Log.i(LOG_TAG, "otherError: " + ex.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                }
                    @Override
                    public void onFinished() {
                        if (!hasError ) {
                            // 成功获取数据
                            Message msg = Message.obtain();
                            Log.i(LOG_TAG, "success_result: " + result);
                            if (result == null) {
                                msg.what = FAILURE;
                            } else
                            if ("".equals(result)||"[]".equals(result)|| "null".equals(result)) {
                                msg.what = EMPTY;
                            }else{
                                msg.what = SUCCESS;
                                msg.obj = result;
                            }
                            msg.arg1 = order;
                            handler.sendMessage(msg);
                        }


                    }

                });
        return cancelable;
    }

    /**
     * 上传文件（Message中的arg1=请求顺序，what=成功、失败等状态，obj请求结果<进度条>）
     *
     * @return
     */
    public Callback.Cancelable uploadFile(final int order, RequestParams params) {
        params.setMultipart(true); // 使用multipart表单上传文件
        // 发起请求
        Callback.Cancelable cancelable = x.http().post(params,
                new Callback.ProgressCallback<String>() {

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.i(LOG_TAG, "onCancelled");
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.i(LOG_TAG, "onError: " + ex.getMessage() + " " + ex.getStackTrace());
                        Message msg = handler.obtainMessage();
                        msg.what = FAILURE;
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFinished() {
                        Log.i(LOG_TAG, "onFinished: ");
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isDownloading) {
                        Log.i(LOG_TAG, "onLoading: " + total + " " + current);
                        Message msg = handler.obtainMessage();
                        msg.what = LOADING;
                        msg.arg1 = order;
                        msg.arg2 = (int) ((current * 1.0 / total) * 100);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onStarted() {
                        Log.i(LOG_TAG, "onStarted: ");
                        Message msg = handler.obtainMessage();
                        msg.what = LOADING;
                        msg.arg1 = order;
                        msg.arg2 = 0;// 上传进度0%
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onWaiting() {
                        Log.i(LOG_TAG, "onWaiting: ");
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.i(LOG_TAG, "onSuccess: " + result);
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        msg.arg1 = order;
                        msg.arg2 = 100;// 上传进度100%
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                });
        return cancelable;
    }

    /**
     * 下载文件
     *
     * @param order    请求顺序
     * @param url      下载地址
     * @param savePath 设置下载文件时文件保存的路径和文件名
     * @return
     */

    public Callback.Cancelable downloadFile(final int order, String url, String savePath) {
        RequestParams params = new RequestParams(url);
        params.setAutoResume(true);//断点下载
        params.setAutoRename(true);//自动重命名
        params.setSaveFilePath(savePath);
        params.setCancelFast(true);//是否立即中断

        // 发起请求
        Callback.Cancelable cancelable = x.http().get(params,
                new Callback.ProgressCallback<File>() {

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.i(LOG_TAG, "onError: " + ex.getMessage());
                        Message msg = handler.obtainMessage();
                        msg.what = FAILURE;
                        msg.arg1 = order;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFinished() {
                        Log.i(LOG_TAG, "onFinished: ");
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isDownloading) {
                        Log.i(LOG_TAG, "onLoading: " + total + " " + current);
                        Message msg = handler.obtainMessage();
                        msg.what = LOADING;
                        msg.arg1 = order;
                        msg.arg2 = (int) ((current * 1.0 / total) * 100);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onStarted() {
                        Log.i(LOG_TAG, "onStarted: ");
                        Message msg = handler.obtainMessage();
                        msg.what = LOADING;
                        msg.arg1 = order;
                        msg.arg2 = 0;// 进度0%
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onWaiting() {
                        Log.i(LOG_TAG, "onWaiting: ");
                    }

                    @Override
                    public void onSuccess(File result) {
                        Log.i(LOG_TAG, "onSuccess: ");
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        msg.arg1 = order;
                        msg.arg2 = 100;// 进度100%
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }

                });
        return cancelable;
    }
}
