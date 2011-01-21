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

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.dbaccess.Utils;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.dbaccess.sql.result.ScrollableDataResult;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.DataSourceUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.services.datasource.service.DataSourceSupplier;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.validator.GenericValidator;
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
	private String databaseDialect = null;
	
	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random random = new Random();

	private static String ALIAS_DELIMITER = null;
	private static String VALUE_ALIAS = "VALUE";
	private static String DESCRIPTION_ALIAS = "DESCRIPTION";
	
	public static final String DIALECT_MYSQL = "org.hibernate.dialect.MySQLInnoDBDialect";
	public static final String DIALECT_POSTGRES = "org.hibernate.dialect.PostgreSQLDialect";
	public static final String DIALECT_ORACLE = "org.hibernate.dialect.OracleDialect";
	public static final String DIALECT_HSQL = "org.hibernate.dialect.HSQLDialect";
	public static final String DIALECT_ORACLE9i10g = "org.hibernate.dialect.Oracle9Dialect";
	public static final String DIALECT_SQLSERVER = "org.hibernate.dialect.SQLServerDialect";
	public static final String DIALECT_INGRES = "org.hibernate.dialect.IngresDialect";
	
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
	 * @see it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail#getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, ExecutionInstance executionInstance) throws Exception;
	 */
	public String getLovResult(IEngUserProfile profile, List<ObjParuse> dependencies, ExecutionInstance executionInstance) throws Exception {
		logger.debug("IN");
		String statement = getWrappedStatement(dependencies, executionInstance);
		statement = StringUtilities.substituteProfileAttributesInString(statement, profile);
		logger.info("User [" + ((UserProfile) profile).getUserId() + "] is executing sql: " + statement);
		String result = getLovResult(profile,statement);
		logger.debug("OUT.result="+result);
		return result;
	}
	
	/**
	 * This methods builds the in-line view that filters the original lov using the dependencies.
	 * For example, suppose the lov definition is 
	 * SELECT country, state_province, city FROM REGION
	 * and there is a dependency that set country to be "USA", this method returns 
	 * SELECT * FROM (SELECT country, state_province, city FROM REGION) T WHERE ( country = 'USA' )
	 * @param dependencies The dependencies' configuration to be considered into the query
	 * @param executionInstance The execution instance (useful to retrieve dependencies values)
	 * @return the in-line view that filters the original lov using the dependencies.
	 */
	public String getWrappedStatement(List<ObjParuse> dependencies, ExecutionInstance executionInstance) {
		logger.debug("IN");
		String result = getQueryDefinition();
		if (dependencies != null && dependencies.size() > 0 && executionInstance != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT * FROM (" + getQueryDefinition() + ") " + getRandomAlias(8) + " ");
			buildWhereClause(buffer, dependencies, executionInstance);
			result = buffer.toString();
		}
		logger.debug("OUT.result=" + result);
		return result;
	}
	
	private String getRandomAlias(int len) {
		  StringBuilder sb = new StringBuilder( len );
		  for( int i = 0; i < len; i++ ) sb.append( AB.charAt( random.nextInt(AB.length()) ) );
		  return sb.toString();
	}

	/**
	 * This method builds the WHERE clause for the wrapped statement (the statement that adds filters for correlations/dependencies)
	 * See getWrappedStatement method. 
	 * 
	 * @param buffer The String buffer that contains query definition
	 * @param dependencies The dependencies configuration
	 * @param executionInstance The execution instance
	 */
	private void buildWhereClause(StringBuffer buffer,
			List<ObjParuse> dependencies, ExecutionInstance executionInstance) {
		buffer.append(" WHERE ");
		if (dependencies.size() == 1) {
			ObjParuse dependency = (ObjParuse) dependencies.get(0);
			addFilter(buffer, dependency, executionInstance);
		} else if (dependencies.size() == 2) {
			ObjParuse leftPart = (ObjParuse) dependencies.get(0);
			ObjParuse rightPart = (ObjParuse) dependencies.get(1);
			String lo = leftPart.getLogicOperator();
			addFilter(buffer, leftPart, executionInstance);
			buffer.append(" " + lo + " ");
			addFilter(buffer, rightPart, executionInstance);
		} else {
			// build the expression
			Iterator iterOps = dependencies.iterator();
			while(iterOps.hasNext())  {
				ObjParuse op = (ObjParuse) iterOps.next();
				buffer.append(" " + op.getPreCondition() + " ");
				addFilter(buffer, op, executionInstance);
				buffer.append(" " + op.getPostCondition() + " " + op.getLogicOperator());
			}
		}
	}

	/**
	 * This methods adds a single filter based on the input dependency's configuration.
	 * See buildWhereClause and getWrappedStatement methods.
	 * 
	 * @param buffer The String buffer that contains query definition
	 * @param dependency The dependency's configuration
	 * @param executionInstance The execution instance
	 */
	private void addFilter(StringBuffer buffer, ObjParuse dependency, ExecutionInstance executionInstance) {
		String operator = findOperator(dependency, executionInstance);
		String value = findValue(dependency, executionInstance);
		if (value != null) {
			buffer.append(" ( ");
			buffer.append( getColumnSQLName(dependency.getFilterColumn()) );
			buffer.append( " " + operator + " ");
			buffer.append( " " + value + " ");
			buffer.append(" ) ");
		} else {
			buffer.append(" ( 1 = 1 ) "); // in case a filter has no value, add a TRUE condition
		}
	}
	
	private String getColumnSQLName(String columnName) {
		if (columnName.contains(" ")) {
			return ALIAS_DELIMITER + columnName + ALIAS_DELIMITER;
		} else 
			return columnName;
	}

	/**
	 * Finds the value to be used into the dependency's filter.
	 * 
	 * @param dependency The dependency's configuration
	 * @param executionInstance The execution instance
	 * @return the value to be used in the wrapped statement
	 */
	private String findValue(ObjParuse dependency,
			ExecutionInstance executionInstance) {
		String typeFilter = dependency.getFilterOperation();
		BIObjectParameter fatherPar = getFatherParameter(dependency, executionInstance);
		List values = fatherPar.getParameterValues();
		if (values == null || values.isEmpty() 
				|| (values.size() == 1 && values.get(0).equals(""))) {
			return null;
		}
		String firstValue = (String) values.get(0);
		if (typeFilter.equalsIgnoreCase(SpagoBIConstants.START_FILTER)) {
			return getSQLValue(fatherPar, firstValue) + "%";
		} else if (typeFilter.equalsIgnoreCase(SpagoBIConstants.END_FILTER)) {
			return "%" + getSQLValue(fatherPar, firstValue);
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.CONTAIN_FILTER)) {
			return "%" + getSQLValue(fatherPar, firstValue) + "%";
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.EQUAL_FILTER)) {
			if (values.size() > 1) {
				return "(" + concatenateValues(fatherPar, values) + ")";
			} else {
				return getSQLValue(fatherPar, firstValue);
			}
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)) {
			return getSQLValue(fatherPar, firstValue); 
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)) {
			return getSQLValue(fatherPar, firstValue);
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)) {
			return getSQLValue(fatherPar, firstValue);
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {
			return getSQLValue(fatherPar, firstValue);
		} else {
			logger.error("Filter operator not supported: [" + typeFilter + "]");
			throw new SpagoBIRuntimeException("Filter operator not supported: [" + typeFilter + "]");
		}
	}

	/**
	 * Concatenates values by ','
	 * @param biparam The BIObjectParameter in the dependency
	 * @param values The values to be concatenated
	 * @return the values concatenated by ','
	 */
	private String concatenateValues(BIObjectParameter biparam, List values) {
		StringBuffer buffer = new StringBuffer();
		Iterator it = values.iterator();
		while (it.hasNext()) {
			String aValue = (String) it.next();
			buffer.append(getSQLValue(biparam, aValue));
			if (it.hasNext()) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	/**
	 * Finds the suitable SQL value for the input value.
	 * A number is not changed.
	 * A String is surrounded by single-quotes.
	 * A date is put inside a database-dependent function. The date must respect the format returned by GeneralUtilities.getServerDateFormat()
	 * Input values are validated.
	 * 
	 * @param biparam The BIObjectParameter in the dependency
	 * @param value The value of the parameter
	 * @return the SQL value suitable for the input value
	 */
	private String getSQLValue(BIObjectParameter biparam, String value) {
		String parameterType = biparam.getParameter().getType();
		if (parameterType.equals(SpagoBIConstants.NUMBER_TYPE_FILTER)) {
			validateNumber(value);
			return value;
		} else if (parameterType.equals(SpagoBIConstants.STRING_TYPE_FILTER)) {
			return "'" + escapeQuotes(value) + "'";
		} else if (parameterType.equals(SpagoBIConstants.DATE_TYPE_FILTER)) {
			validateDate(value);
			String dialect = getDataSourceDialect();
			String toReturn = composeStringToDt(dialect, value);
			return toReturn;
		} else {
			logger.error("Parameter type not supported: [" + parameterType + "]");
			throw new SpagoBIRuntimeException("Parameter type not supported: [" + parameterType + "]");
		}
	}

	private void validateNumber(String value) {
		if (!(GenericValidator.isInt(value) || GenericValidator.isFloat(value)
				|| GenericValidator.isDouble(value)
				|| GenericValidator.isShort(value) || GenericValidator
				.isLong(value))) {
			throw new SecurityException("Input value " + value + " is not a valid number");
		}
	}
	
	private void validateDate(String value) {
		String dateFormat = GeneralUtilities.getServerDateFormat();
		String timestampFormat = GeneralUtilities.getServerTimeStampFormat();
		if (!GenericValidator.isDate(value, dateFormat, true)
				&& !GenericValidator.isDate(value, timestampFormat, true)) {
			throw new SecurityException("Input value " + value
					+ " is not a valid date according to the date format "
					+ dateFormat + " or timestamp format " + timestampFormat);
		}
	}

	private String getDataSourceDialect() {
		return databaseDialect;
	}
	
	private void setDataSourceDialect() {
		DataSourceSupplier supplierDS = new DataSourceSupplier();		
		SpagoBiDataSource ds = supplierDS.getDataSourceByLabel(dataSource);
		databaseDialect = ds.getHibDialectClass();
		if (databaseDialect.equalsIgnoreCase(DIALECT_MYSQL)) {
			ALIAS_DELIMITER = "`";
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_HSQL)) {
			ALIAS_DELIMITER = "\"";
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_INGRES)) {
			ALIAS_DELIMITER = "\""; // TODO check it!!!!
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_ORACLE)) {
			ALIAS_DELIMITER = "\"";
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_ORACLE9i10g)) {
			ALIAS_DELIMITER = "\"";
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_POSTGRES)) {
			ALIAS_DELIMITER = "\"";
		} else if (databaseDialect.equalsIgnoreCase(DIALECT_SQLSERVER)) {
			ALIAS_DELIMITER = ""; // TODO check it!!!!
		} else {
			ALIAS_DELIMITER = "\""; // TODO mmmmmmmmm.........
		}
	}
	
	private String escapeQuotes(String value) {
		if (value == null) return null;
		return value.replace("'", "''");
	}
	
	private String composeStringToDt(String dialect, String date){
		String toReturn = "";
		date = escapeQuotes(date); // for security reasons
		if(dialect!=null){
			if( dialect.equalsIgnoreCase(DIALECT_MYSQL)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = " STR_TO_DATE("+date+",'%d/%m/%Y %h:%i:%s') ";
				}else{
					toReturn = " STR_TO_DATE('"+date+"','%d/%m/%Y %h:%i:%s') ";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_HSQL)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = date;
				}else{
					toReturn = "'"+date+"'";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_INGRES)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = " STR_TO_DATE("+date+",'%d/%m/%Y') ";
				}else{
					toReturn = " STR_TO_DATE('"+date+"','%d/%m/%Y') ";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_ORACLE)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+date+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+date+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_ORACLE9i10g)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+date+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+date+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_POSTGRES)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+date+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+date+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(DIALECT_SQLSERVER)){
				if (date.startsWith("'") && date.endsWith("'")) {
					toReturn = date;
				}else{
					toReturn = "'"+date+"'";
				}
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Finds the suitable operator for the input dependency.
	 * 
	 * @param dependency The dependency's configuration
	 * @param executionInstance The Execution instance
	 * @return the suitable operator for the input dependency
	 */
	private String findOperator(ObjParuse dependency, ExecutionInstance executionInstance) {
		String typeFilter = dependency.getFilterOperation();
		if (typeFilter.equalsIgnoreCase(SpagoBIConstants.START_FILTER)) {
			return "LIKE";
		} else if (typeFilter.equalsIgnoreCase(SpagoBIConstants.END_FILTER)) {
			return "LIKE";
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.CONTAIN_FILTER)) {
			return "LIKE";
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.EQUAL_FILTER)) {
			BIObjectParameter fatherPar = getFatherParameter(dependency, executionInstance);
			List values = fatherPar.getParameterValues();
			if (values != null && values.size() > 1) {
				return "IN";
			} else {
				return "=";
			}
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.LESS_FILTER)) {
			return "<"; 
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.LESS_OR_EQUAL_FILTER)) {
			return "<=";
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.GREATER_FILTER)) {
			return ">";
		} else if (typeFilter
				.equalsIgnoreCase(SpagoBIConstants.GREATER_OR_EQUAL_FILTER)) {
			return ">=";
		} else {
			logger.error("Filter operator not supported: [" + typeFilter + "]");
			throw new SpagoBIRuntimeException("Filter operator not supported: [" + typeFilter + "]");
		}
	}

	private BIObjectParameter getFatherParameter(ObjParuse dependency,
			ExecutionInstance executionInstance) {
		List parameters = executionInstance.getBIObject().getBiObjectParameters();
		Integer fatherId = dependency.getObjParFatherId();
		Iterator it = parameters.iterator();
		while (it.hasNext()) {
			BIObjectParameter temp = (BIObjectParameter) it.next();
			if (temp.getId().equals(fatherId)) {
				return temp;
			}
		}
		return null;
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
			sqlCommand = dataConnection.createSelectCommand(statement, true);
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
	 * This methods find out if the input parameters' values are admissible for this QueryDetail instance, i.e. if the values are
	 * contained in the query result.
	 * 
	 * @param profile The user profile
	 * @param biparam The BIObjectParameter with the values that must be validated
	 * @return a list of errors: it is empty if all values are admissible, otherwise it will contain a EMFUserError for each wrong value
	 * @throws Exception
	 */
	public List validateValues(IEngUserProfile profile, BIObjectParameter biparam) throws Exception {
		List toReturn = new ArrayList();
		List<String> values = biparam.getParameterValues();
		List parameterValuesDescription = new ArrayList();
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		String statement = null;
		SourceBean result = null;
		try {
			statement = getValidationQuery(profile, biparam, values);
			//gets connection
			DataSourceUtilities dsUtil = new DataSourceUtilities();
			Connection conn = dsUtil.getConnection(profile,dataSource); 
			dataConnection = dsUtil.getDataConnection(conn);
			sqlCommand = dataConnection.createSelectCommand(statement, true);
			dataResult = sqlCommand.execute();
	        ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			result = scrollableDataResult.getSourceBean();
		} finally {
			Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		
		// START converting the SourceBean into a string and then into SourceBean again:
		// this a necessary work-around (workaround, work around) because the getFilteredSourceBeanAttribute is not able to filter on numbers!!!
		// By making this conversion, the information on data type is lost and every attribute becomes a String
		String xml = result.toXML(false);
		result = SourceBean.fromXMLString(xml);
		// END converting the SourceBean into a string and then into SourceBean again:
		
		Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			String description = null;
			String aValue = it.next();
			Object obj = result.getFilteredSourceBeanAttribute(DataRow.ROW_TAG, VALUE_ALIAS, aValue);
			if (obj == null) {
				// value was not found!!
				logger.error("Parameter '" + biparam.getLabel() + "' cannot assume value '" + aValue + "'" +
						" for user '" + ((UserProfile) profile).getUserId().toString()
						+ "'.");
				List l = new ArrayList();
				l.add(biparam.getLabel());
				l.add(aValue);
				EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 1077, l);
				toReturn.add(userError);
				description = "NOT ADMISSIBLE";
			} else {
				// value was found, retrieve description
				if (obj instanceof SourceBean) {
					SourceBean sb = (SourceBean) obj;
					Object descriptionObj = sb.getAttribute(DESCRIPTION_ALIAS);
					description = descriptionObj != null ? descriptionObj.toString() : null;
				} else {
					List l = (List) obj;
					Object descriptionObj = ((SourceBean) l.get(0)).getAttribute(DESCRIPTION_ALIAS);
					description = descriptionObj != null ? descriptionObj.toString() : null;
				}
			}
			parameterValuesDescription.add(description);
		}
		biparam.setParameterValuesDescription(parameterValuesDescription);
		return toReturn;
	}
	
	/**
	 * This methods builds the validation query, see validateValues method.
	 */
	private String getValidationQuery(IEngUserProfile profile, BIObjectParameter biparam, List<String> values) throws Exception {
		String statement = getQueryDefinition();
		statement = StringUtilities.substituteProfileAttributesInString(statement, profile);
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		buffer.append(getColumnSQLName(this.valueColumnName) + " AS \"" + VALUE_ALIAS + "\", ");
		buffer.append(getColumnSQLName(this.descriptionColumnName) + " AS \"" + DESCRIPTION_ALIAS + "\" ");
		buffer.append("FROM (");
		buffer.append(statement);
		buffer.append(") " +  getRandomAlias(8) + " WHERE ");
		if (values.size() == 1) {
			buffer.append(getColumnSQLName(this.valueColumnName) + " = ");
			buffer.append(getSQLValue(biparam, values.get(0)));
		} else {
			buffer.append(getColumnSQLName(this.valueColumnName) + " IN (");
			buffer.append(concatenateValues(biparam, values));
			buffer.append(")");
		}
		return buffer.toString();
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
		setDataSourceDialect();
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

	public QueryDetail clone() {
		QueryDetail toReturn = new QueryDetail();
		toReturn.setDataSource(this.getDataSource());
		toReturn.setDescriptionColumnName(this.getDescriptionColumnName());
		List invisibleColumnNames = new ArrayList();
		invisibleColumnNames.addAll(this.getInvisibleColumnNames());
		toReturn.setInvisibleColumnNames(invisibleColumnNames);
		toReturn.setQueryDefinition(this.getQueryDefinition());
		toReturn.setValueColumnName(this.getValueColumnName());
		List visibleColumnNames = new ArrayList();
		visibleColumnNames.addAll(this.getVisibleColumnNames());
		toReturn.setVisibleColumnNames(visibleColumnNames);
		return toReturn;
	}

	
	
}
