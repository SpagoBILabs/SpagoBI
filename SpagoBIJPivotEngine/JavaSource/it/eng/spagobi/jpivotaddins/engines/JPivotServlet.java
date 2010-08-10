/**
 * 
 * LICENSE: see LICENSE.txt file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.jpivotaddins.util.SessionObjectRemoval;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tonbeller.wcf.controller.RequestContext;

public class JPivotServlet extends HttpServlet {

    private transient Logger logger = Logger.getLogger(this.getClass());

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

	logger.debug("Starting service method...");
	HttpSession session = request.getSession();
	// USER PROFILE
	String documentId = (String) request.getParameter("document");
	String forward = request.getParameter("forward");
	String dimAccRulStr = request.getParameter("dimension_access_rules");
	logger.debug("documentId:" + documentId);
	logger.debug("forward:" + forward);
	logger.debug("dimAccRulStr:" + dimAccRulStr);

	String language = request.getParameter(SpagoBIConstants.SBI_LANGUAGE);
	String country = request.getParameter(SpagoBIConstants.SBI_COUNTRY);
	logger.debug("Locale parameters received: language = [" + language + "] ; country = [" + country + "]");

	Locale locale = null;

	try {
	    locale = new Locale(language, country);
	} catch (Exception e) {
	    logger.debug("Error while creating Locale object from input parameters: language = [" + language
		    + "] ; country = [" + country + "]");
	    logger.debug("Creating default locale [en,US].");
	    locale = new Locale("en", "US");
	}

	SessionObjectRemoval.removeSessionObjects(session);

	RequestContext context = RequestContext.instance();
	context.setLocale(locale);

	session.setAttribute("document", documentId);

	session.setAttribute("dimension_access_rules", dimAccRulStr);

	if (forward == null || forward.trim().equals("")) {
	    logger.debug("forward == null, then set forward = jpivotOlap.jsp");
	    forward = "jpivotOlap.jsp";
	}
	
	try {
	    request.getRequestDispatcher(forward).forward(request, response);
	} catch (ServletException e) {
	    logger.error("Error while forwarding to " + forward, e);
	}
	logger.debug("End service method");
    }
}
