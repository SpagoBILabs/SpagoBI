/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.tags;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.expr.ExprUtils;

/**
 * creates a MondrianModel from config.xml
 * @author av
 */
public class MondrianModelFactory {
	private static Logger logger = Logger.getLogger(MondrianModelFactory.class);

	private static final BASE64Encoder ENCODER = new BASE64Encoder();
	
	private MondrianModelFactory() {
	}

	static String makeConnectString(String filers,Config cfg, IEngUserProfile profile) {

		// for an external datasource, we do not need JdbcUrl *and* data source

		// if ((cfg.getJdbcUrl() == null) == (cfg.getDataSource() == null))
		// throw new IllegalArgumentException("exactly one of jdbcUrl or dataSource must be specified");

		// provider=Mondrian;Jdbc=jdbc:odbc:MondrianFoodMart;Catalog=file:///c:/dev/mondrian/demo/FoodMart.xml
		StringBuffer sb = new StringBuffer("provider=Mondrian");
		if (cfg.getJdbcUrl() != null) {
			String jdbcUrl = cfg.getJdbcUrl();
			sb.append(";Jdbc=");
			// if the url contains a semicolon, it must be in quotes
			if (jdbcUrl.indexOf(';') >= 0) {
				char c = jdbcUrl.charAt(0);
				if (c != '"' && c != '\'') {
					char escape = '"';
					if (jdbcUrl.indexOf('"') >= 0) {
						if (jdbcUrl.indexOf('\'') >= 0) {
							// this is not valid
							throw new IllegalArgumentException(
							"jdbcUrl is not valid - contains single and double quotes");
						}
						escape = '\'';
					}
					sb.append(escape);
					sb.append(jdbcUrl);
					sb.append(escape);
				} else
					sb.append(jdbcUrl); // already quoted
			} else
				sb.append(jdbcUrl); // no quotes neccessary

			if (cfg.getJdbcUser() != null)
				sb.append(";JdbcUser=").append(cfg.getJdbcUser());
			if (cfg.getJdbcPassword() != null && cfg.getJdbcPassword().length() > 0)
				sb.append(";JdbcPassword=").append(cfg.getJdbcPassword());
		} else if (cfg.getDataSource() != null) {
			//sb.append(";DataSource=java:comp/env/").append(cfg.getDataSource());
			sb.append(";DataSource=").append(cfg.getDataSource());
			testDataSource(cfg.getDataSource());
		}
		sb.append(";Catalog=").append(cfg.getSchemaUrl());
		/* risultato: 'provider=Mondrian;DataSource=java:comp/env/jdbc/foodmart;
		 * 				Catalog="file:D:/Progetti/DEMO_SPAGOBI_20/Resources/Olap/FoodMart_RoleDSP.xml"Drink;
		 * 				DynamicSchemaProcessor=it.eng.spagobi.jpivotaddins.roles.SpagoBIFilterDynamicSchemaProcessor;UseSchemaPool=true;
		 * 				family=Drink'
		 * */
		/*
    try {
    	sb.append(";Catalog=").append(cfg.getSchemaUrl()+profile.getUserAttribute("family"));
    } catch (EMFInternalError e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 */
		if (cfg.getDynLocale() != null)
			sb.append(";Locale=").append(cfg.getDynLocale());

		// debug
		if (cfg.getRole() != null) {
			sb.append(";Role=").append(cfg.getRole());
		}
		if (cfg.getDataSourceChangeListener() != null) {
			sb.append(";dataSourceChangeListener=").append(cfg.getDataSourceChangeListener());
		}
		sb.append(";DynamicSchemaProcessor=it.eng.spagobi.jpivotaddins.roles.SpagoBIFilterDynamicSchemaProcessor");
		// cache control: if filtering by user profile attributes with queries inside schema definition, UseSchemaPool must be false
		//orig: sb.append(";UseSchemaPool=true");
		sb.append(";UseSchemaPool=true;UseContentChecksum=true"); 

		// there could be more than one attribute
		if (filers!=null){
			logger.debug("Data Access is ACTIVE!!!");
			try {
				InputStream is = new java.io.ByteArrayInputStream(filers.getBytes());
				org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
				org.dom4j.Document document = reader.read(is);	

				//org.dom4j.Node attribute = document.selectSingleNode("//DATA-ACCESS/ATTRIBUTE");
				List<Node> attributes = document.selectNodes("//DATA-ACCESS/ATTRIBUTE");
				if (attributes != null) {
					for (Iterator<Node> iterator = attributes.iterator(); iterator.hasNext();) {
						Node attribute = iterator.next();
						String attributeName = attribute.valueOf("@name");
						if (attributeName != null) {
							// if no value is found put null 
							String value = profile.getUserAttribute(attributeName) != null ? profile.getUserAttribute(attributeName).toString() : null;
							logger.debug("add profile attribute "+attributeName+ " with value "+value);
							// put attribute not with filter name but with attribute name!
							
							// encoding value in Base64 
							String valueBase64 = null;
							if (value != null) {
								try {
									valueBase64 = ENCODER.encode(value.getBytes("UTF-8"));
								} catch (UnsupportedEncodingException e) {
									logger.error("UTF-8 encoding not supported!!!!!", e);
									valueBase64 = ENCODER.encode(value.getBytes());
								}
							}
							logger.debug("Attribute value in Base64 encoding is " + valueBase64);
							sb.append(";"+attributeName+"=" + valueBase64);		    		
							//sb.append(";filter=" + profile.getUserAttribute(attributeName));

						} else {
							logger.warn("Filter attribute name not found!!");
						}
					}
				} else {
					logger.warn("//DATA-ACCESS/ATTRIBUTE not found!!");
				}
			} catch (EMFInternalError e) {
				logger.error("",e);
			} catch (DocumentException e) {
				logger.error("",e);
			}
		}
		// sb.append(";Role=California manager");
		return sb.toString();
	}

