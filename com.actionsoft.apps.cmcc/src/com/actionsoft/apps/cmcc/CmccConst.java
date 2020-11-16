package com.actionsoft.apps.cmcc;
/**
 * 常量类
 * @author nch
 * @date 2017-06-22
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.sdk.local.SDK;

public class CmccConst {
	
	//第一个节点常量，在com.actionsoft.apps.cmcc.email中EmailConst类中同时维护
	public static String result_submit_step01_activityid = "obj_c76c2a8637200001351f24741beb1092";//成果提交第一节点ID
	public static String lx_step01_activityid = "obj_c77574e9b5b0000193ea20301b301177";//立项第一节点ID
	public static String titleScore_step01_activityid = "obj_c76ec121d7000001175133811780166c";//结题评分第一节点ID
	public static String jx_step01_activityid = "obj_c78359997ea00001d5d51b601b1611f0";//结项第一节点ID
	public static String projectFeedBack_step01_activityid="obj_c76ec3d76fc00001b3399dab1a3e32a0";//一般委托项目启动反馈流程第一节点
	public static String Unplaned_step01_activityid = "obj_c795167017800001e9921a031f505550";//计划外项目流程第一节点ID
	public static String Cancel_step01_activityid = "obj_c796f4458360000113d21d3571de5870";//取消终止流程第一节点ID
	public static String Change_step01_activityid = "obj_c79b74ac24f00001a044d6c01eb0cb50";//项目变更流程第一节点ID
	public static String Contract_step01_activityid = "obj_c7a4739491a00001bda9c60b1570f920";//合同计提流程第一节点ID
	public static String Budget_step01_activityid="obj_c7a48582578000018c6616c91eeb1ef6";//预算调整流程第一节点ID
	public static String Virtual_step01_activityid= "obj_c7ac81339bd00001e1b313d471661299";//虚拟收入确认流程第一节点ID
	public static String labslx_step01_activityid= "obj_c7b071df1d800001c6c5ecb0b9301302";//研究院内立项流程第一节点ID
	public static String yjyjx_step01_activityid= "obj_c7b0c2f1738000016fe01b971f6a1761";//研究院内结项流程第一节点ID
	//企标管理
	public static String esbp_step01_activityid = "obj_c7bfdd0789a0000113a31a173db6184e";//企标报批流程第一节点ID
	public static String eszdss_step01_activityid = "obj_e5fa1ceda4284c29a4af23596017c854";//重点企标送审流程第一节点ID
	public static String esfzdss_step01_activityid = "obj_68256bd730344615ad689b807cc3b0a6";//非重点企标送审流程第一节点ID
	public static String esjhxz_step01_activityid = "obj_cb1f4b25814a4e64898e996c0b7b9bf3";//企标计划新增流程第一节点ID
	public static String esjhbg_step01_activityid = "obj_33b5266118204c4b95558f1abcd46525";//企标计划变更流程第一节点ID
	public static String esjhqx_step01_activityid = "obj_7baa1ed97a16429084674fc1775ae3a1";//企标计划取消流程第一节点ID
	public static String esfs_step01_activityid = "obj_c8281682fdd00001364a18f662001af4";//企标计划复审流程第一节点ID
	public static String wdsy_step01_activityid = "obj_c88235bab4800001fe546a1c5480127f";//企标文档水印流程第一节点ID
	
	
	//预算管理
	public static String budget_step01_activityid = "obj_c7db7cbf00d0000125b7183011981f37";//预算调整流程第一节点ID
	//测试管理
	public static String TEST_MANAGEMENT_STEP01_ACTIVITUID = "obj_c83e1142d210000146931620102d147e";//测试管理确认单流程第一节点ID
	public static String TEST_MANAGEMENT_SPECIAL_STEP01_ACTIVITUID = "obj_c83e5cbfc2f00001a1929cf410a71be0";//测试管理特殊确认单流程第一节点ID
	public static String TEST_START_MEETING_STEP01_ACTIVITUID = "obj_c842d03e85c0000165971560b0001a25";//启动会流程第一节点ID
	public static String TEST_OUTSOURCE_PROMISE_STEP01_ACTIVITUID = "obj_c842cff318600001ab6947792c98a340";//承诺书流程第一节点ID
	public static String TEST_REPORT_RELEASE_STEP01_ACTIVITUID = "obj_c848fe41907000014dbd1fb017406a10";//测试报告发布流程第一节点ID
	public static String TEST_ONSITE_INSPECTION_STEP01_ACTIVITUID = "obj_c847658693c0000125b656ae9ed41073";//测试现场检查流程第一节点ID
	public static String TEST_DOC_INSPECTION_STEP01_ACTIVITUID = "obj_c854842088d000012040f45a17401e20";//测试文档检查申请流程第一节点ID
	public static String TEST_SECURITY_EVALUATION_STEP01_ACTIVITUID = "obj_c858b3f394a00001b9151379b5401ac5";//外包评估和选择流程第一节点ID
	
	//标准化管理
	public static String STANDARD_ORG_JOIN_STEP01_ACTIVITUID = "obj_c866edfc4b100001cfd6fb50148b6160";//标准化组织加入流程第一节点ID
	
	public static String deptid_yjy = "5b93a3f7-3ae2-4831-9fcc-d8809ffc462c";//研究院路径

	/*
	 * 立项、结项主流程中，配合研发机构项目接口人处理节点ID
	 */
	public static String lx_activityid = "obj_c7864b31f8e00001f1fc1910f9901802";
	public static String jx_activityid = "obj_c7835a504e2000016eab5d20114311bc";
	
	public static List<String> list_isundoTask;//允许撤办节点集合
	static{
		list_isundoTask = new ArrayList<String>();
		list_isundoTask.add(result_submit_step01_activityid);
		list_isundoTask.add(lx_step01_activityid);
		list_isundoTask.add(titleScore_step01_activityid);
		list_isundoTask.add(jx_step01_activityid);
		list_isundoTask.add(projectFeedBack_step01_activityid);
		list_isundoTask.add(Unplaned_step01_activityid);
		list_isundoTask.add(Cancel_step01_activityid);
		list_isundoTask.add(Change_step01_activityid);
		list_isundoTask.add(Contract_step01_activityid);
		list_isundoTask.add(Budget_step01_activityid);
		list_isundoTask.add(labslx_step01_activityid);
		list_isundoTask.add(yjyjx_step01_activityid);
		
		/**
		 * 企标
		 */
		list_isundoTask.add(esbp_step01_activityid);
		list_isundoTask.add(eszdss_step01_activityid);
		list_isundoTask.add(esfzdss_step01_activityid);
		
		list_isundoTask.add(esjhxz_step01_activityid);
		list_isundoTask.add(esjhbg_step01_activityid);
		list_isundoTask.add(esjhqx_step01_activityid);
		list_isundoTask.add(esfs_step01_activityid);
		list_isundoTask.add(wdsy_step01_activityid);
		/*
		 * 预算
		 */
		list_isundoTask.add(budget_step01_activityid);
		/*
		 * 测试管理
		 */
		list_isundoTask.add(TEST_MANAGEMENT_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_MANAGEMENT_SPECIAL_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_START_MEETING_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_OUTSOURCE_PROMISE_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_REPORT_RELEASE_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_ONSITE_INSPECTION_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_DOC_INSPECTION_STEP01_ACTIVITUID);
		list_isundoTask.add(TEST_SECURITY_EVALUATION_STEP01_ACTIVITUID);
		
		/**
		 * 标准化管理
		 */
		list_isundoTask.add(STANDARD_ORG_JOIN_STEP01_ACTIVITUID);
	}
	
	//员工职级常量
	public static String user_leave1 = "1";//公司领导
	public static String user_leave2 = "2";//部门负责人
	public static String user_leave3 = "3";//处长
	public static String user_leave4 = "4";//普通员工或处长
	public static String user_leave5 = "5";//普通员工
	public static String user_leave6 = "6";//普通员工
	
	public static String deptWsgRolename = "需求部门接口人";
	
	public static String projectManagerRolename = "项目管理人员";
	
	//测试人员角色
    public static String deptCsglRolename = "测试人员";
	
	
	public static String sourceAppId = "com.actionsoft.apps.cmcc";//接口调用app
	public static String getProjectAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSGetProjectAslp";//获取项目信息aslp
	public static String resultSubAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSResultSubAslp";//成果提交回去牵头部所
	public static String stateAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSAslp";//状态回写aslp
	public static String sendMsgAslp = "aslp://com.actionsoft.apps.cmcc.email/TaskSendMsgASPLP";//针对任务发送信息通知aslp
	//chenxf add 2018-05-24
	//获取承担部门ID
	public static String getUndertakeDeptId = "aslp://com.actionsoft.apps.cmcc.integration/SPMSGetUndertakeDeptId";
	
	public static String xqbmAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSProcessDataAslp";//调用接口获取需求部门信息
	public static String projectInfor = "aslp://com.actionsoft.apps.cmcc.integration/SPMSProjectInforAslp";//一般委托调用接口修改项目信息
	public static String projectInjection = "aslp://com.actionsoft.apps.cmcc.integration/SPMSProjectInjectionAslp";//一般委托调用接口注入项目信息
	public static String virtualAslp = "aslp://com.actionsoft.apps.cmcc.integration/SPMSVirtualIncomeAslp";//调用接口获取需求部门信息(虚拟收入)

	public static String propertyApp = "com.actionsoft.apps.cmcc";//系统参数app
	public static String propertyName = "PROJECTUSER_DEPTNO";//项目管理人员加签部门参数名称
	public static String propertyRoleName = "PROJECTUSER_ROLENAME";//项目管理人员加签人员角色参数名称
	public static String propertyNoleaders = "NOLEADERS_OUTIDS";//需求部门审批没有领导部门编号
	/*public static List<String> list_processDefid;//主流程流程定义ID
	static{
		list_processDefid = new ArrayList<String>();
		list_processDefid.add("obj_fe69beabb5c541dda3c6cfd01a967692");//立项
		list_processDefid.add("obj_7732d3a4633b41d3bfb781b87710f44a");//结项
		list_processDefid.add("obj_43dd834a379249759e96d119f08c760f");//成果提交
		list_processDefid.add("obj_05d732d2518e400086b0f449f6585053");//一般委托
		list_processDefid.add("obj_16e57f31c7d34296a01fa18607933129");//结题打分
		list_processDefid.add("obj_c9a0e4a892c141ab8cbc428b8609abb3");//取消终止
		list_processDefid.add("obj_7bec2d6c89ca47a98b0089c1a90c1fb4");//计划外项目
		list_processDefid.add("obj_7ecfa9898b8d409785badb958fa89ebb");//项目变更
		list_processDefid.add("obj_aa6ba831222d409388f861767500d652");//合同计提流程
		list_processDefid.add("obj_dc271d41cc314bc7ac77928cff57973a");//预算调整流程
		list_processDefid.add("obj_574598eaa56d403089d531fc5030fa3f");//虚拟收入确认流程
		list_processDefid.add("obj_441cc3e3ba374e44b1c0607cc5e8cb94");//研究院内结项流程
		list_processDefid.add("obj_e6bfeebb3d564474ba7642ad08bd5a8c");//企标报批稿流程
	}*/
	
	public static String deptNo_jt = "00030000000000000000";//集团部门编号
	
	public static String deptNo_yjy = "00030087000000000000";//研究院部门编号
	//一般委托接口人通知流程邮件模板唯一标识
	public static String ognlSign_projectFeedback = 
				SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.email", "projectFeedback_ognlSign");
	//一般委托研发机构部所通知流程邮件模板唯一标识
	public static String Dept_projectFeedback = 
			SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.email", "projectFeedback_Dept");
	
	public static String IT_DEPT_1 = "205e93d4-20b0-417c-b3ff-437bed897815";//信息技术中心
	public static String IT_DEPT_2 = "ec17b312-cb5c-407e-a830-03eacac41e3b";//中移动信息技术有限公司 PRO
	//public static String IT_DEPT_2 = "e49e07ad-9c23-4191-a77c-71dd35e3d61f";//中移动信息技术有限公司 DEV
	
}
