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
  * - name (mail)
  */

Ext.ns("Sbi.xxx");

Sbi.xxx.Xxxx = function(config) {
	
	var defaultSettings = {
			title: LN('sbi.qbe.queryeditor.title'),
		};
		
		if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.queryBuilderPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.queryBuilderPanel);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		
		Ext.apply(this, c);
		
		
		this.services = this.services || new Array();	
		this.services['doThat'] = this.services['doThat'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'DO_THAT_ACTION'
			, baseParams: new Object()
		});
		
		this.addEvents('customEvents');
		
		this.initThis(c.westConfig || {});
		this.initThat(c.westConfig || {});
	
		c = Ext.apply(c, {
	      	layout: 'border',      	
	      	items: [this.thisPanel, this.thatPanel]
		});

		// constructor
		Sbi.xxx.Xxxx.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.xxx.Xxxx, Ext.util.Observable, {
    
    services: null
   
   
    // public methods
});