/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.cache;

import it.eng.spagobi.jpivotaddins.bean.AnalysisBean;
import it.eng.spagobi.jpivotaddins.bean.ToolbarBean;
import it.eng.spagobi.jpivotaddins.bean.adapter.AnalysisAdapterUtil;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import mondrian.olap.CacheControl;
import mondrian.olap.Connection;
import mondrian.olap.Cube;
import mondrian.olap.Query;

import com.tonbeller.jpivot.chart.ChartComponent;
import com.tonbeller.jpivot.mondrian.ScriptableMondrianDrillThrough;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.navi.MdxQuery;
import com.tonbeller.jpivot.table.TableComponent;

public class FlushCacheServlet extends HttpServlet {
	
	public void service(HttpServletRequest request, HttpServletResponse response) {
		
		Logger logger = Logger.getLogger(this.getClass());
		logger.debug("Entering service method");
		
		// retrieves analysis information from session
		HttpSession session = request.getSession();
		OlapModel olapModel = (OlapModel) session.getAttribute("query01");
		ChartComponent chart = (ChartComponent) session.getAttribute("chart01");
		TableComponent table = (TableComponent) session.getAttribute("table01");
		AnalysisBean analysis = (AnalysisBean) session.getAttribute("analysisBean");
		analysis = AnalysisAdapterUtil.createAnalysisBean(analysis.getConnectionName(), analysis.getCatalogUri(),
			chart, table, olapModel);
		// stores current analysis information on session
		session.setAttribute("analysisBean", analysis);
		
		// retrieves Mondrian connection
		ScriptableMondrianDrillThrough smdt = (ScriptableMondrianDrillThrough) olapModel.getExtension("drillThrough");
		Connection mondrianConnection = smdt.getConnection();
		// retrieves CacheControl object
		CacheControl cacheControl = mondrianConnection.getCacheControl(null);
		// retrieves the MDX query
		MdxQuery mdxQuery = (MdxQuery) olapModel.getExtension("mdxQuery");
		Query mondrianQuery = mondrianConnection.parseQuery(mdxQuery.getMdxQuery());
		// finds the cube in the MDX query
	    Cube cube = mondrianQuery.getCube();
	    // flush cache on all measures for that cube
	    CacheControl.CellRegion measuresRegion = cacheControl.createMeasuresRegion(cube);
	    cacheControl.flush(measuresRegion);
	    
//		Cube[] cubes = mondrianConnection.getSchema().getCubes();
//		for (int i = 0; i < cubes.length; i++) {
//			Cube aCube = cubes[i];
//			CacheControl.CellRegion measuresRegion = cacheControl.createMeasuresRegion(aCube);
//			cacheControl.flush(measuresRegion);
//		}
		
		
		try {
			response.sendRedirect(request.getContextPath() + "/jpivotOlap.jsp?query=refresh");
		} catch (IOException e) {
			logger.error("Error while redirecting response", e);
		}
		
		logger.debug("Exiting service method");
		
	}
	
}
