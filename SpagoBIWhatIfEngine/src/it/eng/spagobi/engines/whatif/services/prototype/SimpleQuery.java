package it.eng.spagobi.engines.whatif.services.prototype;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.datasource.SimpleOlapDataSource;
import com.eyeq.pivot4j.impl.PivotModelImpl;
import com.eyeq.pivot4j.transform.DrillReplace;
import com.eyeq.pivot4j.transform.SwapAxes;
import com.eyeq.pivot4j.ui.html.HtmlRenderer;

@Path("/mdx")
public class SimpleQuery {

	@GET
	public String executeSimpleQuery(){

		Properties connectionProps = new Properties();
		connectionProps.put("JdbcUser","foodmart");
		connectionProps.put("JdbcPassword","foodmart");
		connectionProps.put("Catalog","file:D:/Sviluppo/mondrian/FoodMartMySQL.xml");
		connectionProps.put("JdbcDrivers","=com.mysql.jdbc.Driver");
		connectionProps.put("Provider","Mondrian");
		//connectionProps.put("jdbc:mondrian:Jdbc","jdbc:mysql:/localhost:3306/foodmart");


		SimpleOlapDataSource dataSource = new SimpleOlapDataSource();
		dataSource.setConnectionString( "jdbc:mondrian:Jdbc=jdbc:mysql://172.27.1.83:3306/foodmart");
		dataSource.setConnectionProperties(connectionProps);
		//dataSource.setConnectionString( "jdbc:mondrian:Jdbc=jdbc:mysql://172.27.1.83:3306/foodmart;Provider=Mondrian;JdbcDrivers=com.mysql.jdbc.Driver;JdbcUser=foodmart;JdbcPassword=foodmart;Catalog=file:D:/Sviluppo/mondrian/FoodMartMySQL.xml;");



		PivotModel model = new PivotModelImpl(dataSource);


		String initialMdx = "SELECT {[Measures].[Unit Sales]} ON COLUMNS, {[Product].[Drink]} ON ROWS FROM [Sales]";
		model.setMdx(initialMdx);
		model.initialize();

		CellSet cellSet = model.getCellSet();



		StringWriter writer = new StringWriter();

		HtmlRenderer renderer = new HtmlRenderer(writer);
		renderer.setShowDimensionTitle(false); // Optionally hide the dimension title headers.
		renderer.setShowParentMembers(true); // Optionally make the parent members visible.


		renderer.render(model);
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


		SwapAxes transform2 = model.getTransform(SwapAxes.class);
		transform2.setSwapAxes(true);

		// Get the updated result.
		cellSet = model.getCellSet();



		renderer.render(model); // Render the result as a HTML page.


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
