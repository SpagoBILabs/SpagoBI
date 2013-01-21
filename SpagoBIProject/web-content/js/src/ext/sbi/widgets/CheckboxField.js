/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * Sbi.widgets.CheckboxFiel
 * 
 * Authors
 *  - Andrea Gioia (mail)
 */

Ext.ns("Sbi.widgets");

Sbi.widgets.CheckboxField = function(config) {

	var c = Ext.apply({
		//itemCls : 'x-check-group-alt',
		columns : 1,
		items : [ {
			boxLabel : 'Loading options...',
			name : 'loading-mask',
			value : 'Food1'
		}]
	}, config || {});
	
	this.store = config.store;
	this.store.on('load', this.refreshOptions, this);
	this.store.load();
	
	// constructor
	Sbi.widgets.CheckboxField.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.widgets.CheckboxField, Ext.form.CheckboxGroup, {

	store: null
	, displayField:'label'
    , valueField:'value'
    , pendingRefreshOptions: false
	
	, refreshOptions: function() {
		//alert('Store succesfully loaded: ' + this.rendered);
		
		// manage the case in which the store is loaded before the component is rendered
		if(this.rendered === false) {
			this.pendingRefreshOptions = true;
			this.on('render', this.refreshOptions, this);
			return;
		}		
		if(this.pendingRefreshOptions === true) {
			this.pendingRefreshOptions = false;
			this.on('render', this.refreshOptions);
		}
		
		// remove old options
		while(this.items.length > 0) {
			var item = this.items.removeAt(0);
			item.destroy();
		}
		this.panel.doLayout();
		
		// add new options
		var records = this.store.getRange();
		for(var i = 0; i < records.length; i++) {
			var label = records[i].get(this.displayField);
			var value = records[i].get(this.valueField);
			
			var optionItem = this.createOptionItem({
				boxLabel : label,
				name : this.parameterId,
				value : value
			});
			var colNo = this.items.getCount() % this.panel.items.getCount();
			var col = this.panel.items.get( colNo );
			//alert('Add option [' + value + '] to column [' + colNo + ']');
			this.items.add(optionItem);
			col.add(optionItem);
			this.panel.doLayout();
		}
		this.panel.doLayout();
	}

	, createOptionItem: function(optionConfig) {
		var checkbox = new Ext.form.Checkbox(optionConfig);
		return checkbox;
	}

	// public methods
	, eachItem : function(fn){
        if(this.items && this.items.each){
            this.items.each(fn, this);
        }
    }
    
	, getValue : function(){
        var out = [];
        this.eachItem(function(item){
            if(item.checked){
                out.push(item.value);
            }
        });
        return out;
    }
    
	, getValues : function() {
    	return this.getValue();
	}	
	
	, getRawValue : function() {
		return this.getValues().join();
	}
});