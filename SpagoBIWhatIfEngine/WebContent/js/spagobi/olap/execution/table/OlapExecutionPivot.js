/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container for the pivot table. Contains:
 * <ul>
 * <li>Filters definition</li>
 * <li>Columns definition</li>
 * <li>Rows definition</li>
 * <li>Table</li>
 * </ul>
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionPivot', {
	extend: 'Ext.panel.Panel',
	
	config:{},

	/**
     * @property {Sbi.olap.execution.table.OlapExecutionTable} olapExecutionTable
     *  The table with the data
     */
	olapExecutionTable: null,
		
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionPivot) {
			this.initConfig(Sbi.settings.olap.execution.table.OlapExecutionPivot);
		}
		this.callParent(arguments);
	},
	
	initComponent: function() {
		
		this.olapExecutionTable   = Ext.create('Sbi.olap.execution.table.OlapExecutionTable',  {}); 
		
		Ext.apply(this, {
			items: [this.olapExecutionTable]
			
		});
		
		this.callParent();
	}
});





