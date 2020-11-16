
package com.actionsoft.apps.cmcc.itemChange;
/**
 * 项目变更流程常量类
 * @author zhaoxs
 * @date 2017-7-18
 */
import java.util.ArrayList;
import java.util.List;

public class ItemChangeConst {
	public static String xmjlqc_noteid = "obj_c79b74ac24f00001a044d6c01eb0cb50";//项目经理起草节点ID
	public static String jsjlsh_noteid = "obj_c79b74ba77000001f8a4c8e0169e7da0";//技术经理审核节点ID
	public static String xmqtbsld_noteid = "obj_c79b74d58d200001d73d171d170010bf";//项目牵头部所领导审核节点ID
	public static String xmglry_noteid = "obj_c79b74efd7d00001fc2a4600aa1088e0";//项目管理人员审核节点ID
	public static String xmglbmld_noteid = "obj_c79b75036e600001b62fd6901fc08810";//项目管理部门领导审核节点ID
	public static String zgyld_noteid = "obj_c79b753586d00001fb341b4a130715d4";//主管院领导审核节点ID
	public static String yld_noteid = "obj_c79b7541c86000018fe0121bb45d6eb0";//院领导审核节点ID
	
	public static List<String>list_noteid;
	public static String list_yfjgName = "研发机构意见";
	
	static {
		list_noteid = new ArrayList<String>();
		list_noteid.add(xmjlqc_noteid);
		list_noteid.add(jsjlsh_noteid);
		list_noteid.add(xmqtbsld_noteid);
		list_noteid.add(xmglry_noteid);
		list_noteid.add(xmglbmld_noteid);
		list_noteid.add(zgyld_noteid);
		list_noteid.add(yld_noteid);
		
	}
	
	
	
	
	

}
