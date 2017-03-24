package com.geek.springdemo.config;

import android.util.Log;

/**
 * @ClassName: WebUrlConfig.java
 * @Description: 网络url（接口）配置文件
 * @Date 2017-1-11
 */
public class WebUrlConfig {
    private static final String HOST_NAME = WebHostConfig.getHostName();
    private static final String LOGIN = HOST_NAME + "dbAction_login.do?";//登录
    private static final String REGISTER = HOST_NAME + "dbAction_register.do?";//注册
    private static final String GETACCOUNTSLIST = HOST_NAME + "dbAction_getAccountList.do?";//得到保存的记账信息
    private static final String GETKINDS = HOST_NAME + "dbAction_getKinds.do?";//得到类型列表
    private static final String UPLOADACCOUNT = HOST_NAME + "dbAction_uploadAccount.do?";//上传账单系信息


    /**
     * 注册
     * @return
     */
    public static String getRegister(){
        return REGISTER;
    }

    /**
     * 登录
     * @return
     */
    public static String getLogin(){
        return LOGIN;
    }

    /**
     * 得到保存的记账信息
     * @param userID       用户id
     * @param type  类别 0 收入   1 支出  全部""
     * @param kindID  类型  全部""
     * @param startTime 开始时间 全部""
     * @param endTime 结束时间  全部""
     * @param page 页面 默认0
     * @return
     */
    public static String getAccountsList(String userID,String type,String kindID,String startTime,String endTime,String page){
        return GETACCOUNTSLIST + "userID="+userID+"&type="+type+"&kindID="+kindID+"&startTime="+startTime+"&endTime="+endTime+"&page="+page;
    }

    /**
     * 得到类型列表
     * @return
     */
    public static String getKinds(){
        return GETKINDS;
    }

    /**
     * 上传账单系信息
     * @param userID
     * @param type
     * @param kind
     * @param money
     * @param note
     * @param time
     * @return
     */
    public static String upLoadAccount(String userID,String type,String kind,String money,String note,String time){
        return UPLOADACCOUNT+"userID="+userID+"&type="+type+"&kind="+kind+"&money="+money+"&note="+note+"&time="+time;
    }

}

	