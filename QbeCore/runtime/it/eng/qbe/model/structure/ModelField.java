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
package it.eng.qbe.model.structure;


/**
 * @author Andrea Gioia
 */
public class ModelField extends AbstractModelNode {
		
	private boolean key;
	private String type;
	private int length;
	private int precision;
	
	//private String datamartName;	
	
	protected ModelField() {
		// in order to let subclass in this package to relax constraints imposed by the public constructor
		// DataMartField(String name, DataMartEntity parent). Ex. DataMartCalculatedField
		// can be created by themself without a pre-existing parent entity.
		initProperties();
	}
	
	public ModelField(String name, ModelEntity parent) {
		setStructure(parent.getStructure());
		setId( getStructure().getNextId() );		
		setName(name);
		setParent(parent);
		initProperties();		
	}

	public String getUniqueName() {
		if(getParent().getParent() == null) {
			return getParent().getType() + ":" + getName();
		}
		return getParent().getUniqueName() + ":" + getName();
	}
	
	public String getQueryName() {
		String fieldName = "";
		
		IModelEntity entity = getParent();
		if(entity.getParent() != null) {
			fieldName = toLowerCase( entity.getName() );
			entity = entity.getParent();
		}
		while(entity.getParent() != null) {
			fieldName = toLowerCase( entity.getName() ) + "." + fieldName;
			entity = entity.getParent();
		}		
		if(!fieldName.equalsIgnoreCase("")) fieldName +=  ".";
		fieldName += getName();
		
		return fieldName;
	}
	
	private String toLowerCase(String str) {
		/*
		String head = str.substring(0,1);
		String tail = str.substring(1, str.length());
		
		return head.toLowerCase() + tail;
		*/
		return str;
	}
		
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}
	
	public String toString() {
		return getName() + "(id="+getId()
		+"; parent:" + (getParent()==null?"NULL": getParent().getName())
		+"; type="+type
		+"; length="+length
		+"; precision="+precision
		+")";
	}








	/*
	public String getDatamartName() {
		return datamartName;
	}


	public void setDatamartName(String datamartName) {
		this.datamartName = datamartName;
	}
	*/


	
}
