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
package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.model.metadata.SbiResources;

import java.util.List;

public interface IResourceDAO extends ISpagoBIDao{

	/**
	 * Inserts a new Resource 
	 * 
	 * @param Resource to insert 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Integer insertResource(Resource value) throws EMFUserError;

	/**
	 * Load resource by Id
	 * 
	 * @param Resource to insert 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Resource loadResourceById(Integer resource) throws EMFUserError;

	
	/**
	 * Returns the Resource of the referred id
	 * 
	 * @param id of the Resource
	 * @return Resource with the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Resource loadResourcesByNameAndModelInst(String resourceName) throws EMFUserError ;
	
	/**
	 * Returns the Resource of the referred code
	 * 
	 * @param code of the Resource
	 * @return Resource with the referred code
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Resource loadResourceByCode(String resourceCode) throws EMFUserError ;
	
	public SbiResources toSbiResource(Resource r) throws EMFUserError; 
	
	public void modifyResource(Resource resource) throws EMFUserError;

	public List loadResourcesList(String fieldOrder, String typeOrder)throws EMFUserError;
	
	public List loadPagedResourcesList(Integer offset, Integer fetchSize)throws EMFUserError;
	
	public Integer countResources()throws EMFUserError;
	
	public void deleteResource(Integer resourceId) throws EMFUserError;

	public Resource toResource(SbiResources r);
}