	private static void testDataSource(String dataSourceName) {
		final DataSource dataSource;
		Connection connection = null;
		//String dsName = "java:comp/env/" + dataSourceName;
		String dsName = dataSourceName;
		try {
			dataSource = (DataSource) new InitialContext().lookup(dsName);
			connection = dataSource.getConnection();
		} catch (Throwable e) {
			String msg = "Datasource " + dsName + " is not configured properly";
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("could not close SQL Connection for DataSource " + dataSourceName, e);
				}
		}
	}

	public static MondrianModel instance() throws SAXException, IOException {
		URL url = MondrianModel.class.getResource("config.xml");
		return (MondrianModel) ModelFactory.instance(url);
	}

	public static MondrianModel instance(Config cfg) throws SAXException, IOException {
		URL url = MondrianModel.class.getResource("config.xml");
		return instance(null,url, cfg, null);
	}

	public static MondrianModel instance(String filers,URL url, Config cfg, IEngUserProfile profile) throws SAXException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info(cfg.toString());
			logger.info("ConnectString=" + makeConnectString(filers,cfg, profile));
		}
		MondrianModel mm = (MondrianModel) ModelFactory.instance(url);
		mm.setMdxQuery(cfg.getMdxQuery());
		mm.setConnectString(makeConnectString(filers,cfg, profile));
		mm.setJdbcDriver(cfg.getJdbcDriver());
		mm.setDynresolver(cfg.getDynResolver());
		mm.setDynLocale(cfg.getDynLocale());    
		mm.setDataSourceChangeListener( cfg.getDataSourceChangeListener());

		if ("false".equalsIgnoreCase(cfg.getConnectionPooling()))
			mm.setConnectionPooling(false);
		mm.setExternalDataSource(cfg.getExternalDataSource());
		return mm;
	}

	public static class Config {
		String jdbcUrl;
		String jdbcDriver;
		String jdbcUser;
		String jdbcPassword;
		// name of datasource, i.e. "jdbc/JPivotDS"
		String dataSource;
		String schemaUrl;
		String mdxQuery;
		String role;
		String dynResolver;
		// Locale requested
		String dynLocale;

		String connectionPooling;
		// external DataSource to be used by Mondrian
		DataSource externalDataSource = null;

		String dataSourceChangeListener;

		/**
		 * allows to override the current JDBC settings. 
		 * <p>
		 * All properties are allowed
		 * to contain ${some.name} that refer to other properties except the
		 * dataSource attribute where ${bean.property} is interpreted as bean EL that
		 * references session beans.
		 * <p>
		 * If a dataSource name is given, it is looked up as bean EL. If the result 
		 * is a DataSource, its used (e.g. the session contains a DataSource). If not,
		 * its interpreted as JNDI name.
		 * <p>
		 * If no dataSource name is given, values for jdbcDriver etc are needed. If these
		 * values are present, they are taken. Otherwise, they are looked up in the resources
		 * with the keys "jdbc.driver", "jdbc.url", "jdbc.user" and "jdbc.password".
		 */
		public void allowOverride(RequestContext context) {
			Resources res = context.getResources();

			// get default values
			setRole(getDefault(res, "mondrian.role", getRole()));
			setDynResolver(getDefault(res, "mondrian.dynResolver", getDynResolver()));
			setDynLocale(getDefault(res, "mondrian.dynLocale", getDynLocale()));

			setDataSourceChangeListener(getDefault(res, "mondrian.dataSourceChangeListener", getDataSourceChangeListener()));

			// if the data source is configured, use it
			if (externalDataSource != null) {
				logger.info("using external data source");
				return;
			}

			// support $-variables
			setJdbcDriver(replace(res, getJdbcDriver()));
			setJdbcUrl(replace(res, getJdbcUrl()));
			setJdbcUser(replace(res, getJdbcUser()));
			setJdbcPassword(replace(res, getJdbcPassword()));
			setConnectionPooling(replace(res, getConnectionPooling()));
			setDataSource(replace(res, getDataSource()));

			// if a data source name was given, use it
			if (!empty(dataSource)) {
				logger.info("using data source " + dataSource);
				findDataSource(context);
				return;
			}

			// if a jdbc driver was given, use it
			if (!empty(jdbcDriver)) {
				logger.info("using driver manager " + jdbcUrl);
				return;
			}

			// try default data source
			setDataSource(getDefault(res, "jdbc.datasource", getDataSource()));
			if (!empty(dataSource)) {
				logger.info("using default data source " + dataSource);
				findDataSource(context);
				return;
			}

			// try default jdbc drivermanager
			logger.info("using default driver manager " + jdbcUrl);
			setJdbcDriver(getDefault(res, "jdbc.driver", getJdbcDriver()));
			setJdbcUrl(getDefault(res, "jdbc.url", getJdbcUrl()));
			setJdbcUser(getDefault(res, "jdbc.user", getJdbcUser()));
			setJdbcPassword(getDefault(res, "jdbc.password", getJdbcPassword()));
			setConnectionPooling(getDefault(res, "jdbc.connectionPooling", getConnectionPooling()));
		}

		/**
		 * tries to find a DataSource as a bean EL in the current request or session. If not
		 * found, <code>dataSource</code> is interpreted as JNDI data source.
		 */
		private void findDataSource(RequestContext context) {
			Object obj;
			if (ExprUtils.isExpression(dataSource)) {
				// try "${bean.dataSource}"
				obj = context.getModelReference(dataSource);
			} else {
				// try "beanName"
				obj = context.getSession().getAttribute(dataSource);
			}
			if (obj instanceof DataSource) {
				logger.info("using app dataSource " + dataSource);
				this.dataSource = null;
				this.externalDataSource = (DataSource) obj;
			}
			// otherwise use it as jndi name
		}

		/**
		 * falls <code>val == null</code>, wird der default aus den resources geladen.
		 * $-Variablen werden ersetzt.
		 */
		private String getDefault(Resources res, String key, String val) {
			// if given, dont change
			val = replace(res, val);
			if (val != null)
				return val;
			return res.getOptionalString(key, null);
		}

		/**
		 * returns null for empty strings
		 */
		private String replace(Resources res, String val) {
			if (empty(val))
				return null;
			return res.replace(val);
		}

		private boolean empty(String s) {
			return s == null || s.trim().length() == 0;
		}

		/**
		 * Returns the jdbcDriver.
		 * @return String
		 */
		public String getJdbcDriver() {
			return jdbcDriver;
		}

		/**
		 * Returns the jdbcPassword.
		 * @return String
		 */
		public String getJdbcPassword() {
			return jdbcPassword;
		}

		/**
		 * Returns the jdbcUrl.
		 * @return String
		 */
		public String getJdbcUrl() {
			return jdbcUrl;
		}

		/**
		 * Returns the jdbcUser.
		 * @return String
		 */
		public String getJdbcUser() {
			return jdbcUser;
		}

		/**
		 * Returns the mdxQuery.
		 * @return String
		 */
		public String getMdxQuery() {
			return mdxQuery;
		}

		/**
		 * Returns the schemaUrl.
		 * @return String
		 */
		public String getSchemaUrl() {
			return schemaUrl;
		}

		/**
		 * Returns the role.
		 * @return String
		 */
		public String getRole() {
			return role;
		}

		/**
		 * Sets the role.
		 * @param role The role to set
		 */
		public void setRole(String role) {
			this.role = role;
		}

		/**
		 * Sets the jdbcDriver.
		 * @param jdbcDriver The jdbcDriver to set
		 */
		public void setJdbcDriver(String jdbcDriver) {
			this.jdbcDriver = jdbcDriver;
		}

		/**
		 * Sets the jdbcPassword.
		 * @param jdbcPassword The jdbcPassword to set
		 */
		public void setJdbcPassword(String jdbcPassword) {
			this.jdbcPassword = jdbcPassword;
		}

		/**
		 * Sets the jdbcUrl.
		 * @param jdbcUrl The jdbcUrl to set
		 */
		public void setJdbcUrl(String jdbcUrl) {
			this.jdbcUrl = jdbcUrl;
		}

		/**
		 * Sets the jdbcUser.
		 * @param jdbcUser The jdbcUser to set
		 */
		public void setJdbcUser(String jdbcUser) {
			this.jdbcUser = jdbcUser;
		}

		/**
		 * Sets the mdxQuery.
		 * @param mdxQuery The mdxQuery to set
		 */
		public void setMdxQuery(String mdxQuery) {
			this.mdxQuery = mdxQuery;
		}

		/**
		 * Sets the schemaUrl.
		 * @param schemaUrl The schemaUrl to set
		 */
		public void setSchemaUrl(String schemaUrl) {
			this.schemaUrl = schemaUrl;
		}

		/**
		 * @return
		 */
		public String getDataSource() {
			return dataSource;
		}

		/**
		 * @param string
		 */
		public void setDataSource(String string) {
			dataSource = string;
		}

		public String getDynResolver() {
			return dynResolver;
		}

		public void setDynResolver(String dynResolver) {
			this.dynResolver = dynResolver;
		}

		public String getConnectionPooling() {
			return connectionPooling;
		}

		public void setConnectionPooling(String connectionPooling) {
			this.connectionPooling = connectionPooling;
		}

		public DataSource getExternalDataSource() {
			return externalDataSource;
		}

		public void setExternalDataSource(DataSource externalDataSource) {
			this.externalDataSource = externalDataSource;
		}

		/**
		 * Getter for property dynLocale.
		 * @return Value of property dynLocale.
		 */
		public String getDynLocale() {
			return this.dynLocale;
		}

		/**
		 * Setter for property dynLocale.
		 * @param dynLocale New value of property dynLocale.
		 */
		public void setDynLocale(String dynLocale) {
			this.dynLocale = dynLocale;
		}

		public String toString() {
			return "Config[" + "jdbcUrl=" + jdbcUrl + ", jdbcDriver=" + jdbcDriver + ", jdbcUser="
			+ jdbcUser + ", jdbcPassword=" + jdbcPassword + ", dataSource=" + dataSource
			+ ", schemaUrl=" + schemaUrl + ", mdxQuery=" + mdxQuery + ", role=" + role
			+ ", dynResolver=" + dynResolver + ", connectionPooling=" + connectionPooling
			+ ", externalDataSource=" + externalDataSource + ", dynLocale=" + dynLocale + ", dataSourceChangeListener=" + dataSourceChangeListener + "]";
		}
		/**
		 * Getter for property dataSourceChangeListener.
		 * @return Value of property dataSourceChangeListener.
		 */

		public String getDataSourceChangeListener() {
			return dataSourceChangeListener;
		}
		/**
		 * Setter for property dataSourceChangeListener.
		 * @param dataSourceChangeListener New value of property dataSourceChangeListener.
		 */

		public void setDataSourceChangeListener(String dataSourceChangeListener) {
			this.dataSourceChangeListener = dataSourceChangeListener;
		}
	}
}