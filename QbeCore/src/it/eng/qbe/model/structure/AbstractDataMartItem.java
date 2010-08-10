/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.model.structure;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractDataMartItem implements IDataMartItem {
	
	private DataMartModelStructure structure;
	private DataMartEntity parent;	
	
	private long id;	
	private String name;
	
	
	
	public DataMartModelStructure getStructure() {
		return structure;
	}

	protected void setStructure(DataMartModelStructure structure) {
		this.structure = structure;
	}
	
	public DataMartEntity getParent() {
		return parent;
	}
	
	public void setParent(DataMartEntity parent) {
		this.parent = parent;
	}
	
	public long getId() {
		return id;
	}
	
	protected void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
}
