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
  * - name (mail)
  */

Ext.ns("Sbi.execution");

Sbi.execution.DocumentExecutionPage = function(config, doc) {
	
	// apply defaults values
	config = Ext.apply({
		// no defaults
		eastPanelWidth: 300
	}, config || {});
	
	// check mandatory values
	// ...
	
		
	// declare exploited services
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
	this.services = new Array();
	this.services['getUrlForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_URL_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	// add events 20100505
    this.addEvents('beforetoolbarinit', 'beforesynchronize', 'moveprevrequest', 'loadurlfailure', 'crossnavigation', 'beforerefresh','collapse3', 'backToAdmin');

    
    this.toolbarHiddenPreference = config.toolbarHidden!== undefined ? config.toolbarHidden : false;
	this.shortcutsHiddenPreference = config.shortcutsHidden !== undefined ? config.shortcutsHidden : false;
    
    this.init(config, doc);    
    
    this.shortcutsPanel.on('applyviewpoint', this.parametersPanel.applyViewPoint, this.parametersPanel);
    this.shortcutsPanel.on('viewpointexecutionrequest', function(v) {
    	this.southPanel.collapse();
    	this.northPanel.collapse();
    	this.parametersPanel.applyViewPoint(v);
    	// save parameters into session
    	Sbi.execution.SessionParametersManager.saveStateObject(this.parametersPanel);
		Sbi.execution.SessionParametersManager.updateMementoObject(this.parametersPanel);
		this.refreshExecution();
    }, this);
    
    this.shortcutsPanel.on('subobjectexecutionrequest', function (subObjectId) {
    	this.southPanel.collapse();
    	this.northPanel.collapse();
    	// save parameters into session
    	Sbi.execution.SessionParametersManager.saveStateObject(this.parametersPanel);
		Sbi.execution.SessionParametersManager.updateMementoObject(this.parametersPanel);
		this.executionInstance.SBI_SUBOBJECT_ID = subObjectId;
		var formState = this.parametersPanel.getFormState();
		var formStateStr = Sbi.commons.JSON.encode( formState );
		this.executionInstance.PARAMETERS = formStateStr;
		this.synchronize(this.executionInstance, false);
	}, this);
	
    this.shortcutsPanel.on('snapshotexcutionrequest', function (snapshotId) {
    	this.southPanel.collapse();
    	this.northPanel.collapse();
		this.executionInstance.SBI_SNAPSHOT_ID = snapshotId;
		this.synchronize(this.executionInstance, false);
	}, this);
	
	this.shortcutsPanel.on('subobjectshowmetadatarequest', function (subObjectId) {
    	 var win_metadata = new Sbi.execution.toolbar.MetadataWindow({'OBJECT_ID': this.executionInstance.OBJECT_ID, 'SUBOBJECT_ID': subObjectId});
		 win_metadata.show();
	}, this);
	
	if(this.toolbar){
		this.toolbar.on('beforerefresh', function (formState) {
			this.fireEvent('beforerefresh', this, this.executionInstance, formState);
		}, this);
		
		this.toolbar.on('beforetoolbarinit', function () {
			this.fireEvent('beforetoolbarinit', this, this.toolbar);
		}, this);
		
		this.toolbar.on('moveprevrequest', function () {
			this.fireEvent('moveprevrequest');
		}, this);
		
		this.toolbar.on('backToAdmin', function () {
			this.fireEvent('backToAdmin');
		}, this);
		
		this.toolbar.on('collapse3', function () {
			this.fireEvent('collapse3');
		}, this);
		
		this.toolbar.on('refreshexecution', function () {
			this.refreshExecution();
		}, this);
	}
    
	var items = [this.miframe, this.southPanel];
	if (config.hideParametersPanel === undefined || config.hideParametersPanel === false) {
		items.push(this.northPanel);
	}
	
	var id = Ext.id();
	
	var c = Ext.apply({}, config, {
		id: 'documentexecutionpage'+id
		, layout: 'border'
		, tbar: this.toolbar
		, items: items
	});	    
	
	// constructor
    Sbi.execution.DocumentExecutionPage.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.execution.DocumentExecutionPage, Ext.Panel, {
    
    // static contents and methods definitions
	services: null
	, executionInstance: null
	
	, toolbar: null
	, miframe : null
	, parametersPanel: null
    , shortcutsPanel: null
    , southPanel: null
    , northPanel: null
    , loadMask: null
   
	// ----------------------------------------------------------------------------------------
	// public methods
	// ----------------------------------------------------------------------------------------

    , synchronize: function( executionInstance, synchronizeSliders ) {
		
		if(this.fireEvent('beforesynchronize', this, executionInstance, this.executionInstance) !== false){
			this.executionInstance = executionInstance;
			if(this.toolbar){
				this.toolbar.synchronizeToolbar( executionInstance,this.miframe,this.southPanel,this.northPanel,this.parametersPanel,this.shortcutsPanel);
			}
			if(synchronizeSliders === undefined || synchronizeSliders === true) {

				this.parametersPanel.synchronize(executionInstance);
				this.shortcutsPanel.synchronize(executionInstance);
			}
			
			Ext.Ajax.request({
		        url: this.services['getUrlForExecutionService'],
		        params: executionInstance,
		        success: function(response, options) {
		      		if(response !== undefined && response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content !== undefined) {
		      				if(content.errors !== undefined && content.errors.length > 0) {
		      					this.fireEvent('loadurlfailure', content.errors);
		      				} else {
		      					this.miframe.getFrame().setSrc( content.url );
		      					if(this.toolbar){
		      						this.toolbar.updateFrame(this.miframe);
		      					}
		      				}
		      			} 
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
		   });
			
			
		}
	}

	, refreshExecution: function() {
		var formState = this.parametersPanel.getFormState();
		var formStateStr = Sbi.commons.JSON.encode( formState );
		
		if(this.fireEvent('beforerefresh', this, this.executionInstance, formState) !== false){
			/*
			 * better synchronizing with server anyway, also in case parameters' values were not changed,
			 * since SpagoBI session may have expired: is this case, it is better to re-authenticate user instead 
			 * of calling the engine directly
			 */
			this.executionInstance.PARAMETERS = formStateStr;
			this.synchronize( this.executionInstance, false )
		}
	}
	// ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------
	 
	, init: function( config, doc ) {
		this.initToolbar(config);
		this.initEastPanel(config);
		this.initCenterPanel(config, doc);
		this.initSouthPanel(config, doc);
	}
	
	, initToolbar: function( config ) {
		
        this.toolbarConfig = config.executionToolbarConfig || {} ;
		
		if (this.toolbarHiddenPreference) 
			return;
			
		/*this.toolbar = new Ext.Toolbar({
			items: ['']
		});*/
		var c = {TOOLBAR_CONFIG: this.toolbarConfig};
		this.toolbar = new Sbi.execution.toolbar.DocumentExecutionPageToolbar(c);
		this.toolbar.on('render', function() {}, this);
		this.toolbar.on('showmask', this.showMask, this);
	}
	
	, initEastPanel: function( config ) {
		Ext.apply(config, {pageNumber: 3, parentPanel: this}); // this let the ParametersPanel know that it is on execution page
		this.parametersPanel = new Sbi.execution.ParametersPanel(config);
		this.parametersPanel.on('synchronize', function() {
			// restore memento (= the list of last N value inputed for each parameters)
			Sbi.execution.SessionParametersManager.restoreMementoObject(this.parametersPanel);
		}, this);
		
		if(this.parametersPanel && this.parametersPanel.width){
			this.eastPanelWidth = this.parametersPanel.width;
		}
		
		this.northPanel = new Ext.Panel({
				region:'east'
				, title: LN('sbi.execution.parametersselection.parameters')
				, border: true
				, frame: false
				, collapsible: true
				, collapsed: true
				//, hideCollapseTool: true
				//, titleCollapse: true
				//, collapseMode: 'mini'
				//, split: true
				, autoScroll: true
				, width: this.eastPanelWidth
				, layout: 'fit'
				, items: [this.parametersPanel]
		});
		
		// fix the bug with the width of option box in combo. Without this fix when the combobox
		// is rendered on an hided panel the width of the option box is far less than the with of
		// the input field
		this.northPanel.on('expand', function() {
			//alert('expand');
			for(p in this.parametersPanel.fields) {
				var aField = this.parametersPanel.fields[p];
				//alert(p + '  : ' + aField.toSource());
				if(aField.xtype == 'combo' && !aField.isHacked && aField.el){
					var box = aField.getSize();
					aField.setWidth(box.width-1); // wont apply same width :)
					aField.setWidth(box.width);
					aField.isHacked = true;
					//alert(p + + ' HACKED !');
				}
			}
		}, this);
	}
	
	, initCenterPanel: function( config, doc ) {
		this.miframe = new Ext.ux.ManagedIframePanel({
			region:'center'
	        , frameConfig : {
				// setting an initial iframe height in IE, to fix resize problem
				autoCreate : Ext.isIE ? {style: 'height:500'} : { },
				disableMessaging : false
	        }
			, defaultSrc: 'about:blank'
	        , loadMask  : true
	        //, fitToParent: true  // not valid in a layout
	        , disableMessaging :false
	        , listeners: {
	        		
	        	'message:subobjectsaved': {
	        		fn: function(srcFrame, message) {
			        	// call metadata open window
						//this.shortcutsPanel.synchronizeSubobjectsAndOpenMetadata(message.data.id, message.data.meta, this.executionInstance);
	        			if(message.data.id != null && message.data.id){
	        				this.shortcutsPanel.synchronizeSubobjectsAndOpenMetadata(message.data.id, this.executionInstance);
	        			}    
	        		}
	        		, scope: this
	        	},
			
				'message:contentexported': {
	        		fn: function(srcFrame, message) {
	        	    	if (this.loadMask != null) {
	        	    		this.hideMask();
	        	    	}  
	        		}
	        		, scope: this
	        	},
			
				'message:worksheetexporttaberror': {
							
	        		fn: function(srcFrame, message) {
	        	    	if (this.loadMask != null) {
	        	    		this.hideMask();
	        	    	}  
	        			Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.worksheet.export.previewtab.msg'), LN('sbi.worksheet.export.previewtab.title'));
	        		}
	        		, scope: this
	        	}
	        
			
	        	, 'message:crossnavigation' : {
	        		fn: function(srcFrame, message){
	                	var config = {
	                		document: {'label': message.data.label}
	            			, preferences: {
	                			parameters: message.data.parameters
	                		  , subobject: {'name': message.data.subobject}
	                		}
	            	    };
	            	    if(message.data.target !== undefined){
	            	    	config.target = message.data.target;
	            	    }
	            	    
	            	    if(message.data.title !== undefined){
	            	    	config.title = message.data.title;
	            	    }
	            	    
	            	    if(message.data.width !== undefined){
	            	    	config.width = message.data.width;
	            	    }
	            	    
	            	    if(message.data.height !== undefined){
	            	    	config.height = message.data.height;
	            	    }
	            	    
	                	// workaround for document composition with a svg map on IE: when clicking on the map, this message is thrown
	                	// but we must invoke execCrossNavigation defined for document composition, only if it's not an external cross navigation
	                	if (Ext.isIE && this.executionInstance.document.typeCode == 'DOCUMENT_COMPOSITE') {	         
	                		if (message.data.typeCross !== undefined && message.data.typeCross === "EXTERNAL"){
	                			this.fireEvent('crossnavigation', config);
	                		}
	                		else {
	                			srcFrame.dom.contentWindow.execCrossNavigation(message.data.windowName, message.data.label, message.data.parameters);
	                		}
	                	} else {
	                		this.fireEvent('crossnavigation', config);
	                	}
	        		}
	        		, scope: this
	            }
				
				, domready : function(frame) {

						//Only for OLAP Documents
						if (this.executionInstance != null &&  this.executionInstance.document.typeCode == 'OLAP') {
							//intercept click on <input> elements and show load  mask
							frame.getDoc().on('click',function(){ frame.showMask() },this,     {delegate:'input[type=image]'});
						}
						
						//intercept click and extend SpagoBI session
						frame.getDoc().on('click', this.extendSession, this);

                }
				
				, documentloaded : function(frame){
                    frame.hideMask();
                }
				
                , scope: this				
				
	        }
	    });
		
		if(doc.refreshSeconds !== undefined && doc.refreshSeconds > 0){
			this.refr = function(seconds) {
						this.miframe.getFrame().setSrc( null ); // refresh the iframe with the latest url
						this.refr.defer(seconds*1000, this,[seconds]);
					}
			this.refr.defer(doc.refreshSeconds*1000, this,[doc.refreshSeconds]);
		}
		
		
		this.miframe.on('documentloaded', function() {
			if (this.miframe.iframe.getDocumentURI() !== 'about:blank'){
				if (this.miframe.iframe.execScript){
					this.miframe.iframe.execScript("parent = document;", true);
				}
	  		}
			var scriptFn = 	"parent.execCrossNavigation = function(d,l,p,s,ti,t) {" +
							"	sendMessage({'label': l, parameters: p, windowName: d, subobject: s, target: t, title: ti},'crossnavigation');" +
							"};";
			this.miframe.iframe.execScript(scriptFn, true);
			this.miframe.iframe.execScript("uiType = 'ext';", true);
			
			// iframe resize when iframe content is reloaded
			if (Ext.isIE) {
				var aFrame = this.miframe.getFrame();
				aFrame.dom.style.height = this.miframe.getSize().height - 6;
			}

		}, this);
		
		this.miframe.on('resize', function() {
			if (Ext.isIE) {
				var aFrame = this.miframe.getFrame();
				// work-around: during cross navigation to a third document, this.miframe.getSize().height is 0
				// therefore this check is necessary in order to avoid height less than 0, that causes side effects in IE
				if (this.miframe.getSize().height > 6) {
					aFrame.dom.style.height = this.miframe.getSize().height - 6;
				}
			}
		}, this);
	}
	
	, initSouthPanel: function( config, doc ) {
		this.shortcutsPanel = new Sbi.execution.ShortcutsPanel(config, doc);
		
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
			, collapsed: true
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
	}
	
	, extendSession: function() {
		Sbi.commons.Session.extend();
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(message){
    	if (this.loadMask == null) {
    		this.loadMask = new Ext.LoadMask(this.getId(), {msg: message});
    	}
    	this.loadMask.show();
    }
	
	/**
	 * Closes the loading mask
	 */
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
    	}
	}
	
});
