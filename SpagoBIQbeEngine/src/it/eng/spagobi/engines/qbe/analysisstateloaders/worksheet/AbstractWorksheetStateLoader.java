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
package it.eng.spagobi.engines.qbe.analysisstateloaders.worksheet;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.json.JSONObject;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public abstract class AbstractWorksheetStateLoader implements IWorksheetStateLoader {
	
	IWorksheetStateLoader nextLoader;
	
	public AbstractWorksheetStateLoader() {}

	public AbstractWorksheetStateLoader(IWorksheetStateLoader loader) {
		setNextLoader(loader);
	}
	
	public JSONObject load(String rowData) {
		JSONObject result;
		
		try {
			// load data
			result = new JSONObject(rowData);
			result = this.load(result);
		}catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from rowData [" + rowData + "]", t);
		}
		
		return result;
	}
	
	public JSONObject load(JSONObject jsonObject) {
		JSONObject result;
		
		try {
			result = this.convert(jsonObject);
			// make next converts
			if (nextLoader != null) {
				result = nextLoader.load(result);
			}
		} catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from JSON object [" + jsonObject + "]", t);
		}
		
		return result;
	}
	
	abstract public JSONObject convert(JSONObject data);
	
	
	public IWorksheetStateLoader getNextLoader() {
		return nextLoader;
	}

	void setNextLoader(IWorksheetStateLoader nextLoader) {
		this.nextLoader = nextLoader;
	}
}
