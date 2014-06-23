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
			drillType: 'position'
		},
		mdx: ""
	},

	/**
	 * @property {Ext.window.Window} mdxWindow
	 *  The window with the medx query
	 */
	mdxWindow: null,

	mdxContainerPanel: null,

// fixed button in toolbar
	drillMode: null,	
	
	// array containing lock buttons
	lockArray: null,
	
	// menu with buttons on toolbar
	menuButtons: null,
	
	// labels of button on toolbar and labels of buttons on menu
	labelsToolbar: null,
	labelsMenu: null,
	
	// two object cntaining label => button and label => configuration
	buttonsContainer: null,
	buttonsConfigContainer: null,

	// lock dinamic informations
	modelStatus: null,
	modelLocker: null,

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
		
		// FIXED Button on toolbar
		
		this.drillMode = Ext.create('Ext.container.ButtonGroup', 
			{
	        xtype: 'buttongroup',
	        columns: 3,
			style:'border-radius: 10px;padding: 0px;margin: 0px;',
			reorderable: false,
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
				reorderable: false
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
				reorderable: false
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
				reorderable: false

	        }]
	    }
		
		);

		
		
		// create function to move button
		var sharedConfig = {
				scope:this,
				reorderable: true,
				visible: false
				};
		
		// MENU BUTTONS CREATION //
		
		this.menuButtons = Ext.create('Ext.button.Split', {
			reorderable: false,
		    renderTo: Ext.getBody(),
		    //text: LN('sbi.olap.execution.menu.buttonMenu'),
		    iconCls: 'context-menu-icon',
		    //width: 30,
		    // handle a click on the button itself
		    handler: function() {
		    },
		    menu: new Ext.menu.Menu({
		    	//width: 20,
		        items: [
		            // these will render as dropdown menu items when the arrow is clicked:
		            {text: '', handler: function(){ }},
		           // {text: 'Item 2', handler: function(){ alert("Item 2 clicked"); }}
		        ]
		    })
		});
		this.menuButtons.setVisible(false);
		
		
		
		// CONFIGURATIONS //
		
		
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
		
			
		
		this.buttonsConfigContainer['BUTTON_FATHER_MEMBERS'] =	Ext.apply({
			tooltip: LN('sbi.olap.toolbar.showParentMembers'),
			iconCls: 'show-parent-members',
			enableToggle: true,
			label: 'BUTTON_FATHER_MEMBERS',
	        toggleHandler: this.onShowParentMembersToggle
		}, sharedConfig);
//		if(this.toolbarConfig != undefined && this.toolbarConfig.showParentMembers == true){
//			this.buttonsConfigContainer['BUTTON_FATHER_MEMBERS'].pressed = true;
//		}
//		else{ this.buttonsConfigContainer['BUTTON_FATHER_MEMBERS'].pressed = false;}

		
		this.buttonsConfigContainer['BUTTON_HIDE_SPANS']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.hideSpans'),
			iconCls: 'hide-spans',
			enableToggle: true,
			label: 'BUTTON_HIDE_SPANS',
	        toggleHandler: this.onHideSpansToggle
			}, sharedConfig);
//		if(this.toolbarConfig != undefined && this.toolbarConfig.hideSpans == true){
//			this.buttonsConfigContainer['BUTTON_HIDE_SPANS'].pressed = true;
//		}
//		else{ this.buttonsConfigContainer['BUTTON_HIDE_SPANS'].pressed = false;}
		
		
		this.buttonsConfigContainer['BUTTON_SHOW_PROPERTIES']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.showProperties'),
			iconCls: 'show-props',
			enableToggle: true,
			label: 'BUTTON_SHOW_PROPERTIES',
	        toggleHandler: this.onShowPropertiesToggle
		}, sharedConfig);
