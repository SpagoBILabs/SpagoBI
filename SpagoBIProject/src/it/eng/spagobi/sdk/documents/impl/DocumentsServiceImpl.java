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
package it.eng.spagobi.sdk.documents.impl;


import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.dispatching.service.DefaultRequestContext;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IObjTemplateDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovResultHandler;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.engines.InternalEngineIFace;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.exporters.KpiExporter;
import it.eng.spagobi.engines.exporters.ReportExporter;
import it.eng.spagobi.engines.kpi.SpagoBIKpiInternalEngine;
import it.eng.spagobi.engines.kpi.bo.KpiResourceBlock;
import it.eng.spagobi.sdk.AbstractSDKService;
import it.eng.spagobi.sdk.documents.DocumentsService;
import it.eng.spagobi.sdk.documents.bo.SDKDocument;
import it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter;
import it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent;
import it.eng.spagobi.sdk.documents.bo.SDKFunctionality;
import it.eng.spagobi.sdk.documents.bo.SDKTemplate;
import it.eng.spagobi.sdk.exceptions.InvalidParameterValue;
import it.eng.spagobi.sdk.exceptions.MissingParameterValue;
import it.eng.spagobi.sdk.exceptions.NonExecutableDocumentException;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.sdk.utilities.SDKObjectsConverter;
import it.eng.spagobi.utilities.file.FileUtils;
import it.eng.spagobi.utilities.mime.MimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;


public class DocumentsServiceImpl extends AbstractSDKService implements DocumentsService {

	static private Logger logger = Logger.getLogger(DocumentsServiceImpl.class);

	public HashMap getAdmissibleValues(Integer documentParameterId, String roleName) throws NonExecutableDocumentException {
		HashMap values = new HashMap<String, String>();
		logger.debug("IN: documentParameterId = [" + documentParameterId + "]; roleName = [" + roleName + "]");
		try {
			IEngUserProfile profile = getUserProfile();
			BIObjectParameter documentParameter = DAOFactory.getBIObjectParameterDAO().loadForDetailByObjParId(documentParameterId);
			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(documentParameter.getBiObjectID());
			if (!ObjectsAccessVerifier.canSee(obj, profile)) {
				logger.error("User [" + ((UserProfile) profile).getUserName() + "] cannot execute document with id = [" + obj.getId() + "]");
				throw new NonExecutableDocumentException();
			}
			List correctRoles = ObjectsAccessVerifier.getCorrectRolesForExecution(obj.getId(), profile);
			if (correctRoles == null || correctRoles.size() == 0) {
				logger.error("User [" + ((UserProfile) profile).getUserName() + "] has no roles to execute document with id = [" + obj.getId() + "]");
				throw new NonExecutableDocumentException();
			}
			if (!correctRoles.contains(roleName)) {
				logger.error("Role [" + roleName + "] is not a valid role for executing document with id = [" + obj.getId() + "] for user [" + ((UserProfile) profile).getUserName() + "]");
				throw new NonExecutableDocumentException();
			}

			// reload BIObjectParameter in execution modality
			BIObjectParameter biParameter = null;
			obj = DAOFactory.getBIObjectDAO().loadBIObjectForExecutionByIdAndRole(obj.getId(), roleName);
			List biparameters = obj.getBiObjectParameters();
			Iterator biparametersIt = biparameters.iterator();
			while (biparametersIt.hasNext()) {
				BIObjectParameter aDocParameter = (BIObjectParameter) biparametersIt.next();
				if (aDocParameter.getId().equals(documentParameterId)) {
					biParameter = aDocParameter;
					break;
				}
			}

			Parameter par = biParameter.getParameter();
			ModalitiesValue paruse = par.getModalityValue();
			if (paruse.getITypeCd().equals("MAN_IN")) {
				logger.debug("Document parameter is manual input. An empty HashMap will be returned.");
			} else {
				String lovResult = biParameter.getLovResult();
				if (lovResult == null) {
					String lovprov = paruse.getLovProvider();
					ILovDetail lovDetail = LovDetailFactory.getLovFromXML(lovprov);
					lovResult = lovDetail.getLovResult(profile);
					LovResultHandler lovResultHandler = new LovResultHandler(lovResult);
					biParameter.setLovResult(lovResult);
					List rows = lovResultHandler.getRows();
					Iterator it = rows.iterator();
					while (it.hasNext()) {
						SourceBean row = (SourceBean) it.next();
						String value = (String) row.getAttribute(lovDetail.getValueColumnName());
						String description = (String) row.getAttribute(lovDetail.getDescriptionColumnName());
						values.put(value, description);
					}
				}
			}
		} catch(NonExecutableDocumentException e) {
			throw e;
		} catch(Exception e) {
			logger.error(e);
		}
		logger.debug("OUT");
		return values;
	}

