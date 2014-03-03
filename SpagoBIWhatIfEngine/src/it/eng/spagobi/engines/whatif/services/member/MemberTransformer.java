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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.DrillReplace;

@Path("/member")
public class MemberTransformer extends AbstractRestService {

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
}
