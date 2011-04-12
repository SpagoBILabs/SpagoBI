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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.tools.dataset");

Sbi.tools.dataset.QbeDatasetBuilder = function(config) {

	var defaultSettings = {
		title: LN("sbi.tools.dataset.qbedatasetbuilder.title")
		, width: 800
		, height: 500
	};
	 
	if(Sbi.settings && Sbi.settings.tools && Sbi.settings.tools.dataset && Sbi.settings.tools.dataset.qbeDatasetBuilder) {
	   defaultSettings = Ext.apply(defaultSettings, Sbi.settings.tools.dataset.qbeDatasetBuilder);
	}
	 
	var c = Ext.apply(defaultSettings, config || {});
	 
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		layout:'fit',
		closeAction: 'hide',
		plain: true,
		title: this.title,
		items: [this.iframe],
		listeners: this.listeners,
		scope: this.scope
	});
	
	Sbi.tools.dataset.QbeDatasetBuilder.superclass.constructor.call(this, c);
	
	this.addEvents('gotqbequery');

};

Ext.extend(Sbi.tools.dataset.QbeDatasetBuilder, Ext.Window, {
	
	title: null
	, iframe: null
	, qbeBaseUrl: null // base URL for SpagoBIQbeEngine web application: it must be set in the constructor's input object
	, jsonQuery: null // query definition: it must be set in the constructor's input object
	, qbeParameters: null // query parameters: it must be set in the constructor's input object
	
	, init: function () {
		
		this.iframe = new Ext.ux.ManagedIFramePanel({
			defaultSrc: this.qbeBaseUrl
	        , loadMask: {msg: 'Loading...'}
	        , fitToParent: true
	        , frameConfig: {
            	disableMessaging: false
            }
	        , disableMessaging: false
	        , listeners: {
	        	'message': {
	        		fn: function(srcFrame, message) {
	        			var messageName = message.data.messageName;
	        			if (messageName == 'gotqbequery') {
	        				this.fireEvent('gotqbequery', this, message);
	        			} else if (messageName == 'catalogueready') {
	        				this.setQbeQuery();
	        			} else { 
	        				alert('qbedatasetbuilder: Unknown message');
	        			}
	        		}
	        		, scope: this
	        	}
				, 'domready': {
					fn: function(frame) {
						if(frame.domWritable()) {
							frame.execScript('init()');
						}
					}
					, scope: this
				}
	        }
		});
		
	}

	, getQbeQuery: function (handler, scope) {
		var message = {};
		message.messageName = 'getQbeQuery';
		this.iframe.sendMessage(message); // ask for the query
	}
	
	, setQbeQuery: function () {
		var message = {};
		message.messageName = 'setQbeQuery';
		message.jsonQuery = this.jsonQuery;
		message.qbeParameters = this.qbeParameters;
		this.iframe.sendMessage(message); // set the query
	}
	
});
