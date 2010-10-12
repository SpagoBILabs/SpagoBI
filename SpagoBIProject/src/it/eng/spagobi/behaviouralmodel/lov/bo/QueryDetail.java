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
package it.eng.spagobi.behaviouralmodel.lov.bo;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.dbaccess.Utils;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.dbaccess.sql.result.ScrollableDataResult;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.DataSourceUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Defines the <code>QueryDetail</code> objects. This object is used to store 
 * Query Wizard detail information.
 */
public class QueryDetail  implements ILovDetail  {
	private static transient Logger logger = Logger.getLogger(QueryDetail.class);
	
	private String dataSource= "" ;
	private String queryDefinition = "";
	
	private List visibleColumnNames = null;
	private String valueColumnName = "";
	private String descriptionColumnName = "";
	private List invisibleColumnNames = null;
	
	/**
	 * constructor.
	 */
	public QueryDetail() { }
	
	/**
	 * constructor.
	 * 
	 * @param dataDefinition the xml representation of the lov
	 * 
	 * @throws SourceBeanException the source bean exception
	 */
	public QueryDetail(String dataDefinition) throws SourceBeanException {
		loadFromXML(dataDefinition);
	}
	
	/**
	 * loads the lov from an xml string.
	 * 
	 * @param dataDefinition the xml definition of the lov
	 * 
	 * @throws SourceBeanException the source bean exception
	 */
	public void loadFromXML (String dataDefinition) throws SourceBeanException {
		logger.debug("IN");
		dataDefinition.trim();
		if(dataDefinition.indexOf("<STMT>")!=-1) {
			int startInd = dataDefinition.indexOf("<STMT>");
			int endId = dataDefinition.indexOf("</STMT>");
			String query = dataDefinition.substring(startInd + 6, endId);
			query =query.trim();
			if(!query.startsWith("<![CDATA[")) {
				query = "<![CDATA[" + query  +  "]]>";
				dataDefinition = dataDefinition.substring(0, startInd+6) + query + dataDefinition.substring(endId); 
			}
		}
		
		SourceBean source = SourceBean.fromXMLString(dataDefinition);
		SourceBean connection = (SourceBean)source.getAttribute("CONNECTION"); 
		String dataSource =  connection.getCharacters(); 
		SourceBean statement = (SourceBean)source.getAttribute("STMT");
		String queryDefinition = statement.getCharacters();
		SourceBean valCol = (SourceBean)source.getAttribute("VALUE-COLUMN");
		String valueColumn = valCol.getCharacters();
		SourceBean visCol = (SourceBean)source.getAttribute("VISIBLE-COLUMNS");
		String visibleColumns = visCol.getCharacters();
		SourceBean invisCol = (SourceBean)source.getAttribute("INVISIBLE-COLUMNS");
		String invisibleColumns = "";
		// compatibility control (versions till 1.9RC does not have invisible columns definition)
		if (invisCol != null) {
			invisibleColumns = invisCol.getCharacters();
			if(invisibleColumns==null) {
				invisibleColumns = "";
			}
		}
		SourceBean descCol = (SourceBean)source.getAttribute("DESCRIPTION-COLUMN");
		String descriptionColumn = null;
		// compatibility control (versions till 1.9.1 does not have description columns definition)
		if (descCol != null) { 
			descriptionColumn = descCol.getCharacters();
			if(descriptionColumn==null) {
				descriptionColumn = valueColumn;
			}
		}
		else descriptionColumn = valueColumn;
		setDataSource(dataSource);
		setQueryDefinition(queryDefinition);
		setValueColumnName(valueColumn);
		setDescriptionColumnName(descriptionColumn);
		List visColNames = new ArrayList();
		if( (visibleColumns!=null) && !visibleColumns.trim().equalsIgnoreCase("") ) {
			String[] visColArr = visibleColumns.split(",");
			visColNames = Arrays.asList(visColArr);
		}
		setVisibleColumnNames(visColNames);
		List invisColNames = new ArrayList();
		if( (invisibleColumns!=null) && !invisibleColumns.trim().equalsIgnoreCase("") ) {
			String[] invisColArr = invisibleColumns.split(",");
			invisColNames = Arrays.asList(invisColArr);
		}
		setInvisibleColumnNames(invisColNames);
		logger.debug("OUT");
	}
	
	/**
	 * serialize the lov to an xml string.
	 * 
	 * @return the serialized xml string
	 */
	public String toXML () { 
		String XML = "<QUERY>" +
				     "<CONNECTION>"+this.getDataSource()+"</CONNECTION>" +
			         "<STMT>"+this.getQueryDefinition() + "</STMT>" +
				     "<VALUE-COLUMN>"+this.getValueColumnName()+"</VALUE-COLUMN>" +
				     "<DESCRIPTION-COLUMN>"+this.getDescriptionColumnName()+"</DESCRIPTION-COLUMN>" +
				     "<VISIBLE-COLUMNS>"+GeneralUtilities.fromListToString(this.getVisibleColumnNames(), ",")+"</VISIBLE-COLUMNS>" +
				     "<INVISIBLE-COLUMNS>"+GeneralUtilities.fromListToString(this.getInvisibleColumnNames(), ",")+"</INVISIBLE-COLUMNS>" +
				     "</QUERY>";
		return XML;
	}
	
	
	/**
	 * Returns the result of the lov using a user profile to fill the lov profile attribute.
	 * 
	 * @param profile the profile of the user
	 * 
	 * @return the string result of the lov
	 * 
	 * @throws Exception the exception
	 */
	public String getLovResult(IEngUserProfile profile) throws Exception {
		logger.debug("IN");
		String statement = getQueryDefinition();
		statement = StringUtilities.substituteProfileAttributesInString(statement, profile);
		logger.info("User [" + ((UserProfile) profile).getUserId() + "] is executing sql: " + statement);
		String result = getLovResult(profile,statement);
		logger.debug("OUT.result="+result);
		return result;
	}
	
