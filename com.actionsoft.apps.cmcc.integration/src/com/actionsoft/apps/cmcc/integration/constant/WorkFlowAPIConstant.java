package com.actionsoft.apps.cmcc.integration.constant;

/**
 * 流程接口公共常量类
 * @author Administrator
 *
 */
public class WorkFlowAPIConstant {
	/** appId **/
	public static final String APPID = "com.actionsoft.apps.cmcc.integration";//APPID
	
	/** app参数 **/ 
	public static final String transferActionIds = "transferActionIds";//转办审核菜单集合
	public static final String workFlowFirstStep = "workFlowFirstStep";//第一节点集合
	public static final String SYNC_VPN_USER = "SYNC_VPNUser";//vpnUser同步地址
	public static final String SYNC_VPN_DEPT = "SYNC_VPNDEPT";//vpnUser所在组织
	public static final String KFWHTD_ROLEID = "KFWHTD_ROLEID";//开发维护团队
	public static final String SYNC_VPN_UPDATE = "SYNC_VPN_UPDATE";//是否开启更新
	
	
	/** 后缀  **/
	public static final String SUFFIX = "@hq.cmcc";//用户默认后缀
	
	/** 基础字典  **/
	public static final String YES = "是";
	public static final String NO = "否";
	public static final String UNDO = "撤回";
	public static final String RECEIVE_TASK= "接收办理";
	
	public static final String YES_KEY = "1";
	public static final String NO_KEY = "2";
	
	public static final int LCGZ = 1;//流程跟踪
	public static final int SPYJ = 2;//审批意见（表单下）
	
	public static final String NO_END = "未结束";
	public static final String NO_SUBMIT = "未结束";
	public static final String EMPTY = "空";
	
	/** 节点相关  **/
	public static final String FRIST_PARENT_TASK_ID="00000000-0000-0000-0000-000000000000";//第一节点的父任务ID
	public static final String EXT_NEXTUSER ="nextTaskUser"; //节点固定办理人key
	
	/** 分页  **/
	public static final int START_SIZE =0; //默认开始行数
	public static final int END_SIZE =20; //默认结束行数
	
	/** 系统参数 **/
	public static final String FLOW_SHEET_CMD = "CLIENT_BPM_FORM_TRACK_OPEN";//流程图参数
	public static final String OAUTH_NAME = "chinamobile"; //单点oauthName
	
}
