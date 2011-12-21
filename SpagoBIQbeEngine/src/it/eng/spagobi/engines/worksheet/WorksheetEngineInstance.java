/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.worksheet;

import it.eng.spagobi.engines.qbe.QbeEngineException;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.engines.worksheet.template.WorksheetTemplate;
import it.eng.spagobi.engines.worksheet.template.WorksheetTemplateParser;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorksheetEngineInstance extends AbstractEngineInstance {

	IDataSource dataSource;
	IDataSet dataSet;
	WorksheetTemplate template;

	/** Logger component. */
	public static transient Logger logger = Logger.getLogger(QbeEngineInstance.class);


	protected WorksheetEngineInstance(Object template, Map env) throws WorksheetEngineException {
		this( WorksheetTemplateParser.getInstance().parse(template, env), env );
	}

	protected WorksheetEngineInstance(WorksheetTemplate template, Map env) throws WorksheetEngineException {
		super( env );
		logger.debug("IN");
		this.template = template;
		logger.debug("OUT");
	}

	public void validate() throws QbeEngineException {
		return;
	}

	public WorksheetTemplate getTemplate() {
		return template;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineInstance#getAnalysisState()
	 */
	//@Override
	public IEngineAnalysisState getAnalysisState() {
		return this.getTemplate().getWorkSheetDefinition();
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineInstance#setAnalysisState(it.eng.spagobi.utilities.engines.IEngineAnalysisState)
	 */
	//@Override
	public void setAnalysisState(IEngineAnalysisState analysisState) {
		this.getTemplate().setWorkSheetDefinition((WorkSheetDefinition)analysisState);
	}

	public IDataSet getDataSet() {
		if(dataSet!=null){
			return dataSet; 
		}
		if(template!=null){
			return template.getDataSet(); 
		}
		return null;
	}

	public void setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet; 
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public QbeEngineInstance getQbeEngineInstance() {
		return template.getQbeEngineInstance();
	}
	
	public void setQbeEngineInstance(QbeEngineInstance qbeEngineInstance){
		template.setQbeEngineInstance(qbeEngineInstance);
	}

}
