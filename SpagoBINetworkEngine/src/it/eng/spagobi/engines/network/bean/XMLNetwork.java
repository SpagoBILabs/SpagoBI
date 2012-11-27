/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.bean;

import it.eng.spagobi.engines.network.serializer.SerializationException;
import it.eng.spagobi.utilities.StringUtils;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class XMLNetwork implements INetwork{

	private static final String TYPE = "XML";
	private String net="";
	
	
	
	/**
	 * @param net
	 */
	public XMLNetwork(String net) {
		super();
		this.net = net;
	}
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.network.bean.INetwork#getNetworkAsString()
	 */
	public String getNetworkAsString() throws SerializationException {
		return net;
	}
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.network.bean.INetwork#getNetworkType()
	 */
	public String getNetworkType() {
		return TYPE;
	}
	

}
