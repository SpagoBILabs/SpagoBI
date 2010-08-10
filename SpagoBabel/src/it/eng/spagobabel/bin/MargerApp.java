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
package it.eng.spagobabel.bin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import it.eng.spagobabel.io.JSBoundleReader;
import it.eng.spagobabel.io.JSBoundleWriter;
import it.eng.spagobabel.marger.Marger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class MargerApp extends AbstractApp {
	

	public static void main(String[] args) throws IOException {
		File boundlesDir, diffFile, ouputDir;
		String targetLanguage;
	
			
		boundlesDir = new File(getArg(args, 0, BOUNDLES_DIR));
		targetLanguage = getArg(args, 1, TARGET_LANGUAGE);
		
		ouputDir = new File(getArg(args, 2, OUTPUT_DIR));
		
		diffFile = new File(targetLanguage + "-diff.properties");
		
		JSBoundleReader boundleReader = new JSBoundleReader();
		Map targetLabelsMap = boundleReader.read(boundlesDir, targetLanguage);
		
		Properties diffProps = new Properties();
		diffProps.load(new FileInputStream(diffFile));

		Marger marger = new Marger();
		Map margedLabelsMap = marger.marge(targetLabelsMap, diffProps);
				
		JSBoundleWriter boundleWriter = new JSBoundleWriter();
		boundleWriter.write(margedLabelsMap, ouputDir, targetLanguage);

	}

}
