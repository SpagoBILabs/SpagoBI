/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engine.mobile.composed.serializer;

import it.eng.spagobi.engine.mobile.template.AbstractMobileTemplateJSONSerializer;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class MobileComposedJSONSerializer extends AbstractMobileTemplateJSONSerializer{
		
		private static Logger logger = Logger.getLogger(MobileComposedJSONSerializer.class);
		
		public Object write(IMobileTemplateInstance templateInstance) throws Exception {
			logger.debug("IN");
			JSONObject features = templateInstance.getFeatures();
			templateInstance.getDocumentProperties();
			features.put("documentProperties",getDocumentPropertiesJSON(templateInstance));
			logger.debug("OUT");
			return features;
		}

}