	public String[] getCorrectRolesForExecution(Integer documentId) throws NonExecutableDocumentException {
		String[] toReturn = null;
		logger.debug("IN: documentId = [" + documentId + "]");
		try {
			IEngUserProfile profile = getUserProfile();
			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(documentId);
			if (!ObjectsAccessVerifier.canSee(obj, profile)) {
				logger.error("User [" + ((UserProfile) profile).getUserName() + "] cannot execute document with id = [" + documentId + "]");
				throw new NonExecutableDocumentException();
			}
			List correctRoles = ObjectsAccessVerifier.getCorrectRolesForExecution(documentId, profile);
			if (correctRoles != null) {
				toReturn = new String[correctRoles.size()];
				toReturn = (String[]) correctRoles.toArray(toReturn);
			} else {
				toReturn = new String[0];
			}
		} catch(NonExecutableDocumentException e) {
			throw e;
		} catch(Exception e) {
			logger.error(e);
		}
		logger.debug("OUT");
		return toReturn;
	}

	public SDKDocumentParameter[] getDocumentParameters(Integer documentId, String roleName) throws NonExecutableDocumentException {
		SDKDocumentParameter parameters[] = null;
		logger.debug("IN: documentId = [" + documentId + "]; roleName = [" + roleName + "]");
		try {
			IEngUserProfile profile = getUserProfile();
			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(documentId);
			if (!ObjectsAccessVerifier.canSee(obj, profile)) {
				logger.error("User [" + ((UserProfile) profile).getUserName() + "] cannot execute document with id = [" + documentId + "]");
				throw new NonExecutableDocumentException();
			}
			List correctRoles = ObjectsAccessVerifier.getCorrectRolesForExecution(documentId, profile);
			if (correctRoles == null || correctRoles.size() == 0) {
				logger.error("User [" + ((UserProfile) profile).getUserName() + "] has no roles to execute document with id = [" + documentId + "]");
				throw new NonExecutableDocumentException();
			}
			if (!correctRoles.contains(roleName)) {
				logger.error("Role [" + roleName + "] is not a valid role for executing document with id = [" + documentId + "] for user [" + ((UserProfile) profile).getUserName() + "]");
				throw new NonExecutableDocumentException();
			}

			obj = DAOFactory.getBIObjectDAO().loadBIObjectForExecutionByIdAndRole(obj.getId(), roleName);
			List parametersList = obj.getBiObjectParameters();
			List toReturn = new ArrayList();
			if (parametersList != null) {
				SDKDocumentParameter aDocParameter;
				Iterator it = parametersList.iterator();
				while (it.hasNext()) {
					BIObjectParameter parameter = (BIObjectParameter)it.next();
					aDocParameter = new SDKObjectsConverter().fromBIObjectParameterToSDKDocumentParameter(parameter);
					toReturn.add(aDocParameter);
				}
			}
			parameters = new SDKDocumentParameter[toReturn.size()];
			parameters = (SDKDocumentParameter[]) toReturn.toArray(parameters);
		} catch(NonExecutableDocumentException e) {
			throw e;
		} catch(Exception e) {
			logger.error(e);
		}
		logger.debug("OUT");
		return parameters;
	}

