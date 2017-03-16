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

}

	