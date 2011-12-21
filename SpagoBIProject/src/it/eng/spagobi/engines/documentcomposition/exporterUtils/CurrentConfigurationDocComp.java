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
package it.eng.spagobi.engines.documentcomposition.exporterUtils;

import it.eng.spago.util.GeneralUtilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * @author gavardi
 * A map that for a document label keeps track of all parameters and their current value
 *
 */

public class CurrentConfigurationDocComp {

	String label=null;
	Map<String, Object> parameters=null;
	
	public CurrentConfigurationDocComp(String label) {
		super();
		this.label = label;
		this.parameters = new HashMap<String, Object>();
	}

	
	public void fillParsFromUrl(String urlString){
		parameters=it.eng.spagobi.commons.utilities.GeneralUtilities.getParametersFromURL(urlString);
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(java.util.Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
}
