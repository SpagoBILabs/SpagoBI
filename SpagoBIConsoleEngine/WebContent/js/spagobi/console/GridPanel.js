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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */
 
Ext.ns("Sbi.console");

Sbi.console.GridPanel = function(config) {

		var defaultSettings = {
			layout: 'fit'
			, loadMask: false
		    , viewConfig: {
	          	forceFit:false,
	           	autoFill: true,
	           	enableRowBody:true,
	           	showPreview:true
	        }
			, start: 0
			, limit: 5
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.gridPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.gridPanel);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		
		c.storeId = c.dataset;
		c.storeLabelsId = c.datasetLabels;
		delete c.dataset;	
		var tableConfig = c.table || {};
		var filterConfig =  c.filterBar || {};
		filterConfig.executionContext = c.executionContext;
		filterConfig.exportName = c.exportName;
		Ext.apply(this, c);
		
		this.initServices();
		this.initStore();		
		this.initColumnModel();
		this.initSelectionModel();	
		this.initFilterBar(filterConfig);
		this.initPagingBar();
		
		Ext.QuickTips.init() ;
		Ext.apply(Ext.QuickTips.getQuickTip(), {
		    maxWidth: 200,
		    minWidth: 100,
		    showDelay: 50,
		    dismissDelay: 0,
		    closable: true,
		 //   title: 'Valore',
		    trackMouse: true
		});
		
		if (this.store !== undefined){
			this.store.pagingParams = {
				start: this.start
			  , limit: this.limit
			  , paginator: this.pagingBar
			};
		
		
			this.pagingBar.refresh.hide();
			this.pagingBar.on('change', function() {
				this.store.pagingParams.start = this.pagingBar.cursor;
			}, this);
			
			this.filterBar.on('beforefilterselect', function() {
				this.pagingBar.moveFirst();
			}, this);
			
			if(this.store.filterPlugin) {
				this.store.filterPlugin.on('filterschange', function(s, f) {
					this.pagingBar.onLoad(this.store, [], {params: this.store.pagingParams || {}});
				}, this);
			}
		}
		
		var c = Ext.apply(c, {
			store: this.store
			, cm: this.columnModel
			, sm: this.selectionModel
			, tbar: this.filterBar
			, bbar: this.pagingBar
		});   
		
		
		// constructor
		Sbi.console.GridPanel.superclass.constructor.call(this, c);
		
		this.addEvents('lock');
		this.addEvents('unlock');
		
};

