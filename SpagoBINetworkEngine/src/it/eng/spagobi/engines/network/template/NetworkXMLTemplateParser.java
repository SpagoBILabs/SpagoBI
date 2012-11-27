/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.json.JSONTemplateUtils;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NetworkXMLTemplateParser implements INetworkTemplateParser{

	public final static String CURRENT_VERSION = "0";
	
	public final static String ATTRIBUTE_VERSION = "version";
	public final static String TAG_GRAPHML = "GRAPHML";
	public final static String TAG_GRAPH_OPTIONS = "options";

	
	public static transient Logger logger = Logger.getLogger(NetworkXMLTemplateParser.class);
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.network.template.INetworkTemplateParser#parse(java.lang.Object, java.util.Map)
	 */
	public NetworkTemplate parse(Object templateObject, Map env) {

		NetworkTemplate networkTemplate;
		String encodingFormatVersion;
		SourceBean template;
		
		
		
		try {
			SourceBean xml =  SourceBean.fromXMLString((String)templateObject);
			Assert.assertNotNull(xml, "SourceBean in input cannot be not be null");
			logger.debug("Parsing template [" + xml.getName() + "] ...");
			
			networkTemplate = new NetworkTemplate();

			encodingFormatVersion = (String) xml.getAttribute(ATTRIBUTE_VERSION);
			
			if (encodingFormatVersion == null) {
				logger.debug("no version found, default is 0");
				encodingFormatVersion = "0";
			}
			
			logger.debug("Row data encoding version  [" + encodingFormatVersion + "]");
			
			if (encodingFormatVersion.equalsIgnoreCase(CURRENT_VERSION)) {				
				template = xml;
			} else {
				logger.warn("Row data encoding version [" + encodingFormatVersion + "] does not match with the current version used by the engine [" + CURRENT_VERSION + "] ");
				logger.debug("Converting from encoding version [" + encodingFormatVersion + "] to encoding version [" + CURRENT_VERSION + "]....");
				logger.debug("NO VERSION TRASFORMER IS SPECIFIED FOR VERSION "+CURRENT_VERSION);
				template = xml;
			}
			
			// TAG_GRAPHML block
			if (template.getName().equalsIgnoreCase(TAG_GRAPHML)) {
				//SourceBean graphmlTemplate = (SourceBean) template.getAttribute(TAG_GRAPHML);
				networkTemplate.setNetworkXML((String)templateObject);
			}
			// TAG_GRAPH_OPTIONS block
			if(template.containsAttribute(TAG_GRAPH_OPTIONS)) {
				SourceBean optionsBean;
				optionsBean = (SourceBean) template.getAttribute(TAG_GRAPH_OPTIONS);
				networkTemplate.setNetworkOptions(loadTemplateFeatures(optionsBean));
			} 

			logger.debug("Templete parsed succesfully");

		} catch(Throwable t) {
			throw new NetworkTemplateParserException("Impossible to parse template [" + templateObject.toString()+ "]", t);
		} finally {
			logger.debug("OUT");
		}	

		return networkTemplate;
	}

	/**
	 * Load the json object with the option for the network from the template
	 * @param optionsBean the xml bean with the options
	 * @return a json object with the options
	 * @throws Exception
	 */
	private JSONObject loadTemplateFeatures(SourceBean optionsBean) throws Exception {
		JSONTemplateUtils ju = new JSONTemplateUtils();
		//JSONArray array = toJSONArray(this.paramsMap);
		JSONArray array =new JSONArray();
		JSONObject features = ju.getJSONTemplateFromXml(optionsBean, array);
		return features;
	}

//	private JSONArray toJSONArray(HashMap<String, String> paramsMap) {
//		JSONArray array = new JSONArray();
//		if (paramsMap != null && !paramsMap.isEmpty()) {
//			Iterator<String> it = paramsMap.keySet().iterator();
//			while (it.hasNext()) {
//				String name = it.next();
//				JSONObject obj = new JSONObject();
//				try {
//					obj.put("name", name);
//					obj.put("value", paramsMap.get(name));
//				} catch (JSONException e) {
//					throw new RuntimeException("cannot convert [" + paramsMap.toString() + "] into a JSONArray");
//				}
//				array.put(obj);
//			}
//		}
//		return array;
//	}

	
}
