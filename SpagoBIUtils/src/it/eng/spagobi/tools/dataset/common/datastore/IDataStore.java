/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
