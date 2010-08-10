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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSBoundleReader {
	
	Pattern stringPattern;
	
	private static final String stringRegExp = "'.*'";
	
	public JSBoundleReader() {
		stringPattern = Pattern.compile(stringRegExp);
	}
	
	public boolean skipLine(String line) {
		return !line.contains("Sbi.locale.ln[");
	}
	
	
	public Map read(File dir, String language) throws IOException {
		return read(new File(dir, language + ".js"));
	}
	
	
	public Map read(File file) throws IOException {
		
		Map<String, String> map;
		
		String line;
		BufferedReader reader;
		Matcher stringMatcher;
		
		map = new HashMap<String, String>();
		
		if(!file.exists()) return map;
		
		reader = new BufferedReader( new FileReader(file));
		
		
		while( (line = reader.readLine()) != null) {
			if( skipLine(line) ) continue;
			System.out.println(line);
			String key = null;
			String value = null;
			
			int splitIndex = line.indexOf("=");
			String lOperand = line.substring(0, splitIndex);
			String rOperand = line.substring(splitIndex + 2);
		
			stringMatcher = stringPattern.matcher(lOperand);
			if(stringMatcher.find()) {
				key = stringMatcher.group();
				key = key.substring(1, key.length()-1);
			}
			
			stringMatcher = stringPattern.matcher(rOperand);
			if(stringMatcher.find()) {
				value = stringMatcher.group();
				value = value.substring(1, value.length()-1);
			}
			
			//System.out.println("--> "  + lOperand  + " = " + key + " ; " + rOperand + " = " + value);
			
			map.put(key, value);			
		}
		
		return map;
	}
	
	
}
