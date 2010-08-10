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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiffAllFile {
		
	public static void diffFile(String subPath, String fileName, String newPath) throws IOException
	{	
		String home = "/home/spinelli/Scrivania";
		
		//Path della cartella che contiene la vecchia versione dei progetti
		String oldPath = "/home/spinelli/Scrivania/OldprogettiSpago";
		
		/*Cartella che conterrà i file presenti nella gerarchia
		 * prodotta da ExtractAllFile, ma non presenti nella versione vecchia 
		 * dei programmi (oldPath)
		 */
		String master = "/home/spinelli/Scrivania/ToTranslate";
		(new File(master)).mkdir();
	    
		String path = oldPath+subPath+"/"+fileName;
		int count = 0;
		File dir = new File (path);
		if(dir.exists())
		{
			//Controllo se i file (vecchia versione e nuova versione) sono diversi
		    try 
		    {
		        BufferedReader oldfileTemp = new BufferedReader(new FileReader(path));
		        BufferedReader newfileTemp = new BufferedReader(new FileReader(newPath+"/"+fileName));
		        String oldStrTemp;
		        String newStrTemp;
		        while ((newStrTemp = newfileTemp.readLine()) != null) 
		        {
		        	oldStrTemp = oldfileTemp.readLine();
		        	
		        	//Se sono diversi incremento la variabile contatore
		            if(!newStrTemp.equals(oldStrTemp))
		            {
		            	count++;
		            }
		        }
		        oldfileTemp.close();
		        newfileTemp.close();
		        
		        /*Se sono sicura che sono diversi creo il percorso nella cartella master e 
		         *creo il file che contiene la differenza rispetto il file della nuova versione
		         */
		        if (count != 0)
		        {
		        	String name = CreateDir.getName(fileName);
					String diff = master+subPath;
					File f1 = new File(diff);
					f1.mkdirs();			
					File fileFirst = new File(master+subPath+"/"+name);
					fileFirst.createNewFile();
					
		        	BufferedReader oldfile = new BufferedReader(new FileReader(path));
			        BufferedReader newfile = new BufferedReader(new FileReader(newPath+"/"+fileName));
			        BufferedWriter outfile = new BufferedWriter(new FileWriter(master+subPath+"/"+name));
			        String oldStr;
			        String newStr;
			        while ((newStr = newfile.readLine()) != null) 
			        {
			        	oldStr = oldfile.readLine();
			            if(!newStr.equals(oldStr))
			            {
			            	count++;
			            	try 
			                {
			            		//Scrivo il nuovo file
			                    outfile.write(newStr);
			                    outfile.write("\n");
			                    
			                }
			                catch (IOException e)    {    }
			            }
			        }
			        oldfile.close();
			        newfile.close();
			        outfile.close();
			        
			        if(count == 0)
			        {
			        	File file = new File(master+subPath+"/"+name);
			            file.delete();
			        }		        	
		        }		        
		      
	            File allDir = new File(master+subPath);
	            allDir.delete();
		        	
		    } 
		    catch (IOException e) 
		    {
		        // Exceptions ignored.
		    }
		    File MastDir = new File(master);
		    String [] lista = MastDir.list();
            for(int j=0; j<lista.length; j++)
            {
            	File temp = new File(master+"/"+lista[j]);
            	temp.delete();
            }
		}
		else
		{
			//Viene eseguito nel caso in cui il file è presente solo nella nuova versione
			String diff = master+subPath;
			File f1 = new File(diff);
			f1.mkdirs();
			File inputFile = new File(home+subPath+"/"+fileName);
			File outputFile = new File(diff+"/"+fileName);
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
		File allDir = new File(master+subPath);
        allDir.delete();		
	}
}
