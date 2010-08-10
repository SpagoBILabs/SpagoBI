/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datastore;

import java.util.List;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public interface IRecord {

    IField getFieldAt(int fieldIndex);
    
    public void appendField(IField field) ;
    public void insertField(int fieldIndex, IField field) ;
    
    public List getFields();
	public void setFields(List fields);
	
	public IDataStore getDataStore();
    
}
