/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.renderer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.map.renderer.configurator.AbstractMapRendererConfigurator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractLabelProducer implements ILabelProducer{
	
	Map settings;
	
	public void init(SourceBean conf) {
		if( settings == null ) {
			settings = new HashMap();
		}
		
		List params = conf.getAttributeAsList("PARAM");
		AbstractMapRendererConfigurator.addSettings(getSettings(), params);
	}

	public Map getSettings() {
		return settings;
	}

	public void setSettings(Map settings) {
		this.settings = settings;
	}
}
