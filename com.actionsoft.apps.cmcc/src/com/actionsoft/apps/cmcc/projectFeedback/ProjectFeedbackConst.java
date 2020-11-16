package com.actionsoft.apps.cmcc.projectFeedback;
/**
 * 一般委托流程常量类
 * @author zhaoxs
 * @date 2017-06-27
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.sdk.local.SDK;
public class ProjectFeedbackConst {
	
	public static String xqbmcld_noteid = "obj_c76ec3f7f8b000018c97ea0029284f20";//第一个需求部门处领导处理节点ID
	public static String xqbmbld_noteid = "obj_c76ec40f9f90000162d7155f19e0ed10";//第一个需求部门部领导处理节点ID
	public static String xqjkr_noteid = "obj_c7cf4268db600001e565186aec708340";//第一个需求部门接口人节点ID
	
	public static String xqbmxmjl_noteid = "obj_c76ec53546200001382e1d60ea9051e0";//需求部门项目经理处理节点ID
	public static String xqbmcld_stepid = "obj_c76ec54681900001c9ddb390c6791e8f";//处领导审批 节点ID
	public static String xqbmbld_stepid = "obj_c76ec5572d8000016e89e3e1a2d4e5f0";//部领导审批节点ID
	public static String xqjkr_stepid = "obj_c7cf56f92a8000015e521370ab5014e0";//第二个需求部门接口人节点ID

	
	public static String yfjgjkr_noteid = "obj_c76ec3ff222000015e4c1d801ab81215";//第一个研发机构接口人节点ID
	public static String yfjgbssh_noteid = "obj_c76ec49a38e00001b94719401c913240";//第一个研发机构承担部所审核节点ID
	public static String yfjgbsshtwo_noteid = "obj_c87a900dd6200001ef3f10bd3530a0a0";//第二个研发机构承担部所审核节点ID
	
	public static String yfjgjkr_stepid = "obj_c76ec50b0cb000019fadbea01f001775";//第二个研发结构接口人节点ID
	public static String yfjgrwfzr_noteid = "obj_c76ec52836a000013f432f701d8010c8";//研发机构任务负责人办理 节点ID
	public static String yfjgbssh_stepid= "obj_c77e77f2e2c000019a8f1fa0c3231f19";//第二个研发机构承担部所审核 节点ID
	public static String yfjgbsshthree_stepid= "obj_c87a904ef6d00001568c1260dab0bcc0";//第三个研发机构承担部所审核 节点ID
	public static String yfjgzgld_noteid = "obj_c77e780201c000012eca11a015b01cd5";//研发机构主管领导审批节点ID
	public static String yfjgjkr_actid = "obj_c77e78141240000136c72e8a1770ba00";//第一个研发机构接口人节点ID
	public static String yfjgcdbsnb_actid = "obj_c87cd881a3f00001d0a010b01c60137d";//研发机构承担部所内部会签
	public static String yfjgxgbshq_actid = "obj_c8788f8a111000019de0c8db9d987d00";//研发机构相关部所会签
	public static String xgbsnbhq_actid = "obj_c8788f3f0930000196fd1f70eb1e1ea2";//相关部所内部会签子流程
	public static String yfjgxgbshq_actidtwo = "obj_c8789df0ece0000174381d3d57907380";//第二个研发机构相关部所会签
	public static String xgbsnbhq_actidtwo = "obj_c87893be8a000001dda8c60015578150";//第二个相关部所内部会签子流程
	
	public static String zbyfjkr_noteid = "obj_c76ec4bb7ed00001c5761cb21b7d1402";//第一个总部研发项目接口人节点ID
	public static String zbcld_noteid = "obj_c76ec4d4feb0000140a319a71c0013f4";//总部处领导审核节点ID
	public static String zbbld_noteid = "obj_c76ec4d9c8b00001731a16a82f8aa8d0";//总部部领导审核节点ID
	public static String zbyfjkr_stepid = "obj_c785f60028600001d4e51a411b005390";//第二个总部研发项目接口人节点ID
	public static String zbyfxmjkr_noteid = "obj_c76ec57117f0000120cd10301fc0cfc0";//最后总部研发项目接口人节点ID
	
	public static List<String> list_xqbmqdyjNoteid;
	public static String xqbmqdyjName = "需求部门启动意见";//需求部门启动意见
	static {
		list_xqbmqdyjNoteid = new ArrayList<String>();
		list_xqbmqdyjNoteid.add(xqbmcld_noteid);
		list_xqbmqdyjNoteid.add(xqbmbld_noteid);
		list_xqbmqdyjNoteid.add(xqjkr_noteid);
	}
	public static List<String> list_yfjgqryjNoteid;
	public static String yfjgqryjName = "研发机构确认意见";//研发机构确认意见
	static{
		list_yfjgqryjNoteid = new ArrayList<String>();
		list_yfjgqryjNoteid.add(yfjgjkr_noteid);
		list_yfjgqryjNoteid.add(yfjgbssh_noteid);
		list_yfjgqryjNoteid.add(yfjgbsshtwo_noteid);
		list_yfjgqryjNoteid.add(yfjgcdbsnb_actid);
		list_yfjgqryjNoteid.add(xgbsnbhq_actid);
	}
	
	public static List<String> list_jsbyjNoteid;
	public static String jsbyjName = "技术部意见";//技术部意见
	static{
		list_jsbyjNoteid = new ArrayList<String>();
		list_jsbyjNoteid.add(zbyfjkr_noteid);
		list_jsbyjNoteid.add(zbcld_noteid);
		list_jsbyjNoteid.add(zbbld_noteid);
		list_jsbyjNoteid.add(zbyfjkr_stepid);
		list_jsbyjNoteid.add(zbyfxmjkr_noteid);
	}
	
	public static List<String> list_yfjgblyjNoteid;
	public static String yfjgblyjName = "研发机构办理意见";//研发机构办理意见
	static{
		list_yfjgblyjNoteid = new ArrayList<String>();
		list_yfjgblyjNoteid.add(yfjgjkr_stepid);
		list_yfjgblyjNoteid.add(yfjgrwfzr_noteid);
		list_yfjgblyjNoteid.add(yfjgbssh_stepid);
		list_yfjgblyjNoteid.add(yfjgbsshthree_stepid);
		list_yfjgblyjNoteid.add(yfjgzgld_noteid);
		list_yfjgblyjNoteid.add(yfjgjkr_actid);
		
		list_yfjgblyjNoteid.add(yfjgxgbshq_actid);
		
		list_yfjgblyjNoteid.add(yfjgxgbshq_actidtwo);
		list_yfjgblyjNoteid.add(xgbsnbhq_actidtwo);
	}
	
	public static List<String> list_xqbmfkyjNoteid;
	public static String xqbmfkyjName="需求部门反馈意见";//需求部门反馈意见
	static{
		list_xqbmfkyjNoteid = new ArrayList<String>();
		list_xqbmfkyjNoteid.add(xqbmxmjl_noteid);
		list_xqbmfkyjNoteid.add(xqbmcld_stepid);
		list_xqbmfkyjNoteid.add(xqbmbld_stepid);
		list_xqbmfkyjNoteid.add(xqbmbld_stepid);
		list_xqbmfkyjNoteid.add(xqjkr_stepid);
	}
	/**
	 * 根据流程定义ID，判断是否是主流程
	 * 
	 * @author nch
	 * @date 2017-10-20
	 * @param processdefid
	 * @return
	 */
	public static boolean isMainProcess(String processdefid) {

		List<BO> list = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSDEFNID = ", processdefid)
				.addQuery("ISMAIN = ", "否").list();
		boolean bol = true;
		if (list != null && list.size() > 0) {
			bol = false;
		}
		//System.err.println("listsize"+list.size());
		return bol;
	}
	
	
	
}
