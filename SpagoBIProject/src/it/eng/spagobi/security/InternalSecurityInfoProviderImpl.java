/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.profiling.bean.SbiAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class InternalSecurityInfoProviderImpl implements ISecurityInfoProvider{

	static private Logger logger = Logger.getLogger(InternalSecurityInfoProviderImpl.class);
	
	public List getAllProfileAttributesNames() {
    	logger.debug("IN");
		List attributes = new ArrayList();
		//gets attributes from database
		try {
			List<SbiAttribute> sbiAttributes = DAOFactory.getSbiAttributeDAO().loadSbiAttributes();
			Iterator it = sbiAttributes.iterator();
			while(it.hasNext()) {
				SbiAttribute attribute = (SbiAttribute)it.next();

				attributes.add(attribute.getAttributeName());
			}
		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("OUT");
		return attributes;
	}

	public List getRoles() {
    	logger.debug("IN");
    	//get roles from database
		List roles = new ArrayList();

		//gets roles from database
		try {
			roles = DAOFactory.getRoleDAO().loadAllRoles();

		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("OUT");
		return roles;
	}

}
