/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.common.utils.DataMiningConstants;
import it.eng.spagobi.engines.datamining.model.DataMiningCommand;
import it.eng.spagobi.engines.datamining.model.DataMiningScript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class ScriptExecutor {

	static private Logger logger = Logger.getLogger(ScriptExecutor.class);

	private Rengine re;
	DataMiningEngineInstance dataminingInstance;
	IEngUserProfile profile;

	public ScriptExecutor(DataMiningEngineInstance dataminingInstance, IEngUserProfile profile) {
		this.dataminingInstance = dataminingInstance;
		this.profile = profile;
	}

	public Rengine getRe() {
		return re;
	}

	public void setRe(Rengine re) {
		this.re = re;
	}

	protected void evalScript(DataMiningCommand command, Boolean rerun) throws Exception {

		// checks whether executed before
		if (rerun || command.getExecuted() == null || !command.getExecuted()) {
			
			//load libraries from local dir (if needed)
			loadLibrariesFromRLocal(command);
			// command-->script name --> execute script without output
			String scriptToExecute = getScriptCodeToEval(command);

			// loading libraries, preprocessing, functions definition in main
			// "auto"
			// script
			String ret = createTemporarySourceScript(scriptToExecute);
			re.eval("source(\"" + ret + "\")");
			// detects action to execute from command --> used to call functions
			String action = command.getAction();
			if (action != null) {
				re.eval(action);
			}

			command.setExecuted(true);
			deleteTemporarySourceScript(ret);
		} else {
			// everithing in user workspace
			logger.debug("Command " + command.getName() + " script " + command.getScriptName() + " already executed");
		}

	}

	protected void deleteTemporarySourceScript(String path) {
		boolean success = (new File(path)).delete();
	}

	private String getScriptCodeToEval(DataMiningCommand command) throws Exception {
		String code = "";

		DataMiningScript script = getScript(command);
		if (script != null) {			
			code = DataMiningUtils.replaceVariables(command.getVariables(), script.getCode());
		}
		return code;
	}
	private void loadLibrariesFromRLocal(DataMiningCommand command){
		DataMiningScript script = getScript(command);
		if (script != null) {
			REXP rHome = re.eval("libdir<-paste(R.home(),\"library\", sep=\"/\")");
			if(rHome != null){
				
				String libraryNames = script.getLibraries();
				String[] libs = libraryNames.split(",");
				for (int i = 0; i < libs.length; i++) {
					String lib = libs[i].trim();
					re.eval("library("+lib+",lib.loc=libdir)");
				}
			}
		}
	}
	private DataMiningScript getScript(DataMiningCommand command){
		String scriptName = command.getScriptName();
		if (dataminingInstance.getScripts() != null && !dataminingInstance.getScripts().isEmpty()) {
			for (Iterator it = dataminingInstance.getScripts().iterator(); it.hasNext();) {
				DataMiningScript script = (DataMiningScript) it.next();
				if (script.getName().equals(scriptName)) {					
					return script;
				}
			}
		}
		return null;
	}
	

	private String createTemporarySourceScript(String code) throws IOException {
		String name = RandomStringUtils.randomAlphabetic(10);
		File temporarySource = new File(DataMiningUtils.getUserResourcesPath(profile) + DataMiningConstants.DATA_MINING_TEMP_PATH_SUFFIX + name + ".R");
		FileWriter fw = null;
		String ret = "";
		try {
			fw = new FileWriter(temporarySource);
			fw.write(code);
			fw.close();
			ret = temporarySource.getPath();
			ret = ret.replaceAll("\\\\", "/");
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}

		return ret;

	}

}
