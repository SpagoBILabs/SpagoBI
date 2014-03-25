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
	 * @property {Ext.Panel} moveUpPanel
	 * ABSTRACT: panel to move up in the axis positions the Dimension
	 */
	moveUpPanel: null,

	/**
	 * @property {Ext.Panel} moveUpDown
	 * ABSTRACT: panel to move down in the axis positions the Dimension
	 */
	moveDownPanel: null,

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
		this.moveUpPanel = Ext.create("Ext.Panel",this.buildUpPanelConf());
		this.moveDownPanel =Ext.create("Ext.Panel",this.buildDownPanelConf());

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
			layout: this.subPanelLayout,
			
			listeners: {
				el: {
					mouseover: {
						fn: function (event, html, eOpts) {
							this.showMovePanels();
						},
						scope: this
					},
					mouseout: {
						fn: function (event, html, eOpts) {
							this.hideMovePanels();
						},
						scope: this
					}
				}
			}
		}
		);
		this.callParent();
	},
	
	buildItems: function(){
		var items = [];
		if(!this.firstDimension){
			items.push( this.moveUpPanel);
		}

		items.push( this.dimensionPanel);

		if(!this.dimension.get("measure")){
			items.push(this.buildFilterButton());
		}
		
		if(!this.lastDimension){
			items.push( this.moveDownPanel);
		}
		return items;
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
	 * Builds the central panel with the name of the dimension
	 */
	buildUpPanelConf: function(){
		return {
			xtype: "panel",
			style: "background-color: transparent !important",
			bodyStyle: "background-color: transparent !important",
			border: false,
			html: "  ",
			hidden: true,
			listeners: {
				el: {
					click: {
						fn: function (event, html, eOpts) {
							this.fireEvent("moveUp",this);
						},
						scope: this
					}
				}
			}
		};
	},
	
	/**
	 * Builds the central panel with the name of the Dimension
	 */
	buildDownPanelConf: function(){
		return {
			xtype: "panel",
			style: "background-color: transparent !important",
			bodyStyle: "background-color: transparent !important",
			border: false,
			html: "  ",
			hidden: true,
			listeners: {
				el: {
					click: {
						fn: function (event, html, eOpts) {
							this.fireEvent("moveDown",this);
						},
						scope: this
					}
				}
			}
		};
	},

	/**
	 * Show the panels to move up/down the Dimension
	 */
	showMovePanels: function(){
		this.moveUpPanel.show();
		this.moveDownPanel.show();
	},

	/**
	 * Hide the panels to move up/down the Dimension
	 */
	hideMovePanels: function(){
		this.moveUpPanel.hide();
		this.moveDownPanel.hide();
	},
	
	/**
	 * Manage the visibility of the members in the axis
	 * @param members
	 */
	setFilterValue: function(members){
		if(members && members.length>0){
			Sbi.olap.eventManager.placeMembersOnAxis(this.dimension, members);
		}
	}



});





