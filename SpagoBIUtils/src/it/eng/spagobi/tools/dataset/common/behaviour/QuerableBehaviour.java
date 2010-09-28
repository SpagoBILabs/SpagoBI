/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.common.behaviour;

import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.DataSetParametersList;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.common.query.IQueryTransformer;
import it.eng.spagobi.tools.dataset.exceptions.ParameterDsException;
import it.eng.spagobi.tools.dataset.exceptions.ProfileAttributeDsException;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QuerableBehaviour extends AbstractDataSetBehaviour {

	IQueryTransformer queryTransformer;
	private static transient Logger logger = Logger.getLogger(QuerableBehaviour.class);

	public QuerableBehaviour(IDataSet targetDataSet) {
		super(QuerableBehaviour.class.getName(), targetDataSet);
	}

	public String getStatement() throws EMFInternalError, EMFUserError{
		String statement;

		logger.debug("IN");
		try {
			Assert.assertNotNull(getTargetDataSet(), "Target dataset of a QuerableBehaviour cannot be null");

			logger.debug("Querable dataset [" + getTargetDataSet().getName() + "] is of type [" + getTargetDataSet().getClass().getName() + "]");


			if (getTargetDataSet() instanceof ScriptDataSet) {
				statement = (String) ((ScriptDataSet)getTargetDataSet()).getScript();
			} else if (getTargetDataSet() instanceof JDBCDataSet) {
				statement = (String) ((JDBCDataSet)getTargetDataSet()).getQuery();
			} else {
				// maybe better to delete getQuery from IDataSet
				statement = (String)getTargetDataSet().getQuery();
			}
			logger.debug("Original dataset statement [" + statement + "]");
			Assert.assertNotNull(statement, "Querable dataset statment cannot be null");
			// if script substitute profile attributes in a strict way
			if (getTargetDataSet() instanceof ScriptDataSet) {
				try{
					HashMap attributes = getAllProfileAttributes(getTargetDataSet().getUserProfile()); // to be cancelled, now substitutution inline
					statement = substituteProfileAttributes(statement, attributes);
				} catch (EMFInternalError e) {
					ProfileAttributeDsException parException= new ProfileAttributeDsException(EMFErrorSeverity.ERROR, 9221, e, "");
					logger.error("Error in query Execution",e);
					throw parException;				
				}
			} else if (getTargetDataSet() instanceof JDBCDataSet) {	
				try {
					statement = StringUtilities.substituteProfileAttributesInString(statement, getTargetDataSet().getUserProfile() );
				} catch (Exception e) {
					List list = checkProfileAttributesUnfilled(statement);
					logger.error("there are profile attributes without values");
					String atts = "";
					for (Iterator iterator = list.iterator(); iterator.hasNext();) {
						String string = (String) iterator.next();
						atts += string;
						if(iterator.hasNext()){
							atts += ", ";
						}
					}
					ProfileAttributeDsException parException= new ProfileAttributeDsException(EMFErrorSeverity.ERROR, 9221, e, atts);
					throw parException;	

					
				}
			}

			logger.debug("Dataset statement after profile attributes substitution [" + statement + "]");
			logger.debug("Dataset paramMap [" + getTargetDataSet().getParamsMap() + "]");

			//check if there are parameters filled
			//if( getTargetDataSet().getParamsMap() != null && !getTargetDataSet().getParamsMap().isEmpty()){
			if( getTargetDataSet().getParamsMap() != null){
				logger.debug("Dataset paramMap contains [" + getTargetDataSet().getParamsMap().size() + "] parameters");

				// if a parameter has value '' put null!
				Map parameterValues = getTargetDataSet().getParamsMap();
				Vector<String> parsToChange = new Vector<String>();

				for (Iterator iterator = parameterValues.keySet().iterator(); iterator.hasNext();) {
					String parName = (String) iterator.next();
					Object val = parameterValues.get(parName);
					if( val != null && val.equals("")){
						val = null;
						parsToChange.add(parName);
					}
					//parameterValues.remove(parName);
					//parameterValues.put(parName, val);
				}
				for (Iterator iterator = parsToChange.iterator(); iterator.hasNext();) {
					String parName = (String) iterator.next();
					parameterValues.remove(parName);
					parameterValues.put(parName, null);
				}

				try{
					Map parTypeMap = getParTypeMap(getTargetDataSet());
					statement = StringUtilities.substituteDatasetParametersInString(statement, getTargetDataSet().getParamsMap(), parTypeMap ,false );
				}
				catch (Exception e) {
					if(e instanceof NumberFormatException){
						String attribute = ((NumberFormatException)e).getMessage() + " value should be of type number";
						ParameterDsException parException= new ParameterDsException(EMFErrorSeverity.ERROR, 9221, e, attribute);
						throw parException;			
					}
					else{
						logger.error("Errore nella valorizzazione dei parametri",e);
						String attribute = e.getMessage();
						ParameterDsException parException= new ParameterDsException(EMFErrorSeverity.ERROR, 9221, e, attribute);
						throw parException;			
					}
				}
			}	

			// after having substituted all parameters check there are not other parameters unfilled otherwise throw an exception;
			List<String> parsUnfilled = checkParametersUnfilled(statement);
			if(parsUnfilled != null){
				// means there are parameters not valorized, throw exception
				logger.error("there are parameters without values");
				String pars = "";
				for (Iterator iterator = parsUnfilled.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					pars += string;
					if(iterator.hasNext()){
						pars += ", ";
					}
				}
				pars += " have no value specified";
				ParameterDsException parException= new ParameterDsException(EMFErrorSeverity.ERROR, 
						9221, new Exception(), pars);
				throw parException;	
			}


			logger.debug("Dataset statement after  attributes substitution [" + statement + "]");


			if(queryTransformer != null) {
				statement = (String)queryTransformer.transformQuery( statement );
			}

		} finally {
			logger.debug("OUT");
		}

		return statement;
	}


	private String substituteProfileAttributes(String script, HashMap attributes) throws EMFInternalError{
		logger.debug("IN");
		String cleanScript=new String(script);
		int indexSubstitution=0;
		int profileAttributeStartIndex = script.indexOf("${",indexSubstitution);

		while (profileAttributeStartIndex != -1) {
			int profileAttributeEndIndex=script.indexOf("}",profileAttributeStartIndex);
			String attributeName = script.substring(profileAttributeStartIndex + 2, profileAttributeEndIndex).trim();
			Object attributeValueObj = attributes.get(attributeName);
			if(attributeValueObj==null)
			{
				logger.error("Profile attribute "+attributeName+" not found");
				attributeValueObj="undefined";
			}
			cleanScript=cleanScript.replaceAll("\\$\\{"+attributeName+"\\}", attributeValueObj.toString());
			indexSubstitution=profileAttributeEndIndex;
			profileAttributeStartIndex = script.indexOf("${",indexSubstitution);
		}
		logger.debug("OUT");
		return cleanScript;	
	}


	public Map getParTypeMap(IDataSet dataSet) throws SourceBeanException{

		Map parTypeMap;
		String parametersXML;
		List parameters;

		logger.debug("IN");

		try {		
			parTypeMap = new HashMap();
			parametersXML= dataSet.getParameters();	

			logger.debug("Dataset parameters string is equals to [" + parametersXML + "]");

			if ( !StringUtilities.isEmpty(parametersXML) ) {
				parameters = DataSetParametersList.fromXML(parametersXML).getItems();
				logger.debug("Dataset have  [" + parameters.size() + "] parameters");

				for (int i = 0; i < parameters.size(); i++) {
					DataSetParameterItem dsDet = (DataSetParameterItem) parameters.get(i); 
					String name = dsDet.getName();
					String type = dsDet.getType();
					logger.debug("Paremeter [" + (i+1) + "] name is equals to  [" + name + "]");
					logger.debug("Paremeter [" + (i+1) + "] type is equals to  [" + type + "]");
					parTypeMap.put(name, type);
				}
			}	


		} finally {
			logger.debug("OUT");
		}

		return parTypeMap;
	}

	/**
	 * Gets the all profile attributes. (Also present in GeneralUtilities) TODO: centralization of two methods
	 * 
	 * @param profile the profile
	 * 
	 * @return the all profile attributes
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public static HashMap getAllProfileAttributes(IEngUserProfile profile) throws EMFInternalError {
		logger.debug("IN");
		if (profile == null)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
			"getAllProfileAttributes method invoked with null input profile object");
		HashMap profileattrs = new HashMap();
		Collection profileattrsNames = profile.getUserAttributeNames();
		if (profileattrsNames == null || profileattrsNames.size() == 0)
			return profileattrs;
		Iterator it = profileattrsNames.iterator();
		while (it.hasNext()) {
			Object profileattrName = it.next();
			Object profileattrValue = profile.getUserAttribute(profileattrName.toString());
			profileattrs.put(profileattrName, profileattrValue);
		}
		logger.debug("OUT");
		return profileattrs;
	}


	/** search if there are parameters unfilled and return their names
	 * 
	 * @param statement
	 * @return
	 */

	public static List checkParametersUnfilled(String statement){
		List toReturn = null;
		int index = statement.indexOf("$P{");
		while (index != -1){
			int endIndex = statement.indexOf('}', index);
			if(endIndex != -1){
				String nameAttr = statement.substring(index, endIndex+1);
				if(toReturn == null) toReturn = new ArrayList<String>();
				toReturn.add(nameAttr);
				index = statement.indexOf("$P{", endIndex);
			}
		}
		return toReturn;
	}

	public static List checkProfileAttributesUnfilled(String statement){
		List toReturn = null;
		int index = statement.indexOf("${");
		while (index != -1){

			int endIndex = statement.indexOf('}', index);
			if(endIndex != -1){
				String nameAttr = statement.substring(index, endIndex+1);
				if(toReturn == null) toReturn = new ArrayList<String>();
				toReturn.add(nameAttr);
				index = statement.indexOf("${", endIndex);
			}
		}
		return toReturn;
	}

	public IQueryTransformer getQueryTransformer() {
		return queryTransformer;
	}

	public void setQueryTransformer(IQueryTransformer queryTransformer) {
		this.queryTransformer = queryTransformer;
	}
}
