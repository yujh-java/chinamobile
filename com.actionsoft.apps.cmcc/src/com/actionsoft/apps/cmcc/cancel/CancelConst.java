package com.actionsoft.apps.cmcc.cancel;
/**
 * 取消终止流程的常量类
 * @author zhaoxs
 * @date 2017-07-17
 * 
 * */
import java.util.ArrayList;
import java.util.List;

public class CancelConst {
	public static String xmjlqcnoteid = "obj_c796f4458360000113d21d3571de5870";//项目经理起草节点ID
	public static String cwsh_noteid = "obj_c796f44eaa700001a3dd1d701b201c54";//财务审核节点ID
	public static String xmjlqr_noteid = "obj_c796f45ac4e000011ca41750194a1fe7";//项目经理确认节点ID
	public static String jsjlsh_noteid="obj_c796f46a647000013f4ad0001070e690";//技术经理审核节点ID
	public static String qtbsld_noteid = "obj_c796f476965000018dda17b018403b80";//牵头部所领导审核节点ID
	public static String xmglry_noteid = "obj_c796f487e3f00001aca8e350d12f174b";//项目管理人员审核节点ID
	public static String xmglbmld_noteid = "obj_c796f4a05230000172731d3c4256c830";//项目管理部门领导审核节点ID
	public static String zgyld_noteid = "obj_c796f4bc23c000019fefe85e16101aac";//主管院领导审核节点ID
	public static String yz_noteid = "obj_c796f4c6d9600001ba531f6916c01548";//院长审批节点ID
	
	
	public static List<String>  yfjg_noteid ;
	public static String yfjg_name = "研发机构意见";
	static{
		yfjg_noteid = new ArrayList<String>();
		yfjg_noteid.add(xmjlqcnoteid);
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
