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
package it.eng.spagobi.analiticalmodel.document.dao;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.dao.ISpagoBIDao;

import java.util.List;


public interface IObjTemplateDAO extends ISpagoBIDao{

	/**
	 * Gets the bI object active template.
	 * 
	 * @param biobjId the biobj id
	 * 
	 * @return the bI object active template
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public ObjTemplate getBIObjectActiveTemplate(Integer biobjId) throws EMFInternalError; 
	
	
	/**
	 * Gets the bI object active template starting by document label
	 * 
	 * @param biobjLabel the BiObject label
	 * 
	 * @return the bI object active template
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public ObjTemplate getBIObjectActiveTemplateByLabel(String label) throws EMFInternalError; 

	
	/**
	 * Gets the bI object template list.
	 * 
	 * @param biobjId the biobj id
	 * 
	 * @return the bI object template list
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public List getBIObjectTemplateList(Integer biobjId) throws EMFInternalError; 
	
	/**
	 * Load bi object template.
	 * 
	 * @param tempId the temp id
	 * 
	 * @return the obj template
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public ObjTemplate loadBIObjectTemplate(Integer tempId) throws EMFInternalError;
	
	/**
	 * Gets the next prog for template.
	 * 
	 * @param biobjId the biobj id
	 * 
	 * @return the next prog for template
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public Integer getNextProgForTemplate(Integer biobjId) throws EMFInternalError;
	
	/**
	 * Delete bi object template.
	 * 
	 * @param tempId the temp id
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void deleteBIObjectTemplate(Integer tempId) throws EMFInternalError;
	
	/**
	 * Insert a new bi object template.
	 * 
	 * @param objTemplate the new template
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void insertBIObjectTemplate(ObjTemplate objTemplate) throws EMFUserError, EMFInternalError;
	
}
