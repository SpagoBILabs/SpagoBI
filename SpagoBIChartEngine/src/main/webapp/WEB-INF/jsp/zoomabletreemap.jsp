<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" 
	     contentType="text/html; charset=UTF-8" 
	     pageEncoding="UTF-8"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engines.chart.ChartEngineConfig"%>
<%@page import="it.eng.spagobi.engines.chart.ChartEngineInstance"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.tools.dataset.bo.IDataSet"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	ChartEngineInstance chartEngineInstance;
	UserProfile profile;
	Locale locale;
	IDataSet ds;
	String dsLabel;
	String dsTypeCd;
	String isFromCross;
	String engineContext;
	String engineServerHost;
	String enginePort;
	String engineProtocol;
	String executionId;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	String documentLabel;
	String actionName;
	String lightNavigator;
	
	//chartEngineInstance = (ChartEngineInstance)ResponseContainerAccess.getResponseContainer(request).getServiceResponse().getAttribute("ENGINE_INSTANCE");
	chartEngineInstance = (ChartEngineInstance)ResponseContainer.getResponseContainer().getServiceResponse().getAttribute("ENGINE_INSTANCE");
	profile = (UserProfile)chartEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);
	locale = (Locale)chartEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	documentLabel = (String)chartEngineInstance.getEnv().get(EngineConstants.ENV_DOCUMENT_LABEL);

	ds =  (IDataSet)chartEngineInstance.getDataSet();
	dsLabel = (ds != null) ? ds.getLabel() : "";
	dsTypeCd = (ds != null) ? ds.getDsType() : "";
	
	isFromCross = (String)chartEngineInstance.getEnv().get("isFromCross");
	if (isFromCross == null) {
		isFromCross = "false";
	}
	
	ChartEngineConfig chartEngineConfig = ChartEngineConfig.getInstance();
    
    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
    spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
    
 // used in local ServiceRegistry
  	engineProtocol = request.getScheme();
 	engineServerHost = request.getServerName();
 	enginePort = "" + request.getServerPort();
    engineContext = request.getContextPath();
    if( engineContext.startsWith("/") || engineContext.startsWith("\\") ) {
    	engineContext = request.getContextPath().substring(1);
    }
    
    executionId = request.getParameter("SBI_EXECUTION_ID");
    if(executionId != null) {
    	executionId = request.getParameter("SBI_EXECUTION_ID");
    } else {
    	executionId = "null";
    } 
     actionName= "GET_CHART_DATA_ACTION";
     lightNavigator= request.getParameter("LIGHT_NAVIGATOR_DISABLED");
     
 	JSONObject template = chartEngineInstance.getTemplate();
 	String chartTemplate = chartEngineInstance.getTemplate().toString();
 	
 	Map<String,String> mapParams = new HashMap<String,String>();

 	try{
	    JSONObject title = (JSONObject)template.opt("title");
	    mapParams.put("titleTxt", title.opt("text").toString());
	    JSONObject style=(JSONObject)title.opt("style");
	    mapParams.put("titleColor", style.opt("color").toString());
	    mapParams.put("titleFontSize", style.opt("fontSize").toString());
	    mapParams.put("titleFontWeight", style.opt("fontWeight").toString());
 	} catch(Exception ex)
 	{
 		// Use default values
 		mapParams.put("titleTxt", "TreeMap");
 	 	mapParams.put("titleColor", "#000000");
 	 	mapParams.put("titleFontSize", "16px");
 	 	mapParams.put("titleFontWeight", "bold");
 	}
 
 	mapParams.put("subtitleTxt", "TreeMap");
 	mapParams.put("subtitleColor", "#000000");
 	mapParams.put("subtitleFontSize", "14px");
 	mapParams.put("subtitleFontWeight", "bold");

 	try{
	    JSONObject title=(JSONObject)template.opt("subtitle");
	    mapParams.put("subtitleTxt", title.opt("text").toString());
	    JSONObject style=(JSONObject)title.opt("style");
	    mapParams.put("subtitleColor", style.opt("color").toString());
	    mapParams.put("subtitleFontSize", style.opt("fontSize").toString());
	    mapParams.put("subtitleFontWeight", style.opt("fontWeight").toString());
 	} catch(Exception ex)
 	{
 		// Use default value
 	} 	
   
   	
    // gets analytical driver
   // Map analyticalDrivers  = chartEngineInstance.getAnalyticalDrivers();
 	//String JSONUrl = "http://localhost:8080/SpagoBIChartEngine/servlet/AdapterHTTP?ACTION_NAME=CHART_ENGINE_D3_START_ACTION&SBI_EXECUTION_ID="+executionId+"&ds_label=treemap&LIGHT_NAVIGATOR_DISABLED=null";
 	String urlProva= engineProtocol+"://"+engineServerHost+":"+enginePort+"/"+engineContext+"/"+"servlet/AdapterHTTP?ACTION_NAME="+actionName+"&SBI_EXECUTION_ID="+executionId+"&ds_label="+dsLabel+"&LIGHT_NAVIGATOR_DISABLED="+lightNavigator;
