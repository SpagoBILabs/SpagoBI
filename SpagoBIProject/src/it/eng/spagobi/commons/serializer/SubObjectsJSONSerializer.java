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
package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.commons.utilities.GeneralUtilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

/**
 * @author Zerbetto Davide
 */
public class SubObjectsJSONSerializer implements Serializer {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String OWNER = "owner";
	public static final String CREATION_DATE = "creationDate";
	public static final String LAST_MODIFICATION_DATE = "lastModificationDate";
	public static final String VISIBILITY = "visibility";

	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof SubObject) ) {
			throw new SerializationException("SubObjectsJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			// dates are sent to the client using a fixed format, the one returned by GeneralUtilities.getServerDateFormat()
			SimpleDateFormat dateFormat =  new SimpleDateFormat();
			dateFormat.applyPattern(GeneralUtilities.getServerTimeStampFormat());
			
			SubObject subObject = (SubObject) o;
			result = new JSONObject();
			result.put(ID, subObject.getId() );
			result.put(NAME, subObject.getName() );
			result.put(DESCRIPTION, subObject.getDescription() );
			result.put(OWNER, subObject.getOwner() );
			Date creationDate = subObject.getCreationDate();
			String creationDateStr = dateFormat.format(creationDate);
			result.put(CREATION_DATE, creationDateStr );
			Date lastChangeDate = subObject.getLastChangeDate();
			String lastChangeDateStr = dateFormat.format(lastChangeDate);
			result.put(LAST_MODIFICATION_DATE, lastChangeDateStr );
			result.put(VISIBILITY, subObject.getIsPublic() );
			
			
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
