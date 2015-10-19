

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
<%@ page import="it.eng.spagobi.twitter.analysis.pojos.*" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
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
	
	TwitterTagCloudDataProcessor tCloudDP = new TwitterTagCloudDataProcessor();
	tCloudDP.initializeTagClouds(searchId);
	
	String hTagCloud = tCloudDP.getHashtagsCloud().toString();
	String topicsCloud = tCloudDP.getTopicsCloud().toString();
	
	boolean rAnalysis = tCloudDP.isrAnalysis();
	 
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

	<div id="report-loading" class="loading"><img src="<%= application.getContextPath() %>/img/ajax-loader.gif" width="32" height="32" /><br /><strong>Loading</strong></div>

	<ul class="navtabs tabsStyle">
	    <li class="navtabs"><a href=<%= summaryLink %>> <label id="summary"></label> </a></li>
	    <li class="navtabs" id="activelink"><a href=<%= topicsLink %>> <label id="topics"></label> </a></li>
	    <li class="navtabs"><a href=<%= networkLink %>> <label id="network"></label> </a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>> <label id="distribution"></label> </a></li>
  	    <li class="navtabs"><a href=<%= sentimentLink %>> <label id="sentiment"></label> </a></li>
	    <li class="navtabs"><a href=<%= impactLink %>> <label id="impact"></label> </a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>> <label id="roi"></label> </a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp"> <label id="searchome"></label> </a></li>
	</ul>
        	

	<div id="tagcloud" class="blank_box tagCloudMain_box">
			
		<div class="tagCloudTitle_box">
			
			<span><label id="hashtagscloud"></label></span>
		
		</div>
			
		<br/>
			
		<div id="hashtagsCloud" class="tagCloud_box"></div>
		
		
	</div>
	
	 <% if (rAnalysis) { %>
	<div id="tagcloud" class="blank_box tagCloudMain_box" style="margin-left: 20px;">
			
		<div class="tagCloudTitle_box">
			
			<span><label id="topicscloud"></label></span>
		
		</div>
			
		<br/>
			
		<div id="topicsCloud" class="tagCloud_box"></div>
		
		
	</div>
	<% } %>
	
	</div>
		
</div>        	

	<%@include file="../commons/includeMessageResource.jspf" %>
	<%@include file="../commons/includeSbiSocialAnalysisComponents.jspf"%>
			
	<script type="text/javascript">
					var word_list_hashtags = <%= hTagCloud %>
					$(function() {
	 						$("#hashtagsCloud").jQCloud(word_list_hashtags);
					});
	</script>
	
	<script type="text/javascript">
					var word_list_topics = <%= topicsCloud %>
					$(function() {
	 						$("#topicsCloud").jQCloud(word_list_topics);
					});
	</script>
		
</body>
</html>