<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>

<head>
  <title>Parameter 1 - sending</title>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <link rel="stylesheet" type="text/css" href="../jpivot/table/mdxtable.css">
  <link rel="stylesheet" type="text/css" href="../jpivot/navi/mdxnavi.css">
  <link rel="stylesheet" type="text/css" href="../wcf/form/xform.css">
</head>


<body>

<h1>Parameter 1 - sending</h1>

Click on a Product.
<p>
<wcf:include id="include01" httpParam="query" prefix="/WEB-INF/queries/" suffix=".jsp"/>

<form action="param3.jsp" method="post" id="form01">


<%-- make all members of the Region dimension clickable --%>
<jp:table id="clicktable02" query="${query01}" visible="true">

  <jp:clickable urlPattern="/test/param4.jsp?param={0}" uniqueName="[Product]"/>

</jp:table>

<wcf:render ref="clicktable02" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>

</form>

<p>
<a href="../index.jsp">Back to index</a>
</body>

</html>
