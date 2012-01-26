package it.eng.spagobi.tools.massiveExport.work;

/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.

 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 **/


import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;

import org.apache.log4j.Logger;

import commonj.work.WorkEvent;
import commonj.work.WorkListener;

/**
 * @author Giulio gavardi
 *         giulio.gavardi@eng.it
 */
public class MassiveExportWorkListener implements WorkListener {


	IEngUserProfile profile;
	LowFunctionality lowFunctionality;
	Integer progressThreadId;

	private static transient Logger logger = Logger.getLogger(MassiveExportWorkListener.class);

	public MassiveExportWorkListener() {
	}




	public MassiveExportWorkListener(IEngUserProfile profile,
			LowFunctionality lowFunctionality,
			Integer progressThreadId) {
		super();
		this.profile = profile;
		this.lowFunctionality = lowFunctionality;
		this.progressThreadId = progressThreadId;
	}


	public void workAccepted(WorkEvent event) {
		logger.info("IN");
		logger.info("OUT");
	}

	public void workRejected(WorkEvent event) {
		logger.info("IN");
		logger.info("OUT");
	}

	public void workCompleted(WorkEvent event) {

		logger.info("IN");
//		try {
//			IProgressThreadDAO threadDAO = DAOFactory.getProgressThreadDAO();
//			threadDAO.setDownloadProgressThread(progressThreadId);
//
//		} catch (EMFUserError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		logger.info("OUT");


	}

	public void workStarted(WorkEvent event) {
		logger.info("IN");
		logger.info("OUT");


	}


	public LowFunctionality getLowFunctionality() {
		return lowFunctionality;
	}


	public void setLowFunctionality(LowFunctionality lowFunctionality) {
		this.lowFunctionality = lowFunctionality;
	}



	public IEngUserProfile getProfile() {
		return profile;
	}




	public void setProfile(IEngUserProfile profile) {
		this.profile = profile;
	}

}
