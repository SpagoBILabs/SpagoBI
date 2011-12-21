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
