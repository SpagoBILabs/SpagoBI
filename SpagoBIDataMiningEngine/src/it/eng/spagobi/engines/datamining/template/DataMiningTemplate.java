/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.template;

import it.eng.spagobi.engines.datamining.model.FileDataset;
import it.eng.spagobi.engines.datamining.model.Output;

import java.util.List;

/**
 * @author Monica Franceschini
 */
public class DataMiningTemplate {

	private String script;

	private List<Output> outputs;

	private List<FileDataset> datasets;

	public List<Output> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}

	public List<FileDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<FileDataset> datasets) {
		this.datasets = datasets;
	}

	private List<Parameter> parameters;

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public class Parameter {
		private String name;
		private String alias;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

	}

}