%>



<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeMessageResource.jspf" %>
		<%@include file="commons/includeD3.jspf"%>
		<link id="zoomabletreemap-style" rel="styleSheet" href ="../css/zoomabletreemap.css" type="text/css" />
		
	</head>
	
	<body>		
	    <div id="<%=executionId%>_title" align="center" style="width:100%;color:<%=mapParams.get("titleColor") %>;font-size:<%=mapParams.get("titleFontSize") %>;font-weight:<%=mapParams.get("titleFontWeight") %>"><%=mapParams.get("titleTxt") %></div>
	    <div id="<%=executionId%>_subtitle" align="center" style="width:100%;color:<%=mapParams.get("subtitleColor") %>;font-size:<%=mapParams.get("subtitleFontSize") %>;font-weight:<%=mapParams.get("subtitleFontWeight") %>"><%=mapParams.get("subtitleTxt") %></div>

	    <div id="chartD3" align="center" style="width:100%;"></div>


	<script type="text/javascript">

	    function prepareData(data,template){
    	//reperisco le variabili dal template 
    	var label = template.label.field;
    	var key = template.key.field;
    	var parentKey = template.parentkey.field;
    	var value = (template.value)?template.value.field:null;
    	
 		fields = data.metaData.fields;
    	rows = data.rows;
      	
    	var dataMap=new Object;
     	for (index = 1; index < fields.length; ++index) {
    		dataMap[fields[index].dataIndex]=fields[index].header;
			var variabile = fields[index].header;
           		if(variabile == label){
         			var decodificaLabel = fields[index].dataIndex;
    	      	}else if(variabile == key){
    	      			var decodificaKey = fields[index].dataIndex;
    	      	}else if(variabile == parentKey){
    	      			var decodificaParentKey = fields[index].dataIndex;
    	      	}else if(variabile == value){
    	      			var decodificaSerie = fields[index].dataIndex;
    	      	}
     		};  
     	
     	var root = {name:"root",children:[]};
     	var map = new Object;
     	for (index = 0; index < rows.length; ++index) {

     		var nodo = { name: rows[index][decodificaLabel], key: rows[index][decodificaKey], parentKey:rows[index][decodificaParentKey], value:(decodificaSerie)?parseFloat(rows[index][decodificaSerie]):1};
     		map[nodo.key]=nodo;
     	};
     	
     	var node;
     	var parent1;
     	for (var key in map) {
     	  if (map.hasOwnProperty(key)) {
     		node=map[key];
    
	     	if(node.parentKey && map[node.parentKey]) 		
	     	{
	     		parent1 = map[node.parentKey];
	     		//console.log(parent1);
	     		
	     		if(parent1.children) 
	     		{
	     			parent1.children.push(node);
	     		}
	     		else 
	     		{
	     			parent1.children=[node];
	     		}
	     	}
	     	else
	     	{
	     		root.children.push(node);
	     	}
     	  }
     	}
     
    	return root;
    } 
	
	
	
	
    function returnValues(item, template){
    	//console.log('template');	
    	//console.log(template);	
    	if (item=='width') {
    		return template.width;	
    	} else if (item =='height') {	
    		return template.height;
    	} 
    }
	
var margin = {top: 20, right: 0, bottom: 0, left: 0},
    width = returnValues('width',<%=chartTemplate%>),
    height = returnValues('height',<%=chartTemplate%>) - margin.top - margin.bottom,
    formatNumber = d3.format(",g"),
    transitioning;

var x = d3.scale.linear()
    .domain([0, width])
    .range([0, width]);

var y = d3.scale.linear()
    .domain([0, height])
    .range([0, height]);

var treemap = d3.layout.treemap()
    .value(function(d) {return d.value})
    .children(function(d, depth) { return depth ? null : d._children; })
    .sort(function(a, b) { console.log(a.value); return a.value - b.value; })
    .ratio(height / width * 0.5 * (1 + Math.sqrt(5)))
    .round(false);

var svg = d3.select("#chartD3").append("svg")
    .attr("width", width)
    .attr("height", height)
    .style("margin-left", -margin.left + "px")
    .style("margin.right", -margin.right + "px")
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
    .style("shape-rendering", "crispEdges");

