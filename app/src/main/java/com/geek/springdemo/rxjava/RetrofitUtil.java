package com.geek.springdemo.rxjava;

import android.support.annotation.NonNull;
import android.util.Log;

import com.geek.springdemo.config.ApiConfig;
import com.geek.springdemo.config.WebHostConfig;
import com.geek.springdemo.config.WebsConfig;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.LineModel;
import com.geek.springdemo.model.PieModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
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
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chenMeng on 2017/6/5.
 */

public class RetrofitUtil implements WebsConfig{

    public static final int DEFAULT_TIMEOUT = 5;//设置超时时间
    private static RetrofitUtil mInstance;//无进度条
    private static RetrofitUtil mInstanceProgress;//有进度显示
    private ApiConfig mApi;

    /**
     * 私有构造方法
     */
    private RetrofitUtil(){
        mApi = createManager(false,null);
    }

    /**
     * 私有构造方法
     */
    private RetrofitUtil(ProgressUploadListener listener){
        mApi = createManager(true,listener);
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
     * 单例模式
     * @param listener    显示百分比进度
     * @return
     */
    public static RetrofitUtil getInstance(ProgressUploadListener listener){
        if (mInstanceProgress == null){
            synchronized (RetrofitUtil.class){
                mInstanceProgress = new RetrofitUtil(listener);
            }
        }
        return mInstanceProgress;
    }

    /**
     * 配置请求属性
     * @param isShowProgress
     * @param listener
     */
    public ApiConfig createManager(boolean isShowProgress,ProgressUploadListener listener){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(sLoggingInterceptor);
        if (isShowProgress && listener != null){
            UpLoadProgressInterceptor uploadInterceptor = new UpLoadProgressInterceptor(listener);
            builder.addInterceptor(uploadInterceptor);
        }
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(WebHostConfig.getHostName())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(ApiConfig.class);
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
            Log.w("jack",URLDecoder.decode(request.url().toString(), "utf-8"));
//            Log.w("jack",request.url() + (request.body() != null ? getParseParams(request.body(), requestBuffer) : ""));

            //打印得到的数据
            Response response = chain.proceed(request);
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Log.d("jack",buffer.clone().readString(Charset.forName("utf-8")));
            return response;
        }
    };

    @NonNull
    private static String getParseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.clone().readUtf8(), "UTF-8");
        }
        return "";
    }

    /**
     * 注册
     * @param name
     * @param psw
     */
    @Override
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
    @Override
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
    public void getAccountList(String userID, String type, String kind, String startTime, String endTime,String note, String page, Subscriber<List<AccountsModel>> subscriber) {
        mApi.getAccountsList(userID, type, kind, startTime, endTime, note,page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<List<AccountsModel>, List<AccountsModel>>() {
                    @Override
                    public List<AccountsModel> call(List<AccountsModel> accountsModels) {
                        return accountsModels;
                    }
                })
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
    public void uploadHeader(String userID, String img,Subscriber<ResultModel> subscriber) {
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), userID);
        File file = new File(img);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("img", file.getName(), requestFile);

        mApi.uploadHeader(body, uid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 上传账单系信息
     */
    @Override
    public void uploadAccount(String userID, String type, String kind, String money, String note, String time, String lat, String lng, String address, Subscriber<ResultModel> subscriber) {
        mApi.uploadAccount(userID, type, kind, money, note, time, lat, lng, address)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 得到类型列表
     * @param subscriber
     */
    @Override
    public void getKinds(Subscriber<List<KindModel>> subscriber) {
        mApi.getKinds()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 得到统计图
     * @param subscriber
     */
    @Override
    public void getPieData(String userID, String type, String kind, String startTime, String endTime, String note,Subscriber<List<PieModel>> subscriber) {
        mApi.getPieData(userID, type, kind, startTime, endTime,note)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 得到统计图
     * @param subscriber
     */
    @Override
    public void getLineData(String userID, String type, String kind, String startTime, String endTime, String note,Subscriber<List<LineModel>> subscriber) {
        mApi.getLineData(userID, type, kind, startTime, endTime,note)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
