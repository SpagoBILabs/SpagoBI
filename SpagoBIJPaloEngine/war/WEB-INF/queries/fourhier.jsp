<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="com.mysql.jdbc.Driver" jdbcUrl="jdbc:mysql://localhost/foodmartmondrian?user=root&password=" catalogUri="/WEB-INF/schema/FoodMart.xml">
select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
{([Gender].[All Gender], [Marital Status].[All Marital Status],
  [Customers].[All Customers],
  [Product].[All Products] ) } on rows
  from Sales where ([Time].[1997])

</jp:mondrianQuery>

<c:set var="title01" scope="session">4 hierarchies on one axis</c:set>
