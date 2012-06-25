/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.utilities;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.CustomJDBCAppender;
import it.eng.spagobi.commons.bo.UserProfile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * @author Chiara Chiarelli (chiara.chiarelli@eng.it)
 *
 */
public class AuditLogUtilities {

	private static transient Logger logger = Logger.getLogger(AuditLogUtilities.class);
	private static Logger audit_logger = Logger.getLogger("audit");


	/**
	 * Substitutes the profile attributes with sintax "${attribute_name}" with
	 * the correspondent value in the string passed at input.
	 * 
	 * @param statement The string to be modified (tipically a query)
	 * @param profile The IEngUserProfile object
	 * 
	 * @return The statement with profile attributes replaced by their values.
	 * 
	 * @throws Exception the exception
	 */
	public static void updateAudit(Connection jdbcConnection,IEngUserProfile profile, String  action_code, String info)
	throws Exception {
		logger.debug("IN");
		CustomJDBCAppender ja = null;
		boolean updateDB= false;
		SingletonConfig serverConfig = SingletonConfig.getInstance();
		String dbTimestampFormat = serverConfig.getConfigValue("SPAGOBI.DB-TIMESTAMP-FORMAT.format");
		String updateDBConf = serverConfig.getConfigValue("SPAGOBI.DB_LOG.value");
		if(updateDBConf!=null && updateDBConf.equalsIgnoreCase("true")){
			updateDB=true;
		}

		String userName = "";
		String userRoles = "";
		if(profile!=null){
			userName = ((UserProfile)profile).getUserId().toString();

			Collection roles = ((UserProfile)profile).getRolesForUse();

			userRoles = createRolesString(roles);
		}

		if(jdbcConnection!=null){
			ja = new CustomJDBCAppender(jdbcConnection);			
			if(updateDB && action_code!=null){
				String sqlInsert = "INSERT INTO SBI_ACTIVITY_MONITORING (ACTION_TIME, USERNAME, USERGROUP, LOG_LEVEL, ACTION_CODE, INFO)";
				sqlInsert += "VALUES('%d{"+dbTimestampFormat+"}','"+userName+"','"+userRoles+"','%5p','"+action_code+"','"+(info!=null?info:"")+"')";
				logger.debug("SQL INSERT:"+sqlInsert);
				ja.setSql(sqlInsert);
				audit_logger.addAppender(ja);
			}
		}
		// These messages with Priority >= setted priority will be logged to the database.
		audit_logger.info("activity_info: USERNAME="+userName+"; USERGROUP="+userRoles+" ACTION_CODE="+action_code+"");

		// not required
		if(updateDB && ja!=null){
			audit_logger.removeAppender(ja);
		}
		logger.debug("OUT");
	}	

	private static String createRolesString(Collection roles){
		logger.debug("IN");
		String rolesStr = "";
		if(roles!=null){
			Object[] temp = roles.toArray();
			int length = temp.length;
			for(int i=0;i<length;i++){
				String role =(String)temp[i];
				rolesStr +=role+";";
			}	
		}
		logger.debug("OUT");
		return rolesStr;
	}

}
