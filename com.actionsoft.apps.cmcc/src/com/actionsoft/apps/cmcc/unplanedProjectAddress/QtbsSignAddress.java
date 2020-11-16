package com.actionsoft.apps.cmcc.unplanedProjectAddress;
/**
 * 这个类主要是对加签地址簿进行过滤(取消终止流程和自立国拨计划外项目)牵头部所领导审核节点加签
 * @author zhaoxs
 * @date 2017-07-05
 * */
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class QtbsSignAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advancedModel) {
		String deptIdPath = model.getPathIdOfCache();//过滤部门全路径
		String ParentId = uc.getDepartmentModel().getParentDepartmentId();
		String parentIdPath = DepartmentCache.getModel(ParentId).getPathIdOfCache();
		if(model.getId().equals(uc.getDepartmentModel().getId())){
			return false;
		}else if(deptIdPath.contains(parentIdPath)||parentIdPath.contains(deptIdPath)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext arg0, UserModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

}
