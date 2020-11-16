package com.actionsoft.apps.cmcc.unplanedProject;
/**
 * 自立、国拨计划外项目流程常量类
 * @author zhaoxs
 * @date 2017-07-17
 * */

import java.util.ArrayList;
import java.util.List;

public class UnplanedProjectConst {
	public static String xmjdqc_noteid = "obj_c795167017800001e9921a031f505550";//项目经理起草节点ID
	public static String jsjl_noteid = "obj_c795167dc540000186ce1c4410b27cd0";//技术经理审核节点ID
	public static String bsld_noteid = "obj_c79516870eb0000148d5f9e61920ea00";//项目牵头部所领导节点ID
	public static String xmglry_noteid = "obj_c79516bb152000013d11756c1bb012b1";//项目管理人员节点ID
	public static String xmglbmld_noteid = "obj_c79516f46ca0000187d770eb12301b28";//项目管理部门领导节点ID
	public static String zgyld_noteid = "obj_c79517171860000111fe15081f0a40c0";//主管院领导节点ID
	public static String yld_noeteid = "obj_c7951732a8c000018260180016e0b550";//院领导审批节点ID
	
	public  static List<String> yfjg_noteid;
	public static String yfjg_name = "研发机构意见";
	static{
		yfjg_noteid = new ArrayList<String>();
		yfjg_noteid.add(xmjdqc_noteid);
		yfjg_noteid.add(jsjl_noteid);
		yfjg_noteid.add(bsld_noteid);
		yfjg_noteid.add(xmglry_noteid);
		yfjg_noteid.add(xmglbmld_noteid);
		yfjg_noteid.add(zgyld_noteid);
		yfjg_noteid.add(yld_noeteid);
	}
}
