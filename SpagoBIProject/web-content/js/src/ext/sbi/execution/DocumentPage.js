/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.ns("Sbi.execution");

Sbi.execution.DocumentPage = function(config, doc) {
	
	// init properties...
	var defaultSettings = {
		// set default values here
		eastPanelWidth: 300
	};
	
	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.documentpage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.documentpage);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
		
	// add events
    this.addEvents('beforesynchronize', 'loadurlfailure', 'crossnavigation');

	// declare exploited services
	this.initServices();
	this.init(config, doc);    
   
	var c = Ext.apply({}, config, {
		id: 'documentexecutionpage' + Ext.id()
		, layout: 'border'
		, items: [this.miframe]
	});
	
	// constructor
    Sbi.execution.DocumentPage.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.execution.DocumentPage
 * @extends Ext.Panel
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.execution.DocumentPage, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
		
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
    // NOTE: the following methods initialize the interface with empty widgets. There are not yet a specific execution 
    // instance to work on. The interface itself can change then when synchronization methods
    // are invoked passing in a specific execution instance.
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - getUrlForExecutionService: get the execution url (by default GET_URL_FOR_EXECUTION_ACTION)
	 */
	, initServices: function() {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		this.services = new Array();
		this.services['getUrlForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_URL_FOR_EXECUTION_ACTION'
			, baseParams: params
		});
	}


	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function( config, doc ) {
		this.initMiframe(config, doc);
	}
	
	
	/**
	 * @method 
	 * 
	 * Initialize the iframe
	 */
	, initMiframe: function( config, doc ) {
		var listeners = this.initMiframeListeners();
		
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
	        , listeners: listeners
	    });
		
		if(doc.refreshSeconds !== undefined && doc.refreshSeconds > 0){
			this.refr = function(seconds) {
						this.miframe.getFrame().setSrc( null ); // refresh the iframe with the latest url
						this.refr.defer(seconds*1000, this,[seconds]);
					}
			this.refr.defer(doc.refreshSeconds*1000, this,[doc.refreshSeconds]);
		}	
	}
	
	/**
	 * @method 
	 * 
	 * Initialize iframe's listeners
	 */
	, initMiframeListeners: function() {
		var listeners = {
			scope: this	
		};

		listeners['message:subobjectsaved'] = this.initSubObjectSavedMessageListner();
		listeners['message:contentexported'] = this.initContentExportedMessageListner();
    	listeners['message:worksheetexporttaberror'] = this.initWorksheetExportTabErrorMessageListner();
		listeners['message:crossnavigation'] = this.initCrossNavigationaMessageListner();
		
		listeners['domready'] = this.initDomReadyListner();
		listeners['documentloaded'] = this.initDocumentLoadedListner();
        listeners['resize'] = this.initResizeListner();
	
		return listeners;
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'message:subobjectsaved'
	 */
	, initSubObjectSavedMessageListner: function() {
		return {
    		fn: function(srcFrame, message) {
	        	// call metadata open window
    			if(message.data.id != null && message.data.id){
    				//this.shortcutsPanel.synchronizeSubobjectsAndOpenMetadata(message.data.id, this.executionInstance);
    				alert("Saved subobject");
    			}    
    		}
    		, scope: this
    	};
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'message:contentexported'
	 */
	, initContentExportedMessageListner: function() {
		return {
	    	fn: function(srcFrame, message) {
	        	if (this.loadMask != null) {
	        		this.hideMask();
	        	}  
	    	}
	    	, scope: this
	    };
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'message:worksheetexporttaberror'
	 */
	, initWorksheetExportTabErrorMessageListner: function() {
		return {
	    	fn: function(srcFrame, message) {
	        	if (this.loadMask != null) {
	        		this.hideMask();
	        	}  
	    	}
	    	, scope: this
	    };
	}
	
	
	/**
	 * @method
	 * 
	 * init the listner for event 'message:crossnavigation'
	 */
	, initCrossNavigationaMessageListner: function() {
		return {
	    	fn: function(srcFrame, message){
	    		Sbi.trace('[DocumentPage.listeners(message:crossnavigation)]: IN');
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
	           	Sbi.trace('[DocumentPage.listeners(message:crossnavigation)]: OUT');
	    	}
	    	, scope: this
		};
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'domready'
	 */
	, initDomReadyListner: function() {
		return {
			fn: function(srcFrame, message) {
		

				//Only for OLAP Documents
				if (this.executionInstance != null &&  this.executionInstance.document.typeCode == 'OLAP') {
					//intercept click on <input> elements and show load  mask
					srcFrame.getDoc().on('click',function(){ frame.showMask() },this,     {delegate:'input[type=image]'});
				}
				
				//intercept click and extend SpagoBI session
				srcFrame.getDoc().on('click', this.extendSession, this);
			}
			, scope: this
		};
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'documentloaded'
	 */
	, initDocumentLoadedListner: function(frame) {
		return {
			fn: function(frame) {
				
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
				
				frame.hideMask();
				
			}
		    , scope: this
	   };
	}
	
	/**
	 * @method
	 * 
	 * init the listner for event 'resize'
	 */
	, initResizeListner: function() {
		return {
			fn: function(frame) {
				if (Ext.isIE) {
					var aFrame = this.miframe.getFrame();
					// work-around: during cross navigation to a third document, this.miframe.getSize().height is 0
					// therefore this check is necessary in order to avoid height less than 0, that causes side effects in IE
					if (this.miframe.getSize().height > 6) {
						aFrame.dom.style.height = this.miframe.getSize().height - 6;
					}
				}
			}
		    , scope: this
	   };
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // synchronization methods
	// -----------------------------------------------------------------------------------------------------------------
	// This methods change properly the interface according to the specific execution instance passed in
	
	/**
	 * Called by Sbi.execution.ExecutionWizard when a new document execution starts. Force
	 * the parameters' panel, the shorcuts' panel and toolbar re-synchronization. 
	 * 
	* @param {Object} executionInstance the execution configuration
	* 
	 * @method
	 */
	, synchronize: function( executionInstance ) {
		
		Sbi.debug('[DocumentPage.synchronize] : IN' );
		
		if(this.fireEvent('beforesynchronize', this, executionInstance, this.executionInstance) !== false){
			this.executionInstance = executionInstance;
		
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
		      				}
		      			} 
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
		   });
			
			Sbi.debug('[DocumentPage.synchronize] : OUT' );
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Extend session (keep alive)
	 */
	, extendSession: function() {
		Sbi.commons.Session.extend();
	}
	
	/**
	 * @method 
	 * 
	 * Opens the loading mask 
	 */
    , showMask : function(message){
    	if (this.loadMask == null) {
    		this.loadMask = new Ext.LoadMask(this.getId(), {msg: message});
    	}
    	this.loadMask.show();
    }
	
	/**
	 * @method 
	 * 
	 * Closes the loading mask
	 * 
	 */
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
    	}
	}
	
	
	
	/**
	 * @method
	 * 
	 * @return {Ext.ux.ManagedIframePanel} the miframe tha contains the executed document
	 */
	, getMiFrame: function() {
		return this.miframe;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
	//this.addEvents(
	/**
     * @event eventone
     * Fired when ...
     * @param {Sbi.execution.DocumentPage} this
     * @param {Ext.Toolbar} ...
     */
	//'eventone'
	/**
     * @event eventtwo
     * Fired before ...
     * @param {Sbi.execution.DocumentPage} this
     * @param {Object} ...
     */
	//'eventtwo'
	//);	
});