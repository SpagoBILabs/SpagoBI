/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.calculatedmember.CalculatedMemberManager;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;
import it.eng.spagobi.engines.whatif.dimension.SbiDimension;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;

@Path("/1.0/calculatedmembers")
public class CalculatedMembersResource extends AbstractWhatIfEngineService {
	public static transient Logger logger = Logger.getLogger(CalculatedMembersResource.class);
	public static final String DIVISION_SIGN = new String("spagobi.operator.division");

	/**
	 * Service to get Dimensions
	 * 
	 * @return the dimensions
	 */
	@GET
	@Path("/initializeData")
	@Produces("text/html; charset=UTF-8")
	public String initializeData() {
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		CalculatedMemberManager cm = new CalculatedMemberManager(ei);
		PivotModel model = ei.getPivotModel();
		List<SbiDimension> dimensions = new ArrayList<SbiDimension>();
		String serializedNames = new String();
		try {
			dimensions = cm.getDimensions(model);
			serializedNames = serialize(dimensions);
		} catch (Exception e) {
			logger.error("Error serializing dimensions");
			throw new SpagoBIEngineRestServiceRuntimeException(getLocale(), e);
		}
		logger.debug("OUT");
		return serializedNames;
	}

	/**
	 * Service to create the calculated member
	 * 
	 * @return the rendered pivot table
	 */
	@POST
	@Path("/execute/{calculateFieldName}/{calculateFieldFormula}/{parentMemberUniqueName}/{axisOrdinal}")
	@Produces("text/html; charset=UTF-8")
	public String execute(
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
		String calculateFieldFormulaParsed = new String();
		try {
			if (!calculateFieldFormula.isEmpty()) {
				calculateFieldFormulaParsed = calculateFieldFormula.replaceAll("\\{" + DIVISION_SIGN + "\\}", "/");
			}

		} catch (Exception e) {
			logger.error("Error parsing the formula. The original formula is " + calculateFieldFormula, e);
		}
		try {
			parentMember = CubeUtilities.getMember(ei.getPivotModel().getCube(), parentMemberUniqueName);
			axis = CubeUtilities.getAxis(axisOrdinal);
		} catch (OlapException e) {
			logger.error("Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
			throw new SpagoBIEngineRestServiceRuntimeException("sbi.olap.celculated.definition.error", getLocale(),
					"Error getting the parent of the calculated field. The unique name of the parent is " + parentMemberUniqueName, e);
		}
		try {
			cm.injectCalculatedIntoMdxQuery(calculateFieldName, calculateFieldFormulaParsed, parentMember, axis);
		} catch (SpagoBIEngineException e) {
			logger.error("Error injecting calculated member inside mdx query", e);
		}
		String table = renderModel(ei.getPivotModel());
		logger.debug("OUT");
		return table;
	}

}
