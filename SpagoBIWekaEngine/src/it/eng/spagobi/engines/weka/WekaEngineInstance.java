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
package it.eng.spagobi.engines.weka;

import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.utilities.engines.EngineAnalysisMetadata;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngineInstance implements IEngineInstance {
	
	Map env;
	String template;
	
	
	private static transient Logger logger = Logger.getLogger(WekaEngineInstance.class);

	public WekaEngineInstance(String template, Map env) {
		this.env = env;
		this.template = template;	
	}
	
	// -----------------------------------------------------------------------
	// ACCESSOR METHODS
	// -----------------------------------------------------------------------
	
	public EngineAnalysisMetadata getAnalysisMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		return null;
	}
	
	public IEngineAnalysisState getAnalysisState() {
		return null;
	}

	public String getTemplate() {
		return  replaceParameters(template, getEnv());
	}
	
	public Map getEnv() {
		return env;
	}

	

	public void setAnalysisMetadata(EngineAnalysisMetadata analysisMetadata) {
		// TODO Auto-generated method stub
	}

	public void setAnalysisState(IEngineAnalysisState analysisState) {
		// TODO Auto-generated method stub	
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setEnv(Map env) {
		this.env = env;
		
	}

	public void validate() throws SpagoBIEngineException {
		// TODO Auto-generated method stub
	}
	
	public static String replaceParameters(String template, Map params)  {
		
		String result = new String(template);

		int index = -1;
		while( (index = result.indexOf("$P{")) != -1) {
			String pname = result.substring(index + 3, result.indexOf("}"));
			
			result = result.substring(0, index) 
				+ params.get(pname) 
				+ result.substring(result.indexOf("}") + 1 , result.length());
		}
	
		return result;
	}
}
