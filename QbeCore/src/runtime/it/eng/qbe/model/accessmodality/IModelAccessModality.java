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
import it.eng.qbe.model.structure.IModelField;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IModelAccessModality {
	
	
	boolean isEntityAccessible(IModelEntity entity);
	
	/**
	 * Checks if is field accessible.
	 * 
	 * @param tableName the table name
	 * @param fieldName the field name
	 * 
	 * @return true, if is field accessible
	 */
	boolean isFieldAccessible( IModelField field );
	
	/**
	 * Gets the entity filter conditions.
	 * 
	 * @param entityName the entity name
	 * 
	 * @return the entity filter conditions
	 */
	List getEntityFilterConditions(String entityName);
	
	/**
	 * Gets the entity filter conditions.
	 * 
	 * @param entityName the entity name
	 * @param parameters the parameters
	 * 
	 * @return the entity filter conditions
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List getEntityFilterConditions(String entityName, Properties parameters);

	public Boolean getRecursiveFiltering();

	public void setRecursiveFiltering(Boolean recursiveFiltering);
}
