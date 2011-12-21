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
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;

public class SecurityProviderUtilities {

	/**
	 * Get all the predefined profile attributes of the user with the given unique identifier passed as String.
	 * The attributes are contained into a configuration file which contains the name 
	 * of the attribute and the value of the attribute.
	 * 
	 * @return HashMap of the attributes. HashMap keys are profile attribute.
	 * @throws EMFInternalError 
	 * 
	 */
	private static HashMap getPredefinedProfileAttributes(String userUniqueIdentifier) throws EMFInternalError {
		SourceBean profileAttrsSB = getProfileAttributesSourceBean();
		if (profileAttrsSB == null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, 
					"Profile attributes attribute not found in ConfigSingleton");
		}
		HashMap attrsMap = new HashMap();
		if (profileAttrsSB == null) return attrsMap;
		SourceBean userProfileAttrsSB = (SourceBean) 
			profileAttrsSB.getFilteredSourceBeanAttribute("USER-PROFILES.USER", "name", userUniqueIdentifier);
		if (userProfileAttrsSB == null)
			return new HashMap();
		List profileAttrs = userProfileAttrsSB.getAttributeAsList("ATTRIBUTE");
		if (profileAttrs == null || profileAttrs.size() == 0) {
			SpagoBITracer.info("SPAGOBI(ExoSecurityProvider)", 
				SecurityProviderUtilities.class.getName(), "getPredefinedProfileAttributes()", 
				"The user with unique identifer '" + userUniqueIdentifier + 
				"' has no predefined profile attributes.");
			return attrsMap;
		}
		Iterator iterAttrs = profileAttrs.iterator();
		SourceBean attrSB = null;
		String nameattr = null;
		String attrvalue = null;
		while(iterAttrs.hasNext()) {
			attrSB = (SourceBean) iterAttrs.next();
			if (attrSB == null)
				continue;
			nameattr = attrSB.getAttribute("name").toString();
		    attrvalue = attrSB.getAttribute("value").toString();
		    attrsMap.put(nameattr, attrvalue);
		}
		return attrsMap;
	}
	
	/**
	 * Get all the default profile attributes of the users.
	 * The default attributes are contained into a configuration file which contains the name 
	 * of the attribute and the default value of the attribute.
	 * 
	 * @param exoProfileAttrs HashMap containing the exo user profile attributes.
	 * @return HashMap of the attributes. HashMap keys are profile attribute.
	 * HashMap values are test values. 
	 * @throws EMFInternalError 
	 * 
	 */
	private static HashMap getAllDefaultProfileAttributes(HashMap exoProfileAttrs) throws EMFInternalError {
		SourceBean profileAttrsSB = getProfileAttributesSourceBean();
		if (profileAttrsSB == null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, 
					"Profile attributes attribute not found in ConfigSingleton");
		}
		HashMap toReturn = new HashMap();
		List attrs = profileAttrsSB.getAttributeAsList("ATTRIBUTE");
		if (attrs != null && attrs.size() > 0) {
			Iterator iterAttrs = attrs.iterator();
			SourceBean attrSB = null;
			String nameattr = null;
			String source = null;
			String defaultvalue = null;
			while(iterAttrs.hasNext()) {
				attrSB = (SourceBean) iterAttrs.next();
				if (attrSB == null)
					continue;
				source = (String) attrSB.getAttribute("source");
				nameattr = (String) attrSB.getAttribute("name");
				if (nameattr == null) {
					throw new EMFInternalError(EMFErrorSeverity.ERROR, 
							"Attribute 'name' missing in SourceBean\n" + attrSB.toXML(false));
				}
				defaultvalue = (String) attrSB.getAttribute("default");
				if ("absolute".equalsIgnoreCase(source)) {
					toReturn.put(nameattr, defaultvalue);
				} else if ("exo".equalsIgnoreCase(source)) {
					String exoname = (String) attrSB.getAttribute("exoname");
					if (exoname == null) {
						throw new EMFInternalError(EMFErrorSeverity.ERROR, 
								"Attribute 'exoname', required for attributes with source='exo', " +
								"missing in SourceBean\n" + attrSB.toXML(false));
					}
					String exovalue = (String) exoProfileAttrs.get(exoname);
					if (exovalue != null) toReturn.put(nameattr, exovalue);
					else toReturn.put(nameattr, defaultvalue);
				} else {
					throw new EMFInternalError(EMFErrorSeverity.ERROR, 
							"Source '" + source + "' not recognized in SourceBean\n" + attrSB.toXML(false));
				}
			}
		}
		return toReturn;
	}
	
	private static SourceBean getProfileAttributesSourceBean() {
		SourceBean profileAttributesSB = (SourceBean) 
			ConfigSingleton.getInstance().getAttribute("EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES");
		if (profileAttributesSB == null) {
			SpagoBITracer.critical("SPAGOBI(ExoSecurityProvider)", 
					SecurityProviderUtilities.class.getName(), "getProfileAttributesSourceBean()", 
					"There is not the needed EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES attribute " +
					"in the ConfigSingleton!!");
			return null;
		} else return profileAttributesSB;
		
	}
	
	public static void debug(Class classErr, String nameMeth, String message){
		SpagoBITracer.debug("SPAGOBI(ExoSecurityProvider)",
	            			classErr.getName(),
	            			nameMeth,
	            			message);
	}
	public static  Pattern getFilterPattern(){
		ConfigSingleton config = ConfigSingleton.getInstance();
		debug(SecurityProviderUtilities.class, "init", "Spago configuration retrived ");
		SourceBean secFilterSB = (SourceBean)config.getAttribute("SPAGOBI.SECURITY.ROLE-NAME-PATTERN-FILTER");
		debug(SecurityProviderUtilities.class, "init", "source bean filter retrived " + secFilterSB);
        String rolePatternFilter = secFilterSB.getCharacters();
        debug(SecurityProviderUtilities.class, "init", "filter string retrived " + rolePatternFilter);
        Pattern pattern = Pattern.compile(rolePatternFilter);
        debug(SecurityProviderUtilities.class, "init", "regular expression pattern compiled " + pattern);
        return pattern;
	}
	
	
	/**
	 * Get all the profile attributes of the users given his unique identifier
	 * The profile attributes are default and predefined attributes (predefined override default attributes) 
	 * and are contained into a configuration file which contains the name 
	 * of the attribute and its default value or its reference to a eXo user profile attribute.
	 * 
	 * @param userUniqueIdentifier String representing the unique identifier.
	 * @return service The eXo OrganizationService object. 
	 * 
	 */
	public static HashMap getUserProfileAttributes (String userUniqueIdentifier, OrganizationService service) {
		
		SpagoBITracer.info(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
				"getUserProfileAttributes",
				" Trying to load user attributes for user with unique identifer '" + userUniqueIdentifier +"'.");
		// load the exo user profile attributes into a tenmporary hashmap
		HashMap exoProfileAttrs = new HashMap();
		HashMap userAttributes = new HashMap();
		UserProfileHandler userProfileHandler = service.getUserProfileHandler();
		if (userProfileHandler==null) {
			SpagoBITracer.warning(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
					              "getUserProfileAttributes", " UserProfileHandler null");
		} else {
			UserProfile exoUserProfile = null;
			try {
				exoUserProfile = userProfileHandler.findUserProfileByName(userUniqueIdentifier);
			} catch (Exception e) {
				SpagoBITracer.critical(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                        "getUserProfileAttributes", " Error while recovering user profile by name '" 
                        + userUniqueIdentifier + "'", e);
				return userAttributes;
			}
			if (exoUserProfile == null){
				SpagoBITracer.critical(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
			                          "getUserProfileAttributes", " exoUserProfile not found for user " 
			                          + userUniqueIdentifier);
				return userAttributes;
			} else {
				Map userInfoMap = exoUserProfile.getUserInfoMap();
				Set infoKeys = userInfoMap.keySet();
				Iterator infoKeyIter = infoKeys.iterator();
				while(infoKeyIter.hasNext()) {
					String labelcode = infoKeyIter.next().toString();
					String value = userInfoMap.get(labelcode) != null ?
							userInfoMap.get(labelcode).toString() : "";
					//String label = PortletUtilities.getMessage(labelcode, "exo_userprofile_labels");
					//exoProfileAttrs.put(label, value);
					exoProfileAttrs.put(labelcode, value);
					SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                                        "getUserProfileAttributes", "Found exo user profile attribute " 
                                        + labelcode + " : " + value);
				}
			}
		}

		HashMap allProfileAttributes = null;
		try {
			allProfileAttributes = getAllDefaultProfileAttributes(exoProfileAttrs);
		} catch (EMFInternalError e) {
			SpagoBITracer.critical(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                    "getUserProfileAttributes", "Error recovering default profile attributes", e);
			return userAttributes;
		}
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                "getUserProfileAttributes", "All users predefined attributes recovered : " + allProfileAttributes);
		HashMap predefinedProfileAttributes = null;
		try {
			predefinedProfileAttributes = 
				SecurityProviderUtilities.getPredefinedProfileAttributes(userUniqueIdentifier);
		} catch (EMFInternalError e) {
			SpagoBITracer.critical(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                    "getUserProfileAttributes", "Error recovering predefined profile attributes for user " 
                    + userUniqueIdentifier, e);
			return userAttributes;
		}
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, SecurityProviderUtilities.class.getName(), 
                            "getUserProfileAttributes", "Current user predefined attributes recovered : " 
                            + predefinedProfileAttributes);
		
		userAttributes.putAll(allProfileAttributes);
		// predefinedProfileAttributes override allProfileAttributes
		userAttributes.putAll(predefinedProfileAttributes);
		
		return userAttributes;
	}
	
	public static List getAllProfileAtributesNames () throws EMFInternalError {
		SourceBean profileAttrsSB = getProfileAttributesSourceBean();
		if (profileAttrsSB == null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, 
					"Profile attributes attribute not found in ConfigSingleton");
		}
		List toReturn = new ArrayList();
		List attrs = profileAttrsSB.getAttributeAsList("ATTRIBUTE");
		if (attrs != null && attrs.size() > 0) {
			Iterator iterAttrs = attrs.iterator();
			SourceBean attrSB = null;
			String nameattr = null;
			while(iterAttrs.hasNext()) {
				attrSB = (SourceBean) iterAttrs.next();
				if (attrSB == null)
					continue;
				nameattr = (String) attrSB.getAttribute("name");
				if (nameattr == null) {
					throw new EMFInternalError(EMFErrorSeverity.ERROR, 
							"Attribute 'name' missing in SourceBean\n" + attrSB.toXML(false));
				}
				toReturn.add(nameattr);
			}
		}
		return toReturn;
	}
}
