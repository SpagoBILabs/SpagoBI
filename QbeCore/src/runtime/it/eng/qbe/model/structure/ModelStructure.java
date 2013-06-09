/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.model.structure;

import it.eng.qbe.model.structure.ModelStructure.RootEntitiesGraph.Relationship;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ModelStructure extends AbstractModelObject implements IModelStructure {
	
	
	public static class RootEntitiesGraph {
		
		public static class Relationship extends DefaultEdge {
	
			private static final long serialVersionUID = 1L;
			
			private String type;
			
			List<IModelField> sourceFields;
			List<IModelField> targetFields;
			
			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}
			
			public IModelEntity getSourceEntity() {
				return (IModelEntity)this.getSource();
			} 
			
			public List<IModelField> getSourceFields() {
				return sourceFields;
			}

			public void setSourceFields(List<IModelField> sourceFields) {
				this.sourceFields = sourceFields;
			}
			
			public IModelEntity getTargetEntity() {
				return (IModelEntity)this.getTarget();
			}

			public List<IModelField> getTargetFields() {
				return targetFields;
			}

			public void setTargetFields(List<IModelField> targetFields) {
				this.targetFields = targetFields;
			}
			
			
		}
	
		Map<String, IModelEntity> rootEntitiesMap;
		UndirectedGraph<IModelEntity, DefaultEdge> rootEntitiesGraph;
		
		public RootEntitiesGraph() {
			rootEntitiesMap = new HashMap<String, IModelEntity>();
			rootEntitiesGraph = new SimpleGraph<IModelEntity, DefaultEdge>(Relationship.class);
		}
		
		public void addRootEntity(IModelEntity entity) {
			rootEntitiesMap.put(entity.getUniqueName(), entity);
			rootEntitiesGraph.addVertex(entity);
		}
		
		public IModelEntity getRootEntityByName(String entityName) {
			return rootEntitiesMap.get(entityName);
		}
		
		public List<IModelEntity> getAllRootEntities() {
			List<IModelEntity> list = new ArrayList<IModelEntity>();
			Iterator<String> it = rootEntitiesMap.keySet().iterator();
			while(it.hasNext()) {
				String entityName = it.next();
				// TODO replace with this ...
				//list.add( entities.get(entityName).getCopy() );
				list.add( rootEntitiesMap.get(entityName) );
			}
			return list;
		}
		
		/**
		 * @return true if the root entities passed as input belongs to the same connected subgraph
		 */
		public boolean areRootEntitiesConnected(Set<IModelEntity> entities) {
			boolean areConnected = true;
			if(entities.size() > 1) {
				ConnectivityInspector inspector = new ConnectivityInspector(rootEntitiesGraph);
				Iterator<IModelEntity> it = entities.iterator();
				IModelEntity entity = it.next();
				Set<DefaultEdge> edges = rootEntitiesGraph.edgesOf(entity);
				System.out.println("Number of edges: " + edges.size());
				for(DefaultEdge edge : edges) System.out.println(edge);
				Set<IModelEntity> connectedEntitySet = inspector.connectedSetOf(entity);
				while(it.hasNext()) {
					entity = it.next();
					if(connectedEntitySet.contains(entity) == false) {
						areConnected = false;
						break;
					}
				}
			}
			
			return areConnected;
		}
		
//		public Relationship addRelationship(String fromEntityName, String toEntityName, String type) {
//			IModelEntity fromEntity = getRootEntityByName(fromEntityName);
//			IModelEntity toEntity = getRootEntityByName(toEntityName);	
//			return addRelationship(fromEntity, toEntity, type);
//		}

		public Relationship addRelationship(IModelEntity fromEntity, List<IModelField> fromFields,
				IModelEntity toEntity, List<IModelField> toFields, String type) {
			Relationship relationship = new RootEntitiesGraph.Relationship();
			relationship.setType(type); // MANY_TO_ONE : FK da 1 a 2
			relationship.setSourceFields(fromFields);
			relationship.setTargetFields(toFields); 
			boolean added = rootEntitiesGraph.addEdge(fromEntity, toEntity, relationship);
			return added? relationship: null;
		}
		
		public Set<Relationship> getConnectingRelatiosnhips(Set<IModelEntity> entities) {
			
			Set<Relationship> connectingRelatiosnhips = new HashSet<Relationship>();
			
			Set<IModelEntity> connectedEntities = new HashSet<IModelEntity>();
			
			Iterator<IModelEntity> it = entities.iterator();
			connectedEntities.add( it.next() );
			
			while(it.hasNext()) {
				IModelEntity entity = it.next();
				if(connectedEntities.contains(entity)) continue;
				GraphPath minimumPath = null;
				double minPathLength = Double.MAX_VALUE;
				for(IModelEntity connectedEntity : connectedEntities) {
					DijkstraShortestPath dsp = new DijkstraShortestPath(rootEntitiesGraph, entity, connectedEntity);
					double pathLength = dsp.getPathLength();
					if(minPathLength > pathLength) {
						minPathLength = pathLength;
						minimumPath = dsp.getPath();
					}
				}
				List<Relationship> relationships = (List<Relationship>)minimumPath.getEdgeList();
				connectingRelatiosnhips.addAll(relationships);
				for(Relationship relatioship: relationships) {
					connectedEntities.add( rootEntitiesGraph.getEdgeSource(relatioship) );
					connectedEntities.add( rootEntitiesGraph.getEdgeTarget(relatioship) );
				}
			}
			
			for(Relationship r : connectingRelatiosnhips) {
				IModelEntity source = rootEntitiesGraph.getEdgeSource(r);
				IModelEntity target = rootEntitiesGraph.getEdgeTarget(r);
				System.err.println(source.getName() + " -> " + target.getName());
			}
			
			return connectingRelatiosnhips;
		}
	}
	
	
	
	public static class ModelRootEntitiesMap {
		protected Map<String, RootEntitiesGraph> modelRootEntitiesMap;
		
		public ModelRootEntitiesMap() {
			modelRootEntitiesMap = new HashMap<String, RootEntitiesGraph>();
		}
		
		public Set<String> getModelNames() {
			return modelRootEntitiesMap.keySet();
		}
		
		public RootEntitiesGraph getRootEntities(String modelName) {
			return modelRootEntitiesMap.get(modelName);
		}

		public void setRootEntities(String modelName, RootEntitiesGraph modelRootEntities) {
			modelRootEntitiesMap.put(modelName, modelRootEntities);
		}
	}
	
	protected long nextId;	
	
	protected ModelRootEntitiesMap modelRootEntitiesMap;
	//protected Map<String, Map<String,IModelEntity>> rootEntities;	// modelName->(entityUniqueName->entity)
	
	protected Map<String, IModelEntity> entities; //entityUniqueName->entity
	protected Map<String, IModelField> fields; // uniqueName -> field	
	protected Map<String, List<ModelCalculatedField>> calculatedFields; // entity uniqueName -> fields' list
	
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================
	
	/**
	 * Instantiate a new empty ModelStructure object
	 */
	public ModelStructure() {
		nextId = 0;
		id = getNextId();
		name = "Generic Model";
		
		//rootEntities = new HashMap<String, Map<String,IModelEntity>>();
		modelRootEntitiesMap = new ModelRootEntitiesMap();
		
		entities = new HashMap<String, IModelEntity>();
		fields = new HashMap<String, IModelField>();
		calculatedFields = new  HashMap<String, List<ModelCalculatedField>>();
		initProperties();
		
	}
	
	
	// =========================================================================
	// ACCESORS 
	// =========================================================================
	
	/**
	 * Gets the next id.
	 * 
	 * @return the next id
	 */
	public long getNextId() {
		return nextId++;
	}
	
	public Set<String> getModelNames() {
		return modelRootEntitiesMap.getModelNames();
	}
	
	public RootEntitiesGraph getRootEntitiesGraph(String modelName, boolean createIfNotExist) {
		RootEntitiesGraph rootEntitiesGraph;
		
		rootEntitiesGraph = modelRootEntitiesMap.getRootEntities(modelName);
		if (rootEntitiesGraph == null && createIfNotExist == true) {
			rootEntitiesGraph = new RootEntitiesGraph();
			modelRootEntitiesMap.setRootEntities(modelName, rootEntitiesGraph);
		}
		
		return rootEntitiesGraph;
	}
	
	// Root Entities -----------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addRootEntity(String modelName, String name, String path, String role, String type)
	 */
	public IModelEntity addRootEntity(String modelName, String name, String path, String role, String type) {
		IModelEntity entity = new ModelEntity(name, path, role, type, this);
		addRootEntity(modelName, entity);
		return entity;
	}
	
	public void addRootEntity(String modelName, IModelEntity entity) {
		RootEntitiesGraph rootEntitiesGraph;
		rootEntitiesGraph = getRootEntitiesGraph(modelName, true);
		rootEntitiesGraph.addRootEntity(entity);
		addEntity(entity);
		entity.setModelName(modelName);
	}
	
	/**
	 * NOTE: At the moment is not possible to have connected entites that belong to different models
	 */
	public boolean areRootEntitiesConnected(Set<IModelEntity> entities) {
		RootEntitiesGraph rootEntitiesGraph;
		
		// check if all entities belong to the same model
		String modelName = null;
		for(IModelEntity entity : entities) {
			Assert.assertTrue(entity.getParent() == null, "Entity [" + entity.getUniqueName() + "] is not a root entity");
			Assert.assertTrue(entity.getModelName() != null, "Entity [" + entity.getUniqueName() + "] does not belong to any model");
			if(modelName == null) {
				modelName = entity.getModelName();
			} else {
				if(modelName.equals( entity.getModelName() ) == false) return false;
			}
		}
		
		rootEntitiesGraph = getRootEntitiesGraph(modelName, false);
		
		return rootEntitiesGraph.areRootEntitiesConnected(entities);
	}
	
	public Set<Relationship> getRootEntitiesConnections(Set<IModelEntity> entities) {
		RootEntitiesGraph rootEntitiesGraph;
		Iterator<IModelEntity> it = entities.iterator();
		IModelEntity entity = it.next();
		rootEntitiesGraph = getRootEntitiesGraph(entity.getModelName(), true);
		return rootEntitiesGraph.getConnectingRelatiosnhips(entities); 
	}
	
	// Root Entities Relationship -------------------------------------------------

	
	/* (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addRootEntityRelationship(it.eng.qbe.model.structure.IModelEntity, it.eng.qbe.model.structure.IModelEntity, java.lang.String)
	 */
	public void addRootEntityRelationship(String modelName, 
			IModelEntity fromEntity, List<IModelField> fromFields,
			IModelEntity toEntity, List<IModelField> toFields,
			String type) {
		RootEntitiesGraph rootEntitiesGraph;
		
		rootEntitiesGraph = modelRootEntitiesMap.getRootEntities(modelName);
		if (rootEntitiesGraph == null) {
			rootEntitiesGraph = new RootEntitiesGraph();
			modelRootEntitiesMap.setRootEntities(modelName, rootEntitiesGraph);
		}
		
		rootEntitiesGraph.addRelationship(fromEntity, fromFields, toEntity, toFields, type);
	}
	
	
	
	// Entities ---------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(String modelName, String entityName)
	 */
	public IModelEntity getRootEntity(String modelName, String entityName) {
		//Map<String, IModelEntity> modelRootEntities = rootEntities.get(modelName);
		RootEntitiesGraph rootEntitiesGraph = modelRootEntitiesMap.getRootEntities(modelName);
		return rootEntitiesGraph == null ? null : (IModelEntity)rootEntitiesGraph.getRootEntityByName(entityName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(IModelEntity entity)
	 */
	public IModelEntity getRootEntity(IModelEntity entity) {
		if (entity == null) {
			return null;
		}
		IModelEntity toReturn = null;
		Iterator<String> keysIt = getModelNames().iterator();
		while (keysIt.hasNext()) {
			String modelName = keysIt.next();
			IModelEntity rootEntity = getRootEntity(entity, modelName);
			if (rootEntity != null) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(IModelEntity entity, String modelName) 
	 */
	public IModelEntity getRootEntity(IModelEntity entity, String modelName) {
		if (entity == null) {
			return null;
		}
		IModelEntity toReturn = null;
		List<IModelEntity> rootEntities = getRootEntities(modelName);
		Iterator<IModelEntity> rootEntitiesIt = rootEntities.iterator();
		while (rootEntitiesIt.hasNext()) {
			IModelEntity rootEntity = (IModelEntity) rootEntitiesIt.next();
			if (entity.getType().equals(rootEntity.getType())) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntityIterator(String modelName)
	 */
	public Iterator<IModelEntity> getRootEntityIterator(String modelName) {
		return getRootEntities(modelName).iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntities(String modelName)
	 */
	public List<IModelEntity> getRootEntities(String modelName) {
		List<IModelEntity> list;
		RootEntitiesGraph rootEntitiesGraph;
		rootEntitiesGraph = modelRootEntitiesMap.getRootEntities(modelName);
		list = rootEntitiesGraph.getAllRootEntities();
		return list;
	}	
	

	// Entities -----------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#ddEntity(IModelEntity entity) 
	 */
	public void addEntity(IModelEntity entity) {
		entities.put(entity.getUniqueName(), entity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getEntity(String entityUniqueName)
	 */
	public IModelEntity getEntity(String entityUniqueName) {
		IModelEntity entity = (IModelEntity)entities.get(entityUniqueName);
		return entity;
	}
	
	// Fields -----------------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addField(IModelField field)
	 */
	public void addField(IModelField field) {
		fields.put(field.getUniqueName(), field);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure# getField(String fieldUniqueName)
	 */
	public IModelField getField(String fieldUniqueName) {
		IModelField field = (IModelField)fields.get(fieldUniqueName);
		return field;
	}
	
	// Calculated Fields ----------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getCalculatedFields()
	 */
	public Map<String, List<ModelCalculatedField>> getCalculatedFields() {
		return calculatedFields;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getCalculatedFieldsByEntity(String entityName)
	 */
	public List<ModelCalculatedField> getCalculatedFieldsByEntity(String entityName) {
		List<ModelCalculatedField> result;
		
		result = new ArrayList<ModelCalculatedField>();
		if(calculatedFields.containsKey(entityName)) {
			result.addAll( calculatedFields.get(entityName) );
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields)
	 */
	public void setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addCalculatedField(String entityName, ModelCalculatedField calculatedFiled)
	 */
	public void addCalculatedField(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFiledsOnTargetEntity;
		if(!calculatedFields.containsKey(entityName)) {
			calculatedFields.put(entityName, new ArrayList<ModelCalculatedField>());
		}
		calculatedFiledsOnTargetEntity = calculatedFields.get(entityName);	
		List<ModelCalculatedField> toRemove = new ArrayList<ModelCalculatedField>();
		for(int i = 0; i < calculatedFiledsOnTargetEntity.size(); i++) {
			ModelCalculatedField f = (ModelCalculatedField)calculatedFiledsOnTargetEntity.get(i);
			if(f.getName().equals(calculatedFiled.getName())) {
				toRemove.add(f);
			}
		}
		for(int i = 0; i < toRemove.size(); i++) {
			calculatedFiledsOnTargetEntity.remove(toRemove.get(i));
		}
		calculatedFiledsOnTargetEntity.add(calculatedFiled);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled)
	 */
	public void removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFieldsOnTargetEntity;
		
		calculatedFieldsOnTargetEntity = calculatedFields.get(entityName);	
		if(calculatedFieldsOnTargetEntity != null) {
			calculatedFieldsOnTargetEntity.remove(calculatedFiled);
		}
	}


	
}
