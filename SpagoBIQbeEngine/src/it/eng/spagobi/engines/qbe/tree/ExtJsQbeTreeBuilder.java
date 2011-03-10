/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.tree;

import it.eng.qbe.cache.QbeCacheManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.i18n.ModelLabels;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.query.serializer.json.QueryJSONSerializer;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.qbe.tree.filter.QbeTreeFilter;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class ExtJsQbeTreeBuilder.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExtJsQbeTreeBuilder  {	
	
	
	private QbeTreeFilter qbeTreeFilter;
	
	
	private IDataSource dataSource;	
	
	private Locale locale;
	
	private ModelLabels datamartLabels;
	

	/**
	 * Instantiates a new ext js qbe tree builder.
	 * 
	 * @param qbeTreeFilter the qbe tree filter
	 */
	public ExtJsQbeTreeBuilder(QbeTreeFilter qbeTreeFilter)  {	
		setQbeTreeFilter( qbeTreeFilter );
	}	
	
	
	public JSONArray getQbeTree(IDataSource dataSource, Locale locale, String datamartName)  {			
		setDatamartModel(dataSource);
		setLocale(locale);
		setDatamartLabels( QbeCacheManager.getInstance().getLabels( getDataSource() , getLocale() ) );
		if( getDatamartLabels() == null) {
			setDatamartLabels( new ModelLabels() );
		}
		return buildQbeTree(datamartName);
	}
	
	private String geEntityLabel(DataMartEntity entity) {
		String label;
		label = getDatamartLabels().getLabel(entity);
		return StringUtilities.isEmpty(label)? entity.getName(): label;
	}
	
	private String geEntityTooltip(DataMartEntity entity) {
		String tooltip = getDatamartLabels().getTooltip(entity);
		return tooltip != null ? tooltip : "";
	}
	
	private String geFieldLabel(DataMartField field) {
		String label;
		label = getDatamartLabels().getLabel(field);
		return StringUtilities.isEmpty(label)? field.getName(): label;
	}

	private String geFieldTooltip(DataMartField field) {
		String tooltip = getDatamartLabels().getTooltip(field);
		return tooltip != null ? tooltip : "";
	}
	

	
	public PrintWriter writer;
	
	/**
	 * Builds the qbe tree.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the jSON array
	 */
	private JSONArray buildQbeTree(String datamartName)  {			
		JSONArray nodes = new JSONArray();	
		File file = new File(new File(ConfigSingleton.getRootPath()), "labels.properties");
		try {
			writer = new PrintWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
			writer = new PrintWriter(new CharArrayWriter());
		}
		addEntityNodes(nodes, datamartName);
		writer.flush();
		writer.close();
		return nodes;
	}
	
	
	/**
	 * Adds the entity nodes.
	 * 
	 * @param nodes the nodes
	 * @param datamartName the datamart name
	 */
	public void addEntityNodes(JSONArray nodes, String datamartName) {
		int nodeCounter = 0;
		
		List entities = getDataSource().getDataMartModelStructure().getRootEntities(datamartName);
		entities = qbeTreeFilter.filterEntities(getDataSource(), entities);
		
		Iterator it = entities.iterator();
		while(it.hasNext()) {
			DataMartEntity entity = (DataMartEntity)it.next();			
			addEntityNode(nodes, entity, 1);
		}
	}
	
	
	/**
	 * Adds the entity node.
	 * 
	 * @param nodes the nodes
	 * @param entity the entity
	 * @param recursionLevel the recursion level
	 */
	public void addEntityNode(JSONArray nodes, 
							 DataMartEntity entity,
							 int recursionLevel) {
		
			
		addEntityRootNode(nodes, entity, recursionLevel);		
	}
		
	
	
	
	/**
	 * Adds the entity root node.
	 * 
	 * @param nodes the nodes
	 * @param entity the entity
	 * @param recursionLevel the recursion level
	 */
	public void addEntityRootNode(JSONArray nodes,
								  DataMartEntity entity,
								  int recursionLevel) {		
		
		//DatamartProperties datamartProperties = dataSource.getDataMartProperties();	
		String iconCls = entity.getPropertyAsString("type");			
		String label = geEntityLabel( entity );
		String londDescription = QueryJSONSerializer.getEntityLongDescription( entity , getDatamartLabels());
		String tooltip = geEntityTooltip( entity );
		
		writer.println("\n\n####################################################");
		writer.println( entity.getUniqueName().replaceAll(":", "/") + "=");
		writer.println( entity.getUniqueName().replaceAll(":", "/") + ".tooltip=");
		
			
		JSONArray childrenNodes = getFieldNodes(entity, recursionLevel);
		
		JSONObject entityNode = new JSONObject();
		try {
			entityNode.put("id", entity.getUniqueName());
			entityNode.put("text", label );
			entityNode.put("iconCls", iconCls);
			entityNode.put("qtip", tooltip );
			
			JSONObject nodeAttributes = new JSONObject();
			nodeAttributes.put("iconCls", iconCls);
			nodeAttributes.put("type", "entity");
			nodeAttributes.put("londDescription", londDescription);
			entityNode.put("attributes", nodeAttributes);
			entityNode.put("children", childrenNodes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nodes.put(entityNode);		
	}
	
	/**
	 * Gets the field nodes.
	 * 
	 * @param entity the entity
	 * @param recursionLevel the recursion level
	 * 
	 * @return the field nodes
	 */
	public JSONArray getFieldNodes(DataMartEntity entity,int recursionLevel) {		
		
		JSONArray children = new JSONArray();
		
		// add key fields
		List keyFields = entity.getKeyFields();
		keyFields = qbeTreeFilter.filterFields(getDataSource(), keyFields);
		
		Iterator keyFieldIterator = keyFields.iterator();
		while (keyFieldIterator.hasNext() ) {
			DataMartField field = (DataMartField)keyFieldIterator.next();
			JSONObject jsObject = getFieldNode(entity, field);
			if(jsObject != null) {
				children.put( jsObject );
			}
			
		}
		
		// add normal fields
		List normalFields = entity.getNormalFields();
		normalFields = qbeTreeFilter.filterFields(getDataSource(), normalFields);
		
		Iterator normalFieldIterator = normalFields.iterator();
		while (normalFieldIterator.hasNext() ) {
			DataMartField field = (DataMartField)normalFieldIterator.next();
			JSONObject jsObject = getFieldNode(entity, field);
			if(jsObject != null) {
				children.put( jsObject );
			}
		}
		
		// add calculated fields
		List calculatedFields = entity.getCalculatedFields();
		normalFields = qbeTreeFilter.filterFields(getDataSource(), normalFields);
		
		Iterator calculatedFieldIterator = calculatedFields.iterator();
		while (calculatedFieldIterator.hasNext() ) {
			DataMartCalculatedField field = (DataMartCalculatedField)calculatedFieldIterator.next();
			
			JSONObject jsObject = getCalculatedFieldNode(entity, field);
			if(jsObject != null) {
				children.put( jsObject );
			}
		}
		
		// add subentities
		JSONArray subentities = getSubEntitiesNodes(entity, children, recursionLevel);
		
		return children;		
	}
	
	/**
	 * Gets the field node.
	 * 
	 * @param parentEntity the parent entity
	 * @param field the field
	 * 
	 * @return the field node
	 */
	public  JSONObject getFieldNode(DataMartEntity parentEntity,
							 DataMartField field) {
		
		//DatamartProperties datamartProperties = dataSource.getDataMartProperties();
		String iconCls = field.getPropertyAsString("type");		
		String fieldLabel = geFieldLabel( field );
		String longDescription = QueryJSONSerializer.getFieldLongDescription( field, getDatamartLabels() );
		String fieldTooltip = geFieldTooltip( field );
		String entityLabel = geEntityLabel( parentEntity );
		
		writer.println( field.getUniqueName().replaceAll(":", "/") + "=");
		writer.println( field.getUniqueName().replaceAll(":", "/") + ".tooltip=");
		
		JSONObject fieldNode = new JSONObject();
		try {
			fieldNode.put("id", field.getUniqueName());
			fieldNode.put("text", fieldLabel);
			fieldNode.put("iconCls", iconCls);
			fieldNode.put("leaf", true);
			fieldNode.put("qtip", fieldTooltip);
			
			JSONObject nodeAttributes = new JSONObject();
			nodeAttributes.put("iconCls", iconCls);
			nodeAttributes.put("type", "field");
			nodeAttributes.put("entity", entityLabel);
			nodeAttributes.put("field", fieldLabel);
			nodeAttributes.put("longDescription", longDescription);
			fieldNode.put("attributes", nodeAttributes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fieldNode;
	}
	
	public  JSONObject getCalculatedFieldNode(DataMartEntity parentEntity, DataMartCalculatedField field) {

		String iconCls = "calculation";;//datamartProperties.getFieldIconClass( field );		
		String fieldLabel = geFieldLabel( field );
		String fieldTooltip = geFieldTooltip( field );
		String entityLabel = geEntityLabel( parentEntity );
		
		writer.println( field.getUniqueName().replaceAll(":", "/") + "=");
		writer.println( field.getUniqueName().replaceAll(":", "/") + ".tooltip=");
		
		JSONObject fieldNode = new JSONObject();
		try {
			fieldNode.put("id", field.getUniqueName());
			fieldNode.put("text", fieldLabel);
			fieldNode.put("leaf", true);
			fieldNode.put("iconCls", "calculation");
			fieldNode.put("qtip", fieldTooltip);
			
			JSONObject nodeAttributes = new JSONObject();
			nodeAttributes.put("iconCls", "calculation");
			if(field.isInLine()){
				nodeAttributes.put("type", "inLineCalculatedField");
			}else{
				nodeAttributes.put("type", "calculatedField");
			}
			
			nodeAttributes.put("entity", entityLabel);
			nodeAttributes.put("field", fieldLabel);
			
			JSONObject formState = new JSONObject();
			formState.put("alias", field.getName());
			formState.put("type", field.getType());
			formState.put("expression", field.getExpression());
			nodeAttributes.put("formState", formState);
			
			fieldNode.put("attributes", nodeAttributes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldNode;
	}
	
	
	/**
	 * Add Calculate Fields on the entity
	 * Control recursion level because calculate field are applied only at entity level not in dimension level.
	 * 
	 * @param tree the tree
	 * @param parentEntityNodeId the parent entity node id
	 * @param nodeCounter the node counter
	 * @param entity the entity
	 * 
	 * @return the int
	 */
	public int addCalculatedFieldNodes(IQbeTree tree, 
			   						   DataMartEntity entity,
			   						   int parentEntityNodeId, int nodeCounter) {
			
		/*
		List manualCalcultatedFieldForEntity = 
			getDatamartModel().getDataSource().getFormula().getManualCalculatedFieldsForEntity( entity.getType() );
			
		CalculatedField calculatedField = null;
		String fieldAction = null;
		
		Iterator manualCalculatedFieldsIterator = manualCalcultatedFieldForEntity.iterator();
		while (manualCalculatedFieldsIterator.hasNext()){
			calculatedField = (CalculatedField)manualCalculatedFieldsIterator.next();
			
			

			if (prefix != null){
				calculatedField.setFldCompleteNameInQuery(prefix + "." + calculatedField.getId());
			}else{
				calculatedField.setFldCompleteNameInQuery(calculatedField.getId());
			}
			
			
			fieldAction = getUrlGenerator().getActionUrlForCalculateField(calculatedField.getId(), entity.getName(), calculatedField.getFldCompleteNameInQuery());
			
			nodeCounter++;
			tree.addNode("" + nodeCounter, "" + parentEntityNodeId, 
					calculatedField.getFldLabel(),
					fieldAction,  
					calculatedField.getFldLabel(),
					"_self",
					getUrlGenerator().getResourceUrl("../img/cfield.gif"),
					getUrlGenerator().getResourceUrl("../img/cfield.gif"),
					"", "", "",  "", "");
		}
			
		return nodeCounter;
		*/
		return -1;
	}
	
	
	/**
	 * Gets the sub entities nodes.
	 * 
	 * @param entity the entity
	 * @param nodes the nodes
	 * @param recursionLevel the recursion level
	 * 
	 * @return the sub entities nodes
	 */
	public JSONArray getSubEntitiesNodes(DataMartEntity entity,
									JSONArray nodes,
								   int recursionLevel ) {
		
		List subEntities = entity.getSubEntities();
		subEntities = qbeTreeFilter.filterEntities(getDataSource(), subEntities);
		
	
		Iterator subEntitiesIterator = subEntities.iterator();
		while (subEntitiesIterator.hasNext()){
			DataMartEntity subentity = (DataMartEntity)subEntitiesIterator.next();
			if (subentity.getType().equalsIgnoreCase( entity.getType() ) || recursionLevel > 10) {
				// stop recursion 
			} else {
				addEntityNode(nodes,
							  subentity, 
								recursionLevel+1);
			}
		}
		
		return nodes;
	}
	
	
	
	
	

	

	/**
	 * Gets the datamart model.
	 * 
	 * @return the datamart model
	 */
	protected IDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the datamart model.
	 * 
	 * @param dataSource the new datamart model
	 */
	protected void setDatamartModel(IDataSource dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * Gets the qbe tree filter.
	 * 
	 * @return the qbe tree filter
	 */
	private QbeTreeFilter getQbeTreeFilter() {
		return qbeTreeFilter;
	}


	/**
	 * Sets the qbe tree filter.
	 * 
	 * @param qbeTreeFilter the new qbe tree filter
	 */
	private void setQbeTreeFilter(QbeTreeFilter qbeTreeFilter) {
		this.qbeTreeFilter = qbeTreeFilter;
	}


	public Locale getLocale() {
		return locale;
	}


	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	private ModelLabels getDatamartLabels() {
		return datamartLabels;
	}


	private void setDatamartLabels(ModelLabels datamartLabels) {
		this.datamartLabels = datamartLabels;
	}
}
