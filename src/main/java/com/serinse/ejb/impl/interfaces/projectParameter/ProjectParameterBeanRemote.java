package com.serinse.ejb.impl.interfaces.projectParameter;

import javax.ejb.Remote;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.pers.entity.projectParameter.ProjectParameter;

@Remote
public interface ProjectParameterBeanRemote {
	public ProjectParameter findProjectParameterByParemeterName(
			ProjectParameterEnum projectParameterEnum);

	public void saveProjectParameter(
			ProjectParameter buildGeneralProjectParametersTimesRecurring);

	public void updateProjectParameter(ProjectParameter projectParameter);
}
