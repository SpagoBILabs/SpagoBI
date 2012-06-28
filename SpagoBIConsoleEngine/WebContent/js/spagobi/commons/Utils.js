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
  * Public Functions
  * 
  *  [list]
  * 
  * 
  * Authors
  * 
  * - Andrea Gioia (adrea.gioia@eng.it)
  */

Ext.ns("Sbi");

Sbi.Msg = function(){
    
	// private variables
	// ...
	
    // public space
	return {

		showError : function(errMessage, title) {
    		var m = errMessage || 'Generic error';
    		var t = title || 'Error';
    	
    		Ext.MessageBox.show({
	       		title: t
	       		, msg: m
	       		, buttons: Ext.MessageBox.OK     
	       		, icon: Ext.MessageBox.ERROR
	       		, modal: false
	   		});
	    },
    
	    showWarning : function(errMessage, title) {
	    	var m = errMessage || 'Generic warning';
	    	var t = title || 'Warning';
	    	
	    	Ext.MessageBox.show({
	       		title: t
	       		, msg: m
	       		, buttons: Ext.MessageBox.OK     
	       		, icon: Ext.MessageBox.WARNING
	       		, modal: false
	   		});
	    }, 
	    
	    showInfo : function(errMessage, title) {
	    	var m = errMessage || 'Generic info';
	    	var t = title || 'Info';
	    	
	    	Ext.MessageBox.show({
	       		title: t
	       		, msg: m
	       		, buttons: Ext.MessageBox.OK     
	       		, icon: Ext.MessageBox.INFO
	       		, modal: false
	   		});
	    },
	    
	    unimplementedFunction: function(fnName) {
			var msg = fnName? 
					'Sorry, the functionality [' + fnName + '] has not been implemented yet':
					'Sorry, this functionality has not been implemented yet';
			
			Sbi.Msg.showInfo(msg, 'Unimplemented functionality');
	    },
	    
	    deprectadeFunction: function(fnClass, fnName) {
	    	var msg = fnName + ' in class ' + fnClass + 'is deprecated';
			
	    	Sbi.Msg.showWarning(msg, 'Deprecated functionality');
	    }
	}
}();	

Sbi.Assert = function(){
 
    // private variables
	
    // public space
	return {
		assertTrue: function(condition, msg) {
			if(!condition) Sbi.Msg.showError(msg);
		}
	
		, assertDefined: function(o, msg) {
			Sbi.Assert.assertTrue( (o !== undefined && o !== null),  msg);
		}
        
	};
}();	


