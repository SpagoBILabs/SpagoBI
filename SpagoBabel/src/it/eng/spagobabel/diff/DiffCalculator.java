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
package it.eng.spagobabel.diff;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DiffCalculator {
	public Properties getDiff(Set<String> keys, Map<String,String> targetLabelsMap, Map<String,String> referenceLabelsMap) {
		Properties diffMap;
		
		diffMap = new Properties();
		
		int count = 0;
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(!targetLabelsMap.containsKey( key )) {
				String value = (referenceLabelsMap.containsKey(key))? referenceLabelsMap.get(key): "?";
				diffMap.setProperty(key, value);
				count++;
			}
		}
		
		return diffMap;
	}
}
