

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
	
</style>
<title>Insert title here</title>
</head>
<body>
	<div style="margin-top: 50px;">
 
        <form action="TwitterAnalysisServlet">
        	
        	<fieldset>
        	
        	 <div style="margin-left: 100px;">
        		On-line monitoring <input type="radio" name="searchType" value="streamingAPI"/>
        		<br/>
        		Historical data  <input type="radio" name="searchType" value="searchAPI"/>
        		<br/>
        		<br/>
        		Logical identifier <input type="text" name="searchLabel" placeholder="Logical identifier" size="20px"> 
        		<br/>
            	Keywords  <input type="text" name="keyword" placeholder="Keyword or #hashtag" size="20px"> 
            	<br/>
            	Accounts to monitor  <input type="text" name="account" placeholder="Accounts" size="20px"> 
            	<br/>
            	Resources     <input type="text" name="link" placeholder="Links" size="20px"> 
            	<br/>            	
            	
            	<input type="submit" value="Search"> 
            </div>
            </fieldset>
            
        </form>
        
    </div>
</body>
</html>