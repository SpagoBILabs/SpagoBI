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
<%@page import="twitter4j.JSONObject"%>


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
	
	TwitterLocationMapDataProcessor locationMapDP = new TwitterLocationMapDataProcessor();
	JSONObject mapData = locationMapDP.locationTracker(searchId);
	
	String ratioInfo = locationMapDP.getRatioInfo(searchId);
	 
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
	    <li class="navtabs"><a href=<%= summaryLink %>> Summary</a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>>Topics</a></li>
	    <li class="navtabs"><a href=<%= networkLink %>>Network</a></li>
	    <li class="navtabs" id="activelink"><a href=<%= distributionLink %>>Distribution</a></li>
   	    <li class="navtabs"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>>ROI</a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
	</ul>
        		
        		
        		
	<div id="locationbox" class="blank_box worldMapMain_box">
	
	
		
<!-- 		<div id="key"></div> -->
		
		<div class="worldMapTitle_box">
		
			<span>Location Tweets</span><img class="map_info" src="<%= application.getContextPath() %>/img/help_map.png" width="16" height="16"></img>
		
		</div>
		
		<br/>
	
		<div id="world-map" class="worldMap_box"></div>
		
		
				
	</div>
			
</div>   
			
	 <script>
			    $(function()
			    {
			    	var gdpData = <%= mapData %>;
			    	$('#world-map').vectorMap({
			    	  map: 'world_mill_en',
			    	  series: {
			              regions: [{
			            	  
			                values: gdpData,
			                scale: ['#ffffff', '#0071A4'],
			                normalizeFunction: 'polynomial'
			              }]
			            },
			            onRegionLabelShow: function(e, el, code)
			            {
			                el.html(el.html()+' (# tweets - '+gdpData[code]+')');
			            }
			      });
			      
// 			      var mapObj = $('#world-map').vectorMap('get', 'mapObject');
// 			      var steps = 4;
// 			      for (var i = 0; i<=steps; i++) {
// 			        var val = 400/steps*i;
// 			        var color = mapObj.series.regions[0].scale.getValue(val);
// 			          $('#key').append('<div style="background-color:' + color + ';">' + val + ' - ' + color + '</div>');
// 			      }
			    });
		  </script>
		  
	
	<script type="text/javascript">
	
			var t = "<%= ratioInfo %>";
	
			$('.map_info').each(function() 
			{
				
				
				$(this).qtip({
    			content: 
    			{
        			text: t
   				},
   				position: 
		   		{ 
		   			corner: 
		   			{ 
		   				target: 'rightMiddle', 
		   				tooltip: 'leftMiddle' 
		   			}
		   		},
		   		show: 
		   		{ 
		   			solo: true, delay: 1 
		   		}, 
		   		hide: 
		   		{ 
		   			delay: 10 
		   		}, 
		   		style: 
		   		{ 
		   			classes: 'qtip-tipped',
		   			tip: true, 
		   			border: 
		   			{ 
		   				width: 0, 
		   				radius: 4,
		   			}, 
		   			name: 'blue', 
		   			width: 420 
		   		} 
				});
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