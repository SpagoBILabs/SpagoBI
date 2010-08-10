/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe;

import it.eng.spagobi.engines.qbe.analysisstateloaders.formbuilder.FormStateLoaderFactory;
import it.eng.spagobi.engines.qbe.analysisstateloaders.formbuilder.IFormStateLoader;
import it.eng.spagobi.engines.qbe.template.QbeJSONTemplateParser;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public class FormState extends EngineAnalysisState {
	
	public static final String CURRENT_VERSION = "1";
	
	public static final String FORM_STATE = "FORM_STATE";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FormState.class);

	public void load(byte[] rowData) throws SpagoBIEngineException {
		String str = null;
		JSONObject formStateJSON = null;
		JSONObject rowDataJSON = null;
		String encodingFormatVersion;
		
		logger.debug("IN");

		try {
			str = new String( rowData );
			logger.debug("loading form state from row data [" + str + "] ...");
			
			rowDataJSON = new JSONObject(str);
			try {
				encodingFormatVersion = rowDataJSON.getString("version");
			} catch (JSONException e) {
				logger.debug("no version found, default is 0");
				encodingFormatVersion = "0";
			}
			
			logger.debug("Row data encoding version  [" + encodingFormatVersion + "]");
			
			if (encodingFormatVersion.equalsIgnoreCase(CURRENT_VERSION)) {				
				formStateJSON = rowDataJSON;
			} else {
				logger.warn("Row data encoding version [" + encodingFormatVersion + "] does not match with the current version used by the engine [" + CURRENT_VERSION + "] ");
				logger.debug("Converting from encoding version [" + encodingFormatVersion + "] to encoding version [" + CURRENT_VERSION + "]....");
				IFormStateLoader formViewerStateLoader;
				formViewerStateLoader = FormStateLoaderFactory.getInstance().getLoader(encodingFormatVersion);
				if (formViewerStateLoader == null) {
					throw new SpagoBIEngineException("Unable to load data stored in format [" + encodingFormatVersion + "] ");
				}
				formStateJSON = (JSONObject) formViewerStateLoader.load(str);
				logger.debug("Encoding conversion has been executed succesfully");
			}
			
			logger.debug("analysis state loaded succsfully from row data");
			
			// adding other info that are created dynamically
			QbeJSONTemplateParser.addAdditionalInfo(formStateJSON);
			setProperty( FORM_STATE,  formStateJSON);
			
		} catch (JSONException e) {
			throw new SpagoBIEngineException("Impossible to load form state from raw data", e);
		} finally {
			logger.debug("OUT");
		}
	}

	public byte[] store() throws SpagoBIEngineException {
		JSONObject formStateJSON = null;
		String rowData = null;	
				
		try {
			formStateJSON = (JSONObject) getProperty( FORM_STATE );
			formStateJSON.put("version", CURRENT_VERSION);
			rowData = formStateJSON.toString();
		} catch (Throwable e) {
			throw new SpagoBIEngineException("Impossible to store form state", e);
		}
		
		return rowData.getBytes();
	}
	
	public JSONObject getConf() {
		return (JSONObject) getProperty( FORM_STATE );
	}
	
	public void setConf(JSONObject json) {
		Assert.assertNotNull(json, "JSON form state cannot be null");
		QbeJSONTemplateParser.addAdditionalInfo(json);
		setProperty(FORM_STATE, json);
	}
	
}
