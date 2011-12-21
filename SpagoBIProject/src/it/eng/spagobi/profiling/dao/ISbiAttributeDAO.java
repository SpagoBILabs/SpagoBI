/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.profiling.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;

import java.util.HashMap;
import java.util.List;

public interface ISbiAttributeDAO extends ISpagoBIDao{
	
	public List<SbiUserAttributes> loadSbiAttributesById(Integer id) throws EMFUserError;
	
	public HashMap<Integer, String> loadSbiAttributesByIds(List<String> ids) throws EMFUserError;
	
	public SbiAttribute loadSbiAttributeByName(String name) throws EMFUserError;
	
	public List<SbiAttribute> loadSbiAttributes() throws EMFUserError;
	
	public Integer saveSbiAttribute(SbiAttribute attribute) throws EMFUserError;
	
	public Integer saveOrUpdateSbiAttribute(SbiAttribute attribute) throws EMFUserError;

	public SbiUserAttributes loadSbiAttributesByUserAndId(Integer userId, Integer id)	throws EMFUserError;
	
	public void deleteSbiAttributeById(Integer id) throws EMFUserError;

}
