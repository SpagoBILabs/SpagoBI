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
   	    <li class="navtabs"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs" id="activelink"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>>ROI</a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
	</ul>		
		
		<div class="timeline_main">	
		
			<div id="main-graph-account"></div>
		
			<div class="demo-container" style="width: 100%; height: 60%;">
				
				<div style="float:right;">
					<div id="hormenu-a">
						<ul> 
							<li><span>View</span>
								<ul>
									<li><a id="months" style="cursor:pointer;">Months</a>							
						          	<li><a id="weeks" style="cursor:pointer;">Weeks</a></li>
						          	<li><a id="days" style="cursor:pointer;">Days</a></li>
						          	<li><a id="hours" style="cursor:pointer; <%= hideHoursAccounts %>">Hours</a></li>
						     	</ul>
						 	</li>
					</div>
					
				</div>
				<div id="placeholder-account"  style="width: 95%;" class="demo-placeholder"></div>
			</div>
			
			<div class="demo-container" style="width: 100%; height: 40%">
				<div id="overview-account" class="demo-placeholder-o"></div>
			</div>	
			
		</div>
		
		<div class="timeline_main">	
		
			<div id="main-graph-link"></div>
				
				
			<div class="demo-container" style="width: 100%; height: 60%;">
				
				<div style="float:right;">
					<div id="hormenu-l">
						<ul> 
							<li><span>View</span>
								<ul>
									<li><a id="months-link" style="cursor:pointer;">Months</a>							
						          	<li><a id="weeks-link" style="cursor:pointer;">Weeks</a></li>
						          	<li><a id="days-link" style="cursor:pointer;">Days</a></li>
						          	<li><a id="hours-link" style="cursor:pointer; <%= hideHoursLinks %>">Hours</a></li>
						     	</ul>
						 	</li>
					</div>
					
				</div>
				<div id="placeholder-link"  style="width: 95%;" class="demo-placeholder"></div>
			</div>
			
			<div class="demo-container" style="width: 100%; height: 40%">
				<div id="overview-link" class="demo-placeholder-o"></div>
			</div>
	
		</div>			
