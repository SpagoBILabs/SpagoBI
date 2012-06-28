/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class contains useful methods for LOV (list of values) that must evaluate dependencies (correlations with 
 * other parameters) AFTER their execution. They are: SCRIPT, FIX_LIST and JAVA_CLASS: classes representing those 
 * kind of LOV ({@link ScriptDetail}, {@link FixedListDetail} and {@link JavaClassDetail}) extend this class.
 * The QUERY lov instead process dependencies when executing the query itself, i.e. the query is modified in order to 
 * consider also the dependencies, therefore the {@link QueryDetail} class does not extend this class.
 * 
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public abstract class DependenciesPostProcessingLov {
	
	private static transient Logger logger = Logger.getLogger(DependenciesPostProcessingLov.class);
	
	/**
	 * Filters the input list according to the provided dependencies' configuration and the parameters' values.
	 * 
	 * @param rows The list of rows
	 * @param selectedParameterValues The values of the parameters
	 * @param dependencies The dependencies' configuration
	 * @return the list filtered considering the dependencies
	 */
	public List processDependencies(List rows, Map selectedParameterValues, List<ObjParuse> dependencies) {
		if (selectedParameterValues != null && dependencies != null && dependencies.size() > 0) {
			if (dependencies.size() == 1) {
				ObjParuse biParameterExecDependency = (ObjParuse) dependencies.get(0);
				rows = filterForCorrelation(rows, biParameterExecDependency, selectedParameterValues);
			} else if (dependencies.size()==2) {
				ObjParuse biParameterExecDependency1 = (ObjParuse) dependencies.get(0);
				ObjParuse biParameterExecDependency2 = (ObjParuse) dependencies.get(1);
				rows = evaluateSingleLogicOperation(rows, biParameterExecDependency1, biParameterExecDependency2, selectedParameterValues);
			} else {
				// build the expression
				int posinlist = 0;
				String expr = "";
				Iterator iterOps = dependencies.iterator();
				while(iterOps.hasNext())  {
					ObjParuse op = (ObjParuse) iterOps.next();
					expr += op.getPreCondition() + posinlist + op.getPostCondition() + op.getLogicOperator();
					posinlist ++;
				}
				expr = expr.trim();
				expr = "(" + expr;
				expr = expr + ")";
				rows = evaluateExpression(rows, expr, dependencies, selectedParameterValues);
			}
		}
		return rows;
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
							return DelegatedBasicListService.filterList(list, valueFilter, valueTypeFilter, 
								objParuse.getFilterColumn(), objParuse.getFilterOperation());
						else return list;
				default: return DelegatedBasicListService.filterList(list, filterValues, valueTypeFilter, 
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

}
