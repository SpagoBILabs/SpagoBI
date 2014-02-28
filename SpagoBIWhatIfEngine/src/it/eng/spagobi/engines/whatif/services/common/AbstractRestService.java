/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.services.common;

import it.eng.spagobi.engines.whatif.services.AbstractWhatIfEngineService;
import it.eng.spagobi.pivot4j.ui.WhatIfHTMLRenderer;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public abstract class AbstractRestService extends AbstractWhatIfEngineService{

	public static transient Logger logger = Logger.getLogger(AbstractRestService.class);

	/**
	 * Renders the model and return the HTML table
	 * @param request
	 * @return the String that contains the HTML table
	 */
	public String renderModel(PivotModel model){

		logger.debug("IN");
		String table="";
		
		
		logger.debug("Creating the renderer");
		StringWriter writer = new StringWriter();
		WhatIfHTMLRenderer renderer = new WhatIfHTMLRenderer(writer);
		
		logger.debug("Setting the properties of the renderer");
		renderer.setShowDimensionTitle(false); // Optionally hide the dimension title headers.
		renderer.setShowParentMembers(true); // Optionally make the parent members visible.
		renderer.setCellSpacing(0);
		renderer.setRowHeaderStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setColumnHeaderStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setCornerStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setCellStyleClass("x-grid-cell x-grid-td x-grid-cell-gridcolumn-1014 x-unselectable x-grid-cell-inner  x-grid-row-alt x-grid-data-row x-grid-with-col-lines x-grid-cell x-pivot-cell");
		renderer.setTableStyleClass("x-panel-body x-grid-body x-panel-body-default x-box-layout-ct x-panel-body-default x-pivot-table");

		logger.debug("Rendering the model");
		renderer.render(model);

		
		try {
			writer.flush();
			writer.close();
			table = writer.getBuffer().toString();
		} catch (IOException e) {
			logger.error("Error serializing the table",e);
			throw new SpagoBIEngineRuntimeException("Error serializing the table",e);
		}

		logger.debug("OUT");
		
		return table;
	}


}
