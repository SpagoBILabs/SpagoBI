/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.behaviour;

import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.bo.ConfigurableDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.common.query.IQueryTransformer;
import it.eng.spagobi.tools.dataset.utils.ParametersResolver;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.scripting.SpagoBIScriptManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 */
public class QuerableBehaviour extends AbstractDataSetBehaviour {

	IQueryTransformer queryTransformer;

	private static transient Logger logger = Logger.getLogger(QuerableBehaviour.class);
	private final ParametersResolver parametersResolver = new ParametersResolver();

	public QuerableBehaviour(IDataSet targetDataSet) {
		super(QuerableBehaviour.class.getName(), targetDataSet);
	}

	public String getStatement() {
		String statement;

		logger.debug("IN");
		try {
			Assert.assertNotNull(getTargetDataSet(), "Target dataset of a QuerableBehaviour cannot be null");
			logger.debug("Querable dataset [" + getTargetDataSet().getName() + "] is of type [" + getTargetDataSet().getClass().getName() + "]");
			statement = getBaseStatement();
			logger.debug("Base dataset statement is equal to [" + statement + "]");
			Assert.assertNotNull(statement, "Querable dataset statment cannot be null");

			statement = parametersResolver.resolveAll(statement, getTargetDataSet());
			logger.debug("Dataset statement after profile attributes and parameters substitution [" + statement + "]");

			if (queryTransformer != null) {
				statement = (String) queryTransformer.transformQuery(statement);
			}
		} finally {
			logger.debug("OUT");
		}

		return statement;
	}

	private String getBaseStatement() {
		String statement = null;
		if (getTargetDataSet() instanceof ScriptDataSet) {
			statement = ((ScriptDataSet) getTargetDataSet()).getScript();
		} else if (getTargetDataSet() instanceof ConfigurableDataSet) {
			ConfigurableDataSet jdbcDataSet = (ConfigurableDataSet) getTargetDataSet();
			if (StringUtilities.isNotEmpty(jdbcDataSet.getQueryScript())) {
				statement = (String) jdbcDataSet.getQuery();
				statement = applyScript(statement, jdbcDataSet.getQueryScript(), jdbcDataSet.getQueryScriptLanguage());
			} else {
				statement = (String) jdbcDataSet.getQuery();
			}
		} else {
			logger.error("Type [" + getTargetDataSet().getClass().getName() + "] of the dataset [" + getTargetDataSet().getName() + "] is not managed! \n"
					+ "Is impossible to define the dataset's statemant! ");
			Assert.assertNotNull(statement, "Querable dataset statment cannot be null");
		}
		return statement;
	}

	private String applyScript(String statement, String script, String language) {
		List<Object> imports = null;
		if ("groovy".equals(language)) {
			imports = new ArrayList<Object>();
			URL url = Thread.currentThread().getContextClassLoader().getResource("predefinedGroovyScript.groovy");
			File scriptFile;
			try {
				logger.debug("predefinedGroovyScript.groovy file URL is equal to [" + url + "]");
				// URI fileURI = url.toURI();
				// logger.debug("predefinedGroovyScript.groovy file URL is equal to ["
				// + fileURI + "]");
				// scriptFile = new File( fileURI );
				// imports.add(scriptFile);
				imports.add(url);
			} catch (Throwable t) {
				logger.warn("Impossible to load predefinedGroovyScript.groovy", t);
			}

		} else if ("ECMAScript".equals(language)) {
			imports = new ArrayList<Object>();
			URL url = Thread.currentThread().getContextClassLoader().getResource("predefinedJavascriptScript.js");
			File scriptFile;
			try {
				logger.debug("predefinedJavascriptScript.js file URL is equal to [" + url + "]");
				// URI fileURI = url.toURI();
				// logger.debug("predefinedJavascriptScript.js file URL is equal to ["
				// + fileURI + "]");
				// scriptFile = new File( fileURI );
				// imports.add(scriptFile);
				imports.add(url);
			} catch (Throwable t) {
				logger.warn("Impossible to load predefinedJavascriptScript.js", t);
			}
		} else {
			logger.debug("There is no predefined script file to import for scripting language [" + language + "]");
		}

		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("attributes", getTargetDataSet().getUserProfileAttributes());
		bindings.put("parameters", getTargetDataSet().getParamsMap());
		bindings.put("query", statement);
		SpagoBIScriptManager scriptManager = new SpagoBIScriptManager();
		Object o = scriptManager.runScript(script, language, bindings, imports);
		return o == null ? statement : o.toString();
	}

	public IQueryTransformer getQueryTransformer() {
		return queryTransformer;
	}

	public void setQueryTransformer(IQueryTransformer queryTransformer) {
		this.queryTransformer = queryTransformer;
	}
}
