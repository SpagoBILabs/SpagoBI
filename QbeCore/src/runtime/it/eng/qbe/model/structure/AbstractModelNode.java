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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


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
	
	/**
	 * Gets the parent of the node from the structure.
	 * The difference with getParent() is that if the parent 
	 * entity is a ModelView: getPathParent() returns the view. 
	 * getParent() returns the entity of the view that contains the node
	 */
	public IModelEntity getLogicalParent(){
		if(parent==null){
			return null;
		}
		String parentViewName = parent.getPropertyAsString("parentView");
		if(parentViewName != null) {
			return structure.getEntity(parentViewName);
		}else{
			return parent;
		}
	}
	
		
	protected List<ModelViewEntity> getParentViews(IModelEntity entity){
		List<ModelViewEntity> parentViews = new ArrayList<ModelViewEntity>();
		IModelEntity nextEntity;
		if(entity instanceof ModelViewEntity){
			parentViews.add((ModelViewEntity)entity);
			nextEntity = entity.getParent();
		}else{
			String parentViewName = entity.getPropertyAsString("parentView");
			if(parentViewName != null) {
				ModelViewEntity viewEntity = (ModelViewEntity)structure.getEntity(parentViewName);
				parentViews.add(viewEntity);
				nextEntity = viewEntity.getParent();
			}else{
				nextEntity = entity.getParent();
			}
		}
		if(nextEntity!=null){
			parentViews.addAll(getParentViews(nextEntity));
		}
		return parentViews;
	}
	
	
}
