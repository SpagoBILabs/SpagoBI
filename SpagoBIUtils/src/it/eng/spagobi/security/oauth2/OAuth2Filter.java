/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security.oauth2;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class OAuth2Filter implements Filter {

	private static Logger logger = Logger.getLogger(OAuth2Filter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.debug("IN");

		HttpSession session = ((HttpServletRequest) request).getSession();

		Properties oauth2Config = OAuth2Config.getInstance().getConfig();

		if ((session.isNew()) || (session.getAttribute("access_token") == null)) {
			if (((HttpServletRequest) request).getParameter("code") == null) {
				String url = oauth2Config.getProperty("AUTHORIZE_BASE_URL") + "?response_type=code&client_id="
						+ OAuth2Config.getInstance().getConfig().getProperty("CLIENT_ID");
				((HttpServletResponse) response).sendRedirect(url);
			} else {
				OAuth2Client client = new OAuth2Client();
				String accessToken = client.getAccessToken(((HttpServletRequest) request).getParameter("code"));

				session.setAttribute("access_token", accessToken);

				((HttpServletResponse) response).sendRedirect("http://localhost:8080/SpagoBI/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE");
			}
		} else
			chain.doFilter(request, response);

		logger.debug("OUT");
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}