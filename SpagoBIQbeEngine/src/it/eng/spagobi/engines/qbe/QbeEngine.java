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

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.engines.qbe.crosstable.serializer.CrosstabDeserializerFactory;
import it.eng.spagobi.engines.qbe.crosstable.serializer.CrosstabSerializerFactory;
import it.eng.spagobi.engines.qbe.serializer.SlotDeserializerFactory;
import it.eng.spagobi.engines.qbe.serializer.SlotSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.AttributeDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.AttributeSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.MeasureDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.MeasureSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.WorkSheetDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.WorkSheetSerializerFactory;
import it.eng.spagobi.utilities.engines.EngineConstants;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeEngine {
	
	private static QbeEngineConfig engineConfig;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeEngine.class);
	
    
    private static void initEngine() {
    	if(engineConfig == null) {
    		engineConfig = QbeEngineConfig.getInstance();
    	}
	}

	public static QbeEngineInstance createInstance(Object template, Map env) throws QbeEngineException {
		QbeEngineInstance qbeEngineInstance = null;
		
		initDeserializers();
		initSerializers();
		
		logger.debug("IN");
		initEngine();
		
		Locale locale = (Locale)env.get(EngineConstants.ENV_LOCALE);	
		String language = locale.getLanguage();
		String userDateFormatPattern = (String)ConfigSingleton.getInstance().getAttribute("QBE.QBE-DATE-FORMAT." + language);
		if(userDateFormatPattern == null) userDateFormatPattern = (String)ConfigSingleton.getInstance().getAttribute("QBE.QBE-DATE-FORMAT.en");
		env.put(EngineConstants.ENV_USER_DATE_FORMAT, userDateFormatPattern);
		String databaseDateFormatPattern = (String)ConfigSingleton.getInstance().getAttribute("QBE.QBE-DATE-FORMAT.database");
		env.put(EngineConstants.ENV_DB_DATE_FORMAT, databaseDateFormatPattern);
		
		qbeEngineInstance = new QbeEngineInstance(template, env);
		logger.debug("OUT");
		return qbeEngineInstance;
	}
	
	private static void initDeserializers() {
    	SlotDeserializerFactory.getInstance();
	}


	private static void initSerializers() {
    	SlotSerializerFactory.getInstance();
	}

	
}