//		if(this.toolbarConfig != undefined && this.toolbarConfig.showProperties == true){
//			this.buttonsConfigContainer['BUTTON_SHOW_PROPERTIES'].pressed = true;
//		}
//		else{ this.buttonsConfigContainer['BUTTON_SHOW_PROPERTIES'].pressed = false;}
		
		this.buttonsConfigContainer['BUTTON_HIDE_EMPTY']  =  Ext.apply({
			tooltip: LN('sbi.olap.toolbar.suppressEmpty'),
			iconCls: 'empty-rows',
			enableToggle: true,
			label: 'BUTTON_HIDE_EMPTY',
	        toggleHandler: this.onSuppressEmptyToggle
		}, sharedConfig);
//		if(this.toolbarConfig != undefined && this.toolbarConfig.suppressEmpty == true){
//			this.buttonsConfigContainer['BUTTON_HIDE_EMPTY'].pressed = true;
//		}
//		else{ this.buttonsConfigContainer['BUTTON_HIDE_EMPTY'].pressed = false;}
		
		this.buttonsConfigContainer['BUTTON_FLUSH_CACHE']  = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.clean'),
			iconCls: 'clean-icon',
			handler: function(e, f, g) {
				Sbi.olap.eventManager.cleanCache();
				},
				label: 'BUTTON_FLUSH_CACHE'
		}, sharedConfig);
		
		
		// if we are in standalone mode save and save new are always shown, if in spagobi mode not because model must be locked		
		var saveHidden= true;
		if(Sbi.config.isStandalone == true){
			saveHidden = false;
		}
		
		
		this.buttonsConfigContainer['BUTTON_UNDO'] = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.undo'),
			iconCls: 'undo',
			label: 'BUTTON_UNDO',
			hidden : saveHidden,
			handler: function() {
				Sbi.olap.eventManager.undo();
			},
			disabled: true
		}, sharedConfig);
		
		this.buttonsConfigContainer['BUTTON_SAVE'] = Ext.apply({
			tooltip: LN('sbi.olap.toolbar.save'),
			iconCls: 'save-icon',
				label: 'BUTTON_SAVE'
			, hidden : saveHidden
			, handler: function() {
				Sbi.olap.eventManager.persistTransformations();
			}
		}, sharedConfig);
		
		this.buttonsConfigContainer['BUTTON_SAVE_NEW'] =Ext.apply({
			tooltip: LN('sbi.olap.toolbar.save.new'),
			iconCls: 'save-new-icon',
			hidden : saveHidden,
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
		
		this.buttonsConfigContainer['BUTTON_EXPORT_OUTPUT'] =Ext.apply({
			tooltip: LN('sbi.olap.toolbar.exportoutput'),
			iconCls: 'export-icon',
			label: 'BUTTON_EXPORT_OUTPUT',
			handler: function() {
				var window = Ext.create('Sbi.olap.toolbar.ExportWizardWindow',{
					actualVersion: this.modelConfig.actualVersion
				});
				window.show();
				window.on('exportOutput', function(params){
					Sbi.olap.eventManager.exportOutput(params);
				},this)
			}

		}, sharedConfig);
		
		
		// LOCK BUTTON CREATIONR
		
		this.lockModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.lock'),
			iconCls: 'lock-icon'
			, handler: function() {
				var afa= null;
				Sbi.olap.eventManager.lockModel();
			},
			scope:this
			,reorderable: false
		});
		this.lockModel.setVisible(false);
		
		this.unlockModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.unlock'),
			iconCls: 'unlock-icon'
			, handler: function() {
				var afa= null;
				Sbi.olap.eventManager.unlockModel();
			},
			scope:this
			,reorderable: false
		});
		this.unlockModel.setVisible(false);

		this.lockOtherModel = Ext.create('Ext.Button', {
			tooltip: LN('sbi.olap.toolbar.lock_other'),
			iconCls: 'lock-other-icon'
			, handler: function() {
			},
			scope:this
			,reorderable: false
		});
		this.lockOtherModel.setVisible(false);
		
		if(Sbi.config.isStandalone == false){
			this.lockArray = new Array(this.lockModel, this.unlockModel, this.lockOtherModel);
		}
		
		
		//var pressedBtn = this.config.toolbarConfig.drillType;
		var pressedBtn = this.toolbarConfig.drillType;
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
			items   : []
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
		
		var firstExecution = false;
		// first execution loads model config
		if(this.modelConfig==null){
			firstExecution = true;
			this.modelConfig = modelConfig;
			
			// change configuration by marking press configurations depending on modelConfig
			this.markPressedButtons(); 
			
			// try to get cookies else take configuration from model config
			// labels Toolbar
			
			var myCookieToolbar = Ext.util.Cookies.get(Sbi.config.documentLabel+'labelsToolbar');
			var toolbarVisibleButtons = modelConfig.toolbarVisibleButtons || new Array();	
			var toolbarVisibleMenu = modelConfig.toolbarMenuButtons || new Array();	
			
			var decodedCookieToolbar = null;
			this.labelsToolbar = new Array();
			this.labelsMenu = new Array();
			
			if(myCookieToolbar!=undefined && myCookieToolbar!=''){
				decodedCookieToolbar = Ext.JSON.decode(myCookieToolbar);
				
				//merge the visible buttons (toolbar and menu) in a list
				toolbarVisibleButtons = toolbarVisibleButtons.concat(toolbarVisibleMenu);
				
				//merge the cookies with the toolbarconfig and create the list of visible buttons in the toolbar
				for(var i=0; i<decodedCookieToolbar.length; i++){
					var tool = decodedCookieToolbar[i];
					var index = toolbarVisibleButtons.indexOf(tool);
					if(index>=0){//if the button live in the cookies and it's visible
						this.labelsToolbar.push(tool);
						toolbarVisibleButtons.splice(index,1);
					}
				}
				
				//all the buttons not visible in the toolbar go in the menu
				for(var i=0; i<toolbarVisibleButtons.length; i++){
					this.labelsMenu.push(toolbarVisibleButtons[i]);
				}		
			}else{
				this.labelsToolbar = toolbarVisibleButtons;
				this.labelsMenu = toolbarVisibleMenu;
			}

			//update the cookie
			Ext.util.Cookies.set(Sbi.config.documentLabel+'labelsToolbar',Ext.JSON.encode(this.labelsToolbar));

		}
		
		// if scenario is not what if do not draw some buttons
		this.cleanButtonsIfNotWhatIfScenario(this.modelConfig.whatIfScenario);
		
		// draw Toolbar and menu
		this.drawToolbarAndMenu(this.modelConfig);
				
		this.mdx=pivot.get("mdxFormatted");
		
		// locker configuration must be set after buttons have been drawed
		this.setLockerConfiguration(firstExecution, this.modelConfig);
	
		// undo button is present only in what if scenario
		if(this.modelConfig.whatIfScenario != undefined && this.modelConfig.whatIfScenario == true){
			var undoButton = this.buttonsContainer["BUTTON_UNDO"];
			if(undoButton != undefined){
				//undoButton.on('move', function(){alert('mosso');});
				undoButton.setDisabled( !pivot.get("hasPendingTransformations") );
			}
		}
	}
	
	
	, drawToolbarAndMenu: function(modelConfig){
				
		// recreate toolbar without destroyng buttons
		this.removeAll(false);
		// add first buttons always on toolbar
		this.addToolbarFixedButtons();
		
		// lock buttons only in what if scenario, lock button is before custom button
		if(modelConfig.whatIfScenario != undefined && modelConfig.whatIfScenario==true){
			this.addLockModel();
		}
		
		// insert customized toolbar
		this.insertInToolbarArray( this.labelsToolbar);


		// customized menu
		this.insertInMenuArray( this.labelsMenu);
		this.add({ xtype: 'tbspacer', flex:1, reorderable: false});
		this.add(this.menuButtons);
		if( this.labelsMenu.length > 0){
			this.menuButtons.setVisible(true);	
		}
		else{
			this.menuButtons.setVisible(false);	
		}

	}
	, addToolbarFixedButtons: function(){
		this.add(this.drillMode);
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
			if(resOb.status == 'unlocked'){
				this.setUnlockState();
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.lock.error"));
			}
			else{
				this.setLockByOtherState(resOb.locker);
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.errorOther")+': '+resOb.locker);
			}

		}
		this.modelStatus = resOb.status;
		this.modelLocker = resOb.locker;

	}
	/**
	 *  render after unlock command
	 */
	, renderUnlockModel: function(result){
		var resOb = Ext.JSON.decode(result.responseText);
		// check if model was really unlocked
		if(resOb.status == 'unlocked'){
			this.setUnlockState();
		}
		else{
			if(resOb.status == 'locked_by_user'){
				this.setLockByUserState();
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.error"));
			}
			else{
				Sbi.exception.ExceptionHandler.showInfoMessage(LN("sbi.olap.artifact.unlock.errorOther")+': '+resOb.locker);
				this.setLockByOtherState(resOb.locker);
			}
			
			//alert('not unlocked');
		}
		this.modelStatus = resOb.status;
		this.modelLocker = resOb.locker;
		
	}
	, setLockByUserState: function(locker){
		if(this.buttonsContainer['BUTTON_UNDO'] != undefined && this.buttonsContainer['BUTTON_UNDO'] != null){
			this.buttonsContainer['BUTTON_UNDO'].show();
		}
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].show();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].show();
		}
		if(this.buttonsContainer['BUTTON_VERSION_MANAGER'] != undefined && this.buttonsContainer['BUTTON_VERSION_MANAGER'] != null){
			this.buttonsContainer['BUTTON_VERSION_MANAGER'].show();
		}
		this.lockModel.hide();
		this.unlockModel.show();
		this.lockOtherModel.hide();
	}
	, setLockByOtherState: function(locker){
		if(this.buttonsContainer['BUTTON_UNDO'] != undefined && this.buttonsContainer['BUTTON_UNDO'] != null){
			this.buttonsContainer['BUTTON_UNDO'].hide();
		}
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].hide();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].hide();
		}
		if(this.buttonsContainer['BUTTON_VERSION_MANAGER'] != undefined && this.buttonsContainer['BUTTON_VERSION_MANAGER'] != null){
			this.buttonsContainer['BUTTON_VERSION_MANAGER'].hide();
		}
		this.lockModel.hide();
		this.unlockModel.hide();
		this.lockOtherModel.show();
		this.lockOtherModel.setTooltip(LN('sbi.olap.toolbar.lock_other')+': '+locker);

	}
	, setUnlockState: function(){
		if(this.buttonsContainer['BUTTON_UNDO'] != undefined && this.buttonsContainer['BUTTON_UNDO'] != null){
			this.buttonsContainer['BUTTON_UNDO'].hide();
		}
		if(this.buttonsContainer['BUTTON_SAVE'] != undefined && this.buttonsContainer['BUTTON_SAVE'] != null){
			this.buttonsContainer['BUTTON_SAVE'].hide();
		}
		if(this.buttonsContainer['BUTTON_SAVE_NEW'] != undefined && this.buttonsContainer['BUTTON_SAVE_NEW'] != null){
			this.buttonsContainer['BUTTON_SAVE_NEW'].hide();
		}
		if(this.buttonsContainer['BUTTON_VERSION_MANAGER'] != undefined && this.buttonsContainer['BUTTON_VERSION_MANAGER'] != null){
			this.buttonsContainer['BUTTON_VERSION_MANAGER'].hide();
		}
		this.lockModel.show();
		this.unlockModel.hide();
		this.lockOtherModel.hide();

	}

	, setLockerConfiguration: function(firstExecution, modelConfig){
		// if it is first Execution take information from model config, else from global variables	
		if(firstExecution==true){
			this.modelStatus = modelConfig.status;
			this.modelLocker = modelConfig.locker;
		}
		
		if(this.modelStatus == 'locked_by_user'){
			this.setLockByUserState(this.modelLocker);
		}
		else if(this.modelStatus == 'locked_by_other'){
			this.setLockByOtherState(this.modelLocker);
		}
		else if(this.modelStatus == 'unlocked'){
			this.setUnlockState();
		}	

	
	}
	/** set buttons whose label are contained in array to visible or not according to boolean visible parameter
	 * 
	 */
	, insertInToolbarArray: function(labelsToolbarArray){
		
		// visible is boolean to set visibile or not
		for( var j = 0; j < labelsToolbarArray.length; j++){
			var lab = labelsToolbarArray[j];
			this.insertButton(lab, false);

		}
	}
	
	, insertInMenuArray: function(labelsMenuArray){
		
		for( var j = 0; j < labelsMenuArray.length; j++){
			var lab = labelsMenuArray[j];
			this.insertButton(lab, true);
		}
		
		this.menuButtons.setVisible(true);
	
	}
	