Ext.extend(Sbi.console.GridPanel, Ext.grid.GridPanel, {
    
	services: null
	// grid
	, store: null
	, storeLabels: null
	, columnModelLabels: null
	, columnModel: null
	, selectionModel: null
	, tempColumnModel: null
	, headersToHide: null
	, fieldsMap: null
	, selectedRowsId: null
	, hideSelectedRow: null
	
	// popup
	, waitWin: null
	, errorWin: null
	, alarmWin: null
	, logsWin: null
	, promptWin: null
	// popup dataset label postfix (the complete name is given by <table_dataset_label><postfix> ie: 'testConsoleErrors')
	, errorDs: 'Errors'
	, alarmDs: 'Alarms'
	, loadMask: null
	
	// configuration bloks
	, filterBar: null
	, inlineCharts: null
	, inlineActions: null
    , AUTOMATIC_FILTERBAR: 'automatic'		// automatic: all dataset fields are added as filter
    , CUSTOM_FILTERBAR: 'custom'			//custom: only configurated fields are added as filter  
    , ACTIVE_VALUE: 1
	, INACTIVE_VALUE: 0
	, USER_ID: 'userId'
	
	
	
	, GRID_ACTIONS: {
		start: {serviceName: 'START_WORK', images: {active:'../img/ico_start.gif', inactive:'../img/ico_start_gray.gif'}}
		, stop: {serviceName: 'STOP_WORK', images: {active:'../img/ico_stop.gif', inactive:'../img/ico_stop_gray.gif'}}
		, informationlog: {serviceName: 'DOWNLOAD_ZIP', images: '../img/ico_download_logs.gif'}
		, crossnav: {serviceName: 'CROSS_ACTION', images: {cross_detail: '../img/ico_cross_detail.gif', popup_detail: '../img/ico_popup_detail.gif'}}
		, monitor: {serviceName: 'UPDATE_ACTION', images: {inactive: '../img/ico_monitor.gif', active: '../img/ico_monitor_gray.gif'}}
		, errors: {serviceName: 'UPDATE_ACTION', images: {active: '../img/ico_errors.gif', inactive: '../img/ico_errors_gray.gif'}} 
		, alarms: {serviceName: 'UPDATE_ACTION', images: {active: '../img/ico_alarms.gif', inactive: '../img/ico_alarms_gray.gif'}}
		, views: {serviceName: 'UPDATE_ACTION', images: {active: '../img/ico_views.gif', inactive: '../img/ico_views_gray.gif'}} 
		, refresh: {serviceName: 'REFRESH_ACTION', images: '../img/ico_refresh.gif'}
		, genericUpdate: {serviceName: 'UPDATE_ACTION'}
		, notifyStartAction: {serviceName: 'NOTIFY_START_ACTION'}
		
	}
   
    //  -- public methods ---------------------------------------------------------
    
    
	, resolveParameters: function(parameters, record, context, callback) {		
		
		if (parameters == undefined) return;
		
		var results = {};  
		var promptables;
		
		
		var staticParams = parameters.staticParams || "";
		
		results = Ext.apply(results, staticParams);
		var dynamicParams = parameters.dynamicParams || "";
		
		
	    if(dynamicParams) { 
	    	var msgErr = ""; 
	    	for (var i = 0, l = dynamicParams.length; i < l; i++) { 
	    	 	var param = dynamicParams[i];              
		              if (param.scope === 'dataset') {		            	  
			               for(p in param) {
			            	   if(p === 'scope') continue;			            	   
			                   if(record.get(this.store.getFieldNameByAlias(param[p])) === undefined) {         			                   
			                    msgErr += 'Parameter "' + param[p] + '" undefined into dataset.<p>';
					           } else {
					              results[p] = record.get(this.store.getFieldNameByAlias(param[p])); 
					           }
			               }
		              } else if (param.scope === 'env'){ 		            	  
			               for(p in param) { 
			                if(p === 'scope') continue;			              			            	  		                
			                  var tmpNamePar =  param[p];
			            	  if (p !== this.USER_ID && context[tmpNamePar] === undefined) {
			            		   msgErr += 'Parameter "' + tmpNamePar + '" undefined into request. <p>';
		                      } else {                                   
		                       results[p] = context[tmpNamePar];
		                      }
			               }
	                 } else if (param.scope === 'promptable'){   
		                  promptables = promptables || [];
		                  promptables.push(param);
	                 }	                 
	           }   
	    		//gets metadata params (linked to dynamic params)
		    	var metaParams = parameters.metaParams;
			    if(metaParams) {  
			    	results['metaParams'] = Ext.util.JSON.encode(metaParams);
			    }
	      	}
	  
        if  (msgErr != ""){
        	Sbi.Msg.showError(msgErr, 'Service Error');
        }	
       
      	//if there are some promptable field, it shows a popup for the insertion
      	if (promptables !== undefined){
    			this.promptWin = new Sbi.console.PromptablesWindow({
    				promptables: promptables	    					
    			});
    			this.promptWin.on('click', function(win, pp) {
    				Ext.apply(results, pp);
    			    callback.call (this, results);	    								
    			}, this);
      		this.promptWin.show();
      		this.promptWin.on('close', function() {
      			this.fireEvent('unlock', this);
      			}, this);
      	}
      	else {
      		callback.call (this, results);      		
      	}
	}
	
	, execCrossNav: function(actionName, record, index, options){	
		this.showMask();
		var msg = {
			label: options.document.label
	    	, windowName: this.name	||  parent.name // parent.name is used in document composition context			
	    	, target: (options.target === 'new')? 'self': 'popup'	    	
	    	, typeCross: 'EXTERNAL' //for manage correctly the IE workaround in document composition context 
	    }; 
		if (msg.target === 'popup'){
			msg.width = options.width || 500;
			msg.height = options.height || 300;
		}
		
		var callback = function(params){
			var separator = '';
			msg.parameters = '';
			for(p in params) {
				msg.parameters += separator + p + '=' + params[p];
				separator = '&';
			}
			if (this.executionContext.EXECUTION_CONTEXT !== undefined && 
					this.executionContext.EXECUTION_CONTEXT === 'DOCUMENT_COMPOSITION'){				
				//document composition context				
				if (params.typeCross !== undefined && params.typeCross == 'INTERNAL'){
					//internal cross	
					var frameName = "iframe_" + this.executionContext.DOCUMENT_LABEL;
		    		parent.execCrossNavigation(frameName, msg.label ,  msg.parameters );
		    		
				}else{
					//external cross
					parent.sendMessage(msg, 'crossnavigation');
				}
			}else{
				sendMessage(msg, 'crossnavigation');
			}
			this.hideMask.defer(2000, this);
		};
		
		this.resolveParameters(options.document, record, this.executionContext, callback);
	}

	
	, execAction: function(action, r, index, options) {	
		
		//toggles all icons of the same family
		var fields = this.getFieldsToToggle(action);
		for(var i = 0, l = fields.length; i < l; i++){ 
			var field = fields[i];
			if (field.toggle) field.toggle(r);
		}

		var callback = function(params){

			params = Ext.apply(params, {
	  			message: action.type, 
	        	userId: Sbi.user.userId 
	  		}); 
			
	  		Ext.Ajax.request({
		       	url: this.services[action.type] 			       
		       	, params: params 			       
		    	, success: function(response, options) {
		    		if(response !== undefined && response.responseText !== undefined) {
							var content = Ext.util.JSON.decode( response.responseText );
							if (content !== undefined) {				      			  
							//	alert(content.toSource());
							}				      		
	    			} else {
	    				Sbi.Msg.showError('Server response is empty', 'Service Error');
	    			}
		    	}
		    	, failure: Sbi.exception.ExceptionHandler.onServiceRequestFailure
		    	, scope: this     
		    });
		};
		this.resolveParameters(options, r, this.executionContext, callback);
    }
	
	, toggleMonitor: function(action, r, index, options) {
		//force the list refresh	                   
        this.store.filterPlugin.applyFilters();	            
        this.execAction(action, r, index, options);
	}

	, showErrors: function(action, r, index, options) {
		if(this.errorWin === null) {
			this.errorWin = new Sbi.console.MasterDetailWindow({
				serviceName: 'GET_ERROR_LIST_ACTION'
			  , action: action				
			});
			this.errorWin.on('show', function() {
      			this.fireEvent('lock', this);
      			}, this);
			this.errorWin.on('checked', function(win, record) {
				this.errorWin.action.toggle(record);
				this.execAction(this.errorWin.action, record, null, options);
				if(this.errorWin.action.isChecked(record)) {
					this.errorWin.checkButton.setText(LN('sbi.console.error.btnSetNotChecked'));
				} else {
					this.errorWin.checkButton.setText(LN('sbi.console.error.btnSetChecked'));
				}				
			}, this);
			this.errorWin.on('hide', function() {
      			this.fireEvent('unlock', this);
      			}, this);
		}
		var callback = function(params){ 
			params.ds_label = this.store.getDsLabel() + this.errorDs;
			this.errorWin.reloadMasterList(params);
			this.errorWin.setTarget(r);
			var isChecked = action.isChecked(r);
			if(isChecked) {
				this.errorWin.checkButton.setText(LN('sbi.console.error.btnSetNotChecked'));
			} else if(!isChecked){
				this.errorWin.checkButton.setText(LN('sbi.console.error.btnSetChecked'));
			}
			this.errorWin.show();
		};
		this.resolveParameters(options, r, this.executionContext, callback);
		
	}
	
	, showAlarms: function(action, r, index, options) {
		if(this.alarmWin === null) {
			this.alarmWin = new Sbi.console.MasterDetailWindow({
				serviceName: 'GET_WARNING_LIST_ACTION'
			  , action: action
			});
			this.alarmWin.on('show', function() {
      			this.fireEvent('lock', this);
      			}, this);
			this.alarmWin.on('checked', function(win, record) {
				this.alarmWin.action.toggle(record);
				this.execAction(action, record, null, options);
				if(this.alarmWin.action.isChecked(record)) {
					this.alarmWin.checkButton.setText(LN('sbi.console.error.btnSetNotChecked'));
				} else {
					this.alarmWin.checkButton.setText(LN('sbi.console.error.btnSetChecked'));
				}	
			}, this);
			this.alarmWin.on('hide', function() {
      			this.fireEvent('unlock', this);
      			}, this);
		}

		//var params = {};
		var callback = function(params){ 			
			params.ds_label = this.store.getDsLabel() + this.alarmDs;
			this.alarmWin.reloadMasterList(params);
			
			
			this.alarmWin.setTarget(r);
			if(action.isChecked(r)) {
				this.alarmWin.checkButton.setText(LN('sbi.console.error.btnSetNotChecked'));
			} else {
				this.alarmWin.checkButton.setText(LN('sbi.console.error.btnSetChecked'));
			}
			this.alarmWin.show();
		};
		this.resolveParameters(options, r, this.executionContext, callback);
		
	}
	
	, startProcess: function(action, r, index, options) {
		this.fireEvent('lock', this);
		
		if(action.isChecked(r)) {
			Sbi.Msg.showWarning('Process is already running');
			return;
		}
	
		var callback = function(params){ 	
			//split the array values in a single string
			for(p in params) { 
				var tmpPar = params[p];
				if(Ext.isArray(tmpPar)) {		
					var strValue = "";
					for(var i = 0; i < tmpPar.length; i++) {
						strValue +=	tmpPar[i]+',';
					}
					strValue = strValue.substr(0,strValue.length-1);
					params[p] = strValue;
				}	
			}
			
			params = Ext.apply(params, {
				userId: Sbi.user.userId 
	          , DOCUMENT_LABEL: options.document.label
	  		}); 
			
			if(this.waitWin === null) {
				this.waitWin = new Sbi.console.WaitWindow({});
			}
			this.waitWin.startingTxt = 'Starting process';
			this.waitWin.start();
			this.waitWin.show();
	  		Ext.Ajax.request({
		       	url: this.services[action.type] 			       
		       	, params: params 			       
		    	, success: function(response, options) {
	  				
		    		if(!response || !response.responseText) {
		    			Sbi.Msg.showError('Server response is empty', 'Service Error');
		    			return;
		    		}
	  				var content = Ext.util.JSON.decode( response.responseText );
	  				action.setBoundColumnValue(r, content.pid);
	  				//this.waitWin.stop('Proecess started succesfully');
					//action.toggle(r);	
					if (params.stmt){
						params.pid = content.pid;
						//calls the update action (if there's a stmt definition)
						Ext.Ajax.request({
					       	url: this.services['notifyStartAction'] 			       
					       	, params: params 			       
					    	, success: function(response, options) {
					    		if(response !== undefined && response.responseText !== undefined) {
										var content = Ext.util.JSON.decode( response.responseText );
										if (content !== undefined) {				      			  
										//	alert(content.toSource());
										}				      		
				    			} else {
				    				Sbi.Msg.showError('Server response is empty', 'Service Error');
				    			}
					    	}
					    	, failure: Sbi.exception.ExceptionHandler.onServiceRequestFailure
					    	, scope: this     
					    });
					}
					this.waitWin.stop('Process started succesfully');
					action.toggle(r);
		    	}
		    	, failure: function(response, options) {
		    		Sbi.exception.ExceptionHandler.onServiceRequestFailure(response, options);
		    		this.waitWin.stop('Impossible to start process');
		    	}
		    	, scope: this     
		    });
		};
		this.resolveParameters(options.document, r, this.executionContext, callback);
	}
	
	, stopProcess: function(action, r, index, options) {
		if(action.isChecked(r)) {
			Sbi.Msg.showWarning('Process is already stopped');
			return;
		}

		var callback = function(params){
			params = Ext.apply(params, {
	        	userId: Sbi.user.userId 
	          , DOCUMENT_LABEL: options.document.label
	  		}); 
			
			if(this.waitWin === null) {
				this.waitWin = new Sbi.console.WaitWindow({});
			}
			this.waitWin.startingTxt = 'Stopping process';
			this.waitWin.start();
			this.waitWin.show();
			
			Ext.Ajax.request({
		       	url: this.services[action.type] 			       
		       	, params: params 			       
		    	, success: function(response, options) {
	  				
		    		if(!response || !response.responseText) {
		    			Sbi.Msg.showError('Server response is empty', 'Service Error');
		    			return;
		    		}
	  				var content = Ext.util.JSON.decode( response.responseText );
	  				action.setBoundColumnValue(r, content.pid);
	  				this.waitWin.stop('Proecess stopped succesfully');
					action.toggle(r);
					
					if (params.stmt){						
						//calls the update action (if there's a stmt definition)
						Ext.Ajax.request({
					       	url: this.services['genericUpdate'] 			       
					       	, params: params 			       
					    	, success: function(response, options) {
					    		if(response !== undefined && response.responseText !== undefined) {
										var content = Ext.util.JSON.decode( response.responseText );
										if (content !== undefined) {				      			  
										//	alert(content.toSource());
										}				      		
				    			} else {
				    				Sbi.Msg.showError('Server response is empty', 'Service Error');
				    			}
					    	}
					    	, failure: Sbi.exception.ExceptionHandler.onServiceRequestFailure
					    	, scope: this     
					    });
					}
		    	}
		    	, failure: function(response, options) {
		    		Sbi.exception.ExceptionHandler.onServiceRequestFailure(response, options);
		    		this.waitWin.stop('Impossible to stop process');
		    	}
		    	, scope: this     
		    });
		};
		this.resolveParameters(options.document, r, this.executionContext, callback);
		
	}
	
	, downloadLogs: function(action, r, index, options) {	
		var callback = function(params){
			var url =  Sbi.config.spagobiServiceRegistry.getServiceUrl({serviceName: 'DOWNLOAD_ZIP'
				     , baseParams: new Object()
					});
			
			params = Ext.apply(params, {
				USER_ID: Sbi.user.userId 
			  , URL: url
			}); 
			
			//if(this.logsWin === null) {
				this.logsWin = new Sbi.console.DownloadLogsWindow({
				serviceName: 'DOWNLOAD_ZIP' 
				, action: action
				, options: options
				});							
			//}
			
			this.logsWin.on('checked', function(win, record) {	
				this.logsWin.downloadLogs(action, record, null, params);
				this.logsWin = null;
			}, this);
			
			this.logsWin.show();
		};
		
		this.resolveParameters(options, r, this.executionContext, callback);
		

	},
	
	updateSelectedRows: function(listRowsSelected){
		if (this.selectedRowsId == null){ 
			this.selectedRowsId = []; 
		}
		this.selectedRowsId = listRowsSelected;
	}
	
	,
	stopLoading : function () {
		this.storeManager.stopRefresh(true, this.storeId);
	}
	
	,
	restartLoading : function () {
		this.storeManager.stopRefresh(false, this.storeId);
	}
	
	
    //  -- private methods ---------------------------------------------------------
    
    
    , initServices: function() {
    	this.services = this.services || new Array();	
		this.images = this.images || new Array();	
				
		for(var actionName in this.GRID_ACTIONS) {
			var actionConf = this.GRID_ACTIONS[actionName];
			if(actionName === 'start') {
				this.services[actionName] = this.services[actionName] || Sbi.config.commonjServiceRegistry.getServiceUrl({
					serviceName: actionConf.serviceName
					, baseParams: new Object()
				});
			} else if(actionName === 'stop') {
				this.services[actionName] = this.services[actionName] || Sbi.config.commonjServiceRegistry.getServiceUrl({
					serviceName: actionConf.serviceName
					, baseParams: new Object()
				});
			}else if(actionName === 'informationlog') {
				this.services[actionName] = this.services[actionName] || Sbi.config.spagobiServiceRegistry.getServiceUrl({
					serviceName: actionConf.serviceName
					, baseParams: new Object()
				});
			} else {
				this.services[actionName] = this.services[actionName] || Sbi.config.serviceRegistry.getServiceUrl({
					serviceName: actionConf.serviceName
					, baseParams: new Object()
				});
			}
		}
    }
    
	, initStore: function() {		
		this.store = this.storeManager.getStore(this.storeId);
		if (this.store === undefined) {
			Sbi.Msg.showError('Dataset with identifier [' + this.storeId + '] is not correctly configurated');			
		}else{
			this.store.remoteSort = false;  //local type		
			this.store.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
			this.store.on('load', this.onLoad, this);
			this.store.on('metachange', this.onMetaChange, this);
		}
	}

	, initColumnModel: function() {	
		this.columnModel = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(), 
			{
				header: "Data",
			    dataIndex: 'data',
			    width: 150
			}
		]);
		this.columnModel.defaultSortable = true;  		
	}
	
	, initSelectionModel: function() {
		this.selectionModel = new Ext.grid.RowSelectionModel({
			singleSelect: false
		});
	}
	
	, initFilterBar: function(filterBarConf) {
		if (filterBarConf.type === 'default') {
			Sbi.Msg.showError('Toolbar of type [' + filterBarConf.type + '] is not yet supported');
		} else if (filterBarConf.type === this.CUSTOM_FILTERBAR || filterBarConf.type === this.AUTOMATIC_FILTERBAR) {
		    this.filterBar = new Sbi.console.CustomFilteringToolbar({filterBar: filterBarConf, store: this.store});   
		}  else {
			Sbi.Msg.showError('Toolbar of type [' + filterBarConf.type + '] is not supported');
		}
	}
	
	//redefines the renderer for inline charts.
	/*
	, updateInLineCharts: function(){

		for(var j = 0, len = this.inlineCharts.length; j < len; j++) {
			var idx = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[j].column));
			this.getColumnModel().setRenderer(idx, this.createInlineChartRenderer(this.inlineCharts[j]) );			
		}
	}*/
	
	, updateMetaStructure: function(cm, headerToHide, fieldsMap){
		for(var i = 0, len = headerToHide.length; i < len; i++) {
			//hides the column with the description of the header
			if (cm.fields[fieldsMap[headerToHide[i]]] !== undefined)
				cm.fields[fieldsMap[headerToHide[i]]].hidden = true;
		}
		//adds numeration column    
		cm.fields[0] = new Ext.grid.RowNumberer();
		//update columnmodel configuration
		this.getColumnModel().setConfig(cm.fields);
	    this.reconfigure(this.store,cm);
	}
	
	, initPagingBar: function() {
		this.pagingBar = new Ext.PagingToolbar({
            pageSize: this.limit,
            store: this.store,
            displayInfo: true
        });
	}
	
	// -- callbacks ---------------------------------------------------------------------------------------------
	//defines the max, min and tot value on all records (only for columns visualized as chart)
	, onLoad: function(){
		var numRec = this.store.getCount();
		
		//redefines the columns labels if they are dynamics
		var tmpMeta = this.getColumnModel();
		var fields = tmpMeta.config;
		var metaIsChanged = false;
		var headerToHide = [];
		var fieldsMap = {};
		tmpMeta.fields = new Array(fields.length);
	 
		for(var i = 0, len = fields.length; i < len; i++) {
			if(fields[i].headerType !== undefined && fields[i].headerType.toUpperCase() === 'DATASET'){
				//-------------------------------------------------------------------------------//
				// 	subsitutes the grid header values with the dataset header fields			 //
				//-------------------------------------------------------------------------------//
				var tmpRec = this.store.getAt(0);
				if (tmpRec !== undefined) {
			    	var tmpHeader =  tmpRec.get(this.store.getFieldNameByAlias(fields[i].header));
			    	if (tmpHeader !== undefined){	
			    		metaIsChanged = true;
			    		fieldsMap[fields[i].header] = (fields[i].id+1);
			    		headerToHide.push(fields[i].header);
			    		if (tmpHeader === "") {
			    			tmpHeader = "header__" + i;
			    			fieldsMap[tmpHeader] = (fields[i].id);
			    			headerToHide.push(tmpHeader);
			    		}else{
				    		fields[i].header = tmpHeader;				    		
			    		}
			    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
			    	}
				}else 
					tmpMeta.fields[i] = Ext.apply({}, fields[i]);				
		    }else if (fields[i].headerType !== undefined && fields[i].headerType.toUpperCase() === 'I18N'){
		    	//-------------------------------------------------------------------------------//
				// subsitutes the grid header values with the label presents into file 			 //
		    	// (ex: \webapps\SpagoBIConsoleEngine\user_messages\it.js)						 //
				//-------------------------------------------------------------------------------//
		    	var tmpHeader = LN(fields[i].header);
		    	if (tmpHeader !== undefined){
		    		metaIsChanged = true;
		    		fields[i].header = tmpHeader;		    		
		    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
		    	}else{
					tmpMeta.fields[i] = Ext.apply({}, fields[i]);
		    	}
	    	}else{
		    	//without substitution; manteins the header defined into the columnConfig section
	    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
	    	}
		}
		
		//-------------------------------------------------------------------------------//
		// 	inline charts updating														 //
		//-------------------------------------------------------------------------------//
		if (this.inlineCharts !== undefined && this.inlineCharts !== null) {
			var minValue = 0;
			var maxValue = 0;
			var totValue = 0;
			var idxFieldThreshold = 0;
			var idxFieldColumn = 0;
			var pointChartConfig = {};		
			var nameFieldThr = "";
			var nameFieldThrFirstInt = "";
			var nameFieldThrSecondInt = "";
			var tooltip = "";
			//var fieldsMap = {};
				
			for(var p = 0, len = this.inlineCharts.length; p < len; p++) {
				minValue = 0;
				maxValue = 0;
				totValue = 0;	
				if (this.inlineCharts[p] !== undefined){		
					for (var i=0; i < numRec; i++){
						var tmpRec = this.store.getAt(i);
						var tmpValue = tmpRec.get(this.store.getFieldNameByAlias(this.inlineCharts[p].column));						
						if ((this.inlineCharts[p].type == 'point' || this.inlineCharts[p].type == 'semaphore') &&
							 this.inlineCharts[p].thresholdType == 'dataset'){ 
							if(this.inlineCharts[p].threshold !== undefined || this.inlineCharts[p].thresholdFirstInt !== undefined || 
								  this.inlineCharts[p].thresholdSecondInt !== undefined) {
								//gets thresholds value dinamically (from dataset)
								pointChartConfig = Ext.apply({}, this.inlineCharts[p] || {});
								//internationalize tooltips
								pointChartConfig.tooltip =  Sbi.locale.getLNValue(pointChartConfig.tooltip);
								pointChartConfig.tooltipGreen =  Sbi.locale.getLNValue(pointChartConfig.tooltipGreen);
								pointChartConfig.tooltipYellow =  Sbi.locale.getLNValue(pointChartConfig.tooltipYellow);
								pointChartConfig.tooltipRed =  Sbi.locale.getLNValue(pointChartConfig.tooltipRed);
								metaIsChanged = true;
								idxFieldColumn = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[p].column));
								if (this.inlineCharts[p].threshold !== undefined){
									nameFieldThr = this.getNameFieldForThreshold(this.inlineCharts[p].threshold, fieldsMap, headerToHide);
									pointChartConfig.nameFieldThr = nameFieldThr;
								}
								if (this.inlineCharts[p].thresholdFirstInt !== undefined){
									nameFieldThrFirstInt = this.getNameFieldForThreshold(this.inlineCharts[p].thresholdFirstInt, fieldsMap, headerToHide);
									pointChartConfig.nameFieldThrFirstInt = nameFieldThrFirstInt;
								}
								if (this.inlineCharts[p].thresholdSecondInt !== undefined){
									nameFieldThrSecondInt = this.getNameFieldForThreshold(this.inlineCharts[p].thresholdSecondInt, fieldsMap, headerToHide);
									pointChartConfig.nameFieldThrSecondInt = nameFieldThrSecondInt;
								}
								//check the tooltip, before try to international it, then substitutes the field value														
								pointChartConfig = this.getTooltipFromFields(pointChartConfig, fieldsMap, headerToHide );

								var renderer = this.createInlineChartRenderer(pointChartConfig);
								if( renderer !== null ) {
									fields[idxFieldColumn].renderer = renderer;
						    		tmpMeta.fields[idxFieldColumn] = Ext.apply({}, fields[idxFieldColumn]);
								}
							}
						}else if ((this.inlineCharts[p].type == 'point' || this.inlineCharts[p].type == 'semaphore') && 
								   this.inlineCharts[p].thresholdType == 'env'){
							if(this.inlineCharts[p].threshold !== undefined || this.inlineCharts[p].thresholdFirstInt !== undefined || 
									  this.inlineCharts[p].thresholdSecondInt !== undefined) {
								idxFieldColumn = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[p].column));
								pointChartConfig = Ext.apply({}, this.inlineCharts[p] || {});
								//internationalize tooltips
								pointChartConfig.tooltip =  Sbi.locale.getLNValue(pointChartConfig.tooltip);
								pointChartConfig.tooltipGreen =  Sbi.locale.getLNValue(pointChartConfig.tooltipGreen);
								pointChartConfig.tooltipYellow =  Sbi.locale.getLNValue(pointChartConfig.tooltipYellow);
								pointChartConfig.tooltipRed =  Sbi.locale.getLNValue(pointChartConfig.tooltipRed);
								metaIsChanged = true;
								pointChartConfig.threshold = this.executionContext[this.inlineCharts[p].threshold];
								pointChartConfig.thresholdFirstInt = this.executionContext[this.inlineCharts[p].thresholdFirstInt];
								pointChartConfig.thresholdSecondInt = this.executionContext[this.inlineCharts[p].thresholdSecondInt];
								pointChartConfig = this.getTooltipFromEnv(pointChartConfig);
								
		  						var renderer = this.createInlineChartRenderer(pointChartConfig);
								if( renderer !== null ) {
									fields[idxFieldColumn].renderer = renderer;
						    		tmpMeta.fields[idxFieldColumn] = Ext.apply({}, fields[idxFieldColumn]);
								}
							}
			            }else if (this.inlineCharts[p].type == 'bar'){
							if (tmpValue !== undefined){
								totValue = totValue + tmpValue;
								if ( tmpValue < minValue || i === 0) minValue = tmpValue;
								
								if ( tmpValue > maxValue ) maxValue = tmpValue;
							}
						}
					}  
					if (numRec > 0 && this.inlineCharts[p].type == 'bar'){
						//update initial value config with news								
						this.inlineCharts[p].maxValue = maxValue;
						this.inlineCharts[p].minValue = minValue;
						this.inlineCharts[p].totValue = totValue; 	
							
						var idx = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[p].column));
						this.getColumnModel().setRenderer(idx, this.createInlineChartRenderer(this.inlineCharts[p]) );
					}
				}
			}
		}
		
		if (this.storeLabelsId !== undefined){
			//check to change headers with internationalized dataset
  		this.tempColumnModel = tmpMeta;
  		this.headersToHide = headerToHide;
  		this.fieldsMap = fieldsMap;
		  this.loadStoreForHeaders();
		}
		else{
		  if (metaIsChanged) this.updateMetaStructure(tmpMeta, headerToHide, fieldsMap);
		}
	}
	
	, onMetaChange: function( store, meta ) {
		var i;
	    var fieldsMap = {};

		var tmpMeta =  Ext.apply({}, meta); // meta;
		var fields = tmpMeta.fields;
		tmpMeta.fields = new Array(fields.length);
		
		for(i = 0; i < fields.length; i++) {
			if( (typeof fields[i]) === 'string') {				
				fields[i] = {name: fields[i]};
			}
			
			if (this.columnId !== undefined && this.columnId === fields[i].header ){
				fields[i].hidden = true;
			}
			tmpMeta.fields[i] = Ext.apply({}, fields[i]);
			fieldsMap[fields[i].name] = i;
		}
		
		var inlineChartMap = {};
		if (this.inlineCharts) { 
			for(var j = 0, len = this.inlineCharts.length; j < len; j++) {
				inlineChartMap[ this.inlineCharts[j].column ] = this.inlineCharts[j];
			}
		}
		
		for(i = 0; i < tmpMeta.fields.length; i++) {	
			var t = Ext.apply({}, this.columnConfig[tmpMeta.fields[i].header] || {},  this.columnDefaultConfig);
		    tmpMeta.fields[i] = Ext.apply(tmpMeta.fields[i], t);
		    
			if(tmpMeta.fields[i].type) {
				var tmpType = tmpMeta.fields[i].type;					
				if (tmpType == 'date' || tmpType == 'timestamp'){
					tmpMeta.fields[i].renderer  =  Sbi.locale.formatters[tmpType];
				}else{							
					tmpMeta.fields[i].renderer = this.renderTooltip.createDelegate(this);
				}
			}
			   
			if(tmpMeta.fields[i].subtype && tmpMeta.fields[i].subtype === 'html') {
				tmpMeta.fields[i].renderer  =  Sbi.locale.formatters['html'];
			}
			
			tmpMeta.fields[i].sortable = true;
	   
			var chartConf = null;
			if( (chartConf = inlineChartMap[tmpMeta.fields[i].header]) !== undefined ) {
				var renderer = this.createInlineChartRenderer(chartConf);
				if( renderer !== null ) {
					tmpMeta.fields[i].renderer  =  renderer;
				} else{
					Sbi.Msg.showWarning('Impossible to create inlineChart on column [' + tmpMeta.fields[i].header + ']');
				}
			}
			
		} 

	    //adds inline action buttons
		if (this.inlineActions) {
			for(var i = 0, l = this.inlineActions.length; i < l; i++){ 
				if ( this.inlineActions[i].type == undefined)  this.inlineActions[i].type =  this.inlineActions[i].name;
				var column = this.createInlineActionColumn(this.inlineActions[i]);					
				if(column !== null) {
					
					if (this.inlineActions[i].imgSrcInactive !== undefined){
						var tmpImgName = this.inlineActions[i].imgSrcInactive.substr(0,this.inlineActions[i].imgSrcInactive.indexOf(".") );
						if (Ext.util.CSS.getRule('.x-grid3-hd-' + tmpImgName + '_header') == null){
							var tmpCSS = '.x-grid3-hd-' + tmpImgName	+ '_header { background: url(../img/'+this.inlineActions[i].imgSrcInactive+') center center no-repeat; height:20px;}';
		    				Ext.util.CSS.createStyleSheet(tmpCSS);
						}
						column.cls = tmpImgName	+ '_header';
					}
					else{
						column.cls = this.inlineActions[i].type + '_header';
					}
					tmpMeta.fields.push( column );
				} else {
					Sbi.Msg.showWarning('Impossible to create inlineActionColumn [' + this.inlineActions[i].type + ']');
				}
				//hides the configuration column linked to inlineActions				
				var tmpName; 
				if(this.inlineActions[i].checkColumn) {
					tmpName = this.store.getFieldNameByAlias(this.inlineActions[i].checkColumn);						
					if (tmpName !== undefined)  tmpMeta.fields[fieldsMap[tmpName]].hidden = true;
				}
				if(this.inlineActions[i].flagColumn) {
					tmpName = this.store.getFieldNameByAlias(this.inlineActions[i].flagColumn);						
					if (tmpName !== undefined)  tmpMeta.fields[fieldsMap[tmpName]].hidden = true;
				}
				
  	  		}	
		}
		//hides flag icons column for massive actions 
		if (this.filterBar.actions) {
			for(var i = 0, l = this.filterBar.actions.length; i < l; i++){
				var massiveAction =  this.filterBar.actions[i];
				if (massiveAction.flagColumn !== undefined){
					var tmpName = this.store.getFieldNameByAlias(massiveAction.flagColumn);						
					if (tmpName !== undefined)  tmpMeta.fields[fieldsMap[tmpName]].hidden = true;
				}
			}
		}
		
		//adds numeration column    
		tmpMeta.fields[0] = new Ext.grid.RowNumberer();
	    //update columnmodel configuration
		this.getColumnModel().setConfig(tmpMeta.fields);
	}

	
	, createInlineChartRenderer: function(config) {
		var chartRenderer = null;
		if(config.type === 'bar') {
			renderer  =  Sbi.console.commons.Format.inlineBarRenderer(config);
		} else if(config.type === 'point') {			
			renderer  =  Sbi.console.commons.Format.inlinePointRenderer(config);
		} else if(config.type === 'semaphore') {			
			renderer  =  Sbi.console.commons.Format.inlineSemaphoreRenderer(config);
		} else{
			Sbi.Msg.showWarning('InlineChart type [' + chartConf.type + '] is not supported');
		}
		return renderer;
	}
		
	, createInlineActionColumn: function(config) {
		
		var inlineActionColumn = null;
		var inlineActionColumnConfig = config;
		
		inlineActionColumnConfig = Ext.apply({
			grid: this
			, scope: this
		//	, headerIconCls: inlineActionColumnConfig.type + '_header'
		}, inlineActionColumnConfig);
		
		inlineActionColumnConfig.tooltip = Sbi.locale.getLNValue(inlineActionColumnConfig.tooltip);
		inlineActionColumnConfig.tooltipInactive = Sbi.locale.getLNValue(inlineActionColumnConfig.tooltipInactive);
		inlineActionColumnConfig.tooltipActive = Sbi.locale.getLNValue(inlineActionColumnConfig.tooltipActive);
		
		//updates img source if it's necessary (only for actionButton)
		if(inlineActionColumnConfig.type !== 'selectRow') {
			if (inlineActionColumnConfig.imgSrcActive !== undefined){
				inlineActionColumnConfig.imgSrcActive = '../img/' + inlineActionColumnConfig.imgSrcActive;
			}
			else if (this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images['active'] !== undefined){
				inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images['active'];			
			}
			if (inlineActionColumnConfig.imgSrcInactive !== undefined){
				inlineActionColumnConfig.imgSrcInactive = '../img/' + inlineActionColumnConfig.imgSrcInactive;
			}else if (this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images['inactive'] !== undefined){
				inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.type ].images['inactive'];
			}
		}
		
		if (inlineActionColumnConfig.type === 'crossnav'){
			if (inlineActionColumnConfig.imgSrc !== undefined){
				inlineActionColumnConfig.imgSrc = '../img/' + inlineActionColumnConfig.imgSrc;
			}else{
				// for default
				inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images['cross_detail'];
				if (inlineActionColumnConfig.config){			
					if (inlineActionColumnConfig.config.target === 'self') {					
						inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images['popup_detail'];	
					}
				}
			}
			inlineActionColumnConfig.handler = this.execCrossNav;
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
			
		}else if (inlineActionColumnConfig.type === 'monitor'){		
			inlineActionColumnConfig.handler = this.toggleMonitor;
				
			//set the filter for view only active items (default)
			var tmpName = this.store.getFieldNameByAlias(inlineActionColumnConfig.checkColumn);
			if (tmpName !== undefined){
				if (this.store.filterPlugin.getFilter(tmpName) === undefined) {					
					var tmpValue = new Array();
					tmpValue.push(this.ACTIVE_VALUE );
					this.store.filterPlugin.addFilter (tmpName, tmpValue);
				}
			}
						
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);			
				
		} else if (inlineActionColumnConfig.type === 'errors'){	
			inlineActionColumnConfig.toggleOnClick = false;
			inlineActionColumnConfig.handler = this.showErrors;
			
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.type === 'alarms'){	
			inlineActionColumnConfig.toggleOnClick = false;
			inlineActionColumnConfig.handler = this.showAlarms;
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.type === 'views'){	
			inlineActionColumnConfig.toggleOnClick = true;			
			inlineActionColumnConfig.handler = this.execAction;
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.type === 'start'){	
			inlineActionColumnConfig.toggleOnClick = false;
			//inlineActionColumnConfig.toggleOnClick = true; //refresh automatico delle icone?
			inlineActionColumnConfig.handler = this.startProcess;
			inlineActionColumnConfig.isChecked = function(record) {
				var v, active;
				if(this.isBoundToColumn()) {
					v = this.getBoundColumnValue(record);
				//	alert('myIsCHecked-v: ' + v);
			    	active = (v != 0);
				}
				
				return active;		
			};
			inlineActionColumnConfig.setChecked = function(record, b) {
				var v, s;
				if(this.isBoundToColumn()) {
					s = this.grid.store;
					if(b) {
						record.set (s.getFieldNameByAlias(this.checkColumn), '34' );
					}
				}
			};
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);
		
		} else if (inlineActionColumnConfig.type === 'stop'){			
			inlineActionColumnConfig.toggleOnClick = false;
			//inlineActionColumnConfig.toggleOnClick = true; //refresh automatico delle icone?
			inlineActionColumnConfig.handler = this.stopProcess;
			inlineActionColumnConfig.isChecked = function(record) {
				var v, active;
				if(this.isBoundToColumn()) {
					v = this.getBoundColumnValue(record);					
			    	active = (v == 0);
				}
				
				return active;		
			};
			inlineActionColumnConfig.setChecked = function(record, b) {
				var v, s;
				if(this.isBoundToColumn()) {
					s = this.grid.store;
					if(b) {
						record.set (s.getFieldNameByAlias(this.checkColumn), 0 );
					}
				}
			};
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);
			
		}else if (inlineActionColumnConfig.type === 'informationlog'){			
			inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images;
			inlineActionColumnConfig.handler = this.downloadLogs;			
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
			
		}else if (inlineActionColumnConfig.type === 'selectRow'){							
			inlineActionColumnConfig.handler = this.updateSelectedRows;		
			inlineActionColumn = new Sbi.console.InlineCheckColumn(inlineActionColumnConfig); 	
			inlineActionColumn.masterCheckValue = null; //reset value
		} else {
			inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.type ].images;
			inlineActionColumnConfig.handler = this.execAction;
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
		}
		return inlineActionColumn;
	}
	
	, getFieldsToToggle: function(action){
		var toReturn = [];
		for (var i=0, l= this.columnModel.fields.length; i<l; i++ ){
			var cmf = this.columnModel.fields[i];
			if (cmf.type == action.type && cmf.name !== action.name){
				toReturn.push(cmf);
			}			
		}
		return toReturn;
	}
	
	, getTooltipFromFields: function(chartConfig, fieldsMap, headerToHide){			
		var tooltipToCheck;
		
		if ((chartConfig.tooltip && chartConfig.tooltip.indexOf("$F{") === -1) &&
			(chartConfig.tooltipGreen && chartConfig.tooltipGreen.indexOf("$F{") === -1) &&
			(chartConfig.tooltipYellow && chartConfig.tooltipYellow.indexOf("$F{") === -1) &&
			(chartConfig.tooltipRed && chartConfig.tooltipRed.indexOf("$F{") === -1)){
			return chartConfig;
		}
		
		if (chartConfig.tooltip !== undefined){
			chartConfig.nameTooltipField = this.getFieldsConfiguration(chartConfig.tooltip, fieldsMap, headerToHide);
		}
		if (chartConfig.tooltipGreen !== undefined){
			chartConfig.nameTooltipFieldGreen = this.getFieldsConfiguration(chartConfig.tooltipGreen, fieldsMap, headerToHide);
		}
		if (chartConfig.tooltipYellow !== undefined){
			chartConfig.nameTooltipFieldYellow = this.getFieldsConfiguration(chartConfig.tooltipYellow, fieldsMap, headerToHide);
		}
		if (chartConfig.tooltipRed !== undefined){
			chartConfig.nameTooltipFieldRed = this.getFieldsConfiguration(chartConfig.tooltipRed, fieldsMap, headerToHide);
		}
				
		return chartConfig;
	}
	
	, getFieldsConfiguration: function(tooltipToCheck, fieldsMap, headerToHide){
		var startFieldTooltip;
		var lenFieldTooltip;
		var nameTooltipField;
		var idxFieldTooltip;	
		var arNameTooltipField;
		var elTooltipField;
		
		arNameTooltipField = new Array();								
		while (tooltipToCheck.indexOf("$F{") !== -1){
			elTooltipField = {};
			startFieldTooltip = tooltipToCheck.indexOf("$F{")+3;
			lenFieldTooltip = tooltipToCheck.indexOf("}")-startFieldTooltip;
			nameTooltipField =  tooltipToCheck.substr(startFieldTooltip,lenFieldTooltip);
			idxFieldTooltip = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(nameTooltipField));
			fieldsMap[nameTooltipField] = idxFieldTooltip;					
			if (headerToHide.indexOf(nameTooltipField)<0) headerToHide.push(nameTooltipField); //hides the column with the tooltip
			
			elTooltipField.name = nameTooltipField;
			elTooltipField.value = this.store.getFieldNameByAlias(nameTooltipField);
			arNameTooltipField.push(elTooltipField);
			
			tooltipToCheck = tooltipToCheck.replace("$F{" + nameTooltipField + "}", "");
		}
		return arNameTooltipField;
	}
	
	, getTooltipFromEnv: function(chartConfig){			
		var tooltipToCheck;
		
		if ((chartConfig.tooltip && chartConfig.tooltip.indexOf("$P{") === -1) &&
			(chartConfig.tooltipGreen && chartConfig.tooltipGreen.indexOf("$P{") === -1) &&
			(chartConfig.tooltipYellow && chartConfig.tooltipYellow.indexOf("$P{") === -1) &&
			(chartConfig.tooltipRed && chartConfig.tooltipRed.indexOf("$P{") === -1)){
			return chartConfig;
		}
		
		if (chartConfig.tooltip !== undefined){
			chartConfig.tooltip = this.getVarConfiguration(chartConfig.tooltip);
		}
		if (chartConfig.tooltipGreen !== undefined){
			chartConfig.tooltipGreen = this.getVarConfiguration(chartConfig.tooltipGreen);
		}
		if (chartConfig.tooltipYellow !== undefined){
			chartConfig.tooltipYellow = this.getVarConfiguration(chartConfig.tooltipYellow);
		}
		if (chartConfig.tooltipRed !== undefined){
			chartConfig.tooltipRed = this.getVarConfiguration(chartConfig.tooltipRed);
		}
				
		return chartConfig;
	}
	
	, getVarConfiguration: function(tooltipToCheck){
		var startFieldTooltip;
		var lenFieldTooltip;
		var nameTooltipField;
	
		while (tooltipToCheck.indexOf("$P{") !== -1){
			startFieldTooltip = tooltipToCheck.indexOf("$P{")+3;
			lenFieldTooltip = tooltipToCheck.indexOf("}")-startFieldTooltip;
			nameTooltipField =  tooltipToCheck.substr(startFieldTooltip,lenFieldTooltip);																
			if (nameTooltipField){
					var tmpTooltipValue = this.executionContext[nameTooltipField] || " ";
					if (tmpTooltipValue){
						var newTooltip = tooltipToCheck.replace("$P{" + nameTooltipField + "}", tmpTooltipValue);
						tooltipToCheck = newTooltip;
					}
			}else 
				break;
		}
		
		return tooltipToCheck;
	}
	
	, getNameFieldForThreshold: function(labelThr, fieldsMap, headerToHide){
		idxFieldThreshold = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(labelThr));
		fieldsMap[labelThr] = idxFieldThreshold;
		if (headerToHide.indexOf(labelThr)<0) headerToHide.push(labelThr); //hides the column with the configuration
		nameFieldThreshold = this.store.getFieldNameByAlias(labelThr);
		return nameFieldThreshold;
	}
	
	, loadStoreForHeaders: function(){
		//load optional dataset with lables for i18N management:			
		this.storeLabels = this.storeManager.getStore(this.storeLabelsId);
		
		if (this.storeLabels === undefined) {
			Sbi.Msg.showError('Dataset with identifier [' + this.storeLabelsId + '] is not correctly configurated');			
		}else{					
			this.storeLabels.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
			this.storeLabels.on('metachange', this.onMetaChangeLabels, this);
			this.storeLabels.on('load', this.changeLabelsByDatatset, this);
			this.storeLabels.loadStore();	
		}
		
	}
	, onMetaChangeLabels: function( store, meta ) {
	 	var fieldsMap = {};
		var tmpMeta =  Ext.apply({}, meta); // meta;
		var fields = tmpMeta.fields;
		tmpMeta.fields = new Array(fields.length);
		
		for(i = 0; i < fields.length; i++) {
			if( (typeof fields[i]) === 'string') {
				fields[i] = {name: fields[i]};
			}
			
			if (this.columnId !== undefined && this.columnId === fields[i].header ){
				fields[i].hidden = true;
			}
			tmpMeta.fields[i] = Ext.apply({}, fields[i]);
			fieldsMap[fields[i].name] = i;
		}
		this.columnModelLabels = tmpMeta.fields;
		if (this.storeLabels.alias2FieldMetaMap == null){
			this.storeLabels.alias2FieldMetaMap = this.columnModelLabels;
		}
	}
	, changeLabelsByDatatset: function(){

		var tmpMeta = this.tempColumnModel;
		var fields = tmpMeta.config;
		var idxLocale = null;
		var idxLabel = null;
		var idxCode = null;
		for(var i = 0, len = fields.length; i < len; i++) {
			if (fields[i] !== undefined && fields[i].headerType !== undefined && fields[i].headerType.toUpperCase() === 'DATASETI18N'){
		    	//-------------------------------------------------------------------------------//
				// 	subsitutes the grid header values with the specific dataset labels.
		    	// This dataset should returns 3 fields: code, label, locale (it_IT, en_US, fr_FR, es_ES)
		    	// Ex: cod_UnitSales, Unit Sales, en_US 													 
				//-------------------------------------------------------------------------------//		
		    	if (this.storeLabels.alias2FieldMetaMap !== undefined && this.storeLabels.alias2FieldMetaMap !==  null ){
		    		if (idxLabel == null || idxLocale == null || idxCode == null){
				    	idxLocale = (this.storeLabels.getFieldMetaByAlias("LOCALE") !== undefined)?this.storeLabels.getFieldMetaByAlias("LOCALE") :
				    		this.storeLabels.getFieldMetaByAlias("locale");
				    	if (idxLocale !== undefined) idxLocale = idxLocale.dataIndex;
				    	idxCode = (this.storeLabels.getFieldMetaByAlias("CODE") !== undefined)?this.storeLabels.getFieldMetaByAlias("CODE") :
				    		this.storeLabels.getFieldMetaByAlias("code");
				    	if (idxCode !== undefined) idxCode = idxCode.dataIndex;		    	
				    	idxLabel = (this.storeLabels.getFieldMetaByAlias("LABEL") !== undefined)?this.storeLabels.getFieldMetaByAlias("LABEL") :
				    		this.storeLabels.getFieldMetaByAlias("label");
				    	if (idxLabel !== undefined) idxLabel = idxLabel.dataIndex;
		    		}
		    	}
		    	if (idxLocale == undefined || idxCode == undefined || idxLabel == undefined){
		    		Sbi.Msg.showError(LN('sbi.console.localization.columnsKO'), 'Service Error');
		    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
		    	}else{
			    	//apply filter on labelsStore:
		    		
			    	var idxRec = this.storeLabels.findBy(function(record){				    		
			    	   if (idxLocale !== undefined && idxCode !== undefined){
			    		  if(record.data[idxLocale] === Sbi.user.locale && 
			    		     record.data[idxCode] === fields[i].header) {		
	  						   return true;  						   
	  					   }	
			    	   } 	
			  		   return false;				   
			  	   }, this);
			  	   
		    		//var idxRec = this.findByLocaleAndCode(idxLocale, Sbi.user.locale, idxCode, fields[i].header);
			    	var tmpRec = this.storeLabels.getAt(idxRec);		    	
					if (tmpRec !== undefined) {
						var tmpHeader =  tmpRec.get(idxLabel);
				    	if (tmpHeader !== undefined){	
				    		metaIsChanged = true;
				    		fields[i].header = tmpHeader;		    		
				    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
				    	}else{
							tmpMeta.fields[i] = Ext.apply({}, fields[i]);
				    	}	
					}else 
						tmpMeta.fields[i] = Ext.apply({}, fields[i]);	
		    	}
	    	}else {
	    		tmpMeta.fields[i] = Ext.apply({}, fields[i]);
	    	}	
		}
	
		this.updateMetaStructure(tmpMeta, this.headersToHide, this.fieldsMap);
	}
	
	, findByLocaleAndCode: function(idxLocale, locale, idxCode, code) {
		for (var count = 0; count < this.storeLabels.getCount(); count++) {			
			var aRecord = this.storeLabels.getAt(count);
			if (aRecord.get(idxLocale) == locale  && aRecord.get(idxCode) == code ) {
				alert("return: " + count);
					return count;
			}
		}		
		return -1;
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {    		
    		this.loadMask = new Ext.LoadMask('GridPanel', {msg: "Loading.."});
    	}
    	if (this.loadMask){
    		this.loadMask.show();
    	}
    }

	/**
	 * Closes the loading mask
	*/
	, hideMask: function() {
    	if (this.loadMask && this.loadMask != null) {	
    		this.loadMask.hide();
    	}
	} 
	
	,renderTooltip:function(val, cell, record) {	
		// get data
		var data = record.data;
		 
		// return markup
		return '<div qtip="' + val +'">' + val + '</div>';
	}
});