<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>




<jp:mondrianQuery id="query01" jdbcDriver="com.mysql.jdbc.Driver" jdbcUrl="jdbc:mysql://localhost/foodmartmondrian?user=root&password=" catalogUri="/WEB-INF/schema/FoodMart.xml">
select
  {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
  {([Promotion Media].[All Media], [Product].[All Products])} ON rows
from Sales
where ([Time].[1997])
</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query uses Mondrian OLAP</c:set>
