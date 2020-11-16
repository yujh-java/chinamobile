package com.actionsoft.apps.cmcc.yjyjx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YjyCommentCont {
	//立项
	public static String lx_zgyld_noteid = "obj_c7b0720c2bb00001fe581a30127fa570";//主管院领导审核节点ID
	public static String lx_yz_noteid = "obj_c7b07218cbb00001637de040f46a1693";//院长审批节点ID
	public static String lx_qtbs_noteid = "obj_c7b071ea466000015d4cf310a5a319f8";//牵头部所领导节点ID
	//结项
	public static String jx_zgyld_noteid = "obj_c7b0c3a054800001d97244e09ee29f90";//主管院领导审核节点ID
	public static String jx_yz_noteid = "obj_c7b0c3a88ef000014676f8ad7170e510";//院长审批节点ID
	public static String jx_qtbs_noteid = "obj_c7b0c38367b0000111e1111914f0dc60";//牵头部所领导节点ID
	//变更
	public static String bg_zgyld_noteid = "obj_c79b753586d00001fb341b4a130715d4";//主管院领导审核节点ID
	public static String bg_yz_noteid = "obj_c79b7541c86000018fe0121bb45d6eb0";//院长审批节点ID
	public static String bg_qtbs_noteid = "obj_c79b74d58d200001d73d171d170010bf";//牵头部所领导节点ID
	//取消
	public static String qx_zgyld_noteid = "obj_c796f4bc23c000019fefe85e16101aac";//主管院领导审核节点ID
	public static String qx_yz_noteid = "obj_c796f4c6d9600001ba531f6916c01548";//院长审批节点ID
	public static String qx_qtbs_noteid = "obj_c796f476965000018dda17b018403b80";//牵头部所领导节点ID
	//计划外立项
	public static String jhw_zgyld_noteid = "obj_c79517171860000111fe15081f0a40c0";//主管院领导审核节点ID
	public static String jhw_yz_noteid = "obj_c7951732a8c000018260180016e0b550";//院长审批节点ID
	public static String jhw_qtbs_noteid = "obj_c79516870eb0000148d5f9e61920ea00";//牵头部所领导节点ID
	
	/*
	 * 院内的主管院领导、院领导节点ID集合
	 */
	public static List<String>  yfjg_noteid = null;
	static{
		yfjg_noteid = new ArrayList<String>();
		yfjg_noteid.add(lx_zgyld_noteid);
		yfjg_noteid.add(lx_yz_noteid);
		yfjg_noteid.add(jx_zgyld_noteid);
		yfjg_noteid.add(jx_yz_noteid);
		yfjg_noteid.add(bg_zgyld_noteid);
		yfjg_noteid.add(bg_yz_noteid);
		yfjg_noteid.add(qx_zgyld_noteid);
		yfjg_noteid.add(qx_yz_noteid);
		yfjg_noteid.add(jhw_zgyld_noteid);
		yfjg_noteid.add(jhw_yz_noteid);
		//yfjg_noteid.add(jhw_qtbs_noteid);
	}
	
	public static Map<String, String>  qtbsld_noteid = new HashMap<String, String>();
	static{
		qtbsld_noteid.put(lx_zgyld_noteid, lx_qtbs_noteid);
		qtbsld_noteid.put(lx_yz_noteid, lx_qtbs_noteid);
		
		qtbsld_noteid.put(jx_zgyld_noteid, jx_qtbs_noteid);
		qtbsld_noteid.put(jx_yz_noteid, jx_qtbs_noteid);
		
		qtbsld_noteid.put(bg_zgyld_noteid, bg_qtbs_noteid);
		qtbsld_noteid.put(bg_yz_noteid, bg_qtbs_noteid);
		
		qtbsld_noteid.put(qx_zgyld_noteid, qx_qtbs_noteid);
		qtbsld_noteid.put(qx_yz_noteid, qx_qtbs_noteid);
		
		qtbsld_noteid.put(jhw_zgyld_noteid, jhw_qtbs_noteid);
		qtbsld_noteid.put(jhw_yz_noteid, jhw_qtbs_noteid);
	}
}
