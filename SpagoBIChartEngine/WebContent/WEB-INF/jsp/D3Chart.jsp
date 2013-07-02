<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  <%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


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
   String chartTemplate = chartEngineInstance.getTemplate().toString();
 
    // gets analytical driver
   // Map analyticalDrivers  = chartEngineInstance.getAnalyticalDrivers();
 	//String JSONUrl = "http://localhost:8080/SpagoBIChartEngine/servlet/AdapterHTTP?ACTION_NAME=CHART_ENGINE_D3_START_ACTION&SBI_EXECUTION_ID="+executionId+"&ds_label=treemap&LIGHT_NAVIGATOR_DISABLED=null";
 	String urlProva= "http://"+engineServerHost+":"+enginePort+"/"+engineContext+"/"+"servlet/AdapterHTTP?ACTION_NAME="+actionName+"&SBI_EXECUTION_ID="+executionId+"&ds_label="+dsLabel+"&LIGHT_NAVIGATOR_DISABLED="+lightNavigator;
%>



<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeD3.jspf"%>
	</head>
	
	<body>		
	    <div id="<%=executionId%>_title" align="center" style="width:90%;"></div>
	    <div id="<%=executionId%>_subtitle" align="center" style="width:90%;"></div>
	    <div id="chartD3" align="center" style="width:90%;"></div>   	
	
    <script type="text/javascript">  
   
       	var w = 1280 - 80,
        h = 800 - 180,
        x = d3.scale.linear().range([0, w]),
        y = d3.scale.linear().range([0, h]),
        color = d3.scale.category20c(),
        root,
        node;

    var treemap = d3.layout.treemap()
        .round(false)
        .size([w, h])
        .sticky(true)
        .value(function(d) { return d.size; });

    var svg = d3.select("#chartD3").append("div")
        .attr("class", "chart")
        .style("width", w + "px")
        .style("height", h + "px")
      .append("svg:svg")
        .attr("width", w)
        .attr("height", h)
      .append("svg:g")
        .attr("transform", "translate(.5,.5)");  	
    	  
    function prepareData(data,template){
    	//reperisco le variabili dal template 
    	var label = template.label.field;
    	var key = template.key.field;
    	var parentKey = template.parentkey.field;
    	var value = template.value.field;
    	
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
    	      		else {
           					var decodificaSerie = 1;
    	      		}
     		};  
     	
     	var root = {nome:"root",children:[]};
     	var map = new Object;
     	for (index = 0; index < rows.length; ++index) {
     		var nodo = { name: rows[index][decodificaLabel], key: rows[index][decodificaKey], parentKey:rows[index][decodificaParentKey], size:rows[index][decodificaSerie]};
     	
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
     		console.log(parent1);
     		
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
    
	var fields,rows;
    d3.json("<%=urlProva %>", function(data) {
     node=root=prepareData(data,<%=chartTemplate%>);
			
      console.log(root);
      var nodes = treemap.nodes(root)
          .filter(function(d) { return !d.children; });

      var cell = svg.selectAll("g")
          .data(nodes)
        .enter().append("svg:g")
          .attr("class", "cell")
          .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
          .on("click", function(d) { return zoom(node == d.parent ? root : d.parent); });

      cell.append("svg:rect")
          .attr("width", function(d) { return d.dx - 1; })
          .attr("height", function(d) { return d.dy - 1; })
          .style("fill", function(d) { return color(d.parent.name); });

      cell.append("svg:text")
          .attr("x", function(d) { return d.dx / 2; })
          .attr("y", function(d) { return d.dy / 2; })
          .attr("dy", ".35em")
          .attr("text-anchor", "middle")
          .text(function(d) { return d.name; })
          .style("opacity", function(d) { d.w = this.getComputedTextLength(); return d.dx > d.w ? 1 : 0; });

      d3.select(window).on("click", function() { zoom(root); });

      d3.select("select").on("change", function() {
        treemap.value(this.value == "size" ? size : count).nodes(root);
        zoom(node);
      });
    });
    function size(d) {
      return d.size;
    }

    function count(d) {
      return 1;
    }

    function zoom(d) {
      var kx = w / d.dx, ky = h / d.dy;
      x.domain([d.x, d.x + d.dx]);
      y.domain([d.y, d.y + d.dy]);

      var t = svg.selectAll("g.cell").transition()
          .duration(d3.event.altKey ? 7500 : 750)
          .attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

      t.select("rect")
          .attr("width", function(d) { return kx * d.dx - 1; })
          .attr("height", function(d) { return ky * d.dy - 1; });

      t.select("text")
          .attr("x", function(d) { return kx * d.dx / 2; })
          .attr("y", function(d) { return ky * d.dy / 2; })
          .style("opacity", function(d) { return kx * d.dx > d.w ? 1 : 0; });

      node = d;
      d3.event.stopPropagation();
    }
	    </script>
 
	</body>

</html>