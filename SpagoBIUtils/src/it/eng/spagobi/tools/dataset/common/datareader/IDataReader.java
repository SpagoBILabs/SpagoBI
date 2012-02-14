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
package it.eng.spagobi.tools.dataset.common.datareader;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public interface IDataReader {
    IDataStore read( Object data )throws EMFUserError, EMFInternalError;
    
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
	public boolean isCalculateResultNumberEnabled();
	
	/**
	 * Set if the calculation of the total result number is enabled or not.
	 * In case this calculation is required, invoke this method with true, otherwise with false.
	 */
	public void setCalculateResultNumberEnabled(boolean enabled);
	
}
