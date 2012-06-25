/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.weka.configurators;

import weka.gui.beans.Filter;

/**
 * @author Gioia
 *
 */
public class WekaBeanConfiguratorFactory {
	
	private static WekaBeanConfigurator filterConfigurator = new FilterConfigurator();
	
	/**
	 * Sets the up.
	 * 
	 * @param bean the new up
	 */
	public static void setup(Object bean) {
		try{
			if(bean.getClass().getName().equalsIgnoreCase(Filter.class.getName()))
				filterConfigurator.setup(bean);	
		} catch(Exception e) {
			e.printStackTrace();
		}
				
	}
}
