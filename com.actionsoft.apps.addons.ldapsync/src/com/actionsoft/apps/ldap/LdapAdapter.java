package com.actionsoft.apps.ldap;

import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import java.util.List;
import org.json.JSONObject;

public class LdapAdapter extends com.actionsoft.apps.addons.ldapsync.LdapAdapter {
	public static List<BO> orderDeptVC;
	
	public DepartmentImpl getDepartment(LdapDept ldapDept, DepartmentImpl deptModel, LdapConf ldapConf) {
		String status = "";
		String deptid = "";
		String displayName = "";
		try {
			status = ldapDept.getLdapVal("status");
			deptid = ldapDept.getLdapVal("o");
			displayName = ldapDept.getLdapVal("displayName");
			String displayOrder = ldapDept.getLdapVal("displayOrder");
			if (status.equals("1"))
				deptModel.setClosed(true);
			else {
				deptModel.setClosed(false);
			}

			if (displayOrder.equals("")) {
				if (deptModel.getOrderIndex() < 10000)
					deptModel.setOrderIndex(10000);
			} else {
				String[] displayOrders = displayOrder.split("/");
				if (displayOrders.length == 2) {
					if ((isNumeric(displayOrders[1])) && (displayOrders[1].length() < 6)
							&& (Integer.parseInt(displayOrders[1]) < 10000)) {
						deptModel.setOrderIndex(Integer.parseInt(displayOrders[1]));
					}

				} else if ((!displayOrders[0].equals("")) && (isNumeric(displayOrders[0]))
						&& (displayOrders[0].length() < 6) && (Integer.parseInt(displayOrders[0]) < 10000))
					deptModel.setOrderIndex(Integer.parseInt(displayOrders[0]));
				else {
					deptModel.setOrderIndex(10000);
				}
			}

			System.out.println("Close:" + deptModel.isClosed() + " deptid:" + deptid + " displayName:" + displayName
					+ " displayOrder:" + displayOrder + " deptModeldisplayOrder:" + deptModel.getOrderIndex());
			System.err.println("Close:" + deptModel.isClosed() + " deptid:" + deptid + " displayName:" + displayName
					+ " displayOrder:" + displayOrder + " deptModeldisplayOrder:" + deptModel.getOrderIndex());
			return deptModel;
		} catch (Exception e) {
			System.err.println(deptid + " LDAP同步异常失败!");
			e.printStackTrace();
		}

		return null;
	}

	public UserModelImpl getUser(LdapUser ldapUser, UserModelImpl userModel, LdapConf ldapConf) {
		String status = "";
		String uid = "";
		try {
			status = ldapUser.getLdapVal("status");
			String cn = ldapUser.getLdapVal("cn");
			uid = ldapUser.getLdapVal("uid");
			if ((uid.indexOf("admin") != -1) || (cn.indexOf("管理员") != -1)) {
				return null;
			}
			String mail = ldapUser.getLdapVal("mail");
			String mobile = ldapUser.getLdapVal("mobile");
			String employeeType = ldapUser.getLdapVal("employeeType");
			String positionLevel = ldapUser.getLdapVal("positionLevel");
			String title = ldapUser.getLdapVal("title");

			String employeeNumber = ldapUser.getLdapVal("employeeNumber");
			String level = ldapUser.getLdapVal("level");
			String levelName = ldapUser.getLdapVal("levelName");
			String function = ldapUser.getLdapVal("function");
			String displayOrder = ldapUser.getLdapVal("displayOrder");
			JSONObject jo = new JSONObject();
			jo.put("employeeType", employeeType);
			jo.put("employeeNumber", employeeNumber);
			jo.put("level", level);
			jo.put("levelName", levelName);
			jo.put("function", function);
			jo.put("title", title);
			jo.put("status", status);
			if (status.equals("0")) {
				userModel.setClosed(true);
			}
			userModel.setExt1(positionLevel);
			userModel.setExt2(displayOrder);

			userModel.setExt5(jo.toString());
			userModel.setEmail(mail);
			userModel.setMobile(mobile);

			if (displayOrder.equals("")) {
				if (userModel.getOrderIndex() < 10000)
					userModel.setOrderIndex(10000);
			} else {
				String[] displayOrders = displayOrder.split(",")[0].split("/");
				if (displayOrders.length == 2) {
					if ((isNumeric(displayOrders[1])) && (displayOrders[1].length() < 6)
							&& (Integer.parseInt(displayOrders[1]) < 10000)) {
						userModel.setOrderIndex(Integer.parseInt(displayOrders[1]));
					}

				} else if ((!displayOrders[0].equals("")) && (isNumeric(displayOrders[0]))
						&& (displayOrders[0].length() < 6) && (Integer.parseInt(displayOrders[0]) < 10000))
					userModel.setOrderIndex(Integer.parseInt(displayOrders[0]));
				else {
					userModel.setOrderIndex(10000);
				}
			}

			System.out.println("uid:" + uid + " displayOrder:" + displayOrder + " positionLevel:" + positionLevel
					+ " userModeldisplayOrder:" + userModel.getOrderIndex());
			System.err.println("uid:" + uid + " displayOrder:" + displayOrder + " positionLevel:" + positionLevel
					+ " userModeldisplayOrder:" + userModel.getOrderIndex());
			return userModel;
		} catch (Exception e) {
			System.err.println(uid + " LDAP同步异常失败!");
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
}