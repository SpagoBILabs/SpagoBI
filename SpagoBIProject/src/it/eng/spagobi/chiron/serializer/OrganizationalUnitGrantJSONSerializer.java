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
package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrant;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class OrganizationalUnitGrantJSONSerializer implements Serializer {
	
	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startdate";
	public static final String END_DATE = "enddate";
	public static final String HIERARCHY = "hierarchy";
	public static final String MODEL_INSTANCE = "modelinstance";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof OrganizationalUnitGrant) ) {
			throw new SerializationException("OrganizationalUnitGrantJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			OrganizationalUnitGrant grant = (OrganizationalUnitGrant) o;
			result = new JSONObject();
			result.put(ID, grant.getId() );
			result.put(LABEL, grant.getLabel() );
			result.put(NAME, grant.getName() );
			result.put(DESCRIPTION, grant.getDescription() );
			String df = GeneralUtilities.getServerDateFormat();
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.applyPattern(df);
			dateFormat.setLenient(false);
			result.put(START_DATE, dateFormat.format(grant.getStartDate()) );
			result.put(END_DATE, dateFormat.format(grant.getEndDate()) );
			
			OrganizationalUnitHierarchyJSONSerializer hierarchySer = new OrganizationalUnitHierarchyJSONSerializer();
			JSONObject hierarchyJSON = (JSONObject) hierarchySer.serialize(grant.getHierarchy(), locale);
			result.put(HIERARCHY, hierarchyJSON);
			
			ModelInstanceNodeJSONSerializer modelSer = new ModelInstanceNodeJSONSerializer();
			JSONObject modelInstanceJSON = (JSONObject) modelSer.serialize(grant.getModelInstance(), locale);
			result.put(MODEL_INSTANCE, modelInstanceJSON);
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		}
		
		return result;
	}
	
	
}
