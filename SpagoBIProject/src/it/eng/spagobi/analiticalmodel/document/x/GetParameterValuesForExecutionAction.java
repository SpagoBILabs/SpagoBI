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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.base.Constants;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.tracing.TracerSingleton;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParuseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovResultHandler;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.chiron.serializer.JSONStoreFeedTransformer;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetParameterValuesForExecutionAction  extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "GET_PARAMETERS_FOR_EXECUTION_SERVICE";
	
	// request parameters
	public static String PARAMETER_ID = "PARAMETER_ID";
	public static String SELECTED_PARAMETER_VALUES = "PARAMETERS";
	public static String FILTERS = "FILTERS";	
	public static String MODE = "MODE";
	public static String MODE_SIMPLE = "simple";
	public static String MODE_COMPLETE = "complete";
	public static String START = "start";
	public static String LIMIT = "limit";
	
	
	// logger component
	private static Logger logger = Logger.getLogger(GetParameterValuesForExecutionAction.class);
	
	
	public void doService() {
		
		String biparameterId;
		JSONObject selectedParameterValuesJSON;
		JSONObject filtersJSON = null;
		Map selectedParameterValues;
		String mode;
		Integer start;
		Integer limit;
		BIObjectParameter biObjectParameter;
		ParameterUse biParameterExecModality;
		List biParameterExecDependencies;
		ExecutionInstance executionInstance;
		String valueColumn;
		String displayColumn;
		String descriptionColumn;
		List rows;
		
		
		logger.debug("IN");
		
		try {
		
			biparameterId = getAttributeAsString( PARAMETER_ID );
			selectedParameterValuesJSON = getAttributeAsJSONObject( SELECTED_PARAMETER_VALUES );
			if(this.requestContainsAttribute( FILTERS ) ) {
				filtersJSON = getAttributeAsJSONObject( FILTERS );
			}
			
			mode = getAttributeAsString( MODE );
			start = getAttributeAsInteger( START );
			limit = getAttributeAsInteger( LIMIT );
			
			logger.debug("Parameter [" + PARAMETER_ID + "] is equals to [" + biparameterId + "]");
			logger.debug("Parameter [" + MODE + "] is equals to [" + mode + "]");
			logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
			logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
			
			if(mode == null) {
				mode = MODE_SIMPLE;
			}
			
			Assert.assertNotNull(getContext(), "Parameter [" + PARAMETER_ID + "] cannot be null" );
			Assert.assertNotNull(getContext(), "Execution context cannot be null" );
			Assert.assertNotNull(getContext().getExecutionInstance( ExecutionInstance.class.getName() ), "Execution instance cannot be null");
		
			executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			
			BIObject obj = executionInstance.getBIObject();
			String roleName = executionInstance.getExecutionRole();
			
			selectedParameterValues = null;
			if(selectedParameterValuesJSON != null) {
				try {
					selectedParameterValues = new HashMap();
					Iterator it = selectedParameterValuesJSON.keys();
					while(it.hasNext()){
						String key = (String)it.next();
						Object v = selectedParameterValuesJSON.get(key);
						if(v instanceof JSONArray) {
							JSONArray a = (JSONArray)v;
							String[] nv = new String[a.length()];
							for(int i = 0; i < a.length(); i++) {
									nv[i] = (String)a.get(i);
							}
							
							selectedParameterValues.put( key, nv );
					} else if(v instanceof String) {
							selectedParameterValues.put( key, (String)v );
						} else {
							Assert.assertUnreachable("attributes of PARAMETERS JSONObject can be only JSONArray or String");
						}
					}
				} catch (JSONException e) {
					throw new SpagoBIServiceException("parameter JSONObject is malformed", e);
				}
			}
	
			biObjectParameter = null;
			List parameters = obj.getBiObjectParameters();
			for(int i = 0; i < parameters.size(); i++) {
				BIObjectParameter p = (BIObjectParameter) parameters.get(i);
				if( biparameterId.equalsIgnoreCase( p.getParameterUrlName() ) ) {
					biObjectParameter = p;
					break;
				}
			}
			
			Assert.assertNotNull(biObjectParameter, "Impossible to find parameter [" + biparameterId + "]" );
			
			String lovResult = null;
			ILovDetail lovProvDet = null;
			try {
				Parameter par = biObjectParameter.getParameter();
				ModalitiesValue lov = par.getModalityValue();
				// build the ILovDetail object associated to the lov
				String lovProv = lov.getLovProvider();
				lovProvDet = LovDetailFactory.getLovFromXML(lovProv);
				// get the result of the lov
				IEngUserProfile profile = getUserProfile();
				lovResult = biObjectParameter.getLovResult();
				if ((lovResult == null) || (lovResult.trim().equals(""))) {
					lovResult = lovProvDet.getLovResult(profile);
				}
				
				// get all the rows of the result
				LovResultHandler lovResultHandler = new LovResultHandler(lovResult);		
				rows = lovResultHandler.getRows();
			
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to get parameter's values", e);
			} 
			
			Assert.assertNotNull(lovResult, "Impossible to get parameter's values" );
			Assert.assertNotNull(lovProvDet, "Impossible to get parameter's meta" );
			
			
			try {
				if(filtersJSON != null) {
					String valuefilter = (String) filtersJSON.get(SpagoBIConstants.VALUE_FILTER);
					String columnfilter = (String) filtersJSON.get(SpagoBIConstants.COLUMN_FILTER);
					String typeFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_FILTER);
					String typeValueFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_VALUE_FILTER);
					rows = filterList(rows, valuefilter, typeValueFilter, columnfilter, typeFilter);
				}
			} catch (JSONException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to read filter's configuration", e);
			}
			

			
			
			
			try {
				// load parameter use ...
				IParameterUseDAO parusedao = DAOFactory.getParameterUseDAO();
				biParameterExecModality = parusedao.loadByParameterIdandRole(biObjectParameter.getParID(), roleName);
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to find any valid execution modality for parameter [" + biparameterId + "] and role [" + roleName + "]", e);
			}
			
			Assert.assertNotNull(biParameterExecModality, "Impossible to find any valid execution modality for parameter [" + biparameterId + "] and role [" + roleName + "]" );
			
			
			try {
				IObjParuseDAO objParuseDAO = DAOFactory.getObjParuseDAO();
				biParameterExecDependencies = objParuseDAO.loadObjParuse(biObjectParameter.getId(), biParameterExecModality.getUseID());
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException("An error occurred while loading parameter dependecies for parameter [" + biparameterId + "]", e);
			}
			
			if (selectedParameterValues != null && biParameterExecDependencies != null && biParameterExecDependencies.size() > 0) {
				if (biParameterExecDependencies.size() == 1) {
					ObjParuse biParameterExecDependency = (ObjParuse) biParameterExecDependencies.get(0);
					rows = filterForCorrelation(rows, biParameterExecDependency, selectedParameterValues);
				} else if (biParameterExecDependencies.size()==2) {
					ObjParuse biParameterExecDependency1 = (ObjParuse) biParameterExecDependencies.get(0);
					ObjParuse biParameterExecDependency2 = (ObjParuse) biParameterExecDependencies.get(1);
					rows = evaluateSingleLogicOperation(rows, biParameterExecDependency1, biParameterExecDependency2, selectedParameterValues);
				} else {
					// build the expression
					int posinlist = 0;
					String expr = "";
					Iterator iterOps = biParameterExecDependencies.iterator();
					while(iterOps.hasNext())  {
						ObjParuse op = (ObjParuse) iterOps.next();
						expr += op.getPreCondition() + posinlist + op.getPostCondition() + op.getLogicOperator();
						posinlist ++;
					}
					expr = expr.trim();
					expr = "(" + expr;
					expr = expr + ")";
					rows = evaluateExpression(rows, expr, biParameterExecDependencies, selectedParameterValues);
				}
	    	}
			
			
			
			JSONObject valuesJSON;
			try {
				JSONArray valuesDataJSON = new JSONArray();
				
				valueColumn = lovProvDet.getValueColumnName();
				displayColumn = lovProvDet.getDescriptionColumnName();
				descriptionColumn = displayColumn;
				
				
				
				int lb = (start != null)? start.intValue(): 0;
				int ub = (limit != null)? lb + limit.intValue(): rows.size() - lb;
				ub = (ub > rows.size())? rows.size(): ub;
				
				for (int q = lb; q < ub; q++) {
					SourceBean row = (SourceBean) rows.get(q);
					JSONObject valueJSON = new JSONObject();
					
					if(MODE_COMPLETE.equalsIgnoreCase( mode )) {
						List columns = row.getContainedAttributes();
						for(int i = 0; i < columns.size(); i++) {
							SourceBeanAttribute attribute = (SourceBeanAttribute)columns.get(i);						
							valueJSON.put(attribute.getKey().toUpperCase(), attribute.getValue());
						}
					} else {
						String value = (String) row.getAttribute(valueColumn);
						String description = (String) row.getAttribute(descriptionColumn);					
						valueJSON.put("value", value);
						valueJSON.put("label", description);
						valueJSON.put("description", description);	
					}					
					
					valuesDataJSON.put(valueJSON);
				}
				
				String[] visiblecolumns;
				
				if(MODE_COMPLETE.equalsIgnoreCase( mode )) {
					visiblecolumns = (String[])lovProvDet.getVisibleColumnNames().toArray(new String[0]);
					for(int j = 0; j< visiblecolumns.length; j++) {
						visiblecolumns[j] = visiblecolumns[j].toUpperCase();
					}
				} else {
					
					valueColumn = "value";
					displayColumn = "label";
					descriptionColumn = "description";
					
					visiblecolumns = new String[]{"value", "label", "description"};
				}
				
				valuesJSON = (JSONObject)JSONStoreFeedTransformer.getInstance().transform(valuesDataJSON, 
						valueColumn.toUpperCase(), displayColumn.toUpperCase(), descriptionColumn.toUpperCase(), visiblecolumns, new Integer(rows.size()));
			} catch (Exception e) {
				throw new SpagoBIServiceException("Impossible to serialize response", e);
			} 
			
			
			try {
				writeBackToClient( new JSONSuccess( valuesJSON ) );
			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
		
		} finally {
			logger.debug("OUT");
		}		

	}
	
	private List filterForCorrelation(List list, ObjParuse objParuse, Map selectedParameterValues) {
		try {
			
			Integer objParFatherId = objParuse.getObjParFatherId();
			BIObjectParameter objParFather = DAOFactory.getBIObjectParameterDAO().loadForDetailByObjParId(objParFatherId);
	        // get the general parameter associated to the bi parameter father
	        IParameterDAO parameterDAO = DAOFactory.getParameterDAO();
	        Parameter parameter = parameterDAO.loadForDetailByParameterID(objParFather.getParID());
	        
	        // get the type of the general parameter
	        String valueTypeFilter = parameter.getType();
			String valueFilter = "";
			
			
			Object values = selectedParameterValues.get(objParFather.getParameterUrlName());	
			
			// if the father parameter is no valued, returns the list unfiltered
			if (values == null) return list;
			
			String[] filterValues = null;
			if(values instanceof String) {
				filterValues = new String[]{(String)values};
			} else if(values instanceof String[]) {
				filterValues = (String[])values;
			} else {
				Assert.assertUnreachable("values associated to parameter [" + objParFather.getParameterUrlName() +"] are naither an instance of JSONObject nor of JSONArray");
			}
			
		
	        // based on the values number do different filter operations
			switch (filterValues.length) {
				case 0: return list;
				case 1: valueFilter = (String) filterValues[0];
						if (valueFilter != null && !valueFilter.equals(""))
							return filterList(list, valueFilter, valueTypeFilter, 
								objParuse.getFilterColumn(), objParuse.getFilterOperation());
						else return list;
				default: return filterList(list, filterValues, valueTypeFilter, 
								objParuse.getFilterColumn(), objParuse.getFilterOperation());
			}
		} catch (Exception e) {
			logger.error("Error while doing filter for corelation ", e);
			return list;
		}
	}

	
	private List evaluateSingleLogicOperation(List list, ObjParuse obpuLeft, ObjParuse obpuRight, Map selectedParameterValues) {
		List listToReturn = list;
		List listLeft = filterForCorrelation(list, obpuLeft, selectedParameterValues);
		String lo = obpuLeft.getLogicOperator();
		if(lo.equalsIgnoreCase("AND")) {
			listToReturn = filterForCorrelation(listLeft, obpuRight, selectedParameterValues);
		} else if(lo.equalsIgnoreCase("OR")) {
			List listRight = filterForCorrelation(list, obpuRight, selectedParameterValues);
			listToReturn = mergeLists(listLeft, listRight);
		} else {
			listToReturn = list;
		}
		return listToReturn;
	}
	
	private List mergeLists(List list1, List list2) {
		List margedList = new ArrayList();
		// transform all row sourcebean of the list 2 into strings and put them into a list
		Iterator rowsSBList2Iter = list2.iterator();
		List rowsList2 = new ArrayList();
		while(rowsSBList2Iter.hasNext()) {
			SourceBean rowSBList2 = (SourceBean)rowsSBList2Iter.next();
			String rowStrList2 = rowSBList2.toXML(false).toLowerCase();
			rowsList2.add(rowStrList2);
			margedList.add(rowSBList2);
		}
		// if a row of the list one is not contained into list 2 then add it to the list 2
		Iterator rowsSBList1Iter = list1.iterator();
		while(rowsSBList1Iter.hasNext()) {
			SourceBean rowSBList1 = (SourceBean)rowsSBList1Iter.next();
			String rowStrList1 = rowSBList1.toXML(false).toLowerCase();
			if(!rowsList2.contains(rowStrList1)) {
				margedList.add(rowSBList1);
			}
		}
		// return list 2
		return margedList;
	}
	
	private List evaluateExpression(List list, String expr, List ops, Map selectedParameterValues) {
		List previusCalculated = list;
		try {
			// check number of left and right break, if numbers are different the expression is wrong
			int numberOfLeftRound = 0;
			String tmpExpr = expr;
			while(tmpExpr.indexOf("(")!=-1) {
				numberOfLeftRound ++;
				int indLR = tmpExpr.indexOf("(");
				tmpExpr = tmpExpr.substring(indLR+1);
			}
			int numberOfRightRound = 0;
			tmpExpr = expr;
			while(tmpExpr.indexOf(")")!=-1) {
				numberOfRightRound ++;
				int indRR = tmpExpr.indexOf(")");
				tmpExpr = tmpExpr.substring(indRR+1);
			}
			if(numberOfLeftRound!=numberOfRightRound) {
				logger.warn("Expression is wrong: number of left breaks is different from right breaks. Returning list without evaluating expression");
				return list;
			}
				
			//TODO make some more formal check on the expression before start to process it
			
			// calculate the list filtered based on each objparuse setting
			Map calculatedLists = new HashMap();
			int posinlist = 0;
			Iterator opsIter = ops.iterator();
			while(opsIter.hasNext()) {
				ObjParuse op = (ObjParuse)opsIter.next();
				List listop = filterForCorrelation(list, op, selectedParameterValues);
				calculatedLists.put(String.valueOf(posinlist), listop);
				posinlist ++;
			}
			
			// generate final list evaluating expression
			
			while(expr.indexOf("(")!=-1) {
				int indLR = expr.indexOf("(");
				int indNextLR = expr.indexOf("(", indLR+1);
				int indNextRR = expr.indexOf(")", indLR+1);
				while( (indNextLR<indNextRR) && (indNextLR!=-1) ) {
					indLR = indNextLR;
					indNextLR = expr.indexOf("(", indLR+1);
					indNextRR = expr.indexOf(")", indLR+1);
				}
				int indRR = indNextRR;
				
				String exprPart = expr.substring(indLR, indRR+1);
				if(exprPart.indexOf("AND")!=-1) {
					int indexOper = exprPart.indexOf("AND");
					String firstListName = (exprPart.substring(1, indexOper)).replace("null", " ");
					String secondListName = (exprPart.substring(indexOper+3, exprPart.length()-1)).replace("null", " ");
					List firstList = null;
					if(!firstListName.trim().equals("previousList")) {
						firstList = (List)calculatedLists.get(firstListName.trim());
					} else {
						firstList = previusCalculated;
					}
					List secondList = null;
					if(!secondListName.trim().equals("previousList")) {
						secondList = (List)calculatedLists.get(secondListName.trim());
					} else {
						secondList = previusCalculated;
					}
					previusCalculated = intersectLists(firstList, secondList);
				} else if( exprPart.indexOf("OR")!=-1 ) {
					int indexOper = exprPart.indexOf("OR");
					String firstListName = (exprPart.substring(1, indexOper)).replace("null", " ");
					String secondListName = (exprPart.substring(indexOper+2, exprPart.length()-1)).replace("null", " ");
					List firstList = null;
					if(!firstListName.trim().equals("previousList")) {
						firstList = (List)calculatedLists.get(firstListName.trim());
					} else {
						firstList = previusCalculated;
					}
					List secondList = null;
					if(!secondListName.trim().equals("previousList")) {
						secondList = (List)calculatedLists.get(secondListName.trim());
					} else {
						secondList = previusCalculated;
					}
					previusCalculated = mergeLists(firstList, secondList);
				} else {
					// previousList remains the same as before
					logger.warn("A part of the Expression is wrong: inside a left break and right break there's no condition AND or OR");
				}
				expr = expr.substring(0, indLR) + "previousList" + expr.substring(indRR+1);
			}
		} catch (Exception e) {
			logger.warn("An error occurred while evaluating expression, return the complete list");
			return list;
		}
		return previusCalculated;
	}
	
	protected static List intersectLists(List list1, List list2) {
		
		// transform all row sourcebean of the list 2 into strings and put them into a list
		
		
		Iterator rowsSBList2Iter = list2.iterator();
		List rowsList2 = new ArrayList();
		while(rowsSBList2Iter.hasNext()) {
			SourceBean rowSBList2 = (SourceBean)rowsSBList2Iter.next();
			String rowStrList2 = rowSBList2.toXML(false).toLowerCase();
			rowsList2.add(rowStrList2);
		}
		
		List newlist = new ArrayList();	
				
		// if a row of the list one is contained into list 2 then add it to the reulting list
		Iterator rowsSBList1Iter = list1.iterator();
		while(rowsSBList1Iter.hasNext()) {
			SourceBean rowSBList1 = (SourceBean)rowsSBList1Iter.next();
			String rowStrList1 = rowSBList1.toXML(false).toLowerCase();
			if(rowsList2.contains(rowStrList1)) {
				newlist.add(rowSBList1);
			}
		}
		
		
		return newlist;
	}
	
	
	/**
	 * Filters the list with a list of filtering values.
	 * 
	 * @param list The list to be filtered
	 * @param valuesfilter The list of filtering values
	 * @param valuetypefilter The type of the value of the filter (STRING/NUM/DATE)
	 * @param columnfilter The column to be filtered
	 * @param typeFilter The type of the filter
	 * @param errorHandler The EMFErrorHandler object, in which errors are stored if they occurs
	 * 
	 * @return the filtered list
	 */
	public List filterList(List list, String[] valuesfilter, String valuetypefilter, String columnfilter, 
						String typeFilter) {
		
		
		List newList = new ArrayList();
		
		if ((valuesfilter == null) || (valuesfilter.length ==0)) {
			return list;
		}
		
		if (StringUtilities.isEmpty(columnfilter)) {
			return list;
		}
		if (StringUtilities.isEmpty(typeFilter)) {
			return list;
		}
		
		if (StringUtilities.isEmpty(valuetypefilter)) {
			return list;
		}
		
		if (typeFilter.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)
				|| typeFilter.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)
				|| typeFilter.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)
				|| typeFilter.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {

			Assert.assertUnreachable("filterList with a list of filtering values: the filter type " + typeFilter + " is not applicable for multi-values filtering.");
		}

		// controls the correctness of the filtering conditions
		//boolean filterConditionsAreCorrect = verifyFilterConditions(valuetypefilter, typeFilter, errorHandler);
		//if (!filterConditionsAreCorrect) return list;

		
		Iterator iterRow = list.iterator();
		while (iterRow.hasNext()) {
			SourceBean row = (SourceBean) iterRow.next();
			boolean doesRowSatisfyCondition = false;
			for(int i = 0; i < valuesfilter.length; i++) {
				String valuefilter = valuesfilter[i];
				try {
					if (valuefilter != null && !valuefilter.equals(""))
						doesRowSatisfyCondition = 
							doesRowSatisfyCondition(row, valuefilter, valuetypefilter, columnfilter, typeFilter);
					else doesRowSatisfyCondition = true;
				} catch (EMFValidationError error) {
					error.printStackTrace();
					return list;
				}
				if (doesRowSatisfyCondition) break;
			}
			if (doesRowSatisfyCondition) newList.add(row);
		}
	
		return newList;
	}

	
	public List filterList(List list, String valuefilter, String valuetypefilter, String columnfilter, 
						String typeFilter) {
		
		
		List newList = new ArrayList();
		
		Assert.assertTrue(!StringUtilities.isEmpty(valuefilter), "the value filter is not set");
		
		if (StringUtilities.isEmpty(columnfilter)) {
			return list;
		}
		if (StringUtilities.isEmpty(typeFilter)) {
			return list;
		}
		
		if (StringUtilities.isEmpty(valuetypefilter)) {
			return list;
		}
		
		// controls the correctness of the filtering conditions
		//boolean filterConditionsAreCorrect = verifyFilterConditions(valuetypefilter, typeFilter, errorHandler);
		//if (!filterConditionsAreCorrect) return list;

		Iterator iterRow = list.iterator();
		while (iterRow.hasNext()) {
			SourceBean row = (SourceBean) iterRow.next();
			boolean doesRowSatisfyCondition = false;
			try {
				doesRowSatisfyCondition = doesRowSatisfyCondition(row, valuefilter, valuetypefilter, columnfilter, typeFilter);
			} catch (EMFValidationError error) {
				error.printStackTrace();
				return list;
			}
			if (doesRowSatisfyCondition) newList.add(row);
		}
		
		return newList;
	}
	
	
	
	private boolean doesRowSatisfyCondition(SourceBean row, String valuefilter, String valuetypefilter, String columnfilter, 
			String typeFilter) throws EMFValidationError {
		Object attribute = row.getAttribute(columnfilter);
		if (attribute == null) return false;
		String value = attribute.toString();
		if (value == null)
			value = "";
		// case of string filtering
		if (valuetypefilter.equalsIgnoreCase(SpagoBIConstants.STRING_TYPE_FILTER)) {
			valuefilter = valuefilter.toUpperCase();
			value = value.toUpperCase();
			if (typeFilter.equalsIgnoreCase(SpagoBIConstants.START_FILTER)) {
				return value.trim().startsWith(valuefilter);
			} else if (typeFilter.equalsIgnoreCase(SpagoBIConstants.END_FILTER)) {
				return value.trim().endsWith(valuefilter);
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.CONTAIN_FILTER)) {
				return value.indexOf(valuefilter) != -1;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.EQUAL_FILTER)) {
				return value.equals(valuefilter)
						|| value.trim().equals(valuefilter);
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)) {
				return value.trim().compareToIgnoreCase(valuefilter) < 0; 
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)) {
				return value.trim().compareToIgnoreCase(valuefilter) <= 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)) {
				return value.trim().compareToIgnoreCase(valuefilter) > 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {
				return value.trim().compareToIgnoreCase(valuefilter) >= 0;
			} else {
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, SpagoBIConstants.TYPE_FILTER, "100", null, params);
				throw error;
			}
		}
		// case of number filtering
		else if (valuetypefilter.equalsIgnoreCase(SpagoBIConstants.NUMBER_TYPE_FILTER)) {
			Double valueDouble = null;
			Double valueFilterDouble = null;
			try {
				valueDouble = new Double(value);
			} catch (Exception e) {
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: the string value is not a recognizable number representations: value to be filtered = " + value, e);
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList");
				Vector v = new Vector();
				v.add(value);
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.WARNING, SpagoBIConstants.TYPE_VALUE_FILTER, "1051", v, params);
				throw error;
			}
			try {
				valueFilterDouble = new Double(valuefilter);
			} catch (Exception e) {
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: input string value is not a recognizable number representations: filter value = " + valuefilter, e);
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList");
				Vector v = new Vector();
				v.add(valuefilter);
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.WARNING, SpagoBIConstants.VALUE_FILTER, "1052", v, params);
				throw error;
			}
			
			//if (valueDouble == null || valueFilterDouble == null) return list;
			
			if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.EQUAL_FILTER)) {
				return valueDouble.doubleValue() == valueFilterDouble.doubleValue();
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)) {
				return valueDouble.doubleValue() < valueFilterDouble.doubleValue();
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)) {
				return valueDouble.doubleValue() <= valueFilterDouble.doubleValue();
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)) {
				return valueDouble.doubleValue() > valueFilterDouble.doubleValue();
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {
				return valueDouble.doubleValue() >= valueFilterDouble.doubleValue();
			} else {
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, SpagoBIConstants.TYPE_FILTER, "100", null, params);
				throw error;
			}
		}
		// case of date filtering
		else if (valuetypefilter.equalsIgnoreCase(SpagoBIConstants.DATE_TYPE_FILTER)) {
	        ConfigSingleton config = ConfigSingleton.getInstance();
//		    SourceBean formatSB = (SourceBean) config.getAttribute("DATA-ACCESS.DATE-FORMAT");
	        SourceBean formatSB = (SourceBean) config.getAttribute("SPAGOBI.DATE-FORMAT-SERVER");
		    String format = (String) formatSB.getAttribute("format");
		    TracerSingleton.log(
					Constants.NOME_MODULO,
					TracerSingleton.WARNING,
					"DelegatedBasicListService::filterList: applying date format " + format + " for filtering.");
//		    format = format.replaceAll("D", "d");
//		    format = format.replaceAll("m", "M");
//		    format = format.replaceAll("Y", "y");
	        Date valueDate = null;
	        Date valueFilterDate = null;
			try {
				valueDate = toDate(value, format);
	        } catch (Exception e) { 
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: the string value is not a valid date representation according to the format " + format + ": value to be filtered = " + value, e);
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList");
				Vector v = new Vector();
				v.add(value);
				v.add(format);
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.WARNING, SpagoBIConstants.TYPE_VALUE_FILTER, "1054", v, params);
				throw error;
	        }
			try {
				valueFilterDate = toDate(valuefilter, format);
	        } catch (Exception e) { 
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: input string is not a valid date representation according to the format " + format + ": filter value = " + valuefilter, e);
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList");
				Vector v = new Vector();
				v.add(valuefilter);
				v.add(format);
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.WARNING, SpagoBIConstants.VALUE_FILTER, "1055", v, params);
				throw error;
	        }
	        
	        //if (valueDate == null || valueFilterDate == null) return list;
	        
			if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.EQUAL_FILTER)) {
				return valueDate.compareTo(valueFilterDate) == 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)) {
				return valueDate.compareTo(valueFilterDate) < 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)) {
				return valueDate.compareTo(valueFilterDate) <= 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)) {
				return valueDate.compareTo(valueFilterDate) > 0;
			} else if (typeFilter
					.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {
				return valueDate.compareTo(valueFilterDate) >= 0;
			} else {
				TracerSingleton.log(
						Constants.NOME_MODULO,
						TracerSingleton.WARNING,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				HashMap params = new HashMap();
				params.put(Constants.NOME_MODULO,
						"DelegatedBasicListService::filterList: the filter type '" + typeFilter + "' is not a valid filter type");
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, SpagoBIConstants.TYPE_FILTER, "100", null, params);
				throw error;
			}
		}
		else {
			TracerSingleton.log(
					Constants.NOME_MODULO,
					TracerSingleton.WARNING,
					"DelegatedBasicListService::filterList: the filter value type '" + valuetypefilter + "' is not a valid filter value type");
			HashMap params = new HashMap();
			params.put(Constants.NOME_MODULO,
					"DelegatedBasicListService::filterList: the filter value type '" + valuetypefilter + "' is not a valid filter value type");
			EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, SpagoBIConstants.TYPE_FILTER, "100", null, params);
			throw error;
		}
	}
	
	/*
	 * Converts a String representing a date into a Date object, given the date format.
	 * 
	 * @param dateStr The String representing the date
	 * @param format The date format
	 * 
	 * @return the relevant Date object
	 * 
	 * @throws Exception if any parsing exception occurs
	 */
	public Date toDate(String dateStr, String format) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
       Date date = null;
		try {
           dateFormat.applyPattern(format);
           dateFormat.setLenient(false);
           date = dateFormat.parse(dateStr);
       } catch (Exception e) { 
       	throw e;
       }
       return date;
	}
	
}
