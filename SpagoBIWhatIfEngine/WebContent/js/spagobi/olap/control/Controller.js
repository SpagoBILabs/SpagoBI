/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * This component manage all the events of the table.
 * The standard use case is: the view send an event at the event manager,
 * the manager captures it, decores it and calls a method of the controller.
 * The controller execute the request and return the result at the event manager. 
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.control.Controller', {
	extend: 'Object',
	
	/**
     * @property {Sbi.olap.control.EventManager} executionPanel
     *  Panel that contains the pivot and the chart
     */
	eventManager: null,	

	
	constructor : function(config) {
		this.eventManager = config.eventManager;
	},
	
	executeMdx: function(mdx){

		
		var service = Ext.create("Sbi.service.RestService",{
			url: "model",
			subPath: "mdx",
			method: "PUT",
			pathParams: [mdx]
		});
		
		service.callService(this);
	}
	, drillDown: function(axis, position,  member){

		var service = Ext.create("Sbi.service.RestService",{
			url: "member",
			subPath: "drilldown",
			pathParams: [axis, position, member]
		});
		
		service.callService(this);
		
	}
	, drillUp: function(axis, position,  member){

		var service = Ext.create("Sbi.service.RestService",{
			url: "member",
			subPath: "drillup",
			pathParams: [axis, position, member]
		});
		
		service.callService(this);
		
	}
	,swapAxis: function(){

		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			subPath: "swap",
			method: "PUT"
		});
		
		service.callService(this);

	}
	,addSlicer: function(hierarchy, member, multiSelection){

		var service = Ext.create("Sbi.service.RestService",{
			url: "hierarchy",
			subPath: "slice",
			pathParams: [hierarchy, member, multiSelection]
		});
		
		service.callService(this);
	}
	,
	swapHierarchies: function(hierarchy1, hierarchy2, axis){
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'PUT',
			pathParams: [axis,"swaphierarchies", hierarchy1, hierarchy2]
		});
		
		service.callService(this);
	}
	,
	moveHierarchy: function(hierarchy1, fromAxis, toAxis){
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'PUT',
			pathParams: [fromAxis,"moveHierarchy", hierarchy1, toAxis]
		});
		
		service.callService(this);
	}
	, setModelConfig: function(config){

		var service = Ext.create("Sbi.service.RestService",{
			url: "modelconfig",
			method: 'POST',
			jsonData: config
			
		});
		
		service.callService(this);
		
	}
});





