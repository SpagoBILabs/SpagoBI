/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.util;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.profiling.bean.SbiUser;

import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class CommunityUtilities {
	static private Logger logger = Logger.getLogger(CommunityUtilities.class);
    final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    final String CUSTOM_SSL_FACTORY = "it.eng.spagobi.commons.services.DummySSLSocketFactory";
	
	public boolean dispatchMail(String communityName, SbiUser userToAccept, SbiUser owner, String ownerEmail, HttpServletRequest request) {
		String contextName = ChannelUtilities.getSpagoBIContextName(request);

		String mailSubj = "Community "+communityName+" membership request";
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>");
		sb.append("<HEAD>");
		sb.append("<TITLE>Community Membership Request</TITLE>");
		sb.append("</HEAD>");
		sb.append("<BODY>");
		sb.append("<p style=\"width:100%; text-align:center;\">");
		sb.append("Dear "+ owner.getFullName()+", <br/> user "+userToAccept.getFullName()+ " wants to join "+communityName+" community");
		sb.append("<br/> Select whether to accept "+userToAccept.getFullName()+" or not, clicking the following image:");		
		String protocol = request.getProtocol();
		if(protocol.equalsIgnoreCase("HTTP/1.1")){
			protocol="http";
		}else{
			protocol="https";
		}
		String server= request.getServerName();
		String port= request.getLocalPort()+"";
		
		sb.append("<br/><a href=\""
				+protocol
				+ "://"+server
				+ ":"
				+ port
				+ contextName
				+"/CommunityRequest.jsp?owner="+owner.getUserId()+"&userToAccept="+userToAccept.getUserId()+"&community="+communityName+"\">");
		sb.append("<img alt=\"Accept/Reject\" src=\""
				+protocol
				+ "://"+server
				+ ":"
				+ port
				+ contextName
				+ "/themes/sbi_default/img/go-community.png\"></a>");

		sb.append("</p>");
		sb.append("</BODY>");
		String mailTxt = sb.toString();

		logger.debug("IN");
		try{


			String smtphost = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtphost");
		    String smtpport = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtpport");
		    String smtpssl = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.useSSL"); 
		    logger.debug(smtphost+" "+smtpport+" use SSL: "+smtpssl);
		    
		    //Custom Trusted Store Certificate Options
		    String trustedStorePath = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.trustedStore.file"); 
		    
		    int smptPort=25;
		    
			if( (smtphost==null) || smtphost.trim().equals(""))
				throw new Exception("Smtp host not configured");
			if( (smtpport==null) || smtpport.trim().equals("")){
				throw new Exception("Smtp host not configured");
			}else{
				smptPort=Integer.parseInt(smtpport);
			}
				
		    
			String from = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.from");
			if( (from==null) || from.trim().equals(""))
				from = "spagobi.scheduler@eng.it";
			String user = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.user");
			if( (user==null) || user.trim().equals("")){
				logger.debug("Smtp user not configured");	
				user=null;
			}
			//	throw new Exception("Smtp user not configured");
			String pass = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.password");
			if( (pass==null) || pass.trim().equals("")){
			logger.debug("Smtp password not configured");	
			}
			//	throw new Exception("Smtp password not configured");


			//Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", smtphost);
			props.put("mail.smtp.port", Integer.toString(smptPort));
			
			// open session
			Session session=null;
			
			// create autheticator object
			Authenticator auth = null;
			if (user!=null) {
				auth = new SMTPAuthenticator(user, pass);
				props.put("mail.smtp.auth", "true");
		 	    //SSL Connection
		    	if (smtpssl.equals("true")){
		            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());	          
        
				    props.put("mail.smtps.auth", "true");
			        props.put("mail.smtps.socketFactory.port", Integer.toString(smptPort));
		            if ((!StringUtilities.isEmpty(trustedStorePath)) ) {            	
				        props.put("mail.smtps.socketFactory.class", CUSTOM_SSL_FACTORY);
		            } else {
				        props.put("mail.smtps.socketFactory.class", DEFAULT_SSL_FACTORY);
		            }
			        props.put("mail.smtp.socketFactory.fallback", "false"); 
		    	}
				
				session = Session.getInstance(props, auth);
				logger.info("Session.getInstance(props, auth)");
				
			}else{
				session = Session.getInstance(props);
				logger.info("Session.getInstance(props)");
			}
			

			// create a message
			Message msg = new MimeMessage(session);
			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0] = new InternetAddress(ownerEmail);

			msg.setRecipients(Message.RecipientType.TO, addressTo);
			// Setting the Subject 
			msg.setSubject(mailSubj);
			msg.setContent(mailTxt, "text/html");


			// send message
	    	if ((smtpssl.equals("true")) && (!StringUtilities.isEmpty(user)) &&  (!StringUtilities.isEmpty(pass))){
	    		//USE SSL Transport comunication with SMTPS
		    	Transport transport = session.getTransport("smtps");
		    	transport.connect(smtphost,smptPort,user,pass);
		    	transport.sendMessage(msg, msg.getAllRecipients());
		    	transport.close(); 
	    	}
	    	else {
	    		//Use normal SMTP
		    	Transport.send(msg);
	    	}
		} catch (Throwable e) {
			logger.error("Error while sending community membership request mail",e);
			return false;
		}finally{
			logger.debug("OUT");
		}
		return true;
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator
	{
		private String username = "";
		private String password = "";

		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(username, password);
		}

		public SMTPAuthenticator(String user, String pass) {
			this.username = user;
			this.password = pass;
		}
	}
}