</div>   

	<script type="text/javascript">
		
			$(function() 
			{
				
				var hourData = <%= accountHourData %>
				var dayData = <%= accountDayData %>
				var weekData = <%= accountWeekData %>
				var monthData = <%= accountMonthData %>
				
				var hourDataOverview = <%= accountHourDataOverview %>
				var dayDataOverview = <%= accountDayDataOverview %>
				var weekDataOverview = <%= accountWeekDataOverview %>
				var monthDataOverview = <%= accountMonthDataOverview %>
				
				var ticks = <%= accountWeekTicks %>			
							
				
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
				
				var hourOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-account"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				
				var dayOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-account"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				var weekOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-account"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				
				var monthOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-account"),
						noColumns:1,
						margin: '5px',
					},
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
					timeformat: "%d %b  %H:%M"
				},
				yaxis: {
					ticks: [],
					min: 0,
					autoscaleMargin: 0.1
				},
				selection: {
					mode: "x"
				},
			};
			
				

			var plot = $.plot("#placeholder-account", weekData, weekOptions);
			
			var overview = $.plot("#overview-account", weekDataOverview, overviewOptionsWeek);
			
			$("#placeholder-account").bind("plotselected", function (event, ranges) {

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

			$("#overview-account").bind("plotselected", function (event, ranges) {
				plot.setSelection(ranges);
				
			});
							
			$("#placeholder-account").bind("plothover", function (event, pos, item) 
			{
				if (item) 
				{
					var someData = weekData;
					var content = item.series.label + " = " + item.datapoint[1] + " followers";
					            
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
						plot = $.plot("#placeholder-account", hourData, hourOptions);
						
						 overview = $.plot("#overview-account", hourDataOverview, overviewOptionsHour);
						
						$("#placeholder-account").bind("plothover", function (event, pos, item) 
						{
						 	if (item) 
						 	{
						 		var someData = hourData;
					            var content = item.series.label + " = " + item.datapoint[1] + " followers";
					            
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
				
				plot = $.plot("#placeholder-account", dayData, dayOptions);
				
				overview = $.plot("#overview-account", dayDataOverview, overviewOptionsDay);
				
				 $("#placeholder-account").bind("plothover", function (event, pos, item) 
							{
							 	if (item) 
							 	{
							 		var someData = dayData;
						            var content = item.series.label + " = " + item.datapoint[1] + " followers";
						            
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
					
					plot = $.plot("#placeholder-account", weekData, weekOptions);
					
					overview = $.plot("#overview-account", weekDataOverview, overviewOptionsWeek);
					
					 $("#placeholder-account").bind("plothover", function (event, pos, item) 
								{
								 	if (item) 
								 	{
								 		var someData = weekData;
							            var content = item.series.label + " = " + item.datapoint[1] + " followers";
							            
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
					plot = $.plot("#placeholder-account", monthData, monthOptions);
					
					overview = $.plot("#overview-account", monthDataOverview, overviewOptionsMonth);
					
					$("#placeholder-account").bind("plothover", function (event, pos, item) 
					{
					 	if (item) 
					 	{
					 		var someData = monthData;
				            var content = item.series.label + " = " + item.datapoint[1] + " followers";
				            
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
		
		
		<!-- *********** LINKs TIMELINE *********************** -->	
	
		<script type="text/javascript">
		
			$(function() 
			{
				
				var hourData = <%= linkHourData %>
				var dayData = <%= linkDayData %>
				var weekData = <%= linkWeekData %>
				var monthData = <%= linkMonthData %>
				
				var hourDataOverview = <%= linkHourDataOverview %>
				var dayDataOverview = <%= linkDayDataOverview %>
				var weekDataOverview = <%= linkWeekDataOverview %>
				var monthDataOverview = <%= linkMonthDataOverview %>
				
				var ticks = <%= linkWeekTicks %>	
				
				
				
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
				
				var hourOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-link"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				
				var dayOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						markings: weekendAreas,
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-link"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				var weekOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-link"),
						noColumns:1,
						margin: '5px',
					},
				};
				
				
				var monthOptions = 
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
							lineWidth: 2
						}
					},
					grid: {
						hoverable: true,
						clickable: true
					},
					legend:
					{
						container: $("#main-graph-link"),
						noColumns:1,
						margin: '5px',
					},
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
					timeformat: "%d %b  %H:%M"
				},
				yaxis: {
					ticks: [],
					min: 0,
					autoscaleMargin: 0.1
				},
				selection: {
					mode: "x"
				},
			};
			
				

			var plot = $.plot("#placeholder-link", weekData, weekOptions);
			
			var overview = $.plot("#overview-link", weekDataOverview, overviewOptionsWeek);
			
			$("#placeholder-link").bind("plotselected", function (event, ranges) {

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

			$("#overview-link").bind("plotselected", function (event, ranges) {
				plot.setSelection(ranges);
				
			});
							
			$("#placeholder-link").bind("plothover", function (event, pos, item) 
			{
				if (item) 
				{
					var someData = weekData;
					var content = item.series.label + " = " + item.datapoint[1] + " clicks";
					            
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
			
			
			$("#hours-link").click(function () 
					{
						plot = $.plot("#placeholder-link", hourData, hourOptions);
						
						 overview = $.plot("#overview-link", hourDataOverview, overviewOptionsHour);
						
						$("#placeholder-link").bind("plothover", function (event, pos, item) 
						{
						 	if (item) 
						 	{
						 		var someData = hourData;
					            var content = item.series.label + " = " + item.datapoint[1] + " clicks";
					            
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
				
				
			$("#days-link").click(function () 
			{					
				
				plot = $.plot("#placeholder-link", dayData, dayOptions);
				
				overview = $.plot("#overview-link", dayDataOverview, overviewOptionsDay);
				
				 $("#placeholder-link").bind("plothover", function (event, pos, item) 
							{
							 	if (item) 
							 	{
							 		var someData = dayData;
						            var content = item.series.label + " = " + item.datapoint[1] + " clicks";
						            
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
				
				$("#weeks-link").click(function () 
				{
					
					plot = $.plot("#placeholder-link", weekData, weekOptions);
					
					overview = $.plot("#overview-link", weekDataOverview, overviewOptionsWeek);
					
					 $("#placeholder-link").bind("plothover", function (event, pos, item) 
								{
								 	if (item) 
								 	{
								 		var someData = weekData;
							            var content = item.series.label + " = " + item.datapoint[1] + " clicks";
							            
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
							                          content += '<br/>' + someData[i].label + " = " + item.datapoint[1] + " clicks"; 
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
				
				$("#months-link").click(function () 
				{
					plot = $.plot("#placeholder-link", monthData, monthOptions);
					
					overview = $.plot("#overview-link", monthDataOverview, overviewOptionsMonth);
					
					$("#placeholder-link").bind("plothover", function (event, pos, item) 
					{
					 	if (item) 
					 	{
					 		var someData = monthData;
				            var content = item.series.label + " = " + item.datapoint[1] + " clicks";
				            
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
		 		
</body>
</html>