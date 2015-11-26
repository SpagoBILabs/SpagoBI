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
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="it.eng.spagobi.twitter.analysis.entities.TwitterUser"%>
<%@page import="twitter4j.JSONArray"%>
<%@page import="twitter4j.JSONObject"%>
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
	
	TwitterInfluencersDataProcessor influencersDP = new TwitterInfluencersDataProcessor();
	influencersDP.initializeTwitterTopInfluencers(searchId);
	List<TwitterUser> mostrInfluencers = influencersDP.getMostInfluencers();
	
	TwitterMentionsCloudDataProcessor mentionsDP = new TwitterMentionsCloudDataProcessor();
	mentionsDP.initializeTwitterMentionsCloud(searchId);
	String mentionsCloud = mentionsDP.getMentions().toString();
	
	UsersNetworkGraphDataProcessor usersGraphDP = new UsersNetworkGraphDataProcessor();
	usersGraphDP.initializeUsersNetworkGraph(searchId);
	
	JSONObject profiles = usersGraphDP.getProfiles();
	JSONArray links = usersGraphDP.getLinks();
	
	UsersNetworkLinkMapDataProcessor usersMapDP = new UsersNetworkLinkMapDataProcessor();
	usersMapDP.initializeUsersNetworkLinkMap(searchId);
	
	JSONArray connections = usersMapDP.getLinks();
	JSONArray codes = usersMapDP.getContriesCodes();
	JSONObject weightsLinks = usersMapDP.getWeightLinks();
	
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
	    <li class="navtabs" id="activelink"><a href=<%= networkLink %>> <label id="network"></label> </a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>> <label id="distribution"></label> </a></li>
   	    <li class="navtabs"><a href=<%= sentimentLink %>> <label id="sentiment"></label> </a></li>
	    <li class="navtabs"><a href=<%= impactLink %>> <label id="impact"></label> </a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>> <label id="roi"></label> </a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp"> <label id="searchome"></label> </a></li>
	    
	</ul>
     
	<div id="influencers" class="blank_box topInfluencersMain_box" >
			
		<div class="topInfluencersTitle_box">
			
			<span><label id="topinfluencers"></label></span>
		
		</div>
		
		<br/>
		
		<div id="freewall" class="free-wall" style="margin-top: 20px;"></div>
			
	</div> 
	
	<div id="mentionscloud" class="blank_box mentionsCloudMain_box">
			
		<div class="mentionsCloudTitle_box">
			
			<span><label id="usersmentions"></label></span>
		
		</div>
			
		<br/>
			
		<div id="my_cloud" class="mentionsCloud_box"></div>
		
	</div>
		
	<div id="usersMainGraph" class="blank_box usersGraphMain_box">
		
		<div id="usersGraphTitle" class="usersGraphTitle_box">
			
			<span><label id="usersinteractionsgraph"></label></span>
		
		</div>
	
		<div id="usersGraph" ></div>
	
	</div>
	
	
	<div id="usersTweetLinkMapMain" class="blank_box usersTweetLinkMapMain_box">
		
		<div id="usersTweetLinkMapTitle" class="usersTweetLinkMapTitle_box">
			
			<span><label id="usersinteractionsmap"></label></span>
		
		</div>
	
		<div id="usersTweetLinkMap" ></div>
	
	</div>
			
</div>        	

	<%@include file="../commons/includeSbiSocialAnalysisComponents.jspf"%>

			
	<script type="text/javascript">
								
				var temp = "<img class='cell' width='{width}px;' height='{height}px;' src='{lImg}' text='{lText}' data-title='{lTitle}'></img>";
				var w = 61, h = 61, html = '', limitItem = 32;
				
				<%				
					for (TwitterUser tempObj : mostrInfluencers) 
					{
						
				%>
						var userInfo = "<%= tempObj.getFollowersCount() %> followers <br/> <%= tempObj.getDescription() %>";
						
				//for (var i = 0; i < limitItem; ++i) {
						html += temp.replace(/\{height\}/g, h).replace(/\{width\}/g, w)
							.replace("{lImg}", "<%= tempObj.getProfileImgSrc() %>" )
							.replace("{lText}", userInfo)
							.replace("{lTitle}", "@<%= tempObj.getUsername() %>");
				//}
				<%
					}
				%>
								
				$("#freewall").html(html);
				
				var wall = new freewall("#freewall");
				wall.reset({
					selector: '.cell',
					animate: true,
					cellW: 58,
					cellH: 58,
					onResize: function() {
						wall.refresh();
					}
				});
				wall.fitWidth();
				// for scroll bar appear;
				$(window).trigger("resize");
			</script>
								
			<script type="text/javascript">						
				$('.cell').each(function() 
					{
						var t = $(this).attr("text")
						var iTitle = $(this).attr("data-title")
					    $(this).qtip(
					   	{
					    	content: 
					    	{ 
					    		text: t, 
					    		title: '<div><img style="float:left; vertical-align:middle;" src="<%= application.getContextPath() %>/img/twitter.png" width="20px;" height="20px;" /><div style="vertical-align:middle;margin-left:25px;">' + iTitle + '</div>'
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
					   				radius: 4 
					   			}, 
					   			name: 'blue', 
					   			width: 420 
					   		} 
					    }); 
					 });
					
			</script>
			
			<script type="text/javascript">
					var word_list = <%= mentionsCloud %>
					$(function() {
	 						$("#my_cloud").jQCloud(word_list);
					});
			</script>
			
			
	<script>
	
		var links = <%= links%>
		var profiles = <%= profiles%>
	
		interactionsGraph(links, profiles);
		
	</script>
	
	<script>
	
	var connections = <%= connections %>
	var codes = <%= codes %>
	var weightsLinks = <%= weightsLinks %>
	
	var world110m = "<%= application.getContextPath() %>/json/world-110m.json";
	var countriesCSV = "<%= application.getContextPath() %>/csv/countries.csv";
	
	interactionsNetwork(connections, codes, weightsLinks, world110m, countriesCSV);
	
	</script>

		
</body>
</html>