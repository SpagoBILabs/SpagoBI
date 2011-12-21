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
package it.eng.spagobi.jpivotaddins.formatters;

import org.apache.log4j.Logger;

import mondrian.olap.CellFormatter;

public class MinutesToHoursFormatter implements CellFormatter {

	private static transient Logger logger = Logger.getLogger(MinutesToHoursFormatter.class);
	
	public String formatCell(Object value) {
		logger.debug("IN");
		if (value == null) {
			logger.warn("Value in input is null");
			return "";
		}
		logger.debug(value.getClass().getName());
		String toReturn = null;
		if (value instanceof Number) {
			Number doubleObj = (Number) value;
			double d = doubleObj.doubleValue();
			int hours = (int) Math.floor(d/60);
			int minutes = (int) d % 60;
			toReturn = hours + "h " + minutes + "m";
		} else {
			toReturn = value.toString();
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

}
