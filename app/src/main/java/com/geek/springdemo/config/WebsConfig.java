package com.geek.springdemo.config;

import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.rxjava.ProgressUploadListener;

import java.util.List;

import rx.Subscriber;

/**
 * Created by chenMeng on 2017/6/8.
 */

public interface WebsConfig {
    void register(String name, String psw, Subscriber<ResultModel> subscriber);
    void login(String name, String psw, Subscriber<UserModel> subscriber);
    void getAccountList(String userID, String type, String kind, String startTime, String endTime, String page, Subscriber<List<AccountsModel>> subscriber);
    void updateAccountNote(String accountID, String userID, String note,Subscriber<ResultModel> subscriber);
    void uploadHeader(String userID,String img,Subscriber<ResultModel> subscriber);
    void uploadAccount(String userID,String type,String kind,String money,String note,String time,String lat,String lng,String address,Subscriber<ResultModel> subscriber);
    void getKinds(Subscriber<List<KindModel>> subscriber);
}
