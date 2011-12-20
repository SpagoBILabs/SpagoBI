/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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

Sbi.console.NavigationToolbar = function(config) {
	
		var defaultSettings = {
			//title: LN('sbi.qbe.queryeditor.title')
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.navigationToolbar) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.navigationToolbar);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		var documentsConfig = c.documents || [];
		documentsConfig.executionContext = c.executionContext;
		delete c.documents;
		delete c.executionContext;
		
		Ext.apply(this, c);
				
		this.initToolbarButtons(documentsConfig);
	
		c = Ext.apply(c, {  	
	      	items: this.toolbarButtons
		});

		// constructor
		Sbi.console.NavigationToolbar.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.console.NavigationToolbar, Ext.Toolbar, {
    
    services: null
    , toolbarButtons: null
    
   
    //  -- public methods ---------------------------------------------------------
    
    //  -- private methods ---------------------------------------------------------
    ,initToolbarButtons: function(documentsConfig) {
		
		this.toolbarButtons = [];
		
		for(var i = 0, l = documentsConfig.length; i < l; i++){
			var d = documentsConfig[i];
			
			this.toolbarButtons.push({
				text: d.text
				, tooltip: d.tooltip
				, documentConf: d 
				, executionContext: documentsConfig.executionContext
				, handler: this.execCrossNavigation
				, scope: this
			});
		}
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
		    var msgErr = ""; 
		    for (var i=0, l=b.documentConf.dynamicParams.length; i < l; i++){     
  		    var tmp = b.documentConf.dynamicParams[i];
          for(p in tmp) {
     	 	    if (p != 'scope'){
       	 		 //  var param = {};   
               if (tmp['scope'] === 'env'){ 
                    if (b.executionContext[p] === undefined) {              	 	 	      
        	 	 	        msgErr += 'Parameter "' + p + '" undefined into request. <p>';
                    } else {          	 	 		           	 	 		  
          	 	 		    msg.parameters += separator + tmp[p] + '=' + b.executionContext[p];
  				            separator = '&';
                    } 	 		 
                }          	 	 		   
    	      }
    	   }
  	    if  (msgErr != ""){
  	    	Sbi.Msg.showError(msgErr, 'Service Error');
        }	
    	}
		}
	//	alert("msg.parameters: " + msg.parameters.toSource());
		sendMessage(msg, 'crossnavigation');
	}
    
    
    
});