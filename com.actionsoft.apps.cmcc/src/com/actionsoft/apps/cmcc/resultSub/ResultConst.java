package com.actionsoft.apps.cmcc.resultSub;

import java.util.ArrayList;
import java.util.List;

/**
 * 成果提交
 * 成果提交节点ID常量类
 * @author nch
 * @date 2017-6-27
 */
public class ResultConst {
	public static String xqbmcsld = "obj_c76e786112500001a440eb6cc1707d90";//需求部门处室领导
	public static String xqbmfzr = "obj_c76e783ba260000133f0a0e7c0d0a170";//需求部门负责人
	public static String xqbmxmjl = "obj_c76e787ba2b000014fbcaad3aa672790";//需求部门项目经理处理

	public static String yfjghq_name = "研发机构意见";//研发机构会签意见名称
	public static List<String> list_yfjghq;//研发机构会签意见
	static{
		list_yfjghq = new ArrayList<String>();
		list_yfjghq.add("obj_c771e93d44f00001c66b1330774018a5");
		list_yfjghq.add("obj_c771e94703d000011d3692a411c4160d");
		list_yfjghq.add("obj_c76c2a8c90900001e3f0142017501de5");
		list_yfjghq.add("obj_c7f884b56c20000180a62851fedbb3b0");//chenxf add 2018-5-2 dev
		list_yfjghq.add("obj_c7fac81dc01000014bcf20ba16e14e40");//chenxf add 2018-5-9 test
		
	}
	
	public static String zbyfjg_name = "总部研发项目接口人意见";//总部研发机构接口人意见名称
	public static List<String> list_zbyfjg;//总部研发机构接口人意见
	static{
		list_zbyfjg = new ArrayList<String>();
		list_zbyfjg.add("obj_c76c2a9316200001dac81931dd8dd2f0");
		list_zbyfjg.add("obj_c76c2ac8d3700001b17c1e258ea0173f");
	}
	
	public static String xqbmsp_name = "需求部门意见";//需求部门审批意见名称
	public static List<String> list_xqbmsp;//需求部门审批意见
	static{
		list_xqbmsp = new ArrayList<String>();
		list_xqbmsp.add("obj_c76e783ba260000133f0a0e7c0d0a170");
		list_xqbmsp.add("obj_c76e786112500001a440eb6cc1707d90");
		list_xqbmsp.add("obj_c7714816df90000180541845e13219f6");
		list_xqbmsp.add("obj_c76e782d21800001e7a53700d2921478");
		list_xqbmsp.add("obj_c76e787ba2b000014fbcaad3aa672790");
	}
	
	public static String jsbsp_name = "技术部内部意见";//技术部意见名称
	public static List<String> list_jsbsp;//技术部意见
	static{
		list_jsbsp = new ArrayList<String>();
		list_jsbsp.add("obj_c770a1faf050000112acacf313305df0");
	}
}
