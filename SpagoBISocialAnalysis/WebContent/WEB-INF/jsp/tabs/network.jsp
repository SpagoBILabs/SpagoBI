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

	<%@include file="../commons/includeSbiSocialAnalysisResources.jspf"%>	
	
	<title>Twitter Analysis</title>
	
</head>
<body>

<div id="navigation">

	<div id="report-loading" class="loading"><img src="<%= application.getContextPath() %>/img/ajax-loader.gif" width="32" height="32" /><br /><strong>Loading</strong></div>

	<ul class="navtabs tabsStyle">
	    <li class="navtabs"><a href=<%= summaryLink %>> Summary</a></li>
	    <li class="navtabs"><a href=<%= topicsLink %>>Topics</a></li>
	    <li class="navtabs" id="activelink"><a href=<%= networkLink %>>Network</a></li>
	    <li class="navtabs"><a href=<%= distributionLink %>>Distribution</a></li>
   	    <li class="navtabs"><a href=<%= sentimentLink %>>Sentiment</a></li>
	    <li class="navtabs"><a href=<%= impactLink %>>Impact</a></li>
	    <% if(withDocs) { %>
	    	<li class="navtabs"><a href=<%= roiLink %>>ROI</a></li>
	    <% } %>
	    <li class="navtabs" style="float:right;"><a href="<%= application.getContextPath() %>/index.jsp">Search</a></li>
	    
	</ul>
     
	<div id="influencers" class="blank_box topInfluencersMain_box" >
			
		<div class="topInfluencersTitle_box">
			
			<span>Top Influencers</span>
		
		</div>
		
		<br/>
		
		<div id="freewall" class="free-wall" style="margin-top: 20px;"></div>
			
	</div> 
	
	<div id="mentionscloud" class="blank_box mentionsCloudMain_box">
			
		<div class="mentionsCloudTitle_box">
			
			<span>Users Mentions</span>
		
		</div>
			
		<br/>
			
		<div id="my_cloud" class="mentionsCloud_box"></div>
		
	</div>
		
	<div id="usersMainGraph" class="blank_box usersGraphMain_box">
		
		<div id="usersGraphTitle" class="usersGraphTitle_box">
			
			<span>Users Interactions Graph</span>
		
		</div>
	
		<div id="usersGraph" ></div>
	
	</div>
	
	
	<div id="usersTweetLinkMapMain" class="blank_box usersTweetLinkMapMain_box">
		
		<div id="usersTweetLinkMapTitle" class="usersTweetLinkMapTitle_box">
			
			<span>Users Interactions Map</span>
		
		</div>
	
		<div id="usersTweetLinkMap" ></div>
	
	</div>
			
</div>        	

			
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
			
			var nodes = {}
			
			// Compute the distinct nodes from the links.
			links.forEach(function(link) {
				  link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
				  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
				});			

			var width =  $('#usersMainGraph').width();
			var height = $('#usersMainGraph').height()-$('#usersGraphTitle').innerHeight();
			var r = 25;

			var force = d3.layout.force()
			    .nodes(d3.values(nodes))
			    .links(links)
			    .size([width, height])
			    .charge(-780)
			    .linkDistance(50)
				.on("tick", tick)
			    .start();
			
			var drag = force.drag()
		    .on("dragstart", dragstart);
			
			var svg = d3.select("#usersGraph").append("svg")
		    .attr("width", width)
		    .attr("height", height);
			
			svg.append("svg:rect")
		    .attr("width", width)
		    .attr("height", height)
		    .style("stroke", "#000");
			
			var link = svg.selectAll(".link")
		    .data(force.links())
		  .enter().append("line")
		    .attr("class", "link");

			var node = svg.selectAll(".node")
			    .data(force.nodes())
			  .enter().append("g")
			    .attr("class", "node")
			    .call(force.drag);

			node.append("svg:defs")
				.append("svg:pattern")
				.attr("id", function(d, i) {
					return "image"+i;	
				})
				.attr("x", "25")
				.attr("y", "25")
				.attr("width", "50")
				.attr("height", "50")
				.attr("patternUnits", "userSpaceOnUse")				
				.append("svg:image")
				.attr("xlink:href", function(d, i) {
			        // d is the node data, i is the index of the node
						return profiles[d.name];
			    })
				.attr("x", "0")
				.attr("y", "0")
