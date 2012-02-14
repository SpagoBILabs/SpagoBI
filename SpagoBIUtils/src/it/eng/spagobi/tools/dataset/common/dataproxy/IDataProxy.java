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
package it.eng.spagobi.tools.dataset.common.dataproxy;

import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;

import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IDataProxy {
	
	IDataStore load(IDataReader dataReader);
	
	// for querable dataset...
	public String getStatement();
	public void setStatement(String statement);
	
	public String getResPath();
	public void setResPath(String resPath);
	
	public String getPredefinedGroovyScriptFileName();
	public void setPredefinedGroovyScriptFileName(String predefinedGroovyScriptFileName) ;
	public String getPredefinedJsScriptFileName() ;
	public void setPredefinedJsScriptFileName(String predefinedJsScriptFileName);
	
	// pagination ...
	boolean isPaginationSupported();
	boolean isOffsetSupported();
	int getOffset();
	void setOffset(int offset);
	boolean isFetchSizeSupported();
	int getFetchSize();
	void setFetchSize(int fetchSize);
	boolean isMaxResultsSupported();
	int getMaxResults();
	void setMaxResults(int maxResults);
	
	/**
	 * Return if the calculation of the total result number is enabled or not (may be it is not necessary)
	 */
	boolean isCalculateResultNumberOnLoadEnabled();
	
	/**
	 * Set if the calculation of the total result number is enabled or not.
	 * In case this calculation is required, invoke this method with true, otherwise with false.
	 */
	void setCalculateResultNumberOnLoad(boolean enabled);
	long getResultNumber();
	
	// profilation ...
	Map getParameters();
	void setParameters(Map parameters);
	Map getProfile();
	void setProfile(Map profile);
}
