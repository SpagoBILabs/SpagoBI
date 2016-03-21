/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.dataproxy;

import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.scripting.SpagoBIScriptManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ScriptDataProxy extends AbstractDataProxy {

	private String language;

	private static transient Logger logger = Logger.getLogger(ScriptDataProxy.class);

	private static final String PREDEFINED_GROOVY_SCRIPT_FILE = "predefinedGroovyScript.groovy";
	private static final String PREDEFINED_JAVASCRIPT_SCRIPT_FILE = "predefinedJavascriptScript.js";

	public ScriptDataProxy() {
		super();
	}

	public ScriptDataProxy(String script, String language) {
		setStatement(script);
		setLanguage(language);
	}

	@Override
	public IDataStore load(IDataReader dataReader) {

		String data = null;
		IDataStore dataStore = null;
		SpagoBIScriptManager scriptManager = new SpagoBIScriptManager();

		logger.debug("IN");

		try {
			if (dataReader == null)
				throw new IllegalArgumentException("Input parameter [" + dataReader + "] cannot be null");

			scriptManager = new SpagoBIScriptManager();

			List<String> imports = null;
			if ("groovy".equals(language)) {
				imports = new ArrayList<String>();
				String predefinedGroovyScript = getScript(PREDEFINED_GROOVY_SCRIPT_FILE);
				imports.add(predefinedGroovyScript);
			} else if ("ECMAScript".equals(language)) {
				imports = new ArrayList<String>();
				String predefinedJavascriptScript = getScript(PREDEFINED_JAVASCRIPT_SCRIPT_FILE);
				imports.add(predefinedJavascriptScript);
			}

			Map<String, Object> bindings = new HashMap<String, Object>();
			bindings.put("attributes", getProfile());
			bindings.put("parameters", getParameters());
			Object o = scriptManager.runScript(statement, language, bindings, imports);
			data = (o == null) ? "" : o.toString();
			dataStore = dataReader.read(data);
		} catch (Throwable t) {
			throw new RuntimeException("An unexpected error occured while loading datastore", t);
		} finally {
			logger.debug("OUT");
		}

		return dataStore;
	}

	private String getScript(String fileName) {
		String importedScript;
		InputStream is;

		importedScript = null;
		is = null;
		try {
			logger.debug("Importing script from file [" + fileName + "]");
			is = ScriptDataProxy.class.getClassLoader().getResourceAsStream(fileName);
			importedScript = getImportedScript(is);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while importing script from file [" + fileName + "]", t);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException t) {
					logger.warn("Impossible to close inpust stream associated to file [" + fileName + "]", t);
				}
			}
		}

		return importedScript;
	}

	private String getImportedScript(InputStream is) {
		String importedScript;

		importedScript = null;

		try {
			StringBuffer buffer = new StringBuffer();
			int arrayLength = 1024;
			byte[] bufferbyte = new byte[arrayLength];
			char[] bufferchar = new char[arrayLength];
			int len;
			while ((len = is.read(bufferbyte)) >= 0) {
				for (int i = 0; i < arrayLength; i++) {
					bufferchar[i] = (char) bufferbyte[i];
				}
				buffer.append(bufferchar, 0, len);
			}
			importedScript = buffer.toString();
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while importing script from stream", t);
		}

		return importedScript;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
