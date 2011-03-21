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

import it.eng.qbe.commons.serializer.Deserializer;
import it.eng.qbe.commons.serializer.SerializationException;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.json.QueryJSONDeserializer;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSONDeserializer implements Deserializer {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JSONDeserializer.class);

	public Query deserializeQuery(Object o, IDataSource m)
			throws SerializationException {
		logger.debug("IN");
		QueryJSONDeserializer d = new QueryJSONDeserializer();
		Query query = d.deserialize(o, m);
		logger.debug("OUT");
		return query;
	}
}