	public SDKDocument[] getDocumentsAsList(String type, String state, String folderPath) {
		SDKDocument documents[] = null;
		logger.debug("IN");
		try {
			IEngUserProfile profile = getUserProfile();
			List list = DAOFactory.getBIObjectDAO().loadBIObjects(type, state, folderPath);
			List toReturn = new ArrayList();
			if(list != null) {
				for(Iterator it = list.iterator(); it.hasNext();) {
					BIObject obj = (BIObject)it.next();
					if(ObjectsAccessVerifier.canSee(obj, profile)) {
						SDKDocument aDoc = new SDKObjectsConverter().fromBIObjectToSDKDocument(obj);
						toReturn.add(aDoc);
					}
				}
			}
			documents = new SDKDocument[toReturn.size()];
			documents = (SDKDocument[])toReturn.toArray(documents);
		} catch(Exception e) {
			logger.error("Error while loading documents as list", e);
		}
		logger.debug("OUT");
		return documents;
	}

	public SDKFunctionality getDocumentsAsTree(String initialPath) {
		logger.debug("IN: initialPath = [" + initialPath + "]");
		SDKFunctionality toReturn = null;
		try {
			IEngUserProfile profile = getUserProfile();
			ILowFunctionalityDAO functionalityDAO = DAOFactory.getLowFunctionalityDAO();
			LowFunctionality initialFunctionality = null;
			if (initialPath == null || initialPath.trim().equals("")) {
				// loading root functionality, everybody can see it
				initialFunctionality = functionalityDAO.loadRootLowFunctionality(false);
			} else {
				initialFunctionality = functionalityDAO.loadLowFunctionalityByPath(initialPath, false);
			}
			boolean canSeeFunctionality = ObjectsAccessVerifier.canSee(initialFunctionality, profile);
			if (canSeeFunctionality) {
				toReturn = new SDKObjectsConverter().fromLowFunctionalityToSDKFunctionality(initialFunctionality);
				setFunctionalityContent(toReturn);
			}
		} catch(Exception e) {
			logger.error("Error while loading documents as tree", e);
		}
		logger.debug("OUT");
		return toReturn;
	}

	private void setFunctionalityContent(SDKFunctionality parentFunctionality) throws Exception {
		logger.debug("IN");
		IEngUserProfile profile = getUserProfile();
		// loading contained documents
		List containedBIObjects = DAOFactory.getBIObjectDAO().loadBIObjects(parentFunctionality.getId(), profile, false);
		List visibleDocumentsList = new ArrayList();
		if (containedBIObjects != null && containedBIObjects.size() > 0) {
			for (Iterator it = containedBIObjects.iterator(); it.hasNext();) {
				BIObject obj = (BIObject) it.next();
				if (ObjectsAccessVerifier.checkProfileVisibility(obj, profile)) {
					SDKDocument aDoc = new SDKObjectsConverter().fromBIObjectToSDKDocument(obj);
					visibleDocumentsList.add(aDoc);
				}
			}
		}
		SDKDocument[] containedDocuments = new SDKDocument[visibleDocumentsList.size()];
		containedDocuments = (SDKDocument[]) visibleDocumentsList.toArray(containedDocuments);
		parentFunctionality.setContainedDocuments(containedDocuments);

		// loading contained functionalities
		List containedFunctionalitiesList = DAOFactory.getLowFunctionalityDAO().loadChildFunctionalities(parentFunctionality.getId(), false);
		List visibleFunctionalitiesList = new ArrayList();
		for (Iterator it = containedFunctionalitiesList.iterator(); it.hasNext();) {
			LowFunctionality lowFunctionality = (LowFunctionality) it.next();
			boolean canSeeFunctionality = ObjectsAccessVerifier.canSee(lowFunctionality, profile);
			if (canSeeFunctionality) {
				SDKFunctionality childFunctionality = new SDKObjectsConverter().fromLowFunctionalityToSDKFunctionality(lowFunctionality);
				visibleFunctionalitiesList.add(childFunctionality);
				// recursion
				setFunctionalityContent(childFunctionality);
			}
		}
		SDKFunctionality[] containedFunctionalities = new SDKFunctionality[visibleFunctionalitiesList.size()];
		containedFunctionalities = (SDKFunctionality[]) visibleFunctionalitiesList.toArray(containedFunctionalities);
		parentFunctionality.setContainedFunctionalities(containedFunctionalities);
		logger.debug("OUT");
	}

