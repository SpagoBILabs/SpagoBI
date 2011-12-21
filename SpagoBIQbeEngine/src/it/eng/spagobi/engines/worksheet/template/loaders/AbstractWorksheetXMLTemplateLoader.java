/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.worksheet.template.loaders;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public abstract class AbstractWorksheetXMLTemplateLoader implements IWorksheetXMLTemplateLoader {
	
	IWorksheetXMLTemplateLoader nextLoader;
	
	public AbstractWorksheetXMLTemplateLoader() {}

	public AbstractWorksheetXMLTemplateLoader(IWorksheetXMLTemplateLoader loader) {
		setNextLoader(loader);
	}
	
	public SourceBean load(String rowData) {
		SourceBean result;
		
		try {
			// load data
			result = SourceBean.fromXMLString(rowData);
			result = this.load(result);
		}catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from rowData [" + rowData + "]", t);
		}
		
		return result;
	}
	
	public SourceBean load(SourceBean xml) {
		SourceBean result;
		
		try {
			result = this.convert(xml);
			// make next converts
			if (nextLoader != null) {
				result = nextLoader.load(result);
			}
		} catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from XML [" + xml + "]", t);
		}
		
		return result;
	}
	
	abstract public SourceBean convert(SourceBean xml);
	
	
	public IWorksheetXMLTemplateLoader getNextLoader() {
		return nextLoader;
	}

	void setNextLoader(IWorksheetXMLTemplateLoader nextLoader) {
		this.nextLoader = nextLoader;
	}
}
