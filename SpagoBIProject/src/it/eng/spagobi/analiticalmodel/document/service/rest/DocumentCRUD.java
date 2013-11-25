/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.analiticalmodel.document.service.rest;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.AnalyticalModelDocumentManagementAPI;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
@Path("/documents")
public class DocumentCRUD {

	public static final String OBJECT_ID = "docId";
	static private Logger logger = Logger.getLogger(DocumentCRUD.class);
	
	/**
	 * Service to clone a document
	 * @param req
	 * @return
	 */
	@POST
	@Path("/clone")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String cloneDocument(@Context HttpServletRequest req){
		
		logger.debug("IN");
		String ids = req.getParameter(OBJECT_ID);
		Integer id = -1;
		try {
			id = new Integer(ids);
		} catch (Exception e) {
			logger.error("Error cloning the document.. Impossible to parse the id of the document "+ids,e);
			throw new SpagoBIRuntimeException("Error cloning the document.. Impossible to parse the id of the document "+ids,e);
		}
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		
		AnalyticalModelDocumentManagementAPI documentManagementAPI = new AnalyticalModelDocumentManagementAPI( profile );
		logger.debug("Execute clone");
		documentManagementAPI.cloneDocument(id);
		logger.debug("OUT");
		return "{}";
	}
	
	/**
	 * Service to send e-mail Feedback about a document
	 * @param req
	 * @return
	 */
	@POST
	@Path("/sendFeedback")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String sendFeedback(@Context HttpServletRequest req){
		
		logger.debug("IN");
		
		// 1- Label of current document
		String label = (String)req.getParameter("label");	
		//Author of the document
		String documentCreationUser = null;
		//2 - email address of creation user
		String emailAddressdocumentCreationUser = null;
		IBIObjectDAO biObjectDao;
		try {
			biObjectDao = DAOFactory.getBIObjectDAO();
			if ((label != null) && (!label.isEmpty()) ){
				BIObject document = biObjectDao.loadBIObjectByLabel(label);
				documentCreationUser = document.getCreationUser();
				
				ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();
				SpagoBIUserProfile userProfile = supplier.createUserProfile(documentCreationUser);
				HashMap userAttributes = userProfile.getAttributes();
				if (userAttributes.get("email") != null){
					emailAddressdocumentCreationUser =(String) userAttributes.get("email");					
				}
				
			}
			// 3 - content of the email to send
			String message = (String)req.getParameter("msg");			

			// 4 - User sending the feedback (from session)
			IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			String userSendingFeedback =  null ;
			if (profile.getUserUniqueIdentifier() instanceof String){
				userSendingFeedback = (String)profile.getUserUniqueIdentifier();
			}
			
			//Check if all the informations to send a mail are valorized
			if ((emailAddressdocumentCreationUser != null) && (!emailAddressdocumentCreationUser.isEmpty())){
				if ((label != null) && (!label.isEmpty()) ){
					if ((userSendingFeedback != null) && (!userSendingFeedback.isEmpty())){
						String subject = "Feedback from user "+userSendingFeedback+" about document "+label;
						sendMail(emailAddressdocumentCreationUser,subject,message);
					}
				}
			}
		} catch (EMFUserError ex) {
			logger.error("Error sending feedback for document "+label,ex);
			try {
				return ( ExceptionUtilities.serializeException("Feedback not sent: "+ex.toString(),null));
			} catch (Exception e) {
				logger.debug("Error sending feedback for document "+label,e);
				throw new SpagoBIRuntimeException(
						"Error sending feedback for document "+label,e);
			}		
		} catch (Exception ex) {
			logger.error("Error sending feedback for document "+label,ex);
			try {
				return ( ExceptionUtilities.serializeException("Feedback not sent: "+ex.toString(),null));
			} catch (Exception e) {
				logger.debug("Error sending feedback for document "+label,e);
				throw new SpagoBIRuntimeException(
						"Error sending feedback for document "+label,e);
			}	
		}

		

		
		logger.debug("OUT");
		return "{}";
	}
	
	//sending email to emailAddress with passed subject and emailContent
	private void sendMail(String emailAddress, String subject, String emailContent) throws Exception{
		
	    final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	    final String CUSTOM_SSL_FACTORY = "it.eng.spagobi.commons.services.DummySSLSocketFactory";
	    
		String smtphost = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtphost");
	    String smtpport = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtpport");
	    String smtpssl = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.useSSL"); 
	    logger.debug(smtphost+" "+smtpport+" use SSL: "+smtpssl);
	    
	    //Custom Trusted Store Certificate Options
	    String trustedStorePath = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.trustedStore.file"); 
	    String trustedStorePassword = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.trustedStore.password"); 
	    
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
			from = "spagobi@eng.it";
		String user = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.user");
		if( (user==null) || user.trim().equals("")){
			logger.debug("Smtp user not configured");	
			user=null;
		}
		String pass = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.password");
		if( (pass==null) || pass.trim().equals("")){
		logger.debug("Smtp password not configured");	
		}
		
		//Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtphost);
		props.put("mail.smtp.port", Integer.toString(smptPort));
		//Set timeout limit for mail server to respond
		props.put("mail.smtp.timeout", "5000");             
        props.put("mail.smtp.connectiontimeout", "5000"); 
		
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
					/* Dynamic configuration of trustedstore for CA
					 * Using Custom SSLSocketFactory to inject certificates directly from specified files
					 */
	            	
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
		InternetAddress addressTo = new InternetAddress(emailAddress);

		msg.setRecipient(Message.RecipientType.TO, addressTo);
		
		// Setting the Subject and Content Type
		msg.setSubject(subject);
		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(emailContent);
		// create the Multipart and add its parts to it
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		// add the Multipart to the message
		msg.setContent(mp);
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
