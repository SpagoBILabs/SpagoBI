/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.conf;

import it.eng.spago.configuration.ConfigSingleton;

import java.io.File;


/**
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeCoreSettings {
	
	private File qbeDataMartDir;

	
	/** The free space lb limit. */
	private Long freeSpaceLbLimit = null;
	
	
	
	
	private File qbeTempDir = null;
	
	
	
	static private QbeCoreSettings instance = null;
	

	static public QbeCoreSettings getInstance() {
		if(instance == null) instance = new QbeCoreSettings();
		return instance;
	}
	
	
	private QbeCoreSettings() {	
		setQbeDataMartDir( new File("resources/datamarts") );
	}
	
	

	public File getQbeDataMartDir() {
		return qbeDataMartDir;
	}
	
	public void setQbeDataMartDir(File qbeDataMartDir) {
		this.qbeDataMartDir = qbeDataMartDir;
	}


	/**
	 * Gets the free space lb limit.
	 * 
	 * @return the free space lb limit
	 */
	public long getFreeSpaceLbLimit() {
		if(freeSpaceLbLimit == null) {
			String freeSpaceLbLimitStr = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE-CHECK-SPACE-BEFORE-CREATEVIEW.failIfSpaceLess");
			freeSpaceLbLimit = new Long(freeSpaceLbLimitStr);
		}
		return freeSpaceLbLimit.longValue();
	}

	/**
	 * Sets the free space lb limit.
	 * 
	 * @param freeSpaceLbLimit the new free space lb limit
	 */
	public void setFreeSpaceLbLimit(long freeSpaceLbLimit) {
		this.freeSpaceLbLimit = new Long(freeSpaceLbLimit);
	}


	
	


	

	/**
	 * Gets the qbe temporary directory.
	 * 
	 * @return the qbe temporary directory
	 */
	public File getQbeTempDir() {
		if(qbeTempDir == null) {
			qbeTempDir = new File( System.getProperty("java.io.tmpdir") );
			if(!qbeTempDir.exists()) {
				qbeTempDir.mkdirs();
			}
		}
		
		return qbeTempDir;
	}


	/**
	 * Sets the qbe temporary directory.
	 * 
	 * @param qbeTempDir the new qbe temporary directory
	 */
	public void setQbeTempDir(File qbeTempDir) {
		this.qbeTempDir = qbeTempDir;
	}

	
	
}
