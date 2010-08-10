/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datastore;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;

/**
 * @authors Angelo Bernabei (angelo.bernabei@eng.it)       
 *          Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IDataStore {

	IDataStoreMetaData getMetaData();
	
	Iterator iterator();    
	boolean isEmpty();
	long getRecordsCount();
	
    IRecord getRecordAt(int i);
    IRecord getRecordByID(Object value);
    
    List findRecords(int fieldIndex, Object fieldValue) ;
    List findRecords(final List fieldIndexes, final List fieldValues) ;
    List findRecords(IRecordMatcher matcher);
    
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
