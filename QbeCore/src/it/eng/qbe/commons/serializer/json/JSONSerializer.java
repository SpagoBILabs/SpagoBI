/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.commons.serializer.json;

import it.eng.qbe.commons.serializer.SerializationException;
import it.eng.qbe.commons.serializer.Serializer;
import it.eng.qbe.crosstab.bo.CrosstabDefinition;
import it.eng.qbe.crosstab.serializer.json.CrosstabJSONSerializer;
import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.json.QueryJSONSerializer;

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSONSerializer implements Serializer {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JSONSerializer.class);

	public Object serialize(Query q, DataMartModel m, Locale locale)
			throws SerializationException {
		logger.debug("IN");
		QueryJSONSerializer s = new QueryJSONSerializer();
		Object object = s.serialize(q, m, locale);
		logger.debug("OUT");
		return object;
	}

	public Object serialize(CrosstabDefinition cd)
			throws SerializationException {
		logger.debug("IN");
		CrosstabJSONSerializer s = new CrosstabJSONSerializer();
		Object object = s.serialize(cd);
		logger.debug("OUT");
		return object;
	}
    
    
}