// 				.attr("id", "fillImage");
				.attr("width", "50")
				.attr("height", "50");			
			
		 var circle = node.append("circle")
		    .attr("r", r)
			.attr("fill",function(d, i) {
				return "url(#image"+i+")";	
			});
		 
		 function tick() {
			 
			 node.attr("cx", function(d) { return d.x = Math.max(r, Math.min(width - r, d.x)); })
			    .attr("cy", function(d) { return d.y = Math.max(r, Math.min(height - r, d.y)); });

			link.attr("x1", function(d) { return d.source.x; })
			    .attr("y1", function(d) { return d.source.y; })
			    .attr("x2", function(d) { return d.target.x; })
			    .attr("y2", function(d) { return d.target.y; });

			node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
			}
		 
		 function dragstart(d) {
			  d3.select(this).classed("fixed", d.fixed = true);
			}

	</script>
	
	<script>
	
	var connections = <%= connections %>
	var codes = <%= codes %>
	var weightsLinks = <%= weightsLinks %>
	
	var connectionsMap = [];
	var linksByOrigin = {};
	
	var w =  $('#usersTweetLinkMapMain').width();
	var h = $('#usersTweetLinkMapMain').height()-$('#usersTweetLinkMapTitle').innerHeight();
	var centered;
	
    var rotate = [0,0];
	
	var tooltip = d3.select("#usersTweetLinkMap").append("div").attr("class", "tooltip hidden");
	
	//offsets for tooltips
	var offsetL = document.getElementById('usersTweetLinkMap').offsetLeft+20;
	var offsetT = document.getElementById('usersTweetLinkMap').offsetTop+10;
	
	var maxPointRadius = 7;
	
	var area = w * h;
	
	var radius = h/3;

	var zoom = d3.behavior.zoom().scaleExtent([1, 9]).on("zoom", move);
	
	function move() {

		  var t = d3.event.translate;
		  var s = d3.event.scale; 
		  zscale = s;
		  var hz = h/4;
		
		
		  t[0] = Math.min(
		    (w/h)  * (s - 1), 
		    Math.max( w * (1 - s), t[0] )
		  );
		
		  t[1] = Math.min(
		    hz * (s - 1) + hz * s, 
		    Math.max(h  * (1 - s) - hz * s, t[1])
		  );
		
		  zoom.translate(t);
		  g.attr("transform", "translate(" + t + ")scale(" + s + ")");

	};
	
	var drag = d3.behavior.drag()
    .on("dragstart", function() {
    // Adapted from http://mbostock.github.io/d3/talk/20111018/azimuthal.html and updated for d3 v3
      var proj = projection.rotate();
      m0 = [d3.event.sourceEvent.pageX, d3.event.sourceEvent.pageY];
      o0 = [-proj[0],-proj[1]];
    })
    .on("drag", function() {
      if (m0) {
        var m1 = [d3.event.sourceEvent.pageX, d3.event.sourceEvent.pageY],
            o1 = [o0[0] + (m0[0] - m1[0]) / 4, o0[1] + (m1[1] - m0[1]) / 4];
        projection.rotate([-o1[0], -o1[1]]);
      }
      
   // Update the map
      path = d3.geo.path().projection(projection);
      d3.selectAll("path").attr("d", path);
    });


// 	var projection = d3.geo.kavrayskiy7().precision(.1),
	var projection = d3.geo.azimuthalEqualArea().scale(h/5).clipAngle(180 - 1e-3)
    .translate([w / 2, h / 2])
    .precision(0.3)
    .rotate(rotate);
    
    var color = d3.scale.category20(),
	graticule = d3.geo.graticule();

// 	var projection = d3.geo.orthographic()
//     .scale(radius)
//     .translate([w / 2, h / 2])
//     .clipAngle(90);
	
	var path = d3.geo.path()
	    .projection(projection);
	
	var svg = d3.select("#usersTweetLinkMap").append("svg")
	    .attr("width", w)
	    .attr("height", h)	    
	    .call(zoom)
