/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Toolbar for the execution.. The buttons are sortable and hiddable by the use.. The configuartion is stored in the cookies and restored every time the engine is opened 
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it), Monica Franceschini (monica.franceschini@eng.it)
 */



Ext.define('Sbi.olap.toolbar.OlapToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	plugins : Ext.create('Ext.ux.BoxReorderer', {}),

	config:{
		toolbarConfig: {
			drillType: 'position',
			dimensionHierarchyMap:{
				x: 1,
				y: 2
			}
		},
		mdx: ""
	},

	/**
	 * @property {Ext.window.Window} mdxWindow
	 *  The window with the medx query
	 */
	mdxWindow: null,

	mdxContainerPanel: null,

	drillMode: null,

	showMdx: null,


	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.toolbar && Sbi.settings.olap.toolbar.OlapToolbar) {
			Ext.apply(this, Sbi.settings.olap.toolbar.OlapToolbar);
		}

		this.callParent(arguments);
		

		
	},

	initComponent: function() {
		var thisPanel = this;
		this.addEvents(
		        /**
		         * @event configChange
		         * The final user changes the configuration of the model 
				 * @param {Object} configuration
		         */
		        'configChange'
				);
		this.drillMode = Ext.create('Ext.Button', {
			text: LN('sbi.olap.toolbar.drill.mode'),
			iconCls: 'drill-mode',
			menu: [{
				text: 'Position',
				scope:thisPanel,
				handler: function() {
					this.setToolbarConf({drillType: 'position'});
				}},
				{text: 'Member',
					scope:thisPanel,
					handler: function() {
						this.setToolbarConf({drillType: 'member'});
					}},
					{text: 'Replace',
						scope:thisPanel,
						handler: function() {
							this.setToolbarConf({drillType: 'replace'});
						}}],
						reorderable: true
		});

		this.showMdx = Ext.create('Ext.Button', {
			text: LN('sbi.olap.toolbar.mdx'),
			iconCls: 'mdx',
			handler: function() {
				this.showMdxWindow();
			},
			scope:this,
			reorderable: true
		});
		
		this.undo = Ext.create('Ext.Button', {
			text: LN('sbi.olap.toolbar.undo'),
			iconCls: 'undo',
			handler: function() {
				Sbi.olap.eventManager.undo();
			},
			scope:this,
			reorderable: true
		});

		Ext.apply(this, {
			layout: {
				overflowHandler: 'Menu'
			},
			items   : [ this.drillMode, this.showMdx, this.undo ]
		});
		this.callParent();
	},

	/**
	 * Gets the configuration of the view
	 * @return {Object} returns the configuration state: position and visibility of the buttons
	 */
	getViewState: function(){

	},

	/**
	 * Sets the state of the view. Updates the toolbar setting the order and the visibility of the buttons.
	 * The hidden buttons are grouped in a new menu in the top right 
	 * @param {Object} state of the view 
	 */
	setViewState: function(state){
		this.toolbarConfig = Ext.apply(this.toolbarConfig, state);
	}

	/**
	 * Sets the state of the view. Updates the toolbar setting the order and the visibility of the buttons.
	 * Syncronize the configuration in the server
	 * @param {Object} state of the view 
	 */
	, setToolbarConf: function (conf){
		this.setViewState(conf);
		this.fireEvent('configChange',this.toolbarConfig);
	}

	/**
	 * Updates the object after the execution of a mdx query
	 * @param {Sbi.olap.PivotModel} pivot model
	 */
	, updateAfterMDXExecution: function(pivot){
		this.mdx=pivot.get("mdxFormatted");
	}

	/**
	 * Updates the object after the execution of a mdx query
	 * @param {Sbi.olap.PivotModel} pivot model
	 */
	, showMdxWindow: function(){
		if(!this.mdxWindow){


			this.mdxContainerPanel = Ext.create('Ext.panel.Panel', {
				frame: false,
				layout: 'fit',
				html: "" 
			});

			this.mdxWindow = Ext.create('Ext.window.Window', {
				height: 400,
				width: 300,
				layout: 'fit',
				closeAction: 'hide',
				items:[this.mdxContainerPanel],
				bbar:[
				      '->',    {
				    	  text: LN('sbi.common.close'),
				    	  handler: function(){
				    		  this.mdxWindow.hide();
				    	  },
				    	  scope: this
				      }]
			});
		}
		this.mdxContainerPanel.update(this.mdx);
		this.mdxWindow.show();
	}

});
