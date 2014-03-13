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
	extend: 'Object',

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
		this.olapPanel.executionPanel.updateAfterMDXExecution(pivot);
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
		this.olapController.addSlicer(hierarchy.raw.uniqueName, member.raw.uniqueName, multiSelection);
	},
	
    /**
     * Swap 2 hierarchies in an axis
     * @param {Number} hierarchy1 position of the first hierarchy to move
     * @param {Number} hierarchy2 position of the second hierarchy to move
     * @param {Number} axis
     */
	swapHierarchies: function(hierarchy1, hierarchy2, axis){
		this.olapController.swapHierarchies(hierarchy1, hierarchy2, axis);
	},
	
    /**
     * Move the hierarchy from an axis to another
     * @param {Number} hierarchy1 position of the hierarchy to move
     * @param {Number} fromAxis axis from witch remove the hierarchy
     * @param {Number} toAxis axis to witch add the hierarchy
     */
	moveHierarchy: function(hierarchy1, fromAxis, toAxis){
		this.olapController.moveHierarchy(hierarchy1, fromAxis, toAxis);
	}
    /**
     * Updates the drill down/up mode on the table renderer
     * @param {String} mode that can be 'position', 'member' or 'replace'
     */
	,setDrillMode: function(mode){
		this.olapController.setDrillMode(mode);
	},
});





