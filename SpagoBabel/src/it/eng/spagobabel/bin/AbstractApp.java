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

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractApp {
	
	public static final String LABELS_FILE = "labels.txt";
	public static String BOUNDLES_DIR = "/home/spinelli/Scrivania/";
	public static final String TARGET_LANGUAGE = "fr";
	public static final String REFERENCE_LANGUAGE = "en";
	public static final String OUTPUT_DIR = ".";
	
	
	public static String getArg(String[] args, int i, String defVal) {
		if(args.length <= i) {
			return defVal;
		} 
		
		return args[i];
	}
	
	public static void setBOUNDLES_DIR(String path)
	{
		BOUNDLES_DIR = path;
	}
	
}
