/**
 *
 *	LICENSE: see COPYING file
 *
**/
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
