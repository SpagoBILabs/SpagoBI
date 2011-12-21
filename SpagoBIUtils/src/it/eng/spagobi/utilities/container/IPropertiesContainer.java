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
package it.eng.spagobi.utilities.container;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IPropertiesContainer {
	Object getProperty(String propertyName);
	void setProperty(String propertyName, Object propertyValue);
	boolean containsProperty(String propertyName);
	
	String getPropertyAsString(String propertyName);
	
	Boolean getPropertyAsBoolean(String attrName);
	
	Boolean getPropertyAsBoolean(String propertyName, boolean defaultValue) ;
	
	Integer getPropertyAsInteger(String propertyName);
	
	List getPropertyAsStringList(String propertyName) ;
	
	List getPropertyAsCsvStringList(String propertyName, String separator) ;
	
	JSONObject getPropertyAsJSONObject(String propertyName) ;
}
