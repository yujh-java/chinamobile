package com.actionsoft.apps.addons.ldapsync.model;

public class StaticModel {

	/* 删除的部门和人员数量 */
	private int addDeptcount, failAddDeptcount, updateDeptcount, failUpdateDeptcount, delDeptcount, faildelDeptcount;
	private int addUsercount, failAddUsercount, updateUsercount, failUpdateUsercount, delUsercount, faildelUsercount;
	private int searchRootCount;
	public long totalDeptCount, totalUserCount;
	/* 搜索出的待删除部门和人员数量 */
	public long delDeptNum, delUserNum;
	private int ignoreDept, ignoreUser, keepUser, keepDept;

	public void addTotalDeptCount() {
		totalDeptCount++;
	}

	public void addTotalUserCount() {
		totalUserCount++;
	}

	public void addSearchRootCount() {
		searchRootCount++;
	}

	public void addIgnoreDept() {
		ignoreDept++;
	}

	public void addIgnoreUser() {
		ignoreUser++;
	}

	public void addAddDeptcount() {
		addDeptcount++;
	}

	public void addFailAddDeptcount() {
		failAddDeptcount++;
	}

	public void addUpdateDeptcount() {
		updateDeptcount++;
	}

	public void addKeepDept() {
		keepDept++;
	}

	public void addFailUpdateDeptcount() {
		failUpdateDeptcount++;
	}

	public void addUpdateUsercount() {
		updateUsercount++;
	}

	public void addKeepUser() {
		keepUser++;
	}

	public void addFailUpdateUsercount() {
		failUpdateUsercount++;
	}

	public void addAddUsercount() {
		addUsercount++;
	}

	public void addFailAddUsercount() {
		failAddUsercount++;
	}

	public void addDelUsercount() {
		delUsercount++;
	}

	public void addFaildelUsercount() {
		faildelUsercount++;
	}

	public void addDelDeptcount() {
		delDeptcount++;
	}

	public void addFaildelDeptcount() {
		faildelDeptcount++;
	}

	public void addFaildelDeptcount(int i) {
		faildelDeptcount += i;
	}

	public int getUpdateDeptcount() {
		return updateDeptcount;
	}

	public int getAddDeptcount() {
		return addDeptcount;
	}

	public int getUpdateUsercount() {
		return updateUsercount;
	}

	public int getAddUsercount() {
		return addUsercount;
	}

	public int getFaildelUsercount() {
		return faildelUsercount;
	}

	public int getFaildelDeptcount() {
		return faildelDeptcount;
	}

	public int getDelDeptcount() {
		return delDeptcount;
	}

	public int getDelUsercount() {
		return delUsercount;
	}

	public int getFailUpdateUsercount() {
		return failUpdateUsercount;
	}

	public int getFailUpdateDeptcount() {
		return failUpdateDeptcount;
	}

	public int getFailAddUsercount() {
		return failAddUsercount;
	}

	public int getFailAddDeptcount() {
		return failAddDeptcount;
	}

	public int getSearchRootCount() {
		return searchRootCount;
	}

	public long getTotalDeptCount() {
		return totalDeptCount;
	}

	public long getTotalUserCount() {
		return totalUserCount;
	}

	public int getIgnoreDept() {
		return ignoreDept;
	}

	public int getIgnoreUser() {
		return ignoreUser;
	}

	public int getKeepUser() {
		return keepUser;
	}

	public int getKeepDept() {
		return keepDept;
	}

	public long getDelDeptNum() {
		return delDeptNum;
	}

	public void addDelDeptNum() {
		this.delDeptNum++;
	}

	public long getDelUserNum() {
		return delUserNum;
	}

	public void addDelUserNum() {
		this.delUserNum++;
	}

}
