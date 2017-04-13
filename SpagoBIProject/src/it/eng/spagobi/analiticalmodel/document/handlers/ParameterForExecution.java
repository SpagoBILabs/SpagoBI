package it.eng.spagobi.analiticalmodel.document.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.utils.ParameterForExecutionHelper;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValue;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValueList;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.DefaultValuesRetriever;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParview;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParuseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParviewDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovResultHandler;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.container.CoreContextManager;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

public class ParameterForExecution {

		// DAOs
		private IParameterUseDAO ANALYTICAL_DRIVER_USE_MODALITY_DAO;
		private IObjParuseDAO DATA_DEPENDENCIES_DAO;
		private IObjParviewDAO VISUAL_DEPENDENCIES_DAO;
		private IBIObjectParameterDAO ANALYTICAL_DOCUMENT_PARAMETER_DAO;
		private IParameterDAO ANALYTICAL_DRIVER_DAO;

		// attribute loaded from spagobi's metadata
		BIObjectParameter analyticalDocumentParameter;
		Parameter analyticalDriver; 
		ParameterUse analyticalDriverExecModality;
		List dataDependencies;
		List visualDependencies;

		// attribute used by the serializer
		String id;
		Integer parameterUseId;
		String label;
		String parType; // DATE, STRING, ...
		String selectionType; // COMBOBOX, LIST, ...
		//String valueSelection; // "lov", "man_in", "map_in"
		boolean enableMaximizer;
		String typeCode; // SpagoBIConstants.INPUT_TYPE_X
		boolean mandatory;
		boolean multivalue;
		boolean visible;
		Integer colspan;
		Integer thickPerc;

		
		int valuesCount;
		// used to comunicate to the client the unique 
		// valid value in case valuesCount = 1
		String value;
				
		// in case of massive export these are the parameter ids referred by current parameter
		List<Integer> objParameterIds;

		AnalyticalDriverValueList values;
		AnalyticalDriverValueList admissibleValuesList;
		AnalyticalDriverValueList defaultValuesList;
		
		ArrayList<HashMap<String, Object>> admissibleValuesOLD;
		
		// dependencies (dataDep & visualDep &lovDep)
		Map<String, List<ParameterDependency>> dependencies;
		
		String executionRole;
		Locale locale;
		BIObject object;
		
		ParameterForExecutionHelper helper = null;
		IEngUserProfile profile;
		CoreContextManager context;
		// logger component
		private static Logger logger = Logger.getLogger(ParameterForExecution.class);
		public static final String SERVICE_NAME = "GET_PARAMETERS_FOR_EXECUTION_SERVICE";

		
		
		public abstract class ParameterDependency {
			public String urlName;
		};
		public class DataDependency extends ParameterDependency {}

		public class VisualDependency extends ParameterDependency {
			public ObjParview condition;
		}

		public class LovDependency extends ParameterDependency {
		}

		public ParameterForExecution(BIObjectParameter biParam, IEngUserProfile _profile, CoreContextManager _context) {
			logger.debug("IN - "+biParam.getLabel());

			analyticalDocumentParameter = biParam;
			profile = _profile;
			context = _context;
			admissibleValuesList = new AnalyticalDriverValueList();
			helper = new ParameterForExecutionHelper(analyticalDocumentParameter, context);			
			initDAO();			
			initAttributes();
			initDependencies();
			//loadAdmissibleValues();                         // get values only if single
			//loadDefaultValues();                  // get default values
			
			objParameterIds = new ArrayList<Integer>();
			logger.debug("OUT");

		}

	


		
		void initAttributes() {
			logger.debug("IN");

			ExecutionInstance executionInstance = helper.getExecutionInstance();
			if (executionInstance == null) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find in context execution instance for execution of document with id "+" [" + analyticalDocumentParameter.getBiObjectID() + "]");		
			}

			id = analyticalDocumentParameter.getParameterUrlName();
			//label = localize( analyticalDocumentParameter.getLabel() );
			label = analyticalDocumentParameter.getLabel();
			analyticalDriver = analyticalDocumentParameter.getParameter();
			parType = analyticalDriver.getType(); 
			
			
			selectionType = analyticalDriver.getModalityValue().getSelectionType();
			
			typeCode = analyticalDriver.getModalityValue().getITypeCd();			

