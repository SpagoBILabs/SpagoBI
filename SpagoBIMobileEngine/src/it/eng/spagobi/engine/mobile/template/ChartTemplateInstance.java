/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */

package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.json.JSONTemplateUtils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChartTemplateInstance implements IMobileTemplateInstance{
	private SourceBean template;
	private JSONArray parameters;
	private JSONObject features;
	
	private static transient Logger logger = Logger.getLogger(ChartTemplateInstance.class);


	public ChartTemplateInstance(SourceBean template, JSONArray parameters) {
		this.template = template;
		this.parameters = parameters;
	}


	@Override
	public void loadTemplateFeatures() throws Exception {
		JSONTemplateUtils ju = new JSONTemplateUtils();
		features = ju.getJSONTemplateFromXml(template, parameters);
	}


	@Override
	public String getDocumentType() {
		return MobileConstants.CHART_TYPE;
	}


	@Override
	public JSONObject getFeatures() {
		return features;
	}

}
