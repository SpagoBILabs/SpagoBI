/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.template;

import org.json.JSONObject;

import it.eng.spagobi.engines.network.bo.NetworkDefinition;


/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NetworkTemplate {

	private NetworkDefinition networkDefinition;
	private JSONObject networkOptions;
	
	public NetworkTemplate() {
		networkDefinition = NetworkDefinition.EMPTY_NETWORK ;
	}

	public NetworkDefinition getNetworkDefinition() {
		return networkDefinition;
	}

	public void setNetworkDefinition(NetworkDefinition networkDefinition) {
		this.networkDefinition = networkDefinition;
	}
	
	public void setNetworkXML(String networkXML){
		networkDefinition.setNetworkXML(networkXML);
	}

	public JSONObject getNetworkOptions() {
		return networkOptions;
	}

	public void setNetworkOptions(JSONObject networkOptions) {
		this.networkOptions = networkOptions;
	}
	
	
	
	
}
