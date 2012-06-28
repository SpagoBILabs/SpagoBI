/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */


Ext.ns("Sbi.execution");

Sbi.execution.ParametersSelectionPage = function(config, doc) {
	
	var c = Ext.apply({
		//columnNo: 3
		//, labelAlign: 'left'
		//maskOnRender: false
	}, config || {});
	
	this.isFromCross = config.isFromCross || false;

	this.maskOnRender = c.maskOnRender;
	
	// variables for preferences and for shortcuts/parameters panel synchronization
	this.isParameterPanelReady = false; // parameters panel has been loaded
	this.isParameterPanelReadyForExecution = false; // parameters panel has been loaded and there are no parameters to be filled
	this.isSubobjectPanelReady = false;
	this.preferenceSubobjectId = (c.subobject !== undefined && c.subobject.id !== undefined) ? c.subobject.id : null;
	this.isSnapshotPanelReady = false;
	this.preferenceSnapshotId = null;
	
	// always declare exploited services first!
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
	this.services = new Array();
	this.services['saveViewpointService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SAVE_VIEWPOINT_ACTION'
		, baseParams: params
	});
	 
    this.addEvents('beforetoolbarinit'
    			  , 'beforesynchronize'
    			  , 'synchronize'
    			  , 'synchronizeexception'
    			  , 'movenextrequest'
    			  , 'moveprevrequest'
    			  , 'collapse3'
    			  , 'backToAdmin');	
	
    this.shortcutsHiddenPreference = config.shortcutsHidden !== undefined ? config.shortcutsHidden : false;
    
	this.init(c, doc);
	
	this.centerPanel = new Ext.Panel({
		region:'center'
		    , border: false
		    , frame: false
		    , collapsible: false
		    , collapsed: false
		    , hideCollapseTool: true
		    , titleCollapse: true
		    , collapseMode: 'mini'
		    , split: true
		    , autoScroll: true
		    , layout: 'fit'
		    , items: [this.parametersPanel]
		});
	
	var shortcutsHidden = (!Sbi.user.functionalities.contains('SeeViewpointsFunctionality') 
							&& !Sbi.user.functionalities.contains('SeeSnapshotsFunctionality') 
							&& !Sbi.user.functionalities.contains('SeeSubobjectsFunctionality'))
							||
							this.shortcutsHiddenPreference;
	
	var southPanelHeight = 
		(Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.shortcutsPanel && Sbi.settings.execution.shortcutsPanel.height) 
		? Sbi.settings.execution.shortcutsPanel.height : 280;
	
	this.southPanel = new Ext.Panel({
		region:'south'
			, border: false
			, frame: false
			, collapsible: true
			, collapsed: false
			, hideCollapseTool: true
			, titleCollapse: true
			, collapseMode: 'mini'
			, split: true
			, autoScroll: true
			, height: southPanelHeight
			, layout: 'fit'
			, items: [this.shortcutsPanel]
			, hidden: shortcutsHidden
	});
	
	c = Ext.apply({}, c, {
		layout: 'fit',
		tbar: this.toolbar,
		//autoScroll : true,
		items: [{
			layout: 'border',
			listeners: {
			    'render': {
	            	fn: function() {
	            		if (!this.loadingMask) {
	            			this.loadingMask = new Sbi.decorator.LoadMask(this.body, {
	            				msg:LN('sbi.execution.parametersselection.loadingmsg')
	            			});
	            		}
	            		this.loadingMask.hide(); /*
	            								this is a workaround (work-around): when executing a document from administration tree or
	            								from menu, this loading mask does not appear. Invoking hide() solve the issue.
	            		 						*/
	          	 		if(this.maskOnRender === true) this.loadingMask.show();
	            	},
	            	scope: this
	          	}
	        },   	        
			items: [this.centerPanel, this.southPanel]
		}]
	});   
	
	this.parametersPanel.on('synchronize', 
		function(panel, readyForExecution, parametersPreference) {
			this.isParameterPanelReady = true;
			if (readyForExecution) {
				this.isParameterPanelReadyForExecution = true;
			}
			// try to find from session the value used for execution
			Sbi.execution.SessionParametersManager.restoreStateObject(panel);
			// restore memento (= the list of last N value inputed for each parameters)
			Sbi.execution.SessionParametersManager.restoreMementoObject(panel);
			this.checkAutomaticStart();
		}
	, this);

	this.shortcutsPanel.on('applyviewpoint', this.parametersPanel.applyViewPoint, this.parametersPanel);
	this.shortcutsPanel.on('viewpointexecutionrequest', this.onExecuteViewpoint, this);
	this.shortcutsPanel.on('subobjectexecutionrequest', this.onExecuteSubobject, this);
	this.shortcutsPanel.on('snapshotexcutionrequest', this.onExecuteSnapshot, this);
	this.shortcutsPanel.on('subobjectshowmetadatarequest', function (subObjectId) {
    	 var win_metadata = new Sbi.execution.toolbar.MetadataWindow({'OBJECT_ID': this.executionInstance.OBJECT_ID, 'SUBOBJECT_ID': subObjectId});
		 win_metadata.show();
	}, this);
	
	this.shortcutsPanel.subobjectsPanel.on('ready', function(){
		this.isSubobjectPanelReady = true;
		//if (preferenceSubobjectId !== undefined && preferenceSubobjectId != null) {
		//	this.preferenceSubobjectId = preferenceSubobjectId;
		//}
		this.checkAutomaticStart();
	}, this);
	
	this.shortcutsPanel.snapshotsPanel.on('ready', function(preferenceSnapshotId){
		this.isSnapshotPanelReady = true;
		this.preferenceSnapshotId = preferenceSnapshotId;
		this.checkAutomaticStart();
	}, this);
	
	// constructor
    Sbi.execution.ParametersSelectionPage.superclass.constructor.call(this, c);
    
    
};

