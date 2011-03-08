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
package it.eng.spagobi.engines.qbe.analysisstateloaders.formbuilder;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.json.JSONObject;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public abstract class AbstractFormStateLoader implements IFormStateLoader {
	
	IFormStateLoader nextLoader;
	
	public AbstractFormStateLoader() {}

	public AbstractFormStateLoader(IFormStateLoader loader) {
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
	
	
	public IFormStateLoader getNextLoader() {
		return nextLoader;
	}

	void setNextLoader(IFormStateLoader nextLoader) {
		this.nextLoader = nextLoader;
	}
}
