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

import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.tools.massiveExport.bo.ProgressThread;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetMassiveExportProgressStatus extends AbstractSpagoBIAction {

	private final String SERVICE_NAME = "GET_MASSIVE_EXPORT_PROGRESS_STATUS";

	// logger component
	private static Logger logger = Logger.getLogger(GetMassiveExportProgressStatus.class);


	public static final String  MESSAGE_STARTED = "STARTED";
	public static final String  MESSAGE_DOWNLOAD = "DOWNLOAD";	
	public static final String  MESSAGE_CLOSED = "CLOSED";
	
	// JSON ATTRIBUTE PASSED
	public static final String  NO_WORK_PRESENT = "noWorkPresent";
	public static final String  FUNCT_CD = "functCd";
	public static final String  TOTAL = "total";
	public static final String  PARTIAL = "partial";
	public static final String  RANDOM_KEY = "randomKey";
	public static final String  PROGRESS_THREAD_ID = "progressThreadId";
	public static final String  MESSAGE = "message";
	
	
	
	static Integer progress = 0;


	@Override
	public void doService() {
		//logger.debug("IN");

		IEngUserProfile profile = getUserProfile();
		String userId = profile.getUserUniqueIdentifier().toString();

		Integer total = null;
		Integer partial = null;

		IProgressThreadDAO progressThreadDAO = null;
		boolean noWorkPresent = false;
		try{
			progressThreadDAO = DAOFactory.getProgressThreadDAO();

			List<ProgressThread> listPT = progressThreadDAO.loadNotClosedProgressThreadsByUserId(userId);
			JSONArray array = new JSONArray();
		
			if(listPT != null){
				//logger.debug("Progress Thread for userId="+userId+ " retrieved "+listPT.size()+" documents");
				for (Iterator iterator = listPT.iterator(); iterator.hasNext();) {
					ProgressThread pT = (ProgressThread) iterator.next();
					JSONObject obj = new JSONObject();
					partial = pT.getPartial();
					total = pT.getTotal();
					obj.put(FUNCT_CD, pT.getFunctionCd());
					obj.put(TOTAL, total);
					obj.put(PARTIAL, partial);
					obj.put(RANDOM_KEY, pT.getRandomKey());
					obj.put(PROGRESS_THREAD_ID, pT.getProgressThreadId());
					if(partial>=total){
						//logger.debug("Work is completed");  // if finish mark as downlod
						obj.put(MESSAGE, MESSAGE_DOWNLOAD);					
					}
					else{
						obj.put(MESSAGE, MESSAGE_STARTED);											
					}
					array.put(obj);
				//logger.debug(""+pT);
				}				

			}
			else{
				//logger.debug("Progress Threads for userId="+userId+ " no more present; set as complete");
				noWorkPresent = true;
				//object.put(NO_WORK_PRESENT, true);
			}

			writeBackToClient(new JSONSuccess(array));

		} catch (EMFUserError err) {
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", err);
		}
		catch (JSONException err) {
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", err);
		}
		catch (IOException err) {
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", err);
		}
	}


}
