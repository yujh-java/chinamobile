package com.actionsoft.apps.cmcc.integration.webapi;
import com.actionsoft.bpms.api.OpenApiClient;
import net.sf.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CallWebAPITest  {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String apiServer = "http://10.1.5.113:8088/workflow/openapi";
    private static String accessKey = "ITMS";
    private static String secret = "ITMS";
	/*private static String apiServer = "http://192.168.4.67:8081/portal/openapi";
    private static String accessKey = "awscmcc";
    private static String secret = "awscmcc";*/
    
    public static void startProcess() {
        try {
            String apiMethod = "WorkFlow.startProcess";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "chengweiqiang@hq.cmcc");
            args.put("flowType", "itsm_data_service_apply");
            String timeCS = sdf.format(System.currentTimeMillis());
            args.put("processTitle", "测试的一");
            args.put("taskTitle", timeCS+"测试");
            args.put("nextUserId", "chengweiqiang@hq.cmcc");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String exec = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+exec);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    public static void getTaskInfo() {
        try {
            String apiMethod = "WorkFlow.getTaskInfo";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "zuojing@hq.cmcc");
            args.put("taskId", "b14872e9-a1df-4469-9292-3c2971a6ea22");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getTaskAction() {
        try {
            String apiMethod = "WorkFlow.getTaskAction";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "wangyonggui@aspirecn.com");
            args.put("taskId", "476f29fa-1352-4aaf-a63d-c9e329eb38ef");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void next() {
        try {
            String apiMethod = "WorkFlow.next";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "zuojing@hq.cmcc");
            args.put("taskId", "3e53cf12-74a0-46fc-828b-624912a6301f");
            args.put("actionName", "送系统负责人审核");
            args.put("actionId", "obj_9475bf68a9c44b569eabad178a4aaf0a");
            args.put("nextStepId", "obj_c8b0f196e5000001d7365da0da4f1a23");
            args.put("nextTaskUserId", "zuojing@hq.cmcc");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getTaskList() {
        try {
            String apiMethod = "WorkFlow.getTaskList";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "mingming_dong@asdc.com.cn");
            args.put("flowType", "CCMS-SQ");
            args.put("taskStates", 1);
            args.put("IOSID", "");
            args.put("taskUserId", "mingming_dong@asdc.com.cn");
            args.put("taskUserDepartmentId", "");
            args.put("taskUserCompanyId", "");
            args.put("taskTitle", "");
            args.put("processCreateUserId", "");
            args.put("processCreateUserDepartmentId", "");
            args.put("processCreateUserCompanyId", "");
            args.put("taskBeginDate", "");
            args.put("taskEndDate", "");
            args.put("processStatus", "");
            args.put("pageNO", 0);
            args.put("pageSize", -1);
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 收回任务
     */
    public static void withdrawTask(){
        try {
            String apiMethod = "WorkFlow.withdrawTask";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "yujunhao@hq.cmcc");
            args.put("taskId", "ad321cfb-828e-4837-b825-5c51efc0e8df");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 注销流程
     */
    public static void deleteProcessInstance(){
        try {
            String apiMethod = "WorkFlow.deleteProcessInstance";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "yujunhao@hq.cmcc");
            args.put("taskId", "03357e03-646a-4763-842d-6b8f0f178e83");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 设置流程变量
     */
    public static void context(){
        try {
            String apiMethod = "WorkFlow.context";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "yujunhao@hq.cmcc");
            args.put("taskId", "7bac47e3-865a-45c5-93a8-5a46ad7f42cd");
            JSONObject json =new JSONObject();
            json.put("a", "1");
            json.put("b", "2");
            args.put("context", json.toString());
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void tracks() {
        try {
            String apiMethod = "WorkFlow.tracks";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "yujunhao@hq.cmcc");
            args.put("taskId", "cf84c0c8-f8ec-44fc-91d5-50a68059a561");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void occupy() {
        try {
            String apiMethod = "WorkFlow.occupy";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "yujunhao2@hq.cmcc");
            args.put("taskId", "434f3bb5-47f9-427e-9666-92dc11a0c703");
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getProcessList() {
        try {
            String apiMethod = "WorkFlow.getProcessList";
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("opeUserId", "zuojing@hq.cmcc");
            args.put("pageNO", 1);
            long currentTimeMillis = System.currentTimeMillis();
            OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret,
            OpenApiClient.FORMAT_JSON);
            String r = client.exec(apiMethod, args);
            long currentTimeMillis1 = System.currentTimeMillis();
            long time =currentTimeMillis1-currentTimeMillis;
            System.out.println(">>"+time);
            System.out.println(">>"+r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]){
    	//startProcess();
    	//getTaskInfo();
        getTaskAction();
    	//next();
    	//getTaskList();
    	//withdrawTask();
    	//deleteProcessInstance();
    	//context();
    	//tracks();
    	//occupy();
    	//getProcessList();
    }

}