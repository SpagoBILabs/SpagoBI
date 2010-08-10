package it.eng.spagobi.tools.dataset.metadata;

public class SbiScriptDataSet extends SbiDataSetConfig {

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
