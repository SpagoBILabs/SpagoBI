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
 *  Alberto Ghedin (alberto.ghedin@eng.it), Monica Franceschini (monica.franceschini@eng.it), Giulio Gavardi (giulio.gavardi@eng.it),
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
	//showParentMembers: null,
	//hideSpans: null,
	//showProperties: null,
	lockArray: null,
	
	menuButtons: null,
	
	labelsToolbar: null,
	labelsMenu: null,
	
	buttonsContainer: null,
	buttonsConfigContainer: null,

	//showMdx: null,

	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.toolbar && Sbi.settings.olap.toolbar.OlapToolbar) {
			Ext.apply(this, Sbi.settings.olap.toolbar.OlapToolbar);
		}

		this.callParent(arguments);
		

		
	},
	
    listeners: {
        render: function() {
                // After the component has been rendered, disable the default browser context menu
                Ext.getBody().on("contextmenu", Ext.emptyFn, null, {preventDefault: true});
        }, 
        contextmenu: function(e) {
        }
    },

	initComponent: function() {
		var thisPanel = this;
		this.labelsMenu = [];
		this.labelsToolbar = [];
		
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

		
		var thisPanel = this;
		
		// create function to move button
		var sharedConfig = {
				scope:this,
				reorderable: true,
				visible: false
				};

		
		this.menuButtons = Ext.create('Ext.button.Split', {
		    renderTo: Ext.getBody(),
		    text: LN('sbi.olap.execution.menu.buttonMenu'),
		    // handle a click on the button itself
		    handler: function() {
		    },
		    menu: new Ext.menu.Menu({
		    	//width: 100,
		        items: [
		            // these will render as dropdown menu items when the arrow is clicked:
		            {text: '', handler: function(){ }},
		           // {text: 'Item 2', handler: function(){ alert("Item 2 clicked"); }}
		        ]
		    })
		});
		this.menuButtons.setVisible(false);
		
		////////////////////
		
		
		this.buttonsContainer = {};
		this.buttonsConfigContainer = {};
		
		this.buttonsConfigContainer['BUTTON_MDX'] = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.mdx'),
			iconCls: 'mdx',
			label: 'BUTTON_MDX',
			handler: function() {
				this.showMdxWindow();
			}
		}, sharedConfig);
		
		
		this.buttonsConfigContainer['BUTTON_UNDO'] = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.undo'),
			iconCls: 'undo',
			label: 'BUTTON_UNDO',
			handler: function() {
				Sbi.olap.eventManager.undo();
			}

		}, sharedConfig);	
		
		this.buttonsConfigContainer['BUTTON_FATHER_MEMBERS'] =	Ext.apply({
			tooltip: LN('sbi.olap.toolbar.showParentMembers'),
			iconCls: 'show-parent-members',
			enableToggle: true,
			label: 'BUTTON_FATHER_MEMBERS',
	        toggleHandler: this.onShowParentMembersToggle
		}, sharedConfig);
		
		this.buttonsConfigContainer['BUTTON_HIDE_SPANS']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.hideSpans'),
			iconCls: 'hide-spans',
			enableToggle: true,
			label: 'BUTTON_HIDE_SPANS',
	        toggleHandler: this.onHideSpansToggle
			}, sharedConfig);
		
		
		this.buttonsConfigContainer['BUTTON_SHOW_PROPERTIES']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.showProperties'),
			iconCls: 'show-props',
			enableToggle: true,
			label: 'BUTTON_SHOW_PROPERTIES',
	        toggleHandler: this.onShowPropertiesToggle
		}, sharedConfig);
		
		
		this.buttonsConfigContainer['BUTTON_HIDE_EMPTY']  =  Ext.apply({
			tooltip: LN('sbi.olap.toolbar.suppressEmpty'),
			iconCls: 'empty-rows',
			enableToggle: true,
			label: 'BUTTON_HIDE_EMPTY',
	        toggleHandler: this.onSuppressEmptyToggle
		}, sharedConfig);
		
		
		this.buttonsConfigContainer['BUTTON_FLUSH_CACHE']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.clean'),
			iconCls: 'clean-icon',
			handler: function(e, f, g) {
				Sbi.olap.eventManager.cleanCache();
				},
				label: 'BUTTON_FLUSH_CACHE'
		}, sharedConfig);
		
		this.buttonsConfigContainer['BUTTON_SAVE'] = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.save'),
			iconCls: 'save-icon',
				label: 'BUTTON_SAVE'
			, handler: function() {
				Sbi.olap.eventManager.persistTransformations();
			}
		}, sharedConfig);
		
		this.buttonsConfigContainer['BUTTON_SAVE_NEW'] =Ext.apply({
			tooltip: LN('sbi.olap.toolbar.save.new'),
			iconCls: 'save-new-icon',
			label: 'BUTTON_SAVE_NEW',
			handler: function() {
				Sbi.olap.eventManager.persistNewVersionTransformations();
			}

		}, sharedConfig);
		
		
		this.buttonsConfigContainer['BUTTON_VERSION_MANAGER'] =Ext.apply({
			tooltip: LN('sbi.olap.toolbar.version.manager'),
			iconCls: 'versions-manager-icon',
			label: 'BUTTON_VERSION_MANAGER',
			handler: function() {
				var window = Ext.create('Sbi.olap.toolbar.VersionManagerWindow',{
					actualVersion: this.modelConfig.actualVersion
				});
				window.show();
			}

		}, sharedConfig);
		
			
		this.lockModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.lock'),
			iconCls: 'lock-icon'
			, handler: function() {
				var afa= null;
				Sbi.olap.eventManager.lockModel();
			},
			scope:this,
			reorderable: true
		});
		this.lockModel.setVisible(false);
		
		this.unlockModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.unlock'),
			iconCls: 'unlock-icon'
			, handler: function() {
				var afa= null;
				Sbi.olap.eventManager.unlockModel();
			},
			scope:this,
			reorderable: true
		});
		this.unlockModel.setVisible(false);

		this.lockOtherModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.lock_other'),
			iconCls: 'lock-other-icon'
			, handler: function() {
			},
			scope:this,
			reorderable: true
		});
		this.lockOtherModel.setVisible(false);
		
		if(Sbi.config.isStandalone == false){
			this.lockArray = new Array(this.lockModel, this.unlockModel, this.lockOtherModel);
		}
		


		
		var pressedBtn = this.config.toolbarConfig.drillType;
		if(pressedBtn == 'position'){
			this.drillMode.items.items[0].pressed = true;
		}else if(pressedBtn == 'member'){
			this.drillMode.items.items[1].pressed = true;
		}else if(pressedBtn == 'replace'){
			this.drillMode.items.items[2].pressed = true;
		}

		
		Ext.apply(this, {
			layout: {
				overflowHandler: 'Menu'
			},
			items   : [ 
			            this.drillMode
			            ]
		});
		
		
		
		this.callParent();
	},
	
	/**
	 * Sets the sho parent members button pressed or not
	 
	 */
    onShowParentMembersToggle: function (item, pressed){
    	if(this.buttonsContainer['BUTTON_FATHER_MEMBERS'] != undefined && this.buttonsContainer['BUTTON_FATHER_MEMBERS']!= null  ){
    		this.buttonsContainer['BUTTON_FATHER_MEMBERS'].pressed = pressed;
    	}
    	    	this.setToolbarConf({showParentMembers: pressed});
    },
    onHideSpansToggle: function (item, pressed){
    	if(this.buttonsContainer['BUTTON_HIDE_SPANS'] != undefined && this.buttonsContainer['BUTTON_HIDE_SPANS']!= null  ){
    		this.buttonsContainer['BUTTON_HIDE_SPANS'].pressed = pressed;
    	}
    	this.setToolbarConf({hideSpans: pressed});
    },
    onShowPropertiesToggle: function (item, pressed){
    	if(this.buttonsContainer['BUTTON_SHOW_PROPERTIES'] != undefined && this.buttonsContainer['BUTTON_SHOW_PROPERTIES']!= null  ){
    		this.buttonsContainer['BUTTON_SHOW_PROPERTIES'].pressed = pressed;
    	}
    	this.setToolbarConf({showProperties: pressed});
    },
    onSuppressEmptyToggle: function (item, pressed){
    	if(this.buttonsContainer['BUTTON_HIDE_EMPTY'] != undefined && this.buttonsContainer['BUTTON_HIDE_EMPTY']!= null  ){
    		this.buttonsContainer['BUTTON_HIDE_EMPTY'].pressed = pressed;
    	}
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
	, updateAfterMDXExecution: function(pivot, modelConfig){
		if(this.modelConfig==null){
			this.setLockerConfiguration(modelConfig);
			this.modelConfig = modelConfig;
		}
		
		// draw Toolbar and menu
		this.drawToolbarAndMenu(modelConfig);
				
		this.mdx=pivot.get("mdxFormatted");
	}
	
	
	, drawToolbarAndMenu: function(modelConfig){
		this.insertInToolbarArray( modelConfig.toolbarVisibleButtons);
		
		this.addLockModel();
		
		if( modelConfig.toolbarMenuButtons.length > 0){
			this.insertInMenuArray( modelConfig.toolbarMenuButtons);
		}
		
		this.setPressedMemory();	
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
				autoScroll: true,
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
	
	/**
	 *  Render the lock command
	 */
	, renderLockModel: function(result){
		// if result contains info that model was locked
		var resOb = Ext.JSON.decode(result.responseText);
		
		if(resOb.status == 'locked_by_user'){
			this.setLockByUserState();			
		}
		else{
			if(resO.status == 'unlocked'){
				this.setUnlockState();
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.lock.error"));
			}
			else{
				this.setLockByOtherState(resOb.locker);
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.errorOther")+': '+resOb.locker);
			}

		}
	}
	, renderUnlockModel: function(result){
		var resOb = Ext.JSON.decode(result.responseText);
		// check if model was really unlocked
		if(resOb.status == 'unlocked'){
			this.setUnlockState();
		}
		else{
			if(resO.status == 'locked_by_user'){
				this.setLockByUserState();
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.error"));
			}
			else{
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.errorOther")+': '+resOb.locker);
				this.setLockByOtherState(resOb.locker);
			}
			
			//alert('not unlocked');
		}
		
	}
	, setLockByUserState: function(locker){
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].enable();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].enable();
		}
		this.lockModel.hide();
		this.unlockModel.show();
		this.lockOtherModel.hide();
	}
	, setLockByOtherState: function(locker){
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].disable();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].disable();
		}
		this.lockModel.hide();
		this.unlockModel.hide();
		this.lockOtherModel.show();
		this.lockOtherModel.setTooltip(LN('sbi.olap.toolbar.lock_other')+': '+locker);

	}
	, setUnlockState: function(){
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].disable();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].disable();
		}
		this.lockModel.show();
		this.unlockModel.hide();
		this.lockOtherModel.hide();

	}
	, setLockerConfiguration: function(modelConfig){
		
		if(modelConfig.status == 'locked_by_user'){
			this.setLockByUserState(modelConfig.locker);
		}
		else if(modelConfig.status == 'locked_by_other'){
			this.setLockByOtherState(modelConfig.locker);
		}
		else if(modelConfig.status == 'unlocked'){
			this.setUnlockState();
		}	
	
	}
	/** set buttons whose label are contained in array to visible or not according to boolean visible parameter
	 * 
	 */
	, insertInToolbarArray: function(buttonArray){
		
		// visible is boolean to set visibile or not
		for( var j = 0; j < buttonArray.length; j++){
			var lab = buttonArray[j];
			this.insertButtonInToolbar(lab);

		}
	}
	
	, insertButtonInToolbar: function(label){
		var config = this.buttonsConfigContainer[label];

		// recreate
		if(this.buttonsContainer[label] != undefined && this.buttonsContainer[label] != null ){
			this.buttonsContainer[label].destroy();
		}
		
		var buttonCreated = this.createButton(config);
		this.addContextMenuListener(buttonCreated, false);	
		this.add(buttonCreated);
		this.labelsToolbar.push(label);
		this.buttonsContainer[buttonCreated.label] = buttonCreated;
	}
	
	, insertInMenuArray: function(buttonArray){
		
		for( var j = 0; j < buttonArray.length; j++){
			var lab = buttonArray[j];
			this.insertButtonInMenu(lab);
		}
		
		this.menuButtons.setVisible(true);
		

		this.add('->');
		this.add(this.menuButtons);

	
	
	}
	, insertButtonInMenu: function(label){
		// recreate
		var config = this.buttonsConfigContainer[label];
		
		if(this.buttonsContainer[label] != undefined && this.buttonsContainer[label] != null ){
			this.buttonsContainer[label].destroy();
		}
		
		var buttonCreated = this.createButton(config);
		this.addContextMenuListener(buttonCreated, true);
		buttonCreated.text = buttonCreated.tooltip;
		this.menuButtons.menu.add(buttonCreated);
		this.labelsMenu.push(label);
		this.buttonsContainer[buttonCreated.label] = buttonCreated;
	}
	
	
	, moveButton: function(button, inMenu){

		// if is in menu must insert in toolbar and viceversa
		if(inMenu==true){
			
			// in moving button from menu to toolbar the menu must be removed and re-added
			this.remove(this.menuButtons, false);

			// remove the space it is the last element after menu has been removed
			this.remove(this.items.length-1);
			

			this.insertButtonInToolbar(button.label);
			this.add('->');
			this.add(this.menuButtons);
			this.labelsToolbar.push(button.label);
			this.deleteFromArray(this.labelsMenu, button.label);
	
			
		}
		else{
			this.insertButtonInMenu(button.label);			
			this.deleteFromArray(this.labelsToolbar, button.label);
		}
	
	}
	
	//returns 'menu', 'toolbar' or 'none
	, isButtonInMenuOrToolbar: function(label){
		if( this.contains(this.labelsToolbar,label)){
			return 'toolbar';
		}
		else if(this.contains(this.labelsMenu,label)){
			return 'menu';
		}	
		else return 'none';

	}
	
	, createButton: function(config){	
		var button = Ext.create('Ext.Button', config);
		this.buttonsContainer[button.label] = button;
		button.setVisible(true);
		return button;
	}
	
	, addContextMenuListener: function(butt, isMenu){
		// add context menu listener
		var msg = null;
		if(isMenu == true){
			msg = 'add to toolbar';
		}
		else if(isMenu == false){
			msg = 'add to menu';
		}
		
		var thisPanel = this;
		  butt.on('render', function(button){
				this.getEl().addListener('contextmenu',
						function(e, el){
							var m = Ext.create('Ext.menu.Menu', {
						width: 100,
						height: 30,
						margin: '0 0 10 0',
						items: [{
							text: msg
								, listeners:{
									click: {
										fn: function(){
											var where = this.isButtonInMenuOrToolbar(button.label);
											if(where == 'toolbar'){
												this.moveButton(button, false);
											}
											else if(where == 'menu'){
												this.moveButton(button, true);
											}
											else return;

										}
						, scope: thisPanel
									}
								}
						}]
					});	

					m.showAt(e.getXY());
				},
				thisPanel);			
			});
		
	}
	
	/* this functions treats particular buttons that need to preserve memory if must be already pressed
	 * 
	 */
	, setPressedMemory: function(){
		
		for(var lab in this.buttonsContainer){
			var button = this.buttonsContainer[lab];
			
			if(button.label == 'BUTTON_FATHER_MEMBERS'){
				var isShownParentMembers = this.config.toolbarConfig.showParentMembers;
					
				if(isShownParentMembers == true){
					button.pressed = true;
				}else{
					button.pressed = false;
				}		

			}
		}

	}
	, addLockModel: function(){
		this.add(this.lockModel);
		this.add(this.unlockModel);
		this.add(this.lockotherModel);

	}
	 
	
	, contains: function(a, obj) {
	    var i = a.length;
	    while (i--) {
	       if (a[i] === obj) {
	           return true;
	       }
	    }
	    return false;
	}
	, deleteFromArray: function(array, search){
		var indexToDelete = -1;
		for (var i = 0; i<array.length; i++){
			if(array[i] != undefined && array[i]==search){
				indexToDelete = i;
			}
		}
		if(i != -1){
			array = array.splice(indexToDelete,1);
			return array;
		}
		else return array;
	}


});
