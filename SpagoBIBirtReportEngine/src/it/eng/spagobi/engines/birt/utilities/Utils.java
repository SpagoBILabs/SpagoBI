/**
 * 
 * LICENSE: see BIRT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.birt.utilities;

public class Utils {

	/**
	 * Resolve system properties.
	 * 
	 * @param logDir the log dir
	 * 
	 * @return the string
	 */
	public static String resolveSystemProperties(String logDir) {
		if (logDir == null) return null;
		int startIndex = logDir.indexOf("${");
		if (startIndex == -1) return logDir;
		else return resolveSystemProperties(logDir, startIndex);
	}
	
	/**
	 * Resolve system properties.
	 * 
	 * @param logDir the log dir
	 * @param startIndex the start index
	 * 
	 * @return the string
	 */
	public static String resolveSystemProperties(String logDir, int startIndex) {
		if (logDir == null) return logDir;
		int endIndex = -1;
		if (logDir.indexOf("${", startIndex) != -1) {
			int beginIndex = logDir.indexOf("${", startIndex);
			endIndex = logDir.indexOf("}", beginIndex);
			if (endIndex != -1) {
				String sysPropertyName = logDir.substring(beginIndex + 2, endIndex);
				String sysPropertyValue = System.getProperty(sysPropertyName);
				if (sysPropertyValue != null) {
					logDir = logDir.replace("${" + sysPropertyName + "}", sysPropertyValue);
				}
			}
		}
		if (endIndex != -1) {
			if (logDir.indexOf("${", endIndex) != -1) {
				logDir = resolveSystemProperties(logDir, endIndex);
			}
		}
		return logDir;
	}
}
