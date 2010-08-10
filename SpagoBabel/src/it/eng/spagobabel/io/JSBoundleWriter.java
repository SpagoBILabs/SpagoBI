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
package it.eng.spagobabel.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSBoundleWriter {
	
	public void write(Map labelsMap, File dir, String language) throws IOException {
		write(labelsMap, new File(dir, language + ".js"));
	}
	
	public void write(Map labelsMap, File file) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		Iterator it = labelsMap.keySet().iterator();
		while( it.hasNext() ) {
			String key = (String)it.next();
			String value = (String)labelsMap.get(key);
			writer.println("Sbi.locale.ln['" + key + "'] = '" + value + "';");
		}
		
		writer.flush();
		writer.close();
	}
}
