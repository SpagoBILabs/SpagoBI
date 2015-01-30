package it.eng.spagobi.services.Oauth2;

import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.security.exceptions.SecurityException;

import java.io.IOException;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Oauth2SsoService implements SsoServiceInterface {

	public void validateTicket(String ticket, String userId) throws SecurityException {
		// TODO Auto-generated method stub

	}

	public String readTicket(HttpSession session) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String readUserIdentifier(HttpServletRequest request) {
		HttpSession session = request.getSession();

		return (String) session.getAttribute("access_token");
	}

	public String readUserIdentifier(PortletSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
