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


Ext.define('Sbi.olap.execution.table.OlapExecutionAxisDimension', {
	extend: 'Sbi.olap.execution.table.OlapExecutionDimension',

	config:{		
		/**
		 * @cfg {boolean} firstDimension
		 * Is this Dimension the first one
		 */
		firstDimension: false,

		/**
		 * @cfg {boolean} lastDimension
		 * Is this Dimension the last one
		 */
		lastDimension: false

	},

	/**
	 * @property {Ext.window.Window} propertyWindow
	 * window with the properties of the panel
	 */
	propertyWindow:null,

	/**
	 * @property {Ext.Panel} dimensionPanel
	 * central panel with the name of the Dimension
	 */
	dimensionPanel:null,
	
	subPanelLayout: null,

	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.execution && Sbi.settings.olap.execution.table && Sbi.settings.olap.execution.table.OlapExecutionAxisMember) {
			Ext.apply(this,Sbi.settings.olap.execution.OlapExecutionAxisDimension);
		}
		this.buildDimensionPanel();

		this.callParent(arguments);
		
		this.addEvents(
		        /**
		         * @event dimensionClick
		         * Fired when the user clicks on the panel
				 * @param {Sbi.model.DimensionModel} dimensionClick
		         */
		        'dimensionClick'
				);
		
	},


	initComponent: function() {
		Ext.apply(this, {
			layout: this.subPanelLayout
		}
		);
		this.callParent();
		
	},
	
	buildItems: function(){
		var items = [];

		if(this.dimension.raw.hierarchies && this.dimension.raw.hierarchies.length>1){
			items.push(this.buildMultiHierarchiesButton());
		}
		
		
		items.push( this.dimensionPanel);

		items.push(this.buildFilterButton());


		return items;
	},

	buildMultiHierarchiesButton: function(){
		var config = {
			style: "background-color: transparent !important",
			bodyStyle: "background-color: transparent !important",
			html: " ",
			border: false,
			cls: "multi-hierarchy",
			listeners: {
//				el: {
//					click: {
//						fn: function (event, html, eOpts) {
//	    					   alert("ok");
//						},
//						scope: this
//					}
//				},
				render:{
					fn: this.buildDimensionInfoPanel,
					scope: this
				}
			}
		};
		
		if(this.axisType=="column"){
			Ext.apply(config,{width: 20, height: 15});
		}else{
			Ext.apply(config,{height: 20});
		}
		
		return config;
	},
	
	buildFilterButton: function(){
		var thisPanel = this;
		var config = {
			style: "background-color: transparent !important",
			bodyStyle: "background-color: transparent !important",
			border: true,
			html: " ",
			bodyCls: "filter-"+this.axisType,
			cls: "filter-funnel-image-small",
			listeners: {
				el: {
					click: {
						fn: function (event, html, eOpts) {
	    					   var win =   Ext.create("Sbi.olap.execution.table.OlapExecutionFilterTree",{
	    						   title: LN('sbi.olap.execution.table.filter.dimension.title'),
	    						   dimension: thisPanel.dimension,
	    						   multiSelection: true
	    					   });
	    					   win.show();
	    					   win.on("select", function(member){
	    						   this.setFilterValue(member);
	    					   },this);
						},
						scope: this
					}
				}
			}
		};
		
		if(this.axisType=="column"){
			Ext.apply(config,{width: 20, height: 15});
		}else{
			Ext.apply(config,{height: 20});
		}
		
		return config;
	},
	
	
	/**
	 * Manage the visibility of the members in the axis
	 * @param members
	 */
	setFilterValue: function(members){
		if(members && members.length>0){
			Sbi.olap.eventManager.placeMembersOnAxis(this.dimension, members);
		}
	},
	
	buildDimensionInfoPanel: function(target){
	
		var thisPanel = this;
		
		var selectId = +target.getId()+"select";
		
		var html = "";
		
		if(this.dimension.raw.hierarchies.length>1){
			html = LN("sbi.olap.execution.table.dimension.selected.hierarchy")+"<i>"+(this.dimension.raw.hierarchies[this.dimension.raw.selectedHierarchyPosition]).name+"</i>."+LN("sbi.olap.execution.table.dimension.selected.hierarchy.2")+
				"<table>"+
				//"<tr><td>The selected hierarchy is "+(this.dimension.raw.hierarchies[this.dimension.raw.selectedHierarchyPosition]).name+"</td></tr>"+
				"<tr><td class='multihierarchy-font'>"+
				LN('sbi.olap.execution.table.dimension.available.hierarchies')+
				"</td><td>"+
				"<select id = '"+selectId+"'>";
			
			for(var i=0; i<this.dimension.raw.hierarchies.length; i++){
				html = html+"<option value='"+this.dimension.raw.hierarchies[i].uniqueName+"'>"+this.dimension.raw.hierarchies[i].name+"</option>";
			}
			
			html = html+"</select></td></tr></table>";
		}

		
		var tool = Ext.create('Ext.tip.ToolTip',{        
            title: thisPanel.dimension.raw.name,
            target: target.getEl(),
            anchor: 'left',
            autoHide: false,
            html: html,
            closable: true,
            width: 300,
            padding: 5,
            buttons:[
		             '->',    {
		            	 text: LN('sbi.common.cancel'),
		            	 handler: function(){
		            		 tool.close();
		            	 }
		             },    {
		            	 text: LN('sbi.common.ok'),
		            	 handler: function(){
		            			var newHierarchy =Ext.get(selectId).dom.value;
		            			if(thisPanel.dimension.raw.selectedHierarchyUniqueName!=newHierarchy){
		           				
		            				thisPanel.updateHierarchyOnDimension(thisPanel.dimension.raw.axis, newHierarchy, thisPanel.dimension.raw.uniqueName, thisPanel.dimension.raw.positionInAxis );
		            			}
		            			 tool.close();
		            	 }
		             }]
//            listeners:{
//            	close: {
//            		fn: function(){
//
//            			
//                	},
//                	scope: thisPanel
//                	
//            	}
//            }
        });
				
		
	},
	
	

	updateHierarchyOnDimension: function(axis, newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition){
		Sbi.olap.eventManager.updateHierarchyOnDimension(axis, newHierarchyUniqueName, oldHierarchyUniqueName, hierarchyPosition);

	}

	


});





