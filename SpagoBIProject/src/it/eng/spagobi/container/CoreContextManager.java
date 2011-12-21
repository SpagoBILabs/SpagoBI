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
package it.eng.spagobi.container;


import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.container.strategy.IContextRetrieverStrategy;


import org.apache.log4j.Logger;

/**
 * This class provides useful methods to manage context on a ISessionContainer
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class CoreContextManager extends ContextManager {
	
	
	
	
	static private Logger logger = Logger.getLogger(CoreContextManager.class);
	
	public CoreContextManager(IBeanContainer beanContainer, IContextRetrieverStrategy strategy) {
		super(beanContainer,strategy);
		logger.debug("IN");
		
	}
	/**
	 * <b>TO BE USED ONLY INSIDE SPAGOBI CORE, NOT INSIDE EXTERNAL ENGINES</b>.
	 * Return the BIObject associated with the input key.
	 * If the key is associated to an object that is not a BIObject instance, a ClassCastException is thrown.
	 * 
	 * @param key The input key
	 * @return the BIObject associated with the input key.
	 */
	
	public BIObject getBIObject(String key) {
		logger.debug("IN");
		BIObject toReturn = null;
		try {
			Object object = get(key);
			toReturn = (BIObject) object;
			return toReturn; 
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * <b>TO BE USED ONLY INSIDE SPAGOBI CORE, NOT INSIDE EXTERNAL ENGINES</b>.
	 * Return the ExecutionInstance associated with the input key.
	 * If the key is associated to an object that is not a ExecutionInstance instance, a ClassCastException is thrown.
	 * 
	 * @param key The input key
	 * @return the ExecutionInstance associated with the input key.
	 */
	public ExecutionInstance getExecutionInstance(String key) {
		logger.debug("IN");
		ExecutionInstance toReturn = null;
		try {
			Object object = get(key);
			toReturn = (ExecutionInstance) object;
			return toReturn; 
		} finally {
			logger.debug("OUT");
		}
	}

}
