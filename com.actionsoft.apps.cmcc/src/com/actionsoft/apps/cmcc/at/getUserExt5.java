package com.actionsoft.apps.cmcc.at;

import java.util.Iterator;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.exception.AWSExpressionException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 根据传进来的参数获取用户扩展5里的值
 * @author sunk 2017-07-03
 *
 */
public class getUserExt5 extends AbstExpression {
	public getUserExt5(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		String uid = getParameter(expression, 1);//获取用户
		String getField = getParameter(expression, 2);//获取用户扩展的属性值
		String ext5=UserCache.getModel(uid).getExt5();
		 JSONObject jo = new JSONObject();
		 //将用户扩展中的json串中的KEY转换成小写
		 jo = transObject(JSONObject.fromObject(ext5));
		return jo.optString(getField.toLowerCase());
	}
	/**
	 * 将JSONObject中的KEY转成小写
	 * @param o1
	 * @return
	 */
	public static JSONObject transObject(JSONObject o1){
        JSONObject o2=new JSONObject();
         Iterator it = o1.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object object = o1.get(key);
                if(object.getClass().toString().endsWith("String")){
                    o2.accumulate(key.toLowerCase(), object);
                }else if(object.getClass().toString().endsWith("JSONObject")){
                    o2.accumulate(key.toLowerCase(), getUserExt5.transObject((JSONObject)object));
                }else if(object.getClass().toString().endsWith("JSONArray")){
                    o2.accumulate(key.toLowerCase(), getUserExt5.transArray(o1.getJSONArray(key)));
                }
            }
            return o2;
    }
	/**
	 * 将JSONArray中的KEY转成小写
	 * @param o1
	 * @return
	 */
	public static JSONArray transArray(JSONArray o1){
        JSONArray o2 = new JSONArray();
        for (int i = 0; i < o1.size(); i++) {
            Object jArray=o1.getJSONObject(i);
            if(jArray.getClass().toString().endsWith("JSONObject")){
                o2.add(getUserExt5.transObject((JSONObject)jArray));
            }else if(jArray.getClass().toString().endsWith("JSONArray")){
                o2.add(getUserExt5.transArray((JSONArray)jArray));
            }
        }
        return o2;
    }

}
