/* SpagoBI, the Open Source Business Intelligence suite

* Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
* If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
* @author Alberto Ghedin (alberto.ghedin@eng.it)
*/
package it.eng.spagobi.engines.whatif.services.table;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractRestService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapConnection;
import org.olap4j.metadata.Hierarchy;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.mdx.MdxStatement;
import com.eyeq.pivot4j.query.QueryAdapter;
import com.eyeq.pivot4j.transform.PlaceHierarchiesOnAxes;
import com.eyeq.pivot4j.transform.impl.PlaceHierarchiesOnAxesImpl;

@Path("/table")
public class TableHierachiesTransformer extends AbstractRestService {

	public static transient Logger logger = Logger.getLogger(TableHierachiesTransformer.class);
	
	@GET
	@Path("/removehierarchy/{axis}/{hierarchy}")
	public String removeHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
		
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);
		
		ph.removeHierarchy(rowsOrColumns.getAxisOrdinal(), model.getCube().getHierarchies().get(0));

	

		return renderModel(model);
	}
	
	@GET
	@Path("/palcehierarchy/{axis}/{hierarchy}")
	public String addHierarchy(@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("axis") int axisPos, @PathParam("hierarchy") String hierarchyName){

		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		OlapConnection connection = ei.getOlapConnection();

		
		QueryAdapter qa = new QueryAdapter(model);
		qa.initialize();
		
		PlaceHierarchiesOnAxes ph = new PlaceHierarchiesOnAxesImpl(qa, connection);
		
		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axes = cellSet.getAxes();
		CellSetAxis rowsOrColumns = axes.get(axisPos);


		List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
		hierarchies.add(model.getCube().getHierarchies().get(3));
		
		ph.placeHierarchies(rowsOrColumns.getAxisOrdinal(),hierarchies ,true);
		MdxStatement s = qa.updateQuery();
		model.setMdx(s.toMdx());

		return renderModel(model);
	}
}
