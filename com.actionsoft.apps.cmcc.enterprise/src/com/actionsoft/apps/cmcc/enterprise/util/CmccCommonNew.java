package com.actionsoft.apps.cmcc.enterprise.util;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CmccCommonNew {
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
		//企标送审（重点）子流程--部门领导审核环节
		public static String sub_fzdbmleader = "obj_c863f8ad88b0000186e01af019d01ce7";
		//企标送审（重点）子流程--部门内部处理环节
		public static String sub_fzdbmnb = "obj_c863f8b141200001c38885403a139cf0";
		//企标送审（重点）主流程--技术部和需求部门会签环节
		public static String z_fzdjsxqbmjkr = "obj_c86405161e4000017d56dfa2178e1b71";
		
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
		  /*
		   * 研发单位，不同节点ID（报批）
		   */
		  public static String ngr_yfdw_bp = "obj_c7bfdd0789a0000113a31a173db6184e";
		  public static String csld_yfdw_bp = "obj_c7bfdd0e82100001a18b1e30f6931927";
		  public static String jkr_yfdw_bp = "obj_c7bfdd282e0000012e591afa9f508cf0";
		  public static String bmld_yfdw_bp = "obj_c7bfdd29b02000016f9719d2dad81c98";
		  public static String bsjkr_yfdw_bp = "obj_c7d2dae4167000017a1d51ad115f12cc";
		  public static String qbglysh_gjr_bp = "obj_c85e39fe4ce00001f096bf1022581c07";//企标单位管理员审核节点
		  public static String qbglysh_gjr_bp_1 = "obj_c8721eebf0200001f11c157016684500";//企标单位管理员审核节点
		  public static String phbshq_gjr_bp = "obj_c86bcaa248e00001c0b44b6014201e33";//企标报批子流程配合部所会签节点
		  public static String phnbhq_gjr_bp = "obj_c86bcaa40b2000013f6210ef1d2e1bdb";//企标报批子流程配合部所内部会签节点
		  public static String phnbhq_phnb_bp = "obj_c8726f90e2100001c7e01e00ece01e22";
		  public static String phnbhq_bsld_bp = "obj_c86bca2c9ac000018f181890cdb99170";
		  //chenxf add 2018-07-12
		  public static String bsjs_yfdw_bp = "obj_c80f4f59bd5000011776b29093d01bc6";
		  public static String qbgly_yfdw_bp = "obj_c80c693f7ae000012b3850d84330154b";
		  public static String qbgly_jsb_bp = "obj_c8672f6a58400001c2d5e1371b101d88";
		  public static String bshq_xgbm_bp = "obj_c8721d5df14000013b988fb0e1139850";
		  /*
		   * 研发单位，不同节点ID（重点企标送审）
		   */
		  public static String ngr_yfdw_zdss = "obj_e5fa1ceda4284c29a4af23596017c854";
		  public static String csld_yfdw_zdss = "obj_f0e386a806e644188d828d02714b3956";
		  public static String jkr_yfdw_zdss = "obj_26a09bba00824fafbbdc82e1b2c2189d";
		  public static String bmld_yfdw_zdss = "obj_6cdfba4d7b0a4129a60af8a1ec94056b";
		  public static String bsjkr_yfdw_zdss = "obj_c7d2dfd4f9f000012dd01b2ce1d075a0";
		  public static String qbglysh_yfdw_zdss = "obj_6cdfba4d7b0a4129a60af8a1ec94056b";//企标单位管理员审核节点
		  public static String qbglysh_yfdw_zdss_1 = "obj_c871e3b73b400001915a1fe07bd01009";//企标单位管理员审核节点
		  public static String qbglycl_yfdw_zdss = "obj_c862bda025500001557c14d03d401229";//企标单位管理员处理节点
		  
		  public static String phbs_yfdw_zdss = "obj_c86b7141abc00001c8f2448c1370127d";//企标送审重点配合部所会签子流程配合部所会签节点
		  public static String phbsnb_yfdw_zdss = "obj_c86b7143e6500001e8d314d01f655a30";//企标送审重点配合部所会签子流程配合部所内部会签节点
		  public static String xqbm_yfdw_zdss = "obj_c871e22fcdd0000165dfa6751de710d4";
		  public static String xqbm_bsnb_zdss = "obj_c871e1e1414000012baca87017e0178a";
		  public static String bsjs_xqdw_ss = "obj_c8673d4ba6c00001a7cda610fc7d1a20";//主流程中部所技术审核节点
		  public static String xqbm_bsld_zdss = "obj_c86bc3ad572000012fc9f7f613207330";
		  
		  
		  /*
		   * 研发单位，不同节点ID（非重点企标送审）
		   */
		  public static String ngr_yfdw_fzdss = "obj_68256bd730344615ad689b807cc3b0a6";
		  public static String csld_yfdw_fzdss = "obj_bb418424344844c3a7b9e2284885c4c7";
		  public static String jkr_yfdw_fzdss = "obj_65a89d3b36d0461d9b96020ae49d1a38";
		  public static String bmld_yfdw_fzdss = "obj_1f71cefd9f844fd28de6b53e1def4fd0";
		  public static String bsjkr_yfdw_fzdss = "obj_c7d2e09349400001cc538730dae0ac70";
		  public static String qbglysh_yfdw_fzdss = "obj_c863f9c695b0000196b716e9476d1777";//企标管理员审核审核节点
		  public static String qbglysh_yfdw_fzdss_1 = "obj_c871cbb7050000016acc73b013de1fe0";//企标管理员审核审核节点
		  public static String bshq_yfdw_fzdss = "obj_c86b193716300001bfc4145013f41800";//配合部所会签子流程中配合部所会签节点
		  public static String bsnbhq_yfdw_fzdss = "obj_c86b193c89c00001c4b927801d402420";//配合部所会签子流程中配合部所内部会签节点
		  public static String xqbm_yfdw_fzdss = "obj_c871cd8e9db000011d735b248e806330";
		  public static String bsjs_xqdw_fzd = "obj_c86742454700000127806b20a1fb1b25";//主流程中部所技术审核节点
		  public static String bsjs_phnb_fzd = "obj_c871cada12a0000130241be36a9e1315";//
		  public static String bsjs_bsld_fzd = "obj_c86b28745290000164c61ecf16404af0";//
		  
		  /*
		   * 研发单位，不同节点ID（企标计划新增）
		   */
		  public static String ngr_yfdw_jhxz = "obj_cb1f4b25814a4e64898e996c0b7b9bf3";
		  public static String bsjs_yfdw_jhxz = "obj_81409103a53a4ad9803b37a340471af2";
		  public static String bsld_yfdw_jhxz = "obj_ca7e58658ac9435eb986a6bbcc51b8e4";
		  public static String bmld_yfdw_jhxz = "obj_e648171fae2f42f0baf1563123bb6753";
		  public static String qbgly_yfdw_jhxz = "obj_f61c9d1dbbf34d5db4cbde107ad4cdb2";
		  public static String qbgly_yfdw_jhxz_1 = "obj_c868de3b99000001e5bc1dbb16c812e5";//承担单位企标管理员审核
		  public static String qbgly_yfdw_jhxz_2 = "obj_c872826fd8f00001d0271450ff3a1475";//承担单位企标管理员审核
		  public static String xqbm_yfdw_jhxz = "obj_c87281bc0ea00001ddf1171019b41c88";
		  public static String phbs_xqdw_jhxz = "obj_c86b73cbca700001b85b1e15c6c0bcf0";//配合部所会签节点
		  public static String xqbm_bsnb_jhxz = "obj_c872d69ef2e000014b3f79408920eca0";
		  public static String xqbm_bsld_jhxz = "obj_c86ca5942e500001d5931cda19dc1a2c";
		  public static String xqbm_qbgly_jhxz = "obj_c872826fd8f00001d0271450ff3a1475";
		  public static String bsjs_xqdw_jhxz = "obj_c868e30f8a2000018ff5e1601cda1f17";//主流程中部所技术审核节点
		  
		  /*
		   * 研发单位，不同节点ID（企标计划变更）
		   */
		  public static String ngr_yfdw_jhbg = "obj_33b5266118204c4b95558f1abcd46525";
		  public static String bsjs_yfdw_jhbg = "obj_679d9243ffc540afbe3da923c3307da5";
		  public static String bsld_yfdw_jhbg = "obj_f8185110654d4e64b9f0fd8c9cd65899";
		  public static String bmld_yfdw_jhbg = "obj_a3e82b53b0904e709a0895d56fc590b7";
		  public static String qbgly_yfdw_jhbg = "obj_a7f8294ebaa34847bf2c1588cd3baf0f";
		  public static String qbglysh_yfdw_jhbg = "obj_c8606a2884200001d433f88cde90189d";//企标单位管理员审核
		  public static String qbglysh_yfdw_jhbg_1 = "obj_c872331421000001cf5b16b2a6501100";//企标单位管理员审核
		  public static String bshq_yfdw_jhbg = "obj_c87231d530100001598deb9a10501467";//相关部门会签子流程第一个节点
		  public static String bsjs_xqdw_bg = "obj_c86732a2e4f000017ee81a501a8810bc";//主流程中部所技术审核节点
		  public static String phbs_xqdw_bg = "obj_c86ca927f070000145df813017101fc1";//配合部所会签节点
		  public static String phbs_xqnb_bg = "obj_c8727060caf00001f189bde0eff048c0";//
		  public static String phbs_bsld_bg = "obj_c86ca86878400001fa7bfe60b4509bd0";//
		  
		  /*
		   * 研发单位，不同节点ID（企标计划取消）
		   */
		  public static String ngr_yfdw_jhqx = "obj_fd5cd96fcd6549e589164f55aa5bac23";
		  public static String bsjs_yfdw_jhqx = "obj_b32700475fe64a548d10476a1f227369";
		  public static String bsld_yfdw_jhqx = "obj_b48514cb5d97437e9018e0c1187105e2";
		  public static String bsjkr_yfdw_jhqx = "obj_c86885b4b4700001155512a01599177a";
		  public static String bmld_yfdw_jhqx = "obj_fe8a46c607314bd699a1aac78247fd4e";
		  public static String qbgly_yfdw_jhqx = "obj_f5d5473774fa48d7bdc8ccc229fc9634";
		  public static String xqbm_yfdw_jhqx = "obj_c87271a52560000135b5f50e3bd04d30";
		  public static String phbs_xqdw_qx = "obj_c86bbe37c77000013f891a3c70fd3300";//配合部所会签节点
		  public static String phbs_phnb_qx = "obj_c872d62ad82000019133650ee5a01dbb";
		  public static String jbglysh_cddw_jhqx = "obj_c86885ee81600001c8a1bb70a98014cd";//企标单位管理员审核
		  public static String jbglysh_cddw_jhqx_1 = "obj_c872749df6800001e0321b691170127f";//企标单位管理员审核
		  public static String bmld_xgbs_qx = "obj_c872749b87000001451a1fc06d80d000";
		  public static String bmld_phbs_qx = "obj_c86ca72f90f000017b79bc101bc05f00";
		  public static String bmld_bsld_qx = "obj_c86ca7357f5000011d8d1a46f150dc20";
		  
		  /*
		   * 承担单位，不同节点ID（企标复审）
		   */
		  public static String gly_yfdw_fs = "obj_c8281691d170000196781e3912764ee0";
		  public static String glycl_yfdw_fs = "obj_c82c4c14df40000172364f005aafc260";
		  public static String bmld_yfdw_fs = "obj_c82c4bfc4cc000014fd31ce5c8ea126f";
		  public static String bsld_yfdw_fs = "obj_c832b5775be0000125421bc011701605";
		  /*
		   * 承担单位，不同节点ID（企标文档水印）
		   */
		  public static String gly_cddw_sy = "obj_c88235bab4800001fe546a1c5480127f";
		  public static String gly_bsld_sy = "obj_c88235c443200001a77a1a60cdf010c9";
		  public static String gly_bmld_sy = "obj_c88235c896b00001a5ca1c9015b9b000";
		  public static String gly_qbgly_sy = "obj_c88235cbfb90000145954e3d1800e300";
		  public static String xgbshq_sy = "obj_c8838037c13000019c5f987316501e56";
		  public static String xgbsnbcl_sy = "obj_c88380394e7000014a108c301aa71dc6";
		  
		  
		  
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
		    list_yfdwNoteid.add(qbglysh_gjr_bp);
		    list_yfdwNoteid.add(qbglysh_gjr_bp_1);
		    list_yfdwNoteid.add(phbshq_gjr_bp);
		    list_yfdwNoteid.add(phnbhq_gjr_bp);
		    list_yfdwNoteid.add(phnbhq_phnb_bp);
		    list_yfdwNoteid.add(phnbhq_bsld_bp);
		    //chenxf add 2018-07-12
		    list_yfdwNoteid.add(bsjs_yfdw_bp);
		    list_yfdwNoteid.add(qbgly_yfdw_bp);
		    list_yfdwNoteid.add(qbgly_jsb_bp);
		    list_yfdwNoteid.add(bshq_xgbm_bp);
		    
		    list_yfdwNoteid.add(ngr_yfdw_zdss);
		    list_yfdwNoteid.add(csld_yfdw_zdss);
		    list_yfdwNoteid.add(jkr_yfdw_zdss);
		    list_yfdwNoteid.add(bmld_yfdw_zdss);
		    list_yfdwNoteid.add(bsjkr_yfdw_zdss);
		    list_yfdwNoteid.add(qbglysh_yfdw_zdss);
		    list_yfdwNoteid.add(qbglysh_yfdw_zdss_1);
		    list_yfdwNoteid.add(qbglycl_yfdw_zdss);
		    list_yfdwNoteid.add(phbs_yfdw_zdss);
		    list_yfdwNoteid.add(phbsnb_yfdw_zdss);
		    list_yfdwNoteid.add(xqbm_yfdw_zdss);
		    list_yfdwNoteid.add(xqbm_bsnb_zdss);
		    list_yfdwNoteid.add(bsjs_xqdw_ss);
		    list_yfdwNoteid.add(xqbm_bsld_zdss);
		    
		    list_yfdwNoteid.add(ngr_yfdw_fzdss);
		    list_yfdwNoteid.add(csld_yfdw_fzdss);
		    list_yfdwNoteid.add(jkr_yfdw_fzdss);
		    list_yfdwNoteid.add(bmld_yfdw_fzdss);
		    list_yfdwNoteid.add(bsjkr_yfdw_fzdss);
		    list_yfdwNoteid.add(qbglysh_yfdw_fzdss);
		    list_yfdwNoteid.add(qbglysh_yfdw_fzdss_1);
		    list_yfdwNoteid.add(bshq_yfdw_fzdss);
		    list_yfdwNoteid.add(bsnbhq_yfdw_fzdss);
		    list_yfdwNoteid.add(xqbm_yfdw_fzdss);
		    list_yfdwNoteid.add(bsjs_xqdw_fzd);
		    list_yfdwNoteid.add(bsjs_phnb_fzd);
		    list_yfdwNoteid.add(bsjs_bsld_fzd);
		    /*企标计划新增*/
		    list_yfdwNoteid.add(ngr_yfdw_jhxz);
		    list_yfdwNoteid.add(bsjs_yfdw_jhxz);
		    list_yfdwNoteid.add(bsld_yfdw_jhxz);
		    list_yfdwNoteid.add(bmld_yfdw_jhxz);
		    list_yfdwNoteid.add(qbgly_yfdw_jhxz);
		    list_yfdwNoteid.add(qbgly_yfdw_jhxz_1);
		    list_yfdwNoteid.add(qbgly_yfdw_jhxz_2);
		    list_yfdwNoteid.add(xqbm_yfdw_jhxz);
		    list_yfdwNoteid.add(phbs_xqdw_jhxz);
		    list_yfdwNoteid.add(xqbm_bsnb_jhxz);
		    list_yfdwNoteid.add(xqbm_bsld_jhxz);
		    list_yfdwNoteid.add(xqbm_qbgly_jhxz);
		    list_yfdwNoteid.add(bsjs_xqdw_jhxz);
		    /*企标计划变更*/
		    list_yfdwNoteid.add(ngr_yfdw_jhbg);
		    list_yfdwNoteid.add(bsjs_yfdw_jhbg);
		    list_yfdwNoteid.add(bsld_yfdw_jhbg);
		    list_yfdwNoteid.add(bmld_yfdw_jhbg);
		    list_yfdwNoteid.add(qbgly_yfdw_jhbg);
		    list_yfdwNoteid.add(qbglysh_yfdw_jhbg);
		    list_yfdwNoteid.add(qbglysh_yfdw_jhbg_1);
		    list_yfdwNoteid.add(bshq_yfdw_jhbg);
		    list_yfdwNoteid.add(bsjs_xqdw_bg);
		    list_yfdwNoteid.add(phbs_xqdw_bg);
		    list_yfdwNoteid.add(phbs_xqnb_bg);
		    list_yfdwNoteid.add(phbs_bsld_bg);
		    /*企标计划取消*/
		    list_yfdwNoteid.add(ngr_yfdw_jhqx);
		    list_yfdwNoteid.add(bsjs_yfdw_jhqx);
		    list_yfdwNoteid.add(bsld_yfdw_jhqx);
		    list_yfdwNoteid.add(bmld_yfdw_jhqx);
		    list_yfdwNoteid.add(qbgly_yfdw_jhqx);
		    list_yfdwNoteid.add(xqbm_yfdw_jhqx);
		    list_yfdwNoteid.add(phbs_xqdw_qx);
		    list_yfdwNoteid.add(phbs_phnb_qx);
		    list_yfdwNoteid.add(jbglysh_cddw_jhqx);
		    list_yfdwNoteid.add(jbglysh_cddw_jhqx_1);
		    list_yfdwNoteid.add(bsjkr_yfdw_jhqx);
		    list_yfdwNoteid.add(bmld_xgbs_qx);
		    list_yfdwNoteid.add(bmld_phbs_qx);
		    list_yfdwNoteid.add(bmld_bsld_qx);
		    /*企标复审*/
		    list_yfdwNoteid.add(gly_yfdw_fs);
		    list_yfdwNoteid.add(glycl_yfdw_fs);
		    list_yfdwNoteid.add(bmld_yfdw_fs);
		    list_yfdwNoteid.add(bsld_yfdw_fs);
		    /*企标文档水印*/
		    list_yfdwNoteid.add(gly_cddw_sy);
		    list_yfdwNoteid.add(gly_bsld_sy);
		    list_yfdwNoteid.add(gly_bmld_sy);
		    list_yfdwNoteid.add(gly_qbgly_sy);
		    list_yfdwNoteid.add(xgbshq_sy);
		    list_yfdwNoteid.add(xgbsnbcl_sy);
		   
		  }
		  /*
		   * 技术单位不同节点ID（报批）
		   */
		  public static String bmld_jsdw_bp = "obj_c7bfdd2f61100001d67c90a899605590";
		  public static String nbry_jsdw_bp = "obj_c7bfdd37d5000001ae6e4b3c10006c40";
		  public static String gly_jsdw_bp = "obj_c7bfdd4949800001c18618e6780b11e1";
		  public static String gly_hsb_bp = "obj_c7bfdd4949800001c18618e6780b11e1";
		  //chenxf add 2018-07-12
		  //public static String qbgly_jsdw_bp = "obj_c80c69accc80000180e811f04f96a3d0";
		  public static String jsb_jsdw_bp = "obj_c80cd0589b40000176edd15019e01ad7";
		  
		  //chenxf add 2018-08-13
		  public static String jsbnb_jsdw_bp = "obj_c817ac97d0600001f1f61f00c4201ed8";
		  /*
		   * 技术单位不同节点ID（重点企标送审）
		   */
		  public static String bmld_jsdw_zdss = "obj_6867329ec1bd42a295e00c877d8b9a08";
		  public static String nbry_jsdw_zdss = "obj_98ac464cfff14ee492523d6e3b7ad287";
		  //chenxf add 2018-08-13
		  public static String jsbnb_jsdw_zdss = "obj_c817ad3182b0000180b64900135612a1";
		  
		  /*
		   * 技术单位不同节点ID（企标计划新增）
		   */
		  //public static String gly_jsdw_jhxz = "obj_17a858d6cc904cd3a5b9e61e6741cb92";
		  public static String bmld_jsdw_jhxz = "obj_66a9206431974ba4a78ce6d76839aa09";
		  public static String jsbnb_jsdw_jhxz = "obj_865bd3acc1f740a8a7960fd6744bf35e";
		  
		  /*
		   * 技术单位不同节点ID（企标计划变更）
		   */
		  //public static String gly_jsdw_jhbg = "obj_f51bebb9a8d547aa906c7f587a721ae5";//技术部企标管理员审核节点
		  public static String bmld_jsdw_jhbg = "obj_1d2eb4d05c454ea3ac2599777ca0a3a5";//技术部领导审核节点
		  public static String jsbnb_jsdw_jhbg = "obj_0d5e3aed5d9841c4a391031d15dd7246";//技术部部门内部审核节点
		  /*
		   * 技术单位不同节点ID（企标计划取消）
		   */
		  
		  public static String bmld_jsdw_jhqx = "obj_3ba90018628d401b889c7a88077af447";
		  public static String jsbnb_jsdw_jhqx = "obj_0954a5f82ad74fd288a571ac20cfe38c";
		  /*
		   * 技术单位不同节点ID（企标复审）
		   */
		  public static String bmld_jsdw_fs = "obj_c82816c5c070000137817a1017109f50";
		  public static String jsbnb_jsdw_fs = "obj_c82816cd93600001cf64bc008db023c0";
		  /*
		   * 技术单位不同节点ID（企标文档水印）
		   */
		  public static String bmld_jsbqbgly_sy = "obj_c88235d127b00001d5f41eb01eb0eb70";
		  public static String bmld_jsbld_sy = "obj_c88235da9da000012173147034a61f82";
		  public static String bmld_jsbnb_sy = "obj_c88235df93e00001f763673519e41390";
		  
		  //技术单位list
		  public static List<String> list_jsdwNoteid;
		  public static String jsdwName = "技术部意见";
		  static{
			  	list_jsdwNoteid = new ArrayList();
			    list_jsdwNoteid.add(bmld_jsdw_bp);
			    list_jsdwNoteid.add(nbry_jsdw_bp);
			    list_jsdwNoteid.add(gly_jsdw_bp);
			    //list_jsdwNoteid.add(bsjs_xqdw_bp);
			    list_jsdwNoteid.add(gly_hsb_bp);
			    //chenxf add 2018-07-12
			    //list_jsdwNoteid.add(qbgly_jsdw_bp);
			    list_jsdwNoteid.add(jsb_jsdw_bp);
			    
			    
			    list_jsdwNoteid.add(bmld_jsdw_zdss);
			    list_jsdwNoteid.add(nbry_jsdw_zdss);
			    //list_jsdwNoteid.add(gly_jsdw_zdss);
			    //chenxf add 2018-08-13
			    list_jsdwNoteid.add(jsbnb_jsdw_bp);
			    list_jsdwNoteid.add(jsbnb_jsdw_zdss);
			    //企标计划新增
			    list_jsdwNoteid.add(bmld_jsdw_jhxz);
			    list_jsdwNoteid.add(jsbnb_jsdw_jhxz);
			    //企标计划变更
			    //list_jsdwNoteid.add(gly_jsdw_jhbg);
			    list_jsdwNoteid.add(bmld_jsdw_jhbg);
			    list_jsdwNoteid.add(jsbnb_jsdw_jhbg);
			    //企标计划取消
			    //list_jsdwNoteid.add(gly_jsdw_jhqx);
			    list_jsdwNoteid.add(bmld_jsdw_jhqx);
			    list_jsdwNoteid.add(jsbnb_jsdw_jhqx);
			    //企标复审
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
		  public static String bmld_jsbzl_fzdss = "obj_c86405aa7790000129c5112de35a172b";
		  public static String bmld_glr_fzdss = "obj_c86ff8ec4c70000135ce1c3014521527";
		  
		  /*
		   * 需求单位不同节点ID（企标复审）
		   */
		  public static String gly_xqdw_fs = "obj_c828178c070000018258f45712a21b34";//需求企标管理员
		  public static String bmld_xqdw_fs = "obj_c8281796d3e00001772481951910b8e0";//需求部门领导
		  public static String bmnb_xqdw_fs = "obj_c82817a3e61000013ebe180019002d40";//需求部门内部审核
		  
		  /*
		   * wxx add 2019-03-29
		   * 需求单位不同节点ID（企标报批子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_bp = "obj_c85e7d088dd0000193408220a3a42100";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_bp = "obj_c85e7d15aa000001354e52d016eca8b0";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_bp = "obj_c85e7d1d105000011ce7c9f0c93014b1";//需求部门内部审核（部门内部处理节点）
		  public static String bmld_js_bp = "obj_c86ff9664df000015a8c38201140cdc0";
		  public static String phbs_zlr_bp = "obj_c80c69accc80000180e811f04f96a3d0";//
		  
		  
		  
		  /*
		   * wxx add 2019-04-01
		   * 需求单位不同节点ID（企标送审（重点）子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_ss = "obj_c862c360c16000018b6d1f401beb14d7";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_ss = "obj_c862c36582400001e99c1a0010c210be";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_ss = "obj_c862c36e1010000158601c5023501ad8";//需求部门内部审核（部门内部处理节点）
		  
		  public static String bmld_jsbzl_ss = "obj_c862bfc5a0600001aa94802019277400";
		  public static String bmld_glr_ss = "obj_c86ff935b56000017b7f140b12572200";
		  public static String bmld_jsbgly_ss = "obj_b936e952a4c749d8a0b1f7c926fe8da0";
		  
		  /*
		   * wxx add 2019-04-02
		   * 需求单位不同节点ID（企标送审（非重点）子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_fzd = "obj_c863f8a8c2a00001d31e17c02ee01827";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_fzd = "obj_c863f8ad88b0000186e01af019d01ce7";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_fzd = "obj_c863f8b141200001c38885403a139cf0";//需求部门内部审核（部门内部处理节点）
		  
		  
		  
		  
		  /*
		   * wxx add 2019-04-01
		   * 需求单位不同节点ID（企标计划变更子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_bg = "obj_c8606c4c09c0000130cd2070112012b0";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_bg = "obj_c8606c50a65000017e8c1f901b71dc80";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_bg = "obj_c8606c54bcc00001435b1692150063e0";//需求部门内部审核（部门内部处理节点）
		  public static String bmld_jsbzl_bg = "obj_c8606c0e293000011cc31144ca2a1880";
		  public static String bmld_glr_bg = "obj_c86ff68ce05000012f491a971c231713";
		  public static String bmld_jsb_bg = "obj_c8626e67e2900001227f7abb8940154a";
		  public static String bmld_jsb_bp = "obj_c85e3ab470700001172baee7114037c0";
		  public static String bmld_jsbgly_bg = "obj_f51bebb9a8d547aa906c7f587a721ae5";
		  
		  
		  /*
		   * wxx add 2019-05-05
		   * 需求单位不同节点ID（企标计划取消子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_qx = "obj_c868888015800001404413141bb91b01";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_qx = "obj_c8688885591000012d8915a01cd0b800";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_qx = "obj_c86888891f2000011df21f50193e165c";//需求部门内部审核（部门内部处理节点）
		  public static String bmld_jsbzl_qx = "obj_c8689035f5d00001a3b32799107017a2";
		  public static String bmld_glr_qx = "obj_c870307eff900001dd5b149061ceae90";
		  public static String bmld_jsbgly_qx = "obj_e62756dd846a43a2b653933ec6512896";
		  
		  /*
		   * wxx add 2019-05-05
		   * 需求单位不同节点ID（企标计划新增子流程）和配合部所会签子流程
		   */
		  public static String gly_xqdw_xz = "obj_c868df54f2e00001a84615f41411192d";//需求企标管理员（技术部和需求部门会签节点）
		  public static String bmld_xqdw_xz = "obj_c868df57c160000178601a40f9e09880";//需求部门领导（部门领导审核节点）
		  public static String bmnb_xqdw_xz = "obj_c868df5a54f000012f281a8e15e9a120";//需求部门内部审核（部门内部处理节点）
		  
		  public static String bmld_jsbzl_xz = "obj_c868e2c2808000012a3d1dc08c203440";
		  public static String bmld_glr_xz = "obj_c87030d47b500001409c52a117301df4";
		  public static String bmld_jsbgly_xz = "obj_17a858d6cc904cd3a5b9e61e6741cb92";
		  public static String gly_jsdw_jhbg = "obj_f51bebb9a8d547aa906c7f587a721ae5";
		  
		  
		  
		  //技术单位list
		  public static List<String> list_xqdwNoteid;
		  public static String xqdwName = "技术部和需求单位意见";
		  static{
			  list_xqdwNoteid = new ArrayList();
			  list_xqdwNoteid.add(jkr_xqdw_fzdss);
			  list_xqdwNoteid.add(bmld_xqdw_fzdss);
			  list_xqdwNoteid.add(nbry_xqdw_fzdss);
			  list_xqdwNoteid.add(nbsh_xqdw_fzdss);
			  list_xqdwNoteid.add(bmld_jsbzl_fzdss);
			  list_xqdwNoteid.add(bmld_glr_fzdss);
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
			  list_xqdwNoteid.add(phbs_zlr_bp);
			  //list_xqdwNoteid.add(phbs_qbgly_bp);
			  
			  
			  /*
			   * wxx add 2019-04-01
			   * 企标送审（重点）子流程--需求部门节点ID
			   */
			  list_xqdwNoteid.add(gly_xqdw_ss);
			  list_xqdwNoteid.add(bmld_xqdw_ss);
			  list_xqdwNoteid.add(bmnb_xqdw_ss);
			  list_xqdwNoteid.add(bmld_jsbzl_ss);
			  list_xqdwNoteid.add(bmld_glr_ss);
			  list_xqdwNoteid.add(bmld_jsbgly_ss);
			  
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
			  list_xqdwNoteid.add(bmld_jsbzl_bg);
			  list_xqdwNoteid.add(bmld_glr_bg);
			  list_xqdwNoteid.add(bmld_jsb_bg);
			  list_xqdwNoteid.add(bmld_jsb_bp);
			  list_xqdwNoteid.add(bmld_jsbgly_bg);
			  
			  /*
			   * wxx add 2019-05-05
			   * 企标计划取消子流程--需求部门节点ID
			   */
			  list_xqdwNoteid.add(gly_xqdw_qx);
			  list_xqdwNoteid.add(bmld_xqdw_qx);
			  list_xqdwNoteid.add(bmnb_xqdw_qx);
			  list_xqdwNoteid.add(bmld_jsbzl_qx);
			  list_xqdwNoteid.add(bmld_glr_qx);
			  list_xqdwNoteid.add(bmld_jsbgly_qx);
			  /*
			   * wxx add 2019-05-05
			   * 企标计划新增子流程--需求部门节点ID
			   */
			  list_xqdwNoteid.add(gly_xqdw_xz);
			  list_xqdwNoteid.add(bmld_xqdw_xz);
			  list_xqdwNoteid.add(bmnb_xqdw_xz);
			  list_xqdwNoteid.add(bmld_jsbzl_xz);
			  list_xqdwNoteid.add(bmld_glr_xz);
			  list_xqdwNoteid.add(bmld_jsbgly_xz);
			  //原来的技术部意见全部放到技术部和需求部门意见下面，将原来的技术部意见全部注销
			  
			  list_xqdwNoteid.add(bmld_jsdw_bp);
			  list_xqdwNoteid.add(nbry_jsdw_bp);
			  list_xqdwNoteid.add(gly_jsdw_bp);
			    //chenxf add 2018-07-12
			  list_xqdwNoteid.add(jsb_jsdw_bp);
			  list_xqdwNoteid.add(bmld_js_bp);
			    
			    
			  list_xqdwNoteid.add(bmld_jsdw_zdss);
			  list_xqdwNoteid.add(nbry_jsdw_zdss);
			  
			    //chenxf add 2018-08-13
			  list_xqdwNoteid.add(jsbnb_jsdw_bp);
			  list_xqdwNoteid.add(jsbnb_jsdw_zdss);
			    /*企标计划新增*/
			  list_xqdwNoteid.add(bmld_jsdw_jhxz);
			  list_xqdwNoteid.add(jsbnb_jsdw_jhxz);
			    /*企标计划变更*/
			  list_xqdwNoteid.add(gly_jsdw_jhbg);
			  list_xqdwNoteid.add(bmld_jsdw_jhbg);
			  list_xqdwNoteid.add(jsbnb_jsdw_jhbg);
			    /*企标计划取消*/
			  list_xqdwNoteid.add(bmld_jsdw_jhqx);
			  list_xqdwNoteid.add(jsbnb_jsdw_jhqx);
			    /*企标复审*/
			  list_xqdwNoteid.add(bmld_jsdw_fs);
			  list_xqdwNoteid.add(jsbnb_jsdw_fs);
			  
			  //企标文档水印
			  list_xqdwNoteid.add(bmld_jsbqbgly_sy);
			  list_xqdwNoteid.add(bmld_jsbld_sy);
			  list_xqdwNoteid.add(bmld_jsbnb_sy);
			  
			  
			    
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
//			  SDK.getTaskAPI().
//			  		createUserCCTaskInstance(
//			  				processInst, 
//			  				parentTaskInst, 
//			  				uc.getUID(), 
//			  				userid, 
//			  				title);
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
