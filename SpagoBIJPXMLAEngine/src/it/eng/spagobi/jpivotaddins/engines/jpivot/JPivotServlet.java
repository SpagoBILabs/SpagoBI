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
package it.eng.spagobi.jpivotaddins.engines.jpivot;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tonbeller.wcf.controller.RequestContext;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.jpivotaddins.engines.jpivot.security.SecurityUtilities;
import it.eng.spagobi.jpivotaddins.util.SessionObjectRemoval;
import it.eng.spagobi.utilities.messages.EngineMessageBundle;

public class JPivotServlet extends HttpServlet {

    private transient PublicKey publicKeyDSASbi = null;
    private transient Logger logger = Logger.getLogger(this.getClass());
    private transient SecurityUtilities secUt = null;
    private transient boolean securityAble = true;
    
  /*  public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	ServletContext context = this.getServletContext();
		String secAblePar = context.getInitParameter("SECURITY_ABLE");
		if (!secAblePar.equalsIgnoreCase("true")) {
        	securityAble = false;
        }
        if (securityAble) {
			secUt = new SecurityUtilities(logger);
			publicKeyDSASbi = secUt.getPublicKey();
		} 
	} */
    
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		logger.debug("Starting service method...");
		String language = request.getParameter(SpagoBIConstants.SBI_LANGUAGE);
		String country = request.getParameter(SpagoBIConstants.SBI_COUNTRY);
		logger.debug("Locale parameters received: language = [" + language + "] ; country = [" + country + "]");
		
		Locale locale = null;
		
		try {
			locale = new Locale(language, country);
		} catch (Exception e) {
			logger.debug("Error while creating Locale object from input parameters: language = [" + language + "] ; country = [" + country + "]");
			logger.debug("Creating default locale [en,US].");
			locale = new Locale("en", "US");
		}
		
		HttpSession session = request.getSession();
		SessionObjectRemoval.removeSessionObjects(session);
		
		RequestContext context = RequestContext.instance();
		context.setLocale(locale);
		
		boolean authorized = true;
		Object auth = session.getAttribute("authorized");
		if (auth!=null) {	
			if(securityAble) {
				authorized = false;
				String token = request.getParameter("TOKEN_SIGN");
				String tokenclear = request.getParameter("TOKEN_CLEAR");
				if((token!=null) && !token.trim().equals("") &&  (tokenclear!=null) && !tokenclear.trim().equals("")) {
		    		if(secUt.authenticate(token, tokenclear, publicKeyDSASbi)) {
		    			authorized = true;
		    			session.setAttribute("authorized", "true");
		    		}
				}
			} else {
				String token = request.getParameter("TOKEN_SIGN");
				if(token!=null) {
					authorized = false;
					logger.error("The engine security check is not active but the driver in sending secure calls." + 
						     	"Please turn on the security check of the engine");
				}
			}
		}
		
		if(!authorized) {
			String unauthorized = EngineMessageBundle.getMessage("Unauthorized", locale);
			if (unauthorized == null) unauthorized = "Unauthorized access.";
			ServletOutputStream out = response.getOutputStream();
			out.write("<html><body><center><h2>".getBytes());
			out.write(unauthorized.getBytes());
			out.write("</h2></center></body></html>".getBytes());
			out.flush();
			out.close();
			return;
		}
		
		// if is the first request the following parameters have a request value
	    // and they are put in session, otherwise their values are taken from the session
		String forward = request.getParameter("forward");
		String jcrPath = request.getParameter("templatePath");
		String spagoBIBaseUrl = request.getParameter("spagobiurl");
		String user = request.getParameter("user");
		String role = request.getParameter("role");
		
		if (jcrPath != null) session.setAttribute("templatePath", jcrPath);
	    if (spagoBIBaseUrl != null) session.setAttribute("spagobiurl", spagoBIBaseUrl);
	    if (user != null) session.setAttribute("user", user);
	    if (role != null) session.setAttribute("role", role);

	    
	    String dimAccRulStr = request.getParameter("dimension_access_rules");
	    if (dimAccRulStr != null) {
	    	if(dimAccRulStr.trim().equalsIgnoreCase("")) {
	    		session.setAttribute("dimension_access_rules", new ArrayList());
	    	} else {
	    		String[] dimAccArray = dimAccRulStr.split(",");
	    		List dimAccList = Arrays.asList(dimAccArray);
	    		session.setAttribute("dimension_access_rules", dimAccList);
	    	}
	    }
	
		if (forward == null || forward.trim().equals("")) {
		    logger.debug("forward == null, then set forward = jpivotOlap.jsp");
		    forward = "jpivotOlap.jsp";
		}
	    
/*	    String forward = request.getParameter("forward");
	    if (forward == null || forward.trim().equals("")) {
	    	forward = "jpivotOlap.jsp";
	    }*/
	    
		try {
			request.getRequestDispatcher(forward).forward(request, response);
		} catch (ServletException e) {
			logger.error("Error while forwarding to " + forward, e);
		}
		
	}
	
}
