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
package it.eng.spagobi.engines.worksheet.template;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.template.QbeTemplateParseException;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorksheetXMLTemplateParser implements IWorksheetTemplateParser{

	public static String TAG_WORKSHEET_DEFINITION = "WORKSHEET_DEFINITION";
	public static String TAG_DATASET = "DATASET";

	/** Logger component. */
	public static transient Logger logger = Logger.getLogger(WorksheetXMLTemplateParser.class);

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.worksheet.template.IWorksheetTemplateParser#parse(java.lang.Object)
	 */
	@Override
	public WorksheetTemplate parse(Object template) {
		Assert.assertNotNull(template, "Input parameter [template] cannot be null");
		Assert.assertTrue(template instanceof SourceBean, "Input parameter [template] cannot be of type [" + template.getClass().getName() + "]");
		return parse((SourceBean)template);
	}

	private WorksheetTemplate parse(SourceBean template) {

		WorksheetTemplate worksheetTemplate = null;
		String templateName;
		SourceBean worksheetSB;
		JSONObject worksheetJSONTemplate;

		try {

			worksheetTemplate = new WorksheetTemplate();

			templateName = template.getName();
			logger.debug("Parsing template [" + templateName + "] ...");
			Assert.assertNotNull(templateName, "Root tag cannot be not be null");

			// DATASET block
			if(template.containsAttribute(TAG_DATASET)) {}

			// worksheet block
			if(template.containsAttribute(TAG_WORKSHEET_DEFINITION)) {
				worksheetSB = (SourceBean) template.getAttribute(TAG_WORKSHEET_DEFINITION);
				worksheetJSONTemplate = new JSONObject(worksheetSB.getCharacters());
				worksheetTemplate.setWorkSheetDefinition(loadWorksheetDefinition(worksheetJSONTemplate));
			} else {
				logger.debug("The template does not contain tag [" + TAG_WORKSHEET_DEFINITION +"]");
			}

			logger.debug("Templete parsed succesfully");

		} catch(Throwable t) {
			throw new QbeTemplateParseException("Impossible to parse template [" + template.toString()+ "]", t);
		} finally {
			logger.debug("OUT");
		}	

		return worksheetTemplate;
	}


	private WorkSheetDefinition loadWorksheetDefinition(JSONObject worksheetDefinition) {
		try {
			WorkSheetDefinition workSheetDefinition = new WorkSheetDefinition();
			workSheetDefinition.load( worksheetDefinition.toString().getBytes() );
			return workSheetDefinition;
		} catch(Throwable t) {
			SpagoBIRuntimeException serviceException;
			String msg = "Impossible load worksheet definition [" + worksheetDefinition + "].";
			Throwable rootException = t;
			while(rootException.getCause() != null) {
				rootException = rootException.getCause();
			}
			String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
			msg += "\nThe root cause of the error is: " + str;
			serviceException = new SpagoBIRuntimeException(msg, t);

			throw serviceException;
		}
	}

}
