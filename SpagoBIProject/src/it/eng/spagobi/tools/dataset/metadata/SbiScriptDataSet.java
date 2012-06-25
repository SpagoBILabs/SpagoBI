/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.metadata;

public class SbiScriptDataSet extends SbiDataSetHistory {

	 private String script = null;
	 private String languageScript = null;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getLanguageScript() {
		return languageScript;
	}

	public void setLanguageScript(String languageScript) {
		this.languageScript = languageScript;
	}
	
}