	/**
	 * Gets the values and return them as an xml structure
	 * @param statement the query statement to execute
	 * @return the xml string containing values
	 * @throws Exception	
	 */
	
	private String getLovResult(IEngUserProfile profile,String statement) throws Exception {
		String resStr = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			//gets connection
			DataSourceUtilities dsUtil = new DataSourceUtilities();
			Connection conn = dsUtil.getConnection(profile,dataSource); 
			dataConnection = dsUtil.getDataConnection(conn);
			sqlCommand = dataConnection.createSelectCommand(statement);
			dataResult = sqlCommand.execute();
	        ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean result = scrollableDataResult.getSourceBean();
			resStr = result.toXML(false);
			resStr = resStr.trim();
			if(resStr.startsWith("<?")) {
				resStr = resStr.substring(2);
				int indFirstTag = resStr.indexOf("<");
				resStr = resStr.substring(indFirstTag);
			}
		} finally {
			Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return resStr;
	}
   
	
	/**
	 * Gets the list of names of the profile attributes required.
	 * 
	 * @return list of profile attribute names
	 * 
	 * @throws Exception the exception
	 */
	public List getProfileAttributeNames() throws Exception {
		List names = new ArrayList();
		String query = getQueryDefinition();
		while(query.indexOf("${")!=-1) {
			int startind = query.indexOf("${");
			int endind = query.indexOf("}", startind);
			String attributeDef = query.substring(startind + 2, endind);
			if(attributeDef.indexOf("(")!=-1) {
				int indroundBrack = query.indexOf("(", startind);
				String nameAttr = query.substring(startind+2, indroundBrack);
				names.add(nameAttr);
			} else {
				names.add(attributeDef);
			}
			query = query.substring(endind);
		}
		return names;
	}

	/**
	 * Checks if the lov requires one or more profile attributes.
	 * 
	 * @return true if the lov require one or more profile attributes, false otherwise
	 * 
	 * @throws Exception the exception
	 */
	public boolean requireProfileAttributes() throws Exception {
		boolean contains = false;
		String query = getQueryDefinition();
		if(query.indexOf("${")!=-1) {
			contains = true;
		}
		return contains;
	}
	
	/**
	 * Builds a simple sourcebean 
	 * @param name name of the sourcebean
	 * @param value value of the sourcebean
	 * @return the sourcebean built
	 * @throws SourceBeanException
	 */
	private SourceBean buildSourceBean(String name, String value) throws SourceBeanException {
		SourceBean sb = null;
		sb = SourceBean.fromXMLString("<"+name+">" + (value != null ? value : "") + "</"+name+">");
		return sb;
	}
	
	/**
	 * Splits an XML string by using some <code>SourceBean</code> object methods
	 * in order to obtain the source <code>QueryDetail</code> objects whom XML has been
	 * built.
	 * 
	 * @param dataDefinition The XML input String
	 * 
	 * @return The corrispondent <code>QueryDetail</code> object
	 * 
	 * @throws SourceBeanException If a SourceBean Exception occurred
	 */
	public static QueryDetail fromXML (String dataDefinition) throws SourceBeanException {
		return new QueryDetail(dataDefinition);
	}
	
	/**
	 * Gets the data source.
	 * 
	 * @return the data source
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the data source.
	 * 
	 * @param dataSource the new data source
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Gets the query definition.
	 * 
	 * @return the query definition
	 */
	public String getQueryDefinition() {
		return queryDefinition;
	}

	/**
	 * Sets the query definition.
	 * 
	 * @param queryDefinition the new query definition
	 */
	public void setQueryDefinition(String queryDefinition) {
		this.queryDefinition = queryDefinition;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getDescriptionColumnName()
	 */
	public String getDescriptionColumnName() {
		return descriptionColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setDescriptionColumnName(java.lang.String)
	 */
	public void setDescriptionColumnName(String descriptionColumnName) {
		this.descriptionColumnName = descriptionColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getInvisibleColumnNames()
	 */
	public List getInvisibleColumnNames() {
		return invisibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setInvisibleColumnNames(java.util.List)
	 */
	public void setInvisibleColumnNames(List invisibleColumnNames) {
		this.invisibleColumnNames = invisibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getValueColumnName()
	 */
	public String getValueColumnName() {
		return valueColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setValueColumnName(java.lang.String)
	 */
	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getVisibleColumnNames()
	 */
	public List getVisibleColumnNames() {
		return visibleColumnNames;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#setVisibleColumnNames(java.util.List)
	 */
	public void setVisibleColumnNames(List visibleColumnNames) {
		this.visibleColumnNames = visibleColumnNames;
	}

}
