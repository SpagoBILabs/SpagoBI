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
  */

Ext.ns("Sbi.console");

Sbi.console.Widget = function(config) {	
		var defaultSettings = {
			defaultMsg: 'Empty widget'
		};

		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widget) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widget);
		}
		var c = Ext.apply(defaultSettings, config || {});
		
		Ext.apply(this, c);
		this.msgPanel = new Ext.Panel({
			html: this.defaultMsg
		});
		if (this.linkableDoc !== undefined){
			//create the toolbar with the link vs external document
			var buttonLink = [];
			buttonLink.push({
				text: this.linkableDoc.text
				, tooltip: (this.linkableDoc.tooltip === undefined) ? "" : this.linkableDoc.tooltip
				, styles: {font:{style: 'bold'}}
				, documentConf: this.linkableDoc 
				, executionContext: this.executionContext
				, handler: this.execCrossNavigation
				, scope: this
			});
		
			this.toolLink = new Ext.Toolbar({autoWidth: true,  items: buttonLink});			
			delete this.linkableDoc;
		}
		if (this.toolLink !== undefined){
			c = Ext.apply(c, {  	
				header: false,
				tbar: this.toolLink,
		      	items: [this.msgPanel]
			});
		}else{	
			c = Ext.apply(c, {  				
		      	items: [this.msgPanel]
			});
		}	
		// constructor
		Sbi.console.Widget.superclass.constructor.call(this, c);	
};

Ext.extend(Sbi.console.Widget, Ext.Panel, {
    services: null
    , parentContainer: null
    
   
    //  -- public methods ---------------------------------------------------------
    
    , setParentContainer: function(c) {	
		this.parentContainer = c;	
	}

	, getStore: function(storeiId) {
		var store;
		
		if(this.parentContainer) {
			var sm = this.parentContainer.getStoreManager();
			if(sm) {
				store = sm.getStore(storeiId);
			} else {
				alert("getStore: storeManager not defined");
			}
		} else {
			alert("getStore: container not defined");
		}	
		return store;
	}
    
    //  -- private methods ---------------------------------------------------------
    
	, onRender: function(ct, position) {	
		Sbi.console.Widget.superclass.onRender.call(this, ct, position);	
	}
	
	, execCrossNavigation: function (b){	
		if(sendMessage === undefined) {
			Sbi.Msg.showError(
					'function [sendMessage] is not defined',
					'Cross navigation error'
			);
			return;
		}
		
		if( (typeof sendMessage) !== 'function') {
			Sbi.Msg.showError(
					'[sendMessage] is not a function',
					'Cross navigation error'
			);
			return;
		}
		
		var msg = {
			label: b.documentConf.label
			, windowName: this.name										
		};
			
		var separator = '';
		//adds static parameters
		if(b.documentConf.staticParams) {
			msg.parameters = '';		
			for(p in b.documentConf.staticParams) {
				msg.parameters += separator + p + '=' + b.documentConf.staticParams[p];
				separator = '&';
			}
			//alert("msg.parameters: " + msg.parameters.toSource());
		}
		
    //adds dynamic parameters (environment type) 
		if(b.documentConf.dynamicParams) {
			if (msg.parameters === undefined) msg.parameters = '';	
		    var msgErr = ""; 
		    for (var i=0, l=b.documentConf.dynamicParams.length; i < l; i++){     
	  		  var param = b.documentConf.dynamicParams[i];
	  		  for(p in param) {
				  var label = param[p];
			      if (p != 'scope'){
			    	if (param['scope'] === 'env'){ 
			            if (b.executionContext[label] === undefined) {              	 	 	      
				 	 	        msgErr += 'Parameter "' + p + '" undefined into request. <p>';
			            } else {          	 	 		           	 	 		  
			  	 	 		    msg.parameters += separator + p + '=' + b.executionContext[label];
					            separator = '&';
			                } 	 		 
			 	    	}          	 	 		   
			 	    }
			    }//for (p in tmp)
	    	}//for
		}
		//alert("msg.parameters: " + msg.parameters.toSource());
		sendMessage(msg, 'crossnavigation');	
	}
    
    
    
});