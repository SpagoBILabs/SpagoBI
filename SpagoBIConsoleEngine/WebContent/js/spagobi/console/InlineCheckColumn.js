/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.console");


Sbi.console.InlineCheckColumn = function(config){
	
	
	config = Ext.apply({
		hideable: true
		, width: 25
	}, config || {});
	
	config.tooltip = config.tooltip || {};
	config.handler = config.handler || Ext.emptyFn;
	config.scope = config.scope || this;

	delete config.config;
	
	Ext.apply(this, config);
    if(!this.id){
        this.id = Ext.id();
    }
    if(!this.dataIndex) {
    	this.dataIndex = this.name + "_" + this.id;
    }
    this.renderer = this.renderer.createDelegate(this);
    
    // parent constructor
    Sbi.console.InlineCheckColumn.superclass.constructor.call(this, config);
    
    if(this.grid) this.init(this.grid);
};


Ext.extend(Sbi.console.InlineCheckColumn, Ext.util.Observable, {
	
	grid: null

	// click handler function
	, handler: null
	, scope: null
	
	, masterCheckValue: null
	, listRowsSelected: []
	// control column. If flagColumn is defined and its value is equal to 
	// UNFLAGGED_VALUE the button will be not active (i.e. not visible and actionable)
	, flagColumn: null
	, UNFLAGGED_VALUE: 0
	, CHECKED_VALUE: 1
	, UNCHECKED_VALUE: 0
	
	// -- public methods --------------------------------------------------------------------------
	, isActive: function(record) {
		var active = true;
		if(this.flagColumn) {
			var v, s;
			
			s = this.grid.store;
			v = record.get(s.getFieldNameByAlias(this.flagColumn));
	    	if (v === undefined || v === null) {
	    		Sbi.Msg.showError('Impossible to draw check column [' + this.dataIndex + ']. Dataset [' + s.storeId + ']does not contain column [' + this.flagColumn + ']');
	    	};
	    	active = (this.UNFLAGGED_VALUE !== v);
	    	//alert(v + ' !== '+ this.UNFLAGGED_VALUE + ' : ' + active);
		}
			
		return active;
	}
	
	, setChecked: function(b, record) {		
		this.masterCheckValue = (b) ? this.CHECKED_VALUE: this.UNCHECKED_VALUE;
		var s = this.grid.store;
		var origValue = record.get(s.getFieldNameByAlias(this.flagColumn));
		var newValue = (origValue === 1 || origValue === '1' ) ? 'true' : '1';
		record.set (s.getFieldNameByAlias(this.flagColumn), newValue );
	}
	
	// -- private methods -------------------------------------------------------------------------
	, init : function(grid){
		this.grid = grid;
        if(this.grid.rendered === true) {
        	var view = this.grid.getView();
            view.mainBody.on('click', this.onClick, this);
        } else {
        	this.grid.on('render', function(){
        		var view = this.grid.getView();
        		view.mainBody.on('click', this.onClick, this);
        	}, this);
        }
    }

	, toggle: function(record) {
		this.setChecked(this.masterCheckValue, record);
	}

    , onClick : function(e, t){
    	if(t.className && (t.className.indexOf('x-mygrid3-check-col-'+ this.id) != -1 ||
    					   t.className.indexOf('x-mygrid3-check-col-on-'+ this.id) != -1) ){
    		e.stopEvent();
	        
	        var index = this.grid.getView().findRowIndex(t);
	        var record = this.grid.store.getAt(index);   
	        //add or remove the columnId from the general list
	        var s = this.grid.store;
	        //this.grid.isDisable = false; 
	        this.updateValuesList(record.get(s.getFieldNameByAlias(this.columnID)));
	        this.toggle(record);       
	        this.handler.call(this.scope, this.listRowsSelected);   
    	}
    }
       
    , updateValuesList: function (value){
    	var isDeleted = false;
    	var pos = this.getPositionEl(value, this.listRowsSelected);
    	if (pos != -1){
    		delete this.listRowsSelected[pos];
			isDeleted = true;
			this.masterCheckValue = this.UNCHECKED_VALUE; //set new check value
    		
    	}    	
    	if (!isDeleted){
    		this.listRowsSelected.push(value);
    		this.masterCheckValue = this.CHECKED_VALUE;		//set new check value
    	}
    }
    
    , getPositionEl: function(value, lst) {    	
		//check if the row is in the listRowsSelected (pagination management)
    	//returns the position element into the array 
    	var toReturn = -1;    	
    	if (lst == null)  return toReturn;
    	
    	for(var i=0; i<lst.length; i++) {
    		if (lst[i] == value ){
    			toReturn = i;
    			break;
    		}   		
    	}
    	return toReturn;	
	}

    , renderer : function(v, p, record){
    	var s = this.grid.store;
    	var value = record.get(s.getFieldNameByAlias(this.columnID));
    	
		if (this.grid.isDirty || this.grid.isDisable){
			this.listRowsSelected = this.grid.selectedRowsId;
		}
    	var isHidePosition = this.getPositionEl(value, this.grid.hideSelectedRow);
    	if(this.isActive(record) === false ||  isHidePosition !== -1) {
    		this.grid.isDisable = false; //reset for next element
    		return '';
    	}
    	p.css += ' x-grid3-check-col-td';    	
    	var toReturn = '';
    	if(	value == undefined || this.getPositionEl(value, this.listRowsSelected) == -1){
    		toReturn = '<div class="x-grid3-check-col x-mygrid3-check-col-'+this.id+ + '" title= "' + this.tooltipInactive + '">&#160;</div>';
    	}
    	else{    		
    		toReturn = '<div class="x-grid3-check-col-on x-mygrid3-check-col-on-'+this.id+ + '" title= "' + this.tooltipActive + '">&#160;</div>';
    	}
        return toReturn;
    }
  });