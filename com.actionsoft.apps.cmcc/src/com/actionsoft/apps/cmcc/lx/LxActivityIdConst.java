package com.actionsoft.apps.cmcc.lx;

import java.util.ArrayList;
import java.util.List;

/**
 * 立项流程
 * 立项各节点ID常量类
 * @author nch
 * @date 2017-6-27
 */
public class LxActivityIdConst {
	
	public static String xqbmjkr = "obj_c775865519d0000147121700b0307730";//主流程需求部门接口人
	public static String phyfjgjkr = "obj_c7864b31f8e00001f1fc1910f9901802";//主流程配合研发机构接口人

	public static String sub_qtyfjkr = "obj_c7864d059a100001dd8218f01ca02ec0";//配合部门相关会签子流程中牵头研发机构接口人
	public static String qtyfjkr = "obj_c775751c70a0000186beff31d5e01308";//主流程中牵头研发机构接口人

	public static String sub_zbyfxmjkr = "obj_c7877d82e6800001a02114f718e4176e";//需求部门会签子流程中总部研发项目接口人
	public static String zbyfxmjkr = "obj_c775758048b000017fb2da1079ed1c93";//主流程中总部研发项目接口人

	public static String zgylg = "obj_c77575076a3000011b15879a36dc67e0";//主流程主管院领导
	public static String xmglbmld = "obj_c7757500b7b00001c32a11501880c4f0";//主流程项目管理部门领导审核

	public static String sub_zgylg = "obj_c78b114ed6200001b7b48bf0b777db80";//配合研发机构会签子流程主管院领导
	public static String sub_xmglbmld = "obj_c77aef206fa000015169a1f0ea7d1506";//配合研发机构会签子流程项目管理部门领导审核

	public static String yzsp = "obj_c78a7af953b000015c4f4a3017cf187f";//主流程院长审批
	public static String xmqtbsld = "obj_c77574f7da400001768b1e406d601e71";//主流程项目牵头部所领导

	public static String sub_yzsp = "obj_c77aef242e200001eb3f92b02c704860";//配合研发机构会签子流程院长审批
	public static String sub_qtbsldsh = "obj_c77aef1942a000016c7e1550eeee1a27";//配合研发机构会签子流程牵头部所领导

	public static String sub_phyfjgcsld = "obj_c77aef4c060000015c5719102ca01c2e";//配合研发机构会签子流程配合研发机构处室领导处理
	public static String sub_phyfjgld = "obj_c77aef399c800001c89c95e0c6d54930";//配合研发机构会签子流程配合研发机构主管领导审核
	public static String sub_phyfjgxmjl = "obj_c77aef517920000156f636541120b980";//配合研发机构会签子流程配合研发机构项目经理处理

	public static String sub_jsjl = "obj_c78b0b95f530000130e2a5c061901b07";//配合研发机构会签子流程技术经理
	public static String sub_xmglry = "obj_c77aef1d9fd00001c431148064c0eeb0";//配合研发机构会签子流程项目管理人员审核
	public static String sub_xmjl = "obj_c77ecae1026000017384cef012fd1595";//配合研发机构会签子流程项目经理处理

	public static String sub_peyfjgjkr = "obj_c7864bf7fb5000013b21150612bd89b0";//子流程配合研发机构接口人
	public static String sub_xqbmjkr = "obj_c775858c80f00001cc88eb1016c31b9a";//子流程需求部门接口人
	
	//立项主流程流程定义ID
	//public static String processDefid = "obj_fe69beabb5c541dda3c6cfd01a967692";

	public static String qtyfjghq_name = "牵头研发机构意见";//牵头研发机构会签意见名称
	public static List<String> list_qtyfjghq;//牵头研发机构会签意见
	static{
		list_qtyfjghq = new ArrayList<String>();
		list_qtyfjghq.add("obj_c77574f7da400001768b1e406d601e71");
		list_qtyfjghq.add("obj_c77574fc10300001e74d19251c001810");
		list_qtyfjghq.add("obj_c77575076a3000011b15879a36dc67e0");
		list_qtyfjghq.add("obj_c775751468b000015f6523b2490216e2");
		list_qtyfjghq.add("obj_c78a7a23f040000151d27b64bc10ccc0");
		list_qtyfjghq.add("obj_c7757500b7b00001c32a11501880c4f0");
		list_qtyfjghq.add("obj_c775750ccdb0000188e41c91ddb09340");
		list_qtyfjghq.add("obj_c775751c70a0000186beff31d5e01308");
		list_qtyfjghq.add("obj_c78a7af953b000015c4f4a3017cf187f");
		list_qtyfjghq.add("obj_c7f45b45cd9000018f6ca43e17008300");//chenxf add 2018-4-19 test
		list_qtyfjghq.add("obj_c7f8a3abb18000011b41a7e0e4001400");//chenxf add 2018-5-2 prd
		
		
	}

