package it.eng.spagobi.engines.whatif.services.table;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractRestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.SwapAxes;

@Path("/table")
public class TableAxisTransformer extends AbstractRestService {
	
	public static transient Logger logger = Logger.getLogger(TableAxisTransformer.class);
	
	/**
	 * Swap the axes
	 * @return
	 */
	@GET
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

}
