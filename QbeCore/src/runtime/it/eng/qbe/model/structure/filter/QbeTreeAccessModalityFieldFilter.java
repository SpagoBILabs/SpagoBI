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
package it.eng.qbe.model.structure.filter;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.IModelField;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class QbeTreeAccessModalityFieldFilter.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeTreeAccessModalityFieldFilter extends ComposableQbeTreeFieldFilter {
	
	/** The parent filter. */
	private IQbeTreeFieldFilter parentFilter;
	
	/**
	 * Instantiates a new qbe tree access modality field filter.
	 */
	public QbeTreeAccessModalityFieldFilter() {
		parentFilter = null;
	}
	
	/**
	 * Instantiates a new qbe tree access modality field filter.
	 * 
	 * @param parentFilter the parent filter
	 */
	public QbeTreeAccessModalityFieldFilter(IQbeTreeFieldFilter parentFilter) {
		setParentFilter(parentFilter);
	}
	
	
	public List filter(IDataSource dataSource, List fields) {
		List list;
		IModelField field;
		
		list = new ArrayList();
		
		for(int i = 0; i < fields.size(); i++) {
			field = (IModelField)fields.get(i);
			if( isFieldVisible(dataSource, field)) {
				list.add(field);
			}
		}
		
		return list;
	}
	
	/**
	 * Checks if is field visible.
	 * 
	 * @param datamartModel the datamart model
	 * @param field the field
	 * 
	 * @return true, if is field visible
	 */
	private boolean isFieldVisible(IDataSource dataSource, IModelField field) {
		//DatamartProperties qbeProperties = dataSource.getDataMartProperties();
		
		if( !field.getPropertyAsBoolean("visible") ) return false;
		if( !dataSource.getModelAccessModality().isFieldAccessible( field ) )return false;
		return true;
	}
}
