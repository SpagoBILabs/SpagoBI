/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analiticalmodel.document.x;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.LovResultCacheManager;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParuseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovResultHandler;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.cache.CacheInterface;
import it.eng.spagobi.utilities.cache.CacheSingleton;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetParametersForExecutionAction  extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "GET_PARAMETERS_FOR_EXECUTION_SERVICE";
	
	// request parameters
	public static String DOCUMENT_ID = ObjectsTreeConstants.OBJECT_ID;
	public static String DOCUMENT_LABEL = ObjectsTreeConstants.OBJECT_LABEL;
	public static String CALLBACK = "callback";
	// logger component
	private static Logger logger = Logger.getLogger(GetParameterValuesForExecutionAction.class);
	
	
	public void doService() {
		
		ExecutionInstance executionInstance;
		
		Assert.assertNotNull(getContext(), "Execution context cannot be null" );
		Assert.assertNotNull(getContext().getExecutionInstance( ExecutionInstance.class.getName() ), "Execution instance cannot be null");
	
		executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
		
		BIObject obj = executionInstance.getBIObject();
		String roleName = executionInstance.getExecutionRole();

		List parametersForExecution = new ArrayList();
		List parameters = obj.getBiObjectParameters();
		
		if (parameters != null && parameters.size() > 0) {
			Iterator iter = parameters.iterator();
			while (iter.hasNext()) {
				BIObjectParameter biparam = (BIObjectParameter) iter.next();
				
				parametersForExecution.add( new ParameterForExecution(biparam) );
			}
		}
		HttpServletRequest httpRequest = getHttpRequest();
		MessageBuilder m = new MessageBuilder();
		Locale locale = m.getLocale(httpRequest);
		JSONArray parametersJSON = null;
		try {
			parametersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( parametersForExecution,locale );
		} catch (SerializationException e) {
			e.printStackTrace();
		}
		
		String callback = getAttributeAsString( CALLBACK );
		logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
		
		try {
			writeBackToClient( new JSONSuccess( parametersJSON, callback )  );
		} catch (IOException e) {
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
		}
	}
	
	
	public class ParameterForExecution {
		
		Parameter par; 
		String id;
		Integer parameterUseId;
		String label;
		String parType; // DATE, STRING, ...
		String selectionType; // COMBOBOX, LIST, ...
		String typeCode; // SpagoBIConstants.INPUT_TYPE_X
		boolean mandatory;
		boolean visible;
		
		List dependencies;
		

		int valuesCount;
		// used to comunicate to the client the unique valid value in case valuesCount = 1
		String value;
		
		public ParameterForExecution(BIObjectParameter biparam) {
			id = biparam.getParameterUrlName();
			label = localize( biparam.getLabel() );
			par = biparam.getParameter();
			parType = par.getType(); 
			selectionType = par.getModalityValue().getSelectionType();
			typeCode = par.getModalityValue().getITypeCd();			
			
			
			mandatory = false;
			Iterator it = par.getChecks().iterator();	
			while (it.hasNext()){
				Check check = (Check)it.next();
				if (check.getValueTypeCd().equalsIgnoreCase("MANDATORY")){
					mandatory = true;
					break;
				}
			} 
			
			visible = biparam.getVisible() == 1;
			
			ExecutionInstance executionInstance =  getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			
			ParameterUse biParameterExecModality;
			try {
				// load parameter use ...
				IParameterUseDAO parusedao = DAOFactory.getParameterUseDAO();
				biParameterExecModality = parusedao.loadByParameterIdandRole(biparam.getParID(), executionInstance.getExecutionRole());
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find any valid execution modality for parameter [" + id + "] and role [" + executionInstance.getExecutionRole() + "]", e);
			}
			
			Assert.assertNotNull(biParameterExecModality, "Impossible to find any valid execution modality for parameter [" + id + "] and role [" + executionInstance.getExecutionRole() + "]" );
			
			parameterUseId = biParameterExecModality.getUseID();
			
			List biParameterExecDependencies;
			try {
				IObjParuseDAO objParuseDAO = DAOFactory.getObjParuseDAO();
				biParameterExecDependencies = objParuseDAO.loadObjParuse(biparam.getId(), biParameterExecModality.getUseID());
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while loading parameter dependecies for parameter [" + id + "]", e);
			}
			
			dependencies = new ArrayList();
			it = biParameterExecDependencies.iterator();
			while (it.hasNext()){
				ObjParuse dependency = (ObjParuse)it.next();
				Integer objParFatherId = dependency.getObjParFatherId();
				try {					
					BIObjectParameter objParFather = DAOFactory.getBIObjectParameterDAO().loadForDetailByObjParId(objParFatherId);
					dependencies.add(objParFather.getParameterUrlName());
				} catch (EMFUserError e) {
					throw new SpagoBIServiceException("An error occurred while loading parameter [" + objParFatherId + "]", e);
				}
			}
			
			if("COMBOBOX".equalsIgnoreCase(selectionType)) { // load values only if it is not a lookup
				List lovs = getLOV( biparam, biParameterExecDependencies, executionInstance);
				setValuesCount( lovs == null? 0: lovs.size() );
				if(getValuesCount() == 1) {
					SourceBean lovSB = (SourceBean)lovs.get(0);
					value = getValueFromLov(biparam, lovSB);
				}
			}
			
			if("LIST".equalsIgnoreCase(selectionType)
					|| "CHECK_LIST".equalsIgnoreCase(selectionType)) {
				setValuesCount( -1 ); // it means that we don't know the lov size
			}
			
		}
		
		private String getValueFromLov(BIObjectParameter biObjectParameter, SourceBean lovSB) {
			String value = null;
			ILovDetail lovProvDet = null;
			try {
				Parameter par = biObjectParameter.getParameter();
				ModalitiesValue lov = par.getModalityValue();
				// build the ILovDetail object associated to the lov
				String lovProv = lov.getLovProvider();
				lovProvDet = LovDetailFactory.getLovFromXML(lovProv);
				
				value = (String) lovSB.getAttribute( lovProvDet.getValueColumnName() );
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to get parameter's value", e);
			} 
			
			return value;
		}
		
		private List getLOV(BIObjectParameter biObjectParameter, List<ObjParuse> dependencies, ExecutionInstance executionInstance){
			List rows = null;
			String lovResult = null;
			try {
				// get the result of the lov
				IEngUserProfile profile = getUserProfile();
				
				LovResultCacheManager executionCacheManager = new LovResultCacheManager();
				lovResult = executionCacheManager.getLovResult(profile, biObjectParameter, executionInstance, true);
				
				// get all the rows of the result
				LovResultHandler lovResultHandler = new LovResultHandler(lovResult);		
				rows = lovResultHandler.getRows();
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to get parameter's values", e);
			} 
			
			return rows;
		}
		
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
			return par;
		}

		public void setPar(Parameter par) {
			this.par = par;
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
		
		public List getDependencies() {
			return dependencies;
		}

		public void setDependencies(List dependencies) {
			this.dependencies = dependencies;
		}
		
		public Integer getParameterUseId() {
			return parameterUseId;
		}

		public void setParameterUseId(Integer parameterUseId) {
			this.parameterUseId = parameterUseId;
		}
		
	}

}
