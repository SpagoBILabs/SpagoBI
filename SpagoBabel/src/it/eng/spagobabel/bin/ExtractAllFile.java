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
 * @author Monia Spinelli (monia.spinelli@eng.it)
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import it.eng.spagobabel.bin.Estensione;

public class ExtractAllFile {
	
	public static final String TARGET_LANGUAGE_JS = "fr";
	public static final String REFERENCE_LANGUAGE_JS = "en";
	public static final String TARGET_LANGUAGE_P = "FR";
	public static final String REFERENCE_LANGUAGE_P = "US";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Creo la directory vuota
		creaDir();

	}
	
	//Passa il nome della directory vuota in cui saranno estratti i file
	private static void creaDir() throws IOException
	{
	  String Dir = "/home/spinelli/Scrivania/newVers";
	  (new File(Dir)).mkdirs();
    
	  //Leggo tutti i file con estensione .properties dalla directory progettiSpago
	  Estensione.makeDir("/home/spinelli/Scrivania/progettiSpago", Dir);
	    
	}

}
