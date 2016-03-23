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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.DownloadLogsWindow = function(config) {
	/**
	 * example of action calling:
	 * http://localhost:8080/SpagoBI/servlet/AdapterHTTP?ACTION_NAME=DOWNLOAD_ZIP&DIRECTORY=C:/logs&BEGIN_DATE=01/03&END_DATE=30/04&BEGIN_TIME=14:00&END_TIME=15:00
	 */
	
	var defaultSettings = Ext.apply({}, config || {}, {
		title: 'Download windows'
		, width: 500
		, height: 300
		, hasBuddy: false		
	});
	
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.downloadLogsWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.downloadLogsWindow);
	}
		
	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
		
	this.initFormPanel(c.options);	

	this.closeButton = new Ext.Button({
		text: LN('sbi.console.downloadlogs.btnClose'),
		handler: function(){
        	//this.hide();
        	this.close();
        }
        , scope: this
	});
	
	this.downloadButton = new Ext.Button({
		text: LN('sbi.console.downloadlogs.btnDownload')
		, handler: function() {
			//check parameters
			if (!this.checkParameters()) { return; }			
			
			
        	this.fireEvent('checked', this, this.target);
        	//this.hide();
			this.close();
        }
        , scope: this
	});
	

	c = Ext.apply(c, {  	
		layout: 'fit'
	//,	closeAction:'hide'
	,	closeAction:'close'
	,	constrain: true
	,	plain: true
	,	modal:true
	,	title: this.title
	,	buttonAlign : 'center'
	,	buttons: [this.closeButton, this.downloadButton]
	,	items: [this.formPanel]
	});

	// constructor
	Sbi.console.DownloadLogsWindow.superclass.constructor.call(this, c);
	
	this.addEvents('checked');
    
};

