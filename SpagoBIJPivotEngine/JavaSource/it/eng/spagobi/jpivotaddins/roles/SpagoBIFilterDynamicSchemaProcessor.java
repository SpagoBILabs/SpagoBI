/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.jpivotaddins.roles;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import mondrian.olap.Util;
import mondrian.spi.impl.FilterDynamicSchemaProcessor;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

public class SpagoBIFilterDynamicSchemaProcessor extends
FilterDynamicSchemaProcessor {

	private static Logger logger = Logger.getLogger(SpagoBIFilterDynamicSchemaProcessor.class);

	private static final BASE64Decoder DECODER = new BASE64Decoder();
	
	Integer indexProgression = null;
	
	protected String filter(String schemaUrl, Util.PropertyList connectInfo, InputStream stream) throws Exception {
		logger.debug("IN");
		String originalSchema = super.filter(schemaUrl, connectInfo, stream);
		String modifiedSchema = originalSchema;
		// search for profile attributes to substitute in schema definition, identified by $P{attribute_name}
		indexProgression = Integer.valueOf(0);
		String att = findProfileAttributeInSchema(originalSchema);
		while(att != null){
			// if value is null I put null, if instead there is no the attribute name in connectInfo I don't substitute			
			if (connectInfo.get(att) != null) {
				String attrValueBase64 = connectInfo.get(att);
				logger.debug("Attribute value in Base64 encoding is " + attrValueBase64);
				String value = null;
				try {
					value = new String(DECODER.decodeBuffer(attrValueBase64), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error("UTF-8 encoding not supported!!!!!", e);
					value = new String(DECODER.decodeBuffer(attrValueBase64));
				}
				logger.debug("change attribute " + att + " with  [" + value + "]");
				
				modifiedSchema = modifiedSchema.replaceAll("\\$\\{"+att+"\\}", value);
			}		
			att = findProfileAttributeInSchema(modifiedSchema);
		}
		logger.debug("OUT: modified schema is:\n" + modifiedSchema);
		return modifiedSchema;
	}



	public String processSchema(
			String schemaUrl,
			Util.PropertyList connectInfo) throws Exception {
		logger.debug("IN: schemaUrl: " + schemaUrl);
		try {
			if (schemaUrl.startsWith("file:")) {
				schemaUrl = schemaUrl.substring("file:".length());
			}
			File schemaFile = new File(schemaUrl);
			schemaUrl = schemaFile.getAbsolutePath();
			logger.debug("Absolute file path: " + schemaUrl);
			return super.processSchema(schemaUrl, connectInfo);
		} finally {
			logger.debug("OUT");
		}
	}

	/** return the first profile attribute in schema
	 * 
	 * @param schema
	 * @param indexProgression. keeps track of the last found index to go always ahead in reading
	 * @return
	 */
	public String findProfileAttributeInSchema(String schema){
		logger.debug("IN");
		String toReturn = null;
		int index = schema.indexOf("${", indexProgression);	
		if (index != -1) {
			int indexEnd = schema.indexOf("}", index );	
			toReturn = schema.substring(index+2, indexEnd);
			indexProgression = new Integer(indexEnd);
		}
		logger.debug("OUT");
		return toReturn;
	}

}
