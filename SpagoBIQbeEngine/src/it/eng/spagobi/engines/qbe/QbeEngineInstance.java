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
package it.eng.spagobi.engines.qbe;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.accessmodality.AbstractModelAccessModality;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.catalogue.QueryCatalogue;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.qbe.datasource.QbeDataSourceManager;
import it.eng.spagobi.engines.qbe.template.QbeTemplate;
import it.eng.spagobi.engines.qbe.template.QbeTemplateParser;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeEngineInstance extends AbstractEngineInstance {
	
	IDataSource dataSource;		
	QueryCatalogue queryCatalogue;
	String activeQueryId;
	QbeTemplate template;
	FormState formState;
	WorkSheetDefinition workSheetDefinition;

	// executable version of the query. cached here for performance reasons (i.e. avoid query re-compilation 
	// over result-set paging)
	IStatement statment;

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeEngineInstance.class);
	

	protected QbeEngineInstance(Object template, Map env) throws QbeEngineException {
		this( QbeTemplateParser.getInstance().parse(template), env );
	}
	
	protected QbeEngineInstance(QbeTemplate template, Map env) throws QbeEngineException {
		super( env );
		
		logger.debug("IN");
		
		this.template = template;
		
		queryCatalogue = new QueryCatalogue();
		queryCatalogue.addQuery(new Query());
		
				
		it.eng.spagobi.tools.datasource.bo.IDataSource dataSrc = (it.eng.spagobi.tools.datasource.bo.IDataSource)env.get( EngineConstants.ENV_DATASOURCE );
		SpagoBiDataSource ds = dataSrc.toSpagoBiDataSource();
		
		ConnectionDescriptor connection = new ConnectionDescriptor();			
		connection.setName( ds.getLabel() );
		connection.setDialect( ds.getHibDialectClass() );			
		connection.setJndiName( ds.getJndiName() );			
		connection.setDriverClass( ds.getDriver() );			
		connection.setPassword( ds.getPassword() );
		connection.setUrl( ds.getUrl() );
		connection.setUsername( ds.getUser() );			
		
		Map<String, Object> dataSourceProperties = new HashMap<String, Object>();
		dataSourceProperties.put("connection", connection);
		dataSourceProperties.put("dblinkMap", template.getDbLinkMap());
		
		
		dataSource = QbeDataSourceManager.getInstance().getDataSource(template.getDatamartNames(), dataSourceProperties);
				
		
		if(template.getDatamartModelAccessModality() != null) {
			
			if(template.getDatamartModelAccessModality().getRecursiveFiltering() == null) {
				String recursiveFilteringAttr = dataSource.getModelStructure().getPropertyAsString(AbstractModelAccessModality.ATTR_RECURSIVE_FILTERING);
				if(!StringUtilities.isEmpty(recursiveFilteringAttr)) {
					if("disabled".equalsIgnoreCase(recursiveFilteringAttr)) {
						template.getDatamartModelAccessModality().setRecursiveFiltering( Boolean.FALSE );
					} else {
						template.getDatamartModelAccessModality().setRecursiveFiltering( Boolean.TRUE );
					}
				} else {
					template.getDatamartModelAccessModality().setRecursiveFiltering( Boolean.TRUE );
				}
			}
			
			dataSource.setDataMartModelAccessModality( template.getDatamartModelAccessModality() );
		}
		
		
		
		if( template.getProperty("query") != null ) {
			try {
				QbeEngineAnalysisState analysisState = new QbeEngineAnalysisState( dataSource );
				// TODO set the encoding
				analysisState.load( template.getProperty("query").toString().getBytes() );
				setAnalysisState( analysisState );
			} catch(Throwable t) {
				SpagoBIRuntimeException serviceException;
				String msg = "Impossible load query [" + template.getProperty("query") + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIRuntimeException(msg, t);
				
				throw serviceException;
			}
		}
		
		if( template.getProperty("formJSONTemplate") != null ) {
			try {
				FormState formState = new FormState();
				// TODO set the encoding
				formState.load( template.getProperty("formJSONTemplate").toString().getBytes() );
				setFormState( formState );
			} catch(Throwable t) {
				SpagoBIRuntimeException serviceException;
				String msg = "Impossible load form state [" + template.getProperty("formJSONTemplate") + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIRuntimeException(msg, t);
				
				throw serviceException;
			}
		}
		
		validate();
		
		logger.debug("OUT");
	}
	
	public void setFormState(FormState formState) {
		this.formState = formState;
	}
	
	public FormState getFormState() {
		return this.formState;
	}

	public void validate() throws QbeEngineException {
		return;
	}
	
	public IEngineAnalysisState getAnalysisState() {
		QbeEngineAnalysisState analysisState = null;
		analysisState= new QbeEngineAnalysisState( dataSource );
		analysisState.setCatalogue( this.getQueryCatalogue() );
		if (this.getWorkSheetDefinition() != null) {
			analysisState.setWorkSheetDefinition( this.getWorkSheetDefinition() );
		} else {
			analysisState.setWorkSheetDefinition( WorkSheetDefinition.EMPTY_WORKSHEET );
		}
		return analysisState;
	}
	
	public void setAnalysisState(IEngineAnalysisState analysisState) {	
		QbeEngineAnalysisState qbeEngineAnalysisState = null;
		
		qbeEngineAnalysisState = (QbeEngineAnalysisState)analysisState;
		setQueryCatalogue( qbeEngineAnalysisState.getCatalogue(  ) );
		setWorkSheetDefinition( qbeEngineAnalysisState.getWorkSheetDefinition( ) );
	}
	

	public IDataSource getDataSource() {
		return dataSource;
	}
	
	public QbeTemplate getTemplate() {
		return template;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}	
	
	
	public QueryCatalogue getQueryCatalogue() {
		return queryCatalogue;
	}

	public void setQueryCatalogue(QueryCatalogue queryCatalogue) {
		this.queryCatalogue = queryCatalogue;
	}
	
	private String getActiveQueryId() {
		return activeQueryId;
	}

	private void setActiveQueryId(String activeQueryId) {
		this.activeQueryId = activeQueryId;
	}
	
	public Query getActiveQuery() {
		return getQueryCatalogue().getQuery( getActiveQueryId() );
	}

	public void setActiveQuery(Query query) {
		setActiveQueryId(query.getId());
		this.statment = getDataSource().createStatement( query );
	}
	
	public void setActiveQuery(String queryId) {
		Query query;
		
		query = getQueryCatalogue().getQuery( queryId );
		if(query != null) {
			setActiveQueryId(query.getId());
			this.statment = getDataSource().createStatement( query );
		}
	}
	
	public void resetActiveQuery() {
		setActiveQueryId(null);
		setStatment(null);
	}
	
	public IStatement getStatment() {
		return statment;
	}

	public void setStatment(IStatement statment) {
		this.statment = statment;
	}

	public WorkSheetDefinition getWorkSheetDefinition() {
		return workSheetDefinition;
	}

	public void setWorkSheetDefinition(WorkSheetDefinition workSheetDefinition) {
		this.workSheetDefinition = workSheetDefinition;
	}


	

	
}
