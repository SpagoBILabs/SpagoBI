
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

import it.eng.spago.base.SourceBean;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.palo.viewapi.internal.AuthUserImpl;

import sun.misc.BASE64Decoder;

/**
 * @author Monica Franceschini
 *
 */
public class JPaloEngineStartServlet extends AbstractEngineStartServlet {
	
	private static final String PALO_BASE_URL = "SpagoBIJPaloEngine.html";
	private static String DOCUMENT_ID="document";
	private static String SUBOBJ_ID="subobjectId";
	private static String IS_DEVELOPER="isSpagoBIDev";
	private static String IS_NEW_DOCUMENT="isNewDocument";
	private static final String LANG = "language";
	private static final String COUNTRY = "country";
	private static final ResourceBundle rb = ResourceBundle.getBundle("deploy", Locale.ITALIAN);
	
	/**
     * Logger component
     */
    private static transient Logger logger = Logger.getLogger(JPaloEngineStartServlet.class);

    /**
     * Initialize the engine
     */
    public void init(ServletConfig config) throws ServletException {
		 super.init(config);
		logger.debug("Initializing SpagoBI JPalo Engine...");
    }

   
    public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
    	JPaloEngineTemplate template = null;
    	String jpaloUrl;

    	logger.debug("IN");

		try {		
			HttpSession session = servletIOManager.getHttpSession();
			cleanSessionAttributes(session);
			
			IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			ContentServiceProxy contentProxy = new ContentServiceProxy((String)profile.getUserUniqueIdentifier(),session);
			String documentId = (String) servletIOManager.getRequest().getParameter(DOCUMENT_ID);
			String subobj = (String) servletIOManager.getRequest().getParameter(SUBOBJ_ID);
			String isSpagoBIDev = (String) servletIOManager.getRequest().getParameter(IS_DEVELOPER);
			String isNewDoc = (String) servletIOManager.getRequest().getParameter(IS_NEW_DOCUMENT);
			
			Locale locale = servletIOManager.getLocale();
			String country = locale.getCountry();
			String language = locale.getLanguage();

			if(language == null || country == null){
				language = (String) servletIOManager.getRequest().getParameter(LANG);
				country = (String) servletIOManager.getRequest().getParameter(COUNTRY);
			}
			
			String pass = AuthUserImpl.encrypt(rb.getString("jpalo.admin.password"));
			
			jpaloUrl = PALO_BASE_URL;
			
			jpaloUrl += "?locale=";			
			jpaloUrl += language+"_"+country;
			jpaloUrl += "&theme=gray&options=(";
			jpaloUrl += "user=\"";
			jpaloUrl += rb.getString("jpalo.admin.user");
			jpaloUrl += "\",pass=\"";
			jpaloUrl += pass;
			jpaloUrl += "\"";
			if((isNewDoc != null && isNewDoc.equals("true")) && 
					(isSpagoBIDev != null && isSpagoBIDev.equals("true"))){
				//new document--> template doesn't exist!
				//open editor with no view
		    	if(documentId != null && profile != null){
		    		jpaloUrl += ",spagobiusr=\""+(String)profile.getUserUniqueIdentifier()+"\"";	    	
		    		jpaloUrl += ",spagobidoc=\""+documentId+"\"";
		    	}
		    	if(isSpagoBIDev != null){
		    		jpaloUrl += ",isdeveloper=\""+isSpagoBIDev+"\"";
		    	}		    	
		    	
			}else{
				Content templateContent = contentProxy.readTemplate(documentId,new HashMap());
	
				byte[] byteContent = null;
				try {
					BASE64Decoder bASE64Decoder = new BASE64Decoder();
					byteContent = bASE64Decoder.decodeBuffer(templateContent.getContent());
					String xmlSourceBean = new String(byteContent);
					SourceBean sb =SourceBean.fromXMLString(xmlSourceBean);
					template = new JPaloEngineTemplate(sb);		
					
					if(template == null){
				    	if(documentId != null && profile != null){
				    		jpaloUrl += ",spagobiusr=\""+(String)profile.getUserUniqueIdentifier()+"\"";	    	
				    		jpaloUrl += ",spagobidoc=\""+documentId+"\"";
				    	}
				    	if(isSpagoBIDev != null){
				    		jpaloUrl += ",isdeveloper=\""+isSpagoBIDev+"\"";
				    	}	
					}else{
						//looks for cube name to create view 
						//NB: methods to create view dinamically available only if already logged in Jpalo
						String cubeName = template.getCubeName();
		
						//adds information about spagobi context of execution
				    	if(documentId != null && profile != null){
				    		jpaloUrl += ",spagobiusr=\""+(String)profile.getUserUniqueIdentifier()+"\"";	    	
				    		jpaloUrl += ",spagobidoc=\""+documentId+"\"";
				    	}
				    	if(subobj != null){
				    		jpaloUrl += ",spagobisubobj=\""+subobj+"\"";
				    	}
				    	if(isSpagoBIDev != null){
				    		jpaloUrl += ",isdeveloper=\""+isSpagoBIDev+"\"";
				    	}
		
						if(cubeName != null && !cubeName.equals("")){				
							jpaloUrl += ",openview=\"";
							jpaloUrl += "\"";
							jpaloUrl += ",cubename=\"";
							jpaloUrl += cubeName;
							jpaloUrl += "\"";
						}else{
							jpaloUrl += ",openview=\"";
							jpaloUrl += template.getViewName();
							jpaloUrl += "\"";
						}
						String account = template.getAccountName();
						if(account != null && !account.equals("")){
							jpaloUrl += ",account=\"";
							jpaloUrl += account;
							jpaloUrl += "\"";
						}
						String connection = template.getConnectionName();
						if(connection != null && !connection.equals("")){
							jpaloUrl += ",connection=\"";
							jpaloUrl += connection;
							jpaloUrl += "\"";
						}
						jpaloUrl += ",hidestaticfilter";
						if(isSpagoBIDev == null || isSpagoBIDev.equals("")){
							jpaloUrl += ",hidenavigator";
						}			
					}
				}catch (Throwable t){
					logger.warn("Error on decompile",t); 
				}

			}
			if(isSpagoBIDev != null && isSpagoBIDev.equals("true")){
				jpaloUrl += ",hidesaveas";

			}else{
				jpaloUrl += ",hidesave";
				//checks if some user tries to execute document with no template
				if(template == null 
						||(template.getViewName() == null || template.getViewName().equals(""))
						||(template.getCubeName() == null || template.getCubeName().equals(""))){
					logger.error("Forbidden operation: user trying to execute document with no template or view defined");
					throw new SpagoBIEngineException("You are trying to execute document with no template or view defined.",
							"You are trying to execute document with no template or view defined.") {
					};	
					
				}
			}
			jpaloUrl += ",hideconnectionaccount";
			jpaloUrl += ",hideuserrights";
			jpaloUrl += ",hideviewtabs";
			jpaloUrl += ")";
			logger.info(jpaloUrl);
			//System.out.println(jpaloUrl);

			String urlWithSessionID = servletIOManager.getResponse().encodeRedirectURL( jpaloUrl );
			servletIOManager.getResponse().sendRedirect( urlWithSessionID );
			
		} catch(Throwable t) {
			t.printStackTrace();
			logger.error(t.getMessage());
			if(t instanceof SpagoBIEngineException){
				throw (SpagoBIEngineException)t;
			}else{
				throw new SpagoBIEngineException("An unpredicted error occured while executing palo-engine. Please check the log for more informations on the causes",
				"An unpredicted error occured while executing palo-engine. Please check the log for more informations on the causes", t);		
			}
		} finally {
			servletIOManager.auditServiceEndEvent();
			logger.debug("OUT");
		}

    }
    
    private void cleanSessionAttributes(HttpSession session){
    	session.removeAttribute("isdeveloper");
    	//logger.info("clean 1 ::"+(String)session.getAttribute("spagobi_state"));
    	session.removeAttribute("spagobi_state");
    	//logger.info("clean 2 ::"+(String)session.getAttribute("spagobi_state"));
    	session.removeAttribute("spagobisubobj");
    }
}
