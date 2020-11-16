package com.actionsoft.apps.cmcc.yjyjx;
/**
 * 研究院结项流程的常量类
 * @author pcj
 * @date 2017-09-22
 * 
 * */
import java.util.ArrayList;
import java.util.List;

public class YjyJX_Const {
	public static String xmjlqc_noteid = "obj_c7b0c2f1738000016fe01b971f6a1761";//项目经理起草节点ID
	public static String cwsh_noteid = "obj_c7b0c35e43b00001e3ec19507130cf20";//财务审核节点ID
	public static String xmjlqr_noteid = "obj_c7b0c36730a00001471916f23258fdc0";//项目经理确认节点ID
	public static String jsjlsh_noteid="obj_c7b0c374963000019e12dee01a20a4f0";//技术经理审核节点ID
	public static String qtbsld_noteid = "obj_c7b0c38367b0000111e1111914f0dc60";//牵头部所领导审核节点ID
	public static String xmglry_noteid = "obj_c7b0c38d68a00001ec661ef017b61f42";//项目管理人员审核节点ID
	public static String xmglbmld_noteid = "obj_c7b0c393470000016124c11b13f0b430";//项目管理部门领导审核节点ID
	public static String zgyld_noteid = "obj_c7b0c3a054800001d97244e09ee29f90";//主管院领导审核节点ID
	public static String yz_noteid = "obj_c7b0c3a88ef000014676f8ad7170e510";//院长审批节点ID
	
	
	public static List<String>  yfjg_noteid ;
	public static String yfjg_name = "研发机构意见";
	static{
		yfjg_noteid = new ArrayList<String>();
		yfjg_noteid.add(xmjlqc_noteid);
		yfjg_noteid.add(cwsh_noteid);
		yfjg_noteid.add(xmjlqr_noteid);
		yfjg_noteid.add(jsjlsh_noteid);
		yfjg_noteid.add(qtbsld_noteid);
		yfjg_noteid.add(xmglry_noteid);
		yfjg_noteid.add(xmglbmld_noteid);
		yfjg_noteid.add(zgyld_noteid);
		yfjg_noteid.add(yz_noteid);
		
	}
	

}
