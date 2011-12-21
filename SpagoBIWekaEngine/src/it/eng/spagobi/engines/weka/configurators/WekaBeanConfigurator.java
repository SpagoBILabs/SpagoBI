/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.weka.configurators;

/**
 * @author Gioia
 *
 */
public interface WekaBeanConfigurator {
	
	/**
	 * Sets the up.
	 * 
	 * @param bean the new up
	 * 
	 * @throws Exception the exception
	 */
	public void setup(Object bean) throws Exception;
}
