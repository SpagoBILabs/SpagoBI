<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
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

	chartEngineInstance = (ChartEngineInstance) ResponseContainer.getResponseContainer().getServiceResponse().getAttribute("ENGINE_INSTANCE");
	profile = (UserProfile) chartEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);
	locale = (Locale) chartEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	documentLabel = (String) chartEngineInstance.getEnv().get(EngineConstants.ENV_DOCUMENT_LABEL);

	ds = (IDataSet) chartEngineInstance.getDataSet();
	dsLabel = (ds != null) ? ds.getLabel() : "";
	dsTypeCd = (ds != null) ? ds.getDsType() : "";

	isFromCross = (String) chartEngineInstance.getEnv().get("isFromCross");
	if (isFromCross == null) {
		isFromCross = "false";
	}

	ChartEngineConfig chartEngineConfig = ChartEngineConfig.getInstance();

	spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
	spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
	spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);

 	engineProtocol = request.getScheme();
	engineServerHost = request.getServerName();
	enginePort = "" + request.getServerPort();
	engineContext = request.getContextPath();
	if (engineContext.startsWith("/") || engineContext.startsWith("\\")) {
		engineContext = request.getContextPath().substring(1);
	}

	executionId = request.getParameter("SBI_EXECUTION_ID");
	if (executionId != null) {
		executionId = request.getParameter("SBI_EXECUTION_ID");
	} else {
		executionId = "null";
	}
	actionName = "GET_CHART_DATA_ACTION";
	lightNavigator = request.getParameter("LIGHT_NAVIGATOR_DISABLED");

	JSONObject template = chartEngineInstance.getTemplate();
	String chartTemplate = chartEngineInstance.getTemplate().toString();

	Map<String, String> mapParams = new HashMap<String, String>();

	try {
		JSONObject title = (JSONObject) template.opt("title");
		mapParams.put("titleTxt", title.opt("text").toString());
		JSONObject style = (JSONObject) title.opt("style");
		mapParams.put("titleColor", style.opt("color").toString());
		mapParams.put("titleFontSize", style.opt("fontSize").toString());
		mapParams.put("titleFontWeight", style.opt("fontWeight").toString());
	} catch (Exception ex) {
		// Use default values
		mapParams.put("titleTxt", "Chord Graph");
		mapParams.put("titleColor", "#000000");
		mapParams.put("titleFontSize", "16px");
		mapParams.put("titleFontWeight", "bold");
	}

	try {
		JSONObject title = (JSONObject) template.opt("subtitle");
		mapParams.put("subtitleTxt", title.opt("text").toString());
		JSONObject style = (JSONObject) title.opt("style");
		mapParams.put("subtitleColor", style.opt("color").toString());
		mapParams.put("subtitleFontSize", style.opt("fontSize").toString());
		mapParams.put("subtitleFontWeight", style.opt("fontWeight").toString());
	} catch (Exception ex) {
		// Use default value
		mapParams.put("subtitleTxt", "Edit template in order to set a subtitle!");
		mapParams.put("subtitleColor", "#000000");
		mapParams.put("subtitleFontSize", "10px");
		mapParams.put("subtitleFontWeight", "bold");
	}

	try {
		JSONObject useFilters = (JSONObject) template.opt("filters");
		if (useFilters != null && useFilters.opt("value").toString().equalsIgnoreCase("TRUE")) {
			mapParams.put("useFilters", "TRUE");
			// use filters for obtain matrix from dataset
			JSONObject source = (JSONObject) useFilters.opt("columnsource");
			mapParams.put("source", source.opt("name").toString());
			JSONObject target = (JSONObject) useFilters.opt("columntarget");
			mapParams.put("target", target.opt("name").toString());
			JSONObject value = (JSONObject) useFilters.opt("columnvalue");
			mapParams.put("value", value.opt("name").toString());
		} else {
			// use dataset as-is
			mapParams.put("useFilters", "FALSE");
		}
	} catch (Exception ex) {

	}
	
	int elemSize = 0;
	JSONArray colors = null;
	try {
		JSONObject elements = (JSONObject) template.opt("elements");
		elemSize = elements.optInt("size", 0);
		if (elemSize != 0) {
			JSONArray arr = elements.optJSONArray("color");		
			colors = new JSONArray(); 
			for (int i = 0; i < arr.length(); i++) {			
				colors.put(i,((JSONObject)arr.get(i)).optString("value"));
			}
		}
	} catch (Exception ex) {
			
	}

	String urlProva = engineProtocol+"://" + engineServerHost + ":" + enginePort
			+ "/" + engineContext + "/"
			+ "servlet/AdapterHTTP?ACTION_NAME=" + actionName
			+ "&SBI_EXECUTION_ID=" + executionId + "&ds_label="
			+ dsLabel + "&LIGHT_NAVIGATOR_DISABLED=" + lightNavigator;
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeMessageResource.jspf" %>
		<%@include file="commons/includeD3.jspf"%>
	</head>
	<style>

		body {
		  font: 10px sans-serif;
		}
		
		.chord path {
		  fill-opacity: .67;
		  stroke: #000;
		  stroke-width: .5px;
		}

	</style>
	
	<body>		
	    <div id="<%=executionId%>_title" align="center" style="width:90%;color:<%=mapParams.get("titleColor")%>;font-size:<%=mapParams.get("titleFontSize")%>;font-weight:<%=mapParams.get("titleFontWeight")%>"><%=mapParams.get("titleTxt")%></div>
	    <div id="<%=executionId%>_subtitle" align="center" style="width:90%;color:<%=mapParams.get("subtitleColor")%>;font-size:<%=mapParams.get("subtitleFontSize")%>;font-weight:<%=mapParams.get("subtitleFontWeight")%>"><%=mapParams.get("subtitleTxt")%></div>
	    <div id="chartD3" align="center" style="width:90%;"></div>   	
	
    <script type="text/javascript">  
 

    var exampleMatrix = [
	          		   //black  brown  blond  red            
	       /*black*/   [11975,  5871, 8916, 2868],
	       /*brown*/   [ 1951, 10048, 2060, 6171],
	       /*blond*/   [ 8010, 16145, 8090, 8045],
	       /*  red*/   [ 1013,   990,  940, 6907]
	        ];

   function contains(a, obj) {
	    for (var i = 0; i < a.length; i++) {
	        if (a[i].value === obj) {
	            return true;
	        }
	    }
	    return false;
   }

   function getIndex(a, obj) {
	    for (var i = 0; i < a.length; i++) {
	        if (a[i].value === obj) {
	            return a[i].index;
	        }
	    }
	    return -1;
   }

   function returnValues(item, template){
   	if (item=='width') {
   		return template.width;	
   	} else if (item =='height') {	
   		return template.height;
   	} 
   }

    //Returns an array of tick angles and labels, given a group.
    function groupTicks(d) {
		var k = (d.endAngle - d.startAngle) / d.value;
		return d3.range(0, d.value, 1000).map(function(v, i) {
			return {
				angle: v * k + d.startAngle,
				label: i % 5 ? null : v / 1000 + "k"
				};
			});
	}
	
	//Returns an event handler for fading a given chord group.
	function fade(opacity) {
		return function(g, i) {
		svg.selectAll(".chord path")
		 .filter(function(d) { return d.source.index != i && d.target.index != i; })
		.transition()
		 .style("opacity", opacity);
		};
	}

	var width = returnValues('width',<%=chartTemplate%>),
    height = returnValues('height',<%=chartTemplate%>),
    innerRadius = Math.min(width, height) * .41,
    outerRadius = innerRadius * 1.1;

  var fill = d3.scale.ordinal()
    .domain(d3.range(<%=elemSize%>))
  //.range(["#000000", "#FFDD89", "#957244", "#F26223"]);
	 .range(<%=colors%>);

  var svg = d3.select("#chartD3").append("div")
	 .attr("class", "chart")
	 .style("width", width + "px")
	 .style("height", height + "px")
	.append("svg:svg")
	 .attr("width", width)
	 .attr("height", height)
	.append("svg:g")
	 .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

 d3.json("<%=urlProva %>", function(data) {

	 var useFilters = "<%=mapParams.get("useFilters")%>";
	 var rows = data.rows;
	 var source,target,value;
	 var matrix = new Array(<%=elemSize%>);
	 for (var i = 0; i < matrix.length;i++)
	 	matrix[i] = new Array(<%=elemSize%>);
	 
	 if(useFilters != null && useFilters.toUpperCase() === "TRUE"){
		 
		// reperire i nomi delle colonne source, target, value
		var fields = data.metaData.fields;
		var sourceCol = "<%=mapParams.get("source")%>";
    	var targetCol = "<%=mapParams.get("target")%>";
    	var valueCol = "<%=mapParams.get("value")%>";
  	
	    for (index = 1; index < fields.length; ++index) {
			var variabile = fields[index].header;
	        if(variabile == sourceCol){
	        	var decodificaSource = fields[index].dataIndex;
	    	}else if(variabile == targetCol){
	    	    var decodificaTarget = fields[index].dataIndex;
	    	}else if(variabile == valueCol){
	    	    var decodificaValue = fields[index].dataIndex;
	    	}
	     }; 
	     	 
		 var keys = [];
		 // estraggo le chiavi di colonna/riga
		 for (var i = 0; i < rows.length; i++) {
			 source = rows[i].column_1;
			 if(i == 0)
				 keys.push({"value":source, "index":i});		 	
			 if(!(contains(keys,source)))
				 keys.push({"value":source, "index":(keys.length)});
	  	 };
	  	 
	  	 // assegno i valori secondo le chiavi
		 for (i = 0; i < rows.length; i++) {			 
			 /*source = rows[i].column_1;
			 target = rows[i].column_2;
			 value = rows[i].column_3; */
			 source = rows[i][decodificaSource];
			 target = rows[i][decodificaTarget];
			 value = rows[i][decodificaValue];
			 matrix[getIndex(keys,source)][getIndex(keys,target)] = parseFloat(value);			 
			 //alert(source+","+target+" - "+getIndex(keys,source)+" , "+getIndex(keys,target)+" --> "+value);		
	  	 }; 	
   		
	 }else{
		// use dataset as-is			 			  	 
		 for (i = 0; i < rows.length; i++) {
			 for (j = 0; j < <%=elemSize%>; j++) {
				var column = 'column_'+(j+2);				
			 	matrix[i][j] = parseFloat(rows[i][column]);
			 };		 	
	  	 }; 
	 }

	 drawGraph(matrix);

 });

 function drawGraph(matrix){

 var chord = d3.layout.chord()
  .padding(.05)
  .sortSubgroups(d3.descending)
  .matrix(matrix);

 //disegna i cerchi e definisce l'effetto sul passaggio mouse	
 svg.append("svg:g").selectAll("path")
	.data(chord.groups)
	.enter().append("svg:path")
	.style("fill", function(d) { return fill(d.index); })
	.style("stroke", function(d) { return fill(d.index); })
	.attr("d", d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius))
	.on("mouseover", fade(.1))
	.on("mouseout", fade(1));

 var ticks = svg.append("svg:g").selectAll("g")
	.data(chord.groups)
	.enter().append("svg:g").selectAll("g")
	.data(groupTicks)
	.enter().append("svg:g")
	.attr("transform", function(d) {
		return "rotate(" + (d.angle * 180 / Math.PI - 90) + ")"
		   + "translate(" + outerRadius + ",0)";
		});

 //aggiunge le lineette "graduate"
 ticks.append("svg:line")
	.attr("x1", "1")
	.attr("y1", "0")
	.attr("x2", "5")
	.attr("y2", "0")
	.style("stroke", "#000");

 //aggiunge le label unità di misura
 ticks.append("svg:text")
	.attr("x", "8")
	.attr("dy", ".35em")
	.attr("transform", function(d) { return d.angle > Math.PI ? "rotate(180)translate(-16)" : null; })
	.style("text-anchor", function(d) { return d.angle > Math.PI ? "end" : null; })
	.text(function(d) { return d.label; });
	
 //disegna le fasce da un'area ad un altra
 svg.append("svg:g")
	.attr("class", "chord")
	.selectAll("path")
	.data(chord.chords)
	.enter().append("svg:path")
	.attr("d", d3.svg.chord().radius(innerRadius))
	.style("fill", function(d) { return fill(d.target.index); })
	.style("opacity", 1);

}

	    </script>
 
	</body>

</html>