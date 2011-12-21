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
package it.eng.spagobi.engines.weka.configurators;


import org.apache.log4j.Logger;

import weka.clusterers.Clusterer;
import weka.filters.unsupervised.attribute.AddCluster;
import weka.gui.beans.Filter;

/**
 * @author Gioia
 *
 */
public class FilterConfigurator extends AbstractWekaBeanConfigurator{
	
	private static transient Logger logger = Logger.getLogger(FilterConfigurator.class);
	
	static private void log(String msg) {
		logger.debug("FilterConfigurator:" + msg);
	}
	
	/**
	 * Sets the up.
	 * 
	 * @param bean the new up
	 * 
	 * @throws Exception the exception
	 */
	public void setup(Filter bean)  throws Exception {
		weka.filters.Filter filter =  bean.getFilter();
		String className = filter.getClass().getName();
		log("Class: " + className);
		if(className.equalsIgnoreCase(AddCluster.class.getName())) {
			AddCluster addCluster = (AddCluster)filter;
			StringBuffer buffer = new StringBuffer();
			buffer.append("Options: ");
			String[] options = addCluster.getOptions();		
			for(int i = 0; i < options.length; i++) {
				buffer.append(options[i] + "; ");
			}
			log(buffer.toString());
			Clusterer clusterer = addCluster.getClusterer();
			log(addCluster.getClusterer().getClass().getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.weka.configurators.WekaBeanConfigurator#setup(java.lang.Object)
	 */
	public void setup(Object bean)  throws Exception {
		if(!bean.getClass().getName().equalsIgnoreCase(Filter.class.getName()))
			throw new Exception();
		setup((Filter)bean);
	}
}
