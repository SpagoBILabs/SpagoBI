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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import it.eng.spagobabel.fileset.FileSet;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */

//Primo

//Crea un file out.txt che contiene il nome dei file
public class FileSetApp {
	//Parametro di input
	public static final String DEFAULT_ROOT = "/home/spinelli/Scrivania";
	
	//Parametro di output
	public static final String DEFAULT_OUT = "out.txt";

	public static String getArg(String[] args, int i, String defVal) {
		if(args.length <= i) {
			return defVal;
		} 
		
		return args[i];
	}
	
	public static void main(String[] args) throws IOException {
		FileSet fileSet;
		File rootFile, outputFile;
		Iterator<File> fileIterator;
		PrintWriter writer;
		
		rootFile = new File(getArg(args, 0, DEFAULT_ROOT));
		outputFile = new File(getArg(args, 1, DEFAULT_OUT));
		
		fileSet = new FileSet(rootFile);		
		writer = new PrintWriter(new FileWriter(outputFile));
		
		fileIterator = fileSet.getFiles().iterator();
		while(fileIterator.hasNext()) {
			File f = fileIterator.next();
			writer.println(f.toString());
		}
		
		writer.flush();
		writer.close();
	}

}
