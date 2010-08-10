/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.dao;

import it.eng.qbe.bo.DatamartProperties;

// TODO: Auto-generated Javadoc
/**
 * The Interface DatamartPropertiesDAO.
 * 
 * @author Andrea Gioia
 */
public interface IDatamartPropertiesDAO {
	
	/**
	 * Load datamart properties.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the datamart properties
	 */
	DatamartProperties loadDatamartProperties(String datamartName);
	
	/**
	 * Save datamart properties.
	 * 
	 * @param datamartName the datamart name
	 * @param datamartProperties the datamart properties
	 */
	void saveDatamartProperties(String datamartName, DatamartProperties datamartProperties);
}
