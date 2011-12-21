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
package it.eng.spagobi.security.init;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiExtUserRoles;
import it.eng.spagobi.profiling.bean.SbiExtUserRolesId;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.bean.SbiUserAttributesId;
import it.eng.spagobi.profiling.dao.ISbiAttributeDAO;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.security.Password;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class InternalSecurityInitializer implements InitializerIFace {

	static private Logger logger = Logger.getLogger(InternalSecurityInitializer.class);
	private SourceBean _config = null;
	
	public SourceBean getConfig() {
		return _config;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
		logger.debug("IN");
		try {
			_config= config;
			/* loads default users , attributes reading from xml*/
			List defaultAttributes = _config.getAttributeAsList("DEFAULT_ATTRIBUTES.ATTRIBUTE");
			ISbiAttributeDAO attrDAO= DAOFactory.getSbiAttributeDAO();
			
			//hashmap to use during
			HashMap< String, Integer> attributesLookup = new HashMap< String, Integer> ();
			for (int i=0; i<defaultAttributes.size();i++) {
			    SourceBean attribute = (SourceBean) defaultAttributes.get(i);
			    
			    SbiAttribute sbiAttribute = new SbiAttribute();
			    String attrName = (String) attribute.getAttribute("name");
			    
			    SbiAttribute existingAttribute = attrDAO.loadSbiAttributeByName(attrName);
			    if(existingAttribute == null){
				    sbiAttribute.setAttributeName(attrName);
				    String attrDescr = (String) attribute.getAttribute("description");
				    sbiAttribute.setDescription(attrDescr);			    
				    
				    try {	
				    	Integer id = attrDAO.saveSbiAttribute(sbiAttribute);
				    	attributesLookup.put(attrName, id);
					} catch (EMFUserError e) {
						logger.error(e.getMessage(), e);
					}	
			    }else{
			    	attributesLookup.put(attrName, existingAttribute.getAttributeId());
			    }
		    
			}
			
			List defaultRoles = _config.getAttributeAsList("DEFAULT_ROLES.ROLE");
			IRoleDAO roleDAO= DAOFactory.getRoleDAO();
			roleDAO.setUserID("server_init");
			IDomainDAO domainDAO = DAOFactory.getDomainDAO();
		    List<Domain> domains =domainDAO.loadListDomainsByType("ROLE_TYPE");
		    HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    for(int i=0; i< domains.size(); i++){
		    	domainIds.put(domains.get(i).getValueCd(), domains.get(i).getValueId());
		    }
		    
		    HashMap< String, Integer> rolesLookup = new HashMap< String, Integer> ();
			for (int i=0; i< defaultRoles.size();i++) {
			    SourceBean role = (SourceBean) defaultRoles.get(i);
			    Role sbiRole = new Role();
			    String roleName = (String) role.getAttribute("roleName");
			    sbiRole.setName(roleName);
			    String roleDescr = (String) role.getAttribute("description");
			    sbiRole.setDescription(roleDescr);
			    
			    String roleTypeCD = (String) role.getAttribute("roleTypeCD");
			    sbiRole.setRoleTypeCD(roleTypeCD);
			    
			    Integer valueId = domainIds.get(roleTypeCD);
			    if(valueId != null){
			    	sbiRole.setRoleTypeID(valueId);
			    }
			    try {
			    	Role roleToInsert = roleDAO.loadByName(roleName);
			    	Integer id = null;
			    	if(roleToInsert == null){
				    	roleDAO.insertRole(sbiRole);
			    	}		    	
			    	Role newRole = roleDAO.loadByName(roleName);
			    	id = newRole.getId();
			    	
			    	rolesLookup.put(roleName, id);
				} catch (EMFUserError e) {
					logger.error(e.getMessage(), e);
				}			    
			}
			//finally default users with associations
			List defaultsUsers = _config.getAttributeAsList("DEFAULT_USERS.USER");
			Iterator it = defaultsUsers.iterator();
			ISbiUserDAO userDAO= DAOFactory.getSbiUserDAO();

			while (it.hasNext()) {
			    SourceBean user = (SourceBean) it.next();
			    
			    SbiUser sbiUser = new SbiUser();
			    String userId = (String) user.getAttribute("userId");
			    sbiUser.setUserId(userId);
			    String password = (String) user.getAttribute("password");
				if (password!=null){
				    try {
				    	sbiUser.setPassword(Password.encriptPassword(password));
					} catch (Exception e) {
						logger.error("Impossible to encript Password", e);
					}
				}
			    String fullName = (String) user.getAttribute("fullName");
			    if(fullName != null){
			    	sbiUser.setFullName(fullName);
			    }

			    try {
			    	//checks if user already exists
			    	Integer existingId = userDAO.loadByUserId(userId);
			    	Integer idUser =existingId;
			    	if(existingId == null){
				    	//create user id
				    	idUser = userDAO.saveSbiUser(sbiUser);
			    	}

			    	
				    List<SourceBean> attributes = user.getAttributeAsList("ATTRIBUTE");
				    if(attributes != null){
					    for(int i= 0; i< attributes.size(); i++){
					    	SourceBean attribute = attributes.get(i);
					    	String name = (String)attribute.getAttribute("name");
					    	String value = (String)attribute.getAttribute("value");
					    	
					    	SbiUserAttributes sbiUserAttr = new SbiUserAttributes();
					    	sbiUserAttr.setAttributeValue(value);
					    	
					    	Integer attrID = attributesLookup.get(name);
					    	
					    	SbiUserAttributesId sbiUserAttrID = new SbiUserAttributesId();
					    	sbiUserAttrID.setId(idUser);//user ID
					    	sbiUserAttrID.setAttributeId(attrID.intValue());
					    	sbiUserAttr.setId(sbiUserAttrID);
					    	
					    	userDAO.updateSbiUserAttributes(sbiUserAttr);

					    }
				    }
				    List<SourceBean> userroles = user.getAttributeAsList("ROLE");
				    if(userroles != null){
				    	for(int i= 0; i< userroles.size(); i++){
					    	SourceBean role = userroles.get(i);
					    	String name = (String)role.getAttribute("name");
					    	SbiExtUserRoles sbiExtUserRole = new SbiExtUserRoles();
					    	SbiExtUserRolesId id = new SbiExtUserRolesId();
					    	
					    	Integer extRoleId = rolesLookup.get(name);

					    	int userIdInt= idUser.intValue();
					    	id.setExtRoleId(extRoleId);//role Id
					    	id.setId(userIdInt);//user ID
					    	
					    	sbiExtUserRole.setId(id);
					    	sbiExtUserRole.setSbiUser(sbiUser);
						
					    	userDAO.updateSbiUserRoles(sbiExtUserRole);


				    	}
				    }

				} catch (EMFUserError e) {
					logger.error(e.getMessage(), e);
				}			    
			}
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
		}
		logger.debug("OUT");

	}

}
