/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.datamining;

import it.eng.spagobi.engines.datamining.model.DataMiningDataset;
import it.eng.spagobi.engines.datamining.model.Output;
import it.eng.spagobi.engines.datamining.template.DataMiningTemplate;
import it.eng.spagobi.engines.datamining.template.DataMiningTemplateParser;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Monica Franceschini
 */
public class DataMiningEngineInstance extends AbstractEngineInstance {

	private final List<String> includes;
	private String script;

	private final List<Output> outputs;

	private List<DataMiningDataset> datasets;

	public static transient Logger logger = Logger.getLogger(DataMiningEngineInstance.class);

	protected DataMiningEngineInstance(Object template, Map env) {
		this(DataMiningTemplateParser.getInstance() != null ? DataMiningTemplateParser.getInstance().parse(template) : null, env);
	}

	public DataMiningEngineInstance(DataMiningTemplate template, Map env) {

		super(env);
		logger.debug("IN");

		includes = DataMiningEngine.getConfig().getIncludes();

		// IEngUserProfile profile = (IEngUserProfile)
		// env.get(EngineConstants.ENV_USER_PROFILE);

		script = template.getScript();
		datasets = template.getDatasets();
		outputs = template.getOutputs();

		logger.debug("OUT");
	}

	public IDataSource getDataSource() {
		return (IDataSource) this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}

	public IDataSet getDataSet() {
		return (IDataSet) this.getEnv().get(EngineConstants.ENV_DATASET);
	}

	public Locale getLocale() {
		return (Locale) this.getEnv().get(EngineConstants.ENV_LOCALE);
	}

	public AuditServiceProxy getAuditServiceProxy() {
		return (AuditServiceProxy) this.getEnv().get(EngineConstants.ENV_AUDIT_SERVICE_PROXY);
	}

	public EventServiceProxy getEventServiceProxy() {
		return (EventServiceProxy) this.getEnv().get(EngineConstants.ENV_EVENT_SERVICE_PROXY);
	}

	// -- unimplemented methods
	// ------------------------------------------------------------

	public IEngineAnalysisState getAnalysisState() {
		throw new DataMiningEngineRuntimeException("Unsupported method [getAnalysisState]");
	}

	public void setAnalysisState(IEngineAnalysisState analysisState) {
		throw new DataMiningEngineRuntimeException("Unsupported method [setAnalysisState]");
	}

	public void validate() throws SpagoBIEngineException {
		throw new DataMiningEngineRuntimeException("Unsupported method [validate]");
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<DataMiningDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DataMiningDataset> datasets) {
		this.datasets = datasets;
	}

	public List<Output> getOutputs() {
		return outputs;
	}

}
