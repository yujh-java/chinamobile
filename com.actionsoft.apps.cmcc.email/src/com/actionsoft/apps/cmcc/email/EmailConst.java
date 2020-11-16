package com.actionsoft.apps.cmcc.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常量类
 * @author nch
 * @date 2017-09-22
 */
public class EmailConst {
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
	public static List<String> list_firstActivityid;//允许撤办节点集合
	static{
		list_firstActivityid = new ArrayList<String>();
		list_firstActivityid.add(result_submit_step01_activityid);
		list_firstActivityid.add(lx_step01_activityid);
		list_firstActivityid.add(titleScore_step01_activityid);
		list_firstActivityid.add(jx_step01_activityid);
		list_firstActivityid.add(projectFeedBack_step01_activityid);
		list_firstActivityid.add(Unplaned_step01_activityid);
		list_firstActivityid.add(Cancel_step01_activityid);
		list_firstActivityid.add(Change_step01_activityid);
		list_firstActivityid.add(Contract_step01_activityid);
		list_firstActivityid.add(Budget_step01_activityid);
		list_firstActivityid.add(Virtual_step01_activityid);
		list_firstActivityid.add(labslx_step01_activityid);
	}
	public static Map<String,String> map_activityis_processdefid;//节点ID与挂子流程定义ID
	static{
		map_activityis_processdefid = new HashMap<String,String>();
		map_activityis_processdefid.put("obj_c77af3d031200001a5feac74e6001438", "obj_1659916ce0a5477d9cb268128ad18de7");
		map_activityis_processdefid.put("obj_c775775ad350000140bf3af01c868890", "obj_2569912f23c24f48b32ce4bef75e97eb");
		map_activityis_processdefid.put("obj_c7835a47a4d0000182bccba011086190", "obj_8426c4500c4a4601af453ca4ce2ac130");
		map_activityis_processdefid.put("obj_c7835a89c5600001b52a65201f5a1048", "obj_f3b1c21c0d4640548bb6ad7c1578e2d5");
	}
	

	public static String sourceAppId = "com.actionsoft.apps.cmcc.email";//当前应用APP
	public static String stateAslp = "aslp://com.actionsoft.apps.cmcc.integration/SendEmainAslp";//调用ASLP应用ID
	
	public static String deptNo_jt = "00030000000000000000";//集团部门编号
}
