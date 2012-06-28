/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.document.utils;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.service.DetailBIObjectModule;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.service.TreeObjectsModule;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

public class DetBIObjModHelper {
	static private Logger logger = Logger.getLogger(DetBIObjModHelper.class);
	SourceBean request = null;
	SourceBean response = null;
	RequestContainer reqCont = null;
	ResponseContainer respCont = null;
	IEngUserProfile profile=null;
	/**
	 * Instantiates a new det bi obj mod helper.
	 * 
	 * @param reqCont the req cont
	 * @param request the request
	 * @param response the response
	 */
	public DetBIObjModHelper(RequestContainer reqCont, ResponseContainer respCont, SourceBean request, SourceBean response) {
		this.request = request;
		this.response = response;
		this.reqCont = reqCont;
		this.respCont = respCont;
		SessionContainer session = reqCont.getSessionContainer();
		SessionContainer permanentSession = session.getPermanentContainer();
		profile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
	}
	
	
	/**
	 * Recover bi object details.
	 * 
	 * @param mod the mod
	 * 
	 * @return the bI object
	 * 
	 * @throws Exception the exception
	 */
	public BIObject recoverBIObjectDetails(String mod) throws Exception {
		// GET THE USER PROFILE

		//String userId=(String)profile.getUserUniqueIdentifier();
		String userId=(String)((UserProfile)profile).getUserId();
		// GET THE INITIAL PATH 
		String initialPath = (String)ChannelUtilities.getPreferenceValue(reqCont, TreeObjectsModule.PATH_SUBTREE, "");
		// CREATE AN EMPTY BIOBJECT
		BIObject obj = new BIObject();
		// RECOVER FROM REQUEST ALL THE DATA USEFUL TO BUILD A BIOBJECT 
		String idStr = (String) request.getAttribute("id");
		String name = (String) request.getAttribute("name");
		String label = (String) request.getAttribute("label");
		String description = (String) request.getAttribute("description");
		String relname = (String) request.getAttribute("relname");
		String criptableStr = (String) request.getAttribute("criptable");
		String visibleStr = (String) request.getAttribute("visible");
		String profiledVisibilityStr = (String) request.getAttribute("profileVisibility");
		// path is unused
		//String path = (String) request.getAttribute("path");
		String typeAttr = (String) request.getAttribute("type");
		String engineIdStr = (String) request.getAttribute("engine");
		String stateAttr = (String) request.getAttribute("state");
		String refreshSecondsString = (String) request.getAttribute("refreshseconds");
		if(refreshSecondsString==null || refreshSecondsString.equalsIgnoreCase(""))refreshSecondsString="0";
		Integer refreshSeconds=Integer.valueOf(refreshSecondsString);
		/*String longDescription = (String) request.getAttribute("longDescription");
		String objective = (String) request.getAttribute("objective");
		String language = (String) request.getAttribute("language");
		String Keywords = (String) request.getAttribute("Keywords");
		String Rating = (String) request.getAttribute("Rating");*/
		// ELABORATE DATA RECOVERED FROM REQUEST
		Integer id = null;
		if(idStr!=null) id = new Integer(idStr);
		if(criptableStr==null) criptableStr = "0";
		Integer encrypt = new Integer(criptableStr);
		if(visibleStr==null) visibleStr = "0";
		Integer visible = new Integer(visibleStr);
		StringTokenizer tokentype = new StringTokenizer(typeAttr, ",");
		String typeIdStr = tokentype.nextToken();
		Integer typeIdInt = new Integer(typeIdStr);
		String typeCode = tokentype.nextToken();
		StringTokenizer tokenState = new StringTokenizer(stateAttr, ",");
		String stateIdStr = tokenState.nextToken();
		Integer stateId = new Integer(stateIdStr);
		String stateCode = tokenState.nextToken();
		// TRY TO LOAD THE ENGINE RELATED TO THE BIOBJECT
		Engine engine = null;
		if (engineIdStr == null || engineIdStr.equals("")) {
			// if engine id is not specified take the first engine for the biobject type
			List engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType(typeCode);
			if(engines.size() == 0) {
				Domain domain = DAOFactory.getDomainDAO().loadDomainById(typeIdInt);
				Vector vector = new Vector();
				vector.add(domain.getValueName());
				throw new EMFUserError(EMFErrorSeverity.ERROR, 1064, vector, new HashMap());
			}
			engine = (Engine) engines.get(0);
		} else {
			Integer engineIdInt = new Integer(engineIdStr);
			engine = DAOFactory.getEngineDAO().loadEngineByID(engineIdInt);
		}
		
		String dsIdStr = (String) request.getAttribute("datasource");
		IDataSource ds = null;
		if (dsIdStr != null && !dsIdStr.equals("")) {
			Integer dsIdInt = new Integer(dsIdStr);
			ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(dsIdInt);
		}
		
		String datasetIdStr = (String) request.getAttribute("dataset");
		IDataSet dataset  = null;
		if (datasetIdStr != null && !datasetIdStr.equals("")) {
			Integer datasetIdInt = new Integer(datasetIdStr);
			dataset = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(datasetIdInt);
		}
		
		// TRY TO LOAD ALL THE FUNCTIONALITIES ASSOCIATED (into request) TO THE BIOBEJCT
		List functionalities = new ArrayList();
		List functionalitiesStr = request.getAttributeAsList(ObjectsTreeConstants.FUNCT_ID);
		if (functionalitiesStr.size() == 0) {
			EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, ObjectsTreeConstants.FUNCT_ID, "1008");
			this.respCont.getErrorHandler().addError(error);
		} else {
			for (Iterator it = functionalitiesStr.iterator(); it.hasNext(); ) {
				String functIdStr = (String) it.next();
				Integer functId = new Integer (functIdStr);
				functionalities.add(functId);
			}
		}
		// lOAD ALL THE FUNCTIONALITIES ASSOCIATED TO THE BIOBJECT (but not into request)
		// First case: the current user is not an administrator (so he cannot see all the functionalities)
		// and the modality is Modify. In this case some functionalities, that the user cannot see, can be 
		// already associated to the object (by different users). This associations mustn't be erased.
		if (!profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN) && mod.equalsIgnoreCase(ObjectsTreeConstants.DETAIL_MOD)) {
			IBIObjectDAO objDAO = DAOFactory.getBIObjectDAO();
			BIObject prevObj = objDAO.loadBIObjectById(id);
			List prevFuncsId = prevObj.getFunctionalities();
			for (Iterator it = prevFuncsId.iterator(); it.hasNext(); ) {
				Integer funcId = (Integer) it.next();
				if (!ObjectsAccessVerifier.canDev(stateCode, funcId, profile)) {
					functionalities.add(funcId);
				}
			}
		}
		// Second case: the current user is a local administrator (he can admin only a part of the tree)
		// and the modality is Modify. In this case some funtionalities in oder part of the tree, which the
		// user cannot see, can be already associated to the object. This associations mustn't be erased. 
		if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)
				&& initialPath != null && !initialPath.trim().equals("")  
				&& mod.equalsIgnoreCase(ObjectsTreeConstants.DETAIL_MOD)) {
			IBIObjectDAO objDAO = DAOFactory.getBIObjectDAO();
			BIObject prevObj = objDAO.loadBIObjectById(id);
			List functionalitiesId = prevObj.getFunctionalities();
			Iterator it = functionalitiesId.iterator();
			while (it.hasNext()) {
				Integer folderId = (Integer) it.next();
				LowFunctionality folder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(folderId, false);
				String folderPath = folder.getPath();
				if (!folderPath.equalsIgnoreCase(initialPath) && !folderPath.startsWith(initialPath + "/")) {
					functionalities.add(folderId);
				}
			}
		}
		// CHECK IF THE LABEL IS ALREADY ASSIGNED TO AN EXISTING OBJECT
		BIObject aBIObject = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(label);
		if (aBIObject != null && !aBIObject.getId().equals(id)) {
			EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "label", "1056");
			this.respCont.getErrorHandler().addError(error);
		}

		// SET DATA INTO OBJECT
		obj.setFunctionalities(functionalities);
		obj.setBiObjectTypeCode(typeCode);
		obj.setBiObjectTypeID(typeIdInt);
		obj.setDescription(description);
		obj.setEncrypt(encrypt);
		obj.setVisible(visible);
		obj.setProfiledVisibility(profiledVisibilityStr);
		obj.setEngine(engine);
		obj.setDataSourceId(ds == null ? null : new Integer(ds.getDsId()));
		obj.setDataSetId(dataset == null ? null : new Integer(dataset.getId()));
		obj.setId(id);
		obj.setName(name);
		obj.setLabel(label);
		obj.setRelName(relname);
		obj.setStateCode(stateCode);
		obj.setStateID(stateId);
		//obj.setPath(path);
		obj.setCreationUser(userId);
		//obj.setRating(Rating == null ? null : new Short(Rating));
		obj.setRefreshSeconds(refreshSeconds);
		// RETURN OBJECT
		return obj;
	}
	
	
	
	
	/**
	 * Recover bi obj template details.
	 * 
	 * @return the obj template
	 * 
	 * @throws Exception the exception
	 */
	public ObjTemplate recoverBIObjTemplateDetails() throws Exception {
		// GET THE USER PROFILE
		SessionContainer session = reqCont.getSessionContainer();
		SessionContainer permanentSession = session.getPermanentContainer();	    
		IEngUserProfile profile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		//String userId=(String)profile.getUserUniqueIdentifier();
		String userId=(String)((UserProfile)profile).getUserId();
	    ObjTemplate templ = null;
	    
		FileItem uploaded = (FileItem) request.getAttribute("UPLOADED_FILE");
		if (uploaded != null) {
			String fileName = GeneralUtilities.getRelativeFileNames(uploaded.getName());
			if (fileName != null && !fileName.trim().equals("")) {
				if (uploaded.getSize() == 0) {
					EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "uploadFile", "201");
					this.respCont.getErrorHandler().addError(error);
					return null;
				}
				int maxSize = GeneralUtilities.getTemplateMaxSize();
				if (uploaded.getSize() > maxSize) {
					EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "uploadFile", "202");
					this.respCont.getErrorHandler().addError(error);
					return null;
				}
				templ = new ObjTemplate();
				templ.setActive(new Boolean(true));
				templ.setCreationUser(userId);
				templ.setDimension(Long.toString(uploaded.getSize()/1000)+" KByte");
		        templ.setName(fileName);
		        byte[] uplCont = uploaded.get();
		        templ.setContent(uplCont);
			}
		}
	    
