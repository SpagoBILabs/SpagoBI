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

package it.eng.spagobi.engines.console.exporter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.services.AbstractConsoleEngineAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.mime.MimeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class retrieves a file from the java.io.tmpdir folder and deletes it.
 * Input parameters must specify file's name and extension.
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class GetTemporaryFileAction extends AbstractConsoleEngineAction {

	// INPUT PARAMETERS
	public static final String FILE_NAME = "name";
	public static final String FILE_EXTENSION = "extension";

	// misc
	public static final String RESPONSE_TYPE_INLINE = "inline";
	public static final String RESPONSE_TYPE_ATTACHMENT = "attachment";

	public static final String SERVICE_NAME = "GET_TEMPORARY_FILE_ACTION";

	// logger component
	private static Logger logger = Logger.getLogger(GetTemporaryFileAction.class);

	public void service(SourceBean request, SourceBean response) {

		try {
			super.service(request,response);
			
			String name = getAttributeAsString( FILE_NAME );
			logger.debug("Parameter [" + FILE_NAME + "] is equals to [" + name + "]");		
			Assert.assertTrue(!StringUtilities.isEmpty( name ), "Parameter [" + FILE_NAME + "] cannot be null or empty");
			
			String extension = getAttributeAsString( FILE_EXTENSION );
			logger.debug("Parameter [" + FILE_EXTENSION + "] is equals to [" + extension + "]");		
			Assert.assertTrue(!StringUtilities.isEmpty( extension ), "Parameter [" + FILE_EXTENSION + "] cannot be null or empty");
			
			String completeFileName = name + "." + extension;
			File tempDir = new File( System.getProperty("java.io.tmpdir") ) ;
			File file = new File( System.getProperty("java.io.tmpdir") + File.separator + completeFileName);
			File parent = file.getParentFile();
			// Prevent directory traversal (path traversal) attacks
			if (!tempDir.equals(parent)) {
				logger.error("Trying to access the file [" + file.getAbsolutePath() 
			                 + "] that is not inside [" + tempDir.getAbsolutePath() + "]!!!");
				throw new SecurityException("Trying to access the file [" 
			                 + file.getAbsolutePath() + "] that is not inside [" 
			                 + tempDir.getAbsolutePath() + "]!!!");
			}
			
			if (!file.exists() || !file.isFile()) {
				logger.error("File " + file.getAbsolutePath() + " does not exist or it is not a file");
				throw new SpagoBIEngineException("File not found");
			}
			
			try {
				String mimeType = MimeUtils.getMimeType(file);
				logger.debug("Mime type recognized for file " + file + " is " + mimeType);
				writeBackToClient(file, null, true, completeFileName, mimeType);
			} catch (IOException ioe) {
				throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
			} finally {
				if (file != null && file.exists()) {
					try {
						file.delete();
					} catch (Exception e) {
						logger.warn("Impossible to delete temporary file " + file, e);
					}
				}
			}
			
		} catch (Throwable t) {
			logger.error("Impossible to get temporary file", t);
		} finally {
			logger.debug("OUT");
		}
	}

}