// 	    .call(drag)
	    .append("g");

  g = svg.append("g");


	    

	d3.json("<%= application.getContextPath() %>/json/world-110m.json", function(error, world) {
		
		
	  var countries = topojson.feature(world, world.objects.countries).features,
	      neighbors = topojson.neighbors(world.objects.countries.geometries);
	  
	  var globe = {type: "Sphere"};
	    g.append("path")
	    .datum(globe)
	    .attr("class", "foreground")
	    .attr("d", path);
	
	  g.selectAll(".country")
	      .data(countries)
	    .enter().insert("path", ".graticule")
	      .attr("class", "country")
	      .attr("d", path);
// 	      .style("fill", "#FFB875");

		
	  
	  d3.csv("<%= application.getContextPath() %>/csv/countries.csv", function(error, data) {// read in and plot the circles
		  
		  var filterData = [];
	  	  var pointData = [];
	  	  var counterPointData = 0;
	  
		  for(var i = 0; i < codes.length; i++)
		  {
			  for(var j = 0; j < data.length; j++)
			  {
			  		if(codes[i][0] == data[j].code)
		  			{
		  				filterData[data[j].code] = data[j];
		  				pointData[counterPointData++] = {country: data[j].country.toUpperCase(), code: data[j].code ,lat: data[j].lat ,lon: data[j].lon , rep: codes[i][1]}
// 		  				pointData.push(data[j]);
		  				break;
		  			}
			  }
						  
		  }  
		  
// 		  console.log(pointData);

		  

	  	
		  
	        g.selectAll("circle").data(pointData).enter().append("circle").attr("class", "circle")
	        .attr("cx", function(d) {
	            return projection([d.lon, d.lat])[0];
	        }).attr("cy", function(d) {
	            return projection([d.lon, d.lat])[1];
	        }).attr("r", function(d) {
	        	if(d.rep <= 6)
	        	{
	        		return d.rep;
	        	}
	        	else
	        	{
	        		return maxPointRadius;
	        	}
	        })
	        .on("mouseover", function(d,i) {
	          	var mouse = d3.mouse(svg.node()).map( function(d) { return parseInt(d); } );

	          tooltip.classed("hidden", false)
	                 .attr("style", "left:"+(mouse[0]+offsetL)+"px;top:"+(mouse[1]+offsetT)+"px")
	                 .html("Country: <label style='font-size: 25px; font-weight:bold;'>" + d.country + "</label><br/>" + "Interactions: <label style='font-size: 25px; font-weight:bold;'>" + d.rep + "</label>");

	          })
	          .on("mouseout",  function(d,i) {
	            tooltip.classed("hidden", true);
	          }); 

	        
	        
	        var lineTransition = function lineTransition(path) {
	            path.transition()

	                .duration(5500)
// 	                .attrTween("stroke-dasharray", tweenDash)
	                .each("end", function(d,i) { 

	                });
	        };
	        var tweenDash = function tweenDash() {

	            var len = this.getTotalLength(),
	                interpolate = d3.interpolateString("0," + len, len + "," + len);

	            return function(t) { return interpolate(t); };
	        };


	        console.log(weightsLinks);
	        console.log(connections);	
	        
	        var connectionsData = [];
	        var count={};
	        
	        for(var i = 0; i < connections.length; i++)
        	{
        		var originCode = connections[i][0];
        		var destinationCode = connections[i][1];
        		
        		var label = originCode+destinationCode;
        		var inverseLabel = destinationCode+originCode;
        		
        		var tempArc = 
        		{
       	                type: "LineString",
       	                coordinates: [
       	                    [ filterData[originCode].lon, filterData[originCode].lat ],
       	                    [ filterData[destinationCode].lon, filterData[destinationCode].lat ]
       	                ],
       	                label: label,
       	                inverseLabel: inverseLabel,
       	                origin: filterData[originCode].country,
       	                destination: filterData[destinationCode].country
       	    	};
        		
        		if(weightsLinks[label])
        		{
        			connectionsData.push(tempArc);	
        		}
        		
        		
        	}
	        

	        // Standard enter / update 
	        var pathArcs = g.selectAll(".arc")
	            .data(connectionsData)
	            .enter()
	            .append("path")
	            .attr("class", "arc")
	            .attr("fill", "none")
				.attr("d", path)
	            .attr("stroke", "#0084B4")
	            .attr("stroke-width", function(d)
	            {
	            	var strokeWidth = weightsLinks[d.label] ? weightsLinks[d.label] : 0;
	            	if(strokeWidth < 5)
	            	{
	            		return weightsLinks[d.label] ? weightsLinks[d.label] : 0;
	            	}
	            	else
	            	{
	            		return 6;	
	            	}
	            })
	            // Uncomment this line to remove the transition
	            .call(lineTransition)
	            .on("mouseover", function(d,i) {
	          		var mouse = d3.mouse(svg.node()).map( function(d) { return parseInt(d); } );

	          tooltip.classed("hidden", false)
	                 .attr("style", "left:"+(mouse[0]+offsetL)+"px;top:"+(mouse[1]+offsetT)+"px")
	                 .html("# interactions between " + d.origin + " and " + d.destination + ": " + (weightsLinks[d.label] ? weightsLinks[d.label] : ""));
	          })
	          .on("mouseout",  function(d,i) {
	            tooltip.classed("hidden", true);
	          });

	        //exit
// 	        pathArcs.exit().remove();

	    });

	  
// 	  d3.timer(function() {
// 		    var angle = velocity * (Date.now() - then);
// 		    projection.rotate([angle,0,0]);
// 		    svg.selectAll("path")
// 		      .attr("d", path.projection(projection));
// 		  });
	  
	    });	
	
	


	
	
// 	for(var i = 0; i < connections.length; i++)
// 	{
// 		var connection = connections[i];
		
// 		d3.geo.greatArc().source(connection["source"]).target(connection["target"]);
// 	}
	
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