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
