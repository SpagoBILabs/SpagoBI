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
	
	JSONArray positiveTopics = sentimentDP.getPositiveBC();
	JSONArray neutralTopics = sentimentDP.getNeutralBC();
	JSONArray negativeTopics = sentimentDP.getNegativeBC();
	 
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
	
	<div style="width: 90%; float:left;">
	
		<div class="blank_box polarity_barchartMain" >
		
			<div class="twitterPolarityBCTitle_box">
				
				<span>Positive Topics</span>
			
			</div>
			
			<div class="posTopics_label" style="float:left;"></div>
			<div class="positive_barchart" style="float:left;"></div>
			
		</div>
		
		<div class="blank_box polarity_barchartMain" >
		
			<div class="twitterPolarityBCTitle_box">
				
				<span>Neutral Topics</span>
			
			</div>
			
			<div class="neuTopics_label" style="float:left;"></div>
			<div class="neutral_barchart" style="float:left;"></div>
			
		</div>
		
		<div class="blank_box polarity_barchartMain" >
		
			<div class="twitterPolarityBCTitle_box">
				
				<span>Negative Topics</span>
			
			</div>
			
			<div class="negTopics_label" style="float:left;"></div>
			<div class="negative_barchart" style="float:left;"></div>
			
		</div>	
	
	</div>
			
</div>      

	<script>

		var positiveData = <%= positiveTopics %>
		var neutralData = <%= neutralTopics %>
		var negativeData = <%= negativeTopics %>

// 		var positiveData = [{name:"test1", value:5}, {name:"test2", value:20}, {name:"test3", value:15}, {name:"test4", value:30}, {name:"test5", value:2}, {name:"test6", value:1}];
// 		var neutralData = [{name:"test1", value:5}, {name:"test2", value:20}, {name:"test3", value:15}, {name:"test4", value:30}, {name:"test5", value:2}, {name:"test6", value:1}];
// 		var negativeData = [{name:"test1", value:5}, {name:"test2", value:20}, {name:"test3", value:15}, {name:"test4", value:30}, {name:"test5", value:2}, {name:"test6", value:1}];

		
		
// 		var w = $('.polarity_barchartMain').width();
		
		d3.select(".positive_barchart")
		  .selectAll("div")
		  .data(positiveData)
		  .enter()
		  .append("div")
		  .style("width", function(d)
		    				{ if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		   .text(function(d) { return d.value; });
	       
		
		d3.select(".posTopics_label")
		  .selectAll("div")
		  .data(positiveData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
		
	

		
		d3.select(".neutral_barchart")
		  .selectAll("div")
		    .data(neutralData)
		  .enter().append("div")
		    .style("width", function(d) { if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		    .text(function(d) { return d.value; });
		
		d3.select(".neuTopics_label")
		  .selectAll("div")
		  .data(neutralData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
	
	
		d3.select(".negative_barchart")
		  .selectAll("div")
		    .data(negativeData)
		  .enter().append("div")
		    .style("width", function(d) { if(d.value > 30) return "300px"; else return (d.value*10) + "px"; })
		    .text(function(d) { return d.value; });
		
		d3.select(".negTopics_label")
		  .selectAll("div")
		  .data(negativeData)
		  .enter()
		  .append("div")
		  .style("margin", "1px 1px 10px 1px")
		  .style("padding", "3px")
		  .text(function(d) { return d.name });
	
	
	
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