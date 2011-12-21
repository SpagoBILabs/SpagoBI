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
package it.eng.spagobi.engines.qbe.template;

import it.eng.spago.base.SourceBean;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeTemplateParser implements IQbeTemplateParser{
	
	Map<String, IQbeTemplateParser> parsers;
	
	static QbeTemplateParser instance;
	
	public static QbeTemplateParser getInstance() {
		if(instance == null) {
			instance = new QbeTemplateParser();
		}
		return instance;
	}
	
	private QbeTemplateParser(){
		parsers = new HashMap();
		parsers.put(SourceBean.class.getName(), new QbeXMLTemplateParser());
		parsers.put(JSONObject.class.getName(), new QbeJSONTemplateParser());
	}
	
	
	public QbeTemplate parse(Object template) {
		QbeTemplate qbeTemplate;
		IQbeTemplateParser parser;
		
		qbeTemplate = null;
		
		if(!parsers.containsKey(template.getClass().getName())) {
			throw new QbeTemplateParseException("Impossible to parse template of type [" + template.getClass().getName() + "]");
		} else {
			parser = parsers.get(template.getClass().getName());
			qbeTemplate = parser.parse(template);
		}
		
		return qbeTemplate;
	}
}
