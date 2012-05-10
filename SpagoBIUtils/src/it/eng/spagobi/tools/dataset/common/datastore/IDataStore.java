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
package it.eng.spagobi.tools.dataset.common.datastore;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;

/**
 * @authors Angelo Bernabei (angelo.bernabei@eng.it)       
 *          Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IDataStore {

	IMetaData getMetaData();
	
	Iterator iterator();    
	boolean isEmpty();
	long getRecordsCount();
	
    IRecord getRecordAt(int i);
    IRecord getRecordByID(Object value);
    
    List<IRecord> findRecords(int fieldIndex, Object fieldValue) ;
    List<IRecord> findRecords(final List fieldIndexes, final List fieldValues) ;
    List<IRecord> findRecords(IRecordMatcher matcher);
    
    List getFieldValues(int fieldIndex);    
    Set getFieldDistinctValues(int fieldIndex);
    
    void sortRecords(int fieldIndex);    
    void sortRecords(int fieldIndex, Comparator filedComparator);    
    void sortRecords(Comparator recordComparator);
        
    void appendRecord(IRecord r);
    void prependRecord(IRecord record);
	void insertRecord(int recordIndex, IRecord record);
    
	/**
	 * @deprecated use the proper DataWriter instead
	 */
    String toXml();
    
    /**
	 * @deprecated use the proper DataWriter instead
	 */
    SourceBean toSourceBean() throws SourceBeanException;
}
