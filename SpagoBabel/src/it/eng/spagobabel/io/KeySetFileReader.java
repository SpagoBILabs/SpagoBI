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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class KeySetFileReader {
	public KeySetFileReader() {
		
	}
	
	
	public Set<String> read() throws IOException {
		return read(new File("."));
	}
	public Set<String> read(File file) throws IOException {
		Set<String> keySet;
		BufferedReader reader;
		String line;
	
		if(file.isDirectory()) {
			file = new File(file, "labels.txt");
		}
		
		keySet = new LinkedHashSet();
		
		reader = new BufferedReader( new FileReader(file));
	
		int count = 0;
		while( (line = reader.readLine()) != null) {
			if(line.startsWith("//") || line.trim().length() == 0) continue;
			keySet.add( line.trim() );
			count++;
		}
		
		System.out.println("XXX -> count: " + count);
		
		return keySet;
		
	}
}
