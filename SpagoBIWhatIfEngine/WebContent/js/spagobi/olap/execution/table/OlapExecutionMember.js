/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The super class of the rows and columns container
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionMember', {
	extend: 'Ext.panel.Panel',
	
	config:{		
		/**
	     * @cfg {Sbi.olap.MemberModel} member
	     * The member represented by the column
	     */
		member: null,
		/**
	     * @cfg {Sbi.olap.execution.table.OlapExecutionPivot} pivotContainer
	     * The container of the columns
	     */
		pivotContainer: null,
		/**
	     * @cfg {Sbi.olap.execution.table.OlapExecutionRow/Column/Filter} containerPanel
	     * The container of the member: filters, columns, rows
	     */
		containerPanel: null,
		frame: true

    },
	

	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionMember) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionMember);
		}
		this.callParent(arguments);
		this.on("render",function(){
		var dd = Ext.create('Ext.dd.DDProxy', this.getEl(), 'memberDDGroup', {
            isTarget  : false
        });
		
		var thisPanel = this;
		
		Ext.apply(dd, {


		    onDragDrop : function(evtObj, targetElId) {
		    	
		    	if(thisPanel.containerPanel.getId()!= targetElId && (thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId || thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId || thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId)){
		    		
		    		thisPanel.containerPanel.removeMember(thisPanel);
		    		
			        if(thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId){
			        	thisPanel.pivotContainer.olapExecutionFilters.addMember(thisPanel);
			        //	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionFilters;
			        }
			        if(thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId){
			        	thisPanel.pivotContainer.olapExecutionRows.addMember(thisPanel);
			        //	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionRows;
			        }
			        if(thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId){
			        	thisPanel.pivotContainer.olapExecutionColumns.addMember(thisPanel);
			        //	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionColumns;
			        }
		        }

		    },
		    endDrag : function() {
		        // Empty. Just to prevent the user to drag the elements outside the dd area
		    }
		});
		
		
		Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionFilters.getId(), 'memberDDGroup');
		Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionRows.getId(), 'memberDDGroup');
		Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionColumns.getId(), 'memberDDGroup');
		},this);
		
	},
	
	
	initComponent: function() {
		
		Ext.apply(this, {html: this.member.get("name")});
		this.callParent();
	},
	
	
    
	
});





