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

Ext.define('Sbi.network.networkObject', {
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
    		layout: 'fit'
    	};

    	var c = Ext.apply(defaultSettings, config || {});
    	
    	Ext.apply(this, c);
    	
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
	
	, drawNetwork: function () {
        // init and draw
        this.networkSwf = new org.cytoscapeweb.Visualization(this.div_id, this.options);

        if(this.networkType == ("json")){
      	   var network = {
             		dataSchema: {
             			nodes: networkEscaped.nodeMetadata
             		}
      	   };
      	  	network.data = {};
      	  	network.data.edges= networkEscaped.edges;
      	  	network.data.nodes= networkEscaped.nodes;
      	  this.networkSwf.draw(Ext.apply({ network: network},this.networkOptions ||{}));

         }else{
        	 this.networkSwf.draw({ network: networkEscaped});
         }	
        this.addCrossNavigation();
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
	  
	            eval("javascript:parent.execCrossNavigation(this.name,  '" +networkLink.document+"','"+parametersString + "','','','"+networkLink.navigationMode+"');");
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
	  
	            eval("javascript:parent.execCrossNavigation(this.name,  '" +networkLink.document+"','"+parametersString + "','','','"+networkLink.navigationMode+"');");
	        });
		}
	}
  

	
	, exportNetwork : function(mimeType) {
		this.networkSwf.exportNetwork(mimeType, this.services['exportNetwork']+'&type='+mimeType);
	}
	
});