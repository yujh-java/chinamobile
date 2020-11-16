package com.actionsoft.apps.cmcc.budget;
/*
 * 预算管理的常量类
 * @author zhaoxs
 * @date 2017-09-15
 * */
import java.util.ArrayList;
import java.util.List;

public class BudgetConst {
	public static String bsqtrysh_noteid="obj_c7a4858a70d00001ef1475e01c7bdf70";//部所其他人员节点ID
	public static String bsldsh_noteid="obj_c7a485928af0000123ab1b50164586d0";//部门领导审核节点ID
	public static String xzxgbssh_noteid="obj_c7a485c90db000014eec16b040d0a0e0";//选择相关部所审核节点ID
	public static String gkbshq_noteid="obj_c7a485ecb0600001d68971f210b41aff";//归口部所会签节点ID
	public static String tjcw_noteid="obj_c7a485f589c0000126754c70d200184f";//提交财务节点ID
	public static String cwbsh_noteid="obj_c7a485edc740000120196bda1d231a4f";//财务部审核节点ID
	public static String zgyld_noteid="obj_c7a4861b6c500001b29e1110233b1578";//主管院领导审核节点ID

	public static List<String> ysbmyj_list_noteid;//预算部门意见
	public static List<String> gkbmyj_list_noteid;//归口部门意见
	public static List<String> cwbmyj_list_noteid;//财务部门意见
	public static String  ysbmyj_name="预算部门意见";//预算部门意见名称（审批意见中使用）
	public static String  gkbmyj_name="归口部门意见";//归口部门意见名称（审批意见中使用）
	public static String  cwbmyj_name="财务部门意见";//财务部门意见名称（审批意见中使用）
	static{
		ysbmyj_list_noteid = new ArrayList<String>();//预算部门节点
		gkbmyj_list_noteid = new ArrayList<String>();// 归口部门节点
		cwbmyj_list_noteid = new ArrayList<String>();//财务部门节点
		
		ysbmyj_list_noteid.add(bsqtrysh_noteid);
		ysbmyj_list_noteid.add(bsldsh_noteid);
		ysbmyj_list_noteid.add(xzxgbssh_noteid);
		
		gkbmyj_list_noteid.add(gkbshq_noteid);
		
		cwbmyj_list_noteid.add(tjcw_noteid);
		cwbmyj_list_noteid.add(cwbsh_noteid);
		cwbmyj_list_noteid.add(zgyld_noteid);
		
		
	}
	
}
