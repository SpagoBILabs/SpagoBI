/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.console");


Sbi.console.InlineActionColumn = function(config){
	
	
	config = Ext.apply({
		hideable: true
		, width: 25
	}, config || {});
	
	config.options = config.config;
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
    Sbi.console.InlineActionColumn.superclass.constructor.call(this, config);
    
    if(this.grid) this.init(this.grid);
};


Ext.extend(Sbi.console.InlineActionColumn, Ext.util.Observable, {
	
	grid: null
	
	// action name and options
	, name: null
	, options: null
	
	// click handler function
	, handler: null
	, scope: null
	
	// control column. If flagColumn is defined and its value is equal to 
	// UNFLAGGED_VALUE the button will be not active (i.e. not visible and actionable)
	, flagColumn: null
	, UNFLAGGED_VALUE: 0
	
	// -- public methods --------------------------------------------------------------------------
	, isActive: function(record) {
		var active = true;
		if(this.flagColumn) {
			var v, s;
			
			s = this.grid.store;
			v = record.get(s.getFieldNameByAlias(this.flagColumn));
	    	if (v === undefined || v === null) {
	    		Sbi.Msg.showError('Impossible to draw button column [' + this.dataIndex + ']. Dataset [' + s.storeId + ']does not contain column [' + this.flagColumn + ']');
	    	};
	    	active = (this.UNFLAGGED_VALUE !== v);
	    	//alert(v + ' !== '+ this.UNFLAGGED_VALUE + ' : ' + active);
		}
			
		return active;
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

    , onClick : function(e, t){
        if(t.className && t.className.indexOf('x-mybutton-'+this.id) != -1){
            e.stopEvent();
            
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);   
            this.handler.call(this.scope, this, record, index, this.options);          
        }
    }
       

    , renderer : function(v, p, record){
    	if(this.isActive(record) === false) {
    		return '';
    	}
        return '<center><img class="x-mybutton-'+this.id+'" width="13px" height="13px" src="' + this.imgSrc + '" title= "' + this.tooltip + '"/></center>';
    }
  });