	public Integer saveNewDocument(SDKDocument document, SDKTemplate sdkTemplate,
			Integer functionalityId) throws NotAllowedOperationException {
		logger.debug("IN");
		Integer toReturn = null;
		try {
			IEngUserProfile profile = getUserProfile();
			// if user cannot develop in the specified folder, he cannot save documents inside it
			if (!ObjectsAccessVerifier.canDev(functionalityId, profile)) {
				NotAllowedOperationException e = new NotAllowedOperationException();
				e.setFaultString("User cannot save new documents in the specified folder since he hasn't development permission.");
				throw e;
			}
			BIObject obj = new SDKObjectsConverter().fromSDKDocumentToBIObject(document);
			String userId = ((UserProfile) profile).getUserId().toString();
			logger.debug("Current user id is [" + userId + "]");
			obj.setCreationUser(((UserProfile) profile).getUserId().toString());
			obj.setCreationDate(new Date());
			obj.setVisible(new Integer(1));
			List functionalities = new ArrayList();
			functionalities.add(functionalityId);
			obj.setFunctionalities(functionalities);

			ObjTemplate objTemplate = null;
			if (sdkTemplate != null) {
				objTemplate = new SDKObjectsConverter().fromSDKTemplateToObjTemplate(sdkTemplate);
				objTemplate.setActive(new Boolean(true));
				objTemplate.setCreationUser(userId);
				objTemplate.setCreationDate(new Date());
			}

			logger.debug("Saving document ...");
			DAOFactory.getBIObjectDAO().insertBIObject(obj, objTemplate);
			toReturn = obj.getId();
			if (toReturn != null) {
				logger.info("Document saved with id = " + toReturn);
			} else {
				logger.error("Document not saved!!");
			}
		} catch(Exception e) {
			logger.error("Error while saving new document", e);
		}
		logger.debug("OUT");
		return toReturn;
	}

	public void uploadTemplate(Integer documentId, SDKTemplate sdkTemplate)
	throws NotAllowedOperationException {
		logger.debug("IN: documentId = [" + documentId + "]; template file name = [" + sdkTemplate.getFileName() + "]");
		try {
			IEngUserProfile profile = getUserProfile();
			// if user cannot develop the specified document, he cannot upload templates on it
			if (!ObjectsAccessVerifier.canDevBIObject(documentId, profile)) {
				NotAllowedOperationException e = new NotAllowedOperationException();
				e.setFaultString("User cannot upload templates on specified document since he cannot develop it.");
				throw e;
			}
			ObjTemplate objTemplate = new SDKObjectsConverter().fromSDKTemplateToObjTemplate(sdkTemplate);
			objTemplate.setBiobjId(documentId);
			objTemplate.setActive(new Boolean(true));
			String userId = ((UserProfile) profile).getUserId().toString();
			logger.debug("Current user id is [" + userId + "]");
			objTemplate.setCreationUser(userId);
			objTemplate.setCreationDate(new Date());
			logger.debug("Saving template....");
			DAOFactory.getObjTemplateDAO().insertBIObjectTemplate(objTemplate);
			logger.debug("Template stored without errors.");
		} catch(Exception e) {
			logger.error("Error while uploading template", e);
		}
		logger.debug("OUT");
	}

