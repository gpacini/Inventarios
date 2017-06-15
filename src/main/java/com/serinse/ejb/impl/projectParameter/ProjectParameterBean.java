
package com.serinse.ejb.impl.projectParameter;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.ejb.impl.interfaces.projectParameter.ProjectParameterBeanRemote;
import com.serinse.pers.dao.projectParameter.DAOJPAProjectParameter;
import com.serinse.pers.entity.projectParameter.ProjectParameter;

/**
 * 
 * @author Giancarlo Pacini
 *
 */
@Stateless
@LocalBean
public class ProjectParameterBean implements ProjectParameterBeanRemote {

	public static Logger LOG = Logger.getLogger(ProjectParameterBean.class);
	@EJB
	private DAOJPAProjectParameter projectParameterDao;

	public ProjectParameter findProjectParameterByParemeterName(ProjectParameterEnum projectParameterEnum) {
		return projectParameterDao.findProjectParameterByParemeterName(projectParameterEnum);
	}

	public void saveProjectParameter(ProjectParameter buildGeneralProjectParametersTimesRecurring) {
		projectParameterDao.merge(buildGeneralProjectParametersTimesRecurring);
	}
	
	public void updateProjectParameter(ProjectParameter projectParameter){
		projectParameterDao.merge(projectParameter);
	}

}
