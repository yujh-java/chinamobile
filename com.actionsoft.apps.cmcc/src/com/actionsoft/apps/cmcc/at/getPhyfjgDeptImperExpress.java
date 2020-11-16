package com.actionsoft.apps.cmcc.at;
/**
 * 获取配合研发机构接口人(未使用)
 * @author nch
 * @date 20170622
 */

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSExpressionException;

public class getPhyfjgDeptImperExpress extends AbstExpression {
	private ExpressionContext _atContext;
	public getPhyfjgDeptImperExpress(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
		_atContext = atContext;
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		String userid = _atContext.getUserContext().getUID();//当前登录人账号
		String processId = getParameter(expression, 1);//流程实例ID
		//配合研发机构部们ID
		String phyfjgbmid = DBSql.getString("SELECT PHYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processId});
		if(!UtilString.isEmpty(phyfjgbmid)){
			String[] deptIdArr = phyfjgbmid.split(",");
			int length = deptIdArr.length;
			StringBuffer sb = new StringBuffer();
			for(int i = 0;i<length;i++){
				sb.append(userid+" ");
			}
			return sb.toString().trim();
		}
		return null;
	}

}
