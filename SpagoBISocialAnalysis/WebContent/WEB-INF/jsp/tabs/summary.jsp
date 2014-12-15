<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@page import="org.apache.tomcat.jni.Local"%>
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
	
	/*************** GENERAL INFO ***********************************************************/
	
	TwitterGeneralStatsDataProcessor tGeneralDP = new TwitterGeneralStatsDataProcessor();
	tGeneralDP.initializeTwitterGeneralStats(searchId);
	
	String totalTweets = tGeneralDP.getTotalTweets();
	String totalUsers = tGeneralDP.getTotalUsers();
	String minDate = tGeneralDP.getMinDateSearch();
 	String maxDate = tGeneralDP.getMaxDateSearch();
 	String reach = tGeneralDP.getReach();
 	String impressions = tGeneralDP.getImpressions();
	

	/*************** TIMELINE ***************************************************************/

	TwitterTimelineDataProcessor tTimelineDP = new TwitterTimelineDataProcessor();
	tTimelineDP.initializeTwitterTimelineDataProcessor(searchId);
	
	String hourData = tTimelineDP.getHourData();
	String dayData = tTimelineDP.getDayData();
	String weekData = tTimelineDP.getWeekData();
	String monthData = tTimelineDP.getMonthData();
	
	String hourDataOverview = tTimelineDP.getHourDataOverview();
	String dayDataOverview = tTimelineDP.getDayDataOverview();
	String weekDataOverview = tTimelineDP.getWeekDataOverview();
	String monthDataOverview = tTimelineDP.getMonthDataOverview();
	
	String weekTicks = tTimelineDP.getWeekTicks();
	
	/*************** PIE CHARTS *************************************************************/
	
	TwitterPieDataProcessor tPieDP = new TwitterPieDataProcessor();
	tPieDP.initializeTwitterPieCharts(searchId);
	TwitterPiePojo pieChartObj = tPieDP.getTweetsPieChart();
 	List<TwitterPieSourcePojo> sources = tPieDP.getTweetsPieSourceChart();
	
	
 	/*************** TWEETS BOXs ************************************************************/
	
 	TwitterTopTweetsDataProcessor tTopDP = new TwitterTopTweetsDataProcessor();
 	tTopDP.initializeTwitterTopData(searchId, 30);
 	List<TwitterTopTweetsPojo> topTweets = tTopDP.getTopTweetsData();
 	List<TwitterTopTweetsPojo> topRecents = tTopDP.getTopRecentTweetsData(); 	
	
	/*****************************************************************************************/
	
	 
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
	    <li class="navtabs" id="activelink"><a href=<%= summaryLink %>> <label id="summary"></label> </a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>> <label id="topics"></label> </a></li>
	    <li class="navtabs"><a href=<%= networkLink %>> <label id="network"></label> </a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>> <label id="distribution"></label> </a></li>
	    <li class="navtabs"><a href=<%= sentimentLink %>> <label id="sentiment"></label> </a></li>
	    <li class="navtabs"><a href=<%= impactLink %>> <label id="impact"></label> </a></li>
	    <% if(withDocs) { %>
	   	 	<li class="navtabs"><a href=<%= roiLink %>> <label id="roi"></label> </a></li>
	   	<% }; %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp"> <label id="searchome"></label> </a></li>
	</ul>
        		
	<div class="generalinfo">

		<div class="generalInfo_main">
	
			<div class="generalInfo_box">
				<span class="generalInfo_infos" ><%= totalTweets %></span>
				<br>
				<span class="generalInfo_label" >tweets</span>
			</div>
		
			<div class="generalInfo_box">
				<span class="generalInfo_infos" ><%= totalUsers %></span>
				<br>
				<span class="generalInfo_label"> <label id="users"></label> </span>
			</div>
			
			<div class="generalInfo_box">
				<span class="generalInfo_infos" ><%= reach %></span>
				<br>
				<span class="generalInfo_label">reach</span>
			</div>
			
			<div class="generalInfo_box">
				<span class="generalInfo_infos" ><%= impressions %></span>
				<br>
				<span class="generalInfo_label">impressions</span>
			</div>
			
			<div class="blank_box dateRange_box" style="float:left;">
				<span class="dateRange_dates"><%= minDate %></span>
				<br />
				<span class="dateRange_dates"><%= maxDate %></span>
				<br />
				<span class="dateRange_label"> <label id="searchrange"></label> </span>
			</div>

		</div>
		
	</div>
		
	<div id="timeline" class="timeline_main">		
		
		<div class="demo-container" style="width: 100%; height: 60%;">
			<div id="hormenu">
				<ul> 
					<li><span> <label id="timescale"></label> </span>
						<ul>
							<li><a id="months" style="cursor:pointer;"> <label id="months"></label> </a>							
				          	<li><a id="weeks" style="cursor:pointer;"> <label id="weeks"></label> </a></li>
				          	<li><a id="days" style="cursor:pointer;"> <label id="days"></label> </a></li>
				          	<li><a id="hours" style="cursor:pointer;"> <label id="hours"></label> </a></li>
				     	</ul>
				 	</li>
			</div>
			<div id="main-graph" style="vertical-align: middle !important;"></div>
			<div id="placeholder" class="demo-placeholder"></div>
		</div>
		<div class="demo-container" style="width: 100%; height: 40%">
			<div id="overview" class="demo-placeholder-o"></div>
		</div>	
	</div>
		
	<div id="pieBoxMain" style="float:left; width:400px">
		
		<div class="blank_box pieChart_box"  ">
			<div class="pieBoxTitle">	
				<span><label id="tweetssummary"></label></span>	
			</div>
			<div id="pieChart" style="float:left"></div>
			
		</div>
		
		<div class="blank_box pieChart_box" style="margin-top: 20px; margin-bottom: 30px;">
			
			<div class="pieBoxTitle">
				<span><label id="tweetssources"></label></span>
			</div>
			
			<div id="pieChartDevice" style="float:left;"></div>
			
		</div>
	</div>
		
	<div id="ttweets" class="blank_box twitterTopWidget_box">
			
		<div class="twitterTopTitle_box">	
			<span><label id="toptweets"></label></span>
		</div>
				
		<div class="twitterTopRT_box">
			<ol>
					<%						
						for (TwitterTopTweetsPojo topObj : topTweets) 
						{ 
							
					%>			
					
					<li>
						<div class="tweetData">

							<img class="tweetprofileimg" src="<%= topObj.getProfileImgSrcFromDB() %>" />

							<div class="tweettext">					
									<div>
										<a href="https://twitter.com/<%= topObj.getUsernameFromDb() %>" ><%= topObj.getUsernameFromDb() %></a>
										<span style="float:right;"><%= topObj.getCreateDateFromDb() %></span>
										<span class="retweetClass"><%= topObj.getCounterRTs() %></span>	
										<img style="float:right;" src="<%= application.getContextPath() %>/img/retweet.png" />																					
									</div>
								<p>
									<%= topObj.getTweetText() %> 
								</p>
								<p>
									<% 
										for (String hashtag : topObj.getHashtags()) 
									{  
									
									%> 
									<a style="color: #87C2ED" href="https://twitter.com/hashtag/<%= hashtag %>" ><%= hashtag %></a>
									<% } %> 
								</p>
							</div>
						</div> 
					</li>
				<% } %> 
				</ol>
			</div>

	</div>
	
	<div id="rtweets" class="blank_box twitterTopWidget_box" style="margin-left:10px;">
			
		<div class="twitterTopTitle_box">	
			<span><label id="recenttweets"></label></span>
		</div>
				
		<div class="twitterTopRT_box">
			<ol>
					<%						
						for (TwitterTopTweetsPojo topObj : topRecents) 
						{ 
							
					%>			
					
					<li>
						<div class="tweetData">

							<img class="tweetprofileimg" src="<%= topObj.getProfileImgSrcFromDB() %>" />

							<div class="tweettext">					
									<div>
										<a href="https://twitter.com/<%= topObj.getUsernameFromDb() %>" ><%= topObj.getUsernameFromDb() %></a>
										<span style="float:right;"><%= topObj.getCreateDateFromDb() %></span>
										<span class="retweetClass"><%= topObj.getCounterRTs() %></span>	
										<img style="float:right;" src="<%= application.getContextPath() %>/img/retweet.png" />																					
									</div>
								<p>
									<%= topObj.getTweetText() %> 
								</p>
								<p>
									<% 
										for (String hashtag : topObj.getHashtags()) 
									{  
									
									%> 
									<a style="color: #87C2ED" href="https://twitter.com/hashtag/<%= hashtag %>" ><%= hashtag %></a>
									<% } %> 
								</p>
							</div>
						</div> 
					</li>
				<% } %> 
				</ol>
			</div>

	</div>
			