			/*
			mandatory = false;
			Iterator it = analyticalDriver.getChecks().iterator();	
			while (it.hasNext()){
				Check check = (Check)it.next();
				if (check.getValueTypeCd().equalsIgnoreCase("MANDATORY")){
					mandatory = true;
					break;
				}
			} 
			*/
			mandatory = analyticalDocumentParameter.getRequired() == 1;
			
			multivalue = analyticalDocumentParameter.isMultivalue();

			visible = analyticalDocumentParameter.getVisible() == 1;
			
			colspan = analyticalDocumentParameter.getColSpan() != null ? analyticalDocumentParameter.getColSpan() : 1;
			thickPerc = analyticalDocumentParameter.getThickPerc() != null ? analyticalDocumentParameter.getThickPerc() : 0;

			try {
				logger.debug("Load parameter and roles");
				analyticalDriverExecModality = ANALYTICAL_DRIVER_USE_MODALITY_DAO.loadByParameterIdandRole(analyticalDocumentParameter.getParID(), executionInstance.getExecutionRole());
				logger.debug("End Load parameter and roles");
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find any valid execution modality for parameter [" + id + "] and role [" + executionInstance.getExecutionRole() + "]", e);
			}

			Assert.assertNotNull(analyticalDriverExecModality, "Impossible to find any valid execution modality for parameter [" + id + "] and role [" + executionInstance.getExecutionRole() + "]" );

			parameterUseId = analyticalDriverExecModality.getUseID();
			
