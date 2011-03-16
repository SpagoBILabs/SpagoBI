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

package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.dbaccess.Utils;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.dbaccess.sql.result.ScrollableDataResult;
import it.eng.spago.dispatching.module.list.basic.AbstractBasicListModule;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.paginator.basic.ListIFace;
import it.eng.spago.paginator.basic.PaginatorIFace;
import it.eng.spago.paginator.basic.impl.GenericList;
import it.eng.spago.paginator.basic.impl.GenericPaginator;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.commons.utilities.DataSourceUtilities;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.DataSetFactory;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


/**   @author Giulio Gavardi
 *     giulio.gavardi@eng.it
 */

public class ListTestDataSetModule extends AbstractBasicListModule  {

	private static transient Logger logger = Logger.getLogger(ListTestDataSetModule.class);
	private EMFErrorHandler errorHandler;
	public static final String messageBundle = "messages";

	/**
	 * Instantiates a new list test data set module.
	 */
	public ListTestDataSetModule() {
		super();
		// TODO Auto-generated constructor stub
	}




	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.list.basic.IFaceBasicListService#getList(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		errorHandler=getResponseContainer().getErrorHandler();
		ListIFace listIFace=null;
		try{
			listIFace=getTestResultList(request, response);

		}
		catch (EMFUserError eex) {
			errorHandler.addError(eex);
			return null;
		} catch (Exception ex) {
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
			errorHandler.addError(internalError);
			return null;
		}
		return listIFace;

	}








	/**
	 * Gets the test result list.
	 * 
	 * @param request the request
	 * @param response the response
	 * 
	 * @return the test result list
	 * 
	 * @throws Exception the exception
	 */
	public ListIFace getTestResultList(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		// define the spago paginator and list object
		PaginatorIFace paginator = new GenericPaginator();
		ListIFace list = new GenericList();
		// recover lov object	
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();


		SpagoBiDataSet dataSetConfig =(SpagoBiDataSet)session.getAttribute(DetailDataSetModule.DATASET);
		IDataSet dataSet = DataSetFactory.getDataSet( dataSetConfig );


		String typeDataset=getDataSetType(dataSet);

		IEngUserProfile profile = null;
		profile = (IEngUserProfile)session.getAttribute(SpagoBIConstants.USER_PROFILE_FOR_TEST);
		if(profile==null) {
			SessionContainer permSess = session.getPermanentContainer();
			profile = (IEngUserProfile) permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		} else {
			session.delAttribute(SpagoBIConstants.USER_PROFILE_FOR_TEST);
		}
		dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));
		

		errorHandler=getErrorHandler();
		if(!errorHandler.isOK()){
			logger.error("Some Errors are found in error Handler");
			logger.error(errorHandler.getStackTrace());
			response.setAttribute(DetailDataSetModule.TEST_EXECUTED, "false");
			response.setAttribute(DetailDataSetModule.DATASET,dataSet);
			return null;
		}


		// based on lov type fill the spago list and paginator object
		SourceBean rowsSourceBean = null;
		List colNames = new ArrayList();


		Object par=(Object)session.getAttribute(DetailDataSetModule.PARAMETERS_FILLED);
		HashMap parametersFilled=(HashMap)par;


		dataSet.setParamsMap(parametersFilled);		
		try{
			dataSet.loadData();

			IDataStore ids = dataSet.getDataStore();

			String metadataToXML=new DatasetMetadataParser().metadataToXML(ids);
			if(metadataToXML!=null){
				session.setAttribute(DetailDataSetModule.DS_METADATA_XML, metadataToXML);
			}
			else{
				logger.warn("Metadata not retrieved");
			}

			rowsSourceBean=ids.toSourceBean();

			//I must get columnNames. assumo che tutte le righe abbiano le stesse colonne
			if(rowsSourceBean!=null){
				List row =rowsSourceBean.getAttributeAsList("ROW");
				if(row.size()>=1){
					Iterator iterator = row.iterator(); 
					SourceBean sb = (SourceBean) iterator.next();
					List sbas=sb.getContainedAttributes();
					for (Iterator iterator2 = sbas.iterator(); iterator2.hasNext();) {
						SourceBeanAttribute object = (SourceBeanAttribute) iterator2.next();
						String name=object.getKey();
						colNames.add(name);
					}

				}
			}		

		}
		catch (Exception e) {
			logger.error("Error while executing dataset for test purpose",e);
			errorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, 2006));
			if(e.getCause()!=null && e.getMessage()!=null){
				response.setAttribute("errorMessage", "Cause: "+e.getCause()+" Message: "+e.getMessage());
			}
			response.setAttribute(DetailDataSetModule.TEST_EXECUTED, "false");
			response.setAttribute(DetailDataSetModule.DATASET,dataSet);
			return null;		
		}



		// Build the list fill paginator
		if(rowsSourceBean != null) {
			List rows = rowsSourceBean.getAttributeAsList(DataRow.ROW_TAG);
			for (int i = 0; i < rows.size(); i++)
				paginator.addRow(rows.get(i));
		}
		list.setPaginator(paginator);

		// build module configuration for the list
		String moduleConfigStr = "";
		moduleConfigStr += "<CONFIG>";
		moduleConfigStr += "	<QUERIES/>";
		moduleConfigStr += "	<COLUMNS>";
		// if there's no colum name add a fake column to show that there's no data
		if(colNames.size()==0) {
			moduleConfigStr += "	<COLUMN name=\"No Result Found\" />";
		} else {
			Iterator iterColNames = colNames.iterator();
			while(iterColNames.hasNext()) {
				String colName = (String)iterColNames.next();
				moduleConfigStr += "	<COLUMN name=\"" + colName + "\" />";
			}
		}
		moduleConfigStr += "	</COLUMNS>";
		moduleConfigStr += "	<CAPTIONS/>";
		moduleConfigStr += "	<BUTTONS/>";
		moduleConfigStr += "</CONFIG>";
		SourceBean moduleConfig = SourceBean.fromXMLString(moduleConfigStr);

		response.setAttribute(DetailDataSetModule.TEST_EXECUTED, "true");


		response.setAttribute(moduleConfig);


		// filter the list 
		String valuefilter = (String) request.getAttribute(SpagoBIConstants.VALUE_FILTER);
		if (valuefilter != null) {
			String columnfilter = (String) request.getAttribute(SpagoBIConstants.COLUMN_FILTER);
			String typeFilter = (String) request.getAttribute(SpagoBIConstants.TYPE_FILTER);
			String typeValueFilter = (String) request.getAttribute(SpagoBIConstants.TYPE_VALUE_FILTER);
			list = DelegatedBasicListService.filterList(list, valuefilter, typeValueFilter, 
					columnfilter, typeFilter, getResponseContainer().getErrorHandler());
		}

		response.setAttribute(DetailDataSetModule.DATASET,dataSet);
		response.setAttribute("typedataset",typeDataset);

		logger.debug("OUT");
		return list;
	}




	private String getDataSetType(it.eng.spagobi.tools.dataset.bo.IDataSet a){
		String type="";
		if(a instanceof FileDataSet)type="0";
		else 		
			if(a instanceof JDBCDataSet)type="1";
			else 		
				if(a instanceof WebServiceDataSet)type="2";
				else 		
					if(a instanceof ScriptDataSet)type="3";
					else 		
						if(a instanceof JavaClassDataSet)type="4";
		return type;

	}




	/**
	 * Find the attributes of the first row of the xml passed at input: this xml is assumed to be:
	 * &lt;ROWS&gt;
	 * 	&lt;ROW attribute_1="value_of_attribute_1" ... /&gt;
	 * 	....
	 * &lt;ROWS&gt; 
	 * 
	 * @param rowsSourceBean The sourcebean to be parsed
	 * @return the list of the attributes of the first row
	 */
	private List findFirstRowAttributes(SourceBean rowsSourceBean) {
		List columnsNames = new ArrayList();
		if (rowsSourceBean != null) {
			List rows = rowsSourceBean.getAttributeAsList(DataRow.ROW_TAG);
			if (rows != null && rows.size() != 0) {
				SourceBean row = (SourceBean) rows.get(0);
				List rowAttrs = row.getContainedAttributes();
				Iterator rowAttrsIter = rowAttrs.iterator();
				while (rowAttrsIter.hasNext()) {
					SourceBeanAttribute rowAttr = (SourceBeanAttribute) rowAttrsIter.next();
					columnsNames.add(rowAttr.getKey());
				}
			}
		}
		return columnsNames;
	}

	/**
	 * Executes a select statement.
	 * 
	 * @param requestContainer The request container object
	 * @param responseContainer The response container object
	 * @param statement The statement definition string
	 * @param datasource the datasource
	 * @param columnsNames the columns names
	 * 
	 * @return A generic object containing the Execution results
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public static Object executeSelect(RequestContainer requestContainer,
			ResponseContainer responseContainer, String datasource, String statement, List columnsNames) throws EMFInternalError {
		//ResponseContainer responseContainer, String pool, String statement, List columnsNames) throws EMFInternalError {
		Object result = null;
		//DataConnectionManager dataConnectionManager = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			/*dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(pool);
			 */
			//gets connection
			DataSourceUtilities dsUtil = new DataSourceUtilities();
			Connection conn = dsUtil.getConnection(requestContainer,datasource); 
			dataConnection = dsUtil.getDataConnection(conn);

			sqlCommand = dataConnection.createSelectCommand(statement);
			dataResult = sqlCommand.execute();
			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult
			.getDataObject();
			List temp = Arrays.asList(scrollableDataResult.getColumnNames());
			columnsNames.addAll(temp);
			result = scrollableDataResult.getSourceBean();
		} finally {
			Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return result;
	}

	private boolean checkSintax(String result) {

		List visibleColumnNames = null;
		String valueColumnName = "";
		String descriptionColumnName = "";

		boolean toconvert = false;
		try{
			SourceBean source = SourceBean.fromXMLString(result);
			if(!source.getName().equalsIgnoreCase("ROWS")) {
				toconvert = true;
			} else {
				List rowsList = source.getAttributeAsList(DataRow.ROW_TAG);
				if( (rowsList==null) || (rowsList.size()==0) ) {
					toconvert = true;
				} else {
					// TODO this part can be moved to the import transformer
					// RESOLVES RETROCOMPATIBILITY PROBLEMS
					// finds the name of the first attribute of the rows if exists 
					String defaultName = "";
					SourceBean rowSB = (SourceBean) rowsList.get(0);
					List attributes = rowSB.getContainedAttributes();
					if (attributes != null && attributes.size() > 0) {
						SourceBeanAttribute attribute = (SourceBeanAttribute) attributes.get(0);
						defaultName = attribute.getKey();
					}
					// if a value column is specified, it is considered
					SourceBean valueColumnSB = (SourceBean) source.getAttribute("VALUE-COLUMN");
					if (valueColumnSB != null) {
						String valueColumn = valueColumnSB.getCharacters();
						if (valueColumn != null) {
							valueColumnName = valueColumn;
						}
					} else {
						valueColumnName = defaultName;
					}
					SourceBean visibleColumnsSB = (SourceBean) source.getAttribute("VISIBLE-COLUMNS");
					if (visibleColumnsSB != null) {
						String allcolumns = visibleColumnsSB.getCharacters();
						if (allcolumns != null) {
							String[] columns = allcolumns.split(",");
							visibleColumnNames = Arrays.asList(columns);
						}
					} else {
						String[] columns = new String[] {defaultName};
						visibleColumnNames = Arrays.asList(columns);
					}
					SourceBean descriptionColumnSB = (SourceBean) source.getAttribute("DESCRIPTION-COLUMN");
					if (descriptionColumnSB != null) {
						String descriptionColumn = descriptionColumnSB.getCharacters();
						if (descriptionColumn != null) {
							descriptionColumnName = descriptionColumn;
						}
					} else {
						descriptionColumnName = defaultName;
					}
				}
			}

		} catch (Exception e) {
			SpagoBITracer.warning(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), 
					"checkSintax", "the result of the lov is not formatted " +
			"with the right structure so it will be wrapped inside an xml envelope");
			toconvert = true;
		}
		return toconvert;
	}

	private String convertResult(String result) {
		List visibleColumnNames = null;
		String valueColumnName = "";
		String descriptionColumnName = "";
		StringBuffer sb = new StringBuffer();
		sb.append("<ROWS>");
		sb.append("<ROW VALUE=\"" + result +"\"/>");
		sb.append("</ROWS>");
		descriptionColumnName = "VALUE";
		valueColumnName = "VALUE";
		String [] visibleColumnNamesArray = new String [] {"VALUE"};
		visibleColumnNames = Arrays.asList(visibleColumnNamesArray);
		return sb.toString();
	}






}
