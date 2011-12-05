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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIDocCollegato =  function(config) {
		
		var defaultSettings = {};
		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);

		
		this.initFrame();
		
		this.initWindow();
		
		var c = {
				layout: 'fit',
				border: false,
				items: [this.perWin],
				scope: this.scope
		};
   
		Sbi.kpi.KpiGUIDocCollegato.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.kpi.KpiGUIDocCollegato , Ext.Panel, {
	doclabel: null
	, kpiValue: null
	, executionInstance: null
	, miframe : null
    , perWin: null
    , noDocText: null
	, initWindow: function(conf){
	
		this.perWin = new Ext.Panel({
	        scope		: this,
	        autoScroll: true,
	        border: false,
	    	autoHeight: true,
	        items       : [this.miframe]
		});

	}
	, initFrame: function(conf){
		
		this.miframe = new Ext.ux.ManagedIFramePanel ({
	        border: false,
	        bodyBorder: false
	        , autoScroll: true
	        , loadMask: {msg: 'Loading...'}
			, minWidth: 475
			, height: 400
	        , defaultSrc: 'about:blank'

	    });
	}
	, getDocViewUrl: function(field) {
		var url = null;

		if(field.attributes.documentLabel && field.attributes.documentExecUrl){
			url = field.attributes.documentExecUrl;
		}

		return url;
	}

	, update:  function(field){	
		if(this.noDocText != null){
			this.noDocText.destroy();
		}

		var url = this.getDocViewUrl(field);
		if(url != null){
			this.miframe.setSrc(url);
			this.miframe.update();
			this.miframe.show();
			this.miframe.doLayout();
			this.doLayout();
			
		}
		else{
			this.miframe.hide();
			this.noDocText =new Ext.form.DisplayField({
				value: 'Nessun documento collegato', 
				style: 'padding-left:5px; font-style: italic;'}); 
			this.perWin.add(this.noDocText);
			this.perWin.doLayout();
		}
	}
});