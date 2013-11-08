/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.statement.graph.cover;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.statement.graph.PathInspector;
import it.eng.qbe.statement.graph.QueryGraphBuilder;
import it.eng.qbe.statement.graph.bean.QueryGraph;
import it.eng.qbe.statement.graph.bean.Relationship;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;

public class ShortestPathsCoverGraph extends AbstractDefaultCover{

	public static transient Logger logger = Logger.getLogger(ShortestPathsCoverGraph.class);

	
	public QueryGraph getCoverGraph( Graph<IModelEntity, Relationship> rootEntitiesGraph, Set<IModelEntity> entities) {

		Iterator<IModelEntity> it = entities.iterator();
		Set<Relationship> connectingRelatiosnhips = new HashSet<Relationship>();

		Set<IModelEntity> connectedEntities = new HashSet<IModelEntity>();
		connectedEntities.add( it.next() );

		while(it.hasNext()) {
			IModelEntity entity = it.next();
			if(connectedEntities.contains(entity)) continue;
			GraphPath minimumPath = null;
			double minPathLength = Double.MAX_VALUE;
			for(IModelEntity connectedEntity : connectedEntities) {
				
				//check the path from entity to connectedEntity
				DijkstraShortestPath dsp = new DijkstraShortestPath(rootEntitiesGraph, entity, connectedEntity);
				double pathLength = dsp.getPathLength();
				if(minPathLength > pathLength) {
					minPathLength = pathLength;
					minimumPath = dsp.getPath();
				}
				
				if(rootEntitiesGraph instanceof DirectedGraph){
					//check the path from connectedEntity to entity
					DijkstraShortestPath dsp2 = new DijkstraShortestPath(rootEntitiesGraph, connectedEntity, entity);
					double pathLength2 = dsp2.getPathLength();
					if(minPathLength > pathLength2) {
						minPathLength = pathLength2;
						minimumPath = dsp2.getPath();
					}
				} 
				
			}
			if(minimumPath!=null){
				List<Relationship> relationships = (List<Relationship>)minimumPath.getEdgeList();
				connectingRelatiosnhips.addAll(relationships);
				for(Relationship relatioship: relationships) {
					connectedEntities.add( rootEntitiesGraph.getEdgeSource(relatioship) );
					connectedEntities.add( rootEntitiesGraph.getEdgeTarget(relatioship) );
				}
			}

		}

		for(Relationship r : connectingRelatiosnhips) {
			IModelEntity source = rootEntitiesGraph.getEdgeSource(r);
			IModelEntity target = rootEntitiesGraph.getEdgeTarget(r);
		}





		QueryGraphBuilder qb = new QueryGraphBuilder();
		QueryGraph monimumGraph = qb.buildGraphFromEdges(connectingRelatiosnhips);

		return monimumGraph;
	}
	
	public Map<IModelEntity, Set<GraphPath<IModelEntity, Relationship>>>  getConnectingRelatiosnhips( Graph<IModelEntity, Relationship> rootEntitiesGraph, Set<IModelEntity> entities) {
		QueryGraph monimumGraph = getCoverGraph(rootEntitiesGraph, entities);
		return getConnectingRelatiosnhips(monimumGraph, entities);
	}
	
	public Map<IModelEntity, Set<GraphPath<IModelEntity, Relationship>>>  getConnectingRelatiosnhips( QueryGraph monimumGraph, Set<IModelEntity> entities) {
		PathInspector pi = new PathInspector(monimumGraph, monimumGraph.vertexSet());
		Map<IModelEntity, Set<GraphPath<IModelEntity, Relationship> >> minimumPaths = pi.getAllEntitiesPathsMap();

		return minimumPaths;
	}
	

}
