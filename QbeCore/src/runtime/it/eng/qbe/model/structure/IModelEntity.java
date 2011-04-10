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

import java.util.Iterator;
import java.util.List;

public interface IModelEntity extends IModelNode {
	
		public IModelEntity getRoot();		
		public String getType();
		public String getUniqueType();
		public String getRole();
		
		public List<IModelField> getAllFields();
		public IModelField getField(String fieldName);
		public List<IModelField> getFieldsByType(boolean isKey);
		public List<IModelField> getKeyFields() ;
		public Iterator<IModelField> getKeyFieldIterator();
		public List<IModelField> getNormalFields();
		public Iterator<IModelField> getNormalFieldIterator();
		public List<ModelCalculatedField> getCalculatedFields();
		
		public IModelEntity getSubEntity(String entityUniqueName);
		public List<IModelEntity> getSubEntities() ;
		public List<IModelEntity> getAllSubEntities() ;
		public List<IModelEntity> getAllSubEntities(String entityName);
		
		
		
		
		public List<IModelField> getAllFieldOccurencesOnSubEntity(String entityName, String fieldName);
		public String toString();
		public String getPath();
		
		public IModelField addNormalField(String fieldName);
		public IModelField addKeyField(String fieldName);

		public void addCalculatedField(ModelCalculatedField calculatedField);
		public void deleteCalculatedField(String fieldName);
		
		public IModelEntity addSubEntity(String subEntityName, String subEntityRole, String subEntityType) ;
		public void addSubEntity(IModelEntity entity) ;
	
		public void setPath(String path);
		
		public void setRole(String role);
	
		public void setType(String type);
}
