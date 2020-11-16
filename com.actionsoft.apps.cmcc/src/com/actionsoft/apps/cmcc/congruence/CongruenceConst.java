package com.actionsoft.apps.cmcc.congruence;
/*
 * 合同计提流程常量类
 * @author zhaoxs
 * @date 2017-09-15
 * */
import java.util.ArrayList;
import java.util.List;

public class CongruenceConst {
	public static String xmjlsh_noteid="obj_c7a473feb09000011be11a607a101e6d";//项目经理审核节点ID
	public static String htjkr_noteid="obj_c7a4741738200001e96315e0857094b0";//部门合同接口人节点ID
	public static String bmld_noteid="obj_c7a4741c39900001ba3141405fa83180";//部门领导审核节点ID

	public static List<String> bmyj_noteid;
	public static String bmyj_name="意见";
	static{
		bmyj_noteid = new ArrayList<String>();
		bmyj_noteid.add(xmjlsh_noteid);
		bmyj_noteid.add(htjkr_noteid);
		bmyj_noteid.add(bmld_noteid);
		
	}
	
}