</div>

	<%@include file="../commons/includeSbiSocialAnalysisComponents.jspf"%>
    
    <script type="text/javascript">
    
	    var hourlyData = <%= hourData %>
		var dailyData = <%= dayData %>
		var weeklyData = <%= weekData %>
		var monthlyData = <%= monthData %>
		
		var hourlyDataOverview = <%= hourDataOverview %>
		var dailyDataOverview = <%= dayDataOverview %>
		var weeklyDataOverview = <%= weekDataOverview %>
		var monthlyDataOverview = <%= monthDataOverview %>
		
		var ticks = <%= weekTicks %>
		
		summaryTimeline(hourlyData, dailyData, weeklyData, monthlyData, hourlyDataOverview, dailyDataOverview, weeklyDataOverview,monthlyDataOverview, ticks); 
    
    </script>
    
 <script>
 
	var pie = new d3pie("pieChart", {
		"header": {
			"title": {
				"fontSize": 24,
				"font": "open sans"
			},
			"subtitle": {
				"color": "#999999",
				"fontSize": 12,
				"font": "open sans"
			},
			"titleSubtitlePadding": 9
		},
		"footer": {
			"color": "#999999",
			"fontSize": 10,
			"font": "open sans",
			"location": "bottom-left"
		},
		"size": {
			"canvasWidth": 350,
			"canvasHeight": 300
		},
		"data": {
			"sortOrder": "value-desc",
			"content": [
			<% if(pieChartObj.getTweets() > 0) { %>
			{
				"label": 'Tweets',
				"value": <%= pieChartObj.getTweets() %>,
			},
			<% } %>
			<% if(pieChartObj.getRTs() > 0) { %>
			{
				"label": "RTs",
				"value": <%= pieChartObj.getRTs() %>,
			},
			<% } %>
			<% if(pieChartObj.getReplies() > 0) { %>
			{
				"label": "Replies",
				"value": <%= pieChartObj.getReplies() %>,
			}
			<% } %>
					]
				},
				"labels": {
					"outer": {
						"pieDistance": 1
					},
// 					"inner": {
// 						"hideWhenLessThanPercentage": 4
// 					},
					"mainLabel": {
						"fontSize": 12
					},
					"percentage": {
						"color": "#ffffff",
						"decimalPlaces": 0
					},
					"value": {
						"color": "#adadad",
						"fontSize": 11
					},
					"lines": {
						"enabled": false
					}
				},
				"effects": {
					"pullOutSegmentOnClick": {
						"effect": "linear",
						"speed": 400,
						"size": 8
					}
				},
				"misc": {
					"gradient": {
						"enabled": true,
						"percentage": 100
					}
				}
			});
			
			
			</script>
			
			<script>
			
			var pieDevice = new d3pie("pieChartDevice", {
				"header": {
					"title": {
						"fontSize": 24,
						"font": "open sans"
					},
					"subtitle": {
						"color": "#999999",
						"fontSize": 12,
						"font": "open sans"
					},
					"titleSubtitlePadding": 9
				},
				"footer": {
					"color": "#999999",
					"fontSize": 10,
					"font": "open sans",
					"location": "bottom-left"
				},
				"size": {
					"canvasWidth": 350,
					"canvasHeight": 300
				},
				"data": {
					"sortOrder": "value-desc",
					"smallSegmentGrouping": {
						"enabled": true,
						"value": 4
					},
					"content": [
					            <% 
					            	for(TwitterPieSourcePojo sourceObj : sources)
					            	{
					            							            	
					            %>
							{
								"label": '<%= sourceObj.getSource()%> ',
								"value": <%= sourceObj.getValue() %>,
		
							},
							
							<%
					            	}
							%>

					]
				},
				"labels": {
					"outer": {
						"pieDistance": 8
					},
					"inner": {
						"hideWhenLessThanPercentage": 4
					},
					"mainLabel": {
						"fontSize": 12
					},
					"percentage": {
						"color": "#ffffff",
						"decimalPlaces": 0
					},
					"value": {
						"color": "#adadad",
						"fontSize": 11
					},
					"lines": {
						"enabled": true
					}
				},
				"effects": {
					"pullOutSegmentOnClick": {
						"effect": "linear",
						"speed": 400,
						"size": 8
					}
				},
				"misc": {
					"gradient": {
						"enabled": true,
						"percentage": 100
					}
				}
			});
		</script>

		
</body>

</html>