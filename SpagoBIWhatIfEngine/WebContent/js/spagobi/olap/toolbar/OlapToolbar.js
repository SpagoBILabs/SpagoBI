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
			showParentMembers: false,
			hideSpans: false,
			showProperties: false,
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
	showParentMembers: null,
	hideSpans: null,
	showProperties: null,

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
		
		
		this.drillMode = Ext.create('Ext.container.ButtonGroup', 
			{
	        xtype: 'buttongroup',
	        columns: 3,
			style:'border-radius: 10px;padding: 0px;margin: 0px;',
	        items: [{
	            text: 'Position',
	            scale: 'small',
	            enableToggle: true,
	            allowDepress: false,
	            pressedCls: 'pressed-drill',
	            toggleGroup: 'drill',
	            cls: 'drill-btn-left',
				scope:thisPanel,
				handler: function() {
					this.setToolbarConf({drillType: 'position'});
				},
				reorderable: true
	        },
			{
	            text: 'Member',
	            scale: 'small',
	            enableToggle: true,
	            allowDepress: false,
	            toggleGroup: 'drill',
	            pressedCls: 'pressed-drill',
	            cls: 'drill-btn-center',
				scope:thisPanel,
				handler: function() {
					this.setToolbarConf({drillType: 'member'});
				},
				reorderable: true
	        },
			{
	            text: 'Replace',
	            scale: 'small',
	            enableToggle: true,
	            allowDepress: false,
	            toggleGroup: 'drill',	
	            pressedCls: 'pressed-drill',
	            cls: 'drill-btn-right',
				scope:thisPanel,
				handler: function() {
					this.setToolbarConf({drillType: 'replace'});
				},
				reorderable: true

	        }]
	    }
		
		);

		
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
		
		this.showParentMembers = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.showParentMembers'),
			iconCls: 'show-parent-members',
			enableToggle: true,
	        toggleHandler: this.onShowParentMembersToggle,

			scope:this,
			reorderable: true
		});

		
		this.hideSpans = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.hideSpans'),
			iconCls: 'hide-spans',
			enableToggle: true,
	        toggleHandler: this.onHideSpansToggle,

			scope:this,
			reorderable: true
		});
		
		this.showProperties = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.showProperties'),
			iconCls: 'show-props',
			enableToggle: true,
	        toggleHandler: this.onShowPropertiesToggle,

			scope:this,
			reorderable: true
		}); 
		
		this.suppressEmpty = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.suppressEmpty'),
			iconCls: 'empty-rows',
			enableToggle: true,
	        toggleHandler: this.onSuppressEmptyToggle,

			scope:this,
			reorderable: true
		});
		this.clean = Ext.create('Ext.Button', {
			text: LN('sbi.olap.toolbar.clean'),
			iconCls: 'undo',
			handler: function() {
				Sbi.olap.eventManager.cleanCache();
			},
			scope:this,
			reorderable: true
		});
		
		this.persist = Ext.create('Ext.Button', {
			text: "PERSIST",
			iconCls: 'undo',
			handler: function() {
				Sbi.olap.eventManager.persistTransformations();
			},
			scope:this,
			reorderable: true
		});
		var pressedBtn = this.config.toolbarConfig.drillType;
		if(pressedBtn == 'position'){
			this.drillMode.items.items[0].pressed = true;
		}else if(pressedBtn == 'member'){
			this.drillMode.items.items[1].pressed = true;
		}else if(pressedBtn == 'replace'){
			this.drillMode.items.items[2].pressed = true;
		}
		
		var isShownParentMembers = this.config.toolbarConfig.showParentMembers;
		if(isShownParentMembers == true){
			this.showParentMembers.pressed = true;
		}else{
			this.showParentMembers.pressed = false;
		}
		

		Ext.apply(this, {
			layout: {
				overflowHandler: 'Menu'
			},
			items   : [ this.drillMode, this.showMdx, this.undo , 
			            this.clean, this.persist,
			            this.showParentMembers, 
			            this.hideSpans, 
			            /*this.showProperties, */
			            this.suppressEmpty]
		});
		this.callParent();
	},
	
	/**
	 * Sets the sho parent members button pressed or not
	 
	 */
    onShowParentMembersToggle: function (item, pressed){
    	this.showParentMembers.pressed = pressed;
    	this.setToolbarConf({showParentMembers: pressed});
    },
    onHideSpansToggle: function (item, pressed){
    	this.hideSpans.pressed = pressed;
    	this.setToolbarConf({hideSpans: pressed});
    },
    onShowPropertiesToggle: function (item, pressed){
    	this.showProperties.pressed = pressed;
    	this.setToolbarConf({showProperties: pressed});
    },
    onSuppressEmptyToggle: function (item, pressed){
    	this.suppressEmpty.pressed = pressed;
    	this.setToolbarConf({suppressEmpty: pressed});
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
		
		this.toolbarConfig = Ext.apply(this.toolbarConfig, conf);		
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
