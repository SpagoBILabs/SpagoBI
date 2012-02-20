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
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class InternalSecurityInitializer implements InitializerIFace {

	private SourceBean _config = null;
	
	static private Logger logger = Logger.getLogger(InternalSecurityInitializer.class);
	
	public SourceBean getConfig() {
		return _config;
	}
	
	public void init(SourceBean config) {
		
		logger.debug("IN");
		
		try {
			if(config == null) {
				logger.warn("Security initialization aborted because the input parameter [config] is null");
				return;
			}
			
			_config = config;
			
			Map<String,Integer> attributesLookupMap = initProfileAttributes(config);
			Map<String,Integer> rolesLookupMap = initRoles(config);
			Map<String,Integer> usersLookupMap = initUsers(config, false);
//			initUserAttributes(config);
//			initUserRoles(config);
			
		
			ISbiUserDAO userDAO= DAOFactory.getSbiUserDAO();
			
			//finally default users associations
			List<SourceBean> defaultsUsers = _config.getAttributeAsList("DEFAULT_USERS.USER");
			for (SourceBean defaultUser : defaultsUsers) {
			  
			  
			    try {
			    	
			    	String userId = (String) defaultUser.getAttribute("userId");
			    	
				    List<SourceBean> attributes = defaultUser.getAttributeAsList("ATTRIBUTE");
				    if(attributes != null){
					    for(int i= 0; i< attributes.size(); i++){
					    	SourceBean attribute = attributes.get(i);
					    	String name = (String)attribute.getAttribute("name");
					    	String value = (String)attribute.getAttribute("value");
					    	logger.debug("Setting attribute [" + name +"] of user [" + userId + "] to value [" + value + "]");
					    	if(usersLookupMap.get(userId) == null) {
					    		logger.debug("User [" + userId + "] was already stored in the database. The value of attribute [" + name +"] will not be overwritten");
					    		continue;
					    	}
					    	
					    	
					    	SbiUserAttributes sbiUserAttr = new SbiUserAttributes();
					    	sbiUserAttr.setAttributeValue(value);
					    	
					    	Integer attrID = attributesLookupMap.get(name);
					    	
					    	SbiUserAttributesId sbiUserAttrID = new SbiUserAttributesId();
					    	sbiUserAttrID.setId( usersLookupMap.get(userId) );//user ID
					    	sbiUserAttrID.setAttributeId(attrID.intValue());
					    	sbiUserAttr.setId(sbiUserAttrID);
					    	
					    	userDAO.updateSbiUserAttributes(sbiUserAttr);
					    	
					    	logger.debug("Attribute [" + name +"] of user [" + userId + "] succesfully set to value [" + value + "]");
					    }
				    }
				   
				    List<SourceBean> userroles = defaultUser.getAttributeAsList("ROLE");
				    if(userroles != null){
				    	for(int i= 0; i< userroles.size(); i++){
					    	SourceBean role = userroles.get(i);
					    	String name = (String)role.getAttribute("name");
					    	logger.debug("Creating association beetween user [" + userId +"] and role [" + name + "]");
					    	if(usersLookupMap.get(userId) == null) {
					    		logger.debug("User [" + userId + "] was already stored in the database. The associatino with role [" + name +"] will not be created");
					    		continue;
					    	}
					    	
					    	SbiExtUserRoles sbiExtUserRole = new SbiExtUserRoles();
					    	SbiExtUserRolesId id = new SbiExtUserRolesId();
					    	
					    	Integer extRoleId = rolesLookupMap.get(name);

					    	int userIdInt= usersLookupMap.get(userId).intValue();
					    	id.setExtRoleId(extRoleId);//role Id
					    	id.setId(userIdInt);//user ID
					    
					    	sbiExtUserRole.setId(id);
					    	
					    	userDAO.updateSbiUserRoles(sbiExtUserRole);

					    	logger.debug("Association beetween user [" + userId +"] and role [" + name + "] succesfully created");
				    	}
				    }

				} catch (Throwable t) {
					logger.error("An unexpected error occurred while executing internal security initializer", t);
				}			    
			}
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
		}
		logger.debug("OUT");

	}
	
	/**
	 * @return The map of role ids (Integer) indexed by role name (String)
	 */
	public HashMap< String, Integer> initUsers(SourceBean config, boolean includeAleredyExistingUser) {
		HashMap< String, Integer> usersLookup;
		
		logger.debug("IN");
		
		usersLookup = new HashMap< String, Integer>();
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			
			ISbiUserDAO userDAO = DAOFactory.getSbiUserDAO();
			
			List<SbiUser> defaultUsers = readUsers(config);
			for(SbiUser defaultUser: defaultUsers) {
				Integer existingId = userDAO.loadByUserId( defaultUser.getUserId() );
		    	if(existingId == null){
		    		String userId = defaultUser.getUserId(); // save this because the dao during save set it to id
		    		logger.debug("Storing user [" + defaultUser.getUserId() + "] into database ");
		    		existingId = userDAO.saveSbiUser(defaultUser);
		    		usersLookup.put(defaultUser.getUserId(), existingId);
		    		logger.debug("User [" + defaultUser.getUserId() + "] sucesfully stored into database with id [" + existingId + "]");
			    } else  {
			    	if(includeAleredyExistingUser) {
			    		usersLookup.put(defaultUser.getUserId(), existingId);
			    	}
			    	logger.debug("User [" + defaultUser.getUserId() + "] is alerdy stored into database with id [" + existingId + "]");
			    }	
		    	
		    	
			}
			

		} catch(Throwable t) {
			logger.error("An unexpected error occurred while initializieng default users", t);
		} finally {
			logger.debug("OUT");
		}
		
		return usersLookup;
	}
	
	public List<SbiUser> readUsers(SourceBean config) {
		List<SbiUser> defaultUsers;
		
		logger.debug("IN");
			
		defaultUsers = new ArrayList<SbiUser>();
		
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			
			List<SourceBean> defaultsUsersSB = _config.getAttributeAsList("DEFAULT_USERS.USER");
			logger.debug("Succesfully read from configuration [" + defaultsUsersSB.size() + "] defualt user(s)");
			
			
			for (SourceBean defaultUserSB : defaultsUsersSB) {
			  
			    SbiUser defaultUser = new SbiUser();
			   
			    String userId = (String) defaultUserSB.getAttribute("userId");
			    defaultUser.setUserId(userId);
			   
			    
			    String password = (String) defaultUserSB.getAttribute("password");
				if (password != null){
				    try {
				    	String pwd = Password.encriptPassword(password);
				    	defaultUser.setPassword(pwd);
					} catch (Exception e) {
						logger.error("Impossible to encript Password", e);
					}
				}
				
			    String fullName = (String) defaultUserSB.getAttribute("fullName");
			    if(fullName != null){
			    	defaultUser.setFullName(fullName);
			    }
			    
			    defaultUsers.add(defaultUser);
			    
			    logger.debug("Succesfully parsed from configuration user [" + userId  + ";" + fullName + "]");
			}
			
			
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while reading defualt users", t);
		} finally {
			logger.debug("OUT");
		}
		
		return defaultUsers;
	}
	
	/**
	 * @return The map of role ids (Integer) indexed by role name (String)
	 */
	public HashMap< String, Integer> initRoles(SourceBean config) {
		HashMap< String, Integer> rolesLookup;
		IRoleDAO roleDAO;
		List<Role> defualtRoles;
		
		logger.debug("IN");
		
		rolesLookup = new HashMap< String, Integer> ();
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			
			roleDAO= DAOFactory.getRoleDAO();
			roleDAO.setUserID("server_init");
			
			defualtRoles = readDefaultRoles(config);
		  
			for (Role defualtRole : defualtRoles) {
				
				Role existingRole = roleDAO.loadByName(defualtRole.getName());
			    if(existingRole == null){
			    	logger.debug("Storing role [" + defualtRole.getName() + "] into database ");
				   	roleDAO.insertRole(defualtRole);
				   	existingRole = roleDAO.loadByName(defualtRole.getName());
				   	logger.debug("Role [" + defualtRole.getName() + "] sucesfully stored into database with id [" + existingRole.getId() + "]");
			    } else  {
			    	logger.debug("Role [" + defualtRole.getName() + "] is alerdy stored into database with id [" + existingRole.getId() + "]");
			    }		    	
			
			    rolesLookup.put(existingRole.getName(), existingRole.getId());			    
			}
		
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while initializieng default roles", t);
		} finally {
			logger.debug("OUT");
		}
		
		return rolesLookup;
	}
	
	public List<Role> readDefaultRoles(SourceBean config) {
		List<Role> defaultRoles;
		List<SourceBean> defaultRolesSB;
		
		logger.debug("IN");
		
		defaultRoles = new ArrayList<Role>();
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			defaultRolesSB = config.getAttributeAsList("DEFAULT_ROLES.ROLE");
		
			logger.debug("Succesfully read from configuration [" + defaultRolesSB.size() + "] defualt role(s)");
			
			IDomainDAO domainDAO = DAOFactory.getDomainDAO();
		    List<Domain> domains =domainDAO.loadListDomainsByType("ROLE_TYPE");
		    HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    for(int i=0; i< domains.size(); i++){
		    	domainIds.put(domains.get(i).getValueCd(), domains.get(i).getValueId());
		    }
		    
			for (SourceBean defaultProfileAttributeSB : defaultRolesSB) {
				Role sbiRole = new Role();
			    
				String roleName = (String) defaultProfileAttributeSB.getAttribute("roleName");
				sbiRole.setName(roleName);
				
				String roleDescr = (String) defaultProfileAttributeSB.getAttribute("description");
				sbiRole.setDescription(roleDescr);
				    
				String roleTypeCD = (String) defaultProfileAttributeSB.getAttribute("roleTypeCD");
				sbiRole.setRoleTypeCD(roleTypeCD);
				    
				Integer valueId = domainIds.get(roleTypeCD);
				if(valueId != null){
					sbiRole.setRoleTypeID(valueId);
				}
				
				defaultRoles.add(sbiRole);
				
			    logger.debug("Succesfully parsed from configuration profile attribute [" + roleName  + ";" + roleDescr + ";" + roleTypeCD + "]");
			}
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while reading defualt profile attibutes", t);
		} finally {
			logger.debug("OUT");
		}
		
		return defaultRoles;
	}
	
	/**
	 * @return The map of attribute ids (Integer) indexed by attribute name (String)
	 */
	private HashMap<String, Integer> initProfileAttributes(SourceBean config) {
		
		HashMap<String, Integer> attributesLookup;
		ISbiAttributeDAO profileAttributeDAO;
		
		logger.debug("IN");
		
		attributesLookup = new HashMap< String, Integer> ();
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			
			profileAttributeDAO = DAOFactory.getSbiAttributeDAO();
			
			List<SbiAttribute> defaultProfileAttributes = readDefaultProfileAttributes(config);
			
			for (SbiAttribute defaultProfileAttribute : defaultProfileAttributes) {
			    SbiAttribute existingAttribute = profileAttributeDAO.loadSbiAttributeByName( defaultProfileAttribute.getAttributeName() );
			    if(existingAttribute == null) {
			    	logger.debug("Storing attribute [" + defaultProfileAttribute.getAttributeName() + "] into database ");
				    try {	
				    	Integer id = profileAttributeDAO.saveSbiAttribute( defaultProfileAttribute );
				    	attributesLookup.put(defaultProfileAttribute.getAttributeName(), id);
				    	logger.debug("Attribute [" + defaultProfileAttribute.getAttributeName() + "] sucesfully stored into database with id equals to [" + id + "]");
					} catch (EMFUserError e) {
						logger.error(e.getMessage(), e);
					}	
			    } else {
			    	attributesLookup.put(defaultProfileAttribute.getAttributeName(), existingAttribute.getAttributeId());
			    	logger.debug("Attribute [" + defaultProfileAttribute.getAttributeName() + "]  is already stored into the database with id equals to [" + existingAttribute.getAttributeId() + "]");
			    }
			}
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while initializing profile attibutes", t);
		} finally {
			logger.debug("OUT");
		}
		
		return attributesLookup;
	}
	
	public List<SbiAttribute> readDefaultProfileAttributes(SourceBean config) {
		List<SbiAttribute> defaultProfileAttributes;
		List<SourceBean> defaultProfileAttributesSB;
		
		logger.debug("IN");
		
		defaultProfileAttributes = new ArrayList<SbiAttribute>();
		try {
			Assert.assertNotNull(config, "Input parameter [config] cannot be null");
			defaultProfileAttributesSB = config.getAttributeAsList("DEFAULT_ATTRIBUTES.ATTRIBUTE");
			
			logger.debug("Succesfully read from configuration [" + defaultProfileAttributesSB.size() + "] defualt profile attribute(s)");
			
			for (SourceBean defaultProfileAttributeSB : defaultProfileAttributesSB) {
			    SbiAttribute sbiAttribute = new SbiAttribute();
			    String attributeName = (String)defaultProfileAttributeSB.getAttribute("name");
			    String attributeDescription = (String) defaultProfileAttributeSB.getAttribute("description");
			    sbiAttribute.setAttributeName(attributeName);			    
			    sbiAttribute.setDescription(attributeDescription);	
			    defaultProfileAttributes.add(sbiAttribute);
			    
			    logger.debug("Succesfully parsed from configuration profile attribute [" + attributeName  + ";" + attributeDescription + "]");
			}
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while reading defualt profile attibutes", t);
		} finally {
			logger.debug("OUT");
		}
		
		return defaultProfileAttributes;
	}

}
