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
package it.eng.spagobi.engines.qbe.tree.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.eng.qbe.model.IDataMartModel;
import it.eng.qbe.model.structure.DataMartEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class QbeTreeOrderEntityFilter.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeTreeBlackListEntityFilter extends ComposableQbeTreeEntityFilter{

	private Set blackList;
		
	public QbeTreeBlackListEntityFilter() {
		super();
	}
	
	public QbeTreeBlackListEntityFilter(IQbeTreeEntityFilter parentFilter, Set blackList) {
		super(parentFilter);
		this.setBlackList( blackList );
	}
	
	public List filter(IDataMartModel datamartModel, List entities) {
		List list = null;
		DataMartEntity entity;

		list = new ArrayList();
		for(int i = 0; i < entities.size(); i++) {
			entity = (DataMartEntity)entities.get(i);
			if(!getBlackList().contains(entity)) {
				list.add(entity);
			}
		}
		
		return list;
	}
	
	public Set getBlackList() {
		return blackList;
	}

	public void setBlackList(Set blackList) {
		this.blackList = blackList;
	}
	
}
