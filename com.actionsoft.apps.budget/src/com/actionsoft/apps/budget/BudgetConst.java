package com.actionsoft.apps.budget;
/**
 * 常量类
 * @author zhaoxs
 * @date 2017-08-21
 */


public class BudgetConst {
	
	
	//员工职级常量
	public static String user_leave1 = "1";//公司领导
	public static String user_leave2 = "2";//部门负责人
	public static String user_leave3 = "3";//处长
	public static String user_leave4 = "4";//普通员工或处长
	public static String user_leave5 = "5";//普通员工
	public static String user_leave6 = "6";//普通员工
	
	public static String stateAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSAslp";//状态回写aslp
	public static String sourceAppId = "com.actionsoft.apps.budget";//接口调用app
	public static String Budget_step01_activityid="obj_c7a48582578000018c6616c91eeb1ef6";//预算调整流程第一节点ID
	
	/** 邮件关键key **/
	public static final String COMPANYNAME = "【COMPANYNAME】";// 公司名称
	public static final String DAYS = "【DAYS】";// 天数
	public static final String ACTIVITYNAME = "【ACTIVITYNAME】";// 节点名称
	public static final String PROCESSTITLE = "【PROCESSTITLE】";// 流程标题
	public static final String TASKTARGET = "【TASKTARGET】";// 办理人姓名
	public static final String PROCESSCREATER = "【PROCESSCREATER】";// 流程发起人姓名
	
	/** 预算管理APPID **/
	public static final String APPID = "com.actionsoft.apps.cmcc.budget";
	
	/** 相关ALSP服务 **/
	public static final String SENDEMAILASLP="aslp://com.actionsoft.apps.cmcc.integration/SendEmainAslp";//发送邮件

}
