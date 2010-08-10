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
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;

import java.sql.Connection;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.chiron.ListEnginesAction;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class UserDocumentsBrowserStartAction extends AbstractBaseHttpAction{
	
	public static final String LABEL_SUBTREE_NODE = "LABEL_SUBTREE_NODE";
	
	// logger component
	private static Logger logger = Logger.getLogger(ListEnginesAction.class);
	
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		logger.debug("IN");

		//Start writing log in the DB
		Session aSession =null;
		try {
			aSession = HibernateUtil.currentSession();
			Connection jdbcConnection = aSession.connection();
			IEngUserProfile profile = UserUtilities.getUserProfile();
			AuditLogUtilities.updateAudit(jdbcConnection,  profile, "activity.DocumentsBrowserMenu", null);
		} catch (HibernateException he) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		//End writing log in the DB
		
		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			DocumentsBrowserConfig config = DocumentsBrowserConfig.getInstance();
			JSONObject jsonObj  = config.toJSON();
			String labelSubTreeNode = this.getAttributeAsString( LABEL_SUBTREE_NODE );
			
			if (labelSubTreeNode != null && !labelSubTreeNode.trim().equals("")) {
				LowFunctionality luwFunc = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByPath(labelSubTreeNode, false);
				if(luwFunc != null) {
					jsonObj.put("rootFolderId", luwFunc.getId());
				}
				
			}
			response.setAttribute("metaConfiguration", jsonObj);			
		} catch (Throwable t) {
			throw new SpagoBIException("An unexpected error occured while executing UserDocumentsBrowserStartAction", t);
		} finally {
			logger.debug("OUT");
		}
	}
}
