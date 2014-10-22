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

	
	<%@include file="../commons/includeSbiSocialAnalysisResources.jspf"%>
	
	<title>Twitter Analysis</title>
	
</head>
<body>

<div id="navigation">

    <div id="report-loading" class="loading"><img src="<%= application.getContextPath() %>/img/ajax-loader.gif" width="32" height="32" /><br /><strong>Loading</strong></div>


	<ul class="navtabs tabsStyle">
	    <li class="navtabs" id="activelink"><a href=<%= summaryLink %>> Summary</a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>>Topics</a></li>
	    <li class="navtabs"><a href=<%= networkLink %>>Network</a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>>Distribution</a></li>
	    <li class="navtabs"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	   	 	<li class="navtabs"><a href=<%= roiLink %>>ROI</a></li>
	   	<% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
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
				<span class="generalInfo_label">users</span>
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
				<span class="dateRange_label">Date range</span>
			</div>

		</div>
		
	</div>
		
	<div id="timeline" class="timeline_main">		
		
		<div class="demo-container" style="width: 100%; height: 60%;">
			<div id="hormenu">
				<ul> 
					<li><span>Time Scale</span>
						<ul>
							<li><a id="months" style="cursor:pointer;">Months</a>							
				          	<li><a id="weeks" style="cursor:pointer;">Weeks</a></li>
				          	<li><a id="days" style="cursor:pointer;">Days</a></li>
				          	<li><a id="hours" style="cursor:pointer;">Hours</a></li>
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
				<span>Tweets Summary</span>	
			</div>
			<div id="pieChart" style="float:left"></div>
			
		</div>
		
		<div class="blank_box pieChart_box" style="margin-top: 20px; margin-bottom: 30px;">
			
			<div class="pieBoxTitle">
				<span>Tweets Sources</span>
			</div>
			
			<div id="pieChartDevice" style="float:left;"></div>
			
		</div>
	</div>
		
	<div id="toptweets" class="blank_box twitterTopWidget_box">
			
		<div class="twitterTopTitle_box">	
			<span>Top Tweets</span>
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
	
	<div id="toptweets" class="blank_box twitterTopWidget_box">
			
		<div class="twitterTopTitle_box">	
			<span>Recent Tweets</span>
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

        		
		<script type="text/javascript">
		
			$(function() 
			{
				var hourlyData = <%= hourData %>
				var dailyData = <%= dayData %>
				var weeklyData = <%= weekData %>
				var monthlyData = <%= monthData %>
				
				var hourlyDataOverview = <%= hourDataOverview %>
				var dailyDataOverview = <%= dayDataOverview %>
				var weeklyDataOverview = <%= weekDataOverview %>
				var monthlyDataOverview = <%= monthDataOverview %>
				
				var ticks = <%= weekTicks %>			
					
				// helper for returning the weekends in a period

				function weekendAreas(axes) {

					var markings = [],
						d = new Date(axes.xaxis.min);

					// go to the first Saturday

					d.setUTCDate(d.getUTCDate() - ((d.getUTCDay() + 1) % 7))
					d.setUTCSeconds(0);
					d.setUTCMinutes(0);
					d.setUTCHours(0);

					var i = d.getTime();

					// when we don't set yaxis, the rectangle automatically
					// extends to infinity upwards and downwards

					do {
						markings.push({ xaxis: { from: i, to: i + 2 * 24 * 60 * 60 * 1000 } });
						i += 7 * 24 * 60 * 60 * 1000;
					} while (i < axes.xaxis.max);

					return markings;
				}
					
				var monthlyOptions = 
				{
					xaxis: 
					{
						mode: "time",
						minTickSize: [1, "month"],
						timeformat: "%b %Y"
					},
					yaxis: 
					{
						tickDecimals: 0
					},
					series: 
					{
						lines:
						{
							show: true,
							fill: true
						}
					},
					grid: {
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph"),
						noColumns:2,
						margin: '5px',
					},
					colors: ["#0084B4", "#ff0000"]
				};
					
				var weeklyOptions = 
				{
					xaxis: 
					{
						mode: "time",
						ticks: ticks,
						tickFormatter: function (val, axis) 
						{
							var month = new Array(12);
							month[0] = "Jan";
							month[1] = "Feb";
							month[2] = "Mar";
							month[3] = "Apr";
							month[4] = "May";
							month[5] = "Jun";
							month[6] = "Jul";
							month[7] = "Aug";
							month[8] = "Sep";
							month[9] = "Oct";
							month[10] = "Nov";
							month[11] = "Dec";
							
						    var firstDayWeek = new Date(val);
						    firstDayWeek.setUTCSeconds(0);
						    firstDayWeek.setUTCMinutes(0);
						    firstDayWeek.setUTCHours(0);
						    
						    var lastDayWeek = new Date(firstDayWeek.getTime() + 6 * 24 * 60 * 60 * 1000);
						    
						    return ('0' + firstDayWeek.getUTCDate()).slice(-2) + " - " + ('0' + lastDayWeek.getUTCDate()).slice(-2) + " " + month[firstDayWeek.getUTCMonth()];
						},
					},
					yaxis: 
					{
						tickDecimals: 0
					},
					series: 
					{
						lines:
						{
							show: true,
							fill: true
						}
					},
					grid: 
					{
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph"),
						noColumns:2,
						margin: '5px',
					},
					colors: ["#0084B4", "#ff0000"]
				};
					
				var dailyOptions = 
				{
					xaxis: 
					{
						mode: "time",
						minTickSize: [1, "day"],
						timeformat: "%d %b"
					},
					yaxis: 
					{
						tickDecimals: 0
					},
					series: 
					{
						lines:
						{
							show: true,
							fill: true
						}
					},
					grid: 
					{
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph"),
						noColumns:2,
						margin: '5px',
					},
					colors: ["#0084B4", "#ff0000"]
				};
					
				var hourlyOptions = 
				{
					xaxis: 
					{
						mode: "time",
						minTickSize: [1, "hour"],
						timeformat: "%d %b  %H:%M"
					},
					yaxis: 
					{
						tickDecimals: 0
					},
					series: 
					{
						lines:
						{
							show: true,
							fill: true
						}
					},
					grid: 
					{
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph"),
						noColumns:2,
						margin: '5px',
					},
					colors: ["#0084B4", "#ff0000"]
				};
					
				overviewOptionsMonth =
					{
						series: {
							lines: {
								show: true,
								lineWidth: 1
							},							
							shadowSize: 0
						},
						xaxis: {
							mode: "time",
							minTickSize: [1, "month"],
							timeformat: "%b %Y"
						},
						yaxis: {
							ticks: [],
							min: 0,
							autoscaleMargin: 0.1
						},
						selection: {
							mode: "x"
						},
						colors: ["#0084B4", "#ff0000"]
				};
					
				overviewOptionsWeek =
				{
					series: {
						lines: {
							show: true,
							lineWidth: 1
						},							
						shadowSize: 0
					},
					xaxis: {
						mode: "time",
						ticks: ticks,
						tickFormatter: function (val, axis) 
						{
							var month = new Array(12);
							month[0] = "Jan";
							month[1] = "Feb";
							month[2] = "Mar";
							month[3] = "Apr";
							month[4] = "May";
							month[5] = "Jun";
							month[6] = "Jul";
							month[7] = "Aug";
							month[8] = "Sep";
							month[9] = "Oct";
							month[10] = "Nov";
							month[11] = "Dec";
							
						    var firstDayWeek = new Date(val);
						    firstDayWeek.setUTCSeconds(0);
						    firstDayWeek.setUTCMinutes(0);
						    firstDayWeek.setUTCHours(0);
						    
						    var lastDayWeek = new Date(firstDayWeek.getTime() + 6 * 24 * 60 * 60 * 1000);
						    
						    return ('0' + firstDayWeek.getUTCDate()).slice(-2) + " - " + ('0' + lastDayWeek.getUTCDate()).slice(-2) + " " + month[firstDayWeek.getUTCMonth()];
						},							
					},
					yaxis: {
						ticks: [],
						min: 0,
						autoscaleMargin: 0.1
					},
					selection: {
						mode: "x"
					},
					colors: ["#0084B4", "#ff0000"]
				};
				
				overviewOptionsDay =
				{
					series: {
						lines: {
							show: true,
							lineWidth: 1
						},							
						shadowSize: 0
					},
					xaxis: {
						mode: "time",
						minTickSize: [1, "day"],
						timeformat: "%d %b"
					},
					yaxis: {
						ticks: [],
						min: 0,
						autoscaleMargin: 0.1
					},
					selection: {
						mode: "x"
					},
					colors: ["#0084B4", "#ff0000"]
				};
				
				overviewOptionsHour =
				{
					series: {
						lines: {
							show: true,
							lineWidth: 1
						},							
						shadowSize: 0
					},
					xaxis: {
						mode: "time",
						minTickSize: [1, "hour"],
						timeformat: "%d %b",
					},
					yaxis: {
						ticks: [],
						min: 0,
						autoscaleMargin: 0.1
					},
					selection: {
						mode: "x"
					},
					colors: ["#0084B4", "#ff0000"]
				};

				var plot = $.plot("#placeholder", weeklyData, weeklyOptions);
				
				var overview = $.plot("#overview", weeklyDataOverview, overviewOptionsWeek);
				
				$("#placeholder").bind("plotselected", function (event, ranges) {

					// do the zooming
					$.each(plot.getXAxes(), function(_, axis) {
						var opts = axis.options;
						opts.min = ranges.xaxis.from;
						opts.max = ranges.xaxis.to;
					});
					plot.setupGrid();
					plot.draw();
					plot.clearSelection();

					// don't fire event on the overview to prevent eternal loop

					overview.setSelection(ranges, true);
				});

				$("#overview").bind("plotselected", function (event, ranges) {
					plot.setSelection(ranges);
					
				});
								
				$("#placeholder").bind("plothover", function (event, pos, item) 
				{
					if (item) 
					{
						var someData = weeklyData;
						var content = item.series.label + " = " + item.datapoint[1];
						            
						for (var i = 0; i < someData.length; i++)
			            {
			                if (someData[i].label == item.series.label)
			                {					                	
			                    continue;   
			                }
			                
			                for (var j=0; j < someData[i].data.length; j++)
			                {
			                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
			                  	{
			                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
			                    }
			                }                
			            }					            
			            
			            showTooltip(item.pageX, item.pageY, content);
			        }
			        else 
			        {
			            $("#tooltip").css('display','none');       
			        }
				});	
				
				
				$("#hours").click(function () 
				{
					
					plot = $.plot("#placeholder", hourlyData, hourlyOptions);
					
					 overview = $.plot("#overview", hourlyDataOverview, overviewOptionsHour);
					
					$("#placeholder").bind("plothover", function (event, pos, item) 
					{
					 	if (item) 
					 	{
					 		var someData = hourlyData;
				            var content = item.series.label + " = " + item.datapoint[1];
				            
				            for (var i = 0; i < someData.length; i++)
				            {
				                if (someData[i].label == item.series.label)
				                {					                	
				                    continue;   
				                }
				                
				                for (var j=0; j < someData[i].data.length; j++)
				                {
				                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
				                  	{
				                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
				                    }
				                }                
				            }					            
				            
				            showTooltip(item.pageX, item.pageY, content);
				        }
				        else 
				        {
				            $("#tooltip").css('display','none');       
				        }
					});					
				});
					
					
				$("#days").click(function () 
				{										
					
					plot = $.plot("#placeholder", dailyData, dailyOptions);
					
					overview = $.plot("#overview", dailyDataOverview, overviewOptionsDay);
					
					 $("#placeholder").bind("plothover", function (event, pos, item) 
								{
								 	if (item) 
								 	{
								 		var someData = dailyData;
							            var content = item.series.label + " = " + item.datapoint[1];
							            
							            for (var i = 0; i < someData.length; i++)
							            {
							                if (someData[i].label == item.series.label)
							                {					                	
							                    continue;   
							                }
							                
							                for (var j=0; j < someData[i].data.length; j++)
							                {
							                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
							                  	{
							                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
							                    }
							                }                
							            }					            
							            
							            showTooltip(item.pageX, item.pageY, content);
							        }
							        else 
							        {
							            $("#tooltip").css('display','none');       
							        }
						});	
							
					});	
					
					$("#weeks").click(function () 
					{
						
						plot = $.plot("#placeholder", weeklyData, weeklyOptions);
						
						overview = $.plot("#overview", weeklyDataOverview, overviewOptionsWeek);
						
						 $("#placeholder").bind("plothover", function (event, pos, item) 
									{
									 	if (item) 
									 	{
									 		var someData = weeklyData;
								            var content = item.series.label + " = " + item.datapoint[1];
								            
								            for (var i = 0; i < someData.length; i++)
								            {
								                if (someData[i].label == item.series.label)
								                {					                	
								                    continue;   
								                }
								                
								                for (var j=0; j < someData[i].data.length; j++)
								                {
								                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
								                  	{
								                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
								                    }
								                }                
								            }					            
								            
								            showTooltip(item.pageX, item.pageY, content);
								        }
								        else 
								        {
								            $("#tooltip").css('display','none');       
								        }
							});	
								
						});	
					
					$("#months").click(function () 
					{
						
						plot = $.plot("#placeholder", monthlyData, monthlyOptions);
						
						overview = $.plot("#overview", monthlyDataOverview, overviewOptionsMonth);
						
						$("#placeholder").bind("plothover", function (event, pos, item) 
						{
						 	if (item) 
						 	{
						 		var someData = monthlyData;
					            var content = item.series.label + " = " + item.datapoint[1];
					            
					            for (var i = 0; i < someData.length; i++)
					            {
					                if (someData[i].label == item.series.label)
					                {					                	
					                    continue;   
					                }
					                
					                for (var j=0; j < someData[i].data.length; j++)
					                {
					                    if (someData[i].data[j][0] == item.datapoint[0] && someData[i].data[j][1] == item.datapoint[1])
					                  	{
					                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1]; 
					                    }
					                }                
					            }					            
					            
					            showTooltip(item.pageX, item.pageY, content);
					        }
					        else 
					        {
					            $("#tooltip").css('display','none');       
					        }
						});					
					});
					
				
					$("<div id='tooltip'></div>").css({
						position: "absolute",
						display: "none",
						border: "1px solid #fdd",
						padding: "2px",
						"background-color": "#fee",
						opacity: 0.80
					}).appendTo("body");
					
					function showTooltip(x, y, contents) 
					{
				        $('#tooltip').html(contents);
				        $('#tooltip').css({
				            top: y + 5,
				            left: x + 5,
				            display: 'block'});
				    }				 
			});
		
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
		
		<script type="text/javascript">
			  $(document).ready(function(){
			
			    $(".navtabs").click(function(){
			    	
			    	var width = $("#navigation").width();
			        var height = $("#navigation").height()
			    	
			    	$("#report-loading").css({
				        top: (100),
				        left: ((width / 2) - 50),
				        display: "block"
				    })			
			    });
			
			  });
		</script>

		
</body>
</html>