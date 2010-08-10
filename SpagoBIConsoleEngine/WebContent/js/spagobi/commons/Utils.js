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


