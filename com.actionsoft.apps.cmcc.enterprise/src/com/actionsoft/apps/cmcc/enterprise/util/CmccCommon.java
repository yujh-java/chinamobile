package com.actionsoft.apps.cmcc.enterprise.util;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CmccCommon{
	//企标复审子流程--需求部门企标管理员环节
	public static String sub_xqbmjkr = "obj_c828178c070000018258f45712a21b34";
	//企标复审子流程--需求部门内部处理环节
	public static String sub_xqbmnbjkr = "obj_c82817a3e61000013ebe180019002d40";
	//企标复审子流程--需求部门领导处理环节
	public static String sub_xqbmldjkr = "obj_c8281796d3e00001772481951910b8e0";
	//企标复审主流程--需求部门企标管理员环节
	public static String z_xqbmjkr = "obj_c82816a505e00001757068403fafda90";
	
	//企标报批子流程--技术部和需求部门会签环节
	public static String sub_jsxqbm = "obj_c85e7d088dd0000193408220a3a42100";
	//企标报批子流程--部门领导审核环节
	public static String sub_bmleader = "obj_c85e7d15aa000001354e52d016eca8b0";
	//企标报批子流程--部门内部处理环节
	public static String sub_bmnb = "obj_c85e7d1d105000011ce7c9f0c93014b1";
	//企标报批主流程--技术部和需求部门会签环节
	public static String z_jsxqbmjkr = "obj_c860becd26d000015ed51770f11b4860";
	
	//企标报批需求部门会签子流程--配合部所会签环节
	public static String sub_bpxqbmhq = "obj_c86bcaa248e00001c0b44b6014201e33";
	//企标报批需求部门会签子流程--配合部所内部会签环节
	public static String sub_bpbmleaderhq = "obj_c86bcaa40b2000013f6210ef1d2e1bdb";
	//企标报批主流程--配合部所会签环节
	public static String z_bpjsxqbmjkrhq = "obj_c86bca28064000017e3753e81d421400";
	//企标报批相关部门会签子流程--相关部门会签环节
	public static String sub_bpbmhq = "obj_c8721d5df14000013b988fb0e1139850";
	//企标水印申请需求部门会签子流程--配合部所会签环节
	public static String sub_qbsybmhq = "obj_c8838037c13000019c5f987316501e56";
	//企标送审（重点）子流程--技术部和需求部门会签环节
	public static String sub_ssxqbm = "obj_c862c360c16000018b6d1f401beb14d7";
	//企标送审（重点）子流程--部门领导审核环节
	public static String sub_ssbmleader = "obj_c862c36582400001e99c1a0010c210be";
	//企标送审（重点）子流程--部门内部处理环节
	public static String sub_ssbmnb = "obj_c862c36e1010000158601c5023501ad8";
	//企标送审（重点）主流程--技术部和需求部门会签环节
	public static String z_ssjsxqbmjkr = "obj_6867329ec1bd42a295e00c877d8b9a08";
	
	//企标送审（非重点）子流程--技术部和需求部门会签环节
	public static String sub_fzdxqbm = "obj_c863f8a8c2a00001d31e17c02ee01827";
	//企标送审（非重点）子流程--部门领导审核环节
	public static String sub_fzdbmleader = "obj_c863f8ad88b0000186e01af019d01ce7";
	//企标送审（非重点）子流程--部门内部处理环节
	public static String sub_fzdbmnb = "obj_c863f8b141200001c38885403a139cf0";
	//企标送审（非重点）主流程--技术部和需求部门会签环节
	public static String z_fzdjsxqbmjkr = "obj_c86405161e4000017d56dfa2178e1b71";
	
	//企标送审（非重点）需求部门会签子流程--配合部所会签环节
	public static String sub_fzdxqbmhq = "obj_c86b193716300001bfc4145013f41800";
	//企标送审（非重点）需求部门会签子流程--配合部所内部会签环节
	public static String sub_fzdbmleaderhq = "obj_c86b193c89c00001c4b927801d402420";
	//企标送审（非重点）主流程--配合部所会签环节
	public static String z_fzdjsxqbmjkrhq = "obj_c86b19b02b4000019aafad4018701989";
	//企标送审（非重点）相关部门会签子流程--相关部门会签环节
	public static String sub_fzdbmhq = "obj_c871cd8e9db000011d735b248e806330";
	
	//企标送审（重点）需求部门会签子流程--配合部所会签环节
	public static String sub_zdxqbmhq = "obj_c86b7141abc00001c8f2448c1370127d";
	//企标送审（重点）需求部门会签子流程--配合部所内部会签环节
	public static String sub_zdbmleaderhq = "obj_c86b7143e6500001e8d314d01f655a30";
	//企标送审（重点）主流程--配合部所会签环节
	public static String z_zdjsxqbmjkrhq = "obj_c86bc3a4c8800001f9b16d901b911077";
	//企标送审（重点）相关部门会签子流程--相关部门会签环节
	public static String sub_zdbmhq = "obj_c871e22fcdd0000165dfa6751de710d4";
	
	//企标计划新增需求部门会签子流程--配合部所会签环节
	public static String sub_bgxqbmhq = "obj_c86b73cbca700001b85b1e15c6c0bcf0";
	//企标计划新增需求部门会签子流程--配合部所内部会签环节
	public static String sub_bgbmleaderhq = "obj_c86b73cda5700001f3b536b05bf914a4";
	//企标计划新增需求部门会签主流程--配合部所会签环节
	public static String z_bgjsxqbmjkrhq = "obj_c86ca589e1d000013e431c701e011c57";
	
	//企标计划变更子流程--技术部和需求部门会签环节
	public static String sub_bgxqbm = "obj_c8606c4c09c0000130cd2070112012b0";
	//企标计划变更子流程--部门领导审核环节
	public static String sub_bgbmleader = "obj_c8606c50a65000017e8c1f901b71dc80";
	//企标计划变更子流程--部门内部处理环节
	public static String sub_bgbmnb = "obj_c8606c54bcc00001435b1692150063e0";
	//企标计划变更主流程--技术部和需求部门会签环节
	public static String z_bgjsxqbmjkr = "obj_c8626e67e2900001227f7abb8940154a";
	//企标计划变更相关部门会签子流程--相关部门会签环节
	public static String sub_bgbmhq = "obj_c87231d530100001598deb9a10501467";
	
	//企标计划变更需求部门会签子流程--配合部所会签环节
	public static String sub_gbxqbmhq = "obj_c86ca927f070000145df813017101fc1";
	//企标计划变更需求部门会签子流程--配合部所内部会签环节
	public static String sub_gbbmleaderhq = "obj_c86ca9297f800001f75818ba44811239";
	//企标计划变更需求部门会签主流程--配合部所会签环节
	public static String z_gbjsxqbmjkrhq = "obj_c86ca862ad800001cdc01e401d421188";
	
	//企标计划取消子流程--技术部和需求部门会签环节
	public static String sub_qxxqbm = "obj_c868888015800001404413141bb91b01";
	//企标计划取消子流程--部门领导审核环节
	public static String sub_qxbmleader = "obj_c8688885591000012d8915a01cd0b800";
	//企标计划取消子流程--部门内部处理环节
	public static String sub_qxbmnb = "obj_c86888891f2000011df21f50193e165c";
	//企标计划取消子流程--技术部和需求部门会签环节
	public static String z_qxjsxqbmjkr = "obj_c868861a06c0000131ad16d01e541f21";
	
	//企标计划取消需求部门会签子流程--配合部所会签环节
	public static String sub_qxxqbmhq = "obj_c86bbe37c77000013f891a3c70fd3300";
	//企标计划取消需求部门会签子流程--配合部所内部会签环节
	public static String sub_qxbmleaderhq = "obj_c86bbe39e8e0000180e56eb013fc37d0";
	//企标计划取消需求部门会签主流程--配合部所会签环节
	public static String z_qxjsxqbmjkrhq = "obj_c86ca72f90f000017b79bc101bc05f00";
	
	//企标计新增消子流程--技术部和需求部门会签环节
	public static String sub_xzxqbm = "obj_c868df54f2e00001a84615f41411192d";
	//企标计新增消子流程--部门领导审核环节
	public static String sub_xzbmleader = "obj_c868df57c160000178601a40f9e09880";
	//企标计新增消子流程--部门内部处理环节
	public static String sub_xzbmnb = "obj_c868df5a54f000012f281a8e15e9a120";
	//企标计新增消子流程--技术部和需求部门会签环节
	public static String z_xzjsxqbmjkr = "obj_c868df1e6770000119691f1e3ba01b40";
	
	//存放已办快照的文件路径
	public static String path = "../webserver/webapps/workflow/formSnapShot/";
	//企标复审主流程--技术部企标管理员审核节点ID
    public static String jsbqbgly = "obj_c82816b468500001c6dd21e118d0ac00";
    //复审流程--承担单位主管领导节点ID
    public static String cddwzgld_stepid = "obj_c82c4bfc4cc000014fd31ce5c8ea126f";
    //复审流程--承担单位内部处理节点ID
    public static String cddwnbcl_stepid = "obj_c82c4c14df40000172364f005aafc260";
	
	  public static String user_leave1 = "1";
	  public static String user_leave2 = "2";
	  public static String user_leave3 = "3";
	  public static String user_leave4 = "4";
	  public static String user_leave5 = "5";
	  public static String user_leave6 = "6";
	  //测试上的
	  public static String pathDeptId_yjy_test = "9de2ff95-b63b-4544-bbea-54120a989d22/3a20fc8a-7157-448d-ad3c-8f82feba842e/";
	  public static String pathDeptId_yjy_prd = "f1c01f98-9184-4e8d-8908-ae5614328586/5b93a3f7-3ae2-4831-9fcc-d8809ffc462c/";
	  public static String deptid_yjy = "f1c01f98-9184-4e8d-8908-ae5614328586";//
	  public static String deptid_sjy = "4f45ced2-36aa-4421-9b8a-aab36e9df402";//设计院中的技术部
	  public static String deptid_jsb = "1f5c54ba-1214-478f-b911-8e7018d3b74b";//技术部
	  
	  //研究院部门全路径集合
	  public static List<String> list_pathDeptId_yjy;
	  static{
		  list_pathDeptId_yjy = new ArrayList<String>();
		  
		  list_pathDeptId_yjy.add(pathDeptId_yjy_test);
		  list_pathDeptId_yjy.add(pathDeptId_yjy_prd);
	  }
	  
	  public static final int ipm_connectTime = 10000;
	  //需求部门角色
	  public static String xqbm_role = "企标需求部门接口人";
	  //集团企标管理员
	  public static String qbgly_role = "企标管理员";
	  
	//部所接口人
	  public static String bmjkr_role = "部门接口人";
	  /*
	   * 研发单位，不同节点ID（报批）
	   */
	  public static String ngr_yfdw_bp = "obj_c7bfdd0789a0000113a31a173db6184e";
	  public static String csld_yfdw_bp = "obj_c7bfdd0e82100001a18b1e30f6931927";
	  public static String jkr_yfdw_bp = "obj_c7bfdd282e0000012e591afa9f508cf0";
	  public static String bmld_yfdw_bp = "obj_c7bfdd29b02000016f9719d2dad81c98";
	  public static String bsjkr_yfdw_bp = "obj_c7d2dae4167000017a1d51ad115f12cc";
	  //chenxf add 2018-07-12
	  public static String bsjs_yfdw_bp = "obj_c80f4f59bd5000011776b29093d01bc6";
	  public static String qbgly_yfdw_bp = "obj_c80c693f7ae000012b3850d84330154b";
	  /*
	   * 研发单位，不同节点ID（重点企标送审）
	   */
	  public static String ngr_yfdw_zdss = "obj_e5fa1ceda4284c29a4af23596017c854";
	  public static String csld_yfdw_zdss = "obj_f0e386a806e644188d828d02714b3956";
	  public static String jkr_yfdw_zdss = "obj_26a09bba00824fafbbdc82e1b2c2189d";
	  public static String bmld_yfdw_zdss = "obj_6cdfba4d7b0a4129a60af8a1ec94056b";
	  public static String bsjkr_yfdw_zdss = "obj_c7d2dfd4f9f000012dd01b2ce1d075a0";
	  
	  
	  /*
	   * 研发单位，不同节点ID（非重点企标送审）
	   */
	  public static String ngr_yfdw_fzdss = "obj_68256bd730344615ad689b807cc3b0a6";
	  public static String csld_yfdw_fzdss = "obj_bb418424344844c3a7b9e2284885c4c7";
	  public static String jkr_yfdw_fzdss = "obj_65a89d3b36d0461d9b96020ae49d1a38";
	  public static String bmld_yfdw_fzdss = "obj_1f71cefd9f844fd28de6b53e1def4fd0";
	  public static String bsjkr_yfdw_fzdss = "obj_c7d2e09349400001cc538730dae0ac70";
	  
	  /*
	   * 研发单位，不同节点ID（企标计划新增）
	   */
	  public static String ngr_yfdw_jhxz = "obj_cb1f4b25814a4e64898e996c0b7b9bf3";
	  public static String bsjs_yfdw_jhxz = "obj_81409103a53a4ad9803b37a340471af2";
	  public static String bsld_yfdw_jhxz = "obj_ca7e58658ac9435eb986a6bbcc51b8e4";
	  public static String bmld_yfdw_jhxz = "obj_e648171fae2f42f0baf1563123bb6753";
	  public static String qbgly_yfdw_jhxz = "obj_f61c9d1dbbf34d5db4cbde107ad4cdb2";
	  
	  /*
	   * 研发单位，不同节点ID（企标计划变更）
	   */
	  public static String ngr_yfdw_jhbg = "obj_33b5266118204c4b95558f1abcd46525";
	  public static String bsjs_yfdw_jhbg = "obj_679d9243ffc540afbe3da923c3307da5";
	  public static String bsld_yfdw_jhbg = "obj_f8185110654d4e64b9f0fd8c9cd65899";
	  public static String bmld_yfdw_jhbg = "obj_a3e82b53b0904e709a0895d56fc590b7";
	  public static String qbgly_yfdw_jhbg = "obj_a7f8294ebaa34847bf2c1588cd3baf0f";
	  
	  /*
	   * 研发单位，不同节点ID（企标计划取消）
	   */
	  public static String ngr_yfdw_jhqx = "obj_fd5cd96fcd6549e589164f55aa5bac23";
	  public static String bsjs_yfdw_jhqx = "obj_b32700475fe64a548d10476a1f227369";
	  public static String bsld_yfdw_jhqx = "obj_b48514cb5d97437e9018e0c1187105e2";
	  public static String bmld_yfdw_jhqx = "obj_fe8a46c607314bd699a1aac78247fd4e";
	  public static String qbgly_yfdw_jhqx = "obj_f5d5473774fa48d7bdc8ccc229fc9634";
	  
	  /*
	   * 承担单位，不同节点ID（企标复审）
	   */
	  public static String gly_yfdw_fs = "obj_c8281691d170000196781e3912764ee0";
	  public static String glycl_yfdw_fs = "obj_c82c4c14df40000172364f005aafc260";
	  public static String bmld_yfdw_fs = "obj_c82c4bfc4cc000014fd31ce5c8ea126f";
	  public static String bsld_yfdw_fs = "obj_c832b5775be0000125421bc011701605";
	  
	  //研发单位list
	  public static List<String> list_yfdwNoteid;
	  public static String yfdwName = "承担单位意见";
	  static{
	    list_yfdwNoteid = new ArrayList();
	    list_yfdwNoteid.add(ngr_yfdw_bp);
	    list_yfdwNoteid.add(csld_yfdw_bp);
	    list_yfdwNoteid.add(jkr_yfdw_bp);
	    list_yfdwNoteid.add(bmld_yfdw_bp);
	    list_yfdwNoteid.add(bsjkr_yfdw_bp);
	    //chenxf add 2018-07-12
	    list_yfdwNoteid.add(bsjs_yfdw_bp);
	    list_yfdwNoteid.add(qbgly_yfdw_bp);
	    
	    list_yfdwNoteid.add(ngr_yfdw_zdss);
	    list_yfdwNoteid.add(csld_yfdw_zdss);
	    list_yfdwNoteid.add(jkr_yfdw_zdss);
	    list_yfdwNoteid.add(bmld_yfdw_zdss);
	    list_yfdwNoteid.add(bsjkr_yfdw_zdss);
	    
	    list_yfdwNoteid.add(ngr_yfdw_fzdss);
	    list_yfdwNoteid.add(csld_yfdw_fzdss);
	    list_yfdwNoteid.add(jkr_yfdw_fzdss);
	    list_yfdwNoteid.add(bmld_yfdw_fzdss);
	    list_yfdwNoteid.add(bsjkr_yfdw_fzdss);
	    /*企标计划新增*/
	    list_yfdwNoteid.add(ngr_yfdw_jhxz);
	    list_yfdwNoteid.add(bsjs_yfdw_jhxz);
	    list_yfdwNoteid.add(bsld_yfdw_jhxz);
	    list_yfdwNoteid.add(bmld_yfdw_jhxz);
	    list_yfdwNoteid.add(qbgly_yfdw_jhxz);
	    /*企标计划变更*/
	    list_yfdwNoteid.add(ngr_yfdw_jhbg);
	    list_yfdwNoteid.add(bsjs_yfdw_jhbg);
	    list_yfdwNoteid.add(bsld_yfdw_jhbg);
	    list_yfdwNoteid.add(bmld_yfdw_jhbg);
	    list_yfdwNoteid.add(qbgly_yfdw_jhbg);
	    /*企标计划取消*/
	    list_yfdwNoteid.add(ngr_yfdw_jhqx);
	    list_yfdwNoteid.add(bsjs_yfdw_jhqx);
	    list_yfdwNoteid.add(bsld_yfdw_jhqx);
	    list_yfdwNoteid.add(bmld_yfdw_jhqx);
	    list_yfdwNoteid.add(qbgly_yfdw_jhqx);
	    /*企标复审*/
	    list_yfdwNoteid.add(gly_yfdw_fs);
	    list_yfdwNoteid.add(glycl_yfdw_fs);
	    list_yfdwNoteid.add(bmld_yfdw_fs);
	    list_yfdwNoteid.add(bsld_yfdw_fs);
	   
	  }
	  /*
	   * 技术单位不同节点ID（报批）
	   */
	  public static String bmld_jsdw_bp = "obj_c7bfdd2f61100001d67c90a899605590";
	  public static String nbry_jsdw_bp = "obj_c7bfdd37d5000001ae6e4b3c10006c40";
	  public static String gly_jsdw_bp = "obj_c7bfdd4949800001c18618e6780b11e1";
	  //chenxf add 2018-07-12
	  public static String qbgly_jsdw_bp = "obj_c80c69accc80000180e811f04f96a3d0";
	  public static String jsb_jsdw_bp = "obj_c80cd0589b40000176edd15019e01ad7";
	  //chenxf add 2018-08-13
	  public static String jsbnb_jsdw_bp = "obj_c817ac97d0600001f1f61f00c4201ed8";
	  /*
	   * 技术单位不同节点ID（重点企标送审）
	   */
	  public static String bmld_jsdw_zdss = "obj_6867329ec1bd42a295e00c877d8b9a08";
	  public static String nbry_jsdw_zdss = "obj_98ac464cfff14ee492523d6e3b7ad287";
	  public static String gly_jsdw_zdss= "obj_b936e952a4c749d8a0b1f7c926fe8da0";
	  //chenxf add 2018-08-13
	  public static String jsbnb_jsdw_zdss = "obj_c817ad3182b0000180b64900135612a1";
	  
	  /*
	   * 技术单位不同节点ID（企标计划新增）
	   */
	  public static String gly_jsdw_jhxz = "obj_17a858d6cc904cd3a5b9e61e6741cb92";
	  public static String bmld_jsdw_jhxz = "obj_66a9206431974ba4a78ce6d76839aa09";
	  public static String jsbnb_jsdw_jhxz = "obj_865bd3acc1f740a8a7960fd6744bf35e";
	  
	  /*
	   * 技术单位不同节点ID（企标计划变更）
	   */
	  public static String gly_jsdw_jhbg = "obj_f51bebb9a8d547aa906c7f587a721ae5";
	  public static String bmld_jsdw_jhbg = "obj_1d2eb4d05c454ea3ac2599777ca0a3a5";
	  public static String jsbnb_jsdw_jhbg = "obj_0d5e3aed5d9841c4a391031d15dd7246";
	  /*
	   * 技术单位不同节点ID（企标计划取消）
	   */
	  public static String gly_jsdw_jhqx = "obj_e62756dd846a43a2b653933ec6512896";
	  public static String bmld_jsdw_jhqx = "obj_3ba90018628d401b889c7a88077af447";
	  public static String jsbnb_jsdw_jhqx = "obj_0954a5f82ad74fd288a571ac20cfe38c";
	  /*
	   * 技术单位不同节点ID（企标复审）
	   */
	  public static String bmld_jsdw_fs = "obj_c82816c5c070000137817a1017109f50";
	  public static String jsbnb_jsdw_fs = "obj_c82816cd93600001cf64bc008db023c0";
	  
	  //技术单位list
	  public static List<String> list_jsdwNoteid;
	  public static String jsdwName = "技术部意见";
	  static{
		  	list_jsdwNoteid = new ArrayList();
		    list_jsdwNoteid.add(bmld_jsdw_bp);
		    list_jsdwNoteid.add(nbry_jsdw_bp);
		    list_jsdwNoteid.add(gly_jsdw_bp);
		    //chenxf add 2018-07-12
		    list_jsdwNoteid.add(qbgly_jsdw_bp);
		    list_jsdwNoteid.add(jsb_jsdw_bp);
		    
		    
		    list_jsdwNoteid.add(bmld_jsdw_zdss);
		    list_jsdwNoteid.add(nbry_jsdw_zdss);
		    list_jsdwNoteid.add(gly_jsdw_zdss);
		    //chenxf add 2018-08-13
		    list_jsdwNoteid.add(jsbnb_jsdw_bp);
		    list_jsdwNoteid.add(jsbnb_jsdw_zdss);
		    /*企标计划新增*/
		    list_jsdwNoteid.add(gly_jsdw_jhxz);
		    list_jsdwNoteid.add(bmld_jsdw_jhxz);
		    list_jsdwNoteid.add(jsbnb_jsdw_jhxz);
		    /*企标计划变更*/
		    list_jsdwNoteid.add(gly_jsdw_jhbg);
		    list_jsdwNoteid.add(bmld_jsdw_jhbg);
		    list_jsdwNoteid.add(jsbnb_jsdw_jhbg);
		    /*企标计划取消*/
		    list_jsdwNoteid.add(gly_jsdw_jhqx);
		    list_jsdwNoteid.add(bmld_jsdw_jhqx);
		    list_jsdwNoteid.add(jsbnb_jsdw_jhqx);
		    /*企标复审*/
		    list_jsdwNoteid.add(bmld_jsdw_fs);
		    list_jsdwNoteid.add(jsbnb_jsdw_fs);
	  }
	  
	  /*
	   * 需求单位不同节点ID（非重点企标送审）
	   */
	  public static String jkr_xqdw_fzdss = "obj_c7c8d57ab9600001e25374ca1070173a";//需求单位企标管理员处理
	  public static String bmld_xqdw_fzdss = "obj_60ea0f6e713a4fba84cc66cb87f63cc0";
	  public static String nbry_xqdw_fzdss = "obj_ca202f305cd0460c977bd4abc6bbd87d";//需求部门审核
	  public static String nbsh_xqdw_fzdss = "obj_c817c0f2b8f0000129fd10d11d64159c";//需求部门内部审核
	  
	  /*
	   * 需求单位不同节点ID（企标复审）
	   */
	  public static String gly_xqdw_fs = "obj_c828178c070000018258f45712a21b34";//需求企标管理员
	  public static String bmld_xqdw_fs = "obj_c8281796d3e00001772481951910b8e0";//需求部门领导
	  public static String bmnb_xqdw_fs = "obj_c82817a3e61000013ebe180019002d40";//需求部门内部审核
	  
	  /*
	   * wxx add 2019-03-29
	   * 需求单位不同节点ID（企标报批子流程）
	   */
	  public static String gly_xqdw_bp = "obj_c85e7d088dd0000193408220a3a42100";//需求企标管理员（技术部和需求部门会签节点）
	  public static String bmld_xqdw_bp = "obj_c85e7d15aa000001354e52d016eca8b0";//需求部门领导（部门领导审核节点）
	  public static String bmnb_xqdw_bp = "obj_c85e7d1d105000011ce7c9f0c93014b1";//需求部门内部审核（部门内部处理节点）
	  
	  /*
	   * wxx add 2019-04-01
	   * 需求单位不同节点ID（企标送审（重点）子流程）
	   */
	  public static String gly_xqdw_ss = "obj_c862c360c16000018b6d1f401beb14d7";//需求企标管理员（技术部和需求部门会签节点）
	  public static String bmld_xqdw_ss = "obj_c862c36582400001e99c1a0010c210be";//需求部门领导（部门领导审核节点）
	  public static String bmnb_xqdw_ss = "obj_c862c36e1010000158601c5023501ad8";//需求部门内部审核（部门内部处理节点）
	  
	  /*
	   * wxx add 2019-04-02
	   * 需求单位不同节点ID（企标送审（非重点）子流程）
	   */
	  public static String gly_xqdw_fzd = "obj_c863f8a8c2a00001d31e17c02ee01827";//需求企标管理员（技术部和需求部门会签节点）
	  public static String bmld_xqdw_fzd = "obj_c863f8ad88b0000186e01af019d01ce7";//需求部门领导（部门领导审核节点）
	  public static String bmnb_xqdw_fzd = "obj_c863f8b141200001c38885403a139cf0";//需求部门内部审核（部门内部处理节点）
	  
	  /*
	   * wxx add 2019-04-01
	   * 需求单位不同节点ID（企标计划变更子流程）
	   */
	  public static String gly_xqdw_bg = "obj_c8606c4c09c0000130cd2070112012b0";//需求企标管理员（技术部和需求部门会签节点）
	  public static String bmld_xqdw_bg = "obj_c8606c50a65000017e8c1f901b71dc80";//需求部门领导（部门领导审核节点）
	  public static String bmnb_xqdw_bg = "obj_c8606c54bcc00001435b1692150063e0";//需求部门内部审核（部门内部处理节点）
	  
	  //wxx add 2018-07-16
	  //public static String qbgly_jsdw_fzd = "obj_c7c8d57ab9600001e25374ca1070173a";//需求单位企标管理员处理
	  //public static String xqb_jsdw_fzd = "obj_ca202f305cd0460c977bd4abc6bbd87d";//需求部门审核
	  //wxx add 2019-047-03
	  //技术部和需求单位list
	  /*public static List<String> list_jsbxqdwNoteid;
	  public static String jsbxqdwName = "技术部和需求单位意见";
	  static{
		 
		  
		   * wxx add 2019-03-29
		   * 企标报批子流程--需求部门节点ID
		   
		  list_jsbxqdwNoteid.add(gly_xqdw_bp);
		  list_jsbxqdwNoteid.add(bmld_xqdw_bp);
		  list_jsbxqdwNoteid.add(bmnb_xqdw_bp);
		  
		   * wxx add 2019-04-03
		   * 企标送审（重点）子流程--需求部门节点ID
		   
		  list_jsbxqdwNoteid.add(gly_xqdw_ss);
		  list_jsbxqdwNoteid.add(bmld_xqdw_ss);
		  list_jsbxqdwNoteid.add(bmnb_xqdw_ss);
		  
		  
		   * wxx add 2019-04-02
		   * 企标送审（非重点）子流程--需求部门节点ID
		   
		  list_jsbxqdwNoteid.add(gly_xqdw_fzd);
		  list_jsbxqdwNoteid.add(bmld_xqdw_fzd);
		  list_jsbxqdwNoteid.add(bmnb_xqdw_fzd);
		  
		  
		    
	  }*/
	  
	  
	  //技术单位list
	  public static List<String> list_xqdwNoteid;
	  public static String xqdwName = "需求单位意见";
	  static{
		  list_xqdwNoteid = new ArrayList();
		  list_xqdwNoteid.add(jkr_xqdw_fzdss);
		  list_xqdwNoteid.add(bmld_xqdw_fzdss);
		  list_xqdwNoteid.add(nbry_xqdw_fzdss);
		  list_xqdwNoteid.add(nbsh_xqdw_fzdss);
		  /*
		   * 企标复审--需求部门节点ID
		   */
		  list_xqdwNoteid.add(gly_xqdw_fs);
		  list_xqdwNoteid.add(bmld_xqdw_fs);
		  list_xqdwNoteid.add(bmnb_xqdw_fs);
		  /*
		   * wxx add 2019-03-29
		   * 企标报批子流程--需求部门节点ID
		   */
		  list_xqdwNoteid.add(gly_xqdw_bp);
		  list_xqdwNoteid.add(bmld_xqdw_bp);
		  list_xqdwNoteid.add(bmnb_xqdw_bp);
		  
		  /*
		   * wxx add 2019-04-01
		   * 企标送审（重点）子流程--需求部门节点ID
		   */
		  list_xqdwNoteid.add(gly_xqdw_ss);
		  list_xqdwNoteid.add(bmld_xqdw_ss);
		  list_xqdwNoteid.add(bmnb_xqdw_ss);
		  
		  /*
		   * wxx add 2019-04-02
		   * 企标送审（非重点）子流程--需求部门节点ID
		   */
		  list_xqdwNoteid.add(gly_xqdw_fzd);
		  list_xqdwNoteid.add(bmld_xqdw_fzd);
		  list_xqdwNoteid.add(bmnb_xqdw_fzd);
		  
		  /*
		   * wxx add 2019-04-01
		   * 企标计划变更子流程--需求部门节点ID
		   */
		  list_xqdwNoteid.add(gly_xqdw_bg);
		  list_xqdwNoteid.add(bmld_xqdw_bg);
		  list_xqdwNoteid.add(bmnb_xqdw_bg);
		  
		  
		    
	  }
	  /*
	   * 企标管理员--企标复审节点ID
	   */
	  public static String qbglyqc_zb_fs = "obj_c8281682fdd00001364a18f662001af4";//企标管理员起草--总部技术部
	  public static String qbglycl_zb_fs = "obj_c828169b2f30000130a44770165e1e00";//企标管理员处理--总部技术部
	  public static String qbglysh_zb_fs = "obj_c82816b468500001c6dd21e118d0ac00";//企标管理员审核--总部技术部
	  //企标管理员list
	  public static List<String> list_qbglyNoteid;
	  public static String qbglyName = "企标管理员意见";
	  static{
		  list_qbglyNoteid = new ArrayList();
		  list_qbglyNoteid.add(qbglyqc_zb_fs);
		  list_qbglyNoteid.add(qbglycl_zb_fs);
		  list_qbglyNoteid.add(qbglysh_zb_fs);
		  
	  }
	  
	  //获取兼职角色“集团企标管理员”的人员所在部门ID集合
	  public static String bmid_ep_manager_sql = "SELECT GROUP_CONCAT(u.DEPARTMENTID) deptid "
							  		+ "FROM ORGUSERMAP m, ORGROLE r, ORGUSER u "
							  		+ "WHERE m.ROLEID = r.ID "
							  		+ "AND m.userid = u.userid "
							  		+ "AND r.ROLENAME = '企标管理员'";
	  //集团企标管理员部门ID集合
	  public static String bmid_ep_manager;
	  static{
		  bmid_ep_manager = DBSql.getString(bmid_ep_manager_sql);
	  }
	  
	  //获取兼职角色“集团企标管理员”的人员账号集合
	  public static String userid_ep_manager_sql = "select GROUP_CONCAT(m.USERID) userid "
							+ "from ORGUSERMAP m,ORGROLE r "
							+ "where m.ROLEID = r.ID "
							+ "and r.ROLENAME = '企标管理员'";
	  //集团企标管理员账号集合
	  public static String userid_ep_manager;
	  static{
		  userid_ep_manager = DBSql.getString(userid_ep_manager_sql);
	  }
	  /**
	   * 通知指定人员
	   * @param processInst
	   * @param parentTaskInst
	   * @param uc
	   * @param userid 指定人员，多个用空格隔开
	   * @param title
	   */
	  public static void circulatePeople(String processInst,
			  String parentTaskInst,
			  UserContext uc,
			  String userid,
			  String title){
		  //发送传阅任务
//		  SDK.getTaskAPI().
//		  		createUserCCTaskInstance(
//		  				processInst, 
//		  				parentTaskInst, 
//		  				uc.getUID(), 
//		  				userid, 
//		  				title);
		  //发送通知任务
		  SDK.getTaskAPI().
		  		createUserNotifyTaskInstance(
		  				processInst, 
		  				parentTaskInst, 
		  				uc.getUID(),
		  				userid, 
		  				title);
	  }
	  /**
	   * 在角色维护表中：BO_ACT_ENTERPRISE_ROLEDATA
	   * 获取指定角色名、部门ID的人员账号
	   * @param rolename 指定角色
	   * @param deptId 指定部门ID
	   * @return
	   */
	  public String getEnterpriseManager(String rolename,String deptId){
		  //在维护表中获取集团企标管理员角色的账号
		  String sql = "SELECT GROUP_CONCAT(userid) userid "
					  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
					  		+ "WHERE ROLENAME = '"+rolename+"' ";
		  
		  /*
		   * 如果指定部门ID不为空
		   */
		  if(deptId != null && !"".equals(deptId)){
			  String[] deptids = deptId.split(" ");
			  for(int i = 0;i<deptids.length;i++){
				  String deptid = deptids[i];
				  //如果为空，过滤
				  if(UtilString.isEmpty(deptid)){
					  continue;
				  }
				  deptid = deptid.trim();
				  System.err.println("======deptid:"+deptid+"========");
				  //当前人部门全路径
				  String pathCurrentDeptId = DepartmentCache.getModel(deptid).getPathIdOfCache();
				  if(i==0){
					  sql += "AND (locate(deptid, '"+pathCurrentDeptId+"' ) > 0 ";
				  }else{
					  sql += "OR locate(deptid, '"+pathCurrentDeptId+"' ) > 0 ";
				  }
			  }
			  sql += ")";
		  }
		  System.err.println("===获取维护表中角色的sql："+sql+"======");
		  
		  String people = DBSql.getString(sql);
		  System.err.println("人人人人"+people);
		  //把字符串中逗号全部替换成空格
		  if(people != null && !"".equals(people)){
			  people = people.replaceAll(",", " ");
		  }
		  return people;
	  }
	 /**
	  * 保存第三方表单为快照
	  * @author chenxf
	  * @date   2018年9月29日 下午5:40:57
	  * @param uc
	  * @param bodyHTML
	  * @param headHTML
	  * @return
	  */
	  public String splicingStaticHtml(String bodyHTML,String headHTML){
		  
		  StringBuffer sb = new StringBuffer();
		  sb.append("<!DOCTYPE html>");
		  sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"zh_CN\">");
		  sb.append("<head>");
		  sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
		  sb.append(headHTML);
		  sb.append("</head>");
		  sb.append("<body>");
		  sb.append(bodyHTML);
		  sb.append("</body>");
		  sb.append("</html>");
		  
		  return sb.toString();
	  }
	/**
	 * 根据流程定义ID，判断是否是主流程
	 * 
	 * @author nch
	 * @date 2017-10-20
	 * @param processdefid
	 * @return
	 */
	public static boolean isMainProcess(String processdefid) {

		List<BO> list = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSDEFNID = ", processdefid)
				.addQuery("ISMAIN = ", "否").list();
		boolean bol = true;
		if (list != null && list.size() > 0) {
			bol = false;
		}
		//System.err.println("listsize"+list.size());
		return bol;
	}
	 /**
	   * 通过部门表中outerid字段获取部门ID的字符串
	   * @author chenxf
	   * @date   2018年7月25日 下午2:58:26
	   * @param outerid
	   * @return
	   */
	  public static String getDeptidByOuterid(String outerid){
		  StringBuffer sb = new StringBuffer();
		  if(!UtilString.isEmpty(outerid)){
			  //拆分
			  String[] outerids = outerid.split(" ");
			  //遍历
			  for(int i = 0;i<outerids.length;i++){
				  String id = outerids[i];
				  if(UtilString.isEmpty(id)){
					  continue;
				  }
				  id = id.trim();
				  //获取流程平台的部门ID
				  String deptid = DBSql.getString("select id from ORGDEPARTMENT "
				  										+ "where outerid = ?", new Object[] { id });
				  sb.append(deptid + " ");
			  }
		  }
		  return sb.toString();
	  }
}
