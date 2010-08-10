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
package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.error.EMFErrorCategory;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.AdmintoolsConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.security.ISecurityInfoProvider;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.DataSetFactory;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.DataSetParametersList;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

/**
 * This class implements a module which  handles data Set management. 
 */
public class DetailDataSetModule extends AbstractModule {
	static private Logger logger = Logger.getLogger(DetailDataSetModule.class);
	private SessionContainer session;
	private EMFErrorHandler errorHandler;
	private IEngUserProfile profile;

	public static final String MOD_SAVE = "SAVE";
	public static final String MOD_SAVEBACK = "SAVEBACK";
	public static final String DATASET_MODIFIED = "DATASET_MODIFIED";
	public static final String DATASET = "DATASET";
	public static final String messageBundle = "messages";
	public static final String PARAMETERS_XML_MODIFIED = "parametersXMLModified";
	public static final String RETURN_FROM_TEST_MSG="RETURN_FROM_TEST_MSG";
	public static final String TEST_BEFORE_SAVE="testDataSetBeforeSave";
	public static final String TEST="testLov";	
	public static final String TEST_AFTER_PARAMETERS_FILLED="testafterparametersfilling";
	public static final String PARAMETERS_FILLED="parametersfilled";
	public static final String TEST_EXECUTED="testExecuted";
	public final static String LIST_TRANSFORMER = "transformers";
	public final static String DS_METADATA_XML = "dsMetadataXML";




	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.module.AbstractModule#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
	}

	/**
	 * Reads the operation asked by the user and calls the insertion, updation or deletion methods.
	 * 
	 * @param request The Source Bean containing all request parameters
	 * @param response The Source Bean containing all response parameters
	 * 
	 * @throws exception If an exception occurs
	 * @throws Exception the exception
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		String message = (String) request.getAttribute("MESSAGEDET");

		logger.debug("begin of detail Data Set service with message =" +message);
		errorHandler = getErrorHandler();
		RequestContainer requestContainer = this.getRequestContainer();
		session = requestContainer.getSessionContainer();
		SessionContainer permSess = session.getPermanentContainer();
		profile = (IEngUserProfile)permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		try {
			if (message == null) {
				EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 101);
				logger.debug("The message parameter is null");
				throw userError;
			}
			logger.debug("The message parameter is: " + message.trim());
			if (message.trim().equalsIgnoreCase(SpagoBIConstants.DETAIL_SELECT)) {
				getDataSet(request, response);
			} else if (message.trim().equalsIgnoreCase(SpagoBIConstants.DETAIL_MOD)) {
				modifyDataSet(request, SpagoBIConstants.DETAIL_MOD, response);
			} else if (message.trim().equalsIgnoreCase(SpagoBIConstants.DETAIL_NEW)) {
				newDataSet(response);
			} else if (message.trim().equalsIgnoreCase(SpagoBIConstants.DETAIL_INS)) {
				modifyDataSet(request, SpagoBIConstants.DETAIL_INS, response);
			} else if (message.trim().equalsIgnoreCase(SpagoBIConstants.DETAIL_DEL)) {
				deleteDataSet(request, SpagoBIConstants.DETAIL_DEL, response);
			}	else if (message.trim().equalsIgnoreCase("EXIT_FROM_DETAIL")){
				exitFromDetail(request, response);
			}
			else if(message.trim().equalsIgnoreCase(DetailDataSetModule.TEST_AFTER_PARAMETERS_FILLED)) {
				testDatasetAfterParameterFilling(request, response);
			}
		}
		catch (EMFUserError eex) {
			errorHandler.addError(eex);
			return;
		} catch (Exception ex) {
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
			errorHandler.addError(internalError);
			return;
		}
	}

	/**
	 * Gets the detail of a data set chosen by the user from the 
	 * data Sets list. It reaches the key from the request and asks to the DB all detail
	 * data Set information, by calling the method <code>loadDataSetByID</code>.
	 *   
	 * @param key The chosen data Set id key
	 * @param response The response Source Bean
	 * @throws EMFUserError If an exception occurs
	 */   
	private void getDataSet(SourceBean request, SourceBean response) throws EMFUserError {		
		try {		 									
			IDataSet ds = DAOFactory.getDataSetDAO().loadDataSetByID(new Integer((String)request.getAttribute("ID")));		
			prepareDetailDatasetPage(ds.toSpagoBiDataSet(), AdmintoolsConstants.DETAIL_MOD, response);

			if (request.getAttribute("SUBMESSAGEDET") != null &&
					((String)request.getAttribute("SUBMESSAGEDET")).equalsIgnoreCase(MOD_SAVEBACK))
			{
				response.setAttribute("loopback", "true");
				return;
			}

			//response.setAttribute("modality", modalita);
			//response.setAttribute("dataset", ds);

			session.setAttribute(DetailDataSetModule.DATASET_MODIFIED, "false");
			session.setAttribute(DetailDataSetModule.DATASET, ds.toSpagoBiDataSet());


		} catch (Exception ex) {
			logger.error("Cannot fill response container" + ex.getLocalizedMessage());	
			HashMap params = new HashMap();
			params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9201, new Vector(), params);
		}

	}










	/**
	 * Inserts/Modifies the detail of a data set according to the user request. 
	 * When a data Set is modified, the <code>modifyDataSet</code> method is called; when a new
	 * data Set is added, the <code>insertDataSet</code>method is called. These two cases are 
	 * differentiated by the <code>mod</code> String input value .
	 * 
	 * @param request The request information contained in a SourceBean Object
	 * @param mod A request string used to differentiate insert/modify operations
	 * @param response The response SourceBean 
	 * @throws EMFUserError If an exception occurs
	 * @throws SourceBeanException If a SourceBean exception occurs
	 */
	private void modifyDataSet(SourceBean serviceRequest, String mod, SourceBean serviceResponse)
	throws EMFUserError, SourceBeanException {

		boolean testCase=false;
		// recover dataset Object
		SpagoBiDataSet	dsNew  = (SpagoBiDataSet) session.getAttribute(DetailDataSetModule.DATASET);


		if(session.getAttribute(DetailDataSetModule.PARAMETERS_FILLED)!=null){
			session.delAttribute(DetailDataSetModule.PARAMETERS_FILLED);
		}
		// check if parameters has been modified
		String parametersXMLModified = (String) serviceRequest.getAttribute(DetailDataSetModule.DATASET_MODIFIED);
		if(parametersXMLModified != null && !parametersXMLModified.trim().equals("")) 
			session.setAttribute(DetailDataSetModule.DATASET_MODIFIED, parametersXMLModified);



		errorHandler = getErrorHandler();
		Object datasetMetadataXMLO=session.getAttribute(DS_METADATA_XML);
		if(datasetMetadataXMLO!=null){
			String datasetMetadataXML=(String)datasetMetadataXMLO;
			dsNew.setDsMetadata(datasetMetadataXML);	
		}

//		check if we are coming from the test
		String returnFromTestMsg = (String) serviceRequest.getAttribute(DetailDataSetModule.RETURN_FROM_TEST_MSG);

		if(returnFromTestMsg!=null) { // Case returning from Test View!
			// save after the test
			if ("SAVE".equalsIgnoreCase(returnFromTestMsg)) {      // Save 		
				//DAOFactory.getDataSetDAO().modifyDataSet(dsNew);
				serviceResponse.setAttribute(DetailDataSetModule.DATASET, dsNew);
				serviceResponse.setAttribute(SpagoBIConstants.MODALITY, mod);
				session.delAttribute(DetailDataSetModule.DATASET);
				session.delAttribute(SpagoBIConstants.MODALITY);
				session.delAttribute(DS_METADATA_XML);
			} 
			// don't save after the test
			else 
				if ("DO_NOT_SAVE".equalsIgnoreCase(returnFromTestMsg)) {   // Don't save
					try {
						prepareDetailDatasetPage(dsNew, mod, serviceResponse);
					} catch (EMFInternalError e) {
						logger.error("Error while preparing the page after coming back", e);
					}
					// exits without writing into DB and without loop
					return;
				}
		} 
		else{ // if we are not coming from the test result page, the fields are in request

			Object test = serviceRequest.getAttribute(DetailDataSetModule.TEST_BEFORE_SAVE); // are we going to test Object
			if(test!=null)testCase=true;

			String type="";
			try{
				dsNew = recoverDataSetDetails(serviceRequest,mod,dsNew);   // build again the dataSet in case user wanted to change some fields
			}
			catch (Exception e) {
				logger.error("Exception while recovering dataset informations",e); 
				EMFUserError error=new EMFUserError(EMFErrorSeverity.ERROR, "9208", messageBundle);
				getErrorHandler().addError(error);
			}

			// check if errors coming from errorHandler
			Collection errorsC = errorHandler.getErrors();				
			// if there are some validation errors into the errorHandler does not write into DB
			if (errorsC != null && errorsC.size() > 0) {
				Iterator iterator = errorsC.iterator();
				while (iterator.hasNext()) {
					Object error = iterator.next();
					if (error instanceof EMFValidationError) {
						logger.error("Validation Error found");
						serviceResponse.setAttribute("modality", mod);
						serviceResponse.setAttribute(DetailDataSetModule.DATASET, dsNew);						
						//session.setAttribute(DetailDataSetModule.DATASET, dsNew);
						return;
					}
					if (error instanceof EMFUserError) {
						logger.error("User Error found");
						serviceResponse.setAttribute("modality", mod);
						serviceResponse.setAttribute(DetailDataSetModule.DATASET, dsNew);						
						//session.setAttribute(DetailDataSetModule.DATASET, dsNew);
						return;
					}					
				}
			}

			try {
				if(FileDataSet.DS_TYPE.equalsIgnoreCase(dsNew.getType())) type="0";			// what type is dataset?
				else if(JDBCDataSet.DS_TYPE.equalsIgnoreCase(dsNew.getType()))type="1";
				else if(WebServiceDataSet.DS_TYPE.equalsIgnoreCase(dsNew.getType()))type="2";
				else if(ScriptDataSet.DS_TYPE.equalsIgnoreCase(dsNew.getType()))type="3";
				else if(JavaClassDataSet.DS_TYPE.equalsIgnoreCase(dsNew.getType()))type="4";

				// check if it has to change parameters		(now only in Query Case) 
				if(type.equals("1") || type.equals("2") || type.equals("3") || type.equals("4")){
					String parametersXML = dsNew.getParameters();
					DataSetParametersList dslistDet = null;
					if( (parametersXML==null) || (parametersXML.trim().equals("")) ) {
						dslistDet = new DataSetParametersList();
					} else {
						dslistDet = new DataSetParametersList(parametersXML);
					}


					boolean itemTaskDone = doDatasetParameterItemTask(dsNew, dslistDet, serviceRequest);



					if(itemTaskDone ) { // if something on on parameters is changed
						prepareDetailDatasetPage(dsNew, mod, serviceResponse);
						session.setAttribute(DetailDataSetModule.DATASET_MODIFIED, "true");
						session.setAttribute(DetailDataSetModule.DATASET, dsNew);
						// exits without writing into DB and without loop
						return;
					} else {
						List items = dslistDet.getItems();
						if(items.size()==0) {
							dsNew.setParameters("<PARAMETERSLIST/>");
						}
					}	
				}

				// Test Case
				if (testCase) {

					// check if is file dataset and has no file associated
					if(dsNew.getType().equals("0") && dsNew.getFileName()==null){
						logger.error("FIle name is empty: if you are trying to uplad new FIl save dataset before testing it"); 
						//EMFUserError error=new EMFUserError(EMFErrorSeverity.ERROR, "FIle name is empty: if you are trying to uplad new FIl save dataset before testing it", messageBundle);
						EMFUserError error=new EMFUserError(EMFErrorSeverity.ERROR, 9226);

						getErrorHandler().addError(error);
					}
					session.setAttribute(SpagoBIConstants.MODALITY, mod);
					//check if there are parameters to fill
					List parameters=getParametersToFill(dsNew);
					//boolean needProfAttrFill = checkProfileAttributes(serviceResponse, (QueryDataSet)dsNew);
					if(parameters!=null && parameters.size()>0){
						serviceResponse.setAttribute("parameters",parameters);
					}
					serviceResponse.setAttribute(DetailDataSetModule.TEST, "true");
					session.setAttribute(DetailDataSetModule.DATASET, dsNew);

					// exits without writing into DB 
					return;
				}

			}
			catch (EMFUserError e){
				logger.error("Cannot fill response container" + e.getLocalizedMessage());
				HashMap params = new HashMap();
				params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
				EMFUserError error= new EMFUserError(EMFErrorSeverity.ERROR, 9203, new Vector(), params);
				errorHandler.addError(error);
			}

			catch (Exception ex) {		
				logger.error("Cannot fill response container" , ex);		
				throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
			}
		}	

		if(!testCase)
			// if is to update dataset check if there is a file to upload aaa
			if(serviceRequest.getAttribute("UPLOADED_FILE")!=null){
				String fileName=uploadFileToResources(serviceRequest);
				if(fileName == null){
					return;
				}
				dsNew.setFileName(fileName);
			}

		if (mod.equalsIgnoreCase(SpagoBIConstants.DETAIL_INS)) {		// in case it is an insert
			//check the type and insert right parameters
			//if a ds with the same label not exists on db ok else error
			if(dsNew.getLabel()==null || dsNew.getLabel().equalsIgnoreCase("")){
				HashMap params = new HashMap();
				params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
				//session.setAttribute(DetailDataSetModule.DATASET, dsNew);
				//serviceResponse.setAttribute(SpagoBIConstants.MODALITY, SpagoBIConstants.DETAIL_NEW);
				logger.error("Campo Label obbligatorio");
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 9214, new Vector(), params );
				getErrorHandler().addError(error);
				return;				
			}

			if (DAOFactory.getDataSetDAO().loadDataSetByLabel(dsNew.getLabel()) != null){
				HashMap params = new HashMap(); logger.error("label already exists "+dsNew.getLabel());
				params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 9202, new Vector(), params );
				getErrorHandler().addError(error);
				return;
			}	

			DAOFactory.getDataSetDAO().insertDataSet( DataSetFactory.getDataSet(dsNew) );   //Insert DataSet
			IDataSet tmpDS = DAOFactory.getDataSetDAO().loadDataSetByLabel(dsNew.getLabel());
			int t=tmpDS.getId();
			dsNew.setDsId(t);
			mod = SpagoBIConstants.DETAIL_MOD;
			//session.setAttribute("dataset", dsNew);
		} else {				
			//update ds
			DAOFactory.getDataSetDAO().modifyDataSet(DataSetFactory.getDataSet(dsNew));			
			//session.setAttribute("dataset", dsNew);
		}  

		if (serviceRequest.getAttribute("SUBMESSAGEDET") != null && 
				((String)serviceRequest.getAttribute("SUBMESSAGEDET")).equalsIgnoreCase(MOD_SAVE)) {	
			serviceResponse.setAttribute(SpagoBIConstants.MODALITY, mod);
			serviceResponse.setAttribute(DetailDataSetModule.DATASET, dsNew);
			List transformers = DAOFactory.getDomainDAO().loadListDomainsByType("TRANSFORMER_TYPE");
			serviceResponse.setAttribute(LIST_TRANSFORMER, transformers);
			return;
		}
		else if (serviceRequest.getAttribute("SUBMESSAGEDET") != null && 
				((String)serviceRequest.getAttribute("SUBMESSAGEDET")).equalsIgnoreCase(MOD_SAVEBACK)){
			serviceResponse.setAttribute("loopback", "true");
			List transformers = DAOFactory.getDomainDAO().loadListDomainsByType("TRANSFORMER_TYPE");
			serviceResponse.setAttribute(LIST_TRANSFORMER, transformers);
			return;
		} 

		serviceResponse.setAttribute("loopback", "true");
		session.delAttribute(DetailDataSetModule.DATASET_MODIFIED);

	}


	protected String uploadFileToResources(SourceBean serviceRequest){
		FileItem uploaded = (FileItem) serviceRequest.getAttribute("UPLOADED_FILE");
		if (uploaded != null) {
			String fileName = GeneralUtilities.getRelativeFileNames(uploaded.getName());
			if (fileName != null && !fileName.trim().equals("")) {
				if (uploaded.getSize() == 0) {
					EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "uploadFile", "201");
					this.getErrorHandler().addError(error);
					return null;
				}
				int maxSize = GeneralUtilities.getTemplateMaxSize();
				if (uploaded.getSize() > maxSize) {
					EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "uploadFile", "202");
					this.getErrorHandler().addError(error);
					return null;
				}

				ConfigSingleton configSingleton = ConfigSingleton.getInstance();
				SourceBean sb = (SourceBean)configSingleton.getAttribute("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
				String pathh = (String) sb.getCharacters();
				String filePath= SpagoBIUtilities.readJndiResource(pathh);
				filePath += "/dataset/files";
				File dir=new File(filePath);	    

				// check if file is already present!
				boolean found=false;
				String[] children = dir.list();
				if (children == null) {
					children=new String[0];
				} 

				for (int i=0; i<children.length; i++) {
					// Get filename of file or directory
					String fileFound = children[i];
					if(fileName.equals(fileFound)){
						found=true;
						logger.error("A file with the same name is already into resources");
						EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "File already present", "9227");
						this.getErrorHandler().addError(error);
						return null;						
					}
				}

				// create a new File and copy the exported one
				File newFile=new File(filePath+"/"+fileName);
				
				try {
					uploaded.write(newFile);
				} catch (Exception e) {
					EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "Error in writing the file", "202");
					this.getErrorHandler().addError(error);	
					return null;						
				}
				// FIle has been written
				return fileName;
			}
		}


		return null;
	}



	/**
	 * Before going into test mode checks if there are parameters to fill and in case puts them in List.
	 * 
	 * @param ds the ds
	 * 
	 * @return the parameters to fill
	 * 
	 * @throws SourceBeanException the source bean exception
	 */
	public static List getParametersToFill(SpagoBiDataSet ds) throws SourceBeanException{
		String parametersXML=ds.getParameters();
		if(parametersXML!=null && !((parametersXML.trim()).equals(""))){
			DataSetParametersList dsParam=new DataSetParametersList(parametersXML);
			List parameters=dsParam.getItems();
			return parameters;}
		else return null;
	}




	/**
	 * Deletes a data Set choosed by user from the data Sets list.
	 * 
	 * @param request	The request SourceBean
	 * @param mod	A request string used to differentiate delete operation
	 * @param response	The response SourceBean
	 * @throws EMFUserError	If an Exception occurs
	 * @throws SourceBeanException If a SourceBean Exception occurs
	 */
	private void deleteDataSet(SourceBean request, String mod, SourceBean response)
	throws EMFUserError, SourceBeanException {

		try {
			String id = (String) request.getAttribute("ID");
			boolean bObjects =  DAOFactory.getDataSetDAO().hasBIObjAssociated(id);
			if(bObjects){	
				HashMap params = new HashMap();
				params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 9204, new Vector(), params );
				getErrorHandler().addError(error);
				return;
			}

			//delete the ds
			IDataSet ds = DAOFactory.getDataSetDAO().loadDataSetByID(new Integer(id));
			DAOFactory.getDataSetDAO().eraseDataSet(ds);
		}
		catch (EMFUserError e){
			logger.error("Cannot fill response container" + e.getLocalizedMessage());
			HashMap params = new HashMap();		  
			params.put(AdmintoolsConstants.PAGE, ListDataSetModule.MODULE_PAGE);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9207, new Vector(), params);

		}
		catch (Exception ex) {		
			ex.printStackTrace();
			logger.error("Cannot fill response container" ,ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
		response.setAttribute("loopback", "true");			
	}



	/**
	 * Instantiates a new <code>dataset<code> object when a new data set insertion is required, in order
	 * to prepare the page for the insertion.
	 * 
	 * @param response The response SourceBean
	 * @throws EMFUserError If an Exception occurred
	 */

	private void newDataSet(SourceBean response) throws EMFUserError {

		try {

			SpagoBiDataSet ds = null;
			//response.setAttribute("modality", modalita);
			EMFErrorHandler errorHandler = getErrorHandler();

			// if there are some validation errors into the errorHandler does not write into DB
			Collection errors = errorHandler.getErrors();
			if (errors != null && errors.size() > 0) {
				Iterator iterator = errors.iterator();
				while (iterator.hasNext()) {
					Object error = iterator.next();
					if (error instanceof EMFValidationError) {
						//response.setAttribute("dsObj", ds); aaa
						//response.setAttribute("modality", mod);
						return;
					}
				}
			}
			ds = new SpagoBiDataSet();
			ds.setDsId(-1);
			ds.setDescription("");
			ds.setLabel("");
			ds.setName("");
			ds.setParameters("");
			session.setAttribute(DetailDataSetModule.DATASET_MODIFIED, "false");
			session.setAttribute(DetailDataSetModule.DATASET, ds);
			//response.setAttribute("dataset", ds);
			prepareDetailDatasetPage(ds, AdmintoolsConstants.DETAIL_INS, response);

		} catch (Exception ex) {
			logger.error("Cannot prepare page for the insertion" , ex);		
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}



	private SpagoBiDataSet recoverDataSetDetails (SourceBean serviceRequest, String mod, SpagoBiDataSet dsOld) throws EMFUserError, SourceBeanException, IOException  {
		SpagoBiDataSet ds  =null;
		String typeds= (String)serviceRequest.getAttribute("typeDataSet");

		if(typeds==null){
			logger.error("type not defined");
			EMFUserError emf=new EMFUserError(EMFErrorSeverity.ERROR, "9208", messageBundle);
			errorHandler.addError(emf);
			if(dsOld!=null) return dsOld;
			else return null;
		}


		if(typeds.equalsIgnoreCase("0")){
			if(FileDataSet.DS_TYPE.equalsIgnoreCase( dsOld.getType() )){
				ds = dsOld;
			}
			else{
				ds = new SpagoBiDataSet();
				ds.setType( FileDataSet.DS_TYPE );
			}
		}
		else 
			if(typeds.equalsIgnoreCase("1")){
				if(JDBCDataSet.DS_TYPE.equalsIgnoreCase( dsOld.getType() )){
					ds = dsOld;
				}
				else{
					ds = new SpagoBiDataSet();
					ds.setType( JDBCDataSet.DS_TYPE );
				}
			}
			else 
				if(typeds.equalsIgnoreCase("2")){
					if(WebServiceDataSet.DS_TYPE.equalsIgnoreCase( dsOld.getType() )){
						ds = dsOld;
					}
					else{
						ds = new SpagoBiDataSet();
						ds.setType( WebServiceDataSet.DS_TYPE );					
					}

				}else
					if(typeds.equalsIgnoreCase("3")){
						if(ScriptDataSet.DS_TYPE.equalsIgnoreCase( dsOld.getType() )){
							ds = dsOld;
						}
						else{
							ds = new SpagoBiDataSet();
							ds.setType( ScriptDataSet.DS_TYPE );
						}
					}
					else
						if(typeds.equalsIgnoreCase("4")){
							if(JavaClassDataSet.DS_TYPE.equalsIgnoreCase( dsOld.getType() )){
								ds = dsOld;
							}
							else{
								ds = new SpagoBiDataSet();
								ds.setType( JavaClassDataSet.DS_TYPE );
							}
						}




		String idStr = (String)serviceRequest.getAttribute("ID");
		String name = (String)serviceRequest.getAttribute("NAME");
		String label = (String)serviceRequest.getAttribute("LABEL");
		String transformerName = (String)serviceRequest.getAttribute("TRANSFORMERNAME");
		Domain domainTransformer =DAOFactory.getDomainDAO().loadDomainByCodeAndValue("TRANSFORMER_TYPE", transformerName);
		Integer transformerId = null;
		String pivotName = null;
		String pivotRow = null;
		String pivotValue = null;
		boolean numRows = false;
		if (domainTransformer!= null){
			transformerId = domainTransformer.getValueId();
			pivotName = (String)serviceRequest.getAttribute("PIVOTCOLUMNNAME");;
			pivotRow = (String)serviceRequest.getAttribute("PIVOTROWNAME");;
			pivotValue = (String)serviceRequest.getAttribute("PIVOTCOLUMNVALUE");;
			numRows = Boolean.valueOf((String)(serviceRequest.getAttribute("PIVOTNUMROWS")));;
		}

		String description = (String)serviceRequest.getAttribute("DESCR");	
		Integer id = new Integer(idStr);
		ds.setDsId(id.intValue());
		ds.setName(name);
		ds.setLabel(label);
		ds.setTransformerId(transformerId);
		ds.setPivotColumnName(pivotName);
		ds.setPivotRowName(pivotRow);
		ds.setPivotColumnValue(pivotValue);
		ds.setNumRows(numRows);
		ds.setDescription(description);


		if(typeds==null){
			logger.error("type not defined");
			EMFUserError emf=new EMFUserError(EMFErrorSeverity.ERROR, "9208", messageBundle);
			errorHandler.addError(emf);
			return ds;
		}



		if(typeds.equalsIgnoreCase("0")){
			if(serviceRequest.getAttribute("FILENAME")!=null){
				String fileName=(String)serviceRequest.getAttribute("FILENAME");
				if(fileName!=null && !fileName.equalsIgnoreCase("") && fileName.length()>0){
					ds.setFileName(fileName);
				}
				else{
					EMFValidationError emf= new EMFValidationError(EMFErrorSeverity.ERROR, "FILENAME", "9224");
					errorHandler.addError(emf);					
					return ds;
				}

			}
		}
		else 
			if(typeds.equalsIgnoreCase("1")){
				if(serviceRequest.getAttribute("QUERY")!=null){
					String query=(String)serviceRequest.getAttribute("QUERY");
					String toVerify = query.toUpperCase();
					toVerify=toVerify.trim();					
					if (toVerify.startsWith("SELECT")){
						ds.setQuery(query);
					}
					else {
						//I set anyway the query cause I do not want it to be cancelled
						ds.setQuery(query); 
						logger.error("Query not valid");
						EMFValidationError emf= new EMFValidationError(EMFErrorSeverity.ERROR, "QUERY", "9215");
						errorHandler.addError(emf);
						// if daling with parameters add this message
						if(isParameterRelatedEvent(serviceRequest)){
							EMFUserError emf2= new EMFUserError(EMFErrorSeverity.WARNING, "9225", messageBundle);
							errorHandler.addError(emf2);
						}

						return ds;
					}
				}
				if(serviceRequest.getAttribute("DATASOURCE")!=null){
					String dataSourceID=(String)serviceRequest.getAttribute("DATASOURCE");
					if(!dataSourceID.equalsIgnoreCase("") && !(dataSourceID == null)){
						IDataSource dataSource=DAOFactory.getDataSourceDAO().loadDataSourceByID(Integer.valueOf(dataSourceID));
						ds.setDataSource(dataSource.toSpagoBiDataSource());
					}else{
						logger.error("Data source not assigned");
						EMFUserError emf= new EMFUserError(EMFErrorSeverity.ERROR, "9222", messageBundle);
						errorHandler.addError(emf);
						if(isParameterRelatedEvent(serviceRequest)){
							EMFUserError emf2= new EMFUserError(EMFErrorSeverity.WARNING, "9225", messageBundle);
							errorHandler.addError(emf2);
						}

						return ds;
					}
				}
			}
			else 
				if(typeds.equalsIgnoreCase("2")){
					if(serviceRequest.getAttribute("ADDRESS")!=null){
						String address=(String)serviceRequest.getAttribute("ADDRESS");
						ds.setAdress(address);
					}
					if(serviceRequest.getAttribute("OPERATION")!=null){
						String operation=(String)serviceRequest.getAttribute("OPERATION");
						ds.setOperation(operation);

					}
				}else
					if(typeds.equalsIgnoreCase("3")){
						if(serviceRequest.getAttribute("SCRIPT")!=null){
							String script=(String)serviceRequest.getAttribute("SCRIPT");
							String languageScript=(String)serviceRequest.getAttribute("LANGUAGESCRIPT");
							ds.setLanguageScript(languageScript);
							String toVerify = script.toUpperCase();
							//	if( !toVerify.contains("<A") &&  !toVerify.contains("<LINK") &&  !toVerify.contains("<IMG") &&  !toVerify.contains("<SCRIPT")){
							ds.setScript(script);
							/*	}else{
								logger.error("error in the script");
								EMFValidationError emf= new EMFValidationError(EMFErrorSeverity.ERROR, "SCRIPT", "9216");
								errorHandler.addError(emf);
								if(isParameterRelatedEvent(serviceRequest)){
									EMFUserError emf2= new EMFUserError(EMFErrorSeverity.WARNING, "9225", messageBundle);
									errorHandler.addError(emf2);
								}								
								return ds;
							} */								
						}
					}
					else
						if(typeds.equalsIgnoreCase("4")){
							if(serviceRequest.getAttribute("JCLASSNAME")!=null){
								String javaClassName=(String)serviceRequest.getAttribute("JCLASSNAME");
								ds.setJavaClassName(javaClassName);
							}
						}


		if(mod.equalsIgnoreCase(AdmintoolsConstants.DETAIL_INS)) {
			//ds.setDsId(id.intValue());
			// check also that label does not exist already
			if (DAOFactory.getDataSetDAO().loadDataSetByLabel(label) != null){
				logger.error("label already exists "+label);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, "9202", messageBundle);
				errorHandler.addError(error);
				if(isParameterRelatedEvent(serviceRequest)){
					EMFUserError emf2= new EMFUserError(EMFErrorSeverity.WARNING, "9225", messageBundle);
					errorHandler.addError(emf2);
				}				
				ds.setLabel("");
				return ds;
			}	
		} 


		return ds;
	}

	private void exitFromDetail (SourceBean request, SourceBean response) throws SourceBeanException {
		response.setAttribute("loopback", "true");
		session.delAttribute(DetailDataSetModule.DATASET_MODIFIED);
		session.delAttribute(DetailDataSetModule.DATASET);
		session.delAttribute(DetailDataSetModule.DS_METADATA_XML);
	}


	/** This function return true if the last event deals with dataset Parameters
	 * 
	 * @param request
	 * @return
	 */

	private boolean isParameterRelatedEvent(SourceBean request){
		Object indexOfDatasetParameterItemToDeleteObj = request.getAttribute("indexOfDatasetParameterItemToDelete");
		if (indexOfDatasetParameterItemToDeleteObj != null){
			return true;
		}
		Object indOfDatasetParameterItemToChangeObj = request.getAttribute("indexOfDatasetParameterItemToChange");
		if (indOfDatasetParameterItemToChangeObj != null) {
			return true;
		}
		Object indexOfItemToDown = request.getAttribute("indexOfParameterToDown");
		if (indexOfItemToDown != null) {
			return true;
		}
		Object indexOfItemToUp = request.getAttribute("indexOfParameterToUp");
		if (indexOfItemToUp != null) {
			return true;
		}
		Object insertFixLovItem = request.getAttribute("insertDatasetParameterItem");
		if (insertFixLovItem != null) {
			return true;
		}
		return false;

	}


	private boolean doDatasetParameterItemTask(SpagoBiDataSet dataset, DataSetParametersList datasetlistDet, SourceBean request) throws Exception {
		boolean changeItems = false;
		// checks if it is requested to delete a Fix Lov item
		Object indexOfDatasetParameterItemToDeleteObj = request.getAttribute("indexOfDatasetParameterItemToDelete");
		if (indexOfDatasetParameterItemToDeleteObj != null) {
			// it is requested to delete a Fix Lov item
			int indexOfDatasetParameterItemToDelete = new Integer((String)indexOfDatasetParameterItemToDeleteObj).intValue();
			datasetlistDet = deleteDatasetParameterValue(datasetlistDet, indexOfDatasetParameterItemToDelete);
			changeItems = true;
		}
		// checks if it is requested to change a Fix Lov item
		Object indOfDatasetParameterItemToChangeObj = request.getAttribute("indexOfDatasetParameterItemToChange");
		if (indOfDatasetParameterItemToChangeObj != null) {
			// it is requested to change a Fix Lov item
			int indexOfDatasetParameterItemToChange = new Integer((String)indOfDatasetParameterItemToChangeObj).intValue();
			String newName = (String)request.getAttribute("nameRow"+indexOfDatasetParameterItemToChange+"InpText");
			String newType = (String)request.getAttribute("typeRow"+indexOfDatasetParameterItemToChange+"InpText");
			request.setAttribute("newNameRow", newName);
			request.setAttribute("newTypeRow", newType);
			//ValidationCoordinator.validate("PAGE", "FixLovChangeValidation", this);
			//if(errorHandler.isOKByCategory(EMFErrorCategory.VALIDATION_ERROR)) {
			datasetlistDet = changeDatasetParameterValue(datasetlistDet, indexOfDatasetParameterItemToChange, newName, newType);
			changeItems = true;
			//}
		}
		//	checks if it is requested to move down a Fix Lov item
		Object indexOfItemToDown = request.getAttribute("indexOfParameterToDown");
		if (indexOfItemToDown != null) {
			// it is requested to move down a Fix Lov item
			int indexOfItemToDownInt = new Integer((String)indexOfItemToDown).intValue();
			datasetlistDet = moveDownDatasetParameterItem(datasetlistDet, indexOfItemToDownInt);
			changeItems = true;
		}
		// checks if it is requested to move up a Fix Lov item
		Object indexOfItemToUp = request.getAttribute("indexOfParameterToUp");
		if (indexOfItemToUp != null) {
			// it is requested to move down a Fix Lov item
			int indexOfItemToUpInt = new Integer((String)indexOfItemToUp).intValue();
			datasetlistDet = moveUpDatasetParameterItem(datasetlistDet, indexOfItemToUpInt);
			changeItems = true;
		}
		//	checks if it is requested to add a Fix Lov item
		Object insertFixLovItem = request.getAttribute("insertDatasetParameterItem");
		if (insertFixLovItem != null) {
			//ValidationCoordinator.validate("PAGE", "FixLovWizardValidation", this);
			if(errorHandler.isOKByCategory(EMFErrorCategory.VALIDATION_ERROR)) {
				datasetlistDet = addDatasetParameterItem(request, dataset);
				changeItems = true;
			}
		}
		// if the request was to insert/delete/modify item of the fix list update the lov
		if(changeItems) {
			String pars = datasetlistDet.toXML();
			dataset.setParameters(pars);
		}
		return changeItems;
	}

	/**
	 * Delete from the list of fix lov items the one at index indexOfFixedListItemToDelete
	 * @param lovDetList	The list of Fix Lov
	 * @param indexOfFixedListItemToDelete	The index of the item to be deleted
	 */
	private DataSetParametersList deleteDatasetParameterValue (DataSetParametersList dsDetList, int indexOfDatasetParameterItemToDelete)  {
		List parameters = dsDetList.getItems();
		parameters.remove(indexOfDatasetParameterItemToDelete);
		dsDetList.setPars(parameters);
		return dsDetList;
	}


	/**
	 * Chnage from the list of fix lov items the one at index indexOfFixedListItemToDelete
	 * @param lovDetList	The list of Fix Lov
	 * @param itemToChange	The index of the item to be changed
	 * @param newName the new name of the item
	 * @param newValue the new value of the item
	 */
	private DataSetParametersList changeDatasetParameterValue (DataSetParametersList dsDetList, int itemToChange, 
			String newName, String newType)  {
		List parameters = dsDetList.getItems();
		parameters.remove(itemToChange);
		DataSetParameterItem dsdet = new DataSetParameterItem();
		dsdet.setName(newName);
		dsdet.setType(newType);
		parameters.add(itemToChange, dsdet);
		dsDetList.setPars(parameters);
		return dsDetList;
	}



	/**
	 * Move up an item of the fix lov list
	 * @param lovDetList	The list of Fix Lov
	 * @param indexOfItemToUp	The index of the item to move up
	 */
	private DataSetParametersList moveUpDatasetParameterItem (DataSetParametersList dsDetList, int indexOfItemToUp)  {
		List parameters = dsDetList.getItems();
		Object o = parameters.get(indexOfItemToUp);
		parameters.remove(indexOfItemToUp);
		parameters.add((indexOfItemToUp-1), o);
		dsDetList.setPars(parameters);
		return dsDetList;
	}


	/**
	 * Move down an item of the fix lov list
	 * @param lovDetList	The list of Fix Lov
	 * @param indexOfItemToDown	The index of the item to move down
	 */
	private DataSetParametersList moveDownDatasetParameterItem (DataSetParametersList dsDetList, int indexOfItemToDown)  {
		List parameters = dsDetList.getItems();
		Object o = parameters.get(indexOfItemToDown);
		parameters.remove(indexOfItemToDown);
		parameters.add((indexOfItemToDown+1), o);
		dsDetList.setPars(parameters);
		return dsDetList;
	}


	/**
	 * Inserts a new Fixed LOV item in the FixedLov Wizard. When this type of Input is selected dring the insertion/
	 * modify of a Value in the LOV list, it is possible to add a series of FixLov Values, showed
	 * at runtime in a table.
	 * 
	 * @param request	The request SourceBean
	 * @param modVal	The ModalitiesValue to modify with the new entry
	 * @throws SourceBeanException	If a SourceBean Exception occurred
	 */
	private DataSetParametersList addDatasetParameterItem (SourceBean request, SpagoBiDataSet dataset) throws SourceBeanException {
		String dsPars = dataset.getParameters();
		DataSetParametersList dsDetList = null;
		if ((dsPars==null) || (dsPars.trim().equals(""))) {
			dsDetList = new DataSetParametersList();
		} else {
			dsDetList = DataSetParametersList.fromXML(dsPars);
		}
		String lovName = (String)request.getAttribute("nameOfDatasetParameterItemNew");
		String lovType = (String)request.getAttribute("typeOfDatasetParameterItemNew");
		dsDetList.add(lovName, lovType);
		return dsDetList;
	}


	/**
	 * Sets some attributes into the response SourceBean. Those attributes are required for 
	 * the correct visualization of the ModalitiesValue form page.
	 * 
	 * @param modVal The ModalitiesValue to visualize 
	 * @param mod The modality (insert/modify)
	 * @param response The SourceBean to set
	 * @throws SourceBeanException
	 * @throws EMFUserError
	 * @throws EMFInternalError 
	 */

	private void prepareDetailDatasetPage (SpagoBiDataSet datasetConfig, String mod, SourceBean response) throws SourceBeanException, EMFUserError, EMFInternalError {
		response.setAttribute(DetailDataSetModule.DATASET, datasetConfig);
		response.setAttribute(SpagoBIConstants.MODALITY, mod);	
		loadValuesDomain(response);
		loadAllProfileAttributes(response);

	}

	private void loadAllProfileAttributes(SourceBean response) throws SourceBeanException {
		SourceBean configSingleton = ConfigSingleton.getInstance();
		SourceBean portalSecuritySB = (SourceBean) configSingleton.getAttribute("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS");
		logger.debug(" Portal security class configuration " + portalSecuritySB);
		String portalSecurityClassName = (String) portalSecuritySB.getAttribute("className");
		logger.debug(" Portal security class name: " + portalSecurityClassName);
		if (portalSecurityClassName == null || portalSecurityClassName.trim().equals("")) {
			logger.error(" Portal security class name not set!!!!");
			return;
		}
		portalSecurityClassName = portalSecurityClassName.trim();
		ISecurityInfoProvider portalSecurityProvider = null;
		try {
			portalSecurityProvider = (ISecurityInfoProvider)Class.forName(portalSecurityClassName).newInstance();
		} catch (Exception e) {
			logger.error(" Error while istantiating portal security class '" + portalSecurityClassName + "'.", e);
			return;
		}
		List profileattrs = portalSecurityProvider.getAllProfileAttributesNames();
		response.setAttribute(SpagoBIConstants.PROFILE_ATTRS, profileattrs);
	}

	/**
	 * Loads into the Response Source Bean all the Input Type Domain objects
	 * 
	 * @param response The response Source Bean
	 * @throws EMFUserError If any exception occurred
	 */
	private void loadValuesDomain(SourceBean response)  throws EMFUserError {
		try {
			List list = DAOFactory.getDomainDAO().loadListDomainsByType("INPUT_TYPE");
			response.setAttribute (SpagoBIConstants.LIST_INPUT_TYPE, list);

			List transformers = DAOFactory.getDomainDAO().loadListDomainsByType("TRANSFORMER_TYPE");
			response.setAttribute(LIST_TRANSFORMER, transformers);
		} catch (Exception ex) {
			logger.error("Cannot prepare page for the insertion", ex  );
			throw new EMFUserError(EMFErrorSeverity.ERROR, 1021);
		}
	}

	private void testDatasetAfterParameterFilling(SourceBean request, 	SourceBean response) throws EMFUserError, SourceBeanException  {
		try {
			SpagoBiDataSet ds = null;
			ds = (SpagoBiDataSet) session.getAttribute(DetailDataSetModule.DATASET);
			List parametersFill = getParametersToFill(ds);
			if(parametersFill.size()!=0) {
				Map attributes = new HashMap();
				Iterator iterator = parametersFill.iterator();
				while(iterator.hasNext()) {
					DataSetParameterItem dsItem=(DataSetParameterItem)iterator.next();
					//I must now check the type and pass the right sintax for the type
					String name=dsItem.getName();
					String nameRequest=name+"att";
					String value = (String)request.getAttribute(nameRequest);
					String type=dsItem.getType();
					String valueToPass="";

					if(type.equals("String")){
						//valueToPass="'"+value+"'";

						StringTokenizer st = new StringTokenizer(value);

						String numTemp = "";
						value = "";


						while(st.hasMoreTokens()){

							numTemp = st.nextToken(",");

							value = value + "'"+numTemp+"'";									 
							if(st.hasMoreTokens()){
								value = value + ",";
							}
						}
						valueToPass = value;	 

					}
					else if(type.equals("Number")){
						valueToPass = value;
//						try {
//							StringTokenizer st = new StringTokenizer(value);
//
//							String numTemp = "";
//							Double doubleValue = null;
//							value = "";
//
//
//							while(st.hasMoreTokens()){
//
//								numTemp = st.nextToken(",");
//								doubleValue=new Double(Double.parseDouble(numTemp));
//								value = value + doubleValue.toString();
//
//								if(st.hasMoreTokens()){
//									value = value + ",";
//								}
//							}
//
//
//							valueToPass = value;
//
//
//						} catch (NumberFormatException e) {
//							throw new Exception();
//						}

					}
					else if(type.equals("Date")){
						valueToPass="\'"+value+"\'";
					}


					attributes.put(name, valueToPass);
				}
				session.setAttribute("parametersfilled", attributes);
			}
			response.setAttribute(DetailDataSetModule.PARAMETERS_FILLED,"true");
			response.setAttribute(DetailDataSetModule.TEST, "true");
			return;
		} catch (Exception e) {
			logger.error("Error while creating user profile for test", e);
			errorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, 9200));
			response.setAttribute(DetailDataSetModule.TEST, "true");

			//throw new EMFUserError(EMFErrorSeverity.ERROR, 9200); 
			return;
		}
	}	

}