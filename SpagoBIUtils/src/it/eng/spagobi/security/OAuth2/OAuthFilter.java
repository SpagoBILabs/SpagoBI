package it.eng.spagobi.security.OAuth2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

import sun.misc.BASE64Encoder;

/**
 * Servlet Filter implementation class OAuthFilter
 */
public class OAuthFilter implements Filter {
	String clientId;
	String secret;
	String redirectUri;

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/*
	 * Per ottenere il token dell'applicazione!
	 *
	 * curl -x https://proxy.eng.it:3128 --proxy-user aldaniel:[password] --data "email=c4327965@trbvm.com&password=provaSP"
	 * https://account.lab.fiware.org/api/v1/tokens.json -k
	 *
	 *
	 * Per ottenere le informazioni dell'applicazione (compresi i ruoli): curl -x https://proxy.eng.it:3128 --proxy-user aldaniel:[password]
	 * https://account.lab.fiware.org/applications/sbi.json?auth_token=ybVEszzhikm3UWZe4fQg -k
	 */

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
				String e = new String(new BASE64Encoder().encode(e1.getBytes()));
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

		ResourceBundle rb = null;

		try {
			rb = ResourceBundle.getBundle("it.eng.spagobi.security.OAuth2.proxy");
		} catch (MissingResourceException e) {
			// TODO
		}

		if (rb != null) {
			final String proxyUrl = rb.getString("PROXY_URL");
			final String proxyPort = rb.getString("PROXY_PORT");
			final String proxyUser = rb.getString("PROXY_USER");
			final String proxyPassword = rb.getString("PROXY_PASSWORD");

			if (proxyUrl != null && proxyPort != null) {
				System.setProperty("https.proxyHost", proxyUrl);
				System.setProperty("https.proxyPort", proxyPort);
			}
			if (proxyUser != null && proxyPassword != null) {
				Authenticator authenticator = new Authenticator() {

					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(proxyUser, proxyPassword.toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
			}
		}
	}
}
