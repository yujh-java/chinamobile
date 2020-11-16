package com.actionsoft.apps.addons.ldapsync.model;

import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;

public class DepartmentImpl extends DepartmentModelImpl {

	private boolean closed;

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

}
