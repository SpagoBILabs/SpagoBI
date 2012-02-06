package it.eng.spagobi.engine.mobile.util;

import org.json.JSONException;
import org.json.JSONObject;


public interface IMobileTemplateInstance {
	
	public void loadTemplateFeatures()throws Exception;
	public JSONObject getFeatures();
	public String getDocumentType();
}
