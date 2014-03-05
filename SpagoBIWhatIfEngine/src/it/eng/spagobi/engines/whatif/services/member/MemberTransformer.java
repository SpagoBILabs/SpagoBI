 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Monica Franceschini (monica.franceschini@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.member;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractRestService;
import it.eng.spagobi.engines.whatif.services.serializer.MemberJsonSerializer;
import it.eng.spagobi.engines.whatif.utilis.CubeUtilities;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.DrillReplace;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Path("/member")
public class MemberTransformer extends AbstractRestService {
	
	private static final String NODE_PARM = "node";
	
	@GET
	@Path("/drilldown/{axis}/{position}/{member}")
	public String drillDown(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("position") int positionPos, @PathParam("member") int memberPos){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		CellSet cellSet = model.getCellSet();

		//Axes of the resulting query.
		List<CellSetAxis> axes = cellSet.getAxes();

		//The ROWS axis
		CellSetAxis rowsOrColumns = axes.get(axisPos);

		//Member positions of the ROWS axis.
		List<Position> positions = rowsOrColumns.getPositions();

		Position p = positions.get(positionPos);


		List<Member> m = p.getMembers();
		Member m2 = m.get(memberPos);
		DrillReplace transform = model.getTransform(DrillReplace.class);
		transform.drillDown(m2);
		
		return renderModel(model);
	}
	
	@GET
	@Path("/filtertree/{hierarchy}")
	public String getMemberValue(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("hierarchy") String hierarchyUniqueName){
		Hierarchy hierarchy= null;
		String node;
		List<Member> list = new ArrayList<Member>(); 
		
		
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		logger.debug("Getting the node path from the request");
		//getting the node path from request
		node = req.getParameter(NODE_PARM);
		if(node==null){
			logger.debug("no node path found in the request");
			return null;
		}	
		logger.debug("The node path is "+node);
		
		logger.debug("Getting the hierarchy "+hierarchyUniqueName+" from the cube");
		try {
			NamedList<Hierarchy> hierarchies = model.getCube().getHierarchies();
			for(int i=0; i<hierarchies.size(); i++){
				String hName = hierarchies.get(i).getUniqueName();
				if(hName.equals(hierarchyUniqueName)){
					hierarchy = hierarchies.get(i);
					break;
				}
			}
		} catch (Exception e) {
			logger.debug("Error getting the hierarchy "+hierarchy,e);
			throw new SpagoBIEngineRuntimeException("Error getting the hierarchy "+hierarchy,e);
		}
		
		try {

			logger.debug("Getting the members of the first level of the hierarchy");
			Level l = hierarchy.getLevels().get(0);
			
			if(CubeUtilities.isRoot(node)){
				logger.debug("This is the root.. Returning the members of the first level of the hierarchy");
				logger.debug("OUT");
				list = l.getMembers();
			}else{
				logger.debug("getting the child members");
				Member m = CubeUtilities.getMember(hierarchy, node);
				if(m!=null){
					list = (List<Member>)m.getChildMembers();
				}
				
			}
		} catch (Exception e) {
			logger.debug("Error getting the member tree "+node,e);
			throw new SpagoBIEngineRuntimeException("Error getting the member tree "+node,e);
		}


		ObjectMapper mapper = new ObjectMapper();   
		SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
		simpleModule.addSerializer(Member.class, new MemberJsonSerializer());
		mapper.registerModule(simpleModule);
		try {
			return mapper.writeValueAsString(list);
		} catch (Exception e) {
			logger.error("Error serializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error serializing the MemberEntry",e);
		}

	}

	
	
}
