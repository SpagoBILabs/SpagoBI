/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.execution.toolbar");

Sbi.execution.toolbar.DocumentExecutionPageToolbar = function(config) {	
	
	// init properties...
	var defaultSettings = {
		// public
		documentMode: 'INFO'
		, expandBtnVisible: true
		, callFromTreeListDoc: false
		// private
	};

	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.toolbar && Sbi.settings.execution.toolbar.documentexecutionpagetoolbar) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.toolbar.documentexecutionpagetoolbar);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	this.addEvents('beforeinit', 'click', 'showmask');
	this.initServices();
	this.init();
	
	Sbi.execution.toolbar.DocumentExecutionPageToolbar.superclass.constructor.call(this, c);
	
	
};

/**
 * @class Sbi.execution.toolbar.DocumentExecutionPageToolbar
 * @extends Ext.Toolbar
 * 
 * The toolbar used by DocumentExecutionPage. The content change accordingly to document's visualization mode (i.e. INFO,
 * VIEW, EDIT).
 */

/**
 * @cfg {Object} config Configuration object.
 * @cfg {Number} [config.documentMode=0]
 * @cfg {Number} [config.expandBtnVisible=1]
 * @cfg {Number} [config.callFromTreeListDoc=2]
 */
Ext.extend(Sbi.execution.toolbar.DocumentExecutionPageToolbar, Ext.Toolbar, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
	
	/**
     * @property {String} documentMode
     * Define which facet of the current documents shown to the user. The content of the toolbar change accordingly. 
     * There are three possibilities mode:
     * - INFO: shows document's metadata and shortcuts 
     * - VIEW: show the executed document
     * - EDIT: show the document in edit mode
     * 
     * The default is INFO
     */ 
	, documentMode: null
	
	/**
     * @property {Boolean} expandBtnVisible
     * True if expand button is visible, false otherwise. 
     * 
     * The default is true.
     */ 
	, expandBtnVisible: null
	
	/**
     * @property {Object} controller
     * The controller object. Must implement the following methods:
     *  - refreshDocument()
     *  - executeDocument(Object executionInstance)
     *  - showInfo()
     *  - openSubobjectSelectionWin()
     *  - openSnapshotSelectionWin()
     *  - getFrame()
     *  - getParameterValues()
     */ 
	, controller: null
	
	/**
     * @property {Object} callFromTreeListDoc
     * Specify if the document has been executed from document browser or from analytical documents'tree (i.e. old admin GUI)
     * 
     * The default is false.
     */ 
	, callFromTreeListDoc: false
	
	
	, executionInstance: null
	
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method
	 * 
	 * @return {String} return the url of the executed document. Null if #controller is null or #controller not implements
	 * methods <code>getFrame</code>. Note: this method is called by the Sbi.execution.toolbar.ExportersMenu in order to create
	 * the exportation url
	 */
	, getDocumentUrl: function() {
		var url = null;
		if(this.controller && this.controller.getFrame) {
			var frame = this.controller.getFrame();
		    url = frame.getDocumentURI();
		}
		return url;		
	}

	/**
	 * @method
	 * 
	 * @return {String} return the window tha conatins the executed document. Null if #controller is null or #controller not implements
	 * methods <code>getFrame</code>. Note: this method is called by the Sbi.execution.toolbar.ExportersMenu in order to create
	 * the exportation url
	 */
	, getDocumentWindow: function() {
		var window = null;
		if(this.controller && this.controller.getFrame) {
			var frame = this.controller.getFrame();
		    window = frame.getWindow();
		}
		return window;		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - showSendToForm: ... (by default SHOW_SEND_TO_FORM)
	 *    - saveIntoPersonalFolder: ... (by default SAVE_PERSONAL_FOLDER)
	 *    - getNotesService: ... (by default GET_NOTES_ACTION)
	 *    - updateDocumentService: ... (by default SAVE_DOCUMENT_ACTION)
	 *    
	 */
	, initServices: function() {
	
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = this.services || new Array();
		
		this.services['showSendToForm'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SHOW_SEND_TO_FORM'
			, baseParams: params
		});
		
		this.services['saveIntoPersonalFolder'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_PERSONAL_FOLDER'
			, baseParams: params
		});
		
//		this.services['toChartPdf'] = Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXPORT_CHART_PDF'
//			, baseParams: params
//		});
		
//		this.services['toChartJpg'] = Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXPORT_CHART_JPG'
//			, baseParams: params
//		});
		
//		this.services['exportDataStore'] = Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXPORT_RESULT_ACTION'
//			, baseParams: params
//		});
		
		this.services['getNotesService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_NOTES_ACTION'
			, baseParams: params
		});
	
		
		var updateDocParams = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: 'DOC_UPDATE'};
		this.services['updateDocumentService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_DOCUMENT_ACTION'
			, baseParams: updateDocParams
		});
	}
	
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: Ext.emptyFn
	
	// -----------------------------------------------------------------------------------------------------------------
    // synchronization methods
	// -----------------------------------------------------------------------------------------------------------------
	, synchronize: function(controller, executionInstance) {
		Sbi.trace('[DocumentExecutionPageToolbar.synchronize]: IN');
		this.controller = controller;
	    this.executionInstance = executionInstance;
	
		// if toolbar is hidden, do nothing
		if (this.toolbarHiddenPreference) return;
		
		this.removeAllButtons();
		
		this.fireEvent('beforeinit', this);
		
		this.addFill();
		
		Sbi.trace('[DocumentExecutionPageToolbar.synchronize]: Document mode is equal to [' + this.documentMode + ']');
		if (this.documentMode === 'INFO') {
			this.addButtonsForInfoMode();
		} else if (this.documentMode === 'VIEW') {
			this.addButtonsForViewMode();
		} else {
			this.addButtonsForEditMode();
		}
		Sbi.trace('[DocumentExecutionPageToolbar.synchronize]: OUT');
   }
	
	// -----------------------------------------------------------------------------------------------------------------
	// edit methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, removeAllButtons: function() {
		this.items.each( function(item) {
			this.items.remove(item);
            item.destroy();           
        }, this); 
	}
	
	, addButtonsForInfoMode: function () {
		Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForInfoMode]: IN');
		
		var drawRoleBack = false;
		
		if (this.executionInstance.isPossibleToComeBackToRolePage == undefined || this.executionInstance.isPossibleToComeBackToRolePage === true) {
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-back' 
				, tooltip: LN('sbi.execution.parametersselection.toolbar.back')
			    , scope: this
			    , handler : function() {
			    	this.fireEvent('click', this, "backToRolePage");
			    }
			}));
			this.toolbar.addSeparator();
			drawRoleBack = true;
		}
		
		// 20100505
		if (this.callFromTreeListDoc == true && drawRoleBack == false) {
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-back' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.documentView')
				, scope: this
				, handler : function() {
					this.fireEvent('click', this, "backToAdminPage");
				}
			}));
		}
		
		if(Sbi.user.ismodeweb){
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-expand' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.expand')
			    , scope: this
			    , handler : function() {
			    	this.fireEvent('click', this, 'expand');
				}			
			}));
		}
		


		// if document is QBE datamart and user is a Read-only user, he cannot execute main document, but only saved queries.
		// If there is a subobject preference, the execution button starts the subobject execution
		if (
				this.executionInstance.document.typeCode != 'DATAMART' || 
				(
					Sbi.user.functionalities.contains('BuildQbeQueriesFunctionality') || 
					(this.preferenceSubobjectId !== undefined && this.preferenceSubobjectId !== null)
				)
			) {
			this.addSeparator();
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-execute'
				, tooltip: LN('sbi.execution.parametersselection.toolbar.next')
				, scope: this
				, handler : function() {
					if (this.preferenceSubobjectId !== undefined && this.preferenceSubobjectId !== null) {
						this.executionInstance.SBI_SUBOBJECT_ID = this.preferenceSubobjectId;
						this.controller.refreshDocument();
					} else {
						this.controller.executeDocument(this.executionInstance);
					}
				}
			}));
		}
		
		Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForInfoMode]: OUT');
	}
	
	// edit mode buttons (at the moment used by Worksheet documents only)
	, addButtonsForEditMode: function () {

		Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForEditMode]: IN');
		
		   if (Sbi.user.ismodeweb && this.expandBtnVisible  === true) {
			   this.addButton(new Ext.Toolbar.Button({
				   iconCls: 'icon-expand' 
					   , tooltip: LN('sbi.execution.executionpage.toolbar.expand')
					   , scope: this
					   , handler : function() {
						   this.fireEvent('click', this, 'expand');
					   }			
			   }));
		   }
		   
		   if (this.executionInstance.document && this.executionInstance.document.decorators &&  this.executionInstance.document.decorators.isSavable) {
			   this.addButton(new Ext.Toolbar.Button({
				   iconCls: 'icon-save' 
					   , tooltip: LN('sbi.execution.executionpage.toolbar.save')
					   , scope: this
					   , handler : this.saveWorksheet
			   }));
		   }

		   this.addButton(new Ext.Toolbar.Button({
			   iconCls: 'icon-saveas' 
				   , tooltip: LN('sbi.execution.executionpage.toolbar.saveas')
				   , scope: this
				   , handler : this.saveWorksheetAs	
		   }));

		   if(this.executionInstance.document.exporters.length > 0){
				
			   this.exportMenu = new Ext.menu.Menu({
				   id: 'basicExportMenu_0',
				   items: this.getWorksheetExportMenuItems(),
				   listeners: {	'mouseexit': {fn: function(item) {item.hide();}}}
			   });
		       this.addButton(new Ext.Toolbar.MenuButton({
				   id: Ext.id()
				   , tooltip: 'Exporters'
				   , path: 'Exporters'	
				   , iconCls: 'icon-export' 	
				   , menu: this.exportMenu
				   , width: 15
				   , cls: 'x-btn-menubutton x-btn-text-icon bmenu '
			    }));	
		   }
		   
		   this.addSeparator();
		   this.addButton(new Ext.Toolbar.Button({
			   iconCls: 'icon-view' 
				   , tooltip: LN('sbi.execution.executionpage.toolbar.view')
				   , scope: this
				   , handler : this.stopWorksheetEditing	
		   }));
		   
		   Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForEditMode]: OUT');
			
	   }
	   
	   
	   , addButtonsForViewMode: function () {
		   
		   Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForViewMode]: IN');
			
		  
		   // BACK TO ADMIN PAGE
		   if (this.callFromTreeListDoc == true) {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-back' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.documentView')
					, scope: this
					, handler : function() {
						this.fireEvent('click', this, "backToAdminPage");
					}
				}));
		   }
		   
		   // EXPAND DOCUMENT
		   if(Sbi.user.ismodeweb && this.expandBtnVisible  === true){
			   this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-expand' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.expand')
				    , scope: this
				    , handler : function() {
				    	this.fireEvent('click', this, 'expand');
					}			
				}));
			}
	    	
		   
		   
	    	this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-execute-subobject' 
				, tooltip: LN('Execute subobject')
			    , scope: this
			    , handler : function() {
			    	this.controller.openSubobjectSelectionWin();
			    }
			}));
			
	    	
	    	this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-execute-snapshot' 
				, tooltip: LN('Execute snapshot')
				, scope: this
			    , handler : function() {
			    	this.controller.openSnapshotSelectionWin();
			    }
			}));
			
			
			if (Sbi.user.functionalities.contains('EditWorksheetFunctionality') && this.executionInstance.document.typeCode === 'WORKSHEET') {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-edit' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.edit')
				    , scope: this
				    , handler : this.startWorksheetEditing	
				}));
			}
			
			if (this.executionInstance.document.typeCode === 'DATAMART') {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-save' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.save')
				    , scope: this
				    , handler : this.saveQbe	
				}));
			}
			
			if (this.executionInstance.document.typeCode === 'SMART_FILTER') {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-save' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.save')
				    , scope: this
				    , handler : this.saveWorksheetAs	
				}));
			}
			
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-rating' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.rating')
			    , scope: this
			    , handler : this.rateExecution	
			}));
			
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-print' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.print')
			    , scope: this
			    , handler : this.printExecution
			}));
			
			if (Sbi.user.functionalities.contains('SendMailFunctionality') && !this.executionInstance.SBI_SNAPSHOT_ID
					&& this.executionInstance.document.typeCode == 'REPORT') {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-sendMail' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.send')
			     	, scope: this
			    	, handler : this.sendExecution
				}));
			}
			
			if (Sbi.user.functionalities.contains('SaveIntoFolderFunctionality') && !this.executionInstance.SBI_SNAPSHOT_ID) {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-saveIntoPersonalFolder' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.saveintopersonalfolder')
			     	, scope: this
			    	, handler : this.saveExecution
				}));
			}

			if (Sbi.user.functionalities.contains('SaveRememberMeFunctionality') && !this.executionInstance.SBI_SNAPSHOT_ID) {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-saveRememberMe'
					, tooltip: LN('sbi.execution.executionpage.toolbar.bookmark')
			     	, scope: this
			    	, handler :this.bookmarkExecution
				}));
			}
			
			if (Sbi.user.functionalities.contains('SeeNotesFunctionality') && !this.executionInstance.SBI_SNAPSHOT_ID) {
				this.getNoteIcon();
		    	this.addButton(new Ext.Toolbar.Button({
		  			   id: 'noteIcon'
		  				, tooltip: LN('sbi.execution.executionpage.toolbar.annotate')
		  				, iconCls: 'icon-no-notes'
		  		     	, scope: this
		  		    	, handler : this.annotateExecution
		  			}));    
			}
			
			if (Sbi.user.functionalities.contains('SeeMetadataFunctionality') && !this.executionInstance.SBI_SNAPSHOT_ID) {
				this.addButton(new Ext.Toolbar.Button({
					iconCls: 'icon-metadata' 
					, tooltip: LN('sbi.execution.executionpage.toolbar.metadata')
			     	, scope: this
			    	, handler : this.metaExecution
				}));
			}
			
			this.initExportersMenu();
			
			
			this.addSeparator();
			
			this.addButton(new Ext.Toolbar.Button({
				iconCls: 'icon-execute' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.refresh')
			    , scope: this
			    , handler : function() {
						// save parameters into session
						// if type is QBE inform user that will lose configurations
						if(this.executionInstance.document.typeCode == 'DATAMART'){
							if(Sbi.user.functionalities.contains('BuildQbeQueriesFunctionality') && Sbi.user.functionalities.contains('SaveSubobjectFunctionality')){
								
								
								Ext.MessageBox.confirm(
	    						    LN('sbi.generic.warning'),
	            					LN('sbi.execution.executionpage.toolbar.qberefresh'),            
	            					function(btn, text) {
	                					if (btn=='yes') {
											this.controller.refreshDocument();
	                					}
	            					},
	            					this
									);
								}else{
									//user who cannot build qbe queries
									this.controller.refreshDocument();
								}
						} // it 's not a qbe
						else {
							this.controller.refreshDocument();
					}
				}			
			}));
			
			Sbi.trace('[DocumentExecutionPageToolbar.addButtonsForViewMode]: OUT');	   
	}
	
	   
	   
	/**
	 * @method
	 * 
	 * Create the exporters' menu. The content of the menu depends on the exportation formats
	 * supported by the specific document type
	 */   
    , initExportersMenu: function() {
			
		var exporters = this.executionInstance.document.exporters;
		
		if(!exporters){
			return;
		}
		
		var menu = new Sbi.execution.toolbar.ExportersMenu({
			exporters: this.executionInstance.document.exporters
			, documentType: this.executionInstance.document.typeCode
			, toolbar: this
			, executionInstance: this.executionInstance
		});
		this.add(menu);
			
//		var menu = null;
//					
//		var documentType = this.executionInstance.document.typeCode;
//		if (  documentType == 'KPI' ) {
//			menu = this.initKpiExportersMenu();
//		} else if ( documentType == 'DOCUMENT_COMPOSITE' ) {
//			menu = this.initDocumentCompositeExportersMenu();
//		} else if( documentType == 'REPORT') {	
//			menu = this.initReportExportersMenu();
//		} else if( documentType == 'OLAP') {
//			menu = this.initOlapExportersMenu();
//		} else if ( documentType == 'DASH') {
//			menu = this.initDashExportersMenu();
//		} else if ( documentType == 'CHART') {			
//			menu = this.initChartExportersMenu();
//		} else if ( documentType == 'NETWORK') {
//			menu =  this.initNetworkExportersMenu();
//		} else if ( documentType == 'WORKSHEET') {
//			menu = this.initWorksheetExportersMenu();
//		} else if ( documentType == 'DATAMART' || documentType == 'SMART_FILTER' ) {
//			menu = this.initQbeExportersMenu();
//		} else if ( documentType == 'MAP') {
//			menu = this.initGeoExportersMenu();
//		}	
//	
//		if(menu != null) this.add(menu);
	}	   
	   
	   
	, baseMenuItemConfig: {
		text: LN('sbi.execution.GenericExport')
		, group: 'group_2'//ok, where's group_1?
		, iconCls: 'icon-pdf'  // use a generic icon here
		, scope: this
		, width: 15
		, handler : Ext.emptyFn
		, href: ''   
	}   
	
	, baseMenuConfig: {
		tooltip: 'Exporters'
		, path: 'Exporters'	
		, iconCls: 'icon-export' 	
		, width: 15
		, cls: 'x-btn-menubutton x-btn-text-icon bmenu '
	}
	
	, createMenuButton: function(menuItems) {
		var menuButton = null;
		if(menuItems && menuItems.length > 0) {
			var menu = new Ext.menu.Menu({
				items: menuItems    
			});				
			menuButton = new Ext.Toolbar.MenuButton(Ext.apply(this.baseMenuConfig, {menu: menu}));
		}
		
		return menuButton;	
	}
	
	
	

	
	   
	
	   
	
	
	

	
	
	
	
	
	
	
	   
	// -----------------------------------------------------------------------------------------------------------------
	// private methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, getNoteIcon: function () {
  		Ext.Ajax.request({
  	        url: this.services['getNotesService'],
  	        params: {SBI_EXECUTION_ID: this.executionInstance.SBI_EXECUTION_ID, MESSAGE: 'GET_LIST_NOTES'},
  	        success : function(response, options) {
	      		if(response !== undefined && response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );		 
	      			//checks if documents has some note for change icon     			
	      			if (content !== undefined && content.totalCount > 0) {		      		
	      				var el = Ext.getCmp('noteIcon');                
	      				el.setIconClass('icon-notes');
	      			}
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
  	        },
  	        scope: this,
  			failure: Sbi.exception.ExceptionHandler.handleFailure      
  		});
	}
	
	, rateExecution: function() {
		this.win_rating = new Sbi.execution.toolbar.RatingWindow({'OBJECT_ID': this.executionInstance.OBJECT_ID});
		this.win_rating.show();
	}
	
	, printExecution: function() {
		this.controller.getFrame().print();
	}
	
	, sendExecution: function () {
		var sendToIframeUrl = this.services['showSendToForm'] 
		        + '&objlabel=' + this.executionInstance.OBJECT_LABEL
		        + '&objid=' + this.executionInstance.OBJECT_ID
				+ '&' + Sbi.commons.Format.toStringOldSyntax(this.controller.getParameterValues());
		this.win_sendTo = new Sbi.execution.toolbar.SendToWindow({'url': sendToIframeUrl});
		this.win_sendTo.show();
	}
	
	, saveExecution: function () {
		Ext.Ajax.request({
	          url: this.services['saveIntoPersonalFolder'],
	          params: {documentId: this.executionInstance.OBJECT_ID},
	          success: function(response, options) {
		      		if (response.responseText !== undefined) {
		      			var responseText = response.responseText;
		      			var iconSaveToPF;
		      			var message;
		      			if (responseText=="sbi.execution.stpf.ok") {
		      				message = LN('sbi.execution.stpf.ok');
		      				iconSaveToPF = Ext.MessageBox.INFO;
		      			}
		      			if (responseText=="sbi.execution.stpf.alreadyPresent") {
		      				message = LN('sbi.execution.stpf.alreadyPresent');
		      				iconSaveToPF = Ext.MessageBox.WARNING;
		      			}
		      			if (responseText=="sbi.execution.stpf.error") {
		      				message = LN('sbi.execution.stpf.error');
		      				iconSaveToPF = Ext.MessageBox.ERROR;
		      			}
	
		      			var messageBox = Ext.MessageBox.show({
		      				title: 'Status',
		      				msg: message,
		      				modal: false,
		      				buttons: Ext.MessageBox.OK,
		      				width:300,
		      				icon: iconSaveToPF,
		      				animEl: 'root-menu'        			
		      			});
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
	          },
	          scope: this,
	  		  failure: Sbi.exception.ExceptionHandler.handleFailure      
	     });
	}
	
	, bookmarkExecution: function () {
		this.win_saveRememberMe = new Sbi.execution.toolbar.SaveRememberMeWindow({'SBI_EXECUTION_ID': this.executionInstance.SBI_EXECUTION_ID});
		this.win_saveRememberMe.show();
	}
	, annotateExecution: function () {
		this.win_notes = new Sbi.execution.toolbar.ListNotesWindow({'SBI_EXECUTION_ID': this.executionInstance.SBI_EXECUTION_ID});
		this.win_notes.show();
	}
	
	, metaExecution: function () {
		var subObjectId = this.executionInstance.SBI_SUBOBJECT_ID;
		if(subObjectId !== undefined){
			this.win_metadata = new Sbi.execution.toolbar.MetadataWindow({'OBJECT_ID': this.executionInstance.OBJECT_ID, 'SUBOBJECT_ID': subObjectId});
		}else{
			this.win_metadata = new Sbi.execution.toolbar.MetadataWindow({'OBJECT_ID': this.executionInstance.OBJECT_ID});
		}	
		this.win_metadata.show();
	}
	
	
   
   , startWorksheetEditing: function() {
	   this.documentMode = 'EDIT';
	   this.synchronize(this.controller, this.executionInstance);
	   var newUrl = this.changeDocumentExecutionUrlParameter('ACTION_NAME', 'WORKSHEET_START_EDIT_ACTION');
	   this.controller.getFrame().setSrc(newUrl);
   }
   
   , saveWorksheet: function () {
		var templateJSON = this.getWorksheetTemplateAsJSONObject();
		var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
		var query = templateJSON.OBJECT_QUERY;
		var formValues = templateJSON.OBJECT_FORM_VALUES;
		var params = this.executionInstance;
				
		if(wkDefinition!=null){
			params = Ext.apply(params, {'wk_definition': Ext.util.JSON.encode(wkDefinition)});
		}
		if(query!=null){
			params = Ext.apply(params, {'query': Ext.util.JSON.encode(query)});
		}
		if(formValues!=null){//the values of the smart filter
			params = Ext.apply(params, {'formValues': Ext.util.JSON.encode(formValues)});
		}

		Ext.Ajax.request({
	        url: this.services['updateDocumentService'],
	        params: params,
	        success : function(response, options) {
	      		if(response !== undefined && response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if (content.text !== 'Operation succeded' && content.responseText !== 'Operation succeded') {
	                    Ext.MessageBox.show({
	                        title: LN('sbi.generic.error'),
	                        msg: content,
	                        width: 150,
	                        buttons: Ext.MessageBox.OK
	                   });              
		      		} else {			      			
		      			Ext.MessageBox.show({
	                        title: LN('sbi.generic.result'),
	                        msg: LN('sbi.generic.resultMsg'),
	                        width: 200,
	                        buttons: Ext.MessageBox.OK
		                });
		      		}  
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
	        },
	        scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
   }
   
   , getWorksheetTemplateAsString: function() {
		try {
			var thePanel = null;
			if(this.executionInstance.document.typeCode == 'WORKSHEET'){
				//the worksheet has been constructed starting from a qbe document
				thePanel = this.controller.getFrame().getWindow().qbe;
				if(thePanel==null){
					//the worksheet has been constructed starting from a smart filter document
					thePanel = this.controller.getFrame().getWindow().Sbi.formviewer.formEnginePanel;
				}
				if(thePanel==null){
					//the worksheet is alone with out the qbe
					thePanel = this.controller.getFrame().getWindow().workSheetPanel;
				}
			}else if(this.executionInstance.document.typeCode == 'DATAMART'){
				thePanel = this.controller.getFrame().getWindow().qbe;
			}else if(this.executionInstance.document.typeCode == 'SMART_FILTER'){
				thePanel = this.controller.getFrame().getWindow().Sbi.formviewer.formEnginePanel;
			}else{
				alert('Sorry, cannot perform operation. Invalid engine..');
				return null;
			}
			//var template = thePanel.getWorksheetTemplateAsString();
			var template = thePanel.validate();	
			return template;
		} catch (err) {
			throw err;
		}
   }
   
   , getWorksheetTemplateAsJSONObject: function() {
		var template = this.getWorksheetTemplateAsString();
		if(template==null){
			return null;
		}
		var templateJSON = Ext.util.JSON.decode(template);
		return templateJSON;
  }
   
   , saveWorksheetAs: function () {
		var templateJSON = this.getWorksheetTemplateAsJSONObject();
		if(templateJSON==null){
			// if it is null validation error has been already showed in QbePanel
			//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.worksheet.validation.error.text'),LN('sbi.worksheet.validation.error.title'));
		}else{

			var documentWindowsParams = this.getSaveDocumentWindowsParams(templateJSON);
			this.win_saveDoc = new Sbi.execution.SaveDocumentWindow(documentWindowsParams);
			this.win_saveDoc.show();
		}
   }
   
   , getSaveDocumentWindowsParams: function(templateJSON){
		var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
		var params = {
				'OBJECT_ID': this.executionInstance.OBJECT_ID,
				'OBJECT_TYPE': 'WORKSHEET',
				'OBJECT_WK_DEFINITION': wkDefinition,
				'OBJECT_DATA_SOURCE': this.executionInstance.document.datasource
			};
		if(this.executionInstance.document.typeCode == 'DATAMART' || this.executionInstance.document.typeCode == 'WORKSHEET'){
			params.OBJECT_QUERY = templateJSON.OBJECT_QUERY;
		}else if(this.executionInstance.document.typeCode == 'SMART_FILTER'){
			params.OBJECT_FORM_VALUES=templateJSON.OBJECT_FORM_VALUES;
			params = Ext.apply(this.executionInstance, params);
		}
		return params;
   }
   
   , stopWorksheetEditing: function() {
	   this.documentMode = 'VIEW';
	   this.synchronize(this.controller, this.executionInstance);
	   var newUrl = this.changeDocumentExecutionUrlParameter('ACTION_NAME', 'WORKSHEET_ENGINE_START_ACTION');
	   this.controller.getFrame().setSrc(newUrl);
   }
   
   , changeDocumentExecutionUrlParameter: function(parameterName, parameterValue) {
		var frame = this.controller.getFrame();
	    var docurl = frame.getDocumentURI();
	    var startIndex = docurl.indexOf('?')+1;
	    var endIndex = docurl.length;
	    var baseUrl = docurl.substring(0, startIndex);
	    var docurlPar = docurl.substring(startIndex, endIndex);
	    
	    docurlPar = docurlPar.replace(/\+/g, " ");
	    var parurl = Ext.urlDecode(docurlPar);
	    parurl[parameterName] = parameterValue;
	    parurl = Ext.urlEncode(parurl);
	    var endUrl = baseUrl +parurl;
	    return endUrl;
   }
	 
	 , saveQbe: function () {
		try {
			if (!Sbi.user.functionalities.contains('BuildQbeQueriesFunctionality')) {
				// If user is not a Qbe power user, he can only save worksheet
				this.saveWorksheetAs();
			} else {
				// If the user is a Qbe power user, he can save both current query and worksheet definition.
				// We must get the current active tab in order to understand what must be saved.
				var qbeWindow = this.controller.getFrame().getWindow();
				var qbePanel = qbeWindow.qbe;
				var anActiveTab = qbePanel.tabs.getActiveTab();
				var activeTabId = anActiveTab.getId();
				var isBuildingWorksheet = (activeTabId === 'WorksheetDesignerPanel' || activeTabId === 'WorkSheetPreviewPage');
				if (isBuildingWorksheet) {
					// save worksheet as document
					this.saveWorksheetAs();
				} else {
					// save query as customized view
					qbePanel.queryEditorPanel.showSaveQueryWindow();
				}
			}
		} catch (err) {
			alert('Sorry, cannot perform operation.');
			throw err;
		}
   }
	 
	 
	 
	
	 
		// =================================================================================================================
		// EVENTS
		// =================================================================================================================
	 	
	 	//this.addEvents(
	 	/**
	     * @event beforeinit
	     * Fired when the toolbar is re-initiated (i.e. just after all buttons have been removed and before specific buttons 
		 * for the current documentMode are added. This event can be used to inject custom buttons in the toolbar. In 
		 * Sbi.execution.ExecutionPanel it is used to inject breadcrumbs buttons on the left part.
		 * 
	     * @param {Sbi.execution.toolbar.DocumentExecutionPageToolbar} this
	     */
	 	//  'beforeinit'
	 	/**
	     * @event click
	     * Fired when the user click on a button of the toolbar
		 * 
	     * @param {Sbi.execution.toolbar.DocumentExecutionPageToolbar} this
	     * @param {String} action 
	     */
	    //);
	 
	 
	 
});