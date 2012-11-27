/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.serializer;


import it.eng.spagobi.engines.network.bean.JSONNetwork;
import it.eng.spagobi.engines.network.serializer.json.NetworkJSONSerializer;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NetworkSerializerFactory implements ISerializerFactory {

	static NetworkSerializerFactory instance;
	
	
	static {
		instance = new NetworkSerializerFactory();
		SerializationManager.registerSerializerFactory(JSONNetwork.class, instance);		
	}
	
	
	public static NetworkSerializerFactory getInstance() {
		if (instance == null) {
			instance = new NetworkSerializerFactory();
		}
		return instance;
	}
	
	private NetworkSerializerFactory() {}

	
	public ISerializer getSerializer(String mimeType) {
		if (mimeType != null && !mimeType.equalsIgnoreCase("application/json")) {
			throw new SpagoBIRuntimeException("Serializer for mimeType " + mimeType + " not implemented");
		}
		return new NetworkJSONSerializer();
	}

}