package com.geek.springdemo.rxjava;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.geek.springdemo.config.ApiConfig;
import com.geek.springdemo.config.WebHostConfig;
import com.geek.springdemo.config.WebsConfig;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chenMeng on 2017/6/5.
 */

public class RetrofitUtil implements WebsConfig{

    public static final int DEFAULT_TIMEOUT = 5;//设置超时时间
    private Retrofit mRetrofit;
    private static RetrofitUtil mInstance;
    private ApiConfig mApi;
    private ApiConfig mApi1;

    /**
     * 私有构造方法
     */
    private RetrofitUtil(){
        OkHttpClient builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(sLoggingInterceptor)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(builder)
                .baseUrl(WebHostConfig.getHostName())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApi = mRetrofit.create(ApiConfig.class);
    }

    /**
     * 单例模式
     * @return
     */
    public static RetrofitUtil getInstance(){
        if (mInstance == null){
            synchronized (RetrofitUtil.class){
                mInstance = new RetrofitUtil();
            }
        }
        return mInstance;
    }

    /**
     * 打印返回的json数据拦截器
     */
    private static Interceptor sLoggingInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Buffer requestBuffer = new Buffer();
            if (request.body() != null) {
                request.body().writeTo(requestBuffer);
            } else {
//                Log.d("jack", "request.body() == null");
            }
            //打印url信息
            Log.w("jack",request.url() + (request.body() != null ? getParseParams(request.body(), requestBuffer) : ""));
            Response response = chain.proceed(request);
            return response;
        }
    };

    @NonNull
    private static String getParseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "";
    }

    /**
     * 注册
     * @param name
     * @param psw
     */
    public void register(String name, String psw, Subscriber<ResultModel> subscriber){
        mApi.register(name, psw)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 登录
     * @param name
     * @param psw
     * @param subscriber
     */
    public void login(String name, String psw, Subscriber<UserModel> subscriber){
        mApi.login(name, psw)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 得到保存的记账信息
     */
    @Override
    public void getAccountList(String userID, String type, String kind, String startTime, String endTime, String page, Subscriber<List<AccountsModel>> subscriber) {
        mApi.getAccountsList(userID, type, kind, startTime, endTime, page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 修改账单备注信息
     * @param accountID
     * @param userID
     * @param note
     * @param subscriber
     */
    @Override
    public void updateAccountNote(String accountID, String userID, String note, Subscriber<ResultModel> subscriber) {
        mApi.updateAccountNote(accountID, userID, note)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 上传头像
     * @param userID
     * @param img
     * @param subscriber
     */
    @Override
    public void uploadHeader(String userID, String img,Subscriber<ResultModel> subscriber,ProgressUploadListener listener) {
        UpLoadProgressInterceptor uploadInterceptor = new UpLoadProgressInterceptor(listener);
        OkHttpClient builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(sLoggingInterceptor)
                .addInterceptor(uploadInterceptor)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(builder)
                .baseUrl(WebHostConfig.getHostName())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApi1 = mRetrofit.create(ApiConfig.class);

        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), userID);
        File file = new File(img);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("img", file.getName(), requestFile);

        mApi1.uploadHeader(body, uid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
