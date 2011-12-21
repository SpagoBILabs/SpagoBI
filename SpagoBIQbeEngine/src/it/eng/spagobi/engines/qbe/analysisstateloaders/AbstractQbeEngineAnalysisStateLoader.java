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
package it.eng.spagobi.engines.qbe.analysisstateloaders;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractQbeEngineAnalysisStateLoader implements IQbeEngineAnalysisStateLoader{
	
	IQbeEngineAnalysisStateLoader previousLoader;
	
	public AbstractQbeEngineAnalysisStateLoader() {}

	public AbstractQbeEngineAnalysisStateLoader(IQbeEngineAnalysisStateLoader loader) {
		setPreviousLoader(loader);
	}
	
	public JSONObject load(String rowData) {
		JSONObject result;
		
		try {
			result = previousLoader != null? (JSONObject)previousLoader.load(rowData): new JSONObject(rowData);
			result = this.convert(result);
		}catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from rowData [" + rowData + "]", t);
		}
		
		return result;
	}
	
	abstract public JSONObject convert(JSONObject data);
	
	
	public IQbeEngineAnalysisStateLoader getPreviousLoader() {
		return previousLoader;
	}

	void setPreviousLoader(IQbeEngineAnalysisStateLoader previousLoader) {
		this.previousLoader = previousLoader;
	}
}
