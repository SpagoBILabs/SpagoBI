<%@page import="java.io.StringWriter"%>
<%@page import="com.eyeq.pivot4j.transform.DrillReplace"%>
<%@page import="com.eyeq.pivot4j.transform.SwapAxes"%>
<%@page import="com.eyeq.pivot4j.ui.html.HtmlRenderer"%>
<%@page import="com.eyeq.pivot4j.impl.PivotModelImpl"%>
<%@page import="com.eyeq.pivot4j.PivotModel"%>
<%@page import="com.eyeq.pivot4j.datasource.SimpleOlapDataSource"%>
<%@page import="com.eyeq.pivot4j.transform.impl.DrillReplaceImpl"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>
<%@page import="org.olap4j.mdx.IdentifierNode"%><%@ page import="org.olap4j.layout.*"%><%@ page import="org.olap4j.*"%><%@ page import="org.olap4j.query.*"%><%@ page import="org.olap4j.metadata.*"%><%@ page import="java.io.PrintWriter"%><%@ page import="java.sql.*"%><%@page import="java.util.Arrays"%>


<%

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
writer.close();

%>

<html>
<head></head>
<body>


  
 <meta http-equiv="refresh" content="0; url=http://localhost:8080/SpagoBIWhatIfEngine/restful-services/mdx">
  
</body>
</html>