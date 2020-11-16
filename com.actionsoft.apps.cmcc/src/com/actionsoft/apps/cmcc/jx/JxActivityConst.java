package com.actionsoft.apps.cmcc.jx;
/**
 * 结项流程
 */
import java.util.ArrayList;
import java.util.List;

/**
 * 结项，节点ID常量类
 * @author nch
 * @date 2017-6-27
 */
public class JxActivityConst {
	
	public static String phyfjgImper = "obj_c7835a504e2000016eab5d20114311bc";//主流程中配合研发机构接口人处理
	public static String xqbmjkr = "obj_c7835a82f0800001e2dd6710da50fb30";//主流程中需求部门接口人处理
	
	public static String sub_qtyfbmjkr = "obj_c7835fd49f70000149a325a02d001c7f";//配合研发机构会签子流程中牵头研发机构接口人
	public static String qtyfbmjkr  = "obj_c7835a012b100001a2551e3085691feb";//主流程牵头研发机构接口人

	public static String sub_zbyfxmjkr = "obj_c783609fbf6000012dbac8e3559072c0";//子流程中总部研发项目接口人
	public static String zbyfxmjkr = "obj_c7835a64531000015d8525a0317019e8";//主流程中总部研发项目接口人
	
	public static String sub_zgylg = "obj_c7835f5736f00001b55f15b01bb0b5b0";//配合研发机构会签子流程主管院领导
	public static String sub_xmglbmld = "obj_c7835f526f300001e87b1ba376d01ebb";//配合研发机构会签子流程项目管理部门领导审核
	
	public static String zgylg = "obj_c78359e5bc400001ca7a16701d415bc0";//主流程主管院领导
	public static String xmglbmld = "obj_c78359ded2d00001ae521b0c18c013f3";//主流程项目管理部门领导审核
	
	public static String yzsp = "obj_c78cc0aed6d00001ca4c11e01ec0ab10";//主流程院长审批
	public static String xmqtbsld = "obj_c78359d06df00001fc7b1800d42038b0";//主流程项目牵头部所领导
	
	public static String sub_yzsp = "obj_c78cf6d4f8200001192e1ec09ac09fe0";//配合研发机构会签子流程院长审批
	public static String sub_qtbsldsh = "obj_c7835f01d1b00001a6c211cf1dcb5ef0";//配合研发机构会签子流程牵头部所领导
	
	public static String sub_phyfjgcsld = "obj_c7835f9a0bb00001902df77510801062";//配合研发机构会签子流程配合研发机构处室领导处理
	public static String sub_phyfjgld = "obj_c7835fa03b90000172721a90148310dd";//配合研发机构会签子流程配合研发机构主管领导审核
	public static String sub_phyfjgxmjl = "obj_c7835f85982000017ee218c013ad8db0";//配合研发机构会签子流程配合研发机构项目经理处理
	
	public static String sub_cwjs = "obj_c7835eefe1f000015aa71e0036f01394";//配合研发机构会签子流程财务决算
	public static String sub_xmjlqr = "obj_c7887265d8900001838d1a101d8068a0";//配合研发机构会签子流程项目经理确认
	public static String sub_jsjl = "obj_c78cf425404000011f3a185a17002210";//配合研发机构会签子流程技术经理
	public static String sub_xmglry = "obj_c7835f0db39000015256cbe0359094b0";//配合研发机构会签子流程项目管理人员审核
	public static String sub_xmjl = "obj_c7835eeadd300001ff491000292a16cc";//配合研发机构会签子流程项目经理处理
	
	public static String sub_peyfjgjkr = "obj_c783b7b7e8700001a766b319103b1de0";//子流程配合研发机构接口人
	public static String sub_xqbmjkr = "obj_c78360858c200001ecb3db46c6f12790";//子流程需求部门接口人
	//结项主流程流程定义ID
	//public static String processDefid = "obj_7732d3a4633b41d3bfb781b87710f44a";

	public static String qtyfjghq_name = "牵头研发机构意见";//牵头研发机构会签意见名称
	public static List<String> list_qtyfjghq;//牵头研发机构会签意见
	static{
		list_qtyfjghq = new ArrayList<String>();
		list_qtyfjghq.add("obj_c78359d74300000124e7152afcea7c60");
		list_qtyfjghq.add("obj_c78359b6fa1000018fb41726a3c919ce");
		list_qtyfjghq.add("obj_c78359c2b84000014c1c150919a04860");
		list_qtyfjghq.add("obj_c78359d06df00001fc7b1800d42038b0");
		list_qtyfjghq.add("obj_c78359a9fe500001d77e12001da01bab");
		list_qtyfjghq.add("obj_c78cc0aed6d00001ca4c11e01ec0ab10");
		list_qtyfjghq.add("obj_c78cbdcf0ef0000168ac1c371cf59200");
		list_qtyfjghq.add("obj_c78359ded2d00001ae521b0c18c013f3");
		list_qtyfjghq.add("obj_c78359e5bc400001ca7a16701d415bc0");
		list_qtyfjghq.add("obj_c78359a207200001f93a1a3217821596");
		list_qtyfjghq.add("obj_c7835a012b100001a2551e3085691feb");
		list_qtyfjghq.add("obj_c7f3659119e00001a82e1738c1f0ab20");//chenxf add 2018-4-16 test
		list_qtyfjghq.add("obj_c7f89da927100001a2ebaf5011d968c0");//chenxf add 2018-5-2 prd
	}
	
