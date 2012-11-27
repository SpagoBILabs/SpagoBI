/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class Node implements Serializable, Comparable<Node>{
	
	private String label;
	private String id;
	//private Map<String,String> properties = new HashMap<String, String>();
	
	
	
	/**
	 * @param id
	 */
	public Node(String id) {
		super();
		this.id = id;
		this.label = id;
	}
	/**
	 * @param name
	 * @param id
	 */
	public Node(String name, String id) {
		super();
		this.label = name;
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String name) {
		this.label = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
//	public Map<String, String> getProperties() {
//		return properties;
//	}
//	public void setProperty(String propertyName, String PropertyValue) {
//		this.properties.put(propertyName, PropertyValue);
//	}
//	public String getProperty(String propertyName) {
//		return this.properties.get(propertyName);
//	}
	
	public JSONObject serialize() throws JSONException{
		
		JSONObject nodeJSON = new JSONObject();
		JSONObject dataJSON = new JSONObject();
		nodeJSON.put("id", id);
		dataJSON.put("label", label);
		nodeJSON.put("data",dataJSON);

		return nodeJSON;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Node arg0) {
		return arg0.getId().compareTo(id);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	public String toString(){
		
		String s = "";
		try {
			s = serialize().toString();
			s=s.replace("\"id\"", "id");
			s=s.replace("\"data\"", "data");
			s=s.replace("\"label\"", "label");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}
	
	
	

}
