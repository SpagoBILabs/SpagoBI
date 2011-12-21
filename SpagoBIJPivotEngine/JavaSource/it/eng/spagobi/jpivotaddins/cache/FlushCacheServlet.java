/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
