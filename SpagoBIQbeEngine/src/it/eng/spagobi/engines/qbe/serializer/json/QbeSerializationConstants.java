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
package it.eng.spagobi.engines.qbe.serializer.json;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeSerializationConstants {
	public static transient String SLOT_NAME = "name";
	public static transient String SLOT_VALUESET = "valueset";	
	public static transient String SLOT_VALUESET_TYPE = "type";
	public static transient String SLOT_VALUESET_TYPE_PUNCTUAL  = "punctual";
	public static transient String SLOT_VALUESET_TYPE_RANGE  = "range";
	public static transient String SLOT_VALUESET_TYPE_DEFAULT  = "default";
	public static transient String SLOT_VALUESET_VALUES = "values";	
	public static transient String SLOT_VALUESET_FROM = "from";
	public static transient String SLOT_VALUESET_INCLUDE_FROM = "includeFrom";
	public static transient String SLOT_VALUESET_TO = "to";
	public static transient String SLOT_VALUESET_INCLUDE_TO = "includeTo";
}
