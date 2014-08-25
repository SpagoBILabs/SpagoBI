<%@ page language="java"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="false" 
%>

<%
HttpSession session=request.getSession(false);
if (session!=null) session.invalidate();
%>
