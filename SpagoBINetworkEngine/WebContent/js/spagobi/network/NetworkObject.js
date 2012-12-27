/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * Object name 
 * 
 * 	Properties networkEscaped, networkLink, networkType, networkOptions must be defined in the custructors object config
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 *  [list]
 * 
 * 
 * Public Events
 * 
 *  [list]
 * 
 * Authors
 * 
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.network.NetworkObject', {
    //alias: 'widget.n',
    extend: 'Ext.panel.Panel',
	div_id: "cytoscapeweb", // id of Cytoscape Web container div
	options: null,
	networkEscaped: null,
	networkLink: null,
	networkType: null,
	networkOptions: null,
	networkSwf: null

	
	, constructor: function(config) {
		
		this.services = new Array();
		var params = {};
		this.services['exportNetwork'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'NETWORK_EXPORT_ACTION'
			, baseParams: params
		});
		
		
    	var defaultSettings = {
    			region:"center",
    		html: '<div id="'+this.div_id+'"> Cytoscape Web will replace the contents of this div with your graph.   </div>',
    		layout: 'fit',
    		border: false
    	};

    	

    	
    	var c = Ext.apply(defaultSettings, config || {});
    	Ext.apply(this, c);
    	this.initOptions();
    	
    	
        // initialization options
        this.options = {
            // where you have the Cytoscape Web SWF
            swfPath: "../swf/CytoscapeWeb",
            // where you have the Flash installer SWF
            flashInstallerPath: "../swf/playerProductInstall"
        };
       
    	this.callParent(arguments);
        this.on('afterrender',this.drawNetwork,this);
    }

	, initOptions: function(){
   	 if(this.networkOptions==null || this.networkOptions==undefined){
		 this.networkOptions={}; 
	 }
	 if(this.networkOptions.visualStyle==null || this.networkOptions.visualStyle==undefined){
		 this.networkOptions.visualStyle={}; 
	 }
	 if(this.networkOptions.visualStyle.edges==null || this.networkOptions.visualStyle.edges==undefined){
		 this.networkOptions.visualStyle.edges={}; 
	 }
	 if(this.networkOptions.visualStyle.nodes==null || this.networkOptions.visualStyle.nodes==undefined){
		 this.networkOptions.visualStyle.nodes={}; 
	 }
	}
	
	, drawNetwork: function () {
	    // init and draw
	    this.networkSwf = new org.cytoscapeweb.Visualization(this.div_id, this.options);
	
	    this.addTooltip();
	    
	    if(this.networkType == ("json")){
	        var network = {
	        		dataSchema: networkEscaped.dataSchema
	        };
	  	  	network.data = {};
	  	  	network.data.edges= networkEscaped.edges;
	  	  	network.data.nodes= networkEscaped.nodes;
	  	    var datasetVisualStyle = this.createMappers(networkEscaped.dataSchema);
	  	    	  	    
	  	    Ext.apply(this.networkOptions.visualStyle.nodes, datasetVisualStyle.nodes);
	  	    Ext.apply(this.networkOptions.visualStyle.edges, datasetVisualStyle.edges);

	  	  	this.networkSwf.draw(Ext.apply({ network: network}, this.networkOptions));
	     }else{
	    	 this.networkSwf.draw({ network: networkEscaped});
	     }	
	    this.addCrossNavigation();
	}
	
	, createMappers:function(dataSchema){
		var mappers = {};
		if(dataSchema!=null && dataSchema!=undefined){
			varDataSchemaNodes = dataSchema.nodes;
			varDataSchemaEdges = dataSchema.edges;
			mappers.nodes = this.createElementMappers(varDataSchemaNodes);
			mappers.edges = this.createElementMappers(varDataSchemaEdges);
		}
		return mappers;
	}

	, createElementMappers:function(dataSchema){
		var mappers = {};
		var propertName;
		if(dataSchema!=null && dataSchema!=undefined){
			for(var property =0; property< dataSchema.length; property++){
				propertName = dataSchema[property].name;
				if(propertName!="id" &&propertName!="label"){
					mappers[propertName] = { passthroughMapper: { attrName: propertName } };
				}
			}
		}
		return mappers;
	}
		
	,addCrossNavigation:function(){
		

		if(networkLink!=null && networkLink!=undefined){
			this.networkSwf.addListener("click", "edges", function(evt) {
	
	            var edge = evt.target;
	            var parametersString="";
	
	            var fixedParameters = networkLink.fixedParameters;
	            if(fixedParameters!=null && fixedParameters!=undefined){
	            	for(var parameter in fixedParameters){
	            		parametersString = parametersString+"&"+parameter+'='+fixedParameters[parameter];
	            	}
	            }
	
	            var dynamicParameters = networkLink.dynamicParameters;
	            if(dynamicParameters!=null && dynamicParameters!=undefined){
	            	var edgeParameters = dynamicParameters.EDGE; 
	                if(edgeParameters!=null && edgeParameters!=undefined){
	                	for(var parameter in edgeParameters){
	                		parametersString = parametersString+"&"+edgeParameters[parameter]+'='+edge.data[parameter];
	                	}
	                }
	            }
	  
	            eval("javascript:parent.execCrossNavigation(this.name,  '" +networkLink.document+"','"+parametersString + "','','','"+networkLink.target+"');");
	        });
			this.networkSwf.addListener("click", "nodes", function(evt) {
				
	            var edge = evt.target;
	            var parametersString="";
	
	            var fixedParameters = networkLink.fixedParameters;
	            if(fixedParameters!=null && fixedParameters!=undefined){
	            	for(var parameter in fixedParameters){
	            		parametersString = parametersString+"&"+parameter+'='+fixedParameters[parameter];
	            	}
	            }
	
	            var dynamicParameters = networkLink.dynamicParameters;
	            if(dynamicParameters!=null && dynamicParameters!=undefined){
	            	var edgeParameters = dynamicParameters.NODE; 
	                if(edgeParameters!=null && edgeParameters!=undefined){
	                	for(var parameter in edgeParameters){
	                		parametersString = parametersString+"&"+edgeParameters[parameter]+'='+edge.data[parameter];
	                	}
	                }
	            }
	  
	            eval("javascript:parent.execCrossNavigation(this.name,  '" +networkLink.document+"','"+parametersString + "','','','"+networkLink.target+"');");
	        });
		}
	}
  
	
	, exportNetwork : function(mimeType) {
		this.networkSwf.exportNetwork(mimeType, this.services['exportNetwork']+'&type='+mimeType);
	}

	
	,addTooltip: function(){
			
		var getTooltipText = function(evt) {
			var tooltipText="";
			var partialText="";
			var propertyName="";
			var propertyText="";
            var target = evt.target;
            var tooltipProperties = networkOptions.visualStyle[target.group].tooltip;
            if(tooltipProperties!=null && tooltipProperties!=undefined){
	            for(var i=0; i<tooltipProperties.length; i++){
	            	propertyName = tooltipProperties[i].property;
	            	propertyText = tooltipProperties[i].text;
	            	if(propertyName!=null && propertyName!=undefined){
	            		partialText = "<strong>"+propertyText+"</strong>: "+target.data[propertyName]+"   ";
	            		tooltipText = tooltipText+partialText;
	            	}
	            	tooltipText = tooltipText+'<br>';
	            }
            }
            return tooltipText;
        };

		
		var tooltip=function(){
			var id = 'tt';
			var top = 3;
			var left = 3;
			var maxw = 300;
			var speed = 10;
			var timer = 20;
			var endalpha = 95;
			var alpha = 0;
			var tt,t,c,b,h;
			var ie = document.all ? true : false;

			return{
				show:function(v,w){
					if(tt == null){
						tt = document.createElement('div');
						tt.setAttribute('id',id);
						t = document.createElement('div');
						t.setAttribute('id',id + 'top');
						c = document.createElement('div');
						c.setAttribute('id',id + 'cont');
						b = document.createElement('div');
						b.setAttribute('id',id + 'bot');
						tt.appendChild(t);
						tt.appendChild(c);
						tt.appendChild(b);
						document.body.appendChild(tt);
						tt.style.opacity = 0;
						tt.style.filter = 'alpha(opacity=0)';
						document.onmousemove = this.pos;
					}
					tt.style.display = 'block';
					c.innerHTML = v;
					tt.style.width = w ? w + 'px' : 'auto';
					if(!w && ie){
						t.style.display = 'none';
						b.style.display = 'none';
						tt.style.width = tt.offsetWidth;
						t.style.display = 'block';
						b.style.display = 'block';
					}
					if(tt.offsetWidth > maxw){tt.style.width = maxw + 'px'}
					h = parseInt(tt.offsetHeight) + top;
					clearInterval(tt.timer);
					tt.timer = setInterval(function(){tooltip.fade(1)},timer);
				},
				pos:function(e){
					var u = ie ? event.clientY + document.documentElement.scrollTop : e.pageY;
					var l = ie ? event.clientX + document.documentElement.scrollLeft : e.pageX;
					tt.style.top = (u - h) + 'px';
					tt.style.left = (l + left) + 'px';
				},
				fade:function(d){
					var a = alpha;
					if((a != endalpha && d == 1) || (a != 0 && d == -1)){
						var i = speed;
						if(endalpha - a < speed && d == 1){
							i = endalpha - a;
						}else if(alpha < speed && d == -1){
							i = a;
						}
						alpha = a + (i * d);
						tt.style.opacity = alpha * .01;
						tt.style.filter = 'alpha(opacity=' + alpha + ')';
					}else{
						clearInterval(tt.timer);
						if(d == -1){tt.style.display = 'none'}
					}
				},
				hide:function(){
					clearInterval(tt.timer);
					tt.timer = setInterval(function(){tooltip.fade(-1)},timer);
				}
			};
		}();


		if(networkOptions.visualStyle.nodes.tooltip!=null && networkOptions.visualStyle.nodes.tooltip!=undefined && networkOptions.visualStyle.nodes.tooltip!=''){
			this.networkSwf.addListener("mouseover", "nodes", function(event) {
				tooltip.show(getTooltipText(event));
			});

			this.networkSwf.addListener("mouseout", "nodes", function(event) {
				tooltip.hide();
			});
		}
		
		if(networkOptions.visualStyle.edges.tooltip!=null && networkOptions.visualStyle.edges.tooltip!=undefined && networkOptions.visualStyle.edges.tooltip!=''){
			this.networkSwf.addListener("mouseover", "edges", function(event) {
				tooltip.show(getTooltipText(event));
			});

			this.networkSwf.addListener("mouseout", "edges", function(event) {
				tooltip.hide();
			});
		}


		
	}
	
});