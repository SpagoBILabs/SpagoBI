/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/

package it.eng.spagobi.engine.mobile;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class MobileEngineInstance extends AbstractEngineInstance {

	private SourceBean template;
	private Map<String, IDataSet> datasets = new HashMap<String, IDataSet>();
	private JSONObject title = new JSONObject();
	private JSONArray columns = new JSONArray();
	private JSONArray fields = new JSONArray();
	private JSONArray conditions = new JSONArray();
	
	public JSONArray getConditions() {
		return conditions;
	}

	public JSONArray getFields() {
		return fields;
	}

	public JSONArray getColumns() {
		return columns;
	}

	public JSONObject getTitle() {
		return title;
	}

	private static transient Logger logger = Logger.getLogger(MobileEngineInstance.class);
	
	public SourceBean getTemplate() {
		return template;
	}
	
	public void setTemplate(SourceBean template) {
		this.template = template;
	}
	
	public void buildColumnsJSON(SourceBean template, Map env) throws Exception {

		logger.debug("IN");
		List cols = (List)template.getAttributeAsList(MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
		if(cols == null) {
			logger.warn("Cannot find columns configuration settings: tag name " + MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
		}
		Vector alarms = new Vector();
		for(int i=0; i<cols.size(); i++){
			SourceBean column = (SourceBean)cols.get(i);
			JSONObject colJSON = new JSONObject();
			String value = (String)column.getAttribute(MobileConstants.COLUMN_VALUE_ATTR);
			colJSON.put("mapping", value);
			
			String header = (String)column.getAttribute(MobileConstants.COLUMN_HEADER_ATTR);
			colJSON.put("header", header);

			String style = (String)column.getAttribute(MobileConstants.COLUMN_STYLE_ATTR);
			if(style!= null){
				colJSON.put("style", style);
			}
			String alarm = (String)column.getAttribute(MobileConstants.COLUMN_ALARM_ATTR);
			if(alarm!= null){
				colJSON.put("alarm", alarm);
			}
			List conditionslist = (List)column.getAttributeAsList(MobileConstants.CONDITIONS_TAG+"."+MobileConstants.CONDITION_TAG);
			if(conditionslist != null){
				for(int k=0; k<conditionslist.size(); k++){
					SourceBean condition = (SourceBean)conditionslist.get(k);
					String styleCond = (String)condition.getAttribute(MobileConstants.CONDITION_STYLE_ATTR);
					String conditionCond = (String)condition.getCharacters();
					JSONObject condJSON = new JSONObject();
					condJSON.put("column", value);
					condJSON.put("style", styleCond);
					condJSON.put("alarm", alarm);
					condJSON.put("condition", conditionCond);
					conditions.put(condJSON);
					if(!alarms.contains(alarm)){
						fields.put(alarm);//check that it has been put only once!!!!
						alarms.add(alarm);
					}
				}
			}
			
			fields.put(value);
			columns.put(colJSON);
		}


		logger.debug("OUT");		

	}
	
	public void buildTitleJSON(SourceBean template, Map env) throws Exception {
		
		SourceBean confSB = null;
		String titleName = null;
		
		logger.debug("IN");
		confSB = (SourceBean)template.getAttribute(MobileConstants.TITLE_TAG);
		if(confSB == null) {
			logger.warn("Cannot find title configuration settings: tag name " + MobileConstants.TITLE_TAG);
		}
		titleName = (String)confSB.getAttribute(MobileConstants.TITLE_VALUE_ATTR);
		String titleStyle = (String)confSB.getAttribute(MobileConstants.TITLE_STYLE_ATTR);
		
		title.put("value", titleName);
		title.put("style", titleStyle);

		logger.debug("OUT");		

	}
	public MobileEngineInstance(SourceBean template, Map env) {
		super( env );	
		
		try {
			buildTitleJSON(template, env);
			buildColumnsJSON(template, env);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to parse template", t);
		}
				
		setTemplate(template);	
		
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}
	
	public IDataSet getDataSet() {		
		return (IDataSet)this.getEnv().get(EngineConstants.ENV_DATASET);
	}

	public DataSetServiceProxy getDataSetServiceProxy() {
		return (DataSetServiceProxy)this.getEnv().get(EngineConstants.ENV_DATASET_PROXY);
	}
	public IDataSet getDataSet(String label) {
		return this.datasets.get(label);
	}
	
	public void setDataSet(String label, IDataSet dataset) {
		this.datasets.put(label, dataset);
	}
	
	@Override
	public IEngineAnalysisState getAnalysisState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnalysisState(IEngineAnalysisState analysisState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate() throws SpagoBIEngineException {
		// TODO Auto-generated method stub

	}

}
