 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * 
 * Renderer of the model...
 * It uses the WhatIfHTMLRenderer to render the table in a HTML format
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.serializer;

import it.eng.spagobi.pivot4j.ui.WhatIfHTMLRenderer;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.metadata.Hierarchy;

import com.eyeq.pivot4j.PivotModel;

public class PivotJsonHTMLSerializer {

	public static transient Logger logger = Logger.getLogger(PivotJsonHTMLSerializer.class);
	
	private static final String HIERARCHY_NAME= "name";
	private static final String COLUMNS= "columns";
	private static final String ROWS= "rows";
	private static final String TABLE= "table";
	
	public static String renderModel(PivotModel model){

		logger.debug("IN");
		String table="";
		JSONObject pivot = new JSONObject();
		
		
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

		CellSet cellSet = model.getCellSet();
		List<CellSetAxis> axis = cellSet.getAxes();
		
		try {
			pivot.put(TABLE, table);
			pivot.put(ROWS, serializeAxis(axis, Axis.ROWS));
			pivot.put(COLUMNS,  serializeAxis(axis, Axis.COLUMNS));
			
		} catch (Exception e) {
			logger.error("Error serializing the pivot table", e);
			throw new SpagoBIRuntimeException("Error serializing the pivot table", e);
		}

		
		
		
		logger.debug("OUT");
		
		return pivot.toString();
	}
	
	private static JSONArray serializeAxis(List<CellSetAxis> axis, Axis type) throws JSONException{
		CellSetAxis aAxis= axis.get(0);
		if(!aAxis.getAxisOrdinal().equals(type)){
			aAxis = axis.get(1);
		}
		List<Hierarchy> hierarchies = aAxis.getAxisMetaData().getHierarchies();
		return serializeHierarchies(hierarchies);
		
	}

	
	private static JSONArray serializeHierarchies(List<Hierarchy> hierarchies) throws JSONException{
		JSONArray hierchiesSerialized = new JSONArray();
		if(hierarchies!=null){
			for (Iterator<Hierarchy> iterator = hierarchies.iterator(); iterator.hasNext();) {
				Hierarchy hierarchy = (Hierarchy) iterator.next();
				JSONObject hierarchyObject = new JSONObject();
				hierarchyObject.put(HIERARCHY_NAME, hierarchy.getName());
				hierchiesSerialized.put(hierarchyObject);
			}
		}
		return hierchiesSerialized;
	}
	
}
