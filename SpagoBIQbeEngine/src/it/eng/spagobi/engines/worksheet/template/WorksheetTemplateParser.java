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

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.HashMap;
import java.util.Map;


/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorksheetTemplateParser {

	Map<String, IWorksheetTemplateParser> parsers;
	
	static WorksheetTemplateParser instance;
	
	public static WorksheetTemplateParser getInstance() {
		if(instance == null) {
			instance = new WorksheetTemplateParser();
		}
		return instance;
	}
	
	private WorksheetTemplateParser(){
		parsers = new HashMap();
		parsers.put(SourceBean.class.getName(), new WorksheetXMLTemplateParser());
	}
	
	
	public WorksheetTemplate parse(Object template,  Map env) {
		
		if(template == null){
			return new WorksheetTemplate();
		}
		
		WorksheetTemplate worksheetTemplate;
		IWorksheetTemplateParser parser;
		
		worksheetTemplate = null;
		
		if(!parsers.containsKey(template.getClass().getName())) {
			throw new SpagoBIEngineRuntimeException("Impossible to parse template of type [" + template.getClass().getName() + "]");
		} else {
			parser = parsers.get(template.getClass().getName());
			worksheetTemplate = parser.parse(template, env);
		}
		return worksheetTemplate;
	}
}