package com.actionsoft.apps.cmcc.enterprise;
/**
 * 企标报批稿流程常量类
 * @author zhaoxs
 * @date 2017-09-28
 * */

import java.util.ArrayList;
import java.util.List;

public class ESConst {
	public static String csld_noteid = "obj_c7b2fde583c00001a4aa4ddf16a030e0";//处室领导节点ID
	public static String yfdwESjkr_noteid = "obj_c7b2fdf4dc80000192861d0fa4b03fe0";//研发单位企标管理接口人审核节点ID
	public static String yfdwld_noteid = "obj_c7b2fe115a20000147851bb01f7016fe";//研发单位领审批节点ID
	public static String jsbld_noteid = "obj_c7b2fe1e85700001f93a143e18d91bbe";//技术部领导节点ID
	public static String jsbnbcl_noteid = "obj_c7b2fe299de00001388ae8e514331e5e";//技术部内部处理节点ID
	public static String jtESgly_noeteid = "obj_c7b2fe378f0000011dbf1ed11c481957";//集团企标管理员节点ID
	
	public  static List<String> yfdw_noteid;
	public static List<String> jsb_noteid;
	public static List<String> ES_noteid;
	public static String yfdw_name = "研发单位意见";
	public static String jsb_name="技术部意见";
	public static String ES_name="企标管理员意见";
	static{
		yfdw_noteid = new ArrayList<String>();
		jsb_noteid = new ArrayList<String>();
		ES_noteid = new ArrayList<String>();
		yfdw_noteid.add(csld_noteid);
		yfdw_noteid.add(yfdwESjkr_noteid);
		yfdw_noteid.add(yfdwld_noteid);
		
		jsb_noteid.add(jsbld_noteid);
		jsb_noteid.add(jsbnbcl_noteid);
		
		ES_noteid.add(jtESgly_noeteid);
		
		
	}
}
