/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * container of the columns definition of the pivot table
 *
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */




Ext.define('Sbi.olap.execution.table.OlapExecutionDimensions', {
	extend: 'Ext.panel.Panel',
			
	config:{
		/**
	     * @cfg {Ext.data.Store} store
	     * The store with the Sbi.olap.execution.table.OlapExecutionDimension
	     */
		store: null,
		/**
	     * @cfg {Sbi.olap.execution.table.OlapExecutionPivot} pivotContainer
	     * The container of the columns
	     */
		pivotContainer: null,
		/**
	     * @cfg {String} dimensionClassName
	     * The name of the children classes
	     */
		dimensionClassName: null,
		/**
	     * @cfg {Number} axisPosition
	     * The position of the axis
	     */
		axisOrdinalPosition: -1
//		,style: {
//			backgroundColor: "transparent",
//			border: "none"
//		},
//		bodyStyle: {
//			backgroundColor: "transparent"
//		},
	    //cls: "empty-member"
    },
	
    
	constructor : function(config) {
		this.initConfig(config);
		this.store = Ext.create('Ext.data.Store', {
		    model: 'Sbi.olap.DimensionModel'
		});
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionDimensions) {
			Ext.apply(this, Sbi.settings.olap.execution.OlapExecutionDimensions);
		}
		this.callParent(arguments);
	},
    
	initComponent: function() {

		if(this.store && this.store.getCount()>0){

			var items = this.getRefreshedItems();
			Ext.apply(this, {items: items});
//			this.removeCls("empty-member");
		}
		Ext.apply(this, {frame: true});
		this.callParent();
	},
	
    /**
     * Adds the Dimension from in Dimension container
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the Dimension to add
     */
	addDimension: function(dimension){
//		if(this.store.getCount()==0){
//			this.removeCls("empty-member");
//		}
		this.store.add(dimension.dimension);
		this.refreshItems();
	},
	
    /**
     * Removes the dimension from the dimension container
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the dimension to remove
     */
	removeDimension: function(dimension){
		this.store.remove(dimension.dimension);
		this.refreshItems();
//		if(this.store.getCount()==0){
//			this.addCls("empty-member");
//		}
	},
	
    /**
     * Adds the Dimension from in Dimension container
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the Dimension to add
     */
	moveDimensionToOtherAxis: function(dimension){
		
		Sbi.olap.eventManager.moveDimension(dimension.dimension.get("uniqueName"), dimension.dimension.get("axis"), this.axisOrdinalPosition);
	},
	
	
	/**
     * Moves up the dimension
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the dimension to move
	 */
	moveUpDimension: function(dimension){
		this.move(dimension, -1);
	},
	
	
	/**
     * Moves down the member
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the dimension to move
	 */
	moveDownDimension: function(dimension){
		this.move(dimension, 1);
	},

	/**
     * Moves the model of pos positions
     * @param {Sbi.olap.execution.table.OlapExecutionDimension} dimension the dimension to remove
	 * @param pos the positions 
	 */
	move: function(dimension, pos){
		var index = this.store.indexOf(dimension.dimension);
		
		if((pos+index)>=0 && (pos+index)<this.store.getCount( )){
			Sbi.olap.eventManager.swapDimensions(index, index+pos, dimension.dimension.get("axis"));
//			this.store.remove(dimension.dimension);
//			this.store.insert((index+pos),dimension.dimension);
//			this.refreshItems();
		}
	},
	
    /**
     * Refresh content
     */
	refreshItems: function(){
		this.removeAll(true);
		
		if(this.store){
			var items = this.getRefreshedItems();
			for(var i=0; i<items.length; i++) {
				this.add(items[i]);
			}
		}
	},
	
    /**
     * Get the refreshed items: builds all the dimensions starting from the store
     */
	getRefreshedItems: function(){
		var items = new Array();
		
		if(this.store && this.store.getCount()>0){
			var dimensionsCount = this.store.getCount( );
			for(var i=0; i<dimensionsCount; i++) {
				var dimension = Ext.create(this.dimensionClassName,{dimension: this.store.getAt(i), pivotContainer: this.pivotContainer, containerPanel: this, firstDimension: (i==0), lastDimension: (i==dimensionsCount-1) });
				dimension.on("moveUp",this.moveUpDimension,this);
				dimension.on("moveDown",this.moveDownDimension,this);
				items.push(dimension);
			}
		}
		
		return items;
	},
	
	/**
	 * Updates the visualization after the execution of a a mdx query
	 * @param pivotModel {Array} the list of dimensions to add
	 * @param axisOrdinalPosition {Number} the ordinal position of the axis
	 */
	updateAfterMDXExecution: function(dimensions, axisOrdinalPosition){
		this.axisOrdinalPosition = axisOrdinalPosition;
		this.store.removeAll();
		if(dimensions){
			for(var i=0; i<dimensions.length; i++){
				this.store.add(Ext.create("Sbi.olap.DimensionModel", dimensions[i]));
			}
		}
		this.refreshItems();
	}
	
});





