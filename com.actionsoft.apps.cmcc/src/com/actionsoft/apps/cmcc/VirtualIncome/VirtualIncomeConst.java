package com.actionsoft.apps.cmcc.VirtualIncome;
/**
 * 虚拟收入流程常量类
 * @author zhaoxs
 * @date 2017-06-27
 */
import java.util.ArrayList;
import java.util.List;
public class VirtualIncomeConst {
	
	//研发机构意见
	public static String yfjgjkr_noteid = "obj_c7ac8145bdb00001a1846340e6602b30";//研发机构接口人反馈进度节点ID
	
	//需求部门意见
	public static String xqbmwsg_noteid = "obj_c7ac817c52c00001c2c91b401cb01383";//需求部门文书岗节点ID
	public static String xqbmld_noteid = "obj_c7ac81c4cab00001354c3546b7f12340";//需求部门领导审批 节点ID
	
	//技术部意见
	public static String zbyfjkr_noteid = "obj_c7ac816177d00001b44a4050108811d3";//总部研发项目接口人处理节点ID
	public static String zbyfxm_noteid = "obj_c7ac81db6bc00001ea231380b05e1e4d";//总部研发项目接口人节点ID

	

	
	
	public static List<String> list_xqbmyjNoteid;
	public static String xqbmyjName = "需求部门意见";//需求部门意见
	static {
		list_xqbmyjNoteid = new ArrayList<String>();
		list_xqbmyjNoteid.add(xqbmwsg_noteid);
		list_xqbmyjNoteid.add(xqbmld_noteid);
	}
	public static List<String> list_jsbyjNoteid;
	public static String jsbyjName = "技术部意见";//技术部意见
	static{
		list_jsbyjNoteid = new ArrayList<String>();
		list_jsbyjNoteid.add(zbyfjkr_noteid);
		list_jsbyjNoteid.add(zbyfxm_noteid);	
	}
	
	public static List<String> list_yfjgNoteid;
	public static String yfjgyjName = "研发机构意见";//研发机构意见
	static{
		list_yfjgNoteid = new ArrayList<String>();
		list_yfjgNoteid.add(yfjgjkr_noteid);
	}
	
}
