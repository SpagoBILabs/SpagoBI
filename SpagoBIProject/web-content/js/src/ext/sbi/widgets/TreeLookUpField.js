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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.widgets");

Sbi.widgets.TreeLookUpField = function(config) {
	
	
	Ext.apply(this, config);
	this.initWin();
	
	var c = Ext.apply({}, config, {
		triggerClass: 'x-form-search-trigger'
		, enableKeyEvents: true
		,  width: 150
		//, 	readOnly: true
	});   
	
	// constructor
	Sbi.widgets.TreeLookUpField.superclass.constructor.call(this, c);
	
	
	this.on("render", function(field) {
		field.trigger.on("click", function(e) {
			if(!this.disabled) {
				this.onLookUp(); 
			}
		}, this);
	}, this);
	
	this.on("render", function(field) {
		field.el.on("keyup", function(e) {
			this.xdirty = true;
		}, this);
	}, this);
	
	this.addEvents('select');	
	
	
};

Ext.extend(Sbi.widgets.TreeLookUpField, Ext.form.TriggerField, {
    

    win: null
    
    , initWin: function() {
    	
    	
		
		this.tree = new Ext.tree.TreePanel({
	        width: 200,
	        autoScroll: true,
	        rootVisible: false,
	        loader: new Ext.tree.TreeLoader({
				dataUrl: this.service ,
				baseParams: this.params,
				createNode: function(attr) {
					attr.text = attr.description;
					if(attr.leaf){
						attr.checked =false;
					}
					var node = Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
					return node;
				}
			}),
	        root: new Ext.tree.AsyncTreeNode({
				text : 'root',
				expanded: true,
				id:  'lovroot___SEPA__0'
	        })
		//,  rootVisible: false
	    });

    	
		this.win = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 580,
            height      : 300,
            closeAction :'hide',
            plain       : true,
            items       : [this.tree],
            buttons: [
	               {
	            	   text: LN('sbi.lookup.Annulla')
	            	   , listeners: {
		           			'click': {
		                  		fn: this.onCancel,
		                  		scope: this
		                	} 
	               		}
	               } , {
	            	   text: LN('sbi.lookup.Confirm')
	            	   , listeners: {
		           			'click': {
		                  		fn: this.onOk,
		                  		scope: this
		                	} 
	               		}
	               }
	        ]
		});
		

	}

 
	, onLookUp: function() {
		this.win.show(this);
	}

	, onOk: function(){
		
		var v = this.getValue();
		this.setValue(v);
		this.fireEvent('select', this, v);
		this.win.hide();
	}
	
	, onCancel: function(){
		this.win.hide();
	}
	
	,getValue: function(){
		var checked =  this.tree.getChecked();
		var values = [];
		if(checked){
			for(var i=0; i<checked.length; i++){
				values.push(checked[i].attributes.value);
			}
		}
		return values;
	}
	

});