var color = d3.scale.category20b();

var grandparent = svg.append("g")
    .attr("class", "grandparent");

grandparent.append("rect")
    .attr("y", -margin.top)
    .attr("width", width)
    .attr("height", margin.top);

grandparent.append("text")
    .attr("x", 6)
    .attr("y", 6 - margin.top)
    .attr("dy", ".75em");

d3.json("<%=urlProva %>", function(root) {
  root=prepareData(root,<%=chartTemplate%>);
  initialize(root);
  accumulate(root);
  layout(root);
  display(root);

  function initialize(root) {
    root.x = root.y = 0;
    root.dx = width;
    root.dy = height;
    root.depth = 0;
  }

  // Aggregate the values for internal nodes. This is normally done by the
  // treemap layout, but not here because of our custom implementation.
  // We also take a snapshot of the original children (_children) to avoid
  // the children being overwritten when when layout is computed.
  function accumulate(d) {
     console.log(d.value);
    return (d._children = d.children)
        ? d.value = d.children.reduce(function(p, v) { return p + accumulate(v); }, 0)
        : d.value;
  }

  // Compute the treemap layout recursively such that each group of siblings
  // uses the same size (1×1) rather than the dimensions of the parent cell.
  // This optimizes the layout for the current zoom state. Note that a wrapper
  // object is created for the parent node for each group of siblings so that
  // the parent's dimensions are not discarded as we recurse. Since each group
  // of sibling was laid out in 1×1, we must rescale to fit using absolute
  // coordinates. This lets us use a viewport to zoom.
  function layout(d) {
    if (d._children) {
      treemap.nodes({_children: d._children});
      d._children.forEach(function(c) {
        c.x = d.x + c.x * d.dx;
        c.y = d.y + c.y * d.dy;
        c.dx *= d.dx;
        c.dy *= d.dy;
        c.parent = d;
        layout(c);
      });
    }
  }

  function display(d) {
    grandparent
        .datum(d.parent)
        .on("click", transition)
      .select("text")
        .text(name(d));

    var g1 = svg.insert("g", ".grandparent")
        .datum(d)
        .attr("class", "depth");

    var g = g1.selectAll("g")
        .data(d._children)
      .enter().append("g");

    g.filter(function(d) { return d._children; })
        .classed("children", true)
        .on("click", transition);

    g.selectAll(".child")
        .data(function(d) { return d._children || [d]; })
      .enter().append("rect")
        .attr("class", "child")
        .call(rect);

    g.append("rect")
        .attr("class", "parent")
        .call(rect)
      .append("title")
        .text(function(d) { return d.name + " " + formatNumber(d.value); });

    g.append("text")
        .attr("dy", ".75em")
        .text(function(d) { return d.name; })
        .call(text);

    function transition(d) {
      if (transitioning || !d) return;
      transitioning = true;

      var g2 = display(d),
          t1 = g1.transition().duration(750),
          t2 = g2.transition().duration(750);

      // Update the domain only after entering new elements.
      x.domain([d.x, d.x + d.dx]);
      y.domain([d.y, d.y + d.dy]);

      // Enable anti-aliasing during the transition.
      svg.style("shape-rendering", null);

      // Draw child nodes on top of parent nodes.
      svg.selectAll(".depth").sort(function(a, b) { return a.depth - b.depth; });

      // Fade-in entering text.
      g2.selectAll("text").style("fill-opacity", 0);

      // Transition to the new view.
      t1.selectAll("text").call(text).style("fill-opacity", 0);
      t2.selectAll("text").call(text).style("fill-opacity", 1);
      t1.selectAll("rect").call(rect);
      t2.selectAll("rect").call(rect);

      // Remove the old node when the transition is finished.
      t1.remove().each("end", function() {
        svg.style("shape-rendering", "crispEdges");
        transitioning = false;
      });
    }

    return g;
  }

  function text(text) {
    text.attr("x", function(d) { return x(d.x) + 6; })
        .attr("y", function(d) { return y(d.y) + 6; });
  }

  function rect(rect) {
    rect.attr("x", function(d) { return x(d.x); })
        .attr("y", function(d) { return y(d.y); })
        .attr("width", function(d) { return x(d.x + d.dx) - x(d.x); })
        .attr("height", function(d) { return y(d.y + d.dy) - y(d.y); })
        //.style("background", function(d) { return color(d.name) });
					.style("fill", function(d) { return d.parent ? color(d.name) : null; });

  }

  function name(d) {
    return d.parent
        ? name(d.parent) + " > " + d.name
        : d.name;
  }
});		
</script>
 
	</body>

</html>