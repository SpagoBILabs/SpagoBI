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

Sbi.console.ActionButton = function(config) {

		var defaultSettings = {
			//id: 'ActionButton'
			iconCls: config.actionConf.type
			,tooltip: (config.actionConf.tooltip === undefined)?config.actionConf.type : config.actionConf.tooltip 
			,hidden: config.actionConf.hidden
			,scope:this
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.actionButton) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.actionButton);
		}
	
		var c = Ext.apply(defaultSettings, config || {});
	
		Ext.apply(this, c);

	    this.initServices();
	    
        c = Ext.apply(c, this);
      
      
	    // constructor
	    Sbi.console.ActionButton.superclass.constructor.call(this, c);
	    this.on('click', this.execAction, this);
	    this.store.on('load', this.initButton, this);
        this.addEvents('toggleIcons');      
        // invokes before each ajax request 
        //Ext.Ajax.on('beforerequest', this.showMask, this);   
        // invokes after request completed 
        //Ext.Ajax.on('requestcomplete', this.hideMask, this);            
        // invokes if exception occured 
        //Ext.Ajax.on('requestexception', this.hideMask, this);   
}; 

Ext.extend(Sbi.console.ActionButton, Ext.Button, {
    
    services: null
    , isActive: null
    , ACTIVE_VALUE: 1
	, INACTIVE_VALUE: 0
	, USER_ID: 'userId'
	, ERRORS: 'errors'
	, ALARMS: 'alarms'
	, VIEWS: 'views'
	, MONITOR: 'monitor'
	, MONITOR_INACTIVE: 'monitor_inactive'
	, SELECT_ROWS: 'selectRow'
	, UNSELECT_ROWS: 'unselectRow'
	, INVERT_SELECT_ROWS: 'invertSelectionRow'
	, loadMask: null
	, columnID : null
		
	, FILTERBAR_ACTIONS: {		
		  monitor: {serviceName: 'UPDATE_ACTION', images: 'monitor'}
		, monitor_inactive: {serviceName: 'UPDATE_ACTION', images: 'monitor_inactive'}
		, errors: {serviceName: 'UPDATE_ACTION', images: {active: 'errors', inactive: 'errors_inactive'}} 
		, alarms: {serviceName: 'UPDATE_ACTION', images: {active: 'alarms', inactive: 'alarms_inactive'}}
		, views: {serviceName: 'UPDATE_ACTION', images: {active: 'views', inactive: 'views_inactive'}}
		, refresh: {serviceName: 'REFRESH_ACTION', images: {active: 'refresh', inactive: 'refresh'}}
	}
   
    // public methods

	//This method search the dynamic parameter value before in the request, if it isn't found it search into filter.
	//If it isn't found again it shows a message error.
	, resolveParameters: function(parameters, context) {
		var results = {};  

		results = Ext.apply(results, parameters.staticParams);
		
		var dynamicParams = parameters.dynamicParams;
	    if(dynamicParams) {        	
	    	var msgErr = ""; 
	    	var useSelRows = false;
	      	for (var i = 0, l = dynamicParams.length; i < l; i++) {      		     
	      		var param = dynamicParams[i]; 
	      		if (param.useSelectedRow !== undefined && param.useSelectedRow == true){	      			
	      			useSelRows = true;
	      		}
	        	for(p in param) { 
	        		if(p === 'scope') continue;	      
	        		if(p === 'useSelectedRow') continue;	
	        		//Searchs into request
        			if (param.scope === 'env'){ 
        			  var tmpNamePar =  param[p];
        			  if (useSelRows){
        				  var tb = this.ownerCt;
        				  var gridConsole = tb.ownerCt;
        				  var finalSelectedRowsId = this.cleanList(gridConsole.selectedRowsId);
        				  if (finalSelectedRowsId == null || finalSelectedRowsId.length == 0){
        					  var msgWar = 'Parameter "' + tmpNamePar + '" has not values selected. Default value is used.<p>';
        					  Sbi.Msg.showWarning(msgWar, 'Service Warning');
        				  }else{
	                    	  results[p] = finalSelectedRowsId;	  	                    	  
	                    	  this.columnID = tmpNamePar;
        				  }
        				  useSelRows = false; //reset flag
        			  } else if (p !== this.USER_ID && context[tmpNamePar] === undefined) {
	            		  // msgErr += 'Parameter "' + tmpNamePar + '" undefined into request. <p>';
                      } else if (!useSelRows){   
                    	  results[p] = context[tmpNamePar];                    	  
                      }		            	
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
	    }
	    
	    return results;
	}
	
	, cleanList: function(lst){
		var toReturn = [];
		if (lst == null) return lst;
		
		for (var i=0, l=lst.length; i<l; i++){
			var el = lst[i];
			if (el !== undefined && el !== ''){
				toReturn.push(el);
			}
		}
		
		return toReturn;
		
	}
    , execAction: function(){
    	//if the action is executable only once and it's disabled, do nothing
    	if (this.actionConf.singleExecution !== undefined && this.actionConf.singleExecution == true && 
    			(this.isActive !== undefined && this.isActive == false)){
    		return;
    	}
    	//views a confirm message if it's configurated
    	if (this.actionConf.msgConfirm !== undefined && this.actionConf.msgConfirm !== ''){
    		Ext.MessageBox.confirm(
    				"Confirm",
    	            this.actionConf.msgConfirm,            
    	            function(btn, text) {
    					 if (btn == 'yes') {
    						 this.execRealAction();
    					 }
    				},    	            
	            this
			);  
    	}else{
    		this.execRealAction();
    	}
	}
 

    // private methods
    , initServices: function() {
    	this.services = this.services || new Array();	
		this.images = this.images || new Array();	
				
		for(var actionName in this.FILTERBAR_ACTIONS) {
			var actionConfig = this.FILTERBAR_ACTIONS[actionName];
			this.services[actionName] = this.services[actionName] || Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: actionConfig.serviceName
				, baseParams: new Object()
			});
		}
    }
    
    , initButton: function(){    	
    	//icons about monitoring are ever enabled
    	if (this.actionConf.type === this.MONITOR || this.actionConf.type === this.MONITOR_INACTIVE){
    		return;
    	}
    	
    	if (this.actionConf.type === this.SELECT_ROWS || this.actionConf.type === this.UNSELECT_ROWS ||
    		this.actionConf.type === this.INVERT_SELECT_ROWS){
	    	if (this.actionConf.imgSrc !== undefined){
				//creates css dynamically if it's an extra-icon
				var tmpImgName = this.actionConf.imgSrc.substr(0,this.actionConf.imgSrc.indexOf(".") );
				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrc+') !important; }');
				}
				this.setIconClass(tmpImgName);    				
			}
	    	return;
    	}
    	//checks if the button is active (when the action is errors or alarms)
    	if (this.actionConf.type === this.ERROR || this.actionConf.type === this.ALARMS ||
    		this.actionConf.type === this.VIEWS && this.actionConf.flagColumn !== undefined){
    		var flagCol = this.store.getFieldNameByAlias(this.actionConf.flagColumn);    
        	if (flagCol === undefined ){
        		return;
        	}else{
        		var flagValue = this.store.findExact(flagCol, this.ACTIVE_VALUE);
        		if (flagValue === -1) {
        			this.hide();
        			return;
        		}
        	}    		
    	}

    	var isCheck = this.store.getFieldNameByAlias(this.actionConf.checkColumn);
    	if (isCheck !== undefined ){
    		//checkValue: -1 if all rows are ACTIVE, greater then -1 when ther's almost one active 
    		var checkValue = this.store.findExact(isCheck,this.INACTIVE_VALUE);
    		if (checkValue > -1){  //there's any inactive --> enable active actions
    			if (this.actionConf.imgSrcActive !== undefined){
    				//creates css dynamically if it's an extra-icon
    				var tmpImgName = this.actionConf.imgSrcActive.substr(0,this.actionConf.imgSrcActive.indexOf(".") );
    				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
    					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrcActive+') !important; }');
    				}
    				this.setIconClass(tmpImgName);    				
    			}else{    			
    				this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.type ].images[ "active"]);
    			}
    			this.isActive = true;
    			this.setTooltip(this.actionConf.tooltipInactive);
    		}else{      
    			if (this.actionConf.imgSrcInactive !== undefined){
    				var tmpImgName = this.actionConf.imgSrcInactive.substr(0,this.actionConf.imgSrcInactive.indexOf(".") );
    				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
    					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrcInactive+') !important; }');
    				}
    				this.setIconClass(tmpImgName);
    			}else{  
    				this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.type ].images["inactive"]); 
    			} 
    			this.isActive = false;
	    		this.setTooltip(this.actionConf.tooltipActive);    			
    	    }
    	}else {
	    	//if the checkColumn is undefined gets the srcActive image (for default)
	    	if (this.actionConf.imgSrcActive !== undefined){
				//creates css dynamically if it's an extra-icon
				var tmpImgName = this.actionConf.imgSrcActive.substr(0,this.actionConf.imgSrcActive.indexOf(".") );
				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrcActive+') !important; }');
				}
				this.setIconClass(tmpImgName);    		
			}else{    			
				this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.type ].images[ "active"]);
			}
			this.isActive = true;
			this.setTooltip(this.actionConf.tooltipInactive);
    	}
    }
  
    //updates checkColumn value in each store's row
    , setCheckValue: function(columnAlias, value, disableCheck){
    	var tb = this.ownerCt;
		var gridConsole = tb.ownerCt;
		if (gridConsole.selectedRowsId == null)  gridConsole.selectedRowsId = [];
		
		if (this.actionConf.hideSelectedRow !== undefined && this.actionConf.hideSelectedRow == true &&
				gridConsole.hideSelectedRow == null) {
			gridConsole.hideSelectedRow = [];
		}
		var s = gridConsole.store;
    	for (var i=0, l= s.getCount(); i < l; i++){
    		var record = s.getAt(i); 
            var valueID = record.get(s.getFieldNameByAlias(this.columnID));
            /*    var posValue = tb.getPositionEl(valueID, gridConsole.selectedRowsId);
           if (posValue !== -1){
            	delete gridConsole.selectedRowsId[posValue];
            	gridConsole.hideSelectedRow.push(valueID);
            }
            */
            if (disableCheck){     
            	var posHideValue = tb.getPositionEl(valueID, gridConsole.hideSelectedRow);
        		if  (posHideValue !== -1){
        			gridConsole.isDisable = true; //to hide the checkbox
        			gridConsole.isDirty = false;        			
        		}else{        		
        			//gridConsole.selectedRowsId = [];
        			gridConsole.isDisable = false;
        			gridConsole.isDirty = true;	 //to clean the checkbox
        		}
        	}             
            record.set (s.getFieldNameByAlias(columnAlias), value );
        } 
    	if (value === this.ACTIVE_VALUE){
    		if (this.actionConf.imgSrcInactive !== undefined){
				//creates css dynamically if it's an extra-icon
				var tmpImgName = this.actionConf.imgSrcInactive.substr(0,this.actionConf.imgSrcInactive.indexOf(".") );
				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrcInactive+') !important; }');
				}
				this.setIconClass(tmpImgName);    				
			}else{    			
				this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.type ].images[ "inactive"]);
			}
    		this.isActive = false;
    	}else{
    		if (this.actionConf.imgSrcActive !== undefined){
				var tmpImgName = this.actionConf.imgSrcActive.substr(0,this.actionConf.imgSrcActive.indexOf(".") );
				if (Ext.util.CSS.getRule('.' + tmpImgName) == null){
					Ext.util.CSS.createStyleSheet('.'+tmpImgName+' { background-image: url(../img/'+this.actionConf.imgSrcActive+') !important; }');
				}
				this.setIconClass(tmpImgName);
			}else{  
				this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.type ].images["active"]); 
			} 
    		this.isActive = true;
    	}
    }

    , execRealAction: function(){    	
    	checkCol = this.actionConf.checkColumn;
    	
    	if (this.actionConf.type === 'selectRow' || this.actionConf.type === 'unselectRow' || 
    		this.actionConf.type === 'invertSelectionRow'){
    		this.fireEvent('toggleIcons', this, null);
    		return;
    	}else if (this.actionConf.type === 'monitor' || this.actionConf.type === 'monitor_inactive'){     		    	
    		this.store.filterPlugin.removeFilter(this.store.getFieldNameByAlias(this.actionConf.checkColumn));
    		var newFilter = new Array();
    		newFilter.push((this.actionConf.type === 'monitor') ? this.ACTIVE_VALUE : this.INACTIVE_VALUE);    	
    		this.store.filterPlugin.addFilter(this.store.getFieldNameByAlias(this.actionConf.checkColumn), newFilter);    		
    		this.store.filterPlugin.applyFilters();	   
    		this.hideMask();
    		return;
    	}else if (this.actionConf.type === 'refresh'){    	
    		if(this.store.pagingParams && this.store.pagingParams.paginator) {
    			if(this.store.lastParams) {
    				delete this.store.lastParams;
    			}
    			var paginator = this.store.pagingParams.paginator;
    			paginator.doLoad(paginator.cursor); 
    		} else {
    			this.store.loadStore();    
    		}
    		var tb = this.ownerCt;
    		tb.ownerCt.selectedRowsId = [];
    		tb.ownerCt.hideSelectedRow = [];
    		tb.ownerCt.isDisable = false;
    		tb.ownerCt.isDirty = true;
    		this.hideMask();
    		return;
    	} else if (this.actionConf.type === 'errors' || this.actionConf.type === 'errors_inactive'){  
    		if (this.isActive !== undefined && this.isActive == true){
    			flgCheck = this.ACTIVE_VALUE;
    		}else if (this.isActive !== undefined && this.isActive == false){
    			flgCheck = this.INACTIVE_VALUE;
    		}else{
    			flgCheck = (this.iconCls === 'errors')? this.ACTIVE_VALUE: this.INACTIVE_VALUE; 
    		}    		   		    	
    	} else if (this.actionConf.type === 'alarms' || this.actionConf.type === 'alarms_inactive'){   
    		if (this.isActive !== undefined && this.isActive == true){
    			flgCheck = this.ACTIVE_VALUE;
    		}else if (this.isActive !== undefined && this.isActive == false){
    			flgCheck = this.INACTIVE_VALUE;
    		}else{
    			flgCheck = (this.iconCls === 'alarms')? this.ACTIVE_VALUE: this.INACTIVE_VALUE;    		
    		}    		
    	} else if (this.actionConf.type === 'views' || this.actionConf.type === 'views_inactive'){     
    		if (this.isActive !== undefined && this.isActive == true){
    			flgCheck = this.ACTIVE_VALUE;
    		}else if (this.isActive !== undefined && this.isActive == false){
    			flgCheck = this.INACTIVE_VALUE;
    		}else{
    			flgCheck = (this.iconCls === 'views')? this.ACTIVE_VALUE: this.INACTIVE_VALUE;
    		}
    	}
    	
    	//if in configuration is set that the action is usable only once, it doesn't change the check if it's yet checked
        if(flgCheck != null  && flgCheck === this.INACTIVE_VALUE &&
        		this.actionConf.singleExecution !== undefined && this.actionConf.singleExecution == true){
        	this.hideMask();
        	return;            	
        }
        
        this.executionContext[checkCol] = flgCheck;
		
		var params = this.resolveParameters(this.actionConf.config, this.executionContext);		
		params = Ext.apply(params, {
				message: this.actionConf.type, 
				userId: Sbi.user.userId 
			}); 
		
		this.showMask();		
		Ext.Ajax.request({
		url: this.services[this.actionConf.type]	       
       	, params: params 			       
    	, success: function(response, options) {
    		if(response !== undefined && response.responseText !== undefined) {
					var content = Ext.util.JSON.decode( response.responseText );
					if (content !== undefined) {				      			  
					//	alert(content.toSource());
					}	
					//if by configuration is required a refresh of the dataset, it executes the store's load method,
					//otherwise it changes the icons by the toggle (default)
					this.hideMask();					
					this.updateHideSelectedRowList();
					if (this.refreshDataAfterAction !== undefined && this.refreshDataAfterAction === true ){
						var tb = this.ownerCt;
			    		tb.ownerCt.selectedRowsId = [];
			    		tb.ownerCt.isDisable = false;
			    		tb.ownerCt.isDirty = true;
						this.store.loadStore();
					} else {
						//fire events to toggle all icons of the same type
						this.setCheckValue(this.actionConf.checkColumn, flgCheck, true);    
						this.fireEvent('toggleIcons', this, flgCheck);
					}
			} else {
				this.hideMask();
				Sbi.Msg.showError('Server response is empty', 'Service Error');
			}
    	}
    	, failure: function (response, options){
    		this.hideMask();
    		Sbi.exception.ExceptionHandler.onServiceRequestFailure(response, options);
    	}
    	, scope: this     
	    });  
		
		//this.hideMask.defer(2000, this);
		
    }
    
    , updateHideSelectedRowList: function(){
    	var tb = this.ownerCt;
		var gridConsole = tb.ownerCt;
		var addHide = false;
		if (this.actionConf.hideSelectedRow !== undefined && this.actionConf.hideSelectedRow == true){
			addHide = true;
		}
		if (addHide && gridConsole.hideSelectedRow == null) {			
			gridConsole.hideSelectedRow = [];
		}
		for (var i=0, l=gridConsole.selectedRowsId.length; i<l; i++){
			var valueID = gridConsole.selectedRowsId[i];
			var posHideValue = tb.getPositionEl(valueID, gridConsole.hideSelectedRow);
			delete gridConsole.selectedRowsId[i];
			if (posHideValue == -1 && addHide){	        	
	        	gridConsole.hideSelectedRow.push(valueID);
	        }
		}
		
    }
    
    /**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {        		    	    		
    		this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg: "Loading.."});
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
	
});
    
