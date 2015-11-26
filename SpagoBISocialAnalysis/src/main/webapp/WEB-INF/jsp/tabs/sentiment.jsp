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
<%@ page import="twitter4j.JSONArray" %>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>

<%

String language = (String) request.getSession().getAttribute(SpagoBIConstants.SBI_LANGUAGE);
String country = (String) request.getSession().getAttribute(SpagoBIConstants.SBI_COUNTRY);

if(language == null || language.equals(""))
{
	language = "en";
}

if(country == null || country.equals(""))
{
	country = "US";
}

Locale locale = new Locale(language, country);
	
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
	
	JSONArray positiveTopics = sentimentDP.getPositiveBC();
	JSONArray neutralTopics = sentimentDP.getNeutralBC();
	JSONArray negativeTopics = sentimentDP.getNegativeBC();
	
	JSONArray positiveRadar = sentimentDP.getPositiveRadar();
	JSONArray neutralRadar = sentimentDP.getNeutralRadar();
	JSONArray negativeRadar = sentimentDP.getNegativeRadar();
	
	boolean rAnalysis = sentimentDP.isrAnalysis();
	 
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<%@include file="../commons/includeMessageResource.jspf" %>
	<%@include file="../commons/includeSbiSocialAnalysisResources.jspf"%>

	 
	
	<title>Twitter Analysis</title>
	
</head>
<body>


<div id="navigation">

	<div id="report-loading" class="loading"><img src="<%= application.getContextPath() %>/img/ajax-loader.gif" width="32" height="32" /><br /><strong>Loading</strong></div>

	<ul class="navtabs tabsStyle">
	    <li class="navtabs"><a href=<%= summaryLink %>> <label id="summary"></label> </a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>> <label id="topics"></label> </a></li>
	    <li class="navtabs"><a href=<%= networkLink %>> <label id="network"></label> </a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>> <label id="distribution"></label> </a></li>
   	    <li class="navtabs" id="activelink"><a href=<%= sentimentLink %>> <label id="sentiment"></label> </a></li>
	    <li class="navtabs"><a href=<%= impactLink %>> <label id="impact"></label> </a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>> <label id="roi"></label> </a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp"> <label id="searchome"></label> </a></li>
	</ul>
	
	<% if (!rAnalysis) { %>
	<center>
		<h1 style="border: 1px white solid;"><span><label id="sentimentanalysisdisabled"></label></span></h1>
    </center>
    <% } %>
    
    
    <% if (rAnalysis) { %>
    <div id="twitterPolarity" class="blank_box twitterPolarityMain_box" >
			
		<div class="twitterPolarityTitle_box">
			
			<span><label id="tweetspolarity"></label></span>
		
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
	<% } %>
	
	 <% if (rAnalysis) { %>
	<div class="blank_box" style="float:left; margin-left: 10px; margin-bottom: 30px;">
		
			<div class="twitterSentimentRadarTitle_box">
				
				<span><label id="sentimentradar"></label></span>
			
			</div>

		<div id="chart"></div>
	</div>
	<% } %>
	
	 <% if (rAnalysis) { %>
	<div style="width: 90%; float:left;">
	
		<div class="blank_box polarity_barchartMain">
		
			<div class="twitterPolarityBCTitle_box">
				
				<span><label id="positivestopics"></label></span>
			
			</div>
			
			<div class="posTopics_label" style="float:left;"></div>
			<div class="positive_barchart" style="float:left; width:80%; overflow-x:auto"></div>
			
		</div>
		
		<div class="blank_box polarity_barchartMain" >
		
			<div class="twitterPolarityBCTitle_box">
				
				<span><label id="neutralstopics"></label></span>
			
			</div>
			
			<div class="neuTopics_label" style="float:left;"></div>
			<div class="neutral_barchart" style="float:left; width:80%; overflow-x:auto"></div>
			
		</div>
		
		<div class="blank_box polarity_barchartMain" >
		
			<div class="twitterPolarityBCTitle_box">
				
				<span><label id="negativestopics"></label></span>
			
			</div>
			
			<div class="negTopics_label" style="float:left;"></div>
			<div class="negative_barchart" style="float:left; width:80%; overflow-x:auto"></div>
			
		</div>	
	
	</div>
	<% } %>
			
</div>

	<%@include file="../commons/includeSbiSocialAnalysisComponents.jspf"%>

	<script>

		var positiveRadar = <%= positiveRadar %>
		var neutralRadar = <%= neutralRadar %>
		var negativeRadar = <%= negativeRadar %>
		
		if(positiveRadar.length > 0 && neutralRadar.length > 0 && negativeRadar.length > 0)
		{		
			sentimentRadar(positiveRadar, neutralRadar, negativeRadar);
		}
	</script>

	<script>

		var positiveData = <%= positiveTopics %>
		var neutralData = <%= neutralTopics %>
		var negativeData = <%= negativeTopics %>
		
		sentimentTopics(positiveData, neutralData, negativeData);
	</script>
		
</body>
</html>