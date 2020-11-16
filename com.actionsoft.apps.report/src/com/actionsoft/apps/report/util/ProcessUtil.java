package com.actionsoft.apps.report.util;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 工具类
 * 
 * @author zhaoxs
 * @date 2017-09-10
 * 
 */
public class ProcessUtil {
	// 获取流程经办人

	public static String getPerson(String processid) {
		Connection conn = null;
		StringBuffer namesbf = new StringBuffer();
		try {
			conn = DBSql.open();
			List<String> useridList = new ArrayList<String>();
			List<ProcessInstance> parentList = SDK.getProcessQueryAPI().parentProcessInstId(processid).list();
			List<RowMap> userList = DBSql.getMaps(conn,
					"SELECT DISTINCT TARGET FROM WFH_TASK WHERE PROCESSINSTID=? AND ACTIVITYTYPE<>?",
					new Object[] { processid, "callActivity" });
			if (userList != null && userList.size() > 0) {
				for (int i = 0; i < userList.size(); i++) {
					String userid = userList.get(i).getString("TARGET");
					String username = DBSql.getString(conn, "SELECT USERNAME FROM ORGUSER WHERE USERID=?",
							new Object[] { userid });
					useridList.add(userid);
					if (i == userList.size() - 1) {
						namesbf.append(username);
					} else {
						namesbf.append(username).append(",");
					}
				}
			}
			if (parentList != null && parentList.size() > 0) {
				List<String> disUserList = new ArrayList<String>();
				for (int i = 0; i < parentList.size(); i++) {
					String pid = parentList.get(i).getId();
					List<RowMap> userList1 = DBSql.getMaps(conn,
							"SELECT DISTINCT TARGET FROM WFH_TASK WHERE PROCESSINSTID=?", new Object[] { pid });
					if (userList1 != null && userList1.size() > 0) {
						for (int j = 0; j < userList1.size(); j++) {
							String user = userList1.get(j).getString("TARGET");
							if (!useridList.contains(user)) {
								if (!disUserList.contains(user)) {
									disUserList.add(user);
								}
							}
						}

					}
				}
				String username;
				for (int k = 0; k < disUserList.size(); k++) {
					username = UserCache.getModel(disUserList.get(k)).getUserName();
					if (k == 0) {
						namesbf.append(",");
					}
					if (k == disUserList.size() - 1) {
						namesbf.append(username);
					} else {
						namesbf.append(username).append(",");
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return namesbf.toString();
	}

	// 获取当前节点名称
	public static String getActivityName(String activityId, String processid, String processDefid) {
		String activityName = "";// 流程环节
		String name = null;
		List<TaskInstance> list = null;
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (UtilString.isEmpty(activityId)) {
				activityName = "已结束";
			} else {
				StringBuffer notesbf = new StringBuffer();
				String str[] = activityId.split(",");
				if (str.length == 1) {
					list = SDK.getTaskQueryAPI().userTaskOfWorking().activityDefId(activityId).processInstId(processid)
							.connection(conn).list();
					if (list != null && list.size() > 0) {
						activityName = ProcessUtil.getNoteIdName(processDefid, activityId);
					} else {
						// 子流程任务
						List<ProcessInstance> subList = SDK.getProcessQueryAPI().parentProcessInstId(processid)
								.connection(conn).list();
						if (subList != null && subList.size() > 0) {
							for (int j = 0; j < subList.size(); j++) {
								String id = subList.get(j).getId();
								List<TaskInstance> subtask = SDK.getTaskQueryAPI().userTaskOfWorking().processInstId(id)
										.connection(conn).list();
								if (subtask != null && subtask.size() > 0) {
									for (int index = 0; index < subtask.size(); index++) {
										activityName = ProcessUtil.getNoteIdName(subtask.get(index).getProcessDefId(),
												subtask.get(index).getActivityDefId());
									}
								}

							}
						}
					}

				} else {
					// 子流程任务
					List<ProcessInstance> subList = SDK.getProcessQueryAPI().parentProcessInstId(processid)
							.activeProcess().connection(conn).list();
					if (subList != null && subList.size() > 0) {
						for (int j = 0; j < subList.size(); j++) {
							String id = subList.get(j).getId();
							List<TaskInstance> subtask = SDK.getTaskQueryAPI().userTaskOfWorking().processInstId(id)
									.connection(conn).list();
							if (subtask != null && subtask.size() > 0) {
								for (int index = 0; index < subtask.size(); index++) {
									name = ProcessUtil.getNoteIdName(subtask.get(index).getProcessDefId(),
											subtask.get(index).getActivityDefId());
									if (index == subtask.size() - 1) {
										if (!notesbf.toString().contains(name)) {
											if (j != 0) {
												notesbf.append(",");
											}
											notesbf.append(name);
										}
									}
								}
							}
						}
					}
					activityName = notesbf.toString();
				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return activityName;
	}

	// 获取节点名称
	public static String getNoteIdName(String processDefid, String activityId) {
		UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
		String activityName = "";
		if (taskModel != null) {
			activityName = taskModel.name;
		} // 节点名称
		return activityName;
	}

	// 获取上一节点节点名称
	public static String getNoteName(String processid, String activityId, String processDefid) {

		String notename = "";
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (UtilString.isEmpty(activityId)) {
				List<HistoryTaskInstance> his = SDK.getHistoryTaskQueryAPI().processInstId(processid).orderByEndTime()
						.asc().list();
				notename = ProcessUtil.getNoteIdName(his.get(his.size() - 1).getProcessDefId(),
						his.get(his.size() - 1).getActivityDefId());
			} else {
				List<TaskInstance> ctask = SDK.getTaskQueryAPI().userTaskOfWorking().processInstId(processid)
						.activityDefId(activityId).connection(conn).list();
				String activityType = "";
				HistoryTaskInstance hislist = null;
				StringBuffer notestr = new StringBuffer(); // 上一节点拼接
				String taskinstid = null;
				if (ctask != null && ctask.size() > 0) {
					for (int i = 0; i < ctask.size(); i++) {
						taskinstid = ctask.get(i).getParentTaskInstId();
						// 获取上一节点流程环节名称
						if (taskinstid.equals("00000000-0000-0000-0000-000000000000")) {
							notename = "无";
						} else {
							if (!UtilString.isEmpty(taskinstid)) {
								hislist = SDK.getHistoryTaskQueryAPI().connection(conn).detailById(taskinstid);
								if (!UtilString.isEmpty(hislist)) {
									activityType = hislist.getActivityType();
									if (activityType.equals("callActivity")) {
										List<RowMap> htask = DBSql.getMaps(
												"SELECT * FROM WFH_TASK  WHERE PROCESSINSTID IN (SELECT ID from WFC_PROCESS where parentprocessinstid=?) AND READTIME<>'' ORDER BY ENDTIME ASC",
												new Object[] { hislist.getProcessInstId() });
										if (htask.size() > 0 && htask != null) {
											notename = ProcessUtil.getNoteIdName(
													htask.get(htask.size() - 1).getString("PROCESSDEFID"),
													htask.get(htask.size() - 1).getString("ACTIVITYDEFID"));
										}
									} else {
										notename = ProcessUtil.getNoteIdName(processDefid, hislist.getActivityDefId());
									}

								}
							}
						}
					}

				} else {
					List<ProcessInstance> subList = SDK.getProcessQueryAPI().parentProcessInstId(processid)
							.activeProcess().list();
					if (subList != null && subList.size() > 0) {
						for (int i = 0; i < subList.size(); i++) {
							String id = subList.get(i).getId();
							List<TaskInstance> subtask = SDK.getTaskQueryAPI().userTaskOfWorking().processInstId(id)
									.connection(conn).list();
							if (subtask != null && subtask.size() > 0) {
								for (int j = 0; j < subtask.size(); j++) {
									if (subtask.get(j).getParentTaskInstId()
											.equals("00000000-0000-0000-0000-000000000000")) {
										List<RowMap> htask = DBSql.getMaps(
												"SELECT * FROM WFH_TASK WHERE PROCESSINSTID=? AND READTIME <> '' ORDER BY ENDTIME asc",
												new Object[] { processid });
										if (htask != null && htask.size() > 0) {
											String name = ProcessUtil.getNoteIdName(
													htask.get(htask.size() - 1).getString("PROCESSDEFID"),
													htask.get(htask.size() - 1).getString("ACTIVITYDEFID"));

											if (!notestr.toString().contains(name)) {
												if (notestr.toString().length() > 0) {
													notestr.append(",");
												}
												notestr.append(name);
											}

										}

									} else {
										HistoryTaskInstance histask = SDK.getHistoryTaskQueryAPI()
												.detailById(subtask.get(0).getParentTaskInstId());
										if (!UtilString.isEmpty(histask)) {
											String noteIdname = ProcessUtil.getNoteIdName(histask.getProcessDefId(),
													histask.getActivityDefId());

											if (!notestr.toString().contains(noteIdname)) {
												if (notestr.toString().length() > 0) {
													notestr.append(",");
												}
												notestr.append(noteIdname);
											}

										} else {
											List<HistoryTaskInstance> hitask = SDK.getHistoryTaskQueryAPI()
													.processInstId(processid).orderByEndTime().asc().connection(conn)
													.list();
											if (hitask != null && hitask.size() > 0) {
												String Defid = hitask.get(hitask.size() - 1).getProcessDefId();
												String actId = hitask.get(hitask.size() - 1).getActivityDefId();
												String notename1 = ProcessUtil.getNoteIdName(Defid, actId);
												if (!notestr.toString().contains(notename1)) {
													if (i != 0) {
														notestr.append(",");
													}
													notestr.append(notename1);
												}
											}

										}

									}
									notename = notestr.toString();
								}

							} else {
								List<HistoryTaskInstance> histask = SDK.getHistoryTaskQueryAPI()
										.processInstId(processid).orderByEndTime().asc().connection(conn).list();
								if (histask != null && histask.size() > 0) {
									String Defid = histask.get(histask.size() - 1).getProcessDefId();
									String actId = histask.get(histask.size() - 1).getActivityDefId();
									notename = ProcessUtil.getNoteIdName(Defid, actId);
								}
							}
						}
					} else {
						List<HistoryTaskInstance> histask = SDK.getHistoryTaskQueryAPI().processInstId(processid)
								.orderByEndTime().asc().connection(conn).list();
						if (histask != null && histask.size() > 0) {
							String Defid = histask.get(histask.size() - 1).getProcessDefId();
							String actId = histask.get(histask.size() - 1).getActivityDefId();
							notename = ProcessUtil.getNoteIdName(Defid, actId);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return notename;

	}

	public static String getCostTime(String createTime, String processEndTime) throws ParseException {

		long day, hour, minute, second;
		long times = 0;// 处理时长
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(createTime);
		StringBuffer sbf = new StringBuffer();
		if (!UtilString.isEmpty(processEndTime)) {
			Date enddate = sdf.parse(processEndTime);
			times = enddate.getTime() - date.getTime();
			// SDK.getLogAPI().getLogger(this.getClass()).error("times="+times);
			day = (times / (60 * 60 * 24 * 1000));
			hour = (times % (60 * 60 * 24 * 1000)) / (60 * 60 * 1000);
			minute = (((times % (60 * 60 * 24 * 1000)) % (60 * 60 * 1000))) / (60 * 1000);
			second = ((((times % (60 * 60 * 24 * 1000)) % (60 * 60 * 1000)) % (60 * 1000))) / 1000;
			DecimalFormat df = new DecimalFormat("00");
			sbf.append(day).append(" ").append(df.format(hour)).append(":").append(df.format(minute)).append(":")
					.append(df.format(second));
		}
		return sbf.toString();
	}

	// 任务查询查找上一节点
	public static String getPreviousNoteName(List<RowMap> ctask, String processid, String endtime) {
		String noteName = "";// 上一节点环节名称
		String taskinstid = "";
		String state = "";
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (ctask != null && ctask.size() > 0) {
				for (int i = 0; i < ctask.size(); i++) {
					taskinstid = ctask.get(i).getString("PARENTTASKINSTID");// 当前父任务实例ID
					state = ctask.get(i).getString("TASKSTATE");// 当前任务状态
				}
			}
			// 获取上一节点流程环节名称
			if (taskinstid.equals("00000000-0000-0000-0000-000000000000")) {
				ProcessInstance pinstace = SDK.getProcessQueryAPI().connection(conn).detailById(processid);
				if (!UtilString.isEmpty(pinstace)) {
					String parentProid = pinstace.getParentProcessInstId();
					if (UtilString.isEmpty(parentProid)) {
						noteName = "无";
					} else {
						List<RowMap> htask = DBSql.getMaps(
								"SELECT * FROM WFH_TASK WHERE PROCESSINSTID=? AND READTIME <> '' ORDER BY ENDTIME asc",
								new Object[] { parentProid });
						if (htask != null && htask.size() > 0) {
							noteName = ProcessUtil.getNoteIdName(htask.get(htask.size() - 1).getString("PROCESSDEFID"),
									htask.get(htask.size() - 1).getString("ACTIVITYDEFID"));
						}
					}

				}
			} else {
				if (state.equals("11")) { // 任务为会签或加签
					if (UtilString.isEmpty(endtime)) { // 待办
						TaskInstance tinstace = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn)
								.detailById(taskinstid);
						if (!UtilString.isEmpty(tinstace)) {
							int taskstate = tinstace.getState();
							String parentTaskid = tinstace.getParentTaskInstId();
							if (taskstate == 11) {
								TaskInstance tinstace1 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn)
										.detailById(parentTaskid);
								if (!UtilString.isEmpty(tinstace1)) {
									HistoryTaskInstance his = SDK.getHistoryTaskQueryAPI().connection(conn)
											.detailById(tinstace1.getParentTaskInstId());
									if (!UtilString.isEmpty(his)) {
										noteName = ProcessUtil.getNoteIdName(his.getProcessDefId(),
												his.getActivityDefId());
									}

								}
							} else {
								HistoryTaskInstance his = SDK.getHistoryTaskQueryAPI().connection(conn)
										.detailById(parentTaskid);
								if (!UtilString.isEmpty(his)) {
									noteName = ProcessUtil.getNoteIdName(his.getProcessDefId(), his.getActivityDefId());
								}
							}

						}
					} else { // 已办
						HistoryTaskInstance his = SDK.getHistoryTaskQueryAPI().connection(conn).detailById(taskinstid);
						if (!UtilString.isEmpty(his)) {
							int taskstate = his.getState();
							String parentTaskid = his.getParentTaskInstId();
							if (taskstate == 11) {
								HistoryTaskInstance his1 = SDK.getHistoryTaskQueryAPI().connection(conn)
										.detailById(parentTaskid);
								if (!UtilString.isEmpty(his1)) {
									HistoryTaskInstance his2 = SDK.getHistoryTaskQueryAPI().connection(conn)
											.detailById(his1.getParentTaskInstId());
									if (!UtilString.isEmpty(his2)) {
										noteName = ProcessUtil.getNoteIdName(his2.getProcessDefId(),
												his2.getActivityDefId());
									}

								}
							} else {
								HistoryTaskInstance his2 = SDK.getHistoryTaskQueryAPI().connection(conn)
										.detailById(parentTaskid);
								if (!UtilString.isEmpty(his2)) {
									noteName = ProcessUtil.getNoteIdName(his2.getProcessDefId(),
											his.getActivityDefId());
								}
							}

						} else {

							TaskInstance tinstance = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn)
									.detailById(taskinstid);
							if (!UtilString.isEmpty(tinstance)) {
								int taskstate = tinstance.getState();
								String parentTaskid = tinstance.getParentTaskInstId();
								if (taskstate == 11) {
									HistoryTaskInstance hist = SDK.getHistoryTaskQueryAPI().connection(conn)
											.detailById(parentTaskid);
									if (!UtilString.isEmpty(hist)) {
										HistoryTaskInstance histi = SDK.getHistoryTaskQueryAPI().connection(conn)
												.detailById(hist.getParentTaskInstId());
										if (!UtilString.isEmpty(histi)) {
											noteName = ProcessUtil.getNoteIdName(histi.getProcessDefId(),
													histi.getActivityDefId());
										}

									}
								} else {
									HistoryTaskInstance hist = SDK.getHistoryTaskQueryAPI().connection(conn)
											.detailById(parentTaskid);
									if (!UtilString.isEmpty(hist)) {
										noteName = ProcessUtil.getNoteIdName(hist.getProcessDefId(),
												hist.getActivityDefId());
									}
								}

							}
						}
					}
				} else { // 正常任务
					HistoryTaskInstance his = SDK.getHistoryTaskQueryAPI().detailById(taskinstid);
					if (!UtilString.isEmpty(his)) {
						if ("callActivity".equals(his.getActivityType())) {
							List<RowMap> task = DBSql.getMaps(
									"SELECT * FROM WFH_TASK  WHERE PROCESSINSTID IN (SELECT ID from WFC_PROCESS where parentprocessinstid=?) AND READTIME<>'' ORDER BY ENDTIME ASC",
									new Object[] { his.getProcessInstId() });
							if (task != null && task.size() > 0) {
								noteName = ProcessUtil.getNoteIdName(
										task.get(task.size() - 1).getString("PROCESSDEFID"),
										task.get(task.size() - 1).getString("ACTIVITYDEFID"));
							}

						} else {
							noteName = ProcessUtil.getNoteIdName(his.getProcessDefId(), his.getActivityDefId());
						}

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return noteName;

	}

}
