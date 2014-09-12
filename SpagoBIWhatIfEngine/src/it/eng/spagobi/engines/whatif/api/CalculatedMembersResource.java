package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.calculatedmember.CalculatedMemberManager;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Member;

@Path("/1.0/calculatedmembers")
public class CalculatedMembersResource extends AbstractWhatIfEngineService {
	public static transient Logger logger = Logger.getLogger(CalculatedMembersResource.class);

	@POST
	@Path("/execute/{calculateFieldName}/{calculateFieldFormula}/{parentMemberUniqueName}/{axisOrdinal}")
	@Produces("text/html; charset=UTF-8")
	public String execute(@javax.ws.rs.core.Context HttpServletRequest req,
			@PathParam("calculateFieldName") String calculateFieldName,
			@PathParam("calculateFieldFormula") String calculateFieldFormula,
			@PathParam("parentMemberUniqueName") String parentMemberUniqueName,
			@PathParam("axisOrdinal") int axisOrdinal) {
		logger.debug("IN");

		Member parentMember;

		logger.debug("expression= " + calculateFieldFormula);
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		CalculatedMemberManager cm = new CalculatedMemberManager(ei);
		Axis axis;

		try {
			parentMember = CubeUtilities.getMember(ei.getPivotModel().getCube(), parentMemberUniqueName);
			axis = CubeUtilities.getAxis(axisOrdinal);
		} catch (OlapException e) {
			logger.error("Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
			throw new SpagoBIEngineRestServiceRuntimeException("sbi.olap.celculated.definition.error", getLocale(),
					"Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
		}

		try {
			cm.injectCalculatedIntoMdxQuery(calculateFieldName, calculateFieldFormula, parentMember, axis);
		} catch (SpagoBIEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String table = renderModel(ei.getPivotModel());
		logger.debug("OUT");
		return table;
	}
}
