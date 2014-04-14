/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * This component manage all the events. The standard use case is: the view notify an event to the event manager,
 * the manager decorates it and calls a method of the controller.
 * The controller execute the request and return the result at the event manager that manage the response.<br>
 * It's a Singleton and all classes can notify an event directly to the component
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.control.EventManager', {
	extend: 'Ext.util.Observable',

	/**
     * @property {Sbi.olap.OlapPanel} olapPanel
     *  The Olap Panel; the observable
     */
	olapPanel: null,
	
	/**
     * @property {Sbi.olap.execution.OlapExecutionPanel} executionPanel
     *  Panel that contains the pivot and the chart
     */
	olapController: null,	

	
	constructor : function(config) {
		this.olapPanel = config.olapPanel;
		this.olapController = Ext.create('Sbi.olap.control.Controller', {eventManager: this});
		this.callParent(arguments);
		this.addEvents(
				/**
				 * [LIST OF EVENTS]
				 */
		        /**
		         * @event execute
		         * This event is thrown when a service is called for execution
		         */
		        'executeService',
		        /**
		         * @event executed
		         * This event is thrown when a service has finished execution
				 * @param {Object} response
		         */
		        'serviceExecuted'
				);
		this.on('executeService', this.executeService, this);
		this.on('serviceExecuted', this.serviceExecuted, this);
		this.loadingMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
	},
	

    /**
     * Notifies the manager that the mdx query is changed
     * @param {String} mdx the mdx query. If null the server will load the initial mdx query
     */
	notifyMdxChanged: function(mdx){
		this.olapController.executeMdx(mdx);
	},

    /**
     * Updates the view after the execution of the mdx query
     * @param {String} pivotHtml the html representation of the pivot table
     */
	updateAfterMDXExecution: function(pivotHtml){
		var tableJson = Ext.decode(pivotHtml);
		var pivot = Ext.create('Sbi.olap.PivotModel', tableJson);
		this.olapPanel.updateAfterMDXExecution(pivot);
		this.loadingMask.hide();
	},
    /**
     * Updates the view after drill down operation
     * @param {int} axis position of the row
     * @param {int} member position of the member
     * @param {int} position in the Position array
     */
	drillDown: function(axis, position,  member){
		this.olapController.drillDown(axis, member, position);
	},
    /**
     * Updates the view after drill up operation
     * @param {int} axis position of the row
     * @param {int} member position of the member
     * @param {int} position in the Position array
     */
	drillUp: function(axis, position,  member){
		this.olapController.drillUp(axis, member, position);
	},
    /**
     * Swaps the axis
     */
	swapAxis: function(){
		this.olapController.swapAxis();
	},
	
    /**
     * Adds a slicer for the hierarchy
     * @param {Sbi.olap.HierarchyModel} hierarchy to slice
     * @param {Sbi.olap.MemberModel} member the slicer value
     */
	addSlicer: function(hierarchy, member, multiSelection){
		this.olapController.addSlicer(hierarchy.raw.uniqueName, member.uniqueName, multiSelection);
	},
	
    /**
     * Swap 2 hierarchies in an axis
     * @param {Sbi.olap.DimensionModel} hierarchy1 position of the first hierarchy to move
     * @param {Number} newPosition new position of the dimension
     * @param {Number} axis
     */
	moveDimension: function(dimension, newPosition, direction){
		this.olapController.moveHierarchy(dimension.get("selectedHierarchyUniqueName"), dimension.get("axis"), newPosition, direction);
	},
	
    /**
     * Move the dimension from an axis to another
     * @param {Number} dimension1 position of the hierarchy to move
     * @param {Number} fromAxis axis from witch remove the hierarchy
     * @param {Number} toAxis axis to witch add the hierarchy
     */
	moveDimensionToOtherAxis: function(hierarchy1, fromAxis, toAxis){
		this.olapController.moveDimensionToOtherAxis(hierarchy1, fromAxis, toAxis);
	},
    /**
     * Updates the model configuration based on the toolbar settings
     * @param {String} config toolbar configuration for the model
     */
	setModelConfig: function(config){
		this.olapController.setModelConfig(config);
	},

	/**
	 * Place the members on the axis
	 * @param {Sbi.olap:DimensionModel} dimension the dimension
	 * @param {Array} The list of members to place in the axis
	 */
	placeMembersOnAxis: function(dimension, members){
		this.olapController.placeMembersOnAxis(dimension.get("axis"), members);
	},

	/**
	 * Place the members on the axis
	 * @param {Sbi.olap:DimensionModel} dimension the dimension
	 * @param {Array} The list of members to place in the axis
	 */
	showHelp: function(title, content, winConf){
		if(!title){
			title = LN('sbi.olap.help.title');
		}
		var win = Ext.create("Sbi.widgets.Help",Ext.apply({title: title, content: content}, winConf||{}));
		win.show();
	},
	
	updateHierarchyOnDimension: function(axis, newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition){
		this.olapController.updateHierarchyOnDimension(axis, newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition);
	}
	
	
	

	,executeService: function(){
		this.loadingMask.show();
	}
	, serviceExecuted: function (response){
		this.updateAfterMDXExecution(response.responseText);
		
	}
	
	
	
});