Ext.extend(Sbi.console.DownloadLogsWindow, Ext.Window, {

    serviceName: null
    , formPanel: null
    , initialDate: null
    , finalDate: null
    , initialTime: null
    , finalTime: null
    , paths: null
    

    // this is the object uppon witch the window has been opened, usually a record
    , target: null
    
   , closeButton: null
   , downloadButton: null
    
    // public methods
   , downloadLogs: function(action, r, index, params) {
		
		//by unique request
		var form = document.getElementById('download-form');
		if(!form) {
			var dh = Ext.DomHelper;
			form = dh.append(Ext.getBody(), {
			    id: 'download-form'
			    , tag: 'form'
			    , method: 'post'
			    , cls: 'download-form'
			});
		}
		//call by ajax for test correct file
		params = Ext.apply(params, {
  			message: action.name, 
        	userId: Sbi.user.userId ,
        	BEGIN_DATE: this.initialDate.value,
        	END_DATE: this.finalDate.value,
        	BEGIN_TIME: this.initialTime.value,
        	END_TIME: this.finalTime.value,
        //	PREFIX1: (params.PREFIX1 !== undefined && params.PREFIX1 !== null)?params.PREFIX1:"",
        //	PREFIX2: (params.PREFIX2 !== undefined && params.PREFIX2 !== null)?params.PREFIX2:"",
        	DIRECTORY: (this.paths !== null)?this.paths.value:params.DIRECTORY
        	//DIRECTORY: params.DIRECTORY
  		}); 

  		Ext.Ajax.request({
	       	url: params.URL			       
	       	, params: params 			       
	    	, success: function(response, options) {
	    		if(response !== undefined && response.responseText !== undefined) {
	    				var path = (this.paths === null)? params.DIRECTORY : this.paths.value;
	    				
						//call by submit to download really 
	    				var actionStr  = params.URL +  								 
									  '&DIRECTORY=' + path + 
									  '&BEGIN_DATE=' + this.initialDate.value + '&END_DATE=' + this.finalDate.value + 
									  '&BEGIN_TIME=' + this.initialTime.value + '&END_TIME=' + this.finalTime.value;
			 
						if (params.PREFIX1 !== undefined && params.PREFIX1 !== null){
							actionStr += '&PREFIX1=' + params.PREFIX1;
						}
						if (params.PREFIX2 !== undefined && params.PREFIX2 !== null){
							actionStr += '&PREFIX2=' + params.PREFIX2;
						}								
						form.action = actionStr;
						form.submit();
										      		
    			} else {
    				Sbi.Msg.showError('Server response is empty', 'Service Error');
    			}
	    	}
	    	, failure: Sbi.exception.ExceptionHandler.onServiceRequestFailure
	    	, scope: this     
	    });
		//call by submit to download really
  		/*
		form.action = params.URL +  '&PREFIX=' + params.PREFIX +  '&DIRECTORY=' + params.DIRECTORY + 
					  '&BEGIN_DATE=' + this.initialDate.value + '&END_DATE=' + this.finalDate.value + 
					  '&BEGIN_TIME=' + this.initialTime.value + '&END_TIME=' + this.finalTime.value;
		form.submit();
	*/
	}

    
    // private methods

    , initFormPanel: function(options) {
    	
    	var elements = [];
    	
    	this.initialDate = new Ext.form.DateField({
            fieldLabel: LN('sbi.console.downloadlogs.initialDate') 
          , width: 150
          , format: 'd/m'
          , allowBlank: false
        });
    	
    	elements.push(this.initialDate);
    	
    	this.initialTime = new Ext.form.TimeField({
    		 					 fieldLabel: LN('sbi.console.downloadlogs.initialTime') 
    		 				   , width: 150
    						   , increment: 30
    						   , format: 'H:i'
    						});
    	 
    	elements.push(this.initialTime);
    			
    	this.finalDate = new Ext.form.DateField({
            fieldLabel: LN('sbi.console.downloadlogs.finalDate')            			   
          , width: 150
          , format: 'd/m'
          , allowBlank: false
        });
    	elements.push(this.finalDate);
    	
    	this.finalTime = new Ext.form.TimeField({
					    	   fieldLabel: LN('sbi.console.downloadlogs.finalTime') 
					    	 , width: 150
							 , increment: 30
							 , format: 'H:i'
    						});
    	elements.push(this.finalTime);
    			
    	//adds a combo with all paths defined into template (ONLY if they are more than one!)
    	var directories = options.staticParams.DIRECTORY;
    	
    	if (Ext.isArray(directories) && directories.length > 1){
    		var data = [];
        	var store = new Ext.data.JsonStore({
				   fields:['name', 'value'],
		           data: []
			   });
    		for(var p = 0, len = directories.length; p < len; p++) {
    			 var row = {
    					  name: directories[p]
    				    , value: directories[p]
    				   };
    			 data.push(row);
    				   
    		}
    		
    		store.loadData(data, false);
			   
		     var combDefaultConfig = {
				   width: 350,
			       displayField:'name',
			       valueField:'value',
			       fieldLabel: LN('sbi.console.downloadlogs.path') ,
			       typeAhead: true,
			       triggerAction: 'all',
			       emptyText:'',
			       //selectOnFocus:true,
			       selectOnFocus:false,
			       validateOnBlur: false,
			       allowBlank:false,
			       mode: 'local'
		     };
			 
    		 this.paths = new Ext.form.ComboBox(
    				 Ext.apply(combDefaultConfig, {	    
			    	   store: store
    				})
  			 );	
    		 
    		 elements.push(this.paths);
    	}else{
    		this.paths = null;
    	}
    	
    	
    	
    	this.formPanel = new  Ext.FormPanel({
    		  title:  LN('sbi.console.downloadlogs.title'),
    		  margins: '50 50 50 50',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
	          width: 850,
	          height: 600,
	          layout: 'form',
	          trackResetOnLoad: true,
	          //items: [this.initialDate, this.initialTime, this.finalDate, this.finalTime, this.paths]
	          items: elements
	      });
    	 
    }
    
    , checkParameters: function(){
    	if (this.initialDate.getValue() === undefined ||  this.initialDate.getValue() === ''){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.initialDateMandatory'));
			this.initialDate.focus();
			return false;
		}
		if (this.initialTime.getValue() === undefined ||  this.initialTime.getValue() === ''){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.initialTimeMandatory'));
			this.initialTime.focus();
			return false;
		}
		if (this.finalDate.getValue() === undefined ||  this.finalDate.getValue() === ''){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.finalDateMandatory'));
			this.finalDate.focus();
			return false;
		}
		if (this.finalTime.getValue() === undefined ||  this.finalTime.getValue() === ''){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.finalTimeMandatory'));
			this.finalTime.focus();
			return false;
		}
		if (this.initialDate.getValue() > this.finalDate.getValue()){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.rangeInvalid'));
			this.initialDate.focus();
			return false;
		}

		if (this.paths !== null && (this.paths.getValue()  === undefined ||  this.paths.getValue() === '')){
			Sbi.Msg.showWarning( LN('sbi.console.downloadlogs.pathsMandatory'));
			this.paths.focus();
			return false;
		}
		return true;
    }
    
});