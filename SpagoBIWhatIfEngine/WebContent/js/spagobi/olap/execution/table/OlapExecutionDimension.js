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


Ext.define('Sbi.olap.execution.table.OlapExecutionDimension', {
	extend: 'Ext.panel.Panel',

	config:{		
		/**
		 * @cfg {Sbi.olap.DimensionModel} dimension
		 * The dimension represented by the column
		 */
		dimension: null,
		/**
		 * @cfg {Sbi.olap.execution.table.OlapExecutionPivot} pivotContainer
		 * The container of the columns
		 */
		pivotContainer: null,
		/**
		 * @cfg {Sbi.olap.execution.table.OlapExecutionRow/Column/Filter} containerPanel
		 * The container of the dimension: filters, columns, rows
		 */
		containerPanel: null

	},


	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionDimension) {
			Ext.apply(this, Sbi.settings.olap.execution.OlapExecutionDimension);
		}
		
		this.callParent(arguments);
		this.addDragAndDrop();
	},
	

	initComponent: function() {
		
		var items = this.buildItems();
		
		this.on('dimensionClick', function(dimension){alert("cklik");},this);
		
		Ext.apply(this, {
			frame: true,
			items: items}
		);
		this.callParent();
	},
	


	/**
	 * Implements the drag and drop of the dimension between filters, rows and columns
	 */
	addDragAndDrop: function(){
		this.on("render",function(){
			var dd = Ext.create('Ext.dd.DDProxy', this.getEl(), 'dimensionDDGroup', {
				isTarget  : false
			});

			var thisPanel = this;

			Ext.apply(dd, {


				onDragDrop : function(evtObj, targetElId) {

					if(thisPanel.containerPanel.getId()!= targetElId && (thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId || thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId || thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId)){

						//thisPanel.containerPanel.removeDimension(thisPanel);

						if(thisPanel.pivotContainer.olapExecutionFilters.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionFilters.moveDimensionToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionFilters;
						}
						if(thisPanel.pivotContainer.olapExecutionRows.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionRows.moveDimensionToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionRows;
						}
						if(thisPanel.pivotContainer.olapExecutionColumns.getId()== targetElId){
							thisPanel.pivotContainer.olapExecutionColumns.moveDimensionToOtherAxis(thisPanel);
							//	thisPanel.containerPanel= thisPanel.pivotContainer.olapExecutionColumns;
						}
					}

				},
				endDrag : function() {
					// Empty. Just to prevent the user to drag the elements outside the dd area
				}
			});


			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionFilters.getId(), 'dimensionDDGroup');
			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionRows.getId(), 'dimensionDDGroup');
			Ext.create('Ext.dd.DDTarget', thisPanel.pivotContainer.olapExecutionColumns.getId(), 'dimensionDDGroup');
		},this);
	},
	
	/**
	 * Returns the name of the dimension
	 * @returns
	 */
	getDimensionName: function(){
		var dimensionName = this.dimension.raw.name;
		var hierarchies =  this.dimension.get("hierarchies");
		if(hierarchies.length>1){
			var selectedHierarchyPosition = this.dimension.get("selectedHierarchyPosition");
			var hierarchy = hierarchies[selectedHierarchyPosition];
			var selectedHierarchyName = hierarchy.name;
			dimensionName = dimensionName+"(<i>"+selectedHierarchyName+"</i>)";
		}
		
		return  dimensionName;
	}


});





