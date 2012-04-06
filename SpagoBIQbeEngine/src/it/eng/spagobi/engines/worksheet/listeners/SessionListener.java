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
package it.eng.spagobi.engines.worksheet.listeners;

import java.util.Iterator;

import it.eng.spagobi.utilities.temporarytable.TemporaryTable;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableRecorder;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class SessionListener implements HttpSessionListener {

	public static transient Logger logger = Logger.getLogger(SessionListener.class);
	
	public void sessionCreated(HttpSessionEvent event) {
		// nothing to do
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		logger.debug("IN");
		String attributeName = TemporaryTableRecorder.class.getName();
		TemporaryTableRecorder recorder = (TemporaryTableRecorder) event.getSession().getAttribute(attributeName);
		if (recorder != null) {
			Iterator<TemporaryTable> it = recorder.iterator();
			while (it.hasNext()) {
				TemporaryTable tt = it.next();
				TemporaryTableManager.removeLastDataSetSignature(tt.getTableName());
				TemporaryTableManager.removeLastDataSetTableDescriptor(tt.getTableName());
				try {
					TemporaryTableManager.dropTableIfExists(tt.getTableName(), tt.getDataSource());
					logger.debug(
							"Temporary table with name ["
									+ tt.getTableName() + "] at datasource ["
									+ tt.getDataSource() + "] dropped successfully");
				} catch (Exception e) {
					logger.error(
							"Error while dropping temporary table with name ["
									+ tt.getTableName() + "] at datasource ["
									+ tt.getDataSource() + "]", e);
					continue;
				}
			}
		}
		logger.debug("OUT");
	}

}
