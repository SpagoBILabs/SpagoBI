/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * All nodes of a IModelStructure (i.e. entities & fields) implement this interface 
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IModelNode extends IModelObject{
	
	IModelStructure getStructure();
	IModelEntity getParent();
	/**
	 * Gets the parent of the node from the structure.
	 * The difference with getParent() is that if the parent 
	 * entity is a ModelView: getPathParent() returns the view. 
	 * getParent() returns the entity of the view that contains the node
	 */
	IModelEntity getPathParent();
	void setParent(IModelEntity parent);
	String getUniqueName();

}
