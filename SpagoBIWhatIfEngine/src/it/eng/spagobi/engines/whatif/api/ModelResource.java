/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * @class ModelTransformer
 * 
 * Services that manage the model:
 * <ul>
 * <li>/model/mdx/{mdx}: executes the mdx query</li>
 * </ul>
 * 
 */
package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.DefaultWeightedAllocationAlgorithm;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceException;
import it.eng.spagobi.utilities.rest.RestUtilities;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.eyeq.pivot4j.PivotModel;

@Path("/1.0/model")
public class ModelResource extends AbstractWhatIfEngineService {
	
	public static transient Logger logger = Logger.getLogger(ModelResource.class);
	
	// input parameters
	public static final String EXPRESSION = "expression";
	
	/**
	 * Executes the mdx query. If the mdx is null it executes the query of the model
	 * @param mdx the query to execute
	 * @return the htm table representing the cellset
	 */
	@PUT
	@Path("/{mdx}")
	public String setMdx(@PathParam("mdx") String mdx){
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		if(!isNullOrEmpty(mdx)){
			logger.debug("Updating the query in the model");
			model.setMdx(mdx);
		}else{
			logger.debug("No query found");
		}
				
		String table = renderModel(model);
		logger.debug("OUT");
		return table;
		
	}
	
	@PUT
	@Path("/setValue/{ordinal}")
	public String setValue(@PathParam("ordinal") int ordinal){
		logger.debug("IN : ordinal = [" + ordinal + "]");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		String expression = null;
		try {
			JSONObject json = RestUtilities.readBodyAsJSONObject(getServletRequest());
			expression = json.getString( EXPRESSION );
		} catch (Exception e) {
			new SpagoBIEngineRestServiceException("generic.error", this.getLocale(), e);
		}
		logger.debug("expression = [" + expression + "]");
		Double value = Double.valueOf(expression);
		SpagoBICellSetWrapper cellSetWrapper = (SpagoBICellSetWrapper) model
				.getCellSet();
		SpagoBICellWrapper cellWrapper = (SpagoBICellWrapper) cellSetWrapper
				.getCell(ordinal);
		CellTransformation transformation = new CellTransformation(value,
				cellWrapper.getValue(), cellWrapper.getMembers(),
				new DefaultWeightedAllocationAlgorithm());
		cellSetWrapper.applyTranformation(transformation);
		String table = renderModel(model);
		logger.debug("OUT");
		return table;
	}
	
	/**
	 * Gets the active mdx statement
	 * @return the mdx active statement
	 */
	@GET
	public String getMdx(){
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		String mdx = model.getMdx();
		
		if(mdx==null){
			mdx = "";
		}
		
		
		logger.debug("OUT");
		return mdx;
		
	}
}

