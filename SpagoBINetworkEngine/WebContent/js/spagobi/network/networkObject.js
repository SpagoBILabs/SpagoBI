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


Ext.ns("Sbi.network");

Sbi.network.networkObject = function(config) {	
	
	Ext.apply(this,config);
	
    // initialization options
    this.options = {
        // where you have the Cytoscape Web SWF
        swfPath: "../swf/CytoscapeWeb",
        // where you have the Flash installer SWF
        flashInstallerPath: "../swf/playerProductInstall"
    };
	Sbi.network.networkObject.superclass.constructor.call(this, config);
};

Ext.extend(Sbi.network.networkObject , Ext.Panel, {
	
	div_id: "cytoscapeweb", // id of Cytoscape Web container div
	option: null,
	networkEscaped: null,
	networkLink: null,
	networkType: null,
	networkOptions: null
	
	, drawNetwork: function () {
        // init and draw
        var vis = new org.cytoscapeweb.Visualization(this.div_id, options);

        if(this.networkType == ("json")){
      	   var network = {
             		dataSchema: {
             			nodes: networkEscaped.nodeMetadata
             		}
      	   };
      	  	network.data = {};
      	  	network.data.edges= networkEscaped.edges;
      	  	network.data.nodes= networkEscaped.nodes;
      	 	vis.draw(Ext.apply({ network: network},this.networkOptions ||{}));

         }else{
        	 vis.draw({ network: networkEscaped});
         }	
	}

	,addCrossNavigation:function(){
        vis.addListener("click", "edges", function(evt) {
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

            alert("Edge " +parametersString + " was clicked");
        });
	}
  

	
});