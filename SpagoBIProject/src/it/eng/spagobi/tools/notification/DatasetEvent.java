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
package it.eng.spagobi.tools.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class DatasetEvent extends AbstractEvent {
	
	
	
	Set<String> emailAdressesOfMapAuthors;
	
	public DatasetEvent(String eventName, String eventDescription, IDataSet dataset){
		this.eventName = eventName;
		this.eventDescritpion = eventDescription;
		this.argument = dataset;
	}
	
	/*
	 * Get all email addresses of authors of Map based on the dataset 
	 */
	public Set<String> retrieveEmailAddressesOfMapAuthors() throws Exception{
		if (emailAdressesOfMapAuthors == null){
			//Set of email addresses
			Set<String> emailsAddressOfAuthors = new HashSet<String>();

			if (argument instanceof IDataSet){
				IDataSet dataset = (IDataSet) argument;
				
				//We have to get all the maps documents based on this dataset
				int datasetId = dataset.getId();
				
				IBIObjectDAO biObjectDAO = DAOFactory.getBIObjectDAO();

				//get all the maps documents
				List mapsDocuments = biObjectDAO.loadBIObjects("MAP", null, null);
				//Set of email addresses to notify
				
				Iterator iterator = mapsDocuments.iterator();
				while (iterator.hasNext()) {
					Object document = iterator.next();
					if (document instanceof BIObject){
						BIObject sbiDocument = (BIObject)document;
						//check if the document is using this dataset
						if (sbiDocument.getDataSetId() == datasetId) {
							String documentCreationUser = sbiDocument.getCreationUser();
							
							ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();
							SpagoBIUserProfile userProfile = supplier.createUserProfile(documentCreationUser);
							HashMap userAttributes = userProfile.getAttributes();
							
							if (userAttributes.get("email") != null){
								String emailAddressDocumentCreationUser =(String) userAttributes.get("email");
								if (!emailAddressDocumentCreationUser.isEmpty()){
									emailsAddressOfAuthors.add(emailAddressDocumentCreationUser);
								}
							}


						}
					}
				}
				

				
			}
			this.setEmailAdressesOfMapAuthors(emailsAddressOfAuthors);
			return emailsAddressOfAuthors;			
		} else {
			return this.getEmailAdressesOfMapAuthors();
		}
		
		
		
		
	}

	/**
	 * @return the emailAdressesOfMapAuthors
	 */
	public Set<String> getEmailAdressesOfMapAuthors() {
		return emailAdressesOfMapAuthors;
	}

	/**
	 * @param emailAdressesOfMapAuthors the emailAdressesOfMapAuthors to set
	 */
	public void setEmailAdressesOfMapAuthors(Set<String> emailAdressesOfMapAuthors) {
		this.emailAdressesOfMapAuthors = emailAdressesOfMapAuthors;
	}

}
