package com.actionsoft.apps.cmcc.budget.ys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YsCommentCont {
	public static String lx_ysgk_noteid = "obj_c808ef6042300001c45f3b10d4101a46";//预算归口管理部门会签节点ID
	public static String lx_zgyld_noteid = "obj_c7db7e2ed53000019f481e901420b460";//主管院领导审核节点ID
	////////////
	public static String lx_cw_noteids = "obj_c7db7e0b018000016f37147040d3e560";//财务部审核
	public static String lx_xz_noteids = "obj_c7db7d3e8bd000013a3624a417e0b040";//选择相关部所审核
	///////////////
	/*
	 * 院内的主管院领导、院领导节点ID集合
	 */
	public static List<String>  yfjg_noteid = null;
	static{
		yfjg_noteid = new ArrayList<String>();
		yfjg_noteid.add(lx_zgyld_noteid);//主管院领导审核节点
		yfjg_noteid.add(lx_xz_noteids);//选择相关部所审核

	}
	
	public static Map<String, String>  qtbsld_noteid = new HashMap<String, String>();
	static{
		qtbsld_noteid.put(lx_zgyld_noteid, lx_cw_noteids);
		qtbsld_noteid.put(lx_xz_noteids, lx_cw_noteids);

	}

}
