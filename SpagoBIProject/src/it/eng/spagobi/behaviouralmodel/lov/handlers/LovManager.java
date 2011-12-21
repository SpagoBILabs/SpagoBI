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
package it.eng.spagobi.behaviouralmodel.lov.handlers;

import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LovManager {
    
	/**
	 * Returns all the names of the columns returned by the lov.
	 * 
	 * @param lov the lov to analize
	 * 
	 * @return List of the columns name (the element of the list are Strings)
	 */
	public List getAllColumnsNames(ModalitiesValue lov) {
		List names = new ArrayList();
		try{
			String lovProvider = lov.getLovProvider();
			ILovDetail lovProvDet = LovDetailFactory.getLovFromXML(lovProvider);
			List viscols = lovProvDet.getVisibleColumnNames();
			List inviscols = lovProvDet.getInvisibleColumnNames();
			names.addAll(viscols);
			names.addAll(inviscols);
		} catch (Exception e) {
			names = new ArrayList();
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), 
					            "getAllColumnsNames", "Error while recovering column names " + e);
		}
		return names;
	}
	
	/**
	 * Gets the labels of documents which use lov.
	 * 
	 * @param lov the lov
	 * 
	 * @return the labels of documents which use lov
	 */
	public static List getLabelsOfDocumentsWhichUseLov(ModalitiesValue lov) {
		List docLabels = new ArrayList();
		try{
			IParameterUseDAO parusedao = DAOFactory.getParameterUseDAO();
			IBIObjectParameterDAO biobjpardao = DAOFactory.getBIObjectParameterDAO();
			List paruses = parusedao.getParameterUsesAssociatedToLov(lov.getId());
			Iterator parusesIt = paruses.iterator();
			while (parusesIt.hasNext()) {
				ParameterUse aParuse = (ParameterUse) parusesIt.next();
				List temp = biobjpardao.getDocumentLabelsListUsingParameter(aParuse.getId());
				docLabels.addAll(temp);
			}
		} catch (Exception e) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, LovManager.class.getName(), 
					            "getLabelsOfDocumentsWhichUseLov", "Error while recovering document labels", e);
			
		}
		return docLabels;
	}
	
	
}
