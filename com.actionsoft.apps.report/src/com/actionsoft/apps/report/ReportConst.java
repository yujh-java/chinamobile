package com.actionsoft.apps.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoxs
 * @date 2017-09-20 子流程节点定义ID常量类
 *
 */
public class ReportConst {

	public static Map<String, String> subProcessDefid_activityid;
	public static String lxphSub_noteid = "obj_c77af3d031200001a5feac74e6001438";//立项配合研发机构会签
	public static String lxxqSub_noetid = "obj_c775775ad350000140bf3af01c868890";// 立项需求部门会签
	public static String jxphSub_noteid = "obj_c7835a47a4d0000182bccba011086190";//结项配合研发机构会签
	public static String jxxqSub_noteid = "obj_c7835a89c5600001b52a65201f5a1048";//结项需求部门会签
	public static List<String> sub_Activityid;//子流程节点
	static {
		sub_Activityid= new ArrayList<>();
		subProcessDefid_activityid = new HashMap<String, String>();
		subProcessDefid_activityid.put("obj_8b5cc3f8cac84dcc98ce2dbaf57061e8", "obj_c77af3d031200001a5feac74e6001438");// 立项配合研发机构会签
		subProcessDefid_activityid.put("obj_2569912f23c24f48b32ce4bef75e97eb", "obj_c775775ad350000140bf3af01c868890");// 立项需求部门会签
		subProcessDefid_activityid.put("obj_b1e878e45d0e4172afb9dc158152da66", "obj_c7835a47a4d0000182bccba011086190");// 结项配合研发机构会签
		subProcessDefid_activityid.put("obj_f3b1c21c0d4640548bb6ad7c1578e2d5", "obj_c7835a89c5600001b52a65201f5a1048");// 结项需求部门会签
		

		sub_Activityid.add(lxphSub_noteid);// 立项配合研发机构会签
		sub_Activityid.add(lxxqSub_noetid);// 立项需求部门会签
		sub_Activityid.add(jxphSub_noteid);// 结项配合研发机构会签
		sub_Activityid.add(jxxqSub_noteid);// 结项需求部门会签

		subProcessDefid_activityid.put("obj_8426c4500c4a4601af453ca4ce2ac130", "obj_c7835a47a4d0000182bccba011086190");// 结项配合研发机构会签
		subProcessDefid_activityid.put("obj_1659916ce0a5477d9cb268128ad18de7", "obj_c77af3d031200001a5feac74e6001438");// 立项配合研发机构会签
		

		
		

	}

}