Ext.extend(Sbi.execution.ParametersSelectionPage, Ext.Panel, {
    
	services: null
	, executionInstance: null
	
	, toolbar: null
	
    , parametersPanel: null
    , shortcutsPanel: null
  
    , saveViewpointWin: null
    
    , loadingMask: null
    , maskOnRender: null
   
    // ----------------------------------------------------------------------------------------
    // public methods
    // ----------------------------------------------------------------------------------------
    
   
    , synchronize: function( executionInstance ) {
		if(this.fireEvent('beforesynchronize', this, executionInstance, this.executionInstance) !== false){
			this.executionInstance = executionInstance;
			this.synchronizeToolbar( executionInstance );
			
			this.parametersPanelSynchronizationPending = true;
			this.parametersPanel.synchronize( this.executionInstance );
			
			this.shortcutsPanelSynchronizationPending = true;
			this.shortcutsPanel.synchronize( this.executionInstance );
		}
	}

	, synchronizeToolbar: function( executionInstance ){
		
		this.toolbar.items.each( function(item) {
			this.toolbar.items.remove(item);
            item.destroy();           
        }, this); 
		
		this.fireEvent('beforetoolbarinit', this, this.toolbar);
		
		this.toolbar.addFill();
	
		var drawRoleBack = false;
		
		if (executionInstance.isPossibleToComeBackToRolePage == undefined || executionInstance.isPossibleToComeBackToRolePage === true) {
			this.toolbar.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-back' 
				, tooltip: LN('sbi.execution.parametersselection.toolbar.back')
			    , scope: this
			    , handler : function() {this.fireEvent('moveprevrequest');}
			}));
			
			this.toolbar.addSeparator();
		
			drawRoleBack = true;
		}
	
				// 20100505
		if (this.callFromTreeListDoc == true && drawRoleBack == false) {
			this.toolbar.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-back' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.documentView')
				, scope: this
				, handler : function() {
					this.fireEvent('backToAdmin');
				}
			}));
		}
		
		
		
		
		if(Sbi.user.ismodeweb){
			this.toolbar.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-expand' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.expand')
			    , scope: this
			    , handler : function() {
					this.fireEvent('collapse3');
				}			
			}));
			
			this.toolbar.addSeparator();
		}
		
		this.toolbar.addButton(new Ext.Toolbar.Button({
			iconCls: 'icon-clear'
			, tooltip: LN('sbi.execution.parametersselection.toolbar.clear')
		   	, scope: this
		   	, handler : function() {
				this.clearParametersForm();
			}
		}));
		
		if (Sbi.user.functionalities.contains('SeeViewpointsFunctionality') && !this.isFromCross) {
			this.toolbar.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-save'
				, tooltip: LN('sbi.execution.parametersselection.toolbar.save')
			   	, scope: this
			   	, handler : function() {
					this.saveParametersFormStateAsViewpoint();
				}
			}));
		}

		// if document is QBE datamart and user is a Read-only user, he cannot execute main document, but only saved queries.
		// If there is a subobject preference, the execution button starts the subobject execution
		if (
				executionInstance.document.typeCode != 'DATAMART' || 
				(
					Sbi.user.functionalities.contains('BuildQbeQueriesFunctionality') || 
					(this.preferenceSubobjectId !== undefined && this.preferenceSubobjectId !== null)
				)
			) {
			this.toolbar.addSeparator();
			this.toolbar.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-execute'
				, tooltip: LN('sbi.execution.parametersselection.toolbar.next')
				, scope: this
				, handler : function() {
					if (this.preferenceSubobjectId !== undefined && this.preferenceSubobjectId !== null) {
						this.executionInstance.SBI_SUBOBJECT_ID = this.preferenceSubobjectId;
					}
					this.fireEvent('movenextrequest');
				}
			}));
		}
	}

	, clearParametersForm: function() {
		this.parametersPanel.reset();
	}
	
	, saveParametersFormStateAsViewpoint: function() {
		if(this.saveViewpointWin === null) {
			this.saveViewpointWin = new Sbi.widgets.SaveWindow();
			this.saveViewpointWin.on('save', function(w, state) {
				var params = Ext.apply({}, state, this.executionInstance);
				var formState = this.parametersPanel.getFormState();
				for(var p in formState) {
					if(formState[p] instanceof Array ) {
						formState[p] = formState[p].join(';');
					}
				}
				params.viewpoint = Sbi.commons.JSON.encode( formState );
				Ext.Ajax.request({
			          url: this.services['saveViewpointService'],
			          
			          params: params,
			          
			          callback : function(options, success, response){
						if(success && response !== undefined) {   
				      		if(response.responseText !== undefined) {
				      			var content = Ext.util.JSON.decode( response.responseText );
				      			if(content !== undefined) {
				      				Ext.MessageBox.show({
					      				title: 'Status',
					      				msg: LN('sbi.execution.viewpoints.msg.saved'),
					      				modal: false,
					      				buttons: Ext.MessageBox.OK,
					      				width:300,
					      				icon: Ext.MessageBox.INFO 			
					      			});
				      				this.shortcutsPanel.viewpointsPanel.addViewpoints(content);
				      			} 
				      		} else {
				      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				      		}
			    	   }			    	  	
			          },
			          scope: this,
			  		  failure: Sbi.exception.ExceptionHandler.handleFailure      
			     });
			}, this);
		}
		this.saveViewpointWin.show();
	}
	
	
    
	
	// ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------
	
	, init: function( config, doc) {
		this.initToolbar(config);
		this.initParametersPanel(config);
		this.initShortcutsPanel(config, doc);
	}
	
	, initToolbar: function( config ) {
		this.toolbar = new Ext.Toolbar({
			items: [new Ext.Toolbar.Button({iconCls: 'icon-back'})]
		});
	}
	
	, initParametersPanel: function( config ) {
		Ext.apply(config, {pageNumber: 2}); // this let the ParametersPanel know that it is on parameters selection page
		if(this.isFromCross == true) {
			
		}
		
		config.isFromCross = this.isFromCross;
		this.parametersPanel = new Sbi.execution.ParametersPanel(config);
		
		this.parametersPanel.on('beforesynchronize', function() {
			if (!this.loadingMask) {
				this.loadingMask = new Sbi.decorator.LoadMask(this.body, {
					msg:LN('sbi.execution.parametersselection.loadingmsg')
				}); 
			}
			this.loadingMask.hide(); /*
									this is a workaround (work-around): when executing a document from administration tree or
									from menu, this loading mask does not appear. Invoking hide() solve the issue.
									 */
			this.loadingMask.show();
		}, this);
		
		this.parametersPanel.on('synchronize', function() {
			if(this.shortcutsPanelSynchronizationPending === false) {
				this.fireEvent('synchronize', this);
			}
			this.parametersPanelSynchronizationPending = false;
		}, this)
		return this.parametersPanel;
	}
	
	, initShortcutsPanel: function( config, doc ) {
		this.shortcutsPanel = new Sbi.execution.ShortcutsPanel(config, doc);
		this.shortcutsPanel.on('synchronize', function() {
			if(this.parametersPanelSynchronizationPending === false) {
				this.fireEvent('synchronize', this);
			}
			this.shortcutsPanelSynchronizationPending = false;
		}, this)
		return this.shortcutsPanel;
	}
	
	, checkAutomaticStart: function() {
		
		// must wait parameters/subobjects/snapshots panels have been loaded
		if (this.isSubobjectPanelReady === false || this.isSnapshotPanelReady === false || this.isParameterPanelReady === false) {
			return;
		}
		
		if(this.loadingMask) this.loadingMask.hide();
		
		// subobject preference wins: if a subobject preference is specified, subobject is executed
		if (this.preferenceSubobjectId != null) {
			// if document is datamart type and there are some parameters to be filled, subobject execution cannot start automatically
			if (this.executionInstance.document.typeCode != 'DATAMART' || this.isParameterPanelReadyForExecution === true) {
				this.executionInstance.isPossibleToComeBackToParametersPage = false;
				this.onExecuteSubobject(this.preferenceSubobjectId);
			}
			return;
		}
		// snapshot preference follows: if a snapshot preference is specified, snapshot is executed
		if (this.preferenceSnapshotId != null) {
			this.executionInstance.isPossibleToComeBackToParametersPage = false;
			this.onExecuteSnapshot(this.preferenceSnapshotId);
			return;
		}
		// parameters form follows: if there are no parameters to be filled, start main document execution
		if (this.isParameterPanelReadyForExecution == true) {
			this.executionInstance.isPossibleToComeBackToParametersPage = false;
			this.fireEvent('movenextrequest', this);
		}
	}
	
	, onExecuteViewpoint: function(v) {
		this.parametersPanel.applyViewPoint(v);
		this.fireEvent('movenextrequest');
	}
	
	, onExecuteSubobject: function (subObjectId) {
		this.executionInstance.SBI_SUBOBJECT_ID = subObjectId;
		this.fireEvent('movenextrequest');
	}
	
	, onExecuteSnapshot: function (snapshotId) {
		this.executionInstance.SBI_SNAPSHOT_ID = snapshotId;
		this.fireEvent('movenextrequest');
	}
	
});