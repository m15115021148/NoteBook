package com.geek.springdemo.config;

/**
 * com.sitemap.wisdomjingjiang.config.RequestCode
 *
 *
 * @author zhangfan
 *         接口请求需要用的辨识常量
 *         create at 2016年1月11日 13:30:35
 */
public class RequestCode {

    //	string类型常量
    public static final String ERRORINFO = "服务器无法连接，请稍后再试！";//网络连接错误信息
    public static final String NOLOGIN = "网络无法连接！";//网络无法连接

    /**注册规则*/
    public static final String REGISTERTOOT = "密码长度应在6-16位，必须是字母跟数字组合";

    //	int类型常量
    public static final int REGISTER = 0x0001;//注册常量
    public static final int LOGIN = 0x0002;//登录常量

}
