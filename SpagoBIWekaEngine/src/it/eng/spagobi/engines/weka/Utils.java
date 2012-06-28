/* SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This program is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, either version 2.1 
 * of the License, or (at your option) any later version. This program is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU  General Public License for more details. You should have received a copy of the GNU  General Public License along with 
 * this program. If not, see: http://www.gnu.org/licenses/. */
package it.eng.spagobi.engines.weka;

import java.util.Vector;

import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;
import weka.gui.beans.Loader;
import weka.gui.beans.Saver;

/**
 * @author Gioia
 *
 */
public class Utils {
	static public String getLoderDesc(Loader loader) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Loder Class: " + loader.getLoader().getClass().getName()  + "\n");
		buffer.append("Global Info: " + loader.globalInfo()  + "\n");
		String className = loader.getLoader().getClass().getName();
		
		if(className.equalsIgnoreCase(DatabaseLoader.class.getName())) {
			DatabaseLoader databaseLoader = (DatabaseLoader)loader.getLoader();
						
			buffer.append("  Loader type: DatabaseLoader"  + "\n");
			buffer.append("Options: ");
			String[] options = databaseLoader.getOptions();		
			for(int i = 0; i < options.length; i++) {
				buffer.append(options[i] + "; ");
			}
			buffer.append("  URL: " + databaseLoader.getUrl() + "\n");
			buffer.append("  User: " + databaseLoader.getUser() + "\n");		
			buffer.append("  Password: " + "?" + "\n");	
			buffer.append("  Query: " + databaseLoader.getQuery() + "\n");
			buffer.append("  Key columns' name: " + databaseLoader.getKeys() + "\n");				
		}
		else if(className.equalsIgnoreCase(ArffLoader.class.getName())) {
			ArffLoader arffLoader = (ArffLoader)loader.getLoader();
			buffer.append("  Loader type: ArffLoader" + "\n");
		}
		else if(className.equalsIgnoreCase(CSVLoader.class.getName())) {
			CSVLoader csvLoader = (CSVLoader)loader.getLoader();
			buffer.append("  Loader type: CSVLoader" + "\n");
		}
		else {
			buffer.append("  Loader type: Unknown" + "\n");
		}
		
		return buffer.toString();
	}
	
	static public String getLoderDesc(Vector loaders) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------\n");
		buffer.append(" LOADERS INFO		   \n");
		buffer.append("------------------------\n");
		for(int i = 0; i < loaders.size(); i++) {
			Loader loader = (Loader)loaders.get(i);
			buffer.append(getLoderDesc(loader));
			buffer.append("------------------------\n");
		}
		return buffer.toString();
	}
	
	public static String getSaverDesc(Saver saver) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Saver Class: " + saver.getSaver().getClass().getName() + "\n");
		buffer.append("Global Info: " + saver.globalInfo() + "\n");
				
		String className = saver.getSaver().getClass().getName();
			
		if(className.equalsIgnoreCase(DatabaseSaver.class.getName())) {
			DatabaseSaver databaseSaver = (DatabaseSaver)saver.getSaver();
										
			buffer.append("Saver type: DatabaseSaver" + "\n");				
			buffer.append("Options: ");
			String[] options = databaseSaver.getOptions();		
			for(int i = 0; i < options.length; i++) {
				buffer.append(options[i] + "; ");
			}
			buffer.append("\n");
			buffer.append("URL: " + databaseSaver.getUrl() + "\n");
			buffer.append("User: " + databaseSaver.getUser() + "\n");
			buffer.append("Password: " + "?" + "\n");
			buffer.append("Table Name: " + databaseSaver.getTableName() + "\n");
			buffer.append("Relation For Table Name: " + databaseSaver.getRelationForTableName() + "\n");
			buffer.append("Auto Key Generation: " + databaseSaver.getAutoKeyGeneration() + "\n");				
		}			
		else if(className.equalsIgnoreCase(ArffSaver.class.getName())) {
			ArffSaver arffSaver = (ArffSaver)saver.getSaver();
			buffer.append("  Loader type: ArffSaver" + "\n");				
		}
		else if(className.equalsIgnoreCase(CSVSaver.class.getName())) {
			CSVSaver csvSaver = (CSVSaver)saver.getSaver();
			buffer.append("  Loader type: CSVLoader" + "\n");
		}	
		else {
			buffer.append("  Loader type: Unknown" + "\n");
		}
		
		return buffer.toString();
	}
	
	static public String getSaverDesc(Vector savers) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------\n");
		buffer.append(" SAVERS INFO		   \n");
		buffer.append("------------------------\n");
		for(int i = 0; i < savers.size(); i++) {
			Saver saver = (Saver)savers.get(i);
			buffer.append(getSaverDesc(saver));
			buffer.append("------------------------\n");
		}
		return buffer.toString();
	}
}
