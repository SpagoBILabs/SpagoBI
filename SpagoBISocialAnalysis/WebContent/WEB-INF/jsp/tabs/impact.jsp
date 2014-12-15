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

<%@ page import="java.util.*" %>
<%@ page import="it.eng.spagobi.twitter.analysis.dataprocessors.*" %>
<%@ page import="it.eng.spagobi.twitter.analysis.pojos.*" %>
<%@ page import="it.eng.spagobi.bitly.analysis.utilities.*" %>
<%@ page import="it.eng.spagobi.twitter.analysis.utilities.*" %>
<%@ page import="twitter4j.JSONArray" %>
<%@ page import="twitter4j.JSONObject" %>
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
	
	/*************** TIMELINE ACCOUNTS ******************************************************/
	
	TwitterAccountsTimelineDataProcessor tAccountsTimelineDP = new TwitterAccountsTimelineDataProcessor();
	tAccountsTimelineDP.initializeTwitterAccountsTimelineDataProcessor(searchId);
	
	String accountHourData = tAccountsTimelineDP.getHourData();
	String accountDayData = tAccountsTimelineDP.getDayData();
	String accountWeekData = tAccountsTimelineDP.getWeekData();
	String accountMonthData = tAccountsTimelineDP.getMonthData();
	
	String accountHourDataOverview = tAccountsTimelineDP.getHourDataOverview();
	String accountDayDataOverview = tAccountsTimelineDP.getDayDataOverview();
	String accountWeekDataOverview = tAccountsTimelineDP.getWeekDataOverview();
	String accountMonthDataOverview = tAccountsTimelineDP.getMonthDataOverview();
	
	String accountWeekTicks = tAccountsTimelineDP.getWeekTicks();
	
	String hideHoursAccounts = tAccountsTimelineDP.hideHours(searchId);
	
	
	/*************** TIMELINE LINKS *****************************************************/
	
	TwitterResourcesTimelineDataProcessor tResourceTimelineDP = new TwitterResourcesTimelineDataProcessor();
	tResourceTimelineDP.initializeTwitterResourcesTimelineDataProcessor(searchId);
	
	String linkHourData = tResourceTimelineDP.getHourData();
	String linkDayData = tResourceTimelineDP.getDayData();
	String linkWeekData = tResourceTimelineDP.getWeekData();
	String linkMonthData = tResourceTimelineDP.getMonthData();
	
	String linkHourDataOverview = tResourceTimelineDP.getHourDataOverview();
	String linkDayDataOverview = tResourceTimelineDP.getDayDataOverview();
	String linkWeekDataOverview = tResourceTimelineDP.getWeekDataOverview();
	String linkMonthDataOverview = tResourceTimelineDP.getMonthDataOverview();
	
	String linkWeekTicks = tResourceTimelineDP.getWeekTicks();
	
	String hideHoursLinks = tResourceTimelineDP.hideHours(searchId);
	 
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
   	    <li class="navtabs"><a href=<%= sentimentLink %>> <label id="sentiment"></label> </a></li>
	    <li class="navtabs" id="activelink"><a href=<%= impactLink %>> <label id="impact"></label> </a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>> <label id="roi"></label> </a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp"> <label id="searchome"></label> </a></li>
	</ul>		
		
		<div class="impacttimeline_main blank_box">	
		
			<div class="twitterFollowersTitle_box">
			
				<span><label id="accountsfollowerstimeline"></label></span>
			
			</div>
		
			<div id="main-graph-account"></div>
		
			<div class="demo-container" style="width: 100%; height: 60%;">
				
				<div style="float:right;">
					<div id="hormenu-a">
						<ul> 
							<li><span><label id="timescalef"></label></span>
								<ul>
									<li><a id="months" style="cursor:pointer;"><label id="monthsf"></label></a>							
						          	<li><a id="weeks" style="cursor:pointer;"><label id="weeksf"></label></a></li>
						          	<li><a id="days" style="cursor:pointer;"><label id="daysf"></label></a></li>
						          	<li><a id="hours" style="cursor:pointer; <%= hideHoursAccounts %>"><label id="hoursf"></label></a></li>
						     	</ul>
						 	</li>
					</div>
					
				</div>
				<div id="placeholder-account"  style="width: 88%;" class="demo-placeholder"></div>
			</div>
			
			<div class="demo-container" style="width: 100%; height: 40%">
				<div id="overview-account" class="demo-placeholder-o"></div>
			</div>	
			
		</div>
		
		<div class="impacttimeline_main blank_box">	
		
			<div class="twitterFollowersTitle_box">
			
				<span><label id="bitlyclickstimeline"></label></span>
			
			</div>
		
			<div id="main-graph-link"></div>
				
				
			<div class="demo-container" style="width: 100%; height: 60%;">
				
				<div style="float:right;">
					<div id="hormenu-l">
						<ul> 
							<li><span><label id="timescalec"></label></span>
								<ul>
									<li><a id="months-link" style="cursor:pointer;"><label id="monthsc"></label></a>							
						          	<li><a id="weeks-link" style="cursor:pointer;"><label id="weeksc"></label></a></li>
						          	<li><a id="days-link" style="cursor:pointer;"><label id="daysc"></label></a></li>
						          	<li><a id="hours-link" style="cursor:pointer; <%= hideHoursLinks %>"><label id="hoursc"></label></a></li>
						     	</ul>
						 	</li>
					</div>
					
				</div>
				<div id="placeholder-link"  style="width: 88%;" class="demo-placeholder"></div>
			</div>
			
			<div class="demo-container" style="width: 100%; height: 40%">
				<div id="overview-link" class="demo-placeholder-o"></div>
			</div>
	
		</div>			
</div>   

	<%@include file="../commons/includeSbiSocialAnalysisComponents.jspf"%>

	<script type="text/javascript">
		
		var hourData = <%= accountHourData %>
		var dayData = <%= accountDayData %>
		var weekData = <%= accountWeekData %>
		var monthData = <%= accountMonthData %>
		
		var hourDataOverview = <%= accountHourDataOverview %>
		var dayDataOverview = <%= accountDayDataOverview %>
		var weekDataOverview = <%= accountWeekDataOverview %>
		var monthDataOverview = <%= accountMonthDataOverview %>
		
		var ticks = <%= accountWeekTicks %>		
		
		followersTimeline(hourData, dayData, weekData, monthData, hourDataOverview, dayDataOverview, weekDataOverview, monthDataOverview, ticks);
		
	</script>     	
		
		
	<!-- *********** LINKs TIMELINE *********************** -->	

	<script type="text/javascript">
	
		var hourData = <%= linkHourData %>
		var dayData = <%= linkDayData %>
		var weekData = <%= linkWeekData %>
		var monthData = <%= linkMonthData %>
		
		var hourDataOverview = <%= linkHourDataOverview %>
		var dayDataOverview = <%= linkDayDataOverview %>
		var weekDataOverview = <%= linkWeekDataOverview %>
		var monthDataOverview = <%= linkMonthDataOverview %>
		
		var ticks = <%= linkWeekTicks %>	
		
		clicksTimeline(hourData, dayData, weekData, monthData, hourDataOverview, dayDataOverview, weekDataOverview, monthDataOverview, ticks);
	
	</script>  
		 		
</body>
</html>