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
package it.eng.spagobi.engines.worksheet.template;

import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorksheetTemplate {

	IDataSet dataSet;
	WorkSheetDefinition workSheetDefinition;
	QbeEngineInstance qbEngineInstance;
	
	public IDataSet getDataSet() {
		return dataSet;
	}
	public void setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet;
	}
	public WorkSheetDefinition getWorkSheetDefinition() {
		return workSheetDefinition;
	}
	public void setWorkSheetDefinition(WorkSheetDefinition workSheetDefinition) {
		this.workSheetDefinition = workSheetDefinition;
	}
	public QbeEngineInstance getQbeEngineInstance() {
		return qbEngineInstance;
	}
	public void setQbeEngineInstance(QbeEngineInstance qbEngineInstance) {
		this.qbEngineInstance = qbEngineInstance;
	}
	
	
}