/**
 *  function that recreate button fromm config and store it in toolbar or menu; 
 *  some information must be preserved from previous button (if existed)
 */
	, insertButton: function(label, inMenu){
		
		var alreadyPresent = false;
		var presentPressed = false;
		var presentDisabled = false;
		
		if(this.buttonsContainer[label] != undefined && this.buttonsContainer[label] != null ){
			alreadyPresent = true;
		}

		if(alreadyPresent==true){
			presentPressed = this.buttonsContainer[label].pressed;
			presentDisabled = this.buttonsContainer[label].disabled;
			this.buttonsContainer[label].destroy();
		}
		
		var config = this.buttonsConfigContainer[label];
		
		var buttonCreated = this.createButton(config);

		this.addContextMenuListener(buttonCreated, false);	
		
		// recreate
		if(alreadyPresent==true){
			buttonCreated.pressed = presentPressed;
			buttonCreated.disabled = presentDisabled;
		}
		
		this.buttonsContainer[buttonCreated.label] = buttonCreated;

		// add particular pressed logic depending on button
		//this.setPressedMemory();	
		if(inMenu == true){
			buttonCreated.text = buttonCreated.tooltip;
			this.menuButtons.menu.add(buttonCreated);
			if(!this.contains(this.labelsMenu, label)){
				this.labelsMenu.push(label);
			}
			
		}
		else{
			this.add(buttonCreated);
			if(!this.contains(this.labelsToolbar, label)){
				this.labelsToolbar.push(label);
				Ext.util.Cookies.set(Sbi.config.documentLabel+'labelsToolbar',Ext.JSON.encode(this.labelsToolbar));
			}
		}
	}

	/**
	 * Move button from menu to toolbar or viceversa
	 */
	, moveButton: function(button, inMenu){

		// if is in menu must insert in toolbar and viceversa
		if(inMenu==true){
			
			// in moving button from menu to toolbar the menu must be removed and re-added
			this.remove(this.menuButtons, false);

			// remove the space it is the last element after menu has been removed
			this.remove(this.items.length-1);
			

			this.insertButton(button.label, false);
			this.add({ xtype: 'tbspacer', flex:1, reorderable: false});
			this.add(this.menuButtons);
			//this.labelsMenu = 
			this.labelsMenu = this.deleteFromArray(this.labelsMenu, button.label);
//			Ext.util.Cookies.set(Sbi.config.documentLabel+'labelsMenu',Ext.JSON.encode(this.labelsMenu));
			
		}
		else{
			this.insertButton(button.label, true);			
			//this.labelsToolbar =
			this.labelsToolbar = this.deleteFromArray(this.labelsToolbar, button.label);
			Ext.util.Cookies.set(Sbi.config.documentLabel+'labelsToolbar',Ext.JSON.encode(this.labelsToolbar));
		}
		
		if(this.labelsMenu.length>0){
			this.menuButtons.setVisible(true);
		}
		else{
			this.menuButtons.setVisible(false);
		}
	
	}
	
	/**
	 * tells if button is in menu or toolbar by looking at labelsMenu, labelsToolbar arrays
	 */
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
		//config.width=20;
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
	 * it is called on the first draw, when buttons has no memory and modelconfig stores info
	 */
	, markPressedButtons: function(){ 
		
		var buttonConfig = this.buttonsConfigContainer['BUTTON_FATHER_MEMBERS'];
		if(buttonConfig != undefined){
			var isShownParentMembers = this.modelConfig.showParentMembers;
			if(isShownParentMembers == true){
				buttonConfig.pressed = true;
			}else{
				buttonConfig.pressed = false;
			}		
		}
		
		buttonConfig = this.buttonsConfigContainer['BUTTON_HIDE_SPANS'];
		if(buttonConfig != undefined){
			var isHideSpans = this.modelConfig.hideSpans;
			if(isHideSpans == true){
				buttonConfig.pressed = true;
			}else{
				buttonConfig.pressed = false;
			}		
		}
		
		buttonConfig = this.buttonsConfigContainer['BUTTON_SHOW_PROPERTIES'];
		if(buttonConfig != undefined){
			var isHideSpans = this.modelConfig.showProperties;
			if(isHideSpans == true){
				buttonConfig.pressed = true;
			}else{
				buttonConfig.pressed = false;
			}		
		}
		
		buttonConfig = this.buttonsConfigContainer['BUTTON_HIDE_EMPTY'];
		if(buttonConfig != undefined){
			var isHideSpans = this.modelConfig.suppressEmpty;
			if(isHideSpans == true){
				buttonConfig.pressed = true;
			}else{
				buttonConfig.pressed = false;
			}		
		}
		
	}
	, addLockModel: function(){
		this.add(this.lockModel);
		this.add(this.unlockModel);
		this.add(this.lockOtherModel);

	}
	, cleanButtonsIfNotWhatIfScenario: function(isWhatIf){
	 //following buttons must not be present if scenario is not what if BUTTON_SAVE, BUTTON_SAVE_NEW	
		if(isWhatIf == undefined || isWhatIf == false){
			this.modelConfig.toolbarVisibleButtons = this.deleteFromArray(this.modelConfig.toolbarVisibleButtons, 'BUTTON_SAVE');
			this.modelConfig.toolbarVisibleButtons = this.deleteFromArray(this.modelConfig.toolbarVisibleButtons, 'BUTTON_SAVE_NEW');
			this.modelConfig.toolbarVisibleButtons = this.deleteFromArray(this.modelConfig.toolbarVisibleButtons, 'BUTTON_UNDO');	
			this.modelConfig.toolbarVisibleButtons = this.deleteFromArray(this.modelConfig.toolbarVisibleButtons, 'BUTTON_VERSION_MANAGER');	
			this.modelConfig.toolbarVisibleButtons = this.deleteFromArray(this.modelConfig.toolbarVisibleButtons, 'BUTTON_EXPORT_OUTPUT');	
			this.modelConfig.toolbarMenuButtons = this.deleteFromArray(this.modelConfig.toolbarMenuButtons, 'BUTTON_SAVE');
			this.modelConfig.toolbarMenuButtons = this.deleteFromArray(this.modelConfig.toolbarMenuButtons, 'BUTTON_SAVE_NEW');
			this.modelConfig.toolbarMenuButtons = this.deleteFromArray(this.modelConfig.toolbarMenuButtons, 'BUTTON_UNDO');
			this.modelConfig.toolbarMenuButtons = this.deleteFromArray(this.modelConfig.toolbarMenuButtons, 'BUTTON_VERSION_MANAGER');
			this.modelConfig.toolbarMenuButtons = this.deleteFromArray(this.modelConfig.toolbarMenuButtons, 'BUTTON_EXPORT_OUTPUT');

		}
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
			array.splice(indexToDelete,1);
			return array;
		}
		else return array;
	}


});
