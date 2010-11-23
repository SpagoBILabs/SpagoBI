/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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
		delete c.dataset;	
		var tableConfig = c.table || {};
		var filterConfig =  c.filterBar || {};
		filterConfig.executionContext = c.executionContext;
		Ext.apply(this, c);
		
		this.initServices();
		this.initStore();		
		this.initColumnModel();
		this.initSelectionModel();	
		this.initFilterBar(filterConfig);
		this.initPagingBar();
		
		
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
	, columnModel: null
	, selectionModel: null
	
	// popup
	, waitWin: null
	, errorWin: null
	, alarmWin: null
	, logsWin: null
	, promptWin: null
	// popup dataset label postfix (the complete name is given by <table_dataset_label><postfix> ie: 'testConsoleErrors')
	, errorDs: 'Errors'
	, alarmDs: 'Alarms'
	
	// conf bloks
	, filterBar: null
	, inlineCharts: null
	, inlineActions: null
	
	// automatic: all dataset fields are added as filter
    , AUTOMATIC_FILTERBAR: 'automatic'
    	//custom: only configurated fields are added as filter  
    , CUSTOM_FILTERBAR: 'custom'
    	
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
		var results = {};  
		var promptables;
		
		var staticParams = parameters.staticParams;
		
		results = Ext.apply(results, parameters.staticParams);
		var dynamicParams = parameters.dynamicParams;
		
	    if(dynamicParams) { 
	    	var msgErr = ""; 
	    	for (var i = 0, l = dynamicParams.length; i < l; i++) { 
	    	 	var param = dynamicParams[i];              
		              if (param.scope === 'dataset') {
			               for(p in param) { 
			            	   if(p === 'scope') continue;
			                   if(record.get(this.store.getFieldNameByAlias(param[p])) === undefined) {         
			                    msgErr += 'Parameter "' + p + '" undefined into dataset.<p>';
					           } else {
					              results[p] = record.get(this.store.getFieldNameByAlias(param[p])); 
					           }
			               }
		              } else if (param.scope === 'env'){ 		            	  
			               for(p in param) { 
			                if(p === 'scope') continue;
			            	  if (p !== this.USER_ID && context[p] === undefined) {                         
			            		   msgErr += 'Parameter "' + p + '" undefined into request. <p>';
		                      } else {                                   
		                       results[p] = context[p];
		                      }
			               }
	                 } else if (param.scope === 'promptable'){   
		                  promptables = promptables || [];
		                  promptables.push(param);
	                 }	                 
	           }    
	    	   
	      	}
	    	var metaParams = parameters.metaParams;
		    if(metaParams) {  
		    	results['metaParams'] = Ext.util.JSON.encode(metaParams);
		    }
	   
        if  (msgErr != ""){
        	Sbi.Msg.showError(msgErr, 'Service Error');
        }	
       
      	//if there are some promptable field, it shows a popup for the insertion
      	if (promptables !== undefined){
      		//if(this.promptWin === null) {
    			this.promptWin = new Sbi.console.PromptablesWindow({
    				promptables: promptables	    					
    			});
    			this.promptWin.on('click', function(win, pp) {
    				Ext.apply(results, pp);
    			    callback.call (this, results);	    								
    			}, this);
    	//	}
      		this.promptWin.show();
      		this.promptWin.on('hide', function() {
      			this.fireEvent('unlock', this);
      			}, this);
      	}
      	else {
      		callback.call (this, results);	  
      	}
	    
	    
	    //return results;
	}
	
	//, execCrossNav: function(docConfig, index){
	, execCrossNav: function(actionName, record, index, options){
		
		
		var msg = {
			label: options.document.label
	    	, windowName: this.name				
	    	, target: (options.target === 'new')? 'self': 'popup'						
	    };
		
		var callback = function(params){
			var separator = '';
			msg.parameters = '';
			for(p in params) {
				msg.parameters += separator + p + '=' + params[p];
				separator = '&';
			}
			sendMessage(msg, 'crossnavigation');
		};
	    
		this.resolveParameters(options.document, record, this.executionContext, callback);
		
	}

	
	, execAction: function(action, r, index, options) {	
		var callback = function(params){

			params = Ext.apply(params, {
	  			message: action.name, 
	        	userId: Sbi.user.userId 
	  		}); 
			
	  		Ext.Ajax.request({
		       	url: this.services[action.name] 			       
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
		//var params = {};
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
		//alert('startProcess');
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
		       	url: this.services[action.name] 			       
		       	, params: params 			       
		    	, success: function(response, options) {
	  				
		    		if(!response || !response.responseText) {
		    			Sbi.Msg.showError('Server response is empty', 'Service Error');
		    			return;
		    		}
	  				var content = Ext.util.JSON.decode( response.responseText );
	  				action.setBoundColumnValue(r, content.pid);
	  				this.waitWin.stop('Proecess started succesfully');
					action.toggle(r);	
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
		       	url: this.services[action.name] 			       
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
			
			if(this.logsWin === null) {
				this.logsWin = new Sbi.console.DownloadLogsWindow({
				serviceName: 'DOWNLOAD_ZIP' 
				, action: action
				});
				
				
			}
			
			this.logsWin.on('checked', function(win, record) {	
				this.logsWin.downloadLogs(action, record, null, params);
				this.logsWin = null;
			}, this);
			
			this.logsWin.show();
		};
		
		this.resolveParameters(options, r, this.executionContext, callback);
		

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
		this.store.remoteSort = false;  //local type		
		this.store.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
		this.store.on('load', this.onLoad, this);
		this.store.on('metachange', this.onMetaChange, this);
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
	, updateInLineCharts: function(){

		for(var j = 0, len = this.inlineCharts.length; j < len; j++) {
			var idx = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[j].column));
			this.getColumnModel().setRenderer(idx, this.createInlineChartRenderer(this.inlineCharts[j]) );			
		}
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
		var minValue = 0;
		var maxValue = 0;
		var totValue = 0;
		
		if (this.inlineCharts !== undefined || this.inlineCharts === null) return;
		
		for(var p = 0, len = this.inlineCharts.length; p < len; p++) {
			minValue = 0;
			maxValue = 0;
			totValue = 0;		
			if (this.inlineCharts[p] !== undefined){		
				for (var i=0; i < numRec; i++){
					var tmpRec = this.store.getAt(i);
					var tmpValue = tmpRec.get(this.store.getFieldNameByAlias(this.inlineCharts[p].column));
					if (tmpValue !== undefined){
						totValue = totValue + tmpValue;
						if ( tmpValue < minValue || i === 0) minValue = tmpValue;
						
						if ( tmpValue > maxValue ) maxValue = tmpValue;
					}
				}  

				//update initial value config with news								
				this.inlineCharts[p].maxValue = maxValue;
				this.inlineCharts[p].minValue = minValue;
				this.inlineCharts[p].totValue = totValue; 	
			}
		}
		
		this.updateInLineCharts();
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
				var t = tmpMeta.fields[i].type;	
				tmpMeta.fields[i].renderer  =  Sbi.locale.formatters[t];			   
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
				var column = this.createInlineActionColumn(this.inlineActions[i]);				
				if(column !== null) {
					//column.id = this.inlineActions[i].name + '_header';	
					column.cls = this.inlineActions[i].name + '_header';
					tmpMeta.fields.push( column );
				} else {
					Sbi.Msg.showWarning('Impossible to create inlineActionColumn [' + this.inlineActions[i].name + ']');
				}
				//hidden the configuration column linked to inlineActions				
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
			, headerIconCls: inlineActionColumnConfig.name + '_header' //doesn't work
		}, inlineActionColumnConfig);
		
		if (inlineActionColumnConfig.name === 'crossnav'){
			// for default
			inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['cross_detail'];
			if (inlineActionColumnConfig.config){			
				if (inlineActionColumnConfig.config.target === 'self') {					
					inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['popup_detail'];	
				}
			}
			inlineActionColumnConfig.handler = this.execCrossNav;
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
			
		}else if (inlineActionColumnConfig.name === 'monitor'){		
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];				
			inlineActionColumnConfig.handler = this.toggleMonitor;
				
			//set the filter for view only active items (default)
			var tmpName = this.store.getFieldNameByAlias(inlineActionColumnConfig.checkColumn);
			if (tmpName !== undefined){
				if (this.store.filterPlugin.getFilter(tmpName) === undefined) {
					this.store.filterPlugin.addFilter (tmpName, this.ACTIVE_VALUE);
				}
			}
						
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);			
				
		} else if (inlineActionColumnConfig.name === 'errors'){	
			
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];	
			inlineActionColumnConfig.toggleOnClick = false;
			inlineActionColumnConfig.handler = this.showErrors;
			
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.name === 'alarms'){	
			
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];			
			inlineActionColumnConfig.toggleOnClick = false;
			inlineActionColumnConfig.handler = this.showAlarms;
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.name === 'views'){	
			
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];			
			inlineActionColumnConfig.toggleOnClick = true;
			inlineActionColumnConfig.handler = this.execAction;
			inlineActionColumn = new Sbi.console.InlineToggleActionColumn(inlineActionColumnConfig);	
			
		} else if (inlineActionColumnConfig.name === 'start'){	
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];	
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
		
		} else if (inlineActionColumnConfig.name === 'stop'){			
			inlineActionColumnConfig.imgSrcActive = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images['active'];			
			inlineActionColumnConfig.imgSrcInactive = this.GRID_ACTIONS[inlineActionColumnConfig.name ].images['inactive'];	
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
			
		}else if (inlineActionColumnConfig.name === 'informationlog'){			
			inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images;
			inlineActionColumnConfig.handler = this.downloadLogs;			
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
			
		}else {
			
			inlineActionColumnConfig.imgSrc = this.GRID_ACTIONS[ inlineActionColumnConfig.name ].images;
			inlineActionColumnConfig.handler = this.execAction;
			inlineActionColumn = new Sbi.console.InlineActionColumn(inlineActionColumnConfig);
		}
		
		
		
		return inlineActionColumn;
	}

});