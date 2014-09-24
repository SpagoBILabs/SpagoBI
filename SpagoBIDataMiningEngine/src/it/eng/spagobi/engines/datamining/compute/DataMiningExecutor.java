/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.bo.DataMiningResult;
import it.eng.spagobi.engines.datamining.model.DataMiningCommand;
import it.eng.spagobi.engines.datamining.model.DataMiningDataset;
import it.eng.spagobi.engines.datamining.model.Output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rosuda.JRI.Rengine;

public class DataMiningExecutor {
	static private Logger logger = Logger.getLogger(DataMiningExecutor.class);

	private Rengine re;
	private IEngUserProfile profile;

	private final CommandsExecutor commandsExecutor;
	private final DatasetsExecutor datasetsExecutor;
	private final OutputExecutor outputExecutor;
	private final ScriptExecutor scriptExecutor;

	public DataMiningExecutor(DataMiningEngineInstance dataminingInstance, IEngUserProfile profile) {
		super();
		commandsExecutor = new CommandsExecutor(dataminingInstance, profile);
		datasetsExecutor = new DatasetsExecutor(dataminingInstance, profile);
		outputExecutor = new OutputExecutor(dataminingInstance, profile);
		scriptExecutor = new ScriptExecutor(dataminingInstance, profile);
	}

	/**
	 * Prepare Rengine and user workspace environmet
	 * 
	 * @param dataminingInstance
	 * @param userProfile
	 * @throws IOException
	 */
	private void setupEnvonment(IEngUserProfile userProfile) throws IOException {
		profile = userProfile;

		// new R-engine
		re = Rengine.getMainEngine();
		if (re == null) {
			/*
			 * attention : setting to --save the engine doesn't remove object
			 * from the current environment,so it's unrelevant the workspace
			 * saving if you don't remove manualli the R objects first through
			 * the rm command:re = new Rengine(new String[] { "--save" }, false,
			 * null);
			 */
			re = new Rengine(new String[] { "--vanilla" }, false, null);

		}

		if (!re.waitForR()) {
			logger.error("Cannot load R");
		}
		commandsExecutor.setRe(re);
		datasetsExecutor.setRe(re);
		outputExecutor.setRe(re);
		scriptExecutor.setRe(re);

		DataMiningUtils.createUserResourcesPath(profile);
		// get user R workspace
		loadUserWorkSpace();
	}

	/**
	 * Starts the execution of the auto mode output and the auto mode command
	 * passed in paramenters (to avoid list discovering).
	 * 
	 * @param dataminingInstance
	 * @param command
	 *            in atuo mode
	 * @param output
	 *            in atuo mode
	 * @param userProfile
	 * @param executionType
	 * @return DataMiningResult
	 * @throws Exception 
	 */
	public DataMiningResult execute(HashMap params, DataMiningCommand command, Output output, IEngUserProfile userProfile, Boolean rerun) throws Exception {

		List<DataMiningResult> results = new ArrayList<DataMiningResult>();

		setupEnvonment(userProfile);

		// datasets preparation
		datasetsExecutor.evalDatasetsNeeded(params);// only
		// those
		// needed
		// by
		// command
		// and
		// output

		// evaluates script code
		scriptExecutor.evalScript(command, rerun);

		// create output
		DataMiningResult result = outputExecutor.evalOutput(output, scriptExecutor);

		// save result of script computation objects and datasets to
		// user workspace
		saveUserWorkSpace();

		// re.end();//has some problems
		return result;
	}

	public void updateDatasetInWorkspace(DataMiningDataset ds, IEngUserProfile userProfile) throws IOException {

		setupEnvonment(userProfile);

		// datasets preparation
		datasetsExecutor.updateDataset(ds);

		// save result of script computation objects and datasets to
		// user workspace
		saveUserWorkSpace();

	}

	
	protected void loadUserWorkSpace() throws IOException {
		/*
		 * example usage > save.image(file = 'D:/script/.Rdata', safe = TRUE) >
		 * load(file = 'D:/script/.Rdata')
		 */
		// create user workspace data
		re.eval("save(list = ls(all = TRUE), file= '" + profile.getUserUniqueIdentifier() + ".RData')");
		re.eval("load(file= '" + profile.getUserUniqueIdentifier() + ".RData')");
	}

	protected void saveUserWorkSpace() throws IOException {
		re.eval("save(list = ls(all = TRUE), file= '" + profile.getUserUniqueIdentifier() + ".RData')");
	}

}
