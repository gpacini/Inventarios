package com.serinse.pers.dao.projectParameter;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.projectParameter.ProjectParameter;
import com.serinse.pers.entity.projectParameter.ProjectParameter_;

@Stateless
@LocalBean
public class DAOJPAProjectParameter extends DAOJPABase<ProjectParameter, Long> {
	public static Logger LOG = Logger.getLogger(DAOJPAProjectParameter.class);

	public DAOJPAProjectParameter() {
		super(ProjectParameter.class);
	}

	/**
	 * 
	 * @author Francisco Muñoz
	 * @date Sep 10, 2014
	 * @description_method returns a ProjectParameter object in base of param
	 *                     projectParameterId
	 * @param projectParameterEnum
	 * @return
	 */
	public ProjectParameter findProjectParameterByParemeterName(ProjectParameterEnum projectParameterEnum) {
		CriteriaBuilder builder = this.em.getCriteriaBuilder();
		CriteriaQuery<ProjectParameter> query = builder.createQuery(ProjectParameter.class);
		Root<ProjectParameter> form = query.from(ProjectParameter.class);
		query.select(form).where(builder.equal(form.get(ProjectParameter_.name), projectParameterEnum.getName()));
		try {
			return this.em.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