//		UploadedFile uploaded = (UploadedFile) request.getAttribute("UPLOADED_FILE");
//		if (uploaded != null) {
//			String fileName = uploaded.getFileName();
//			if (fileName != null && !fileName.trim().equals("")) {
//				templ = new ObjTemplate();
//				templ.setActive(new Boolean(true));
//				templ.setCreationUser(userId);
//				templ.setDimension(Long.toString(uploaded.getSizeInBytes()/1000)+" KByte");
//		        templ.setName(fileName);
//		        byte[] uplCont = uploaded.getFileContent();
//		        templ.setContent(uplCont);
//			}
//		}
		return templ;
	}
	
	
	
	
	/**
	 * Recover bi object parameter details.
	 * 
	 * @param biobjIdInt the biobj id int
	 * 
	 * @return the bI object parameter
	 */
	public BIObjectParameter recoverBIObjectParameterDetails(Integer biobjIdInt) {
		String idStr = (String) request.getAttribute("objParId");
		Integer idInt = null;
		if (idStr == null || idStr.trim().equals("")) idInt = new Integer (-1);
		else idInt = new Integer(idStr);
		String parIdStr = (String) request.getAttribute("par_id");
		Integer parIdInt = null;
		if (parIdStr == null || parIdStr.trim().equals("")) 
			parIdInt = new Integer (-1);
		else parIdInt = new Integer (parIdStr);
		String label = (String) request.getAttribute("objParLabel");
	    String parUrlNm = (String)request.getAttribute("parurl_nm");
		String priorityStr = (String)request.getAttribute("priority");
		Integer priority = new Integer(priorityStr);
		String reqFl = (String)request.getAttribute("req_fl");
		Integer reqFlBD = new Integer(reqFl);
		String modFl = (String) request.getAttribute("mod_fl");
		Integer modFlBD = new Integer(modFl);
		String viewFl = (String) request.getAttribute("view_fl");
		if (viewFl == null || viewFl.trim().equals("")) {
			viewFl = "0";
		}
		Integer viewFlBD = new Integer(viewFl);
		String multFl = (String) request.getAttribute("mult_fl");
		Integer multFlBD = new Integer(multFl);
		BIObjectParameter objPar  = new BIObjectParameter();
		objPar.setId(idInt);
		objPar.setBiObjectID(biobjIdInt);
		objPar.setParID(parIdInt);
        Parameter par = new Parameter();
        par.setId(parIdInt);
        objPar.setParameter(par);
        objPar.setLabel(label);
        objPar.setParameterUrlName(parUrlNm);
        objPar.setRequired(reqFlBD);
        objPar.setModifiable(modFlBD);
        objPar.setVisible(viewFlBD);
        objPar.setMultivalue(multFlBD);
        objPar.setPriority(priority); 
		return objPar;
	}
	
	
	
	/**
	 * Fills the response SourceBean with some needed BI Objects information.
	 * 
	 * @param initialPath the initial path
	 * @throws Exception 
	 */
	public void fillResponse(String initialPath) throws EMFUserError {
		try {
			IDomainDAO domaindao = DAOFactory.getDomainDAO();
	        List types = domaindao.loadListDomainsByType("BIOBJ_TYPE");	       
	        // load list of states and engines
	        List states = domaindao.loadListDomainsByType("STATE");
	        List engines =  DAOFactory.getEngineDAO().loadAllEngines();
	        List datasource =  DAOFactory.getDataSourceDAO().loadAllDataSources();
	       List dataset=DAOFactory.getDataSetDAO().loadAllActiveDataSets();
	        // List languages = ConfigSingleton.getInstance().getFilteredSourceBeanAttributeAsList("LANGUAGE_SUPPORTED", "LANGUAGE", "language");
		    response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_ENGINES, engines);
		    response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_DS, datasource);
		    response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_DATASET, dataset);
		    response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_OBJ_TYPES, types);
		    response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_STATES, states);
		   // response.setAttribute(DetailBIObjectModule.NAME_ATTR_LIST_LANGUAGES, languages);
			List functionalities = new ArrayList();
			try {
				if (initialPath != null && !initialPath.trim().equals("")) {
					functionalities = DAOFactory.getLowFunctionalityDAO().loadSubLowFunctionalities(initialPath, false);
					response.setAttribute(TreeObjectsModule.PATH_SUBTREE, initialPath);
				} else {
					functionalities = DAOFactory.getLowFunctionalityDAO().loadAllLowFunctionalities(false);
				}
			} catch (EMFUserError e) {
				SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, 
						"DetailBIObjectsMOdule", 
						"fillResponse", 
						"Error loading functionalities", e);
			}
			response.setAttribute(SpagoBIConstants.FUNCTIONALITIES_LIST, functionalities);
		} catch (Exception e) {
			logger.error("Cannot fill the response",e);
			throw new EMFUserError( "",1);
		}
	}
	
	
	
	/**
	 * Clone.
	 * 
	 * @param biObjPar the bi obj par
	 * 
	 * @return the bI object parameter
	 */
	public static BIObjectParameter clone (BIObjectParameter biObjPar) {
		if (biObjPar == null) return null;
		BIObjectParameter objParClone = new BIObjectParameter();
		objParClone.setId(biObjPar.getId());
		objParClone.setBiObjectID(biObjPar.getBiObjectID());
		objParClone.setLabel(biObjPar.getLabel());
		objParClone.setModifiable(biObjPar.getModifiable());
		objParClone.setMultivalue(biObjPar.getMultivalue());
		objParClone.setParameter(biObjPar.getParameter());
		objParClone.setParameterUrlName(biObjPar.getParameterUrlName());
		objParClone.setParameterValues(biObjPar.getParameterValues());
		objParClone.setParID(biObjPar.getParID());
		objParClone.setProg(biObjPar.getProg());
		objParClone.setRequired(biObjPar.getRequired());
		objParClone.setVisible(biObjPar.getVisible());
		return objParClone;
	}


	/**
	 * Clone.
	 * 
	 * @param obj the obj
	 * 
	 * @return the bI object
	 */
	public static BIObject clone (BIObject obj) {
		if (obj == null) return null;
		BIObject objClone = new BIObject();
		objClone.setBiObjectTypeCode(obj.getBiObjectTypeCode());
		objClone.setBiObjectTypeID(obj.getBiObjectTypeID());
		objClone.setDescription(obj.getDescription());
		objClone.setEncrypt(obj.getEncrypt());
		objClone.setVisible(obj.getVisible());
		objClone.setEngine(obj.getEngine());
		objClone.setDataSourceId(obj.getDataSourceId());
		objClone.setDataSetId(obj.getDataSetId());
		objClone.setId(obj.getId());
		objClone.setLabel(obj.getLabel());
		objClone.setName(obj.getName());
		objClone.setPath(obj.getPath());
		objClone.setRelName(obj.getRelName());
		objClone.setStateCode(obj.getStateCode());
		objClone.setStateID(obj.getStateID());
		objClone.setRefreshSeconds(obj.getRefreshSeconds());
		return objClone;
	}



	/**
	 * Creates the new bi object parameter.
	 * 
	 * @param objId the obj id
	 * 
	 * @return the bI object parameter
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public static BIObjectParameter createNewBIObjectParameter(Integer objId) throws EMFUserError {
		BIObjectParameter biObjPar = new BIObjectParameter();
		biObjPar.setId(new Integer(-1));
		biObjPar.setParID(new Integer(-1));
		biObjPar.setBiObjectID(objId);
		biObjPar.setLabel("");
		biObjPar.setModifiable(new Integer(0));
		biObjPar.setMultivalue(new Integer(0));
		biObjPar.setParameter(null);
		biObjPar.setParameterUrlName("");
		biObjPar.setProg(new Integer(0));
		biObjPar.setRequired(new Integer(0));
		biObjPar.setVisible(new Integer(1));
		int objParsNumber = 0;
		IBIObjectParameterDAO objParDAO = DAOFactory.getBIObjectParameterDAO();
		List objPars = objParDAO.loadBIObjectParametersById(objId);
		if (objPars != null) objParsNumber = objPars.size();
		biObjPar.setPriority(new Integer(objParsNumber + 1));
		return biObjPar;
	}

	
	/**
	 * Find bi obj par id.
	 * 
	 * @param objParIdObj the obj par id obj
	 * 
	 * @return the int
	 */
	public static int findBIObjParId (Object objParIdObj) {
		String objParIdStr = "";
		if (objParIdObj instanceof String) {
			objParIdStr = (String) objParIdObj;
		} else if (objParIdObj instanceof List) {
			List objParIdList = (List) objParIdObj;
			Iterator it = objParIdList.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				if (item instanceof SourceBean) continue;
				if (item instanceof String) objParIdStr = (String) item;
			}
		}
		int objParId = Integer.parseInt(objParIdStr);
		return objParId;
	}
	
	
	
	/**
	 * Fills the request container object with some BIObject and BIObjectParameter information contained into
	 * the request Source Bean (they are all attributes). It is useful for validation process.
	 * 
	 * @param request The request Source Bean 
	 * @throws SourceBeanException If any exception occurred
	 */
