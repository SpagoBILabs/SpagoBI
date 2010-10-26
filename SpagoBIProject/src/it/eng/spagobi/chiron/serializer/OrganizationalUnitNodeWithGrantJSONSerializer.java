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

import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrantNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNodeWithGrant;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class OrganizationalUnitNodeWithGrantJSONSerializer implements Serializer {
	
	public static final String ID = "id";
	public static final String OU = "ou";
	public static final String LEAF= "leaf";
	public static final String PATH = "path";
	public static final String PARENT_NODE_ID = "parentnodeid";
	public static final String MODEL_INSTANCE_NODES = "modelinstancenodes";
	
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof OrganizationalUnitNodeWithGrant) ) {
			throw new SerializationException("OrganizationalUnitNodeWithGrantJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			OrganizationalUnitNodeWithGrant nodeWithGrants = (OrganizationalUnitNodeWithGrant) o;
			
			OrganizationalUnitNode node = nodeWithGrants.getNode();
			List<OrganizationalUnitGrantNode> nodeGrants = nodeWithGrants.getGrants();
			
			result = new JSONObject();
			result.put(ID, node.getNodeId() );
			result.put(PARENT_NODE_ID, node.getParentNodeId() );
			result.put(PATH, node.getPath() );
			result.put(LEAF, node.isLeaf() );
			OrganizationalUnitJSONSerializer orgUnitJSONSerializer = new OrganizationalUnitJSONSerializer();
			result.put(OU, orgUnitJSONSerializer.serialize(node.getOu(), locale));
			// TODO serialize hierarchy?
			
			JSONArray modelInstanceNodesIds = new JSONArray();
			Iterator<OrganizationalUnitGrantNode> it = nodeGrants.iterator();
			while (it.hasNext()) {
				OrganizationalUnitGrantNode grant = it.next();
				modelInstanceNodesIds.put(grant.getModelInstanceNode().getModelInstanceNodeId());
			}
			result.put(MODEL_INSTANCE_NODES, modelInstanceNodesIds);
			
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		}
		
		return result;
	}
	
	
}
