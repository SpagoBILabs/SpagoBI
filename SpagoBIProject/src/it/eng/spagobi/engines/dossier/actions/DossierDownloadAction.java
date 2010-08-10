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
package it.eng.spagobi.engines.dossier.actions;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.engines.dossier.bo.DossierPresentation;
import it.eng.spagobi.engines.dossier.constants.DossierConstants;
import it.eng.spagobi.engines.dossier.dao.DossierDAOHibImpl;
import it.eng.spagobi.engines.dossier.dao.IDossierDAO;
import it.eng.spagobi.engines.dossier.dao.IDossierPresentationsDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;


public class DossierDownloadAction extends AbstractHttpAction {
	
	public static final String ACTION_NAME = "DOSSIER_DOWNLOAD_ACTION";
	
	static private Logger logger = Logger.getLogger(DossierDownloadAction.class);
	
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		logger.debug("IN");
		freezeHttpResponse();
		HttpServletResponse response = getHttpResponse();
		OutputStream out = null;
		String task = "";
		try{
	 		task = (String) serviceRequest.getAttribute(DossierConstants.DOSSIER_SERVICE_TASK);		
	 		out = response.getOutputStream();
	 		if(task.equalsIgnoreCase(DossierConstants.DOSSIER_SERVICE_TASK_GET_TEMPLATE_IMAGE)){
	 			String pathimg = (String)serviceRequest.getAttribute(DossierConstants.DOSSIER_SERVICE_PATH_IMAGE);
			 	if(pathimg!=null) {
			 		if (!pathimg.startsWith("/") && !(pathimg.charAt(1) == ':')) {
			 			String root = ConfigSingleton.getRootPath();
			 			pathimg = root + "/" + pathimg;
			 		}
				 	File imgFile = new File(pathimg);
				 	FileInputStream fis = new FileInputStream(imgFile);
				 	byte[] content = GeneralUtilities.getByteArrayFromInputStream(fis);
				 	out.write(content);
				 	out.flush();
		            fis.close();
		            imgFile.delete();
		            return;
			 	} 
	 		} else if(task.equalsIgnoreCase(DossierConstants.DOSSIER_SERVICE_TASK_DOWN_FINAL_DOC)){
	 			String activityKey = (String) serviceRequest.getAttribute(SpagoBIConstants.ACTIVITYKEY);
	 			JbpmContext jbpmContext = null;
	 			Integer dossierId = null;
	 			Long workflowProcessId = null;
	 			try {
		 			JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
		 	    	        jbpmContext = jbpmConfiguration.createJbpmContext();
		 			long activityKeyId = Long.valueOf(activityKey).longValue();
		 			TaskInstance taskInstance = jbpmContext.getTaskInstance(activityKeyId);
		 			ContextInstance contextInstance = taskInstance.getContextInstance();
		 			ProcessInstance processInstance = contextInstance.getProcessInstance();
		 			workflowProcessId = new Long(processInstance.getId());
		 			String dossierIdStr = (String) contextInstance.getVariable(DossierConstants.DOSSIER_ID);
		 			dossierId = new Integer(dossierIdStr);
	 			} finally {
	 				if (jbpmContext != null) jbpmContext.close();
	 			}
	 			if (dossierId != null) {
	 				BIObject dossier = DAOFactory.getBIObjectDAO().loadBIObjectById(dossierId);
	 				IDossierPresentationsDAO dpDAO = DAOFactory.getDossierPresentationDAO();
	 				DossierPresentation presentation = dpDAO.getCurrentPresentation(dossierId, workflowProcessId);
	 				byte[] finalDocBytes = presentation.getContent();
				 	response.setHeader("Content-Disposition","attachment; filename=\"" + dossier.getName() + ".ppt" + "\";");
		 			response.setContentLength(finalDocBytes.length);
		 			out.write(finalDocBytes);
		 			out.flush();
	 			} else {
	 				logger.error("Dossier configuration path not found!");
	 			}
	            return;
		 		
	 		} else if(task.equalsIgnoreCase(DossierConstants.DOSSIER_SERVICE_TASK_DOWN_PRESENTATION_VERSION)) {
	 			String dossierIdStr = (String) serviceRequest.getAttribute(DossierConstants.DOSSIER_ID);
	 			Integer dossierId = new Integer(dossierIdStr);
	 			String versionStr = (String) serviceRequest.getAttribute(DossierConstants.VERSION_ID);
	 			Integer versionId = new Integer(versionStr);
 				BIObject dossier = DAOFactory.getBIObjectDAO().loadBIObjectById(dossierId);
 				IDossierPresentationsDAO dpDAO = DAOFactory.getDossierPresentationDAO();
 				byte[] finalDocBytes = dpDAO.getPresentationVersionContent(dossierId, versionId);
			 	response.setHeader("Content-Disposition","attachment; filename=\"" + dossier.getName() + ".ppt" + "\";");
	 			response.setContentLength(finalDocBytes.length);
	 			out.write(finalDocBytes);
	            return;
	            
	 		} else if(task.equalsIgnoreCase(DossierConstants.DOSSIER_SERVICE_TASK_DOWN_OOTEMPLATE)) {
	 			String tempFolder = (String) serviceRequest.getAttribute(DossierConstants.DOSSIER_TEMP_FOLDER);
	 			IDossierDAO dossierDao = new DossierDAOHibImpl();
	 			String templateFileName = dossierDao.getPresentationTemplateFileName(tempFolder);
	 			InputStream templateIs = dossierDao.getPresentationTemplateContent(tempFolder);
	 			byte[] templateByts = GeneralUtilities.getByteArrayFromInputStream(templateIs);
	 			response.setHeader("Content-Disposition","attachment; filename=\"" + templateFileName + "\";");
	 			response.setContentLength(templateByts.length);
	 			out.write(templateByts);
	 			out.flush();
	            return;
	 			
	 		} else if(task.equalsIgnoreCase(DossierConstants.DOSSIER_SERVICE_TASK_DOWN_WORKFLOW_DEFINITION)) {
	 			String tempFolder = (String) serviceRequest.getAttribute(DossierConstants.DOSSIER_TEMP_FOLDER);
	 			IDossierDAO dossierDao = new DossierDAOHibImpl();
	 			String workDefName = dossierDao.getProcessDefinitionFileName(tempFolder);
	 			InputStream workIs = dossierDao.getProcessDefinitionContent(tempFolder);
	 			byte[] workByts = GeneralUtilities.getByteArrayFromInputStream(workIs);
	 			response.setHeader("Content-Disposition","attachment; filename=\"" + workDefName + "\";");
	 			response.setContentLength(workByts.length);
	 			out.write(workByts);
	 			out.flush();
	            return;
	 		}
	 		logger.debug("OUT");
	 	} catch(Exception e) {
	 		logger.error("Exception during execution of task " + task, e);
	 	}
	 }
	
}
