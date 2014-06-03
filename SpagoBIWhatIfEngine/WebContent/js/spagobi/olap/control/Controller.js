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
			method: "POST",
			pathParams: [mdx]
		});
		
		service.callService(this);
	}
	,drillDown: function(axis, position,  member){

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
			method: "POST"
		});
		
		service.callService(this);

	}
	,addSlicer: function(hierarchy, member, multiSelection){

		var service = Ext.create("Sbi.service.RestService",{
			url: "hierarchy",
			pathParams: [hierarchy, "slice", member, multiSelection]
		});
		
		service.callService(this);
	}
	,
	moveHierarchy: function(uniqueName, axis, newPosition, direction){
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'POST',
			pathParams: [axis,"moveHierarchy", uniqueName, newPosition, direction]
		});
		
		service.callService(this);
	}
	,
	moveDimensionToOtherAxis: function(hierarchy1, fromAxis, toAxis){
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'POST',
			pathParams: [fromAxis,"moveDimensionToOtherAxis", hierarchy1, toAxis]
		});
		
		service.callService(this);
	}
	,
	updateHierarchyOnDimension: function(axis, newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition){
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'POST',
			pathParams: [axis,"updateHierarchyOnDimension",newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition]
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
	, placeMembersOnAxis: function(axis, members){

		var service = Ext.create("Sbi.service.RestService",{
			url: "axis",
			method: 'POST',
			pathParams: [axis,"placeMembersOnAxis"],
			jsonData: members
			
		});
		
		service.callService(this);
		
	}
	, setValue: function(ordinal, expression) {

		var service = Ext.create("Sbi.service.RestService", {
			url: "model",
			method: 'POST',
			pathParams: ["setValue", ordinal],
			jsonData: { "expression" : expression }
		});
		
		service.callService(this);
	}
	
	,
	undo: function() {

		var service = Ext.create("Sbi.service.RestService", {
			url: "model",
			method: 'POST',
			pathParams: ["undo"]
		});
		
		service.callService(this);
	}
	
	, cleanCache: function() {

		var service = Ext.create("Sbi.service.RestService", {
			url: "cache",
			method: 'DELETE'
		});
		
		service.callService(this);
	}
	
	,persistTransformations: function() {

		var service = Ext.create("Sbi.service.RestService", {
			url: "model",
			method: 'POST',
			pathParams: ["persistTransformations"],
			timeout: Sbi.settings.olap.whatif.timeout.persistTransformations
		});
		
		service.callService(this);
	}
	,persistNewVersionTransformations: function() {

		var service = Ext.create("Sbi.service.RestService", {
			url: "version",
			method: 'POST',
			pathParams: ["increase"],
			timeout: Sbi.settings.olap.whatif.timeout.persistNewVersionTransformations
		});
		
		service.callService(this);
	}
	,lockModel: function() {

		var olapToolbar = this.eventManager.olapPanel.executionPanel.olapToolbar;
		
		var externalUrlPath = Sbi.config.externalUrl;
		var artifactVersionId = olapToolbar.modelConfig.artifactVersionId;
		
		
		var service = Ext.create("Sbi.service.RestService", {
			url: "locker",
			method: 'POST',
			pathParams: [artifactVersionId, "lock"],
			externalUrl: externalUrlPath
		});
				
		
		service.callService(olapToolbar,
				function(result){
					olapToolbar.renderLockModel(result);
					Sbi.olap.eventManager.hideLoadingMask();
				}
				);
	}
	,unlockModel: function() {
	
		var olapToolbar = this.eventManager.olapPanel.executionPanel.olapToolbar;
		
		var externalUrlPath = Sbi.config.externalUrl;
		var artifactVersionId = olapToolbar.modelConfig.artifactVersionId;
		
		var service = Ext.create("Sbi.service.RestService", {
			url: "locker",
			method: 'POST',
			pathParams: [artifactVersionId, "unlock"],
			externalUrl: externalUrlPath
		});
		
		

		
		service.callService(olapToolbar,
				function(result){
					olapToolbar.renderUnlockModel(result);
					Sbi.olap.eventManager.hideLoadingMask();
				}
				);
	}
	
	
	

});





