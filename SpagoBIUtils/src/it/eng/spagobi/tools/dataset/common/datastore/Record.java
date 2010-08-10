/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class Record implements IRecord,Serializable {

	IDataStore dataStore;
	List fields = new ArrayList();

	public Record() {
		super();
		this.fields = new ArrayList();
	}
	  
    public Record(IDataStore dataStore) {
		super();
		this.fields = new ArrayList();
		this.setDataStore(dataStore);
	}


	public IField getFieldAt(int position) {
		return (IField)fields.get(position);  	
    }
	
	public void appendField(IField field) {    	
		fields.add(field);	
    }
	
	public void insertField(int fieldIndex, IField field) {    	
		fields.add(fieldIndex, field);	
    }


   


	public List getFields() {
		return this.fields;
	}


	public void setFields(List fields) {
		this.fields = fields;
	}


	public IDataStore getDataStore() {
		return dataStore;
	}


	public void setDataStore(IDataStore dataStore) {
		this.dataStore = dataStore;
	}
	
	public String toString() {
		return "" + getFields().toString();
	}

}
