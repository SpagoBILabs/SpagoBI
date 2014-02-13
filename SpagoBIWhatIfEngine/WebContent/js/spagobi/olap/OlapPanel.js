/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container of all the UI of the olap engine.<br>
 * It contains:
 * <ul>
 *		<li>View definition tools</li>
 *		<li>Table/Chart</li>
 *		<li>Options</li>
 *	</ul>
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
  
Ext.define('Sbi.olap.OlapPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'border'
    },
	
	config:{

	},
	
	/**
     * @property {Sbi.olap.tools.OlapViewDefinitionTools} definitionTools
     *  Tools for the view definition
     */
	definitionTools: null,
	
	/**
     * @property {Sbi.olap.execution.OlapExecutionPanel} executionPanel
     *  Panel that contains the pivot and the chart
     */
	executionPanel: null,

	/**
     * @property {Sbi.olap.options.OlapOptions} optionsPanel
     *  Panel that contains the options of the chart
     */
	optionsPanel: null,

	constructor : function(config) {
		this.initConfig(config||{});
//		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.OlapPanel) {
//			this.initConfig(Sbi.settings.olap.OlapPanel);
//		}
		this.callParent(arguments);
	},

	initComponent: function() {

		this.definitionTools = Ext.create('Sbi.olap.tools.OlapViewDefinitionTools', {region:"west",width: '15%'});
		this.executionPanel = Ext.create('Sbi.olap.execution.OlapExecutionPanel', {region:"center",width: '45%'});
		this.optionsPanel = Ext.create('Sbi.olap.options.OlapOptions', {region:"east",width: '10%'});

		Ext.apply(this, {
			items: [this.definitionTools, this.executionPanel, this.optionsPanel]
		});
		this.callParent();
	}
});