/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
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
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
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
import it.eng.spagobi.sdk.utilities.SDKObjectsConverter.MemoryOnlyDataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.file.FileUtils;
import it.eng.spagobi.utilities.mime.MimeUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;


public class DocumentsServiceImpl extends AbstractSDKService implements DocumentsService {

	public static final String DATAMART_FILE_NAME = "datamart.jar";
	public static final String CFIELDS_FILE_NAME = "cfields_meta.xml";

	static private Logger logger = Logger.getLogger(DocumentsServiceImpl.class);

	public HashMap getAdmissibleValues(Integer documentParameterId, String roleName) throws NonExecutableDocumentException {
		HashMap values = new HashMap<String, String>();
		logger.debug("IN: documentParameterId = [" + documentParameterId + "]; roleName = [" + roleName + "]");
		
		this.setTenant();
		
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
				String lovprov = paruse.getLovProvider();
				ILovDetail lovDetail = LovDetailFactory.getLovFromXML(lovprov);
				String lovResult = lovDetail.getLovResult(profile, null, null);
				LovResultHandler lovResultHandler = new LovResultHandler(lovResult);
				List rows = lovResultHandler.getRows();
				Iterator it = rows.iterator();
				while (it.hasNext()) {
					SourceBean row = (SourceBean) it.next();
					String value = (String) row.getAttribute(lovDetail.getValueColumnName());
					String description = (String) row.getAttribute(lovDetail.getDescriptionColumnName());
					values.put(value, description);
				}
			}
		} catch(NonExecutableDocumentException e) {
			throw e;
		} catch(Exception e) {
			logger.error(e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return values;
	}

	public String[] getCorrectRolesForExecution(Integer documentId) throws NonExecutableDocumentException {
		String[] toReturn = null;
		logger.debug("IN: documentId = [" + documentId + "]");
		
		this.setTenant();
		
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
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return toReturn;
	}

	public SDKDocumentParameter[] getDocumentParameters(Integer documentId, String roleName) throws NonExecutableDocumentException {
		SDKDocumentParameter parameters[] = null;
		logger.debug("IN: documentId = [" + documentId + "]; roleName = [" + roleName + "]");
		
		this.setTenant();
		
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
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return parameters;
	}

	public SDKDocument[] getDocumentsAsList(String type, String state, String folderPath) {
		SDKDocument documents[] = null;
		logger.debug("IN");
		
		this.setTenant();
		
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
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return documents;
	}

	public SDKFunctionality getDocumentsAsTree(String initialPath) {
		logger.debug("IN: initialPath = [" + initialPath + "]");
		SDKFunctionality toReturn = null;
		
		this.setTenant();
		
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
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
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
		
		this.setTenant();
		
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
			IBIObjectDAO biObjDAO = DAOFactory.getBIObjectDAO();
			biObjDAO.setUserProfile(profile);
			biObjDAO.insertBIObject(obj, objTemplate);
			toReturn = obj.getId();
			if (toReturn != null) {
				logger.info("Document saved with id = " + toReturn);
			} else {
				logger.error("Document not saved!!");
			}
		} catch(Exception e) {
			logger.error("Error while saving new document", e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return toReturn;
	}

	public void uploadTemplate(Integer documentId, SDKTemplate sdkTemplate) throws NotAllowedOperationException {
		logger.debug("IN: documentId = [" + documentId + "]; template file name = [" + sdkTemplate.getFileName() + "]");
		
		this.setTenant();
		
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
			IObjTemplateDAO tempDAO = DAOFactory.getObjTemplateDAO();
			tempDAO.setUserProfile(profile);
			tempDAO.insertBIObjectTemplate(objTemplate);
			logger.debug("Template stored without errors.");
		} catch(Exception e) {
			logger.error("Error while uploading template", e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
	}

	public SDKTemplate downloadTemplate(Integer documentId) throws NotAllowedOperationException {
		logger.debug("IN");
		SDKTemplate toReturn = null;
		
		this.setTenant();
		
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
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
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

	public SDKExecutedDocumentContent executeDocument(SDKDocument document,
			SDKDocumentParameter[] parameters, String roleName,
			String outputType) throws NonExecutableDocumentException,
			NotAllowedOperationException, MissingParameterValue,
			InvalidParameterValue {
		logger.debug("IN");
		SDKExecutedDocumentContent toReturn = null;
		
		this.setTenant();
		
		try {
		
			String output = (outputType != null && !outputType.equals("")) ? outputType : "PDF";
			

			IEngUserProfile profile = null;

			Integer idDocument = document.getId();

			try {
				profile = getUserProfile();
			} catch (Exception e) {
				logger.error("could not retrieve profile", e);
				throw new NonExecutableDocumentException();
			}

			ExecutionInstance instance = null;
			try {
				instance = new ExecutionInstance(profile, "111", "111",
						idDocument, roleName,
						SpagoBIConstants.SDK_EXECUTION_SERVICE, false, false,
						null);
			} catch (Exception e) {
				logger.error("error while creating instance", e);
				throw new NonExecutableDocumentException();
			}
			// put the parameters value in SDKPArameters into BiObject
			instance.refreshBIObjectWithSDKParameters(parameters);

			// check if there were errors referring to parameters

			List errors = null;
			try {
				errors = instance.getParametersErrors();
			} catch (Exception e) {
				logger.error("error while retrieving parameters errors", e);
				throw new NonExecutableDocumentException();
			}
			if (errors != null && errors.size() > 0) {
				for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
					Object error = (Object) iterator.next();
					if (error instanceof EMFUserError) {
						EMFUserError emfUser = (EMFUserError) error;
						String message = "Error on parameter values ";
						if (emfUser.getMessage() != null)
							message += " " + emfUser.getMessage();
						if (emfUser.getAdditionalInfo() != null)
							message += " " + emfUser.getAdditionalInfo();
						logger.error(message);
						throw new MissingParameterValue();
					} else if (error instanceof EMFValidationError) {
						EMFValidationError emfValidation = (EMFValidationError) error;
						String message = "Error while checking parameters: ";
						if (emfValidation.getMessage() != null)
							message += " " + emfValidation.getMessage();
						if (emfValidation.getAdditionalInfo() != null)
							message += " " + emfValidation.getAdditionalInfo();
						logger.error(message);
						throw new InvalidParameterValue();

					}

				}
			}

			logger.debug("Check the document type and call the exporter (if present)");
			try {

				if (document.getType().equalsIgnoreCase("KPI")) { // CASE KPI
					toReturn = executeKpi(document, instance.getBIObject(),
							(String) profile.getUserUniqueIdentifier(), output);
				} else if (document.getType().equalsIgnoreCase("REPORT")
						|| document.getType().equalsIgnoreCase(
								"ACCESSIBLE_HTML")) { // CASE REPORT OR
														// ACCESSIBLE_HTML
					toReturn = executeReport(document, instance.getBIObject(),
							profile, output);
				} else {
					logger.error("NO EXPORTER AVAILABLE");
				}

			} catch (Exception e) {
				logger.error("Error while executing document");
				throw new NonExecutableDocumentException();
			}

			if (toReturn == null) {
				logger.error("No result returned by the document");
				throw new NonExecutableDocumentException();
			}

		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		
		return toReturn;
	}

	public SDKDocument getDocumentById(Integer id) {
		SDKDocument toReturn = null;
		logger.debug("IN: document in input = " + id);

		this.setTenant();

		try {
			super.checkUserPermissionForFunctionality(
					SpagoBIConstants.DOCUMENT_MANAGEMENT,
					"User cannot see documents congifuration.");
			if (id == null) {
				logger.warn("Document identifier in input is null!");
				return null;
			}
			BIObject biObject = DAOFactory.getBIObjectDAO()
					.loadBIObjectById(id);
			if (biObject == null) {
				logger.warn("BiObject with identifier [" + id
						+ "] not existing.");
				return null;
			}
			toReturn = new SDKObjectsConverter()
					.fromBIObjectToSDKDocument(biObject);
		} catch (Exception e) {
			logger.error("Error while retrieving document", e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return toReturn;
	}

	public SDKDocument getDocumentByLabel(String label) {
		SDKDocument toReturn = null;
		logger.debug("IN: document in input = " + label);

		this.setTenant();

		try {
			super.checkUserPermissionForFunctionality(
					SpagoBIConstants.DOCUMENT_MANAGEMENT,
					"User cannot see documents congifuration.");
			if (label == null) {
				logger.warn("Document label in input is null!");
				return null;
			}
			BIObject biObject = DAOFactory.getBIObjectDAO()
					.loadBIObjectByLabel(label);
			if (biObject == null) {
				logger.warn("BiObject with label [" + label + "] not existing.");
				return null;
			}
			toReturn = new SDKObjectsConverter()
					.fromBIObjectToSDKDocument(biObject);
		} catch (Exception e) {
			logger.error("Error while retrieving document", e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return toReturn;
	}


	public void uploadDatamartTemplate(SDKTemplate sdkTemplate,
			SDKTemplate calculatedFields, String dataSourceLabel) {
		logger.debug("IN: template file name = [" + sdkTemplate.getFileName()
				+ "] and optional calculatedFields file [" + calculatedFields
				+ "]");

		this.setTenant();

		try {

			/***********************************************************************************************************/
			/* STEP 1: uploads the datamart document */
			/***********************************************************************************************************/
			try {
				uploadFisicalFile(sdkTemplate, DATAMART_FILE_NAME);
				logger.debug("datamart.jar file uploaded");
			} catch (Exception e) {
				logger.error("Could not upload datamart.jar file", e);
				throw new SpagoBIRuntimeException(
						"Could not upload datamart.jar file: " + e.getMessage());
			}

			try {
				/***********************************************************************************************************/
				/* STEP 1,5: if present uploads also the calculatedFields xml */
				/***********************************************************************************************************/
				if (calculatedFields.getContent() != null) {
					logger.debug("Upload calculatedFields xml: cfields.xml ");
					uploadFisicalFile(calculatedFields, CFIELDS_FILE_NAME);
					logger.debug("cfields.xml file uploaded");
				} else {
					logger.debug("No cfields xml recevied");
				}
			} catch (Exception e) {
				logger.error("Could not upload cfields file", e);
				throw new SpagoBIRuntimeException(
						"Could not upload cfieldds.xml file: " + e.getMessage());
			}

			try {

				/***********************************************************************************************************/
				/*
				 * STEP 2: template creation in SpagoBI Metadata (under the
				 * personal folder) to use the previous datamart.
				 */
				/***********************************************************************************************************/
				BIObject obj = null;
				String datamartName = sdkTemplate.getFolderName();

				// checks if the template already exists. In this case doesn't
				// create the new one!
				obj = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(
						datamartName);
				if (obj != null) {
					logger.info("The datamart with name "
							+ datamartName
							+ " is already been inserted in SpagoBI. Template not loaded! ");
					return;
				}

				IEngUserProfile profile = getUserProfile();

				obj = new BIObject();
				String userId = ((UserProfile) profile).getUserId().toString();
				logger.debug("Current user id is [" + userId + "]");

				obj.setCreationUser(((UserProfile) profile).getUserId()
						.toString());
				obj.setCreationDate(new Date());
				obj.setVisible(new Integer(1));
				obj.setLabel(datamartName);
				obj.setName(datamartName);
				obj.setDescription("");
				obj.setEncrypt(0);
				obj.setStateCode("DEV");
				Domain state = DAOFactory.getDomainDAO()
						.loadDomainByCodeAndValue("STATE", "DEV");
				obj.setStateID(state.getValueId());
				// sets the qbe engine
				Domain objectType = DAOFactory.getDomainDAO()
						.loadDomainByCodeAndValue("BIOBJ_TYPE",
								SpagoBIConstants.DATAMART_TYPE_CODE);
				obj.setBiObjectTypeID(objectType.getValueId());
				obj.setBiObjectTypeCode(objectType.getValueCd());
				List<Engine> lstQbeEngines = DAOFactory.getEngineDAO()
						.loadAllEnginesForBIObjectType(
								SpagoBIConstants.DATAMART_TYPE_CODE);
				if (lstQbeEngines == null || lstQbeEngines.size() == 0) {
					logger.error("Error while retrieving Engine list.");
					return;
				}
				Engine qbeEngine = lstQbeEngines.get(0);
				obj.setEngine(qbeEngine);

				// get the dataSource if label is not null
				IDataSource dataSource = null;
				if (dataSourceLabel != null) {
					logger.debug("retrieve data source with label "
							+ dataSourceLabel);
					dataSource = DAOFactory.getDataSourceDAO()
							.loadDataSourceByLabel(dataSourceLabel);
					obj.setDataSourceId(dataSource.getDsId());
				}

				// sets the default functionality (personal folder).
				List functionalities = new ArrayList();
				LowFunctionality funct = null;
				funct = DAOFactory.getLowFunctionalityDAO()
						.loadLowFunctionalityByPath("/" + userId, false);
				if (funct != null) {
					functionalities.add(funct.getId());
					obj.setFunctionalities(functionalities);
				} else {
					// the personal folder doesn't exist yet. It creates it, and
					// uses it.
					UserUtilities.createUserFunctionalityRoot(profile);
					logger.error("Error while retrieving Functionality identifier.");
					funct = DAOFactory.getLowFunctionalityDAO()
							.loadLowFunctionalityByPath("/" + userId, false);
					functionalities.add(funct.getId());
					obj.setFunctionalities(functionalities);
				}
				// sets the template's content
				ObjTemplate objTemplate = new ObjTemplate();
				objTemplate.setActive(new Boolean(true));
				objTemplate.setCreationUser(userId);
				objTemplate.setCreationDate(new Date());
				objTemplate.setName(sdkTemplate.getFolderName() + ".xml");
				String template = getTemplate(datamartName);
				objTemplate.setContent(template.getBytes());

				// inserts the document
				logger.debug("Saving document ...");
				IBIObjectDAO biObjDAO = DAOFactory.getBIObjectDAO();
				biObjDAO.setUserProfile(profile);
				biObjDAO.insertBIObject(obj, objTemplate);
				Integer newIdObj = obj.getId();
				if (newIdObj != null) {
					logger.info("Document saved with id = " + newIdObj);
				} else {
					logger.error("Document not saved!!");
				}
			} catch (Exception e) {
				logger.error("Error while uploading template", e);
				throw new SpagoBIRuntimeException(
						"Error while uploading template");
			}

		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
	}

	public void uploadDatamartModel(SDKTemplate sdkTemplate) {
		logger.debug("IN: template file name = [" + sdkTemplate.getFileName()
				+ "]");
		
		this.setTenant();
		
		try {
			uploadFisicalFile(sdkTemplate, "");
		} catch (Exception e) {
			logger.error("Error while uploading model template", e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
	}

	public SDKTemplate downloadDatamartFile(String folderName, String fileName) {
		LogMF.debug(logger, "IN: folderName = [{0}], fileName = [{1}]", folderName, fileName);
		SDKTemplate toReturn = null;
		
		this.setTenant();
		
		try {
			FileInputStream isDatamartFile = downloadSingleFile(folderName, fileName);
			//defines a content to return
			byte[] templateContent = SpagoBIUtilities.getByteArrayFromInputStream(isDatamartFile);

			toReturn = new SDKTemplate();
			toReturn.setFileName(fileName);
			SDKObjectsConverter objConverter = new SDKObjectsConverter();
			MemoryOnlyDataSource mods = objConverter.new MemoryOnlyDataSource(templateContent, null);
			DataHandler dhSource = new DataHandler(mods);
			toReturn.setContent(dhSource);
		} catch(Exception e) {
			logger.error("Error downloading datamart file", e);
			logger.debug("Returning null");
			return null;
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		return toReturn;
	}


	//download a zip file with datamart.jar and modelfile
	public SDKTemplate downloadDatamartModelFiles(String folderName, String fileDatamartName , String fileModelName) {
		logger.debug("IN");

		this.setTenant();
		
		File file = null;
		FileOutputStream fileZip = null;
		ZipOutputStream zip = null;
		File inFileZip = null;

		try {
			// These are the files to include in the ZIP file
			String[] filenames = new String[]{fileDatamartName, fileModelName};
			String fileZipName  = folderName + ".zip";
			//String path = getResourcePath()  + System.getProperty("file.separator") + fileZipName;
			String path = System.getProperty("java.io.tmpdir") +  System.getProperty("file.separator") + fileZipName;;

			// Create the ZIP file
			file = new File(path);
			fileZip = new FileOutputStream(file);
			zip = new ZipOutputStream(fileZip);

			for (int i=0; i<filenames.length; i++) {
				if (filenames[i] != null && !filenames[i].equals("")){ 
					// Add ZIP entry to output stream.
					zip.putNextEntry(new ZipEntry(filenames[i]));

					FileInputStream in = downloadSingleFile(folderName, filenames[i]);
					zip.write(SpagoBIUtilities.getByteArrayFromInputStream(in));
					// Complete the entry
					zip.closeEntry();
					in.close();

				}
			}
			//writes the fisical file just created
			zip.close();
			fileZip.close();
			//reopen the zip file as input stream to save as SDKTemplate object because is not possible to convert
			//automatically an outputStream in inputStream
			inFileZip = new File(path);

			//creates the returned object
			SDKTemplate toReturn = new SDKTemplate();
			toReturn.setFileName(fileZipName);
			SDKObjectsConverter objConverter = new SDKObjectsConverter();
			MemoryOnlyDataSource mods = objConverter.new MemoryOnlyDataSource(new FileInputStream(inFileZip), null);
			DataHandler dhSource = new DataHandler(mods);
			toReturn.setContent(dhSource);

			logger.debug("OUT");
			return toReturn;

		} catch(Exception e) {
			logger.error("Error downloading datamart model file", e);
			logger.debug("Returning null");
			return null;
		} finally {
			this.unsetTenant();
			if (zip != null) {
				try {		
					zip.close();
				} catch (IOException e) {
					logger.error("Error closing output stream", e);
				}
			}
			if (fileZip != null) {
				try {		
					fileZip.close();
				} catch (IOException e) {
					logger.error("Error closing file output", e);
				}
			}
			if (inFileZip != null) {
				try {		
					if (!inFileZip.delete()) {
						inFileZip.deleteOnExit();
					}
				} catch (Exception e) {
					logger.error("Error deleting temporary input zip file", e);
				}
			}
			if (file != null) {
				try {		
					if (!file.delete()) {
						file.deleteOnExit();
					}
				} catch (Exception e) {
					logger.error("Error deleting temporary output zip file", e);
				}
			}
		}	
	}

	public HashMap<String, String> getAllDatamartModels() {
		logger.debug("IN");

		this.setTenant();

		HashMap<String, String> toReturn = new HashMap<String, String>();
		try {
			String pathDatatamartsDir = getResourcePath();
			File datamartsDir = new File(pathDatatamartsDir);
			File[] dirs = datamartsDir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					if (pathname.isDirectory()) {
						return true;
					}
					return false;
				}
			});
			if (dirs == null || dirs.length == 0) {
				throw new SpagoBIRuntimeException(
						"No datamarts found!! Check configuration for datamarts repository");
			}
			for (int i = 0; i < dirs.length; i++) {
				File dir = dirs[i];
				File[] models = dir.listFiles(new FileFilter() {
					public boolean accept(File file) {
						if (file.getName().endsWith(".sbimodel")) {
							return true;
						}
						return false;
					}
				});
				for (int j = 0; j < models.length; j++) {
					toReturn.put(dir.getName(), models[j].getName());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
		
		return toReturn;
	}

	private void uploadFisicalFile (SDKTemplate sdkTemplate, String defaultName) throws Exception{
		InputStream is = null;
		FileOutputStream osFile = null;
		DataHandler dh = null;

		try {	
			String fileName = sdkTemplate.getFolderName();

			// if user cannot develop the specified document, he cannot upload templates on it
			super.checkUserPermissionForFunctionality(SpagoBIConstants.DOCUMENT_MANAGEMENT, "User cannot see documents congifuration.");
			if (sdkTemplate == null) {
				logger.warn("SDKTemplate in input is null!");
				return;
			}

			//creates the folder correct (the name is given by the name of the file).
			String path = getResourcePath()  + System.getProperty("file.separator") + fileName;
			logger.debug("File path: " + path);
			File datamartFolder = new File (path);
			if (!datamartFolder.exists()){
				datamartFolder.mkdir();
			}
			path += System.getProperty("file.separator") + (sdkTemplate.getFileName() == null || sdkTemplate.getFileName().equals("")?defaultName:sdkTemplate.getFileName());
			File datamartFile = new File(path);
			logger.debug("File: " + path);
			if (!datamartFile.exists()){
				datamartFile.createNewFile();
			}
			osFile = new FileOutputStream(path);
			dh = sdkTemplate.getContent();
			is = dh.getInputStream();
			logger.debug("Upload file template....");
			byte[] templateContent = SpagoBIUtilities.getByteArrayFromInputStream(is);
			/* ----------- test code ---------
			String ss = new String(templateContent);
			System.out.println(ss);
 				----------- test code --------- */

			osFile.write(templateContent);
			logger.debug("Template uploaded without errors.");

		} catch(Exception e) {
			logger.error("Error while uploading template", e);
			throw e;
		} finally {
			if (is != null) {
				try {				
					is.close();
				} catch (IOException e) {
					logger.error("Error closing file input stream", e);
				}
			}
			if (osFile != null) {
				try {				
					osFile.close();
				} catch (IOException e) {
					logger.error("Error closing output stream", e);
				}
			}
		}
	}

	private FileInputStream downloadSingleFile(String folderName, String fileName) throws Exception{
		FileInputStream toReturn = null;

		try {
			// if user cannot develop the specified document, he cannot upload templates on it
			super.checkUserPermissionForFunctionality(SpagoBIConstants.DOCUMENT_MANAGEMENT, "User cannot see documents congifuration.");

			// retrieves template
			String path = getResourcePath()  + System.getProperty("file.separator") + folderName;
			logger.debug("Path: " + path);
			File folder = new File (path);
			if(!folder.exists()) {
				throw new RuntimeException("Folder [" + folder.getPath() + "] does not exist");
			}
			if(!folder.isDirectory()) {
				throw new RuntimeException("Folder [" + folder + "] is a file not a folder");
			}
			path += System.getProperty("file.separator") + fileName;
			File datamartFile = new File(path);
			logger.debug("File: " + path);
			if(!datamartFile.exists()) {
				throw new RuntimeException("File [" + datamartFile.getPath() + "] does not exist");
			}
			//check file content
			toReturn = new FileInputStream(path);
			if (toReturn == null) {
				logger.warn("The template for document [" + folderName + "] is NULL");
				return null;
			}


			logger.debug("Template for document [" + folderName + "] retrieved.");
		} catch(Exception e) {
			logger.error(e);
			throw e;
		}
		return toReturn;
	}

	private String getResourcePath() {

		String path = null;
		SourceBean pathSB;
		SingletonConfig configSingleton = SingletonConfig.getInstance();
		String jndiPath = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
		Assert.assertNotNull(jndiPath, "Impossible to find block [<SPAGOBI.RESOURCE_PATH_JNDI_NAME>] into configuration");
		//path = SpagoBIUtilities.readJndiResource(jndiPath) + System.getProperty("file.separator") + "qbe" + System.getProperty("file.separator") + "datamarts" ;
		path = SpagoBIUtilities.readJndiResource(jndiPath) + System.getProperty("file.separator") + "qbe";
		//checks if the 'qbe' folder exists. If not, it creates it.
		File datamartFolder = new File(path);
		if (!datamartFolder.exists()){
			datamartFolder.mkdir();
		}
		//checks if the 'datamarts' folder exists. If not, it creates it.
		path += System.getProperty("file.separator") + "datamarts";
		datamartFolder = new File(path);
		if (!datamartFolder.exists()){
			datamartFolder.mkdir();
		}

		//returns the complete path
		return path;
	}

	private String getTemplate(String datamartName) throws IOException {
		String template = "";
		template += "<QBE>\n";
		template += "\t<DATAMART name=\"" + datamartName + "\"/>\n";
		template += "</QBE>";

		return template;
	}

}
