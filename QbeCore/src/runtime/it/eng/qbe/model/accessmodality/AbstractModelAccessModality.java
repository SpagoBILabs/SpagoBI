/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.model.accessmodality;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.ModelField;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class AbstractModelAccessModality implements IModelAccessModality{

	Boolean recursiveFiltering = Boolean.TRUE;
	
	public static final String ATTR_RECURSIVE_FILTERING = "recursiveFiltering";
	
	
	public boolean isEntityAccessible(IModelEntity entity) {
		return true;
	}

	public boolean isFieldAccessible(ModelField field) {
		return true;
	}

	public List getEntityFilterConditions(String entityName) {
		return new ArrayList();
	}

	public List getEntityFilterConditions(String entityName, Properties parameters) {
		return new ArrayList();
	}

	public Boolean getRecursiveFiltering() {
		return recursiveFiltering;
	}

	public void setRecursiveFiltering(Boolean recursiveFiltering) {
		this.recursiveFiltering = recursiveFiltering;
	}

}
