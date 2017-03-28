package com.geek.springdemo.config;

/**
 * @author zhangfan
 * @ClassName: WebHostConfig.java
 * @Description: 网络ip、port配置文件
 * @Date 2017-1-11
 */

public class WebHostConfig {

//    private static final String HOST_ADDRESS = "http://192.168.3.120:8080/";//meet
    private static final String HOST_ADDRESS = "http://192.168.2.101:8080/";//office
//    private static final String HOST_ADDRESS = "http://115.159.6.47/";//外网
    private static final String HOST_NAME = HOST_ADDRESS + "MySSM/";

    public static String getHostAddress() {
        return HOST_ADDRESS;
    }

    public static String getHostName() {
        return HOST_NAME;
    }


}
