package it.eng.spagobi.security.OAuth2;

import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * Servlet Filter implementation class OAuthFilter
 */
public class OAuthFilter implements Filter {
	String clientId;
	String secret;
	String redirectUri;

	/**
	 * Default constructor.
	 */
	public OAuthFilter() {
		System.setProperty("https.proxyHost", "proxy.eng.it");
		System.setProperty("https.proxyPort", "3128");
		System.setProperty("https.proxyUser", "aldaniel");
		System.setProperty("https.proxyPassword", "5JYMu17.");

		Authenticator.setDefault(new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("aldaniel", "5JYMu17.".toCharArray());
			}
		});
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/*
	 * login user: c4327965@trbvm.com password: provaSP login user: dnozs3un.fhf@20mail.it password: password jbk31676@kiois.com
	 */

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();

		if (session.isNew() || session.getAttribute("access_token") == null) {
			if (((HttpServletRequest) request).getParameter("code") == null) {
				String url = "https://account.lab.fiware.org/authorize?response_type=code&client_id=" + clientId;
				((HttpServletResponse) response).sendRedirect(url);
			} else {
				String e1 = clientId + ":" + secret;
				String e = new String(Base64.encodeBase64(e1.getBytes()));
				URL url = new URL("https://account.lab.fiware.org/token");

				// HttpsURLConnection
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

				// add request header
				con.setDoOutput(true);
				con.setRequestProperty("Authorization", "Basic " + e);
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestMethod("POST");

				con.setConnectTimeout(10000);
				con.setReadTimeout(10000);

				OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
				String body = "grant_type=authorization_code&code=" + ((HttpServletRequest) request).getParameter("code") + "&redirect_uri=" + redirectUri;

				out.write(body);
				out.close();

				JsonReader r = Json.createReader(con.getInputStream());
				JsonObject j = r.readObject();
				con.disconnect();

				System.out.println(j);
				System.out.println();
				String access_token = j.getString("access_token");
				String refresh_token = j.getString("refresh_token"); // TODO
				r.close();

				session = ((HttpServletRequest) request).getSession();
				session.setAttribute("access_token", access_token);
				((HttpServletResponse) response).sendRedirect("http://localhost:8080/SpagoBI/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE");
			}
		} else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		clientId = fConfig.getInitParameter("clientId");
		secret = fConfig.getInitParameter("secret");
		redirectUri = fConfig.getInitParameter("redirectUri");
	}

	private void setRole(Role role, int userIdInt) {
		SbiExtUserRoles sbiExtUserRole = new SbiExtUserRoles();
		SbiExtUserRolesId id = new SbiExtUserRolesId();
		try {
			ISbiUserDAO userDAO = DAOFactory.getSbiUserDAO();
			Integer extRoleId = role.getId();
			id.setExtRoleId(extRoleId); // role Id
			id.setId(userIdInt); // user Id
			sbiExtUserRole.setId(id);
			userDAO.updateSbiUserRoles(sbiExtUserRole);
			RoleDAOHibImpl roleDAO = new RoleDAOHibImpl();
			userDAO.updateSbiUserRoles(sbiExtUserRole);
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("An unexpected error occurred while associating role [" + role.getName() + "] to user with id " + userIdInt, e);
		}
	}
}
