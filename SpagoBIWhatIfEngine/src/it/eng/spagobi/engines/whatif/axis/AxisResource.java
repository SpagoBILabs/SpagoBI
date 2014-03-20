/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * @class AxisResource
 * 
 * Provides services to manage the axis resource
 * 
 */
package it.eng.spagobi.engines.whatif.axis;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.SwapAxes;

@Path("/1.0/axis")
public class AxisResource extends AbstractWhatIfEngineService {
	
	public static transient Logger logger = Logger.getLogger(AxisResource.class);
	
	private AxisDimensionManager axisBusiness;
	
	
	
	private AxisDimensionManager getAxisBusiness() {
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
				
		if(axisBusiness==null){
			axisBusiness = new AxisDimensionManager(ei);
		}
		return axisBusiness;
	}


	/**
	 * Service to swap the axis
	 * @return the rendered pivot table
	 */
	@PUT
	@Path("/swap")
	public String swapAxis(){
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		SwapAxes transform = model.getTransform(SwapAxes.class);
		if(transform.isSwapAxes()){
			transform.setSwapAxes(false);
		}else{
			transform.setSwapAxes(true);
		}
				
		String table = renderModel(model);
		logger.debug("OUT");
		return table;
		
	}
	
	/**
	 * Service to move an hierarchy from an axis to another
	 * @param req the HttpServletRequest
	 * @param fromAxisPos the source axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param toAxisPos the destination axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param hierarchyName the unique name of the hierarchy to move
	 * @return the rendered pivot table
	 */
	@PUT
	@Path("/{fromAxis}/moveHierarchy/{hierarchy}/{toAxis}")
	public String moveHierarchyHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("fromAxis") int fromAxisPos, @PathParam("toAxis") int toAxisPos,@PathParam("hierarchy") String hierarchyName){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();

		getAxisBusiness().moveHierarchy(fromAxisPos, toAxisPos, hierarchyName);
	
		return renderModel(model);
	}
	
	/**
	 * Service to swap 2 hierarchies in an axis
	 * @param req the HttpServletRequest
	 * @param axisPos the source axis(0 for rows, 1 for columns, -1 for filters)  
	 * @param hierarchyPos1 the position of the first hierarchy in the axis
	 * @param hierarchyPos2 the position of the second hierarchy in the axis
	 * @return the rendered pivot table
	 */
	@PUT
	@Path("/{axis}/swaphierarchies/{hierarchy1}/{hierarchy2}")
	public String swapHierarchies(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy1") int hierarchyPos1, @PathParam("hierarchy2") int hierarchyPos2){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();	
		
		getAxisBusiness().swapHierarchies(axisPos, hierarchyPos1, hierarchyPos2);
		
		return renderModel(model);
	}
	
	
//	
//	/**
//	 * Service to swap the axis
//	 * @return the rendered pivot table
//	 * @param req
//	 * @param axisPos
//	 * @param hierarchyName
//	 * @return
//	 */
//	@PUT
//	@Path("/{axis}/removehierarchy/{hierarchy}")
//	public String removeHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){
//
//		WhatIfEngineInstance ei = getWhatIfEngineInstance();
//		PivotModel model = ei.getPivotModel();
//		OlapConnection connection = ei.getOlapConnection();
//
//		QueryAdapter qa = new QueryAdapter(model);
//		qa.initialize();
//		
//		//AxisBusiness ab = new AxisBusiness();
//		axisBusiness.removeHierarchy(qa, connection, axisPos, hierarchyName);
//	
//		MdxStatement s = qa.updateQuery();
//		model.setMdx(s.toMdx());
//
//		return renderModel(model);
//	}
//	
//	@PUT
//	@Path("/{axis}/placehierarchy/{hierarchy}")
//	public String addHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){
//
//		WhatIfEngineInstance ei = getWhatIfEngineInstance();
//		PivotModel model = ei.getPivotModel();
//		OlapConnection connection = ei.getOlapConnection();
//
//		QueryAdapter qa = new QueryAdapter(model);
//		qa.initialize();
//		
//		//AxisBusiness ab = new AxisBusiness();
//		axisBusiness.addHierarchy(qa, connection, axisPos, hierarchyName);
//	
//		MdxStatement s = qa.updateQuery();
//		model.setMdx(s.toMdx());
//		
//		return renderModel(model);
//	}
//	
	

}
