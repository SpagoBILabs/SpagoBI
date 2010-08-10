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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Monia Spinelli (monia.spinelli@eng.it)
 */

public class Estensione
{	 
	//il path indica da dove iniziare la ricerca dei file
    public static void makeDir(String path, String copyPath) throws IOException
    {
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
   	   			FileExtFilter	fef1 = new FileExtFilter ("properties", ExtractAllFile.REFERENCE_LANGUAGE_JS, ExtractAllFile.REFERENCE_LANGUAGE_P);
   	   			FileExtFilter	fef2 = new FileExtFilter ("js", ExtractAllFile.REFERENCE_LANGUAGE_JS, ExtractAllFile.REFERENCE_LANGUAGE_P);
    		
    			//lista dei File properties
    			String[] list1 = dir.list (fef1);
    			
    			//lista dei File js
    			String[] list2 = dir.list (fef2);
    		
    			//Lista di tutte le directory e file diversi da properties
    			String[] as = dir.list();
    	        
    			/*-- elenca tutti i file con estensione properties --*/
    			for (int i = 0; i < list1.length; i++)
    			{
    				String subPath = newPath.substring(24);
    				String copyDir = copyPath+subPath;
    				File f1 = new File(copyDir);
    				f1.mkdirs();
    				DiffAllFile.diffFile(subPath, list1[i], newPath);
    				File inputFile = new File(dir+"/"+list1[i]);
    				File outputFile = new File(copyDir+"/"+list1[i]);
    				InputStream finput;
    				finput = new BufferedInputStream(new FileInputStream(inputFile));
    				OutputStream foutput;
    				foutput = new BufferedOutputStream( new FileOutputStream(outputFile));
    				byte[] buffer = new byte[1024 * 500];
    				int bytes_letti = 0;
    				while((bytes_letti = finput.read(buffer)) > 0)
    					foutput.write(buffer, 0, bytes_letti);
    				finput.close();
    				foutput.close();
    			}
    			
    			/*-- elenca tutti i file con estensione js --*/
    			for (int i = 0; i < list2.length; i++)
    			{
    				String subPath = newPath.substring(24);
    				if(newPath.contains("locale"))
    				{
    					String copyDir = copyPath+subPath;
    					File f1 = new File(copyDir);
    					f1.mkdirs();
    					DiffAllFile.diffFile(subPath, list2[i], newPath);
    					File inputFile = new File(dir+"/"+list2[i]);
    					File outputFile = new File(copyDir+"/"+list2[i]);
    					InputStream finput;
    					finput = new BufferedInputStream(new FileInputStream(inputFile));
    					OutputStream foutput;
    					foutput = new BufferedOutputStream( new FileOutputStream(outputFile));
    					byte[] buffer = new byte[1024 * 500];
    					int bytes_letti = 0;
    					while((bytes_letti = finput.read(buffer)) > 0)
    						foutput.write(buffer, 0, bytes_letti);
    					finput.close();
    					foutput.close();
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

class FileExtFilter implements FilenameFilter
{
	
	private String estensione;

	//Controlla qual è l'estensione del file e che sia quello della lingua di interesse
    public FileExtFilter (String estensione, String REFERENCE_LANGUAGE_JS, String REFERENCE_LANGUAGE_P)
    {
    	if("js".equals(estensione))
    		this.estensione = REFERENCE_LANGUAGE_JS+"." + estensione;
    	else
    		this.estensione = REFERENCE_LANGUAGE_P+"." + estensione;
    }

    public boolean accept (File dir, String name)
    {
    	return name.endsWith (estensione);
    }
    
   
}

class FileExtFilterExt implements FilenameFilter
{
	private String estensione;

	//Controlla qual è l'estensione del file e che sia quello della lingua di interesse
    public FileExtFilterExt (String estensione)
    {
    	this.estensione = "." + estensione;
    }

    public boolean accept (File dir, String name)
    {
    	return name.endsWith (estensione);
    }
    
   
}
