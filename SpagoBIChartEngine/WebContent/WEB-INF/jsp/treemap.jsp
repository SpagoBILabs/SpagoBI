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
		<link id="treemap-style" rel="styleSheet" href ="../css/treemap.css" type="text/css" />

	</head>
	
	<body>		
	    <div id="<%=executionId%>_title" align="center" style="width:90%;color:<%=mapParams.get("titleColor") %>;font-size:<%=mapParams.get("titleFontSize") %>;font-weight:<%=mapParams.get("titleFontWeight") %>"><%=mapParams.get("titleTxt") %></div>
	    <div id="<%=executionId%>_subtitle" align="center" style="width:90%;color:<%=mapParams.get("subtitleColor") %>;font-size:<%=mapParams.get("subtitleFontSize") %>;font-weight:<%=mapParams.get("subtitleFontWeight") %>"><%=mapParams.get("subtitleTxt") %></div>
	    		<div class="footer" align="center">
			Tiles dimension by 
			<select>
				<option value="size">Size</option>
				<option value="count">Count</option>
			</select>
		</div>
	    <div id="chartD3" align="center" style="width:90%;"></div>


	<script type="text/javascript">

    function returnValues(item, template){
    	//console.log('template');	
    	//console.log(template);	
    	if (item=='width') {
    		return template.width;	
    	} else if (item =='height') {	
    		return template.height;
    	} 
    }
    var chartWidth = returnValues('width',<%=chartTemplate%>);
    var chartHeight = returnValues('height',<%=chartTemplate%>);
    var xscale = d3.scale.linear().range([0, chartWidth]);
    var yscale = d3.scale.linear().range([0, chartHeight]);
    var color = d3.scale.category10();
    var headerHeight = 20;
    var headerColor = "#555555";
    var transitionDuration = 500;
    var root;
    var node;

    var treemap = d3.layout.treemap()
        .round(false)
        .size([chartWidth, chartHeight])
        .sticky(true)
        .value(function(d) {
            return d.size;
        });

    var chart = d3.select("#chartD3")
        .append("svg:svg")
        .attr("width", chartWidth)
        .attr("height", chartHeight)
        .append("svg:g");


    function prepareData(data,template){
    	//retrieve variables from the template 
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
     	
     	var root = {nome:"root",children:[]};
     	var map = new Object;
     	for (index = 0; index < rows.length; ++index) {

     		var nodo = { name: rows[index][decodificaLabel], key: rows[index][decodificaKey], parentKey:rows[index][decodificaParentKey], size:(decodificaSerie)?rows[index][decodificaSerie]:1};
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

	var fields,rows;

	d3.json("<%=urlProva %>", function(data) {
    
     node=root=prepareData(data,<%=chartTemplate%>);
        var nodes = treemap.nodes(root);

        var children = nodes.filter(function(d) {
            return !d.children;
        });
        var parents = nodes.filter(function(d) {
            return d.children;
        });

        // create parent cells
        var parentCells = chart.selectAll("g.cell.parent")
            .data(parents, function(d) {
                return "p-" + d.name;
            });
        var parentEnterTransition = parentCells.enter()
            .append("g")
            .attr("class", "cell parent")
            .on("click", function(d) {
                zoom(d);
            })
            .append("svg")
            .attr("class", "clip")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight);
        parentEnterTransition.append("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .style("fill", headerColor);
        parentEnterTransition.append('text')
            .attr("class", "label")
            .attr("transform", "translate(3, 13)")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .text(function(d) {
                return d.name;
            });
        // update transition
        var parentUpdateTransition = parentCells.transition().duration(transitionDuration);
        parentUpdateTransition.select(".cell")
            .attr("transform", function(d) {
                return "translate(" + d.dx + "," + d.y + ")";
            });
        parentUpdateTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .style("fill", headerColor);
        parentUpdateTransition.select(".label")
            .attr("transform", "translate(3, 13)")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .text(function(d) {
                return d.name;
            });
        // remove transition
        parentCells.exit()
            .remove();

        // create children cells
        var childrenCells = chart.selectAll("g.cell.child")
            .data(children, function(d) {
                return "c-" + d.name;
            });
        // enter transition
        var childEnterTransition = childrenCells.enter()
            .append("g")
            .attr("class", "cell child")
            .on("click", function(d) {
                zoom(node === d.parent ? root : d.parent);
            })
            .append("svg")
            .attr("class", "clip");
        childEnterTransition.append("rect")
            .classed("background", true)
            .style("fill", function(d) {
                return color(d.parent.name);
            });
        childEnterTransition.append('text')
            .attr("class", "label")
            .attr('x', function(d) {
                return d.dx / 2;
            })
            .attr('y', function(d) {
                return d.dy / 2;
            })
            .attr("dy", ".35em")
            .attr("text-anchor", "middle")
            .style("display", "none")
            .text(function(d) {
                return d.name;
            });
        // update transition
        var childUpdateTransition = childrenCells.transition().duration(transitionDuration);
        childUpdateTransition.select(".cell")
            .attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });
        childUpdateTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", function(d) {
                return d.dy;
            })
            .style("fill", function(d) {
                return color(d.parent.name);
            });
        childUpdateTransition.select(".label")
            .attr('x', function(d) {
                return d.dx / 2;
            })
            .attr('y', function(d) {
                return d.dy / 2;
            })
            .attr("dy", ".35em")
            .attr("text-anchor", "middle")
            .style("display", "none")
            .text(function(d) {
                return d.name;
            });

        // exit transition
        childrenCells.exit()
            .remove();

        d3.select("select").on("change", function() {
            console.log("select zoom(node)");
            treemap.value(this.value == "size" ? size : count)
                .nodes(root);
            zoom(node);
        });

        zoom(node);
   
	});	



    function size(d) {
        return d.size;
    }


    function count(d) {
        return 1;
    }


    //and another one
    function textHeight(d) {
        var ky = chartHeight / d.dy;
        yscale.domain([d.y, d.y + d.dy]);
        return (ky * d.dy) / headerHeight;
    }

    function getRGBComponents(color) {
        var r = color.substring(1, 3);
        var g = color.substring(3, 5);
        var b = color.substring(5, 7);
        return {
            R: parseInt(r, 16),
            G: parseInt(g, 16),
            B: parseInt(b, 16)
        };
    }


    function idealTextColor(bgColor) {
        var nThreshold = 105;
        var components = getRGBComponents(bgColor);
        var bgDelta = (components.R * 0.299) + (components.G * 0.587) + (components.B * 0.114);
        return ((255 - bgDelta) < nThreshold) ? "#000000" : "#ffffff";
    }


    function zoom(d) {
        this.treemap
            .padding([headerHeight / (chartHeight / d.dy), 0, 0, 0])
            .nodes(d);

        // moving the next two lines above treemap layout messes up padding of zoom result
        var kx = chartWidth / d.dx;
        var ky = chartHeight / d.dy;
        var level = d;

        xscale.domain([d.x, d.x + d.dx]);
        yscale.domain([d.y, d.y + d.dy]);

        if (node != level) {
            chart.selectAll(".cell.child .label")
                .style("display", "none");
        }

        var zoomTransition = chart.selectAll("g.cell").transition().duration(transitionDuration)
            .attr("transform", function(d) {
                return "translate(" + xscale(d.x) + "," + yscale(d.y) + ")";
            })
            .each("start", function() {
                d3.select(this).select("label")
                    .style("display", "none");
            })
            .each("end", function(d, i) {
                if (!i && (level !== self.root)) {
                    chart.selectAll(".cell.child")
                        .filter(function(d) {
                            return d.parent === self.node; // only get the children for selected group
                        })
                        .select(".label")
                        .style("display", "")
                        .style("fill", function(d) {
                            return idealTextColor(color(d.parent.name));
                        });
                }
            });

        zoomTransition.select(".clip")
            .attr("width", function(d) {
                return Math.max(0.01, (kx * d.dx));
            })
            .attr("height", function(d) {
                return d.children ? headerHeight : Math.max(0.01, (ky * d.dy));
            });

        zoomTransition.select(".label")
            .attr("width", function(d) {
                return Math.max(0.01, (kx * d.dx));
            })
            .attr("height", function(d) {
                return d.children ? headerHeight : Math.max(0.01, (ky * d.dy));
            })
            .text(function(d) {
                return d.name;
            });

        zoomTransition.select(".child .label")
            .attr("x", function(d) {
                return kx * d.dx / 2;
            })
            .attr("y", function(d) {
                return ky * d.dy / 2;
            });

        zoomTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, (kx * d.dx));
            })
            .attr("height", function(d) {
                return d.children ? headerHeight : Math.max(0.01, (ky * d.dy));
            })
            .style("fill", function(d) {
                return d.children ? headerColor : color(d.parent.name);
            });

        node = d;

        if (d3.event) {
            d3.event.stopPropagation();
        }
    }
</script>
 
	</body>

</html>