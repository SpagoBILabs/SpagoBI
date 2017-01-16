package it.eng.spagobi.analiticalmodel.document.utils;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.document.handlers.ParameterForExecution;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.container.CoreContextManager;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

public class ParameterForExecutionHelper {

		// logger component
		private static Logger logger = Logger.getLogger(ParameterForExecutionHelper.class);
		
		BIObjectParameter analyticalDocumentParameter = null;
CoreContextManager context = null;
		
		public ParameterForExecutionHelper(BIObjectParameter _analyticalDocumentParameter, CoreContextManager _context) {
			analyticalDocumentParameter = _analyticalDocumentParameter;
			context = _context;
		}
		
		
		public String getValueFromLov(SourceBean lovSB) {
			String value = null;
			ILovDetail lovProvDet = null;
			logger.debug("IN");
			try {
				Parameter par = analyticalDocumentParameter.getParameter();
				ModalitiesValue lov = par.getModalityValue();
				// build the ILovDetail object associated to the lov
				String lovProv = lov.getLovProvider();
				lovProvDet = LovDetailFactory.getLovFromXML(lovProv);

				value = (String) lovSB.getAttribute( lovProvDet.getValueColumnName() );
			} catch (Exception e) {
				throw new SpagoBIServiceException(ParameterForExecution.SERVICE_NAME, "Impossible to get parameter's value", e);
			} 
			logger.debug("OUT");
			return value;
		}
		
		public String getDescriptionFromLov(SourceBean lovSB) {
			String description = null;
			ILovDetail lovProvDet = null;
			logger.debug("IN");
			try {
				Parameter par = analyticalDocumentParameter.getParameter();
				ModalitiesValue lov = par.getModalityValue();
				// build the ILovDetail object associated to the lov
				String lovProv = lov.getLovProvider();
				lovProvDet = LovDetailFactory.getLovFromXML(lovProv);

				description = (String) lovSB.getAttribute( lovProvDet.getDescriptionColumnName() );
			} catch (Exception e) {
				throw new SpagoBIServiceException(ParameterForExecution.SERVICE_NAME, "Impossible to get parameter's value", e);
			} 
			logger.debug("OUT");
			return description;
		}
		
		
		public boolean hasParameterInsideLOV() {
			// ExecutionInstance executionInstance = context.getExecutionInstance( ExecutionInstance.class.getName() );
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
				throw new SpagoBIServiceException(ParameterForExecution.SERVICE_NAME, "Impossible to find in context execution instance for execution of document with id " + " ["
						+ analyticalDocumentParameter.getBiObjectID() + "]: was it searched as a map? " + isAMap);
			}
			return hasParameterInsideLOV(executionInstance);
		}
		
		private boolean hasParameterInsideLOV(ExecutionInstance executionInstance) {
			ILovDetail lovDetail = executionInstance.getLovDetail(analyticalDocumentParameter);
			if (lovDetail != null) {
				Set<String> parameterNames = null;
				try {
					parameterNames = lovDetail.getParameterNames();
				} catch (Exception e) {
					throw new SpagoBIServiceException(ParameterForExecution.SERVICE_NAME, "Impossible to find in context execution lov parameters for execution of document with id "
							+ " [" + analyticalDocumentParameter.getBiObjectID() + "]", e);
				}
				if (parameterNames != null && !parameterNames.isEmpty()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		
		
		public ExecutionInstance getExecutionInstance () {
			ExecutionInstance executionInstance = null;

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
			return executionInstance;
		}
		
		
		
	}
