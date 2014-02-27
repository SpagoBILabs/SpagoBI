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
		this.olapPanel.executionPanel.updateAfterMDXExecution(pivotHtml);
	},
    /**
     * Updates the view after drill down operation
     * @param {int} axis position of the row
     * @param {int} member position of the member
     * @param {int} position in the Position array
     */
	drillDown: function(axis, position,  member){
		this.olapController.drillDown(axis, member, position);
	}
});





