/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.weka.services.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.WekaEngine;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DownloadOutputFileServlet extends HttpServlet {

	private static transient Logger logger = Logger.getLogger(DownloadOutputFileServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.debug("IN");
		try {
			String outputFileName = request.getParameter("outputFileName");
			File outputFilesDir = WekaEngine.getConfig().getEngineOutputFilesDir();
			File outputFile = new File(outputFilesDir, outputFileName);
			
			// setup response header
			if(response instanceof HttpServletResponse) {
				response.setHeader("Content-Disposition", "attachment" + "; filename=\"" + outputFile.getName() + "\";");
			}
			
			response.setContentType("text/plain");
			response.setStatus(200);
			
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(outputFile));
			
			int b = -1;
			int contentLength = 0;
			byte[] buf = new byte[1024];
			while((b = in.read(buf)) != -1) {
				response.getOutputStream().write(buf, 0, b);
				contentLength += b;
			}	
			response.setContentLength( contentLength );
			response.getOutputStream().flush();
			
			in.close();
		} catch(Throwable t) {
			
		} finally {
			logger.debug("IN");
		}
	}
}
