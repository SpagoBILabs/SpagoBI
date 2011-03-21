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
public abstract class AbstractModelNode extends AbstractModelObject implements IModelNode {
	
	protected IModelStructure structure;
	protected IModelEntity parent;	
	
	
	
	public IModelStructure getStructure() {
		return structure;
	}

	protected void setStructure(IModelStructure structure) {
		this.structure = structure;
	}
	
	public IModelEntity getParent() {
		return parent;
	}
	
	public void setParent(IModelEntity parent) {
		this.parent = parent;
	}
	
	
	
	
	
	
}
