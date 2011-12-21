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
package it.eng.spagobi.engines.worksheet;

import it.eng.spagobi.engines.qbe.QbeEngine;
import it.eng.spagobi.engines.qbe.crosstable.serializer.CrosstabDeserializerFactory;
import it.eng.spagobi.engines.qbe.crosstable.serializer.CrosstabSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.AttributeDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.AttributeSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.MeasureDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.MeasureSerializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.WorkSheetDeserializerFactory;
import it.eng.spagobi.engines.worksheet.serializer.WorkSheetSerializerFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorksheetEngine {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeEngine.class);

	public static WorksheetEngineInstance createInstance(Object object, Map env) throws WorksheetEngineException {
		WorksheetEngineInstance worksheetEngineInstance = null;
		initDeserializers();
		initSerializers();
		logger.debug("IN");
		worksheetEngineInstance = new WorksheetEngineInstance(object, env);
		logger.debug("OUT");
		return worksheetEngineInstance;
	}
	
	private static void initDeserializers() {
    	WorkSheetDeserializerFactory.getInstance();
    	CrosstabDeserializerFactory.getInstance();
    	AttributeDeserializerFactory.getInstance();
    	MeasureDeserializerFactory.getInstance();
	}


	private static void initSerializers() {
    	WorkSheetSerializerFactory.getInstance();
    	CrosstabSerializerFactory.getInstance();
    	AttributeSerializerFactory.getInstance();
    	MeasureSerializerFactory.getInstance();
	}

}
