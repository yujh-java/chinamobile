package com.actionsoft.apps.cmcc.integration;
/**
 * 常量类
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class CMCCConst {
	
	
	
	public static final String PROCESSTYPE_RESULTSUB = "milestone";//成果提交流程类型编号
	
	public static final String PROCESSID_RESULTSUB = "obj_43dd834a379249759e96d119f08c760f";//成果提交流程标识ID
	
	public static final String ROLECONFIG_PROCESSID = "obj_2c09dfa736824067bff9af7d7c1e5007";//角色同步流程视图ID
	
	public static LinkedHashMap<String, String> PROCESSTYPE_NAME;//流程名称
	static {
		PROCESSTYPE_NAME = new LinkedHashMap<String, String>();
		PROCESSTYPE_NAME.put(CMCCConst.PROCESSID_RESULTSUB, "里程碑成果提交");//成果提交流程
	}
	
	public static LinkedHashMap<String, String> PROCESSTYPE_PROCESSID;//流程类型
	static {
		PROCESSTYPE_PROCESSID = new LinkedHashMap<String, String>();
		PROCESSTYPE_PROCESSID.put(CMCCConst.PROCESSTYPE_RESULTSUB, CMCCConst.PROCESSID_RESULTSUB);//成果提交流程
	}
	
	public static final LinkedHashMap<String, String> PROCESS_APPLICATION;//流程应用
	static {
		PROCESS_APPLICATION = new LinkedHashMap<String, String>();
		PROCESS_APPLICATION.put(CMCCConst.PROCESSTYPE_RESULTSUB, "项目管理");
	}
	public static final List<String> NotDoHistoryTask;
	static {
		NotDoHistoryTask = new ArrayList<String>();
		NotDoHistoryTask.add("obj_c7864b31f8e00001f1fc1910f9901802");
		NotDoHistoryTask.add("obj_c775865519d0000147121700b0307730");
		NotDoHistoryTask.add("obj_c7864d059a100001dd8218f01ca02ec0");
		NotDoHistoryTask.add("obj_c7877d82e6800001a02114f718e4176e");
		NotDoHistoryTask.add("obj_c7835a504e2000016eab5d20114311bc");
		NotDoHistoryTask.add("obj_c7835a82f0800001e2dd6710da50fb30");
		NotDoHistoryTask.add("obj_c7835fd49f70000149a325a02d001c7f");
		NotDoHistoryTask.add("obj_c783609fbf6000012dbac8e3559072c0");
	}
	public static final int ipm_connectTime = 10000;//接口连接时间控制
	
	/*public static final List<String> DiffFormSnapshot;//表单快照根据研究院和非研究院不同的流程标识ID集合
	static {
		DiffFormSnapshot = new ArrayList<String>();
		DiffFormSnapshot.add("obj_8b5cc3f8cac84dcc98ce2dbaf57061e8");//立项配合研发机构流程
		DiffFormSnapshot.add("obj_2569912f23c24f48b32ce4bef75e97eb");//立项需求部门流程
		DiffFormSnapshot.add("obj_fe69beabb5c541dda3c6cfd01a967692");//立项申请流程
		
		DiffFormSnapshot.add("obj_f3b1c21c0d4640548bb6ad7c1578e2d5");//结项需求部门流程
		DiffFormSnapshot.add("obj_b1e878e45d0e4172afb9dc158152da66");//结项配合研发机构流程
		DiffFormSnapshot.add("obj_7732d3a4633b41d3bfb781b87710f44a");//结项申请流程
	}*/
	
	/**
	 * 公共路由所需参数
	 */
	public static final String ROUTE_MASTER_TABLE = "BO_ACT_CMCC_PROCESSINFO";//主表名称
	public static final String ROUTE_CHILD_TABLE = "BO_ACT_CMCC_STEPINFO";//子表名称
	public static final String ROUTE_FIELD_TABLE = "BO_ACT_CMCC_STEPROUTE";//字段子表
	public static final int PROCESSINFO_IRRELEVANT = 0;//不相关
	public static final int PROCESSINFO_STEP_COMPANY = 1;//某节点所属公司
	public static final int PROCESSINFO_STEP_DEPARTMENT = 2;//某节点所属部门
	public static final int PROCESSINFO_STEP_HENDLER = 3;//某节点办理人员
	public static final int PROCESSINFO_PARENTSTEP_HENDLER = 4;//父流程某节点办理人员
	
	
	
	
	
	
 }





