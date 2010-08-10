/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.utility;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class JarUtils.
 * 
 * @author Gioia
 */
public class JarUtils {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JarUtils.class);
		
	/**
	 * Gets the file name.
	 * 
	 * @param path the path
	 * 
	 * @return the file name
	 */
	private static String getFileName(String path) {
		String fileName = null;
		
		int index = (path.lastIndexOf("/") != -1)? path.lastIndexOf("/"): path.lastIndexOf("\\");
		
		if(index != -1 && (index + 1) < path.length()) 
			fileName = path.substring(index + 1, path.length());
		else
			fileName = path;
		
		return fileName;
	}
	
	/**
	 * Gets the resource from jar file.
	 * 
	 * @param jar the jar
	 * @param resourceName the resource name
	 * 
	 * @return the resource from jar file
	 */
	public static URL getResourceFromJarFile(File jar, String resourceName) {
		//log.info( "Searching for mapping documents in jar: " + jar.getName() );
		String path = null;
		
		JarFile jarFile = null;
		try {

			try {
				jarFile = new JarFile( jar );
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}

			Enumeration jarEntries = jarFile.entries();
			while ( jarEntries.hasMoreElements() ) {

				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
				
				
				String fileName = getFileName(ze.getName());
				logger.debug(" Find file ["+ fileName + "] in jar File");
				if ( fileName.equalsIgnoreCase(resourceName) ) {
					//log.info( "Found mapping document in jar: " + ze.getName() );
					try {
						path = ze.getName();
					}
					catch (Exception e) {
						/*
						throw new MappingException(
								"Could not read mapping documents from jar: " + jar.getName(),
								e
							);
							*/
					}
				}
			}

		}
		finally {

			try {
				if (jarFile!=null) jarFile.close();
			}
			catch (IOException ioe) {
				//log.error("could not close jar", ioe);
			}

		}
		if (path != null){
			String urlStr = "jar:file:/" + jar.toString() + "!/" + path;
			return getUrl(jar, path);
		}else{
			return null;
		}
	}
	
	/**
	 * Gets the url str.
	 * 
	 * @param jarFile the jar file
	 * @param path the path
	 * 
	 * @return the url str
	 */
	public static String getUrlStr(File jarFile, String path) {
		return ("jar:file:/" + jarFile.toString() + "!/" + path);
	}
	
	/**
	 * Gets the url.
	 * 
	 * @param jarFile the jar file
	 * @param path the path
	 * 
	 * @return the url
	 */
	public static URL getUrl(File jarFile, String path) {
		try {
			return new URL( getUrlStr(jarFile, path) );
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Prints the file.
	 * 
	 * @param url the url
	 */
	private static void printFile(URL url) {
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(url.openStream()) );			
			String line = null;
			while( (line = reader.readLine()) != null ) {
				System.out.println(line);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @throws MalformedURLException the malformed url exception
	 */
	public static void main(String[] args) throws MalformedURLException{
		File jar = new File("C:\\testqbe.jar");
		URL url = getResourceFromJarFile(jar, "ProductClass.hbm.xml");
		printFile(url);
	}
}
