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
import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;

public class MergeAllFile 
{
	//Valore di default di sovrascivi
	public static String sovrascrivi = "N";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Creo la directory vuota
		mergeFile(sovrascrivi);

	}
	
	public static void mergeFile(String scrivi) throws IOException
	{
		//path che contiene i file tradotti
		String path = "/home/spinelli/Scrivania/ToTranslate";
		
		//path della directory contenente le nuove versioni dei progetti
		String versPath = "/home/spinelli/Scrivania/progettiSpago";
	
		//Coda che conterrà tutte le directory da esplorare
		Queue<String> codaPath = new LinkedList<String>();
	
		//inserisco nella coda il path di partenza da cui iniziare la ricerca dei file
		codaPath.add(path);
	
		String percorso="";
	
		//Finchè ci sono path nella coda esploro le cartelle
		while(!codaPath.isEmpty())
		{	
			//Rimuovo il path dalla coda e cerco al suo interno i file properties
			String newPath = codaPath.remove();
		
				File dir = new File (newPath);
			
				if(dir.isDirectory())
				{
					//Cerco i file .properties e .js
					FileExtFilterExt	fef1 = new FileExtFilterExt ("properties");
					FileExtFilterExt	fef2 = new FileExtFilterExt ("js");
		
					//lista dei File properties
					String[] list1 = dir.list (fef1);
			
					//lista dei File js
					String[] list2 = dir.list (fef2);
		
					//Lista di tutte le directory e file diversi da properties
					String[] as = dir.list();
	        
					/*-- elenca tutti i file con estensione properties --*/
					for (int i = 0; i < list1.length; i++)
					{
						String subPath = newPath.substring(36);
						String versDir = versPath+subPath;
						//File f1 = new File(versDir);
						MergeSingleFile.mergeSingle(subPath, list1[i], newPath, scrivi);
					}
			
					/*-- elenca tutti i file con estensione js --*/
					for (int i = 0; i < list2.length; i++)
					{
						String subPath = newPath.substring(24);
						if(newPath.contains("locale"))
						{
							String versDir = versPath+subPath;
							//File f1 = new File(versDir);
							MergeSingleFile.mergeSingle(subPath, list2[i], newPath, scrivi);
						}
					}
    
					//tutti i file in directory
					for (int i = 0; i < as.length; i++)
					{    		
						if (!as[i].contains("."))
						{
							percorso = newPath+"/"+as[i];
							codaPath.add(percorso);
						}
					}
				}
		}
	}

}

