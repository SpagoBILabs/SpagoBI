/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * The super class of the filters, rows and columns container
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.olap.execution.table.OlapExecutionHierarchy', {
	extend: 'Ext.panel.Panel',

	config:{		
		/**
		 * @cfg {Sbi.olap.HierarchyModel} hierarchy
		 * The hierarchy represented by the column
		 */
		hierarchy: null,
		/**
		 * @cfg {Sbi.olap.execution.table.OlapExecutionPivot} pivotContainer
		 * The container of the columns
		 */
		pivotContainer: null,
		/**
		 * @cfg {Sbi.olap.execution.table.OlapExecutionRow/Column/Filter} containerPanel
		 * The container of the hierarchy: filters, columns, rows
		 */
		containerPanel: null

	},


	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionHierarchy) {
			this.initConfig(Sbi.settings.olap.execution.OlapExecutionHierarchy);
		}
		
		this.callParent(arguments);
		this.addDragAndDrop();
	},

	/**
	 * Implements the drag and drop of the hierarchy between filters, rows and columns
	 */
	addDragAndDrop: function(){
		this.on("render",function(){
			var dd = Ext.create('Ext.dd.DDProxy', this.getEl(), 'hierarchyDDGroup', {
				isTarget  : false
			});

			var thisPanel = this;

			Ext.apply(dd, {


				onDragDrop : function(evtObj, targetElId) {

					if(thisPanel.containerPanel.getId()!= targetElId && (thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId || thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId || thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId)){

						//thisPanel.containerPanel.removeHierarchy(thisPanel);

						if(thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionFilters.moveHierarchyToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionFilters;
						}
						if(thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionRows.moveHierarchyToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionRows;
						}
						if(thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionColumns.moveHierarchyToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionColumns;
						}
					}

				},
				endDrag : function() {
					// Empty. Just to prevent the user to drag the elements outside the dd area
				}
			});


			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionFilters.getId(), 'hierarchyDDGroup');
			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionRows.getId(), 'hierarchyDDGroup');
			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionColumns.getId(), 'hierarchyDDGroup');
		},this);
	},
	
	/**
	 * Returns the name of the memebr
	 * @returns
	 */
	getHierarchyName: function(){
		return  this.hierarchy.raw.name;
	}


});





