
package com.geek.springdemo.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * com.sitemap.stm.parser.ParserUtil
 * @author zhang
 * @category 网络数据解析
 * create at 2016年8月24日 下午3:50:50
 */
public class ParserUtil {
	
	/**
	 * JSON解析成对象
	 * @param text JSON字符串
	 * @param clazz JavaBean
	 * @return JavaBean的对象
	 */
	public static <T>Object jsonToObject(String text, Class<T> clazz){
		Object object=JSONObject.parseObject(text,clazz);
		return object;
	}
	
	
	/**
	 * JSON解析成对象的集合
	 * @param text JSON字符串
	 * @param clazz JavaBean
	 * @return JavaBean的对象的集合
	 */
	public static <T> List<T> jsonToList(String text, Class<T> clazz){
		List<T> list=JSONObject.parseArray(text, clazz);
		return list;
	}
	
	
	/**
	 * 对象生成JSON
	 * @param object JavaBean的对象
	 * @return JSON字符串
	 */
	public static String objectToJson(Object object){
		String json=JSONObject.toJSONString(object);
		return json;
	}
	
	/**
	 * 对象集合生成JSON
	 * @param <T>JavaBean
	 * @param list JavaBean对象的集合
	 * @param prettyFormat 是否序列化
	 * @return JSON字符串
	 */
	public static <T> String arrayObjectToJson(List<T> list, boolean prettyFormat){
		String json=JSON.toJSONString(list, prettyFormat);
		return json;
	}
}
