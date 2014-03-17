 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Monica Franceschini (monica.franceschini@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.member;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.services.serializer.SerializationManager;
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
import com.eyeq.pivot4j.transform.DrillExpandMember;
import com.eyeq.pivot4j.transform.DrillExpandPosition;
import com.eyeq.pivot4j.transform.DrillReplace;
import com.eyeq.pivot4j.ui.command.DrillDownCommand;

@Path("/v1.0/member")
public class MemberResource extends AbstractWhatIfEngineService {
	
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
		
		String drillType=ei.getModelConfig().getDrillType(); 
		
		if(drillType == null || drillType.equals(DrillDownCommand.MODE_POSITION)){
			DrillExpandPosition transform = model.getTransform(DrillExpandPosition.class);
			if(transform.canExpand(p, m2)){
				transform.expand(p, m2);
			}
		}else if(drillType != null && drillType.equals(DrillDownCommand.MODE_REPLACE)){
			
			DrillReplace transform = model.getTransform(DrillReplace.class);
			if(transform.canDrillDown(m2)){
				transform.drillDown(m2);
			}
		}else if(drillType != null && drillType.equals(DrillDownCommand.MODE_MEMBER)){
			DrillExpandMember transform = model.getTransform(DrillExpandMember.class);
			if(transform.canExpand(m2)){
				transform.expand(m2);
			}
		}				
		
		return renderModel(model);
	}
	@GET
	@Path("/drillup/{axis}/{position}/{member}")
	public String drillUp(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("position") int positionPos, @PathParam("member") int memberPos){

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
		Hierarchy hierarchy = m.get(memberPos).getHierarchy();
		
		String drillType=ei.getModelConfig().getDrillType();  
		
		if(drillType == null || drillType.equals(DrillDownCommand.MODE_POSITION)){
			DrillExpandPosition transform = model.getTransform(DrillExpandPosition.class);
			if(transform.canCollapse(p, m2)){
				transform.collapse(p, m2);
			}
		}else if(drillType != null && drillType.equals(DrillDownCommand.MODE_REPLACE)){
			DrillReplace transform = model.getTransform(DrillReplace.class);
			if(transform.canDrillUp(hierarchy)){
				transform.drillUp(hierarchy);
			}
		}else if(drillType != null && drillType.equals(DrillDownCommand.MODE_MEMBER)){
			DrillExpandMember transform = model.getTransform(DrillExpandMember.class);
			if(transform.canCollapse(m2)){
				transform.collapse(m2);
			}
		}
				
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


		
		

		try {
			return (String) serialize(list);
		} catch (Exception e) {
			logger.error("Error serializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error serializing the MemberEntry",e);
		}

	}
}
