/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 Ext.ns("Sbi.console");


Sbi.console.InlineToggleActionColumn = function(config){
	
    // parent constructor
    Sbi.console.InlineToggleActionColumn.superclass.constructor.call(this, config);
  
};


Ext.extend(Sbi.console.InlineToggleActionColumn, Sbi.console.InlineActionColumn, {
	
	toggleOnClick: true
	
	, checkColumn: null

	, CHECKED_VALUE: 1
	, UNCHECKED_VALUE: 0
	
	// -- public methods ------------------------------------------------------------------------
	
	, isBoundToColumn: function() {
		return (this.checkColumn !== null);
	}

	

	, getBoundColumnValue: function(record) {
		var v, s;
		
		s = this.grid.store;
		v = record.get(s.getFieldNameByAlias(this.checkColumn));
    	if (v === undefined || v === null) {
    		Sbi.Msg.showError('Impossible to draw toggle column [' + this.dataIndex + ']. Dataset [' + s.storeId + ']does not contain column [' + this.checkColumn + ']');
    	} /*else if(v !== this.CHECKED_VALUE && v !== this.UNCHECKED_VALUE) {
    		Sbi.Msg.showError('Column [' + this.checkColumn + '] of dataset [' + s.storeId + '] contains a wrong value [' + v + ']. Impossible to determinate the state of the bounded togle column [' + this.checkColumn + ']');
    	}*/
    	return v;
	}
	
	, setBoundColumnValue: function(record, value) {
		var s;
		s = this.grid.store;
		record.set (s.getFieldNameByAlias(this.checkColumn), value );
	}
	
	
	, isChecked: function(record) {
		var v, active;
		if(this.isBoundToColumn()) {
			v = this.getBoundColumnValue(record);
			
	    	active = (v === this.CHECKED_VALUE);
	    	//alert(v + ' === '+ this.CHECKED_VALUE + ' : ' + active);
		}
		
		return active;		
	}
	
	, setChecked: function(record, b) {
		if(this.isBoundToColumn()) {
			this.setBoundColumnValue(record, (b? this.CHECKED_VALUE: this.UNCHECKED_VALUE));
		}
	}
	
	, toggle: function(record) {
		this.setChecked(record, !this.isChecked(record));
	}

	

	
	
	// -- private methods ------------------------------------------------------------------------

    , onClick : function(e, t){

        if(t.className && t.className.indexOf('x-mybutton-'+ this.id) != -1){
            e.stopEvent();
            
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);   
            //if in configuration is set that the action is usable only once, it doesn't change the check if it's yet checked
            if(!this.isChecked(record) || 
            		this.singleExecution === undefined || this.singleExecution == false) {            	
            	if(this.toggleOnClick) this.toggle(record);  
            	this.handler.call(this.scope, this, record, index, this.options);
            }
        }
    }


    , renderer : function(v, p, record){
    	
    	if(this.isActive(record) === false) {
    		return '';
    	}

	    if (this.isChecked(record)) {
	    	img = '<center><img class="x-mybutton-'+this.id+'" width="13px" height="13px" src="' + this.imgSrcInactive + '" title= "' + this.tooltipInactive + '"/></center>';
	    } else {
	    	img = '<center><img class="x-mybutton-'+this.id+'" width="13px" height="13px" src="' + this.imgSrcActive + '" title= "' + this.tooltipActive + '"/></center>';  
	    }	    		
    	
        return img;
    }
});