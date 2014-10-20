<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         session="true"
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>

<%@ page import="it.eng.spagobi.twitter.analysis.dataprocessors.TwitterDocumentsDataProcessor" %>
<%@ page import="it.eng.spagobi.twitter.analysis.pojos.TwitterDocumentPojo" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
	
<%
	String searchId = request.getParameter("searchID");
	boolean withDocs = "TRUE".equalsIgnoreCase(request.getParameter("withDocs"));
	
	String summaryLink = "summary?searchID=" + searchId + "&withDocs=" + withDocs;
	String topicsLink = "topics?searchID=" + searchId + "&withDocs=" + withDocs; 
	String networkLink = "network?searchID=" + searchId + "&withDocs=" + withDocs; 
	String distributionLink = "distribution?searchID=" + searchId + "&withDocs=" + withDocs; 
	String sentimentLink = "sentiment?searchID=" + searchId + "&withDocs=" + withDocs; 
	String impactLink = "impact?searchID=" + searchId + "&withDocs=" + withDocs; 
	String roiLink = "";
	
	List<String> labels = new ArrayList<String>();
	List<TwitterDocumentPojo> documents = new ArrayList<TwitterDocumentPojo>();
	
	if(withDocs)
	{			
		roiLink = "roi?searchID=" + searchId + "&withDocs=" + withDocs;
		TwitterDocumentsDataProcessor tDocumentsDP = new TwitterDocumentsDataProcessor();
		tDocumentsDP.initializeTwitterDocumentsDataProcessor(searchId);
		labels = tDocumentsDP.getLabels();
		documents = tDocumentsDP.getDocuments();
	}
	
	
	 
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>



    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

    <%@include file="../commons/includeSbiSocialAnalysisResources.jspf"%>

	<script>
		 $(function() {
		   $("#tabs").tabs();
		 });
	 </script>
	 
	
	<title>Twitter Analysis</title>
	
</head>
<body>

<div id="navigation">
	

	<ul class="navtabs tabsStyle">
	    <li class="navtabs"><a href=<%= summaryLink %>> Summary</a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>>Topics</a></li>
	    <li class="navtabs"><a href=<%= networkLink %>>Network</a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>>Distribution</a></li>
   	    <li class="navtabs"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs" id="activelink"><a href=<%= roiLink %>>ROI</a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
	</ul>
        	
     
    <div id="tabs" style="float: left; margin-top:30px; margin-bottom:30px; width: 90%; margin-left:10px;">
  		<ul> 		
  			
  			<% for(int i = 0; i < labels.size(); i++) {  
  				String label = labels.get(i);
  				String tab = "#tabs-" + i; 
  			%>
  			
    		<li><a href=<%= tab %>><%= label %></a></li>
    		
    		<% } %>
    		
  		</ul>
  	
  		<% for(int i = 0; i < documents.size(); i++) {  
  			String tab = "tabs-" + i;
  			String url = documents.get(i).getUrl();
  		%>
  		
		<div id=<%= tab %>>
	    	<iframe src=<%= url %>
	    			style="width: 100%; height: 500px;">
  					<p>Your browser does not support iframes.</p>
			</iframe>	
	    </div>
	    
	    <% } %>
	    
 	</div>	
 	
 	
 	
	
<!-- 	 <div style="float:left; margin-left: 30px; margin-top: 50px; margin-bottom: 30px;">	 -->
<!-- 		<img src="img/screens/roi_tab.png" ></img> -->
<!-- 	</div>	 -->
			
</div>      
			
			

		
</body>
</html>