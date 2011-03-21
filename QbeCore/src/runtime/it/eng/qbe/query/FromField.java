/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.query;

import it.eng.qbe.model.structure.IModelEntity;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class FromField {
	private IModelEntity dataMartEntity;
	private String alias;
	
	
	
	public FromField(IModelEntity dataMartEntity) {
		setDataMartEntity( dataMartEntity );
	}

	public IModelEntity getDataMartEntity() {
		return dataMartEntity;
	}

	public void setDataMartEntity(IModelEntity dataMartEntity) {
		this.dataMartEntity = dataMartEntity;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
