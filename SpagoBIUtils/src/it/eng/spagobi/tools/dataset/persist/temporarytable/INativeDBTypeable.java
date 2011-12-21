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
package it.eng.spagobi.tools.dataset.persist.temporarytable;

import java.util.Map;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public interface INativeDBTypeable {

	public static final String SIZE ="size";
	public static final String PRECISION ="precision";
	public static final String SCALE ="scale";
	public static final String DECIMAL ="decimal";
	
	/**
	 * Translate the java type in input with the corresponding native db type
	 * @param typeJavaName the java type
	 * @param properties the properties (for example scale, mantissa length, scale)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	String getNativeTypeString(String typeJavaName, Map properties);
	
}
