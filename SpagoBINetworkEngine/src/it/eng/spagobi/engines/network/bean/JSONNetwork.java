/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.bean;



import it.eng.spagobi.engines.network.NetworkEngineRuntimeException;
import it.eng.spagobi.engines.network.serializer.SerializationException;
import it.eng.spagobi.engines.network.serializer.json.EdgeJSONSerializer;
import it.eng.spagobi.engines.network.serializer.json.NodeJSONSerializer;
import it.eng.spagobi.engines.network.template.NetworkTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class JSONNetwork implements INetwork{

	private Set<Node> nodes; //list of nodes
	private Set<Edge> edges; //list of edges
	private JSONObject networkOptions;
	private static final String TYPE="json";
	private Map<String, String> targetNodeProperties;//column-->property
	private Map<String, String> sourceNodeProperties;//column-->property
	private Map<String, String> edgeProperties;//column-->property
	private Set<JSONNetworkMappingMetadata> nodeMetadata;//structure of the data for the nodes. For example if the node has the property id,label,color the nodeMetadata are {label:string,color:string}. So all the property without the id
	private CrossNavigationLink networkCrossNavigation;//Cross navigation link structure

	public static transient Logger logger = Logger.getLogger(JSONNetwork.class);
	
	public JSONNetwork() {
		super();
		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
		targetNodeProperties = new HashMap<String, String>();
		sourceNodeProperties = new HashMap<String, String>();
		edgeProperties = new HashMap<String, String>();
		nodeMetadata = new HashSet<JSONNetworkMappingMetadata>();
		
	}
	
	
	public JSONNetwork(JSONObject network, CrossNavigationLink networkCrossNavigation) {
		this();
		this.networkCrossNavigation = networkCrossNavigation;
		try {
			this.networkOptions = network.getJSONObject(NetworkTemplate.OPTIONS);
			parseDataSetMapping(network.getJSONArray(NetworkTemplate.DATA_SET_MAPPING));
		} catch (Exception e) {
			logger.error("Error loading building the Network object from the json object", e);
			throw new NetworkEngineRuntimeException("Error loading building the Network object from the json object", e);
		}
	}
	
	/**
	 * Build the maps that map the properties and the columns of the dataset
	 * @param dataSetMapping
	 * @throws JSONException
	 */
	private void parseDataSetMapping(JSONArray dataSetMapping) throws JSONException{
		JSONObject mapping;
		String element, column, property;
		for (int i = 0; i < dataSetMapping.length(); i++) {
			mapping = dataSetMapping.getJSONObject(i);
			element = mapping.getString(NetworkTemplate.DATA_SET_MAPPING_ELEMENT);
			column = mapping.getString(NetworkTemplate.DATA_SET_MAPPING_COLUMN);
			property = mapping.getString(NetworkTemplate.DATA_SET_MAPPING_PROPERTY);
			if(element.equalsIgnoreCase(NetworkTemplate.DATA_SET_MAPPING_SOURCE)){
				sourceNodeProperties.put(column,property);
			}else if(element.equalsIgnoreCase(NetworkTemplate.DATA_SET_MAPPING_TARGHET)){
				targetNodeProperties.put(column,property);
			}else if(element.equalsIgnoreCase(NetworkTemplate.DATA_SET_MAPPING_EDGE)){
				edgeProperties.put(column,property);
			}
		}
		//remove the property id
		sourceNodeProperties.remove("id");
		targetNodeProperties.remove("id");
		edgeProperties.remove("id");
		this.nodeMetadata= buildNodesMetadata();
	} 


	public JSONNetwork(Set<Node> nodes,Set<Edge> edges, JSONObject networkOptions) {
		super();
		this.networkOptions = networkOptions;
		this.nodes = nodes;
		this.edges = edges;
	}

	
	public String getElementFromMapping(String column){
		if(targetNodeProperties.containsKey(column)){
			return "targhet";
		}
		if(sourceNodeProperties.containsKey(column)){
			return "source";
		}
		if(edgeProperties.containsKey(column)){
			return "edge";
		}
		return "no";
	}
	

	/**
	 * Builds the schema for the nodes. 
	 * @return Set({name: "label", type: "string"},...)
	 */
	private Set<JSONNetworkMappingMetadata> buildNodesMetadata(){
		String property;
		Set<JSONNetworkMappingMetadata> metadata = new HashSet<JSONNetworkMappingMetadata>();
		Iterator<String> propertiesIterator = sourceNodeProperties.values().iterator();
		while(propertiesIterator.hasNext()){
			property = propertiesIterator.next();
			metadata.add(new JSONNetworkMappingMetadata(property));
		}
		propertiesIterator = targetNodeProperties.values().iterator();
		while(propertiesIterator.hasNext()){
			property = propertiesIterator.next();
			metadata.add(new JSONNetworkMappingMetadata(property));
		}
		return metadata;
	}
	
	//SERIALIZABLE PROPERTIES
	
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

	
	public Set<JSONNetworkMappingMetadata> getNodeMetadata() {
		return nodeMetadata;
	}
	
	//NOT SERIALIZABLE PROPERTIES
	


	@JsonIgnore
	public String getNetworkType() {
		return TYPE;
	}

	@JsonIgnore
	public String getNetworkOptions() {
		return networkOptions.toString();
	}

	@JsonIgnore
	public String getMappingForNodeSource(String column){
		return sourceNodeProperties.get(column);
	}
	@JsonIgnore
	public String getMappingForNodeTarget(String column){
		return targetNodeProperties.get(column);
	}
	@JsonIgnore
	public String getMappingForEdge(String column){
		return edgeProperties.get(column);
	}
	
	public void setNetworkCrossNavigation(CrossNavigationLink networkCrossNavigation) {
		this.networkCrossNavigation = networkCrossNavigation;
	}


	/**
	 * JSON serializer for this object
	 * @return the network serialized
	 * @throws SerializationException
	 */
	@JsonIgnore
	public String getNetworkAsString() throws SerializationException{
		ObjectMapper mapper = new ObjectMapper();
		String s ="";
		try {
			SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
			simpleModule.addSerializer(Node.class, new NodeJSONSerializer());
			simpleModule.addSerializer(Edge.class, new EdgeJSONSerializer());
			mapper.registerModule(simpleModule);
			s = mapper.writeValueAsString((JSONNetwork)this);

			
		} catch (Exception e) {
			
			throw new SerializationException("Error serializing the network",e);
		}
		s = StringEscapeUtils.unescapeJavaScript(s);
		return  s; 
	}

	/**
	 * Serializer for the cross navigation structure
	 */
	@JsonIgnore
	public String getNetworkCrossNavigation() throws SerializationException{
		ObjectMapper mapper = new ObjectMapper();
		String s ="";
		try {
			
			s = mapper.writeValueAsString(networkCrossNavigation);

		} catch (Exception e) {
			
			throw new SerializationException("Error serializing the network",e);
		}
		return  s; 
	}
	
	

	
}
