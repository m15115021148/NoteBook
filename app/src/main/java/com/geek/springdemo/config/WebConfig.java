package com.geek.springdemo.config;


import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chenMeng on 2017/6/1.
 */

public interface WebConfig {
    @POST("dbAction_register.do")
    Observable<ResultModel> register(@Query("name") String name, @Query("password") String password);

    @POST("dbAction_login.do")
    Observable<UserModel> login(@Query("name") String name, @Query("password") String password);

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
    Observable<List<AccountsModel>> getAccountsList(
            @Query("userID") String userID,
            @Query("type") String type,
            @Query("kind") String kind,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("page") String page
    );
}