	public static String phyfjghq_name = "配合研发机构意见";//牵头配合研发机构会签意见名称
	public static List<String> list_phyfjghq;//牵头配合研发机构会签意见
	static{
		list_phyfjghq = new ArrayList<String>();
		list_phyfjghq.add("obj_c77aef04e290000114b81c8014f014b6");
		list_phyfjghq.add("obj_c77aef1942a000016c7e1550eeee1a27");
		list_phyfjghq.add("obj_c77aef1d9fd00001c431148064c0eeb0");
		list_phyfjghq.add("obj_c77aef206fa000015169a1f0ea7d1506");
		list_phyfjghq.add("obj_c77aef242e200001eb3f92b02c704860");
		list_phyfjghq.add("obj_c7864bf7fb5000013b21150612bd89b0");
		list_phyfjghq.add("obj_c78b114ed6200001b7b48bf0b777db80");
		list_phyfjghq.add("obj_c77aef06249000016d9576d0d6c49d20");
		list_phyfjghq.add("obj_c77aef399c800001c89c95e0c6d54930");
		list_phyfjghq.add("obj_c77aef4c060000015c5719102ca01c2e");
		list_phyfjghq.add("obj_c77aef517920000156f636541120b980");
		list_phyfjghq.add("obj_c77ecae1026000017384cef012fd1595");
		list_phyfjghq.add("obj_c78b0b95f530000130e2a5c061901b07");
		list_phyfjghq.add("obj_c7864ccc3390000153fb19b34b001285");//chenxf add 2018-4-20 test
		list_phyfjghq.add("obj_c7f45fa8653000018ebe1906b5832160");//chenxf add 2018-4-19 test
		list_phyfjghq.add("obj_c7f8a67ec7d00001b2451a62152a117c");//chenxf add 2018-5-2 prd
	}

	public static String xqbmsp_name = "需求部门意见";//需求/配合部门审批意见名称
	public static List<String> list_xqbmsp;//需求/配合部门审批意见
	static{
		list_xqbmsp = new ArrayList<String>();
		list_xqbmsp.add("obj_c775858c80f00001cc88eb1016c31b9a");
		list_xqbmsp.add("obj_c7758591c2b00001b1cd1af0d09012b0");
		list_xqbmsp.add("obj_c78cfa6af1900001a0941b2023b52610");
	}
	
	public static String jsbsp_name = "技术部门意见";//技术部意见名称
	public static List<String> list_jsbsp;//技术部意见
	static{
		list_jsbsp = new ArrayList<String>();
		list_jsbsp.add("obj_c77575ad93400001899b1de7a810139c");
	}
	
	public static String jtgs_name = "集团领导意见";//公司领导意见名称
	public static List<String> list_jtgs;//公司领导意见
	static{
		list_jtgs = new ArrayList<String>();
		list_jtgs.add("obj_c77575b9b9400001e1f51b471ef01256");
	}
	
	public static String zbyfjkr_name = "研发机构接口人意见";//总部研发接口人意见名称
	public static List<String> list_zbyfjkr;//总部研发接口人意见
	static{
		list_zbyfjkr = new ArrayList<String>();
		list_zbyfjkr.add("obj_c775758048b000017fb2da1079ed1c93");
		list_zbyfjkr.add("obj_c7f45dc9ebb000019acb10e01a107c30");//chenxf add 2018-4-19 test
		list_zbyfjkr.add("obj_c7f8a43a36300001d47d14d0126011d5");//chenxf add 2018-5-2 prd
	}
	
	public static String xgbmls_name = "相关部门落实";//相关部门落实意见名称
	public static List<String> list_xgbmls;//相关部门落实意见
	static{
		list_xgbmls = new ArrayList<String>();
		list_xgbmls.add("obj_c77575dd90c00001a61346b437301d4f");
	}
}
