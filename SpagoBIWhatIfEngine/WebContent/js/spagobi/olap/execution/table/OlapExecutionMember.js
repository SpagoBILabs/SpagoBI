/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * A member..
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionMember', {
	extend: 'Ext.panel.Panel',
	html:'MM',
	
	config:{
		frame: true,
		border: false,
		height: 20,
		width: 40,
		containerPanel: null,
		filtersPanel: null,
		rowsPanel: null,
		columnsPanel: null,
    },
	

	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionMember) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionMember);
		}
		this.callParent(arguments);
		this.on("render",function(){
		var dd = Ext.create('Ext.dd.DD', this.getEl(), 'memberDDGroup', {
            isTarget  : false
        });
		
		var thisPanel = this;
		
		Ext.apply(dd, {

		    onDragDrop : function(evtObj, targetElId) {
		    	
		    	if(thisPanel.containerPanel.getId()!= targetElId){
		    		
		    		thisPanel.containerPanel.remove(thisPanel,false);
		    		
			        if(thisPanel.filtersPanel.getId()== targetElId){
			        	thisPanel.filtersPanel.add(thisPanel);
			        	thisPanel.containerPanel= thisPanel.filtersPanel;
			        }
			        if(thisPanel.rowsPanel.getId()== targetElId){
			        	thisPanel.rowsPanel.add(thisPanel);
			        	thisPanel.containerPanel= thisPanel.rowsPanel;
			        }
			        if(thisPanel.columnsPanel.getId()== targetElId){
			        	thisPanel.columnsPanel.add(thisPanel);
			        	thisPanel.containerPanel= thisPanel.columnsPanel;
			        }
		        }

		    }
		});
		
		
		Ext.create('Ext.dd.DDTarget', thisPanel.filtersPanel.getId(), 'memberDDGroup');
		Ext.create('Ext.dd.DDTarget', thisPanel.rowsPanel.getId(), 'memberDDGroup');
		Ext.create('Ext.dd.DDTarget', thisPanel.columnsPanel.getId(), 'memberDDGroup');
		},this);
		
	}
	
});





