/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analiticalmodel.document.service;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;

import org.apache.log4j.Logger;


public class EraseDocumentModule extends AbstractModule {
	private static transient Logger logger = Logger.getLogger(EraseDocumentModule.class);

	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.module.AbstractModule#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		SessionContainer permSession = this.getRequestContainer().getSessionContainer().getPermanentContainer();
		IEngUserProfile profile = (IEngUserProfile) permSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		if (profile == null) {
			logger.error("Profile not found");
			throw new Exception("User profile not found");
		}

		String userId = ((UserProfile)profile).getUserId().toString();

			boolean onlyOneFunct=false;
			String objIdStr = (String)request.getAttribute(ObjectsTreeConstants.OBJECT_ID);
			logger.debug("object id "+objIdStr);
			String funcIdStr = (String)request.getAttribute(ObjectsTreeConstants.FUNCT_ID);
			logger.debug("funct id "+funcIdStr);
			if(funcIdStr==null || funcIdStr.equalsIgnoreCase("")){
				logger.error("Functionality not specified"); 
				throw new Exception("Functionality not specified");
			}

			//onlyOneFunct=true;
			Integer objId = new Integer(objIdStr);
			IBIObjectDAO biobjdao = DAOFactory.getBIObjectDAO();
			BIObject obj = biobjdao.loadBIObjectById(objId);
			Integer fId=Integer.decode(funcIdStr);

			// check that the functionality specified is the user one or that user is administrator

			// first case: user is administrator
			if(profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)){
				biobjdao.eraseBIObject(obj, fId);
				logger.debug("Object deleted by administrator");	
			}
			else
			{ 
				ILowFunctionalityDAO functDAO = DAOFactory.getLowFunctionalityDAO();
				LowFunctionality lowFunc = functDAO.loadLowFunctionalityByID(fId, false);

				if(lowFunc==null){
					logger.error("Functionality does not exist");
					throw new Exception("Functionality does not exist");					
				}

				if(lowFunc.getPath().equals("/"+userId)){ // folder is current user one
					biobjdao.eraseBIObject(obj, fId);
					logger.debug("Object deleted");
				}
				else{
					logger.error("Functionality is not user's one");
					throw new Exception("Functionality  is not user's one");					
				}

			}
			logger.debug("OUT");
		}

	}

