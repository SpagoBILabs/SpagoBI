/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.spagobi.engines.geo.map.utils;

import it.eng.spago.configuration.ConfigSingleton;

import java.io.File;
import java.net.MalformedURLException;

// TODO: Auto-generated Javadoc
/**
 * The Class MapCatalogueMock.
 * 
 * @author Andrea Gioia
 */
public class MapCatalogueMock {
	
	/**
	 * Gets the standard hierarchy.
	 * 
	 * @return the standard hierarchy
	 */
	public static String getStandardHierarchy() {	
		return "<HIERARCHY name=\"default\" table_name=\"gerarchia_geo\">"
			+ " <LEVEL name=\"unita_urbanistiche\" column_id=\"cod_uu\"  column_desc=\"desc_uu\" feature_name=\"unita_urbanistiche\"/>" 
			+ "	<LEVEL name=\"circoscrizioni\" column_id=\"cod_circoscrizione\"  column_desc=\"desc_circoscrizione\" feature_name=\"circoscrizioni\"/>"             
			+ "</HIERARCHY>";
	}
	
	/**
	 * Gets the map url.
	 * 
	 * @param mapName the map name
	 * 
	 * @return the map url
	 */
	public static String getMapUrl(String mapName) {
		if(mapName.equalsIgnoreCase("circoscrizioniBis")) mapName = "circoscrizioni";
		
		File file = new File(ConfigSingleton.getRootPath() + "/maps/genova/" + mapName + ".svg");
		try {
			return file.toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the map names by feature.
	 * 
	 * @param featureName the feature name
	 * 
	 * @return the map names by feature
	 */
	public static String[] getMapNamesByFeature(String featureName) {
		if(featureName.equalsIgnoreCase("circoscrizioni")) {
			return new String[]{"circoscrizioni", "circoscrizioniBis"};
		}
		return new String[]{featureName};
	}

	/**
	 * Gets the feature names in map.
	 * 
	 * @param mapName the map name
	 * 
	 * @return the feature names in map
	 */
	public static String[] getFeatureNamesInMap(String mapName) {
		if(mapName.equalsIgnoreCase("circoscrizioniBis")) {
			return new String[]{
					"circoscrizioni"			
			};			
		}
		return new String[]{
				mapName			
		};
	}
}
