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

		Ext.Ajax.request({
			url: Sbi.service.Service.callService("model", "mdx", [mdx]),
			method: "GET",
			success : function(response, options) {
				if(response !== undefined && response.statusText !== undefined && response.responseText!=null && response.responseText!=undefined) {
					this.eventManager.updateAfterMDXExecution(response.responseText);
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			},
			scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
	}
	, drillDown: function(axis, position,  member){

		Ext.Ajax.request({
			url: Sbi.service.Service.callService("member","drilldown", [axis, position, member]),
			method: "GET",
			success : function(response, options) {
				if(response !== undefined && response.statusText !== undefined && response.responseText!=null && response.responseText!=undefined) {
					this.eventManager.updateAfterMDXExecution(response.responseText);
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			},
			scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
	}
});