			enableMaximizer = analyticalDriverExecModality.isMaximizerEnabled();
			logger.debug("OUT");
		}

		private void initDependencies() {
			logger.debug("IN");
			initDataDependencies();
			initVisualDependencies();
			initLovDependencies();
			logger.debug("OUT");
		}

		private void initVisualDependencies() {

			logger.debug("IN");
			if(dependencies == null) {
				dependencies = new HashMap<String, List<ParameterDependency>>();
			}

			try {
				visualDependencies = VISUAL_DEPENDENCIES_DAO.loadObjParviews(analyticalDocumentParameter.getId());
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while loading parameter visual dependecies for parameter [" + id + "]", e);
			}

			Iterator it = visualDependencies.iterator();
			while (it.hasNext()){
				ObjParview dependency = (ObjParview)it.next();
				Integer objParFatherId = dependency.getObjParFatherId();
				try {					
					BIObjectParameter objParFather = ANALYTICAL_DOCUMENT_PARAMETER_DAO.loadForDetailByObjParId(objParFatherId);
					VisualDependency visualDependency = new VisualDependency();
					visualDependency.urlName = objParFather.getParameterUrlName();
					visualDependency.condition = dependency;
					if( !dependencies.containsKey(visualDependency.urlName) ) {
						dependencies.put(visualDependency.urlName, new ArrayList<ParameterDependency>());
					}
					List<ParameterDependency> depList = dependencies.get(visualDependency.urlName);
					depList.add(visualDependency);
				} catch (EMFUserError e) {
					throw new SpagoBIServiceException("An error occurred while loading parameter [" + objParFatherId + "]", e);
				}
			}
			logger.debug("OUT");
		}

		private void initDataDependencies() {
			logger.debug("IN");
			if(dependencies == null) {
				dependencies = new HashMap<String, List<ParameterDependency>>();
			}


			try {
				dataDependencies = DATA_DEPENDENCIES_DAO.loadObjParuse(analyticalDocumentParameter.getId(), analyticalDriverExecModality.getUseID());
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while loading parameter data dependecies for parameter [" + id + "]", e);
			}

			Iterator it = dataDependencies.iterator();
			while (it.hasNext()){
				ObjParuse dependency = (ObjParuse)it.next();
				Integer objParFatherId = dependency.getObjParFatherId();
				try {					
					BIObjectParameter objParFather = ANALYTICAL_DOCUMENT_PARAMETER_DAO.loadForDetailByObjParId(objParFatherId);
					DataDependency dataDependency = new DataDependency();
					dataDependency.urlName = objParFather.getParameterUrlName();
					if( !dependencies.containsKey(dataDependency.urlName) ) {
						dependencies.put(dataDependency.urlName, new ArrayList<ParameterDependency>());
					}
					List<ParameterDependency> depList = dependencies.get(dataDependency.urlName);
					depList.add(dataDependency );
				} catch (EMFUserError e) {
					throw new SpagoBIServiceException("An error occurred while loading parameter [" + objParFatherId + "]", e);
				}
			}
			logger.debug("OUT");
		}
		
		
		private void initLovDependencies() {
			logger.debug("IN");
			if (dependencies == null) {
				dependencies = new HashMap<String, List<ParameterDependency>>();
			}
			// the execution instance could be a map if in massive export case
			ExecutionInstance executionInstance = null;
			Assert.assertNotNull(context.isExecutionInstanceAMap(ExecutionInstance.class.getName()), "Execution instance cannot be null");
			boolean isAMap = context.isExecutionInstanceAMap(ExecutionInstance.class.getName());
			if (!isAMap) {
				executionInstance = context.getExecutionInstance(ExecutionInstance.class.getName());
			} else {
				Map<Integer, ExecutionInstance> instances = context.getExecutionInstancesAsMap(ExecutionInstance.class.getName());
				Integer objId = analyticalDocumentParameter.getBiObjectID();
				executionInstance = instances.get(objId);
			}
			if (executionInstance == null) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find in context execution instance for execution of document with id " + " ["
						+ analyticalDocumentParameter.getBiObjectID() + "]: was it searched as a map? " + isAMap);
			}

			ILovDetail lovDetail = executionInstance.getLovDetail(analyticalDocumentParameter);
			Set<String> lovParameters = null;
			try {
				if (lovDetail != null) {
					lovParameters = lovDetail.getParameterNames();
					if (lovParameters != null && !lovParameters.isEmpty()) {
						logger.debug("Found one or more parameters inside the LOV");
						List<BIObjectParameter> objParameters = ANALYTICAL_DOCUMENT_PARAMETER_DAO.loadBIObjectParametersById(analyticalDocumentParameter
								.getBiObjectID());
						LovDependency lovDependency = new LovDependency();
						for (BIObjectParameter objParameter : objParameters) {
							Parameter objAnalyticalDriver = ANALYTICAL_DRIVER_DAO.loadForDetailByParameterID(objParameter.getParameter().getId());
							if (objAnalyticalDriver != null && lovParameters.contains(objAnalyticalDriver.getLabel())) {
								logger.debug("Found the analytical driver [" + objAnalyticalDriver.getLabel() + "] associated to the placeholder in the LOV");
								lovDependency.urlName = objParameter.getParameterUrlName();
								break;
							}
						}
						if (lovDependency.urlName == null || lovDependency.urlName.isEmpty()) {
							throw new SpagoBIRuntimeException(
									"Impossible to found a parameter to satisfy the dependecy associated with the placeholder in the LOV [" + id + "]");
						}

						if (!dependencies.containsKey(lovDependency.urlName)) {
							dependencies.put(lovDependency.urlName, new ArrayList<ParameterDependency>());
						}
						List<ParameterDependency> depList = dependencies.get(lovDependency.urlName);
						depList.add(lovDependency);
					}
				}
			} catch (Exception e) {
				throw new SpagoBIServiceException("An error occurred while loading parameter lov dependecies for parameter [" + id + "]", e);
			}
			logger.debug("OUT");
		}


		
		
		
		public void loadAdmissibleValues(Map<String, AnalyticalDriverValueList> processedParameters) {	
			logger.debug("IN");
			logger.debug("load admissible values for parameter "+id + " of type "+selectionType);

			// exclude manual input case
			if ("COMBOBOX".equalsIgnoreCase(selectionType) 
					|| "LIST".equalsIgnoreCase(selectionType) 
					|| "SLIDER".equalsIgnoreCase(selectionType) 
					|| "TREE".equalsIgnoreCase(selectionType) 
					|| "LOOKUP".equalsIgnoreCase(selectionType)) { 
				List lovResultsSB = getLOV(processedParameters);
				logger.debug("Retrieved values "+lovResultsSB != null ? lovResultsSB.size() : " Null");
				setValuesCount( lovResultsSB == null? 0: lovResultsSB.size() );
				logger.debug("Set values size");
				for (int i = 0; i < getValuesCount(); i++) {
					//logger.debug("Counter in result "+i);
					SourceBean lovSB = (SourceBean)lovResultsSB.get(i);
					String value = helper.getValueFromLov(lovSB);
					//logger.debug("value is "+value);
					String description = helper.getDescriptionFromLov(lovSB);
					//logger.debug("description is "+description);
									// create list of admissible values
					AnalyticalDriverValue adv = new AnalyticalDriverValue(value, description);
					admissibleValuesList.add(adv);
				}					
				logger.debug("Admissible value list built");

			} else {
				setValuesCount( -1 ); // it means that we don't know the lov size
			}
//			for (Iterator iterator = admissibleValuesList.iterator(); iterator.hasNext();) {
//				AnalyticalDriverValue analyticalDriverValue = (AnalyticalDriverValue) iterator.next();
//				logger.debug("Admissible vlue found with value "+analyticalDriverValue.getValue()+ " and description "+analyticalDriverValue.getDescription());
//			}
			logger.debug("OUT");
		}
		

		public void loadDefaultValues() {
			logger.debug("IN");
			try {
				ExecutionInstance executionInstance = helper.getExecutionInstance();
				DefaultValuesRetriever retriever = new DefaultValuesRetriever();
				//IEngUserProfile profile = getUserProfile();
				defaultValuesList = retriever.getDefaultValues(analyticalDocumentParameter, executionInstance, profile);
				
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to get parameter's default values", e);
			} 
			logger.debug("OUT");
		}

		private List getLOV(Map<String, AnalyticalDriverValueList> processedParameters){
			//ExecutionInstance executionInstance =  context.getExecutionInstance( ExecutionInstance.class.getName() );
			logger.debug("IN");
			// the execution instance could be a map if in massive export case
			ExecutionInstance executionInstance =  null;
			Assert.assertNotNull(context.isExecutionInstanceAMap( ExecutionInstance.class.getName() ), "Execution instance cannot be null");
			boolean isAMap = context.isExecutionInstanceAMap( ExecutionInstance.class.getName() );
			if(!isAMap){
				executionInstance =  context.getExecutionInstance( ExecutionInstance.class.getName() );
			}
			else{
				Map<Integer, ExecutionInstance> instances = context.getExecutionInstancesAsMap( ExecutionInstance.class.getName() );
				Integer objId = analyticalDocumentParameter.getBiObjectID();
				executionInstance = instances.get(objId);
			}
			if(executionInstance == null){
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find in context execution instance for execution of document with id "+" [" + analyticalDocumentParameter.getBiObjectID() + "]: was it searched as a map? "+isAMap);		
			}
			

			// modify execution Instance with alredy chosen parameters for dependencies
			logger.debug("modify execution Instance with alredy chosen parameters for dependencies");
			List biObjectParameters = executionInstance.getBIObject().getBiObjectParameters();
			if(biObjectParameters != null){
				for (Iterator iterator = processedParameters.keySet().iterator(); iterator.hasNext();) {
					String id = (String)iterator.next();

					// convert analyticalDrvierValue
					AnalyticalDriverValueList values = processedParameters.get(id);

					List<String> valuesList = values.getValuesList();
					for (Iterator iterator2 = biObjectParameters.iterator(); iterator2.hasNext();) {
						BIObjectParameter par = (BIObjectParameter) iterator2.next();
						if(par.getParameterUrlName().equals(id)){
							par.setParameterValues(valuesList);
						}
					}
				}
				logger.debug("end of modify execution Instance with alredy chosen parameters for dependencies");

			}
			
			List rows = null;
			String lovResult = null;
			try {
				// get the result of the lov
				//IEngUserProfile profile = getUserProfile();
				logger.debug("execute Lov");
				
				LovResultCacheManager executionCacheManager = new LovResultCacheManager();
				lovResult = executionCacheManager.getLovResult(profile,
						executionInstance
								.getLovDetail(analyticalDocumentParameter),
						executionInstance
								.getDependencies(analyticalDocumentParameter),
						executionInstance, true);

				// get all the rows of the result
				LovResultHandler lovResultHandler = new LovResultHandler(lovResult);		
				rows = lovResultHandler.getRows();
				logger.debug("end of lov execution number of results "+rows != null ? rows.size() : "null");
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to get parameter's values", e);
			} 
			logger.debug("OUT");
			return rows;
		}
		

		
		
		private void initDAO() {
			logger.debug("IN");
			try {
				ANALYTICAL_DRIVER_USE_MODALITY_DAO = DAOFactory.getParameterUseDAO();
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while retrieving DAO [" + ANALYTICAL_DRIVER_USE_MODALITY_DAO.getClass().getName() + "]", e);
			}

			try {
				DATA_DEPENDENCIES_DAO = DAOFactory.getObjParuseDAO();
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while retrieving DAO [" + DATA_DEPENDENCIES_DAO.getClass().getName() + "]", e);
			}

			try {
				VISUAL_DEPENDENCIES_DAO = DAOFactory.getObjParviewDAO();
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while retrieving DAO [" + VISUAL_DEPENDENCIES_DAO.getClass().getName() + "]", e);

			}
			try {
				ANALYTICAL_DOCUMENT_PARAMETER_DAO = DAOFactory.getBIObjectParameterDAO();
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while retrieving DAO [" + ANALYTICAL_DOCUMENT_PARAMETER_DAO.getClass().getName() + "]", e);
			}
			
			try {
				ANALYTICAL_DRIVER_DAO = DAOFactory.getParameterDAO();
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while retrieving DAO [" + ANALYTICAL_DRIVER_DAO.getClass().getName() + "]", e);
			}
			logger.debug("OUT");
		}

		// ========================================================================================
		// ACCESSOR METHODS
		// ========================================================================================
		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}

		public String getTypeCode() {
			return typeCode;
		}

		public void setTypeCode(String typeCode) {
			this.typeCode = typeCode;
		}

		public Parameter getPar() {
			return analyticalDriver;
		}

		public void setPar(Parameter par) {
			this.analyticalDriver = par;
		}

		public String getParType() {
			return parType;
		}

		public void setParType(String parType) {
			this.parType = parType;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public boolean isMandatory() {
			return mandatory;
		}

		public void setMandatory(boolean mandatory) {
			this.mandatory = mandatory;
		}
		
		public boolean isMultivalue() {
			return multivalue;
		}

		public void setMultivalue(boolean multivalue) {
			this.multivalue = multivalue;
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public String getSelectionType() {
			return selectionType;
		}

		public void setSelectionType(String selectionType) {
			this.selectionType = selectionType;
		}
		
		public boolean isEnableMaximizer() {
			return enableMaximizer;
		}

		public void setEnableMaximizer(boolean enableMaximizer) {
			this.enableMaximizer = enableMaximizer;
		}

		public int getValuesCount() {
			return valuesCount;
		}

		public void setValuesCount(int valuesCount) {
			this.valuesCount = valuesCount;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Map<String, List<ParameterDependency>> getDependencies() {
			return dependencies;
		}

		public void setDependencies(Map<String, List<ParameterDependency>> dependencies) {
			this.dependencies = dependencies;
		}

		public Integer getParameterUseId() {
			return parameterUseId;
		}

		public void setParameterUseId(Integer parameterUseId) {
			this.parameterUseId = parameterUseId;
		}

		public List<Integer> getObjParameterIds() {
			return objParameterIds;
		}

		public void setObjParameterIds(List<Integer> objParameterIds) {
			this.objParameterIds = objParameterIds;
		}

		public List getVisualDependencies() {
			return visualDependencies;
		}

		public void setVisualDependencies(List visualDependencies) {
			this.visualDependencies = visualDependencies;
		}

		public List getDataDependencies() {
			return dataDependencies;
		}

		public void setDataDependencies(List dataDependencies) {
			this.dataDependencies = dataDependencies;
		}
		
		public AnalyticalDriverValueList getDefaultValues() {
			return defaultValuesList;
		}

		public void setDefaultValues(AnalyticalDriverValueList defaultValues) {
			this.defaultValuesList = defaultValues;
		}

		public int getColspan() {
			return colspan;
		}

		public void setColspan(int colspan) {
			this.colspan = colspan;
		}

		public Integer getThickPerc() {
			return thickPerc;
		}

		public void setThickPerc(Integer thickPerc) {
			this.thickPerc = thickPerc;
		}

		public AnalyticalDriverValueList getAdmissibleValuesList() {
			return admissibleValuesList;
		}

		public void setAdmissibleValuesList(AnalyticalDriverValueList _admissibleValuesList) {
			this.admissibleValuesList = _admissibleValuesList;
		}

		public AnalyticalDriverValueList getValues() {
			return values;
		}

		public void setValues(AnalyticalDriverValueList values) {
			this.values = values;
		}

		public BIObjectParameter getAnalyticalDocumentParameter() {
			return analyticalDocumentParameter;
		}

		public void setAnalyticalDocumentParameter(BIObjectParameter analyticalDocumentParameter) {
			this.analyticalDocumentParameter = analyticalDocumentParameter;
		}
		
		


		
	}
