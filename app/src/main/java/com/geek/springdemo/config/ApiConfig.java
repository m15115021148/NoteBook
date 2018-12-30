package com.geek.springdemo.config;


import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.LineModel;
import com.geek.springdemo.model.PieModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chenMeng on 2017/6/1.
 */

public interface ApiConfig {
    @POST("dbAction_register.do")
    Observable<ResultModel> register(@Query("name") String name, @Query("password") String password);

    @POST("dbAction_login.do")
    Observable<UserModel> login(@Query("name") String name, @Query("password") String password);

//    @FormUrlEncoded
//    @POST("dbAction_login.do?")
//    Observable<UserModel> login(@Field("name") String name, @Field("password") String password);

    /**
     *  得到保存的记账信息
     * @param userID       用户id
     * @param type  类别 0 收入   1 支出  全部""
     * @param kind  类型  全部""
     * @param startTime 开始时间 全部""
     * @param endTime 结束时间  全部""
     * @param page 页面 默认0
     * @return
     */
    @GET("dbAction_getAccountList.do")
    Observable<AccountsModel> getAccountsList(
            @Query("userID") int userID,
            @Query("type") String type,
            @Query("kind") String kind,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("note") String note,
            @Query("page") int page
    );

    @GET("dbAction_updateAccountNote.do")
    Observable<ResultModel> updateAccountNote(
            @Query("accountID") int accountID,
            @Query("userID") int userID,
            @Query("note") String note
    );

    @Multipart
    @POST("dbAction_uploadHeader.do")//上传图片  文件
    Observable<ResultModel> uploadHeader(
            @Part MultipartBody.Part file,
            @Part("userID") RequestBody userID
            );

    @POST("dbAction_uploadAccount.do")//上传账单系信息
    Observable<ResultModel> uploadAccount(
            @Query("userID") int userID,
            @Query("type") String type,
            @Query("kind") String kind,
            @Query("money") String money,
            @Query("note") String note,
            @Query("time") String time,
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("address") String address
    );

    @GET("dbAction_getKinds.do")
    Observable<List<KindModel>> getKinds();

    @GET("dbAction_getPieData.do")//统计图
    Observable<List<PieModel>> getPieData(
            @Query("userID") int userID,
            @Query("type") String type,
            @Query("kind") String kind,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("note") String note
    );

    @GET("dbAction_getLineData.do")//统计图
    Observable<List<LineModel>> getLineData(
            @Query("userID") int userID,
            @Query("type") String type,
            @Query("kind") String kind,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("note") String note
    );
}
