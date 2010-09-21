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
package it.eng.spagobi.engines.kpi;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.engines.InternalEngineIFace;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;
import it.eng.spagobi.engines.kpi.bo.KpiLine;
import it.eng.spagobi.engines.kpi.bo.KpiLineVisibilityOptions;
import it.eng.spagobi.engines.kpi.bo.KpiResourceBlock;
import it.eng.spagobi.engines.kpi.utils.StyleLabel;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiDocuments;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.KpiRel;
import it.eng.spagobi.kpi.config.bo.KpiValue;
import it.eng.spagobi.kpi.config.dao.KpiDAOImpl;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Chiara Chiarelli
 * 
 */

public class SpagoBIKpiInternalEngine implements InternalEngineIFace {

	private static transient Logger logger = Logger.getLogger(SpagoBIKpiInternalEngine.class);
	
	public static final String messageBundle = "messages";

	private static final String RESOURCE="RES_NAME";

	protected String publisher_Name= "KPI_DEFAULT_PUB";//Kpi default publisher
	protected String metadata_publisher_Name= "KPI_METADATA_DEFAULT_PUB";//Kpi default publisher
	protected String trend_publisher_Name= "TREND_DEFAULT_PUB";//Kpi default publisher
	
	protected String name = "";// Document's title
	protected String subName = "";// Document's subtitle
	protected StyleLabel styleTitle;// Document's title style
	protected StyleLabel styleSubTitle;// Document's subtitle style
	protected String userIdField=null;

	protected Locale locale=null;

	private IEngUserProfile profile=null;
	//internationalized DateFormat
	protected String internationalizedFormat = null;
	//Server dateFormat
	protected String formatServer = null;

	protected HashMap parametersObject;

	protected boolean closed_tree  = false;// true if the kpi tree has to start closed
	
	protected String model_title = "MODEL";// 
	// displayed
	protected String threshold_image_title = null;// 
	// displayed
	protected String bullet_chart_title = null;//
	
	protected String value_title = null;
	// will be displayed
	protected String kpi_title = null;// 
	// will be displayed
	protected String weight_title = null;// 

	protected boolean display_semaphore = true;// true if the semaphore will be
	// displayed
	protected boolean display_bullet_chart = false;// true if the bullet chart
	// displayed
	protected boolean display_threshold_image = false;// true if the bullet chart
	// will be displayed
	protected boolean display_weight = false;// true if the weight will be
	// displayed
	protected boolean display_alarm = false;// true if the alarm state will be
	// displayed
	protected boolean register_values = true;//true if the new values calculated will have to be inserted into the db

	protected boolean recalculate_anyway = false;//true if the scheduler calls the engine and wants the kpi to be calculated even if another value already exists

	protected boolean register_par_setted = false;//true if register_values is setted by SpagoBIparameter and so is more important than the template value

	protected boolean show_axis = false;//true if the range axis on the bullet chart has to be shown
	
	protected boolean weighted_values = false;//true if values visualized have to be weighted

	protected HashMap confMap;// HashMap with all the config parameters

	protected List resources;// List of resources linked to the
	// ModelInstanceNode

	protected Date dateOfKPI = new Date();//date when the kpiValues are requested

	protected Date endKpiValueDate = null;//End validity date for the kpiValues 

	protected Integer periodInstID = null;

	protected Integer modelInstanceRootId = null;
	
	protected String lang = null;
	protected String country = null;

	protected String behaviour = "default";// 4 possible values:
	//default:all old kpiValues are recalculated; all kpivalues without periodicity will not be recalculated
	//display:shows all the last calculated values, even if they are not valid anymore
	//force_recalculation: all the values are recalculated
	//recalculate:old kpiValues are recalculated and also the one without a periodicity

	protected boolean dataset_multires = false;

	protected Date timeRangeFrom = null;//Begin date of range

	protected Date timeRangeTo = null;//End date of range
	
	protected Date dateIntervalFrom = null;//Begin date of range

	protected Date dateIntervalTo = null;//End date of range

	// used to set the return of the execution
	protected List<KpiResourceBlock> kpiResultsList;

	//Method usually called by the scheduler only in order to recalculate kpi values
	public void execute(RequestContainer requestContainer, SourceBean response) throws EMFUserError, SourceBeanException {
		logger.debug("IN");
		//setting locale, formats, profile
		setGeneralVariables(requestContainer);
		this.parametersObject = new HashMap();
		
		String recalculate = (String)requestContainer.getAttribute("recalculate_anyway");
		if(recalculate.equals("true")){
			this.recalculate_anyway = true;
		}	
		// Date for which we want to see the KpiValues
		this.dateOfKPI = (Date)requestContainer.getAttribute("start_date");
		this.endKpiValueDate = (Date)requestContainer.getAttribute("end_date");

		String cascade = (String)requestContainer.getAttribute("cascade");	

		// **************take informations on the modelInstance and its KpiValues*****************
		String modelNodeInstance = (String) requestContainer.getAttribute("model_node_instance");
		logger.info("ModelNodeInstance : " + modelNodeInstance);

		if (modelNodeInstance == null) {
			logger.error("The modelNodeInstance specified in the template is null");
			throw new EMFUserError(EMFErrorSeverity.ERROR, "10106", messageBundle);
		}

		List kpiRBlocks = new ArrayList();// List of KpiValues Trees for each Resource: it will be sent to the jsp
		
		if(!parametersObject.containsKey("ParKpiDate")){
			String dateForDataset = getDateForDataset(dateOfKPI);	
			parametersObject.put("ParKpiDate", dateForDataset);
		}

		// gets the ModelInstanceNode
		ModelInstanceNode mI = DAOFactory.getModelInstanceDAO().loadModelInstanceByLabel(modelNodeInstance, this.dateOfKPI);
		logger.debug("ModelInstanceNode, ID=" + mI.getModelInstanceNodeId());
		modelInstanceRootId = mI.getModelInstanceNodeId();
		logger.debug("Loaded the modelInstanceNode with LABEL " + modelNodeInstance);

		if(dataset_multires){//if datasets return a value for each resource
			this.resources = mI.getResources(); //Set all the Resources for the Model Instance
			logger.info("Dataset multiresource");
			try {
				calculateAndInsertKpiValueWithResources(mI.getModelInstanceNodeId(),this.resources);  	
			} catch (EMFInternalError e) {
				e.printStackTrace();
				logger.error("Error in calculateAndInsertKpiValueWithResources",e);
			}
			logger.info("Inserted all values!!");
			return;    	
		}

		// I set the list of resources of that specific ModelInstance
		if (this.resources == null || this.resources.isEmpty()) {
			this.resources = mI.getResources();
		}
		logger.debug("Setted the List of Resources related to the specified Model Instance");

		KpiLineVisibilityOptions options = setVisibilityOptions();
		
		if (cascade!=null && cascade.equals("true")){//in case all the kpi children have to be calculated too

			try {
				if (this.resources == null || this.resources.isEmpty()) {
					logger.debug("There are no resources assigned to the Model Instance");

					KpiResourceBlock block = new KpiResourceBlock();
					block.setD(this.dateOfKPI);
					KpiLine line = getBlock(mI.getModelInstanceNodeId(), null);				
					block.setRoot(line);
					block.setOptions(options);
					logger.debug("Setted the tree Root.");
					kpiRBlocks.add(block);
				} else {
					Iterator resourcesIt = this.resources.iterator();
					while (resourcesIt.hasNext()) {
						Resource r = (Resource) resourcesIt.next();
						logger.info("-------Resource: " + r.getName());
						KpiResourceBlock block = new KpiResourceBlock();
						block.setR(r);
						block.setD(dateOfKPI);
						block.setOptions(options);
						KpiLine line = getBlock(mI.getModelInstanceNodeId(), r);
						block.setRoot(line);
						logger.debug("Setted the tree Root.");
						kpiRBlocks.add(block);
					}
				}
			} catch (EMFInternalError e) {
				e.printStackTrace();
			}
		}else{//in case all the kpi children don't have to be calculated 
			try {
				KpiInstance kpiI = mI.getKpiInstanceAssociated();
				IDataSet dataSet = DAOFactory.getKpiDAO().getDsFromKpiId(kpiI.getKpi());
				KpiValue value = new KpiValue();
				if (this.resources == null || this.resources.isEmpty()) {
					logger.debug("There are no resources assigned to the Model Instance");
					logger.debug("Retrieved the Dataset to be calculated: " + dataSet.getId());
					value = getNewKpiValue(dataSet, kpiI, null,mI.getModelInstanceNodeId());
					logger.debug("New value calculated");
				} else {
					Iterator resourcesIt = this.resources.iterator();
					while (resourcesIt.hasNext()) {
						Resource r = (Resource) resourcesIt.next();
						logger.debug("Resource: " + r.getName());
						logger.debug("Retrieved the Dataset to be calculated: " + dataSet.getId());
						value = getNewKpiValue(dataSet, kpiI, r,mI.getModelInstanceNodeId());					
					}
				}
				logger.debug("New value calculated");
				// Insert new Value into the DB
				DAOFactory.getKpiDAO().insertKpiValue(value);
				logger.debug("New value inserted in the DB");		
				// Checks if the value is alarming (out of a certain range)
				// If the value is alarming a new line will be inserted in the sbi_alarm_event table and scheduled to be sent
				DAOFactory.getAlarmDAO().isAlarmingValue(value);   	
			} catch (EMFInternalError e) {
				e.printStackTrace();
			}
		}
		logger.debug("OUT");    	
	}

