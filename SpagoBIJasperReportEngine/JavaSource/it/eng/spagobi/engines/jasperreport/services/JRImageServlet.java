/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * @authors
 * Andrea Gioia (andrea.gioia@eng.it)
 * Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class JRImageServlet extends HttpServlet {

	private static transient Logger logger = Logger
			.getLogger(JRImageServlet.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		logger.debug("IN");
		HttpSession session = request.getSession(true);

		java.text.SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 10); // Adding 10 minute to current date time
		Date date = cal.getTime();
		String dateString = dateFormat.format(date) + " GMT";
		logger.debug(dateString);
		response.setDateHeader("Expires", date.getTime());
		// response.setHeader("Expires", "Sat, 6 May 2010 12:00:00 GMT");
		response.setHeader("Cache-Control: max-age", "600");

		response.setContentType("image/png");
		response.setHeader("Content-Type", "image/png");

		String mapName = request.getParameter("mapname");
		Map imagesMap = (Map) session.getAttribute(mapName);
		if (imagesMap != null) {
			String imageName = request.getParameter("image");
			if (imageName != null) {
				byte[] imageData = (byte[]) imagesMap.get(imageName);
				imagesMap.remove(imageName);
				if (imagesMap.isEmpty()) {
					session.removeAttribute(mapName);
				}
				response.setContentLength(imageData.length);
				ServletOutputStream ouputStream = response.getOutputStream();
				ouputStream.write(imageData, 0, imageData.length);
				ouputStream.flush();
				ouputStream.close();
			}
		}

		logger.debug("OUT");
	}

}