//	public static void fillRequestContainer (RequestContainer req, SourceBean request, EMFErrorHandler errorHandler) throws Exception{
//		String label = (String)request.getAttribute("label");
//		String name = (String)request.getAttribute("name");
//		String description = (String)request.getAttribute("description");
//		String relName = (String)request.getAttribute("relName");
//		String engine = (String)request.getAttribute("engine");
//		String datasource = (String)request.getAttribute("datasource");
//		String state = (String)request.getAttribute("state");
//		String path = "";
//		String objParLabel = (String)request.getAttribute("objParLabel");
//		String parurl_nm = (String)request.getAttribute("parurl_nm");
//		String par_Id = (String)request.getAttribute("par_Id");
//		String req_fl = (String)request.getAttribute("req_fl");
//		String mod_fl = (String) request.getAttribute("mod_fl");
//		String view_fl = (String) request.getAttribute("view_fl");
//		String mult_fl = (String) request.getAttribute("mult_fl");
//		Object pathParentObj = request.getAttribute("PATH_PARENT");
//		if( (pathParentObj != null) && (!(pathParentObj instanceof String))) {
//			errorHandler.addError(new EMFValidationError(EMFErrorSeverity.ERROR, 1032));
//		}else {
//			String pathParent = (String)pathParentObj;
//			if(pathParent != null){
//				path = pathParent;
//			}
//		}
//		SourceBean _serviceRequest = req.getServiceRequest();
//		if(_serviceRequest.getAttribute("label")==null)
//			_serviceRequest.setAttribute("label",label);
//		if(_serviceRequest.getAttribute("description")==null)
//			_serviceRequest.setAttribute("description",description);
//		if(_serviceRequest.getAttribute("name")==null)
//			_serviceRequest.setAttribute("name",name);
//		if(_serviceRequest.getAttribute("relName")==null)
//			_serviceRequest.setAttribute("relName",relName);
//		if (engine == null) {
//			List engines = DAOFactory.getEngineDAO().loadAllEngines();
//			if (engines.size() > 0) {
//				engine = ((Engine) engines.get(0)).getId().toString();
//			}
//		}
//		if (datasource == null) {
//			List lstDataSource = DAOFactory.getDataSourceDAO().loadAllDataSources();
//			if (lstDataSource.size() > 0) {
//				datasource = new Integer(((DataSource) lstDataSource.get(0)).getDsId()).toString();
//			}
//		}		
//		if(_serviceRequest.getAttribute("engine")==null)
//			_serviceRequest.setAttribute("engine", engine);
//		if(_serviceRequest.getAttribute("datasource")==null)
//			_serviceRequest.setAttribute("datasource", datasource);		
//		if(_serviceRequest.getAttribute("state")==null)
//			_serviceRequest.setAttribute("state", state);
//		if(_serviceRequest.getAttribute("path")==null)
//			_serviceRequest.setAttribute("path", path);
//		if(_serviceRequest.getAttribute("objParLabel")==null)
//			_serviceRequest.setAttribute("objParLabel", objParLabel == null ? "" : objParLabel);
//		if(_serviceRequest.getAttribute("parurl_nm")==null)
//			_serviceRequest.setAttribute("parurl_nm", parurl_nm == null ? "" : parurl_nm);
//		if(_serviceRequest.getAttribute("par_Id")==null)
//			_serviceRequest.setAttribute("par_Id", par_Id == null ? "" : par_Id);
//		if(_serviceRequest.getAttribute("req_fl")==null)
//			_serviceRequest.setAttribute("req_fl", req_fl == null ? "" : req_fl);
//		if(_serviceRequest.getAttribute("mod_fl")==null)
//			_serviceRequest.setAttribute("mod_fl", mod_fl == null ? "" : mod_fl);
//		if(_serviceRequest.getAttribute("view_fl")==null)
//			_serviceRequest.setAttribute("view_fl", view_fl == null ? "" : view_fl);
//		if(_serviceRequest.getAttribute("mult_fl")==null)
//			_serviceRequest.setAttribute("mult_fl", mult_fl == null ? "" : mult_fl);
//	}
	
	
	
}
