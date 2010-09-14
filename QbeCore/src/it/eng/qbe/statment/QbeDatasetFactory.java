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
package it.eng.qbe.statment;

import it.eng.qbe.statment.hibernate.HQLDataSet;
import it.eng.qbe.statment.hibernate.HQLStatement;
import it.eng.qbe.statment.jpa.JPQLDataSet;
import it.eng.qbe.statment.jpa.JPQLStatement;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeDatasetFactory {
	IDataSet createDataSet(IStatement statement) {
		IDataSet dataSet;
		
		dataSet = null;
		if(statement instanceof HQLStatement) {
			dataSet = new HQLDataSet( statement );
		} else if(statement instanceof JPQLStatement) {
			dataSet = new JPQLDataSet( statement );
		} else {
			throw new RuntimeException("Impossible to create dataset from a statement of type [" + statement.getClass().getName() + "]");
		}
		
		return dataSet;
	}
}
