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
import java.io.IOException;



public class CreateDir {
	
	public static final String TARGET_LANGUAGE_JS = "fr";
	public static final String REFERENCE_LANGUAGE_JS = "en";
	public static final String TARGET_LANGUAGE_P = "FR";
	public static final String REFERENCE_LANGUAGE_P = "US";
	
	//Assegna la nomenclatura standard ai file .js o . properties che conterranno le lable da tradurre
	public static String getName(String fileName)
	{
		if(fileName.contains("js")) 
		{
			String name = fileName.replace(REFERENCE_LANGUAGE_JS, TARGET_LANGUAGE_JS);
			
			return name;
		}
		else
		{
			String name = fileName.replace(REFERENCE_LANGUAGE_P, TARGET_LANGUAGE_P);
			name = name.replace(REFERENCE_LANGUAGE_JS,TARGET_LANGUAGE_JS);
			return name;
		}
	}
}