	public SDKTemplate downloadTemplate(Integer documentId) throws NotAllowedOperationException {
		logger.debug("IN");
		SDKTemplate toReturn = null;
		try {
			IEngUserProfile profile = getUserProfile();
			// if user cannot develop the specified document, he cannot upload templates on it
			if (!ObjectsAccessVerifier.canDevBIObject(documentId, profile)) {
				NotAllowedOperationException e = new NotAllowedOperationException();
				e.setFaultString("User cannot download templates of specified document since he cannot develop it.");
				throw e;
			}
			// retrieves template
			IObjTemplateDAO tempdao = DAOFactory.getObjTemplateDAO();
			ObjTemplate temp = tempdao.getBIObjectActiveTemplate(documentId);
			if (temp == null) {
				logger.warn("The template dor document [" + documentId + "] is NULL");
				return null;
			}
			logger.debug("Template dor document [" + documentId + "] retrieved: file name is [" + temp.getName() + "]");
			toReturn = new SDKObjectsConverter().fromObjTemplateToSDKTemplate(temp);
		} catch(Exception e) {
			logger.error(e);
		}
		logger.debug("OUT");
		return toReturn;
	}


	private SDKExecutedDocumentContent executeKpi(SDKDocument document, BIObject biobj, String userId,String ouputType){
		logger.debug("IN");
		SDKExecutedDocumentContent toReturn=null;
		SourceBean request = null;
		SourceBean resp = null;
		EMFErrorHandler errorHandler = null;

		try {
			request = new SourceBean("");
			resp = new SourceBean("");
		} catch (SourceBeanException e1) {
			logger.error("Source Bean Exception");
			return null;
		}
		RequestContainer reqContainer = new RequestContainer();
		ResponseContainer resContainer = new ResponseContainer();
		reqContainer.setServiceRequest(request);
		resContainer.setServiceResponse(resp);
		DefaultRequestContext defaultRequestContext = new DefaultRequestContext(
				reqContainer, resContainer);
		resContainer.setErrorHandler(new EMFErrorHandler());
		RequestContainer.setRequestContainer(reqContainer);
		ResponseContainer.setResponseContainer(resContainer);
		SessionContainer session = new SessionContainer(true);
		reqContainer.setSessionContainer(session);
		errorHandler = defaultRequestContext.getErrorHandler();

		Engine engine;
		try {
			engine = DAOFactory.getEngineDAO().loadEngineByID(document.getEngineId());
		} catch (EMFUserError e1) {
			logger.error("Error while retrieving engine", e1);
			return null;
		}
		if(engine==null){
			logger.error("No engine found");
			return null;
		}
		String className = engine.getClassName();
		logger.debug("Try instantiating class " + className
				+ " for internal engine " + engine.getName() + "...");
		InternalEngineIFace internalEngine = null;
		// tries to instantiate the class for the internal engine
		try {
			if (className == null && className.trim().equals("")) throw new ClassNotFoundException();
			internalEngine = (InternalEngineIFace) Class.forName(className).newInstance();
		} catch (ClassNotFoundException cnfe) {
			logger.error("The class ['" + className
					+ "'] for internal engine " + engine.getName()
					+ " was not found.", cnfe);
			return null;
		} catch (Exception e) {
			logger.error("Error while instantiating class " + className, e);
			return null;
		}

		// result of the Kpi
		List<KpiResourceBlock> blocksList=null;
		try {
			blocksList=((SpagoBIKpiInternalEngine)internalEngine).executeCode(reqContainer, biobj, resp, userId);
			if(blocksList==null){
				logger.error("No result returned by kpi execution");
				return null;
			}
			else{
				logger.debug("Kpi executed and result returned");
			}
		} catch (EMFUserError e) {
			logger.error("Error during engine execution", e);
			return null;		
		} catch (Exception e) {
			logger.error("Error while engine execution", e);
			return null;
		}

		File tmpFile=null;
		String mimeType = "application/pdf";
		logger.debug("setting object to return of type SDKExecuteDocumentContent");
		toReturn=new SDKExecutedDocumentContent();
		// call exporter!
		try{
			KpiExporter exporter=new KpiExporter();
			if(ouputType.equals("PDF")){
				logger.debug("call PDF Exporter");
				tmpFile=exporter.getKpiReportPDF(blocksList, biobj, userId);
				toReturn.setFileName(biobj.getLabel()+".pdf");
			}else if (ouputType.equals("XML")){
				mimeType = "text/xml";
				logger.debug("call XML Exporter");
				tmpFile=exporter.getKpiExportXML(blocksList, biobj, userId);
				toReturn.setFileName(biobj.getLabel()+".xml");
			}
		}
		catch (Exception e) {
			logger.error("error while exporting",e);
			return null;
		}
		if(tmpFile==null){
			logger.error("file not created");
			return null;
		}
		else{
			logger.debug("file created");
		}

		try{
			FileDataSource mods = new FileDataSource(tmpFile);		
			toReturn.setFileType(mimeType);
			DataHandler dhSource = new DataHandler(mods);
			toReturn.setContent(dhSource);
		}

		finally{
			logger.debug("deleting file Tmp");
			logger.debug("file Tmp deleted");
		}
		logger.debug("OUT");
		return toReturn;

	}