	public static String phyfjghq_name = "配合研发机构意见";//牵头配合研发机构会签意见名称
	public static List<String> list_phyfjghq;//牵头配合研发机构会签意见
	static{
		list_phyfjghq = new ArrayList<String>();
		list_phyfjghq.add("obj_c783b7b7e8700001a766b319103b1de0");
		list_phyfjghq.add("obj_c7835eeadd300001ff491000292a16cc");
		list_phyfjghq.add("obj_c7835eefe1f000015aa71e0036f01394");
		list_phyfjghq.add("obj_c7835f01d1b00001a6c211cf1dcb5ef0");
		list_phyfjghq.add("obj_c7835f0db39000015256cbe0359094b0");
		list_phyfjghq.add("obj_c7835f9a0bb00001902df77510801062");
		list_phyfjghq.add("obj_c7835fbe3eb00001a750b7c2b5179720");
		list_phyfjghq.add("obj_c78cf425404000011f3a185a17002210");
		list_phyfjghq.add("obj_c7835f526f300001e87b1ba376d01ebb");
		list_phyfjghq.add("obj_c7835f5736f00001b55f15b01bb0b5b0");
		list_phyfjghq.add("obj_c7835f7586d00001a4b7e99015f01cdd");
		list_phyfjghq.add("obj_c78cf6d4f8200001192e1ec09ac09fe0");
		list_phyfjghq.add("obj_c7835eddb4f000014a681b92ac101ddd");
		list_phyfjghq.add("obj_c7835f85982000017ee218c013ad8db0");
		list_phyfjghq.add("obj_c7835fa03b90000172721a90148310dd");
		list_phyfjghq.add("obj_c7887265d8900001838d1a101d8068a0");
		list_phyfjghq.add("obj_c7f3694874500001c96ee0601da02530");//chenxf add 2018-4-16 test
		list_phyfjghq.add("obj_c7f8a12f3dd000011b5a376dc120b9a0");//chenxf add 2018-4-16 prd
		
	}
	
	public static String xqbmsp_name = "需求部门意见";//需求/配合部门审批意见名称
	public static List<String> list_xqbmsp;//需求/配合部门审批意见
	static{
		list_xqbmsp = new ArrayList<String>();
		list_xqbmsp.add("obj_c78360858c200001ecb3db46c6f12790");
		list_xqbmsp.add("obj_c783608d71d00001becc1cdd15501acd");
		list_xqbmsp.add("obj_c78cfa3c237000014a302740fb231ef7");
	}
	
	public static String jsbsp_name = "技术部门意见";//技术部意见名称
	public static List<String> list_jsbsp;//技术部意见
	static{
		list_jsbsp = new ArrayList<String>();
		list_jsbsp.add("obj_c7835a7796f00001abba116f1c6b5ec0");
	}
	
	public static String jtgs_name = "集团领导意见";//公司领导意见名称
	public static List<String> list_jtgs;//公司领导意见
	static{
		list_jtgs = new ArrayList<String>();
		list_jtgs.add("obj_c7835d00afe00001e15315be8100db40");
	}
	
	public static String zbyfjkr_name = "总部研发项目接口人意见";//总部研发接口人意见名称
	public static List<String> list_zbyfjkr;//总部研发接口人意见
	static{
		list_zbyfjkr = new ArrayList<String>();
		list_zbyfjkr.add("obj_c7835a64531000015d8525a0317019e8");
		list_zbyfjkr.add("obj_c7f3662ecc700001491413d3104c6420");//chenxf add 2018-4-16 test
		list_zbyfjkr.add("obj_c7f89fc53a2000011a1f10ed53e99c00");//chenxf add 2018-5-2 prd
	}
	
	public static String xgbmls_name = "相关部门落实";//相关部门落实意见名称
	public static List<String> list_xgbmls;//相关部门落实意见
	static{
		list_xgbmls = new ArrayList<String>();
		list_xgbmls.add("obj_c7835d078f500001477b19d01c80159d");
	}
}
