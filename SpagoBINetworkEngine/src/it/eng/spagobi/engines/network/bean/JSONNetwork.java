/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.bean;



import it.eng.spagobi.engines.network.serializer.SerializationException;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class JSONNetwork implements INetwork{

	private Set<Node> nodes;
	private Set<Edge> edges;
	private JSONObject networkOptions;
	private static final String TYPE="json";
	

	public JSONNetwork() {
		super();
		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
	}
	
	
	public JSONNetwork(JSONObject networkOptions) {
		this();
		this.networkOptions = networkOptions;
	}



	public JSONNetwork(Set<Node> nodes,Set<Edge> edges, JSONObject networkOptions) {
		super();
		this.networkOptions = networkOptions;
		this.nodes = nodes;
		this.edges = edges;
	}


	public Set<Node> getNodes() {
		return nodes;
	}
	
	public Set<Edge> getEdges() {
		return edges;
	}

	public void addNode(Node n){
		this.nodes.add(n);
	}
	public void addEdge(Edge e){
		this.edges.add(e);
	}
	@JsonIgnore
	public String getNetworkAsString() throws SerializationException{
		ObjectMapper mapper = new ObjectMapper();
		String s ="";
		try {
			
			
			s = mapper.writeValueAsString((JSONNetwork)this);

			
		} catch (Exception e) {
			
			throw new SerializationException("Error serializing the network",e);
		}
		return  s; 
	}


	@JsonIgnore
	public String getNetworkType() {
		return TYPE;
	}

	@JsonIgnore
	public String getNetworkOptions() {
		return networkOptions.toString();
	}
	
	
	
}
