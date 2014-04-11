package it.eng.spagobi.engines.whatif.services.prototype;

import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.datasource.SimpleOlapDataSource;
import com.eyeq.pivot4j.impl.PivotModelImpl;
import com.eyeq.pivot4j.transform.DrillReplace;
import com.eyeq.pivot4j.ui.html.HtmlRenderer;

@Path("/mdxwrapped")
public class WrappedQuery {

	@GET
	public String executeSimpleQuery(@Context HttpServletRequest req){
		
		
		try {
			Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot load Mondrian Olap4j Driver", e);
		}
		
		String initialMdx = "SELECT {[Measures].[Unit Sales]} ON COLUMNS, {[Product].[Drink]} ON ROWS FROM [Sales]";
		
		if(req.getParameter("mdx")!=null && !req.getParameter("mdx").equals("")){
			initialMdx = req.getParameter("mdx");
		}

		Properties connectionProps = new Properties();
		connectionProps.put("JdbcUser","root");
		connectionProps.put("JdbcPassword","");
		connectionProps.put("Catalog","file:D:/Progetti/SpagoBI/SpagoBI-4.x-Juno-runtimes/apache-tomcat-7.0.14/resources/Olap/FoodMart.xml");
		connectionProps.put("JdbcDrivers","com.mysql.jdbc.Driver");
		connectionProps.put("Provider","Mondrian");
		//connectionProps.put("jdbc:mondrian:Jdbc","jdbc:mysql:/localhost:3306/foodmart");


		SimpleOlapDataSource dataSource = new SimpleOlapDataSource();
		dataSource.setConnectionString( "jdbc:mondrian:Jdbc=jdbc:mysql://localhost:3306/foodmart");
		dataSource.setConnectionProperties(connectionProps);
//		dataSource.setConnectionString( "jdbc:mondrian:Jdbc=jdbc:mysql://localhost/foodmart;Provider=Mondrian;JdbcDrivers=com.mysql.jdbc.Driver;JdbcUser=root;JdbcPassword=;Catalog=file:D:/Progetti/SpagoBI/SpagoBI-4.x-Juno-runtimes/apache-tomcat-7.0.14/resources/Olap/FoodMart.xml;");

		
//		dataSource.setConnectionString( 
//				 "jdbc:mondrian:" + 
//				 "Jdbc=jdbc:mysql://sibilla2/foodmart;Provider=Mondrian;PoolNeeded=false;JdbcDrivers=com.mysql.jdbc.Driver;JdbcUser=foodmart;JdbcPassword=foodmart;" + 
//				 "Catalog=file:D:/Progetti/SpagoBI/SpagoBI-4.x-Juno-runtimes/apache-tomcat-7.0.14/resources/Olap/FoodMart.xml;");


		PivotModel model = new SpagoBIPivotModel(dataSource);


		
		model.setMdx(initialMdx);
		model.initialize();

		CellSet cellSet = model.getCellSet();


		StringWriter writer = new StringWriter();

		HtmlRenderer renderer = new HtmlRenderer(writer);
		renderer.setShowDimensionTitle(false); // Optionally hide the dimension title headers.
		renderer.setShowParentMembers(true); // Optionally make the parent members visible.
		
		renderer.setCellSpacing(0);

		renderer.setRowHeaderStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setColumnHeaderStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setCornerStyleClass("x-column-header-inner x-column-header x-column-header-align-left x-box-item x-column-header-default x-unselectable x-grid-header-ct x-docked x-grid-header-ct-default x-docked-top x-grid-header-ct-docked-top x-grid-header-ct-default-docked-top x-box-layout-ct x-docked-noborder-top x-docked-noborder-right x-docked-noborder-left x-pivot-header");
		renderer.setCellStyleClass("x-grid-cell x-grid-td x-grid-cell-gridcolumn-1014 x-unselectable x-grid-cell-inner  x-grid-row-alt x-grid-data-row x-grid-with-col-lines x-grid-cell x-pivot-cell");
		renderer.setTableStyleClass("x-panel-body x-grid-body x-panel-body-default x-box-layout-ct x-panel-body-default x-pivot-table");

		
//		renderer.render(model);
		//Axes of the resulting query.
		List<CellSetAxis> axes = cellSet.getAxes();

		//The COLUMNS axis
		CellSetAxis columns = axes.get(0);

		//The ROWS axis
		CellSetAxis rows = axes.get(1);

		//Member positions of the ROWS axis.
		List<Position> positions = rows.getPositions();

		Position p = positions.get(0);
		List<Member> m = positions.get(0).getMembers();
		Member m2 = m.get(0);
		DrillReplace transform = model.getTransform(DrillReplace.class);
		transform.drillDown(m2);

		// Get the updated result.
		cellSet = model.getCellSet();


		renderer.render(model); // Render the result as a HTML page.

//
//		SwapAxes transform2 = model.getTransform(SwapAxes.class);
//		transform2.setSwapAxes(true);
//
//		// Get the updated result.
//		cellSet = model.getCellSet();
//
//
//
//		renderer.render(model); // Render the result as a HTML page.


		writer.flush();
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return writer.getBuffer().toString();
	}
}
