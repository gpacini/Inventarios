
package com.serinse.pers.entity.projectParameter;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(ProjectParameter.class)
public class ProjectParameter_ {
	public static volatile SingularAttribute<ProjectParameter, Long> id;
	public static volatile SingularAttribute<ProjectParameter, String> name;
	public static volatile SingularAttribute<ProjectParameter, String> value;
}
