package com.actionsoft.apps.cmcc.titleScore;
/**
 * 结题评分流程常量类
 * @author zhaoxs
 * @date 2017-06-27
 */
import java.util.ArrayList;
import java.util.List;


public class TitleScoreConst {
	public static String xqbmjkr_noteid = "obj_c76ec12c42f000012910aa328767c730";// 第一个需求部门接口人处理节点ID
	public static String xmjl_noteid = "obj_c7bc056f2e400001a92d1db91e002060";// 需求部门内部项目经理处理节点ID
	public static String nbcs_noteid = "obj_c7bc055627b00001a5c41f10d84013b7";// 需求部门内部处室处理节点ID
	public static String bmld_noteid = "obj_c76ec14218e0000175f51dd0b643ff60";// 需求部门领导处理节点ID
	public static String xmbmjkr_stepid = "obj_c7bc055e55a00001f210c7402223fd10";// 第二个需求部门接口人处理节点ID
	public static String gkbmld_noteid = "obj_c76ec15e977000019849120170609640";// 归口部门领导处理节点ID
	public static String yfxmjkr_noeteid = "obj_c76ec165f7400001baefc2e01230fbb0";// 研发项目接口人处理节点ID

	public static List<String> list_jsbyjNoteid;
	public static String jsbyjName = "技术部意见";
	static {
		list_jsbyjNoteid = new ArrayList<String>();
		list_jsbyjNoteid.add(gkbmld_noteid);
		list_jsbyjNoteid.add(yfxmjkr_noeteid);
	}
	public static List<String> list_xqbmyjNoteid;
	public static String xqbmyjName = "需求部门意见";
	static {
		list_xqbmyjNoteid = new ArrayList<String>();
		list_xqbmyjNoteid.add(xqbmjkr_noteid);
		list_xqbmyjNoteid.add(bmld_noteid);
		list_xqbmyjNoteid.add(nbcs_noteid);
		list_xqbmyjNoteid.add(xmjl_noteid);
		list_xqbmyjNoteid.add(xmbmjkr_stepid);

	}

}