	private SDKExecutedDocumentContent executeReport(SDKDocument document, BIObject biobj, IEngUserProfile profile, String output){

		logger.debug("IN");
		SDKExecutedDocumentContent toReturn = null;
		
		try {
			
			ReportExporter jse = new ReportExporter();
			File tmpFile = jse.getReport(biobj, profile, output);
			if (tmpFile == null) {
				logger.error("File returned from exporter is NULL!");
				return null;
			}
	
			logger.debug("setting object to return of type SDKExecuteDocumentContent");
			toReturn = new SDKExecutedDocumentContent();
			FileDataSource mods = new FileDataSource(tmpFile);
			DataHandler dataHandler = new DataHandler(mods);
			toReturn.setContent(dataHandler);
			String fileExtension = FileUtils.getFileExtension(tmpFile);
			String fileName = null;
			if (fileExtension != null && !fileExtension.trim().equals("")) {
				fileName = biobj.getLabel() + "." + fileExtension;
			} else {
				fileName = biobj.getLabel();
			}
			String mimeType = MimeUtils.getMimeType(tmpFile);
			logger.debug("Produced file name is " + fileName);
			logger.debug("Produced file mimetype is " + mimeType);
			toReturn.setFileName(fileName);
			toReturn.setFileType(mimeType);
			DataHandler dhSource = new DataHandler(mods);
			toReturn.setContent(dhSource);

		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}

	/** Executes a document and return an object containing the result
	 * @param: document : the document
	 * @param: parameters: ana array of SDKDocumentParameters, already filled with values
	 * @param: roleName : name of the role
	 */

	public SDKExecutedDocumentContent executeDocument(SDKDocument document, SDKDocumentParameter[] parameters, String roleName, String outputType)
	throws NonExecutableDocumentException, NotAllowedOperationException,MissingParameterValue,InvalidParameterValue {
		logger.debug("IN");
		String output = (outputType != null && !outputType.equals("")) ? outputType : "PDF";
		SDKExecutedDocumentContent toReturn = null;

		IEngUserProfile profile = null;

		Integer idDocument=document.getId();

		try{
			profile= getUserProfile();
		}
		catch (Exception e) {
			logger.error("could not retrieve profile",e);
			throw new NonExecutableDocumentException();
			}

		ExecutionInstance instance =null;
		try{
			instance = new ExecutionInstance(profile, "111", "111", idDocument, roleName, SpagoBIConstants.SDK_EXECUTION_SERVICE, false, false);
		}
		catch (Exception e) {
			logger.error("error while creating instance",e);
			throw new NonExecutableDocumentException();
		}
		// put the parameters value in SDKPArameters into BiObject
		instance.refreshBIObjectWithSDKParameters(parameters);

//		check if there were errors referring to parameters

		List errors=null;
		try{
			errors=instance.getParametersErrors();
		}
		catch (Exception e) {
			logger.error("error while retrieving parameters errors",e);
			throw new NonExecutableDocumentException();
		}
		if(errors!=null && errors.size()>0){
			for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
				Object error = (Object) iterator.next();
				if(error instanceof EMFUserError){
					EMFUserError emfUser=(EMFUserError)error;
					String message="Error on parameter values ";
					if(emfUser.getMessage()!=null) message+=" "+emfUser.getMessage();
					if(emfUser.getAdditionalInfo()!=null) message+=" "+emfUser.getAdditionalInfo();
					logger.error(message);
					throw new MissingParameterValue();
				}
				else if(error instanceof EMFValidationError){
					EMFValidationError emfValidation=(EMFValidationError)error;
					String message="Error while checking parameters: ";
					if(emfValidation.getMessage()!=null) message+=" "+emfValidation.getMessage();
					if(emfValidation.getAdditionalInfo()!=null) message+=" "+emfValidation.getAdditionalInfo();
					logger.error(message);
					throw new InvalidParameterValue();

				}


			}
		}


		logger.debug("Check the document type and call the exporter (if present)");
		try {

			if ( document.getType().equalsIgnoreCase("KPI")) {  // CASE KPI
				toReturn = executeKpi(document, instance.getBIObject(), (String)profile.getUserUniqueIdentifier(), output);
			} else if (document.getType().equalsIgnoreCase("REPORT") || document.getType().equalsIgnoreCase("ACCESSIBLE_HTML")){  // CASE REPORT OR ACCESSIBLE_HTML
				toReturn = executeReport(document, instance.getBIObject(), profile, output);					
			} else {
				logger.error("NO EXPORTER AVAILABLE");
			}

		} catch(Exception e) {
			logger.error("Error while executing document");
			throw new NonExecutableDocumentException();
		}

		if(toReturn==null){
			logger.error("No result returned by the document");
			throw new NonExecutableDocumentException();
		}

		logger.debug("OUT");
		return toReturn;
	}

