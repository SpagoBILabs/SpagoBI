/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * @class HierarchyResource
 * 
 * Services that manage the hierarchies of the model:
 * 
 */
package it.eng.spagobi.engines.whatif.services.hierarchy;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.utilis.CubeUtilities;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.mdx.MdxStatement;
import com.eyeq.pivot4j.query.QueryAdapter;
import com.eyeq.pivot4j.transform.ChangeSlicer;
import com.eyeq.pivot4j.transform.impl.ChangeSlicerImpl;

@Path("/1.0/hierarchy")
public class HierarchyResource extends AbstractWhatIfEngineService {
	
	
	public static transient Logger logger = Logger.getLogger(HierarchyResource.class);
	
	@GET
	@Path("/slice/{hierarchy}/{member}/{multi}")
	public String addSlicer(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("hierarchy") String hierarchyName, @PathParam("member") String memberName, @PathParam("multi") boolean multiSelection){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();
		Hierarchy hierarchy =null;
		Member member =null;
		
		
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		ChangeSlicer ph = new ChangeSlicerImpl(qa, connection);
	

		try {
			hierarchy = CubeUtilities.getHierarchy(model.getCube(), hierarchyName);
			member = CubeUtilities.getMember(hierarchy, memberName);
		} catch (OlapException e) {
			logger.debug("Error getting the member "+memberName+" from the hierarchy "+hierarchyName,e);
			throw new SpagoBIEngineRuntimeException("Error getting the member "+memberName+" from the hierarchy "+hierarchyName,e);
		}
		
		
		List<org.olap4j.metadata.Member> slicers = ph.getSlicer(hierarchy);
		
		if(!multiSelection){
			slicers.clear();
		}
		
		slicers.add(member);
		ph.setSlicer(hierarchy,slicers);


		MdxStatement s = qa.updateQuery();
		model.setMdx(s.toMdx());

		return renderModel(model);
	}

}
