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

import java.util.Date;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.DefaultWeightedAllocationAlgorithm;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;
import it.eng.spagobi.utilities.rest.RestUtilities;
import it.eng.spagobi.writeback4j.mondrian.CacheManager;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.olap4j.OlapDataSource;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.export.poi.ExcelExporter;

@Path("/1.0/model")
public class ModelResource extends AbstractWhatIfEngineService {
	
	public static transient Logger logger = Logger.getLogger(ModelResource.class);
	
	// input parameters
	public static final String EXPRESSION = "expression";
	
	private static final String exportFileName = "SpagoBIOlapExport";
	
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
			new SpagoBIEngineRestServiceRuntimeException("generic.error", this.getLocale(), e);
		}
		logger.debug("expression = [" + expression + "]");
		Double value = Double.valueOf(expression);
		SpagoBICellSetWrapper cellSetWrapper = (SpagoBICellSetWrapper) model
				.getCellSet();
		SpagoBICellWrapper cellWrapper = (SpagoBICellWrapper) cellSetWrapper
				.getCell(ordinal);
		CellTransformation transformation = new CellTransformation(value,
				cellWrapper.getValue(), cellWrapper,
				new DefaultWeightedAllocationAlgorithm(ei));
		cellSetWrapper.applyTranformation(transformation);
		String table = renderModel(model);
		
		
		logger.debug("OUT");
		return table;
	}
	
	@PUT
	@Path("/persistTransformations")
	public String persistTransformations(){
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		OlapDataSource olapDataSource = ei.getOlapDataSource();
		PivotModel model = ei.getPivotModel();

		SpagoBIPivotModel modelWrapper = (SpagoBIPivotModel) model;
		
		logger.debug("Persisting the modifications..");
		
		try {
			modelWrapper.persistTransformations();
		} catch (WhatIfPersistingTransformationException e) {
			logger.debug("Error persisting the modifications",e);
			throw new SpagoBIEngineRestServiceRuntimeException(e.getLocalizationmessage(), modelWrapper.getLocale(), "Error persisting modifications", e);
		}
		logger.debug("Modification persisted...");
		
		logger.debug("Cleaning the cache and restoring the model");
		CacheManager.flushCache(olapDataSource);
		String mdx = modelWrapper.getCurrentMdx();
		modelWrapper.setMdx(mdx);
		modelWrapper.initialize();
		logger.debug("Finish to clean the cache and restoring the model");
		
		String table = renderModel(model);
		logger.debug("OUT");
		return table;
	}
	
	@PUT
	@Path("/undo")
	public String undo() {
		logger.debug("IN");
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		SpagoBIPivotModel model = (SpagoBIPivotModel) ei.getPivotModel();
		model.undo();
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
		
		String mdx = model.getCurrentMdx();
		
		if(mdx==null){
			mdx = "";
		}

		logger.debug("OUT");
		return mdx;
		
	}
	
	
	/**
	 * Exports the actual model in a xls format.. Since it takes
	 * the actual model, it takes also the pendingg transformations (what you see it's what you get)
	 * @return the response with the file embedded
	 */
    @GET
    @Path("/exportXLS")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportXLS()
    {
    	
		WhatIfEngineInstance ei = getWhatIfEngineInstance();
		PivotModel model = ei.getPivotModel();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	ExcelExporter exporter = new ExcelExporter(out);
    	exporter.render(model);
    	
    	
        byte[] outputByte = out.toByteArray();

        String fileName = exportFileName+"-"+(new Date()).toLocaleString()+".xls";
        
    	return Response
                .ok(outputByte, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = "+fileName)
                .build();
    }
	
	
}

