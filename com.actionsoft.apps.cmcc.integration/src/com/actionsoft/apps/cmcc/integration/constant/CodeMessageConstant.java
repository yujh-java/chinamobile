package com.actionsoft.apps.cmcc.integration.constant;

/**
 * 状态码以及信息常量类
 * @author Administrator
 *
 */
public class CodeMessageConstant {
	/** 状态码  **/
	public static final String CODE_1 = "1"; //成功
	public static final String CODE_2 = "2"; //header缺少参数
	public static final String CODE_3 = "3"; //签名验证失败
	public static final String CODE_4 = "4";; //系统未授权或标识错误
	public static final String CODE_5 = "5";; //参数缺少或错误
	public static final String CODE_6 = "6";; //未授权此流程
	public static final String CODE_7 = "7";; //操作用户无权限
	public static final String CODE_8 = "8";; //后台错误
	public static final String CODE_100 = "100"; //其他错误
	
	/** 流程类型 **/
	public static final String PROCESS_STATUS_1 = "1"; //流程已启动
	public static final String PROCESS_STATUS_2 = "2"; //流程已结束
	public static final String PROCESS_STATUS_3 = "3"; //流程已注销
	
	/** 相关信息 **/
	public static final String CODE_1_MESSAGE = "成功"; //成功
	public static final String CODE_2_MESSAGE = "header缺少参数"; //header缺少参数
	public static final String CODE_3_MESSAGE = "签名验证失败"; //签名验证失败
	public static final String CODE_4_MESSAGE = "系统未授权或标识错误";; //系统未授权或标识错误
	public static final String CODE_5_MESSAGE = "参数缺少或错误";; //参数缺少或错误
	public static final String CODE_6_MESSAGE = "未授权此流程";; //未授权此流程
	public static final String CODE_7_MESSAGE = "操作用户无权限";; //操作用户无权限
	public static final String CODE_8_MESSAGE = "后台错误";; //后台错误
	public static final String CODE_100_MESSAGE = "其他错误"; //其他错误
}