	/**
	 * Executes the document and populates the response.
	 * @param requestContainer
	 *                The <code>RequestContainer</code> object (the session
	 *                can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document to be executed
	 * @param response The response <code>SourceBean</code> to be populated
	 * @throws EMFUserError the EMF user error
	 */

	public void execute(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError {
		logger.debug("IN");

		ResponseContainer responseContainer = ResponseContainer.getResponseContainer();
		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
		//setting locale, formats, profile, parameters, startDate, endDate
		setGeneralVariables(requestContainer);

		if (obj == null) {
			logger.error("The input object is null.");
			throw new EMFUserError(EMFErrorSeverity.ERROR, "100", messageBundle);
		}
		if (!obj.getBiObjectTypeCode().equalsIgnoreCase("KPI")) {
			logger.error("The input object is not a KPI.");
			throw new EMFUserError(EMFErrorSeverity.ERROR, "1001", messageBundle);
		}
		String userId = null;

		if(profile!=null){
			userId=(String) ((UserProfile) profile).getUserId();
		}
		else{
			userId=userIdField;
		}
		
		String documentId = obj.getId().toString();
		logger.debug("Loaded documentId:" + documentId);		

		try {
			// **************get the template*****************		
			SourceBean content = getTemplate(documentId);
			logger.debug("Got the template.");

			// Date for which we want to see the KpiValues
			this.dateOfKPI = new Date();
			this.parametersObject = readParameters(obj.getBiObjectParameters());
			if(!parametersObject.containsKey("ParKpiDate")){
				String dateForDataset = getDateForDataset(dateOfKPI);	
				parametersObject.put("ParKpiDate", dateForDataset);
			}
			logger.debug("Got the date for which the KpiValues have to be calculated. Date:" + this.dateOfKPI);

			// **************take informations on the modelInstance and its KpiValues*****************
			String modelNodeInstance = (String) content.getAttribute("model_node_instance");
			logger.info("ModelNodeInstance : " + modelNodeInstance);

			if (modelNodeInstance == null) {
				logger.error("The modelNodeInstance specified in the template is null");
				throw new EMFUserError(EMFErrorSeverity.ERROR, "10106", messageBundle);
			}
			String periodInstanceID = (String) content.getAttribute("periodicity_id");
			logger.debug("PeriodInstanceID : " + (periodInstanceID!=null ? periodInstanceID : "null"));

			if (periodInstanceID == null) {
				logger.debug("No periodInstID specified will use default one");
			}else{
				periodInstID = new Integer(periodInstanceID);
			}
			getSetConf(content);
			logger.debug("Setted the configuration of the template");

			List kpiRBlocks = new ArrayList();// List of KpiValues Trees for each Resource: it will be sent to the jsp

			// gets the ModelInstanceNode
			ModelInstanceNode mI = DAOFactory.getModelInstanceDAO().loadModelInstanceByLabel(modelNodeInstance, this.dateOfKPI);
			if (mI==null) {
				logger.error("MODEL INSTANCE IS NULL, CHECK model_node_instance IN DOCUMENT TEMPLATE.!!!!!!!!!!!!!!");
			}else {
				logger.debug("ModelInstanceNode, ID=" + (mI.getModelInstanceNodeId()!=null ? mI.getModelInstanceNodeId().toString():"null"));
				modelInstanceRootId = (mI.getModelInstanceNodeId()!=null ? mI.getModelInstanceNodeId() : null );
				logger.debug("Loaded the modelInstanceNode with LABEL " + modelNodeInstance);
			}

			if(dataset_multires){//if datasets return a value for each resource
				this.resources = mI.getResources(); //Set all the Resources for the Model Instance
				logger.info("Dataset multiresource");
				calculateAndInsertKpiValueWithResources(mI.getModelInstanceNodeId(),this.resources);  	
				logger.info("Inserted all values!!");
				return;    	
			}

			// I set the list of resources of that specific ModelInstance
			if (this.resources == null || this.resources.isEmpty()) {
				this.resources = mI.getResources();
			}
			logger.debug("Setted the List of Resources related to the specified Model Instance");

			KpiLineVisibilityOptions options = setVisibilityOptions();
			
			//sets up register values
			ModelInstanceNode modI = DAOFactory.getModelInstanceDAO().loadModelInstanceById(mI.getModelInstanceNodeId(), dateOfKPI);

			logger.debug("Setted the List of Kpis that does not need to be persisted in db");
			if (this.resources == null || this.resources.isEmpty()) {
				logger.debug("There are no resources assigned to the Model Instance");
				KpiResourceBlock block = new KpiResourceBlock();
				block.setD(this.dateOfKPI);
				block.setParMap(this.parametersObject);
				KpiLine line = getBlock(mI.getModelInstanceNodeId(), null);
				block.setRoot(line);
				block.setTitle(name);
				block.setSubtitle(subName);
				block.setOptions(options);
				logger.debug("Setted the tree Root.");
				kpiRBlocks.add(block);

			}else {
				Iterator resourcesIt = this.resources.iterator();
				while (resourcesIt.hasNext()) {
					Resource r = (Resource) resourcesIt.next();
					logger.info("Resource: " + r.getName());
					KpiResourceBlock block = new KpiResourceBlock();
					block.setR(r);
					block.setD(dateOfKPI);
					block.setParMap(this.parametersObject);
					KpiLine line = getBlock(mI.getModelInstanceNodeId(), r);
					block.setRoot(line);
					block.setOptions(options);
					logger.debug("Setted the tree Root.");
					kpiRBlocks.add(block);
				}
			}

			try {
				logger.debug("Successfull kpis creation");

				response.setAttribute(ObjectsTreeConstants.SESSION_OBJ_ATTR, obj);
				response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, publisher_Name);
				response.setAttribute("metadata_publisher_Name", metadata_publisher_Name);
				response.setAttribute("trend_publisher_Name", trend_publisher_Name);
	
				if (name != null) {
					response.setAttribute("title", name);
					response.setAttribute("styleTitle", styleTitle);
				}
				if (subName != null) {
					response.setAttribute("subName", subName);
					response.setAttribute("styleSubTitle", styleSubTitle);
				}
				response.setAttribute("kpiRBlocks", kpiRBlocks);
				kpiResultsList = kpiRBlocks;
			} catch (Exception eex) {
				EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 10107);
				userError.setBundle("messages");
				throw userError;
			}

			
			logger.debug("OUT");
		} catch (EMFUserError e) {
			errorHandler.addError(e);
		} catch (Exception e) {
			EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 101);
			logger.error("Generic Error", e);
			errorHandler.addError(userError);
		}
	}
	
	private SourceBean getTemplate(String documentId) throws EMFUserError{
		logger.debug("IN");
		SourceBean content = null;
		byte[] contentBytes = null;
		try {
			ObjTemplate template = DAOFactory.getObjTemplateDAO().getBIObjectActiveTemplate(Integer.valueOf(documentId));
			if (template == null) {
				logger.warn("Active Template null.");
				throw new Exception("Active Template null.");
			}
			contentBytes = template.getContent();
			if (contentBytes == null) {
				logger.warn("Content of the Active template null.");
				throw new Exception("Content of the Active template null");
			}
			// get bytes of template and transform them into a SourceBean
			String contentStr = new String(contentBytes);
			content = SourceBean.fromXMLString(contentStr);
			logger.debug("Got the content of the template");
		} catch (Exception e) {
			logger.error("Error while converting the Template bytes into a SourceBean object");
			EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 2003);
			userError.setBundle("messages");
			throw userError;
		}
		logger.debug("OUT");
		return content;
	}

	private void setGeneralVariables(RequestContainer requestContainer){
		logger.debug("IN");
		SessionContainer session = requestContainer.getSessionContainer();
		profile = (IEngUserProfile) session.getPermanentContainer().getAttribute(
				IEngUserProfile.ENG_USER_PROFILE);

		locale=GeneralUtilities.getDefaultLocale();
		lang=(String)session.getPermanentContainer().getAttribute(SpagoBIConstants.AF_LANGUAGE);
		country=(String)session.getPermanentContainer().getAttribute(SpagoBIConstants.AF_COUNTRY);
		if(lang!=null && country!=null){
			locale=new Locale(lang,country,"");
		}
		
		SourceBean internationalizedFormatSB = null; 
		if(lang!=null && country!=null){
			internationalizedFormatSB = ((SourceBean)ConfigSingleton.getInstance().getAttribute("SPAGOBI.DATE-FORMAT-"+lang.toUpperCase()+"_"+country.toUpperCase()));				
		}else{
			internationalizedFormatSB = ((SourceBean)ConfigSingleton.getInstance().getAttribute("SPAGOBI.DATE-FORMAT-SERVER"));
		}
		internationalizedFormat = (String) internationalizedFormatSB.getAttribute("format");	
		SourceBean formatServerSB = ((SourceBean)ConfigSingleton.getInstance().getAttribute("SPAGOBI.DATE-FORMAT-SERVER"));
		formatServer = (String) formatServerSB.getAttribute("format");

		logger.debug("OUT");
	}
	
	
	/**
	 * 
	 * @param 
	 * @param resources
	 * @throws EMFUserError
	 * @throws EMFInternalError
	 */
	public List<KpiResourceBlock> executeCode(RequestContainer requestContainer, BIObject obj, SourceBean response, String userId) throws EMFUserError {   
		logger.debug("IN");
		userIdField=userId;
		this.execute(requestContainer, obj, response);
		if(kpiResultsList==null){
			logger.error("error while executing KPI");
			return null;
		}
		else{
			logger.error("Kpi executed succesfully");
			logger.debug("OUT");
			return kpiResultsList;
		}

	}
	
	
	private KpiLineVisibilityOptions setVisibilityOptions(){
		logger.debug("IN");
		KpiLineVisibilityOptions options = new KpiLineVisibilityOptions();
		options.setClosed_tree(closed_tree);
		options.setDisplay_alarm(display_alarm);
		options.setDisplay_bullet_chart(display_bullet_chart);
		options.setDisplay_semaphore(display_semaphore);
		options.setDisplay_threshold_image(display_threshold_image);
		options.setDisplay_weight(display_weight);
		options.setShow_axis(show_axis);
		options.setWeighted_values(weighted_values);
		
		options.setBullet_chart_title(bullet_chart_title);
		options.setKpi_title(kpi_title);
		options.setModel_title(model_title);
		options.setThreshold_image_title(threshold_image_title);
		options.setWeight_title(weight_title);
		options.setValue_title(value_title);
		logger.debug("OUT");
		return options;
	}

	public void calculateAndInsertKpiValueWithResources(Integer miId,List resources)throws EMFUserError, EMFInternalError, SourceBeanException {
		logger.debug("IN");
		ModelInstanceNode modI =  DAOFactory.getModelInstanceDAO().loadModelInstanceById(miId, dateOfKPI);
		if (modI != null) {
			logger.info("Loaded Model Instance Node with id: " + modI.getModelInstanceNodeId());
		}

		List childrenIds = modI.getChildrenIds();
		if (!childrenIds.isEmpty()) {
			Iterator childrenIt = childrenIds.iterator();
			while (childrenIt.hasNext()) {
				Integer id = (Integer) childrenIt.next();	
				calculateAndInsertKpiValueWithResources(id, resources);
			}
		}
		KpiInstance kpiI = modI.getKpiInstanceAssociated();
		if (kpiI != null) {
			KpiValue kVal = new KpiValue();
			logger.info("Got KpiInstance with ID: " + kpiI.getKpiInstanceId().toString());
			IDataSet dataSet = DAOFactory.getKpiDAO().getDsFromKpiId(kpiI.getKpi());	
			logger.info("Retrieved the Dataset to be calculated: " + (dataSet!=null ? dataSet.getId():"null"));
			Integer kpiInstanceID = kpiI.getKpiInstanceId();
			Date kpiInstBegDt = kpiI.getD();

			kVal = setTimeAttributes(kVal, kpiI);		
			kVal.setKpiInstanceId(kpiInstanceID);
			logger.debug("Setted the KpiValue Instance ID:"+kpiInstanceID);	

			if ( (dateOfKPI.after(kpiInstBegDt)||dateOfKPI.equals(kpiInstBegDt))) {
				//kpiInstance doesn't change
			}else{
				KpiInstance tempKIn = DAOFactory.getKpiInstanceDAO().loadKpiInstanceByIdFromHistory(kpiInstanceID,dateOfKPI);
				if(tempKIn==null){//kpiInstance doesn't change
				}else{
					// in case older thresholds have to be retrieved
					kpiI = tempKIn;
				}
			}

			kVal = getFromKpiInstAndSetKpiValueAttributes(kpiI,kVal);
			
			// If it has to be calculated for a Resource. The resource will be set as parameter
			HashMap temp = (HashMap) this.parametersObject.clone();
			temp.put("ParModelInstance", miId);	
			temp.put("ParKpiInstance", kpiInstanceID.toString());
			// If not, the dataset will be calculated without the parameter Resource
			// and the DataSet won't expect a parameter of type resource
			//if(dataSet.hasBehaviour( QuerableBehaviour.class.getName()) ) {
			if(dataSet!=null){
				Kpi kpi = DAOFactory.getKpiDAO().loadKpiById(kpiI.getKpi());
				kVal = recursiveGetKpiValueFromKpiRel(kpi,dataSet,temp,kVal,dateOfKPI,kVal.getEndDate());
				kVal = getKpiValueFromDataset(dataSet,temp,kVal,dateOfKPI,kVal.getEndDate(), true);
			}
		} 	
		logger.debug("OUT");
	}

	public KpiLine getBlock(Integer miId, Resource r) throws EMFUserError, EMFInternalError, SourceBeanException {
		logger.debug("IN");
		KpiLine line = new KpiLine();
		ModelInstanceNode modI = DAOFactory.getModelInstanceDAO().loadModelInstanceById(miId, dateOfKPI);
		if (modI != null) {
			logger.info("Loaded Model Instance Node with id: " + modI.getModelInstanceNodeId());
		}
		String modelNodeName = modI.getName();
		line.setModelNodeName(modelNodeName);
		line.setModelInstanceNodeId(miId);
		line.setModelInstanceCode(modI.getModelCode());

		List children = new ArrayList();
		List childrenIds = modI.getChildrenIds();
		if (!childrenIds.isEmpty()) {
			Iterator childrenIt = childrenIds.iterator();
			while (childrenIt.hasNext()) {
				Integer id = (Integer) childrenIt.next();	
				KpiLine childrenLine = getBlock(id, r);
				children.add(childrenLine);
			}
		}

		KpiInstance kpiI = modI.getKpiInstanceAssociated();
		//if true the kpi value will always use the display behaviour
		boolean alreadyExistent = false;
		
		if (kpiI == null && modI.getModelInstaceReferenceLabel() != null){
	        ModelInstanceNode modelInstanceRefered = DAOFactory.getModelInstanceDAO().loadModelInstanceByLabel(modI.getModelInstaceReferenceLabel(), dateOfKPI);
	        alreadyExistent = true;
	        if (modelInstanceRefered != null && modelInstanceRefered.getKpiInstanceAssociated() != null){
	           kpiI = modelInstanceRefered.getKpiInstanceAssociated();
	           modI.setKpiInstanceAssociated(kpiI);
	        }
	    }
		
		
		line.setChildren(children);
		if (kpiI != null) {
			Integer kpiInstID = kpiI.getKpiInstanceId();
			logger.info("Got KpiInstance with ID: " + kpiInstID.toString());
			KpiValue value = getValueDependingOnBehaviour(kpiI, miId, r,alreadyExistent );
			line.setValue(value);
			Integer kpiId = kpiI.getKpi();
			Kpi k = DAOFactory.getKpiDAO().loadKpiById(kpiId);
			logger.debug("Retrieved the kpi with id: " + kpiId.toString());

			if (k != null) {
				List docs = k.getSbiKpiDocuments();
				Iterator it = docs.iterator();
				List documents = new ArrayList();
				while(it.hasNext()){
					KpiDocuments doc = (KpiDocuments)it.next();
					String docLabel = doc.getBiObjLabel();
					if (docLabel != null && !docLabel.equals("")) {						
						logger.debug("Retrieved documents associated to the KPI");
						documents.add(docLabel);						
					}
				}
				line.setDocuments(documents);
				
			}
			if (display_alarm && value!=null && value.getValue()!= null) {
				Boolean alarm = DAOFactory.getKpiInstanceDAO().isKpiInstUnderAlramControl(kpiInstID);
				logger.debug("KPI is under alarm control: " + alarm.toString());
				line.setAlarm(alarm);
			}
		}

		logger.debug("OUT");
		return line;
	}
	
	private KpiValue getValueDependingOnBehaviour(KpiInstance kpiI,Integer miId, Resource r, boolean alreadyExistent) throws EMFUserError, EMFInternalError, SourceBeanException{
		logger.debug("IN");
		KpiValue value = new KpiValue();
		boolean no_period_to_period = false;

		if(alreadyExistent){//use diplay behaviour since value already exists
			logger.debug("Display behaviour");
			value = DAOFactory.getKpiDAO().getDisplayKpiValue(kpiI.getKpiInstanceId(), dateOfKPI, r);
			logger.debug("Old KpiValue retrieved it could be still valid or not");
		}else{	
			if(behaviour.equalsIgnoreCase("timeIntervalDefault")){
				if(dateIntervalFrom!=null && dateIntervalTo!=null){
					value = DAOFactory.getKpiDAO().getKpiValueFromInterval(kpiI.getKpiInstanceId(), dateIntervalFrom, dateIntervalTo, r);
					if (value==null){
						IDataSet dataSet = DAOFactory.getKpiDAO().getDsFromKpiId(kpiI.getKpi());	
						logger.info("Retrieved the Dataset to be calculated: " + (dataSet!=null ? dataSet.getId():"null"));
						value = getNewKpiValue(dataSet, kpiI, r, miId);		
					}
				}else{
					value = null;
				}
			}else if(behaviour.equalsIgnoreCase("timeIntervalForceRecalculation")){
				if(dateIntervalFrom!=null && dateIntervalTo!=null){
					DAOFactory.getKpiDAO().deleteKpiValueFromInterval(kpiI.getKpiInstanceId(), dateIntervalFrom, dateIntervalTo, r);
					IDataSet dataSet = DAOFactory.getKpiDAO().getDsFromKpiId(kpiI.getKpi());	
					logger.info("Retrieved the Dataset to be calculated: " + (dataSet!=null ? dataSet.getId():"null"));
					value = getNewKpiValue(dataSet, kpiI, r, miId);		
				}else{
					value = null;
				}
			}else{
				if(behaviour.equalsIgnoreCase("default") || //If the behaviour is default
						(behaviour.equalsIgnoreCase("recalculate") && kpiI.getPeriodicityId()!=null)){//or the behaviour is recalculate and the kpiinstance has a setted periodicity
					// the old value still valid is retrieved
					logger.debug("Trying to retrieve the old value still valid");
					value = DAOFactory.getKpiDAO().getKpiValue(kpiI.getKpiInstanceId(), dateOfKPI, r);
					logger.debug("Old KpiValue retrieved");		
				}
				if(value!=null && value.getEndDate()!=null && kpiI.getPeriodicityId()!=null){
					GregorianCalendar c2 = new GregorianCalendar();		
					c2.setTime(value.getEndDate());
					int year = c2.get(1);
					if(year==9999){
						no_period_to_period = true;
						logger.debug("The periodicity was null and now exists");
					}
				}
				if (value==null || //If the retrieved value is null
						no_period_to_period || //or the periodicity was before null and now is setted
						behaviour.equalsIgnoreCase("force_recalculation") || //or the  behaviour is force_recalculation
						(behaviour.equalsIgnoreCase("recalculate") && kpiI.getPeriodicityId()==null) ) {// or the behaviour is recalculate and the kpiinstance hasn't a setted periodicity
					//a new value is calculated
					logger.debug("Old value not valid anymore or recalculation forced");
					IDataSet dataSet = DAOFactory.getKpiDAO().getDsFromKpiId(kpiI.getKpi());	
					logger.info("Retrieved the Dataset to be calculated: " + (dataSet!=null ? dataSet.getId():"null"));
					value = getNewKpiValue(dataSet, kpiI, r, miId);		
		
				}else if(behaviour.equalsIgnoreCase("display")){//diplay behaviour
					logger.debug("Display behaviour");
					value = DAOFactory.getKpiDAO().getDisplayKpiValue(kpiI.getKpiInstanceId(), dateOfKPI, r);
					logger.debug("Old KpiValue retrieved it could be still valid or not");
				} 
			}
		}
		logger.debug("OUT");
		return value;
	}

	public KpiValue getNewKpiValue(IDataSet dataSet, KpiInstance kpiInst, Resource r, Integer modelInstanceId) throws EMFUserError, EMFInternalError, SourceBeanException {

		logger.debug("IN");
		Integer kpiInstanceID = kpiInst.getKpiInstanceId();
		Date kpiInstBegDt = kpiInst.getD();
		
		KpiValue kVal = new KpiValue();
		kVal = setTimeAttributes(kVal, kpiInst);		
		kVal.setKpiInstanceId(kpiInstanceID);
		logger.debug("Setted the KpiValue Instance ID:"+kpiInstanceID);	
		logger.debug("kpiInstBegDt begin date: "+(kpiInstBegDt!=null ? kpiInstBegDt.toString(): "Begin date null"));

		if ( (dateOfKPI.after(kpiInstBegDt)||dateOfKPI.equals(kpiInstBegDt))) {
			//kpiInstance doesn't change
		}else{
			KpiInstance tempKIn = DAOFactory.getKpiInstanceDAO().loadKpiInstanceByIdFromHistory(kpiInstanceID,dateOfKPI);
			if(tempKIn==null){//kpiInstance doesn't change
			}else{
				// in case older thresholds have to be retrieved
				kpiInst = tempKIn;
			}
		}

		kVal = getFromKpiInstAndSetKpiValueAttributes(kpiInst,kVal);

		// If it has to be calculated for a Resource. The resource will be set
		// as parameter
		HashMap temp = (HashMap) this.parametersObject.clone();
		if (r != null) {
			String colName = r.getColumn_name();
			String value = r.getName();
			String code = r.getCode();
			kVal.setR(r);
			logger.info("Setted the Resource:"+r.getName());
			temp.put("ParKpiResource", value);
			temp.put("ParKpiResourceCode", code);
		}
		// cast Integer Ids to String
		temp.put("ParModelInstance", modelInstanceId.toString());
		temp.put("ParKpiInstance", kpiInstanceID.toString());
		// If not, the dataset will be calculated without the parameter Resource
		// and the DataSet won't expect a parameter of type resource
		//if(dataSet.hasBehaviour( QuerableBehaviour.class.getName()) ) {
		if(dataSet!=null){
			Kpi kpi = DAOFactory.getKpiDAO().loadKpiById(kpiInst.getKpi());
			kVal = recursiveGetKpiValueFromKpiRel(kpi,dataSet,temp,kVal,dateOfKPI,kVal.getEndDate());
			if(behaviour.equalsIgnoreCase("timeIntervalDefault") || behaviour.equalsIgnoreCase("timeIntervalForceRecalculation")){
				if(dateIntervalFrom!=null && dateIntervalTo!=null){
					kVal.setBeginDate(dateIntervalFrom);
					kVal.setEndDate(dateIntervalTo);
				}
			}
			kVal = getKpiValueFromDataset(dataSet,temp,kVal,dateOfKPI,kVal.getEndDate(), true);
		}
		logger.debug("OUT");
		return kVal;
	}
	
	
	private KpiValue setTimeAttributes(KpiValue kVal, KpiInstance kpiInst) throws EMFUserError{
		logger.debug("IN");
		Date begD = dateOfKPI;
		Date endDate = null;
		kVal.setBeginDate(begD);
		logger.debug("Setted the KpiValue begin Date:"+begD);
		if(endKpiValueDate!=null){
			endDate = endKpiValueDate;
			kVal.setEndDate(endKpiValueDate);
		}else if(kpiInst.getPeriodicityId()!=null){
			Integer seconds = null;
			if(periodInstID!=null){
				kpiInst.setPeriodicityId(periodInstID);
				logger.debug("Setted new Periodicity ID:"+periodInstID.toString());
			}
			seconds = DAOFactory.getPeriodicityDAO().getPeriodicitySeconds(kpiInst.getPeriodicityId());
			// Transforms seconds into milliseconds
			long milliSeconds = seconds.longValue() * 1000;
			long begDtTime = begD.getTime();
			long endTime = begDtTime + milliSeconds;
			endDate = new Date(endTime);
			kVal.setEndDate(endDate);
		}else{
			GregorianCalendar c = new GregorianCalendar();
			c.set(9999, 11, 31);			
			endDate = c.getTime();
			kVal.setEndDate(endDate);
		}
		logger.debug("Setted the KpiValue end Date:"+endDate);
		logger.debug("OUT");
		return kVal;
	}
	
	private KpiValue getFromKpiInstAndSetKpiValueAttributes(KpiInstance kpiInst,KpiValue kVal) throws EMFUserError{
		logger.debug("IN");
		Double weight = null;
		Double target = null;
		String scaleCode = null;
		String scaleName = null;
		List thresholdValues = null;
		String chartType = null;
		if (kpiInst!=null){
			Integer thresholdId = kpiInst.getThresholdId();
			if (thresholdId!=null) thresholdValues = DAOFactory.getThresholdValueDAO().loadThresholdValuesByThresholdId(thresholdId);
			chartType = "BulletGraph";
			logger.debug("Requested date d: "+dateOfKPI.toString()+" in between beginDate and EndDate");
			weight = kpiInst.getWeight();
			logger.debug("SbiKpiValue weight: "+(weight!=null ? weight.toString() : "weight null"));
			target = kpiInst.getTarget();
			scaleCode = kpiInst.getScaleCode();
			logger.debug("SbiKpiValue scaleCode: "+(scaleCode!=null ? scaleCode : "scaleCode null"));
			scaleName =kpiInst.getScaleName();
			logger.debug("SbiKpiValue scaleName: "+(scaleName!=null ? scaleName : "scaleName null"));
		}
		kVal.setWeight(weight);
		logger.debug("Setted the KpiValue weight:"+weight);	
		kVal.setThresholdValues(thresholdValues);
		logger.debug("Setted the KpiValue thresholds");	
		kVal.setScaleCode(scaleCode);
		logger.debug("Kpi value scale Code setted");
		kVal.setScaleName(scaleName);
		logger.debug("Kpi value scale Name setted");
		kVal.setTarget(target);
		logger.debug("Kpi value target setted");
		if (chartType != null) kVal.setChartType(chartType);
		logger.debug("OUT");
		return kVal;
	}

	public KpiValue recursiveGetKpiValueFromKpiRel(Kpi kpiParent, IDataSet dataSet, HashMap pars, KpiValue kVal, Date begD, Date endDate) throws EMFUserError, EMFInternalError, SourceBeanException{
		logger.debug("IN");
		List <KpiRel> relations = DAOFactory.getKpiDAO().loadKpiRelListByParentId(kpiParent.getKpiId());
		logger.info("extracts relations for kpi parent : "+kpiParent.getKpiName());
		KpiDAOImpl kpiDao = (KpiDAOImpl)DAOFactory.getKpiDAO();
		for(int i= 0; i< relations.size(); i++){
			KpiRel rel = relations.get(i);
			Kpi child = rel.getKpiChild();
			IDataSet chDataSet = kpiDao.getDsFromKpiId(child.getKpiId());
			HashMap chPars  = new HashMap();
			chPars.putAll(pars);
			//then the one in rel table
			String parameter = rel.getParameter();
			KpiValue kpiVal = recursiveGetKpiValueFromKpiRel(child, chDataSet, chPars, kVal, begD, endDate);
			pars.put(parameter, kpiVal.getValue());
			
		}
		//checks if it is to recalculate
		//calculate with dataset
		KpiValue value = getKpiValueFromDataset(dataSet, pars, kVal, begD, endDate, false);
		logger.debug("gets value from dataset : "+value.getValue());
		logger.debug("OUT");
		return value;
	}
	
	public KpiValue getKpiValueFromDataset(IDataSet dataSet, HashMap pars, KpiValue kVal, Date begD, Date endDate, boolean doSave) throws EMFUserError, EMFInternalError, SourceBeanException{
		logger.debug("IN");
		KpiValue kpiValTemp = null;
		dataSet.setParamsMap(pars);
		dataSet.setUserProfile(profile);	
		logger.info("Load Data Set. Label="+dataSet.getLabel());
		dataSet.loadData();
		IDataStore dataStore = dataSet.getDataStore();
		logger.debug("Got the datastore");
		
		if (dataStore != null && !dataStore.isEmpty()) {
			// Transform result into KPIValue (I suppose that the result has a unique value)
			IDataStoreMetaData d = dataStore.getMetaData();		
			int indexRes = d.getFieldIndex(RESOURCE);

			if(indexRes!=-1){
				Iterator it = dataStore.iterator();
				while(it.hasNext()){

					kpiValTemp = kVal.clone();
					IRecord record =(IRecord)it.next();
					List fields = record.getFields();
					kpiValTemp = setKpiValuesFromDataset(kpiValTemp,fields,d, begD, endDate);
					
					if (kpiValTemp.getR()!=null && kVal.getR()!=null && kpiValTemp.getR().getId()!=null && 
							kVal.getR().getId()!=null && kpiValTemp.getR().getId().equals(kVal.getR().getId())){
						kVal = kpiValTemp.clone() ;
					}
					logger.debug("New value calculated");
					if(register_values && kpiValTemp.getR().getName()!=null){
						
						if(doSave){
							// Insert new Value into the DB
							Integer kpiValueId = DAOFactory.getKpiDAO().insertKpiValue(kpiValTemp);
							kVal.setKpiValueId(kpiValueId);
							logger.info("New value inserted in the DB. Resource="+kpiValTemp.getR().getName()+" KpiInstanceId="+kpiValTemp.getKpiInstanceId());
						}
						// Checks if the value is alarming (out of a certain range)
						// If the value is alarming a new line will be inserted in the
						// sbi_alarm_event table and scheduled to be sent
						DAOFactory.getAlarmDAO().isAlarmingValue(kpiValTemp);
						logger.debug("Alarms sent if the value is over the thresholds");
					}
								

				} 					
			}else{

				IRecord record = dataStore.getRecordAt(0);
				List fields = record.getFields();
				kVal = setKpiValuesFromDataset(kVal,fields,d, begD, endDate);
				logger.debug("New value calculated");
				if(register_values){
					if(doSave){
						// Insert new Value into the DB
						Integer kpiValueId =DAOFactory.getKpiDAO().insertKpiValue(kVal);
						kVal.setKpiValueId(kpiValueId);
						logger.debug("New value inserted in the DB");
					}
				}			
				// Checks if the value is alarming (out of a certain range)
				// If the value is alarming a new line will be inserted in the
				// sbi_alarm_event table and scheduled to be sent
				DAOFactory.getAlarmDAO().isAlarmingValue(kVal);
				logger.debug("Alarms sent if the value is over the thresholds");
			}
		} else {
			logger.warn("The Data Set doesn't return any value!!!!!");
			if(register_values){
				if(doSave){
					// Insert new Value into the DB
					Integer kpiValueId =DAOFactory.getKpiDAO().insertKpiValue(kVal);
					kVal.setKpiValueId(kpiValueId);
					logger.debug("New value inserted in the DB");
				}
			}		
			DAOFactory.getAlarmDAO().isAlarmingValue(kVal);
			logger.debug("Alarms sent if the value is over the thresholds");
		}
		logger.debug("OUT");
		
		return kVal;
	}
	
	private KpiValue setKpiValuesFromDataset(KpiValue kpiValueToReturn, List fields,IDataStoreMetaData d, Date begD, Date endDate ) throws EMFUserError, SourceBeanException{
		int length = fields.size();
		String xmlData = null;
		String tempXMLroot = "<XML_DATA></XML_DATA>";
		SourceBean dsValuesXml = SourceBean.fromXMLString(tempXMLroot);
		for(int fieldIndex =0; fieldIndex<length; fieldIndex++){

			IField f = (IField)fields.get(fieldIndex);			
			if (f != null) {
				if (f.getValue() != null) {
					String fieldName = d.getFieldName(fieldIndex);	  
					if (fieldName.equalsIgnoreCase("DESCR")){
						String descr = f.getValue().toString();
						kpiValueToReturn.setValueDescr(descr);
						logger.debug("Setted the kpiValue description:"+descr);
					}else if(fieldName.equalsIgnoreCase("END_DATE")){
						String endD = f.getValue().toString();
						String format = "dd/MM/yyyy hh:mm:ss";
						SimpleDateFormat form = new SimpleDateFormat();
						form.applyPattern(format);
						try {
							endDate = form.parse(endD);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(endDate!=null && endDate.after(begD)) {				 
							kpiValueToReturn.setEndDate(endDate);
							logger.debug("Setted the new EndDate description:"+endD.toString());
						}
					}else if(fieldName.equalsIgnoreCase("VALUE")){
						String fieldValue = f.getValue().toString();
						kpiValueToReturn.setValue(fieldValue);
						logger.debug("Setted the kpiValue value:"+fieldValue);
					}   
					else if(fieldName.equalsIgnoreCase("XML_DATA")){
						xmlData = f.getValue().toString();
						kpiValueToReturn.setValueXml(xmlData);
						logger.debug("Setted the kpiValue xmlData:"+xmlData);
					}   
					else if(fieldName.equalsIgnoreCase(RESOURCE)){
						String fieldValue = f.getValue().toString();
						if (fieldValue!=null){
							Resource rTemp = DAOFactory.getResourceDAO().loadResourcesByNameAndModelInst(fieldValue);
							kpiValueToReturn.setR(rTemp);
							logger.info("Setted the kpiValue Resource with resource name:"+fieldValue);
						}
					} else{
						String fieldValue = f.getValue().toString();
						if (fieldValue!=null){
							dsValuesXml.setAttribute(fieldName, fieldValue);
						}
					}
				}
			}
		}
		if(xmlData==null && dsValuesXml!=null){
			xmlData = dsValuesXml.toXML(false);
			kpiValueToReturn.setValueXml(xmlData);
			logger.debug("Setted the kpiValue xmlData:"+xmlData);
		}
		return kpiValueToReturn;
	}
	

	private HashMap readParameters(List parametersList) throws EMFUserError {
		logger.debug("IN");
		if (parametersList == null) {
			logger.warn("parametersList si NULL!!!");
			return new HashMap();
		}
		HashMap parametersMap = new HashMap();
		logger.debug("Check for BIparameters and relative values");

		for (Iterator iterator = parametersList.iterator(); iterator.hasNext();) {
			SimpleDateFormat f = new SimpleDateFormat();
			f.applyPattern(formatServer);
			BIObjectParameter par = (BIObjectParameter) iterator.next();
			String url = par.getParameterUrlName();
			List values = par.getParameterValues();
			if (values != null) {
				if (values.size() == 1) {
					if (url.equals("ParKpiResources")) {
						this.resources = new ArrayList();
						String value = (String) values.get(0);
						Integer res = new Integer(value);
						Resource toAdd = DAOFactory.getResourceDAO().loadResourceById(res);
						this.resources.add(toAdd);
					}else if (url.equals("ParKpiResourcesCode")) {
						this.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceByCode(value);
							this.resources.add(toAdd);
						}
					}else if(url.equals("register_values")){
						String value = (String) values.get(0);
						if (value.equalsIgnoreCase("true")){
							this.register_values = true;
							this.register_par_setted = true;
						}else if (value.equalsIgnoreCase("false")){
							this.register_values = false;
							this.register_par_setted = true;
						}
					}else if (url.equals("behaviour")){
						String value = (String) values.get(0);
						behaviour = value;
						logger.debug("Behaviour is: "+ behaviour);
					}else if(url.equals("dataset_multires")){
						String value = (String) values.get(0);
						if (value.equalsIgnoreCase("true")){
							this.dataset_multires = true;
						}else if (value.equalsIgnoreCase("false")){
							this.dataset_multires = false;
						}
					}else{
						String value = (String) values.get(0);	
						if (url.equals("ParKpiDate")) {
							value = setCalculationDateOfKpi(value);		
						}else if (url.equals("TimeRangeFrom")) {
							try {
								timeRangeFrom = f.parse(value);
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE FROM");
						}else if (url.equals("TimeRangeTo")) {
							try {
								timeRangeTo = f.parse(value);
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}else if(url.equals("dateIntervalFrom")){
							try {
								dateIntervalFrom = f.parse(value);
								value = getDateForDataset(dateIntervalFrom);
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}else if(url.equals("dateIntervalTo")){
							try {
								dateIntervalTo = f.parse(value);
								value = getDateForDataset(dateIntervalTo);
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}
						parametersMap.put(url, value);
					}   
					//instead if parameter has more than one value
				}else if (values != null && values.size() >= 1) {
					if (url.equals("ParKpiResources")) {
						this.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Integer res = new Integer(value);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceById(res);
							this.resources.add(toAdd);
						}
					}else if (url.equals("ParKpiResourcesCode")) {
						this.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceByCode(value);
							this.resources.add(toAdd);
						}
					}else {
						String value = "'" + (String) values.get(0) + "'";
						for (int k = 1; k < values.size(); k++) {
							value = value + ",'" + (String) values.get(k) + "'";
						}					
						parametersMap.put(url, value);
					}
				}
			}
		}
		logger.debug("OUT. Date:" + this.dateOfKPI);
		return parametersMap;
	}
	
	private String getDateForDataset(Date d){
		String toReturn = "";
		SourceBean formatSB = ((SourceBean) ConfigSingleton.getInstance().getAttribute("SPAGOBI.DATE-FORMAT-SERVER"));
		String format = (String) formatSB.getAttribute("format");
		SimpleDateFormat f = new SimpleDateFormat();
		f.applyPattern(format);
		toReturn = f.format(d);
		return toReturn;
	}
	
	private String setCalculationDateOfKpi(String value){
		logger.debug("IN");
		SourceBean formatSB = ((SourceBean) ConfigSingleton.getInstance().getAttribute("SPAGOBI.DATE-FORMAT-SERVER"));
		String format = (String) formatSB.getAttribute("format");
		SimpleDateFormat f = new SimpleDateFormat();
		f.applyPattern(format);
		String temp = f.format(this.dateOfKPI);
		try {
			this.dateOfKPI = f.parse(value);
			Long milliseconds = this.dateOfKPI.getTime();
			//If the date required is today then the time considered will be the actual date
			if(temp.equals(value)){
				Calendar calendar = new GregorianCalendar();
				int hrs = calendar.get(Calendar.HOUR); 
				int mins = calendar.get(Calendar.MINUTE); 
				int secs = calendar.get(Calendar.SECOND); 
				int AM = calendar.get(Calendar.AM_PM);//if AM then int=0, if PM then int=1
				if(AM==0){
					int millisec =  (secs*1000) + (mins *60*1000) + (hrs*60*60*1000);
					Long milliSecToAdd = new Long (millisec);
					milliseconds = new Long(milliseconds.longValue()+milliSecToAdd.longValue());
					this.dateOfKPI = new Date(milliseconds);
				}else{
					int millisec =  (secs*1000) + (mins *60*1000) + ((hrs+12)*60*60*1000);
					Long milliSecToAdd = new Long (millisec);
					milliseconds = new Long(milliseconds.longValue()+milliSecToAdd.longValue());
					this.dateOfKPI = new Date(milliseconds);
				}    
						
				String h = "";
				String min = "";
				String sec = "";
				if(hrs<10){
					if(AM==0){
						h="0"+hrs;
					}else{
						hrs = hrs+12;
						h=""+hrs;
					}
				}else{
					if(AM==0){
						h =""+ hrs;
					}else{
						hrs = hrs+12;
						h=""+hrs;
					}
				}
				if(mins<10){
					min="0"+mins;
				}else{
					min =""+ mins;
				}
				if(secs<10){
					sec="0"+secs;
				}else{
					sec =""+ secs;
				}
				value ="'"+ value +" "+h+":"+min+":"+sec+"'";
			}else{
				value ="'"+ value +" 00:00:00'";
			}
		} catch (ParseException e) {
			logger.error("ParseException.value=" + value, e);
		}
		logger.debug("OUT");
		return value;
	}

	/**
	 * Function that sets the basic values getting them from the xml template
	 * @param content The template SourceBean containing parameters configuration
	 */
	public void getSetConf(SourceBean content) {
		logger.debug("IN");
		this.confMap = new HashMap();

		//Getting TITLE and replacing eventual parameters
		if (content.getAttribute("name") != null) {
			String titleChart = (String) content.getAttribute("name");
			titleChart = replaceParsInString(titleChart);
			setName(titleChart);
		} 

		//Setting title style
		SourceBean styleTitleSB = (SourceBean) content.getAttribute("STYLE_TITLE");
		if (styleTitleSB != null) {
			String fontS = (String) content.getAttribute("STYLE_TITLE.font");
			String sizeS = (String) content.getAttribute("STYLE_TITLE.size");
			String colorS = (String) content.getAttribute("STYLE_TITLE.color");
			try {
				Color color = Color.decode(colorS);
				int size = Integer.valueOf(sizeS).intValue();
				styleTitle = new StyleLabel(fontS, size, color);
			} catch (Exception e) {
				logger.error("Wrong style Title settings, use default");
			}

		} else {
			styleTitle = new StyleLabel("Arial", 16, new Color(255, 0, 0));
		}
		this.confMap.put("styleTitle", styleTitle);

		//Getting SUBTITLE and setting its style
		SourceBean styleSubTitleSB = (SourceBean) content.getAttribute("STYLE_SUBTITLE");
		if (styleSubTitleSB != null) {

			String subTitle = (String) content.getAttribute("STYLE_SUBTITLE.name");
			subTitle = replaceParsInString(subTitle);
			setSubName(subTitle);		
			String fontS = (String) content.getAttribute("STYLE_SUBTITLE.font");
			String sizeS = (String) content.getAttribute("STYLE_SUBTITLE.size");
			String colorS = (String) content.getAttribute("STYLE_SUBTITLE.color");
			try {
				Color color = Color.decode(colorS);
				int size = Integer.valueOf(sizeS).intValue();
				styleSubTitle = new StyleLabel(fontS, size, color);
			} catch (Exception e) {
				logger.error("Wrong style SubTitle settings, use default");
			}
		} else {
			styleSubTitle = new StyleLabel("Arial", 12, new Color(0, 0, 0));
		}
		this.confMap.put("styleSubTitle", styleSubTitle);

		// get all the other template parameters
		try {
			Map dataParameters = new HashMap();
			SourceBean dataSB = (SourceBean) content.getAttribute("CONF");
			List dataAttrsList = dataSB.getContainedSourceBeanAttributes();
			Iterator dataAttrsIter = dataAttrsList.iterator();
			while (dataAttrsIter.hasNext()) {
				SourceBeanAttribute paramSBA = (SourceBeanAttribute) dataAttrsIter.next();
				SourceBean param = (SourceBean) paramSBA.getValue();
				String nameParam = (String) param.getAttribute("name");
				String valueParam = (String) param.getAttribute("value");
				dataParameters.put(nameParam, valueParam);
			}

			closed_tree = true;
			if (dataParameters.get("closed_tree") != null
					&& !(((String) dataParameters.get("closed_tree")).equalsIgnoreCase(""))) {
				String leg = (String) dataParameters.get("closed_tree");
				if (leg.equalsIgnoreCase("false"))
					closed_tree = false;
			}
			this.confMap.put("closed_tree", closed_tree);

			display_semaphore = true;
			if (dataParameters.get("display_semaphore") != null
					&& !(((String) dataParameters.get("display_semaphore")).equalsIgnoreCase(""))) {
				String leg = (String) dataParameters.get("display_semaphore");
				if (leg.equalsIgnoreCase("false"))
					display_semaphore = false;
			}
			this.confMap.put("display_semaphore", display_semaphore);

			display_bullet_chart = true;
			if (dataParameters.get("display_bullet_chart") != null
					&& !(((String) dataParameters.get("display_bullet_chart")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("display_bullet_chart");
				if (fil.equalsIgnoreCase("false"))
					display_bullet_chart = false;
			}
			this.confMap.put("display_bullet_chart", display_bullet_chart);
			
			display_threshold_image = false;
			if (dataParameters.get("display_threshold_image") != null
					&& !(((String) dataParameters.get("display_threshold_image")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("display_threshold_image");
				if (fil.equalsIgnoreCase("true"))
					display_threshold_image = true;
			}
			this.confMap.put("display_threshold_image", display_threshold_image);

			display_weight = true;
			if (dataParameters.get("display_weight") != null
					&& !(((String) dataParameters.get("display_weight")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("display_weight");
				if (fil.equalsIgnoreCase("false"))
					display_weight = false;
			}
			this.confMap.put("display_weight", display_weight);

			display_alarm = true;
			if (dataParameters.get("display_alarm") != null
					&& !(((String) dataParameters.get("display_alarm")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("display_alarm");
				if (fil.equalsIgnoreCase("false"))
					display_alarm = false;
			}
			this.confMap.put("display_alarm", display_alarm);

			if(!register_par_setted){//the spagobi register_values if setted has priority
				register_values = true;
				if (dataParameters.get("register_values") != null
						&& !(((String) dataParameters.get("register_values")).equalsIgnoreCase(""))) {
					String fil = (String) dataParameters.get("register_values");
					if (fil.equalsIgnoreCase("false"))
						register_values = false;
				}
				this.confMap.put("register_values", register_values);
			}

			show_axis = false;
			if (dataParameters.get("show_axis") != null
					&& !(((String) dataParameters.get("show_axis")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("show_axis");
				if (fil.equalsIgnoreCase("true"))
					show_axis = true;
			}
			this.confMap.put("show_axis", show_axis);
			
			weighted_values = false;
			if (dataParameters.get("weighted_values") != null
					&& !(((String) dataParameters.get("weighted_values")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("weighted_values");
				if (fil.equalsIgnoreCase("true"))
					weighted_values = true;
			}
			this.confMap.put("weighted_values", weighted_values);
			
			if (dataParameters.get("model_title") != null
					&& !(((String) dataParameters.get("model_title")).equalsIgnoreCase(""))) {
				String fil = (String) dataParameters.get("model_title");
				if (fil!=null) model_title = fil;
			}else{
				MessageBuilder msgBuild=new MessageBuilder();
				model_title = msgBuild.getMessage("sbi.kpi.modelLineTitle", locale);					
			}
			this.confMap.put("model_title", model_title);
			
			if (dataParameters.get("kpi_title") != null) {
				String fil = (String) dataParameters.get("kpi_title");
				if (fil!=null) kpi_title = fil;
			}
			this.confMap.put("kpi_title", kpi_title);
			
			if (dataParameters.get("weight_title") != null) {
				String fil = (String) dataParameters.get("weight_title");
				if (fil!=null) weight_title = fil;
			}
			this.confMap.put("weight_title", weight_title);
			
			if (dataParameters.get("bullet_chart_title") != null) {
				String fil = (String) dataParameters.get("bullet_chart_title");
				if (fil!=null) bullet_chart_title = fil;
			}
			this.confMap.put("bullet_chart_title", bullet_chart_title);
			
			if (dataParameters.get("threshold_image_title") != null) {
				String fil = (String) dataParameters.get("threshold_image_title");
				if (fil!=null) threshold_image_title = fil;
			}
			this.confMap.put("threshold_image_title", threshold_image_title);
			
			if (dataParameters.get("value_title") != null) {
				String fil = (String) dataParameters.get("value_title");
				if (fil!=null) value_title = fil;
			}
			this.confMap.put("value_title", value_title);
			
			if (dataParameters.get(SpagoBIConstants.PUBLISHER_NAME) != null && dataParameters.get(SpagoBIConstants.PUBLISHER_NAME) != "") {
				String fil = (String) dataParameters.get(SpagoBIConstants.PUBLISHER_NAME);
				if (fil!=null) publisher_Name = fil;
			}
			
			if (dataParameters.get("metadata_publisher_Name") != null && dataParameters.get("metadata_publisher_Name") != "") {
				String fil = (String) dataParameters.get("metadata_publisher_Name");
				if (fil!=null) metadata_publisher_Name = fil;
			}
			
			if (dataParameters.get("trend_publisher_Name") != null && dataParameters.get("trend_publisher_Name") != "") {
				String fil = (String) dataParameters.get("trend_publisher_Name");
				if (fil!=null) trend_publisher_Name = fil;
			}
		} catch (Exception e) {
			logger.error("error in reading template parameters");
		}
	}
	
	private String replaceParsInString(String title){
		logger.debug("IN");
		SimpleDateFormat f = new SimpleDateFormat();
		f.applyPattern(internationalizedFormat);
		SimpleDateFormat fServ = new SimpleDateFormat();
		fServ.applyPattern(formatServer);
		if (title != null) {
			String tmpTitle = title;
			while (!tmpTitle.equals("")) {
				if (tmpTitle.indexOf("$P{") >= 0) {
					String parName = tmpTitle
					.substring(tmpTitle.indexOf("$P{") + 3, tmpTitle.indexOf("}"));
					String parValue = (parametersObject.get(parName) == null) ? "" : (String) parametersObject
							.get(parName);
					parValue = parValue.replaceAll("\'", "");
					if (parValue.equals("%"))
						parValue = "";
					int pos = tmpTitle.indexOf("$P{" + parName + "}") + (parName.length() + 4);
					if(parName.equalsIgnoreCase("ParKpiDate")){
						try {
							if(parValue!=null && !parValue.equalsIgnoreCase("")){
								Date d = fServ.parse(parValue);
								parValue = f.format(d);
							}
						} catch (ParseException e) {
							logger.error(e);
							e.printStackTrace();
						}
					}
					title = title.replace("$P{" + parName + "}", parValue);
					tmpTitle = tmpTitle.substring(pos);
				} else
					tmpTitle = "";
			}
		} else{
			title = "";
		}
		logger.debug("OUT");
		return title;
	}

	/**
	 * The <code>SpagoBIDashboardInternalEngine</code> cannot manage
	 * subobjects so this method must not be invoked. 
	 * @param requestContainer
	 *                The <code>RequestContainer</code> object (the session
	 *                can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document
	 * @param response The response <code>SourceBean</code> to be populated
	 * @throws EMFUserError the EMF user error
	 */
	public void executeSubObject(RequestContainer requestContainer, BIObject obj, SourceBean response,
			Object subObjectInfo) throws EMFUserError {
		// it cannot be invoked
		logger.error("SpagoBIDashboardInternalEngine cannot exec subobjects.");
		throw new EMFUserError(EMFErrorSeverity.ERROR, "101", messageBundle);
	}

	public void setName(String _name) {
		name = _name;
	}

	public void setSubName(String _name) {
		subName = _name;
	}

	/**
	 * Function not implemented. Thid method should not be called
	 * @param requestContainer
	 *                The <code>RequestContainer</code> object (the session
	 *                can be retrieved from this object)
	 * @param response The response <code>SourceBean</code> to be populated
	 * @param obj the obj
	 * @throws InvalidOperationRequest the invalid operation request
	 * @throws EMFUserError
	 *                 the EMF user error
	 */
	public void handleNewDocumentTemplateCreation(RequestContainer requestContainer, BIObject obj, SourceBean response)
	throws EMFUserError, InvalidOperationRequest {
		logger.error("SpagoBIDashboardInternalEngine cannot build document template.");
		throw new InvalidOperationRequest();

	}

	/**
	 * Function not implemented. Thid method should not be called
	 * @param requestContainer
	 *                The <code>RequestContainer</code> object (the session
	 *                can be retrieved from this object)
	 * @param response The response <code>SourceBean</code> to be populated
	 * @param obj the obj
	 * @throws InvalidOperationRequest the invalid operation request
	 * @throws EMFUserError the EMF user error
	 */
	public void handleDocumentTemplateEdit(RequestContainer requestContainer, BIObject obj, SourceBean response)
	throws EMFUserError, InvalidOperationRequest {
		logger.error("SpagoBIDashboardInternalEngine cannot build document template.");
		throw new InvalidOperationRequest();
	}
}
