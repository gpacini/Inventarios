
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
	
	public ProjectParameter findByName(String name){
		return projectParameterDao.findByName(name);
	}
	
	public ProjectParameter getOrInitialize(ProjectParameterEnum projectParameterEnum){
		ProjectParameter pp = findProjectParameterByParemeterName(projectParameterEnum);
		if( pp == null ){
			pp = initializeProjectParameter(projectParameterEnum);
		}
		return pp;
	}
	
	public ProjectParameter getOrInitialize(String name){
		ProjectParameter pp = findByName(name);
		if( pp == null ){
			pp = initializeProjectParameter(name);
		}
		return pp;
	}
	
	public ProjectParameter initializeProjectParameter(String name){
		ProjectParameter pp = new ProjectParameter();
		pp.setName(name);
		pp.setValue("");
		pp.setIsNew(true);
		return pp;
	}
	
	public ProjectParameter initializeProjectParameter(ProjectParameterEnum enumName){
		ProjectParameter pp = new ProjectParameter();
		pp.setName(enumName.getName());
		pp.setValue("");
		pp.setIsNew(true);
		return pp;
	}

}
