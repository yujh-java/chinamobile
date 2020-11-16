package com.actionsoft.apps.cmcc.integration.aslp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;

/** 
* @author yujh
* @version 创建时间：2019年9月4日 下午2:50:05 
* 状态回写接口，支持所有流程调用
*/
public class StateWriteBackAslp implements ASLP{
	@Override
	@Meta(parameter = {
			"name:'url',required:true,desc:'回写接口地址'",
			"name:'parentProcessId',required:false,desc:'父流程实例ID'",
			"name:'processId',required:true,desc:'流程实例ID'",
			"name:'taskId',required:true,desc:'任务实例ID'",
			"name:'parentTaskId',required:true,desc:'父任务实例ID'", 
			"name:'taskBeginTime',required:true,desc:'任务接受时间'", 
			"name:'userName',required: false,desc:'流程创建者姓名'", 
			"name:'submitterid',required:true,desc:'任务提交人'", 
			"name:'ownerId',required:true,desc:'当前任务办理者'", 
			"name:'node',required:false,desc:'环节名称'", 
			"name:'state',required:true, desc:'流程状态'",
			"name:'taskState',required:true,desc:'流程平台任务状态'", 
			"name:'EXT1',required:false,desc:'扩展字段一'" ,
			"name:'EXT2',required:false,desc:'扩展字段二'"
			})
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		String parentProcessId = params.get("parentProcessId").toString();//父流程实例ID
		String processId = params.get("processId").toString();//流程实例ID
		String taskId = params.get("taskId").toString();//任务实例ID 
		String parentTaskId = params.get("parentTaskId").toString();//父任务实例ID
		String taskBeginTime = params.get("taskBeginTime").toString();//任务接受时间
		String userName = params.get("userName").toString();//流程创建者
		String submitterid = params.get("submitterid").toString();//任务提交人
		String ownerId = params.get("ownerId").toString();//当前任务办理者
		String node = params.get("node").toString();//环节名称
		//流程状态（两种状态：2--【退回起草】、3--【审批中】、4--【审批通过】、5 --【子流程结束相当于会签结束】、6--【收回类任务】）
		String state = params.get("state").toString();
		String taskState = params.get("taskState").toString();//流程平台任务状态
		String EXT1 = params.get("EXT1").toString();//扩展字段一
		String EXT2 = params.get("EXT2").toString();//扩展字段二
		String url = params.get("url").toString();//回写接口地址
		
		/** 拼接参数并发送请求 **/
		StringBuilder sb = new StringBuilder(url);
		sb.append("?parentProcessId=");//父流程实例ID
	    sb.append(parentProcessId);
	    sb.append("&processId=");//当前流程实例ID
	    sb.append(processId);
	    sb.append("&taskId=");//任务实例ID
	    sb.append(taskId);
	    sb.append("&parentTaskId=");//父任务实例ID
	    sb.append(parentTaskId);
	    sb.append("&taskBeginTime=");//任务开始时间
	    try {
			taskBeginTime= URLEncoder.encode(taskBeginTime, "UTF-8");
			taskBeginTime = taskBeginTime.replace("+", "%20");
			node=URLEncoder.encode(node, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    sb.append(taskBeginTime);
	    sb.append("&state=");//流程状态
	    sb.append(state);//流程状态
	    sb.append("&userName=");//流程创建者
	    sb.append(submitterid);
	    sb.append("&submitterid=");//流程提交人
	    sb.append(submitterid);
	    sb.append("&ownerId=");//当前任务办理者
	    sb.append(ownerId);
	    sb.append("&node=");//节点名称
	    sb.append(node);
	    sb.append("&taskState=");//任务状态
	    sb.append(taskState);
	    sb.append("&EXT1=");//扩展标记1
	    sb.append(EXT1);
	    sb.append("&EXT2");//扩展标记2
	    sb.append(EXT2);
	    System.err.println("======url:"+sb.toString()+"=====");
	    String json = CmccUrlUtil.get(url);
	    System.err.println(">>>>回写ALSPshuju ");
	    	
	    try {
	    	
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ro;
	}

}
