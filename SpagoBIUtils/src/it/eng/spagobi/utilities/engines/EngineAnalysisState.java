/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
package it.eng.spagobi.utilities.engines;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class EngineAnalysisState implements IEngineAnalysisState {
	
	Map properties;
	
	/**
	 * Instantiates a new engine analysis state.
	 * 
	 * @param rowData the row data
	 */
	public EngineAnalysisState() {
		properties = new HashMap();
	}
	

	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineAnalysisState#getProperty(java.lang.Object)
	 */
	public Object getProperty(Object pName) {
		return properties.get( pName ); 
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineAnalysisState#setProperty(java.lang.Object, java.lang.Object)
	 */
	public void setProperty(Object pName, Object pValue) {
		properties.put( pName, pValue ); 
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineAnalysisState#containsProperty(java.lang.Object)
	 */
	public boolean containsProperty(Object pName) {
		return properties.containsKey( pName ); 
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.IEngineAnalysisState#propertyNameSet()
	 */
	public Set propertyNameSet() {
		return properties.keySet(); 
	}
	
	public String toString() {
		StringBuffer buffer = null;
		Iterator it = null;
		
		buffer = new StringBuffer();
		it = propertyNameSet().iterator();
		while( it.hasNext() ) {
			Object pName = it.next();
			Object pValue = getProperty( pName );
			buffer.append( pName.toString() + "=" + pValue.toString() + "; ");
		}
		
		return buffer.toString();
	}
	
}
