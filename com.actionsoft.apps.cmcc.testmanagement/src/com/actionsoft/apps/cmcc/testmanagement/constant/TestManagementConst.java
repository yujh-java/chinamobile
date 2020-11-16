package com.actionsoft.apps.cmcc.testmanagement.constant;

/**
 * @author yujh
 * @version 创建时间：2018年12月20日 上午11:36:35 测试管理常量类
 */
public class TestManagementConst {
	/** 测试管理APPID **/
	public static final String APPID = "com.actionsoft.apps.cmcc.testmanagement";
	
	/** 员工职级常量 **/
	public static final String user_leave1 = "1";// 公司领导
	public static final String user_leave2 = "2";// 部门负责人
	public static final String user_leave3 = "3";// 处长
	public static final String user_leave4 = "4";// 普通员工或处长
	public static final String user_leave5 = "5";// 普通员工
	public static final String user_leave6 = "6";// 普通员工
	
	/** 邮件关键key **/
	public static final String COMPANYNAME = "【COMPANYNAME】";// 公司名称
	public static final String DAYS = "【DAYS】";// 天数
	public static final String ACTIVITYNAME = "【ACTIVITYNAME】";// 节点名称
	public static final String PROCESSTITLE = "【PROCESSTITLE】";// 流程标题
	public static final String TASKTARGET = "【TASKTARGET】";// 办理人姓名
	public static final String PROCESSCREATER = "【PROCESSCREATER】";// 流程发起人姓名

	/** 相关ALSP服务 **/
	public static final String SENDEMAILASLP="aslp://com.actionsoft.apps.cmcc.integration/SendEmainAslp";//发送邮件
	
	/** 相关接口key **/
	public static final String GET_PROJECT_INFO ="GET_PROJECT_INFO";
	
	/** 相关流程定义ID **/
	public static final String CSJL_ID="obj_a0838f1a56474e8a98f14724fca6365c";//现场检查申请流程定义ID
	public static final String WDJL_ID="obj_a43808c143414aa09cd5ea240aa73ceb";//文档检查申请流程定义ID
	
	/** 相关节点ID **/
	public static final String CSJL_STEP_ID="obj_c85497532fa00001152c1fa019606d20";//现场检查申请流程-测试经理审核节点ID
	public static final String WDJL_STEP_ID="obj_c8548433899000013e4823e019d81500";//文档检查申请流程-测试经理审核节点ID
	
	/** 快照相关参数 **/
	public static final String path = "../webserver/webapps/workflow/formSnapShot/";
	
	/** 相关角色ID **/
	public static final String TEST_MANAGER_NAME="测试管理经理";//测试管理经理角色
	
	/** 相关部门ID **/
	public static final String DEPT_ALL="f1c01f98-9184-4e8d-8908-ae5614328586";
	//public static final String DEPT_ALL="4f08fc1a-c4c7-4c68-85cd-c17b0fc23536";
	
	public static final int ipm_connectTime = 10000;//接口连接时间控制
}
