package com.actionsoft.apps.cmcc.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共类
 * 不过滤同一个节点上不同人之间的流程跟踪意见 
 * @date 2018-4-19
 * @author chenxf
 *
 */
public class NoFilterSameStepOptionUtil {
	/*
	 * 企标流程定义ID
	 */
	public static String fdz_uuid = "obj_ed4464d2126b4a8e987250b3736dd79b";
	public static String dz_uuid = "obj_2f9448bbc24748c1b874afacc9e553af";
	public static String bp_uuid = "obj_d23544417cf54becb210b93a7c13752b";
	public static String bp_uuid_new = "obj_7912575b5aa942caac404866c90f73c1";//2版本报批流程
	public static String dz_uuid_new = "obj_464293ce274743dc919ac3ad4ea599f3";//2版本重点流程
	public static String fdz_uuid_new = "obj_e0e7896ac22249d0bc61cfe270dd859a";//2版本非重点流程
	
	public static String jhxz_uuid = "obj_b55064e46a9e4b2f825e33c298a550d6";//企标计划新增
	public static String jhbg_uuid = "obj_6ed1e9bfbfef41aea92a32b99d608109";//企标计划变更
	public static String jhqx_uuid = "obj_7baa1ed97a16429084674fc1775ae3a1";//企标计划取消
	public static String fs_uuid = "obj_c0d984344b16433fb17910f1c863e465";//企标复审
	public static String fs_uuid_sub = "obj_c0d984344b16433fb17910f1c863e465";//企标复审需求部门子流程
	
	
	//不过滤同一个节点上不同人之间的流程跟踪意见的流程定义ID集合
	public static List<String> list_NoFilterProcessUUID;
	static{
		list_NoFilterProcessUUID = new ArrayList<String>();
		/*
		 * 企标
		 */
		list_NoFilterProcessUUID.add(fdz_uuid);
		list_NoFilterProcessUUID.add(dz_uuid);
		list_NoFilterProcessUUID.add(bp_uuid);
		
		list_NoFilterProcessUUID.add(jhxz_uuid);//计划新增
		list_NoFilterProcessUUID.add(jhbg_uuid);//计划变更
		list_NoFilterProcessUUID.add(jhqx_uuid);//计划取消
		list_NoFilterProcessUUID.add(fs_uuid);//企标复审
		list_NoFilterProcessUUID.add(fs_uuid_sub);//企标复审需求部门子流程
		/*
		 * 企标
		 */
		list_NoFilterProcessUUID.add("obj_e0e7896ac22249d0bc61cfe270dd859a");//企标非重点
		list_NoFilterProcessUUID.add("obj_7912575b5aa942caac404866c90f73c1");//企标报批
		list_NoFilterProcessUUID.add("obj_be26d7911cdd4c9999458dd8c3497421");//企标报批3.0版本
		list_NoFilterProcessUUID.add("obj_301494b352c84dc8ab9bdffffc8c47f4");//企标计划取消流程2.0版本
		list_NoFilterProcessUUID.add("obj_8f220f141bd049329b02ce40da8b42f6");//企标计划取消子流程
		list_NoFilterProcessUUID.add("obj_0b7de89814c2421e8f814cec1a9f54b9");//企标计划变更2.0版本
		list_NoFilterProcessUUID.add("obj_094c715ec6db4aaf9a08653c43757175");//企标计划变更1.0版本子流程
		list_NoFilterProcessUUID.add("obj_fce9a0aab6434bb3b6aae11113c19814");//企标报批子流程
		list_NoFilterProcessUUID.add("obj_464293ce274743dc919ac3ad4ea599f3");//企标送审
		list_NoFilterProcessUUID.add("obj_a6621d3ed56f4d78ad5199e38c03d0e6");//企标送审重点3.0版本
		list_NoFilterProcessUUID.add("obj_0402de73e193438f904a323bd03ec2a1");//企标送审重点1.0版本子流程
		list_NoFilterProcessUUID.add("obj_d6db21439eda4066b9f830d45db18bbe");//企标计划新增2.0版本
		list_NoFilterProcessUUID.add("obj_47bd1aecf6c34871bcdd06adf13c5840");//企标计划新增子流程
		list_NoFilterProcessUUID.add("obj_73907bda2bc04c35954e257df665a521");//企标送审非重点
		list_NoFilterProcessUUID.add("obj_6d3f3c95f21f40509a4e08de3ff4f6a5");//企标送审非重点子流程
		list_NoFilterProcessUUID.add("obj_3b0b256ff6d0488c8948defdc0c1494f");//企标水印流程
		
		
		//2版本
		list_NoFilterProcessUUID.add(bp_uuid_new);
		list_NoFilterProcessUUID.add(dz_uuid_new);
		list_NoFilterProcessUUID.add(fdz_uuid_new);
		/*
		 * 结项
		 */
		list_NoFilterProcessUUID.add("obj_ddbb3b381fab4b069b4bdfab8f297b52");
		list_NoFilterProcessUUID.add("obj_b5e7f538223645d3b8b4074aac008b36");
		//正式
		list_NoFilterProcessUUID.add("obj_5683dd9be5ce47bcbc31cf691ca92004");
		list_NoFilterProcessUUID.add("obj_5abade2ce8504894bb43287444defec9");
		/*
		 * 立项
		 */
		list_NoFilterProcessUUID.add("obj_d7797b523d2c4b32bbedec8ae7c38488");
		list_NoFilterProcessUUID.add("obj_20695df2c06a42289ee35d7aadbe7edb");
		//正式
		list_NoFilterProcessUUID.add("obj_b6d444eeea6d4ff292c64e57bdc082f6");
		list_NoFilterProcessUUID.add("obj_b656bb4a39984a6ebee8ee281a17c414");
		
		/*
		 * 成果提交
		 */
		list_NoFilterProcessUUID.add("obj_d691aed42c384a1c934aae0350bb1fac");//dev
		list_NoFilterProcessUUID.add("obj_4f28b2520b0642c1b902235195d0db88");//test
		/*
		 * 一般委托
		 */
		list_NoFilterProcessUUID.add("obj_78fca64a745d47d19451cd40dd8a4eea");//test流程id
		/*
		 * 预算调整
		 */
		list_NoFilterProcessUUID.add("obj_2d634a550d974ef7b4c274cbac669e4b");//test
		list_NoFilterProcessUUID.add("obj_0568104fac974bbc82c08fa5afc4280b");//test,2版本
		list_NoFilterProcessUUID.add("obj_b2530c4f2dae4f76881e9193c5d77d90");//test,2版本，子流程的流程id
		
		
		/*
		 * 标准化
		 */
		list_NoFilterProcessUUID.add("obj_c2ee0d3bd4fe4623a4cf0c9536d8b44a");//标准化
		//由于需求变更，删掉
//		/*
//		 * 研究院--立项
//		 */
//		list_NoFilterProcessUUID.add("obj_a604afde630943deb9de2a506a23584e");//test，2版本
//		/*
//		 * 研究院--结项
//		 */
//		list_NoFilterProcessUUID.add("obj_5daf378bc5b64a439ca2346b7ef583ca");//test，2版本
//		/*
//		 * 研究院--取消
//		 */
//		list_NoFilterProcessUUID.add("obj_69f5b80dcf784b2b974c1ecc4fc55ac4");//test，2版本
//		/*
//		 * 研究院--变更
//		 */
//		list_NoFilterProcessUUID.add("obj_ae4a1779cfd24ea2b19ebbfc5b30a729");//test，2版本
//		/*
//		 * 研究院--计划外
//		 */
//		list_NoFilterProcessUUID.add("obj_2f59335aac0e472f9d3d0b55fd464c3b");//test，2版本
		
	}
	//不过滤同一个节点上不同人之间的流程跟踪意见的节点ID集合
	public static List<String> list_NoFilterProcessStepId;
	static{
		list_NoFilterProcessStepId = new ArrayList<String>();
		/*
		 * 企标--技术部内部处理节点
		 */
		list_NoFilterProcessStepId.add("obj_c817ac97d0600001f1f61f00c4201ed8");
		list_NoFilterProcessStepId.add("obj_c817ad3182b0000180b64900135612a1");
		list_NoFilterProcessStepId.add("obj_c817c0f2b8f0000129fd10d11d64159c");
		list_NoFilterProcessStepId.add("obj_0d5e3aed5d9841c4a391031d15dd7246");//计划变更
		list_NoFilterProcessStepId.add("obj_865bd3acc1f740a8a7960fd6744bf35e");//计划新增
		list_NoFilterProcessStepId.add("obj_0954a5f82ad74fd288a571ac20cfe38c");//计划取消
		list_NoFilterProcessStepId.add("obj_c82816cd93600001cf64bc008db023c0");//企标复审
		list_NoFilterProcessStepId.add("obj_c82817a3e61000013ebe180019002d40");//企标复审需求内部子流程
		list_NoFilterProcessStepId.add("obj_65a89d3b36d0461d9b96020ae49d1a38");//企标送审非重点
		list_NoFilterProcessStepId.add("obj_c863f8b141200001c38885403a139cf0");//企标送审非重点子流程部门内部处理节点
		list_NoFilterProcessStepId.add("obj_6cdfba4d7b0a4129a60af8a1ec94056b");//重点企标送审
		list_NoFilterProcessStepId.add("obj_26a09bba00824fafbbdc82e1b2c2189d");//重点企标送审3.0版本。承担单位归口领导审核节点
		list_NoFilterProcessStepId.add("obj_c862c36e1010000158601c5023501ad8");//重点企标送审1.0版本子流程。部门内部处理节点
		list_NoFilterProcessStepId.add("obj_c7bfdd29b02000016f9719d2dad81c98");//企标报批
		list_NoFilterProcessStepId.add("obj_fe8a46c607314bd699a1aac78247fd4e");//企标计划取消流程2.0版本，承担单位归口领导审核
		list_NoFilterProcessStepId.add("obj_c86888891f2000011df21f50193e165c");//企标计划取消子流程，部门内部处理节点
		list_NoFilterProcessStepId.add("obj_a3e82b53b0904e709a0895d56fc590b7");//企标计划变更2.0版本。承担单位归口领导审核节点
		list_NoFilterProcessStepId.add("obj_c8606c54bcc00001435b1692150063e0");//企标计划变更1.0版本子流程。部门内部审核节点
		list_NoFilterProcessStepId.add("obj_c85e7d1d105000011ce7c9f0c93014b1");//企标报批子流程部门内部处理节点id
		list_NoFilterProcessStepId.add("obj_e648171fae2f42f0baf1563123bb6753");//企标计划新增2.0版本承担单位主管领导审核
		list_NoFilterProcessStepId.add("obj_c868df5a54f000012f281a8e15e9a120");//企标计划新增子流程部门内部处理节点
		list_NoFilterProcessStepId.add("obj_c88235df93e00001f763673519e41390");//企标水印流程部门内部处理节点
		/*
		 * 企标--承担单位内部处理节点
		 */
//		list_NoFilterProcessStepId.add("obj_c82c4c14df40000172364f005aafc260");//企标复审
		list_NoFilterProcessStepId.add("obj_6cdfba4d7b0a4129a60af8a1ec94056b");//企标送审
		/*
		 * 结项--处领导审核节点
		 */
		list_NoFilterProcessStepId.add("obj_c78359a207200001f93a1a3217821596");
		list_NoFilterProcessStepId.add("obj_c7835f9a0bb00001902df77510801062");
		list_NoFilterProcessStepId.add("obj_c7f89fc53a2000011a1f10ed53e99c00");
		/*
		 * 立项--处领导审核节点
		 */
		list_NoFilterProcessStepId.add("obj_c775750ccdb0000188e41c91ddb09340");
		list_NoFilterProcessStepId.add("obj_c77aef4c060000015c5719102ca01c2e");
		list_NoFilterProcessStepId.add("obj_c7f8a43a36300001d47d14d0126011d5");
		/*
		 * 成果提交--处领导审核节点
		 */
		list_NoFilterProcessStepId.add("obj_c771e93d44f00001c66b1330774018a5");
		list_NoFilterProcessStepId.add("obj_c76e783ba260000133f0a0e7c0d0a170");//部长增加办理人
		/*
		 * 一般委托--部领导审核节点，部领导审批节点
		 */
		list_NoFilterProcessStepId.add("obj_c76ec40f9f90000162d7155f19e0ed10");//部领导审核节点
		list_NoFilterProcessStepId.add("obj_c76ec5572d8000016e89e3e1a2d4e5f0");//部领导审批节点
		/*
		 * 预算调整
		 */
		list_NoFilterProcessStepId.add("obj_c7db7cf2c7e00001ebe912e01b201c71");//test
		list_NoFilterProcessStepId.add("obj_c8302458dc600001fbb91672c3901f3f");//test，2.0版本子流程中自增节点的id
		
		/**
		 * 标准化
		 */
		list_NoFilterProcessStepId.add("obj_c88c8665bd40000191911fe02c804ef0");//test
		list_NoFilterProcessStepId.add("obj_c88c86dcafa00001ee9959c021801a67");//test
		
		//由于需求变更，删掉
//		/*
//		 * 研究院--立项
//		 */
//		list_NoFilterProcessStepId.add("obj_c7b071ea466000015d4cf310a5a319f8");//test，2版本--项目总监或所主管审核节点ID
//		/*
//		 * 研究院--结项
//		 */
//		list_NoFilterProcessStepId.add("obj_c7b0c38367b0000111e1111914f0dc60");//test，2版本--项目总监或所主管审核节点ID
//		/*
//		 * 研究院--取消
//		 */
//		list_NoFilterProcessStepId.add("obj_c796f476965000018dda17b018403b80");//test，2版本--项目总监或所主管审核节点ID
//		/*
//		 * 研究院--变更
//		 */
//		list_NoFilterProcessStepId.add("obj_c79b74d58d200001d73d171d170010bf");//test，2版本--项目总监或所主管审核节点ID
//		/*
//		 * 研究院--计划外
//		 */
//		list_NoFilterProcessStepId.add("obj_c79516870eb0000148d5f9e61920ea00");//test，2版本--项目总监或所主管审核节点ID
	}
}
