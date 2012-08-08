/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.utilities;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.CustomJDBCAppender;
import it.eng.spagobi.commons.bo.UserProfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.InetAddress;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
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
	
	//parametri PCS
    public static String PROFILE1_KEY  = "PROFILESM";
    public static String PROFILE2_KEY  = "profilesm";
    public static String OPERATOR1_KEY  = "USERSM";
    public static String OPERATOR2_KEY  = "usersm";
    public static String CLIENT_IP1_KEY = "IP_CLIENT";
    public static String CLIENT_IP2_KEY = "ip_client";
    //parametri ActiveX
   // public static String CLIENT_HOSTNAME_KEY  = "clientHost";
    //public static String CLIENT_USERNAME_KEY = "clientUser";


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
			 strbuf.append("'");
			 strbuf.append(customDate);
	        }
	        catch(ParseException pe) {
	            System.out.println("ERROR: Cannot parse \"" + dateString + "\"");
	        }
	        strbuf.append("';'");
	        strbuf.append(request.getLocalAddr());  // IP GENERATORE
	        strbuf.append("';'");
	        strbuf.append(request.getLocalName()); // HOSTNAME GENERATORE
	        strbuf.append("';'");
	        strbuf.append(request.getRemoteAddr()); // IP SORGENTE
	        strbuf.append("';'");
	        String IpClient = request.getHeader(CLIENT_IP1_KEY);
	        String hostNameClient = "";
	        if (IpClient == null) {
            	IpClient = request.getHeader(CLIENT_IP2_KEY);
                if (IpClient == null) {
                	IpClient = "";
                }else {
                	InetAddress hostName= InetAddress.getByAddress(IpClient.getBytes());
                	hostNameClient=hostName.getHostName();
                }
	        }
            strbuf.append(IpClient); // IP CLIENT
	        strbuf.append("';'");
	        strbuf.append(hostNameClient); // HOST NAME CLIENT
	        strbuf.append("';'';'");  // UTENZA CLIENT  lasciato vuoto
	        String utenzaApplicativa = request.getHeader(OPERATOR1_KEY);
            if (utenzaApplicativa == null) {
            	utenzaApplicativa = request.getHeader(OPERATOR2_KEY);
                if (utenzaApplicativa == null) {
                	utenzaApplicativa = userName;			// se l'utenza applicativa Ã¨ vuota inserisco lo user id di spagobi
                }
            }
            
	        strbuf.append(utenzaApplicativa);	// UTENZA APPLICATIVA
	        strbuf.append("';'");
	        
	        String profiloApplicativo = request.getHeader(PROFILE1_KEY);
            if (profiloApplicativo == null) {
            	profiloApplicativo = request.getHeader(PROFILE2_KEY);
                if (profiloApplicativo == null) {
                	profiloApplicativo = userRoles;			// se il profilo applicativo Ã¨ vuoto inserisco i ruoli di spagobi
                }
            }	        
	        strbuf.append(profiloApplicativo);					// PROFILO UTENTE
	        strbuf.append("';'");
	        strbuf.append(request.getHeader("user-agent"));		// APPLICATIVO CLIENT
	        strbuf.append("';'");
	        if (action_code!=null)  strbuf.append(action_code);	// AZIONE
	        else strbuf.append("");	
	        strbuf.append("';'");
	        strbuf.append(userName);							// OGGETTO
	        strbuf.append("';'");
	        strbuf.append(request.getRequestURI());				// URI
	        strbuf.append("';'");								
	        													// PARAMETRI
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
				strbuf.append(me.getValue().toString());
			}
			separator++;
		}
		strbuf.append("';'");
		}
		strbuf.append(esito);						// ESITO
		strbuf.append("';'");
		if(esito.equals("OK")){
			strbuf.append("0';");					// RET CODE
		}
		else{
			strbuf.append("-1';");
		}
		String logString = strbuf.toString();

        MessageDigest md5  = MessageDigest.getInstance("MD5");        

        strbuf.append(calculateHash(md5, strbuf));
		
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
	
	public static String calculateHash(MessageDigest algorithm,
			StringBuffer str) throws Exception{
		String is=new String (str);
        DigestInputStream   dis = new DigestInputStream(new ByteArrayInputStream(is.getBytes()), algorithm);
        while (dis.read() != -1);
        byte[] hash = algorithm.digest();
        return byteArray2Hex(hash);
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

}
