/**
 * 
 * LICENSE: see LICENSE.txt file
 * 
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