	public SDKDocument getDocumentById(Integer id) {
		SDKDocument toReturn = null;
        logger.debug("IN: document in input = " + id);
        try {
        	super.checkUserPermissionForFunctionality(SpagoBIConstants.DOCUMENT_MANAGEMENT, "User cannot see documents congifuration.");
        	if (id == null) {
	        	logger.warn("Document identifier in input is null!");
	        	return null;
	        }
        	BIObject biObject = DAOFactory.getBIObjectDAO().loadBIObjectById(id);
        	if (biObject == null) {
        		logger.warn("BiObject with identifier [" + id + "] not existing.");
        		return null;
        	}
        	toReturn = new SDKObjectsConverter().fromBIObjectToSDKDocument(biObject);
        } catch(NotAllowedOperationException e) {
        	
        } catch(Exception e) {
            logger.error("Error while retrieving SDKEngine list", e);
            logger.debug("Returning null");
            return null;
        } finally {
        	logger.debug("OUT");
        }
        return toReturn;
	}

	public SDKDocument getDocumentByLabel(String label) {
		SDKDocument toReturn = null;
        logger.debug("IN: document in input = " + label);
        try {
        	super.checkUserPermissionForFunctionality(SpagoBIConstants.DOCUMENT_MANAGEMENT, "User cannot see documents congifuration.");
        	if (label == null) {
	        	logger.warn("Document label in input is null!");
	        	return null;
	        }
        	BIObject biObject = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(label);
        	if (biObject == null) {
        		logger.warn("BiObject with label [" + label + "] not existing.");
        		return null;
        	}
        	toReturn = new SDKObjectsConverter().fromBIObjectToSDKDocument(biObject);
        } catch(NotAllowedOperationException e) {
        	
        } catch(Exception e) {
            logger.error("Error while retrieving SDKEngine list", e);
            logger.debug("Returning null");
            return null;
        } finally {
        	logger.debug("OUT");
        }
        return toReturn;
	}






}
