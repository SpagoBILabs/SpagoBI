/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.utilities;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.CustomJDBCAppender;
import it.eng.spagobi.commons.bo.UserProfile;

import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * @author Chiara Chiarelli (chiara.chiarelli@eng.it) , Monia Spinelli (monia.spinelli@eng.it)
 *
 */
public class AuditLogUtilities {

	private static transient Logger logger = Logger.getLogger(AuditLogUtilities.class);
	private static Logger audit_logger = Logger.getLogger("audit");
	public static String CLIENT_IP1_KEY = "IP_CLIENT";
    public static String CLIENT_IP2_KEY = "ip_client";


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
	public static void updateAudit(HttpServletRequest request,IEngUserProfile profile, String  action_code, HashMap<String, String> parameters, String esito)
	throws Exception {
		logger.debug("IN");
		
		StringBuffer strbuf = new StringBuffer();
		
		String userName = "";
		String userRoles = "";
		if(profile!=null){
			userName = ((UserProfile)profile).getUserId().toString();

			Collection roles = ((UserProfile)profile).getRolesForUse();

			userRoles = createRolesString(roles);
		}
		Date now = new Date();
		String dateString = now.toString();
		Format formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		 try {
			 Date parsed = new Date();
			 String customDate = formatter.format(parsed);
			 strbuf.append(customDate);
	        }
	        catch(ParseException pe) {
	            System.out.println("ERROR: Cannot parse \"" + dateString + "\"");
	        }
	        strbuf.append("';'");
	        strbuf.append(request.getLocalAddr());
	        strbuf.append("';'");
	        strbuf.append(request.getLocalName());
	        strbuf.append("';'");
	        strbuf.append(request.getRemoteAddr());
	        strbuf.append("';'");
	        String myClientHostname = request.getHeader(CLIENT_IP1_KEY);
            if (myClientHostname == null) {
                myClientHostname = request.getHeader(CLIENT_IP2_KEY);
                if (myClientHostname == null) {
                    myClientHostname = "";
                }
            }
            strbuf.append(myClientHostname);
	        strbuf.append("';'");
	        strbuf.append(request.getRemoteHost());
	        strbuf.append("';'';'");
	        strbuf.append(profile.getUserUniqueIdentifier());
	        strbuf.append("';'");
	        strbuf.append(profile.getRoles());
	        strbuf.append("';'");
	        strbuf.append(request.getHeader("user-agent"));
	        strbuf.append("';'");
	        strbuf.append(action_code);
	        strbuf.append("';'");
	        strbuf.append(request.getRequestURI());
	        strbuf.append("';'");
	        strbuf.append(profile.getUserUniqueIdentifier());
	        strbuf.append("';'");
		if(parameters!=null){
		Set set = parameters.entrySet(); 
		Iterator i = set.iterator();
		int separator = 0;
		// Display elements
		
		while(i.hasNext()) {
			Map.Entry me = (Map.Entry)i.next();
			if(separator == 0){
				strbuf.append(me.getKey());
				strbuf.append("=");
				strbuf.append(me.getValue());
			}
			else{
				strbuf.append("&");
				strbuf.append(me.getKey());
				strbuf.append("=");
				strbuf.append(me.getValue());
			}
			separator++;
		}
		strbuf.append("';'");
		}
		strbuf.append(esito);
		strbuf.append("';'");
		if(esito.equals("OK")){
			strbuf.append("0';");
		}
		else{
			strbuf.append("-1';");
		}
		
		
		
		
	

//		if(jdbcConnection!=null){
//			ja = new CustomJDBCAppender(jdbcConnection);			
//			if(updateDB && action_code!=null){
//				String sqlInsert = "INSERT INTO SBI_ACTIVITY_MONITORING (ACTION_TIME, USERNAME, USERGROUP, LOG_LEVEL, ACTION_CODE, INFO)";
//				sqlInsert += "VALUES('%d{"+dbTimestampFormat+"}';'"+userName+"';'"+userRoles+"';'%5p';'"+action_code+"';'"+(info!=null?info:"")+"')";
//				logger.debug("SQL INSERT:"+sqlInsert);
//				ja.setSql(sqlInsert);
//				audit_logger.addAppender(ja);
//			}
//		}
		// These messages with Priority >= setted priority will be logged to the database.
		audit_logger.info(strbuf);
		logger.info("NUOVO_LOG.....................");

		// not required
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
