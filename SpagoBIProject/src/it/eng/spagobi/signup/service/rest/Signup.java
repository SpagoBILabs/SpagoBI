package it.eng.spagobi.signup.service.rest;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.bean.SbiUserAttributesId;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.rest.annotations.ToValidate;
import it.eng.spagobi.rest.publishers.PublisherService;
import it.eng.spagobi.security.Password;
import it.eng.spagobi.tools.dataset.validation.FieldsValidatorFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.IOException;
import java.security.Security;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/signup")
public class Signup {

	@Context
	private HttpServletResponse servletResponse;

	private static Logger logger = Logger.getLogger(PublisherService.class);

	
    @POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@ToValidate(typeName=FieldsValidatorFactory.SIGNUP)
	public String create(@Context HttpServletRequest req) {
		
    	
        String nome     =  req.getParameter("nome");
		String cognome  =  req.getParameter("cognome");
		String username =  req.getParameter("username");
		String password =  req.getParameter("password");
		
		String email    =  req.getParameter("email");
		
		try {
		  ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
		  
		  if( userDao.isUserIdAlreadyInUse( username ) != null ){
		    JSONObject errorMsg = new JSONObject();
		    JSONArray errors = new JSONArray();
		    errors.put(new JSONObject("{message: 'Username in uso'}"));
		    errorMsg.put("errors", errors);
			errorMsg.put("message", "validation-error");
			return errorMsg.toString(); 
		  }
		  
		  SbiUser user = new SbiUser();
		  user.setUserId(username);
		  user.setPassword( Password.encriptPassword( password ));
		  user.setFullName( nome + " " + cognome );
		  user.getCommonInfo().setOrganization("SPAGOBI");
		  user.setFlgPwdBlocked(true);
		  
		  Set<SbiExtRoles> roles = new HashSet<SbiExtRoles>();
		  SbiExtRoles r = new SbiExtRoles();
		  r.setExtRoleId(3);
		  r.getCommonInfo().setOrganization("SPAGOBI");
		  roles.add(r);
		  user.setSbiExtUserRoleses(roles);
		  
		  Set<SbiUserAttributes> attributes = new HashSet<SbiUserAttributes>();
		  SbiUserAttributes aemail = new SbiUserAttributes();
		  aemail.getCommonInfo().setOrganization("SPAGOBI");
		  SbiUserAttributesId emailId = new SbiUserAttributesId();
		  emailId.setAttributeId(5);
		  aemail.setId(emailId);
		  aemail.setAttributeValue(email);
		  attributes.add(aemail);
		  
		  user.setSbiUserAttributeses(attributes);
		  
		  int id = userDao.fullSaveOrUpdateSbiUser(user);
		  
		  sendMail(email, "attivazione account", "ciao " +  id );
		  
			
			
		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
		}

		return new JSONObject().toString();
	}
	
    @POST
	@Path("/prepare")
	public void prepare(@Context HttpServletRequest req) {
		
		
	  try {
	    req.getRequestDispatcher("/WEB-INF/jsp/signup/signup.jsp").forward(req, servletResponse);
	  } catch (ServletException e) {
			logger.error("Error dispatching request");
	  } catch (IOException e) {
			logger.error("Error writing content");
	  }
	}
    
    private void sendMail(String emailAddress, String subject, String emailContent) throws Exception{
		
	    final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	    final String CUSTOM_SSL_FACTORY = "it.eng.spagobi.commons.services.DummySSLSocketFactory";
	    
		String smtphost = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtphost");
	    String smtpport = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtpport");
	    String smtpssl  = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.useSSL"); 
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
    private class SMTPAuthenticator extends javax.mail.Authenticator {
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
