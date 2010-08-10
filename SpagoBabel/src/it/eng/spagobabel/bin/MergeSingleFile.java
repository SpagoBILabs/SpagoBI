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

public class MergeSingleFile 
{					
		public static void mergeSingle(String subPath, String fileName, String newPath, String sovrascrivi) throws IOException
		{	
			String home = "/home/spinelli/Scrivania";
			
			//Imposto come valore di default di sovrascrivi N
			if(sovrascrivi==null)
				sovrascrivi = "N";
			
			//Path della cartella che contiene la vecchia versione dei progetti
			//String oldPath = "/home/spinelli/Scrivania/";
			
			/*Cartella che conterrà i file presenti nella gerarchia
			 * prodotta da ExtractAllFile, ma non presenti nella versione vecchia 
			 * dei programmi (oldPath)
			 */
			String master = "/home/spinelli/Scrivania/ToTranslate";
			String mergePath = home+subPath+"/"+"merge";
		    
			String path = home+subPath+"/"+fileName;
			File dir = new File (path);
			
			if(dir.exists())
			{
				//Controllo se i file (vecchia versione e nuova versione) sono diversi
				
			    try 
			    {	
			        
			        BufferedWriter outfile;
			        
			        if("Y".equals(sovrascrivi))
			        {
			        	File merge = new File(mergePath);
						merge.createNewFile();
			        	//Caso in cui sovrascrivo le chiavi che hanno la stessa label
			        	
			        	//Creo una copia del file perchè dovrò poi sovrascriverlo
			        	File inputFile = new File(path);
						File outputFile = new File(mergePath);
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
			        	
			        	BufferedReader oldfile = new BufferedReader(new FileReader(mergePath));
			        	outfile = new BufferedWriter(new FileWriter(path));
			 				
			        	String oldStr;
			        	String newStr;
			     		
			        	while((oldStr = oldfile.readLine())!= null)
			        	{
			        		boolean exist = false;
			        		
			        		BufferedReader newfile = new BufferedReader(new FileReader(newPath+"/"+fileName));
			        		while ((newStr = newfile.readLine()) != null) 
			        		{		
			        			String key = newStr;
			        			String[] arrayNewStr = newStr.split("=");
			        			String subNewStr = arrayNewStr[0];
			        			String[] arrayOldStr = oldStr.split("=");
			        			String subOldStr = arrayOldStr[0];
			        		
			        			if(subNewStr.contains(subOldStr))
			        			{
			        				exist = true;
			        				outfile.write(newStr);
			        				outfile.newLine();
			        			}
			        			
			        		}
			        		
			        		if(!exist)
			        		{
			        			try 
			        			{
			        				//Scrivo il nuovo file
			        				outfile.write(oldStr);
			        				outfile.newLine();
			        			}
			        			catch (IOException e)    {    }
			        		}
			        		newfile.close();
			        	}
			        	
			        	oldfile.close();
			        	outfile.close();
			        	merge.delete();
			        }
			        else
			        {
			        	//Caso in cui non sovrascrivo le chiavi
			        	outfile = new BufferedWriter(new FileWriter(path, true));
			        	BufferedReader newfile = new BufferedReader(new FileReader(newPath+"/"+fileName));
			        
			        	String oldStr;
			        	String newStr;
			     			       
			        	while ((newStr = newfile.readLine()) != null) 
			        	{
			        		boolean exist = false;
			        		
			        		try 
			        		{
			        			//Scrivo il nuovo file
			        			outfile.newLine();
			        			outfile.append(newStr);
			        		}
			        		catch (IOException e)    {    }
			        	}
			        	newfile.close();
			        }
		        	outfile.close();
			    } 
			    catch (IOException e) 
			    {
			        // Exceptions ignored.
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
		}
}



