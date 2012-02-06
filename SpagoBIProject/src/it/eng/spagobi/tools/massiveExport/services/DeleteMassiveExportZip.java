/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/

package it.eng.spagobi.tools.massiveExport.services;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.tools.massiveExport.utils.Utilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class DeleteMassiveExportZip extends AbstractSpagoBIAction {

	private final String SERVICE_NAME = "DOWNLOAD_MASSIVE_EXPORT_ZIP";

	// logger component
	private static Logger logger = Logger.getLogger(DeleteMassiveExportZip.class);


	public static final String  RANDOM_KEY = "RANDOM_KEY";
	public static final String  PROGRESS_THREAD_ID = "PROGRESS_THREAD_ID";
	public static final String  FUNCT_CD = "FUNCT_CD";

	@Override
	public void doService() {
		logger.debug("IN");

		IEngUserProfile profile = getUserProfile();

		String randomKey = null;
		String functCd = null;
		Integer progressThreadId = null;
		File zipFile = null;
		
		try{

			randomKey = getAttributeAsString( RANDOM_KEY);
			functCd = getAttributeAsString( FUNCT_CD);
			progressThreadId = getAttributeAsInteger( PROGRESS_THREAD_ID);

			logger.debug(RANDOM_KEY+": "+randomKey);
			logger.debug(FUNCT_CD+": "+functCd);
			logger.debug(PROGRESS_THREAD_ID+": "+progressThreadId);


			// delete the record
			IProgressThreadDAO progressThreadDAO = DAOFactory.getProgressThreadDAO();
			boolean progressThreadDeleted = progressThreadDAO.deleteProgressThread(progressThreadId);
			if(!progressThreadDeleted){
				logger.warn("progress thread with id "+progressThreadId+" was not deleted, probably due to asynchron call");
				return;
			}
			logger.debug("progress thread with id "+progressThreadId+" has been deleted");

			logger.debug("Delete zipFile RandomKey = "+randomKey+" FunctCd = "+functCd+ " ProgressThreadId = "+progressThreadId);

			zipFile = Utilities.getMassiveExportZipFile(functCd, randomKey);
			if(zipFile.exists()) {
				boolean zipFileDeleted = zipFile.delete();
				if(zipFileDeleted){
					logger.debug("File [" + zipFile + "] has been deleted");
				} else{
					logger.debug("could not delete file [" + zipFile + "]");
				} 
			} else {
				logger.warn("File [" + zipFile + "] does not exist");
			}
			
			//delte folder directory if no more used
			Utilities.deleteMassiveExportFolderIfEmpty(functCd);

			writeBackToClient(new JSONSuccess(new JSONObject()));

		}
		catch (Exception err) {
			logger.error("Error in retrieving file", err);
			throw new SpagoBIServiceException("Error during delete: cannot retrieve zip file ["+ zipFile + "]", err);
		}
	}




}
