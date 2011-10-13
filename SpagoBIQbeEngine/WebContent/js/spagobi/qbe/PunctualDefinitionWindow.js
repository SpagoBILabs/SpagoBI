/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.ZONE
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
  * PunctualDefinitionWindow - short description
  * 
  * Object documentation ...
  * 
  * by Monica Franceschini
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.PunctualDefinitionWindow = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		title: 'Punctual Values Definition'
		, width: 400
		, height: 200
		, hasBuddy: false	
		
	});

	Ext.apply(this, c);
	
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	this.services = new Array();
	
	this.services['getValuesForQbeFilterLookupService'] = Sbi.config.serviceRegistry.getServiceUrl({
		  serviceName: 'GET_VALUES_FOR_QBE_FILTER_LOOKUP_ACTION'
		, baseParams: params
	});
	
	this.initMainPanel(c);	
	
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	Sbi.qbe.PunctualDefinitionWindow.superclass.constructor.call(this, c);  
	this.add(this.mainPanel);
  
};

Ext.extend(Sbi.qbe.PunctualDefinitionWindow, Ext.Window, {
	
	  hasBuddy: null
    , buddy: null
    , slotPanel : null
   
    , mainPanel: null
    , valuesToSave: []
	, services: null

    
	, initMainPanel: function(c) {
		
		this.slotPanel = c.slotPanel;
		var record = c.record;
		
		var btnFinish = new Ext.Button({
	        text: 'Save',
	        disabled: false,
	        scope: this,
	        handler : this.save.createDelegate(this, [record])
		});
		/*
	      var valuesSt = new Ext.data.SimpleStore({
	          fields: ['value'],
	          data : [['valore1'],['valore2'], ['valore3'], ['valore4']]
	      });
	      */	     
		// for test uses MATRICOLA of table ACCESSO (enel db)
	      var storeP = this.createStore(c.record, 'it.eng.spagobi.meta.Accesso:matricola');
	      storeP.load();
	      
	      var sm = new Ext.grid.CheckboxSelectionModel();
		  this.mainPanel = new Ext.grid.GridPanel({
				store: storeP,
				columns: [
	               {
	                   id       :'value',
	                   header   : 'Value', 
	                   sortable : true, 
	                   dataIndex: 'value'
	               }, 
	               sm
	            ],
	            selMode: sm,
		        border:true,  
				width: 380,
				height: 170,
		        style:'padding:0px',
		        scrollable: true,
		        iconCls:'icon-grid',
		        collapsible:false,
		        layout: 'fit',
			    bbar: [
			           btnFinish
			    ], 
		        viewConfig: {
		            forceFit: true
		        }
		    });

		this.mainPanel.doLayout();

    }
	, save: function(rec){
		var recs = this.mainPanel.selModel.getSelections();
		if(recs !== null && recs !== undefined && recs.length !== 0){			
			for(i=0; i<recs.length; i++){
				var sel = recs[i];
				this.valuesToSave.push(sel.data.value);
			}
			this.slotPanel.addPunctualVals(this.valuesToSave, rec);
		}
		
		this.close();
	}
	
	, createStore: function(record, entityId) {
		//var record = this.activeEditingContext.grid.store.getAt(this.activeEditingContext.row);
		//var entityId = record.get('leftOperandValue');
		//var entityId = record.get('entityId');
		var createStoreUrl = this.services['getValuesForQbeFilterLookupService']
		        		   + '&ENTITY_ID=' + entityId;
		var store = new Ext.data.JsonStore({
			url: createStoreUrl
		});
		
		store.on('loadexception', function(store, options, response, e) {
			var msg = '';
			var content = Ext.util.JSON.decode( response.responseText );
  			if(content !== undefined) {
  				msg += content.serviceName + ' : ' + content.message;
  			} else {
  				msg += 'Server response is empty';
  			}
	
			Sbi.exception.ExceptionHandler.showErrorMessage(msg, response.statusText);
		});
		return store;	
	}

});