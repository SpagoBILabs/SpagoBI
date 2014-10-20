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

<%@ page import="it.eng.spagobi.twitter.analysis.dataprocessors.*" %>

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
	
	if(withDocs)
	{			
		roiLink = "roi?searchID=" + searchId + "&withDocs=" + withDocs;
	}
	
	
	TwitterSentimentDataProcessor sentimentDP = new TwitterSentimentDataProcessor();
	sentimentDP.initializeTwitterSentimentDataProcessor(searchId);
	
	String positivePercentage = sentimentDP.getPositivePercentage();
	String neutralPercentage = sentimentDP.getNeutralPercentage();
	String negativePercentage = sentimentDP.getNegativePercentage();

	String positiveNumber = sentimentDP.getPositiveNumber();
	String neutralNumber = sentimentDP.getNeutralNumber();
	String negativeNumber = sentimentDP.getNegativeNumber();
	 
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<%@include file="../commons/includeSbiSocialAnalysisResources.jspf"%>

	 
	
	<title>Twitter Analysis</title>
	
</head>
<body>


<div id="navigation">

	<ul class="navtabs tabsStyle">
	    <li class="navtabs"><a href=<%= summaryLink %>> Summary</a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>>Topics</a></li>
	    <li class="navtabs"><a href=<%= networkLink %>>Network</a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>>Distribution</a></li>
   	    <li class="navtabs" id="activelink"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>>ROI</a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
	</ul>
        	
    
    <div id="twitterPolarity" class="blank_box twitterPolarityMain_box" >
			
		<div class="twitterPolarityTitle_box">
			
			<span>Tweets Polarity</span>
		
		</div>
		
		<br/>
		
		<div class="smileBox">
			
			<img src="<%= application.getContextPath() %>/img/positive.png"></img>
			
			<br/>
			
			<span><%= positivePercentage %></span>
			
			<br/>
			
			<span class="smileBoxNTweetsBox"><%= positiveNumber %></span>
		
		</div>
		
		<div class="smileBox">
			
			<img src="<%= application.getContextPath() %>/img/neutral.png"></img>
			
			<br/>
			
			<span><%= neutralPercentage %></span>
			
			<br/>
			
			<span class="smileBoxNTweetsBox"><%= neutralNumber %></span>
		
		</div>
		
		<div class="smileBox">
			
			<img src="<%= application.getContextPath() %>/img/negative.png"></img>
			
			<br/>
			
			<span><%= negativePercentage %></span>
			
			<br/>
			
			<span class="smileBoxNTweetsBox"><%= negativeNumber %></span>
		
		</div>
		
		
			
	</div> 
	
			
</div>        	
			

		
</body>
</html>