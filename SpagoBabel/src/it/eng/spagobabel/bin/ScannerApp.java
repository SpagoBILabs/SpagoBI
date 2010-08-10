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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import it.eng.spagobabel.scanner.IScanner;
import it.eng.spagobabel.scanner.JSFileScanner;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */

//Secondo
//Prende in input l'output di FileSetApp e crea labels.txt
public class ScannerApp {
	
	//Lista dei file che devono essere scannerizzati
	public static final String DEFAULT_IN = "out.txt";
	
	//label del contenuto del file javascript
	public static final String DEFAULT_OUT = "labels.txt";
	
	
	public static String getArg(String[] args, int i, String defVal) {
		if(args.length <= i) {
			return defVal;
		} 
		
		return args[i];
	}
	
	public static void main(String[] args) throws IOException {
		File inputFile, outputFile;
		BufferedReader reader;
		PrintWriter writer;
		String line;
		IScanner scanner;
			
		inputFile = new File(getArg(args, 0, DEFAULT_IN));
		outputFile = new File(getArg(args, 1, DEFAULT_OUT));
				
		scanner = new JSFileScanner();
		writer = new PrintWriter(new FileWriter(outputFile));
		
		reader = new BufferedReader( new FileReader(inputFile));
		
		while( (line = reader.readLine()) != null) {
			
			System.out.println(line);
			
			File f = new File(line.trim());
			
			//Crea un set di risultati a partire dai file presenti nel percorso di SpagoBI
			Set results = scanner.scan(f);
			
			writer.println("");
			writer.println("//===============================================================================================================");
			writer.println("// file: " + f.toString());
			writer.println("// labels: " + results.size());
			writer.println("//===============================================================================================================");
			writer.println("");
			
			Iterator it = results.iterator();
			
			while( it.hasNext() ) {
				writer.println(it.next());
			}
		}
		
		reader.close();
		writer.flush();
		writer.close();
	}

}
