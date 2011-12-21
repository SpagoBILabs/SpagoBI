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
package it.eng.spagobi.engines.georeport.features.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class FeaturesProviderDAOFactory {
	
	private static Map<String,IFeaturesProviderDAO> mappings;

	static {
		mappings = new HashMap();
		mappings.put("wfs", new FeaturesProviderDAOWFSImpl());
		mappings.put("file", new FeaturesProviderDAOFileImpl());
	}
	
	public static void initMappings() {
		mappings = new HashMap();
		mappings.put("wfs", new FeaturesProviderDAOWFSImpl());
		mappings.put("file", new FeaturesProviderDAOFileImpl());
	}
	
	public static IFeaturesProviderDAO getFeaturesProviderDAO(String featureSourceType) {
		initMappings();
		return mappings.get(featureSourceType);
	}
}
